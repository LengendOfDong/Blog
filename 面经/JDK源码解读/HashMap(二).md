```java
 * Implementation notes.
 *
 * 实现说明
 *
 * This map usually acts as a binned (bucketed) hash table, but
 * when bins get too large, they are transformed into bins of
 * TreeNodes, each structured similarly to those in
 * java.util.TreeMap. Most methods try to use normal bins, but
 * relay to TreeNode methods when applicable (simply by checking
 * instanceof a node).  Bins of TreeNodes may be traversed and
 * used like any others, but additionally support faster lookup
 * when overpopulated. However, since the vast majority of bins in
 * normal use are not overpopulated, checking for existence of
 * tree bins may be delayed in the course of table methods.
 * 
 * 该映射通常用作装箱（存储桶）的哈希表，但当桶太大时，它们会转换为TreeNode，
 * 每个的结构都类似于java.util.TreeMap中的结构。 
 * 大多数方法尝试使用普通的桶，但是在适用时中继到TreeNode方法（只需通过检查节点的实例）。
 * TreeNodes的bin可以像其他任何遍历一样使用，但在数目众多时还支持更快的查找。
 * 但是，由于正常使用中的绝大多数桶都不是众多的，因此在使用表方法的过程中可能会延迟检查是否存在树节点。
 *
 * Tree bins (i.e., bins whose elements are all TreeNodes) are
 * ordered primarily by hashCode, but in the case of ties, if two
 * elements are of the same "class C implements Comparable<C>",
 * type then their compareTo method is used for ordering. (We
 * conservatively check generic types via reflection to validate
 * this -- see method comparableClassFor).  The added complexity
 * of tree bins is worthwhile in providing worst-case O(log n)
 * operations when keys either have distinct hashes or are
 * orderable, Thus, performance degrades gracefully under
 * accidental or malicious usages in which hashCode() methods
 * return values that are poorly distributed, as well as those in
 * which many keys share a hashCode, so long as they are also
 * Comparable. (If neither of these apply, we may waste about a
 * factor of two in time and space compared to taking no
 * precautions. But the only known cases stem from poor user
 * programming practices that are already so slow that this makes
 * little difference.)
 * 
 * 树形桶（即元素都是TreeNode的桶）主要由hashCode进行排序，但在并列的情况下，如果
 * 两个元素具有相同的”class C implements Comparable<C>“的情况，然后使用其compareTo
 * 方法进行排序。我们通过反射保守地检查泛型类型以进行验证这一点，参见方法comparableClassFor).
 * 增加的复杂性的树形桶是值得的，无论key具有不同哈希值或者是有序的，都提供最坏情况的O(logn),
 * 因此，hashCode（）方法的偶然或恶意使用返回分布不均的值，以及只要它们也可比，许多键共享一个hashCode，
 * 在这些情况下性能会通常降低。(如果以上两种情况均不适用，我们可能会浪费约与不使用时相比，
 * 时间和空间的两倍预防措施。 但是唯一已知的案例是由于用户不佳已经很慢的编程实践，这使得差别不大。）
 * 
 * Because TreeNodes are about twice the size of regular nodes, we
 * use them only when bins contain enough nodes to warrant use
 * (see TREEIFY_THRESHOLD). And when they become too small (due to
 * removal or resizing) they are converted back to plain bins.  In
 * usages with well-distributed user hashCodes, tree bins are
 * rarely used.  Ideally, under random hashCodes, the frequency of
 * nodes in bins follows a Poisson distribution
 * (http://en.wikipedia.org/wiki/Poisson_distribution) with a
 * parameter of about 0.5 on average for the default resizing
 * threshold of 0.75, although with a large variance because of
 * resizing granularity. Ignoring variance, the expected
 * occurrences of list size k are (exp(-0.5) * pow(0.5, k) /
 * factorial(k)). The first values are:
 * 
 * 因为树形节点是普通节点的两倍大小，所以只当树形桶包含足够的节点来保证使用时，我们才使用它们。（查看TREEIFY_THRESHOLD）
 * 并且当它们变得非常小（由于移除或者重组），它们被转变回普通的桶。在使用很好分布的hashCode的情况下，树形桶是很好使用的。
 * 理想情况下，在默认调整大小的阈值0.75的随机hashCode下，桶中节点遵循带有平均约为0.5的参数的泊松分布，尽管由于调整粒度而
 * 差异很大(http://en.wikipedia.org/wiki  /Poisson_distribution)。
 * 忽略掉差异，列表大小k的期望出现k 遵循 (exp(-0.5) * pow(0.5, k) / factorial(k))，第一个值是：
 * 
 * 0:    0.60653066
 * 1:    0.30326533
 * 2:    0.07581633
 * 3:    0.01263606
 * 4:    0.00157952
 * 5:    0.00015795
 * 6:    0.00001316
 * 7:    0.00000094
 * 8:    0.00000006
 * more: less than 1 in ten million  更多： 小于千万分之一
 *
 * The root of a tree bin is normally its first node.  However,
 * sometimes (currently only upon Iterator.remove), the root might
 * be elsewhere, but can be recovered following parent links
 * (method TreeNode.root()).
 *
 * 树形桶的根节点通常是它的第一个节点，然而，有时（通常仅发生在Iterator.remove）
 * 根节点也许会在别的什么地方，但都能够被紧接着的父连接给覆盖到。（使用TreeNode.root方法）
 * 
 * All applicable internal methods accept a hash code as an
 * argument (as normally supplied from a public method), allowing
 * them to call each other without recomputing user hashCodes.
 * Most internal methods also accept a "tab" argument, that is
 * normally the current table, but may be a new or old one when
 * resizing or converting.
 * 
 * 所有可使用的内部方法接收一个hash码作为一个参数（通常由公共方法提供），允许它们调用
 * 对方而不是重新计算hash码。大多数内部方法总接受一个”标签“参数，通常是当前表，但当重新调整
 * 大小或者转换时，可能是一个旧的或者新的表。
 * 
 * When bin lists are treeified, split, or untreeified, we keep
 * them in the same relative access/traversal order (i.e., field
 * Node.next) to better preserve locality, and to slightly
 * simplify handling of splits and traversals that invoke
 * iterator.remove. When using comparators on insertion, to keep a
 * total ordering (or as close as is required here) across
 * rebalancings, we compare classes and identityHashCodes as
 * tie-breakers.
 * 
 * 当桶列表被树形化，分割，或者未被树形化时，我们将它们保持在相同的相对访问/遍历顺序
 *（例如字段Node.next）中，以更好地保留局部性，并略微简化调用迭代器的拆分和遍历的处理
 *
 * The use and transitions among plain vs tree modes is
 * complicated by the existence of subclass LinkedHashMap. See
 * below for hook methods defined to be invoked upon insertion,
 * removal and access that allow LinkedHashMap internals to
 * otherwise remain independent of these mechanics. (This also
 * requires that a map instance be passed to some utility methods
 * that may create new nodes.)
 *
 * 子类LinkedHashMap的存在使普通模式与树模式之间的使用和转换变得复杂。 请参阅下面的定义为在插入，
 * 删除和访问时调用的钩子方法，这些挂钩方法使LinkedHashMap内部能够以其他方式独立于这些机制。 
 *（这还要求将地图实例传递给一些可能创建新节点的实用程序方法。）
 *
 * The concurrent-programming-like SSA-based coding style helps
 * avoid aliasing errors amid all of the twisty pointer operations.
 * 
 * 类似于并发编程的基于SSA的编码样式有助于 避免在所有曲折的指针操作中出现混叠错误。
 */
```
