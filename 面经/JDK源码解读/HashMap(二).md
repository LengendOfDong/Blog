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
 * 因此，在以下情况下性能会正常降低hashCode（）方法的偶然或恶意使用返回分布不均的值，
 * 以及许多键共享一个hashCode，只要它们也可比。(如果以上两种情况均不适用，我们可能会浪费约与不使用时相比，
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
 * 0:    0.60653066
 * 1:    0.30326533
 * 2:    0.07581633
 * 3:    0.01263606
 * 4:    0.00157952
 * 5:    0.00015795
 * 6:    0.00001316
 * 7:    0.00000094
 * 8:    0.00000006
 * more: less than 1 in ten million
 *
 * The root of a tree bin is normally its first node.  However,
 * sometimes (currently only upon Iterator.remove), the root might
 * be elsewhere, but can be recovered following parent links
 * (method TreeNode.root()).
 *
 * All applicable internal methods accept a hash code as an
 * argument (as normally supplied from a public method), allowing
 * them to call each other without recomputing user hashCodes.
 * Most internal methods also accept a "tab" argument, that is
 * normally the current table, but may be a new or old one when
 * resizing or converting.
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
 * The use and transitions among plain vs tree modes is
 * complicated by the existence of subclass LinkedHashMap. See
 * below for hook methods defined to be invoked upon insertion,
 * removal and access that allow LinkedHashMap internals to
 * otherwise remain independent of these mechanics. (This also
 * requires that a map instance be passed to some utility methods
 * that may create new nodes.)
 *
 * The concurrent-programming-like SSA-based coding style helps
 * avoid aliasing errors amid all of the twisty pointer operations.
 */
```
