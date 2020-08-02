# Mybatis动态SQL
在SQL语句中出现情况判断时，就需要用到动态语句的方式编写SQL

例如要判断username是否为空，不为空时才把username座位条件，加入到SQL语句中
```java
<select id="findUserByCondition" resultType="com.dong.domain.User" parameterType="com.dong.domain.User">
        select * from customer where 1 = 1
        <if test="username != null">
            and username = #{username}
        </if>
    </select>
```

where标签的使用，主要是省略了”where 1= 1“，这样使SQL语句更清晰更简洁
```java
<select id="findUserByCondition" resultType="com.dong.domain.User" parameterType="com.dong.domain.User">
        select * from customer
        <where>
            <if test="username != null">
                and username = #{username}
            </if>
        </where>
    </select>
```

