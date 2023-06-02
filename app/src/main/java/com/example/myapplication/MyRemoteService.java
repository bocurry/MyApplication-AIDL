package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.myapplication.IMyAidlInterface;

import java.util.ArrayList;
import java.util.List;

public class MyRemoteService extends Service {

    public final String TAG = this.getClass().getName();
    private List<Book> books = new ArrayList<>();

    private IRemoteServiceCallBack mCallback = null;
    public final String HELLO = "this is IMyAidlInterface sayHello function";


    @Override
    public void onCreate() {
        super.onCreate();
        initBookData();
        Log.i(TAG,"onCreate");
    }

    private void initBookData() {
        books.add(new Book("math"));
        books.add(new Book("english"));
        books.add(new Book("gym"));
        books.add(new Book("story"));
    }

    private IMyAidlInterface.Stub mBinder = new IMyAidlInterface.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
            Log.d(TAG, "IMyAidlInterface basicTypes function enter");
        }

        @Override
        public int getPid() throws RemoteException {
            Log.d(TAG, "IMyAidlInterface getPid function enter");
            int pid = android.os.Process.myPid();
            int callerPid = Binder.getCallingPid();
            int callerUid = Binder.getCallingUid();
            Log.d(TAG, "callerPid = " + callerPid + ", callerUid = " + callerUid);
            return pid;
        }

        @Override
        public String sayHello() throws RemoteException {
            Log.d(TAG, "IMyAidlInterface sayHello function enter");
            int callerPid = Binder.getCallingPid();
            int callerUid = Binder.getCallingUid();
            Log.d(TAG, "callerPid = " + callerPid + ", callerUid = " + callerUid);
            return HELLO;
        }

        @Override
        public List<Book> getBookList() throws RemoteException {
            return books;
        }

        @Override
        public void addBookInOut(Book book) throws RemoteException {
            if (book != null) {
                books.add(book);
                mCallback.bookChanged();
            } else {
                Log.e(TAG, "接收到了一个空对象 InOut");
            }
        }

        @Override
        public void getClientClassName(String className) throws RemoteException{
            Log.d(TAG, "ClientClassName = " + className);
        }

        @Override
        public void registerCallback(IRemoteServiceCallBack callback) throws RemoteException{
            Log.d(TAG, "registerCallback enter" );
            mCallback = callback;
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBinder function enter");
        return mBinder;
    }
}
