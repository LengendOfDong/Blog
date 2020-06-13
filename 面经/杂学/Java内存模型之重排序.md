# as-if-serial语义

在执行程序时，为了提高性能，处理器和编译器常常会对指令进行重排序，但是不能随意重排序，需要满足以下两个条件：
- 在单线程环境下不能改变程序运行的结果；
- 存在数据依赖关系的不允许重排序

as-if-serial语义的意思是，所有的操作均可以为了优化而被重排序，但是必须要保证重排序后执行的结果不能被改变，编译器、runtime、处理器都必须遵守as-if-serial语义。注意as-if-serial只保证单线程环境，多线程环境无效。
```java
public class RecordExample1 {
    public static void main(String[] args){
        int a = 1;
        int b = 2;

        try {
            a = 3;           //A
            b = 1 / 0;       //B
        } catch (Exception e) {

        } finally {
            System.out.println("a = " + a);
        }
    }
}
```

按照重排序的规则，操作A与操作B有可能会进行重排序，如果重排序了，B会抛出异常（ / by zero），此时A语句一定会执行不到，那么a还会等于3么？如果按照as-if-serial原则它就改变了程序的结果。其实JVM对异常做了一种特殊的处理，为了保证as-if-serial语义，Java异常处理机制对重排序做了一种特殊的处理：JIT在重排序时会在catch语句中插入错误代偿代码（a = 3）,这样做虽然会导致cathc里面的逻辑变得复杂，但是JIT优化原则是：尽可能地优化程序正常运行下的逻辑，哪怕以catch块逻辑变得复杂为代价。 
