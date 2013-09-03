package com.example.audiostreaming;

import android.os.Bundle;
import android.app.Activity;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import android.os.Handler;

public class Main extends Activity {

    public static final int IDLE = 1; //green
    public static final int BUSY = 2; //red
    public static final int RECORDING = 3; //blue

    public static final int SCANNING = 11;
    public static final int CONNECTING = 12;
    public static final int CONNECTED = 13;

    Button mButton;
    Recorder mRecoder;
    int mStatus;
    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mButton = (Button) findViewById(R.id.button);
        mRecoder = new Recorder(mHandler);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.button:
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

    private void clickRecordButton(int status){
        switch(status){
            case IDLE:
                mRecoder.start();
                break;
            case BUSY:
                mRecoder.reset();
                break;
            case RECORDING:
                mRecoder.stop();
                break;
        }
    }

    private class UiUpdateHandler extends Handler{
        @Override
        public void handleMessage(Message msg){
            displayButton(msg.what);
        }
    }
}
