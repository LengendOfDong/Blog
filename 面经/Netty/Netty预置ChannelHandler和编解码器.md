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
