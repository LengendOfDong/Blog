# ACL权限控制
ZK 类似文件系统，Client 可以在上面创建节点、更新节点、删除节点等如何做到权限的控制？查阅文档，zk的ack（Access Control List）能够保证权限，但是调研完后发现它不是很好用。

ACL 权限控制，使用：schema: id  :permission 来标识，主要涵盖 3 个方面：
- Schema：权限模式，鉴权的策略
- ID： 授权对象
- 权限：Permission

其特性如下：
- zookeeper的权限
