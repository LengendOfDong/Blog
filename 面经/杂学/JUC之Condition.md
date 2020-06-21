# 简介
在没有Lock之前，我们使用synchronized来控制同步，配合Object的wait()、notify()系列方法可以实现等待、通知模式。在Java SE5后，Java提供了Lock接口，相对与Synchronized而言，Lock提供了条件Condition，对线程的等待，唤醒操作更加详细和灵活。
![Condition与Synchronized](https://github.com/LengendOfDong/Blog/blob/master/img/Condition%E4%B8%8ESynchronized.png)

Condition是一种广义上的条件队列。他为线程提供了一种更为灵活的等待、通知模式，线程在调用await方法后执行挂起操作，直到线程等待某个条件为真时才会被唤醒。Condition必须要配合锁一起使用，因为对共享状态变量的访问发生在多线程环境下。一个Condition的实例必须与一个Lock绑定，因此Condition一般都是作为Lock的内部实现。

# Condition的实现
获取一个Condition必须要通过Lock的newCondition()方法。该方法定义在接口Lock下面，返回的结果是绑定到此Lock实例的新Condition实例。

## 等待队列
每个Condition对象包含着一个FIFO队列，该队列是Condition对象通知、等待功能的关键。在队列中每个节点都包含着一个线程引用，该线程就是在该Condition对象上等待的线程。
```java
public class ConditionObject implements Condition, java.io.Serializable {
    private static final long serialVersionUID = 1173984872572414699L;

    //头节点
    private transient Node firstWaiter;
    //尾节点
    private transient Node lastWaiter;

    public ConditionObject() {
    }

    /** 省略方法 **/
}
```
## 等待
调用Condition的await()方法会使当前线程进入等待状态，同时会加入到Condition等待队列同时释放锁。当从await()方法返回时，当前线程一定是获取了Condition相关连的锁。
```java
public final void await() throws InterruptedException {
        // 当前线程中断
        if (Thread.interrupted())
            throw new InterruptedException();
        //当前线程加入等待队列
        Node node = addConditionWaiter();
        //释放锁
        long savedState = fullyRelease(node);
        int interruptMode = 0;
        /**
         * 检测此节点的线程是否在同步队上，如果不在，则说明该线程还不具备竞争锁的资格，则继续等待
         * 直到检测到此节点在同步队列上
         */
        while (!isOnSyncQueue(node)) {
            //线程挂起
            LockSupport.park(this);
            //如果已经中断了，则退出
            if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
                break;
        }
        //竞争同步状态
        if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
            interruptMode = REINTERRUPT;
        //清理下条件队列中的不是在等待条件的节点
        if (node.nextWaiter != null) // clean up if cancelled
            unlinkCancelledWaiters();
        if (interruptMode != 0)
            reportInterruptAfterWait(interruptMode);
    }
```
此段代码的逻辑是：首先将当前线程新建一个节点同时加入到条件队列中，然后释放当前线程持有的同步状态。然后则是不断检测该节点代表的线程是否出现在CLH同步队列中（收到signal信号之后就会在AQS队列中检测到），如果不存在则一直挂起，否则参与竞争同步状态。

# 通知
调用Condition的signal()方法，将会唤醒在等待队列中等待最长时间的节点（条件队列里的首节点），在唤醒节点前，会将节点移到CLH同步队列中。
```java
public final void signal() {
        //检测当前线程是否为拥有锁
        if (!isHeldExclusively())
            throw new IllegalMonitorStateException();
        //头节点，唤醒条件队列中的第一个节点
        Node first = firstWaiter;
        if (first != null)
            doSignal(first);    //唤醒
    }
```
该方法会首先判断当前线程是否已经获得了锁，这是前提条件，然后唤醒条件队列中的头节点。

# Condition的应用
```java
public class ConditionTest {
    private LinkedList<String> buffer;    //容器
    private int maxSize ;           //容器最大
    private Lock lock;
    private Condition fullCondition;
    private Condition notFullCondition;

    ConditionTest(int maxSize){
        this.maxSize = maxSize;
        buffer = new LinkedList<String>();
        lock = new ReentrantLock();
        fullCondition = lock.newCondition();
        notFullCondition = lock.newCondition();
    }

    public void set(String string) throws InterruptedException {
        lock.lock();    //获取锁
        try {
            while (maxSize == buffer.size()){
                notFullCondition.await();       //满了，添加的线程进入等待状态
            }

            buffer.add(string);
            fullCondition.signal();
        } finally {
            lock.unlock();      //记得释放锁
        }
    }

    public String get() throws InterruptedException {
        String string;
        lock.lock();
        try {
            while (buffer.size() == 0){
                fullCondition.await();
            }
            string = buffer.poll();
            notFullCondition.signal();
        } finally {
            lock.unlock();
        }
        return string;
    }
}
```
