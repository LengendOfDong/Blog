# 回顾
在核心逻辑方法doLoadBeanDefinitions()中主要是做三件事情：
- 调用getValidationModeForResource()获取xml文件的验证模式。
- 调用loadDocument()根据xml文件获取相应的Document实例。
- 调用registerBeanDefinitions()注册Bean实例。

本文主要分析获取xml文件的验证模式。

> XML文件的验证模式保证了XML文件的正确性。

## DTD与XSD的区别
DTD（Document Type Definition),即文档类型定义，为XML文件的验证机制，属于XML文件中组成的一部分。DTD是一种保证XML文档格式正确的有效验证方式，它定义了相关XML文档的元素、属性、排列方式、元素的内容类型以及元素的层次结构。其实DTD就相当于XML中的“词汇”和“语法”，我们可以通过比较XML稳健和DTD文件来看文档是否符合规范，元素和标签使用是否正确。要在Spring中使用DTD，需要在Spring XML文件头部声明：
```java
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE beans PUBLIC  "-//SPRING//DTD BEAN//EN"  "http://www.springframework.org/dtd/spring-beans.dtd">
```
DTD在一定的阶段推动了XML的发展，但是它本身存在一定的缺陷：
- 它没有使用 XML 格式，而是自己定义了一套格式，相对解析器的重用性较差；而且 DTD 的构建和访问没有标准的编程接口，因而解析器很难简单的解析 DTD 文档。
