# 简介
CyclicBarrier所描述的是“允许一组线程互相等待，直到到达某个公共屏障点，才会进行后续任务”，而CountDownLatch所描述的是“在完成一组正在其他线程执行的操作之前，它允许一个或多个线程一直等待”。

CountDownLatch举例：

看门大爷等十个工人都到达了才能休息，但是工人到达的时间点不一样，所以每到一个工人就减一，直到最后减到0，表示所有工人都到了，大爷就可以睡觉了。

CyclicBarrier举例：

看门大爷怀疑有人冒充（一人顶替两人），需要清点人数完才可以开门上工，终于等十个人都到齐了，大爷打开大门，工人**同时**进入。

CountDownLatch更加强调的是主线程要触发做什么事情，而CyclicBarrier则强调的是子线程同时做什么事情。

在CountDownLatch中触发了条件之后，子线程可以继续进行别的操作，而在CyclicBarrier中子线程是被阻塞住的，需要等到所有线程都触发条件之后才能继续进行。

CountDownLatch是通过一个计数器来实现的，当我们在new 一个CountDownLatch对象的时候需要带入该计数器值，该值就表示了线程的数量。每当一个线程完成自己的任务后，计数器的值就会减1。当计数器的值变为0时，就表示所有的线程均已经完成了任务，然后就可以恢复等待的线程继续执行了。

虽然，CountDownlatch与CyclicBarrier有那么点相似，但是他们还是存在一些区别的：

1. CountDownLatch的作用是允许1或N个线程等待其他线程完成执行；而CyclicBarrier则是允许N个线程相互等待

2. CountDownLatch的计数器无法被重置；CyclicBarrier的计数器可以被重置后使用，因此它被称为是循环的barrier


