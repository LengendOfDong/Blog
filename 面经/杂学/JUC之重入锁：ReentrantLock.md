# 简介
ReentrantLock,可重入锁，是一种递归无阻塞的同步机制。它可以等同于synchronized的使用，但是ReentrantLock提供了比Synchronized更强大，灵活的锁机制，可以减少死锁发生的概率。

一个可重入的互斥锁定Lock,它具有与使用synchronized方法和语句所访问的隐式监视器锁定相同的一些基本行为和语义，但功能更强大。ReentrantLock将由最近成功获得锁定，并且还没有释放该锁定的线程所拥有。当锁定没有被另一个线程所拥有时，调用lock的线程将成功获取该锁定并返回。如果当前线程已经拥有锁定，此方法将立即返回。可以使用isHeldByCurrentThread()和getHoldCount()方法来检查此情况是否发生。

ReentrantLock还提供了公平锁与非公平锁的选择，构造方法接受一个可选的公平参数（默认非公平锁），当设置为true时，表示公平锁，否则为非公平锁。公平锁与非公平锁的区别在于公平锁的锁获取是有顺序的。但是公平锁的效率往往没有非公平锁的效率高，在许多线程访问的情况下，公平锁表现出较低的吞吐量。

# 获取锁
使用ReentrantLock获取锁：
```java
//非公平锁
ReentrantLock lock = new ReentrantLock();
lock.lock();
```
lock方法：
```java
public void lock() {
        sync.lock();
    }
```
Sync为ReentrantLock里面的一个内部类，它继承AQS，它有两个类：公平锁FairSync和非公平锁NonFairSync。ReentrantLock里面大部分的功能都是委托给Sync来实现的，同时Sync内部定义了lock()抽象方法由其子类去实现，默认实现了nonfairTryAcquire(int acquires)方法，可以看出它是非公平锁的默认实现方式。下面我们看非公平锁的lock()方法：
```java
final void lock() {
        //尝试获取锁
        if (compareAndSetState(0, 1))
            setExclusiveOwnerThread(Thread.currentThread());
        else
            //获取失败，调用AQS的acquire(int arg)方法
            acquire(1);
    }
```
首先会第一次尝试快速获取锁，如果获取失败，则调用acquire(int arg)方法，该方法定义在AQS中，如下：
```java
public final void acquire(int arg) {
        if (!tryAcquire(arg) &&
                acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }
```
这个方法首先调用tryAcquire(int arg)方法，在AQS中讲述过，tryAcquire(int arg)需要自定义同步组件提供实现，非公平锁实现如下： 
```java
 protected final boolean tryAcquire(int acquires) {
        return nonfairTryAcquire(acquires);
    }

    final boolean nonfairTryAcquire(int acquires) {
        //当前线程
        final Thread current = Thread.currentThread();
        //获取同步状态
        int c = getState();
        //state == 0,表示没有该锁处于空闲状态
        if (c == 0) {
            //获取锁成功，设置为当前线程所有
            if (compareAndSetState(0, acquires)) {
                setExclusiveOwnerThread(current);
                return true;
            }
        }
        //线程重入
        //判断锁持有的线程是否为当前线程
        else if (current == getExclusiveOwnerThread()) {
            int nextc = c + acquires;
            if (nextc < 0) // overflow
                throw new Error("Maximum lock count exceeded");
            setState(nextc);
            return true;
        }
        return false;
    }
```
该方法主要逻辑：首先判断同步状态state == 0 ?，如果是表示该锁还没有被线程持有，直接通过CAS获取同步状态，如果成功返回true。如果state != 0，则判断当前线程是否为获取锁的线程，如果是则获取锁，成功返回true。成功获取锁的线程再次获取锁，这是增加了同步状态state。 

# 释放锁
获取同步锁后，使用完毕则需要释放锁，ReentrantLock提供了unlock释放锁： 
```java
public void unlock() {
        sync.release(1);
    }
```
unlock内部使用Sync的release(int arg)释放锁，release(int arg)是在AQS中定义的： 
```java
public final boolean release(int arg) {
        if (tryRelease(arg)) {
            Node h = head;
            if (h != null && h.waitStatus != 0)
                unparkSuccessor(h);
            return true;
        }
        return false;
    }
```
与获取同步状态的acquire(int arg)方法相似，释放同步状态的tryRelease(int arg)同样是需要自定义同步组件自己实现： 
```java
 protected final boolean tryRelease(int releases) {
        //减掉releases
        int c = getState() - releases;
        //如果释放的不是持有锁的线程，抛出异常
        if (Thread.currentThread() != getExclusiveOwnerThread())
            throw new IllegalMonitorStateException();
        boolean free = false;
        //state == 0 表示已经释放完全了，其他线程可以获取同步状态了
        if (c == 0) {
            free = true;
            setExclusiveOwnerThread(null);
        }
        setState(c);
        return free;
    }
```
只有当同步状态彻底释放后该方法才会返回true。当state == 0 时，则将锁持有线程设置为null，free= true，表示释放成功。 
