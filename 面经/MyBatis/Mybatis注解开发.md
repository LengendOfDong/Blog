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
