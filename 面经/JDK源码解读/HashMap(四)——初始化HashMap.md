# 初始化HashMap
```java
/**
 * Constructs an empty <tt>HashMap</tt> with the specified initial
 * capacity and load factor.
 *
 * @param  initialCapacity the initial capacity
 * @param  loadFactor      the load factor
 * @throws IllegalArgumentException if the initial capacity is negative
 *         or the load factor is nonpositive
 */
public HashMap(int initialCapacity, float loadFactor) {
    if (initialCapacity < 0)
        throw new IllegalArgumentException("Illegal initial capacity: " +
                                           initialCapacity);
    if (initialCapacity > MAXIMUM_CAPACITY)
        initialCapacity = MAXIMUM_CAPACITY;
    if (loadFactor <= 0 || Float.isNaN(loadFactor))
        throw new IllegalArgumentException("Illegal load factor: " +
                                           loadFactor);
    this.loadFactor = loadFactor;
    this.threshold = tableSizeFor(initialCapacity);
}
```
上面的代码中最重要的部分就是
```java
this.threshold = tableSizeFor(initialCapacity);
```

tableSizeFor的代码如下：
```java
/**
 * Returns a power of two size for the given target capacity.
 */
static final int tableSizeFor(int cap) {
    int n = cap - 1;
    n |= n >>> 1;
    n |= n >>> 2;
    n |= n >>> 4;
    n |= n >>> 8;
    n |= n >>> 16;
    return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
}
```

详解如下：

先来分析有关n位操作部分：先来假设n的二进制为01xxx...xxx。接着

对n右移1位：001xx...xxx，再位或：011xx...xxx

对n右移2为：00011...xxx，再位或：01111...xxx

此时前面已经有四个1了，再右移4位且位或可得8个1

同理，有8个1，右移8位肯定会让后八位也为1。

综上可得，该算法让最高位的1后面的位全变为1。

最后再让结果n+1，即得到了2的整数次幂的值了。

```
int n = cap - 1;
```
这句的意义是，当cap正好为2的整数次幂时，返回自身。例如，二进制1000，十进制数值为8。如果不对它减1而直接操作，将得到答案10000，即16。显然不是结果。减1后二进制为111，再进行操作则会得到原来的数值1000，即8。

## 总结
tableSizeFor方法的功能是返回大于输入参数且最近的2的整数次幂的数。比如10，则返回16。
