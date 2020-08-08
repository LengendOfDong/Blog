# 什么是缓存
缓存是存在于内存中的临时数据

# 为什么使用缓存
缓存能够减少数据库的交互次数，提高执行效率

# 什么样的数据能使用缓存，什么样的数据不能使用
适用于缓存：经常查询并且不经常改变的，数据的正确与否对最终结果影响不大的

不适用于缓存：经常改变的数据，数据的正确性与否对最终结果影响很大的，例如：商品的库存，银行的汇率，股市的牌价

# Mybatis中的一级缓存和二级缓存
一级缓存：

它指的是Mybatis中SqlSession对象的缓存，当我们执行查询之后，查询的结果会同时存入到SqlSession为我们提供一块区域中。

该区域的结构是一个Map，当我们再次查询同样的数据，mybatis会先去sqlsession中查询是否有，有的话直接拿出来使用。

当SqlSession消失的时候，一级缓存也就消失了。

比如sqlsession.close()可以是关闭sqlsession对象，此时一级缓存就消失了

另外，调用sqlsession.clearCache()也可以清除缓存

一级缓存是SqlSession范围的缓存，当调用SqlSession的修改，添加，删除，commit()，close()等方法时，就会清空一级缓存。


二级缓存：

它指的是Mybatis中SqlSessionFactory对象的缓存。由同一个SqlSessionFactory对象创建的SqlSession共享其缓存。

二级缓存的使用步骤：
- 第一步：让Mybatis框架支持二级缓存（SqlMapConfig.xml中配置）
- 第二步：让当前的映射文件支持二级缓存（在IUserDao.xml中配置）
- 第三步：在当前的操作支持二级缓存（在select标签中配置）

