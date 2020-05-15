# HashCode的作用是什么
hashcode特性体现主要在它的查找快捷性，在Set和Map使用哈希表结构存储数据的集合中。HashCode方法的就大大体现了它的价值，主要用于在这些集合中确定对象在整个哈希表中存储的区域。 

如果两个对象相同，则两个对象的equals方法返回的一定为true,两个对象HashCode方法返回的值也一定相同。

如果两个对象返回的HashCode的值相同，不能够说明两个对象的equals方法返回的值一定为true,只能说明这两个对象在存储在哈希表中的一个桶中。

如果一个对象的equals方法被重写，那么该对象的HashCode方法也应该被重写。

# 区别和联系
HashCode()和equals() 的区别与联系

1、如果eqauls()相同，那么它们的hashCode()一定要相同；

2、如果hashCode()相同，那么它们eqauls()并不一定相同.

所以，Java对于eqauls方法和hashCode方法是这样规定的：

1、如果两个对象相同，那么它们的hashCode值一定要相同；

2、如果两个对象的hashCode相同，它们并不一定相同     

