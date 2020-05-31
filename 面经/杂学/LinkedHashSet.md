# 总结
（1）LinkedHashSet的底层使用LinkedHashMap存储元素。

（2）LinkedHashSet是有序的，它是按照插入的顺序排序的。

LinkedHashSet底层使用LinkedHashMap存储元素，而LinkedHashMap是支持按元素访问顺序遍历元素的，也就是可以用来实现LRU的。

LinkedHashSet所有的构造方法都是调用HashSet的同一个构造方法，如下：

```java
 // HashSet的构造方法
    HashSet(int initialCapacity, float loadFactor, boolean dummy) {
        map = new LinkedHashMap<>(initialCapacity, loadFactor);
    }
```

然后，通过调用LinkedHashMap的构造方法初始化Map，如下所示：

```java
public LinkedHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        accessOrder = false;
    }
```
可以看到，这里把accessOrder写死成false了。

所以，LinkedHashSet不支持按访问顺序对元素排序的，只能按插入顺序排序。

