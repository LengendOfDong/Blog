# 原创：SPRING CLOUD微服务实战笔记--微服务构建:Spring Boot

### 文章目录

## 快速入门

1.实现RESTful API

```
@RestController
public class HelloController{
	@RequestMapping("/hello")
	public String hello(){
		return "hello world!";
	}
}

```

2.启动Spring Boot应用

```

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class HelloApplicationTests {

	private MockMvc mvc;

	@Before
	public void setUp() throws Exception{
		mvc = MockMvcBuilders.standaloneSetup(new HelloController()).build();
	}

	@Test
	public void hello() throws Exception{
		mvc.perform(MockMvcRequestBuilders.get("/hello").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo("Hello World!")));
	}

}

```

`@RunWith(SpringRunner.class)`:书中为`@RunWith(SpringJUnit4ClassRunner.class)`,引入Spring对JUnit4的支持<br/>
`@SpringBootTest`:书中为`@SpringApplicationConfiguration`,指定SpringBoot的启动类<br/>
`@WebAppConfiguration`:开启Web应用的配置,用于模拟ServletContext<br/>
`MockMvc`对象:用于模拟调用Controller的接口发起请求,在`@Test`定义的hello测试用例中,perform执行一次请求调用,accept用于执行接收的数据类型,andExpect用于判断接口返回的期望值<br/>
`@Before`:JUnit定义在测试用例@Test内容执行前预加载的内容,这里用来初始化对HelloController的模拟

## 配置详解

### SpringBoot默认配置文件位置

`src/main/resources/application.properties`

### YAML配置格式

```
environments:
	dev:
		url: http://dev.bar.com
		name: developer setup

```

### 自定义参数

```
book.name=SpringCloudApplication

```

```
@Component
public class Book{
	@Value("${book.name}")
	private String name;
}

```

### 参数引用

属性文件中的各个参数也可相互调用

```
book.name=SpringCloud
book.author=haha
book.desc=${book.author} is writing &lt;${book.name}&gt;

```

### 生成随机数

```
${random}的配置方式主要有以下几种
#随机字符串
blog.value=${random.value}
#随机int
blog.number=${random.int}
#随机long
blog.bignumber=${random.log}
#10以内的随机数
blog.test1=${random.int(10)}
#10~20以内的随机数
blog.test2=${random.int[10,20]}

```

### 命令行参数

通过`java -jar xxx.jar --server.port=8888`可以将配置文件中server.port修改为8888

### 多环境配置

`spring.profiles.active`属性设置{profile}值<br/>
例如:`spring.profiles.active=test`就会加载`application-test.properties`

### 加载顺序

> 
<p>1)在命令行传入的参数<br/>
2)SPRING_APPLICATION_JSON中的属性,SPRING_APPLICATION_JSON是以JSON格式<br/>
配置在系统环境变量中的内容<br/>
3)java:comp/env中的JNDI属性<br/>
4)Java的系统属性,可以通过System.getProperties()获得的内容<br/>
5)操作系统的环境变量<br/>
6)通过random.*配置的随机属性<br/>
7)jar包之外,不同环境对应的application-{profile}配置文件<br/>
8)jar包之内,不同环境对应的application-{profile}配置文件<br/>
9)jar包之外的application.properties或YAML<br/>
10)jar包之内的application.properties或YAML<br/>
11)@Configuration修改的类,通过@PropertySource注解定义的属性<br/>
12)应用默认属性,使用SpringApplication.setDefaultProperties定义的内容</p>


`以上数字越小优先级越高`

## 监控与管理

### 初识actuator

引入依赖

```
	&lt;dependency&gt;
			&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
			&lt;artifactId&gt;spring-boot-starter-actuator&lt;/artifactId&gt;
   &lt;/dependency&gt;

```

刷新pom,此处若不刷新会报错<br/>
<img alt="actuator引入失败解决" src="https://img-blog.csdnimg.cn/20190316091018365.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/><br/>
重启应用,控制台输出<br/>
<img alt="控制台输出" src="https://img-blog.csdnimg.cn/20190316091649997.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/><br/>
访问`http://localhost:8080/health`可以实时获取应用的各项监控指标

### 原生端点

1.应用配置类:获取应用程序中加载的应用配置,环境变量,自动化配置报告等与SpringBoot应用密切相关的配置类信息<br/>
2.度量指标类:获取应用程序运行过程中用于监控的度量指标,比如内存信息,线程池信息,HTTP请求统计等<br/>
3.操作控制类:提供了对应用的关闭等操作类功能

#### 应用配置类

若要端点能够生效,需要在`application.properties`中加入`management.security.enabled=false`<br/>
否则会出现如下报错:status=401<br/>
<img alt="未生效端点" src="https://img-blog.csdnimg.cn/20190316125418820.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/><br/>
`/autoconfig`:该端点用来获取应用的自动化配置报告<br/>
`positiveMatches`中返回的是条件匹配成功的自动化配置	<br/>
`negativeMatches`中返回的是条件匹配不成功的自动化配置,可以查看具体没有生效的原因<br/>
`/beans`:该端点用来获取应用上下文中创建的所有bean<br/>
`/configprops`:该端点用于获取应用中配置的属性信息报告<br/>
`/env`:用来获取所有可用的环境属性报告,如环境变量,JVM属性,应用的配置属性,命令行中的参数,可以通过<kbd>@ConfigurationProperties</kbd>注解来引入到应用程序中使用<br/>
`/mappings`:该端点用来返回所有SpringMvc的控制器映射关系报告<br/>
`/info`:该端点用来返回一些应用自定义的信息<br/>
比如在application.properties中通过info前缀设置属性:`info.app.name=spring-boot-hello`

#### 度量指标类

`/metrics`:该端点用来返回当前应用的各类重要度量指标,比如内存信息,线程信息,垃圾回收信息等<br/>
`/health`:该端点用于获取应用的各类健康指标信息.

|检测器|功能
|------
|DiskSpaceHealthIndicator|低磁盘空间检测
|DataSourceHealthIndicator|检测DataSource的连接是否可用
|MongoHealthIndicator|检测Mongo数据库是否可用
|RedisHealthIndicator|检测Redis服务器是否可用

对于没有自动化配置的检测器,需要自己来实现

```
@Component
public class RocketMQHealthIndicator implements HealthIndicator{
    @Override
    public Health health() {
        int errorCode = check();
        if(errorCode != 0){
            return Health.down().withDetail("Error Code",errorCode).build();
        }
        return Health.up().build();
    }

    private int check(){
        //对监控对象的检测操作
        return 1;
    }
}

```

`/dump`:该端点用于暴露程序运行时的线程信息<br/>
`/trace`:该端点用于返回基本的HTTP跟踪信息

#### 操作控制类

`/shutdown`可以通过如下配置来开启,需要加入保护机制如定制化的actuator的端点路径或者整合Spring Security进行安全校验等<br/>
`endpoints.shutdown.enabled=true`
