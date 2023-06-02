# MyApplication-AIDL
这个仓主要是练习AIDL调用的

第一阶段
1.remoteService中实现了一个服务端，remoteService单独运行在一个进程中
2.MainActivity运行在APP的进程中，通过bindService的方式来启动remoteService并获取到binder对象。

第二阶段
使用parcel，添加自定义对象，来在client端获取自定义对象。

第三阶段
client端传输数据到Server端

第四阶段
Server端通过AIDL调用到Client端的接口


