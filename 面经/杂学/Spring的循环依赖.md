# 什么是循环依赖
循环依赖其实就是循环引用也就是两个或两个以上的bean互相持有对方，形成闭环，比如A依赖B，B依赖C，C又依赖A。

注意，这里不是函数的循环调用，是对象的相互依赖关系。循环调用其实就是一个死循环，除非有终结条件。

Spring中循环依赖场景有：
- 构造器的循环依赖。
- field属性的循环依赖。

# 怎么检测是否存在循环依赖
检测循环依赖相对比较容易，Bean在创建的时候可以给该Bean打标记，如果递归调用回来发现正在创建中的话，说明循环依赖了。

# Spring怎么解决循环依赖
Spring的循环依赖的理论依据其实是基于Java的引用传递，当我们获取到对象的引用时，对象的field或者属性是可以延后设置的。但是构造器必须是在获取引用之前。

Spring的单例对象的初始化主要分为三步：
- createBeanInstance：实例化，其实也就是调用对象的构造方法实例化对象
- populateBean:填充属性，这一步主要是多bean的依赖属性进行填充。
- initialization:调用spring xml中的init方法。

从上面讲述的单例bean初始化步骤我们可以知道，循环依赖主要发生在第一，第二步。也就是构造器循环依赖和field循环依赖。

那么我们要解决循环引用也应该从初始化过程着手，对于单例来说，在Spring容器整个生命周期内，有且只有一个对象，所以很容易想到这个对象应该存放在Cache中，Spring为了解决单例的循环依赖问题，使用了三级缓存。

首先我们看源码，三级缓存主要指：
```java
/** Cache of singleton objects: bean name --> bean instance */
private final Map<String, Object> singletonObjects = new ConcurrentHashMap<String, Object>(256);

/** Cache of singleton factories: bean name --> ObjectFactory */
private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<String, ObjectFactory<?>>(16);

/** Cache of early singleton objects: bean name --> bean instance */
private final Map<String, Object> earlySingletonObjects = new HashMap<String, Object>(16);
```
这三级缓存分别指：
singletonFactories:单例对象工厂的cache
earlySingletonObjects：提前曝光的单例对象的Cache
singletonObjects:
