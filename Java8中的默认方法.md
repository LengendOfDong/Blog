# Java8中的默认方法

## 默认方法

  Java8中加入默认方法主要是为了支持库设计师，让他们能够写出容易改进的接口。这一方法很重要，因为你会在接口中遇到越来越多的默认方法，但由于真正需要编写默认方法的程序员相对较少，而且它们只是有助于程序改进，而不是用于编写任何具体的程序。<br/>
  两段Java8示例代码：

```
List&lt;Apple&gt; heavyApples1 =
	inventory.stream().filter((Apple a) -&gt; a.getWeight() &gt; 50).collect(toList());
List&lt;Apple&gt; heavyApples2 = 
    inventory.parallelStream().filter((Apple a) -&gt; a.getWeight() &gt; 50).collect(toList())	

```

但这里有个问题：在Java8之前，List并没有stream或者parallelStream方法，它实现的Collection接口也没有。可没有这些方法，这些代码就不能编译。最简单的方法就是在Collection接口中加入stream方法，并加入ArrayList类的实现。<br/>
  可要是这样做，对用户来说就是噩梦了。有很多的替代集合框架都用Collection API实现了接口。但给接口加入一个新方法，意味着所有的实体类都必须为其提供一个实现。语言设计者没法控制Collections所有现有的实现，所以如何改变已发布的接口而不破坏已有的实现呢？<br/>
  Java8的解决办法就是打破最后一环——接口如今可以包含实现类没有提供实现的方法签名了。缺失的方法主体随接口提供了，而不是由实现类提供。<br/>
  这就给接口设计者提供了一个扩充接口的方式，而不会破坏现有的代码。Java 8在接口声明中使用新的**default**关键字来表示这一点。<br/>
  例如，在Java 8里，现在可以直接对List用sort方法。它是用Java8 List接口中如下所示的默认方法实现的，它会调用Collections.sort静态方法：

```
default void sort(Comparator&lt;? super E&gt; c){
	Collections.sort(this,c);
}

```

  这意味着List的任何实体类都不需要显式实现sort,而在以前的Java版本中，除非提供了sort的实现，否则这些实体类在重新编译时都会失败。
