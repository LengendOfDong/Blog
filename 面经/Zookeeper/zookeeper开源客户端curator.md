# zookeeper原生api的不足
zookeeper原生api存在以下不足之处：
- 连接的创建是异步的，需要开发人员自行编码实现等待；
- 连接没有自动的超时重连机制；
- zk自身不提供序列化机制，需要开发人员自行制定，从而实现数据的序列化和反序列化；
- Watcher注册一次只会生效一次，需要不断的重复注册；
- Watcher本身的使用方式不符合java本身的术语，如果采用监听器的方式，更容易理解；
- 不支持递归创建树形节点

# zookeeper第三方开源客户端
zookeeper的第三方开源客户端主要有zkClient，其中zkClient解决了session会话超时重连、Watcher反复注册等问题，提供了更加简洁的api,但zkClient社区不活跃，文档不够完善。而Curator是Apache基金会的顶级项目之一，它解决了session会话的超时重连，Watcher反复注册、NodeExistException异常等问题，Curator具有更加完善的文档，因此我们这里主要学习Curator的使用。

# Curator客户端api介绍
Curator包含了如下几个包：
- curator-framework:对zookeeper底层api的一些封装
- curator-client:提供一些客户端的操作，如重试策略等
- curator-recipes:封装了一些高级特性，如Cache事件监听，选举。分布式锁、分布式计数器、分布式Barrier等

