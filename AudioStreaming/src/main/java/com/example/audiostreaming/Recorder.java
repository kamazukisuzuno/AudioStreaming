package com.example.audiostreaming;

import android.media.MediaRecorder;
import android.os.Handler;
import android.os.ParcelFileDescriptor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by suzuno on 13-9-3.
 */
public class Recorder implements MediaRecorder.OnErrorListener,MediaRecorder.OnInfoListener{

    public interface OnRecorder{

    }

    String mHostName = "192.168.16.103";
    int mPort = 9000;
    Socket mClientSocket;
    ParcelFileDescriptor mFileDescriptor;
    MediaRecorder mRecorder;
    Handler mHandler;


    public Recorder(){

    }

    public Recorder(Handler handler){
        mHandler = handler;
    }


    public void connect(){
        try{
            connectServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        try{
            connectServer();
            startRecorder();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mHandler.sendEmptyMessage(Main.RECORDING);
    }

    public void stop(){
        mRecorder.stop();
        mHandler.sendEmptyMessage(Main.IDLE);
    }

    public void reset() {

    }

    private void connectServer() throws UnknownHostException,IOException {
        mClientSocket = new Socket(InetAddress.getByName(mHostName),mPort);

        mFileDescriptor = ParcelFileDescriptor.fromSocket(mClientSocket);

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(mFileDescriptor.getFileDescriptor());
    }

    private void startRecorder() throws IOException {
        mRecorder.prepare();
        mRecorder.start();
    }

    private void onRecorderCreated(MediaRecorder recorder){
        recorder.setOnInfoListener(this);
        recorder.setOnErrorListener(this);
    }



    @Override
    public void onError(MediaRecorder mr, int what, int extra) {

    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {

    }
}
