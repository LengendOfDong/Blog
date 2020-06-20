# 简介
在没有Lock之前，我们使用synchronized来控制同步，配合Object的wait()、notify()系列方法可以实现等待、通知模式。在Java SE5后，Java提供了Lock接口，相对与Synchronized而言，Lock提供了条件Condition，对线程的等待，唤醒操作更加详细和灵活。
![Condition与Synchronized](https://github.com/LengendOfDong/Blog/blob/master/img/Condition%E4%B8%8ESynchronized.png)

Condition是一种广义上的条件队列。他为线程提供了一种更为灵活的等待、通知模式，线程在调用await方法后执行挂起操作，直到线程等待某个条件为真时才会被唤醒。Condition必须要配合锁一起使用，因为对共享状态变量的访问发生在多线程环境下。一个Condition的实例必须与一个Lock绑定，因此Condition一般都是作为Lock的内部实现。

# Condition的实现
获取一个Condition必须要通过Lock的newCondition()方法。该方法定义在接口Lock下面，返回的结果是绑定到此Lock实例的新Condition实例。

