# 查看面经中高频的类
比如ReentrantLock,ThreadPoolExecutor,ConcurrentHashMap,HashMap等这些类的源码

# Java基础SE
- [hashcode与equals区别与关系](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/hashcode%E4%B8%8Eequals%E6%96%B9%E6%B3%95%E7%9A%84%E5%8C%BA%E5%88%AB%E4%B8%8E%E8%81%94%E7%B3%BB.md)
- [RuntimeException、Exception、Error和Throwable](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/RuntimeException%E3%80%81Exception%E3%80%81Error%E5%92%8CThrowable.md)
- [如何使用异常处理机制](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/%E5%A6%82%E4%BD%95%E4%BD%BF%E7%94%A8%E5%BC%82%E5%B8%B8%E5%A4%84%E7%90%86%E6%9C%BA%E5%88%B6.md)
- [注解的使用](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/java%E6%B3%A8%E8%A7%A3%E7%9A%84%E8%87%AA%E5%AE%9A%E4%B9%89%E5%92%8C%E4%BD%BF%E7%94%A8.md)
- [五种常见的网络IO模型](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/%E4%BA%94%E7%A7%8D%E7%BD%91%E7%BB%9CIO%E6%A8%A1%E5%9E%8B.md)
- [hashMap与hashtable的区别](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/hashmap%E4%B8%8Ehashtable%E7%9A%84%E5%8C%BA%E5%88%AB.md)
concurrenthashmap与hashtable的区别
- [select,poll,epoll的区别](https://github.com/LengendOfDong/Blog/edit/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/select,poll,epoll%E7%9A%84%E5%8C%BA%E5%88%AB.md)
- [为什么使用红黑树而不使用AVL树](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/AVL%E6%A0%91%E5%92%8C%E7%BA%A2%E9%BB%91%E6%A0%91%E7%9A%84%E5%8C%BA%E5%88%AB.md)
- [HashMap 的并发不安全体现在哪](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/HashMap%E7%9A%84%E7%BA%BF%E7%A8%8B%E4%B8%8D%E5%AE%89%E5%85%A8.md)
- HashMap 在扩容时读写操作
  - 以参考rehash的做法。逐步迁移数据到一个新的hash表，然后最终的时候，数据全部转移到新表。删除旧表
- poll()方法和remove()方法的区别
  - poll和remove都是从队列中取出一个元素，不过队列为空时，poll返回空，而remove会抛异常
- [linkedList与ArrayList的区别](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/ArrayList%E4%B8%8ELinkedList%E7%9A%84%E5%8C%BA%E5%88%AB.md)
- [CopyOnWriteArrayLst](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/CopyOnWriteArrayList.md)
- [ArrayList](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/ArrayList.md)
- [LinkedList](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/LinkedList.md)
- [HashMap](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/HashMap.md)
- [史上最全Java集合](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/%E5%8F%B2%E4%B8%8A%E6%9C%80%E5%85%A8Java%E9%9B%86%E5%90%88%E5%9B%BE.md)
- [LinkedHashMap](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/LinkedHashMap.md)
- [WeakHashMap](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/WeakHashMap.md)
- [左旋与右旋的实现](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/%E5%B7%A6%E6%97%8B%E4%B8%8E%E5%8F%B3%E6%97%8B%E7%9A%84%E5%AE%9E%E7%8E%B0.md)
- [红黑树的树化过程](https://www.cnblogs.com/finite/p/8251587.html)
- [TreeMap](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/TreeMap.md)
- [HashSet](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/HashSet.md)
- [LinkedHashSet](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/LinkedHashSet.md)
- [TreeSet](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/TreeSet.md)
- [CopyOnWriteArraySet](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/CopyOnWriteArraySet.md)
- [ConcurrentSkipListSet](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/ConcurrentSkipListSet.md)
- [ArrayBlockingQueue](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/ArrayBlockingQueue.md)
- [PriorityQueue](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/PriorityQueue.md)
- [LinkedBlockingQueue](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/LinkedBlockingQueue.md)
- [SynchronousQueue](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/SynchronousQueue.md)
- [PriorityBlockingQueue](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/PriorityBlockingQueue.md)
- [LinkedTransferQueue](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/LinkedTransferQueue.md)
- [ConcurrentLinkedQueue](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/ConcurrentLinkedQueue.md)
- [DelayQueue](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/DelayQueue.md)
- [ArrayDeque](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/ArrayDequeue.md)
- [ConcurrentSkipListMap](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/ConcurrentSkipListMap.md)
- [ConcurrentHashMap](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/ConcurrentHashMap.md)

# 操作系统
- [进程间通信的方式](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/%E8%BF%9B%E7%A8%8B%E9%97%B4%E9%80%9A%E4%BF%A1%E6%96%B9%E5%BC%8F.md)
- [进程同步的方式](https://github.com/LengendOfDong/Blog/edit/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/%E8%BF%9B%E7%A8%8B%E5%90%8C%E6%AD%A5%E7%9A%84%E6%96%B9%E5%BC%8F.md)
- [线程同步与互斥的区别与联系](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/%E7%BA%BF%E7%A8%8B%E5%90%8C%E6%AD%A5%E4%B8%8E%E4%BA%92%E6%96%A5%E7%9A%84%E5%8C%BA%E5%88%AB%E4%B8%8E%E8%81%94%E7%B3%BB.md)

# 框架
shiro框架的原理

# 网络协议
- [TCP与UDP区别](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/TCP%E4%B8%8EUDP%E7%9A%84%E5%8C%BA%E5%88%AB.md)
- [TCP可靠性如何保证](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/TCP%E5%8D%8F%E8%AE%AE-%E5%A6%82%E4%BD%95%E4%BF%9D%E8%AF%81%E4%BC%A0%E8%BE%93%E5%8F%AF%E9%9D%A0%E6%80%A7.md)
TCP网络包分片与重组
- [TCP三次握手与四次挥手](https://github.com/LengendOfDong/Blog/edit/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/TCP%E8%BF%9E%E6%8E%A5.md)
- [TCP的TIME_WAIT状态](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/TCP%E7%9A%84TIME_WAIT%E7%8A%B6%E6%80%81.md)
- [SSL握手协议使用的加密算法](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/SSL%E7%89%88%E6%9C%AC%E4%B8%AD%E6%89%80%E7%94%A8%E5%88%B0%E7%9A%84%E5%8A%A0%E5%AF%86%E7%AE%97%E6%B3%95.md)
- [对称加密与非对称加密的优缺点](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/%E5%AF%B9%E7%A7%B0%E5%8A%A0%E5%AF%86%E4%B8%8E%E9%9D%9E%E5%AF%B9%E7%A7%B0%E5%8A%A0%E5%AF%86%E7%9A%84%E4%BC%98%E7%BC%BA%E7%82%B9.md)
- [XSS和CSRF介绍]
http的响应与请求的结构
TCP拥塞控制

# Spring源码和SpringBoot源码
- [Spring生命周期](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/Spring%E4%B8%ADBean%E7%9A%84%E7%94%9F%E5%91%BD%E5%91%A8%E6%9C%9F.md)
- [Spring循环依赖](https://github.com/LengendOfDong/Blog/edit/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/Spring%E7%9A%84%E5%BE%AA%E7%8E%AF%E4%BE%9D%E8%B5%96.md)
- [Spring IOC](https://www.cnblogs.com/chenssy/p/9576769.html)
- [Spring AOP](https://www.zhihu.com/question/23641679)
- [SpringBoot启动方式和配置顺序]
SpringMVC和Spring父子容器的关系
Spring的事务实现方式
如何自定义实现SpringBoot中的Starter
spring响应请求的全流程
Spring多线程调度器

# 数据结构与算法（每日）
- [跳表](https://mp.weixin.qq.com/s/wacN04NHN2Zm0mZIlftxaw)
- [堆]()
LRU算法实现
链表倒数第K个数
雪花算法的原理
Paxos算法与ZAB协议
倒排索引
抖音小视频每日点击量最高的10个（Hash + 最小堆）
单项进行练习，如动态规划，深搜，广搜，贪心，排序，查找，并查集等
练习并进行总结规律

# 数据库设计
分库分表如何操作
某一个业务中现在需要生成全局唯一的递增 ID, 并发量非常大, 怎么做 （UUID）
头条的文章的评论量非常大, 比如说一篇热门文章就有几百万的评论, 设计一个后端服务, 实现评论的时序展示与分页

# MySQL
NoSQL的了解，为什么要有NoSQL
数据库索引原理
InnoDB引擎和MyISAM引擎
数据库调优
查询优化
B-树和B+树的区别
组合索引怎么使用
最左匹配的原理
MySQL隔离级别
一致性视图
日志模块：undo log/redo log/binlog
索引结构，回表和最左前缀原则
间隙锁、幻读、MVCC
MySQL in的原理，如何优化
B+树和二叉树有什么区别和优劣
MySQL 的聚簇索引和非聚簇索引有什么区别

# MyBatis
MyBatis中#和$符号的区别

# Redis
- [Redis高负载情况下的中断优化](https://blog.csdn.net/meituantech/article/details/80062289)
Redis缓存回收机制
Redis准备同步
Redis哨兵机制
redis实现分布式锁
redis数据结构类型
redis zset实现延时队列
跳表如何维护
redis线程模型
redis的数据过期方式
redis持久化方式
缓存雪崩，缓存穿透，缓存击穿
redis主从分布
Redis为什么这么快

# ZooKeeper
zookeeper锁是如何实现的

# Kafka
Kafka主题，分区和日志文件的关系
Kafka日志压缩和删除
Kafka如何保证消息有序
Kafka 的消费者如何做消息去重
介绍一下 Kafka 的 ConsumerGroup 

# 消息队列
消息队列的基本特性

# Netty与RPC
- [同步与阻塞](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/%E9%98%BB%E5%A1%9E%E4%B8%8E%E5%90%8C%E6%AD%A5.md)
- [NIO和IO的关系，通道与缓冲区](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/NIO%E5%92%8CIO%E7%9A%84%E5%85%B3%E7%B3%BB%EF%BC%8C%E9%80%9A%E9%81%93%E4%B8%8E%E7%BC%93%E5%86%B2%E5%8C%BA.md)
- [NIO的三大组件](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/NIO%E7%9A%84%E4%B8%89%E5%A4%A7%E7%BB%84%E4%BB%B6.md)

# 分布式
分布式缓存redis原理
分布式缓存读写不一致问题
分布式事务的几种形式
分布式锁
CAP理论，分区容错性的意义

# 多线程并发
- [深入分析synchronized的实现原理](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/%E6%B7%B1%E5%85%A5%E5%88%86%E6%9E%90synchronized%E7%9A%84%E5%AE%9E%E7%8E%B0%E5%8E%9F%E7%90%86.md)
- [深入分析volatile的实现原理](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/%E6%B7%B1%E5%85%A5%E5%88%86%E6%9E%90volatile%E7%9A%84%E5%AE%9E%E7%8E%B0%E5%8E%9F%E7%90%86.md)
- [Java内存模型之happens-before](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/Java%E5%86%85%E5%AD%98%E6%A8%A1%E5%9E%8B%E4%B9%8Bhappens-before.md)
- [JUC之AQS](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/JUC%E4%B9%8BAQS.md)
- [JUC之AQS：阻塞和唤醒线程](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/JUC%E4%B9%8BAQS%EF%BC%9A%E9%98%BB%E5%A1%9E%E5%92%8C%E5%94%A4%E9%86%92%E7%BA%BF%E7%A8%8B.md)
- [JUC之重入锁：ReentrantLock](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/JUC%E4%B9%8B%E9%87%8D%E5%85%A5%E9%94%81%EF%BC%9AReentrantLock.md)
- [JUC之读写锁：ReentrantReadWriteLock](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/JUC%E4%B9%8B%E8%AF%BB%E5%86%99%E9%94%81%EF%BC%9AReentrantReadWriteLock.md)
- [JUC之Condition](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/JUC%E4%B9%8BCondition.md)
-[JUC之并发工具类：Semaphore](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/JUC%E4%B9%8B%E5%B9%B6%E5%8F%91%E5%B7%A5%E5%85%B7%E7%B1%BB%EF%BC%9ASemaphore.md)
- []()
线程池有哪些类型
线程池的构造函数及其含义
如何确定线程池中的线程的个数
定时线程池是如何实现的
synchronized，volatile关键字
ReentrantLock与AQS
ThreadLocal实现原理
ConcurrentHashMap分段锁原理
wait和sleep的区别

# JVM
JVM内存模型，内存区域划分
OOM介绍
对象分配与回收
G1和CMS垃圾回收器
对象可达性分析
类加载机制
JVM调优参数
Full GC.Minor GC
对象的栈上分配（JIT编译器）
逃逸分析
垃圾回收算法
JVM加载类的过程
什么时候使用STOP THE WORLD

# 系统设计
- 秒杀系统
- 广告推送
- 排队系统
- 红包系统
- 停车场系统
- 点餐系统

# 故障问题
- [MyBatis版本升级引发的线上告警回顾及原理分析](https://blog.csdn.net/MeituanTech/article/details/106846456)

# 综合题目
- [扫微信二维码经历了什么](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/%E5%BE%AE%E4%BF%A1%E4%BA%8C%E7%BB%B4%E7%A0%81%E6%89%AB%E7%A0%81%E8%BF%87%E7%A8%8B.md)
- [在浏览器上输入url后的过程](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/%E6%B5%8F%E8%A7%88%E5%99%A8%E7%9A%84%E4%B8%80%E4%B8%AA%E8%AF%B7%E6%B1%82%E4%BB%8E%E5%8F%91%E9%80%81%E5%88%B0%E8%BF%94%E5%9B%9E%E9%83%BD%E7%BB%8F%E5%8E%86%E4%BA%86%E4%BB%80%E4%B9%88.md)
公司内部的RPC框架

# 工具方面
git rebase命令发生了什么
maven打包配置依赖

# 加分
了解GO语言

# hr
为啥离职，怎么想的
怎么和团队的人沟通，和成员出现冲突时怎么解决。
期望薪资
对自己这几年的经历满意吗
觉得自己有什么缺点
碰到过什么很挫败的事情
未来的职业规划
看机会的时候，主要考虑的是待遇、平台、人员还是什么其他因素？

