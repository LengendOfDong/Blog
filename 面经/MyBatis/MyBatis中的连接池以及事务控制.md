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

POOLED从池中获取一个连接来用，而UNPOOLED是每次创建一个新的连接来用。


```java
private PooledConnection popConnection(String username, String password) throws SQLException {
    boolean countedWait = false;
    PooledConnection conn = null;
    long t = System.currentTimeMillis();
    int localBadConnectionCount = 0;

    //连接为空，需要继续获取连接
    while (conn == null) {
      //需要对状态进行锁定，此处state可以理解为资源集合
      synchronized (state) {
        //资源中空闲连接集合不为空
        if (state.idleConnections.size() > 0) {
          // Pool has available connection
          // 池中有可用的连接，取出第一个赋值给conn,这样就能够退出循环了
          conn = state.idleConnections.remove(0);
          if (log.isDebugEnabled()) {
            log.debug("Checked out connection " + conn.getRealHashCode() + " from pool.");
          }
          //资源中空闲连接集合为空
        } else {
          // Pool does not have available connection
          //活动连接池没有达到最大数量限制
          if (state.activeConnections.size() < poolMaximumActiveConnections) {
            // Can create new connection
            // 能够创建一个新的连接，并赋值给conn
            conn = new PooledConnection(dataSource.getConnection(), this);
            @SuppressWarnings("unused")
            //used in logging, if enabled
            Connection realConn = conn.getRealConnection();
            if (log.isDebugEnabled()) {
              log.debug("Created connection " + conn.getRealHashCode() + ".");
            }
          } else {
            // Cannot create new connection
            // 活动连接池已经达到最大数量限制，则不能创建新的连接
            // 从活动连接中取第一个也就是最老的那个连接
            PooledConnection oldestActiveConnection = state.activeConnections.get(0);
            // 最老的连接的交付时间
            long longestCheckoutTime = oldestActiveConnection.getCheckoutTime();
            // 交付时间超过了连接池定义的最长交付时间，则说明是过期了
            if (longestCheckoutTime > poolMaximumCheckoutTime) {
              // Can claim overdue connection
              // 声明此连接为过期交付连接，资源中过期交付连接数加一
              state.claimedOverdueConnectionCount++;
              // 统计过期连接的交付时间的总和
              state.accumulatedCheckoutTimeOfOverdueConnections += longestCheckoutTime;
              // 统计交付时间总和
              state.accumulatedCheckoutTime += longestCheckoutTime;
              // 从活动连接池中移除该连接
              state.activeConnections.remove(oldestActiveConnection);
              // 此最老的连接不是自动提交的，则需要进行回滚操作
              if (!oldestActiveConnection.getRealConnection().getAutoCommit()) {
                oldestActiveConnection.getRealConnection().rollback();
              }
              // 最老的连接退位，让出位子，可以创建一个新的连接
              conn = new PooledConnection(oldestActiveConnection.getRealConnection(), this);
              // 将此连接设置为无效
              oldestActiveConnection.invalidate();
              if (log.isDebugEnabled()) {
                log.debug("Claimed overdue connection " + conn.getRealHashCode() + ".");
              }
              //说明此时最老的连接还没有过期
            } else {
              // Must wait
              // 此时就必须等待下去
              try {
                // 就进入一次该方法体
                if (!countedWait) {
                  //用于记录资源不得不等待的次数，该次数加一
                  state.hadToWaitCount++;
                  //下次就不进来了
                  countedWait = true;
                }
                if (log.isDebugEnabled()) {
                  log.debug("Waiting as long as " + poolTimeToWait + " milliseconds for connection.");
                }
                long wt = System.currentTimeMillis();
                //等待20秒，和连接池最大交付的时间相同，保证最老的连接释放
                state.wait(poolTimeToWait);
                // 统计等待的时间
                state.accumulatedWaitTime += System.currentTimeMillis() - wt;
              } catch (InterruptedException e) {
                break;
              }
            }
          }
        }
        if (conn != null) {
          if (conn.isValid()) {
            if (!conn.getRealConnection().getAutoCommit()) {
              conn.getRealConnection().rollback();
            }
            conn.setConnectionTypeCode(assembleConnectionTypeCode(dataSource.getUrl(), username, password));
            conn.setCheckoutTimestamp(System.currentTimeMillis());
            conn.setLastUsedTimestamp(System.currentTimeMillis());
            state.activeConnections.add(conn);
            state.requestCount++;
            state.accumulatedRequestTime += System.currentTimeMillis() - t;
          } else {
            if (log.isDebugEnabled()) {
              log.debug("A bad connection (" + conn.getRealHashCode() + ") was returned from the pool, getting another connection.");
            }
            state.badConnectionCount++;
            localBadConnectionCount++;
            conn = null;
            if (localBadConnectionCount > (poolMaximumIdleConnections + 3)) {
              if (log.isDebugEnabled()) {
                log.debug("PooledDataSource: Could not get a good connection to the database.");
              }
              throw new SQLException("PooledDataSource: Could not get a good connection to the database.");
            }
          }
        }
      }

    }

    if (conn == null) {
      if (log.isDebugEnabled()) {
        log.debug("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
      }
      throw new SQLException("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
    }

    return conn;
  }
```
存在两个池，一个空闲连接池，一个活动连接池，需要创建连接时，先从空闲连接池中获取连接，失败则判断活动连接池中连接是否已满，没有满则创建一个新的连接，如果满了则判断活动连接池中最老的连接是否过期，过期了就将其置为无效并新建一个连接，如果没有过期，则等待它过期。

# Mybatis中的事务
事务相关的问题：
- 事务什么？
- 事务的四大特性ACID
- 不考虑隔离性会产生3个问题
  - 解决办法：四种隔离级别
  
