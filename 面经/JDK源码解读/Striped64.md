# Striped64

## 源码：

```java
package java.util.concurrent.atomic;
import java.util.function.LongBinaryOperator;
import java.util.function.DoubleBinaryOperator;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 这个类是为了支持64位值的动态条纹而设计的。这个类是包私有的，意味着它只能在同一个包中被访问。
 * 这个类继承了Number类，这意味着任何具体的子类也必须公开地继承这个类。
 */
@SuppressWarnings("serial")
abstract class Striped64 extends Number {
    /*
     * This class maintains a lazily-initialized table of atomically
     * updated variables, plus an extra "base" field. The table size
     * is a power of two. Indexing uses masked per-thread hash codes.
     * Nearly all declarations in this class are package-private,
     * accessed directly by subclasses.
     * 此类维护一个延迟初始化的原子更新变量表，以及一个额外的“基本”字段。表大小是 2 的幂。
     * 索引使用屏蔽的每线程哈希代码。此类中的几乎所有声明都是包私有的，可由子类直接访问。
     * Table entries are of class Cell; a variant of AtomicLong padded
     * (via @sun.misc.Contended) to reduce cache contention. Padding
     * is overkill for most Atomics because they are usually
     * irregularly scattered in memory and thus don't interfere much
     * with each other. But Atomic objects residing in arrays will
     * tend to be placed adjacent to each other, and so will most
     * often share cache lines (with a huge negative performance
     * impact) without this precaution.
     * 表条目属于 Cell 类;填充 AtomicLong 的变体（通过 @sun.misc.Contended）以减少缓存争用。
     * 对于大多数原子来说，填充是矫枉过正的，因为它们通常不规则地分散在内存中，因此彼此之间不会有
     * 太大的干扰。但是，驻留在数组中的原子对象往往会彼此相邻放置，因此在没有这种预防措施的情况下，
     * 通常会共享缓存行（对性能产生巨大的负面影响）。
     * In part because Cells are relatively large, we avoid creating
     * them until they are needed.  When there is no contention, all
     * updates are made to the base field.  Upon first contention (a
     * failed CAS on base update), the table is initialized to size 2.
     * The table size is doubled upon further contention until
     * reaching the nearest power of two greater than or equal to the
     * number of CPUS. Table slots remain empty (null) until they are
     * needed.
     * 在某种程度上，由于单元格相对较大，因此在需要它们之前，我们会避免创建它们。当没有争用时，
     * 将对基本字段进行所有更新。在第一次争用（基本更新的 CAS 失败）时，表将初始化为大小2。
     * 在进一步争用时，表大小将加倍，直到达到大于或等于 CPU 数量的 2 的最接近的幂。表插槽保持
     * 空 （null），直到需要它们为止。
     * A single spinlock ("cellsBusy") is used for initializing and
     * resizing the table, as well as populating slots with new Cells.
     * There is no need for a blocking lock; when the lock is not
     * available, threads try other slots (or the base).  During these
     * retries, there is increased contention and reduced locality,
     * which is still better than alternatives.
     * 单个自旋锁（“cellsBusy”）用于初始化和调整表的大小，以及用新单元填充槽。不需要阻塞锁;
     * 当锁不可用时，线程会尝试其他插槽（或底座）。在这些重试期间，争用增加，局部性减少，这仍然优于替代方案。
     * The Thread probe fields maintained via ThreadLocalRandom serve
     * as per-thread hash codes. We let them remain uninitialized as
     * zero (if they come in this way) until they contend at slot
     * 0. They are then initialized to values that typically do not
     * often conflict with others.  Contention and/or table collisions
     * are indicated by failed CASes when performing an update
     * operation. Upon a collision, if the table size is less than
     * the capacity, it is doubled in size unless some other thread
     * holds the lock. If a hashed slot is empty, and lock is
     * available, a new Cell is created. Otherwise, if the slot
     * exists, a CAS is tried.  Retries proceed by "double hashing",
     * using a secondary hash (Marsaglia XorShift) to try to find a
     * free slot.
     * 通过 ThreadLocalRandom 维护的 Thread 探测字段用作每个线程的哈希代码。我们让它们保持未初始化
     * 为零（如果它们以这种方式出现），直到它们在插槽 0 处争用。然后，它们被初始化为通常不会经常与其他值
     * 冲突的值。执行更新操作时，失败的 CAS 指示争用和/或表冲突。发生碰撞时，如果表大小小于容量，则其
     * 大小将增加一倍，除非其他线程保持锁定。如果散列插槽为空，并且锁定可用，则创建一个新单元。否则，
     * 如果插槽存在，则尝试 CAS。重试通过“双重哈希”进行，使用辅助哈希 （Marsaglia XorShift） 尝试
     * 查找空闲插槽。
     * The table size is capped because, when there are more threads
     * than CPUs, supposing that each thread were bound to a CPU,
     * there would exist a perfect hash function mapping threads to
     * slots that eliminates collisions. When we reach capacity, we
     * search for this mapping by randomly varying the hash codes of
     * colliding threads.  Because search is random, and collisions
     * only become known via CAS failures, convergence can be slow,
     * and because threads are typically not bound to CPUS forever,
     * may not occur at all. However, despite these limitations,
     * observed contention rates are typically low in these cases.
     * 表大小是有上限的，因为当线程数多于 CPU 时，假设每个线程都绑定到一个 CPU，则将存在一个完美的哈希函数，
     * 将线程映射到插槽，从而消除冲突。当我们达到容量时，我们通过随机改变冲突线程的哈希码来搜索此映射。
     * 由于搜索是随机的，并且只有通过 CAS 故障才能知道冲突，因此收敛速度可能很慢，并且由于线程通常不会
     * 永远绑定到 CPU，因此可能根本不会发生。然而，尽管存在这些限制，但在这些情况下观察到的争用率通常较低。
     * It is possible for a Cell to become unused when threads that
     * once hashed to it terminate, as well as in the case where
     * doubling the table causes no thread to hash to it under
     * expanded mask.  We do not try to detect or remove such cells,
     * under the assumption that for long-running instances, observed
     * contention levels will recur, so the cells will eventually be
     * needed again; and for short-lived ones, it does not matter.
     * 当曾经对 Cell 进行哈希处理的线程终止时，以及将表加倍导致没有线程在扩展掩码下对其进行哈希处理的情况，
     * 单元可能会变得未使用。我们不会尝试检测或删除此类单元，前提是对于长时间运行的实例，观察到的争用级别
     * 将再次出现，因此最终将再次需要这些单元;而对于短命的，也没关系。
     */

	//初始化Cell类
    @sun.misc.Contended static final class Cell {
        volatile long value;
        Cell(long x) { value = x; }
        final boolean cas(long cmp, long val) {
            return UNSAFE.compareAndSwapLong(this, valueOffset, cmp, val);
        }

        // Unsafe mechanics
        private static final sun.misc.Unsafe UNSAFE;
        private static final long valueOffset;
        static {
            try {
                UNSAFE = sun.misc.Unsafe.getUnsafe();
                Class<?> ak = Cell.class;
                valueOffset = UNSAFE.objectFieldOffset
                    (ak.getDeclaredField("value"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    //CPU的数量，用来绑定表的大小
    static final int NCPU = Runtime.getRuntime().availableProcessors();

    /**
     * Cell表。当不是null时，size 是 2 的幂。
     */
    transient volatile Cell[] cells;

    /**
 	 * 基值，主要在没有争用时使用，但也在表初始化争用期间用作回退。通过 CAS 更新。
     */
    transient volatile long base;

    /**
     * 调整大小和/或创建单元格时使用的旋转锁（通过 CAS 锁定）。
     */
    transient volatile int cellsBusy;

    /**
     * 包私有构造器
     */
    Striped64() {
    }

    /**
     * 对基值进行CAS操作
     */
    final boolean casBase(long cmp, long val) {
        return UNSAFE.compareAndSwapLong(this, BASE, cmp, val);
    }

    /**
     * 将 cellsBusy 字段从 0 CAS 到 1 以获取锁定。
     */
    final boolean casCellsBusy() {
        return UNSAFE.compareAndSwapInt(this, CELLSBUSY, 0, 1);
    }

    /**
     * 返回当前线程的探测值。由于包的限制，从 ThreadLocalRandom 复制。
     */
    static final int getProbe() {
        return UNSAFE.getInt(Thread.currentThread(), PROBE);
    }

    /**
     *  Xorshift 算法生成一个新的随机数赋值给probe。由于包限制，从 ThreadLocalRandom 复制。
     */
    static final int advanceProbe(int probe) {
        probe ^= probe << 13;   // xorshift
        probe ^= probe >>> 17;
        probe ^= probe << 5;
        UNSAFE.putInt(Thread.currentThread(), PROBE, probe);
        return probe;
    }

    /**
     * 处理涉及初始化、调整大小、创建新单元和/或争用的更新情况。这种方法存
     * 在乐观重试代码的常见非模块化问题，依赖于重新检查的读取集。
     */
    final void longAccumulate(long x, LongBinaryOperator fn,
                              boolean wasUncontended) {
        int h;
        //当前线程的探测值为0时，需要对线程进行初始化
        if ((h = getProbe()) == 0) {
            ThreadLocalRandom.current(); // force initialization
            h = getProbe();
            wasUncontended = true;
        }
        boolean collide = false;                // True if last slot nonempty
        for (;;) {
            Cell[] as; Cell a; int n; long v;
            if ((as = cells) != null && (n = as.length) > 0) {
                //(n - 1) & h这个在hashmap的源码中也有，类似与hash % n,哈希后在数组中的位置
                //注：这段if中的代码是在尝试安全地在一个并发数据结构中添加一个新的元素（Cell）。如果添加成功，则退出循环；如果添加失败（即数据结构已经被其他线程修改），则继续下一次循环。
                if ((a = as[(n - 1) & h]) == null) {
                    //是否可以安全修改数据，Cell对象没有竞争
                    if (cellsBusy == 0) {       // Try to attach new Cell
                        //创建一个新的Cell对象，并将其引用赋值给变量r
                        Cell r = new Cell(x);   // Optimistically create
                        //再次检查是否有竞争，使用CAS操作来更新cellsBusy字段从0到1以获取锁定
                        if (cellsBusy == 0 && casCellsBusy()) {
                            //用来标记是否成功地创建了新的Cell对象
                            boolean created = false;
                            try {               // Recheck under lock
                                Cell[] rs; int m, j;
                                //Cell数组不为空，并且数组的长度大于0，检查索引处是否为空
                                if ((rs = cells) != null &&
                                    (m = rs.length) > 0 &&
                                    rs[j = (m - 1) & h] == null) {
                                    //将新创建的Cell对象都放入数组指定位置
                                    rs[j] = r;
                                    //成功创建了新的Cell对象
                                    created = true;
                                }
                            } finally {
                                //说明塞入对象完毕，现在可以继续安全修改数据，解除占用
                                cellsBusy = 0;
                            }
                            //如果成功创建，则退出循环
                            if (created)
                                break;
                            //如果没有创建成功，则继续循环
                            continue;           // Slot is now non-empty
                        }
                    }
                    //将碰撞标志位置为false
                    collide = false;
                }
                //在调用前CAS已经失败了，说明有竞争
                else if (!wasUncontended)       // CAS already known to fail
                    //更改竞争标识
                    wasUncontended = true;      // Continue after rehash
                //CAS更新a的当前值，如果fn为空，则将当前值加上x构成新的值，如果不为空，则将v和x用
                //fn函数进行处理构成新的值
                else if (a.cas(v = a.value, ((fn == null) ? v + x :
                                             fn.applyAsLong(v, x))))
                    break;
                //n >= NCPU 表示当前线程数超过了CPU数
                //cells != as 表示当前数据结构不是最新的或者已经过时
                else if (n >= NCPU || cells != as)
                    //在最大的大小或者已经过时
                    collide = false;            // At max size or stale
                else if (!collide)
                    //在上面条件都不满足的情况下，collide为false，那么可能发生了碰撞或者数据更新
                    collide = true;
                else if (cellsBusy == 0 && casCellsBusy()) {
                    try {
                        if (cells == as) {      // Expand table unless stale
                            //扩容至两倍大小，将原来的值都迁移到扩容之后的容器中
                            Cell[] rs = new Cell[n << 1];
                            for (int i = 0; i < n; ++i)
                                rs[i] = as[i];
                            cells = rs;
                        }
                    } finally {
                        cellsBusy = 0;
                    }
                    collide = false;
                    //用扩容之后的表进行重试
                    continue;                   // Retry with expanded table
                }
                //重新进行hash操作
                h = advanceProbe(h);
            }
            //初始化一张表，大小为2，并将1位置放入x
            else if (cellsBusy == 0 && cells == as && casCellsBusy()) {
                boolean init = false;
                try {                           // Initialize table
                    if (cells == as) {
                        Cell[] rs = new Cell[2];
                        rs[h & 1] = new Cell(x);
                        cells = rs;
                        init = true;
                    }
                } finally {
                    cellsBusy = 0;
                }
                if (init)
                    break;
            }
            //更新基值操作
            else if (casBase(v = base, ((fn == null) ? v + x :
                                        fn.applyAsLong(v, x))))
                break;                          // Fall back on using base
        }
    }

    /**
     * 与 longAccumulate 相同，但考虑到此类的低开销要求，在太多地方注入long、double
     * 转换以合理地与 long 版本合并。所以必须通过copy/paste/adapt来维护。
     */
    final void doubleAccumulate(double x, DoubleBinaryOperator fn,
                                boolean wasUncontended) {
        int h;
        if ((h = getProbe()) == 0) {
            ThreadLocalRandom.current(); // force initialization
            h = getProbe();
            wasUncontended = true;
        }
        boolean collide = false;                // True if last slot nonempty
        for (;;) {
            Cell[] as; Cell a; int n; long v;
            if ((as = cells) != null && (n = as.length) > 0) {
                if ((a = as[(n - 1) & h]) == null) {
                    if (cellsBusy == 0) {       // Try to attach new Cell
                        Cell r = new Cell(Double.doubleToRawLongBits(x));
                        if (cellsBusy == 0 && casCellsBusy()) {
                            boolean created = false;
                            try {               // Recheck under lock
                                Cell[] rs; int m, j;
                                if ((rs = cells) != null &&
                                    (m = rs.length) > 0 &&
                                    rs[j = (m - 1) & h] == null) {
                                    rs[j] = r;
                                    created = true;
                                }
                            } finally {
                                cellsBusy = 0;
                            }
                            if (created)
                                break;
                            continue;           // Slot is now non-empty
                        }
                    }
                    collide = false;
                }
                else if (!wasUncontended)       // CAS already known to fail
                    wasUncontended = true;      // Continue after rehash
                else if (a.cas(v = a.value,
                               ((fn == null) ?
                                Double.doubleToRawLongBits
                                //将一个双精度浮点数转换一个一个长整型
                                (Double.longBitsToDouble(v) + x) :
                                Double.doubleToRawLongBits
                                (fn.applyAsDouble
                                 (Double.longBitsToDouble(v), x)))))
                    break;
                else if (n >= NCPU || cells != as)
                    collide = false;            // At max size or stale
                else if (!collide)
                    collide = true;
                else if (cellsBusy == 0 && casCellsBusy()) {
                    try {
                        if (cells == as) {      // Expand table unless stale
                            Cell[] rs = new Cell[n << 1];
                            for (int i = 0; i < n; ++i)
                                rs[i] = as[i];
                            cells = rs;
                        }
                    } finally {
                        cellsBusy = 0;
                    }
                    collide = false;
                    continue;                   // Retry with expanded table
                }
                h = advanceProbe(h);
            }
            else if (cellsBusy == 0 && cells == as && casCellsBusy()) {
                boolean init = false;
                try {                           // Initialize table
                    if (cells == as) {
                        Cell[] rs = new Cell[2];
                        rs[h & 1] = new Cell(Double.doubleToRawLongBits(x));
                        cells = rs;
                        init = true;
                    }
                } finally {
                    cellsBusy = 0;
                }
                if (init)
                    break;
            }
            else if (casBase(v = base,
                             ((fn == null) ?
                              Double.doubleToRawLongBits
                              (Double.longBitsToDouble(v) + x) :
                              Double.doubleToRawLongBits
                              (fn.applyAsDouble
                               (Double.longBitsToDouble(v), x)))))
                break;                          // Fall back on using base
        }
    }

    // Unsafe mechanics
    private static final sun.misc.Unsafe UNSAFE;
    private static final long BASE;
    private static final long CELLSBUSY;
    private static final long PROBE;
    static {
        try {
            UNSAFE = sun.misc.Unsafe.getUnsafe();
            Class<?> sk = Striped64.class;
            BASE = UNSAFE.objectFieldOffset
                (sk.getDeclaredField("base"));
            CELLSBUSY = UNSAFE.objectFieldOffset
                (sk.getDeclaredField("cellsBusy"));
            Class<?> tk = Thread.class;
            PROBE = UNSAFE.objectFieldOffset
                (tk.getDeclaredField("threadLocalRandomProbe"));
        } catch (Exception e) {
            throw new Error(e);
        }
    }

}
```

 @sun.misc.Contended注解解析

