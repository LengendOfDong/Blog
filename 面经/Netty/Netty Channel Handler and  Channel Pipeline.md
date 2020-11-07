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

可以使用ChannelInBoundHandlerAdapter和ChannelOutBoundHandlerAdapter作为自己的ChannelHandler的起始点。

## 资源管理
泄露检测级别：
- DISABLED:禁用泄露检测，只有在详尽的测试之后才应设置这个值
- SIMPLE:使用1%的默认采样率检测并报告任何发现的泄露，这是默认级别，适合绝大部分的情况。
- ADVANCED:使用默认的采样率，报告所发现的任何泄露以及对应的消息被访问的位置
- PARANOID:类似于ADVANCED，但是其将会对每次（对消息的）访问都进行采样，这对性能将会有很大的影响，应该只是在调试阶段使用

## ChannelPipeline
每一个新创建的Channel都会被分配一个ChannelPipeline,这项关联是永久性的，Channel既不能附加另外一个ChannelPipeline,也不能分离其当前的。
