# ZooKeeper是什么
zookeeper是一个高性能、开源的分布式应用协调服务，它提供了简单原始的功能，分布式应用可以基于它实现更高级的服务，比如实现同步（分布式锁），配置管理、集群管理。它被设计为易于编程，使用文件系统目录树作为数据模型。服务端使用Java语言编写，并且提供了Java和C语言的客户端。

注：分布式意味着由多台计算机构成的集群，每台计算机之间通过网络通信，这些计算机协调完成共同的目标，对外看来这些机器就是一个整体，协调的意思是多个节点一起完成一个动作。

# zookeeper数据模型
zookeeper数据模型是一种分层的树形结构：
- 树形结构中每个节点成为Znode;
- 每个Znode都可以有数据（byte[]类型），也可以有子节点
- Znode的路径使用斜线分割，例如：/Zoo/Duck,zookeeper中没有相对路径的说法，也即所有节点的路径都要写为绝对路径的方式。
- zookeeper定义了org.apache.zookeeper.data.Stat数据结构来存储数据的变化、ACL(访问权限)的变化和时间戳
- 当Zookeeper中的数据发生变化时，版本号会变更
- 可以对Znode中的数据进行读写操作

# zookeeper典型的应用场景
## 数据发布/订阅
数据发布订阅即所谓的配置中心：发布者将数据发布到zk的一个或者一系列的节点上，订阅者进行数据订阅，可以及时得到数据的变化通知。

## 负载均衡
zookeeper实现负载均衡本质上是利用zookeeper的配置管理功能，zookeeper实现负载均衡的步骤为：
- 服务提供者把自己的域名及IP端口映射注册到zookeeper中
- 服务消费者通过域名从zookeeper中获取到对应的IP及端口，这里的IP及端口可能有多个，只是获取其中一个。
- 当服务提供者宕机时，对应的域名与IP的对应就会减少一个映射。
- 阿里的dubbo服务框架就是基于zooKeeper来实现服务路由和负载。

## 命名服务
在分布式系统当中，命名服务（name service）也是很重要的应用场景，通过zookeeper也可以实现类似于J2EE中的JNDI的效果；分布式环境下，命名服务更多的是资源定位，并不是真正的实体资源。

## 分布式协调/通知
通过zookeeper的watcher和通知机制实现分布式锁和分布式事务。

## 集群管理
获取当前集群中机器的数量，集群中机器的运行状态、集群中节点的上下线操作、集群节点的统一配置等。

# zookeeper基本概念
## 集群角色
- Leader: 为客户端提供读写服务
- Follower:为客户端提供读服务，客户端到Follower的写请求会转交给Leader角色，Follower会参与Leader的选举
- Observer:为客户端提供读服务，不参与Leader的选举过程，一般时为了增强zookeeper集群的读请求 并发能力。

## 会话（Session）
- session是客户端与zookeeper服务端之间建立的长连接
- zookeeper在一个会话中进行心跳检测来感知客户端链接的存活
- zookeeper客户端在一个会话中接收来自服务端的watch事件通知
- zookeeper可以给会话设置超时时间

## zookeeper的数据节点（Znode）
- Znode是zookeeper树形结构中的数据节点，用于存储数据
- Znode分为持久节点和临时节点两种类型：
  - 持久节点：一旦创建，除非主动调用删除操作，否则一直存储在zookeeper上；
  - 临时节点：与客户端会话绑定，一旦客户端失效，这个客户端创建的所有临时节点都会被删除
- 可以为持久节点或临时节点设置Sequential属性，如果设置该属性则会自动在该节点名称后面追加一个整型数字

## zookeeper中的版本
zookeeper中有三种类型的版本：
- Version: 代表当前Znode的版本
- Cversion:代表当前Znode的子节点的版本，子节点发生变化时会增加该版本号的值。
- Aversion:代表当前Znode的ACL（访问控制）的版本，修改节点的访问控制权限时会增加该版本号的值。

## zookeeper中的watcher
- watcher监听在Znode节点上
- 当节点的数据更新或者子节点的状态发生变化都会使客户端的watcher得到通知

## zookeeper中的ACL（访问控制）
类似于Linux/Unix下的权限控制，有以下几种访问控制权限：
- CREATE: 创建子节点的权限
- READ: 获取节点数据和子节点列表的权限
- WRITE:更新节点数据的权限
- DELETE：删除子节点的权限
- ADMIN：设置节点ACL的权限

