# NIO的三大组件
NIO的三大组件：Buffer,Channel,Selector

Channel组件：
- FileChannecl:文件通道，用于文件的读和写
- DatagramChannel:用于UDP连接的接收和发送
- SocketChannel:把它理解为TCP连接通道，简单理解就是TCP客户端
- ServerSocketChannel:TCP对应的服务端，用于监听某个端口进来的请求

Channel经常翻译为通道，类似于IO中的流，用于读和写入。它与前面介绍的Buffer打交道，读操作的时候将Channel中的数据填充到Buffer中，而写操作时将Buffer中的数据写入到Channel中。

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
