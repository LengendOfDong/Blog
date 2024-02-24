# AtomicMarkableReference

源码：

```java
package java.util.concurrent.atomic;

/**
 * AtomicMarkableReference 维护一个对象引用以及一个标记位，该标记位可以原子方式更新。
 *
 */
public class AtomicMarkableReference<V> {

    private static class Pair<T> {
        final T reference;
        final boolean mark;
        private Pair(T reference, boolean mark) {
            this.reference = reference;
            this.mark = mark;
        }
        static <T> Pair<T> of(T reference, boolean mark) {
            return new Pair<T>(reference, mark);
        }
    }

    private volatile Pair<V> pair;

    /**
     * 创建一个AtomicMarkableReference对象，用给定的初始化值，初始化的引用和初始化的标记位
     */
    public AtomicMarkableReference(V initialRef, boolean initialMark) {
        pair = Pair.of(initialRef, initialMark);
    }

    /**
     * 返回引用的当前值
     */
    public V getReference() {
        return pair.reference;
    }

    /**
     * 返回标记的当前值
     */
    public boolean isMarked() {
        return pair.mark;
    }

    /**
     * 返回引用和标记的当前值
     * markHolder[0]将会持有mark标记位
     */
    public V get(boolean[] markHolder) {
        Pair<V> pair = this.pair;
        markHolder[0] = pair.mark;
        return pair.reference;
    }

    /**
     * 如果当前引用等于期望的引用，当前的标记等于期望的标记，那么会原子地设置当前引用
     * 和标记位为给定的值
     */
    public boolean weakCompareAndSet(V       expectedReference,
                                     V       newReference,
                                     boolean expectedMark,
                                     boolean newMark) {
        return compareAndSet(expectedReference, newReference,
                             expectedMark, newMark);
    }

    /**
     * 如果当前引用等于期望的引用，当前的标记等于期望的标记，那么会原子地设置当前引用
     * 和标记位为给定的值
     */
    public boolean compareAndSet(V       expectedReference,
                                 V       newReference,
                                 boolean expectedMark,
                                 boolean newMark) {
        Pair<V> current = pair;
        return
            //当前引用等于期望引用
            expectedReference == current.reference &&
            //当前标记等于期望标记
            expectedMark == current.mark &&
            //当前引用已经等于设置的新引用或者替换为新引用成功
            ((newReference == current.reference &&
              newMark == current.mark) ||
             casPair(current, Pair.of(newReference, newMark)));
    }

    /**
     * 不管当前值是多少，直接替换成新的引用和标记
     */
    public void set(V newReference, boolean newMark) {
        Pair<V> current = pair;
        if (newReference != current.reference || newMark != current.mark)
            this.pair = Pair.of(newReference, newMark);
    }

    /**
     * 在给定的预期引用与当前引用相等的情况下，将标记的值设置为给定的更新值。这个操作可能无意义
     * 地失败（返回false），但是当当前值持有预期值并且没有其他线程试图设置值时，重复的调用将最
     * 终成功。
     *
     */
    public boolean attemptMark(V expectedReference, boolean newMark) {
        Pair<V> current = pair;
        return
            expectedReference == current.reference &&
            (newMark == current.mark ||
             casPair(current, Pair.of(expectedReference, newMark)));
    }

    private static final sun.misc.Unsafe UNSAFE = sun.misc.Unsafe.getUnsafe();
    //获取pair域的内存中的偏移量
    private static final long pairOffset =
        objectFieldOffset(UNSAFE, "pair", AtomicMarkableReference.class);
	//找到Pair对象的地址后，利用CAS交换值
    private boolean casPair(Pair<V> cmp, Pair<V> val) {
        return UNSAFE.compareAndSwapObject(this, pairOffset, cmp, val);
    }
	//先反射获取某个对象，再利用UNSAFE的方法来获取这个对象的内存中的位置
    static long objectFieldOffset(sun.misc.Unsafe UNSAFE,
                                  String field, Class<?> klazz) {
        try {
            return UNSAFE.objectFieldOffset(klazz.getDeclaredField(field));
        } catch (NoSuchFieldException e) {
            NoSuchFieldError error = new NoSuchFieldError(field);
            error.initCause(e);
            throw error;
        }
    }
}

```



### AtomicMarkableReference多线程应用举例

```java
import java.util.concurrent.atomic.AtomicMarkableReference;

public class AtomicMarkableReferenceExample {

    public static void main(String[] args) throws InterruptedException {
        // 初始化一个AtomicMarkableReference对象，包含一个字符串引用和一个标记位
        AtomicMarkableReference<String> atomicReference = new AtomicMarkableReference<>("initial", false);

        // 启动一个线程来尝试更新引用
        Thread updaterThread = new Thread(() -> {
            for(int i = 0; i <10;i++) {
                // 尝试原子地更新引用和标记位
                String prev = atomicReference.getReference();
                boolean wasMarked = atomicReference.isMarked();

                // 只有当标记位为false时，才尝试更新
                if (!wasMarked && "initial".equals(prev)) {
                    // 尝试设置新的引用和标记位
                    if (atomicReference.compareAndSet(prev, "updated", wasMarked, true)) {
                        System.out.println("Updated successfully!");
                        break; // 更新成功，退出循环
                    }
                }

                // 短暂休眠，避免忙等待
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        // 启动一个线程来标记引用
        Thread markerThread = new Thread(() -> {
            for(int i = 0; i <10;i++) {
                // 尝试原子地设置标记位
                String prev = atomicReference.getReference();
                boolean wasMarked = atomicReference.isMarked();

                // 如果引用是"initial"，则标记它
                if ("initial".equals(prev) && !wasMarked) {
                    if (atomicReference.compareAndSet(prev, "marked", wasMarked, true)) {
                        System.out.println("Marked successfully!");
                        break; // 标记成功，退出循环
                    }
                }

                // 短暂休眠，避免忙等待
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        // 启动线程
        updaterThread.start();
        markerThread.start();

        // 等待两个线程执行完成
        updaterThread.join();
        markerThread.join();

        // 打印最终的引用和标记位状态
        System.out.println("Final Reference: " + atomicReference.getReference());
        System.out.println("Final Mark: " + atomicReference.isMarked());
    }
}
```

