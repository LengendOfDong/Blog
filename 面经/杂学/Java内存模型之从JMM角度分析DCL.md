# 问题分析
```java
public class Singleton {
   private static Singleton singleton;

   private Singleton(){}

   public static Singleton getInstance(){
       if(singleton == null){                              // 1
           synchronized (Singleton.class){                 // 2
               if(singleton == null){                      // 3
                   singleton = new Singleton();            // 4
               }
           }
       }
       return singleton;
   }
}
```

就如上面所示，这个代码看起来很完美，理由如下：

1. 如果检查第一个singleton不为null,则不需要执行下面的加锁动作，极大提高了程序的性能；

2. 如果第一个singleton为null,即使有多个线程同一时间判断，但是由于synchronized的存在，只会有一个线程能够创建对象；

3.当第一个获取锁的线程创建完成后singleton对象后，其他的在第二次判断singleton一定不会为null，则直接返回已经创建好的singleton对象；

实例化一个对象要分为三个步骤：
1. 分配内存空间

2. 初始化对象

3.将内存空间的地址赋值给对应的应用

但是发生重排序的缘故，步骤2.3可能会发生重排序。

解决这个问题有两个解决办法：

1.不允许初始化阶段步骤2.步骤3反生重排序

2.允许初始化阶段步骤2、3发生重排序，但是不允许其他线程”看到“这个重排序

# 解决方案

## 基于volatile的解决方案
```java
public class Singleton {
   //通过volatile关键字来确保安全
   private volatile static Singleton singleton;

   private Singleton(){}

   public static Singleton getInstance(){
       if(singleton == null){
           synchronized (Singleton.class){
               if(singleton == null){
                   singleton = new Singleton();
               }
           }
       }
       return singleton;
   }
}
```
当singleton声明为volatile之后，步骤2、步骤3就不会重排序了，也就可以解决上面的问题了。

## 基于类初始化的解决方案
该解决方案的根本就在于：利用classloader的机制来保证初始化instance时只有一个线程。JVM在类初始化阶段会获取一个锁，这个锁可以同步多个线程对同一个类的初始化。
```java
public class Singleton {
   private static class SingletonHolder{
       public static Singleton singleton = new Singleton();
   }

   public static Singleton getInstance(){
       return SingletonHolder.singleton;
   }
}
```
这种解决方案的实质是：运行步骤2和步骤3重排序，但是不允许其他线程看见。
