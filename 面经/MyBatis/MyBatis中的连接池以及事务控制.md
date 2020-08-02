# Mybatis连接池使用与分析
连接池：减少我们获取连接所消耗的时间。

连接池就是用于存储连接的一个容器，该集合必须是线程安全的，不能两个线程拿到同一连接，该集合还必须实现队列，做到先进先出。

Mybatis中的连接池提供了3种方式的配置：
- 配置的位置：主配置文件SqlMapConfig.xml中的dataSource标签，type属性就是表示采用何种连接池方式。
- type属性的取值：
  - POOLED：采用传统的javax.sql.DataSource规范中的连接池，mybatis中有针对规范的实现
  - UNPOOLED:采用传统的获取连接的方式，虽然也实现Javax.sql.DataSource接口，但是并没有使用池的思想
  - JNDI：采用服务器提供的JNDI技术实现，来获取DataSource对象，不同的服务器所能拿到的DataSource是不一样的，如果不是web或者maven的war工程，是不能使用的
