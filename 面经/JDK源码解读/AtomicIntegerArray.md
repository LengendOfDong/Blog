# AtomicIntegerArray

源码：

```java

package java.util.concurrent.atomic;
import java.util.function.IntUnaryOperator;
import java.util.function.IntBinaryOperator;
import sun.misc.Unsafe;

/**
* int数组中元素可以被原子地更新
*/
public class AtomicIntegerArray implements java.io.Serializable {
    private static final long serialVersionUID = 2862133569453604235L;

    private static final Unsafe unsafe = Unsafe.getUnsafe();
    //通过unsafe获取数组的基本偏移量
    private static final int base = unsafe.arrayBaseOffset(int[].class);
    private static final int shift;
    private final int[] array;

    static {
        //数据中元素的比例因子
        int scale = unsafe.arrayIndexScale(int[].class);
        //例如8的二进制为1000，减去1后二进制为0111，两者相与之后为0
        //如果相与之后不为0，说明不是2的整数倍
        if ((scale & (scale - 1)) != 0)
            throw new Error("data type scale not a power of two");
        shift = 31 - Integer.numberOfLeadingZeros(scale);
    }
	//获取某数组元素的偏移地址
    private long checkedByteOffset(int i) {
        if (i < 0 || i >= array.length)
            throw new IndexOutOfBoundsException("index " + i);

        return byteOffset(i);
    }

    //通过数据元素的位置计算偏移地址,比如a[0]是16，a[1]是20，a[2]是24
    private static long byteOffset(int i) {
        return ((long) i << shift) + base;
    }

    /**
     * 创建一个原子的整型数组，其中的元素都初始化为0
     */
    public AtomicIntegerArray(int length) {
        array = new int[length];
    }

    /**
     * 创建一个同等长度的原子整型数组，其中的元素从给定的数组中拷贝
     *
     */
    public AtomicIntegerArray(int[] array) {
        this.array = array.clone();
    }

    /**
     * 返回数组的长度
     */
    public final int length() {
        return array.length;
    }

    /**
     * 获取数组实际的值
     */
    public final int get(int i) {
        return getRaw(checkedByteOffset(i));
    }

    private int getRaw(long offset) {
        return unsafe.getIntVolatile(array, offset);
    }

    /**
     * 设置对应位置的值
     *
     */
    public final void set(int i, int newValue) {
        unsafe.putIntVolatile(array, checkedByteOffset(i), newValue);
    }

    /**
     * 最终设置对应位置的值
     *
     */
    public final void lazySet(int i, int newValue) {
        unsafe.putOrderedInt(array, checkedByteOffset(i), newValue);
    }

    /**
     * 原子地设置新的值，并返回原来的旧值
     *
     */
    public final int getAndSet(int i, int newValue) {
        return unsafe.getAndSetInt(array, checkedByteOffset(i), newValue);
    }

    /**
     * 如果当前值等于期望值，则原子地设置指定位置的值为更新值
     * 如果当前值不等于期望值，则返回false
     */
    public final boolean compareAndSet(int i, int expect, int update) {
        return compareAndSetRaw(checkedByteOffset(i), expect, update);
    }

    private boolean compareAndSetRaw(long offset, int expect, int update) {
        return unsafe.compareAndSwapInt(array, offset, expect, update);
    }

    /**
     * 如果当前值等于期望值，则原子地设置指定位置的值为更新值
     * 如果当前值不等于期望值，则返回false
     */
    public final boolean weakCompareAndSet(int i, int expect, int update) {
        return compareAndSet(i, expect, update);
    }

    /**
     * 原子地递增下标位置的值，使其加1，返回原来的值
     */
    public final int getAndIncrement(int i) {
        return getAndAdd(i, 1);
    }

    /**
     * 原子地递减下标位置的值，使其减1，返回原来的值
     */
    public final int getAndDecrement(int i) {
        return getAndAdd(i, -1);
    }

    /**
     * 原子地增加下标位置的值，增加delta大小，返回原来的值
     */
    public final int getAndAdd(int i, int delta) {
        return unsafe.getAndAddInt(array, checkedByteOffset(i), delta);
    }

    /**
     * 原子地递增下标位置的值，使其加1，返回更新后的值
     */
    public final int incrementAndGet(int i) {
        return getAndAdd(i, 1) + 1;
    }

    /**
     * 原子地递减下标位置的值，使其减1，返回更新后的值
     *
     */
    public final int decrementAndGet(int i) {
        return getAndAdd(i, -1) - 1;
    }

    /**
     * 原子地增加下标位置的值，使其加delta大小，并返回更新后的值
     *
     */
    public final int addAndGet(int i, int delta) {
        return getAndAdd(i, delta) + delta;
    }


    /**
     * 通过使用指定的方法，原子地更新指定下标位置的元素，返回原来的值
     * 该方法应该是无副作用的，因为可能在线程竞争的情况下失败后重复执行
     */
    public final int getAndUpdate(int i, IntUnaryOperator updateFunction) {
        long offset = checkedByteOffset(i);
        int prev, next;
        do {
            prev = getRaw(offset);
            next = updateFunction.applyAsInt(prev);
        } while (!compareAndSetRaw(offset, prev, next));
        return prev;
    }

    /**
     * 通过使用指定的方法，原子地更新指定下标位置的元素，返回更新后的值
     * 该方法应该是无副作用的，因为可能在线程竞争的情况下失败后重复执行
     */
    public final int updateAndGet(int i, IntUnaryOperator updateFunction) {
        long offset = checkedByteOffset(i);
        int prev, next;
        do {
            prev = getRaw(offset);
            next = updateFunction.applyAsInt(prev);
        } while (!compareAndSetRaw(offset, prev, next));
        return next;
    }

    /**
     * 通过使用指定的方法，原子地更新指定下标位置的元素，返回原来的值
     * 该方法应该是无副作用的，因为可能在线程竞争的情况下失败后重复执行
     * 第一个参数是下标位置的当前值，第二个参数是给定的更新值
     */
    public final int getAndAccumulate(int i, int x,
                                      IntBinaryOperator accumulatorFunction) {
        long offset = checkedByteOffset(i);
        int prev, next;
        do {
            prev = getRaw(offset);
            next = accumulatorFunction.applyAsInt(prev, x);
        } while (!compareAndSetRaw(offset, prev, next));
        return prev;
    }

    /**
     * 通过使用指定的方法，原子地更新指定下标位置的元素，返回更新后的值
     * 该方法应该是无副作用的，因为可能在线程竞争的情况下失败后重复执行
     * 第一个参数是下标位置的当前值，第二个参数是给定的更新值
     */
    public final int accumulateAndGet(int i, int x,
                                      IntBinaryOperator accumulatorFunction) {
        long offset = checkedByteOffset(i);
        int prev, next;
        do {
            prev = getRaw(offset);
            next = accumulatorFunction.applyAsInt(prev, x);
        } while (!compareAndSetRaw(offset, prev, next));
        return next;
    }

    /**
     * Returns the String representation of the current values of array.
     * 返回当前数组值的字符串展示
     * @return the String representation of the current values of array
     */
    public String toString() {
        //获取数组的最大下标
        int iMax = array.length - 1;
        //如果最大下标为-1，则打印[]
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        //设置起始符[
        b.append('[');
        for (int i = 0; ; i++) {
            //获取数组实际的值并连接
            b.append(getRaw(byteOffset(i)));
            //如果当前遍历的下标为最大下标，则连接结束符]
            if (i == iMax)
                return b.append(']').toString();
            //数组元素之间用逗号分割，并增加空格隔开
            b.append(',').append(' ');
        }
    }

}

```

