# 简介
双端队列是一种特殊的队列，它的两端都可以进出元素，故而得名双端队列。

ArrayDequeue是一种以数组方式实现的双端队列，它是非线程安全的。

# 源码分析
- 入队
```java
// 从队列头入队
public void addFirst(E e) {
    // 不允许null元素
    if (e == null)
        throw new NullPointerException();
    // 将head指针减1并与数组长度减1取模
    // 这是为了防止数组到头了边界溢出
    // 如果到头了就从尾再向前
    // 相当于循环利用数组
    elements[head = (head - 1) & (elements.length - 1)] = e;
    // 如果头尾挨在一起了，就扩容
    // 扩容规则也很简单，直接两倍
    if (head == tail)
        doubleCapacity();
}
// 从队列尾入队
public void addLast(E e) {
    // 不允许null元素
    if (e == null)
        throw new NullPointerException();
    // 在尾指针的位置放入元素
    // 可以看到tail指针指向的是队列最后一个元素的下一个位置
    elements[tail] = e;
    // tail指针加1，如果到数组尾了就从头开始
    if ( (tail = (tail + 1) & (elements.length - 1)) == head)
        doubleCapacity();
}
```
（1）入队有两种方式，从队列头或者从队列尾；

（2）如果容量不够了，直接扩大为两倍；

（3）通过取模的方式让头尾指针在数组范围内循环；

（4）x & (len - 1) = x % len，使用&的方式更快；

- 扩容
```java
private void doubleCapacity() {
    assert head == tail;
    // 头指针的位置
    int p = head;
    // 旧数组长度
    int n = elements.length;
    // 头指针离数组尾的距离
    int r = n - p; // number of elements to the right of p
    // 新长度为旧长度的两倍
    int newCapacity = n << 1;
    // 判断是否溢出
    if (newCapacity < 0)
        throw new IllegalStateException("Sorry, deque too big");
    // 新建新数组
    Object[] a = new Object[newCapacity];
    // 将旧数组head之后的元素拷贝到新数组中
    System.arraycopy(elements, p, a, 0, r);
    // 将旧数组下标0到head之间的元素拷贝到新数组中
    System.arraycopy(elements, 0, a, r, p);
    // 赋值为新数组
    elements = a;
    // head指向0，tail指向旧数组长度表示的位置
    head = 0;
    tail = n;
}
```
![array-deque](https://github.com/LengendOfDong/Blog/blob/master/img/array-deque1.png)

- 出队
```java
// 从队列头出队
public E pollFirst() {
    int h = head;
    @SuppressWarnings("unchecked")
    // 取队列头元素
    E result = (E) elements[h];
    // 如果队列为空，就返回null
    if (result == null)
        return null;
    // 将队列头置为空
    elements[h] = null;     // Must null out slot
    // 队列头指针右移一位
    head = (h + 1) & (elements.length - 1);
    // 返回取得的元素
    return result;
}
// 从队列尾出队
public E pollLast() {
    // 尾指针左移一位
    int t = (tail - 1) & (elements.length - 1);
    @SuppressWarnings("unchecked")
    // 取当前尾指针处元素
    E result = (E) elements[t];
    // 如果队列为空返回null
    if (result == null)
        return null;
    // 将当前尾指针处置为空
    elements[t] = null;
    // tail指向新的尾指针处
    tail = t;
    // 返回取得的元素
    return result;
}
```
（1）出队有两种方式，从队列头或者从队列尾；

（2）通过取模的方式让头尾指针在数组范围内循环；

（3）出队之后没有缩容

Deque还可以直接用来作为栈使用，入栈和出栈只要都操作队列头就可以了。

# 总结
（1）ArrayDeque是采用数组方式实现的双端队列；

（2）ArrayDeque的出队入队是通过头尾指针循环利用数组实现的；

（3）ArrayDeque容量不足时是会扩容的，每次扩容容量增加一倍；

（4）ArrayDeque可以直接作为栈使用；
