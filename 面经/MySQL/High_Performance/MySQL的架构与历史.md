# MySQL的架构与历史
## 多版本并发控制
InnoDB的MVCC，是通过在每行记录后面保存两个隐藏的列来实现的。这两个列，一个保存了行的创建时间，一个保存行的过期时间（或删除时间）。当然存储的并不是实际的时间值，而是系统版本号。每开始一个新的事务，系统版本号就会自动递增。事务开始时刻的系统版本号会作为事务的版本号，用来和查询到的每行记录的版本号进行比较。

下面看一下在REPEATABLE READ隔离级别下，MVCC具体是如何操作的。

SELECT
&emsp;&emsp;InnoDB会根据以下两个条件检查每行记录：
  - a.InnoDB只查找版本早于当前事务版本的数据行(也就是，行的系统版本号小于或等于事务的系统版本号），这样可以确保事务读取的行，要么是在事务开始之前就已经存在的，要么就是当前事务自身插入或修改的。
  - b.行的删除版本要么未定义，要么大于当前事务版本号。这可以确保事务读取到的行，在事务开始之前未被删除。
 
 只有符合上述两个条件的记录，才可以作为查询结果。
 
INSERT
  - InnoDB为新插入的每一行保存当前系统版本号作为行版本号。

DELETE
  - InnoDB为删除的每一行保存当前系统版本号作为行删除标识。
  
UPDATE
  - InnoDB为插入一行新记录，保存当前系统版本号作为行版本号，同时保存当前系统版本号到原来的行作为行删除标识。
  
MVCC只在REPEATABLE READ和READ COMMITTED两个隔离级别下工作。其他两个隔离级别都和MVCC不兼容，因为READ UNCOMMITTED总是读取最新的数据行，而不是符合当前事务版本的数据行。而SERIALIZABLE则会对所有读取的行都加锁。

## 选择合适的引擎
如果应用需要不同的存储引擎，请先考虑以下几个因素：
- 事务
如果应用需要支持事务，那么InnoDB是目前最稳定并且经过验证的选择。如果不需要事务，并且主要是SELECT和INSERT操作，那么MyISAM是不错的选择。一般日志型的应用比较符合这一特性。

- 备份
备份的需求也会影响存储引擎的选择。如果可以定期地关闭服务器来执行备份，那么备份的因素可以忽略。反之，如果需要在线热备份，那么选择InnoDB就是基本的要求。

- 崩溃恢复
数据量比较大的时候，系统崩溃后如何快速地恢复是一个需要考虑的问题。相对而言，MyISAM崩溃后发生损坏的概率比InnoDB要高得多，而且恢复速度也要慢。

- 特有的特性
MySQL中只有MyISAM支持地理空间搜索。某些存储引擎无法直接支持的特性，有时候通过变通也可以满足需求。

## 转换表的引擎
### ALTER TABLE
将表从一个引擎改为另一个引擎最简单的办法是使用ALTER TABLE语句，下面的语句将mytable的引擎修改为InnoDB:
```SQL
mysql>ALTER TABLE mytable ENGINE=InnoDB;
```
这个语法可以使用任何存储引擎，但有一个问题：需要执行很长时间。MySQL会按行将数据从原表复制到一张新的表中，在复制期间可能会消耗系统所有的I/O能力，同时整张表会加上读锁，所以在繁忙的表上执行此操作要格外小心。

### 导出与导入
为了更好地控制转换的过程，可以使用mysqldump工具将数据导出到文件，然后修改文件中CREATE TABLE语句的存储引擎选项，注意同时修改表名。

### 创建与查询（CREATE 和 SELECT)
第三种转换的技术综合了第一种方法的高效和第二种方法的安全。不需要导出整个表的数据，而是先创建一个新的存储引擎的表，然后利用INSERT..SELECT语法来导数据：
```SQL
mysql>CREATE TABLE innodb table LIKE myisam_table;
mysql>ALTER TABLE innodb table ENGINE=InnoDB;
mysql>INSERT INTO innodb_table SELECT * FROM myisam_table;
```
数据量不大的话，这样做工作得很好。如果工作量大的话，可以使用分批处理，针对每一段数据执行事务提交操作。假设有主键字段id,重复运行以下语句将数据导入到新表：
```SQL
mysql>START TRANSACTION;
mysql>INSERT INTO innodb_table SELECT * FROM myisam_table WHERE id BETWEEN X AND Y;
mysql>COMMIT;
```
这样操作完成以后，新表是原表的一个全量复制，原表还在，如果需要可以删除原表。
