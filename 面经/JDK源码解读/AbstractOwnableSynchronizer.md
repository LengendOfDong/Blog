## AbstractOwnableSynchronizer

源码：

```java

package java.util.concurrent.locks;

/**
 * 一个同步器可能由线程独占。此类为创建锁和相关同步器提供了基础，这些同步器可能需要所有权概念。 	
 * AbstractOwnableSynchronizer类本身不管理或使用此信息。但是，子类和工具可以使用适当维护的值来帮助
 * 控制和监视访问并提供诊断。
 */
public abstract class AbstractOwnableSynchronizer
    implements java.io.Serializable {

    //即使所有字段都是瞬态的，也使用序列号 ID。
    private static final long serialVersionUID = 3737899427754241961L;

    //供子类使用的空构造函数。
    protected AbstractOwnableSynchronizer() { }

    //独占模式同步的当前所有者。
    private transient Thread exclusiveOwnerThread;

    //设置当前拥有独占访问权限的线程。null 参数表示没有线程拥有访问权限。此方法不会以
    //其他方式强制执行任何同步或volatile字段访问。
    protected final void setExclusiveOwnerThread(Thread thread) {
        exclusiveOwnerThread = thread;
    }

    /**
     * 返回 setExclusiveOwnerThread 上次设置的线程，如果从不设置，则返回 null。
     * 此方法不会以其他方式强制执行任何同步或volatile字段访问。
     */
    protected final Thread getExclusiveOwnerThread() {
        return exclusiveOwnerThread;
    }
}
```

