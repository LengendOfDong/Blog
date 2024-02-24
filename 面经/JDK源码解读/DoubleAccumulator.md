# DoubleAccumulator

源码：

```java
package java.util.concurrent.atomic;
import java.io.Serializable;
import java.util.function.DoubleBinaryOperator;

/**
 * 一个或多个变量，它们共同维护使用提供的函数更新的运行双精度值。当更新（方法累积）在线程
 * 之间争用时，变量集可能会动态增长以减少争用。方法 get（或等效的 doubleValue）返回维护更新
 * 的变量的当前值。
 * 当多个线程更新用于频繁更新但读取频率较低的摘要统计信息等目的的公共值时，此类通常比
 * 替代方法更可取。
 * 提供的累加器函数应该没有副作用，因为当尝试更新由于线程之间的争用而失败时，它可能会重新
 * 应用。应用该函数时，将当前值作为其第一个参数，将给定的更新作为第二个参数。例如，若要保持
 * 运行最大值，可以提供 Double：：max 和 Double.NEGATIVE_INFINITY 作为标识。无法保证
 * 线程内或线程之间的累积顺序。因此，如果需要数值稳定性，特别是当组合数量级大不相同的值时，
 * 此类可能不适用。
 * 类 DoubleAdder 为维护总和的常见特殊情况提供了此类功能的类似物。调用 new DoubleAdder()
 * 等效于 new DoubleAccumulator（（x， y） -> x + y， 0.0）
 * 此类扩展了 Number，但不定义 equals、hashCode 和 compareTo 等方法，因为实例应
 * 发生突变，因此不能用作集合键。
 */
public class DoubleAccumulator extends Striped64 implements Serializable {
    private static final long serialVersionUID = 7249069246863182397L;

    private final DoubleBinaryOperator function;
    private final long identity; // use long representation

    /**
     * 使用给定的累加器函数和标识元素创建一个新实例
     */
    public DoubleAccumulator(DoubleBinaryOperator accumulatorFunction,
                             double identity) {
        this.function = accumulatorFunction;
        base = this.identity = Double.doubleToRawLongBits(identity);
    }

    /**
     * 用给定的值进行更新
     *
     */
    public void accumulate(double x) {
        Cell[] as; long b, v, r; int m; Cell a;
        if ((as = cells) != null ||
            //base赋值给b，将b转换成double类型,再用函数计算b和x的值，然后将结果再经过
            //转换变成long型，赋值给r, 如果r不等于b并且进行CAS操作失败
            (r = Double.doubleToRawLongBits
             (function.applyAsDouble
              (Double.longBitsToDouble(b = base), x))) != b  && !casBase(b, r)) {
            boolean uncontended = true;
            //数组为空，或者数组长度为0，或者数组指定位置的元素为空
            if (as == null || (m = as.length - 1) < 0 ||
                (a = as[getProbe() & m]) == null ||
                !(uncontended =
                  (r = Double.doubleToRawLongBits
                   (function.applyAsDouble
                    (Double.longBitsToDouble(v = a.value), x))) == v ||
                  a.cas(v, r)))
                doubleAccumulate(x, function, uncontended);
        }
    }

    /**
     * 返回当前值，返回不是一个原子的快照，当调用不是并发更新的时候，会返回一个准确的值，
     * 但并发更新出现时，返回值将不会是一个准确的值
     */
    public double get() {
        Cell[] as = cells; Cell a;
        double result = Double.longBitsToDouble(base);
        if (as != null) {
            for (int i = 0; i < as.length; ++i) {
                if ((a = as[i]) != null)
                    result = function.applyAsDouble
                        (result, Double.longBitsToDouble(a.value));
            }
        }
        return result;
    }

    /**
     * 重置变量，以维护对标识值的更新。此方法可能是创建新更新程序的有用替代方法，但仅在没有并发更新时
     * 才有效。仅当已知没有线程同时更新时才应使用它。
     */
    public void reset() {
        Cell[] as = cells; Cell a;
        base = identity;
        if (as != null) {
            for (int i = 0; i < as.length; ++i) {
                if ((a = as[i]) != null)
                    a.value = identity;
            }
        }
    }

    //返回当前值的string类型
    public String toString() {
        return Double.toString(get());
    }

    //获取double类型的当前值
    public double doubleValue() {
        return get();
    }

    /**
     * 返回当前值的long值
     */
    public long longValue() {
        return (long)get();
    }

    //返回当前值的int类型
    public int intValue() {
        return (int)get();
    }

    //返回当前值的float类型
    public float floatValue() {
        return (float)get();
    }

}

```

应用举例：

```java
package juc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.DoubleAccumulator;

public class MultiThreadedLongAccumulatorExample {

    public static void main(String[] args) throws InterruptedException {
        // 创建一个固定大小的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // 创建一个 LongAccumulator 实例，用于累加操作
        DoubleAccumulator accumulator = new DoubleAccumulator(Double::sum, 0);

        // 提交任务到线程池中进行并发计算
        for (int i = 0; i < 3; i++) {
            final int taskId = i;
            executorService.submit(() -> {
                // 模拟一些计算或任务
                long valueToAccumulate = performSomeCalculation(taskId);
                // 将计算结果累加到 DoubleAccumulator 中
                accumulator.accumulate(valueToAccumulate);
            });
        }

        // 关闭线程池并等待所有任务完成
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        // 获取最终的累加结果
        double finalResult = accumulator.get();
        System.out.println("The final result is: " + finalResult);
    }

    // 模拟的计算函数，这里只是简单返回一个基于任务ID的值
    private static long performSomeCalculation(int taskId) {
        return taskId * taskId; // 举例：返回任务ID的平方
    }
}
```

