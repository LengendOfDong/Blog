# 原创：SPRING CLOUD微服务实战笔记--分布式服务跟踪:Spring Cloud Sleuth

### Spring Cloud Sleuth

# 快速入门

## 准备工作

```
&lt;dependencies&gt;
	&lt;dependency&gt;
		&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
		&lt;artifactId&gt;spring-cloud-starter-eureka&lt;/artifactId&gt;
		&lt;version&gt;1.4.6.RELEASE&lt;/version&gt;
	&lt;/dependency&gt;
	&lt;dependency&gt;
		&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
		&lt;artifactId&gt;spring-boot-starter-web&lt;/artifactId&gt;
	&lt;/dependency&gt;
	&lt;dependency&gt;
		&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
		&lt;artifactId&gt;spring-cloud-starter-ribbon&lt;/artifactId&gt;
		&lt;version&gt;1.4.6.RELEASE&lt;/version&gt;
	&lt;/dependency&gt;
	&lt;dependency&gt;
		&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
		&lt;artifactId&gt;spring-boot-starter-test&lt;/artifactId&gt;
		&lt;scope&gt;test&lt;/scope&gt;
	&lt;/dependency&gt;
&lt;/dependencies&gt;

```

2)创建主类，实现/trace-1接口，并使用RestTemplate调用trace-2应用的接口

```
@RestController
@EnableEurekaClient
@SpringBootApplication
public class HelloApplication {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Bean
	@LoadBalanced
	RestTemplate restTemplate(){
		return new RestTemplate();
	}
	@RequestMapping(value = "/trace-1",method = RequestMethod.GET)
	public String trace(){
		logger.info("===call trace-1====");
		return restTemplate().getForEntity("http://trace-2/trace-2",String.class).getBody();
	}
	public static void main(String[] args) {
		SpringApplication.run(HelloApplication.class, args);
	}
}

```

3)在`application.properties`中，将`eureka.client.serviceUrl.defaultZone`参数指向`eureka-server`地址

```
spring.application.name=trace-1
server.port=9101
eureka.client.service-url.defaultZone=http://localhost:1111/eureka

```

```
@RestController
@EnableEurekaClient
@SpringBootApplication
public class HelloApplication {
   private final Logger logger = LoggerFactory.getLogger(getClass());
   @RequestMapping(value = "/trace-2",method = RequestMethod.GET)
   public String trace(){
   	logger.info("===&lt;call trace-2&gt;====");
   	return "Trace";
   }
   public static void main(String[] args) {
   	SpringApplication.run(HelloApplication.class, args);
   }
}

```

3)在application.properties中，将eureka.client.serviceUrl.defaultZone参数指向eureka-server地址，并设置不同的应用名与端口

```
spring.application.name=trace-2
server.port=9102
eureka.client.service-url.defaultZone=http://localhost:1111/eureka

```

## 实现跟踪

在trace-1和trace-2的pom.xml依赖管理中增加`spring-cloud-starter-sleuth`依赖即可

```
&lt;dependency&gt;
	&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
	&lt;artifactId&gt;spring-cloud-starter-sleuth&lt;/artifactId&gt;
	&lt;version&gt;1.3.5.RELEASE&lt;/version&gt;
&lt;/dependency&gt;

```

重启trace-1和trace-2后，控制台打印发生变化，

```
INFO [trace-1,ddcf47da76da68c7,ddcf47da76da68c7,false] 70283 --- [nio-9101-exec-1] ication$$EnhancerBySpringCGLIB$$84347891 : ===call trace-1====
INFO [trace-2,ddcf47da76da68c7,248958640a14c496,false] 70280 --- [nio-9102-exec-1] ication$$EnhancerBySpringCGLIB$$84347891 : ===&lt;call trace-2&gt;====

```

可以看到增加了`[trace-2,ddcf47da76da68c7,ddcf47da76da68c7,false]`类似的日志信息，含义如下：

# 跟踪原理

TraceID和SpanID是Spring Cloud Sleuth实现分布式服务跟踪的核心

