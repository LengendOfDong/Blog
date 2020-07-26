# Mybatis的概述
mybatis是一个持久层框架，是用java编写的。

# Mybatis入门
 mybatis的环境搭建：
 - 创建maven工程并导入坐标
 - 创建实体类和dao的接口
 - 创建Mybatis的主配置文件  SqlMapConfig.xml
 - 创建映射配置文件  IUserDao.xml
 
 环境搭建的注意事项：
 - 创建IUserDao.xml和IUserDao.java时名称是为了和我们之前的知识保持一致。在Mybatis中它把持久层的操作接口名称和映射文件也叫做：Mapper
 - mybatis的映射配置文件位置必须和dao接口的包结构相同
 - 映射配置文件的mapper标签namespace属性的取值必须是dao接口的全限定类名
 - 映射配置文件的操作配置（select)，id属性的取值必须是dao接口的方法名
 
 在完成以上几点之后，Mybatis会自行实现dao接口的实现，并建立与数据库之间的映射关系。SqlMapConfig.xml用于建立数据库连接，并制定要建立对象映射关系的mapper文件，而IUserDao.xml或者IUserMaper.xml文件会作为对象映射关系的依据。这两个文件就完成了Jdbc通常要完成的几个步骤：建立数据库连接，生成statement,执行查询，取出数据并进行set到对应属性中。
 
 
 
