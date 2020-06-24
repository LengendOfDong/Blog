# 简介
在API中是这么介绍的：可以对元素进行配对和交换的线程的同步点。

Exchanger允许在并发任务之间交换数据，具体来说，Exchanger类允许在两个线程之间定义同步点。当两个线程都到达同步点时，它们交换数据结构，因此第一个线程的数据进入到第二个线程中，第二个线程的数据结构进入到第一个线程中。

# 应用示例
```java
public class ExchangerTest {

    static class Producer implements Runnable{

        //生产者、消费者交换的数据结构
        private List<String> buffer;

        //步生产者和消费者的交换对象
        private Exchanger<List<String>> exchanger;

        Producer(List<String> buffer,Exchanger<List<String>> exchanger){
            this.buffer = buffer;
            this.exchanger = exchanger;
        }

        @Override
        public void run() {
            for(int i = 1 ; i < 5 ; i++){
                System.out.println("生产者第" + i + "次提供");
                for(int j = 1 ; j <= 3 ; j++){
                    System.out.println("生产者装入" + i  + "--" + j);
                    buffer.add("buffer：" + i + "--" + j);
                }

                System.out.println("生产者装满，等待与消费者交换...");
                try {
                    exchanger.exchange(buffer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Consumer implements Runnable {
        private List<String> buffer;

        private final Exchanger<List<String>> exchanger;

        public Consumer(List<String> buffer, Exchanger<List<String>> exchanger) {
            this.buffer = buffer;
            this.exchanger = exchanger;
        }

        @Override
        public void run() {
            for (int i = 1; i < 5; i++) {
                //调用exchange()与消费者进行数据交换
                try {
                    buffer = exchanger.exchange(buffer);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("消费者第" + i + "次提取");
                for (int j = 1; j <= 3 ; j++) {
                    System.out.println("消费者 : " + buffer.get(0));
                    buffer.remove(0);
                }
            }
        }
    }

    public static void main(String[] args){
        List<String> buffer1 = new ArrayList<String>();
        List<String> buffer2 = new ArrayList<String>();

        Exchanger<List<String>> exchanger = new Exchanger<List<String>>();

        Thread producerThread = new Thread(new Producer(buffer1,exchanger));
        Thread consumerThread = new Thread(new Consumer(buffer2,exchanger));

        producerThread.start();
        consumerThread.start();
    }
}
```
运行结果：
```java
生产者第1次提供
生产者装入1--1
生产者装入1--2
生产者装入1--3
生产者装满，等待与消费者交换...
生产者第2次提供
生产者装入2--1
消费者第1次提取
生产者装入2--2
消费者 : buffer：1--1
生产者装入2--3
消费者 : buffer：1--2
消费者 : buffer：1--3
生产者装满，等待与消费者交换...
生产者第3次提供
消费者第2次提取
消费者 : buffer：2--1
生产者装入3--1
消费者 : buffer：2--2
消费者 : buffer：2--3
生产者装入3--2
生产者装入3--3
生产者装满，等待与消费者交换...
生产者第4次提供
消费者第3次提取
消费者 : buffer：3--1
生产者装入4--1
消费者 : buffer：3--2
生产者装入4--2
消费者 : buffer：3--3
生产者装入4--3
生产者装满，等待与消费者交换...
消费者第4次提取
消费者 : buffer：4--1
消费者 : buffer：4--2
消费者 : buffer：4--3
```

# 实现分析
Exchanger算法的核心是通过一个可交换数据的slot,以及一个可以带有数据item的参与者。源码中的描述如下：
```java
for (;;) {
        if (slot is empty) {                       // offer
          place item in a Node;
          if (can CAS slot from empty to node) {
            wait for release;
            return matching item in node;
          }
        }
        else if (can CAS slot from node to empty) { // release
          get the item in node;
          set matching item in node;
          release waiting thread;
        }
        // else retry on CAS failure
      }
```
Exchanger中定义了如下几个重要的成员变量：
```java
private final Participant participant;
private volatile Node[] arena;
private volatile Node slot;
```
participant的作用是为每个线程保留唯一的一个Node节点。slot为单个槽，arena为数组槽，它们都是Node类型。一个slot交换场所原则上来说应该是可以的，但实际情况却不是如此，多个参与者使用同一个交换场所时，会存在严重伸缩性问题。既然单个交换场所存在问题，那么我们就安排多个，也就是数组arena.通过数组arena来安排不同的线程使用不同的slot来降低竞争问题，并且可以保证最终一定会成对交换数据。

Exchanger不是一来就会生成arena数组来降低竞争，只有当产生竞争是才会生成arena数组。那么怎么将Node与当前线程绑定呢？Participant，Participant的作用就是为每个线程保留唯一的一个Node节点，它继承ThreadLocal，同时在Node节点中记录在arena中的下标index.

Node定义如下：
```java
@sun.misc.Contended static final class Node {
        int index;              // Arena index
        int bound;              // Last recorded value of Exchanger.bound
        int collides;           // Number of CAS failures at current bound
        int hash;               // Pseudo-random for spins
        Object item;            // This thread's current item
        volatile Object match;  // Item provided by releasing thread
        volatile Thread parked; // Set to this thread when parked, else null
    }
```
1.index：arena的下标

2.bound：上一次记录的Exchanger.bound

3.collides:在当前bound下CAS失败的次数

4.hash：伪随机数，用于自旋

5.item:这个线程的当前向，也就是需要交换的数据

6.match:做releasing操作的线程传递的项

7.parked：挂起时设置线程值，其他情况下为null

