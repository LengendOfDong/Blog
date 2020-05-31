# 简介
TreeSet底层是采用TreeMap实现的一种Set,所以它是有序的，同样也是非线程安全的。

TreeSet实现了NavigableSet接口，所以它是有序的。

# 总结
（1）TreeSet底层使用NavigableMap存储元素；

（2）TreeSet是有序的；

（3）TreeSet是非线程安全的；

（4）TreeSet实现了NavigableSet接口，而NavigableSet继承自SortedSet接口；

（5）TreeSet实现了SortedSet接口；

- TreeSet和LinkedHashSet都是有序的，有何不同？
LinkedHashSet并没有实现SortedSet接口，它的有序性主要依赖于LinkedHashMap的有序性，所以它的有序性是指按照插入顺序保证的有序性。

TreeSet实现了SortedSet接口，它的有序性主要依赖于NavigableMap的有序性，而Navigable又继承自SortedMap,这个接口的有序性是指按照key的自然排序保证
的有序性，而key的自然排序又有两种实现方式，一种是key实现Comparable接口，一种是构造方法传入Comparator比较器。

- TreeSet里面真的是使用TreeMap来存储元素的吗？

通过源码分析我们知道TreeSet里面实际上是使用的NavigableMap来存储元素，虽然大部分时候这个map确实是TreeMap，但不是所有时候都是TreeMap。

因为有一个构造方法是TreeSet(NavigableMap<E,Object> m)，而且这是一个非public方法，通过调用关系我们可以发现这个构造方法都是在自己类中使用的，比如下面这个：

```java
public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
        return new TreeSet<>(m.tailMap(fromElement, inclusive));
    }
```

而这个m我们姑且认为它是TreeMap，也就是调用TreeMap的tailMap()方法：
```java
public NavigableMap<K,V> tailMap(K fromKey, boolean inclusive) {
        return new AscendingSubMap<>(this,
                                     false, fromKey, inclusive,
                                     true,  null,    true);
    }
```
可以看到，返回的是AscendingSubMap对象，这个类的继承链：

![AscendingSubMap继承链](https://github.com/LengendOfDong/Blog/blob/master/img/AscendingSubMap%E7%BB%A7%E6%89%BF%E9%93%BE.png)

可以看到，这个类并没有继承TreeMap,不过通过源码分析也可以看出来这个类是组合了TreeMap,也算和TreeMap有点关系，只是不是继承关系。

所以，TreeSet的底层不完全是使用TreeMap来实现的，更准确说，应该是NavigableMap.
