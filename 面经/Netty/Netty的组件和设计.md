# Channel、EventLoop和ChannelFuture
Channel:Socket

EventLoop:控制流、多线程处理、并发

ChannelFuture:异步通知

## Channel接口
基本的I/O操作（bind(),connect()、read()和write()依赖于底层网络传输所提供的原语）。在基于Java的网络编程中，基本的构造时class Socket。Netty的Channel接口所提供的API，大大地降低了直接使用Socket类的复杂性。


