# Lock

​	与使用同步方法和语句相比，锁实现提供了更广泛的锁定操作。它们允许更灵活的结构，可能具有完全不同的属性，并且可能支持多个关联的Condition对象。

​	锁是一种用于控制多个线程对共享资源的访问的工具。通常，锁提供对共享资源的独占访问：一次只有一个线程可以获取该锁，并且对共享资源的所有访问都需要首先获取该锁。但是，某些锁可能允许并发访问共享资源，例如 ReadWriteLock 的读取锁。

​	使用同步方法或语句提供对与每个对象关联的隐式监视器锁的访问，但强制所有锁的获取和释放以块结构的方式进行：当获取多个锁时，它们必须以相反的顺序释放，并且所有锁必须在获取它们的同一词法范围内释放。

​	虽然同步方法和语句的作用域机制使使用监视器锁进行编程变得更加容易，并有助于避免许多涉及锁的常见编程错误，但在某些情况下，您需要以更灵活的方式使用锁。例如，一些遍历并发访问数据结构的算法需要使用“hand-over-handing”或“链锁”：获取节点 A 的锁，然后获取节点 B，然后释放 A 并获取 C，然后释放 B 并获取 D，依此类推。Lock 接口的实现允许在不同作用域中获取和释放锁，并允许以任意顺序获取和释放多个锁，从而支持使用此类技术。

​	随着灵活性的提高，责任也随之增加。没有块结构锁定会消除同步方法和语句发生的锁的自动释放。在大多数情况下，应使用以下固定形式：

```java
Lock l = ...;  
l.lock();  
try {    // access the resource protected by this lock  
} finally {    
    l.unlock();  
}
```

​	当锁定和解锁发生在不同的范围内时，必须注意确保在保持锁定时执行的所有代码都受到 try-finally 或 try-catch 的保护，以确保在必要时释放锁。

​	与使用同步方法和语句相比，Lock 实现提供了额外的功能，包括提供获取锁的非阻塞尝试 （tryLock（））、获取可中断的锁的尝试 （lockInterruptibly） 和获取可超时的锁的尝试 （tryLock（long， TimeUnit））。

​	Lock 类还可以提供与隐式监视器锁完全不同的行为和语义，例如保证排序、不可重入使用或死锁检测。如果实现提供了这种专门的语义，则该实现必须记录这些语义。

​	Lock 实例只是普通对象，本身可以用作同步语句中的目标。获取 Lock 实例的监视器锁与调用该实例的任何锁定方法没有指定关系。为避免混淆，建议永远不要以这种方式使用 Lock 实例，除非在它们自己的实现中。

​	锁获取的三种形式（可中断、不可中断和定时）在性能特征、顺序保证或其他实现质量方面可能有所不同。此外，中断正在进行的锁获取的功能在给定的锁类中可能不可用。因此，不需要实现为所有三种形式的锁获取定义完全相同的保证或语义，也不需要支持中断正在进行的锁获取。需要实现来清楚地记录每个锁定方法提供的语义和保证。它还必须遵守此接口中定义的中断语义，只要支持锁获取的中断：要么完全中断，要么仅在方法输入时中断。

​	由于中断通常意味着取消，并且对中断的检查通常不频繁，因此实现可能更倾向于响应中断而不是正常方法返回。即使可以证明中断发生在另一个操作可能已取消阻止线程之后，也是如此，实现应记录此行为。

源码：

```java

public interface Lock {
	获取锁。如果锁不可用，则当前线程将出于线程调度目的而被禁用，并处于休眠状态，直到获取锁。
    void lock();

    获取锁，除非当前线程中断。获取锁（如果可用）并立即归还。如果锁不可用，则当前线程将出于线程调度目的而被禁用并处于休眠状态，直到发生以下两种情况之一：锁由当前线程获取;或 其他线程中断当前线程，支持锁获取中断。如果当前线程：在进入此方法时设置了中断状态;或者在获取锁时被中断，并且支持锁获取中断，则抛出 InterruptedException，清除当前线程的中断状态。
    void lockInterruptibly() throws InterruptedException;

    仅当锁在调用时处于空闲状态时，才会获取锁。获取锁（如果可用），并立即返回值为 true。如果锁不可用，则此方法将立即返回值 false。此方法的典型用法是：
    Lock lock = ...;  
    if (lock.tryLock()) {    
        try {      
            // manipulate protected state    
        } finally {      
            lock.unlock();    
        }  
    } else {
        // perform alternative actions  
    }
    此用法可确保在获取锁时解锁，如果未获取锁，则不会尝试解锁。
    boolean tryLock();

    如果锁在给定的等待时间内处于空闲状态，并且当前线程未被中断，则获取锁。如果锁可用，此方法将立即返回值 true。如果锁不可用，则当前线程将出于线程调度目的而被禁用并处于休眠状态，直到发生以下三种情况之一：锁由当前线程获取;或者其他线程中断当前线程，支持锁获取中断;或 经过指定的等待时间 如果获取了锁，则返回值 true。
    如果当前线程：在进入此方法时设置了中断状态;或者在获取锁时被中断，并且支持锁获取中断，则抛出 InterruptedException，清除当前线程的中断状态。如果已过指定的等待时间，则返回值 false。如果时间小于或等于零，则该方法将完全不等待。
    boolean tryLock(long time, TimeUnit unit) throws InterruptedException;

    Lock 实现通常会对哪个线程可以释放锁施加限制（通常只有锁的持有者才能释放锁），并且如果违反限制，可能会抛出（未选中的）异常。任何限制和异常类型都必须由该 Lock 实现记录
    void unlock();

    返回绑定到此 Lock 实例的新 Condition 实例。在等待条件之前，锁必须由当前线程保持。对 Condition.await（） 的调用将在等待之前以原子方式释放锁，并在等待返回之前重新获取锁。
    Condition newCondition();
}
```

举例：

```java
import java.util.concurrent.locks.Lock;  
import java.util.concurrent.locks.ReentrantLock;  
  
public class LockInterruptiblyExample {  
  
    private final Lock lock = new ReentrantLock();  
  
    public void performTask() throws InterruptedException {  
        // 尝试获取锁，如果线程在等待期间被中断，则抛出 InterruptedException  
        lock.lockInterruptibly();  
        try {  
            // 执行一些需要同步的任务  
            // ...  
        } finally {  
            // 释放锁  
            lock.unlock();  
        }  
    }  
  
    public static void main(String[] args) throws InterruptedException {  
        LockInterruptiblyExample example = new LockInterruptiblyExample();  
  
        Thread taskThread = new Thread(() -> {  
            try {  
                example.performTask();  
            } catch (InterruptedException e) {  
                System.out.println("Task thread interrupted.");  
                // 在这里可以选择结束线程或处理中断  
                Thread.currentThread().interrupt(); // 重置中断状态，以便上层可以处理  
            }  
        });  
  
        taskThread.start();  
  
        // 等待一段时间后中断线程  
        Thread.sleep(1000);  
        taskThread.interrupt();  
    }  
}
```

