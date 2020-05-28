# 简介
LinkedHashMap内部维护了一个双向链表，能保证元素按插入的顺序访问，也能以访问顺序访问，可以用来实现LRU缓存策略。

LinkedHashMap可以看成是 LinkedList + HashMap。

# LinkedHashMap存储结构
![LinkedHashMap存储结构图](https://github.com/LengendOfDong/Blog/blob/master/img/LinkedHashMap%E5%AD%98%E5%82%A8%E7%BB%93%E6%9E%84.png)

# LRU缓存淘汰策略
LRU，Least Recently Used，最近最少使用，也就是优先淘汰最近最少使用的元素。
如果使用LinkedHashMap，我们把accessOrder设置为true是不是就差不多能实现这个策略了呢？答案是肯定的。请看下面的代码：
```java
package com.coolcoding.code;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author: tangtong
 * @date: 2019/3/18
 */
public class LRUTest {
    public static void main(String[] args) {
        // 创建一个只有5个元素的缓存
        LRU<Integer, Integer> lru = new LRU<>(5, 0.75f);
        lru.put(1, 1);
        lru.put(2, 2);
        lru.put(3, 3);
        lru.put(4, 4);
        lru.put(5, 5);
        lru.put(6, 6);
        lru.put(7, 7);

        System.out.println(lru.get(4));

        lru.put(6, 666);

        // 输出: {3=3, 5=5, 7=7, 4=4, 6=666}
        // 可以看到最旧的元素被删除了
        // 且最近访问的4被移到了后面
        System.out.println(lru);
    }
}

class LRU<K, V> extends LinkedHashMap<K, V> {

    // 保存缓存的容量
    private int capacity;

    public LRU(int capacity, float loadFactor) {
        super(capacity, loadFactor, true);
        this.capacity = capacity;
    }

    /**
    * 重写removeEldestEntry()方法设置何时移除旧元素
    * @param eldest
    * @return
    */
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        // 当元素个数大于了缓存的容量, 就移除元素
        return size() > this.capacity;
    }
}
```

# 总结

（1）LinkedHashMap继承自HashMap，具有HashMap的所有特性；

（2）LinkedHashMap内部维护了一个双向链表存储所有的元素；

（3）如果accessOrder为false，则可以按插入元素的顺序遍历元素；

（4）如果accessOrder为true，则可以按访问元素的顺序遍历元素；

（5）LinkedHashMap的实现非常精妙，很多方法都是在HashMap中留的钩子（Hook），直接实现这些Hook就可以实现对应的功能了，并不需要再重写put()等方法；

（6）默认的LinkedHashMap并不会移除旧元素，如果需要移除旧元素，则需要重写removeEldestEntry()方法设定移除策略；

（7）LinkedHashMap可以用来实现LRU缓存淘汰策略；

## Reference
http://cmsblogs.com/?p=4733
