# 原创：SPRING CLOUD微服务实战笔记--服务容错保护:Spring Cloud Hystrix(一)

### Spring Cloud Hystrix

> 
<p>微服务架构中,系统拆分成了多个服务单元,各单元的应用间通过服务注册与订阅的方式互相依赖.由于每个单元都在不同的进程中运行,依赖通过进程调用的方式执行,这样就有可能因为网络原因或是依赖服务自身问题出现调用故障或延迟,而这些问题会直接导致调用方的对外服务也出现延迟,若此时调用方的请求不断增加,最后就会因等待出现故障的依赖方响应形成任务积压,最终导致自身服务的瘫痪.<br/>
一句话就是,服务提供方出现问题,服务调用方就有可能瘫痪</p>


# 快速入门

```
&lt;dependency&gt;
	&lt;groupId&gt;org.springframework.cloud&lt;/groupId&gt;
	&lt;artifactId&gt;spring-cloud-starter-hystrix&lt;/artifactId&gt;
	&lt;version&gt;1.4.6.RELEASE&lt;/version&gt;
&lt;/dependency&gt;

```

```
@Service
public class HelloService {
    private final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(String.valueOf(HelloService.class));

    @Autowired
    RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "helloFallback",commandKey = "helloKey")
    public String helloService(){
        long start = System.currentTimeMillis();
        String result = restTemplate.getForEntity("http://HELLO-SERVICE/hello",String.class).getBody();
        long end = System.currentTimeMillis();
        logger.info("SpendTime = " + (end - start));
        return result;
    }

    public String helloFallback(){
        return "error";
    }
}

```

两种情况,第一种是断开具体的服务实例,第二种是服务阻塞<br/>
第一种是直接断开服务,第二种是在服务提供方处直接sleep一段时间

# 原理分析

Hystrix的环节<br/>
<img alt="在这里插入图片描述" src="https://img-blog.csdnimg.cn/20190323171240855.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/><br/>
1.创建HystrixCommand或HystrixObservableCommand对象<br/>
命令模式:将来自客户端的请求封装成一个对象,从而使用不同的请求对客户端进行参数化<br/>
2.命令执行:<br/>
`HystrixCommand`实现了两个方法:

## 断路器原理

断路器`HystrixCircuitBreaker`的三个方法:

## 依赖隔离

Hystrix使用"**舱壁模式**"实现线程池的隔离,会为每一个依赖服务创建一个独立的线程池<br/>
对依赖服务的线程池的隔离,可以有如下优势:

# 使用详解

## 创建请求命令

1.通过继承的方式,继承`HystrixCommand`

```
User u = new UserCommand(restTemplate,1L).execute();

```

```
Future&lt;User&gt; futureUser = new UserCommand(restTemplate,1L).queue();

```

异步执行可以通过futureUser的get方法获取结果<br/>
2.通过注解`@HystrixCommand`,优雅实现Hystrix命令<br/>
通过observableExecutionMode参数来控制是observe()还是toObservable()执行方式<br/>
`@HystrixCommand(observableExecutionMode = ObservableExecution-Mode.EAGER)`:<br/>
EAGER表示使用observe()执行方式<br/>
`@HystrixCommand(observableExecutionMode = ObservableExecution-Mode.LAZY)`:表示使用toObservable()执行方式

### 定义服务降级

1.重载HystrixCommand中的getFallback()方法来实现服务降级<br/>
2.通过注解实现服务降级,使用@HystrixCommand中的fallbackMethod参数来指定具体的服务降级实现方法<br/>
不用实现服务降级的场景:<br/>
1)执行写操作的命令<br/>
2)执行批处理或离线计算的命令

### 异常处理

1.异常传播<br/>
通过设置`@HystrixCommand`注解的`ignoreException`参数

```
@HystrixCommand(ignoreExceptions = {BadRequestException.class})
public User getUserById(Long id){
	return restTemplate.getForObject("http://USER-SERVICE/users/{1}",User.class,id);
}

```

当getUserById方法抛出类型为`BadRequestException`的异常时,Hystrix会将异常包装在`HystrixBadRequestException`中抛出,不会触发后续的fallback逻辑<br/>
2.异常获取<br/>
1)传统方式继承,getFallback()方法通过`Throwable getExecutionException()`方法获取具体的异常<br/>
2)注解配置方式实现异常获取,在fallback实现方法的参数增加`Throwable e`

```
@HystrixCommand(fallbackMethod="fallback1")
User getUserById(String id){
	throw new RuntimeException("getUserById command failed");
}
User fallback1(String id,Throwable e){
	assert "getUserById command failed".equals(e.getMessage());
}

```

