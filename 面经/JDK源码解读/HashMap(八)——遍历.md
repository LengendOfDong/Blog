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
              int mc = modCount;
              for (int i = 0; i < tab.length; ++i) {
                  for (Node<K,V> e = tab[i]; e != null; e = e.next)
                      action.accept(e.key);
              }
              if (modCount != mc)
                  throw new ConcurrentModificationException();
          }
      }
  }
```
