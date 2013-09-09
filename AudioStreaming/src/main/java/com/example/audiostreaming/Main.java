package com.example.audiostreaming;

import android.os.Bundle;
import android.app.Activity;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import android.os.Handler;
import android.widget.TextView;

import java.util.ArrayList;

public class Main extends Activity {

    public static final int IDLE = 1; //green
    public static final int BUSY = 2; //red
    public static final int RECORDING = 3; //blue

    public static final int SCANNING = 11;
    public static final int CONNECTING = 12;
    public static final int CONNECTED = 13;

    public static final String KEY_WIFI = "wifi";
    public static final String KEY_3G = "3g";
    public static final String KEY_HOSTLIST = "hostlist";

    public static final int EVENT_WLAN_ADDRESS_ACQUIRED = 21;
    public static final int EVENT_UI_UPDATE = 22;
    public static final int EVENT_BUSY = 23;
    public static final int EVENT_DIAL_ESTABLISHED = 24;
    public static final int EVENT_DIAL_DISCONNECTED = 25;
    public static final int EVENT_RESET = 26;
    public static final int EVENT_HOSTLIST_UPDATE = 27;

    int mCurrentLayout;
    Button mButton;
    Recorder mRecoder;
    Player mPlayer;
    int mStatus;
    Handler mHandler;
    NetListener mListener;
    private HostListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    void init(){

    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.call:
                tryCall();
                break;
            case R.id.cancel:
                tryRest();
                break;
            case R.id.answer:
                tryAnswer();
                break;
            case R.id.deny:
                tryDeny();
                break;
            case R.id.record:
                tryRecord();
                break;
            case R.id.disconnect:
                tryDisconnect();
                break;
        }
    }

    public void tryCall(){
        mHandler.sendEmptyMessage(EVENT_BUSY);
        call();
    }

    void call(){
        boolean established = false;

        if(established){
            mHandler.sendEmptyMessage(EVENT_DIAL_ESTABLISHED);
        }else{
            mHandler.sendEmptyMessage(EVENT_RESET);
        }
    }

    public void tryRest(){
        reset();
    }

    void reset(){
        boolean isReset = false;

        if(isReset){
            mHandler.sendEmptyMessage(EVENT_RESET);
        }
    }

    public void tryAnswer(){
        answer();
    }

    void answer(){

    }

    public void tryDeny(){
        deny();
    }

    void deny(){

    }

    public void tryRecord(){
        record();
    }

    void record(){

    }

    public void tryDisconnect(){
        disconnect();
    }

    void disconnect(){

    }

    public void switchLayout(int id){
        findViewById(mCurrentLayout).setVisibility(View.GONE);
        findViewById(id).setVisibility(View.VISIBLE);
    }

    public void showNetworkStatus(Bundle data){
        String wifiStatus = data.getString(KEY_WIFI);
        String g3Status = data.getString(KEY_3G);

        if(wifiStatus!=null){
            TextView wifi = (TextView) findViewById(R.id.wifi_status);
            wifi.setText(wifiStatus);
        }

        if(g3Status!=null){
            TextView g3 = (TextView) findViewById(R.id.g3_status);
            g3.setText(g3Status);
        }
    }

    void tryUpdateHostlist(Bundle data){
        ArrayList<String> list = data.getStringArrayList(KEY_HOSTLIST);
        updateHostList(list);
    }

    void updateHostList(ArrayList list){
        if(mAdapter==null){
            mAdapter = new HostListAdapter();
        }
    }

    public void displayButton(int status){
        switch(status){
            case IDLE:
                mButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.sharp_rect_green));
                break;
            case BUSY:
                mButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.sharp_rect_red));
                break;
            case RECORDING:
                mButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.sharp_rect_blue));
                break;
        }
    }

    private class EventHandler extends Handler{
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case EVENT_UI_UPDATE:
                    displayButton(msg.what);
                    break;
                case EVENT_WLAN_ADDRESS_ACQUIRED:
                    showNetworkStatus(msg.getData());
                    break;
                case EVENT_HOSTLIST_UPDATE:
                    tryUpdateHostlist(msg.getData());
                    break;
            }
        }
    }
}
