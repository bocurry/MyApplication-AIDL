// IMyAidlInterface.aidl
package com.example.myapplication;

// Declare any non-default types here with import statements
import com.example.myapplication.Book;
import com.example.myapplication.IRemoteServiceCallBack;

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
    int getPid();
    void getClientClassName(String className);
    String sayHello();
    List<Book> getBookList();
    //in、out、inout表示非基本数据类型的数据的走向标记，三个值都是针对服务端的（服务端接收客户端的输入和服务端输出给客户端），in表示只能输入；out表示只能输出；inout表示既能输入也能输出。
    void addBookInOut(inout Book book);
    oneway void registerCallback(IRemoteServiceCallBack callback);
}