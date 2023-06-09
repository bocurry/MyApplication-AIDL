package com.example.myapplication;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ActivityMessenger extends Activity {
    /** Messenger for communicating with the service. */
    Messenger mServiceMessenger = null;

    /** Flag indicating whether we have called bind on the service. */
    boolean bound;

    private HandlerThread handlerThread = null;

    private final String TAG = "ActivityMessenger";

    /**
     * client's messenger.server can use it to communicate to client
     */
    Messenger mMessenger = null;
    /**
     * Handler of incoming messages from service.
     */
//    static class IncomingHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case MessengerService.MSG_SET_VALUE:
//                    Log.d("ActivityMessenger", "handleMessage:MSG_SET_VALUE!");
//                    break;
//                default:
//                    super.handleMessage(msg);
//            }
//        }
//    }

    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            Log.d(TAG, "onServiceConnected!");
            mServiceMessenger = new Messenger(service);
            // We want to monitor the service for as long as we are
            // connected to it.
            try {
                Message msg = Message.obtain(null,
                        MessengerService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mServiceMessenger.send(msg);

                // Give it some value as an example.
                msg = Message.obtain(null,
                        MessengerService.MSG_SET_VALUE, this.hashCode(), 0);
                mServiceMessenger.send(msg);

                msg = Message.obtain(null,
                        MessengerService.MSG_SEND_BUNDLE, this.hashCode(), 0);
                Bundle bundle=new Bundle();
                bundle.putString("name","Tony");
                msg.setData(bundle);
                //使用
                mServiceMessenger.send(msg);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }

            // As part of the sample, tell the user what happened.
            Toast.makeText(getApplicationContext(), "remote_service_connected", Toast.LENGTH_SHORT).show();
            bound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            Log.d(TAG, "onServiceDisconnected!");
            mServiceMessenger = null;
            bound = false;
        }

        @Override
        public void onBindingDied(ComponentName name) {
            ServiceConnection.super.onBindingDied(name);
        }

        @Override
        public void onNullBinding(ComponentName name) {
            ServiceConnection.super.onNullBinding(name);
        }
    };

    public void sayHello() {
        Log.d(TAG, "sayHello enter!");
        if (!bound) return;
        // Create and send a message to the service, using a supported 'what' value
        Message msg = Message.obtain(null, MessengerService.MSG_SAY_HELLO, 0, 0);
        try {
            mServiceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate！!");

        setContentView(R.layout.activity_messenger);
        Button button = findViewById(R.id.sayHello);
        Button button2 = findViewById(R.id.bindMessenger);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick sayHello!");
                sayHello();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Bind to the service
                Log.d(TAG, "onClick bindMessenger!");
                Intent service = new Intent(ActivityMessenger.this, MessengerService.class);
                bindService(service, mConnection, Context.BIND_AUTO_CREATE);
            }
        });
        handlerThread = new HandlerThread("worker");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        Handler workHandler = new Handler(looper) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MessengerService.MSG_SET_VALUE:
                        Log.d("ActivityMessenger", "handleMessage:MSG_SET_VALUE!");
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        };
        mMessenger = new Messenger(workHandler);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart！!");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (bound) {
            unbindService(mConnection);
            bound = false;
        }
    }
}
