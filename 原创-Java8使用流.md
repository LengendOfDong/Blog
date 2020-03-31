# 原创：Java8使用流

## 筛选和切片

### 用谓词筛选

Stream接口支持filter方法。该操作会接受一个谓词（一个返回boolean的函数）作为参数，并返回一个包括所有符合谓词的元素的流。例如筛选出菜单中所有的素菜，创建一张素食菜单：

```
List&lt;Dish&gt; vegetarianMenu = menu.stream().filter(Dish::isVegetarian).collect(toList());

```

### 筛选各异的元素

  流还支持一个叫做distinct的方法，它会返回一个元素各异的流。例如，筛选出列表中所有的偶数，并确保没有重复。

```
List&lt;Integer&gt; numbers = Arrays.asList(1,2,1,3,3,2,4);
numbers.stream().filter(i -&gt; i % 2 == 0 ).distinct().forEach(System.out::println);

```

### 截短流

  流支持limit(n)方法，该方法会返回一个不超过给定长度的流。所需的长度作为参数传递给limit。如果流是有序的，则最多会返回前n个元素。比如，建立一个List，选出热量超过300卡路里的头三道菜：

```
List&lt;Dish&gt; dishes = menu.stream().filter(d -&gt; d.getCalories() &gt; 300).limit(3).collect(toList());

```

**注意**：limit也可以用在无序流上，比如源是一个Set。在这种情况下，limit的结果不会以任何顺序排列。

### 跳过元素

  流还支持skip(n)方法，返回一个扔掉了前n个元素的流。如果流中元素不足n个，则返回一个空流。<br/>
**注意**:limit(n)和skip(n)是互补的，例如，下面的代码将跳过超过300卡路里的头两道菜，并返回剩下的。

```
List&lt;Dish&gt; dishes = menu.stream().filter(d -&gt; d.getCalories() &gt; 300 ).skip(2).collect(toList());

```

## 映射

  一个非常常见的数据处理套路就是从某些对象中选择信息。比如在SQL里，你可以从表中选择一列。Stream API也通过map和flatMap方法提供了类似的工具。

### 对流中每一个元素应用函数

  流支持map方法，它会接受一个函数作为参数。这个函数会被应用到每个元素上，并将其映射成一个新的元素（使用映射一词，是因为它和转换类似，但其中的细微差别在于它是”创建一个新版本“而不是去”修改“)。例如，下面的代码把方法引用Dish::getName传给了map方法，来提取流中菜肴的名称：

```
List&lt;String&gt; dishNames =  menu.stream().map(Dish::getName).collect(toList());

```

如果要找出每道菜的名称有多长，怎么做？可以如下：

```
List&lt;Integer&gt; dishNameLengths = menu.stream().map(Dish::getName).map(String::length).collect(toList());

```

第一步是获取流中的菜肴名称，然后再获取菜肴名称的长度，返回一个List

### 流的扁平化

  对于一张单词表，如何返回一张列表，列出里面各不相同的字符呢？例如，给定单词列表[“Hello”,“World”],你想要返回列表[“H”,“e”,“l”,“o”,“W”,“r”,“d”]。<br/>
可以使用flatMap来达到目的：

```
List&lt;String&gt; uniqueCharacters = words.stream().map(w -&gt; w.split("")).flatMap(Arrays::stream).distinct().collect(Collections.toList())

```

使用flatMap方法的效果是，各个数组并不是分别映射成一个流，而是映射成流的内容（数组中的内容映射出来，形成一个流的内容，实现了流的合并）。所有使用map(Arrays::stream)时生成的单个流都被合并起来，扁平化为一个流。<br/>
总之，flatMap方法让你把一个流中的每个值都换成另一个流，然后把所有的流连接起来成为一个流。

## 查找和匹配

  另一个常见的数据处理套路是看看数据集中的某些元素是否匹配一个给定的属性。Stream API通过allMatch,anyMatch,noneMatch,findFirst和findAny方法提供了这样的工具。

### 检查谓词是否至少匹配一个元素

anyMatch方法可以回答”流中是否有一个元素能匹配给定的谓词“。比如，你可以用它来看看菜单里面是否有素食可选择：

```
if （menu.stream().anyMatch(Dish::isVegetarian)）{
	System.out.println("The menu is vegetarian friendly!")
}

```

### 检查谓词是否匹配所有元素

使用allMatch检查菜品的热量是否都小于1000卡路里

```
boolean isHealthy = menu.stream().allMatch(d -&gt; d.getCalories() &lt; 1000)

```

与此相对的是noneMatch，可以重写上面的例子：

```
boolean isHealthy = menu.stream().noneMatch(d -&gt; d.getCalories() &gt;= 1000)

```

### 查找元素

findAny方法将返回当前流中的任意元素。它可以与其他流操作结合使用。可以结合使用filter和findAny方法来实现查询素食菜肴：

```
Optional&lt;Dish&gt; dish = menu.stream().filter(Dish::isVegetarian).findAny();

```

### 查找第一个元素

  有些流有一个出现顺序来指定流中项目出现的逻辑顺序（比如由List或排序好的数据列生成的流）。给定一个数字列表，下面的代码能找出第一个平方能被3整除的数：

```
List&lt;Integer&gt; someNumbers = Arrays.aslist(1,2,3,4,5);
Optional&lt;Integer&gt; firstSquareDivisibleByThree = 
		someNumbers.stream().map(x -&gt; x * x).filter(x -&gt; x % 3 == 0)
					.findFirst();//9

```

**注**：何时使用findFirst和findAny<br/>
找到第一个元素在并行上限制更多，如果不关心返回的元素是哪个，请使用findAny，因为它在使用并行流时限制更少。

## 归约

### 元素求和

对元素求和：

```
int sum = numbers.stream().reduce(0,(a,b) -&gt; a + b);

```

变形后：

```
int sum = numbers.stream().reduce(0,(a,b) -&gt; Integer.sum(a,b))

```

再进一步变形：

```
int sum = numbers.stream().reduce(0,Integer::sum);

```

对于无初始值的情况：<br/>
reduce还有一个重载的变体，它不接受初始值，但是会返回一个Optional对象：

```
Optional&lt;Integer&gt; sum = numbers.stream().reduce((a,b) -&gt; a + b);

```

当流中没有任何元素是，reduce操作无法返回其和，因为它没有初始值。这就是为什么包裹在一个Optional对象里，以表明和可能不存在。

### 最大值和最小值

计算最大值：

```
Optional&lt;Integer&gt; max = numbers.stream().reduce(Integer::max);

```

计算最小值：

```
Optional&lt;Integer&gt; min = numbers.stream().reduce(Integer::min);

```

计算流中个数：<br/>
方法一：int count = menu.stream().map(d -&gt; 1).reduce(0,(a,b) -&gt; a + b);<br/>
map和reduce的连接通常称为map-reduce模式，因Google用它来进行网络搜索而出名，因为它很容易并行化。<br/>
方法二：long count = menu.stream().count();
