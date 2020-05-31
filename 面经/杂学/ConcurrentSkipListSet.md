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
