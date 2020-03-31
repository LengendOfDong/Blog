# 原创：并发编程学习笔记（八）------ volatile的内存语义

注明：<br/>
参考书作者：方腾飞 魏鹏 程晓明<br/>
参考书目：《Java 并发编程的艺术》

# volatile的内存语义

## volatile的特性

  理解volatile特性的一个好方法是把对volatile变量的单个读/写，看成是使用同一个锁对这些单个读/写操作做了同步。下面通过具体的示例来说明：

```
class VolatileFeatureExample{
	volatile long v1 = 0L;                     //使用volatile声明64位的long型变量
	public void set(long l){                     
		v1 = l;                               //单个volatile变量的写
	}
	public void getAndIncrement(){
		v1 ++;                                //复合（多个）volatile变量的读/写
	}
	public long get(){
		return v1;                            //单个volatile变量的读
	}
		
}

```

假设有多个线程分别调用上面程序的3个方法，这个程序在语义上和下面程序等价。

```
class VolatileFeatureExample{
	long v1 = 0L;								//64位的long型普通变量
	public synchronized void set(long l){		//对单个的普通变量的写用同一个锁同步
		v1 = l;	
	}
	public void getAndIncrement（）{				//普通方法调用   
		long temp = get();						//调用已同步的读方法
		temp += 1L;								//普通写操作
		set(temp);								//调用已同步的写方法
	}
	public synchronized long get(){				//对单个的普通变量的读用同一个锁同步
		return v1;
	}
}

```

  如上面示例程序所示，一个volatile变量的单个读/写操作，与一个普通变量的读/写操作都是使用一个锁来同步，他们之间的执行效果相同。<br/>
  锁的happens-before规则保证释放锁和获取锁的两个线程之间的内存可见性，这意味着对一个volatile变量的读，总是能看到（任意线程）对这个volatile变量最后的写入。<br/>
  锁的语义决定了临界区代码的执行具有原子性。这意味着，即使是64位的long型和double型变量，只要它是volatile变量，对该变量的读/写就具有原子性。如果是多个volatile操作或类似于volatile++这种复合操作，这些操作整体上不具有原子性。<br/>
简而言之，volatile变量自身具有下列特性：

## volatile写-读建立的happens-before关系

  上面讲的volatile变量自身的特性，对程序员来说，volatile对线程的内存可见性的影响比volatile自身的特性更为重要，也更需要我们去关注。<br/>
  从JSR-133开始（即从JDK5开始），volatile变量的写-读可以实现线程之间的通信。<br/>
  从内存语义的角度来说，volatile的写-读与锁的释放-获取有相同的内存效果：volatile写和锁的释放有相同的内存语义;volatile读与锁的获取有相同的内存语义。<br/>
下面是使用volatile变量的示例代码：

```
class VolatileExample{
	int a = 0;
	volatile boolean flag = false;
	public void writer(){
		a = 1;					//1
		flag = true;			//2
	}
	public void reader(){
		if(flag){				//3
			int i = a;			//4
			...
		}
	}
}

```

假设线程A执行writer()方法之后，线程B执行reader()方法。根据happens-before规则，这个过程建立的happens-before关系可以分为3类：<br/>
1）根据程序次序规则，1 happens-before 2; 3 happens-before  4<br/>
2）根据volatile 规则，2  happens-before 3。<br/>
3）根据happens-before的传递性规则，1  happens-before 4。<br/>
上述happens-before关系的图形化表现形式如下：<br/>
<img alt="在这里插入图片描述" src="https://img-blog.csdnimg.cn/20190721230929503.png?"/><br/>
  在上图中，每一个箭头链接的两个节点，代表了一个happens-before关系。黑色箭头表示程序顺序规则；橙色箭头表示volatile规则，蓝色箭头表示组合这些规则后提供的happens-before保证。<br/>
  这里A线程写一个volatile变量后，B线程读同一个volatile变量。A线程在写volatile变量之前所有可见的共享变量，在B线程读同一个volatile变量后，将立即变得对B线程可见。

## volatile写-读的内存语义

volatile写的内存语义：当写一个volatile变量时，JMM会把该线程对应的本地内存中的共享变量值刷新到主内存。<br/>
  以上面的示例程序VolatileExample为例，假设线程A首先执行writer()方法，随后线程B执行reader()方法，初始时两个线程的本地内存中的flag和a都是初始状态。下图是线程A执行volatile后，共享变量的状态示意图。<br/>
<img alt="在这里插入图片描述" src="https://img-blog.csdnimg.cn/20190721235017712.png?"/><br/>
如上图所示，线程A在写flag变量后，本地内存A中被线程A更新过的两个共享变量的值被刷新到主内存中。此时，本地内存A和主内存中的共享变量的值是一致的。<br/>
volatile读的内存语义：当读一个volatile变量时，JMM会把该线程对应的本地内存置为无效。线程接下来将从主内存中读取共享变量。<br/>
下图为线程B读同一个volatile变量后，共享变量的状态示意图：<br/>
<img alt="在这里插入图片描述" src="https://img-blog.csdnimg.cn/20190722000003875.png?"/><br/>
如上图所示，在读flag变量后，本地内存B包含的值已经被置为无效。此时，线程B必须从主内存中读取共享变量。线程B的读取操作将导致本地内存B与主内存中的共享变量的值变成一致。<br/>
  如果我们把volatile写和volatile读两个步骤综合起来看的话，在读线程B读一个volatile变量后，写线程A在写这个volatile变量之前所有可见的共享变量的值都将立即变得对读线程B可见。<br/>
下面对volatile写和volatile读的内存语义做个总结：

## volatile内存语义的实现

为了实现volatile内存语义，JMM会分别限制编译器重排序和处理器重排序。<br/>
基于保守策略的JMM内存屏障插入策略：

```
class VolatileBarrierExample{
	int a;
	volatile int v1 = 1;
	volatile int v2 = 2;
	void readAndWrite(){
		int i = v1;				//第一个volatile读
		int j = v2;	        	//第二个volatile读
		a = i + j;				//普通写
		v1 = i + 1;				//第一个volatile写
		v2 = j * 2;				//第二个volatile写
	}	
	... 						//其他方法
}

```

  针对readAndWrite()方法，编译器在生成字节码时可以做如下优化：<br/>
<img alt="在这里插入图片描述" src="https://img-blog.csdnimg.cn/20190722235226931.png?"/><br/>
  上图中，最后的StoreLoad屏障不能省略。因为第二个volatile写之后，方法立即return。此时编译器可能无法准确断定后面是否会有voaltile读或写，为了安全起见，编译器通常会在这里插入一个StoreLoad屏障。<br/>
  X86处理器仅会对写-读操作做重排序。X86不会对读-读，读-写和写-写操作做重排序，因此在X86处理器中会省略掉这3中操作类型对应的内存屏障。在X86中，JMM仅需在volatile写后面插入一个StoreLoad屏障即可正确实现volatile写-读的内存语义。这意味着在X86处理器中，volatile写的开销比volatile读的开销会大很多。<br/>
保守策略下的读和写，X86处理器可以优化成下图：<br/>
<img alt="在这里插入图片描述" src="https://img-blog.csdnimg.cn/20190724065426219.png?"/><br/>
  由于volatile仅仅保证对单个volatile变量的读/写具有原子性，而锁的互斥执行的特性可以确保对整个临界区代码的执行具有原子性。在功能上，锁比volatile更强大；在可伸缩性和执行性能上，volatile更有优势。
