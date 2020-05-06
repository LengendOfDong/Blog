# Steam
Stream是一个新的强大的支持多播的可持久化的消息队列，极大地借鉴了Kafka的设计。

Redis Stream有一个消息链表，所有加入的消息都会串起来，每个消息都有一个唯一的ID和对应的内容。消息是持久化的，Redis重启后，内容还在。追加消息时自动创建。

每个Stream可以挂多个消费组，每个消费组会有个游标last_delivered_id在Stream数组上往前移动，表示当前消费组已经消费到了哪条消息了。

每个消费组都有一个Stream内唯一的名称，消费组不会自动创建，它需要单独的指令xgroup create进行创建，需要指定从Stream的某个消息ID开始消费，这个ID用来初始化last_delivered_id变量。

同一份Stream内部的消息会被每个消费组都消费到。

同一个消费组可以挂接多个消费者，这些消费者之间是竞争关系，任意一个消费者读取了消息都会使游标last_delivered_id往前移动。每个消费者有个组内唯一名称。

消费者内部会有一个状态变量pending_ids,记录了当前已经被客户端读取但是还没有ack的消息。如果客户端没有ack,这个变量里面的消息ID就会越来越多，一旦某个消息被ack,消息就开始减少。

pending_list变量在Redis官方被称为PEL，也就是Pending Entries List,这是一个核心的数据结构，它用来确保客户端至少消费了消息一次，而不会在网络传输中中途丢失了而没有被处理。

## 增删改查
- xadd: 向Stream追加消息
- xdel: 从Stream中删除消息，这里的删除仅仅是设置标志位，不影响消息总长度。
- xrange: 获取Stream中的消息列表，会自动过滤已经删除的消息。
- xlen: 获取Stream消息长度。
- del: 删除整个Stream消息列表中的所有消息。

## 独立消费
xread可以将Stream当成普通的消息队列（list）来使用。

## 创建消费组
Stream通过xgroup create创建消费组，创建消费组需要提供起始消息ID来初始化last_delivered_id变量。

## 消费
Stream提供了xreadgroup 指令可以进行消费组的组内消费，需要提供消费组名称、消费者名称和起始消息ID。

当客户端读到新消息后对应的消息ID就会进入消费者的PEL（正在处理的消息）结构里，客户端处理完毕后使用xack指令通知服务器，本条消息已经处理完毕，该消息ID就会从PEL中移除。

