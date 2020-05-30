# TreeMap的特性
（1）TreeMap的存储结构只有一颗红黑树；

（2）TreeMap中的元素是有序的，按key的顺序排列；

（3）TreeMap比HashMap要慢一些，因为HashMap前面还做了一层桶，寻找元素要快很多；

（4）TreeMap没有扩容的概念；

（5）TreeMap的遍历不是采用传统的递归式遍历；

（6）TreeMap可以按范围查找元素，查找最近的元素；

## 红黑树插入调整
- 无需调整的情况：
  - X为根节点，将X由红染黑，简称rootOVer
  - 父节点P为黑色，BlackParentOver,简称bpOver
- 仅仅需要考虑父节点P为红色的情况，由于性质4，爷爷节点G必定为黑色，分为三种情况：
  - case1:Y为红色，X可左可右；P、Y染黑，G染红，X回溯至G
  - case2:Y为黑色，X为右孩子；左旋P，X指向P，转化为case3
  - case3:Y为黑色，X为左孩子；P染黑，G染红，右旋G，结束
- 结论：RBT最多旋转两次调整


## AVL插入 与 RBT的插入
- 插入元素都是BST的插入，区别在于调整
- 旋转次数：AVL与RBT均是O（1）
- 指针回溯次数，最好情况：
  - AVL树很早就遇到单旋或者双旋的情况，为O（1）
  - RBT很早就遇到case2或者case3,为O(1)
- 指针回溯次数，最坏情况：
  - 回溯至根节点才发现平衡因子大于1，为logN
  - 不断执行case1，直到根节点，但每次向上回溯两层，为1/2 logN
- 插入效率：RBT略好于AVL
- 查询效率：AVL略好于RBT

