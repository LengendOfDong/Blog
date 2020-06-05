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

# 总结
（1）LinkedTransferQueue可以看作LinkedBlockingQueue、SynchronousQueue（公平模式）、ConcurrentLinkedQueue三者的集合体；

（2）LinkedTransferQueue的实现方式是使用一种叫做双重队列的数据结构；

（3）不管是取元素还是放元素都会入队；

（4）先尝试跟头节点比较，如果二者模式不一样，就匹配它们，组成CP，然后返回对方的值；

（5）如果二者模式一样，就入队，并自旋或阻塞等待被唤醒；

（6）至于是否入队及阻塞有四种模式，NOW、ASYNC、SYNC、TIMED；

（7）LinkedTransferQueue全程都没有使用synchronized、重入锁等比较重的锁，基本是通过 自旋+CAS 实现；

（8）对于入队之后，先自旋一定次数后再调用LockSupport.park()或LockSupport.parkNanos阻塞；

- LinkedTransferQueue与SynchronousQueue（公平模式）有什么异同呢？

（1）在java8中两者的实现方式基本一致，都是使用的双重队列；

（2）前者完全实现了后者，但比后者更灵活；

（3）后者不管放元素还是取元素，如果没有可匹配的元素，所在的线程都会阻塞；

（4）前者可以自己控制放元素是否需要阻塞线程，比如使用四个添加元素的方法就不会阻塞线程，只入队元素，使用transfer()会阻塞线程；

（5）取元素两者基本一样，都会阻塞等待有新的元素进入被匹配到；
