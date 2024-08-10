# Springboot3启动过程

本文采用Springboot 3.3.1版本，分析springboot3 的启动过程

```java
@SpringBootApplication
public class SprintBootTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SprintBootTestApplication.class, args);
    }

}
```

进入run方法：

```java
public static ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args) {
    return new SpringApplication(primarySources).run(args);
}
```

## 一、初始化SpringApplication

```java
//初始化SpringApplication
public SpringApplication(Class<?>... primarySources) {
    this(null, primarySources);
}

@SuppressWarnings({ "unchecked", "rawtypes" })
//传入的resourceLoader为空， primarySources表示SprintBootTestApplication.class
public SpringApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
    this.resourceLoader = resourceLoader;
    Assert.notNull(primarySources, "PrimarySources must not be null");
    this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));
    this.webApplicationType = WebApplicationType.deduceFromClasspath();
    this.bootstrapRegistryInitializers = new ArrayList<>(
        getSpringFactoriesInstances(BootstrapRegistryInitializer.class));
    setInitializers((Collection) getSpringFactoriesInstances(ApplicationContextInitializer.class));
    setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));
    this.mainApplicationClass = deduceMainApplicationClass();
}
```

[跳转到deduceFromClasspath源码](#1)

[跳转到getSpringFactoriesInstances源码](#2)

[跳转到deduceMainApplicationClass源码](#3)

<a id="1"></a>针对deduceFromClasspath源码解析：

```java
static WebApplicationType deduceFromClasspath() {
    //"org.springframework.web.reactive.DispatcherHandler"这个类如果存在，并且"org.springframework.web.servlet.DispatcherServlet"不存在
    //以及"org.glassfish.jersey.servlet.ServletContainer"也不存在时，会认为是响应式应用
    if (ClassUtils.isPresent(WEBFLUX_INDICATOR_CLASS, null) && !ClassUtils.isPresent(WEBMVC_INDICATOR_CLASS, null)
        && !ClassUtils.isPresent(JERSEY_INDICATOR_CLASS, null)) {
        return WebApplicationType.REACTIVE;
    }
    //如果不存在"jakarta.servlet.Servlet","org.springframework.web.context.ConfigurableWebApplicationContext"中，则说明是其他类型应用
    for (String className : SERVLET_INDICATOR_CLASSES) {
        if (!ClassUtils.isPresent(className, null)) {
            return WebApplicationType.NONE;
        }
    }
    return WebApplicationType.SERVLET;
}
```

对于响应式web应用，Servlet应用以及其他类型应用的区别见下：

https://tongyi.aliyun.com/qianwen/share?shareId=c6c6f5d2-e17d-4b5e-b509-4187653e84ca

<a id="2"></a>针对getSpringFactoriesInstances方法源码解析：

```java
private <T> List<T> getSpringFactoriesInstances(Class<T> type) {
    return getSpringFactoriesInstances(type, null);
}

private <T> List<T> getSpringFactoriesInstances(Class<T> type, ArgumentResolver argumentResolver) {
    return SpringFactoriesLoader.forDefaultResourceLocation(getClassLoader()).load(type, argumentResolver);
}

//先查看forDefaultResourceLocation方法的源码
public static SpringFactoriesLoader forDefaultResourceLocation(@Nullable ClassLoader classLoader) {
    return forResourceLocation(FACTORIES_RESOURCE_LOCATION, classLoader);
}

//从"META-INF/spring.factories"中，同时初始化了一个map,key是对应的资源路径，value就是资源加载对象
public static SpringFactoriesLoader forResourceLocation(String resourceLocation, @Nullable ClassLoader classLoader) {
    Assert.hasText(resourceLocation, "'resourceLocation' must not be empty");
    ClassLoader resourceClassLoader = (classLoader != null ? classLoader :
                                       SpringFactoriesLoader.class.getClassLoader());
    Map<String, SpringFactoriesLoader> loaders = cache.computeIfAbsent(
        resourceClassLoader, key -> new ConcurrentReferenceHashMap<>());
    return loaders.computeIfAbsent(resourceLocation, key ->
                                   new SpringFactoriesLoader(classLoader, loadFactoriesResource(resourceClassLoader, resourceLocation)));
}


protected static Map<String, List<String>> loadFactoriesResource(ClassLoader classLoader, String resourceLocation) {
    Map<String, List<String>> result = new LinkedHashMap<>();
    try {
        //从类加载器的相对路径下去寻找资源，即properties文件
        Enumeration<URL> urls = classLoader.getResources(resourceLocation);
        while (urls.hasMoreElements()) {
            UrlResource resource = new UrlResource(urls.nextElement());
            //加载properties文件获得对象
            Properties properties = PropertiesLoaderUtils.loadProperties(resource);
            properties.forEach((name, value) -> {
                //将properties中的类都收集到列表中
                String[] factoryImplementationNames = StringUtils.commaDelimitedListToStringArray((String) value);
                List<String> implementations = result.computeIfAbsent(((String) name).trim(),
                                                                      key -> new ArrayList<>(factoryImplementationNames.length));
                Arrays.stream(factoryImplementationNames).map(String::trim).forEach(implementations::add);
            });
        }
        //去重处理
        result.replaceAll(SpringFactoriesLoader::toDistinctUnmodifiableList);
    }
    catch (IOException ex) {
        throw new IllegalArgumentException("Unable to load factories from location [" + resourceLocation + "]", ex);
    }
    return Collections.unmodifiableMap(result);
}
//再查看load的方法，
public <T> List<T> load(Class<T> factoryType, @Nullable ArgumentResolver argumentResolver,
                        @Nullable FailureHandler failureHandler) {

    Assert.notNull(factoryType, "'factoryType' must not be null");
    //获取到对应工厂类型的实现名称，比如：BootstrapRegistryInitializer、ApplicationContextInitializer、ApplicationListener
    List<String> implementationNames = loadFactoryNames(factoryType);
    logger.trace(LogMessage.format("Loaded [%s] names: %s", factoryType.getName(), implementationNames));
    List<T> result = new ArrayList<>(implementationNames.size());
    FailureHandler failureHandlerToUse = (failureHandler != null) ? failureHandler : THROWING_FAILURE_HANDLER;
    for (String implementationName : implementationNames) {
        //初始化工厂实例，并将其加入到列表中
        T factory = instantiateFactory(implementationName, factoryType, argumentResolver, failureHandlerToUse);
        if (factory != null) {
            result.add(factory);
        }
    }
    //对工厂实例列表按照@Order或者@Priority定义的顺序排序
    AnnotationAwareOrderComparator.sort(result);
    return result;
}

@Nullable
protected <T> T instantiateFactory(String implementationName, Class<T> type,
                                   @Nullable ArgumentResolver argumentResolver, FailureHandler failureHandler) {

    try {
        //类加载器获取工厂类型的对应实例
        Class<?> factoryImplementationClass = ClassUtils.forName(implementationName, this.classLoader);
        //type与factoryImplementationClass之间是父子关系
        Assert.isTrue(type.isAssignableFrom(factoryImplementationClass), () ->
                      "Class [%s] is not assignable to factory type [%s]".formatted(implementationName, type.getName()));
        FactoryInstantiator<T> factoryInstantiator = FactoryInstantiator.forClass(factoryImplementationClass);
        //返回初始化实例
        return factoryInstantiator.instantiate(argumentResolver);
    }
    catch (Throwable ex) {
        failureHandler.handleFailure(type, implementationName, ex);
        return null;
    }
}
```

<a id="3"></a>针对deduceMainApplicationClass方法的源码解析：

```java
//推断出应用的主类
private Class<?> deduceMainApplicationClass() {
    //这行代码创建了一个 StackWalker 实例，并指定了 RETAIN_CLASS_REFERENCE 选项。这个选项确保了在遍历栈帧时，
    //所有遇到的类的引用会被保留下来，不会被垃圾回收器回收
    return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
        //这行代码使用 StackWalker 遍历当前线程的调用栈（即栈帧）
        .walk(this::findMainClass)
        .orElse(null);
}

private Optional<Class<?>> findMainClass(Stream<StackFrame> stack) {
    //找到第一个栈帧的方法名为main的类，就是要找的主类，比如这里就是SprintBootTestApplication.class
    return stack.filter((frame) -> Objects.equals(frame.getMethodName(), "main"))
        .findFirst()
        .map(StackWalker.StackFrame::getDeclaringClass);
}
```

## 二、启动应用

```java
public ConfigurableApplicationContext run(String... args) {
    // 创建一个Startup对象，用于记录启动过程的时间
    Startup startup = Startup.create();
    // 如果设置了注册关闭钩子，则启用关闭钩子的添加,用于优雅关闭SpringBoot应用
    if (this.registerShutdownHook) {
        SpringApplication.shutdownHook.enableShutdownHookAddition();
    }
    // 创建一个默认的引导上下文
    DefaultBootstrapContext bootstrapContext = createBootstrapContext();
    // 初始化上下文变量
    ConfigurableApplicationContext context = null;
    // 配置无头模式属性，确保在没有图形用户界面的环境中可以正常运行
    configureHeadlessProperty();
    // 获取运行监听器
    SpringApplicationRunListeners listeners = getRunListeners(args);
    // 通知监听器开始启动
    listeners.starting(bootstrapContext, this.mainApplicationClass);
    try {
        // 解析命令行参数
        ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
        // 准备环境，包括加载配置文件
        ConfigurableEnvironment environment = prepareEnvironment(listeners, bootstrapContext, applicationArguments);
        // 打印横幅信息
        Banner printedBanner = printBanner(environment);
        // 创建应用上下文
        context = createApplicationContext();
        // 设置启动上下文
        context.setApplicationStartup(this.applicationStartup);
        // 准备上下文，包括初始化Bean
        prepareContext(bootstrapContext, context, environment, listeners, applicationArguments, printedBanner);
        // 刷新上下文，完成Bean的初始化
        refreshContext(context);
        // 上下文刷新后的处理
        afterRefresh(context, applicationArguments);
        // 记录启动阶段
        startup.started();
        // 如果设置了打印启动信息，则打印启动日志
        if (this.logStartupInfo) {
            new StartupInfoLogger(this.mainApplicationClass).logStarted(getApplicationLog(), startup);
        }
        // 通知监听器启动完成
        listeners.started(context, startup.timeTakenToStarted());
        // 调用Runner接口的实现类
        callRunners(context, applicationArguments);
    }
    catch (Throwable ex) {
        throw handleRunFailure(context, ex, listeners);
    }
    try {
        // 如果上下文正在运行，则通知监听器准备好
        if (context.isRunning()) {
            listeners.ready(context, startup.ready());
        }
    }
    catch (Throwable ex) {
        // 处理准备好阶段失败的情况
        throw handleRunFailure(context, ex, null);
    }
    // 返回创建的应用上下文
    return context;
}
```

