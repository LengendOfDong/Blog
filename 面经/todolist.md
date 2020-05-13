# 查看面经中高频的类
比如ReentrantLock,ThreadPoolExecutor,ConcurrentHashMap,HashMap等这些类的源码

# Java基础SE
- [NIO的实现原理](https://blog.csdn.net/charjay_lin/article/details/81810922)
HashMap，HashSet,LinkedHashMap以及ConcurrentHashMap
hashcode与equals方法的区别
RuntimeException Exception Error
注解的使用
基本数据类型
五种常见的网络IO模型

# 网络协议
TCP与UDP区别
TCP可靠性如何保证
TCP网络包分片与重组
三次握手与四次挥手，状态转换
SSL握手协议使用的加密算法，非对称加密的缺点
XSS和CSRF介绍


# Spring源码和SpringBoot源码
Spring生命周期
Spring循环依赖
Spring IOC
Spring AOP
SpringBoot启动方式和配置顺序
SpringMVC和Spring父子容器的关系
Spring的事务实现方式
如何自定义实现SpringBoot中的Starter

# 数据结构与算法（每日）
单项进行练习，如动态规划，深搜，广搜，贪心，排序，查找，并查集等
练习并进行总结规律

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

# ZooKeeper
zookeeper锁是如何实现的

# Kafka
Kafka主题，分区和日志文件的关系
Kafka日志压缩和删除

# 消息队列
消息队列的基本特性

# Netty与RPC

# 分布式缓存
分布式缓存redis原理
分布式缓存读写不一致问题

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

# 系统设计
- 秒杀系统
- 广告推送
- 排队系统
- 红包系统
- 停车场系统
- 点餐系统

# 综合题目
扫微信二维码经历了什么
发送一个请求到返回之间经历了什么

# 工具方面
git rebase命令发生了什么
maven打包配置依赖
