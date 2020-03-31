# 原创：SPRING CLOUD微服务实战笔记--声明式服务调用:Spring Cloud Feign

### Spring Cloud Feign

# 快速入门

通过Spring Cloud Feign提供的声明式服务绑定功能来实现对该服务接口的调用

```
&lt;dependency&gt;
	&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
	&lt;artifactId&gt;spring-cloud-starter-feign&lt;/artifactId&gt;
	&lt;version&gt;1.4.6.RELEASE&lt;/version&gt;
&lt;/dependency&gt;

```

```
@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
public class HelloApplication {
	public static void main(String[] args) {
		SpringApplication.run(HelloApplication.class, args);
	}
}

```

```
@FeignClient("hello-service")
public interface HelloService {
    @RequestMapping("/hello")
    String hello();
}

```

```
@RestController
public class ConsumerController {
    @Autowired
    HelloService helloService;
    @RequestMapping(value = "/feign-consumer" , method = RequestMethod.GET)
    public String helloConsumer(){
        return helloService.hello();
    }
}

```

```
spring.application.name=feign-consumer
server.port=9003
eureka.client.service-url.defaultZone=http://localhost:1111/eureka/

```

测试验证<br/>
访问`http://localhost:9003/feign-consumer`,页面如下:<br/>
<img alt="在这里插入图片描述" src="https://img-blog.csdnimg.cn/20190326213615567.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/>

# 参数绑定

第一步:改造服务提供方的服务<br/>
服务提供方修改服务,增加接口定义,分别带有Request参数的请求,带有Header信息的请求,带有RequestBody的请求

```
@RequestMapping(value = "/hello1",method = RequestMethod.GET)
    public String hello(@RequestParam String name){
        return "Hello " + name;
    }

    @RequestMapping(value = "/hello2",method = RequestMethod.GET)
    public User hello(@RequestHeader String name,@RequestHeader Integer age){
        return new User(name,age);
    }

    @RequestMapping(value = "/hello3" , method = RequestMethod.POST)
    public String hello(@RequestBody User user){
        return "Hello " + user.getName() + ", " + user.getAge();
    }

```

User对象定义如下:

```
public class User {
    private String name;
    private Integer age;
    public User(){}
    public User(String name,Integer age){
        this.name = name;
        this.age = age;
    }
//省略getter和setter方法
    @Override
    public String toString(){
        return "name=" + name + ",age=" + age;
    }
}

```

第二步:改造服务消费端的请求绑定

```
@FeignClient("hello-service")
public interface HelloService {
        @RequestMapping("/hello")
        String hello();
        @RequestMapping(value="/hello1",method = RequestMethod.GET)
        String hello(@RequestParam("name") String name);
        @RequestMapping(value = "/hello2",method = RequestMethod.GET)
        User hello(@RequestHeader("name") String name,@RequestHeader("age") Integer age);
        @RequestMapping(value = "/hello3",method = RequestMethod.POST)
        String hello(@RequestBody User user);
}

```

```
@RequestMapping(value = "/feign-consumer2",method = RequestMethod.GET)
    public String helloConsumer2(){
        StringBuilder sb = new StringBuilder();
        sb.append(helloService.hello()).append("\n");
        sb.append(helloService.hello("DIDI")).append("\n");
        sb.append(helloService.hello("DIDI",30)).append("\n");
        sb.append(helloService.hello(new User("DIDI",30))).append("\n");
        return sb.toString();
    }

```

测试验证<br/>
访问`http://localhost:9003/feign-consumer2`,返回如下:

```
Hello World! Hello DIDI name=DIDI,age=30 Hello DIDI, 30

```

# 继承特性

通过Spring Cloud Feign的继承特性来实现REST接口定义的复用<br/>
先新建一个Maven工程:

```
&lt;modelVersion&gt;4.0.0&lt;/modelVersion&gt;
	&lt;groupId&gt;com.didispace&lt;/groupId&gt;
	&lt;artifactId&gt;hello-service-api&lt;/artifactId&gt;
	&lt;packaging&gt;jar&lt;/packaging&gt;
	&lt;version&gt;0.0.1-SNAPSHOT&lt;/version&gt;
	&lt;name&gt;hello-service-api&lt;/name&gt;
	&lt;description&gt;Demo project for Spring Boot&lt;/description&gt;
	&lt;parent&gt;
		&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
		&lt;artifactId&gt;spring-boot-starter-parent&lt;/artifactId&gt;
		&lt;version&gt;1.3.7.RELEASE&lt;/version&gt;
		&lt;relativePath&gt;&lt;/relativePath&gt;
	&lt;/parent&gt;
	&lt;properties&gt;
		&lt;java.version&gt;1.8&lt;/java.version&gt;
	&lt;/properties&gt;
	&lt;dependencies&gt;
		&lt;dependency&gt;
			&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
			&lt;artifactId&gt;spring-boot-starter-web&lt;/artifactId&gt;
		&lt;/dependency&gt;
	&lt;/dependencies&gt;

```

```
@RequestMapping("/refactor")
public interface HelloService {
    @RequestMapping(value = "/hello4",method = RequestMethod.GET)
    public String hello(@RequestParam String name);
    @RequestMapping(value = "/hello5",method = RequestMethod.GET)
    public User hello(@RequestHeader String name, @RequestHeader Integer age);
    @RequestMapping(value = "/hello6" , method = RequestMethod.POST)
    public String hello(@RequestBody User user);
}

```

再修改服务提供方,对其进行重构

```
&lt;dependency&gt;
	&lt;groupId&gt;com.didispace&lt;/groupId&gt;
	&lt;artifactId&gt;hello-service-api&lt;/artifactId&gt;
	&lt;version&gt;0.0.1-SNAPSHOT&lt;/version&gt;
&lt;/dependency&gt;

```

```
@RestController
public class RefactorHelloController implements HelloService {
    @Override
    public String hello(@RequestParam("name") String name) {
        return "Hello " + name;
    }
    @Override
    public User hello(@RequestHeader("name") String name,@RequestHeader("age") Integer age) {
        return new User(name,age);
    }
    @Override
    public String hello(@RequestBody User user) {
        return "Hello " + user.getName() + ", " + user.getAge();
    }
}

```

最后修改服务消费者

```
&lt;dependency&gt;
	&lt;groupId&gt;com.didispace&lt;/groupId&gt;
	&lt;artifactId&gt;hello-service-api&lt;/artifactId&gt;
	&lt;version&gt;0.0.1-SNAPSHOT&lt;/version&gt;
&lt;/dependency&gt;

```

```
@FeignClient(value = "HELLO-SERVICE")
public interface RefactorHelloService extends com.didispace.service.HelloService{
}

```

```
@RequestMapping(value = "/feign-consumer3",method = RequestMethod.GET)
    public String helloConsumer3(){
        StringBuilder sb = new StringBuilder();
        sb.append(refactorHelloService.hello("MIMI")).append("\n");
        sb.append(refactorHelloService.hello("MIMI",30)).append("\n");
        sb.append(refactorHelloService.hello(new com.didispace.User.User("MIMI",20))).append("\n");
        return sb.toString();
    }

```

遇到的问题:<br/>
将项目引入到maven私服库的时候,项目中调用接口不能识别,原来之前为了打包将pom中的编译插件换了,导致执行`mvn install`命令时编译有问题

```
&lt;build&gt;
	&lt;plugins&gt;
		&lt;plugin&gt;
			&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
			&lt;artifactId&gt;spring-boot-maven-plugin&lt;/artifactId&gt;
			&lt;version&gt;2.0.3.RELEASE&lt;/version&gt;
			&lt;executions&gt;
				&lt;execution&gt;
					&lt;goals&gt;
						&lt;goal&gt;repackage&lt;/goal&gt;
					&lt;/goals&gt;
				&lt;/execution&gt;
			&lt;/executions&gt;
		&lt;/plugin&gt;
	&lt;/plugins&gt;
&lt;/build&gt;

```

将上面这段从pom中删除,重新执行`mvn install`后成功

# Ribbon配置

## 全局配置

通过使用ribbon.=方式来设置ribbon的各项默认参数,比如修改默认的客户端调用超时时间:

```
ribbon.ConnectTimeout = 500
ribbon.ReadTimeout=5000

```

## 指定服务配置

有时需要个性化配置,采用.ribbon.key=value的格式进行设置<br/>
使用@FeignClient(value = “HELLO-SERVICE”)来创建Feign客户端的时候,同时也创建了一个名为HELLO-SERVICE的Ribbon客户端

```
HELLO-SERVICE.ribbon.ConnectTimeout=500
HELLO-SERVICE.ribbon.ReadTimeout=2000

```

# Hystrix配置

## 全局配置

Hystrix的全局配置采用默认配置前缀`hystrix.command.default`就可以设置,比如设置全局的超时时间:

```
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds=5000

```

通过`feign.hystrix.enabled=false`来全局关闭Hystrix功能<br/>
通过`feign.command.default.execution.timeout.enabled=false`来关闭熔断功能

## 禁用Hystrix

@Scope(“prototype”)注解为指定的客户端配置Feign.Builder实例

```
@Configuration
public class DisableHystrixConfiguration{
	@Bean
	@Scope("prototype")
	public Feign.Builder feignBuilder(){
		return Feign.builder();
	}
}

```

```
@FeignClient(name="HELLO-SERVICE",configuration =DisableHystrixConfiguration.class)
public interface HelloService{
}

```

# 其他配置

## 请求压缩

SpringCloudFeign支持对请求与响应进行GZIP压缩,以减少通信过程中的性能损耗

```
feign.compression.request.enabled=true
feign.compression.response.enabled=true

```

同时还可以进行更加细化的配置

```
feign.compression.request.enabled=true;
//指定压缩请求数据类型
feign.comresstion.request.mime-types=text/xml,application/xml,application/json
//请求压缩的大小下限,只有查过这个大小的请求才会对其进行压缩
feign.compression.request.min-request-size=2048

```

## 日志配置

可以在application.properties文件中使用`logging.level.&lt;FeignClient&gt;`的参数配置格式来开启指定Feign客户端的DEBUG日志,其中`&lt;FeignClient&gt;`为Feign客户端定义接口的完整路径,比如:

```
logging.level.com.didispace.web.HelloService=DEBUG

```

同时调整全局的日志级别,可以在应用主类中直接加入Logger.Level的Bean创建,具体如下:

```
@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
public class HelloApplication {
	@Bean
	Logger.Level feignLoggerLevel(){
		return Logger.Level.FULL;
	}
	public static void main(String[] args) {
		SpringApplication.run(HelloApplication.class, args);
	}
}

```

对于Feign的Logger级别主要有如下4类,可以根据实际需要进行调整使用
