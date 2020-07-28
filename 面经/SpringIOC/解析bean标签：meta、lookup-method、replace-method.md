# 简介
在完成Bean标签基本属性解析后，会依次调用parseMetaElements()、parseLookUpOverrideSubElements()、parseReplaceMethodSubElements()对子元素meta、lookup-method、replace-method完成解析。三个子元素的作用如下：
- meta:元数据
- lookup-method:Spring动态改变bean里方法的实现。方法执行返回的对象，使用Spring内原有的这类对象替换，通过改变方法返回值来改变方法。内部实现为使用cglib方法，重新生成子类，重写配置的方法和返回对象，达到动态改变的效果。
- replace-method:Spring动态改变bean里方法的实现。需要改变的方法，使用Spring内原有其他类（需要继承接口org.springframework.beans.factory.support.MethodReplacer）的逻辑，替换这个方法。通过改变方法执行逻辑来动态改变方法。

## meta子元素
> meta： 元数据，当需要使用里面的信息时可以通过key获取

meta所声明的key并不会在Bean中体现，只是一个额外的声明，当我们需要使用里面的信息时，通过BeanDefinition的getAttribute()获取。该子元素的解析过程如下：
```java
public void parseMetaElements(Element ele, BeanMetadataAttributeAccessor attributeAccessor) {
        NodeList nl = ele.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (isCandidateElement(node) && nodeNameEquals(node, META_ELEMENT)) {
                Element metaElement = (Element) node;
                String key = metaElement.getAttribute(KEY_ATTRIBUTE);
                String value = metaElement.getAttribute(VALUE_ATTRIBUTE);
                BeanMetadataAttribute attribute = new BeanMetadataAttribute(key, value);
                attribute.setSource(extractSource(metaElement));
                attributeAccessor.addMetadataAttribute(attribute);
            }
        }
    }
```
解析过程较为简单，获取相应的key - value构建BeanMetadataAttribute对象,  然后通过addMetadataAttribute()加入到AbstractBeanDefinition中。如下：
```java
 public void addMetadataAttribute(BeanMetadataAttribute attribute) {
        super.setAttribute(attribute.getName(), attribute);
    }
```
委托AttributeAccessorSupport实现，如下：
```java
public void setAttribute(String name, @Nullable Object value) {
        Assert.notNull(name, "Name must not be null");
        if (value != null) {
            this.attributes.put(name, value);
        }
        else {
            removeAttribute(name);
        }
    }
```
AttributeAccessorSupport是接口AttributeAccessor的实现者。AttributeAccessor接口定义了与其他对象的元数据进行连接和访问的约定，可以通过该接口对属性进行获取、设置、删除操作。设置元数据后，可以通过getAttribute()获取，如下：
```java
public Object getAttribute(String name) {
        BeanMetadataAttribute attribute = (BeanMetadataAttribute) super.getAttribute(name);
        return (attribute != null ? attribute.getValue() : null);
    }
```

## lookup-method子元素
> lookup-method:获取器注入，是把一个方法声明为返回某种类型的bean但实际要返回的bean是在配置文件里面配置的。该方法可以用于设计一些可插拔的功能上，解除程序依赖。

```java
public interface Car {

    void display();
}

public class Bmw implements Car{
    @Override
    public void display() {
        System.out.println("我是 BMW");
    }
}

public class Hongqi implements Car{
    @Override
    public void display() {
        System.out.println("我是 hongqi");
    }
}

public abstract class Display {


    public void display(){
        getCar().display();
    }

    public abstract Car getCar();
}

   public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring.xml");

        Display display = (Display) context.getBean("display");
        display.display();
    }
}
```
配置内容如下：
```java
<bean id="display" class="org.springframework.core.test1.Display">
        <lookup-method name="getCar" bean="hongqi"/>
    </bean>
```
运行结果为：
```java
我是 hongqi
```
如果将 bean="hongqi"替换为bean="bwm",则运行结果变成：
```java
我是bwm
```
lookup-method解析过程如下：
```java
public void parseLookupOverrideSubElements(Element beanEle, MethodOverrides overrides) {
        NodeList nl = beanEle.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (isCandidateElement(node) && nodeNameEquals(node, LOOKUP_METHOD_ELEMENT)) {
                Element ele = (Element) node;
                String methodName = ele.getAttribute(NAME_ATTRIBUTE);
                String beanRef = ele.getAttribute(BEAN_ELEMENT);
                LookupOverride override = new LookupOverride(methodName, beanRef);
                override.setSource(extractSource(ele));
                overrides.addOverride(override);
            }
        }
    }
```
解析过程和meta子元素没有多大区别，同样是解析methodName，beanRef构造一个LookupOverride对象，然后覆盖即可。

## replaced-method子元素
>replaced-method：可以在运行时调用新的方法替换现有的方法，还能动态地更新原有方法的逻辑。