```
@RequestMapping(value = "/trace-1",method = RequestMethod.GET)
	public String trace(HttpServletRequest request){
		logger.info("====&lt;call trace-1&gt;=====,TraceId={},SpanId={}，ParentSpanId={},Sampled={},SpanName={}"
				,request.getHeader("X-B3-TraceId"),request.getHeader("X-B3-SpanId")
				,request.getHeader("X-B3-ParentSpanId"),request.getHeader("X-B3-Sampled"),request.getHeader("X-Span-Name"));
		return restTemplate().getForEntity("http://trace-2/trace-2",String.class).getBody();
	}

```

```
@RequestMapping(value = "/trace-2",method = RequestMethod.GET)
	public String trace(HttpServletRequest request){
		logger.info("====&lt;call trace-2&gt;=====,TraceId={},SpanId={}，ParentSpanId={},Sampled={},SpanName={}"
				,request.getHeader("X-B3-TraceId"),request.getHeader("X-B3-SpanId")
				,request.getHeader("X-B3-ParentSpanId"),request.getHeader("X-B3-Sampled"),request.getHeader("X-Span-Name"));
		return "Trace";
	}

```

2.在trace-1和trace-2的`application.properties`中加入配置,可以更加直观的观察跟踪信息

```
logging.level.org.springframework.web.servlet.DispatcherServlet=debug

```

重启trace-1和trace-2的应用，可以看到<br/>
trace-1的控制台<br/>
<img alt="trace-1" src="https://img-blog.csdnimg.cn/201904031455418.png"/><br/>
trace-2的控制台<br/>
<img alt="trace-2" src="https://img-blog.csdnimg.cn/20190403145619545.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/><br/>
trace-2中的`ParentSpanId`就是trace-1中的`SpanId`，trace-2中的`TraceId`与trace-1中的`TraceId`相同

# 与Logstash整合

由于日志文件都离散地存储在各个服务实例的文件系统之上，引入基于日志的分析系统是一个不错的选择，比如ELK平台，它可以轻松地收集和存储跟踪日志，同时还可以根据TraceId搜索出对应请求链路相关的明细日志<br/>
ELK平台主要由ElasticSearch、Logstash和Kibana三个工具组成

```
&lt;dependency&gt;
	&lt;groupId&gt;net.logstash.logback&lt;/groupId&gt;
	&lt;artifactId&gt;logstash-logback-encoder&lt;/artifactId&gt;
	&lt;version&gt;4.6&lt;/version&gt;
&lt;/dependency&gt;

```

# 与ZipKin整合

<img alt="Zipkin基础架构" src="https://img-blog.csdnimg.cn/20190403163504862.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/><br/>
Collector:收集器组件，它主要处理从外部系统发过来的跟踪信息，将这些信息转换为Zipkin内部处理的Span格式，以支持后续的存储、分析、展示等功能<br/>
Storage:存储组件，它主要处理收集器接收到的跟踪信息，默认会将这些信息存储在内存中。我们也可以修改此存储策略，通过使用其他存储组件将跟踪信息存储到数据库中。<br/>
RESTful API:API组件，它主要用来提供外部访问接口。比如给客户端展示跟踪信息，或是外接系统访问以实现监控等。<br/>
Web UI:UI组件，基于API组件实现的上层应用。通过UI组件，用户可以方便而又直观地查询和分析跟踪信息。

## HTTP收集

第一步：搭建Zipkin Server

```
&lt;dependency&gt;
	&lt;groupId&gt;io.zipkin.java&lt;/groupId&gt;
	&lt;artifactId&gt;zipkin-server&lt;/artifactId&gt;
	&lt;version&gt;2.0.1&lt;/version&gt;
&lt;/dependency&gt;
&lt;!-- https://mvnrepository.com/artifact/io.zipkin.java/zipkin-autoconfigure-ui --&gt;
&lt;dependency&gt;
	&lt;groupId&gt;io.zipkin.java&lt;/groupId&gt;
	&lt;artifactId&gt;zipkin-autoconfigure-ui&lt;/artifactId&gt;
	&lt;version&gt;2.0.1&lt;/version&gt;
&lt;/dependency&gt;

```

```
@EnableZipkinServer
@SpringBootApplication
public class HelloApplication {
	public static void main(String[] args) {
		SpringApplication.run(HelloApplication.class, args);
	}
}

```

