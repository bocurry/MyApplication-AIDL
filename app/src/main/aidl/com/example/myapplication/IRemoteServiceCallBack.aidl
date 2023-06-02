// IRemoteServiceCallBack.aidl
package com.example.myapplication;

// Declare any non-default types here with import statements
import com.example.myapplication.Book;

oneway interface IRemoteServiceCallBack {
    void bookChanged();
}