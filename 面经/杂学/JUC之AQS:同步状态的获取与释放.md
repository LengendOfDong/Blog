# 简介
AQS是构建Java同步组件的基础，AQS的设计模式采用的模板方法模式，子类通过继承的方式，实现它的抽象方法来管理同步状态，对于子类而言它并没有太多的活要做，AQS提供了大量的模板方法来实现同步，主要是分为三类：独占式获取和释放同步状态、共享式获取和释放同步状态、查询同步队列中的等待线程情况。

自定义子类使用AQS提供的模板方法就可以实现自己的同步语义。

# 独占式
独占式，同一时刻仅有一个线程持有同步状态

## 独占式同步状态获取
acquire(int arg)方法为AQS提供的模板方法，该方法为独占式获取同步状态，但是该方法对中断不敏感，也就是说由于线程获取同步状态失败加入到CLH同步队列中，后续对线程进行中断操作时，线程不会从同步队列中移除。
```java
public final void acquire(int arg) {
        if (!tryAcquire(arg) &&
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }
```
![AQS同步状态获取](https://github.com/LengendOfDong/Blog/blob/master/img/AQS%E7%8B%AC%E5%8D%A0%E5%BC%8F%E5%90%8C%E6%AD%A5%E7%8A%B6%E6%80%81%E8%8E%B7%E5%8F%96.png)

