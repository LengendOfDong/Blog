# 原创：SPRING CLOUD微服务实战笔记--消息总线:Spring Cloud Bus

### Spring Cloud Bus

# 消息代理

消息代理是一种消息验证、传输、路由的架构模式。

# RabbitMQ实现消息总线

消息投递到队列的整个过程如下：<br/>
1.客户端连接到消息队列服务器，打开一个Channel<br/>
2.客户端声明一个Exchange，并设置相关属性<br/>
3.客户端声明一个Queue,并设置相关属性<br/>
4.客户端使用RoutingKey，在Exchange和Queue之间建立好绑定关系<br/>
5.客户端投递消息到Exchange<br/>
6.Exchange接收到消息后，根据消息的Key和已经设置的Binding，进行消息路由，将消息投递到一个或多个Queue中<br/>
Exchange有几种类型：<br/>
1.Direct交换机:完全根据Key进行投递<br/>
2.Topic交换机:对Key进行模式匹配后进行投递，可以使用符号#匹配一个或多个词，符号*匹配正好一个词<br/>
3.Fanout交换机:不需要任何Key,它采取广播的模式，一个消息进来时，投递到与该交换机绑定的所有队列

## 快速入门

在Spring Boot中整合RabbitMQ,pom中AMQP模块可以很好地支持RabbitMQ

```
&lt;dependencies&gt;
	&lt;dependency&gt;
		&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
		&lt;artifactId&gt;spring-boot-starter-amqp&lt;/artifactId&gt;
	&lt;/dependency&gt;
	&lt;dependency&gt;
		&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
		&lt;artifactId&gt;spring-boot-starter-test&lt;/artifactId&gt;
		&lt;scope&gt;test&lt;/scope&gt;
	&lt;/dependency&gt;
&lt;/dependencies&gt;

```

```
spring.application.name=rabbitmq-hello
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=springcloud
spring.rabbitmq.password=123456

```

```
@Component
public class Sender {
    @Autowired
    private AmqpTemplate rabbitTemplate;
    public void send(){
        String context = "hello" + new Date();
        System.out.println("Sender: " + context);
        this.rabbitTemplate.convertAndSend("hello",context);
    }
}

```

```
@Component
@RabbitListener(queues = "hello")
public class Receiver {
    @RabbitHandler
    public void process(String hello){
        System.out.println("Receiver: " + hello);
    }
}

```

```
import org.springframework.amqp.core.Queue;
@Configuration
public class RabbitConfig {
    @Bean
    public Queue helloQueue(){
        return new Queue("hello");
    }
}

```

```
@SpringBootApplication
public class HelloApplication {
	public static void main(String[] args) {
		SpringApplication.run(HelloApplication.class, args);
	}
}

```

```
@RunWith(SpringRunner.class)
@SpringBootTest
public class HelloApplicationTests {
	@Autowired
	private Sender sender;
	@Test
	public void hello() throws Exception{
		sender.send();
	}
}

```

```
org.springframework.amqp.AmqpConnectException: java.net.ConnectException: Connection refused (Connection refused)

```

这里不得不提下这个问题，书中没有提到这个问题，所以自己想办法解决了，首先打开RabbitMQ的管理界面可以看到springcloud处于`No access`状态<br/>
<img alt="No access" src="https://img-blog.csdnimg.cn/20190331153234605.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/><br/>
给springcloud用户添加权限，单击`springcloud`，进入下图<img alt="Set permission" src="https://img-blog.csdnimg.cn/20190331154135508.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/><br/>
单击Set permission，此时springcloud用户获取到权限了<br/>
<img alt="Set permission2" src="https://img-blog.csdnimg.cn/20190331154331915.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/><br/>
<img alt="have permission" src="https://img-blog.csdnimg.cn/20190331154449182.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/>

```
Created new connection: rabbitConnectionFactory#2b608c2a:0/SimpleConnection@5e53c92d [delegate=amqp://springcloud@127.0.0.1:5672/, localPort= 49700]

```

## 整合Spring Cloud Bus

```
&lt;dependency&gt;
	&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
	&lt;artifactId&gt;spring-cloud-starter-bus-amqp&lt;/artifactId&gt;
	&lt;version&gt;1.3.4.RELEASE&lt;/version&gt;
&lt;/dependency&gt;

```

```
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=springcloud
spring.rabbitmq.password=123456

```

## 原理分析

<img alt="原理分析" src="https://img-blog.csdnimg.cn/20190331221613667.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/><br/>
这里主要是通过消息总线的方式，将其中一个实例的更新请求`/bus/refresh`，传递给其他实例，达到同时刷新的目的

## 指定刷新范围

特别情况下，希望可以刷新微服务中某个具体实例的配置<br/>
通过`/bus/refresh?destination=customers:9000`来刷新某个具体的实例，`customers:9000`指的是各个微服务的ApplicationContext ID<br/>
默认情况下，ApplicationContext ID是`spring.application.name:server.port`，详见`org.springframework.boot.context.ContextIdApplicationContextInitializer.getApplicationId(ConfigurableEnvironment)`方法。<br/>
destination参数也可以用来定位特定的微服务。例如：`/bus/refresh?destination=customers:**`，这样就可以触发customers微服务所有实例的配置刷新。

## 架构优化

在之前的架构中，服务是不对等的，如果固定向某个实例发送刷新请求，再触发整个服务集群的刷新，这种方式会造成运维工作的复杂性。若此服务实例发生迁移，则会造成配置不能刷新的潜在后果。<br/>
架构调整可以做如下改动：<br/>
1.在Config Server中也引入Spring Cloud Bus,将配置服务端也加入到消息总线中来<br/>
2./bus/refresh请求不再发送到具体服务实例上，而是发送给Config Server，并通过<br/>
destination参数来指定需要更新配置的服务实例<br/>
对于Git的触发等配置只需要针对Config Server即可，从而简化了集群上的一些维护工作
