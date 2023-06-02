package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.IMyAidlInterface;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private IMyAidlInterface myAidlInterface;
    private final String TAG = this.getClass().getName();

    private Handler handler =  new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BOOK_CHANGED:
                    Log.d(TAG, "handleMessage: book changed!");
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };
    private final int BOOK_CHANGED = 1;

    private IRemoteServiceCallBack.Stub mCallback = new IRemoteServiceCallBack.Stub() {
        @Override
        public void bookChanged() {
            Log.d(TAG, "bookChanged");
            Message msg = handler.obtainMessage(BOOK_CHANGED);
            handler.sendMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button bindServiceButton = findViewById(R.id.button);
        bindServiceButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //bind service
                Log.d(TAG, "onClick: start bind service");
                Intent service = new Intent(MainActivity.this, MyRemoteService.class);
                bindService(service, serviceConnection, Context.BIND_AUTO_CREATE);
            }
        });


    }
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myAidlInterface = IMyAidlInterface.Stub.asInterface(service);
            Log.i(TAG,"onServiceConnected");
            try {
                //registerCallback
                myAidlInterface.registerCallback(mCallback);
                String s = myAidlInterface.sayHello();
                int i = myAidlInterface.getPid();
                Log.i(TAG,s + ", pid is " + i);
                List<Book> books = myAidlInterface.getBookList();
                printBookList(books);
                //add new book
                myAidlInterface.addBookInOut(new Book("basketball"));
                books = myAidlInterface.getBookList();
                Log.i(TAG,s + "after add new book");
                printBookList(books);
                myAidlInterface.getClientClassName(TAG);
            }catch (RemoteException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG,"onServiceDisconnected");
            myAidlInterface = null;
        }
    };

    private void printBookList(List<Book> books) {
        for (Book book : books) {
            Log.d(TAG, "getBookList: book is " + book.getName());
        }
    }
}