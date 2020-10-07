# zookeeper缓存
可以利用zookeeper在集群的各个节点之间缓存数据，每个节点都可以得到最新的缓存的数据，Curator提供了三种类型的缓存方式：Path Cache, Node Cache和Tree Cache

## Path Cache
Path Cache用来监控一个Znode的子节点，当一个子节点增加，更新，删除时，Path Cache会改变它的状态，会包含最新的子节点，子节点的数据和状态。

