# 原创：Lambda表达式之方法引用

## 方法引用

方法引用可以被看做仅仅调用特定方法的Lambda的一种快捷写法。它的基本思想是，如果一个Lambda代表的只是”直接调用这个方法“，那最好还是用名称来调用它，而不是去描述如何调用它。<br/>
  事实上，方法引用就是让你根据已有的方法实现来创建Lambda表达式。但是，显式地指明方法的名称，你的代码可读性会更好。

### 如何构建方法引用

方法引用主要有三类：<br/>
（1）指向静态方法的方法引用（例如Integer的parseInt方法，写作Integer::parseInt)<br/>
（2）指向任意类型实例方法的方法引用（例如String的length方法，写作String::length)<br/>
（3）指向现有对象的实例方法的方法引用（假设你有一个局部变量expensiveTransaction用于存放Transaction<br/>
第一种很好理解，就是静态方法<br/>
第二种就是你在引用一个对象的方法，而这个对象本身是Lambda的一个参数。<br/>
例如：Lambda表达式（String s) - &gt;  s.toUpperCase()可以写作String::toUpperCase。<br/>
第三种就是你在Lambda中调用一个已经存在的外部对象中的方法。<br/>
例如：Lambda表达式（）-&gt; expensiveTransaction.getValue()可以写作expensiveTransaction::getValue。

### 构造函数引用

  对于一个现有构造函数，可以利用它的名称和关键字new，来创建一个它的一个引用：ClassName::new。它的功能与指向静态方法的引用类似。

```
Supplier&lt;Apple&gt; c1 = Apple::new;
Apple a1 = c1.get();

```

等价于：

```
Supplier&lt;Apple&gt; c1 = () -&gt; new Apple();
Apple a1 = c1.get();

```

如果你的构造函数的签名是Apple(Integer weight),那么它就适合Function接口的签名，于是你可以这样写：

```
Function&lt;Integer,Apple&gt; c2 = Apple::new;
Apple a2 = c2.apple(110);

```

这等价于：

```
Function&lt;Integer,Apple&gt; c2 = (weight) -&gt; new Apple(weight);
Apple a2 = c2.apple(110);

```
