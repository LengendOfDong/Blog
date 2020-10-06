# Zookeeper队列
zookeeper不适合做Queue,或者说zk没有实现一个好的Queue,原因有五：
- zk有1MB的传输限制，实践中Znode必须相对较小，而队列包含成千上万的消息，非常的大。
- 如果有很多节点，zk启动时相当的慢，而使用queue会导致好多ZNODE，需要显著增大initLimit和syncLimit
- ZNode很大的时候很难清理，Netflix不得不创建了一个专门的程序做这事。
- 当很大量的包含成千上万的子节点的Znode时，zk的性能变得不好
- zk的数据库完全放在内存中，大量的Quue意味着会占用很多的内存空间

## DistributedQueue
DistributedQueue是最普通的一种队列，
