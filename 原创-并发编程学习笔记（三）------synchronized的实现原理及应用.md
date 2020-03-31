# 原创：并发编程学习笔记（三）------synchronized的实现原理及应用

注明：<br/>
参考书作者：方腾飞 魏鹏 程晓明<br/>
参考书目：《Java 并发编程的艺术》

# Synchronized的实现原理

JAVA SE 1.6对synchronized进行了各种优化之后，有些情况下它显得不那么重了。为了减少获得锁和释放锁带来的性能消耗而引入了偏向锁和轻量级锁。<br/>
首先了解一下synchronized实现同步的基础：Java中每一个对象都可以作为锁。具体表现为三种形式：

## 锁升级与对比

Java SE 1.6为了减少获得锁和释放锁带来的性能损耗，引入了”偏向锁“和”轻量级锁“，在Java SE 1.6中，锁一共有4种状态，级别从低到高依次是：无锁状态、偏向锁状态、轻量级锁状态和重量级锁状态，这几种状态会随着竞争情况逐渐升级。<br/>
1.偏向锁<br/>
在大多数情况下，锁不仅不存在多线程竞争，而且总是由同一线程多次获得，为了让线程获得锁的代价更低而引入了偏向锁。当一个线程访问同步块并获取锁时，会在对象头和栈帧中的锁记录里存储锁偏向的线程ID。<br/>
个人理解：这就像去一个地方，第一次去的时候审核很严格，进去之后办了个会员卡，后面再去的时候查询下Id就可以了，道理都是一样的。<br/>
（1）偏向锁的撤销<br/>
偏向锁使用了一种等到竞争出现才释放锁的机制，所以当其他线程尝试竞争偏向锁时，持有偏向锁的线程才会释放锁。<br/>
<img alt="在这里插入图片描述" src="https://img-blog.csdnimg.cn/20190707223526742.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/><br/>
（2）关闭偏向锁<br/>
偏向锁在JAVA6 和JAVA7中是默认启用的，可以使用JVM参数来关闭延迟：`-XX:BiasedLockingStartupDelay=0`。若确定应用程序中所有的锁通常情况下处于竞争状态，可以通过JVM参数关闭偏向锁：`-XX:UseBiasedLocking=false`,那么程序默认会进入轻量级锁状态。<br/>
2.轻量级锁<br/>
（1）轻量级锁加锁<br/>
线程在执行同步块之前，JVM会先在当前线程的栈帧中创建用于存储锁记录的空间，并将对象头中的Mark Word 复制到锁记录中。然后线程尝试使用CAS将对象头中的Mark Word替换为指向锁记录的指针。如果成功，当前线程获得锁，如果失败，表示其他线程竞争锁，当前线程便尝试使用自旋来获取锁。<br/>
个人理解：这个地方就像甲将东西（Displaced Mark Word）从丙（对象头）那里借过来，然后给丙留了个家庭住址(锁记录的指针)。乙这个时候也要借这个东西，就需要徘徊等待（自旋）。<br/>
（2）轻量级锁解锁<br/>
轻量级解锁时，会使用原子的CAS操作将Displaced Mark Word替换回到对象头，如果成功，则表示没有竞争发生。如果失败，表示当前锁存在竞争，锁就会膨胀成重量级锁。<br/>
自旋会消耗CPU，为了避免无用的自旋，一旦升级为重量级锁，就不会再恢复成轻量级锁状态。<br/>
个人理解：这个时候甲用完了，想要把东西（Displaced Mard Word）还给丙（对象头）,如果乙等不及不借了，甲就可以很轻松的还了，好借好还再借不难（后续还是轻量级锁）。但是这个时候乙还没放弃，依然想借，甲和乙就商量好（锁膨胀，重量级锁），下次用好了直接叫对方（释放锁并唤醒其他线程），在那边不停徘徊（自旋）多浪费时间啊。<br/>
<img alt="在这里插入图片描述" src="https://img-blog.csdnimg.cn/20190707235159328.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/><br/>
3.锁的优缺点对比

<th align="center">锁</th><th align="center">优点</th><th align="center">缺点</th><th align="center">适用场景</th>
|------
<td align="center">偏向锁</td><td align="center">加锁和解锁不需要额外的消耗，和执行非同步方法相比仅存在纳秒级的差距</td><td align="center">如果线程间存在锁竞争，会带来额外的锁撤销的消耗</td><td align="center">适用于只有一个线程访问同步块场景</td>
<td align="center">轻量级锁</td><td align="center">竞争的线程不会阻塞，提高了程序的响应速度</td><td align="center">如果始终得不到锁竞争的线程，使用自旋会消耗CPU</td><td align="center">追求响应时间，同步块执行速度非常快</td>
<td align="center">重量级锁</td><td align="center">线程竞争不使用自旋，不会消耗CPU</td><td align="center">线程阻塞，响应时间慢</td><td align="center">追求吞吐量，同步块执行时间较长</td>
