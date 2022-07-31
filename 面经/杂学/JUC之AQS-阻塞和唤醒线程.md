# 阻塞判断
在线程获取同步状态时如果获取失败，则加入CLH同步队列，通过自旋的方式不断地获取同步状态，但是在自旋的过程中则需要判断当前线程是否需要阻塞，其主要方法在
acquireQueue():
```java
if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
                    interrupted = true;
```
通过这段代码我们可以看到，在获取同步状态失败后，线程并不是立马进行阻塞，需要检查该线程的状态，检查状态的方法为shouldParkAfterFailedAcquire(Node pred, Node node) 方法，该方法主要靠前驱节点判断当前线程是否应该被阻塞，代码如下：
```java
 private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
        //前驱节点
        int ws = pred.waitStatus;
        //状态为signal，表示当前线程处于等待状态，直接放回true
        if (ws == Node.SIGNAL)
            return true;
        //前驱节点状态 > 0 ，则为Cancelled,表明该节点已经超时或者被中断了，需要从同步队列中取消
        if (ws > 0) {
            do {
                node.prev = pred = pred.prev;
            } while (pred.waitStatus > 0);
            pred.next = node;
        }
        //前驱节点状态为Condition、propagate
        else {
            compareAndSetWaitStatus(pred, ws, Node.SIGNAL);
        }
        return false;
    }
```
这段代码主要检查当前线程是否需要被阻塞，具体规则如下：
- 如果当前线程的前驱节点状态为SIGNAL，则表明当前线程需要被阻塞，调用unpark()方法唤醒，直接返回true,当前线程阻塞
- 如果当前线程的前驱节点状态为CANCELLED(ws>0),则表明该线程的前驱节点已经等待超时或者被中断了，则需要从CLH队列中将该前驱节点删除掉，直到回溯到前驱节点状态<=0, 返回false
- 如果前驱节点非SIGNAL，非CANNELLED，则通过CAS的方式将其前驱节点设置为SIGNAL,返回false

如果shouldParkAfterFailedAcquire(Node pred, Node node)方法返回true,则调用parkAndCheckInterrupt方法阻塞当前线程：
```java
  private final boolean parkAndCheckInterrupt() {
        LockSupport.park(this);
        return Thread.interrupted();
    }
```

parkAndCheckInterrupt()方法主要是把当前线程挂起，从而阻塞住线程的调用栈，同时返回当前线程的中断状态。其内部则是调用LockSupport工具类的park()方法来阻塞该方法。当线程释放同步状态后，则需要唤醒该线程的后继节点：
```java
 public final boolean release(int arg) {
        if (tryRelease(arg)) {
            Node h = head;
            if (h != null && h.waitStatus != 0)
                //唤醒后继节点
                unparkSuccessor(h);
            return true;
        }
        return false;
    }
```
调用unparkSuccessor(Node node)唤醒后继节点：
```java
 private void unparkSuccessor(Node node) {
        //当前节点状态
        int ws = node.waitStatus;
        //当前状态 < 0 则设置为 0
        if (ws < 0)
            compareAndSetWaitStatus(node, ws, 0);

        //当前节点的后继节点
        Node s = node.next;
        //后继节点为null或者其状态 > 0 (超时或者被中断了)
        if (s == null || s.waitStatus > 0) {
            s = null;
            //从tail节点来找可用节点
            for (Node t = tail; t != null && t != node; t = t.prev)
                if (t.waitStatus <= 0)
                    s = t;
        }
        //唤醒后继节点
        if (s != null)
            LockSupport.unpark(s.thread);
    }
```
可能会存在当前线程的后继节点为null,超时、被中断的情况，如果遇到这种情况，则需要跳过该节点.采用tail回溯办法找到第一个可用的线程。最后调用LockSupport的unpark(Thread thread)方法唤醒该线程。

# LockSupport
当需要阻塞或者唤醒一个线程的时候，AQS都是使用LockSupport这个工具类来完成的。

>LockSupport是用来创建锁和其他同步类的基本线程阻塞原语

每个使用LockSupport的线程都会与一个许可关联，如果该许可可用，并且可在进程中使用，则调用park()将会立即返回，否则可能阻塞。如果许可尚不可用，则可以调用unpark使其可用。
