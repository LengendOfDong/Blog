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