### 命令名称\分组以及线程池划分

在继承HystrixCommand的类的构造器中使用`Setter`静态类来设置

```
public UserCommand(){
super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("GroupName"))
	.andCommandKey(HystrixCommandKey.Factory.asKey("CommandName"));

```

默认情况下,Hystrix会让相同组名的命令使用同一个线程池,所以GroupKey是每个Setter必需的参数,但是CommandKey不是必需的<br/>
Hystrix还提供了`HystrixThreadPoolKey`来对线程池进行设置,实现更加细粒度的线程池划分

```
public UserCommand(){
super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("CommandGroupKey"))
	.andCommandKey(HystrixCommandKey.Factory.asKey("CommandKey"))
	.andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("ThreadPoolKey")));

```

多个不同的命令可能从业务逻辑上来看属于同一个组,但往往从实现本身上需要跟其他命令进行隔离,所以尽量通过`HystrixThreadPoolKey`的方式来指定线程池的划分

```
@HystrixCommand(commandKey="getUserById",groupKey="UserGroup",threadPoolKey="getUserByIdThread")
public User getUserById(Long id){
	return restTemplate.getForObject("http://USER-SERVICE/users/{1}",User.class,id);
}

```

### 请求缓存

分布式环境下,依赖服务会引起一部分性能损失<br/>
高并发环境下,http相比于其他高性能的通信协议在速度上处于劣势,容易成为系统瓶颈<br/>
1.开启请求缓存功能

```
public class UserCommand extends HystrixCommand&lt;User&gt;{
    private RestTemplate restTemplate;
    private Long id;
    public UserCommand(RestTemplate restTemplate,Long id){       super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("UserGroup")));
    }
   @Override
    protected User run() throws Exception {
        return restTemplate.getForObject("http://USER-SERVICE/users/{1}",User.class,id);
    }
    @Override
    protected String getCacheKey(){
        return String.valueOf(id);
    }
}

```

开启请求缓存具备几个好处:

```
protected String getCacheKey(){
	//根据id置入缓存
	return String.valueOf(id);
}
public static void flushCache(Long id){
	//刷新缓存,根据id进行清理
	HystrixRequestCache.getInstance(GETTER_KEY,HystrixConcurrencyStrategyDefault			   .getInstance()).clear(String.valueOf(id));
}

```

写操作:

```
protected User run(){
	//写操作
	User r = restTemplate.postForObject("http://USER-SERVICE/users",user,User.class);
	//刷新缓存,清理缓存中失效的User
	UserGetCommand.flushCache(user.getId());
	return r;
}

```

可以看到,id作为很重要的索引存在,读写操作都是通过id来进行处理的<br/>
3.工作原理<br/>
主要有两个步骤:尝试获取请求缓存以及将请求结果加入缓存

4.使用注解实现请求缓存

|注解<th align="center">描述</th>|属性
|------
|`@CacheResult`<td align="center">该注解用来标记请求命令返回的结果应该被缓存,必须与`@HystrixCommand`注解结合使用</td>|`cacheKeyMethod`
|`@CacheRemove`<td align="center">该注解用来让请求命令的缓存失效,失效的缓存根据定义的Key决定</td>|`commandKey,cacheKeyMethod`
|`@CacheKey`<td align="center">该注解用来在请求命令的参数上标记,使其作为缓存的Key值,如果没有标注则会使用所有参数.如果同时还使用了`@CacheResult`和`@CacheRemove`注解的`cacheKeyMethod`方法指定缓存key的生成,那么该注解将不会起作用</td>|`value`

注解方式的几个用法:<br/>
1)设置请求缓存:<br/>
加上`@CacheResult`之后,Hystrix会将该结果置入请求缓存中,而key值使用所有的参数,这里就是Long id

```
@CacheResult
@HystrixCommand
public User getUserById(Long id){
   return restTemplate.getForObject("http://USER-SERVICE/users/{1}",User.class,id);
}

```

2)定义缓存Key:<br/>
第一种方式: 配置方式如同`@HystrixCommand`服务降级`fallbackMethod`的使用

```
@CacheResult(cacheKeyMethod="getUserByIdCacheKey")
@HystrixCommand
public User getUserById(Long id){
   return restTemplate.getForObject("http://USER-SERVICE/users/{1}",User.class,id);
}
private Long getUserByIdCacheKey(Long id){
   return  id;
}

```

第二种方式:通过`@CacheKey`注解实现,但是就如上面表格所说,如果已经使用了`cacheKeyMethod`的生成函数,则`@CacheKey`注解不会生效

```
@CacheResult
@HystrixCommand
public User getUserById(@CacheKey("id") Long id){
	return restTemplate.getForObject("http://USER-SERVICE/users/{1}",User.class,id);
}

```

