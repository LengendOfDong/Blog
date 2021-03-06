# 超越Java8

## 声明式编程

”如何做”风格的编程非常适合经典的面向对象编程，有些时候我们也称之为“命令式”编程，因为它的特点是它的指令和计算机底层的词汇非常相近，比如赋值，条件分支以及循环，就像下面的这段代码：

```
Transaction mostExpensive = transactions.get(0);
if(mostExpensive == null){
	throw new IllegalArgumentException("Empty list of transaction");
}
for(Transaction t: transactions.subList(1,transactions.size())){
	if(t.getValue() &gt; mostExpensive.getValue()){
		mostExpensive = t;
	}
}

```

另一种方式则更加关注要做什么，例如如下：

```
Optional&lt;Transaction&gt; mostExpensive = transactions.stream().max(comparing(Transaction::getValue));

```

这个查询把最终如何实现的细节留给了函数库。我们把这种思想称之为内部迭代。它的巨大优势在于你的查询语句现在读起来就像是问题描述，由于采用了这种方式，我们马上就能理解它的功能，比理解一系列的命令要简洁得多。

采用这种“要做什么”风格的编程通常称为声明式编程。它带来的好处非常明显，用这种方式编写的代码更加接近问题陈述了。

## 函数式编程

编程实战中，你是无法用Java语言以纯粹的函数式来完成一个程序的。比如，Java的I/0模型就包含了带副作用的方法。纯粹的函数式需要使用同样的参数调用数学函数，它所返回的结果一定是相同的。<br/>
我们的准则是，被称为“函数式”的函数或方法都只能修改本地变量。除此之外，它引用的对象都应该是不可修改的对象。通过这种规定，我们期望所有的字段都为final类型，所有的引用类型都指向不可变对象。<br/>
如果你的共享变量在某一刻是改变的，那么这个变量在多线程程序中就是有风险的。你用加锁的方式来对方法的方法体进行封装，掩盖这一问题，但是这样做了之后，就丧失了多核处理器并发执行两个方法调用的能力。<br/>
作为函数式地程序，你的函数或方法调用的库函数如果有副作用，你必须设法隐藏它们的非函数式行为，否则就不能调用这些方法。

## 引用透明性

”没有可感知的副作用“（不改变对调用者可见的变量、不进行I/O、不抛出异常）的这些限制都隐含着引用透明性。如果一个函数只要传递同样的参数值，总是返回同样的结果，那这个函数就是引用透明的。<br/>
换句话说，函数无论在何处、何时调用，如果使用同样的输入总能持续地得到相同的结果，就具备了函数式的特征。所以，Random.nextInt不是函数式的方法，而final int 类型的变量相加总能得到相同的结果。<br/>
引用透明性是理解程序的一个重要属性。
