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

```python
# -*- coding=utf-8 -*-

from collections import OrderedDict

class LRUDict(OrderedDict):

    def __init__(self, capacity):
        self.capacity = capacity
        self.items = OrderedDict()

    def __setitem__(self, key, value):
        old_value = self.items.get(key)
        # 旧值不为空则弹出旧值塞入新值
        if old_value is not None:
            self.items.pop(key)
            self.items[key] = value
        # 旧值为空，并且当前空间小于总的容量，则直接塞入新值
        elif len(self.items) < self.capacity:
            self.items[key] = value
        # 旧值为空，并且当前空间不小于总的容量，则直接弹出最后一个元素
        # 并将新值塞入
        else:
            self.items.popitem(last=True)
            self.items[key] = value

    def __getitem__(self, key):
        value = self.items.get(key)
        # 将值弹出并重新塞入，将其放到表头，表明最近访问
        if value is not None:
            self.items.pop(key)
            self.items[key] = value
        return value

    def __repr__(self):
        return repr(self.items)

if __name__ == '__main__':
    d = LRUDict(10)

    for i in range(15):
        d[i] = i

    print d
```
