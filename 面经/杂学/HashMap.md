# 简介
HashMap采用key/value存储结构，每个key对应唯一的value，查询和修改的速度都很快，能达到O(1)的平均时间复杂度。它是非线程安全的，且不保证元素存储的顺序；

在Java中，HashMap的实现采用了（数组 + 链表 + 红黑树）的复杂结构，数组的一个元素又称作桶。

在添加元素时，会根据hash值算出元素在数组中的位置，如果该位置没有元素，则直接把元素放置在此处，如果该位置有元素了，则把元素以链表的形式放置在链表的尾部。

当一个链表的元素个数达到一定的数量（且数组的长度达到一定的长度）后，则把链表转化为红黑树，从而提高效率。

数组的查询效率为O(1)，链表的查询效率是O(k)，红黑树的查询效率是O(log k)，k为桶中的元素个数，所以当元素数量非常多的时候，转化为红黑树能极大地提高效率。

# 总结

（1）HashMap是一种散列表，采用（数组 + 链表 + 红黑树）的存储结构；

（2）HashMap的默认初始容量为16（1<<4），默认装载因子为0.75f，容量总是2的n次方；

（3）HashMap扩容时每次容量变为原来的两倍；

（4）当桶的数量小于64时不会进行树化，只会扩容；

（5）当桶的数量大于64且单个桶中元素的数量大于8时，进行树化；

（6）当单个桶中元素数量小于6时，进行反树化；

（7）HashMap是非线程安全的容器；

（8）HashMap查找添加元素的时间复杂度都为O(1)；

## 相关面试题
https://mp.weixin.qq.com/s?__biz=MjM5NzMyMjAwMA==&mid=2651487383&idx=1&sn=01b76cc546364e31394a3f03e3988366&chksm=bd2510e88a5299feb6cf2435d131d2af22e43b2c77184d3c77cd0fa17bf814d8d9c3b6a4eb31&xtrack=1&scene=0&subscene=92&sessionid=1590020740&clicktime=1590020764&enterid=1590020764&ascene=7&devicetype=android-29&version=27000e39&nettype=WIFI&abtest_cookie=AAACAA%3D%3D&lang=zh_CN&exportkey=AcAEJWpBb0lr9LxXp4VirtU%3D&pass_ticket=eC0UP38kInc6fK7YaZIs8mVioGCrPrvRKaSUuQLQl41GJssjMKzQc5x9F6JfB5%2Bq&wx_header=1

## Reference
http://cmsblogs.com/?p=4731
