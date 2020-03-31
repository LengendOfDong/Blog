# 原创：SPRING CLOUD微服务实战笔记--分布式配置中心:Spring Cloud Config

### Spring Cloud Config

# 快速入门

## 构建配置中心

通过SpringCloudConfig构建一个分布式配置中心,分成三步:

```
&lt;dependencies&gt;
	&lt;dependency&gt;
		&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
		&lt;artifactId&gt;spring-cloud-config-server&lt;/artifactId&gt;
		&lt;version&gt;1.4.5.RELEASE&lt;/version&gt;
	&lt;/dependency&gt;
&lt;/dependencies&gt;

```

```
@EnableConfigServer
@SpringBootApplication
public class HelloApplication {
	public static void main(String[] args) {
		SpringApplication.run(HelloApplication.class, args);
	}
}

```

```
spring.application.name=config-server
server.port=7001
spring.cloud.config.server.git.uri=http://git.oschina.net/dongarea/SpringCloud-Learning/
spring.cloud.config.server.git.search-paths=/spring_cloud_in_action
spring.cloud.config.server.git.username=username
spring.cloud.config.server.git.password=password

```

spring.cloud.config.server.git.uri:配置Git仓库位置<br/>
spring.cloud.config.server.git.searchPaths:配置仓库路径下的相对搜索位置,可以配置多个<br/>
spring.cloud.config.server.git.username:访问Git仓库的用户名<br/>
spring.cloud.config.server.git.password:访问Git仓库的密码

## 配置规则详解

在码云上建立自己的git仓库`http://git.oschina.net/dongarea/SpringCloud-Learning/`,新建四个配置文件

```
dongarea.properties
dongarea-dev.properties
dongarea-test.properties
dongarea-prod.properties

```

并为每个配置文件设置不同的值,如下:

```
from=git-default-1.0
from=git-dev-1.0
from=git-test-1.0
from=git-prod-1.0

```

同时创建一个config-label-test分支,并将各配置文件中的值用2.0作为后缀<br/>
启动应用,访问配置信息的URL与配置文件的映射关系如下所示:

```
{
    "name":"dongarea",
    "profiles":[
        "prod"
    ],
    "label":"config-label-test",
    "version":"4b2a044ee54afe2e849aa3b1d747a3d60aa959eb",
    "state":null,
    "propertySources":[
        {
            "name":"http://git.oschina.net/dongarea/SpringCloud-Learning/spring_cloud_in_action/dongarea-prod.properties",
            "source":{
                "from":"git-prod-2.0"
            }
        },
        {
            "name":"http://git.oschina.net/dongarea/SpringCloud-Learning/spring_cloud_in_action/dongarea.properties",
            "source":{
                "from":"git-default-2.0"
            }
        }
    ]
}

```

## 客户端配置映射

```
&lt;dependencies&gt;
	&lt;dependency&gt;
		&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
		&lt;artifactId&gt;spring-boot-starter-web&lt;/artifactId&gt;
	&lt;/dependency&gt;

	&lt;dependency&gt;
		&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
		&lt;artifactId&gt;spring-cloud-starter-config&lt;/artifactId&gt;
		&lt;version&gt;1.4.5.RELEASE&lt;/version&gt;
	&lt;/dependency&gt;
&lt;/dependencies&gt;

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
spring.application.name=dongarea
spring.cloud.config.profile=dev
spring.cloud.config.label=master
spring.cloud.config.uri=http://localhost:7001/
server.port=7002

```

`spring.application.name`:对应配置文件规则中的{application}部分<br/>
`spring.cloud.config.profile`:对应配置文件规则中的{profile}部分<br/>
`spring.cloud.config.label`:对应配置文件规则中的{label}部分<br/>
`spring.cloud.config.uri`:配置中心config-server的地址<br/>
这些属性必须配置在bootstrap.properties中,因为jar包之外的配置会优先

```
@RefreshScope
@RestController
public class TestController {
    @Value("${from}")
    private String from;
    @RequestMapping("/from")
    public String from(){
        return this.from;
    }
}

```

启动config-server应用,再启动config-client应用,访问`http://localhost:7002/from`,可以获取配置内容并输出对应环境的from内容

# 服务端详解

## Git配置仓库

连接Git在线开发:

```
spring.cloud.config.server.git.uri=http://git.oschina.net/dongarea/SpringCloud-Learning/
spring.cloud.config.server.git.search-paths=/spring_cloud_in_action
spring.cloud.config.server.git.username=username
spring.cloud.config.server.git.password=password

```

本地离线开发(仅供测试使用):

```
spring.cloud.config.server.git.uri=file://${user.name}/config-repo

```

## SVN配置仓库

```
&lt;dependency&gt;
	&lt;groupId&gt;org.tmatesoft.svnkit&lt;/groupId&gt;
	&lt;artifactId&gt;svnkit&lt;/artifactId&gt;
	&lt;version&gt;1.8.10&lt;/version&gt;
&lt;/dependency&gt;

```

```
spring.cloud.config.server.svn.uri=svn://localhost:443/dongarea/SpringCloud-Learning/
spring.cloud.config.server.svn.search-paths=/spring_cloud_in_action
spring.cloud.config.server.svn.username=username
spring.cloud.config.server.svn.password=password

```

