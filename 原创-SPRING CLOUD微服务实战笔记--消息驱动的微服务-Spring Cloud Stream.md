# 原创：SPRING CLOUD微服务实战笔记--消息驱动的微服务:Spring Cloud Stream

### Spring Cloud Stream

# 快速入门

```
&lt;dependencies&gt;
	&lt;dependency&gt;
		&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
		&lt;artifactId&gt;spring-cloud-starter-stream-rabbit&lt;/artifactId&gt;
		&lt;version&gt;1.3.4.RELEASE&lt;/version&gt;
	&lt;/dependency&gt;
	&lt;dependency&gt;
		&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
		&lt;artifactId&gt;spring-boot-starter-web&lt;/artifactId&gt;
	&lt;/dependency&gt;

	&lt;dependency&gt;
		&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
		&lt;artifactId&gt;spring-boot-starter-test&lt;/artifactId&gt;
		&lt;scope&gt;test&lt;/scope&gt;
	&lt;/dependency&gt;
&lt;/dependencies&gt;

```

```
@EnableBinding(Sink.class)
public class SinkReceiver {
    private static Logger logger = LoggerFactory.getLogger(HelloApplication.class);
    @StreamListener(Sink.INPUT)
    public void receive(Object payload){
        logger.info("Received: " + payload);
    }
}

```

```
Received: [B@7236cac6

```

刚才我们在RabbitMQ管理界面发布的信息，由SinkReceiver来消费了<br/>
说一下定义在SinkReceiver的Spring Cloud Stream的核心注解<br/>
`@EnableBinding`:该注解用来指定一个或多个定义了@Input或@Output注解的接口，以此来实现对消息通道（Channel）的绑定。<br/>
`@StreamListener`：该注解主要定义在方法上，作用是将被修饰的方法注册为消息中间上数据流的事件监听器，注解中属性值对应了监听的消息通道名。<br/>
第一个注解主要用来将当前类`SinkReceiver`绑定到`Sink.class`上，而`Sink.class`已经绑定input通道，第二个注解主要监听input通道上的数据

# 核心概念

Spring Cloud Stream构建的应用程序与消息中间件之间是通过绑定器Binder相关联的，绑定器对于应用程序而言起到了隔离作用，它使得不同消息中间件的实现细节对应用程序来说是透明的。如下图所示，绑定器是作为通道和消息中间件之间的桥梁进行通信<br/>
<img alt="Binder" src="https://img-blog.csdnimg.cn/20190402155040334.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/>

## 绑定器

绑定器与数据库中的DataSource相同，在连接数据库时有很多数据库厂家，每个厂家的实现数据源细节都不尽相同，所以将数据源抽象成DataSource接口，就可以将具体实现细节进行隔离。此处的绑定器与其有相同的效果，由于各消息中间件构建的初衷不同，在实现细节上也有很大差异。通过定义绑定器作为中间层，完美地实现了应用程序与消息中间件细节之间的隔离。<br/>
通过向应用程序暴露统一的Channel通道，不需要再考虑各种不同的消息中间件的实现。当需要更换其他消息中间件时，要做的就是更换它们对应的Binder绑定器而不需要修改任何SpringBoot的应用逻辑。

## 发布-订阅模式

Spring Cloud Stream中的消息通信方式遵循了发布-订阅模式，当一条消息被投递到消息中间件之后，它会通过共享的Topic主题进行广播，消息消费者在订阅的主题中收到它并触发自身的业务逻辑处理。<br/>
生产者 生产消息发布在shared topic（共享主题）上，然后 消费者 通过订阅这个topic来获取消息<br/>
<img alt="发布订阅模式" src="https://img-blog.csdnimg.cn/20190402161514452.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/><br/>
在RabbitMQ中，Topic对应Exchange,在Kafka中对应Kafka中的Topic

## 消费组

在现实的微服务架构中，每一个微服务应用为了实现高可用和负载均衡，实际上都会部署多个实例。<br/>
如果在同一个主题上的应用需要启动多个实例时，可以通过`spring.cloud.stream.bindings.input.group`属性为应用指定一个组名，这样一个组里只有一个成员真正接收到消息并进行处理。<br/>
<img alt="消费组" src="https://img-blog.csdnimg.cn/20190402163121925.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/>

## 消息分区

对于同一条消息，多次到达之后可能是由不同的实例进行消费的。<br/>
对于一些场景，需要对一些具有相同特征的消息设置每次都被同一个消费实例处理<br/>
分区概念的引入就是为了解决这样的问题：当生产者将消息数据发送给多个消费者实例时，保证拥有共同特征的消息数据始终是由同一个消费者实例接收和处理。

# 使用详解

@EnableBinding注解用来创建消息通道的绑定

```
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
//注解后会成为Spring的基本配置类
@Configuration
//加载Spring Cloud Stream运行需要的几个基础配置类
@Import({BindingServiceConfiguration.class, BindingBeansRegistrar.class, BinderFactoryConfiguration.class, SpelExpressionConverterConfiguration.class})
@EnableIntegration
public @interface EnableBinding {
    Class&lt;?&gt;[] value() default {};
}

```

## 绑定消息通道

### 注入绑定接口

```
@Component
public interface SinkSender {
    @Output(Source.OUTPUT)
    MessageChannel output();
}

```

```
@EnableBinding(value = {Sink.class, SinkSender.class})
public class SinkReceiver {
    private static Logger logger = LoggerFactory.getLogger(SinkSender.class);
    @StreamListener(Sink.INPUT)
    public void receive(Object payload){
        logger.info("Received: " + payload);
    }
}

```

```
spring.cloud.stream.bindings.input.destination=raw-sensor-data
spring.cloud.stream.bindings.output.destination=raw-sensor-data

```

