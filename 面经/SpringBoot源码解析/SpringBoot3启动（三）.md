## SpringBoot3启动过程三

本文采用Springboot 3.3.1版本，分析springboot3 的启动过程

<a id="begin"></a>

上次讲到了prepareContext中的load的方法，其中的load方法比较复杂：

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
    // 通知监听器开始启动,遍历执行每个监听器的starting方法，可以进行自定义
    listeners.starting(bootstrapContext, this.mainApplicationClass);
    try {
        // 解析命令行参数
        ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
        // 准备环境，包括加载配置文件
        ConfigurableEnvironment environment = prepareEnvironment(listeners, bootstrapContext, applicationArguments);
        // 打印横幅信息
        Banner printedBanner = printBanner(environment);
        // 创建应用上下文
        // 根据web应用类型，创建对应的应用上下文实例
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
prepareContext中的load的方法：

```java
protected void load(ApplicationContext context, Object[] sources) {
    if (logger.isDebugEnabled()) {
        logger.debug("Loading source " + StringUtils.arrayToCommaDelimitedString(sources));
    }
    BeanDefinitionLoader loader = createBeanDefinitionLoader(getBeanDefinitionRegistry(context), sources);
    if (this.beanNameGenerator != null) {
        loader.setBeanNameGenerator(this.beanNameGenerator);
    }
    if (this.resourceLoader != null) {
        loader.setResourceLoader(this.resourceLoader);
    }
    if (this.environment != null) {
        loader.setEnvironment(this.environment);
    }
    //加载之前，如果有自定义BeanName生成器就设置，有自定义资源加载器就设置，有自定义环境变量就设置
    //这样在加载资源的时候，就可以使用自定义类进行处理
    loader.load();
}
//遍历资源进行加载
void load() {
    for (Object source : this.sources) {
        load(source);
    }
}
//分情况进行讨论资源的加载
private void load(Object source) {
    Assert.notNull(source, "Source must not be null");
    //加载类资源
    if (source instanceof Class<?> clazz) {
        load(clazz);
        return;
    }
    //加载资源文件
    if (source instanceof Resource resource) {
        load(resource);
        return;
    }
    //加载包文件
    if (source instanceof Package pack) {
        load(pack);
        return;
    }
    //加载字符序列
    if (source instanceof CharSequence sequence) {
        load(sequence);
        return;
    }
    // 如果source不属于上述任何类型，则抛出IllegalArgumentException
    throw new IllegalArgumentException("Invalid source type " + source.getClass());
}

```

#### 一、加载类

```java
//加载指定的Groovy脚本或Java类作为配置源
private void load(Class<?> source) {
    //如果Groovy可用，并且source类是GroovyBeanDefinitionSource的子类或实现了该接口
    if (isGroovyPresent() && GroovyBeanDefinitionSource.class.isAssignableFrom(source)) {
        // 创建GroovyBeanDefinitionSource的实例
        GroovyBeanDefinitionSource loader = BeanUtils.instantiateClass(source, GroovyBeanDefinitionSource.class);
        //如果groovyReader是一个GroovyBeanDefinitionReader（意味着它能处理Groovy脚本）,将Groovy脚本中定义的beans加载到groovyReader中
        ((GroovyBeanDefinitionReader) this.groovyReader).beans(loader.getBeans());
    }
    //如果source类是一个符合条件的Java类（例如，带有Spring的组件注解如@Component、@Service、@Repository或@Controller），则使用annotatedReader来注册这个类
    if (isEligible(source)) {
        this.annotatedReader.register(source);
    }
}

public void register(Class<?>... componentClasses) {
    for (Class<?> componentClass : componentClasses) {
        registerBean(componentClass);
    }
}

public void registerBean(Class<?> beanClass) {
    doRegisterBean(beanClass, null, null, null, null);
}

private <T> void doRegisterBean(Class<T> beanClass, @Nullable String name,
                                @Nullable Class<? extends Annotation>[] qualifiers, @Nullable Supplier<T> supplier,
                                @Nullable BeanDefinitionCustomizer[] customizers) {

    AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(beanClass);
    //这个是用来处理@Conditional注解，决定是否跳过某个Bean定义的处理
    if (this.conditionEvaluator.shouldSkip(abd.getMetadata())) {
        return;
    }

    abd.setAttribute(ConfigurationClassUtils.CANDIDATE_ATTRIBUTE, Boolean.TRUE);
    abd.setInstanceSupplier(supplier);
    ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);
    abd.setScope(scopeMetadata.getScopeName());
    String beanName = (name != null ? name : this.beanNameGenerator.generateBeanName(abd, this.registry));
	//处理通用注解
    AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);
    if (qualifiers != null) {
        for (Class<? extends Annotation> qualifier : qualifiers) {
            if (Primary.class == qualifier) {
                abd.setPrimary(true);
            }
            else if (Lazy.class == qualifier) {
                abd.setLazyInit(true);
            }
            else {
                abd.addQualifier(new AutowireCandidateQualifier(qualifier));
            }
        }
    }
    if (customizers != null) {
        for (BeanDefinitionCustomizer customizer : customizers) {
            customizer.customize(abd);
        }
    }

    BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);
    definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
    BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, this.registry);
}

//处理基于条件的Bean定义的跳过逻辑，确定是否应该跳过某个Bean定义的处理，基于条件注解@Conditional的存在与否以及这些条件是否满足。
public boolean shouldSkip(@Nullable AnnotatedTypeMetadata metadata, @Nullable ConfigurationPhase phase) {
    //metadata为空或者其上没有@Conditional注解，则直接返回false,表示不应该跳过此Bean定义的处理
    if (metadata == null || !metadata.isAnnotated(Conditional.class.getName())) {
        return false;
    }
	
    if (phase == null) {
        //判断是否是配置类
        if (metadata instanceof AnnotationMetadata annotationMetadata &&
            ConfigurationClassUtils.isConfigurationCandidate(annotationMetadata)) {
            //递归调用，
            return shouldSkip(metadata, ConfigurationPhase.PARSE_CONFIGURATION);
        }
        return shouldSkip(metadata, ConfigurationPhase.REGISTER_BEAN);
    }
	//保存所有Condition实例
    List<Condition> conditions = new ArrayList<>();
    //遍历所有条件类名数组
    for (String[] conditionClasses : getConditionClasses(metadata)) {
        for (String conditionClass : conditionClasses) {
            //实例化一个Condition对象，并添加到列表中
            Condition condition = getCondition(conditionClass, this.context.getClassLoader());
            conditions.add(condition);
        }
    }
	//对所有收集到的条件按顺序进行排序，以确保他们按照正确的优先级被检查
    //通过@Order或者@Priority注解进行标注的顺序
    AnnotationAwareOrderComparator.sort(conditions);
	
    for (Condition condition : conditions) {
        ConfigurationPhase requiredPhase = null;
        //遍历每个条件并获取配置阶段
        if (condition instanceof ConfigurationCondition configurationCondition) {
            requiredPhase = configurationCondition.getConfigurationPhase();
        }
        //如果配置阶段未指定或者等于所需阶段与当前阶段相同，并且条件不满足，则应该跳过此Bean定义的处理
        if ((requiredPhase == null || requiredPhase == phase) && !condition.matches(this.context, metadata)) {
            return true;
        }
    }
	//没有条件需要跳过处理，可以进行此Bean的处理
    return false;
}
```

查看doRegisterBean方法中的AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);

