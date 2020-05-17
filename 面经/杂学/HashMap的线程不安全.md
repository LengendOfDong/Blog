# HashMap的线程不安全
HashMap在并发场景下可能存在哪些问题？

1. 数据丢失
2. 数据重复
3. 死循环

在jdk1.7及之前的HashMap中，扩容的时候会调用resize()方法中的transfer方法，在这里由于是头插法所以在多线程情况下可能出现循环链表，所以后面的数据定位到这条链表的时候会造成数据丢失，读取的时候会造成死循环。

在jdk1.8中，HashMap对此进行了优化，resize采用了尾插法，即不改变原来的链表的顺序，所以不会出现1.7的循环链表的问题，但是仍会出现：数据重复和数据丢失问题。

```java
作者：网易云
链接：https://www.zhihu.com/question/28516433/answer/490921378
来源：知乎
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

public V put(K key, V value) {
        if (table == EMPTY_TABLE) {
            inflateTable(threshold);
        }
        if (key == null)
            return putForNullKey(value);
        int hash = hash(key);
        int i = indexFor(hash, table.length);
        for (Entry e = table[i]; e != null; e = e.next) {
            Object k;
            if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
                V oldValue = e.value;
                e.value = value;
                e.recordAccess(this);
                return oldValue;
            }
        }

        modCount++;
        addEntry(hash, key, value, i);
        return null;
    }

 void addEntry(int hash, K key, V value, int bucketIndex) {
        if ((size >= threshold) && (null != table[bucketIndex])) {
            resize(2 * table.length);
            hash = (null != key) ? hash(key) : 0;
            bucketIndex = indexFor(hash, table.length);
        }

        createEntry(hash, key, value, bucketIndex);
    }


    void createEntry(int hash, K key, V value, int bucketIndex) {
        Entry e = table[bucketIndex];
        table[bucketIndex] = new Entry<>(hash, key, value, e);
        size++;
    }
```
如果有两条线程同时执行到这条语句 table[i]=null,时两个线程都会区创建Entry,这样存入会出现数据丢失。 

如果有两个线程同时发现自己都key不存在，而这两个线程的key实际是相同的，在向链表中写入的时候第一线程将e设置为了自己的Entry,而第二个线程执行到了e.next，此时拿到的是最后一个节点，依然会将自己持有是数据插入到链表中，这样就出现了数据 重复。


