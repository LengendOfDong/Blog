# ArrayIntegerFieldUpdater

源码：

```java
package java.util.concurrent.atomic;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Objects;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;

/**
 * 一个基于反射的实用程序，用于对指定类的指定可变 int 字段进行原子更新。此类设计用于原子数据结构，
 * 其中同一节点的多个字段独立地受到原子更新的影响。
 * 请注意，此类中 compareAndSet 方法的保证比其他原子类中的保证弱。由于此类无法确保字段的所有用法
 * 都适用于原子访问的目的，因此它只能保证对同一更新程序上 compareAndSet 和 set 的其他调用的原子性。
 * <T> 持有更新字段的类型
 */
public abstract class AtomicIntegerFieldUpdater<T> {
    /**
     * 为具有给定字段的对象创建并返回更新器。需要 Class 参数来检查反射类型和泛型类型是否匹配。
     * tclass指持有域的对象的类
     * fieldName表示更新域的名称
     */
    @CallerSensitive
    public static <U> AtomicIntegerFieldUpdater<U> newUpdater(Class<U> tclass,
                                                              String fieldName) {
        return new AtomicIntegerFieldUpdaterImpl<U>
            (tclass, fieldName, Reflection.getCallerClass());
    }

	
    private static final class AtomicIntegerFieldUpdaterImpl<T>
        extends AtomicIntegerFieldUpdater<T> {
        private static final sun.misc.Unsafe U = sun.misc.Unsafe.getUnsafe();
        private final long offset;
        private final Class<?> cclass;
        private final Class<T> tclass;

        AtomicIntegerFieldUpdaterImpl(final Class<T> tclass,
                                      final String fieldName,
                                      final Class<?> caller) {
            final Field field;
            final int modifiers;
            try {
                field = AccessController.doPrivileged(
                    new PrivilegedExceptionAction<Field>() {
                        public Field run() throws NoSuchFieldException {
                            return tclass.getDeclaredField(fieldName);
                        }
                    });
                //获取字段的访问控制符
                modifiers = field.getModifiers();
                //确保 caller 对正在执行的反射操作具有对 tclass 的必要访问权限
                sun.reflect.misc.ReflectUtil.ensureMemberAccess(
                    caller, tclass, null, modifiers);
                //获取tclass的类加载器
                ClassLoader cl = tclass.getClassLoader();
                //获取caller的类加载器
                ClassLoader ccl = caller.getClassLoader();
                //tclass与caller不相等，或者不是祖先，则检查tclass的包访问权限
                if ((ccl != null) && (ccl != cl) &&
                    ((cl == null) || !isAncestor(cl, ccl))) {
                    sun.reflect.misc.ReflectUtil.checkPackageAccess(tclass);
                }
            } catch (PrivilegedActionException pae) {
                throw new RuntimeException(pae.getException());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
			//字段类型必须是整型
            if (field.getType() != int.class)
                throw new IllegalArgumentException("Must be integer type");
			//必须被volatile类型修饰
            if (!Modifier.isVolatile(modifiers))
                throw new IllegalArgumentException("Must be volatile type");

            //修饰符是否是protected，检查 tclass 是否是 caller 的超类或超接口
            //检查 tclass 和 caller 是否不在同一个包中
            //最后决定是caller赋值给cclass还是tclass赋值给cclass
            this.cclass = (Modifier.isProtected(modifiers) &&
                           tclass.isAssignableFrom(caller) &&
                           !isSamePackage(tclass, caller))
                          ? caller : tclass;
            this.tclass = tclass;
            this.offset = U.objectFieldOffset(field);
        }
    }
}

```

@CallerSensitive注解解析：

在ArrayIntegerFieldUpdater类源码中使用到了@CallerSensitive注解，实际是在执行**Reflection.getCallerClass()**起作用了。

可以看到Reflection类的代码中也有这个注解：

```java
    @CallerSensitive
    public static native Class<?> getCallerClass();
```

Reflection.getCallerClass()方法规定，调用它的对象，必须有 @CallerSensitive 注解，否则 报异常 Exception in thread "main" java.lang.InternalError: CallerSensitive annotation expected at frame 1

@CallerSensitive 有个特殊之处，必须由 启动类classloader加载（如rt.jar ），才可以被识别。 所以rt.jar下面的注解可以正常使用。

### ArrayIntegerFieldUpdater多线程应用举例 

下面是一个使用 `AtomicIntegerFieldUpdater` 在多线程环境中安全更新对象的例子：

命名一个Person对象，`AtomicIntegerFieldUpdater` 是用于更新对象的某个 `volatile int` 字段的，所以将Person对象的age用volatile修饰，另外访问控制符也不能使用private，其他三种访问权限都可以使用。

```java
import lombok.Data;

@Data
public class Person {

    public volatile int age;

    public String name;
}
```

使用AtomicIntegerFieldUpdater更新Person对象的age参数

```java
public class AtomicIntegerFieldUpdaterExample2 {

    // 创建一个针对数组中特定索引位置的AtomicIntegerFieldUpdater
    private static final AtomicIntegerFieldUpdater<Person> updater =
            AtomicIntegerFieldUpdater.newUpdater(Person.class, "age");

    public static void main(String[] args) throws InterruptedException {
        Person person = new Person();
        person.setAge(20);
        person.setName("hong");

        // 启动两个线程来更新数组中的第三个元素
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                updater.incrementAndGet(person);
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                updater.incrementAndGet(person);
            }
        });

        // 启动线程
        thread1.start();
        thread2.start();

        // 等待两个线程执行完成
        thread1.join();
        thread2.join();

        System.out.println(person);
    }
}
```

输出打印结果：

```java
Person(age=40, name=hong)
```

