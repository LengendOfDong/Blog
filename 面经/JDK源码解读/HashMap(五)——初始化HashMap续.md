# HashMap初始化
HashMap初始化的第三个方法：
```java
 /**
    * Constructs a new <tt>HashMap</tt> with the same mappings as the
    * specified <tt>Map</tt>.  The <tt>HashMap</tt> is created with
    * default load factor (0.75) and an initial capacity sufficient to
    * hold the mappings in the specified <tt>Map</tt>.
    * 
    * @param   m the map whose mappings are to be placed in this map
    * @throws  NullPointerException if the specified map is null
    */
    public HashMap(Map<? extends K, ? extends V> m) {
    this.loadFactor = DEFAULT_LOAD_FACTOR;
    putMapEntries(m, false);
    }
```

最主要的就是putMapEntries方法：
```java
/**
 * Implements Map.putAll and Map constructor
 *
 * 实现Map.putAll和 Map构造器
 *
 * @param m the map
 * @param evict false when initially constructing this map, else
 * true (relayed to method afterNodeInsertion).
 */
final void putMapEntries(Map<? extends K, ? extends V> m, boolean evict) {
    int s = m.size();
    if (s > 0) {
        //如果表一开始是空的
        if (table == null) { // pre-size
            //原map的大小除以加载因子并加上1
            float ft = ((float)s / loadFactor) + 1.0F;
            int t = ((ft < (float)MAXIMUM_CAPACITY) ?
                     (int)ft : MAXIMUM_CAPACITY);
            //根据t来获取不小于t且最接近t的2的幂次，threshold在后续增加节点的过程中还会变化。
            if (t > threshold)
                threshold = tableSizeFor(t);
        }
        //如果表一开始已经初始化完毕，不是空的，则将原map的size和当前扩容阈值进行比较，如果超出阈值
        //则需要扩容
        else if (s > threshold)
            resize();
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            K key = e.getKey();
            V value = e.getValue();
            //将原有的map中的值都放入到新的map中
            putVal(hash(key), key, value, false, evict);
        }
    }
}
```

循环调用putVal方法，主要是将map的值放入到新的map中，这个方法也是Map.put的核心。
```java
/**
  * Implements Map.put and related methods
  *
  * @param hash hash for key
  * @param key the key
  * @param value the value to put
  * @param onlyIfAbsent if true, don't change existing value
  * @param evict if false, the table is in creation mode.
  * @return previous value, or null if none
  */
 final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                boolean evict) {
     Node<K,V>[] tab; Node<K,V> p; int n, i;
     //表是空的话，先对表进行扩容，初始化时n代表扩容之后的长度
     if ((tab = table) == null || (n = tab.length) == 0)
         n = (tab = resize()).length;
     //值对表长进行取模，此处由于n是2的幂次，所以**(n-1)&hash**与**(hash % n)**等价，
     //如果为空表示此处没有值，则新建一个节点
     if ((p = tab[i = (n - 1) & hash]) == null)
         tab[i] = newNode(hash, key, value, null);
     //如果不为空则说明此位置已经有元素，需要进行处理
     else {
         Node<K,V> e; K k;
         //取模后的hash和传入的hash相同并且key值要么地址相同，要么值相同，则说明在同一个位置
         //将此位置的节点指针给取出赋值给e
         if (p.hash == hash &&
             ((k = p.key) == key || (key != null && key.equals(k))))
             e = p;
         //若原来元素是红黑树节点，调用红黑树的插入方法:putTreeVal
         else if (p instanceof TreeNode)
             e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
         //此时使用链表进行处理，从此节点开始向后寻找合适插入位置
         else {
             //对这个桶数量进行计算
             for (int binCount = 0; ; ++binCount) {
                 //p节点的后面是空的，表明p节点为尾节点，在其后面追加新的节点
                 if ((e = p.next) == null) {
                     //将p指向新的节点，形成新的链表
                     p.next = newNode(hash, key, value, null);
                     //可以看到TREEIFY_THRESHOLD的默认值为8，表明链表长度为8时，
                     //会调用treeifyBin方法将整个表转换成红黑树形式
                     if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                         treeifyBin(tab, hash);
                     break;
                 }
                 //遍历过程中发现链表中有节点和当前节点一样，即hash相同，key也相同
                 if (e.hash == hash &&
                     ((k = e.key) == key || (key != null && key.equals(k))))
                     break;
                 //不断循环，binCount应该最多为7,后面链表就转换成红黑树了，直到找到相同节点
                 //或者在链表的尾部新增节点
                 p = e;
             }
         }
         //存在key的映射，要么之前已经存在，要么已经创建红黑树节点或者链表节点
         if (e != null) { // existing mapping for key
             V oldValue = e.value;
             //判断e的值，onlyIfAbsent表示仅当缺失的时候，此标识只对putIfAbsent生效,
             //只有旧值为空的时候才进行覆盖,其他情况都是直接进行覆盖
             if (!onlyIfAbsent || oldValue == null)
                 e.value = value;
             afterNodeAccess(e);
             return oldValue;
         }
     }
     ++modCount;
     if (++size > threshold)
         resize();
     afterNodeInsertion(evict);
     return null;
 }
```

调用红黑树节点插入方法：putTreeVal
```java
/**
  * Tree version of putVal.
  */
 final TreeNode<K,V> putTreeVal(HashMap<K,V> map, Node<K,V>[] tab,
                                int h, K k, V v) {
     Class<?> kc = null;
     boolean searched = false;
     TreeNode<K,V> root = (parent != null) ? root() : this;
     for (TreeNode<K,V> p = root;;) {
         int dir, ph; K pk;
         if ((ph = p.hash) > h)
             dir = -1;
         else if (ph < h)
             dir = 1;
         else if ((pk = p.key) == k || (k != null && k.equals(pk)))
             return p;
         else if ((kc == null &&
                   (kc = comparableClassFor(k)) == null) ||
                  (dir = compareComparables(kc, k, pk)) == 0) {
             if (!searched) {
                 TreeNode<K,V> q, ch;
                 searched = true;
                 if (((ch = p.left) != null &&
                      (q = ch.find(h, k, kc)) != null) ||
                     ((ch = p.right) != null &&
                      (q = ch.find(h, k, kc)) != null))
                     return q;
             }
             dir = tieBreakOrder(k, pk);
         }

         TreeNode<K,V> xp = p;
         if ((p = (dir <= 0) ? p.left : p.right) == null) {
             Node<K,V> xpn = xp.next;
             TreeNode<K,V> x = map.newTreeNode(h, k, v, xpn);
             if (dir <= 0)
                 xp.left = x;
             else
                 xp.right = x;
             xp.next = x;
             x.parent = x.prev = xp;
             if (xpn != null)
                 ((TreeNode<K,V>)xpn).prev = x;
             moveRootToFront(tab, balanceInsertion(root, x));
             return null;
         }
     }
 }
```
