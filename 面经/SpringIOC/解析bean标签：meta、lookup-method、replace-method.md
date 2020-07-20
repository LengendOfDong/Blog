# 简介
在完成Bean标签基本属性解析后，会依次调用parseMetaElements()、parseLookUpOverrideSubElements()、parseReplaceMethodSubElements()对子元素meta、lookup-method、replace-method完成解析。三个子元素的作用如下：
- meta:元数据
- lookup-method:Spring动态改变bean里方法的实现。方法执行返回的对象，使用Spring内原有的这类对象替换，通过改变方法返回值来改变方法。内部实现为使用cglib方法，重新生成子类，重写配置的方法和返回对象，达到动态改变的效果。
- replace-method:Spring动态改变bean里方法的实现。