```java
public static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd) {
    processCommonDefinitionAnnotations(abd, abd.getMetadata());
}
//此方法是为了处理Bean定义上的通用注解
static void processCommonDefinitionAnnotations(AnnotatedBeanDefinition abd, AnnotatedTypeMetadata metadata) {
    //Lazy注解用于指示一个Bean是否应该懒加载
    AnnotationAttributes lazy = attributesFor(metadata, Lazy.class);
    if (lazy != null) {
        //如果Lazy注解存在说明Bean在第一次请求时才初始化
        abd.setLazyInit(lazy.getBoolean("value"));
    }
    //未找到Lazy注解，并且abd元数据与传入的metadata不同（这可能是因为Lazy注解在类级别定义），则从abd的元数据中再次尝试获取Lazy注解
    else if (abd.getMetadata() != metadata) {
        lazy = attributesFor(abd.getMetadata(), Lazy.class);
        if (lazy != null) {
            abd.setLazyInit(lazy.getBoolean("value"));
        }
    }
	//如果存在Primary注解，那么Bean应该在相同类型中有多个实现时被优先考虑
    if (metadata.isAnnotated(Primary.class.getName())) {
        abd.setPrimary(true);
    }
    //从metadata中获取DependsOn注解的属性，指定此Bean依赖于哪些其他Bean的初始化
    AnnotationAttributes dependsOn = attributesFor(metadata, DependsOn.class);
    if (dependsOn != null) {
        //获取value中的字符串数组
        abd.setDependsOn(dependsOn.getStringArray("value"));
    }
	//通过Role注解来定义Bean的作用域或角色
    AnnotationAttributes role = attributesFor(metadata, Role.class);
    if (role != null) {
        //将value中的值转换成整型并设置为abd的角色
        abd.setRole(role.getNumber("value").intValue());
    }
    //从metadata中获取Description注解的属性，用于给Bean提供描述信息
    AnnotationAttributes description = attributesFor(metadata, Description.class);
    if (description != null) {
        abd.setDescription(description.getString("value"));
    }
}
```



