# NIO的三大组件
NIO的三大组件：Buffer,Channel,Selector

## Buffer组件

一个Buffer本质上是内存中的一块，我们可以将数据写入这块内存，之后从这块内存获取数据。

![Buffer](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/img/Buffer%E7%9A%84%E5%88%86%E7%B1%BB.png)

核心是最后的ByteBuffer，前面的一大串类只是包装了一下它而已，我们使用最多的通常也是ByteBuffer。

我们应该将Buffer理解为一个数组，IntBuffer、CharBuffer、DoubleBuffer等分别对应int[]、char[]、double[]等。

MappedByteBuffer用于实现内存映射文件。

操作Buffer和操作数组、类集差不多，只不过大部分时候我们都把它放到了NIO的场景里面来使用而已。

## Channel组件

所有的NIO操作都始于通道，通道是数据来源或数据写入的目的地，主要地，我们将关心java.nio包中实现的以下几个Channel：

![Channel的分类](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/img/Channel%E7%9A%84%E5%88%86%E7%B1%BB.png)

- FileChannecl:文件通道，用于文件的读和写
- DatagramChannel:用于UDP连接的接收和发送
- SocketChannel:把它理解为TCP连接通道，简单理解就是TCP客户端
- ServerSocketChannel:TCP对应的服务端，用于监听某个端口进来的请求

Channel经常翻译为通道，类似于IO中的流，用于读和写入。它与前面介绍的Buffer打交道，读操作的时候将Channel中的数据填充到Buffer中，而写操作时将Buffer中的数据写入到Channel中。

![Channel读操作](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/img/Channel%E8%AF%BB%E6%93%8D%E4%BD%9C.png)

![Channel写操作](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/img/Channel%E5%86%99%E6%93%8D%E4%BD%9C.png)

ServerSocketChannel的应用
```java
//通过ServerSocketChannel创建channel通道
ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
// 3. 为channel通道绑定监听端口
serverSocketChannel.bind(new InetSocketAddress(8000));
// 4. 设置channel为非阻塞模式
serverSocketChannel.configureBlocking(false);
```
SocketChannel的应用
```java
// 如果要是接入事件，创建socketChannel
SocketChannel socketChannel = serverSocketChannel.accept();

// 将socketChannel设置为非阻塞工作模式
socketChannel.configureBlocking(false);
```

读操作：就是将数据从Channel读到Buffer中，进行后续处理。channel.read(buf)

写操作：就是将数据从Buffer中写到Channel中，channel.write(buf)

## Selector
NIO三大组件就剩Selector了，Selector建立在非阻塞的基础之上，大家经常听到的多路复用在Java世界中指的就是它，用于实现一个线程管理多个Channel。

- 首先，我们开启一个 Selector。你们爱翻译成选择器也好，多路复用器也好。
```java
Selector selector = Selector.open();
```
- 将 Channel 注册到 Selector 上。前面我们说了，Selector 建立在非阻塞模式之上，所以注册到 Selector 的 Channel 必须要支持非阻塞模式，**FileChannel 不支持非阻塞** ，我们这里讨论最常见的 SocketChannel 和 ServerSocketChannel。
```java
// 将通道设置为非阻塞模式，因为默认都是阻塞模式的
channel.configureBlocking(false);
// 注册
SelectionKey key = channel.register(selector, SelectionKey.OP_READ);
```

register 方法的第二个 int 型参数（使用二进制的标记位）用于表明需要监听哪些感兴趣的事件，共以下四种事件：

    - SelectionKey.OP_READ

        对应 00000001，通道中有数据可以进行读取

    - SelectionKey.OP_WRITE

        对应 00000100，可以往通道中写入数据

    - SelectionKey.OP_CONNECT

        对应 00001000，成功建立 TCP 连接

    - SelectionKey.OP_ACCEPT

        对应 00010000，接受 TCP 连接


