# 线程IO模型
Redis是单线程程序，Nodejs是单线程程序，Nginx是单线程程序，都是服务器高性能的典范。

## 阻塞IO
调用套接字的读写方法，默认是阻塞的，read方法要传递进去一个参数n,表示最多读取n个字节后再返回，如果一个字节都没有，线程就会卡在那里，直到新的数据到来或者连接关闭。read方法才可以返回，线程才能继续处理。

write方法一般来说不会阻塞，除非内核为套接字分配的写缓冲区已经满了，write方法就会阻塞，直到缓冲区中有空间空闲出来。

## 非阻塞IO
非阻塞IO在套接字对象上提供了一个选项Non_Blocking,当这个选项打开时，读写方法不会阻塞，而是能读多少读多少，能写多少写多少。能读多少取决于内核为套接字分配的读缓冲区内部的数据字节数，能写多少取决于内核为套接字分配的写缓冲区的空闲空间字节数。

有了非阻塞IO意味着线程在读写IO时可以不必再阻塞了，读写可以瞬间完成，然后线程就可以继续干别的事情了。

## 事件轮询（多路复用）
最简单的事件轮询API是select函数，是操作系统给用户程序的API。

如果没有任何事件到来，那么在timeout之后自动返回；如果有事件到来，就立即返回。
```java
read_events, write_events = select(read_fds, write_fds, timeout)
for event in read_events:
    handle_read(event.fd)
for event in write_events:
    handle_write(event.fd)
handle_others()       #处理其他事情，如定时任务等
```
现代操作系统的多路复用API已经不再使用select系统调用，而改用epoll（linux）和kqueue(FreeBSD和Macosx)

事件轮询API就是Java语言里面的NIO技术。Java的NIO并不是Java特有的技术，其他计算机语言都有这个技术，只是不叫这个名字罢了。

## 指令队列
Redis会将每个客户端套接字都关联一个指令队列，客户端的指令通过队列来排队进行顺序处理，先到先服务。
