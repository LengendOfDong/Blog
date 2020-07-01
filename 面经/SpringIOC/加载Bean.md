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

