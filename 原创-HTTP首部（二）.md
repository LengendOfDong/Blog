# 原创：HTTP首部（二）

# 请求首部字段

请求首部字段是从客户端往服务端发送请求报文中所使用的字段，用于补充请求的附加信息、客户端信息、对响应内容相关的优先级等内容。

## Accept

Accept首部字段可通知服务器，用户代理能够处理的媒体类型及媒体类型的相对优先级。可以使用type/subtype这种形式，一次指定多种媒体类型。

```
Accept:text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8

```

以下为常见的几种媒体类型：

## Accept-Charset

```
Accept-Charset: iso-8859-5,unicode-1-1;q=0.8

```

Accept-Charset首部字段可用来通知服务器用户代理支持的字符集及字符集的相对优先顺序。另外，可一次性指定多种字符集。与首部字段Accept相同的是可用权重q值来表示相对优先级。<br/>
该首部字段应用于内容协商机制的服务器驱动协商。

## Accept-Encoding

```
Accept-Encoding:gzip,deflate

```

Accept-Encoding首部字段用来告知服务器用户代理支持的内容编码以及内容编码的优先级顺序。可一次性指定多种内容编码。<br/>
以下列出几种常见的内容编码格式：

## Accept-Language

```
Accept-Language:zh-cn,zh;q=0.7,en-us,en;q=0.3

```

首部字段Accept-Language用来告知服务器用户代理能够处理的自然语言集（指中文或英文等），以及自然语言集的相对优先级。可一次指定多种自然语言集。<br/>
上例中，客户端在服务器有中文版资源的情况下，会请求其返回中文版对应的响应，没有中文版时，则请求返回英文版响应。

## Authorization

```
Authorization:Basic   dWVub3N1bjpwYXnZD29Yza==

```

首部字段Authorization是用来告知服务器，用户代理的认证信息（证书值）。<br/>
请求报文：

```
GET  /index.htm

```

返回验证要求：

```
401  Unauthorized
WWW-Authenticated:Basic ...

```

发送认证信息：

```
GET  /index.htm
Authorization:Basic   dWVub3N1bjpwYXnZD29Yza==

```

## Except

```
Except:100-continue

```

客户端使用首部字段Except来告知服务器，期望出现的某种特定行为。

## From

```
From:info@hackr.jp

```

首部字段From用来告知服务器使用用户代理的用户的电子邮件地址。通常，其使用目的就是为了显示搜索引擎等用户代理的负责人的电子邮件联系方式。

## Host

```
Host:www.baidu.com

```

虚拟主机运行在同一个IP上，此时使用首部字段Host加以区分。<br/>
Host首部字段在HTTP/1.1规范内是唯一一个必须被包含在请求内的首部字段。

## If-Match

```
GET  /index.html
If-Match:"123456"

```

服务器会比对If-Match的字段值和资源的ETag值，仅当两者一致时，才会执行请求。<br/>
实体标记（ETag）是与特定资源关联的确定值。资源更新后ETag也会随之更新。

## If-Modified-Since

请求报文：

```
GET  /index.htm
If-Modified-Since:Thu,15  Apr   2004  00:00:00  GMT

```

若资源在2004年4月15日 之后更新过，则返回

```
200  OK
Last-Modified:Sun,29  Aug  2004  14:03:05  GMT

```

若资源在2004年4月15日之后没更新过，则返回

```
304   Not  Modified

```

## If-None-Match

请求报文：

```
PUT  /sample.html
If-None-Match:  *

```

若没有匹配的ETag,则返回

```
200  OK

```

只有在If-None-Match的字段值与ETag值不一致时，可处理该请求。与If-Match首部字段的作用相反。

## If-Range

该首部字段告知服务器若指定的If-Range字段值（ETag值或时间）和请求资源的ETag值或时间相一致时，则作为范围请求处理。反之，则返回全体资源。<br/>
尤其是在不匹配的情况下，节省了请求资源，无需重新发送请求，如果是If-Match则需要重新发送请求来获取最新的资源。

## If-Unmodified-Since

```
If-Unmodified-Since:Thu,03  Jul  2012  00:00:00  GMT

```

首部字段If-Unmodified-Since和首部字段If-Modified-Since的作用相反。它的作用是告知服务器，指定的请求资源只有在字段值内指定的日期时间之后，未发生更新的情况下，才能处理请求。

## Max-Forwards

```
Max-Forwards:10

```

通过TRACE方法或OPTIONS方法，发送包含首部字段Max-Forwards的请求时，该字段以十进制整数形式指定可经过的服务器最大数目。<br/>
服务器在往下一个服务器转发请求之前，会将Max-Forwards的值减1后重新赋值。当服务器接收到Max-Forwards值为0的请求时，则不再进行转发，而是直接返回响应。<br/>
Max-Forwards也可以用来观察以某台服务器为终点的传输路径的通信状况。

## Proxy-Authorization

```
Proxy-Authorization: Basic    dG1w0jkpNLGAFDFIJ

```

接收到从代理服务器发来的认证质询时，客户端会发送包含首部字段Proxy-Authorization的请求，以告知服务器认证锁需要的信息。<br/>
该认证发生在客户端与代理之间。客户端与服务器之间的认证，使用首部字段Authorization可起到相同作用。

## Range

```
Range:bytes=5001-10000

```

上面示例表示请求获取从第5001字节至第10000字节的资源。<br/>
接收到附带Range首部字段请求的服务器，会在处理请求之后返回状态码为206 Partial Content的响应。无法处理该范围请求时，则会返回状态码200 OK的响应及全部资源。

## Referer

```
GET   /
Referer:http://www.hackr.jp/index.htm

```

首部字段Referer会告知服务器请求的原始资源的URI.<br/>
客户端一般都会发送Referer首部字段给服务器。

## TE

```
TE:gzip,deflate；q=0.5

```

首部字段TE会告知服务器客户端能够处理响应的传输编码方式及相对优先级。它和首部字段Accept-Encoding的功能很像，但是用于传输编码。

## User-Agent

```
User-Agent:Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Mobile Safari/537.36

```

首部字段User-Agent会将创建请求的浏览器和用户代理名称等信息传达给服务器。
