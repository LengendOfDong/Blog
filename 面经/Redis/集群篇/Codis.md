# Codis
在大数据高并发场景下，单个Redis实例往往是不够用的。首先，单个Redis的内存不宜过大，否则会造成快照同步的时间很长，快照同步死循环。其次单个Redis实例只能利用单个核心，压力非常大。

因为Codis是无状态的，它只是一个转发代理中间件，这意味着可以启动多个Codis实例，供客户端使用，每个Codis节点都是对等的。

## Codis分片原理
Codis默认将所有的KEY划分为1024个槽位，首先对传输过来的key进行crc32运算计算hash值，再将hash后的整数值对1024这个整数进行取模得到一个余数，这个余数就是对应的key的槽位。

```java
hash = crc32(command.key)
slot_index = hash % 1024
redis = slots[slot_index].redis
redis.do(command)
```

槽位默认数量是1024，它是可以设置的，如果集群节点比较多，可以设置大些，如2048，4096

## Codis实例槽位同步
Codis的槽位映射关系不能存放在内存中，否则无法同步，这时需要一个分布式配置存储数据库专门用来持久化槽位关系。

Codis使用Zookeeper，或者etcd来做这个事情。

## 扩容
Codis对Redis进行了改造，增加了SLOTSSCAN指令，可以遍历指定slot下所有的key。Codis通过SLOTSSCAN扫描出所有待迁移槽位的所有key，然后挨个迁移每个key到新的Redis节点。

当Codis接收到位于正在迁移槽位中的key后，会立即强制对当前的单个key进行迁移，迁移完成后，再将请求转发到新的Redis实例。
```java
slot_index = crc32(command.key) % 1024
if slot_index in migrating_slots:
  do_migrate_key(command_key)         #强制执行迁移
  redis = slots[slot_index].new_redis
 else:
  redis = slots[slot_index].redis
 redis.do(command)
```

## 自动均衡
Codis提供了自动均衡的功能，在系统比较空闲的时候观察每个Redis实例对应的slot数量，如果不平衡，就会自动进行迁移。

