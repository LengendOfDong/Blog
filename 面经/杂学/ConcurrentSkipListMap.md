# 简介
跳表是一个随机化的数据结构，实质就是一种可以进行二分查找的有序链表。

跳表在原有的有序链表上面增加了多级索引，通过索引来实现快速查找。

跳表不仅能提高搜索性能，同时也可以提高插入和删除操作的性能。

日常生活中，图书馆中的图书分类就是差不多的用法，给出图书的大致范围，通过查看图书分类，进行一层层细分。

# 添加元素
![初始链表](https://github.com/LengendOfDong/Blog/blob/master/img/ConcurrentSkipList%E5%88%9D%E5%A7%8B%E9%93%BE%E8%A1%A8.png)

假如，我们现在要插入一个元素9。

（1）寻找目标节点之前最近的一个索引对应的数据节点，在这里也就是找到了5这个数据节点；

（2）从5开始向后遍历，找到目标节点的位置，也就是在8和12之间；

（3）插入9这个元素，Part I 结束；
![添加元素1](https://github.com/LengendOfDong/Blog/blob/master/img/ConcurrentSkipList%E6%B7%BB%E5%8A%A01.png)

然后，计算其索引层级，假如是3，也就是level=3。

（1）建立竖直的down索引链表；

（2）超过了现有高度2，还要再增加head索引链的高度；

（3）至此，Part II 结束；
![添加元素2](https://github.com/LengendOfDong/Blog/blob/master/img/ConcurrentSkipList%E6%B7%BB%E5%8A%A02.png)

最后，把right指针补齐。

（1）从第3层的head往右找当前层级目标索引的位置；

（2）找到就把目标索引和它前面索引的right指针连上，这里前一个正好是head；

（3）然后前一个索引向下移，这里就是head下移；

（4）再往右找目标索引的位置；

（5）找到了就把right指针连上，这里前一个是3的索引；

（6）然后3的索引下移；

（7）再往右找目标索引的位置；

（8）找到了就把right指针连上，这里前一个是5的索引；

（9）然后5下移，到底了，Part III 结束，整个插入过程结束；

![添加元素3](https://github.com/LengendOfDong/Blog/blob/master/img/ConcurrentSkipList%E6%B7%BB%E5%8A%A03.png)

# 删除元素
初始跳表如下：
![删除前初始跳表](https://github.com/LengendOfDong/Blog/blob/master/img/ConcurrentSkipList%E5%88%A0%E9%99%A41.png)

（1）找到9这个数据节点；

（2）把9这个节点的value值设置为null；

（3）在9后面添加一个marker节点，标记9已经删除了；

（4）让8指向12；

（5）把索引节点与它前一个索引的right断开联系；

（6）跳表高度降级；

![删除节点](https://github.com/LengendOfDong/Blog/blob/master/img/ConcurrentSkipList%E5%88%A0%E9%99%A42.png)

# 查找元素
（1）寻找目标节点之前最近的一个索引对应的数据节点，这里就是5；

（2）从5开始往后遍历，经过8，到9；

（3）找到了返回；

![查找元素](https://github.com/LengendOfDong/Blog/blob/master/img/ConcurrentSkipList%E6%9F%A5%E6%89%BE.png)
