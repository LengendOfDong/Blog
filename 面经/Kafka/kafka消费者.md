## 创建Kafka消费者
创建消费者与生产者类似，不过就是新增了一个参数group.id,这个参数用来指定KafkaCustomer属于哪一个消费者群组。

## 订阅主题
subscribe()方法接受一个主题列表作为参数：
```java
customer.subscribe(Collections.singletonList("customerCountries"));
```
可以在调用subscribe()方法时传入一个正则表达式，正则表达式可以匹配多个主题。在kafka和其他系统之间复制数据时，使用正则表达式的方式订阅多个主题是很常见的。

例如要订阅所有与test相关的主题，可以写成：
```java
customer.subscribe("test.*")
```

## 轮询
消息轮询是消费者API的核心，消费者代码如下：
```java
try {
   while(true) {
        ConsumerRecords<String, String>  records = consumer.poll(100);
        for (ConsumerRecord<String,String>  record : records ) {
             //do something with record
        }
   }
} finally {
   consumer.close();
}
```
消费者是一个长期运行的应用程序，通过持续轮询向Kafka发送请求数据。

传给poll()方法的参数是一个超时时间，用于控制poll()方法的阻塞时间(在消费者的缓冲区中没有数据会发生阻塞)。如果该参数被设置为0，poll()会立即返回，否则会在指定的毫秒数内一直等待broker返回数据。

在退出应用程序之前使用close()方法关闭消费者。网络连接和socket也会随之关闭，并立即触发一次再均衡，而不是等待群组协调器发现它不再发送心跳并认定它已死亡，因为那样需要更长的时间，导致整个群组在一段时间内无法读取消息。
