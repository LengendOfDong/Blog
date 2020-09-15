# 引入依赖
zookeeper中自带一个客户端，只需要引入zookeeper,在build.gradle中添加以下依赖即可。
```java
compile ('org.apache.zookeeper:zookeeper:3.4.13')
```

# 创建zookeeper会话
org.apache.zookeeper.Zookeeper类的构造方法用于创建zookeeper客户端与服务端之间的会话。该类提供了如下几个构造方法：
```java
public Zookeeper(String connectString, int sessionTimeOut, Watcher watcher)
public Zookeeper(String connectString, int sessionTimeOut, Watcher watcher, boolean canBeReadOnly)
public Zookeeper(String connectString, int sessionTimeOut, Watcher watcher, long sessionId, byte[] sessionPasswd)
public Zookeeper(String connectString, int sessionTimeOUt, Watcher watcher, long sessionId, byte[] sessionPasswd,boolean canBeReadOnly)
```

构造方法参数说明：
- connectString: 指zk的服务器列表，以英文输入法下逗号分隔的host:port,比如192.168.1.1：2181， 192.168.1.2：2181，也可以通过在后面跟着根目录，表示此客户端的操作都是在此根目录下，比如:比如192.168.1.1:2181，192.168.1.2:2181/zk-book,表示此客户端操作的节点都是在/zk-book根目录下，比如创建/foo/bar，实际完整路径为/zk-book/foo/bar；

