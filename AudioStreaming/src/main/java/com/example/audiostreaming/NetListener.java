package com.example.audiostreaming;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Created by suzuno on 13-9-3.
 */
public class NetListener {

    public static final int STATUS_ = 1;
    public static final int PORT = 9000;
    public static final byte HEAD = 077;
    public static final byte TYPE_REQDECL = 021;
    public static final byte TYPE_DECLARE = 022;
    public static final byte TYPE_REQCONN = 023;
    public static final byte TYPE_DECLINE = 024;
    public static final byte TYPE_DISCONN = 025;
    public static final byte TYPE_ACCCONN = 026;
    private static final String BROADCAST_ADDRESS = "255.255.255.255";

    int mBroadcastRepeatCount = 10;
    int mBufferSize = 2048;

    Handler mHandler;

    String mLocalWanIP;
    String mHostName;

    volatile boolean mListening;

    public NetListener(Handler handler){
        mHandler = handler;
    }

    public void startListening(){
        new ListeningThread().start();
    }

    public void startBroadcasting(){
        new BroadcastThread().start();
    }

    public void getWlanAddress(){
        new AcquireLocalWanAddressThread().start();
    }

    private void sendData(byte type,byte sequence,byte[] data,String targetIP) throws IOException, InterruptedException {
        Packet p = new Packet();

        p.mAddress = mLocalWanIP;
        p.mType = type;
        p.mSequence = sequence;
        p.mData = data;

        DatagramSocket socket;
        DatagramPacket packet;

        byte[] packetData = p.getBytes();

        socket = new DatagramSocket();
        socket.setBroadcast(true);
        packet = new DatagramPacket(packetData,packetData.length, InetAddress.getByName(targetIP),PORT);

        for(int i=0;i<mBroadcastRepeatCount;i++){
            socket.send(packet);
            Thread.currentThread().sleep(100);
        }
    }

    private void acquireWlanAddress() throws SocketException, UnknownHostException {
        String local = null;
        Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
        while(e.hasMoreElements()){
            NetworkInterface ni = e.nextElement();
            String name = ni.getName();
            if(name.contains("wlan")){
                Enumeration<InetAddress> enumeration = ni.getInetAddresses();
                while (enumeration.hasMoreElements()){
                    InetAddress adr = enumeration.nextElement();
                    String address = adr.getHostAddress();
                    if(!address.contains("wlan")) local = address;
                }
            }
        }
        mLocalWanIP= local==null?InetAddress.getLocalHost().getHostAddress():local;
    }

    private void broadcast() throws IOException, InterruptedException {
        sendData(TYPE_REQDECL,(byte)0,mHostName.getBytes(),BROADCAST_ADDRESS);
    }

    private void listen() throws IOException, InterruptedException {
        byte[] buffer = new byte[mBufferSize];

        DatagramSocket socket = new DatagramSocket(9000);
        DatagramPacket packet = new DatagramPacket(buffer,buffer.length);

        mListening = true;

        while(mListening){
            socket.receive(packet);
            byte[] data = packet.getData();

            Packet p = assemblePacket(data);

            if(p!=null){
                switch(p.mType){
                    case TYPE_DECLARE:
                        dealDeclare(p.mAddress,p.mData);
                        break;
                    case TYPE_REQDECL:
                        dealRequestDeclare(p);
                        break;
                }
            }
        }
    }

    private void dealDeclare(String ip,byte[] hostname){
        Message message = new Message();
        message.what = TYPE_DECLARE;
        Bundle bundle = message.getData();
        bundle.putString("host_name",new String(hostname));
        bundle.putString("host_address",ip);
        mHandler.sendMessage(message);
    }

    private void dealRequestDeclare(Packet p) throws IOException, InterruptedException {
        String address = p.mAddress;
        sendData(TYPE_DECLARE,(byte)0,mHostName.getBytes(),address);
    }

    private Packet assemblePacket(byte[] data){
        if(data[0]!=HEAD){
            return null;
        }

        Packet packet = new Packet();

        int index = 1;
        packet.mAdrSize = data[index++];

        if(packet.mAdrSize>0){
            byte[] adrData = new byte[packet.mAdrSize];
            for(int i=0;i<packet.mAdrSize;i++,index++){
                adrData[i] = data[index];
            }
            packet.mAddress = new String(adrData);
        }

        packet.mType = data[index++];
        packet.mSequence = data[index++];

        byte dataSize1 = data[index++];
        byte dataSize2 = data[index++];
        byte dataSize3 = data[index++];
        byte dataSize4 = data[index++];

        packet.mDataSize = dataSize1&0xff|dataSize2&0xff<<8|dataSize3&0xff<<16|dataSize4&0xff<<24;

        if(packet.mDataSize>0){
            packet.mData = new byte[packet.mDataSize];
            for(int i=0;i<packet.mDataSize;i++,index++){
                packet.mData[i] = data[index];
            }

        }

        return packet;
    }

    private class Packet{

        byte    mAdrSize;
        String  mAddress;
        byte    mType;
        byte    mSequence;
        int     mDataSize;
        byte[]  mData;

        private byte[] getBytes(){
            byte[] adr = null;
            if(mAddress!=null){
                adr = mAddress.getBytes();
            }
            if(adr.length<=127){
                mAdrSize = (byte) adr.length;
            }
            if(mData!=null){
                mDataSize = mData.length;
            }
            if(mAdrSize+mDataSize+7>mBufferSize){

            }
            byte data[] = new byte[mBufferSize];
            int index = 0;
            data[index++] = HEAD;
            data[index++] = mAdrSize;
            for(int i=0;i<mAdrSize;i++,index++){
                data[index]=adr[i];
            }
            data[index++] = mType;
            data[index++] = mSequence;
            int dataSize = mDataSize;
            data[index++] = (byte) (dataSize&0xff);
            dataSize = dataSize>>8;
            data[index++] = (byte) (dataSize&0xff);
            dataSize = dataSize>>8;
            data[index++] = (byte) (dataSize&0xff);
            dataSize = dataSize>>8;
            data[index++] = (byte) (dataSize&0xff);
            for(int i=0;i<mDataSize;i++,index++){
                data[index]=adr[i];
            }
            return data;
        }
    }

    private class AcquireLocalWanAddressThread extends Thread{
        @Override
        public void run(){
            try{
                acquireWlanAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            mHandler.sendEmptyMessage(Main.EVENT_WLAN_ADDRESS_ACQUIRED);
        }
    }

    private class ListeningThread extends Thread{
        @Override
        public void run(){
            try {
                listen();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class BroadcastThread extends Thread{
        @Override
        public void run(){
            try {
                broadcast();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
