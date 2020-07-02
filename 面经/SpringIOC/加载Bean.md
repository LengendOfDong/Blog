# 简介
```java
ClassPathResource resource = new ClassPathResource("bean.xml");
DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
reader.loadBeanDefinitions(resource);
```
这段代码是Spring中编程时使用IOC容器，通过这四段简单的代码，我们可以初步判断IOC容器的使用过程。

- 获取资源
- 获取BeanFactory
- 根据新建的BeanFactory创建一个BeanDefinitionReader对象，该Reader对象为资源的解析器。
- 装载资源 整个过程就分为三个步骤：资源定位，装载，注册

资源定位：用外部资源描述Bean对象，在初始化IOC容器的第一步就是需要定位这个外部资源。

装载：装载就是BeanDefinition的载入，BeanDefinitionReader 读取、解析 Resource 资源，也就是将用户定义的 Bean 表示成 IOC 容器的内部数据结构：BeanDefinition。在 IOC 容器内部维护着一个 BeanDefinition Map 的数据结构，在配置文件中每一个 <bean> 都对应着一个BeanDefinition对象。
  
注册：向IOC容器注册在第二步解析好的 BeanDefinition，这个过程是通过 BeanDefinitionRegistry 接口来实现的。在 IOC 容器内部其实是将第二个过程解析得到的 BeanDefinition 注入到一个 HashMap 容器中，IOC 容器就是通过这个 HashMap 来维护这些 BeanDefinition 的。在这里需要注意的一点是这个过程并没有完成依赖注入，依赖注册是发生在应用第一次调用 getBean() 向容器索要 Bean 时。当然我们可以通过设置预处理，即对某个 Bean 设置 lazyinit 属性，那么这个 Bean 的依赖注入就会在容器初始化的时候完成。

reader.loadBeanDefinitions(resource) 才是加载资源的真正实现。
```java
public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
        return loadBeanDefinitions(new EncodedResource(resource));
    }
```
从指定的 xml 文件加载 Bean Definition，这里会先对 Resource 资源封装成 EncodedResource。这里为什么需要将 Resource 封装成 EncodedResource呢？主要是为了对 Resource 进行编码，保证内容读取的正确性。封装成 EncodedResource 后，调用 loadBeanDefinitions()，这个方法才是真正的逻辑实现。


