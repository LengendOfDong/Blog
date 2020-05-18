# 生命周期
对于普通的Java对象，当new的时候创建对象，当它没有任何引用的时候被垃圾回收机制回收。而由Spring IoC容器托管的对象，它们的生命周期完全由容器控制。Spring中每个Bean的生命周期如下：
![Spring生命周期](https://github.com/LengendOfDong/Blog/blob/master/img/Spring%E5%A3%B0%E6%98%8E%E5%91%A8%E6%9C%9F.jpg)

## 实例化Bean
对于BeanFactory容器，当客户向容器请求一个尚未初始化的bean时，或初始化bean的时候需要注入另一个尚未初始化的依赖时，容器就会调用createBean进行实例化。

对于Appliaction容器，当容器启动结束后，便实例化所有的Bean.

容器通过获取BeanDefinition对象中的信息进行实例化。并且这一步仅仅是简单的实例化，并未进行依赖注入。

实例化对象被包装在BeanWrapper对象中，BeanWrapper提供了设置对象属性的接口，从而避免了使用反射机制设置属性。

## 设置对象属性（依赖注入）


## Reference

https://www.jianshu.com/p/1dec08d290c1
