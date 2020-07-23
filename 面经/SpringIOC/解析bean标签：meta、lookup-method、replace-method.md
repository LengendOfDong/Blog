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
