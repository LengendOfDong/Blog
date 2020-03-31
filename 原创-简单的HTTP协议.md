# 原创：简单的HTTP协议

# 请求和响应的交换

```
GET   /index.htm   HTTP/1.1
Host： hackr.jp

```

起始行开头的<mark>GET</mark>表示请求访问服务器的类型，称为方法。<br/>
<mark>/index.htm</mark>指明请求访问的资源对象，也叫做请求URI<br/>
<mark>HTTP/1.1</mark>表示HTTP的版本号，用来提示客户端使用的HTTP协议功能<br/>
请求报文是由请求方法、请求URI、协议版本、可选的请求首部字段和内容实体构成。如下面所示：

```
方法:POST   URI:/form/entry     协议版本：HTTP/1.1
请求首部字段：
Host: hackr.jp
Connection: keep - alive
Content-Type :application/x-www-form-urlencoded
Content-Length:16
内容实体：
name=ueno&amp;age=37

```

响应报文基本上由协议版本、状态码（表示请求成功或失败的数字代码）、用以解释状态码的原因短语、可选的响应首部字段以及实体主体构成。

```
协议版本：HTTP/1.1   状态码：200     状态码的原因短语：OK
响应首部字段：
Date:Tue ,10  Jul   2012   06:50:15  GMT
Content-Length:362
Content-Type:text/html

&lt;html&gt;
...

```

# HTTP是无状态协议

HTTP是一种不保存状态，即无状态的协议。<br/>
HTTP协议自身不对请求和响应之间的通信状态进行保存。也就是说在HTTP这个级别，协议对于发送过的请求或响应都不做持久化处理。<br/>
HTTP/1.1虽然是无状态协议，但为了实现期望的保持状态功能，于是引入了Cookie技术。有了Cookie再用HTTP协议通信，就可以管理状态了。

# 请求URI定位资源

当客户端请求访问资源而发送请求时，URI需要将作为请求报文中的请求URI包含在内。<br/>
访问特定资源如下：

```
GET  http://hackr.jp/index.htm   HTTP/1.1

```

请求报文：

```
GET  /index.htm  HTTP/1.1
Host: hackr.jp

```

访问非特定资源，即对服务器本身发起请求：

```
OPTIONS  *   HTTP/1.1

```

# 告知服务器意图的HTTP方法

HTTP/1.1中可以使用的方法：

```
CONNECT 代理服务器名：端口号  HTTP版本

```

# 使用方法下达命令

向请求URI指定的资源发送请求报文时，采用称为方法的命令。<br/>
方法的作用在于，可以指定请求的资源按期望产生某种行为。方法中有GET，POST和HEAD等<br/>
下表列出了HTTP/1.0和HTTP/1.1支持的方法。

|方法|说明|HTTP协议版本
|------
|GET|获取资源|1.0、1.1
|POST|传输实体主体|1.0、1.1
|PUT|传输文件|1.0、1.1
|HEAD|获得报文首部|1.0、1.1
|DELETE|删除文件|1.0、1.1
|OPTIONS|询问支持的方法|1.1
|TRACE|追踪路径|1.1
|CONNECT|要求用隧道协议连接代理|1.1
|LINK|建立和资源之间的联系|1.0
|UNLINK|断开连接关系|1.0

# 持久连接节省通信量

<mark>背景</mark>：<br/>
以往的HTTP版本中，每进行一次HTTP通信就要断开一次TCP连接。随着HTTP的普及，通信过程中包含很多图片的情况多了起来。每次请求都会造成无谓的TCP连接和断开，增加通信量的开销。

<mark>解决方法</mark>：<br/>
为了节省通信量，想出了<mark>持久连接</mark>（HTTP Persistent Connections,也称为HTTP keep-alive或HTTP connection reuse）的方法。持久连接的特点是，只要任意一端没有明确提出断开连接，则保持TCP连接状态。

持久连接的<mark>好处</mark>在于：<br/>
1）减少了TCP连接的重复建立和断开所造成的额外开销，减轻了服务器端的负载。<br/>
2）减少开销的时间，使HTTP请求和响应能够更早地结束，这样Web页面的显示速度也就相应提高了

## 管线化

  持久连接使得多数请求以管线化的方式发送成为可能。管线化方式即异步的方式，不用等到接收到响应之后发送下一个请求。

# 使用Cookie的状态管理

1）Cookie会根据服务器端发送的响应报文内的一个叫做Set-Cookie的首部字段信息，通知客户端保存Cookie。<br/>
2）当下次客户端再往该服务器发送请求时，客户端会自动在请求报文中加入Cookie值后发送出去。<br/>
3）服务端发现客户端发送过来的Cookie后，会去检查究竟是从哪一个客户端发来的连接请求，然后对比服务器上的记录，最后得到之前的状态信息<br/>
HTTP请求报文和响应报文的内容如下：

```
Get  /reader/   HTTP/1.1
Host: hackr.jp
*首部字段内没有Cookie的相关信息

```

```
HTTP/1.1   200  OK
Date :Thu, 12  Jul   2012   07:12:20  GMT
Server:Apache
&lt;Set-Cookie:sid = 1342077140226724;path = /;expires=Wed,10-Oct-12 07:12:20 GMT&gt;
Content-Type:text/plain;charset=UTF-8

```

```
GET  /image/  HTTP/1.1
Host: hackr.jp
Cookie: sid = 1342077140226724

```
