# NIO底层工作原理

1.这一步其实是当我们刚开始初始化这个buffer数组的时候，开始默认是这样的

![NIO1](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/img/NIO1.png)

2. 但是当你往buffer数组中开始写入的时候几个字节的时候就会变成下面的图，position会移动你数据的结束的下一个位置，这个时候你需要把buffer中的数据写到channel管道中，所以此时我们就需要用这个buffer.flip();方法

   ![NIO2](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/img/NIO2.png)

3. 当你调用完2中的方法时，这个时候就会变成下面的图了，这样的话其实就可以知道你刚刚写到buffer中的数据是在position---->limit之间，然后下一步调用clear（）

![nio3](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/img/NIO3.png)

4、这时底层操作系统就可以从缓冲区中正确读取这 5 个字节数据发送出去了。在下一次写数据之前我们在调一下 clear() 方法。缓冲区的索引状态又回到初始位置。（其实这一步有点像IO中的把转运字节数组 char[] buf = new char[1024]; 不足1024字节的部分给强制刷新出去的意思） 

![NIO4](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/img/NIO1.png)

NIO的工作流程步骤：

    1.首先是先创建ServerSocketChannel 对象，和真正处理业务的线程池
    2.然后给刚刚创建的ServerSocketChannel 对象进行绑定一个对应的端口，然后设置为非阻塞
    3.然后创建Selector对象并打开，然后把这Selector对象注册到ServerSocketChannel 中，并设置好监听的事件，监听 SelectionKey.OP_ACCEPT
    4.接着就是Selector对象进行死循环监听每一个Channel通道的事件，循环执行 Selector.select() 方法，轮询就绪的 Channel
    5.从Selector中获取所有的SelectorKey（这个就可以看成是不同的事件），如果SelectorKey是处于 OP_ACCEPT 状态，说明是新的客户端接入，调用 ServerSocketChannel.accept 接收新的客户端。
    6.然后对这个把这个接受的新客户端的Channel通道注册到ServerSocketChannel上，并且把之前的OP_ACCEPT 状态改为SelectionKey.OP_READ读取事件状态，并且设置为非阻塞的，然后把当前的这个SelectorKey给移除掉，说明这个事件完成了
    7.如果第5步的时候过来的事件不是OP_ACCEPT 状态，那就是OP_READ读取数据的事件状态，然后调用本文章的上面的那个读取数据的机制就可以了
NIO示例代码如下：

```java
public class TestChannel {
    public static void main(String[] args) throws IOException {
        //先给缓冲区申请内存空间
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //打开Selector为了它可以轮询每个Channel的状态
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //设置为非阻塞方式
        ssc.configureBlocking(false);
        //绑定端口
        ssc.socket().bind(new InetSocketAddress(8080));
        //注册监听的事件
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        //从整个循环来看，遍历所有的key之后，总共两个操作
        //第一个，将OP_ACCEPT的key，通过建立通道以及监听，进行OP_READ
        //第二个，将OP_READ的key,将buffer中的数据写到Channel中。
        while (true) {
            //取得所有key集合
            Set selectedKey = selector.selectedKeys();
            //开始遍历每个key
            Iterator it = selectedKey.iterator();
            while (it.hasNext()) {
                //SelectionKey
                SelectionKey key = (SelectionKey) it.next();
                if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
                    //建立通道
                    ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
                    //接收到服务端的请求
                    SocketChannel sc = ssChannel.accept();
                    //设置为非阻塞的方式
                    sc.configureBlocking(false);
                    //监听selector的读事件
                    sc.register(selector, SelectionKey.OP_READ);
                } else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
                    SocketChannel sc = (SocketChannel) key.channel();
                    while (true) {
                        //先进行清空，limit指针和operation指针都回到对应位置，limit到capacity，position回到0
                        buffer.clear();
                        //读取buffer中的数据
                        int n = sc.read(buffer);
                        //没有读取到数据就退出循环
                        if(n <= 0){
                            break;
                        }
                        //读取到数据，就需要将buffer中的数据写到Channel中
                        buffer.flip();
                    }
                }
                it.remove();
            }
        }
    }
}
```

