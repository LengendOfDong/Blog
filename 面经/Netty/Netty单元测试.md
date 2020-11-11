# EmbeddedChannel
将入站数据或者出站数据写入到EmbeddedChannel中，然后检查是否有任何东西叨叨了ChannelPipeline的尾端。以这种方式，可以确定消息是否已经被编码或者被解码过了，以及是否触发了任何的ChannelHandler动作。

在每种情况下，消息都将会传递过ChannelPipeline，并且被相关的ChannelInboundHandler或者ChannelOutboundHandler处理。如果消息没有被消费，那么可以使用readInbound()或者readOutbound()方法来处理过了这些消息之后，将他们打印出来。

绝对值编码器AbsIntegerHandler:
```java
public class AbsIntegerEncoder extends MessageToMessageEncoder<ByteBuf> {

    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        while (msg.readableBytes() >= 4) {   //检查是否有足够的字节用来编码
            int value = Math.abs(msg.readInt());          //从输入得ByteBuf中读取下一个整数，并且计算其绝对值
            out.add(value);                    //将该整数写入到编码消息的List中
        }
    }
}
```

测试AbsIntegerHandler
```java
public class AbsIntegerEncoderTest {
    @Test
    public void testEncoded() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 1; i < 10; i++) {
            buf.writeInt(i * -1);
        }

        EmbeddedChannel channel = new EmbeddedChannel(new AbsIntegerEncoder());//创建一个EmbeddedChannel，并安装一个要测试的AbsIntegerEncoder
        assertTrue(channel.writeOutbound(buf));      //写入ByteBuf并断言调用readOutBound()方法将会产生数据
        assertTrue(channel.finish());                //判断Channel已经完成

        //read bytes
        for (int i = 1; i < 10; i++) {
            assertEquals(i, channel.readOutbound());        //每循环一次就进行一次读取，每次读取四个字节，即一个整数。此时整数正好与i相等。
        }
        assertNull(channel.<String>readOutbound());         //最后读取完毕，剩下为空。
    }
}

```
以上例子分为五步：
- 将4个字节的负整数放入到一个ByteBuf中
- 创建一个EmbeddedChannel，并将AbsIntegerHandler放入其中
- 通过调用EmbeddedChannel的writeOutBound()方法来写入出站数据
- 查看channel是否是已完成状态
- 通过调用EmbeddedChannel的readOutBound()方法来读取出站数据，是否和绝对值相同。
