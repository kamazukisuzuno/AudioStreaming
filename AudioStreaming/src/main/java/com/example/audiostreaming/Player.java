package com.example.audiostreaming;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.ParcelFileDescriptor;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by suzuno on 13-9-3.
 */
public class Player {

    MediaPlayer mPlayer;
    Socket mClientSocket;
    ParcelFileDescriptor mFileDescriptor;
    Handler mHandler;

    public void prepare(){
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    public void play() throws IOException {
        mPlayer.setDataSource(mFileDescriptor.getFileDescriptor());
    }

    private class PrepareAsyncTask extends AsyncTask<ParcelFileDescriptor,Integer,Boolean>{


        @Override
        protected Boolean doInBackground(ParcelFileDescriptor... params) {
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress){

        }

        @Override
        protected void onPostExecute(Boolean result){

        }
    }
}
