# DoubleAccumulator

源码：

```java
package java.util.concurrent.atomic;
import java.io.Serializable;
import java.util.function.DoubleBinaryOperator;

/**
 * One or more variables that together maintain a running {@code double}
 * value updated using a supplied function.  When updates (method
 * {@link #accumulate}) are contended across threads, the set of variabl	es
 * may grow dynamically to reduce contention.  Method {@link #get}
 * (or, equivalently, {@link #doubleValue}) returns the current value
 * across the variables maintaining updates.
 * 一个或多个变量，它们共同维护使用提供的函数更新的运行双精度值。当更新（方法累积）在线程
 * 之间争用时，变量集可能会动态增长以减少争用。方法 get（或等效的 doubleValue）返回维护更新
 * 的变量的当前值。
 * <p>This class is usually preferable to alternatives when multiple
 * threads update a common value that is used for purposes such as
 * summary statistics that are frequently updated but less frequently
 * read.
 * 当多个线程更新用于频繁更新但读取频率较低的摘要统计信息等目的的公共值时，此类通常比
 * 替代方法更可取。
 * <p>The supplied accumulator function should be side-effect-free,
 * since it may be re-applied when attempted updates fail due to
 * contention among threads. The function is applied with the current
 * value as its first argument, and the given update as the second
 * argument.  For example, to maintain a running maximum value, you
 * could supply {@code Double::max} along with {@code
 * Double.NEGATIVE_INFINITY} as the identity. The order of
 * accumulation within or across threads is not guaranteed. Thus, this
 * class may not be applicable if numerical stability is required,
 * especially when combining values of substantially different orders
 * of magnitude.
 * 提供的累加器函数应该没有副作用，因为当尝试更新由于线程之间的争用而失败时，它可能会重新
 * 应用。应用该函数时，将当前值作为其第一个参数，将给定的更新作为第二个参数。例如，若要保持
 * 运行最大值，可以提供 Double：：max 和 Double.NEGATIVE_INFINITY 作为标识。无法保证
 * 线程内或线程之间的累积顺序。因此，如果需要数值稳定性，特别是当组合数量级大不相同的值时，
 * 此类可能不适用。
 * <p>Class {@link DoubleAdder} provides analogs of the functionality
 * of this class for the common special case of maintaining sums.  The
 * call {@code new DoubleAdder()} is equivalent to {@code new
 * DoubleAccumulator((x, y) -> x + y, 0.0)}.
 * 类 DoubleAdder 为维护总和的常见特殊情况提供了此类功能的类似物。调用 new DoubleAdder()
 * 等效于 new DoubleAccumulator（（x， y） -> x + y， 0.0）
 * <p>This class extends {@link Number}, but does <em>not</em> define
 * methods such as {@code equals}, {@code hashCode} and {@code
 * compareTo} because instances are expected to be mutated, and so are
 * not useful as collection keys.
 * 此类扩展了 Number，但不定义 equals、hashCode 和 compareTo 等方法，因为实例应
 * 发生突变，因此不能用作集合键。
 * @since 1.8
 * @author Doug Lea
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
                //
                !(uncontended =
                  (r = Double.doubleToRawLongBits
                   (function.applyAsDouble
                    (Double.longBitsToDouble(v = a.value), x))) == v ||
                  a.cas(v, r)))
                doubleAccumulate(x, function, uncontended);
        }
    }

    /**
     * Returns the current value.  The returned value is <em>NOT</em>
     * an atomic snapshot; invocation in the absence of concurrent
     * updates returns an accurate result, but concurrent updates that
     * occur while the value is being calculated might not be
     * incorporated.
     *
     * @return the current value
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
     * Resets variables maintaining updates to the identity value.
     * This method may be a useful alternative to creating a new
     * updater, but is only effective if there are no concurrent
     * updates.  Because this method is intrinsically racy, it should
     * only be used when it is known that no threads are concurrently
     * updating.
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

    /**
     * Equivalent in effect to {@link #get} followed by {@link
     * #reset}. This method may apply for example during quiescent
     * points between multithreaded computations.  If there are
     * updates concurrent with this method, the returned value is
     * <em>not</em> guaranteed to be the final value occurring before
     * the reset.
     *
     * @return the value before reset
     */
    public double getThenReset() {
        Cell[] as = cells; Cell a;
        double result = Double.longBitsToDouble(base);
        base = identity;
        if (as != null) {
            for (int i = 0; i < as.length; ++i) {
                if ((a = as[i]) != null) {
                    double v = Double.longBitsToDouble(a.value);
                    a.value = identity;
                    result = function.applyAsDouble(result, v);
                }
            }
        }
        return result;
    }

    /**
     * Returns the String representation of the current value.
     * @return the String representation of the current value
     */
    public String toString() {
        return Double.toString(get());
    }

    /**
     * Equivalent to {@link #get}.
     *
     * @return the current value
     */
    public double doubleValue() {
        return get();
    }

    /**
     * Returns the {@linkplain #get current value} as a {@code long}
     * after a narrowing primitive conversion.
     */
    public long longValue() {
        return (long)get();
    }

    /**
     * Returns the {@linkplain #get current value} as an {@code int}
     * after a narrowing primitive conversion.
     */
    public int intValue() {
        return (int)get();
    }

    /**
     * Returns the {@linkplain #get current value} as a {@code float}
     * after a narrowing primitive conversion.
     */
    public float floatValue() {
        return (float)get();
    }

    /**
     * Serialization proxy, used to avoid reference to the non-public
     * Striped64 superclass in serialized forms.
     * @serial include
     */
    private static class SerializationProxy implements Serializable {
        private static final long serialVersionUID = 7249069246863182397L;

        /**
         * The current value returned by get().
         * @serial
         */
        private final double value;
        /**
         * The function used for updates.
         * @serial
         */
        private final DoubleBinaryOperator function;
        /**
         * The identity value
         * @serial
         */
        private final long identity;

        SerializationProxy(DoubleAccumulator a) {
            function = a.function;
            identity = a.identity;
            value = a.get();
        }

        /**
         * Returns a {@code DoubleAccumulator} object with initial state
         * held by this proxy.
         *
         * @return a {@code DoubleAccumulator} object with initial state
         * held by this proxy.
         */
        private Object readResolve() {
            double d = Double.longBitsToDouble(identity);
            DoubleAccumulator a = new DoubleAccumulator(function, d);
            a.base = Double.doubleToRawLongBits(value);
            return a;
        }
    }

    /**
     * Returns a
     * <a href="../../../../serialized-form.html#java.util.concurrent.atomic.DoubleAccumulator.SerializationProxy">
     * SerializationProxy</a>
     * representing the state of this instance.
     *
     * @return a {@link SerializationProxy}
     * representing the state of this instance
     */
    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    /**
     * @param s the stream
     * @throws java.io.InvalidObjectException always
     */
    private void readObject(java.io.ObjectInputStream s)
        throws java.io.InvalidObjectException {
        throw new java.io.InvalidObjectException("Proxy required");
    }

}

```

