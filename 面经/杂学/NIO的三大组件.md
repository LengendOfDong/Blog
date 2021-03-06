# NIO的三大组件
NIO的三大组件：Buffer、Channel、Selector

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

我们可以同时监听一个 Channel 中的发生的多个事件，比如我们要监听 ACCEPT 和 READ 事件，那么指定参数为二进制的 00010001 即十进制数值 17 即可。

注册方法返回值是 SelectionKey 实例，它包含了 Channel 和 Selector 信息，也包括了一个叫做 Interest Set 的信息，即我们设置的我们感兴趣的正在监听的事件集合。

- 调用 select() 方法获取通道信息。用于判断是否有我们感兴趣的事件已经发生了。

Selector 的操作就是以上 3 步，这里来一个简单的示例，大家看一下就好了。之后在介绍非阻塞 IO 的时候，会演示一份可执行的示例代码。

```java
Selector selector = Selector.open();

channel.configureBlocking(false);

SelectionKey key = channel.register(selector, SelectionKey.OP_READ);

while(true) {
  // 判断是否有事件准备好
  int readyChannels = selector.select();
  if(readyChannels == 0) continue;

  // 遍历
  Set<SelectionKey> selectedKeys = selector.selectedKeys();
  Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
  while(keyIterator.hasNext()) {
    SelectionKey key = keyIterator.next();

    if(key.isAcceptable()) {
        // a connection was accepted by a ServerSocketChannel.

    } else if (key.isConnectable()) {
        // a connection was established with a remote server.

    } else if (key.isReadable()) {
        // a channel is ready for reading

    } else if (key.isWritable()) {
        // a channel is ready for writing
    }

    keyIterator.remove();
  }
}

```

对于 Selector，我们还需要非常熟悉以下几个方法：

    select()

    调用此方法，会将上次 select 之后的准备好的 channel 对应的 SelectionKey 复制到 selected set 中。如果没有任何通道准备好，这个方法会阻塞，直到至少有一个通道准备好。

    selectNow()

    功能和 select 一样，区别在于如果没有准备好的通道，那么此方法会立即返回 0。

    select(long timeout)

    看了前面两个，这个应该很好理解了，如果没有通道准备好，此方法会等待一会

    wakeup()

    这个方法是用来唤醒等待在 select() 和 select(timeout) 上的线程的。如果 wakeup() 先被调用，此时没有线程在 select 上阻塞，那么之后的一个 select() 或 select(timeout) 会立即返回，而不会阻塞，当然，它只会作用一次。

## 小结
Buffer和数组差不多，它有position、limit、capacity几个重要属性。put()一下数据、flip()切换到读模式、然后用get()获取数据、clear()一下清空数据、重新回到put()写入数据。

Channel基本上只和Buffer打交道，最重要的接口就是channel.read(buf)和channel.write(buf)

Selector用于实现非阻塞IO。

## Reference
参考资料：https://www.javadoop.com/post/java-nio
