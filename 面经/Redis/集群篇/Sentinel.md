# Sentinel（哨兵）
Redis Sentinel集群可以看成是一个zookeeper集群，一般由3~5个节点组成，这样即使个别节点挂了，集群还可以正常运转。

Sentinel负责持续监控主从节点的健康，当主节点挂掉时，自动选择一个最优的从节点切换成主节点。

客户端连接集群时，会首先连接Sentinel，通过Sentinel查询出主节点的地址，然后再连接主节点进行数据交互。当主节点发生故障时，客户端会重新向Sentinel要地址，Sentinel会将最新的主节点地址告诉客户端。如此应用程序将无须重启即可自动完成节点切换。

等原来的主节点重新恢复后，会变成从节点，从新的主节点那里建立复制关系。

# 消息丢失
Redis主从采用异步复制，如果主从延迟特别大，那么丢失的数据就会非常多。Sentinel无法保证消息完全不丢失，但是也能尽量保证消息少丢失。

Sentinel有两个选项可以限制主从延迟过大：
- min-slaves-to-write 1
- min-slaves-max-lag 10

第一个参数表示主节点必须至少有一个从节点在进行正常复制，否则就停止对外写服务，丧失可用性。

那么什么是正常复制呢：

这是由第二参数控制的，它的单位是秒,表示如果在10s内没有收到从节点的反馈，就意味着从节点同步不正常。

# Sentinel基本用法
Sentinel的默认端口是26379，不同于Redis的默认端口6379

当Sentinel进行主从切换时，redis-py在建立连接时进行了主节点地址变更判断。

Sentinel被动切换：连接池建立新连接时，会去查询主节点地址，然后跟内存中的主节点地址进行比对，如果变更了，就断开所有连接，重新使用新地址建立新连接。如果是旧的主节点挂掉了，那么所有正在使用的连接都会被关闭，然后在重连时就会用上新地址。

Sentinel主动切换：主动切换是指主节点并没有挂掉，而之前的主节点连接已经建立且在使用中，没有新连接需要建立。

redis-py在处理命令的时候捕获了一个特殊的异常ReadOnlyError，在这个异常里将所有的旧连接全部关闭了，后续指令就会进行重连。

主从切换后，之前的主节点被降级为从节点，所有的修改性的指令都会抛出ReadOnlyError，如果没有修改性指令，虽然连接不会得到切换，但是数据不会被破坏，所以即使不切换也没有关系。


