# hashmap与hashtable的区别
1、继承的父类不同
Hashtable继承自Dictionary类，而HashMap继承自AbstractMap类。但二者都实现了Map接口。
      
2、线程安全性不同

javadoc中关于hashmap的一段描述如下：此实现不是同步的。如果多个线程同时访问一个哈希映射，而其中至少一个线程从结构上修改了该映射，则它必须保持外部同步。

Hashtable 中的方法是Synchronize的，而HashMap中的方法在缺省情况下是非Synchronize的。在多线程并发的环境下，可以直接使用Hashtable，不需要自己为它的方法实现同步，但使用HashMap时就必须要自己增加同步处理。（结构上的修改是指添加或删除一个或多个映射关系的任何操作；仅改变与实例已经包含的键关联的值不是结构上的修改。）这一般通过对自然封装该映射的对象进行同步操作来完成。如果不存在这样的对象，则应该使用 Collections.synchronizedMap 方法来“包装”该映射。最好在创建时完成这一操作，以防止对映射进行意外的非同步访问，如下所示：
```java
Map m = Collections.synchronizedMap(new HashMap(...));
```
Hashtable 线程安全很好理解，因为它每个方法中都加入了Synchronize。这里我们分析一下HashMap为什么是线程不安全的：

HashMap底层是一个Entry数组，当发生hash冲突的时候，hashmap是采用链表的方式来解决的，在对应的数组位置存放链表的头结点。对链表而言，新加入的节点会从头结点加入。
      
3、是否提供contains方法

HashMap把Hashtable的contains方法去掉了，改成containsValue和containsKey，因为contains方法容易让人引起误解。

Hashtable则保留了contains，containsValue和containsKey三个方法，其中contains和containsValue功能相同。

Hashtable的Containskey方法与ContainsValue的源码：
```java

    public boolean containsValue(Object value) {      
         return contains(value);      
     }  
```

```java
    // 判断Hashtable是否包含“值(value)”      
     public synchronized boolean contains(Object value) {      
         //注意，Hashtable中的value不能是null，      
         // 若是null的话，抛出异常!      
         if (value == null) {      
             throw new NullPointerException();      
         }      
        
         // 从后向前遍历table数组中的元素(Entry)      
         // 对于每个Entry(单向链表)，逐个遍历，判断节点的值是否等于value      
         Entry tab[] = table;      
         for (int i = tab.length ; i-- > 0 ;) {      
             for (Entry<K,V> e = tab[i] ; e != null ; e = e.next) {      
                 if (e.value.equals(value)) {      
                     return true;      
                 }      
             }      
         }      
         return false;      
     }  
```

```java
    // 判断Hashtable是否包含key      
     public synchronized boolean containsKey(Object key) {      
         Entry tab[] = table;      
    /计算hash值，直接用key的hashCode代替    
         int hash = key.hashCode();        
         // 计算在数组中的索引值     
         int index = (hash & 0x7FFFFFFF) % tab.length;      
         // 找到“key对应的Entry(链表)”，然后在链表中找出“哈希值”和“键值”与key都相等的元素      
         for (Entry<K,V> e = tab[index] ; e != null ; e = e.next) {      
             if ((e.hash == hash) && e.key.equals(key)) {      
                 return true;      
             }      
         }      
         return false;      
     }  
```
再看下HashMap的ContainsKey和ContainsValue的源码：
```java
    // HashMap是否包含key      
        public boolean containsKey(Object key) {      
            return getEntry(key) != null;      
        }  
```
```java
    // 返回“键为key”的键值对      
        final Entry<K,V> getEntry(Object key) {      
            // 获取哈希值      
            // HashMap将“key为null”的元素存储在table[0]位置，“key不为null”的则调用hash()计算哈希值      
            int hash = (key == null) ? 0 : hash(key.hashCode());      
            // 在“该hash值对应的链表”上查找“键值等于key”的元素      
            for (Entry<K,V> e = table[indexFor(hash, table.length)];      
                 e != null;      
                 e = e.next) {      
                Object k;      
                if (e.hash == hash &&      
                    ((k = e.key) == key || (key != null && key.equals(k))))      
                    return e;      
            }      
            return null;      
        }  
```
```java
    // 是否包含“值为value”的元素      
        public boolean containsValue(Object value) {      
        // 若“value为null”，则调用containsNullValue()查找      
        if (value == null)      
                return containsNullValue();      
         
        // 若“value不为null”，则查找HashMap中是否有值为value的节点。      
        Entry[] tab = table;      
            for (int i = 0; i < tab.length ; i++)      
                for (Entry e = tab[i] ; e != null ; e = e.next)      
                    if (value.equals(e.value))      
                        return true;      
        return false;      
        }  
```

4、key和value是否允许null值

其中key和value都是对象，并且不能包含重复key，但可以包含重复的value。

通过上面的ContainsKey方法和ContainsValue的源码我们可以很明显的看出：

Hashtable中，key和value都不允许出现null值。但是如果在Hashtable中有类似put(null,null)的操作，编译同样可以通过，因为key和value都是Object类型，但运行时会抛出NullPointerException异常，这是JDK的规范规定的。
HashMap中，null可以作为键，这样的键只有一个；可以有一个或多个键所对应的值为null。当get()方法返回null值时，可能是 HashMap中没有该键，也可能使该键所对应的值为null。因此，在HashMap中不能由get()方法来判断HashMap中是否存在某个键， 而应该用containsKey()方法来判断。

5、两个遍历方式的内部实现上不同

Hashtable、HashMap都使用了 Iterator。而由于历史原因，Hashtable还使用了Enumeration的方式 。
      
6、hash值不同

哈希值的使用不同，HashTable直接使用对象的hashCode。而HashMap重新计算hash值。

hashCode是jdk根据对象的地址或者字符串或者数字算出来的int类型的数值。

Hashtable计算hash值，直接用key的hashCode()，而HashMap重新计算了key的hash值，Hashtable在求hash值对应的位置索引时，用取模运算，而HashMap在求位置索引时，则用与运算，且这里一般先用hash&0x7FFFFFFF后，再对length取模，&0x7FFFFFFF的目的是为了将负的hash值转化为正值，因为hash值有可能为负数，而&0x7FFFFFFF后，只有符号外改变，而后面的位都不变。

7、内部实现使用的数组初始化和扩容方式不同

HashTable在不指定容量的情况下的默认容量为11，而HashMap为16，Hashtable不要求底层数组的容量一定要为2的整数次幂，而HashMap则要求一定为2的整数次幂。

Hashtable扩容时，将容量变为原来的2倍加1，而HashMap扩容时，将容量变为原来的2倍。

Hashtable和HashMap它们两个内部实现方式的数组的初始大小和扩容的方式。HashTable中hash数组默认大小是11，增加的方式是 old*2+1。
