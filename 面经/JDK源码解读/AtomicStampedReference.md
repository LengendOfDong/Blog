# AtomicStampedReference

源码：

```java
package java.util.concurrent.atomic;

/**
 * AtomicStampedReference 维护一个对象引用以及一个整数“stamp”，该引用可以原子化地
 * 更新。实现说明：此实现通过创建表示包装的[引用，整数]对的内部对象来维护标记引用
 * 这相当于给这个引用打上了版本号标记
 */
public class AtomicStampedReference<V> {
	//创建一个内部类，用于存放引用和整数标记
    private static class Pair<T> {
        final T reference;
        final int stamp;
        private Pair(T reference, int stamp) {
            this.reference = reference;
            this.stamp = stamp;
        }
        static <T> Pair<T> of(T reference, int stamp) {
            return new Pair<T>(reference, stamp);
        }
    }

    private volatile Pair<V> pair;

    /**
     * 用给定的初始值来创建一个新的AtomicStampedReference
     * @param initialRef the initial reference
     * @param initialStamp the initial stamp
     */
    public AtomicStampedReference(V initialRef, int initialStamp) {
        pair = Pair.of(initialRef, initialStamp);
    }

    /**
     * 返回引用的当前值
     */
    public V getReference() {
        return pair.reference;
    }

    /**
     * 返回引用的标记，这个可以用来获取版本号，比较版本号的时候使用
     */
    public int getStamp() {
        return pair.stamp;
    }

    /**
     * 传入一个数组， 这个数组的长度至少为1，返回时数组中存放着标记的值，返回值是引用
     * 的当前值
     * @param stampHolder an array of size of at least one.  On return,
     * {@code stampholder[0]} will hold the value of the stamp.
     * @return the current value of the reference
     */
    public V get(int[] stampHolder) {
        Pair<V> pair = this.pair;
        stampHolder[0] = pair.stamp;
        return pair.reference;
    }
    
    /**
     * 如果当前应用等于期望引用，并且当前标记等于期望标记，则原子地设置引用和标记为给定值
     */
    public boolean compareAndSet(V   expectedReference,
                                 V   newReference,
                                 int expectedStamp,
                                 int newStamp) {
        Pair<V> current = pair;
        return
            expectedReference == current.reference &&
            expectedStamp == current.stamp &&
            ((newReference == current.reference &&
              newStamp == current.stamp) ||
             casPair(current, Pair.of(newReference, newStamp)));
    }

    /**
     * 不管当前值是多少，直接替换成新的引用和标记
     */
    public void set(V newReference, int newStamp) {
        Pair<V> current = pair;
        if (newReference != current.reference || newStamp != current.stamp)
            this.pair = Pair.of(newReference, newStamp);
    }

    /**
     * 在给定的预期引用与当前引用相等的情况下，将标记的值设置为给定的更新值。这个操作可能无意义
     * 地失败（返回false），但是当当前值持有预期值并且没有其他线程试图设置值时，重复的调用将最
     * 终成功。
     */
    public boolean attemptStamp(V expectedReference, int newStamp) {
        Pair<V> current = pair;
        return
            expectedReference == current.reference &&
            (newStamp == current.stamp ||
             casPair(current, Pair.of(expectedReference, newStamp)));
    }

	//先反射获取某个对象，再利用UNSAFE的方法来获取这个对象的内存中的位置
    private static final sun.misc.Unsafe UNSAFE = sun.misc.Unsafe.getUnsafe();
    private static final long pairOffset =
        objectFieldOffset(UNSAFE, "pair", AtomicStampedReference.class);

    private boolean casPair(Pair<V> cmp, Pair<V> val) {
        return UNSAFE.compareAndSwapObject(this, pairOffset, cmp, val);
    }

    static long objectFieldOffset(sun.misc.Unsafe UNSAFE,
                                  String field, Class<?> klazz) {
        try {
            return UNSAFE.objectFieldOffset(klazz.getDeclaredField(field));
        } catch (NoSuchFieldException e) {
            // Convert Exception to corresponding Error
            NoSuchFieldError error = new NoSuchFieldError(field);
            error.initCause(e);
            throw error;
        }
    }
}

```

