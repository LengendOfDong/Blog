# 简介
DelayQueue是java并发包下的延时阻塞队列，常用语实现定时任务。

DelayQueue实现了BlockingQueue接口，所以它是一个阻塞队列。

DelayQueue还组合了一个叫做Delayed的接口，DelayQueue中存储的所有元素都必须实现Delayed接口。

```java
public interface Delayed extends Comparable<Delayed> {

    long getDelay(TimeUnit unit);
}
```

Delayed是一个继承自Comparable的接口，并且定义了一个getDelay()方法，用于表示还有多少时间到期，到期了应返回小于等于0的数值。

# 源码分析

- 主要属性
```java
// 用于控制并发的锁
private final transient ReentrantLock lock = new ReentrantLock();
// 优先级队列
private final PriorityQueue<E> q = new PriorityQueue<E>();
// 用于标记当前是否有线程在排队（仅用于取元素时）
private Thread leader = null;
// 条件，用于表示现在是否有可取的元素
private final Condition available = lock.newCondition();
```
从属性可以看到，延时队列主要使用优先级队列来实现，并辅以重入锁和条件来控制并发安全。

因为优先队列是无界的，所以这里只需要一个条件就可以了。

