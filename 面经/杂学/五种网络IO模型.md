# 五种网络IO模型
IO模型有5种之多：阻塞IO，非阻塞IO，IO复用，信号驱动IO，异步IO。

blocking IO的特点就是在IO执行的两个阶段（等待数据和拷贝数据两个阶段）都被block了。

在非阻塞式IO中，用户进程其实是需要不断的主动询问kernel数据准备好了没有

循环调用recv()将大幅度推高CPU 占用率；此外，在这个方案中recv()更多的是起到检测“操作是否完成”的作用，实际操作系统提供了更为高效的检测“操作是否完成“作用的接口，例如select()多路复用模式，可以一次检测多个连接是否活跃。

在多路复用模型中，对于每一个socket，一般都设置成为non-blocking，但是，如上图所示，整个用户的process其实是一直被block的。只不过process是被select这个函数block，而不是被socket IO给block。因此select()与非阻塞IO类似。

前四种都属于同步IO，阻塞IO不必说了。非阻塞IO，IO请求时立即返回，IO没有就绪会返回错误，需要请求进程主动轮询不断发IO请求直到返回正确。IO复用同非阻塞IO本质一样，不过利用了新的select系统调用，由内核来负责本来是请求进程该做的轮询操作。看似比非阻塞IO还多了一个系统调用开销，不过因为可以支持多路IO，才算提高了效率。信号驱动IO，调用sigaltion系统调用，当内核中数据就绪时以SIGIO信号通知请求进程，请求进程再把数据从内核读入到用户空间，这一步是阻塞的。异步IO，如定义所说，不会因为IO操作阻塞，IO操作全部完成才通知请求进程。

## 阻塞和非阻塞的区别
调用阻塞IO会一直阻塞住对应的进程直到操作完成，而非阻塞IO在kernel还在准备数据的情况下会立刻返回。

## 同步和异步的区别
```
 * A synchronous I/O operation causes the requesting process to be blocked until that I/O operation completes;
 * An asynchronous I/O operation does not cause the requesting process to be blocked;
```
两者的区别就在于同步IO做“IO Operation”会将process阻塞。按照这个定义，之前所述的阻塞IO，非阻塞IO，多路复用IO都属于同步IO。非阻塞IO其实也被阻塞了，因为定义中所指的“IO Operation”是指真实的IO操作，就是例子中的recvfrom这个系统调用。非阻塞IO在执行recvfrom这个系统调用的时候，如果kernel的数据没有准备好，这时候不会block进程。但是当kernel中数据准备好的时候，recvfrom会将数据从kernel拷贝到用户内存中，这个时候进程是被block了，在这段时间内进程是被block的。

## HTTP Server
- HTTP Server 1.0:先在80端口进行监听，然后进入无限循环，如果有连接请求了，就接受（accept),创建新的socket,最后才可以通过这个socket来接收和发送HTTP数据。
- HTTP Server 2.0:当接收连接后，对于这个新的socket,不在主进程里处理，而是新创建子进程来接管。这样主进程就不会阻塞在receive上，可以继续接收新的连接了。
- HTTP Server 3.0:select模型，一个socket连接就是一个所谓的文件描述符（File Descriptor即fd,是一个整数），使用一个进程来表示对它的读写操作，实在是浪费。每个socket都有fd编号，每次HTTP Server和操作系统之间传递fd_set，去查询socket有没有数据，需要动态地维护这个集合，进行socket的增加或者减少。操作系统内核对需要处理的socket进行了标记，HTTP Server在检查每个socket后，对于有标记的进行处理，调用read操作，将数据从内核拷贝到用户进程。可见HTTP Server在查询的时候是在阻塞的，同时在处理socket的时候也是阻塞的。
- HTTP Server 4.0：epoll模型，select模型每次最多处理1024个socket,并且需要遍历这么多socket fd，只为了找到哪些需要处理。epoll模型可以先给操作系统内核传递一份需要监控的socket fd集合。操作系统内核只返回需要处理的fd,此时仅需要处理操作系统发来的socket。

## 非阻塞IO和异步IO
非阻塞IO和异步IO的区别还是很明显的，在非阻塞IO中，虽然进程大部分时间都不会被block,但是它仍然要求进程去主动的check，并且当数据准备完成以后，也需要进程主动的再次调用recvfrom来将数据拷贝到用户内存。而异步IO则完全不同，它就像是用户进程将整个IO操作交给了他人（kernel）完成，然后他人做完后发信号通知。在此期间，用户进程不需要去检查IO操作的状态，也不需要主动的去拷贝数据。

参考文档：

https://www.cnblogs.com/findumars/p/6361627.html
