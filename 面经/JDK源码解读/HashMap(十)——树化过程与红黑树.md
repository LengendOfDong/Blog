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
从代码中可以看到，在treeifyBin函数中，先将所有节点替换为TreeNode，然后再将单链表转为双链表，方便之后的遍历和移动操作。而最终的操作，实际上是调用TreeNode的方法treeify进行的。
```java
/**
         * Forms tree of the nodes linked from this node.
         * @return root of tree
         */
        final void treeify(Node<K,V>[] tab) {
            TreeNode<K,V> root = null;
            //x是当前节点，next是后继
            for (TreeNode<K,V> x = this, next; x != null; x = next) {
                next = (TreeNode<K,V>)x.next;
                x.left = x.right = null;
                //如果根节点为null,把当前节点设置为根节点
                if (root == null) {
                    x.parent = null;
                    x.red = false;
                    root = x;
                }
                else {
                    K k = x.key;
                    int h = x.hash;
                    Class<?> kc = null;
                    //循环遍历，进行二叉搜索树的插入
                    for (TreeNode<K,V> p = root;;) {
                    //p指向遍历中的当前节点，x为待插入节点，k是x的key,h是x的hash值，ph是p的hash
                    //dir用来指示x节点与p的比较，-1表示比p小，1表示比p大，不存在相等情况，因为
                    //HashMap中是不存在两个key完全一致的情况。
                        int dir, ph;
                        K pk = p.key;
                        if ((ph = p.hash) > h)
                            dir = -1;
                        else if (ph < h)
                            dir = 1;
                        //如果hash相等，那么判断k是否实现了comparable接口，如果实现了comparable接口就
                        //使用compareTo进行比较，如果仍旧相等或者没有实现comparable接口，则在tieBreakOrder中比较。
                        else if ((kc == null &&
                                  (kc = comparableClassFor(k)) == null) ||
                                 (dir = compareComparables(kc, k, pk)) == 0)
                            dir = tieBreakOrder(k, pk);

                        TreeNode<K,V> xp = p;
                        if ((p = (dir <= 0) ? p.left : p.right) == null) {
                            x.parent = xp;
                            if (dir <= 0)
                                xp.left = x;
                            else
                                xp.right = x;
                                //进入插入平衡处理
                            root = balanceInsertion(root, x);
                            break;
                        }
                    }
                }
            }
            //确保给定节点是桶中的第一个元素
            moveRootToFront(tab, root);
        }
```
