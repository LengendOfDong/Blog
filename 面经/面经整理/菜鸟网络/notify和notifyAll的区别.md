# notify和notifyAll的区别是什么
当你调用notify时，只有一个等待线程会被唤醒而且它不能保证哪个线程会被唤醒，这取决于线程调度器。

当你调用notifyAll时，等待该锁的所有线程都会被唤醒

```java
/**
     * Wakes up a single thread that is waiting on this object's
     唤醒在该对象监视器上等待的一个单一线程
     * monitor. If any threads are waiting on this object, one of them
     如果有许多线程都在这个对象监视器上等待，那么其中一个将会被唤醒
     * is chosen to be awakened. The choice is arbitrary and occurs at
      唤醒线程的选择是随机的，这取决于线程调度器的实现。
     * the discretion of the implementation. A thread waits on an object's
     线程通过调用wait方法之一来等待对象监视器
     * monitor by calling one of the {@code wait} methods.
     * <p>
     * The awakened thread will not be able to proceed until the current
      被唤醒线程将不能继续进行直到当前线程的放弃在这个对象上的锁。
     * thread relinquishes the lock on this object. The awakened thread will
      被唤醒线程将以惯用的方式和其他被激活的同步线程竞争
     * compete in the usual manner with any other threads that might be
     * actively competing to synchronize on this object; for example, the
      例如，被激活的线程成为下一个锁定这个对象的线程将会变得不可靠
     * awakened thread enjoys no reliable privilege or disadvantage in being
     * the next thread to lock this object.
     * <p>
     * This method should only be called by a thread that is the owner
     这个方法应该仅被这个对象监视器的拥有线程来调用
     * of this object's monitor. A thread becomes the owner of the
     线程通过三种方式来成为对象监视器的拥有者：
     * object's monitor in one of three ways:
     * <ul>
     * <li>By executing a synchronized instance method of that object.
     通过执行对象的同步执行方法
     * <li>By executing the body of a {@code synchronized} statement
     *     that synchronizes on the object.
     通过执行同步对象的语句体
     * <li>For objects of type {@code Class,} by executing a
     *     synchronized static method of that class.
     通过执行那个类的同步静态方法
     * </ul>
     * <p>
     * Only one thread at a time can own an object's monitor.
     仅有一条线程能够每次能够拥有一个对象的监视器
     *
     * @throws  IllegalMonitorStateException  if the current thread is not
     *               the owner of this object's monitor.
     * @see        java.lang.Object#notifyAll()
     * @see        java.lang.Object#wait()
     */
    public final native void notify();
  
/**
     * Wakes up all threads that are waiting on this object's monitor. A
     唤醒在对象监视器上等待的所有线程
     * thread waits on an object's monitor by calling one of the
     通过调用一个wait方法来等待对象监视器
     * {@code wait} methods.
     * <p>
     * The awakened threads will not be able to proceed until the current
     * thread relinquishes the lock on this object. The awakened threads
     * will compete in the usual manner with any other threads that might
     * be actively competing to synchronize on this object; for example,
     * the awakened threads enjoy no reliable privilege or disadvantage in
     * being the next thread to lock this object.
     * <p>
     * This method should only be called by a thread that is the owner
     * of this object's monitor. See the {@code notify} method for a
     * description of the ways in which a thread can become the owner of
     * a monitor.
     *
     * @throws  IllegalMonitorStateException  if the current thread is not
     *               the owner of this object's monitor.
     * @see        java.lang.Object#notify()
     * @see        java.lang.Object#wait()
     */
      public final native void notifyAll();
```

# 为什么总是在循环内调用wait()

