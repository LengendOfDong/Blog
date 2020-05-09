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
     * 计算key.hashCode（）并扩展（XOR）哈希的更高位降低。 由于该表使用2的幂次掩码，因此
     * 仅在当前掩码上方的位上变化的散列将总是相撞。 （众所周知的示例是在小表中保存连续整数的Float键集。）
     * 因此，我们应用了一种将向下扩展较高位的影响的变换。 在速度，实用性和位扩展质量之间需要权衡。 
     * 由于许多常见的哈希集已经合理分布（因此不能从扩展中受益），并且由于我们使用树来处理容器中的大量冲突，
     * 因此我们仅以最便宜的方式对一些移位后的位进行XOR，以减少系统损失， 以及合并最高位的影响，
     * 否则由于表范围的限制，这些位将永远不会在索引计算中使用。
     */
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
```
