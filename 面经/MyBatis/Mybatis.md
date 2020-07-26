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
 
 mybatis的入门案例：
 - 读取配置文件
 - 创建SqlSessionFactory工厂
 - 创建SqlSession
 - 创建Dao接口的代理对象
 - 执行Dao中的方法
 - 释放资源
 
 mybatis基于注解的入门案例：把IUserDao.xml移除，在dao接口的方法上使用@Select注解，并且指定SQL语句，同时需要在SqlMapConfig.xml中的mapper配置时，使用class属性指定dao接口的全限定类名。
 
 明确：在实际开发中越简便越好，所以都是采用不写dao实现类的方式，不管使用XML还是注解配置，但是Mybatis是支持写dao实现类的。
 
 ```java
  public static void main(String[] args) throws IOException {
        //1.读取配置文件
        //读取路径有两个方法：
        //第一个：使用类加载器，它能读取类路径的配置文件
        //第二个：使用ServletContext对象的getRealPath()
        InputStream in = Resources.getResourceAsStream("SqlMapConfig.xml");
        //2.创建SqlSessionFactory工厂
        //创建工厂mybatis使用了构建者模式
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(in);
        //3.使用工厂生产SqlSession对象
        //生产SqlSession使用了工厂模式，优势：解耦（降低类之间的依赖关系）
        SqlSession session = factory.openSession();
        //4.使用SqlSession创建Dao接口的代理对象
        //创建Dao接口实现类使用了代理模式，优势：不修改解码的基础上对已有方法增强
        IUserDao userDao = session.getMapper(IUserDao.class);
        //5.使用代理对象执行方法
        List<User> users = userDao.findAll();
        for(User user:users){
            System.out.println(user);
        }
        //6.释放资源
        session.close();
        in.close();
    }
 ```

 
 
