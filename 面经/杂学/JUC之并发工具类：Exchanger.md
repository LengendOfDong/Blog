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
