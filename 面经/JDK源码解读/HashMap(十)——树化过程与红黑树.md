# 树化过程

```java
/**
     * Replaces all linked nodes in bin at index for given hash unless
     * table is too small, in which case resizes instead
     */
    final void treeifyBin(Node<K,V>[] tab, int hash) {
        int n, index; Node<K,V> e;
        //如果表为空或者表小于最小树化容量，则进行扩容
        if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY)
            resize();
        else if ((e = tab[index = (n - 1) & hash]) != null) {
            TreeNode<K,V> hd = null, tl = null;
            do {
            //将节点替换为TreeNode
                TreeNode<K,V> p = replacementTreeNode(e, null);
               //初始化第一个节点
                if (tl == null)
                    hd = p;
                else {
                //这里其实是将单链表转化成了双向链表，tl是p的前驱，p是t1的后驱
                //每次循环更新指向双链表的最后一个元素，用来和p相连，p是当前节点
                    p.prev = tl;
                    tl.next = p;
                }
                tl = p;
            } while ((e = e.next) != null);
            if ((tab[index] = hd) != null)
                //将链表进行树形化
                hd.treeify(tab);
        }
    }
```

