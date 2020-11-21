# 生产者概览
一个应用程序在很多情况下需要往kafka中写入消息：记录用户的活动用于审计和分析、记录度量指标、保存日志消息、与其他应用程序进行异步通信、缓冲即将写入到数据库的数据等等。

创建一个ProducerRecord对象，需要包含目标主题和要发送的内容，当然还可以指定键或者分区。
- 第一步：在发送ProducerRecord时，生产者需要先把键和值对象序列化成字节数组，这样它们才能在网络上传输
- 第二步：数据被传输给分区器，此时分为两种情况处理，第一种情况是如果之前指定了分区，则直接返回指定的分区，第二种情况是没有指定分区，则会根据ProducerRecord中的键来选择一个分区。选好分区之后，生产者就知道往哪个主题和分区发送消息了。
- 第三步：这条记录被添加到一个批次里，这个批次里的所有记录都会发送到相同的主题和分区上，有一个独立的线程负责把这些记录批次发送到相应的broker上。
- 第四步：如果消息成功写入Kafka中，就会返回一个RecordMetadata对象，包含了主题和分区信息，以及记录在分区中的偏移量。如果没有写入成功，则会返回一个错误，生产者在收到这个错误之后会重新发送消息，几次之后如果还是失败，就返回错误信息。

# 创建Kafka生产者
Kafka生产者有3个必选的属性：
- bootstrap.servers:该属性指定broker的地址清单，地址的格式为host:port,建议至少两个broker的信息，一旦其中一个宕机，生产者仍然能够连接到集群上。
- key.serializer:broker希望接收到的消息的键和值都是字节数组。可以设置为自定义的序列化类，需要实现org.apache.kafka.common.serialization.Serializer接口的类。kafka客户端默认提供了ByteArraySerializer/StringSerializer/IntegerSerializer.
- value.serializer:同key.serializer情况相同。

```java
private Properties kafkaProps = new Properties();
kafkaProps.put("bootstrap.servers","broker1:9092,broker2:9092")
kafkaProps.put("key.serializer","org.apache.kafka.common.serialization.Serializer");
kafkaProps.put("value.serializer","org.apache.kafka.common.serialization.Serializer");
producer= new KafkaProducer<String, String>(kafkaProps);
```

发送消息主要有3种方式：
- 发送并忘记（fire-and-forget）：消息发送给服务器，并不关心发送是否成功，通常情况下是会成功的，因为kafka是高可用的，失败之后还会进行重试，不过有时候也会丢失消息。
- 同步发送：使用send()方法发送消息，会返回一个Future对象，调用get()方法进行等待，就可以知道消息是否成功
- 异步发送：调用send()方法，并指定一个回调函数，服务器在返回响应时调用该函数。

# 发送消息到Kafka
## 发送并忘记
```java
ProducerRecord<String,String> record = new ProducerRecord<String,String>("CustomerCountry","Precision Products","France");
try {
  producer.send(record);
} catch (Exception e) {
  e.printStackTrace();
}
```
生产者的send()方法将ProducerRecord对象作为参数，键和值得对象的类型必须与序列化器相匹配。此处调用send()方法会返回一个Future对象，但是此处忽略了返回值，无法知道消息是否发送成功。如果不关心发送结果，那么可以使用这种方式。

虽然可以忽略发送消息时的错误，或者服务器端发生的错误，但是在发送之前，生产者还是有可能发生其他的错误。比如SerializationException(发生在序列化器中的错误，序列化异常)，BufferExhaustedException和TimeOutException（发生在缓冲区的错误，缓冲区已满），InterruptedException(发生在发送线程的错误，发送线程被中断)

## 同步发送消息
```java
ProducerRecord<String,String> record = new ProducerRecord<String,String>("CustomerCountry","Precision Products","France");
try {
  producer.send(record).get();
} catch (Exception e) {
  e.printStackTrace();
}
```
producer.send()会返回一个Future对象，然后调用Future对象的get()方法等待Kafka响应。如果服务器返回错误，get()方法会抛出异常，如果没有发生错误，会得到一个RecordMetadata对象，可以用它获取消息的偏移量。

KafkaProducer一般会发生两类错误，其中一类是可重试错误，这类错误可以通过重发消息来解决。比如对于连接错误，可以通过再次建立连接来解决。另一类错误无法通过重试解决，比如“消息太大”异常。对于这类错误，KafkaProducer不会进行任何重试，直接返回异常。

## 异步发送消息
为了在异步发送消息的同时能够对异常情况进行处理，生产者提供了回调支持。
```java
private class DemoProducerCallBack implements Callback {
   public void onCompletion(RecordMetadata recordMetadata, Exception e) {
     if ( e != null ) {
          e.printStackTrace();
     }
   }
}
ProducerRecord<String,String> record = new ProducerRecord<String,String>("CustomerCountry","Precision Products","France");
producer.send(record, new DemoProducerCallBack());
```
为了使用回调，需要实现Callback接口的OnCompletion方法，在发送消息的时候传进去一个回调对象。

## 生产者的配置
生产者的大部分配置都有合理的默认值，但是有几个参数需要说明，会对内存使用、性能以及可靠性方面造成影响。

1. acks:acks参数指定了必须有多少个分区副本收到消息，生产者才会认为消息写入时成功的。
- acks=0:生产者在成功写入消息之前不会等待任何来自服务器的响应。缺点是消息如果出现了丢失，生产者也无从得知。优点是生产者不需要等待服务器的响应，所以它可以以网络能够支持的最大速度发送消息，从而达到很高的吞吐量。
- acks=1:只要集群的首领节点接受到消息，生产者就会收到一个来自服务器的成功响应。此时的吞吐量取决于使用的是同步发送还是异步发送。
- acks=all:只有当所有的参与复制的节点都收到消息时，生产者才会收到一个来自服务器的成功响应。这种模式是最安全的，它可以保证不止一个服务器收到消息，就算有服务器发生崩溃，整个集群仍可以运行。

2.buffer.memory参数用来设置生产者内存缓冲区的大小，生产者用它缓冲要发送到服务器的消息。如果应用程序发送消息的速度超过发送到服务器的速度，会导致生产者空间不足。

3.compression.type:使用压缩可以降低网络开销和存储开销，这往往是Kafka发送消息的瓶颈所在。默认情况下，消息发送时不会被压缩，该参数可以设置为snappy,gzip和lz4，它指定了消息被发送给broker之前使用
哪种压缩算法来进行压缩

4.retries:生产者从服务器收到的错误有可能是临时性的错误，比如分区找不到首领。retries参数的值决定了生产者可以重发消息的次数，超过这个次数，生产者就会放弃重试并返回错误。该参数配合上retry.backoff.ms参数改变时间间隔，来确定重试的总时间。

5.batch.size：该参数确定了一个批次可以使用的内存大小，按照字节数计算。当批次被填满，批次里的所有消息都会被发送出去。如果设置得太小，那么生产者需要更频繁地发送消息，会增加一些额外的开销。

6.client.id:该参数可以是任意的字符串，服务器会用它来识别消息的来源，还可以用在日志或者配合指标里。

7.
