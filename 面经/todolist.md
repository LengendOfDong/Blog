# 查看面经中高频的类
比如ReentrantLock,ThreadPoolExecutor,ConcurrentHashMap,HashMap等这些类的源码

# Java基础SE
- [hashcode与equals区别与关系](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/hashcode%E4%B8%8Eequals%E6%96%B9%E6%B3%95%E7%9A%84%E5%8C%BA%E5%88%AB%E4%B8%8E%E8%81%94%E7%B3%BB.md)
- [RuntimeException、Exception、Error和Throwable](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/RuntimeException%E3%80%81Exception%E3%80%81Error%E5%92%8CThrowable.md)
- [如何使用异常处理机制](https://github.com/LengendOfDong/Blog/blob/master/%E9%9D%A2%E7%BB%8F/%E6%9D%82%E5%AD%A6/%E5%A6%82%E4%BD%95%E4%BD%BF%E7%94%A8%E5%BC%82%E5%B8%B8%E5%A4%84%E7%90%86%E6%9C%BA%E5%88%B6.md)
- [注解的使用]()
- 五种常见的网络IO模型

反射应用，private static final
hashMap与hashtable的区别
concurrenthashmap与hashtable的区别
IO多路复用模型中的select，poll，epoll的区别

# 框架
shiro框架的原理

# 网络协议
TCP与UDP区别
TCP可靠性如何保证
TCP网络包分片与重组
三次握手与四次挥手，状态转换，TIME_WAIT
SSL握手协议使用的加密算法，非对称加密的缺点
XSS和CSRF介绍
http的响应与请求的结构
TCP拥塞控制，UDP拥塞控制

# Spring源码和SpringBoot源码
Spring生命周期
Spring循环依赖
Spring IOC
Spring AOP
SpringBoot启动方式和配置顺序
SpringMVC和Spring父子容器的关系
Spring的事务实现方式
如何自定义实现SpringBoot中的Starter
spring响应请求的全流程

# 数据结构与算法（每日）
最大堆和最小堆
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

# MySQL
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

# MyBatis
MyBatis中#和$符号的区别

# Redis
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
线程池有哪些类型
线程池的构造函数及其含义
如何确定线程池中的线程的个数
定时线程池是如何实现的
synchronized，volatile关键字
ReentrantLock与AQS
ThreadLocal实现原理
ConcurrentHashMap分段锁原理
wait和sleep的区别
进程同步的方式

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

# 综合题目
扫微信二维码经历了什么
发送一个请求到返回之间经历了什么（在浏览器上输入url后的过程）
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