#### 二、加载资源文件

```java
private void load(Resource source) {
    //如果资源文件后缀是groovy，说明是groovy文件，如果groovy读取器没有找到则会报错，否则使用groovy读取器进行加载Bean定义
    if (source.getFilename().endsWith(".groovy")) {
        if (this.groovyReader == null) {
            throw new BeanDefinitionStoreException("Cannot load Groovy beans without Groovy on classpath");
        }
        this.groovyReader.loadBeanDefinitions(source);
    }
    else {
        //xmlreader加载bean定义
        this.xmlReader.loadBeanDefinitions(source);
    }
}
```

#### 三、加载包文件

```java
private void load(Package source) {
    this.scanner.scan(source.getName());
}
```

scan方法对包文件进行扫描：

```java
public int scan(String... basePackages) {
    int beanCountAtScanStart = this.registry.getBeanDefinitionCount();
	//扫描包文件
    doScan(basePackages);

    // Register annotation config processors, if necessary.
    if (this.includeAnnotationConfig) {
        AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
    }
	//Bean定义注册类中所有Bean定义个数减去已有ban定义个数
    return (this.registry.getBeanDefinitionCount() - beanCountAtScanStart);
}
```

进入ClassPathBeanDefinitionScanner类的doScan方法：

```java
protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
    Assert.notEmpty(basePackages, "At least one base package must be specified");
    Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<>();
    for (String basePackage : basePackages) {
        Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
        for (BeanDefinition candidate : candidates) {
            ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(candidate);
            candidate.setScope(scopeMetadata.getScopeName());
            String beanName = this.beanNameGenerator.generateBeanName(candidate, this.registry);
            if (candidate instanceof AbstractBeanDefinition abstractBeanDefinition) {
                postProcessBeanDefinition(abstractBeanDefinition, beanName);
            }
            if (candidate instanceof AnnotatedBeanDefinition annotatedBeanDefinition) {
                AnnotationConfigUtils.processCommonDefinitionAnnotations(annotatedBeanDefinition);
            }
            if (checkCandidate(beanName, candidate)) {
                BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
                definitionHolder =
                    AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
                beanDefinitions.add(definitionHolder);
                registerBeanDefinition(definitionHolder, this.registry);
            }
        }
    }
    return beanDefinitions;
}
```

registerAnnotationConfigProcessors方法用来注册相关注解后置处理器：

