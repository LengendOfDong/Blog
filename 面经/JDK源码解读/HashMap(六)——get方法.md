# Get方法
get方法的key可以是null,返回值为null并不一定代表map中没有映射，也有可能映射的键就是null。在map中key和value是可以同时都为null的。
```java
/**
 * Returns the value to which the specified key is mapped,
 * or {@code null} if this map contains no mapping for the key.
 * 
 * 返回指定key映射的相应的值或者这个Map不包含key对应的映射。
 *
 * <p>More formally, if this map contains a mapping from a key
 * {@code k} to a value {@code v} such that {@code (key==null ? k==null :
 * key.equals(k))}, then this method returns {@code v}; otherwise
 * it returns {@code null}.  (There can be at most one such mapping.)
 *
 * 更正式地讲，如果此映射包含从键{@code k}到值{@code v}的映射，使得{@code
 * （key == null？k == null：key.equals（k））}，
 * 然后此方法返回{@code v}; 否则返回{@code null}。 （最多可以有一个这样的映射。）
 *
 * <p>A return value of {@code null} does not <i>necessarily</i>
 * indicate that the map contains no mapping for the key; it's also
 * possible that the map explicitly maps the key to {@code null}.
 * The {@link #containsKey containsKey} operation may be used to
 * distinguish these two cases.
 * 
 * 返回值{@code null}不一定<i>不必</ i>表示映射不包含该键的映射。 
 * 映射也可能将键显式映射到{@code null}。 {@link #containsKey containsKey}
 * 操作可用于区分这两种情况。
 * @see #put(Object, Object)
 */
public V get(Object key) {
    Node<K,V> e;
    return (e = getNode(hash(key), key)) == null ? null : e.value;
}
```

get方法调用getNode方法进行判断：
```java
/**
 * Implements Map.get and related methods
 *
 * @param hash hash for key
 * @param key the key
 * @return the node, or null if none
 */
final Node<K,V> getNode(int hash, Object key) {
    Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (first = tab[(n - 1) & hash]) != null) {
        if (first.hash == hash && // always check first node
            ((k = first.key) == key || (key != null && key.equals(k))))
            return first;
        if ((e = first.next) != null) {
            if (first instanceof TreeNode)
                return ((TreeNode<K,V>)first).getTreeNode(hash, key);
            do {
                if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k))))
                    return e;
            } while ((e = e.next) != null);
        }
    }
    return null;
}
```
