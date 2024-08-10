# Condition

​	Condition 将 Object 监视器方法（wait、notify 和 notifyAll）分解为不同的对象，通过将它们与任意 Lock 实现相结合，使每个对象具有多个等待集的效果。其中 Lock 取代了同步方法和语句的使用，而 Condition 取代了 Object 监视器方法的使用。

​	Conditions（也称为条件队列或条件变量）为一个线程提供了一种暂停执行（“等待”）的方法，直到另一个线程通知某些状态条件现在可能为真。由于对此共享状态信息的访问发生在不同的线程中，因此必须对其进行保护，因此某种形式的锁与条件相关联。等待条件提供的关键属性是它以原子方式释放关联的锁并挂起当前线程，就像 Object.wait 一样。

​	Condition 实例本质上绑定到锁。要获取特定 Lock 实例的 Condition 实例，请使用其 newCondition() 方法。

​	例如，假设我们有一个支持 put 和 take 方法的有界缓冲区。如果尝试在空缓冲区上获取，则线程将阻塞，直到项目可用;如果尝试在完整缓冲区上放入，则线程将阻塞，直到空格可用。我们希望继续等待，放置线程，并将线程放在单独的等待集中，以便我们可以使用优化，即在缓冲区中的项目或空间可用时仅通知单个线程。这可以使用两个条件实例来实现。

```java
class BoundedBuffer {
    final Lock lock = new ReentrantLock();
    final Condition notFull  = lock.newCondition(); 
    final Condition notEmpty = lock.newCondition(); 
 
    final Object[] items = new Object[100];
    int putptr, takeptr, count;
 
    public void put(Object x) throws InterruptedException {
      lock.lock();
      try {
        while (count == items.length)
          notFull.await();
        items[putptr] = x;
        if (++putptr == items.length) putptr = 0;
        ++count;
        notEmpty.signal();
      } finally {
        lock.unlock();
      }
    }
 
    public Object take() throws InterruptedException {
      lock.lock();
      try {
        while (count == 0)
          notEmpty.await();
        Object x = items[takeptr];
        if (++takeptr == items.length) takeptr = 0;
        --count;
        notFull.signal();
        return x;
      } finally {
        lock.unlock();
      }
    }
 }
```

