# KeySet
keySet方法用来获取key的集合
```java
/**
   * Returns a {@link Set} view of the keys contained in this map.
   * The set is backed by the map, so changes to the map are
   * reflected in the set, and vice-versa.  If the map is modified
   * while an iteration over the set is in progress (except through
   * the iterator's own <tt>remove</tt> operation), the results of
   * the iteration are undefined.  The set supports element removal,
   * which removes the corresponding mapping from the map, via the
   * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
   * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt>
   * operations.  It does not support the <tt>add</tt> or <tt>addAll</tt>
   * operations.
   *
   * 返回这个map中包含的所有key的集合视图。由于该集合由map支持，所以对于map的修改都会
   * 反映在这个集合中，反之亦然。如果在对集合进行迭代时修改了map（通过迭代器自己的remove
   * 操作除外），则迭代结果不确定。该集合支持元素删除，通过Iterator.remove,Set.remove
   * removeAll,retainAll和clear操作。但它不支持add和addAll操作。
   * 
   * @return a set view of the keys contained in this map
   */
  public Set<K> keySet() {
      Set<K> ks = keySet;
      if (ks == null) {
          ks = new KeySet();
          keySet = ks;
      }
      return ks;
  }

  final class KeySet extends AbstractSet<K> {
      public final int size()                 { return size; }
      public final void clear()               { HashMap.this.clear(); }
      public final Iterator<K> iterator()     { return new KeyIterator(); }
      public final boolean contains(Object o) { return containsKey(o); }
      public final boolean remove(Object key) {
          return removeNode(hash(key), key, null, false, true) != null;
      }
      public final Spliterator<K> spliterator() {
          return new KeySpliterator<>(HashMap.this, 0, -1, 0, 0);
      }
      public final void forEach(Consumer<? super K> action) {
          Node<K,V>[] tab;
          if (action == null)
              throw new NullPointerException();
          if (size > 0 && (tab = table) != null) {
              //用于记录修改
              int mc = modCount;
              for (int i = 0; i < tab.length; ++i) {
                  //遍历每一条目，条目的起点是数组上的节点
                  for (Node<K,V> e = tab[i]; e != null; e = e.next)
                      action.accept(e.key);
              }
              //modCount和之前的值不同，说明进行了修改
              if (modCount != mc)
                  throw new ConcurrentModificationException();
          }
      }
  }
  
  /**
     * The number of times this HashMap has been structurally modified
     * Structural modifications are those that change the number of mappings in
     * the HashMap or otherwise modify its internal structure (e.g.,
     * rehash).  This field is used to make iterators on Collection-views of
     * the HashMap fail-fast.  (See ConcurrentModificationException).
     * 
     * HashMap被结构化修改的次数，结构化修改是指改变HashMap映射数量的那些操作，或者修改它的内部结构
     * （比如rehash）。这个属性被用来使那些HashMap的集合视图快速失败的。
     */
    transient int modCount;
```

为了测试下keySet，自己写个类试试：
```java
public class MyIterator {
    public static void main(String[] args){
        Map  map = new HashMap();
        map.put(1, 2);
        map.put(2,3);
        map.put(3,4);

        Set set = map.keySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
            map.remove(1);
        }
    }
}
```
我在迭代的过程中故意使用map.remove方法进行元素的移除，之后果然就报错了：
```java
Exception in thread "main" java.util.ConcurrentModificationException
1
	at java.util.HashMap$HashIterator.nextNode(HashMap.java:1442)
	at java.util.HashMap$KeyIterator.next(HashMap.java:1466)
	at com.zte.MyIterator.main(MyIterator.java:22)
```

跟着报错，看下KeyIterator的next()方法和HashIterator的nextNode方法，首先是KeyIterator的next()
```java
final class KeyIterator extends HashIterator implements Iterator<K> {
        public final K next() { return nextNode().key; }
    }
```
这个主要是调用HashIterator的nextNode()的方法，如下是HashIterator的nextNode()的方法
```java
HashIterator() {
      expectedModCount = modCount;
      Node<K,V>[] t = table;
      current = next = null;
      index = 0;
      if (t != null && size > 0) { // advance to first entry
          do {} while (index < t.length && (next = t[index++]) == null);
      }
  }
final Node<K,V> nextNode() {
            Node<K,V>[] t;
            Node<K,V> e = next;
            //HashIterator在初始化时，已经将modCount的值赋值给expectedModCount
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            if (e == null)
                throw new NoSuchElementException();
            if ((next = (current = e).next) == null && (t = table) != null) {
                do {} while (index < t.length && (next = t[index++]) == null);
            }
            return e;
        }
```
modCount这个值是用来判断有没有进行增删改这样的操作的，只要进行了这样的操作，modCount就会自增1。在测试代码中进行了
**map.remove()** 的操作，modCount自然发生了变化，所以抛出了**ConcurrentModificationException** 异常。