```java
/**
     * Causes the current thread to wait until either another thread invokes the
     * {@link java.lang.Object#notify()} method or the
     * {@link java.lang.Object#notifyAll()} method for this object, or a
     * specified amount of time has elapsed.
     引发当前线程等待，直到另一个线程调用notify或者notifyall方法，或者经过一段时间之后。
     * <p>
     * The current thread must own this object's monitor.
     当前线程必须要持有对象监视器
     * <p>
     * This method causes the current thread (call it <var>T</var>) to
     该方法引发当前线程将自身放置到等待集合中，然后放弃所有的对象上的同步声明
     * place itself in the wait set for this object and then to relinquish
     * any and all synchronization claims on this object. Thread <var>T</var>
     * becomes disabled for thread scheduling purposes and lies dormant
     线程T不能被线程调度，休眠到以下四种情况发生：
     * until one of four things happens:
     * <ul>
     * <li>Some other thread invokes the {@code notify} method for this
     其他某个线程调用了该对象的notify方法，线程T碰巧被任意选择，当线程被唤醒时。
     * object and thread <var>T</var> happens to be arbitrarily chosen as
     * the thread to be awakened.
     * <li>Some other thread invokes the {@code notifyAll} method for this
     其他某个线程调用了该对象的notifyAll方法
     * object.
     * <li>Some other thread {@linkplain Thread#interrupt() interrupts}
     其他某个线程中断了线程T
     * thread <var>T</var>.
     * <li>The specified amount of real time has elapsed, more or less.  If
     * {@code timeout} is zero, however, then real time is not taken into
     * consideration and the thread simply waits until notified.
     在一定时间后被唤醒，如果超时时间设置为0，那么超时时间不考虑在内，线程只是简单地等待被通知。
     * </ul>
     * The thread <var>T</var> is then removed from the wait set for this
     线程T从这个对象的等待集合中移除，并重新恢复被线程调度器调度
     * object and re-enabled for thread scheduling. It then competes in the
     之后以惯用的方式和其他线程竞争同步权利
     * usual manner with other threads for the right to synchronize on the
     * object; once it has gained control of the object, all its
     一旦它获取到了对象的控制，所有它在对象上的同步声明都恢复到原状
     * synchronization claims on the object are restored to the status quo
     * ante - that is, to the situation as of the time that the {@code wait}
     原状就是wait方法被调用时的场景
     * method was invoked. Thread <var>T</var> then returns from the
     线程T从wait方法调用中返回
     * invocation of the {@code wait} method. Thus, on return from the
     此时，从wait方法返回时，对象和线程T的同步状态就如同wait方法被调用时相同
     * {@code wait} method, the synchronization state of the object and of
     * thread {@code T} is exactly as it was when the {@code wait} method
     * was invoked.
     * <p>
     * A thread can also wake up without being notified, interrupted, or
     线程也能够不需要通知，中断，超时或者虚假唤醒的情况下呗唤醒
     * timing out, a so-called <i>spurious wakeup</i>.  While this will rarely
     尽管这很少在实际中出现，应用也必须防止它出现，通过检测引发线程被唤醒的情况，如果情况不满足然后继续等待
     * occur in practice, applications must guard against it by testing for
     * the condition that should have caused the thread to be awakened, and
     * continuing to wait if the condition is not satisfied.  In other words,
     换句话说，等待应该总是出现在循环中，就像这样：
     * waits should always occur in loops, like this one:
     * <pre>
     *     synchronized (obj) {
     *         while (&lt;condition does not hold&gt;)
     *             obj.wait(timeout);
     *         ... // Perform action appropriate to condition
     *     }
     * </pre>
     * (For more information on this topic, see Section 3.2.3 in Doug Lea's
     * "Concurrent Programming in Java (Second Edition)" (Addison-Wesley,
     * 2000), or Item 50 in Joshua Bloch's "Effective Java Programming
     高效Java中的第50条
     * Language Guide" (Addison-Wesley, 2001).
     *
     * <p>If the current thread is {@linkplain java.lang.Thread#interrupt()
     当前线程被其他线程中断或者当它正在等待时，一个中断异常被抛出
     * interrupted} by any thread before or while it is waiting, then an
     * {@code InterruptedException} is thrown.  This exception is not
     * thrown until the lock status of this object has been restored as
     异常不会被抛出来，直到对象的锁状态如上描述那样恢复。
     * described above.
     *
     * <p>
     * Note that the {@code wait} method, as it places the current thread
     * into the wait set for this object, unlocks only this object; any
     * other objects on which the current thread may be synchronized remain
     * locked while the thread waits.
     * <p>
     * This method should only be called by a thread that is the owner
     * of this object's monitor. See the {@code notify} method for a
     * description of the ways in which a thread can become the owner of
     * a monitor.
     *
     * @param      timeout   the maximum time to wait in milliseconds.
     * @throws  IllegalArgumentException      if the value of timeout is
     *               negative.
     * @throws  IllegalMonitorStateException  if the current thread is not
     *               the owner of the object's monitor.
     * @throws  InterruptedException if any thread interrupted the
     *             current thread before or while the current thread
     *             was waiting for a notification.  The <i>interrupted
     *             status</i> of the current thread is cleared when
     *             this exception is thrown.
     * @see        java.lang.Object#notify()
     * @see        java.lang.Object#notifyAll()
     */
    public final native void wait(long timeout) throws InterruptedException;
```
