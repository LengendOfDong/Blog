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

## 消费者的配置
一些重要的参数配置：
- fetch.min.bytes:该属性指定了消费者从服务器获取记录的最小字节数。
- fetch.max.wait.ms:用于指定broker的等待时间，默认是500ms。在同时设置了fetch.max.wait.ms和fetch.min.bytes之后，就看哪个条件先满足。
- max.partition.fetch.bytes:该属性指定了服务器从每个分区里返回给消费者的最大字节数，默认值为1MB。max.partition.fetch.bytes的值必须比broker能够接受的最大消息的字节数大，否则消费者可能无法读取这些消息，导致消费者一直挂起重试。
- session.timeout.ms:该属性指定了消费者在被认为死亡之前可以与服务器断开连接的时间，默认是3s。如果消费者没有在session.timeout.ms指定的时间内发送心跳给群组协调器，就被认为已经死亡了，协调器就会触发再平衡，将分区分配给其他消费者。
- heartbeat.interval.ms：指定了poll()方法向协调器发送心跳的频率，通常来说，heartbeat.interval.ms是session.timeout.ms的三分之一。
- auto.offset.reset：当消费者长时间失效，包含偏移量的记录已经被删除，造成偏移量无效的情况下，该参数设置为latest，则从最新的记录开始读取数据，如果设置为earlist，则从起始的位置开始读取数据。
- enable.auto.commit:该属性指定了消费者是否自动提交偏移量，默认值为true。为了避免出现重复数据和数据丢失，可以将其设置为false，由自己控制何时提交偏移量。
- partition.assignment.strategy:PartitionAssignor根据给定的消费者和主题，决定哪些分区应该被分配给哪个消费者。Kafka有两个默认的分配策略。
   - Range:如果存在两个主题，每个主题3个分区，同时有两个消费者订阅这两个主题，则第一个消费者分配到两个，第二个消费者只分配到一个，相当于给了第一个消费者整除的部分，而第二个消费者余数部分。只要使用了Range策略，而且分区数量无法被消费者数量整除，就会出现这种情况。
   - RoundRobin:该策略把主题的所有分区逐个分配给消费者。默认是用的Range策略。
- max.poll.records:该属性用于控制单次调用call()方法能够返回的记录数量，可以帮你控制在轮询里需要处理的数据量。
- receive.buffer.bytes:socket在读写数据时用到的TCP缓冲区也可以设置大小。如果它们被设置为-1，就使用操作系统的默认值。

## 提交和偏移量
消费者可以在Kafka中追踪到消息在分区中的位置（偏移量）。我们将更新分区位置叫做**提交**。

消费者通过_consumer_offset的特殊主题发送消息，消息里包含每个分区的偏移量，如果消费者一直处于运行状态，那么偏移量就没有用处。

当消费者发生崩溃或者有新的消费者加入到消费者组中时，会触发**再平衡**，此时每个消费者可能不会接收原来的分区，为了能够继续之前的工作，消费者需要读取每个分区最后一次提交的偏移量，然后从偏移量的地方继续处理。

如果提交的偏移量小于客户端处理的最后一个消息的偏移量，那么处于两个偏移量之间的消息就会重复处理。如果提交的偏移量大于客户端处理的最后一个消息的偏移量，那么处于两个偏移量之间的消息就会丢失。

同步和异步组合提交：如果发生在关闭消费者或者再均衡前的最后一次提交，就要确保能够提交成功。在消费者关闭前一般会组合使用commitAsync()和commitSync()。
```java
 try{
    while(true) {
        ConsumerRecords<String,String> records = consumer.poll(100);
        //do something with record
        
       consumer.commitAsync();
    }
 } catch (Exception e) {
    log.error("Unexception error", e);
 } finally {
    try {
       consumer.commitSync();
    } finally {
       consumer.close();
    }
 }
```

## 提交特定的偏移量
如果想要在中间提交偏移量，而不是等到所有消息处理完之后再提交，可以调用commitSync()和commitAsync()方法时传进去希望提交的分区和偏移量的map。
```java
private Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
int count = 0;

while (true) {
   ConsumerRecords<String,String> records = customer.poll(100);
   for (ConsumerRecord<String,String> record: records ) {
      //do something with record
      currentOffsets.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1, "no metadata"));
      if (count % 1000 == 0) {
          consumer.commitAsync(currentOffsets, null);
      }
      count++;
   }
   
}
```

## 独立消费者
如果需要消费者从一个主题的所有分区或者某个特定的分区读取数据，这时候就会用到独立消费者。
```java
List<PartitionInfo> partitionInfos = null;
partitionInfos = consumer.partitionsFor("topic");//向集群请求主题可用的分区

if(partitionInfos ! = null) {
   for(PartitionInfo partition: partitionInfos) {
      partitions.add(new TopicPartition(partition.topic(), partition.partition());
   }
   consumer.assign(partitions);
   while (true) {
      ConsumerRecords<String,String> records = customer.poll(100);
      for (ConsumerRecord<String,String> record: records ) {
         //do something with record
      }
      consumer.commitSync();
   }
}
```
如果主题增加了新的分区，消费者并不会收到通知， 所以要么周期性地调用consumer.partitionFor方法来检查是否有新的分区加入，要么在添加新分区后重启应用程序。

