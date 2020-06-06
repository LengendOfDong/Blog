# 简介
ConcurrentLinkedQueue只实现了Queue接口，并没有实现BlockingQueue接口，所以它不是阻塞队列，也不能用于线程池中，但是它是线程安全的，可用于多线程环境中。

# 总结
（1）ConcurrentLinkedQueue不是阻塞队列；

（2）ConcurrentLinkedQueue不能用在线程池中；

（3）ConcurrentLinkedQueue使用（CAS+自旋）更新头尾节点控制出队入队操作；

- ConcurrentLinkedQueue与LinkedBlockingQueue对比？

（1）两者都是线程安全的队列；

（2）两者都可以实现取元素时队列为空直接返回null，后者的poll()方法可以实现此功能；

（3）前者全程无锁，后者全部都是使用重入锁控制的；

（4）前者效率较高，后者效率较低；

（5）前者无法实现如果队列为空等待元素到来的操作；

（6）前者是非阻塞队列，后者是阻塞队列；

（7）前者无法用在线程池中，后者可以；
