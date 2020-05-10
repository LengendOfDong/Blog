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
        if (table == null) { // pre-size
            float ft = ((float)s / loadFactor) + 1.0F;
            int t = ((ft < (float)MAXIMUM_CAPACITY) ?
                     (int)ft : MAXIMUM_CAPACITY);
            if (t > threshold)
                threshold = tableSizeFor(t);
        }
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
