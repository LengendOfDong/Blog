# SslHandler
在大多数情况下，SslHandler将是ChannelPipeline中的第一个ChannelHandler，这确保了只有在所有其他的ChannelHandler将它们的逻辑应用到数据之后，才会进行加密。
```java
public class SslChannelHandler extends ChannelInitializer<Channel> {
    private final SslContext context;
    private final boolean startTls;
    
    //startTls设置为true，第一个写入的消息将不会被加密（客户端应该设置为true）
    public SslChannelHandler(SslContext context, boolean startTls) {//传入要使用的SslContext
        this.context = context;
        this.startTls = startTls;
    }
    
    protected void initChannel(Channel ch) throws Exception {
        //对于每个SslHandler实例，都使用Channel的ByteBuf Allocator从SslContext获取一个新的SSLEngine
        SSLEngine engine = context.newEngine(ch.alloc());
        //将SslHandler作为第一个ChannelHandler添加到ChannelPipeline中
        ch.pipeline().addFirst("ssl", new SslHandler(engine, startTls));
    }
}
```

# Http/Https应用程序
一个完整的HTTP请求包含：
- 第一个部分包含了HTTP请求的头部信息
- HTTPContent包含了数据，后面可能还跟着一个或者多个HttpContent部分
- LastHttpContent标记流该HTTP请求的结束，可能还包含了尾随的HTTP头部信息

一个HTTP的响应包含：
- HTTP响应的第一个部分包含了HTTP的头部信息
- HTTPContent包含了响应数据，后面可能还跟着一个或者多个HttpContent部分
- LastHttpContent标记了该HTTP响应的结束，可能还包含了尾随的HTTP头部信息

HTTP的请求和响应编码器和解码器无非就是将字节与HttpRequest(HttpResponse)、HttpContent和LastHttpContent之间进行相互转换。将接收到的字节转换成有意义的请求头，请求内容数据以及请求结束数据。或者将响应头信息、响应内容数据以及响应结束信息转换成字节方便传输。

```java
public class HttpPipelineInitializer  extends ChannelInitializer<Channel> {
    
    private final boolean client;
    
    public HttpPipelineInitializer(boolean client) {
        this.client = client;
    }
    
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (client) {
            //如果是客户端，则添加HttpResponseDecoder以处理来自服务器的响应
            pipeline.addLast("decoder", new HttpResponseDecoder());
            //如果是客户端，则添加HttpRequestEncoder以向服务器发送请求
            pipeline.addLast("encoder", new HttpRequestDecoder());
        } else {
            //如果是服务器，则添加HttpRequestDecoder以处理来自客户端的请求
            pipeline.addLast("decoder", new HttpRequestDecoder());
            //如果是服务器，则添加HttpResponseEncoder以向客户端发送响应。
            pipeline.addLast("encoder", new HttpResponseEncoder());
        }
    }
}
```

# 聚合HTTP消息
Netty提供了一个聚合器，可以将多个消息合并为FullHttpRequest或者FullHttpResponse消息。通过这样的方式，总是可以看到完整的消息内容

由于消息分段需要缓冲，直到可以转发一个完整的消息给下一个ChannelInBoundHandler，所以这个操作有轻微的开销。但是带来的好处就是不用关心消息碎片了。
```java
public class HttpAggregatorInitializer  extends ChannelInitializer<Channel> {
    
    private final boolean isClient;
    
    public HttpAggregatorInitializer(boolean isClient) {
        this.isClient = isClient;
    }
    
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (isClient) {//如果是客户端，则添加HttpClientCodec
            pipeline.addLast("codec", new HttpClientCodec());
        } else {//如果是服务器，则添加HttpServerCodec
            pipeline.addLast("codec", new HttpServerCodec());
        }
        //将最大的消息大小为512KB的HttpObjectAggregator添加到ChannelPipeline
        pipeline.addLast("aggregator", new HttpObjectAggregator(512 * 1024));
    }
}
```

# Http压缩
当使用HTTP时，建议开启压缩功能以尽可能多地减小传输数据的大小，虽然压缩会带来CPU时钟周期上的开销，但是对于文本数据来说，它是一个很好的主意。
```java
public class HttpCompressionInitializer extends ChannelInitializer<Channel> {

    private final boolean isClient;

    public HttpCompressionInitializer(boolean isClient) {
        this.isClient = isClient;
    }

    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (isClient) {
            pipeline.addLast("codec", new HttpClientCodec());
            //如果是客户端，则添加以处理来自服务器的压缩内容
            pipeline.addLast("decompressor", new HttpContentDecompressor());
        } else {
            pipeline.addLast("codec", new HttpServerCodec());
            //如果是服务端，则添加以压缩数据
            pipeline.addLast("compressor", new HttpContentCompressor());
        }
    }
}
```

# 使用HTTPS
启用HTTPS只需要将SSLhandler添加到Channel的ChannelPipeline中即可。
```java
public class HttpsCodecInitializer extends ChannelInitializer<Channel> {
    private final SslContext context;
    private final boolean isClient;
    
    public HttpsCodecInitializer(SslContext context, boolean isClient) {
        this.context = context;
        this.isClient = isClient;
    }
    
    
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        SSLEngine engine = context.newEngine(ch.alloc());
        pipeline.addFirst("ssl", new SslHandler(engine));
        
        if(isClient) {
            pipeline.addLast("codec", new HttpClientCodec());
        } else  {
            pipeline.addLast("codec", new HttpServerCodec());
        }
    }
}
```

# WebSocket
- BinaryWebSocketFrame:数据帧，二进制数据
- TextWebSocketFrame:数据帧，文本数据
- ContinuationWebSocketFrame:数据帧：属于上一个BinaryWebSocketFrame或者TextWebSocketFrame的文本或者二进制数据
- CloseWebSocketFrame:控制帧，一个CLOSE请求，关闭的状态和关闭的原因
- PingWebSocketFrame:控制帧，请求一个PingWebSocketFrame
- PongWebSocketFrame:控制帧，对PingWebSocketFrame请求的响应。
