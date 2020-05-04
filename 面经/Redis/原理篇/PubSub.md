# 消息多播
消息多播允许生产者只生产一次消息，由中间件负责将消息复制到多个消息队列，每个队列由相应的消费组进行消费。它是分布式系统常用的一种解耦方式，用于将多个消费组的逻辑进行拆分。

支持了消息多播，多个消费者的逻辑就可以放到不同的子系统中。

## PubSub
为了支持消息多播，Redis单独使用了一个模块来支持消息多播，这个模块的名字叫做PubSub,也就是PublisherSubscriber.

消费者可以同时订阅多个主题，例如”subscribe codehole.image codehole.text codehole.blog“

另外为了简化订阅的繁琐，Redis提供了模式订阅功能Pattern Subscribe，这样就可以一次订阅多个主题，例如”psubscribe codehole.*“

## 消息结构
```python
{u'pattern': None, u'type': 'message', u'channel': 'codehole', u'data': 'python comes'}
{u'pattern': None, u'type': 'message', u'channel': 'codehole', u'data': 'java comes'}
{u'pattern': None, u'type': 'message', u'channel': 'codehole', u'data': 'golang comes'}
```
上面消息的字段含义如下：
- data表示消息的内容，一个字符串
- channel表示当前订阅的主题名称
- type表示消息的类型，如果是一个普通的消息，那么类型就是message;如果是控制消息，比如订阅指令的反馈，它的类型就是subscribe;如果是模式订阅的反馈，它的类型就是psubscribe等
- pattern,表示当前消息是使用哪种模式订阅到的，如果是通过subscribe指令订阅的，那么那个字段就是空的。

## 内存回收机制
Redis并不总是将空闲内存立即归还给操作系统，因为操作系统是以页为单位来回收内存的，这个页上只要还有一个key在使用，那么它就不能被回收。

Redis虽然无法保证立即回收已经删除的key的内存，但是它会重新使用那些尚未回收的空闲内存。这就好比电影院里虽然一拨观众走了，但是座位还在，下一拨观众来了，直接坐上就行，而操作系统回收内存就好比把座位也都给搬走了。

## 内存分配算法
Redis为了保持自身结构的简单性，在内存分配方面直接用第三方内存分配库去实现，目前Redis使用的是jemalloc(facebook)来管理内存，也可以切换到tcmalloc(google)库。因为jemalloc的性能相比tcmalloc要好一些，所以Redis默认使用了jemalloc。

