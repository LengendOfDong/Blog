# 原创：SPRING CLOUD微服务实战笔记--客户端负载均衡Spring Cloud Ribbon

### Spring Cloud Ribbon

# 客户端负载均衡

负载均衡是对系统的高可用,网络压力的缓解和处理能力扩容的重要手段之一<br/>
通常说的负载均衡都是指服务端的负载均衡,分为硬件负载均衡和软件负载均衡<br/>
硬件负载均衡主要通过在服务器节点之间安装专门用于负载均衡的设备,如F5等<br/>
软件负载均衡主要通过在服务器上安装一些具有均衡负载功能或模块的软件来完成请求分发工作,比如Nginx等<br/>
客户端使用负载均衡分成两步:<br/>
1.服务提供者只需要启动多个服务实例并注册到一个注册中心或者多个相关联的服务注册中心<br/>
2.服务消费者直接通过调用被@LoadBalanced注解修饰过的RestTemplate来实现面向服务的接口调用

## RestTemplate详解

### GET请求

1.getForEntity函数

```
RestTemplate restTemplate = new RestTemplate();
ResponseEntity&lt;String&gt; responseEntity = restTemplate.getForEntity("http://USER-SERVICE/user?name={1}",String.class,"didi");
String body = responseEntity.getBody();

```

三种重载实现:其中url为请求的地址,responseType为请求相应体body的包装类型,urlVariables为url中的参数绑定,如上面就是拼接的url`http://USER-SERVICE/user?name=didi`

```
getForEntity(String url,Class responseType,Object... urlVariables);
getForEntity(String url,Class responseType,Map urlVariables);
getForEntity(URI url,Class responseType);

```

2.getForObject函数

```
RestTemplate restTemplate = new RestTemplate();
String result = restTemplate.getForObject(uri,String.class);

```

三种重载实现:

```
getForObject(String url,Class responseType,Object... urlVariables);
getForObject(String url,Class responseType,Map urlVariables);
getForObject(URI url,Class responseType);

```

### POST请求

1.postForEntity函数<br/>
其实从get和post的区别就可以想象出postForEntity的用法:<br/>
1)有个类来封装要传送的信息<br/>
2)请求地址url中不会出现请求的参数<br/>
3)封装类被作为参数放在postForEntity方法中

```
RestTemplate restTemplate = new RestTemplate();
User user= new User("didi",30);
ResponseEntity&lt;String&gt; responseEntity = restTemplate.postForEntity("http://USER-SERVICE/user",user,String.class);
String body = responseEntity.getBody();

```

三种重载实现:

```
postForEntity(String url,Object request,Class responseType,Object... urlVariables);
postForEntity(String url,Object request,Class responseType,Map urlVariables);
postForEntity(URI url,Object request,Class responseType);

```

2.postForObject函数<br/>
postForObject简化了postForEntity的处理:

```
RestTemplate restTemplate = new RestTemplate();
User user = new User("didi",20);
String postResult = restTemplate.postForObject("http://USER-SERVICE/user",user,String.class);

```

三种重载实现:

```
postForObject(String url,Object request,Class responseType,Object... urlVariables);
postForObject(String url,Object request,Class responseType,Map urlVariables);
postForObject(URI url,Object request,Class responseType);

```

3.postForLocation函数<br/>
以post请求提交资源,并返回新资源的URI

```
User user = new User("didi",40);
URI responseURI = restTemplate.postForLocation("http://USER-SERVICE/user",user);

```

三种重载实现:

```
postForLocation(String url,Object request,Object... urlVariables);
postForLocation(String url,Object request,Map urlVariables);
postForLocation(URI url,Object request);

```

### PUT请求

```
RestTemplate restTemplate  = new RestTemplate();
Long id = 10001L;
User user = new User("didi",40);
restTemplate.put("http://USER-SERVICE/user/{1}",user,id);

```

put函数也实现了三种不同的重载方法:

```
put(String url,Object request,Object... urlVariables);
put(String url,Object request,Map urlVariables);
put(URI url,Object request);

```

### DELETE请求

```
RestTemplate restTemplate = new RestTemplate();
Long id = 10001L;
restTemplate.delete("http://USER-SERVICE/user/{1}",id);

```

delete函数三种不同的重载方法:

```
delete(String url,Object... urlVariables)
delete(String url,Map urlVariables)
delete(URI url)

```

## 配置详解

在引入Spring Cloud Ribbon的依赖以后,就能够自动化构建下面这些接口的实现:

|接口<th align="center">默认实现</th>|作用
|------
|IClientConfig<td align="center">com.netflix.client.config.DefaultClientConfigImpl</td>|Ribbon的客户端配置
|IRule<td align="center">com.netflix.loadbalancer.ZoneAvoidanceRule</td>|Ribbon的负载均衡策略,能够在多区域环境下选出最佳区域的实例进行访问
|IPing<td align="center">com.netflix.loadbalancer.NoOpPing</td>|Ribbon的实例检查策略,默认所有服务实例都是可用的
|ServerList<td align="center">com.netflix.loadbalancer.ConfigurationBasedServerList</td>|服务实例清单的维护机制
|ServerListFilter<td align="center">org.springframework.cloud.netflix.ribbon.ZonePreferenceServerListFilter</td>|服务实例清单过滤机制,优先过滤出于请求调用方处于同区域的服务实例
|ILoadBalancer<td align="center">com.netflix.loadbalancer.ZoneAwareLoadBalancer</td>|负载均衡器,具备了区域感知的能力

覆盖默认的配置,实现个性化需求

```
@Configuration
public class MyRibbonConfiguration{
	@Bean
	public IPing ribbonPing(IClientConfig config){
		return new PingUrl();
	}
}

```

### 参数配置

对于Ribbon的参数配置通常有两种方式,全局配置以及指定客户端配置

## 重试机制

Eureka和Zookeeper的区别<br/>
[https://www.cnblogs.com/vincent928/p/10084176.html](https://www.cnblogs.com/vincent928/p/10084176.html)<br/>
Cap定理:<br/>
[http://www.ruanyifeng.com/blog/2018/07/cap.html](http://www.ruanyifeng.com/blog/2018/07/cap.html)<br/>
为了增加服务调用的时候的容错,需要加入一些重试机制,在配置文件中加入内容

```
spring.cloud.loadbalancer.retry.enabled=true
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds=1000
&lt;服务名&gt;.ribbon.ConnectTimeOut=250
&lt;服务名&gt;.ribbon.ReadTimeOut=1000
&lt;服务名&gt;.ribbon.OkToRetryOnAllOperations=true
&lt;服务名&gt;.ribbon.MaxAutoRetriesNextServer=2
&lt;服务名&gt;.ribbon.MaxAutoRetries=1

```
