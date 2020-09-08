# ACL权限控制
ZK 类似文件系统，Client 可以在上面创建节点、更新节点、删除节点等如何做到权限的控制？查阅文档，zk的ack（Access Control List）能够保证权限，但是调研完后发现它不是很好用。

ACL 权限控制，使用：schema: id  :permission 来标识，主要涵盖 3 个方面：
- Schema：权限模式，鉴权的策略
- ID： 授权对象
- Permission： 权限

其特性如下：
- zookeeper的权限控制是基于每个Znode节点的，需要对每个节点设置权限
- 每个znode支持设置多种权限控制方案和多个权限
- 子节点不会继承父节点的权限，客户端无权访问某节点，但可能可以访问它的子节点

## schema、id和permission
1. schema:
zookeeper内置了一些权限控制方案，可以用以下方案为每个节点设置权限：

|方案|描述|
|--|--|
|world|只有一个用户：anyone,代表所有人（默认）|
|ip|使用IP地址认证|
|auth|使用已添加认证的用户认证|
|digest|使用“用户名：密码”方式认证|

2.。id:
授权对象id是指，权限赋予的用户或者一个实体，例如：ip地址或者机器。授权模式schema与授权对象id之间关系：

|权限模式|授权对象|
|--|--|
|IP|一个IP地址或是IP段|
|Digest|