代码中***shift* = 31 - Integer.*numberOfLeadingZeros*(scale);**

```java
public static int numberOfLeadingZeros(int i) {
        // HD, Figure 5-6
        if (i == 0)
            return 32;
        int n = 1;
    	//举例i为0000 0000 0000 0000 0000 0000 0000 0001
    	//执行完n为17, i为0000 0000 0000 0001 0000 0000 0000 0000
        if (i >>> 16 == 0) { n += 16; i <<= 16; }
    	//执行完n为25，i为0000 0001 0000 0000 0000 0000 0000 0000 
        if (i >>> 24 == 0) { n +=  8; i <<=  8; }
    	//执行完n为29，i为0001 0000 0000 0000 0000 0000 0000 0000
        if (i >>> 28 == 0) { n +=  4; i <<=  4; }
    	//执行完n为31，i为01 0000 0000 0000 0000 0000 0000 0000 00
        if (i >>> 30 == 0) { n +=  2; i <<=  2; }
    	//向右移动31位，判断倒数第二位是否为1，如果为1则n - 1等于 30，如果不为1，则n - 0,n为31
        n -= i >>> 31;
        return n;
    }
```

方法的主要目的：

> 利用二分法查找的方法，返回无符号整型 i 的最高非零位前面的n个0的个数，包括符号位。
> 如果i等于0则返回32。
> 例：10的二进制为：0000 0000 0000 0000 0000 0000 0000 1010
> java的int长度为32位，那么这个方法返回的就是28。

