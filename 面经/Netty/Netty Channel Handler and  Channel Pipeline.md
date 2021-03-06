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

根据事件的起源，事件将会被ChannelInBoundHandler或者ChannelOutBoundHandler处理，随后，通过调用ChannelHandlerContext的实现，转发给同一超类型的下一个ChannelHandler处理。

ChannelHandlerContext：就像工作流中的上下文，能够传递处理的信息。它使得ChannelHandler能够和它的ChannelPipeline以及其他的ChannelHandler交互。ChannelHandler可以通知其所属的ChannelPipeline中的下一个ChannelHandler，甚至可以动态修改它所属的ChannelPipeline.

Netty总是将ChannelPipelINE的入站口作为头部，而将出站口作为尾端。在ChannelPipeline传播事件时，它会测试ChannelPipeline中的下一个ChannelHandler类型是否和事件的运动方向相匹配。。

ChannelPipeline保存了于Channel相关联的ChannelHandler。

ChannelPipeline可以根据需要，通过添加或者删除ChannelHandler来动态地修改

ChannelPipeline有着丰富的API用以被调用，以响应入站和出站事件。

## Channel、ChannelPipeline以及ChannelHandler、ChannelHandlerContext之间的关系
1.Channel被绑定到ChannelPipeline上

2.ChannelPipeline包括了所有的入站和出站的ChannelHandler

3.当把ChannelHandler添加到ChannelPipeline时，ChannelHandlerContext将会被创建。

```java
ChannelHandlerContext ctx = ...;
//ChannelPipeline pipeline = ctx.pipeline(); 
Channel channel = ctx.channel();
//pipeline.write(Unpooled.copiedBuffer("Netty in Action",CharsetUtil.UTF_8)); 
channel.write(Unpooled.copiedBuffer("Netty in Action",CharsetUtil.UTF_8));
```
通过上面的代码，ChannelHandlerContext绑定的channel与pipeline都可以通过write()方法写入数据，效果是一样的。

一个ChannelHandler可以从属于多个ChannelPipeline，所以它可以绑定到多个ChannelHandlerContext实例，对于这种用法（指在多个ChannelPipeline中共享同一个ChannelHandler），对应的ChannelHandler必须要使用@Sharable注解标注，否则，试图将它添加到多个ChannelPipeline时将会触发异常。

只有在确定了ChannelHandler是线程安全的时候才使用@Sharable注解。

## 异常处理
ChannelHandler.exceptionCaught()的默认实现是简单地将当前异常转发给ChannelPipeline中的下一个ChannelHandler

如果异常到达了ChannelPipeline的尾端，它将会被记录为未被处理

要想定义自定义的处理逻辑，你需要重写exceptionCaught()方法，然后你需要决定是否需要将异常传播出去。