## 本地文件系统

使用本地文件系统的存储方式来保存配置信息,通过`spring.profiles.active=native`来实现<br/>
指定具体的配置文件位置:`spring.cloud.config.server.native.searchLocations`<br/>
为了更好的内容管理和版本控制,建议还是使用Git仓库的方式

## 安全保护

```
&lt;dependency&gt;
	&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
	&lt;artifactId&gt;spring-boot-starter-security&lt;/artifactId&gt;
&lt;/dependency&gt;

```

并修改配置文件:

```
security.user.name=user
security.user.password=123456

```

如果在客户端不配置相应的安全信息,则会报下面的错误:<br/>
<img alt="Exception" src="https://img-blog.csdnimg.cn/20190330194640739.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/><br/>
在客户端也配置上安全信息,则通过校验

```
spring.cloud.config.username=user
spring.cloud.config.password=123456

```

这种安全信息就像是暗号一样,需要双方都有才可以

## 高可用配置

# 客户端详解

## URI指定配置中心

默认Spring Cloud Config会尝试连接`http://localhost:8888`<br/>
在`bootstrap.properties`中配置服务端配置中心地址,才会覆盖默认配置

## 服务化配置中心

服务端配置

```
&lt;dependencies&gt;
	&lt;dependency&gt;
		&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
		&lt;artifactId&gt;spring-cloud-config-server&lt;/artifactId&gt;
		&lt;version&gt;1.4.5.RELEASE&lt;/version&gt;
	&lt;/dependency&gt;
	&lt;dependency&gt;
		&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
		&lt;artifactId&gt;spring-boot-starter-security&lt;/artifactId&gt;
	&lt;/dependency&gt;
	&lt;dependency&gt;
		&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
		&lt;artifactId&gt;spring-cloud-starter-eureka&lt;/artifactId&gt;
		&lt;version&gt;1.4.6.RELEASE&lt;/version&gt;
	&lt;/dependency&gt;
&lt;/dependencies&gt;

```

```
spring.application.name=config-server
server.port=7001
#Git配置管理
spring.cloud.config.server.git.uri=http://git.oschina.net/dongarea/SpringCloud-Learning/
spring.cloud.config.server.git.search-paths=/spring_cloud_in_action
spring.cloud.config.server.git.username=dongarea
spring.cloud.config.server.git.password=j7k8l9;0

security.user.name=user
security.user.password=123456
#配置服务注册中心
eureka.client.service-url.defaultZone=http://localhost:1111/eureka

```

```
@EnableEurekaClient
@EnableConfigServer
@SpringBootApplication
public class HelloApplication {
	public static void main(String[] args) {
		SpringApplication.run(HelloApplication.class, args);
	}
}

```

```
&lt;dependencies&gt;
	&lt;dependency&gt;
		&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
		&lt;artifactId&gt;spring-boot-starter-web&lt;/artifactId&gt;
	&lt;/dependency&gt;

	&lt;dependency&gt;
		&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
		&lt;artifactId&gt;spring-cloud-starter-config&lt;/artifactId&gt;
		&lt;version&gt;1.4.5.RELEASE&lt;/version&gt;
	&lt;/dependency&gt;
	&lt;dependency&gt;
		&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
		&lt;artifactId&gt;spring-cloud-starter-eureka&lt;/artifactId&gt;
		&lt;version&gt;1.4.6.RELEASE&lt;/version&gt;
	&lt;/dependency&gt;
&lt;/dependencies&gt;

```

```
spring.application.name=dongarea
spring.cloud.config.profile=dev
#指定服务注册中心,用于服务注册与发现
eureka.client.service-url.defaultZone=http://localhost:1111/eureka;
server.port=7002
#开启通过服务来访问Config Server的功能
spring.cloud.config.discovery.enabled=true
#指定Config Server注册的服务名
spring.cloud.config.discovery.service-id=config-server
spring.cloud.config.username=user
spring.cloud.config.password=123456

```

```
@EnableEurekaClient
@SpringBootApplication
public class HelloApplication {
	public static void main(String[] args) {
		SpringApplication.run(HelloApplication.class, args);
	}
}

```

## 失败快速响应与重试

在不启动config-server的情况下,启动客户端config-client时,会报错:

```
org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'scopedTarget.testController': Injection of autowired dependencies failed;

```

主要是在报错之前,已经加载了很多东西<br/>
在bootstrap.properties中加上`spring.cloud.config.failFast=true`,直接验证config-server配置是否有误,加载信息少很多,达到了快速返回失败的效果

```
java.lang.IllegalStateException: No instances found of configserver (config-server)

```

在网络波动等其他间隙性原因导致的问题时,可以开启重试功能

```
&lt;dependency&gt;
	&lt;groupId&gt;org.springframework.retry&lt;/groupId&gt;
	&lt;artifactId&gt;spring-retry&lt;/artifactId&gt;
&lt;/dependency&gt;
&lt;dependency&gt;
	&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
	&lt;artifactId&gt;spring-boot-starter-aop&lt;/artifactId&gt;
&lt;/dependency&gt;

```

## 获取远程配置

通过URL和客户端配置的访问对应可以总结如下:
