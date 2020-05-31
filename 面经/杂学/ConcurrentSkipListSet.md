# 简介
ConcurrentSkipListSet底层是通过ConcurrentNavigableMap来实现的，它是一个有序的线程安全的集合。

ConcurrentSkipListSet基本上都是使用ConcurrentSkipListMap实现的，虽然取子set部分是使用ConcurrentSkipListMap中的内部类，但是这些内部类其实也是和ConcurrentSkipListMap相关的，它们返回ConcurrentSkipListMap的一部分数据。

# 总结
（1）ConcurrentSkipListSet底层是使用ConcurrentNavigableMap实现的
（2）ConcurrentSkipListSet有序的，基于元素的自然排序或者通过比较器确定的顺序
（3）ConcurrentSkipListSet是线程安全的

Set大汇总：

Set 	|有序性 |	线程安全 |	底层实现 |	关键接口 |	特点|
|:-|:-|:-|:-|:-|:-|
HashSet |	无| 	否 |	HashMap| 	无 |	简单|
LinkedHashSet |	有 |	否 |	LinkedHashMap| 	无 |	插入顺序|
TreeSet |	有 |	否 |	NavigableMap |	NavigableSet |	自然顺序|
CopyOnWriteArraySet |	有 |	是 |	CopyOnWriteArrayList |	无 |	插入顺序，读写分离|
ConcurrentSkipListSet| 	有 |	是 |	ConcurrentNavigableMap 	|NavigableSet| 	自然顺序|

从中我们可以发现一些规律：

（1）除了HashSet其它Set都是有序的；

（2）实现了NavigableSet或者SortedSet接口的都是自然顺序的；

（3）使用并发安全的集合实现的Set也是并发安全的；

（4）TreeSet虽然不是全部都是使用的TreeMap实现的，但其实都是跟TreeMap相关的（TreeMap的子Map中组合了TreeMap）；

（5）ConcurrentSkipListSet虽然不是全部都是使用的ConcurrentSkipListMap实现的，但其实都是跟ConcurrentSkipListMap相关的（ConcurrentSkipListeMap的子Map中组合了ConcurrentSkipListMap）；
