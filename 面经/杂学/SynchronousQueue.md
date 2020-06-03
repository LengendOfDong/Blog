# 简介
SynchronousQueue是java并发包下无缓冲阻塞队列，它用来在两个线程之间移交元素，但是它有个很大的问题。

# 主要构造方法
```java
public SynchronousQueue() {
    // 默认非公平模式
    this(false);
}

public SynchronousQueue(boolean fair) {
    // 如果是公平模式就使用队列，如果是非公平模式就使用栈
    transferer = fair ? new TransferQueue<E>() : new TransferStack<E>();
}
```
（1）默认使用非公平模式，也就是栈结构；

（2）公平模式使用队列，非公平模式使用栈；

# 总结

（1）SynchronousQueue是java里的无缓冲队列，用于在两个线程之间直接移交元素；

（2）SynchronousQueue有两种实现方式，一种是公平（队列）方式，一种是非公平（栈）方式；

（3）栈方式中的节点有三种模式：生产者、消费者、正在匹配中；

（4）栈方式的大致思路是如果栈顶元素跟自己一样的模式就入栈并等待被匹配，否则就匹配，匹配到了就返回；

- SynchronousQueue真的是无缓冲的队列吗？

通过源码分析，我们可以发现其实SynchronousQueue内部或者使用栈或者使用队列来存储包含线程和元素值的节点，如果同一个模式的节点过多的话，它们都会存储进来，且都会阻塞着，所以，严格上来说，SynchronousQueue并不能算是一个无缓冲队列。

- SynchronousQueue有什么缺点呢？

试想一下，如果有多个生产者，但只有一个消费者，如果消费者处理不过来，是不是生产者都会阻塞起来？反之亦然。

这是一件很危险的事，所以，SynchronousQueue一般用于生产、消费的速度大致相当的情况，这样才不会导致系统中过多的线程处于阻塞状态。
