# Mybatis连接池使用与分析
连接池：减少我们获取连接所消耗的时间。

连接池就是用于存储连接的一个容器，该集合必须是线程安全的，不能两个线程拿到同一连接，该集合还必须实现队列，做到先进先出。

Mybatis中的连接池提供了3种方式的配置：
- 配置的位置：主配置文件SqlMapConfig.xml中的dataSource标签，type属性就是表示采用何种连接池方式。
- type属性的取值：
  - POOLED：采用传统的javax.sql.DataSource规范中的连接池，mybatis中有针对规范的实现
  - UNPOOLED:采用传统的获取连接的方式，虽然也实现Javax.sql.DataSource接口，但是并没有使用池的思想
  - JNDI：采用服务器提供的JNDI技术实现，来获取DataSource对象，不同的服务器所能拿到的DataSource是不一样的，如果不是web或者maven的war工程，是不能使用的

POOLED:
```java
2020-08-02 16:01:16,446 173    [           main] DEBUG ansaction.jdbc.JdbcTransaction  - Opening JDBC Connection
2020-08-02 16:01:16,715 442    [           main] DEBUG source.pooled.PooledDataSource  - Created connection 326298949.
2020-08-02 16:01:16,717 444    [           main] DEBUG ansaction.jdbc.JdbcTransaction  - Setting autocommit to false on JDBC Connection [com.mysql.cj.jdbc.ConnectionImpl@1372ed45]
2020-08-02 16:01:16,719 446    [           main] DEBUG  com.dong.dao.IUserDao.findAll  - ooo Using Connection [com.mysql.cj.jdbc.ConnectionImpl@1372ed45]
2020-08-02 16:01:16,720 447    [           main] DEBUG  com.dong.dao.IUserDao.findAll  - ==>  Preparing: select * from customer; 
2020-08-02 16:01:16,769 496    [           main] DEBUG  com.dong.dao.IUserDao.findAll  - ==> Parameters: 
2020-08-02 16:01:16,797 524    [           main] DEBUG  com.dong.dao.IUserDao.findAll  - <==      Total: 6
User{id=42, username='小二王', birthday=Sat Mar 03 05:09:37 CST 2018, sex='女', address='北京金燕龙'}
User{id=43, username='小二王', birthday=Mon Mar 05 01:34:34 CST 2018, sex='女', address='北京金燕龙'}
User{id=45, username='传智播客', birthday=Mon Mar 05 02:04:06 CST 2018, sex='男', address='北京金燕龙'}
User{id=46, username='老王', birthday=Thu Mar 08 07:37:26 CST 2018, sex='男', address='北京'}
User{id=48, username='小马宝莉', birthday=Fri Mar 09 01:44:00 CST 2018, sex='女', address='北京修正'}
User{id=49, username='mybatis_test', birthday=Sun Aug 02 16:00:54 CST 2020, sex='?', address='??????'}
2020-08-02 16:01:16,802 529    [           main] DEBUG ansaction.jdbc.JdbcTransaction  - Resetting autocommit to true on JDBC Connection [com.mysql.cj.jdbc.ConnectionImpl@1372ed45]
2020-08-02 16:01:16,803 530    [           main] DEBUG ansaction.jdbc.JdbcTransaction  - Closing JDBC Connection [com.mysql.cj.jdbc.ConnectionImpl@1372ed45]
2020-08-02 16:01:16,803 530    [           main] DEBUG source.pooled.PooledDataSource  - Returned connection 326298949 to pool.
```

UNPOOLED:
```java
2020-08-02 16:02:53,570 186    [           main] DEBUG ansaction.jdbc.JdbcTransaction  - Opening JDBC Connection
2020-08-02 16:02:53,932 548    [           main] DEBUG ansaction.jdbc.JdbcTransaction  - Setting autocommit to false on JDBC Connection [com.mysql.cj.jdbc.ConnectionImpl@31206beb]
2020-08-02 16:02:53,935 551    [           main] DEBUG  com.dong.dao.IUserDao.findAll  - ooo Using Connection [com.mysql.cj.jdbc.ConnectionImpl@31206beb]
2020-08-02 16:02:53,950 566    [           main] DEBUG  com.dong.dao.IUserDao.findAll  - ==>  Preparing: select * from customer; 
2020-08-02 16:02:54,042 658    [           main] DEBUG  com.dong.dao.IUserDao.findAll  - ==> Parameters: 
2020-08-02 16:02:54,093 709    [           main] DEBUG  com.dong.dao.IUserDao.findAll  - <==      Total: 6
User{id=42, username='小二王', birthday=Sat Mar 03 05:09:37 CST 2018, sex='女', address='北京金燕龙'}
User{id=43, username='小二王', birthday=Mon Mar 05 01:34:34 CST 2018, sex='女', address='北京金燕龙'}
User{id=45, username='传智播客', birthday=Mon Mar 05 02:04:06 CST 2018, sex='男', address='北京金燕龙'}
User{id=46, username='老王', birthday=Thu Mar 08 07:37:26 CST 2018, sex='男', address='北京'}
User{id=48, username='小马宝莉', birthday=Fri Mar 09 01:44:00 CST 2018, sex='女', address='北京修正'}
User{id=49, username='mybatis_test', birthday=Sun Aug 02 16:00:54 CST 2020, sex='?', address='??????'}
2020-08-02 16:02:54,102 718    [           main] DEBUG ansaction.jdbc.JdbcTransaction  - Resetting autocommit to true on JDBC Connection [com.mysql.cj.jdbc.ConnectionImpl@31206beb]
2020-08-02 16:02:54,103 719    [           main] DEBUG ansaction.jdbc.JdbcTransaction  - Closing JDBC Connection [com.mysql.cj.jdbc.ConnectionImpl@31206beb]
```
可以看到POOLED比UNPOOLED多了一个操作，”reated connection 326298949.“ 以及"Returned connection 326298949 to pool",POOLED从连接池中获取了连接之后再还回了连接。


