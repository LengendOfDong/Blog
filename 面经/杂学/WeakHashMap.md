# 简介
WeakHashMap是一种弱引用map，内部的key会存储为弱引用，当jvm gc的时候，如果这些key没有强引用存在的话，会被gc回收掉，下一次当我们操作map的时候会把对应的Entry整个删除掉，基于这种特性，WeakHashMap特别适用于缓存处理。

# 
