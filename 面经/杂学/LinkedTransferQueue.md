# 简介
LinkedTransferQueue是LinkedBlockingQueue、SynchronousQueue（公平模式）、ConcurrentLinkedQueue三者的集合体，它综合了这三者的方法，并且提供了更加高效的实现方式。

LinkedTransferQueue实现了TransferQueue接口，而TransferQueue接口是继承自BlockingQueue的，所以LinkedTransferQueue也是一个阻塞队列。

# 存储结构
LinkedTransferQueue使用了一个叫做dual data structure的数据结构，或者叫做dual queue,译为双重数据结构或者双重队列。

双重队列是什么意思呢？

放取元素使用同一个队列，队列中的节点具有两种模式，一种是数据节点，一种是非数据节点。

放元素时先跟队列头节点对比，如果头节点是非数据节点，就让他们匹配，如果头节点是数据节点，就生成一个数据节点放在队列尾端（入队）。

取元素时也是先跟队列头节点对比，如果头节点是数据节点，就让他们匹配，如果头节点是非数据节点，就生成一个非数据节点放在队列尾端（入队）。

用图形来表示就是下面这样：
![dual queue](https://github.com/LengendOfDong/Blog/blob/master/img/dual-queue.png)

