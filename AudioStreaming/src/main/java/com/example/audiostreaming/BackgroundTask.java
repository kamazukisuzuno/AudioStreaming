package com.example.audiostreaming;

import android.os.AsyncTask;

/**
 * Created by suzuno on 13-9-9.
 */
public class BackgroundTask extends AsyncTask<Task,Integer,Boolean>{

    @Override
    protected Boolean doInBackground(Task... tasks) {
        return false;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {

    }

    @Override
    protected void onPreExecute(){

    }

    @Override
    protected void onPostExecute(Boolean result) {
    }
}
