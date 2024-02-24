# AtomicLongFieldUpdater

源码：

主要对比CASUpdater与LockedUpdater之间的compareAndSet 与 Set方法之间的区别

```java

public abstract class AtomicLongFieldUpdater<T> {
    @CallerSensitive
    public static <U> AtomicLongFieldUpdater<U> newUpdater(Class<U> tclass,
                                                           String fieldName) {
        Class<?> caller = Reflection.getCallerClass();
        //判断是否支持long类型的CAS操作
        //如果支持的话，就使用CASUpdater来进行原子更新
        //如果不支持的话，就使用LockedUpdater来进行原子更新
        if (AtomicLong.VM_SUPPORTS_LONG_CAS)
            return new CASUpdater<U>(tclass, fieldName, caller);
        else
            return new LockedUpdater<U>(tclass, fieldName, caller);
    }
	
    private static final class CASUpdater<T> extends AtomicLongFieldUpdater<T> {
        private static final sun.misc.Unsafe U = sun.misc.Unsafe.getUnsafe();
        private final long offset;
		//直接使用Unsafe的compareAndSwapLong方法来实现CAS
        public final boolean compareAndSet(T obj, long expect, long update) {
            accessCheck(obj);
            return U.compareAndSwapLong(obj, offset, expect, update);
        }
		//通过调用Unsafe的putLongVolatile来实现
        public final void set(T obj, long newValue) {
            accessCheck(obj);
            U.putLongVolatile(obj, offset, newValue);
        }
    }

    private static final class LockedUpdater<T> extends AtomicLongFieldUpdater<T> {
        private static final sun.misc.Unsafe U = sun.misc.Unsafe.getUnsafe();
        private final long offset;
		//用加锁的方式来实现CAS，先获取long类型数据，然后再比较是否等于期望值，如果等于则进行更新
        public final boolean compareAndSet(T obj, long expect, long update) {
            accessCheck(obj);
            synchronized (this) {
                long v = U.getLong(obj, offset);
                if (v != expect)
                    return false;
                U.putLong(obj, offset, update);
                return true;
            }
        }
		//使用加锁的方式，并通过调用Unsafe的putLong来实现
        public final void set(T obj, long newValue) {
            accessCheck(obj);
            synchronized (this) {
                U.putLong(obj, offset, newValue);
            }
        }
    }
}

```

LockedUpdater中的CompareAndSet方法和Set方法都使用了putLong来进行设置值，而不是像CASUpdater中那样使用putLongVolatile来设置值，主要是因为前者已经在锁的范围内，当解锁时就会保证数据写入到内存中，效果和putLongVolatile一样，但使用putLongVolatile反而会多此一举了。
