# 简介
PriorityBlockingQUeue是java并发包下的优先级阻塞队列，它是线程安全的。

# 总结
（1）PriorityBlockingQueue整个入队出队的过程与PriorityQueue基本是保持一致的；

（2）PriorityBlockingQueue使用一个锁+一个notEmpty条件控制并发安全；

（3）PriorityBlockingQueue扩容时使用一个单独变量的CAS操作来控制只有一个线程进行扩容；

（4）入队使用自下而上的堆化；

（5）出队使用自上而下的堆化；


- 为什么PriorityBlockingQueue不需要notFull条件？

因为PriorityBlockingQueue在入队的时候如果没有空间了是会自动扩容的，也就不存在队列满了的状态，也就是不需要等待通知队列不满了可以放元素了，所以也就不需要notFull条件了。也就是说队列是一直不满的。
