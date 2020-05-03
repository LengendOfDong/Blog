# GeoHash
GeoHash算法是将二维的经纬度数据映射到一维的整数，这样所有的元素都将挂载到一条线上，距离靠近的二维坐标映射到一维后的点之间距离也会很近。

## Geo指令的基本用法
- 增加

  geoadd 指令携带集合名称以及多个经纬度名称三元组，注意这里可以加入多个三元组
  ```java
  > geoadd company 经度 纬度  jd  经度  纬度  xiaomi
  (integer) 2
  ```
  
- 距离
  
  geodist指令可以用来计算两个元素之间的距离，携带集合名称、两个名称和距离单位。
  ```java
  > geodist company juejin  xiaomi  km
 "12.9606"
  ```
- 获取元素位置
  
  geopos指令可以获取集合中任意元素的经纬度坐标，可以一次获取多个。
  ```java
  > geopos company juejin ireader
  ```

- 获取元素的hash值
  
  GeoHash可以获取元素的经纬度编码字符串，它是base32编码。可以使用这个编码值去http://geohash.org/${hash}上进行直接定位。它是GeoHash的标准编码值。
  ```java
  > geohash company ireader
  1) "wx4g52e1ce0"
  ```
  
- 附近的公司

georadiusbymember指令是最为关键的指令之一，可以用来查询指定元素附近的其他元素，例如查询小区周围的银行或者超市等。Redis还提供了根据坐标值来查询附近元素的指令georadius,这个指令可以根据用户的定位来计算“附近的车”，“附近的餐馆”等。
```java
# 坐标位置范围20km以内最多3个元素按距离正排的公司
> georadius company 116.541202 39.905409 20 km withdist count 3 asc
```

  
