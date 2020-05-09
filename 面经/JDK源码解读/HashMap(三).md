## hash方法

```java
/**
     * Computes key.hashCode() and spreads (XORs) higher bits of hash
     * to lower.  Because the table uses power-of-two masking, sets of
     * hashes that vary only in bits above the current mask will
     * always collide. (Among known examples are sets of Float keys
     * holding consecutive whole numbers in small tables.)  So we
     * apply a transform that spreads the impact of higher bits
     * downward. There is a tradeoff between speed, utility, and
     * quality of bit-spreading. Because many common sets of hashes
     * are already reasonably distributed (so don't benefit from
     * spreading), and because we use trees to handle large sets of
     * collisions in bins, we just XOR some shifted bits in the
     * cheapest possible way to reduce systematic lossage, as well as
     * to incorporate impact of the highest bits that would otherwise
     * never be used in index calculations because of table bounds.
     * 
     * 计算key.hashCode（）并异或哈希的较高位变成较低的值。 由于该表使用2的幂次掩码，因此
     * 仅在当前掩码上方的位上变化的散列将总是相撞。 （众所周知的示例是在小表中保存连续整数的Float键集。）
     * 因此，我们应用了一种将向下扩展较高位的影响的变换。 在速度，实用性和位扩展质量之间需要权衡。 
     * 由于许多常见的哈希集已经合理分布（因此不能从扩展中受益），并且由于我们使用树来处理容器中的大量冲突，
     * 因此我们仅以最便宜的方式对一些移位后的位进行异或，以减少系统损失， 以及合并最高位的影响，
     * 否则由于表范围的限制，这些位将永远不会在索引计算中使用。
     */
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
```
hash方法作为HashMap的核心方法，怎么计算尽可能地减少碰撞。

## h >>> 16 是什么，有什么用?
h是hashcode。h >>> 16是用来取出h的高16，(>>>是无符号右移)
```java
0000 0100 1011 0011  1101 1111 1110 0001
 
>>> 16 
 
0000 0000 0000 0000  0000 0100 1011 0011
```

##  为什么 h = key.hashCode()) 与 (h >>> 16) 异或？
首先，需要看hash值使用的地方。

在jdk1.8中，主要在getNode方法中使用hash值
```java
final Node<K,V> getNode(int hash, Object key) {
        Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
        if ((tab = table) != null && (n = tab.length) > 0 &&
            (first = tab[(n - 1) & hash]) != null) {
            if (first.hash == hash && // always check first node
                ((k = first.key) == key || (key != null && key.equals(k))))
                return first;
            if ((e = first.next) != null) {
                if (first instanceof TreeNode)
                    return ((TreeNode<K,V>)first).getTreeNode(hash, key);
                do {
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        return e;
                } while ((e = e.next) != null);
            }
        }
        return null;
    }
```
先不看其他，只看与hash有关的部分**tab[(n - 1) & hash]**。

在jdk1.7中，使用indexFor方法中使用hash值
```java
static int indexFor(int h, int length) {
    // assert Integer.bitCount(length) == 1 : "length must be a non-zero power of 2";
    return h & (length-1);
}
```

可以看到，在使用hash值的方式都是一样的，没有什么差别。

然后，看下**h & (length - 1)** 代表什么意思

h &（length - 1)与 h % length相同，其中length代表Map的容量，要求是2的幂次。

举例说明：
```java
假如有length 为 8，则length - 1 = 7 ,转换为二进制则为 111，此时h & (length - 1)代表取h的最低三位
解释h % length 之前，先说明一下 h / length，它表示h右移三位，那么移出的值就是余数h % length。
因此 h & (length - 1) = h % length。这也就是我们常说的利用hash值对长度取模。
```

绝大多数情况下，length的长度都小于16位，所以最多只会与最低16位进行

为了方便验证，假设length为8。HashMap的默认初始容量为16

length = 8;  （length-1） = 7；转换二进制为111；

假设一个key的 hashcode = 78897121 转换二进制：100101100111101111111100001，与（length-1）& 运算如下
```java
0000 0100 1011 0011 1101 1111 1110 0001
 
&运算
 
    0000 0000 0000 0000 0000 0000 0000 0111
 
=   0000 0000 0000 0000 0000 0000 0000 0001 （就是十进制1，所以下标为1）
```

上述运算实质是：001 与 111 & 运算。也就是哈希值的低三位与length与运算。如果让哈希值的低三位更加随机，那么&结果就更加随机，如何让哈希值的低三位更加随机，那么就是让其与高位异或。

补充知识：

当length=8时    下标运算结果取决于哈希值的低三位

当length=16时  下标运算结果取决于哈希值的低四位

当length=32时  下标运算结果取决于哈希值的低五位

当length=2的N次方， 下标运算结果取决于哈希值的低N位。

## 总结
由于和（length-1）运算，length 绝大多数情况小于2的16次方。所以始终是hashcode 的低16位（甚至更低）参与运算。要是高16位也参与运算，会让得到的下标更加散列。

所以这样高16位是用不到的，如何让高16也参与运算呢。所以才有hash(Object key)方法。让他的hashCode()和自己的高16位^运算。所以(h >>> 16)得到他的高16位与hashCode()进行^运算。

## 为什么用^而不用&和|？

因为&和|都会使得结果偏向0或者1 ,当为&时，有75%的概率是0，25%的概率是1，当为|时，有75%的概率是1，25%的概率是0，而当为^时，有50%的概率为0，有50%的概率为1，所以经过比较，还是异或^的分布更加均匀。
