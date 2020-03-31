# 转载：[Java集合] 彻底搞懂HashMap,HashTable,ConcurrentHashMap之关联（各大公司面试经常遇到）


注: 今天看到的一篇讲hashMap,hashTable,concurrentHashMap很透彻的一篇文章, 感谢原作者的分享. <br/>
原文地址: [http://blog.csdn.net/zhangerqing/article/details/8193118](http://blog.csdn.net/zhangerqing/article/details/8193118) <br/>
<br/>
Java集合类是个非常重要的知识点，HashMap、HashTable、ConcurrentHashMap等算是集合类中的重点，可谓“重中之重”，首先来看个问题，如面试官问你：HashMap和HashTable有什么区别，一个比较简单的回答是：


1、HashMap是非线程安全的，HashTable是线程安全的。


2、HashMap的键和值都允许有null值存在，而HashTable则不行。


3、因为线程安全的问题，HashMap效率比HashTable的要高。


能答出上面的三点，简单的面试，算是过了，但是如果再问：Java中的另一个线程安全的与HashMap极其类似的类是什么？同样是线程安全，它与HashTable在线程同步上有什么不同？能把第二个问题完整的答出来，说明你的基础算是不错的了。带着这个问题，本章开始系**Java之美[从菜鸟到高手演变]系列**之深入解析HashMap和HashTable类应用而生！总想在文章的开头说点儿什么，但又无从说起。从最近的一些面试说起吧，感受就是：知识是永无止境的，永远不要觉得自己已经掌握了某些东西。如果对哪一块知识感兴趣，那么，请多多的花时间，哪怕最基础的东西也要理解它的原理，尽量往深了研究，在学习的同时，记得多与大家交流沟通，因为也许某些东西，从你自己的角度，是很难发现的，因为你并没有那么多的实验环境去发现他们。只有交流的多了，才能及时找出自己的不足，才能认识到：“哦，原来我还有这么多不知道的东西！”。








**一、HashMap的内部存储结构** <br/>
Java中数据存储方式最底层的两种结构，一种是数组，另一种就是链表，数组的特点：连续空间，寻址迅速，但是在删除或者添加元素的时候需要有较大幅度的移动，所以查询速度快，增删较慢。而链表正好相反，由于空间不连续，寻址困难，增删元素只需修改指针，所以查询慢、增删快。有没有一种数据结构来综合一下数组和链表，以便发挥他们各自的优势？答案是肯定的！就是：哈希表。哈希表具有较快（常量级）的查询速度，及相对较快的增删速度，所以很适合在海量数据的环境中使用。一般实现哈希表的方法采用“拉链法”，我们可以理解为“链表的数组”，如下图：


<img alt="" src="https://img-my.csdn.net/uploads/201211/17/1353118778_2052.png"/>


从上图中，我们可以发现哈希表是由数组+链表组成的，一个长度为16的数组中，每个元素存储的是一个链表的头结点。那么这些元素是按照什么样的规则存储到数组中呢。一般情况是通过hash(key)%len获得，也就是元素的key的哈希值对数组长度取模得到。比如上述哈希表中，12%16=12,28%16=12,108%16=12,140%16=12。所以12、28、108以及140都存储在数组下标为12的位置。它的内部其实是用一个Entity数组来实现的，属性有key、value、next。接下来我会从初始化阶段详细的讲解HashMap的内部结构。


<strong><em>1、初始化 <br/>
</em></strong>首先来看三个常量： <br/>
static final int DEFAULT_INITIAL_CAPACITY = 16; 初始容量：16 <br/>
static final int MAXIMUM_CAPACITY = 1  <br/>
&lt;&lt; 30; 最大容量：2的30次方：1073741824 <br/>
static final float DEFAULT_LOAD_FACTOR = 0.75f;  <br/>
装载因子，后面再说它的作用 <br/>
来看个无参构造方法，也是我们最常用的：

<li>
public HashMap() {  </li><li>
        this.loadFactor = DEFAULT_LOAD_FACTOR;  </li><li>
        threshold = (int)(DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);  </li><li>
        table = new Entry[DEFAULT_INITIAL_CAPACITY];  </li><li>
        init();  </li><li>
    }  </li>

loadFactor、threshold的值在此处没有起到作用，不过他们在后面的扩容方面会用到，此处只需理解table=new Entry[DEFAULT_INITIAL_CAPACITY].说明，默认就是开辟16个大小的空间。另外一个重要的构造方法：

<li>
public HashMap(int initialCapacity, float loadFactor) {  </li><li>
        if (initialCapacity &lt; 0)  </li><li>
            throw new IllegalArgumentException("Illegal initial capacity: " +  </li><li>
                                               initialCapacity);  </li><li>
        if (initialCapacity &gt; MAXIMUM_CAPACITY)  </li><li>
            initialCapacity = MAXIMUM_CAPACITY;  </li><li>
        if (loadFactor &lt;= 0 || Float.isNaN(loadFactor))  </li><li>
            throw new IllegalArgumentException("Illegal load factor: " +  </li><li>
                                               loadFactor);  </li><li>
  </li><li>
        // Find a power of 2 &gt;= initialCapacity  </li><li>
        int capacity = 1;  </li><li>
        while (capacity &lt; initialCapacity)  </li><li>
            capacity &lt;&lt;= 1;  </li><li>
  </li><li>
        this.loadFactor = loadFactor;  </li><li>
        threshold = (int)(capacity * loadFactor);  </li><li>
        table = new Entry[capacity];  </li><li>
        init();  </li><li>
    }  </li>

<br/>
就是说传入参数的构造方法，我们把重点放在：

<li>
while (capacity &lt; initialCapacity)  </li><li>
           capacity &lt;&lt;= 1;  </li>

<br/>
上面，该代码的意思是，实际的开辟的空间要大于传入的第一个参数的值。举个例子：<br/>
new HashMap(7,0.8),loadFactor为0.8，capacity为7，通过上述代码后，capacity的值为：8.（1 &lt;&lt; 2的结果是4,2 &lt;&lt; 2的结果为8&lt;此处感谢网友wego1234的指正&gt;）。所以，最终capacity的值为8，最后通过new Entry[capacity]来创建大小为capacity的数组，所以，这种方法最红取决于capacity的大小。<br/>
<em><strong>2、put(Object key,Object value)操作<br/>
</strong></em> <br/>
当调用put操作时，首先判断key是否为null，如下代码1处：

<li>
&lt;p&gt;public V put(K key, V value) {  </li><li>
        if (key == null)  </li><li>
            return putForNullKey(value);  </li><li>
        int hash = hash(key.hashCode());  </li><li>
        int i = indexFor(hash, table.length);  </li><li>
        for (Entry&lt;K,V&gt; e = table[i]; e != null; e = e.next) {  </li><li>
            Object k;  </li><li>
            if (e.hash == hash &amp;&amp; ((k = e.key) == key || key.equals(k))) {  </li><li>
                V oldValue = e.value;  </li><li>
                e.value = value;  </li><li>
                e.recordAccess(this);  </li><li>
                return oldValue;  </li><li>
            }  </li><li>
        }&lt;/p&gt;&lt;p&gt;        modCount++;  </li><li>
        addEntry(hash, key, value, i);  </li><li>
        return null;  </li><li>
    }&lt;/p&gt;  </li>

<br/>
如果**key是null**，则调用如下代码：

<li>
private V putForNullKey(V value) {  </li><li>
        for (Entry&lt;K,V&gt; e = table[0]; e != null; e = e.next) {  </li><li>
            if (e.key == null) {  </li><li>
                V oldValue = e.value;  </li><li>
                e.value = value;  </li><li>
                e.recordAccess(this);  </li><li>
                return oldValue;  </li><li>
            }  </li><li>
        }  </li><li>
        modCount++;  </li><li>
        addEntry(0, null, value, 0);  </li><li>
        return null;  </li><li>
    }  </li>

<br/>
就是说，获取Entry的第一个元素table[0]，并基于第一个元素的next属性开始遍历，直到找到key为null的Entry，将其value设置为新的value值。<br/>
如果没有找到key为null的元素，则调用如上述代码的addEntry(0, null, value, 0);增加一个新的entry，代码如下：

<li>
void addEntry(int hash, K key, V value, int bucketIndex) {  </li><li>
    Entry&lt;K,V&gt; e = table[bucketIndex];  </li><li>
        table[bucketIndex] = new Entry&lt;K,V&gt;(hash, key, value, e);  </li><li>
        if (size++ &gt;= threshold)  </li><li>
            resize(2 * table.length);  </li><li>
    }  </li>

<br/>
先获取第一个元素table[bucketIndex],传给e对象，新建一个entry，key为null，value为传入的value值，next为获取的e对象。如果容量大于threshold，容量扩大2倍。<br/>
如果**key不为null**，这也是大多数的情况，重新看一下源码：

<li>
public V put(K key, V value) {  </li><li>
        if (key == null)  </li><li>
            return putForNullKey(value);  </li><li>
        int hash = hash(key.hashCode());//---------------2---------------  </li><li>
        int i = indexFor(hash, table.length);  </li><li>
        for (Entry&lt;K,V&gt; e = table[i]; e != null; e = e.next) {//--------------3-----------  </li><li>
            Object k;  </li><li>
            if (e.hash == hash &amp;&amp; ((k = e.key) == key || key.equals(k))) {  </li><li>
                V oldValue = e.value;  </li><li>
                e.value = value;  </li><li>
                e.recordAccess(this);  </li><li>
                return oldValue;  </li><li>
            }  </li><li>
        }//-------------------4------------------  </li><li>
        modCount++;//----------------5----------  </li><li>
        addEntry(hash, key, value, i);-------------6-----------  </li><li>
        return null;  </li><li>
    }  </li>

<br/>
看源码中2处，首先会进行key.hashCode()操作，获取key的哈希值，hashCode()是Object类的一个方法，为本地方法，内部实现比较复杂，我们<br/>
会在后面作单独的关于Java中Native方法的分析中介绍。hash()的源码如下：

<li>
static int hash(int h) {  </li><li>
        // This function ensures that hashCodes that differ only by  </li><li>
        // constant multiples at each bit position have a bounded  </li><li>
        // number of collisions (approximately 8 at default load factor).  </li><li>
        h ^= (h &gt;&gt;&gt; 20) ^ (h &gt;&gt;&gt; 12);  </li><li>
        return h ^ (h &gt;&gt;&gt; 7) ^ (h &gt;&gt;&gt; 4);  </li><li>
    }  </li>

int i = indexFor(hash, table.length);的意思，相当于int i = hash % Entry[].length;得到i后，就是在Entry数组中的位置，（~~上述代码5和6处是如果Entry数组中不存在新要增加的元素，则执行5,6处的代码，如果存在，即Hash冲突，则执行 3-4处的代码，此处HashMap中采用链地址法解决Hash冲突~~。此处经网友**bbycszh**指正，发现上述陈述有些问题）。重新解释：其实不管Entry数组中i位置有无元素，都会去执行5-6处的代码，如果没有，则直接新增，如果有，则将新元素设置为Entry[0]，其next指针指向原有对象，即原有对象为Entry[1]。具体方法可以解释为下面的这段文字：（**3-4处的代码只是检查在索引为i的这条链上有没有key重复的，有则替换且返回原值，程序不再去执行5-6处的代码，无则无处理**）


上面我们提到过Entry类里面有一个next属性，作用是指向下一个Entry。如， 第一个键值对A进来，通过计算其key的hash得到的i=0，记做:Entry[0] = A。一会后又进来一个键值对B，通过计算其i也等于0，现在怎么办？HashMap会这样做:B.next = A,Entry[0] = B,如果又进来C,i也等于0,那么C.next = B,Entry[0] = C；这样我们发现i=0的地方其实存取了A,B,C三个键值对,他们通过next这个属性链接在一起,也就是说数组中存储的是最后插入的元素。


到这里为止，HashMap的大致实现，我们应该已经清楚了。当然HashMap里面也包含一些优化方面的实现，这里也说一下。比如：Entry[]的长度一定后，随着map里面数据的越来越长，这样同一个i的链就会很长，会不会影响性能？HashMap里面设置一个因素（也称为因子），随着map的size越来越大，Entry[]会以一定的规则加长长度。<br/>



****2、get(Object key)操作****<br/>
get(Object key)操作时根据键来获取值，如果了解了put操作，get操作容易理解，先来看看源码的实现：

<li>
public V get(Object key) {  </li><li>
        if (key == null)  </li><li>
            return getForNullKey();  </li><li>
        int hash = hash(key.hashCode());  </li><li>
        for (Entry&lt;K,V&gt; e = table[indexFor(hash, table.length)];  </li><li>
             e != null;  </li><li>
             e = e.next) {  </li><li>
            Object k;  </li><li>
            if (e.hash == hash &amp;&amp; ((k = e.key) == key || key.equals(k)))//-------------------1----------------  </li><li>
                return e.value;  </li><li>
        }  </li><li>
        return null;  </li><li>
    }  </li>

<br/>
意思就是：1、当key为null时，调用getForNullKey()，源码如下：

<li>
private V getForNullKey() {  </li><li>
        for (Entry&lt;K,V&gt; e = table[0]; e != null; e = e.next) {  </li><li>
            if (e.key == null)  </li><li>
                return e.value;  </li><li>
        }  </li><li>
        return null;  </li><li>
    }  </li>

2、当key不为null时，先根据hash函数得到hash值，在更具indexFor()得到i的值，循环遍历链表，如果有：key值等于已存在的key值，则返回其value。如上述get()代码1处判断。


总结下HashMap新增put和获取get操作：

<li>
//存储时:  </li><li>
int hash = key.hashCode();  </li><li>
int i = hash % Entry[].length;  </li><li>
Entry[i] = value;  </li><li>
  </li><li>
//取值时:  </li><li>
int hash = key.hashCode();  </li><li>
int i = hash % Entry[].length;  </li><li>
return Entry[i];  </li>

理解了就比较简单。


此处附一个简单的HashMap小算法应用：

<li>
package com.xtfggef.hashmap;  </li><li>
  </li><li>
import java.util.HashMap;  </li><li>
import java.util.Map;  </li><li>
import java.util.Set;  </li><li>
  </li><li>
/** </li><li>
 * 打印在数组中出现n/2以上的元素 </li><li>
 * 利用一个HashMap来存放数组元素及出现的次数 </li><li>
 * @author erqing </li><li>
 * </li><li>
 */  </li><li>
public class HashMapTest {  </li><li>
      </li><li>
    public static void main(String[] args) {  </li><li>
          </li><li>
        int [] a = {2,3,2,2,1,4,2,2,2,7,9,6,2,2,3,1,0};  </li><li>
          </li><li>
        Map&lt;Integer, Integer&gt; map = new HashMap&lt;Integer,Integer&gt;();  </li><li>
        for(int i=0; i&lt;a.length; i++){  </li><li>
            if(map.containsKey(a[i])){  </li><li>
                int tmp = map.get(a[i]);  </li><li>
                tmp+=1;  </li><li>
                map.put(a[i], tmp);  </li><li>
            }else{  </li><li>
                map.put(a[i], 1);  </li><li>
            }  </li><li>
        }  </li><li>
        Set&lt;Integer&gt; set = map.keySet();//------------1------------  </li><li>
        for (Integer s : set) {  </li><li>
            if(map.get(s)&gt;=a.length/2){  </li><li>
                System.out.println(s);  </li><li>
            }  </li><li>
        }//--------------2---------------  </li><li>
    }  </li><li>
}  </li>

此处注意两个地方，map.containsKey()，还有就是上述1-2处的代码。


理解了HashMap的上面的操作，其它的大多数方法都很容易理解了。搞清楚它的内部存储机制，一切OK！<br/>



**二、HashTable的内部存储结构**


HashTable和HashMap采用相同的存储机制，二者的实现基本一致，不同的是：


1、HashMap是非线程安全的，HashTable是线程安全的，内部的方法基本都是synchronized。


2、HashTable不允许有null值的存在。


在HashTable中调用put方法时，如果key为null，直接抛出NullPointerException。其它细微的差别还有，比如初始化Entry数组的大小等等，但基本思想和HashMap一样。


**三、HashTable和ConcurrentHashMap的比较**


如我开篇所说一样，ConcurrentHashMap是线程安全的HashMap的实现。同样是线程安全的类，它与HashTable在同步方面有什么不同呢？


之前我们说，synchronized关键字加锁的原理，其实是对对象加锁，不论你是在方法前加synchronized还是语句块前加，**锁住的都是对象整体**，但是ConcurrentHashMap的同步机制和这个不同，它不是加synchronized关键字，而是基于lock操作的，这样的目的是保证同步的时候，锁住的不是整个对象。事实上，ConcurrentHashMap可以满足concurrentLevel个线程并发无阻塞的操作集合对象。关于concurrentLevel稍后介绍。


****1、构造方法****


为了容易理解，我们先从构造函数说起。ConcurrentHashMap是基于一个叫Segment数组的，其实和Entry类似，如下：

<li>
public ConcurrentHashMap()  </li><li>
  {  </li><li>
    this(16, 0.75F, 16);  </li><li>
  }  </li>

<br/>
默认传入值16，调用下面的方法：

<li>
public ConcurrentHashMap(int paramInt1, float paramFloat, int paramInt2)  </li><li>
  {  </li><li>
    if ((paramFloat &lt;= 0F) || (paramInt1 &lt; 0) || (paramInt2 &lt;= 0))  </li><li>
      throw new IllegalArgumentException();  </li><li>
  </li><li>
    if (paramInt2 &gt; 65536) {  </li><li>
      paramInt2 = 65536;  </li><li>
    }  </li><li>
  </li><li>
    int i = 0;  </li><li>
    int j = 1;  </li><li>
    while (j &lt; paramInt2) {  </li><li>
      ++i;  </li><li>
      j &lt;&lt;= 1;  </li><li>
    }  </li><li>
    this.segmentShift = (32 - i);  </li><li>
    this.segmentMask = (j - 1);  </li><li>
    this.segments = Segment.newArray(j);  </li><li>
  </li><li>
    if (paramInt1 &gt; 1073741824)  </li><li>
      paramInt1 = 1073741824;  </li><li>
    int k = paramInt1 / j;  </li><li>
    if (k * j &lt; paramInt1)  </li><li>
      ++k;  </li><li>
    int l = 1;  </li><li>
    while (l &lt; k)  </li><li>
      l &lt;&lt;= 1;  </li><li>
  </li><li>
    for (int i1 = 0; i1 &lt; this.segments.length; ++i1)  </li><li>
      this.segments[i1] = new Segment(l, paramFloat);  </li><li>
  }  </li>

<br/>
你会发现比HashMap的构造函数多一个参数，paramInt1就是我们之前谈过的initialCapacity，就是数组的初始化大小，paramfloat为loadFactor（装载因子），而paramInt2则是我们所要说的concurrentLevel，这三个值分别被初始化为16,0.75,16，经过：

<li>
while (j &lt; paramInt2) {  </li><li>
      ++i;  </li><li>
      j &lt;&lt;= 1;  </li><li>
    }  </li>

<br/>
后，j就是我们最终要开辟的数组的size值，当paramInt1为16时，计算出来的size值就是16.通过：


this.segments = Segment.newArray(j)后，我们看出了，最终稿创建的Segment数组的大小为16.最终创建Segment对象时：

<li>
this.segments[i1] = new Segment(cap, paramFloat);  </li>

需要cap值，而cap值来源于：

<li>
int k = paramInt1 / j;  </li><li>
  if (k * j &lt; paramInt1)  </li><li>
    ++k;  </li><li>
  int cap = 1;  </li><li>
  while (cap &lt; k)  </li><li>
    cap &lt;&lt;= 1;  </li>

组后创建大小为cap的数组。最后根据数组的大小及paramFloat的值算出了threshold的值：


this.threshold = (int)(paramArrayOfHashEntry.length * this.loadFactor)。


****2、put操作****

<li>
public V put(K paramK, V paramV)  </li><li>
  {  </li><li>
    if (paramV == null)  </li><li>
      throw new NullPointerException();  </li><li>
    int i = hash(paramK.hashCode());  </li><li>
    return segmentFor(i).put(paramK, i, paramV, false);  </li><li>
  }  </li>

<br/>
与HashMap不同的是，如果key为null，直接抛出NullPointer异常，之后，同样先计算hashCode的值，再计算hash值，不过此处hash函数和HashMap中的不一样：

<li>
private static int hash(int paramInt)  </li><li>
  {  </li><li>
    paramInt += (paramInt &lt;&lt; 15 ^ 0xFFFFCD7D);  </li><li>
    paramInt ^= paramInt &gt;&gt;&gt; 10;  </li><li>
    paramInt += (paramInt &lt;&lt; 3);  </li><li>
    paramInt ^= paramInt &gt;&gt;&gt; 6;  </li><li>
    paramInt += (paramInt &lt;&lt; 2) + (paramInt &lt;&lt; 14);  </li><li>
    return (paramInt ^ paramInt &gt;&gt;&gt; 16);  </li><li>
  }  </li>

<br/>
 

<li>
final Segment&lt;K, V&gt; segmentFor(int paramInt)  </li><li>
  {  </li><li>
    return this.segments[(paramInt &gt;&gt;&gt; this.segmentShift &amp; this.segmentMask)];  </li><li>
  }  </li>

<br/>
根据上述代码找到Segment对象后，调用put来操作：

<li>
V put(K paramK, int paramInt, V paramV, boolean paramBoolean)  </li><li>
{  </li><li>
  lock();  </li><li>
  try {  </li><li>
    Object localObject1;  </li><li>
    Object localObject2;  </li><li>
    int i = this.count;  </li><li>
    if (i++ &gt; this.threshold)  </li><li>
      rehash();  </li><li>
    ConcurrentHashMap.HashEntry[] arrayOfHashEntry = this.table;  </li><li>
    int j = paramInt &amp; arrayOfHashEntry.length - 1;  </li><li>
    ConcurrentHashMap.HashEntry localHashEntry1 = arrayOfHashEntry[j];  </li><li>
    ConcurrentHashMap.HashEntry localHashEntry2 = localHashEntry1;  </li><li>
    while ((localHashEntry2 != null) &amp;&amp; (((localHashEntry2.hash != paramInt) || (!(paramK.equals(localHashEntry2.key)))))) {  </li><li>
      localHashEntry2 = localHashEntry2.next;  </li><li>
    }  </li><li>
  </li><li>
    if (localHashEntry2 != null) {  </li><li>
      localObject1 = localHashEntry2.value;  </li><li>
      if (!(paramBoolean))  </li><li>
        localHashEntry2.value = paramV;  </li><li>
    }  </li><li>
    else {  </li><li>
      localObject1 = null;  </li><li>
      this.modCount += 1;  </li><li>
      arrayOfHashEntry[j] = new ConcurrentHashMap.HashEntry(paramK, paramInt, localHashEntry1, paramV);  </li><li>
      this.count = i;  </li><li>
    }  </li><li>
    return localObject1;  </li><li>
  } finally {  </li><li>
    unlock();  </li><li>
  }  </li><li>
}  </li>

<br/>
先调用lock()，lock是ReentrantLock类的一个方法，用当前存储的个数+1来和threshold比较，如果大于threshold，则进行rehash，将当前的容量扩大2倍，重新进行hash。之后对hash的值和数组大小-1进行按位于操作后，得到当前的key需要放入的位置，从这儿开始，和HashMap一样。


从上述的分析看出，****ConcurrentHashMap基于concurrentLevel划分出了多个Segment来对key-value进行存储，从而避免每次锁定整个数组，在默认的情况下，允许16个线程并发无阻塞的操作集合对象，尽可能地减少并发时的阻塞现象。****


在多线程的环境中，相对于HashTable，ConcurrentHashMap会带来很大的性能提升！


**<u>欢迎读者批评指正，有任何建议请联系:</u>**


**<u>EGG：xtfggef@gmail.com      http://weibo.com/xtfggef</u>**


**四、HashMap常见问题分析**


****1、此处我觉得****网友**[huxb23@126](mailto:huxb23@126)**的一篇文章说的很好，[**分析多线程并发写HashMap线程被hang住的原因**](http://blog.163.com/huxb23@126/blog/static/625898182011211318854/) ，因为是优秀的资源，此处我整理下搬到这儿。


以下内容转自博文：[http://blog.163.com/huxb23@126/blog/static/625898182011211318854/](http://blog.163.com/huxb23@126/blog/static/625898182011211318854/) 


先看原问题代码：

<li>
import java.util.HashMap;  </li><li>
  </li><li>
public class TestLock {  </li><li>
  </li><li>
    private HashMap map = new HashMap();  </li><li>
  </li><li>
    public TestLock() {  </li><li>
        Thread t1 = new Thread() {  </li><li>
            public void run() {  </li><li>
                for (int i = 0; i &lt; 50000; i++) {  </li><li>
                    map.put(new Integer(i), i);  </li><li>
                }  </li><li>
                System.out.println("t1 over");  </li><li>
            }  </li><li>
        };  </li><li>
  </li><li>
        Thread t2 = new Thread() {  </li><li>
            public void run() {  </li><li>
                for (int i = 0; i &lt; 50000; i++) {  </li><li>
                    map.put(new Integer(i), i);  </li><li>
                }  </li><li>
  </li><li>
                System.out.println("t2 over");  </li><li>
            }  </li><li>
        };  </li><li>
  </li><li>
        t1.start();  </li><li>
        t2.start();  </li><li>
  </li><li>
    }  </li><li>
  </li><li>
    public static void main(String[] args) {  </li><li>
        new TestLock();  </li><li>
    }  </li><li>
}  </li>

<br/>
就是启了两个线程，不断的往一个非线程安全的HashMap中put内容，put的内容很简单，key和value都是从0自增的整数（这个put的内容做的并不好，以致于后来干扰了我分析问题的思路）。对HashMap做并发写操作，我原以为只不过会产生脏数据的情况，但反复运行这个程序，会出现线程t1、t2被hang住的情况，多数情况下是一个线程被hang住另一个成功结束，偶尔会两个线程都被hang住。说到这里，你如果觉得不好好学习ConcurrentHashMap而在这瞎折腾就手下留情跳过吧。<br/>
好吧，分析下HashMap的put函数源码看看问题出在哪，这里就罗列出相关代码（jdk1.6）：

<li>
public V put(K paramK, V paramV)  </li><li>
{  </li><li>
  if (paramK == null)  </li><li>
    return putForNullKey(paramV);  </li><li>
  int i = hash(paramK.hashCode());  </li><li>
  int j = indexFor(i, this.table.length);  </li><li>
  for (Entry localEntry = this.table[j]; localEntry != null; localEntry = localEntry.next)  </li><li>
  {  </li><li>
    if (localEntry.hash == i) { java.lang.Object localObject1;  </li><li>
      if (((localObject1 = localEntry.key) == paramK) || (paramK.equals(localObject1))) {  </li><li>
        java.lang.Object localObject2 = localEntry.value;  </li><li>
        localEntry.value = paramV;  </li><li>
        localEntry.recordAccess(this);  </li><li>
        return localObject2;  </li><li>
      }  </li><li>
    }  </li><li>
  }  </li><li>
  this.modCount += 1;  </li><li>
  addEntry(i, paramK, paramV, j);  </li><li>
  return null;  </li><li>
}  </li><li>
  </li><li>
private V putForNullKey(V paramV)  </li><li>
{  </li><li>
  for (Entry localEntry = this.table[0]; localEntry != null; localEntry = localEntry.next)  </li><li>
    if (localEntry.key == null) {  </li><li>
      java.lang.Object localObject = localEntry.value;  </li><li>
      localEntry.value = paramV;  </li><li>
      localEntry.recordAccess(this);  </li><li>
      return localObject;  </li><li>
    }  </li><li>
  </li><li>
  this.modCount += 1;  </li><li>
  addEntry(0, null, paramV, 0);  </li><li>
  return null;  </li><li>
}  </li>

 


通过jconsole（或者thread dump），可以看到线程停在了transfer方法的while循环处。这个transfer方法的作用是，当Map中元素数超过阈值需要resize时，它负责把原Map中的元素映射到新Map中。我修改了HashMap，加上了@标记2和@标记3的代码片断，以打印出死循环时的状态，结果死循环线程总是出现类似这样的输出：“Thread-1,e==next:false,e==next.next:true,e:108928=108928,next:108928=108928,eq:true”。<br/>
这个输出表明：<br/>
1）这个Entry链中的两个Entry之间的关系是：e=e.next.next，造成死循环。<br/>
2）e.equals(e.next)，但e!=e.next。因为测试例子中两个线程put的内容一样，并发时可能同一个key被保存了多个value，这种错误是在addEntry函数产生的，但这和线程死循环没有关系。


接下来就分析transfer中那个while循环了。先所说这个循环正常的功能：src[j]保存的是映射成同一个hash值的多个Entry的链表，这个src[j]可能为null，可能只有一个Entry，也可能由多个Entry链接起来。假设是多个Entry，原来的链是(src[j]=a)-&gt;b（也就是src[j]=a,a.next=b,b.next=null），经过while处理后得到了(newTable[i]=b)-&gt;a。也就是说，把链表的next关系反向了。


再看看这个while中可能在多线程情况下引起问题的语句。针对两个线程t1和t2,这里它们可能的产生问题的执行序列做些个人分析：


1）假设同一个Entry列表[e-&gt;f-&gt;...]，t1先到，t2后到并都走到while中。t1执行“e.next = newTable[i];newTable[i] = e;”这使得e.next=null（初始的newTable[i]为null），newTable[i]指向了e。这时t2执行了“e.next = newTable[i];newTable[i] = e;”，这使得e.next=e，e死循环了。因为循环开始处的“final
 Entry next = e.next;”，尽管e自己死循环了，在最后的“e = next;”后，两个线程都会跳过e继续执行下去。


2）在while中逐个遍历Entry链表中的Entry而把next关系反向时，newTable[i]成为了被交换的引用，可疑的语句在于“e.next = newTable[i];”。假设链表e-&gt;f-&gt;g被t1处理成e&lt;-f&lt;-g，newTable[i]指向了g，这时t2进来了，它一执行“e.next = newTable[i];”就使得e-&gt;g，造成了死循环。所以，理论上来说，死循环的Entry个数可能很多。尽管产生了死循环，但是t1执行到了死循环的右边，所以是会继续执行下去的，而t2如果执行“final
 Entry next = e.next;”的next为null，则也会继续执行下去，否则就进入了死循环。


3）似乎情况会更复杂，因为即便线程跳出了死循环，它下一次做resize进入transfer时，有可能因为之前的死循环Entry链表而被hang住（似乎是一定会被hang住）。也有可能，在put检查Entry链表时（@标记1），因为Entry链表的死循环而被hang住。也似乎有可能，活着的线程和死循环的线程同时执行在while里后，两个线程都能活着出去。所以，可能两个线程平安退出，可能一个线程hang在transfer中，可能两个线程都被hang住而又不一定在一个地方。


4）我反复的测试，出现一个线程被hang住的情况最多，都是e=e.next.next造成的，这主要就是例子put两份增量数据造成的。我如果去掉@标记3的输出，有时也能复现两个线程都被hang住的情况，但加上后就很难复现出来。我又把put的数据改了下，比如让两个线程put范围不同的数据，就能复现出e=e.next，两个线程都被hang住的情况。


上面罗哩罗嗦了很多，一开始我简单的分析后觉得似乎明白了怎么回事，可现在仔细琢磨后似乎又不明白了许多。有一个细节是，每次死循环的key的大小也是有据可循的，我就不打哈了。感觉，如果样本多些，可能出现问题的原因点会很多，也会更复杂，我姑且不再蛋疼下去。至于有人提到ConcurrentHashMap也有这个问题，我觉得不大可能，因为它的put操作是加锁的，如果有这个问题就不叫线程安全的Map了。


****2、HashMap中Value可以相同，但是键不可以相同****


当插入HashMap的key相同时，会覆盖原有的Value，且返回原Value值，看下面的程序：

<li>
public class Test {  </li><li>
  </li><li>
    public static void main(String[] args) {  </li><li>
          </li><li>
        HashMap&lt;String,Integer&gt; map = new HashMap&lt;String,Integer&gt;();  </li><li>
  </li><li>
        //出入两个Value相同的值，没有问题  </li><li>
        map.put("egg", 1);  </li><li>
        map.put("niu", 1);  </li><li>
          </li><li>
        //插入key相同的值，看返回结果  </li><li>
        int egg = (Integer) map.put("egg", 3);  </li><li>
          </li><li>
        System.out.println(egg);   //输出1  </li><li>
        System.out.println(map.get("egg"));   //输出3，将原值1覆盖  </li><li>
        System.out.println(map.get("niu"));   //输出1  </li><li>
    }  </li><li>
}  </li>

相同的键会被覆盖，且返回原值。


****3、HashMap按值排序****


给定一个数组，求出每个数据出现的次数并按照次数的由大到小排列出来。我们选用HashMap来做，key存储数组元素，值存储出现的次数，最后用Collections的sort方法对HashMap的值进行排序。代码如下：

<li>
public class Test {  </li><li>
  </li><li>
    public static void main(String[] args) {  </li><li>
  </li><li>
        int data[] = { 2, 5, 2, 3, 5, 2, 3, 5, 2, 3, 5, 2, 3, 5, 2,  </li><li>
                7, 8, 8, 7, 8, 7, 9, 0 };  </li><li>
        Map&lt;Integer, Integer&gt; map = new HashMap&lt;Integer, Integer&gt;();  </li><li>
        for (int i : data) {  </li><li>
            if (map.containsKey(i)) {//判断HashMap里是否存在  </li><li>
                map.put(i, map.get(i) + 1);//已存在，值+1  </li><li>
            } else {  </li><li>
                map.put(i, 1);//不存在，新增  </li><li>
            }  </li><li>
        }  </li><li>
        //map按值排序  </li><li>
        List&lt;Map.Entry&lt;Integer, Integer&gt;&gt; list = new ArrayList&lt;Map.Entry&lt;Integer, Integer&gt;&gt;(  </li><li>
                map.entrySet());  </li><li>
        Collections.sort(list, new Comparator&lt;Map.Entry&lt;Integer, Integer&gt;&gt;() {  </li><li>
            public int compare(Map.Entry&lt;Integer, Integer&gt; o1,  </li><li>
                    Map.Entry&lt;Integer, Integer&gt; o2) {  </li><li>
                return (o2.getValue() - o1.getValue());  </li><li>
            }  </li><li>
        });  </li><li>
        for (Map.Entry&lt;Integer, Integer&gt; m : list) {  </li><li>
            System.out.println(m.getKey() + "-" + m.getValue());  </li><li>
        }  </li><li>
    }  </li><li>
  </li><li>
}  </li>

输出：


2-6<br/>
5-5<br/>
3-4<br/>
8-3<br/>
7-3<br/>
9-1<br/>
0-1

<li>
public HashMap()
 { </li><li>
        this.loadFactor
 = DEFAULT_LOAD_FACTOR; </li><li>
        threshold = (int)(DEFAULT_INITIAL_CAPACITY
 * DEFAULT_LOAD_FACTOR); </li><li>
        table = new Entry[DEFAULT_INITIAL_CAPACITY]; </li><li>
        init(); </li><li>
    } </li>

loadFactor、threshold的值在此处没有起到作用，不过他们在后面的扩容方面会用到，此处只需理解table=new Entry[DEFAULT_INITIAL_CAPACITY].说明，默认就是开辟16个大小的空间。另外一个重要的构造方法：

<li>
public HashMap(int initialCapacity, float loadFactor)
 { </li><li>
        if (initialCapacity
 &lt; 0) </li><li>
            throw new IllegalArgumentException("Illegal
 initial capacity: " + </li><li>
                                               initialCapacity); </li><li>
        if (initialCapacity
 &gt; MAXIMUM_CAPACITY) </li><li>
            initialCapacity = MAXIMUM_CAPACITY; </li><li>
        if (loadFactor
 &lt;= 0 || Float.isNaN(loadFactor)) </li><li>
            throw new IllegalArgumentException("Illegal
 load factor: " + </li><li>
                                               loadFactor); </li><li>
 </li><li>
        // Find a power of 2 &gt;= initialCapacity </li><li>
        int capacity
 = 1; </li><li>
        while (capacity
 &lt; initialCapacity) </li><li>
            capacity &lt;&lt;= 1; </li><li>
 </li><li>
        this.loadFactor
 = loadFactor; </li><li>
        threshold = (int)(capacity
 * loadFactor); </li><li>
        table = new Entry[capacity]; </li><li>
        init(); </li><li>
    } </li>

<br/>
就是说传入参数的构造方法，我们把重点放在：

<li>
while (capacity
 &lt; initialCapacity) </li><li>
           capacity &lt;&lt;= 1; </li>

<br/>
上面，该代码的意思是，实际的开辟的空间要大于传入的第一个参数的值。举个例子： <br/>
new HashMap(7,0.8),loadFactor为0.8，capacity为7，通过上述代码后，capacity的值为：8.（1 &lt;&lt; 2的结果是4,2 &lt;&lt; 2的结果为8&lt;此处感谢网友wego1234的指正&gt;）。所以，最终capacity的值为8，最后通过new Entry[capacity]来创建大小为capacity的数组，所以，这种方法最红取决于capacity的大小。 <br/>
<em><strong>2、put(Object key,Object value)操作 <br/>
</strong></em>  <br/>
当调用put操作时，首先判断key是否为null，如下代码1处：

<li>
&lt;p&gt;public V
 put(K key, V value) { </li><li>
        if (key
 == null) </li><li>
            return putForNullKey(value); </li><li>
        int hash
 = hash(key.hashCode()); </li><li>
        int i
 = indexFor(hash, table.length); </li><li>
        for (Entry&lt;K,V&gt;
 e = table[i]; e != null;
 e = e.next) { </li><li>
            Object k; </li><li>
            if (e.hash
 == hash &amp;&amp; ((k = e.key) == key || key.equals(k))) { </li><li>
                V oldValue = e.value; </li><li>
                e.value = value; </li><li>
                e.recordAccess(this); </li><li>
                return oldValue; </li><li>
            } </li><li>
        }&lt;/p&gt;&lt;p&gt;        modCount++; </li><li>
        addEntry(hash, key, value, i); </li><li>
        return null; </li><li>
    }&lt;/p&gt; </li>

<br/>
如果**key是null**，则调用如下代码：

<li>
private V
 putForNullKey(V value) { </li><li>
        for (Entry&lt;K,V&gt;
 e = table[0]; e != null;
 e = e.next) { </li><li>
            if (e.key
 == null) { </li><li>
                V oldValue = e.value; </li><li>
                e.value = value; </li><li>
                e.recordAccess(this); </li><li>
                return oldValue; </li><li>
            } </li><li>
        } </li><li>
        modCount++; </li><li>
        addEntry(0, null,
 value, 0); </li><li>
        return null; </li><li>
    } </li>

<br/>
就是说，获取Entry的第一个元素table[0]，并基于第一个元素的next属性开始遍历，直到找到key为null的Entry，将其value设置为新的value值。 <br/>
如果没有找到key为null的元素，则调用如上述代码的addEntry(0, null, value, 0);增加一个新的entry，代码如下：

<li>
void addEntry(int hash,
 K key, V value, int bucketIndex)
 { </li><li>
    Entry&lt;K,V&gt; e = table[bucketIndex]; </li><li>
        table[bucketIndex] = new Entry&lt;K,V&gt;(hash,
 key, value, e); </li><li>
        if (size++
 &gt;= threshold) </li><li>
            resize(2 *
 table.length); </li><li>
    } </li>

<br/>
先获取第一个元素table[bucketIndex],传给e对象，新建一个entry，key为null，value为传入的value值，next为获取的e对象。如果容量大于threshold，容量扩大2倍。 <br/>
如果**key不为null**，这也是大多数的情况，重新看一下源码：

<li>
public V
 put(K key, V value) { </li><li>
        if (key
 == null) </li><li>
            return putForNullKey(value); </li><li>
        int hash
 = hash(key.hashCode());//---------------2--------------- </li><li>
        int i
 = indexFor(hash, table.length); </li><li>
        for (Entry&lt;K,V&gt;
 e = table[i]; e != null;
 e = e.next) {//--------------3----------- </li><li>
            Object k; </li><li>
            if (e.hash
 == hash &amp;&amp; ((k = e.key) == key || key.equals(k))) { </li><li>
                V oldValue = e.value; </li><li>
                e.value = value; </li><li>
                e.recordAccess(this); </li><li>
                return oldValue; </li><li>
            } </li><li>
        }//-------------------4------------------ </li><li>
        modCount++;//----------------5---------- </li><li>
        addEntry(hash, key, value, i);-------------6----------- </li><li>
        return null; </li><li>
    } </li>

<br/>
看源码中2处，首先会进行key.hashCode()操作，获取key的哈希值，hashCode()是Object类的一个方法，为本地方法，内部实现比较复杂，我们 <br/>
会在后面作单独的关于Java中Native方法的分析中介绍。hash()的源码如下：

<li>
static int hash(int h)
 { </li><li>
        // This function ensures that
 hashCodes that differ only by </li><li>
        // constant multiples at each
 bit position have a bounded </li><li>
        // number of collisions (approximately
 8 at default load factor). </li><li>
        h ^= (h &gt;&gt;&gt; 20)
 ^ (h &gt;&gt;&gt; 12); </li><li>
        return h
 ^ (h &gt;&gt;&gt; 7) ^ (h &gt;&gt;&gt; 4); </li><li>
    } </li>

int i = indexFor(hash, table.length);的意思，相当于int i = hash % Entry[].length;得到i后，就是在Entry数组中的位置，（~~上述代码5和6处是如果Entry数组中不存在新要增加的元素，则执行5,6处的代码，如果存在，即Hash冲突，则执行 3-4处的代码，此处HashMap中采用链地址法解决Hash冲突~~。此处经网友**bbycszh**指正，发现上述陈述有些问题）。重新解释：其实不管Entry数组中i位置有无元素，都会去执行5-6处的代码，如果没有，则直接新增，如果有，则将新元素设置为Entry[0]，其next指针指向原有对象，即原有对象为Entry[1]。具体方法可以解释为下面的这段文字：（**3-4处的代码只是检查在索引为i的这条链上有没有key重复的，有则替换且返回原值，程序不再去执行5-6处的代码，无则无处理**）


上面我们提到过Entry类里面有一个next属性，作用是指向下一个Entry。如， 第一个键值对A进来，通过计算其key的hash得到的i=0，记做:Entry[0] = A。一会后又进来一个键值对B，通过计算其i也等于0，现在怎么办？HashMap会这样做:B.next = A,Entry[0] = B,如果又进来C,i也等于0,那么C.next = B,Entry[0] = C；这样我们发现i=0的地方其实存取了A,B,C三个键值对,他们通过next这个属性链接在一起,也就是说数组中存储的是最后插入的元素。


到这里为止，HashMap的大致实现，我们应该已经清楚了。当然HashMap里面也包含一些优化方面的实现，这里也说一下。比如：Entry[]的长度一定后，随着map里面数据的越来越长，这样同一个i的链就会很长，会不会影响性能？HashMap里面设置一个因素（也称为因子），随着map的size越来越大，Entry[]会以一定的规则加长长度。 <br/>



****2、get(Object key)操作**** <br/>
get(Object key)操作时根据键来获取值，如果了解了put操作，get操作容易理解，先来看看源码的实现：

<li>
public V
 get(Object key) { </li><li>
        if (key
 == null) </li><li>
            return getForNullKey(); </li><li>
        int hash
 = hash(key.hashCode()); </li><li>
        for (Entry&lt;K,V&gt;
 e = table[indexFor(hash, table.length)]; </li><li>
             e != null; </li><li>
             e = e.next) { </li><li>
            Object k; </li><li>
            if (e.hash
 == hash &amp;&amp; ((k = e.key) == key || key.equals(k)))//-------------------1---------------- </li><li>
                return e.value; </li><li>
        } </li><li>
        return null; </li><li>
    } </li>

<br/>
意思就是：1、当key为null时，调用getForNullKey()，源码如下：

<li>
private V
 getForNullKey() { </li><li>
        for (Entry&lt;K,V&gt;
 e = table[0]; e != null;
 e = e.next) { </li><li>
            if (e.key
 == null) </li><li>
                return e.value; </li><li>
        } </li><li>
        return null; </li><li>
    } </li>

2、当key不为null时，先根据hash函数得到hash值，在更具indexFor()得到i的值，循环遍历链表，如果有：key值等于已存在的key值，则返回其value。如上述get()代码1处判断。


总结下HashMap新增put和获取get操作：

<li>
//存储时: </li><li>
int hash
 = key.hashCode(); </li><li>
int i
 = hash % Entry[].length; </li><li>
Entry[i] = value; </li><li>
 </li><li>
//取值时: </li><li>
int hash
 = key.hashCode(); </li><li>
int i
 = hash % Entry[].length; </li><li>
return Entry[i]; </li>

理解了就比较简单。


此处附一个简单的HashMap小算法应用：

<li>
package com.xtfggef.hashmap; </li><li>
 </li><li>
import java.util.HashMap; </li><li>
import java.util.Map; </li><li>
import java.util.Set; </li><li>
 </li><li>
/** </li><li>
* 打印在数组中出现n/2以上的元素 </li><li>
* 利用一个HashMap来存放数组元素及出现的次数 </li><li>
* @author erqing </li><li>
* </li><li>
*/ </li><li>
public class HashMapTest
 { </li><li>
     </li><li>
    public static void main(String[]
 args) { </li><li>
         </li><li>
        int []
 a = {2,3,2,2,1,4,2,2,2,7,9,6,2,2,3,1,0}; </li><li>
         </li><li>
        Map&lt;Integer, Integer&gt; map = new HashMap&lt;Integer,Integer&gt;(); </li><li>
        for(int i=0;
 i&lt;a.length; i++){ </li><li>
            if(map.containsKey(a[i])){ </li><li>
                int tmp
 = map.get(a[i]); </li><li>
                tmp+=1; </li><li>
                map.put(a[i], tmp); </li><li>
            }else{ </li><li>
                map.put(a[i], 1); </li><li>
            } </li><li>
        } </li><li>
        Set&lt;Integer&gt; set = map.keySet();//------------1------------ </li><li>
        for (Integer
 s : set) { </li><li>
            if(map.get(s)&gt;=a.length/2){ </li><li>
                System.out.println(s); </li><li>
            } </li><li>
        }//--------------2--------------- </li><li>
    } </li><li>
} </li>

此处注意两个地方，map.containsKey()，还有就是上述1-2处的代码。


理解了HashMap的上面的操作，其它的大多数方法都很容易理解了。搞清楚它的内部存储机制，一切OK！ <br/>



**二、HashTable的内部存储结构**


HashTable和HashMap采用相同的存储机制，二者的实现基本一致，不同的是：


1、HashMap是非线程安全的，HashTable是线程安全的，内部的方法基本都是synchronized。


2、HashTable不允许有null值的存在。


在HashTable中调用put方法时，如果key为null，直接抛出NullPointerException。其它细微的差别还有，比如初始化Entry数组的大小等等，但基本思想和HashMap一样。


**三、HashTable和ConcurrentHashMap的比较**


如我开篇所说一样，ConcurrentHashMap是线程安全的HashMap的实现。同样是线程安全的类，它与HashTable在同步方面有什么不同呢？


之前我们说，synchronized关键字加锁的原理，其实是对对象加锁，不论你是在方法前加synchronized还是语句块前加，**锁住的都是对象整体**，但是ConcurrentHashMap的同步机制和这个不同，它不是加synchronized关键字，而是基于lock操作的，这样的目的是保证同步的时候，锁住的不是整个对象。事实上，ConcurrentHashMap可以满足concurrentLevel个线程并发无阻塞的操作集合对象。关于concurrentLevel稍后介绍。


****1、构造方法****


为了容易理解，我们先从构造函数说起。ConcurrentHashMap是基于一个叫Segment数组的，其实和Entry类似，如下：

<li>
public ConcurrentHashMap() </li><li>
  { </li><li>
    this(16, 0.75F, 16); </li><li>
  } </li>

<br/>
默认传入值16，调用下面的方法：

<li>
public ConcurrentHashMap(int paramInt1, float paramFloat, int paramInt2) </li><li>
  { </li><li>
    if ((paramFloat
 &lt;= 0F) || (paramInt1 &lt; 0)
 || (paramInt2 &lt;= 0)) </li><li>
      throw new IllegalArgumentException(); </li><li>
 </li><li>
    if (paramInt2
 &gt; 65536) { </li><li>
      paramInt2 = 65536; </li><li>
    } </li><li>
 </li><li>
    int i
 = 0; </li><li>
    int j
 = 1; </li><li>
    while (j
 &lt; paramInt2) { </li><li>
      ++i; </li><li>
      j &lt;&lt;= 1; </li><li>
    } </li><li>
    this.segmentShift
 = (32 - i); </li><li>
    this.segmentMask
 = (j - 1); </li><li>
    this.segments
 = Segment.newArray(j); </li><li>
 </li><li>
    if (paramInt1
 &gt; 1073741824) </li><li>
      paramInt1 = 1073741824; </li><li>
    int k
 = paramInt1 / j; </li><li>
    if (k
 * j &lt; paramInt1) </li><li>
      ++k; </li><li>
    int l
 = 1; </li><li>
    while (l
 &lt; k) </li><li>
      l &lt;&lt;= 1; </li><li>
 </li><li>
    for (int i1
 = 0; i1 &lt; this.segments.length;
 ++i1) </li><li>
      this.segments[i1]
 = new Segment(l, paramFloat); </li><li>
  } </li>

<br/>
你会发现比HashMap的构造函数多一个参数，paramInt1就是我们之前谈过的initialCapacity，就是数组的初始化大小，paramfloat为loadFactor（装载因子），而paramInt2则是我们所要说的concurrentLevel，这三个值分别被初始化为16,0.75,16，经过：

<li>
while (j
 &lt; paramInt2) { </li><li>
      ++i; </li><li>
      j &lt;&lt;= 1; </li><li>
    } </li>

<br/>
后，j就是我们最终要开辟的数组的size值，当paramInt1为16时，计算出来的size值就是16.通过：


this.segments = Segment.newArray(j)后，我们看出了，最终稿创建的Segment数组的大小为16.最终创建Segment对象时：

<li>
this.segments[i1]
 = new Segment(cap,
 paramFloat); </li>

需要cap值，而cap值来源于：

<li>
int k
 = paramInt1 / j; </li><li>
  if (k
 * j &lt; paramInt1) </li><li>
    ++k; </li><li>
  int cap
 = 1; </li><li>
  while (cap
 &lt; k) </li><li>
    cap &lt;&lt;= 1; </li>

组后创建大小为cap的数组。最后根据数组的大小及paramFloat的值算出了threshold的值：


this.threshold = (int)(paramArrayOfHashEntry.length * this.loadFactor)。


****2、put操作****

<li>
public V
 put(K paramK, V paramV) </li><li>
  { </li><li>
    if (paramV
 == null) </li><li>
      throw new NullPointerException(); </li><li>
    int i
 = hash(paramK.hashCode()); </li><li>
    return segmentFor(i).put(paramK,
 i, paramV, false); </li><li>
  } </li>

<br/>
与HashMap不同的是，如果key为null，直接抛出NullPointer异常，之后，同样先计算hashCode的值，再计算hash值，不过此处hash函数和HashMap中的不一样：

<li>
private static int hash(int paramInt) </li><li>
  { </li><li>
    paramInt += (paramInt &lt;&lt; 15 ^ 0xFFFFCD7D); </li><li>
    paramInt ^= paramInt &gt;&gt;&gt; 10; </li><li>
    paramInt += (paramInt &lt;&lt; 3); </li><li>
    paramInt ^= paramInt &gt;&gt;&gt; 6; </li><li>
    paramInt += (paramInt &lt;&lt; 2)
 + (paramInt &lt;&lt; 14); </li><li>
    return (paramInt
 ^ paramInt &gt;&gt;&gt; 16); </li><li>
  } </li>

<br/>
 

<li>
final Segment&lt;K,
 V&gt; segmentFor(int paramInt) </li><li>
  { </li><li>
    return this.segments[(paramInt
 &gt;&gt;&gt; this.segmentShift
 &amp; this.segmentMask)]; </li><li>
  } </li>

<br/>
根据上述代码找到Segment对象后，调用put来操作：

<li>
V put(K paramK, int paramInt,
 V paramV, boolean paramBoolean) </li><li>
{ </li><li>
  lock(); </li><li>
  try { </li><li>
    Object localObject1; </li><li>
    Object localObject2; </li><li>
    int i
 = this.count; </li><li>
    if (i++
 &gt; this.threshold) </li><li>
      rehash(); </li><li>
    ConcurrentHashMap.HashEntry[] arrayOfHashEntry = this.table; </li><li>
    int j
 = paramInt &amp; arrayOfHashEntry.length - 1; </li><li>
    ConcurrentHashMap.HashEntry localHashEntry1 = arrayOfHashEntry[j]; </li><li>
    ConcurrentHashMap.HashEntry localHashEntry2 = localHashEntry1; </li><li>
    while ((localHashEntry2
 != null) &amp;&amp; (((localHashEntry2.hash
 != paramInt) || (!(paramK.equals(localHashEntry2.key)))))) { </li><li>
      localHashEntry2 = localHashEntry2.next; </li><li>
    } </li><li>
 </li><li>
    if (localHashEntry2
 != null) { </li><li>
      localObject1 = localHashEntry2.value; </li><li>
      if (!(paramBoolean)) </li><li>
        localHashEntry2.value = paramV; </li><li>
    } </li><li>
    else { </li><li>
      localObject1 = null; </li><li>
      this.modCount
 += 1; </li><li>
      arrayOfHashEntry[j] = new ConcurrentHashMap.HashEntry(paramK,
 paramInt, localHashEntry1, paramV); </li><li>
      this.count
 = i; </li><li>
    } </li><li>
    return localObject1; </li><li>
  } finally { </li><li>
    unlock(); </li><li>
  } </li><li>
} </li>

<br/>
先调用lock()，lock是ReentrantLock类的一个方法，用当前存储的个数+1来和threshold比较，如果大于threshold，则进行rehash，将当前的容量扩大2倍，重新进行hash。之后对hash的值和数组大小-1进行按位于操作后，得到当前的key需要放入的位置，从这儿开始，和HashMap一样。


从上述的分析看出，****ConcurrentHashMap基于concurrentLevel划分出了多个Segment来对key-value进行存储，从而避免每次锁定整个数组，在默认的情况下，允许16个线程并发无阻塞的操作集合对象，尽可能地减少并发时的阻塞现象。****


在多线程的环境中，相对于HashTable，ConcurrentHashMap会带来很大的性能提升！





**四、HashMap常见问题分析**


****1、此处我觉得****网友**[huxb23@126](mailto:huxb23@126)**的一篇文章说的很好，[**分析多线程并发写HashMap线程被hang住的原因**](http://blog.163.com/huxb23@126/blog/static/625898182011211318854/) ，因为是优秀的资源，此处我整理下搬到这儿。


以下内容转自博文：[http://blog.163.com/huxb23@126/blog/static/625898182011211318854/](http://blog.163.com/huxb23@126/blog/static/625898182011211318854/) 


先看原问题代码：

<li>
import java.util.HashMap; </li><li>
 </li><li>
public class TestLock
 { </li><li>
 </li><li>
    private HashMap
 map = new HashMap(); </li><li>
 </li><li>
    public TestLock()
 { </li><li>
        Thread t1 = new Thread()
 { </li><li>
            public void run()
 { </li><li>
                for (int i
 = 0; i &lt; 50000;
 i++) { </li><li>
                    map.put(new Integer(i),
 i); </li><li>
                } </li><li>
                System.out.println("t1 over"); </li><li>
            } </li><li>
        }; </li><li>
 </li><li>
        Thread t2 = new Thread()
 { </li><li>
            public void run()
 { </li><li>
                for (int i
 = 0; i &lt; 50000;
 i++) { </li><li>
                    map.put(new Integer(i),
 i); </li><li>
                } </li><li>
 </li><li>
                System.out.println("t2 over"); </li><li>
            } </li><li>
        }; </li><li>
 </li><li>
        t1.start(); </li><li>
        t2.start(); </li><li>
 </li><li>
    } </li><li>
 </li><li>
    public static void main(String[]
 args) { </li><li>
        new TestLock(); </li><li>
    } </li><li>
} </li>

<br/>
就是启了两个线程，不断的往一个非线程安全的HashMap中put内容，put的内容很简单，key和value都是从0自增的整数（这个put的内容做的并不好，以致于后来干扰了我分析问题的思路）。对HashMap做并发写操作，我原以为只不过会产生脏数据的情况，但反复运行这个程序，会出现线程t1、t2被hang住的情况，多数情况下是一个线程被hang住另一个成功结束，偶尔会两个线程都被hang住。说到这里，你如果觉得不好好学习ConcurrentHashMap而在这瞎折腾就手下留情跳过吧。 <br/>
好吧，分析下HashMap的put函数源码看看问题出在哪，这里就罗列出相关代码（jdk1.6）：

<li>
public V
 put(K paramK, V paramV) </li><li>
{ </li><li>
  if (paramK
 == null) </li><li>
    return putForNullKey(paramV); </li><li>
  int i
 = hash(paramK.hashCode()); </li><li>
  int j
 = indexFor(i, this.table.length); </li><li>
  for (Entry
 localEntry = this.table[j];
 localEntry != null;
 localEntry = localEntry.next) </li><li>
  { </li><li>
    if (localEntry.hash
 == i) { java.lang.Object localObject1; </li><li>
      if (((localObject1
 = localEntry.key) == paramK) || (paramK.equals(localObject1))) { </li><li>
        java.lang.Object localObject2 = localEntry.value; </li><li>
        localEntry.value = paramV; </li><li>
        localEntry.recordAccess(this); </li><li>
        return localObject2; </li><li>
      } </li><li>
    } </li><li>
  } </li><li>
  this.modCount
 += 1; </li><li>
  addEntry(i, paramK, paramV, j); </li><li>
  return null; </li><li>
} </li><li>
 </li><li>
private V
 putForNullKey(V paramV) </li><li>
{ </li><li>
  for (Entry
 localEntry = this.table[0];
 localEntry != null;
 localEntry = localEntry.next) </li><li>
    if (localEntry.key
 == null) { </li><li>
      java.lang.Object localObject = localEntry.value; </li><li>
      localEntry.value = paramV; </li><li>
      localEntry.recordAccess(this); </li><li>
      return localObject; </li><li>
    } </li><li>
 </li><li>
  this.modCount
 += 1; </li><li>
  addEntry(0, null,
 paramV, 0); </li><li>
  return null; </li><li>
} </li>

 


通过jconsole（或者thread dump），可以看到线程停在了transfer方法的while循环处。这个transfer方法的作用是，当Map中元素数超过阈值需要resize时，它负责把原Map中的元素映射到新Map中。我修改了HashMap，加上了@标记2和@标记3的代码片断，以打印出死循环时的状态，结果死循环线程总是出现类似这样的输出：“Thread-1,e==next:false,e==next.next:true,e:108928=108928,next:108928=108928,eq:true”。 <br/>
这个输出表明： <br/>
1）这个Entry链中的两个Entry之间的关系是：e=e.next.next，造成死循环。 <br/>
2）e.equals(e.next)，但e!=e.next。因为测试例子中两个线程put的内容一样，并发时可能同一个key被保存了多个value，这种错误是在addEntry函数产生的，但这和线程死循环没有关系。


接下来就分析transfer中那个while循环了。先所说这个循环正常的功能：src[j]保存的是映射成同一个hash值的多个Entry的链表，这个src[j]可能为null，可能只有一个Entry，也可能由多个Entry链接起来。假设是多个Entry，原来的链是(src[j]=a)-&gt;b（也就是src[j]=a,a.next=b,b.next=null），经过while处理后得到了(newTable[i]=b)-&gt;a。也就是说，把链表的next关系反向了。


再看看这个while中可能在多线程情况下引起问题的语句。针对两个线程t1和t2,这里它们可能的产生问题的执行序列做些个人分析：


1）假设同一个Entry列表[e-&gt;f-&gt;...]，t1先到，t2后到并都走到while中。t1执行“e.next = newTable[i];newTable[i] = e;”这使得e.next=null（初始的newTable[i]为null），newTable[i]指向了e。这时t2执行了“e.next = newTable[i];newTable[i] = e;”，这使得e.next=e，e死循环了。因为循环开始处的“final
 Entry next = e.next;”，尽管e自己死循环了，在最后的“e = next;”后，两个线程都会跳过e继续执行下去。


2）在while中逐个遍历Entry链表中的Entry而把next关系反向时，newTable[i]成为了被交换的引用，可疑的语句在于“e.next = newTable[i];”。假设链表e-&gt;f-&gt;g被t1处理成e&lt;-f&lt;-g，newTable[i]指向了g，这时t2进来了，它一执行“e.next = newTable[i];”就使得e-&gt;g，造成了死循环。所以，理论上来说，死循环的Entry个数可能很多。尽管产生了死循环，但是t1执行到了死循环的右边，所以是会继续执行下去的，而t2如果执行“final
 Entry next = e.next;”的next为null，则也会继续执行下去，否则就进入了死循环。


3）似乎情况会更复杂，因为即便线程跳出了死循环，它下一次做resize进入transfer时，有可能因为之前的死循环Entry链表而被hang住（似乎是一定会被hang住）。也有可能，在put检查Entry链表时（@标记1），因为Entry链表的死循环而被hang住。也似乎有可能，活着的线程和死循环的线程同时执行在while里后，两个线程都能活着出去。所以，可能两个线程平安退出，可能一个线程hang在transfer中，可能两个线程都被hang住而又不一定在一个地方。


4）我反复的测试，出现一个线程被hang住的情况最多，都是e=e.next.next造成的，这主要就是例子put两份增量数据造成的。我如果去掉@标记3的输出，有时也能复现两个线程都被hang住的情况，但加上后就很难复现出来。我又把put的数据改了下，比如让两个线程put范围不同的数据，就能复现出e=e.next，两个线程都被hang住的情况。


上面罗哩罗嗦了很多，一开始我简单的分析后觉得似乎明白了怎么回事，可现在仔细琢磨后似乎又不明白了许多。有一个细节是，每次死循环的key的大小也是有据可循的，我就不打哈了。感觉，如果样本多些，可能出现问题的原因点会很多，也会更复杂，我姑且不再蛋疼下去。至于有人提到ConcurrentHashMap也有这个问题，我觉得不大可能，因为它的put操作是加锁的，如果有这个问题就不叫线程安全的Map了。


****2、HashMap中Value可以相同，但是键不可以相同****


当插入HashMap的key相同时，会覆盖原有的Value，且返回原Value值，看下面的程序：

<li>
public class Test
 { </li><li>
 </li><li>
    public static void main(String[]
 args) { </li><li>
         </li><li>
        HashMap&lt;String,Integer&gt; map = new HashMap&lt;String,Integer&gt;(); </li><li>
 </li><li>
        //出入两个Value相同的值，没有问题 </li><li>
        map.put("egg", 1); </li><li>
        map.put("niu", 1); </li><li>
         </li><li>
        //插入key相同的值，看返回结果 </li><li>
        int egg
 = (Integer) map.put("egg", 3); </li><li>
         </li><li>
        System.out.println(egg);   //输出1 </li><li>
        System.out.println(map.get("egg"));   //输出3，将原值1覆盖 </li><li>
        System.out.println(map.get("niu"));   //输出1 </li><li>
    } </li><li>
} </li>

相同的键会被覆盖，且返回原值。


****3、HashMap按值排序****


给定一个数组，求出每个数据出现的次数并按照次数的由大到小排列出来。我们选用HashMap来做，key存储数组元素，值存储出现的次数，最后用Collections的sort方法对HashMap的值进行排序。代码如下：

<li>
public class Test
 { </li><li>
 </li><li>
    public static void main(String[]
 args) { </li><li>
 </li><li>
        int data[]
 = { 2, 5, 2, 3, 5, 2, 3, 5, 2, 3, 5, 2, 3, 5, 2, </li><li>
                7, 8, 8, 7, 8, 7, 9, 0 }; </li><li>
        Map&lt;Integer, Integer&gt; map = new HashMap&lt;Integer,
 Integer&gt;(); </li><li>
        for (int i
 : data) { </li><li>
            if (map.containsKey(i))
 {//判断HashMap里是否存在 </li><li>
                map.put(i, map.get(i) + 1);//已存在，值+1 </li><li>
            } else { </li><li>
                map.put(i, 1);//不存在，新增 </li><li>
            } </li><li>
        } </li><li>
        //map按值排序 </li><li>
        List&lt;Map.Entry&lt;Integer, Integer&gt;&gt; list = new ArrayList&lt;Map.Entry&lt;Integer,
 Integer&gt;&gt;( </li><li>
                map.entrySet()); </li><li>
        Collections.sort(list, new Comparator&lt;Map.Entry&lt;Integer,
 Integer&gt;&gt;() { </li><li>
            public int compare(Map.Entry&lt;Integer,
 Integer&gt; o1, </li><li>
                    Map.Entry&lt;Integer, Integer&gt; o2) { </li><li>
                return (o2.getValue()
 - o1.getValue()); </li><li>
            } </li><li>
        }); </li><li>
        for (Map.Entry&lt;Integer,
 Integer&gt; m : list) { </li><li>
            System.out.println(m.getKey() + "-" +
 m.getValue()); </li><li>
        } </li><li>
    } </li><li>
 </li><li>
} </li>

输出：


2-6 <br/>
5-5 <br/>
3-4 <br/>
8-3 <br/>
7-3 <br/>
9-1 <br/>
0-1