```
spring.application.name=zipkin-server
server.port=9411

```

创建完成后，启动工程，并访问`http://localhost:9411/`,可以看到如下Zipkin管理页面:<br/>
<img alt="Zipkin" src="https://img-blog.csdnimg.cn/20190403165516804.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/><br/>
第二步：为应用引入和配置Zipkin服务<br/>
在完成了Zipkin Server的搭建之后，还需要对应用做一些配置，以实现将跟踪信息输出到Zipkin Server。

```
&lt;dependency&gt;
	&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
	&lt;artifactId&gt;spring-cloud-sleuth-zipkin&lt;/artifactId&gt;
	&lt;version&gt;1.3.5.RELEASE&lt;/version&gt;
&lt;/dependency&gt;

```

```
spring.zipkin.base-url=http://localhost:9411

```

重启eureka-server、trace-1、trace-2，发送请求`http://localhost:9101/trace-1`,可以查询出在日志中出现的跟踪信息<br/>
<img alt="Find Traces" src="https://img-blog.csdnimg.cn/20190403175723728.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/><br/>
单击每一条记录，还能看到具体的跟踪信息<br/>
<img alt="跟踪信息" src="https://img-blog.csdnimg.cn/20190403175847209.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/>

## 消息中间件收集

第一步：修改客户端trace-1 和trace-2

```
&lt;dependency&gt;
	&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
	&lt;artifactId&gt;spring-cloud-starter-sleuth&lt;/artifactId&gt;
	&lt;version&gt;1.3.5.RELEASE&lt;/version&gt;
&lt;/dependency&gt;
&lt;!--注意下面的这个依赖必须注释掉，否则会报错，与上面的依赖冲突重复了--&gt;
&lt;!--&lt;dependency&gt;
	&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
	&lt;artifactId&gt;spring-cloud-sleuth-zipkin&lt;/artifactId&gt;
	&lt;version&gt;1.3.5.RELEASE&lt;/version&gt;
&lt;/dependency&gt; --&gt;
&lt;dependency&gt;
	&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
	&lt;artifactId&gt;spring-cloud-sleuth-stream&lt;/artifactId&gt;
	&lt;version&gt;1.3.5.RELEASE&lt;/version&gt;
&lt;/dependency&gt;
&lt;dependency&gt;
	&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
	&lt;artifactId&gt;spring-cloud-starter-stream-rabbit&lt;/artifactId&gt;
	&lt;version&gt;1.3.4.RELEASE&lt;/version&gt;
&lt;/dependency&gt;

```

```
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=springcloud
spring.rabbitmq.password=123456

```

第二步：修改zipkin-server服务端

```
&lt;dependencies&gt;
	&lt;dependency&gt;
		&lt;groupId&gt;io.zipkin.java&lt;/groupId&gt;
		&lt;artifactId&gt;zipkin-server&lt;/artifactId&gt;
		&lt;version&gt;2.0.1&lt;/version&gt;
	&lt;/dependency&gt;
	&lt;dependency&gt;
		&lt;groupId&gt;io.zipkin.java&lt;/groupId&gt;
		&lt;artifactId&gt;zipkin-autoconfigure-ui&lt;/artifactId&gt;
		&lt;version&gt;2.0.1&lt;/version&gt;
	&lt;/dependency&gt;
	&lt;dependency&gt;
		&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
		&lt;artifactId&gt;spring-cloud-sleuth-zipkin-stream&lt;/artifactId&gt;
		&lt;version&gt;1.3.5.RELEASE&lt;/version&gt;
	&lt;/dependency&gt;
	&lt;dependency&gt;
		&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
		&lt;artifactId&gt;spring-cloud-starter-stream-rabbit&lt;/artifactId&gt;
		&lt;version&gt;1.3.4.RELEASE&lt;/version&gt;
	&lt;/dependency&gt;
&lt;/dependencies&gt;

```

## 数据存储

默认情况下，Zipkin Server会将跟踪信息存储在内存中，一方面每次重启Zipkin Server都会使之前收集的跟踪信息丢失，另一方面当有大量跟踪信息时内存存储会成为瓶颈，所以通常情况下都会将跟踪信息对接到外部存储组件中去，比如使用MySQL存储