Java的数组元素在内存中以连续的空间进行存储。每个数组元素都有自己的索引位置，可以通过该索引来访问对应的值。

当我们创建一个数组时，JVM会为其分配一段连续的内存空间，并将这些内存地址保存到数组变量中。数组的第一个元素被放置在最低的内存地址上，而后面的元素则按顺序依次存储在相邻的内存地址上。

下面是一个示例代码展示了数组元素在内存中的存储情况：

```java
public class ArrayMemoryExample {
    public static void main(String[] args) {
        int[] array = new int[5]; // 定义长度为5的int类型数组
        
        for (int i = 0; i < array.length; i++) {
            System.out.println("array[" + i + "]的内存地址：" + getAddressOfArrayElement(array, i));
        }
    }
    
    private static long getAddressOfArrayElement(Object obj, int index) {
        return unsafe.getInt(obj, offset);
    }
}
```

输出结果：

```java
array[0]的内存地址：123456789
array[1]的内存地址：123456790
array[2]的内存地址：123456791
array[3]的内存地址：123456792
array[4]的内存地址：123456793
```

对于数组来说，实际的数据内容存储在堆内存中。而数组变量（引用）本身存储在栈内存中。当我们执行赋值操作时，实际上是将一个引用复制给另一个引用，这使得两个引用指向了相同的堆内存位置。



### AtomicIntegerArray多线程应用

```java
public class AtomicIntegerArrayExample {

    public static void main(String[] args) throws InterruptedException {
        // 初始化一个包含5个元素的AtomicIntegerArray
        final AtomicIntegerArray atomicArray = new AtomicIntegerArray(5);
        for (int i = 0; i < atomicArray.length(); i++) {
            atomicArray.set(i, i);
        }

        // 启动两个线程来更新数组中的第三个元素
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                atomicArray.incrementAndGet(2); // 增加索引为2的元素
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                atomicArray.incrementAndGet(2); // 增加索引为2的元素
            }
        });

        // 启动线程
        thread1.start();
        thread2.start();

        // 等待两个线程执行完成
        thread1.join();
        thread2.join();

        // 打印数组中所有元素的值
        for (int i = 0; i < atomicArray.length(); i++) {
            System.out.print(atomicArray.get(i) + " ");
        }

        // 预期输出中，索引2的元素值应该是20002（初始值为2，每个线程增加10000次）
        // 其他元素的值应该保持不变
    }
}
```

输出结果为：

```java
0 1 20002 3 4
```



