# 空闲的连接和超时
检测空闲连接和超时对于及时释放资源来说是至关重要的。

```java
public class IdleStateHandlerInitializer extends ChannelInitializer<Channel> {
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new IdleStateHandler(0,0,60, TimeUnit.SECONDS));
        pipeline.addLast(new HeartBeatHandler());
    }

    public static final class HeartBeatHandler extends ChannelInboundHandlerAdapter {
        private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("HEARTBEAT", CharsetUtil.ISO_8859_1));

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate())
                        .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            } else {
                super.userEventTriggered(ctx, evt); //不是IdleStateEvent事件则将它传递给下一个ChannelInboundHandler
            }
        }
    }
}
```
如果连接超过60秒没有接收或者发送任何的数据，那么IdleStateHandler将会使用一个IdleStateEvent事件来调用fireUserEventTriggered()方法。HeartBeatHandler实现了UserEventTriggered()方法，如果这个方法检测到IdleStateEvent事件，它将会发送心跳消息，并且添加一个将在发送操作失败时关闭该链接的ChannelFutureListener.

# 写大型数据
在写大型数据时，需要准备好处理到远程节点的连接时慢速连接的情况，这种情况会导致内存释放的延迟。

NIO的零拷贝特性，这种特性消除了将文件从文件系统移动到网络栈的复制过程。所有的这一切都发生在Netty的核心中，所以应用程序所需要做的就是使用一个FileRegion接口的实现。

