# LRU
当Redis内存超出物理内存限制时，内存的数据会开始和磁盘产生频繁的交换。

生产环境中是不允许Redis出现交换行为的，为了限制最大使用内存，Redis提供了配置参数maxmemory来限制内存超出期望大小。

当实际内存超出maxmemory时，有几种可选策略来让用户自己决定该如何腾出新的空间：

- noeviction(罢工):除了del删除可以，还有读操作可以，其他都不能继续进行，影响线上业务。默认策略
- volatile-lru(过期key中最少使用淘汰)
- volatile-ttl(过期key中过期时间最短的淘汰)
- volatile-random(过期key中随机淘汰)
- allkeys-lru(全体key最少使用淘汰)
- allkeys-random(随机key淘汰)

如果只是用Redis来作为缓存，可以使用allkeys-xxx策略，如果需要用到Redis的持久化功能，就需要和过期的keys进行区分，使用volatile-xxx策略。

