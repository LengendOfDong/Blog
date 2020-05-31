# 简介
ArrayBlockingQueue是java并发包下一个以数组实现的阻塞队列，它是线程安全的。

队列是一种线性表，它的特点是先进先出，又叫FIFO，就像我们平常排队一样，先到先得，即先进入队列的人先出队。

# 源码分析
```java
public ArrayBlockingQueue(int capacity) {
    this(capacity, false);
}

public ArrayBlockingQueue(int capacity, boolean fair) {
    if (capacity <= 0)
        throw new IllegalArgumentException();
    // 初始化数组
    this.items = new Object[capacity];
    // 创建重入锁及两个条件
    lock = new ReentrantLock(fair);
    notEmpty = lock.newCondition();
    notFull =  lock.newCondition();
}
```
通过构造方法可以得出以下结论：
- ArrayBlockingQueue初始化时必须传入容量，也就是数组的大小
- 可以通过构造方法控制重入锁的类型是公平锁还是非公平锁

## 入队
入队有四个方法，它们分别为add(E e)、offer(E e)、put(E e)、offer(E e,long timeout,TimeUnit unit),它们有什么区别呢？

```java
public boolean add(E e) {
    // 调用父类的add(e)方法
    return super.add(e);
}

// super.add(e)
public boolean add(E e) {
    // 调用offer(e)如果成功返回true，如果失败抛出异常
    if (offer(e))
        return true;
    else
        throw new IllegalStateException("Queue full");
}

public boolean offer(E e) {
    // 元素不可为空
    checkNotNull(e);
    final ReentrantLock lock = this.lock;
    // 加锁
    lock.lock();
    try {
        if (count == items.length)
            // 如果数组满了就返回false
            return false;
        else {
            // 如果数组没满就调用入队方法并返回true
            enqueue(e);
            return true;
        }
    } finally {
        // 解锁
        lock.unlock();
    }
}

public void put(E e) throws InterruptedException {
    checkNotNull(e);
    final ReentrantLock lock = this.lock;
    // 加锁，如果线程中断了抛出异常
    lock.lockInterruptibly();
    try {
        // 如果数组满了，使用notFull等待
        // notFull等待的意思是说现在队列满了
        // 只有取走一个元素后，队列才不满
        // 然后唤醒notFull，然后继续现在的逻辑
        // 这里之所以使用while而不是if
        // 是因为有可能多个线程阻塞在lock上
        // 即使唤醒了可能其它线程先一步修改了队列又变成满的了
        // 这时候需要再次等待
        while (count == items.length)
            notFull.await();
        // 入队
        enqueue(e);
    } finally {
        // 解锁
        lock.unlock();
    }
}

public boolean offer(E e, long timeout, TimeUnit unit)
    throws InterruptedException {
    checkNotNull(e);
    long nanos = unit.toNanos(timeout);
    final ReentrantLock lock = this.lock;
    // 加锁
    lock.lockInterruptibly();
    try {
        // 如果数组满了，就阻塞nanos纳秒
        // 如果唤醒这个线程时依然没有空间且时间到了就返回false
        while (count == items.length) {
            if (nanos <= 0)
                return false;
            nanos = notFull.awaitNanos(nanos);
        }
        // 入队
        enqueue(e);
        return true;
    } finally {
        // 解锁
        lock.unlock();
    }
}

private void enqueue(E x) {
    final Object[] items = this.items;
    // 把元素直接放在放指针的位置上
    items[putIndex] = x;
    // 如果放指针到数组尽头了，就返回头部
    if (++putIndex == items.length)
        putIndex = 0;
    // 数量加1
    count++;
    // 唤醒notEmpty，因为入队了一个元素，所以肯定不为空了
    notEmpty.signal();
}
```
（1）add(e)时如果队列满了则抛出异常；

（2）offer(e)时如果队列满了则返回false；

（3）put(e)时如果队列满了则使用notFull等待；

（4）offer(e, timeout, unit)时如果队列满了则等待一段时间后如果队列依然满就返回false；

（5）利用放指针循环使用数组来存储元素；