```
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class HelloApplicationTests {
	@Autowired
	private SinkSender sinkSender;
	@Test
	public void contextLoads() {
		sinkSender.output().send(MessageBuilder.withPayload("From SinkSender").build());
	}
}

```

### 注入消息通道

在测试类中加入下面内容，用于注入消息通道

```
@Autowired
private MessageChannel input;
@Test
public void contextLoads1(){
	input.send(MessageBuilder.withPayload("From MessageChannel").build());
}

```

## 消费组与消息分区

1.消费组<br/>
有些业务场景下，希望生产者产生的消息只被一个实例消费，这个时候就需要为这些消费者设置消费组来实现这样的功能

```
@EnableBinding(value = {Sink.class})
public class SinkReceiver {
    private static Logger logger = LoggerFactory.getLogger(SinkReceiver.class);
    @StreamListener(Sink.INPUT)
    public void receive(User user){
        logger.info("Received: " + user);
    }
}

```

```
spring.cloud.stream.bindings.input.group=Service-A
spring.cloud.stream.bindings.input.destination=greetings

```

这其中`spring.cloud.stream.bindings.input.group`属性指定了该应用实例都属于Service-A消费组，而`spring.cloud.stream.bindings.input.destination`属性则指定了输入通道对应的主题名

```
@EnableBinding(value = {Source.class})
public class SinkSender {
    private static Logger logger = LoggerFactory.getLogger(SinkSender.class);
    @Bean
    @InboundChannelAdapter(value = Source.OUTPUT,poller = @Poller(fixedDelay = "2000"))
    public MessageSource&lt;String&gt; timerMessageSource(){
        return ()-&gt;new GenericMessage&lt;&gt;("{\"name\":\"didi\",\"age\":30}");
    }
}

```

```
spring.cloud.stream.bindings.output.destination=greetings

```

启动多个消费者实例，再启动生产者实例，通过输出可以看到生产者发出的消息会被启动的消费者以轮询的方式进行接收和输出<br/>
<img alt="消费者交替" src="https://img-blog.csdnimg.cn/20190402232015744.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/><br/>
2.消息分区<br/>
消费组只是保证了同一消息只被一个消费者实例进行接收和处理，但是不能保证消息总能被同一个实例进行消费。这个时候需要对消息进行分区处理。

```
spring.cloud.stream.bindings.input.group=Service-A
spring.cloud.stream.bindings.input.destination=greetings
spring.cloud.stream.bindings.input.consumer.partitioned=true
spring.cloud.stream.instance-count=2
spring.cloud.stream.instance-index=0

```

`spring.cloud.stream.bindings.input.consumer.partitioned`:通过该参数开启消费者分区功能<br/>
`spring.cloud.stream.instanceCount`:该参数指定了当前消费者的总实例数量<br/>
`spring.cloud.stream.instanceIndex`:该参数设置当前实例的索引号，从0开始，最大值为`spring.cloud.stream.instanceCount`参数-1。

```
spring.cloud.stream.bindings.output.producer.partitionKeyExpression=payload
spring.cloud.stream.bindings.output.producer.partitionCount=2

```

`spring.cloud.stream.bindings.output.producer.partitionKeyExpression`:通过该参数指定了分区键的表达式规则，我们可以根据实际的输出消息规则SpEL来生成合适的分区键<br/>
`spring.cloud.stream.bindings.output.producer.partitionCount`:该参数指定了消息分区的数量<br/>
`spring.cloud.stream.bindings.output.producer.partition-key-expression=1` 表示只有分区ID为1的消费端能接收到信息。<br/>
`spring.cloud.stream.bindings.output.producer.partition-key-expression=0` 表示只有分区ID为0的消费端能接收到信息。

## 消息类型

目前，Spring Cloud Stream中自带支持了以下几种常用的消息类型转换：

# 绑定器详解

## 多绑定器配置

在一个应用程序中使用多个绑定器时，往往其中一个绑定器会是主要使用的，而第二个可能是为了适应一些特殊要求（比如性能等原因）。可以先通过设置默认绑定器来为大部分的通道设置绑定器。比如，使用RabbitMQ设置默认绑定器：

```
spring.cloud.stream.defaultBinder=rabbit

```

在设置默认绑定器之后，再为其他一些少数的消息通道单独设置绑定器，比如：

```
spring.cloud.stream.bindings.input.binder=kafka

```

当需要在一个应用程序中使用同一类型不同环境的绑定器时，可以通过配置实现通道绑定。

```
spring.cloud.stream.bindings.input.binder=rabbit1
spring.cloud.stream.bindings.input.binder=rabbit2
#rabbit1的通道类型为rabbit
spring.cloud.stream.binders.rabbit1.type=rabbit
spring.cloud.stream.binders.rabbit1.environment.spring.rabbitmq.host=192.168.0.101
spring.cloud.stream.binders.rabbit1.environment.spring.rabbitmq.port=5672
#rabbit2的通道类型为rabbit
spring.cloud.stream.binders.rabbit2.type=rabbit
spring.cloud.stream.binders.rabbit2.environment.spring.rabbitmq.host=192.168.0.102
spring.cloud.stream.binders.rabbit2.environment.spring.rabbitmq.port=5672

```

从上面的配置中，可以看出rabbit1和rabbit2的通道类型都为rabbit,只是主机地址不同。<br/>
当采用显示配置方式时会自动禁用默认的绑定器配置，所以当定义了显示配置以后，对于这些绑定器的配置需要通过spring.cloud.stream.binders.属性来进行设置。对于绑定器的配置 主要有下面4个参数：

## RabbitMQ与Kafka绑定器

RabbitMQ与Kafka的绑定器是如何使用消息中间件中不同概念来实现消息的生产与消费的：
