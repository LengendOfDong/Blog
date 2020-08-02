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

 两个配置文件SqlMapConfig.xml与IUserDao.xml，进行了以下几个步骤：
 
 1）读取配置文件：用到解析XML的技术，此处用的是dom4j解析xml技术
 
 2）根据配置文件的信息，创建Connection对象，就比如：注册驱动，获取连接
 
 3）获取预处理对象PreparedStatement，此时需要SQL语句，conn.prepareStatement(sql);
 
 4) 执行查询，ResultSet  resultSet = preparedStatement.executeQuery();
 
 5) 遍历结果集用于封装：
 ```java
 List<E> list = new ArrayList():
 while(resultSet.hasNext()){
    E  element = (E)Class.forName(配置的全限定类名).newInstance();
    进行封装，把每个rs的内容都添加到element中，把Element加入到list中
    我们的实体类属性和表中的列名是一致的。于是我们就可以把表的列名看成是实体类的属性名称。就可以使用反射的方式来根据名称获取每个属性，并把值赋进去。
    把element加入到list中
    list.add(element);
 }
 ```
  
 6) 返回list,  return list;
 
 要让上述的方法执行，需要提供两个信息：第一个：连接信息，第二个映射信息。映射信息包含了两个部分，第一个部分执行SQL语句，第二个部分封装结果的实体类全限定名。 
 
 ## OGNL表达式
 OGNL:Object Graphic  Navigation Language,对象图像导航语言
 
 它是通过对象的取值方法来获取数据，在写法上把get给省略了。
 
 假如实体类与数据库中的字段没有对应，该如何处理？
 - 执行效率：在mapper文件中，将 sql语句使用 as 起别名的方式进行替换。
 ```java
 select id as userId, name as userName, sex as userSex, address as userAddress, birthday as userBirthday from user 
 ```
 - 开发效率：在mapper文件中，使用resultMap来进行配置，并且将sql语句返回类型设置为resultMap
```java
<resultMap id="userMap" type="com.dong.domain.User">
        <!-- 主键字段的对应 -->
        <id property="userId" column="id" />
        <!-- 非主键字段的对应 -->
        <result property="userNme" column="username" />
        <result property="userAddress" column="address" />
        <result property="userSex" column="sex" />
        <result property="userBirthday" column="birthday" />
    </resultMap>
```

## properties标签的使用
```java
<properties resource="" url="">
    <property name="" value=""></property>
</properties>
```
可以在标签内部配置连接数据库的信息，也可以通过属性引用外部配置文件信息
 
resource 属性：常用于指定配置文件的位置，是按照类路径的写法来写，并且必须存在于类路径下。
 
url属性：是要求按照url的写法来写地址
     
URL：Uniform Resource Locator 统一资源定位符，它是可以唯一标识一个资源的位置
     
URI：Uniform Resource Identifier 统一资源标识符，它是在应用中可以唯一定位一个资源的。
    
URL是全网络中都可以唯一标识一个资源，而URI是在应用中唯一标识，所以URL比URI更加准确。日常访问文件也是通过file协议来访问的，只是在Windows系统中默认支持file协议，所以没有显示"file:///"开头

## typeAliaes标签的使用
```java
<!-- 使用typeAliaes配置别名，只能配置domain中类的别名-->
<typeAliases>
    <!--typealias用于配置别名，type属性指定的是实体类全限定类名，alias属性指定别名，当指定了别名就再区分大小写-->
    <typeAlias type="com.dong.domain.User" alias="user"></typeAlias>
</typeAliases>
```
