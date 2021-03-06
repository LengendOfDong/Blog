# ByteBuf的优点
ByteBuf的优点：
- 它可以被用户自定义的缓冲区类型扩展
- 通过内置的复合缓冲区类型实现透明的零拷贝
- 容量可以按需增长（类似于JDK的StringBuilder）
- 在读和写这两种模式之间切换不需要调用ByteBuffer的flip()方法
- 读写使用了不同的索引
- 支持方法的链式调用
- 支持引用计数
- 支持池化。

# ByteBuf的使用模式
堆缓冲区：最常用的ByteBuf模式是将数据存储在JVM的堆空间中。这种模式被称为支撑数组，它能在没有使用池化的情况下提供快速的分配和释放。

直接缓冲区：如果你的数据包含在一个在堆上分配的缓冲区中，那么事实上，在通过套接字发送它之前，JVM将会在内部把你的缓冲区复制到一个直接缓冲区中。

复合缓冲区：Netty通过一个ByteBuf子类即CompositeByteBuf实现了这个模式，它提供了一个将多个缓冲区表示为单个合并缓冲区的虚拟表示。

Netty使用了CompositeByteBuf来优化套接字的I/O操作，尽可能地消除了由JDK得缓冲区实现所导致的性能以及内存使用率的惩罚。

# 字节级操作
## 随机访问索引
ByteBuf的索引是从0开始的，第一个字节的索引是0，最后一个字节的索引是capacity() - 1。

## 顺序访问索引
JDK的ByteBuffer只有一个索引，需要通过flip()方法来回切换读模式和写模式。

ByteBuf被分为3个区域：
- 已经被读过的可被丢弃的字节
- 尚未被读过的字节，可读字节
- 可以添加更多字节的空间，可写字节。

discardReadBytes():将可丢弃字节分段中的空间变为可写空间

readBytes(ByteBuf dest):源缓冲区的readIndex和writeIndex都会增加相同的大小

writeBytes(ByteBuf dest):源缓冲区的readIndex和writeIndex都会增加相同的大小

mark(int readlimit):将流中的当前位置标记为指定的值。

reset()：将流重置

markReaderIndex()：标记ByteBuf中的readIndex
