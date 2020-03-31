# Java8数值流

## 数值流

背景：<br/>
当计算菜单的热量时，

```
int calories = menu.stream().map(Dish::getCalories).reduce(0,Integer::sum);

```

问题：<br/>
上例中暗含了装箱的成本，每个Integer都必须拆箱成一个原始类型，再进行求和。

解决方法：<br/>
Java8中引入了三个原始类型特化流接口来解决这个问题：IntStream,DoubleStream和LongStream，分别将流中的元素特化为int,long和double，从而避免了暗含的装箱成本。<br/>
1.映射到数值流<br/>
将流转换为特化版本的常用方法时mapToInt，mapToDouble和mapToLong。这些方法和前面说的map方法的工作方式一样，只是它们返回的是一个特化流，而不是Stream。例如，可以用mapToInt对menu中的卡路里求和：

```
int calories  = menu.stream().mapToInt(Dish::getCalories).sum();

```

2.转换回对象流<br/>
同样，一旦有了数值流，你可能会想把它转换回非特化流。

```
IntStream intStream = menu.stream().mapToInt(Dish::getCalories);
Stream&lt;Integer&gt; stream = intStream.boxed();

```

3.默认值OptionalInt<br/>
Optional可以用Integer，String等参考类型来参数化。对于三种原始流特化，也分别有一个Optional原始类型特化版本：OptionalInt,OptionalDouble和OptionalLong。

### 数值范围

Java8引入了两个可以用于IntStream和LongStream的静态方法，帮助生成这种范围：range和rangeClosed。这两个方法都是第一个参数接受起始值，第二个参数接受结束值。但range是不包含结束值的，而rangeClosed则包含结束值。例如选出1到100中的偶数个数：

```
IntStream evenNumbers = IntStream.rangeClosed(1,100).filter(n -&gt; n % 2 == 0);
System.out.println(evenNumbers.count());

```

上例的结果返回的是50，rangeClosed是算上结束值的。<br/>
如果改用IntStream.range(1,100),则结果将会是49个偶数，因为range是不包含结束值的。

## 构建流

### 由值创建流

可以使用静态方法Stream.of，通过显式值创建一个流。它可以接受任意数量的参数。例如，下面的代码直接使用Stream.of创建一个字符串流。

```
Stream&lt;String&gt; stream = Stream.of("Java 8","Lambda","In ","Action");
stream.map(String::toUpperCase).forEach(System.out::println);

```

另外，还可以使用empty得到一个空流，如下所示：

```
Stream&lt;String&gt; emptyStream = Stream.empty();

```

### 由数组创建流

可以使用静态方法Arrays.stream从数组创建一个流，接受一个数组作为参数。例如，可以将一个原始类型int的数组转换成一个IntStream,如下所示：

```
int[] numbers = {2,3,5,7,11,13};
int sum = Arrays.stream(numbers).sum();

```

### 从文件生成流

Java中用于处理文件等I/O操作的NIO api已更新，以便利用Stream api。java.nio.file.Files中的很多静态方法都会返回一个流。例如，一个很有用的方法是Files.lines,它会返回一个由指定文件中的各行构成的字符串流。例如下面的例子中查看一个文件中有多少个不相同的词：

```
long uniqueWords = 0;
try(Stream&lt;String&gt; lines = 
		Files.lines(Paths.get("data.txt"),Charset.defaultCharset())){
	uniqueWords = lines.flatMap( line -&gt; Arrays.stream(line.split(" ")))
			.distinct().count()
	)
}catch(Exception e){

}

```

### 由函数生成流：创建无限流

Stream API提供了两个静态方法来从函数生成流：Stream.iterate和Stream.generate.这两个操作可以创建所谓的无限流：不像从固定集合创建的流那样有固定大小的流。由iterate和generate产生的流会用给定的函数按需创建值，因此可以无穷无尽地计算下去。一般来说，应该使用limit(n)来对这种流加以限制，以避免打印无穷多个值。<br/>
1.迭代

```
Stream.iterate(0,n -&gt; n + 2）.limit(10).forEach(System.out::println);

```

上例中每次生成的值都作为下次循环的参数，初始值为0，每次迭代增加2，2，4，6，8…<br/>
2.生成<br/>
与iterate方法类似，generate方法也可让你按需生成一个无限流。但generate不是依次对每个新生成的值应用函数 的。它接受一个Supplier类型的Lambda提供新的值。

```
Stream.generate(Math::random)
		.limit(5).forEach(System.out::println);

```
