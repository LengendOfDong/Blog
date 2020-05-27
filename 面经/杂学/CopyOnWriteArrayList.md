# 总结

（1）CopyOnWriteArrayList使用ReentrantLock重入锁加锁，保证线程安全；

（2）CopyOnWriteArrayList的写操作都要先拷贝一份新数组，在新数组中做修改，修改完了再用新数组替换老数组，所以空间复杂度是O(n)，性能比较低下；

（3）CopyOnWriteArrayList的读操作支持随机访问，时间复杂度为O(1)；

（4）CopyOnWriteArrayList采用读写分离的思想，读操作不加锁，写操作加锁，且写操作占用较大内存空间，所以适用于读多写少的场合；

（5）CopyOnWriteArrayList只保证最终一致性，不保证实时一致性；

## 为什么没有size属性
因为每次修改都是拷贝一份正好可以存储目标个数元素的数组，所以不需要size属性了，数组的长度就是集合的大小，而不像ArrayList数组的长度实际是要大于集合的大小的。

比如，add(E e)操作，先拷贝一份n+1个元素的数组，再把新元素放到新数组的最后一位，这时新数组的长度为len+1了，也就是集合的size了。

## Reference
http://cmsblogs.com/?p=4729
