package com.example.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class MessengerService extends Service {
    /**
     * Command to the service to display a message
     */
    static final int MSG_SAY_HELLO = 1;

    static final int MSG_SET_VALUE = 2;
    static final int MSG_REGISTER_CLIENT = 3;
    static final int MSG_UNREGISTER_CLIENT = 4;

    static final int MSG_SEND_BUNDLE = 5;


    private final String TAG = "MessengerService";

    /**
     * Handler of incoming messages from clients.
     */
    static class IncomingHandler extends Handler {
        private Context applicationContext;
        /** Keeps track of all current registered clients. */
        ArrayList<Messenger> mClients = new ArrayList<Messenger>();
        /** Holds last value set by a client. */
        int mValue = 0;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SAY_HELLO:
                    Log.d("MessengerService", "handleMessage: MSG_SAY_HELLO");
                    break;
                case MSG_REGISTER_CLIENT:
                    Log.d("MessengerService", "handleMessage: MSG_REGISTER_CLIENT");
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    Log.d("MessengerService", "handleMessage: MSG_UNREGISTER_CLIENT");
                    mClients.remove(msg.replyTo);
                    break;
                case MSG_SET_VALUE:
                    Log.d("MessengerService", "handleMessage: MSG_SET_VALUE");
                    mValue = msg.arg1;
                    for (int i = mClients.size()-1; i>=0; i--) {
                        try {
                            mClients.get(i).send(Message.obtain(null,
                                    MSG_SET_VALUE, mValue, 0));
                        } catch (RemoteException e) {
                            // The client is dead.  Remove it from the list;
                            // we are going through the list from back to front
                            // so this is safe to do inside the loop.
                            mClients.remove(i);
                        }
                    }
                    break;
                case MSG_SEND_BUNDLE:
                    Log.d("MessengerService", "handleMessage: MSG_SEND_BUNDLE");
                    String value = msg.getData().getString("name");
                    Log.d("MessengerService", "bundle key-value: " + value);
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    Messenger mMessenger;

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind enter");
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        mMessenger = new Messenger(new IncomingHandler());
        return mMessenger.getBinder();
    }
}