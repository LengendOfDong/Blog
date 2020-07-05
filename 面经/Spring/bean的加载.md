# 简介
加载过程步骤大致如下：
- 转换对应的beanName

传入的参数可能是别名，也可能是FactoryBean，所以需要一系列的解析，解析内容如下：

1. 去除FactoryBean的修饰符，也就是如果name="&aa",那么会首先去除&而使name="aa"

2.取指定alias所表示的最终beanName

- 尝试从缓存中加载单例

单例在Spring的同一个容器内只会被创建一次，后续再获取Bean,就直接从单例缓存中获取了。当然这里也只是尝试加载，首先尝试从缓存中加载，如果加载不成功则再次尝试从singletonFactories中加载。

- bean的实例化

- 原型模式的依赖检查

只有在单例情况下才会尝试解决循环依赖

- 检测parentBeanFactory

检测如果当前加载的XML配置文件中不包含beanName所对应的配置，就只能到parentBeanFactory去尝试下了，然后再去递归的调用getBean方法。

- 将存储XML配置文件的GenericBeanDefinition转换为RootBeanDefinition

因为从XML配置中读取到的Bean信息是存储在GenericBeanDefinition中的，但是所有的Bean后续处理都是针对于RootBeanDefinition的，所以这里需要进行一个转换，转换的同时如果父类bean不为空的话，则会合并父类的属性。

- 寻找依赖

因为bean的初始化过程中很可能会用到某些属性，而某些属性很可能是动态配置的，并且配置成依赖于其他的bean，那么这个时候就有必要先加载依赖的bean，所以，在Spring的加载顺序中，在初始化某一个bean的时候首先会初始化这个bean的依赖。

- 针对不同的scope进行bean的创建

在Spring中存在着不同的Scope，其中默认的是singleton,但是还有些其他的配置诸如prototype，request之类的。在这个步骤中，Spring会根据不同的配置进行不同的初始化策略。

- 类型转换

通常对该方法的调用参数requiredType是为空的，但是可能会存在这样的情况，返回的bean是个String,但是requiredType却传入Integer类型，那么这时候本步骤就会起作用了，它的功能是将返回的bean转换成requiredType所指定的类型。
