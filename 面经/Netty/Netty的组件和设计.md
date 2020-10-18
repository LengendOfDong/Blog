# Channel、EventLoop和ChannelFuture
Channel:Socket

EventLoop:控制流、多线程处理、并发

ChannelFuture:异步通知

## Channel接口
基本的I/O操作（bind(),connect()、read()和write()依赖于底层网络传输所提供的原语）。在基于Java的网络编程中，基本的构造时class Socket。Netty的Channel接口所提供的API，大大地降低了直接使用Socket类的复杂性。

## EventLoop接口
EventLoop定义了Netty的核心抽象，用于处理连接的生命周期中所发生的事件。

Channel、EventLoop、Thread和EventLoopGroup之间的关系如下：
- 一个EventLoopGroup包含一个或者多个EventLoop
- 一个EventLoop在它的生命周期内只和一个Thread绑定
- 所有由EventLoop处理的I/O事件都将在它专有的Thread上被处理
- 一个Channel在它的生命周期内只注册一个或多个Channel
- 一个EventLoop可能会被分配给一个或者多个Channel

## ChannelFuture
ChannelFuture看作是将来要执行的操作的结果的占位符。Netty中所有的I/O操作都是异步的，因为一个操作可能不会立即返回，所以我们需要一种用于在之后的某个时间点确定其结果的方法。
