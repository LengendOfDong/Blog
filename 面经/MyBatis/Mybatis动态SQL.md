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

添加范围查询的设置
```java
<!-- 根据QueryVo中的ID集合实现查询用户列表 -->
    <select id="findUserInIds" resultMap="com.dong.domain.User" parameterType="com.dong.domain.QueryVo">
        select * from customer
        <where>
            <if test="ids != null and ids.size() > 0">
                <foreach collection="ids" open="and id in (" close=")" item="id" separator=",">
                    #{id}
                </foreach>
            </if>
        </where>
    </select>
```
<foreach>标签用于遍历集合，它的属性：
- collection:代表要遍历的集合元素，注意编写时不要写#{}
- open：代表语句的开始部分
- close:代表语句的结束部分
- item:代表遍历结合的每个元素，生成的变量名
- seperator:代表分隔符

