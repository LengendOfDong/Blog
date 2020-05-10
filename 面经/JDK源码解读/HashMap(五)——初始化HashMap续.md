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
```
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
     //表是空的话，先对表进行扩容
     if ((tab = table) == null || (n = tab.length) == 0)
         n = (tab = resize()).length;
     if ((p = tab[i = (n - 1) & hash]) == null)
         tab[i] = newNode(hash, key, value, null);
     else {
         Node<K,V> e; K k;
         if (p.hash == hash &&
             ((k = p.key) == key || (key != null && key.equals(k))))
             e = p;
         else if (p instanceof TreeNode)
             e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
         else {
             for (int binCount = 0; ; ++binCount) {
                 if ((e = p.next) == null) {
                     p.next = newNode(hash, key, value, null);
                     if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                         treeifyBin(tab, hash);
                     break;
                 }
                 if (e.hash == hash &&
                     ((k = e.key) == key || (key != null && key.equals(k))))
                     break;
                 p = e;
             }
         }
         if (e != null) { // existing mapping for key
             V oldValue = e.value;
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
