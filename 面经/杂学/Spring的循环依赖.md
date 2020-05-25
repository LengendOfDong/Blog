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

singletonObjects:单对象的Cache

我们在创建bean的时候，首先想到的是从cache中获取这个单例的bean,这个缓存就是singletonObjects。主要调用方法就是：
```java
protected Object getSingleton(String beanName, boolean allowEarlyReference) {
    Object singletonObject = this.singletonObjects.get(beanName);
    //对象正在创建中
    if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
        synchronized (this.singletonObjects) {
        //从提前曝光的单例对象的Cache中获取单例对象
            singletonObject = this.earlySingletonObjects.get(beanName);
            if (singletonObject == null && allowEarlyReference) {
                ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
                if (singletonFactory != null) {
                    singletonObject = singletonFactory.getObject();
                    this.earlySingletonObjects.put(beanName, singletonObject);
                    this.singletonFactories.remove(beanName);
                }
            }
        }
    }
    return (singletonObject != NULL_OBJECT ? singletonObject : null);
}
```
上面的代码需要解释两个参数：
- isSingletonCurrentlyInCreation()判断当前单例bean是否正在创建中，也就是没有初始化完成（比如A的构造器依赖了B对象所以得先去创建B对象，或者在A的populateBean过程中依赖了B对象，得先去创建B对象，这时的A就是处于创建中的状态）
- allowEarlyReference是否允许从SingletonFactories中通过getObject拿到对象。

分析getSingleton()的整个过程，Spring首先从一级缓存singletonObjects中获取。如果获取不到，并且对象正在创建中，就再从二级缓存earlySingletonObjects中获取。如果还是获取不到

## Reference
https://zhuanlan.zhihu.com/p/84267654
