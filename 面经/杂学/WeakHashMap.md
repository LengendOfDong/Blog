# 简介
WeakHashMap是一种弱引用map，内部的key会存储为弱引用，当jvm gc的时候，如果这些key没有强引用存在的话，会被gc回收掉，下一次当我们操作map的时候会把对应的Entry整个删除掉，基于这种特性，WeakHashMap特别适用于缓存处理。

# 总结

（1）WeakHashMap使用（数组 + 链表）存储结构；

（2）WeakHashMap中的key是弱引用，gc的时候会被清除；

（3）每次对map的操作都会剔除失效key对应的Entry；

（4）使用String作为key时，一定要使用new String()这样的方式声明key，才会失效，其它的基本类型的包装类型是一样的；

（5）WeakHashMap常用来作为缓存使用；

## 使用举例
在这里通过new String()声明的变量才是弱引用，使用"6"这种声明方式会一直存在于常量池中，不会被清理，所以"6"这个元素会一直在map里面，其它的元素随着gc都会被清理掉。
```java
package com.coolcoding.code;

import java.util.Map;
import java.util.WeakHashMap;

public class WeakHashMapTest {

public static void main(String[] args) {
    Map<String, Integer> map = new WeakHashMap<>(3);

    // 放入3个new String()声明的字符串
    map.put(new String("1"), 1);
    map.put(new String("2"), 2);
    map.put(new String("3"), 3);

    // 放入不用new String()声明的字符串
    map.put("6", 6);

    // 使用key强引用"3"这个字符串
    String key = null;
    for (String s : map.keySet()) {
        // 这个"3"和new String("3")不是一个引用
        if (s.equals("3")) {
            key = s;
        }
    }

    // 输出{6=6, 1=1, 2=2, 3=3}，未gc所有key都可以打印出来
    System.out.println(map);

    // gc一下
    System.gc();

    // 放一个new String()声明的字符串
    map.put(new String("4"), 4);

    // 输出{4=4, 6=6, 3=3}，gc后放入的值和强引用的key可以打印出来
    System.out.println(map);

    // key与"3"的引用断裂
    key = null;

    // gc一下
    System.gc();

    // 输出{6=6}，gc后强引用的key可以打印出来
    System.out.println(map);
}
}
```

## 强引用、软引用、弱引用和虚引用

强、软、弱、虚引用知多少？

（1）强引用

使用最普遍的引用。如果一个对象具有强引用，它绝对不会被gc回收。如果内存空间不足了，gc宁愿抛出OutOfMemoryError，也不是会回收具有强引用的对象。

（2）软引用

如果一个对象只具有软引用，则内存空间足够时不会回收它，但内存空间不够时就会回收这部分对象。只要这个具有软引用对象没有被回收，程序就可以正常使用。

（3）弱引用

如果一个对象只具有弱引用，则不管内存空间够不够，当gc扫描到它时就会回收它。

（4）虚引用

如果一个对象只具有虚引用，那么它就和没有任何引用一样，任何时候都可能被gc回收。

软（弱、虚）引用必须和一个引用队列（ReferenceQueue）一起使用，当gc回收这个软（弱、虚）引用引用的对象时，会把这个软（弱、虚）引用放到这个引用队列中。

比如，上述的Entry是一个弱引用，它引用的对象是key，当key被回收时，Entry会被放到queue中。

## Reference
http://cmsblogs.com/?p=4735