```java
public static void registerAnnotationConfigProcessors(BeanDefinitionRegistry registry) {
    registerAnnotationConfigProcessors(registry, null);
}

public static Set<BeanDefinitionHolder> registerAnnotationConfigProcessors(
    BeanDefinitionRegistry registry, @Nullable Object source) {

    DefaultListableBeanFactory beanFactory = unwrapDefaultListableBeanFactory(registry);
    if (beanFactory != null) {
        if (!(beanFactory.getDependencyComparator() instanceof AnnotationAwareOrderComparator)) {
            beanFactory.setDependencyComparator(AnnotationAwareOrderComparator.INSTANCE);
        }
        if (!(beanFactory.getAutowireCandidateResolver() instanceof ContextAnnotationAutowireCandidateResolver)) {
            beanFactory.setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
        }
    }

    Set<BeanDefinitionHolder> beanDefs = new LinkedHashSet<>(8);

    if (!registry.containsBeanDefinition(CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME)) {
        RootBeanDefinition def = new RootBeanDefinition(ConfigurationClassPostProcessor.class);
        def.setSource(source);
        beanDefs.add(registerPostProcessor(registry, def, CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME));
    }

    if (!registry.containsBeanDefinition(AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME)) {
        RootBeanDefinition def = new RootBeanDefinition(AutowiredAnnotationBeanPostProcessor.class);
        def.setSource(source);
        beanDefs.add(registerPostProcessor(registry, def, AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME));
    }

    // Check for Jakarta Annotations support, and if present add the CommonAnnotationBeanPostProcessor.
    if ((jakartaAnnotationsPresent || jsr250Present) &&
        !registry.containsBeanDefinition(COMMON_ANNOTATION_PROCESSOR_BEAN_NAME)) {
        RootBeanDefinition def = new RootBeanDefinition(CommonAnnotationBeanPostProcessor.class);
        def.setSource(source);
        beanDefs.add(registerPostProcessor(registry, def, COMMON_ANNOTATION_PROCESSOR_BEAN_NAME));
    }

    // Check for JPA support, and if present add the PersistenceAnnotationBeanPostProcessor.
    if (jpaPresent && !registry.containsBeanDefinition(PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME)) {
        RootBeanDefinition def = new RootBeanDefinition();
        try {
            def.setBeanClass(ClassUtils.forName(PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME,
                                                AnnotationConfigUtils.class.getClassLoader()));
        }
        catch (ClassNotFoundException ex) {
            throw new IllegalStateException(
                "Cannot load optional framework class: " + PERSISTENCE_ANNOTATION_PROCESSOR_CLASS_NAME, ex);
        }
        def.setSource(source);
        beanDefs.add(registerPostProcessor(registry, def, PERSISTENCE_ANNOTATION_PROCESSOR_BEAN_NAME));
    }

    if (!registry.containsBeanDefinition(EVENT_LISTENER_PROCESSOR_BEAN_NAME)) {
        RootBeanDefinition def = new RootBeanDefinition(EventListenerMethodProcessor.class);
        def.setSource(source);
        beanDefs.add(registerPostProcessor(registry, def, EVENT_LISTENER_PROCESSOR_BEAN_NAME));
    }

    if (!registry.containsBeanDefinition(EVENT_LISTENER_FACTORY_BEAN_NAME)) {
        RootBeanDefinition def = new RootBeanDefinition(DefaultEventListenerFactory.class);
        def.setSource(source);
        beanDefs.add(registerPostProcessor(registry, def, EVENT_LISTENER_FACTORY_BEAN_NAME));
    }

    return beanDefs;
}

private static BeanDefinitionHolder registerPostProcessor(
    BeanDefinitionRegistry registry, RootBeanDefinition definition, String beanName) {

    definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
    registry.registerBeanDefinition(beanName, definition);
    return new BeanDefinitionHolder(definition, beanName);
}
```



#### 四、加载字符序列

```java
private void load(CharSequence source) {
    String resolvedSource = this.scanner.getEnvironment().resolvePlaceholders(source.toString());
    // Attempt as a Class
    try {
        load(ClassUtils.forName(resolvedSource, null));
        return;
    }
    catch (IllegalArgumentException | ClassNotFoundException ex) {
        // swallow exception and continue
    }
    // Attempt as Resources
    if (loadAsResources(resolvedSource)) {
        return;
    }
    // Attempt as package
    Package packageResource = findPackage(resolvedSource);
    if (packageResource != null) {
        load(packageResource);
        return;
    }
    throw new IllegalArgumentException("Invalid source '" + resolvedSource + "'");
}
```

