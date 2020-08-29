# 开启bean的加载
Spring将整个流程分为两个阶段，容器初始化和加载bean的阶段。
- 容器初始化阶段：首先通过某种方式加载Configuration Metadata（主要是依据Resource/ResourceLoader两个体系），然后容器会对加载的Configuration Metadata进行解析和分析，并将分析的信息组装成BeanDefinition，并将其保存注册到相应的BeanDefinitionRegistry中，至此，Spring IOC的初始化工作完成。
- 加载Bean阶段：经过容器初始化阶段之后，应用程序中定义的bean信息已经全部加载到系统中了，当我们显示或者隐式地调用getBean()时，则会触发加载bean阶段。在这阶段，容器会首先检查所请求的对象是否已经初始化完成了，如果没有，则会根据注册的Bean信息实例化请求的对象，并为其注册依赖，然后将其返回给请求方。至此第二阶段也已经完成。

getBean()方法会触发加载bean阶段，如下：
```java
 public Object getBean(String name) throws BeansException {
        return doGetBean(name, null, null, false);
   }
```
内部调用doGetBean()方法，其接受四个参数：
- name:要获取的bean名字
- requiredType:要获取的bean的类型
- args:创建bean时传递的参数，这个参数仅限于创建bean时使用
- typeCheckOnly:是否为类型检查。

1.获取beanName
```java
final String beanName = transformedBeanName(name);
```
这里传递的是 name，不一定就是 beanName，可能是 aliasName，也有可能是 FactoryBean，所以这里需要调用 transformedBeanName() 方法对 name 进行一番转换，主要如下： 
```java
protected String transformedBeanName(String name) {
   return canonicalName(BeanFactoryUtils.transformedBeanName(name));
 }

  // 去除 FactoryBean 的修饰符
  public static String transformedBeanName(String name) {
      Assert.notNull(name, "'name' must not be null");
      String beanName = name;
      while (beanName.startsWith(BeanFactory.FACTORY_BEAN_PREFIX)) {
          beanName = beanName.substring(BeanFactory.FACTORY_BEAN_PREFIX.length());
      }
      return beanName;
  }

  // 转换 aliasName
  public String canonicalName(String name) {
      String canonicalName = name;
      // Handle aliasing...
      String resolvedName;
      do {
          resolvedName = this.aliasMap.get(canonicalName);
          if (resolvedName != null) {
              canonicalName = resolvedName;
          }
      }
      while (resolvedName != null);
      return canonicalName;
  }
```
主要处理过程包括两步： 
- 去除 FactoryBean 的修饰符。如果 name 以 “&” 为前缀，那么会去掉该 "&"，例如，name = "&studentService"，则会是 name = "studentService"。
- 取指定的 alias 所表示的最终 beanName。主要是一个循环获取 beanName 的过程，例如别名 A 指向名称为 B 的 bean 则返回 B，若 别名 A 指向别名 B，别名 B 指向名称为 C 的 bean，则返回 C。

2.从单例bean缓存中获取bean，对应代码如下：
```java
 Object sharedInstance = getSingleton(beanName);
  if (sharedInstance != null && args == null) {
      if (logger.isDebugEnabled()) {
          if (isSingletonCurrentlyInCreation(beanName)) {
              logger.debug("Returning eagerly cached instance of singleton bean '" + beanName +
                      "' that is not fully initialized yet - a consequence of a circular reference");
          }
          else {
              logger.debug("Returning cached instance of singleton bean '" + beanName + "'");
          }
      }
      bean = getObjectForBeanInstance(sharedInstance, name, beanName, null);
  }
```
单例模式的 bean 在整个过程中只会被创建一次，第一次创建后会将该 bean 加载到缓存中，后面在获取 bean 就会直接从单例缓存中获取。如果从缓存中得到了 bean，则需要调用 getObjectForBeanInstance() 对 bean 进行实例化处理，因为缓存中记录的是最原始的 bean 状态，我们得到的不一定是我们最终想要的 bean。 

3.原型模式依赖检查与 parentBeanFactory 对应代码段 ：
```java
if (isPrototypeCurrentlyInCreation(beanName)) {
      throw new BeanCurrentlyInCreationException(beanName);
  }

  // Check if bean definition exists in this factory.
  BeanFactory parentBeanFactory = getParentBeanFactory();
  if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
      // Not found -> check parent.
      String nameToLookup = originalBeanName(name);
      if (parentBeanFactory instanceof AbstractBeanFactory) {
          return ((AbstractBeanFactory) parentBeanFactory).doGetBean(
                  nameToLookup, requiredType, args, typeCheckOnly);
      }
      else if (args != null) {
          // Delegation to parent with explicit args.
          return (T) parentBeanFactory.getBean(nameToLookup, args);
      }
      else {
          // No args -> delegate to standard getBean method.
          return parentBeanFactory.getBean(nameToLookup, requiredType);
      }
  }
```
Spring只处理单例模式下的循环依赖，对于原型模式的循环依赖直接抛出异常。主要原因还是在于Spring解决循环依赖的策略有关。对于单例模式Spring在创建bean的时候并不是等bean完全创建完成后才会将bean添加至缓存中，而是不等bean创建完成就会将创建bean的ObjectFactory提早加入到缓存中，这样一旦下一个bean创建的时候需要依赖bean时直接使用ObjectFactory。

对于原型模式我们知道时没法使用缓存的，所以Spring对原型模式的循环依赖处理策略则是不处理。如果容器缓存中没有相对应的BeanDefinition则会尝试从父类工厂（parentBeanFactory）中加载，然后再去递归调用getBean().

4.依赖处理
```java
 String[] dependsOn = mbd.getDependsOn();
 if (dependsOn != null) {
     for (String dep : dependsOn) {
         if (isDependent(beanName, dep)) {
             throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                     "Circular depends-on relationship between '" + beanName + "' and '" + dep + "'");
         }
         registerDependentBean(dep, beanName);
         try {
             getBean(dep);
         }
         catch (NoSuchBeanDefinitionException ex) {
             throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                     "'" + beanName + "' depends on missing bean '" + dep + "'", ex);
         }
     }
 }
```
每个 bean 都不是单独工作的，它会依赖其他 bean，其他 bean 也会依赖它，对于依赖的 bean ，它会优先加载，所以在 Spring 的加载顺序中，在初始化某一个 bean 的时候首先会初始化这个 bean 的依赖。

5.作用域处理
```java
 // 从指定的 scope 下创建 bean
  String scopeName = mbd.getScope();
  final Scope scope = this.scopes.get(scopeName);
  if (scope == null) {
      throw new IllegalStateException("No Scope registered for scope name '" + scopeName + "'");
  }
  try {
      Object scopedInstance = scope.get(beanName, () -> {
          beforePrototypeCreation(beanName);
          try {
              return createBean(beanName, mbd, args);
          }
          finally {
              afterPrototypeCreation(beanName);
          }
      });
      bean = getObjectForBeanInstance(scopedInstance, name, beanName, mbd);
  }
  catch (IllegalStateException ex) {
      throw new BeanCreationException(beanName,
              "Scope '" + scopeName + "' is not active for the current thread; consider " +
                      "defining a scoped proxy for this bean if you intend to refer to it from a singleton",
              ex);
  }
```
Spring bean 的作用域默认为 singleton，当然还有其他作用域，如prototype、request、session 等，不同的作用域会有不同的初始化策略。
