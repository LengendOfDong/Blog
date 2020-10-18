# Channel
Channel是Java NIO的一个基本构造。

它代表一个到实体（如一个硬件设备、一个文件、一个网络套接字或者一个能够执行一个或者多个不同的I/O操作的程序组件）的开放连接，如读操作或者写操作。

Channel可以被看作是传入或者传出数据的载体。

# 回调
一个回调就是一个方法，一个指向已经被提供给另一个方法的方法的引用。这使得后者可以在适当的时候调用前者。是在操作完成后通知相关方最常见的方式之一。

Netty中采用回调的方式，一个回调被触发时，相关事件可以被interface-ChannelHandler的实现进行处理。
```java
public class ConnectHandler extends ChannelInBoundHandlerAdapter{

  public void channelActive(ChannelHandlerContext ctx) throws Exception{
      System.out.println("Client " + ctx.channel().remoteAddress() + " connected");
  }
}
```
以上例子，Netty采用回调的方式，在连接建立的时候会回调channelActive方法打印连接成功信息。

# Future
Future提供了另一种在操作完成时通知应用程序的方式
