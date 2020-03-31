# 原创：SPRING CLOUD微服务实战笔记--服务容错保护:Spring Cloud Hystrix(二)

### Spring Cloud Hystrix

# 属性详解

四种不同优先级别的配置(优先级由低到高)

## Command属性

主要有5种不同类型的属性配置:

### execution配置

execution配置控制的是`HystrixCommand.run()`的执行

### fallback配置

这些属性用来控制`HystrixCommand.getFallback()`的执行.这些属性同时适用于线程池的信号量的隔离策略

### circuitBreaker配置

下面这些是断路器的属性配置,用来控制`HystrixCircuitBreaker`的行为

### metrics设置

下面的属性均与`HystrixCommand`和`HystrixObservableCommand`执行中捕获的指标信息有关.

### requestContext配置

下面这些属性涉及`HystrixCommand`使用的`HystrixRequestContext`的配置

### collapser属性

该属性除了在代码中用set和配置文件配置之外,也可使用注解进行配置.可使用`@HystrixCollapser`中的`collapserProperties`属性来设置,比如:

```
@HystrixCollapser(batchMethod = "batch",collapserProperties = {@HystrixProperty(name = "timerDelayInMilliseconds",value = "20")})

```

下面这些属性用来控制命令合并相关的行为

### threadPool属性

该属性除了在代码中用set和属性文件配置之外,还可使用注解进行设置.可使用`@HystrixCommand`中的`threadPoolProperties`属性来设置,比如:

```
@HystrixCommand(fallbackMethod="helloFallback" , commandKey="helloKey",threadPoolProperties={@HystrixProperty(name="coreSize",value="20")}

```

下面这些属性用来控制Hystrix命令所属线程池的配置

# Hystrix仪表盘

构建一个Hystrix Dashboard,需要四步:

```
&lt;dependency&gt;
	&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
	&lt;artifactId&gt;spring-cloud-starter-hystrix-dashboard&lt;/artifactId&gt;
	&lt;version&gt;1.4.6.RELEASE&lt;/version&gt;
&lt;/dependency&gt;

```

```
spring.application.name=hystrix-dashboard
server.port=2001

```

启动应用,看到如下页面:<br/>
<img alt="Hystrix Dashboard" src="https://img-blog.csdnimg.cn/20190325152310174.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/><br/>
首页的两个参数,Delay和Title:

```
&lt;dependency&gt;
	&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
	&lt;artifactId&gt;spring-cloud-starter-hystrix&lt;/artifactId&gt;
	&lt;version&gt;1.4.6.RELEASE&lt;/version&gt;
&lt;/dependency&gt;
&lt;dependency&gt;
	&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
	&lt;artifactId&gt;spring-boot-starter-actuator&lt;/artifactId&gt;
&lt;/dependency&gt;

```

第二步,在服务消费方的主类中使用@EnableCircuitBreaker注解,开启断路器功能<br/>
重启服务消费方,在Hystrix Dashboard的首页输入http://localhost:9000/hystrix.stream,可以看到如下页面<img alt="在这里插入图片描述" src="https://img-blog.csdnimg.cn/20190325170829377.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/>

# Turbine 集群监控

## 构建监控聚合服务

1.引入Turbine来聚合RIBBON-CONSUMER服务的监控信息,并输出给Hystrix Dashboard来进行展示,具体步骤如下:

```
&lt;parent&gt;
		&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
		&lt;artifactId&gt;spring-cloud-starter-parent&lt;/artifactId&gt;
		&lt;version&gt;Brixton.SR5&lt;/version&gt;
		&lt;relativePath/&gt; &lt;!-- lookup parent from repository --&gt;
	&lt;/parent&gt;
	&lt;groupId&gt;com.didispace&lt;/groupId&gt;
	&lt;artifactId&gt;hello&lt;/artifactId&gt;
	&lt;version&gt;0.0.1-SNAPSHOT&lt;/version&gt;
	&lt;name&gt;hello&lt;/name&gt;
	&lt;description&gt;Demo project for Spring Boot&lt;/description&gt;

	&lt;properties&gt;
		&lt;java.version&gt;1.8&lt;/java.version&gt;
	&lt;/properties&gt;

	&lt;dependencies&gt;
		&lt;!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-turbine --&gt;
		&lt;dependency&gt;
			&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
			&lt;artifactId&gt;spring-cloud-starter-turbine&lt;/artifactId&gt;
		&lt;/dependency&gt;

		&lt;dependency&gt;
			&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
			&lt;artifactId&gt;spring-boot-starter-actuator&lt;/artifactId&gt;
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
@EnableEurekaClient
@EnableTurbine
@SpringBootApplication
public class HelloApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloApplication.class, args);
	}
}

```

```
spring.application.name=turbine

server.port=8989
management.port=8990

eureka.client.service-url.defaultZone=http://localhost:1111/eureka/

turbine.app-config=RIBBON-CONSUMER
turbine.cluster-name-expression="default"
turbine.combine-host-port=true

```

turbine.app-config:该参数指定了需要收集监控信息的服务名<br/>
turbine.cluster-name-expression:该参数指定了集群名称为default<br/>
turbine.combine-host-port:该参数设置为true,可以让同一主机上的服务通过主机名和端口号的组合来进行区分<br/>
启动eureka-server/HELLO-SERVER/RIBBON-CONSUMER/Turbine/Hystrix Dashboard,页面如下:<br/>
<img alt="Turbine" src="https://img-blog.csdnimg.cn/2019032518435437.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/>

## 与消息代理结合

实现基于消息代理的Turbine聚合服务<br/>
1)创建一个标准的Spring Boot工程,命名为turbine-amqp<br/>
2)编辑pom.xml,导入依赖

```
&lt;parent&gt;
		&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
		&lt;artifactId&gt;spring-cloud-starter-parent&lt;/artifactId&gt;
		&lt;version&gt;Brixton.SR5&lt;/version&gt;
		&lt;relativePath/&gt; &lt;!-- lookup parent from repository --&gt;
	&lt;/parent&gt;
	&lt;groupId&gt;com.didispace&lt;/groupId&gt;
	&lt;artifactId&gt;hello&lt;/artifactId&gt;
	&lt;version&gt;0.0.1-SNAPSHOT&lt;/version&gt;
	&lt;name&gt;hello&lt;/name&gt;
	&lt;description&gt;Demo project for Spring Boot&lt;/description&gt;

	&lt;properties&gt;
		&lt;java.version&gt;1.8&lt;/java.version&gt;
	&lt;/properties&gt;

	&lt;dependencies&gt;
		&lt;dependency&gt;
			&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
			&lt;artifactId&gt;spring-cloud-starter-turbine-amqp&lt;/artifactId&gt;
		&lt;/dependency&gt;
		&lt;dependency&gt;
			&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
			&lt;artifactId&gt;spring-boot-starter-actuator&lt;/artifactId&gt;
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

3)在应用类中使用@EnableTurbineStream注解来启用Turbine Stream的配置

```
@EnableTurbineStream
@EnableEurekaClient
@SpringBootApplication
public class HelloApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloApplication.class, args);
	}

}

```

4)配置application.properties文件

```
spring.application.name=turbine
server.port=8989
management.port=8990
eureka.client.service-url.defaultZone=http://localhost:1111/eureka/

```

5)服务消费者也要进行更改pom.xml,使其监控信息能够输出到RabbitMQ上.

```
&lt;dependency&gt;
	&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
	&lt;artifactId&gt;spring-cloud-netflix-hystrix-amqp&lt;/artifactId&gt;
	&lt;version&gt;1.4.6.RELEASE&lt;/version&gt;
&lt;/dependency&gt;

```
