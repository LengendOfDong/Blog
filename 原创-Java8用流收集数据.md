# 原创：Java8用流收集数据

## 归约和汇总

### 统计总数

利用收集器来统计菜单中有多少中菜：

```
long howManyDishes = menu.stream().collect(Collectors.counting());

```

### 查找流中的最大值和最小值

可以使用两个收集器，Collectors.maxBy和Collectors.minBy,来计算流中的最大值和最小值。这两个收集器接收一个Comparator参数来比较流中的元素。可以创建一个Comparator来根据所含热量对菜肴进行比较，并把它传递给Collectors.maxBy:

```
Comparator&lt;Dish&gt; dishCaloriesComparator = 
	Comparator.comparingInt(Dish::getCalories);
Optional&lt;Dish&gt; mostCaloriesDish = 
	menu.stream().collect(maxBy(dishCaloriesComparator))

```

### 汇总

Collectors类专门为汇总提供了一个工厂方法：Collectors.summingInt。它可接收一个把对象映射为求和所需Int的函数，并返回一个收集器;该收集器在传递给普通的collect方法后即执行我们所需要的汇总操作。举个例子，可以这样求出菜单列表的总热量：

```
int totalCalories = menu.stream().collect(summingInt(Dish::getCalories));

```

另外，汇总不仅仅是求和：还有Collectors.averagingInt，连同对应的averagingLong和averagingDouble可以计算数值的平均数：

```
double avgCalories = 
	menu.stream().collect(averagingInt(Dish::getCalories));

```

通过一次summerizing操作就可以数出菜单中元素的个数，并得到菜肴热量总和、平均值、最大值和最小值：

```
IntSummaryStatistics menuStatistics = 
	menu.stream().collect(summarizingInt(Dish::getCalories));

```

打印menuStaticsticObject会得到：<br/>
IntSummaryStatistics{count=9,sum=4300,min=120,average=477.77778,max=800}

### 连接字符串

joining工厂方法返回的收集器会把对流中每一个对象应用toString方法得到的所有字符串连接成一个字符串。可以使用这种方法，把菜单中的菜肴名称都连接起来，如下所示：

```
String shortMenu = menu.stream().map(Dish::getName()).collect(joining());

```

joining在内部使用了StringBuilder来把生成的字符串逐个追加起来。<br/>
如果Dish类本身就有一个toString方法来返回菜肴的名称，可以使用下面方法：

```
String shortMenu = menu.stream().collect(joining());

```

通过使用joining工厂方法的一个重载版本，可以得到一个逗号分隔的菜肴名称列表：

```
String shortMenu = menu.stream().collect(joining(","));

```

## 分组

假如要把菜单中的菜按照类型进行分类，有肉的放一组，有鱼的放一组，其他的都放另一组。用Collectors.groupingBy工厂方法返回的收集器就可以轻松完成这项任务。

```
Map&lt;Dish.Type,List&lt;Dish&gt;&gt; dishesByType = 
	menu.stream().collect(groupingBy(Dish::getType));

```

结果为下面的Map:<br/>
{FISH=[prawns,salmon],OTHER=[french fries,rice,season fruit,pizza],MEAT=[pork,beef,chicken]}

### 多级分组

要实现多级分组，可以使用一个由双参数版本的Collectors.groupingBy工厂方法创建的收集器。

```
public enum CaloricLevel(DIET,NORMAL,FAT}
Map&lt;Dish.Type,Map&lt;CaloricLevel,List&lt;Dish&gt;&gt;&gt; dishesByTypeCaloricLevel =
    menu.stream().collect(
    	groupingBy(Dish::getType,
    		groupingBy(dish -&gt; {
    			if (dish.getCalories() &lt;= 400) return CaloricLevel.DIET;
    			else if(dish.getCalories() &lt;=700) return CaloricLevel.NORMAL;
    			else return CaloricLevel.FAT;
    			})
    		)
    	);

```

另外groupingBy和其他收集器联合使用也可以，例如groupingBy和mapping收集器结合起来：

```
Map&lt;Dish.Type,Set&lt;CaloricLevel&gt;&gt; caloricLevelByType = 
	menu.stream().collect(
		groupingBy(Dish::getType,mapping(
			dish-&gt; { if (dish.getCalories() &lt;= 400 ) return CaloricLevel.DIET;
			else if (dish.getCalories() &lt;= 700 ) return CaloricLevel.NORMAL;
			else return CaloricLevel.FAT;
			},toSet()
			)
		) 
	);

```

## 分区

分区是分组的特殊情况：由一个谓词（返回一个布尔值的函数）作为分类函数，称为分区函数。

```
Map&lt;Boolean,List&lt;Dish&gt;&gt; partitionedMenu =
	menu.stream().collect(partitioningBy(Dish::isVegetarian));

```

可以看到partitioningBy(Dish::isVegetarian)和groupingBy(Dish::getType)很相似，就是返回的类型不一样罢了。<br/>
找出所有的素食菜肴，利用get方法获得：

```
List&lt;Dish&gt; vegetarianDishes = partitionedMenu.get(true);

```

用下面的方法也可以获得同样的结果：

```
List&lt;Dish&gt; vegetarianDishes = menu.stream().filter(Dish::isVegetarian).collect(toList());

```
