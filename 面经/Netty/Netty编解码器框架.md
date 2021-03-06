# 编解码器
如果将消息看作是对于特定的应用程序具有具体含义的结构化的字节序列，即它的数据。那么编码器就是将消息转换为适合于传输的格式（最有可能的就是字节流）。而对应的解码器则是将网络字节流转换回应用程序的消息格式。

因此编码器操作出站数据，而解码器处理入站数据。

每当需要为ChannelPipeline中的下一个ChannelInBoundHandler转换入站数据时会用到。可以将多个解码器链接在一起，实现任意复杂的转换逻辑。

两个不同的用例：
- 将字节解码为消息——ByteToMessageDecoder和ReplayingDecoder
- 将一种消息类型解码为另一种——MessageToMessageDecoder

