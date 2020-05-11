# Resize

```java
/**
 * Initializes or doubles table size.  If null, allocates in
 * accord with initial capacity target held in field threshold.
 * Otherwise, because we are using power-of-two expansion, the
 * elements from each bin must either stay at same index, or move
 * with a power of two offset in the new table.
 *
 * 初始化或者扩容表大小。
 * 如果为空，则根据字段阈值中保持的初始容量目标进行分配。 否则，因为我们使用的是2的幂，
 * 所以每个bin中的元素必须保持相同的索引，或者在新表中以2的幂偏移。
 * @return the table
 */
final Node<K,V>[] resize() {
    Node<K,V>[] oldTab = table;
    //旧表为空则为0，不为空则为旧表的长度
    int oldCap = (oldTab == null) ? 0 : oldTab.length;
    //旧的门限值
    int oldThr = threshold;
    int newCap, newThr = 0;
    if (oldCap > 0) {
        //如果原容量大于默认最大容量
        if (oldCap >= MAXIMUM_CAPACITY) {
            //门限值设为Integer.MAX_VALUE
            threshold = Integer.MAX_VALUE;
            return oldTab;
        }
        else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                 oldCap >= DEFAULT_INITIAL_CAPACITY)
            newThr = oldThr << 1; // double threshold
    }
    //新容量使用旧的阈值
    else if (oldThr > 0) // initial capacity was placed in threshold
        newCap = oldThr;
    //旧门限值和旧容量都为0，使用默认初始容量为新的容量，初始容量乘以加载因子作为新的阈值
    else {               // zero initial threshold signifies using defaults
        newCap = DEFAULT_INITIAL_CAPACITY;
        newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
    }
    //如果新阈值为空
    if (newThr == 0) {
        float ft = (float)newCap * loadFactor;
        //新容量和加载因子的乘积小于最大容量，并且新容量也小于最大容量，则乘积为新的阈值
        newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                  (int)ft : Integer.MAX_VALUE);
    }
    threshold = newThr;
    @SuppressWarnings({"rawtypes","unchecked"})
        Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
    table = newTab;
    if (oldTab != null) {
        for (int j = 0; j < oldCap; ++j) {
            Node<K,V> e;
            //遍历旧表
            if ((e = oldTab[j]) != null) {
               //抹去旧表的节点
                oldTab[j] = null;
                //此节点没有重复节点，不是链表也不是红黑树节点，用它的hash值在新表的容量上取模，确定它在新表中的位置
                if (e.next == null)
                    newTab[e.hash & (newCap - 1)] = e;
                //如果该节点是红黑树节点，需要对该节点进行分割
                else if (e instanceof TreeNode)
                    ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                //如果该节点是链表头节点
                else { // preserve order
                    Node<K,V> loHead = null, loTail = null;
                    Node<K,V> hiHead = null, hiTail = null;
                    Node<K,V> next;
                    do {
                        next = e.next;
                        if ((e.hash & oldCap) == 0) {
                            if (loTail == null)
                                loHead = e;
                            else
                                loTail.next = e;
                            loTail = e;
                        }
                        else {
                            if (hiTail == null)
                                hiHead = e;
                            else
                                hiTail.next = e;
                            hiTail = e;
                        }
                    } while ((e = next) != null);
                    if (loTail != null) {
                        loTail.next = null;
                        newTab[j] = loHead;
                    }
                    if (hiTail != null) {
                        hiTail.next = null;
                        newTab[j + oldCap] = hiHead;
                    }
                }
            }
        }
    }
    return newTab;
}
```