@CacheKey还可以用参数的内部属性作为key

```
@CacheResult
@HystrixCommand
public User getUserById(@CacheKey("id") User user){
	return restTemplate.getForObject("http://USER-SERVICE/users/{1}",User.class,user.getId());
}

```

3)缓存清理:

```
@CacheResult
@HystrixCommand
public User getUserById(@CacheKey("id") Long id){
	return restTemplate.getForObject("http://USER-SERVICE/users/{1}",User.class,id);
}
@CacheRemove(commandKey="getUserById")
@HystrixCommand
public void update(@CacheKey("id")User user){
	return restTemplate.postForObject("http://USER-SERVICE/users",user,User.class);
}

```

### 请求合并

在高并发的情况下,通信次数增加,总的通信时间消耗也会变得不理想,同时依赖服务的线程池资源有限,将出现排队等待和响应延迟的情况,通过请求的合并,可以达到减少通信消耗和线程数占用的效果.<br/>
第一步:为请求合并的实现准备一个批量请求命令的实现

```
public class UserBatchCommand extends HystrixCommand&lt;List&lt;User&gt;&gt;{
	UserService userService;
	List&lt;Long&gt; userIds;
	public UserBatchCommand(UserService userService,List&lt;Long&gt; userIds){
		super(Setter.withGroupKey(asKey("userServiceCommand")));
		this.userIds = userIds;
		this.userService = userService;
	}
	@Override
	protected List&lt;User&gt; run() throws Exception{
		return userService.findAll(userIds);
	]
}

```

通过调用userService.findAll方法来访问/users?ids={ids}接口以返回User的列表结果<br/>
第二步:通过继承HystrixCollapser实现请求合并器

```
public class UserCollapseCommand extends HystrixCollapser&lt;List&lt;User&gt;,User,Long&gt;{
    
    private UserService userService;
    private Long userId;
    
    public UserCollapseCommand(UserService userService,Long userId){
        //设置时间延迟窗口
        super(Setter.withCollapserKey(HystrixCollapserKey.Factory.asKey("userCollapseCommand")).andCollapserPropertiesDefaults(
                HystrixCollapserProperties.Setter().withTimerDelayInMilliseconds(100)
        ));
        this.userService = userService;
        this.userId = userId;
    }

    @Override
    public Long getRequestArgument() {
        return userId;
    }
    
    
    @Override
    protected HystrixCommand&lt;List&lt;User&gt;&gt; createCommand(Collection&lt;CollapsedRequest&lt;User, Long&gt;&gt; collection) {
        //初始化一个list
        List&lt;Long&gt; userIds = new ArrayList&lt;&gt;(collection.size());
        //将所有请求的id放在这个list中
        userIds.addAll(collection.stream().map(CollapsedRequest::getArgument).collect(Collectors.toList()));
        //合并成一个批量请求返回
        return new UserBatchCommand(userService,userIds);
    }

    @Override
    protected void mapResponseToRequests(List&lt;User&gt; users, Collection&lt;CollapsedRequest&lt;User, Long&gt;&gt; collection) {
        int count = 0;
        //将响应分发到每个请求上,完成批量结果到单个请求结果的转换
        for(CollapsedRequest&lt;User,Long&gt; collapsedRequest : collection){
            User user = users.get(count++);
            collapsedRequest.setResponse(user);
        }
    }
}

```

以下是请求合并器的原理图,在资源有效并且短时间内会产生高并发请求的时候,为避免连接不够用而引起的延迟可以考虑使用请求合并器的方式来处理和优化<br/>
<img alt="HystrixCollapser请求合并器" src="https://img-blog.csdnimg.cn/20190324150656414.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/><br/>
1.使用注解实现请求合并器

```
@Service
public class UserService {
    @Autowired
    private RestTemplate restTemplate;    
    @HystrixCollapser(batchMethod = "findAll", collapserProperties = {@HystrixProperty(name="timerDelayInMilliseconds",value="100")})
    public User find(Long id){
        return null;
    }    
    @HystrixCommand
    public List&lt;User&gt; findAll(List&lt;Long&gt; ids){
        return restTemplate.getForObject("http://USER-SERVICE/users?ids={1}",List.class, StringUtils.join(ids,","));
    }    
}

```

2.请求合并的额外开销<br/>
若请求不经过合并器访问的平均耗时为5ms,请求合并器的延迟时间窗为10ms,那么最坏情况下需要15ms,所以请求合并器的延迟时间窗会带来额外开销,所以是否使用请求合并器需要根据服务调用的实际情况来选择,主要考虑两个方面:
