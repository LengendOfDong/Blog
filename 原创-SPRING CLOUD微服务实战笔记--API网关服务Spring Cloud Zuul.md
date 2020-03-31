# 原创：SPRING CLOUD微服务实战笔记--API网关服务Spring Cloud Zuul

### Spring Cloud Zuul

# 快速入门

## 构建网关

API网关服务的构建过程

```
&lt;dependency&gt;
	&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
	&lt;artifactId&gt;spring-cloud-starter-zuul&lt;/artifactId&gt;
	&lt;version&gt;1.4.6.RELEASE&lt;/version&gt;
&lt;/dependency&gt;
&lt;dependency&gt;
	&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
	&lt;artifactId&gt;spring-cloud-starter-eureka&lt;/artifactId&gt;
	&lt;version&gt;1.4.6.RELEASE&lt;/version&gt;
&lt;/dependency&gt;

```

```
@EnableZuulProxy
@SpringBootApplication
public class HelloApplication {
	public static void main(String[] args) {
		SpringApplication.run(HelloApplication.class, args);
	}
}

```

## 请求路由

```
spring.application.name=api-gateway
server.port=5555
zuul.routes.api-a.path=/api-a/**
zuul.routes.api-a.serviceId=hello-service
zuul.routes.api-b.path=/api-b/**
zuul.routes.api-b.serviceId=feign-consumer
eureka.client.service-url.defaultZone=http://localhost:1111/eureka

```

启动服务后,查看eureka-server的信息面板,可以看到API-GATEWAY已经在列表中<br/>
<img alt="eureka-server面板" src="https://img-blog.csdnimg.cn/2019032722381578.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/><br/>
经过配置,`http://localhost:5555/api-a/hello`符合`/api-a/**`的规则,最终`/hello`的请求会被发送到`hello-service`服务的某个实例上去<br/>
`http://localhost:5555/api-b/feign-consumer`符合`/api-b/**`的规则,最终`/feign-consumer`的请求会被发送到`feign-consumer`服务的某个实例上去

## 请求过滤

定义一个类来继承ZuulFilter抽象类并重写下面4个方法来实现自定义的过滤器

```
//zuul过滤该请求,不对其路由
ctx.setSendZuulResponse(false);
//设置返回的错误码
ctx.setResponseStatusCode(401);
//对返回的body内容进行编辑
ctx.setResponseBody("Your request has been lost!");

```

在实现了自定义过滤器之后,并不会直接生效,还需要在主类中创建具体的Bean

```
@EnableZuulProxy
@SpringBootApplication
public class HelloApplication {
	public static void main(String[] args) {
		SpringApplication.run(HelloApplication.class, args);
	}
	@Bean
	public AccessFilter accessFilter(){
		return new AccessFilter();
	}
}

```

总结Spring Cloud Zuul的优点:<br/>
1)作为系统的统一入口,屏蔽了系统内部各个微服务的细节<br/>
2)可以与服务治理框架结合,实现自动 化的服务实例维护以及负载均衡的路由转发<br/>
3)可以实现接口权限校验与微服务业务逻辑的解耦<br/>
4)通过服务网关中的过滤器,在各生命周期中去校验请求的内容,将原本在对外服务层做的校验前移,保证了微服务的无状态性,同时降低了微服务的测试难度,让服务本身更集中关注业务逻辑的处理

# 路由详解

## 服务路由配置

对于面向服务的路由配置,可以用`zuul.routes.user-service=/user-service/**`来配置<br/>
在Eureka的帮助下,API网关服务自身就已经维护了系统中所有serviceId与实例地址的映射关系

## 服务路由的默认规则

Spring Cloud Zuul构建的API网关服务引入Spring Cloud Eureka之后,它为Eureka中的每个服务都自动创建一个默认路由规则<br/>
对于不希望对外开放的服务可以通过`zuul.ignored-services`参数来设置一个服务名匹配表达式来定义不自动创建服务的规则,然后在配置文件中通过`zuul.routes.&lt;serviceId&gt;=&lt;path&gt;`配置方式来创建路由,其他的依然是使用默认规则

## 本地跳转

在Zuul实现的API网关路由功能中,还支持forward形式的服务端跳转配置

