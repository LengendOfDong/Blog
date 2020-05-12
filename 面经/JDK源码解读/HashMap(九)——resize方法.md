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
 //三种情况，原容量大于0， 原容量等于0且原阈值大于0，原容量等于0且原阈值等于0.
final Node<K,V>[] resize() {
    Node<K,V>[] oldTab = table;
    //旧表为空则为0，不为空则为旧表的长度
    int oldCap = (oldTab == null) ? 0 : oldTab.length;
    //旧的门限值
    int oldThr = threshold;
    int newCap, newThr = 0;
    if (oldCap > 0) {
        //如果原容量大于默认最大容量，则修改阈值为最大，并直接返回，不进行扩容
        if (oldCap >= MAXIMUM_CAPACITY) {
            //门限值设为Integer.MAX_VALUE
            threshold = Integer.MAX_VALUE;
            return oldTab;
        }
        //原容量大于0，新容量为原容量的两倍，新阈值为原阈值的两倍
        else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                 oldCap >= DEFAULT_INITIAL_CAPACITY)
            newThr = oldThr << 1; // double threshold
    }
    //原容量为0，新容量使用旧的阈值
    else if (oldThr > 0) // initial capacity was placed in threshold
        newCap = oldThr;
    //旧门限值和旧容量都为0，使用默认初始容量为新的容量，初始容量乘以加载因子作为新的阈值
    else {               // zero initial threshold signifies using defaults
        newCap = DEFAULT_INITIAL_CAPACITY;
        newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
    }
    //如果新阈值为空，此情况针对原容量为0的情况。
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
                    //通过循环，可以将原来的链表分成
                    do {
                        next = e.next;
                        //取hash值的最高位，如果为0，则将节点给loHead,后续通过loTail进行递增
                        if ((e.hash & oldCap) == 0) {
                            if (loTail == null)
                                loHead = e;
                            else
                                loTail.next = e;
                            loTail = e;
                        }
                        //取hash值的最高位，如果不为0，则将节点给hiHead,后续通过hiTail进行递增
                        else {
                            if (hiTail == null)
                                hiHead = e;
                            else
                                hiTail.next = e;
                            hiTail = e;
                        }
                    } while ((e = next) != null);
                    //将loHead保留对应位置，如旧表的位置为j=0，则保留的新表的位置也为0，另一部分保存的位置为0+oldCap。
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
红黑树进行扩容，使用split方法：
```java
/**
     * Splits nodes in a tree bin into lower and upper tree bins,
     * or untreeifies if now too small. Called only from resize;
     * see above discussion about split bits and indices.
     *
     * 分割树形桶中的节点，分成高低两个树形桶，如果数量太少就进行解除树形。
     * @param map the map
     * @param tab the table for recording bin heads
     * @param index the index of the table being split
     * @param bit the bit of hash to split on
     */
    final void split(HashMap<K,V> map, Node<K,V>[] tab, int index, int bit) {
        TreeNode<K,V> b = this;
        // Relink into lo and hi lists, preserving order
        TreeNode<K,V> loHead = null, loTail = null;
        TreeNode<K,V> hiHead = null, hiTail = null;
        int lc = 0, hc = 0;
        for (TreeNode<K,V> e = b, next; e != null; e = next) {
            next = (TreeNode<K,V>)e.next;
            e.next = null;
            if ((e.hash & bit) == 0) {
                if ((e.prev = loTail) == null)
                    loHead = e;
                else
                    loTail.next = e;
                loTail = e;
                ++lc;
            }
            else {
                if ((e.prev = hiTail) == null)
                    hiHead = e;
                else
                    hiTail.next = e;
                hiTail = e;
                ++hc;
            }
        }

        if (loHead != null) {
            if (lc <= UNTREEIFY_THRESHOLD)
                tab[index] = loHead.untreeify(map);
            else {
                tab[index] = loHead;
                if (hiHead != null) // (else is already treeified)
                    loHead.treeify(tab);
            }
        }
        if (hiHead != null) {
            if (hc <= UNTREEIFY_THRESHOLD)
                tab[index + bit] = hiHead.untreeify(map);
            else {
                tab[index + bit] = hiHead;
                if (loHead != null)
                    hiHead.treeify(tab);
            }
        }
    }
```
