##  Channel的生命周期
ChannelUnregistered: Channel已经被创建，但还未被注册到EventLoop

ChannelRegistered: Channel被注册到EventLoop

ChannelActive: Channel处于活动状态（已经连接到它的远程节点），它现在可以接收和发送数据了

ChannelInActive:Channel 没有连接到远程节点

## ChannelHandler的生命周期
ChannelHandler的生命周期方法：
- handlerAdded: 当把ChannelHandler添加到ChannelPipeline中时被调用
- handlerRemoved: 当从ChannelPipeline中移除ChannelHandler时被调用
- exceptionCaught: 当处理过程中在ChannelPipeline中有错误产生时被调用

Channel中的大部分方法都需要一个ChannelPromise参数，以便在操作完成时得到通知。ChannelPromise是ChannelFuture的一个子类，其定义了一些可写的方法，如setSuccess()和setFailure(),从而使ChannelFuture不可变。



