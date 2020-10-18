# Netty服务端
所有的Netty服务器都需要以下两个部分：
- 至少一个ChannelHandler，该组件实现了服务器对从客户端接收的数据的处理，即它的业务逻辑
- 引导，这是配置服务器的启动代码，至少，它会将服务器绑定到它要监听连接请求的端口上。

```java
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class EchoServer {
    private final int port;

    public EchoServer(int port){
        this.port  = port;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1){
            System.err.println("Usage: " + EchoServer.class.getSimpleName() + "<port>");
            return;
        }

        int port = Integer.parseInt(args[0]);
        new EchoServer(port).start();
    }

    private void start() throws Exception{
        final EchoServerHandler serverHandler = new EchoServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();//指定NioEventLoopGroup来接受和处理新的连接
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(NioServerSocketChannel.class)//指定使用的NIO传输channel
                    .localAddress(new InetSocketAddress(port))//使用指定的端口设置套接字地址
                    .childHandler(new ChannelInitializer<SocketChannel>() {//添加一个EchoServerHandler到子Channel的ChannelPipeline
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(serverHandler);
                        }
                    });
            ChannelFuture f = b.bind().sync();//异步地绑定服务器，调用sync()方法阻塞等待直到绑定完成
            f.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully().sync();
        }
    }
}

```
服务器实现的重要步骤：
- EchoServerHandler实现了业务逻辑
- main方法中引导了服务器

引导过程中所需要的步骤如下：
- 创建一个ServerBootStrap的实例以引导和绑定服务器
- 创建并分配一个NioEventLoopGroup实例以进行事件的处理，如接受新连接以及读写数据。
- 指定服务器绑定的本地的InetSocketAddress
- 使用一个EchoServerHandler的实例初始化每一个新的Channel
- 调用ServerBootStrap.bind()方法以绑定服务器

# Netty客户端
```java
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class EchoClient {
    private final String host;
    private final int port;
    
    public EchoClient(String host, int port){
        this.port = port;
        this.host = host;
    }
    
    public void start() throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap b = new Bootstrap();//创建BootStrap
            b.group(group)
                    .channel(NioSocketChannel.class)//适用于NIO传输的Channel类型，客户端与服务端Channle类型没有必要一致，客户端可以是OIO传输
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new EchoClientHandler());     
                        }
                    });
            ChannelFuture f = b.connect().sync();
            f.channel().closeFuture().sync();//阻塞直到Channel关闭
        }finally {
            group.shutdownGracefully().sync();//关闭线程池并释放所有的资源
        }
    }
}

```
客户端的步骤：
- 初始化客户端，创建一个Bootstrap的实例
- 为进行事件处理分配了一个
