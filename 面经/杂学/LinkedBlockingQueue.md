# 简介
LinkedBlockingQueue是java并发包下一个以单链表实现的阻塞队列，它是线程安全的。

入队和出队使用两个不同的锁控制，锁分离，提高效率。

# 总结

（1）LinkedBlockingQueue采用单链表的形式实现；

（2）LinkedBlockingQueue采用两把锁的锁分离技术实现入队出队互不阻塞；

（3）LinkedBlockingQueue是有界队列，不传入容量时默认为最大int值；

- LinkedBlockingQueue和ArrayBlockingQueue对比？

a)后者入队出队采用一把锁，导致入队出队相互阻塞，效率低下；

b)前者入队出队采用两把锁，入队出队互不干扰，效率较高；

c)二者都是有界队列，如果长度相等且出队速度跟不上入队速度，都会导致大量线程阻塞

d)前者如果初始化不传入初始容量，则使用最大int值，如果出队速度跟不上入队速度，会导致队列特别长，占用大量内存。
