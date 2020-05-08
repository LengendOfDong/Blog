
```java
 * Hash table based implementation of the <tt>Map</tt> interface.  This
 * implementation provides all of the optional map operations, and permits
 * <tt>null</tt> values and the <tt>null</tt> key.  (The <tt>HashMap</tt>
 * class is roughly equivalent to <tt>Hashtable</tt>, except that it is
 * unsynchronized and permits nulls.)  This class makes no guarantees as to
 * the order of the map; in particular, it does not guarantee that the order
 * will remain constant over time.
 *  
 * Map接口的Hash表实现。这个实现提供了所有的map可选操作，并允许null作为值和null作为key。
 * HashMap类大致等同于Hashtable，除了它是非同步的且允许为null。这个类不保证map的顺序。
 * 尤其是，它不保证顺序总保持一成不变的。
 *
 * <p>This implementation provides constant-time performance for the basic
 * operations (<tt>get</tt> and <tt>put</tt>), assuming the hash function
 * disperses the elements properly among the buckets.  Iteration over
 * collection views requires time proportional to the "capacity" of the
 * <tt>HashMap</tt> instance (the number of buckets) plus its size (the number
 * of key-value mappings).  Thus, it's very important not to set the initial
 * capacity too high (or the load factor too low) if iteration performance is
 * important.
 * 
 * 这个实现提供了常数时间性能的基本操作(O(1）get和put）,假设使用函数恰当地分散元素到各个桶中。
 * 集合视图上的迭代所需的时间与HashMap实例的“容量”（存储桶数）及其大小（键值映射的数量）成正比。
 * 因此，如果很重视迭代性能，就不要把初始值设置得很高（或者设置加载因子太低），这是非常重要的。
 *
 * <p>An instance of <tt>HashMap</tt> has two parameters that affect its
 * performance: <i>initial capacity</i> and <i>load factor</i>.  The
 * <i>capacity</i> is the number of buckets in the hash table, and the initial
 * capacity is simply the capacity at the time the hash table is created.  The
 * <i>load factor</i> is a measure of how full the hash table is allowed to
 * get before its capacity is automatically increased.  When the number of
 * entries in the hash table exceeds the product of the load factor and the
 * current capacity, the hash table is <i>rehashed</i> (that is, internal data
 * structures are rebuilt) so that the hash table has approximately twice the
 * number of buckets.
 *
 * HashMap的实例有两个参数来影响它的性能：初始值和加载因子。容量是指hash表中的桶的数量，初始容量
 * 仅仅指hash表被创建时的容量。加载因子是hash表在它的容量自增之前它能够达到多满的测量指标。当hash
 * 表中的条目数超出了加载因子和当前容量的乘积，hash表将会被重新hash。（也就是说，内部数据结构将被重构）
 * 这样hash表有大约两倍的桶。
 *
 * <p>As a general rule, the default load factor (.75) offers a good
 * tradeoff between time and space costs.  Higher values decrease the
 * space overhead but increase the lookup cost (reflected in most of
 * the operations of the <tt>HashMap</tt> class, including
 * <tt>get</tt> and <tt>put</tt>).  The expected number of entries in
 * the map and its load factor should be taken into account when
 * setting its initial capacity, so as to minimize the number of
 * rehash operations.  If the initial capacity is greater than the
 * maximum number of entries divided by the load factor, no rehash
 * operations will ever occur.
 * 
 * 通常，默认负载因子（.75）在时间和空间成本之间提供了一个很好的折衷方案。
 * 较高的值会降低空间开销，但增加了查找成本（反映在HashMap类的大多数操作中，包括get和put）。 
 * 设置映射表的初始容量时，应考虑映射中的预期条目数及其负载因子，以最大程度地减少重新哈希操作的数量。 
 * 如果初始容量大于最大条目数除以负载因子，则将不会进行任何哈希操作。
 * 
 * <p>If many mappings are to be stored in a <tt>HashMap</tt>
 * instance, creating it with a sufficiently large capacity will allow
 * the mappings to be stored more efficiently than letting it perform
 * automatic rehashing as needed to grow the table.  Note that using
 * many keys with the same {@code hashCode()} is a sure way to slow
 * down performance of any hash table. To ameliorate impact, when keys
 * are {@link Comparable}, this class may use comparison order among
 * keys to help break ties.
 * 
 * 如果要在HashMap中存储许多映射，例如，以足够大的容量创建该表将比使表根据增长表的
 * 需要进行自动重新哈希处理更有效地存储映射。 请注意，使用许多具有相同hashCode的键会明确
 * 降低任何哈希表性能。 为了改善影响，当键为可比较时，此类可以使用键之间的比较顺序来帮助打破平局
 * 
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access a hash map concurrently, and at least one of
 * the threads modifies the map structurally, it <i>must</i> be
 * synchronized externally.  (A structural modification is any operation
 * that adds or deletes one or more mappings; merely changing the value
 * associated with a key that an instance already contains is not a
 * structural modification.)  This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the map.
 *
 * 请注意，这个实现是非同步的。如果多线程同时获取hashMap,并且至少一个线程修改了map的结构，
 * 它必须在外部同步化。（结构化修改是指那些增加或删除一个或多个映射的操作，仅仅改动实例已经
 * 包含的关联key的值不是一个结构化修改。) 通常，通过在自然封装map的某个对象上进行同步来实现。
 * 
 * If no such object exists, the map should be "wrapped" using the
 * {@link Collections#synchronizedMap Collections.synchronizedMap}
 * method.  This is best done at creation time, to prevent accidental
 * unsynchronized access to the map:<pre>
 *   Map m = Collections.synchronizedMap(new HashMap(...));</pre>
 * 
 * 如果没有这个对象存在，map就应该用Collections的synchronizedMap方法进行”包装“。
 * 最好在创建时完成，以防止意外的非同步访问map。
 * "Map m = Collections.synchronizedMap(new HashMap(...));"
 *
 * <p>The iterators returned by all of this class's "collection view methods"
 * are <i>fail-fast</i>: if the map is structurally modified at any time after
 * the iterator is created, in any way except through the iterator's own
 * <tt>remove</tt> method, the iterator will throw a
 * {@link ConcurrentModificationException}.  Thus, in the face of concurrent
 * modification, the iterator fails quickly and cleanly, rather than risking
 * arbitrary, non-deterministic behavior at an undetermined time in the
 * future.
 *
 * 如果在创建迭代器之后的任何时间对结构进行结构修改，则通过该类的所有“集合视图方法”返回的迭代器
 * 都是快速失败的，除非通过迭代器自己的remove方法，否则迭代器将抛出{@ 链接ConcurrentModificationException}。 
 * 因此，面对并发修改，迭代器会快速干净地失败，而不会在未来的不确定时间内冒任意，不确定的行为的风险。
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw <tt>ConcurrentModificationException</tt> on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness: <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i>
 *
 * 请注意，迭代器的快速失败行为无法得到保证，因为通常来说，在存在不同步的并发修改的情况下，
 * 不可能做出任何严格的保证。 快速迭代器尽最大努力抛出<tt> ConcurrentModificationException。
 * 因此，编写依赖于此异常的程序的正确性是错误的：迭代器的快速失败行为应该仅用于检测错误。
 * 
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 *
 * @author  Doug Lea
 * @author  Josh Bloch
 * @author  Arthur van Hoff
 * @author  Neal Gafter
 * @see     Object#hashCode()
 * @see     Collection
 * @see     Map
 * @see     TreeMap
 * @see     Hashtable
 * @since   1.2
 */
```
