# 生命周期
对于普通的Java对象，当new的时候创建对象，当它没有任何引用的时候被垃圾回收机制回收。而由Spring IoC容器托管的对象，它们的生命周期完全由容器控制。Spring中每个Bean的生命周期如下：
![Spring生命周期](https://github.com/LengendOfDong/Blog/blob/master/img/Spring%E5%A3%B0%E6%98%8E%E5%91%A8%E6%9C%9F.jpg)

## 四个阶段
实例化和属性赋值对应构造方法和setter方法的注入，初始化和销毁是用户能自定义扩展的两个阶段。

- 实例化 instantiation
- 属性赋值 Populate
- 初始化  Initialization
- 销毁  Destruction

实例化 -> 属性赋值 -> 初始化 -> 销毁

主要逻辑都在doCreate()方法中，逻辑很清晰，就是顺序调用以下三个方法，这三个方法与三个生命周期阶段一一对应，非常重要，在后续扩展接口分析中也会涉及。

    createBeanInstance() -> 实例化
    populateBean() -> 属性赋值
    initializeBean() -> 初始化

源码如下，能证明实例化，属性赋值和初始化这三个生命周期的存在。关于本文的Spring源码都将忽略无关部分，便于理解：

```java
// 忽略了无关代码
protected Object doCreateBean(final String beanName, final RootBeanDefinition mbd, final @Nullable Object[] args)
      throws BeanCreationException {

   // Instantiate the bean.
   BeanWrapper instanceWrapper = null;
   if (instanceWrapper == null) {
       // 实例化阶段！
      instanceWrapper = createBeanInstance(beanName, mbd, args);
   }

   // Initialize the bean instance.
   Object exposedObject = bean;
   try {
       // 属性赋值阶段！
      populateBean(beanName, mbd, instanceWrapper);
       // 初始化阶段！
      exposedObject = initializeBean(beanName, exposedObject, mbd);
   }
   }
```
于销毁，是在容器关闭时调用的，详见ConfigurableApplicationContext#close()

## 常用扩展点


## 设置对象属性（依赖注入）


## Reference

https://www.jianshu.com/p/1dec08d290c1