```
zuul.routes.api-a.path=/api-a/**
zuul.routes.api-a.url=http://localhost:8001/
zuul.routes.api-b.path=/api-b/**
zuul.routes.api-b.url=forward:/local

```

当接收到/api-b/hello的请求,对于`api-b`的路由规则生效,请求会被转发到网关的/local/hello请求上进行本地处理,所以需要增加一个/local/hello的接口实现才可以

```
@RestController
public class HelloController{
	@RequestMapping("/local/hello")
	public String hello(){
		return "Hello World Local";
	}
	
}

```

## Cookie和头信息

```
#方法一:对指定路由开启自定义敏感头
zuul.routes.&lt;router&gt;.customSensitiveHeaders=true
#方法二:将指定路由的敏感头设置为空
zuul.routes.&lt;router&gt;.sensitiveHeaders=

```

仅对指定的Web应用开启对敏感信息的传递,影响范围小,不至于引起其他服务的信息泄露问题<br/>
重定向问题描述:在登录完成后,通过重定向的方式跳到登录后的页面,指向了具体的服务实例地址<br/>
重定向问题解决方案:网关在进行路由转发之前为请求设置Host头信息,以标识最初的服务端请求地址,具体配置如下:

```
zuul.addHostHeader=true

```

## Hystrix和Ribbon支持

Zuul拥有线程隔离和断路器自我保护功能,以及对服务调用的客户端负载均衡功能<br/>
当使用path和url的映射关系来配置路由的时候,不会采用HystrixCommand来包装,所以这类路由请求没有线程隔离和断路器的保护,并且也不会有负载均衡的能力.<br/>
所以,尽量使用path和serviceId的组合来进行配置,这样不仅保证API网关的健壮和稳定,也能用到Ribbon的客户端负载均衡功能<br/>
在使用Zuul搭建API网关的时候,可以通过Hystrix和Ribbon的参数来调整路由请求的各种超时时间等配置<br/>
在有些情况下,可能需要关闭重试机制,可以使用下面的配置:

```
#全局关闭重试机制
zuul.retryable=false
#指定路由关闭重试机制
zuul.routes.&lt;route&gt;.retryable=false

```

# 过滤器详解

## 过滤器

在Spring Cloud Zuul中实现的过滤器必须包含4个基本特征:过滤类型,执行顺序,执行条件,具体操作,具体含义前面已经讲过

```
String filterType();
int filterOrder();
boolean shouldFilter();
Object run();

```

## 请求生命周期

一个HTTP请求到达API网关之后,如何在各种不同类型的过滤器之间流转的详细过程<br/>
<img alt="请求生命周期图解" src="https://img-blog.csdnimg.cn/20190328132332213.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/><br/>
第一阶段pre:在进行请求路由之前做一些前置加工<br/>
第二阶段routing:将外部请求转发到具体服务实例上去的过程,当服务实例将请求结果都返回之后,routing阶段完成,请求进入第三阶段post<br/>
第三阶段post:此类型的过滤器在处理的时候不仅可以获取到请求信息,还能获取到服务实例的返回信息,所以在post类型的过滤器中,可以对处理结果进行一些加工或者转换等内容<br/>
特殊阶段error:此阶段只有上述三个阶段中发生异常时才会触发,但是最后的流向还是post过滤器,需要通过post过滤器将最终结果返回给请求客户端

## 禁用过滤器

对于过滤器不想使用了,想要禁用,在Zuul中特别提供了一个参数来禁用指定的过滤器,参数配置格式如下:

```
zuul.&lt;SimpleClassName&gt;.&lt;filterType&gt;.disable=true

```

其中`&lt;SimpleClassName&gt;`代表过滤器的类名,比如AccessFilter<br/>
`&lt;filterType&gt;`代表过滤器类型,比如AccessFilter的pre类型<br/>
比如想要禁用AccessFilter过滤器,只要在application.properties配置文件中增加如下配置:<br/>
`zuul.AccessFilter.pre.disable=true`<br/>
这样就可以禁用Spring Cloud Zuul中默认定义的核心过滤器,来实现一套更符合实际需求的处理机制
