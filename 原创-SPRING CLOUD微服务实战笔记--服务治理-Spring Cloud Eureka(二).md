# 原创：SPRING CLOUD微服务实战笔记--服务治理:Spring Cloud Eureka(二)

### 文章目录

# Spring Cloud Eureka

## Eureka详解

### 基础架构

1.服务注册中心:Eureka提供的服务端,提供服务的注册与发现<br/>
2.服务提供者:提供服务的应用,将服务注册到Eureka<br/>
3.服务消费者:从服务注册中心获取到服务列表<br/>
很多时候,客户端既是服务提供者,也是服务消费者<br/>
从之前的例子中也可以看出,pom.xml中依赖都是`spring-cloud-starter-eureka`<br/>
如果在服务提供者这一方也用Ribbon,那么就像服务消费者一样了

### 服务治理机制

#### 服务提供者

Eureka Server将元数据信息存储在一个双层结构Map中,第一层的key为服务名,第二层的key为具体服务的实例名<br/>
<img alt="双层Map" src="https://img-blog.csdnimg.cn/2019031715351519.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/>

##### 服务同步

服务提供者提供发送注册请求会被转发到其他相连的注册中心,从而实现注册中心之间的服务同步.通过服务同步,服务信息可以从服务注册中心的任意一台都能够获取到.<br/>
对于服务提供者,注册一台就相当于注册所有台<br/>
对于服务消费者,请求一台就相当于请求所有台

##### 服务续约

服务提供者通过心跳报文来通知Eureka Server,以免被排除

#### 服务消费者

##### 获取服务

1.获取服务是服务消费者的基础,所以需要确定的参数<br/>
`eureka.client.fetch-registry=true`,且默认为true<br/>
2.服务缓存清单`30`秒更新一次

##### 服务调用

Ribbon默认会采用轮询的方式进行调用,从而实现客户端的负载均衡

##### 服务下线

服务客户端会在下线时发送REST请求给Eureka Server,服务端接收到请求之后,会将该服务状态置为下线(`Down`)

#### 服务注册中心

##### 失效剔除

Eureka Server在启动时会创建定时任务,默认每隔`60`秒将当前清单中超时(默认`90`秒)没有续约的服务剔除

##### 自我保护

自我保护标准:心跳失败比例是否低于85%<br/>
应用场景:当Eureka Server节点在短时间内丢失过多客户端时（可能发生了网络分区故障）<br/>
应用目的:在出现故障以后,Eureka Server将注册信息保护起来,不再删除注册信息,在故障恢复后,Eureka Server会自动退出自我保护模式<br/>
这个自我保护在本地进行开发的时候,其实并不是那么需要,可以通过参数关闭<br/>
`eureka.server.enable-self-preservation=false`,这样就可以及时剔除无效的服务

## 配置详解

Eureka客户端的配置对象存在于所有Eureka服务治理体系下的应用体系中<br/>
Eureka客户端的配置主要分为两个方面:<br/>
1.服务注册相关的配置信息,包括服务注册中心的地址,服务获取的间隔时间等<br/>
2.服务实例相关的配置信息,包括服务实例的名称,IP地址,端口号等

### 服务实例类配置

1.元数据:Eureka客户端在向服务注册中心发送注册请求时,用来描述自身服务信息的对象,其中包含了一些标准化数据,比如服务名称,实例名称,实例IP,实例端口等<br/>
2.实例名配置<br/>
`eureka.instance.instanceId=${spring.application.name}:${random.int}`<br/>
通过上面的配置,利用应用名加上随机数的方式来区分不同的实例,从而实现在同一主机上,不指定端口就能轻松启动多个实例的效果<br/>
3.健康检测<br/>
为了避免客户端进程能够正常运作,但是客户端应用不能够提供服务的情况发生,通过默认的心跳检测方式,不是很合理<br/>
正确的检测如下:

## 跨平台支持

Eureka的通信机制使用了HTTP 的REST接口实现,由于HTTP的平台无关性,使得其下的微服务应用并不限于使用JAVA来开发
