# CRUD注解
@Select("select * from user")

@Update

@Delete

# 实体类属性与数据库列名对应
@Results注解

```java
@Results( id="userMap"
  value={
    @Result(id=true, column="id", property="userId"),
    @Result(column="username", property="userName"),
    @Result(column="address", property="userAddress"),
    @Result(column="sex", property="userSex"),
    @Result(column="birthday", property="userBirthday"),
  }
)
```

在其他位置使用@ResultMap进行引用， @ResultMap(value={"userMap"})

# 注解开发一对一

```java
@Select("select * from account)
@Results( id="accountMap"
  value={
    @Result(id=true, column="id", property="id"),
    @Result(column="uid", property="uid"),
    @Result(column="money", property="money"),
    //封装的是user对象，user作为account对象中的一个属性存在，通过uid进行查询，所以column中填写uid
    @Result(property="user", column="uid", property=@One(select="com.dong.dao.IUserDao.findById",fetchType=FetchType.EAGER))
  }
)
```

