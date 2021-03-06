# IO模型
一个输入操作通常包括两个阶段：
- 等待数据准备好
- 从内核向进程复制数据

对于一个套接字上的输入操作，第一步通常涉及等待数据从网络中到达。当所等待数据到达时，它被复制到内核中的某个缓冲区。第二步就是把内核缓冲区复制到应用进程缓冲区。

Unix有五种I/O模型：
- 阻塞式I/O
- 非阻塞式I/O
- I/O复用（select和poll)
- 信号驱动式(SIGIO)
- 异步IO（AIO)

## 阻塞式I/O
应用程序被阻塞，直到数据从内核缓冲区复制到应用程序缓冲区中才返回。

在阻塞的过程中，其它应用程序还可以执行，因此阻塞不意味着整个操作系统都阻塞。因为其它应用程序还可以执行，所以不消耗CPU时间，这种模型的CPU利用率会比较高。


## 非阻塞式I/O
应用进程执行系统调用之后，内核返回一个错误码，应用程序可以继续执行，但是需要不断的执行系统调用来获知I/O是否完成，这种方式称为轮询（polling).

由于CPU要处理更多的系统调用，因此这种模型的CPU利用率比较低。

## I/O复用
使用select或者poll等待数据，并且可以等待多个套接字中的任何一个变为可读。这一过程会被阻塞，当某一个套接字可读时返回，之后再使用recvfrom把数据从内核复制到进程中。

它可以让单个进程具有处理多个I/O事件的能力。又被称为Event Driven I/O,即事件驱动I/O。

如果一个Web服务器没有I/O复用，那么每个Socket连接都需要创建一个线程去处理，如果同时有几万个连接，那么就需要创建相同数量的线程。相比于多线程和多线程技术，I/O复用不需要进程创建和切换的开销，系统开销更小。

## 信号驱动I/O
应用进程使用sigaction系统,内核立即返回，应用进程可以继续执行，也就是说等待数据阶段应用程序是非阻塞的。内核在数据到达时向应用程序发送SIGIO信号，应用进程收到之后在信号处理程序中调用recvfrom将数据从内核复制到引用程序中。

## 异步I/O
应用程序执行aio_read系统调用会立即返回，应用进程可以继续执行，不会被阻塞，内核会在所有操作完成之后向应用进程发送信号。

异步I/O与信号驱动I/O的区别在于，异步I/O的信号是通知应用进程I/O完成，而信号驱动I/O的信号是通知应用进程可以开始I/O。

## 五大I/O模型比较
- 同步I/O：将数据从内核缓冲区复制到应用进程缓冲区的阶段（第二阶段），应用进程会阻塞。
- 异步I/O：第二阶段应用进程不会阻塞

同步I/O包括阻塞式I/O、非阻塞式I/O、I/O复用和信号驱动I/O，他们的主要区别在第一个阶段。
非阻塞式I/O、信号驱动I/O和异步I/O在第一阶段不会阻塞。


