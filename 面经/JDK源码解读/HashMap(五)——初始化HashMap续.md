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
             //如果是LinkedHashMap还需要进行后续操作
             afterNodeAccess(e);
             //返回原有的旧值（类似被新值挤出的值，作为返回值返回）
             return oldValue;
         }
     }
     //这个主要在生成迭代器的时候使用，记录HashMap结构变更的次数
     ++modCount;
     //判断当前的大小增加1，并与扩容阈值进行判断，如果大于阈值则进行扩容操作
     if (++size > threshold)
         resize();
     //如果是LinkedHashMap还需要在节点插入之后进行其他操作
     afterNodeInsertion(evict);
     return null;
 }
```

在初始化过程中，如果链表长度为8的时候，开始将链表转换成红黑树，之后插入新的节点时，需要调用红黑树节点插入方法：putTreeVal
```java
/**
  * Tree version of putVal.
  * 红黑树版本的插入值
  */
 final TreeNode<K,V> putTreeVal(HashMap<K,V> map, Node<K,V>[] tab,
                                int h, K k, V v) {
     Class<?> kc = null;
     boolean searched = false;
     //如果父节点不为空，则获取父节点作为根节点，否则自己作为根节点
     TreeNode<K,V> root = (parent != null) ? root() : this;
     for (TreeNode<K,V> p = root;;) {
         int dir, ph; K pk;
         //节点的hash值大于h表明在p节点的右边
         if ((ph = p.hash) > h)
             dir = -1;
         //节点的hash值小于h表明在p节点的左边
         else if (ph < h)
             dir = 1;
         //是否为同一个key，是的话直接返回
         else if ((pk = p.key) == k || (k != null && k.equals(pk)))
             return p;
         //hash值相同但key不同，此时需要判断
         /*要进入下面这个else if,代表有以下几个含义:
           1、当前节点与待插入节点　key不同,　hash 值相同
      　　　2、ｋ是不可比较的，即ｋ并未实现comparable<K>接口（若 k 实现了comparable<K>　接口，comparableClassFor（k）返回的是ｋ的　class,而不是　null）或者　compareComparables(kc, k, pk)　返回值为 0(pk 为空　或者　按照 k.compareTo(pk) 返回值为0，
返回值为0可能是由于ｋ的compareTo 方法实现不当引起的)*/
         else if ((kc == null &&
                   (kc = comparableClassFor(k)) == null) ||
                  (dir = compareComparables(kc, k, pk)) == 0) {
             //在以当前节点为根的整个树上搜索是否存在待插入节点（只会搜索一次）
             //在它的左子树和右子树中遍历寻找。
             if (!searched) {
                 TreeNode<K,V> q, ch;
                 searched = true;
                 if (((ch = p.left) != null &&
                      (q = ch.find(h, k, kc)) != null) ||
                     ((ch = p.right) != null &&
                      (q = ch.find(h, k, kc)) != null))
                     return q;
             }
             // 既然ｋ是不可比较的，那我自己指定一个比较方式
             dir = tieBreakOrder(k, pk);
         }

         TreeNode<K,V> xp = p;
         //dir小于等于0说明是应该放在p节点的左边，dir大于0说明应该放在p节点的右边
         if ((p = (dir <= 0) ? p.left : p.right) == null) {
             Node<K,V> xpn = xp.next;
             //新建节点
             TreeNode<K,V> x = map.newTreeNode(h, k, v, xpn);
             //根据dir的正负，判断是应该将新增节点接在左边还是右边。
             if (dir <= 0)
                 xp.left = x;
             else
                 xp.right = x;
             //更新xp的属性，将xp节点指向x节点，使xp成为x的父节点
             xp.next = x;
             x.parent = x.prev = xp;
             //如果原本p节点下有节点，需要将此节点接在新增节点下
             if (xpn != null)
                 ((TreeNode<K,V>)xpn).prev = x;
             //插入节点后进行二叉树的平衡操作
             moveRootToFront(tab, balanceInsertion(root, x));
             return null;
         }
     }
 }
 
//当节点有相同的hashCode时，但是没有实现comparable接口，或者实现的有问题，需要
//使用内存地址来进行比较。
static int tieBreakOrder(Object a, Object b) {
     int d;
     //System.identityHashCode()实际是利用对象 a,b 的内存地址进行比较
     if (a == null || b == null ||
         (d = a.getClass().getName().
          compareTo(b.getClass().getName())) == 0)
         d = (System.identityHashCode(a) <= System.identityHashCode(b) ?
              -1 : 1);
     return d;
 }
```


