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

