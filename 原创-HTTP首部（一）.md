# 原创：HTTP首部（一）

# HTTP报文首部

HTTP报文的结构如下所示:

|报文首部
|------
|空行（CR+LF）
|报文主体

HTTP协议的请求和响应报文中必定包含HTTP首部。首部内容为客户端和服务器分别处理请求和响应提供所需要的信息。<br/>
在请求中，HTTP报文由方法、URI、HTTP版本、HTTP首部字段等部分构成<br/>
在响应中，HTTP报文由HTTP版本、状态码（数字和原因短语）、HTTP首部字段3部分构成。

# HTTP首部字段

## HTTP首部字段传递重要信息

HTTP首部字段是由首部字段名和字段值构成的，中间用冒号":"分隔

```
首部字段名：字段值

```

如，在HTTP首部中以Content-Type这个字段来表示报文主体的对象类型<br/>
Content-Type:text/html<br/>
字段值对应单个HTTP首部字段可以有多个值，如下所示。

```
keep-Alive:timeout=15,max=100

```

## 4种HTTP首部字段类型

HTTP首部字段根据实际用途被分为以下4种类型。

## End-to-end 首部和Hop-by-hop首部

HTTP首部字段将定义成缓存代理和非缓存代理的行为，分成2种类型。

# HTTP/1.1通用首部字段

通用首部字段是指，请求报文和响应报文双方都会使用的首部。

## Cache-Control

通过指定首部字段Cache-Control的指令，就能操作缓存的工作机制。<br/>
Cache-Control指令：

## Connection

Connection首部字段具备如下两个作用：

控制代理不再转发的首部字段：

```
Connection:不再转发的首部字段名

```

管理持久连接：

```
Connection:close

```

HTTP/1.1版本默认连接都是持久连接。为此，客户端会在持久连接上连续发送请求。当服务器端想明确断开连接时，则指定Connection首部字段的值为Close

```
Connection:Keep-Alive

```

HTTP/1.1之前的HTTP版本的默认连接都是非持久连接。为此，如果想在旧版本的HTTP协议上维持持续连接，则需要指定Connection首部字段的值为Keep-Alive.

## Date

首部字段Date表明创建HTTP报文的日期和时间

```
Date :Tue, 03 Jul  2012  04:40:59 GMT

```

## Pragma

该指令作为与HTTP/1.0的向后兼容而定义

```
Pragma:no-cache

```

该首部字段属于通用首部字段，但只用在客户端发送的请求中。客户端会要求所有的中间服务器不返回缓存的资源

## Trailer

首部字段Trailer会事先说明在报文主体后记录了哪些首部字段。该首部字段可应用在HTTP/1.1版本分块传输编码时。

```
...
Transfer-Encoding:chunked
Trailer: Expires
...(报文主体)...
0
Expires:Tue,28 Sep  2004  23:59:59  GMT

```

在上面例子中，指定首部字段Trailer的值为Expires,在报文主体之后（分块长度0之后）出现了首部字段Expires

## Transfer -Encoding

首部字段Transfer-Encoding规定了传输报文主体是采用的编码方式。

## Upgrade

首部字段Upgrade用于检测HTTP协议及其他协议是否可使用更高的版本进行通信，其参数值可以用来指定一个完全不同的通信协议。

## Via

使用首部字段Via是为了追踪客户端与服务器之间的请求和响应报文的传输路径。<br/>
报文经过代理或者网关时，会先在首部字段Via中附加该服务器的信息，然后再进行转发。<br/>
首部字段Via不仅用于追踪报文的转发，还可避免请求回环的发生。所以必须在经过代理时附加该首部字段内容。<br/>
请求报文：

```
GET   /   HTTP/1.1

```

经过代理服务器之后：

```
GET  /   HTTP/1.1
Via: 1.0  gw.hackr.jp(Squid/3.1)

```

## Warning

HTTP/1.1的Warning首部是从HTTP/1.0的响应首部（Retry-After）演变过来的。该首部通常会告知用户一些与缓存相关的问题警告<br/>
该首部的格式如下：

```
Warning:[警告码][警告的主机：端口号]"[警告内容]"([日期时间])

```

HTTP/1.1定义了7种警告，警告码如下：

|警告码|警告内容|说明
|------
|110|Response is stale(响应已过期)|代理返回已过期的资源
|111|Revalidation  failed(再验证失败)|代理再验证资源有效性时失败（服务器无法到达等原因）
|112|Disconnection operation(断开连接操作)|代理与互联网连接被故意切断
|113|Heuristic expiration(试探性过期)|响应的使用期超过24小时（有效缓存的设定时间大于24小时的情况下）
|199|Miscellaneous warning(杂项警告)|任意的警告内容
|214|Transformation applied(使用了转换)|代理对内容编码或媒体类型等执行了某些处理时
|299|Miscellaneous persistent warning(持久杂项警告)|任意的警告内容
