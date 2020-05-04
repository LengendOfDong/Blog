# RESP
RESP是Redis序列化协议（Redis Serialization Protocol）的简写。它是一种直观的文本协议，优势在于实现过程异常简单，解析性能极好。

Redis协议将传输的结构数据分为5中最小单元类型。单元结束时统一添加回车换行符号\r\n。
- 单行字符串以”+“符号开头
- 多行字符串以”$“符号开头，后跟字符串长度
- 整数值以”：“符号开头，后跟整数的字符串形式
- 错误消息以”-“符号开头
- 数组以”*“号开头，后跟数组的长度

单行字符串hello world：
```java
+hello world\r\n
```

多行字符串hello world：
```java
$11\r\nhello world\r\n
```
多行字符串也可以表示单行字符串

整数1024
```java
:1024\r\n
```

错误
- 参数类型错误
```java
-WRONGTYPE Operation against a key holding the wrong kind of value\r\n
```

数组【1，2，3】
```java
*3\r\n:1\r\n:2\r\n:3\r\n
```

NULL
- NULL用多行字符串表示，不过长度要写成-1.
```java
$-1\r\n
```

空串
- 空串用多行字符串表示，长度填0
```java
$0\r\n\r\n
```
两个\r\n之间隔的是空串。

## 客户端 -> 服务端
客户端向服务端发送的指令只有一种形式，多行字符串数组。

## 服务端 -> 客户端
服务器向客户端回复的响应要支持多种数据结构。

Redis协议中包含大量冗余的回车换行符，但是这并不影响它成为互联网技术领域非常受欢迎的一个文本协议。
