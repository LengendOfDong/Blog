# zookeeper是什么
zookeeper是一个分布式协调服务，可用于服务发现、分布式锁、分布式领导选举、配置管理等

这一切的基础，都是zookeeper提供了一个类似于Linux文件系统的树形结构（可认为是轻量级的内存文件系统，但是只适合存少量信息，完全不适合存储大量文件或者大文件），同时提供了对于每个节点的监控以及通知机制。

既然是一个文件系统，就不得不提zookeeper是如何保证数据一致性的。

# zookeeper服务器角色
zookeeper集群是一个基于主从复制的高可用集群，每个服务器承担如下三种角色中的一种：
- Leader  一个zookeeper集群同时只会有一个实际工作的Leader，它会发起并维护与各Follower以及Observer间的心跳。
- Follower 一个Zookeeper集群可能同时存在多个Follower，它会响应Leader的心跳。Follower可直接处理并返回客户端的读请求，同时会将写请求转发给Leader处理，并且负责在Leader处理写请求时对请求进行投票。
- Observer 角色与Follower类似，但是无投票权。

zookeeper的这种角色分配类似于董事长（Leader),董事会大股东（Follower）， 董事会小股东（Observer），董事会大股东处理日常事务，重要事务签署经过董事长，而且重大事项由董事长于董事会大股东共同投票决定。董事会小股东也是处理日常事务，但是没有重大事项决定权。

# 原子广播（ZAB)
为了保证写操作的一致性与可用性，zookeeper专门设计了一种名为原子广播（ZAB）的支持崩溃恢复的一致性协议。基于该协议，zookeeper实现了一种主从模式的系统架构来保持集群中各个副本之间的数据一致性。

根据ZAB协议，所有的写操作都必须通过Leader完成，Leader写入本地日志后再复制到所有的Follower节点。

一旦Leader节点无法工作，ZAB协议能够自动从Follower节点中重新选出一个合适的替代者，即新的Leader，该过程即为领导选举。该领导选举过程，是ZAB协议中最为重要和复杂的过程。


## 写Leader
通过Leader进行写操作，主要分为五步：
- 客户端向Leader发起写请求
- Leader将写请求以Proposal的形式发给所有Follower并等待ACK
- Follower收到Leader的Proposal后返回Ack
- Leader得到过半的ACK（Leader对自己默认有一个ACK）后向所有的Follower和Observer发送Commit
- Leader将处理结果返回给客户端

这里要注意：
- Leader并不需要得到Observer的ACK，即Observer无投票权
- Leader不需要得到所有Follower的ACK，只要收到过半的ACK即可，同时Leader本身对自己有一个ACK，即 （n + 1) / (Follower number + 1) > 1 / 2,当有4个Follower时，只需要2个Follower返回ACK即可，即（2 + 1）/ (4 + 1) > 1/2 
- Observer虽然没有投票权，但仍需同步Leader的数据从而在处理读请求时可以返回尽可能新的数据。

## 写Follower/Observer
通过Follower/Observer进行写操作流程， Follower/Observer均可接受写请求，但不能直接处理，而需要将写请求转发给Leader处理

除了多了一步请求转发，其它流程与直接写Leader无任何区别。

## 读操作
Leader/Follower/Observer都可直接处理读请求，从本地内存中读取数据并返回给客户端即可。

由于处理读请求不需要服务器之间的交互，Follower/Observer越多，整体可处理的读请求量越大，也即读性能越好。

