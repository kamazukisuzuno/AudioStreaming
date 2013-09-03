package com.example.audiostreaming;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by suzuno on 13-9-3.
 */
public class NetListener {

    public static final int LISTENING_PORT = 9000;
    public static final int CONNECT_PORT = 9001;

    ServerSocket mServer;

    public void startListening(){
    }

    private void prepareServerSocket() throws IOException {
        mServer = new ServerSocket(LISTENING_PORT);
        while(true){

        }
    }

    private class ListeningThread extends Thread{

    }
}
