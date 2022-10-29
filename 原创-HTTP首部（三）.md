# 原创：HTTP首部（三）

# 响应首部字段

响应首部字段是由服务器端向客户端返回响应报文中所使用的字段。用于补充响应的附加信息、服务器信息，以及对客户端的附加要求等信息。

## Accept-Ranges

```
Accept-Ranges:bytes

```

首部字段Accept-Ranges是用来告知客户端服务器是否能处理范围请求，以指定获取服务器端某个部分的资源。<br/>
可指定的字段值有两种，可处理范围请求时指定其为bytes,反之则指定其为none。

## Age

```
Age:600

```

首部字段Age能告知客户端，源服务器在多久前创建了响应。字段值的单位为秒。<br/>
若创建该响应的服务器为缓存服务器，Age值是指缓存后的响应再次发起认证到认证完成的时间值。

## ETag

```
ETag:"9819364440793772187"

```

首部字段ETag能告知客户端实体标识。它是一种可将资源以字符串形式做唯一性标识的方式。服务器会为每份资源分配对应的ETag值。<br/>
资源被缓存时，就会被分配唯一性标识。<br/>
强ETag值和弱ETag值<br/>
ETag中有强ETag值和弱ETag值之分

```
ETag:"usagi-1234"

```

```
ETag: W/"usagi-1234"

```

## Location

请求报文：

```
GET /sample.html

```

返回重定向，位置转移：

```
302  Found
Location:http://www.usagedesign.jp/sample.html

```

根据返回的location,重新发起请求：

```
GET  /sample.html

```

返回响应成功：

```
200 OK

```

使用首部字段Location可以将响应接收方引导至某个与请求URI位置不同的资源。<br/>
基本上，该字段会配合3xx:Redirection的响应，提供重定向的URI.

## Proxy-Authenticate

```
Proxy-Authenticate:Basic  realm="Usagidesign Auth"

```

首部字段Proxy-Authenticate会把由代理服务器所要求的认证信息发送给客户端<br/>
该认证行为是在客户端与代理之间进行的。

## Retry-After

```
Retry-After:120

```

上例表示120秒后再尝试访问<br/>
首部字段Retry-After告知客户端应该在多久之后再次发送请求。主要配合状态码503 Service Unavailable响应，或3xx Redirect响应一起使用。

## Server

```
Server:Apache/2.2.17(Unix)

```

首部字段Server告知客户端当前服务器上安装的HTTP服务器应用程序的信息。不单单会标出服务器上的软件应用名称，还有可能包括版本号和安装时启用的可选项。

## Vary

```
Vary:Accept-Language

```

Vary即改变的意思，如何理解改变？<br/>
指的是响应的返回方式进行了改变<br/>
当代理服务器接收到带有Vary首部字段指定获取资源的请求时，如果使用的Accept-Language字段的值相同，那么就直接从缓存返回响应。反之，则需要先从源服务器端获取资源后才能作为响应返回。

## WWW-Authenticate

```
WWW-Authenticate: Basic realm="Usagidesign Auth"

```

首部字段WWW-Authenticate用于HTTP访问认证。它会告知客户端适用于访问请求URI所指定资源的认证方案（Basic或是Digest）和带参数提示的质询（challenge）。

# 实体首部字段

实体首部字段是包含在请求报文和响应报文中的实体部分所使用的首部，用于补充内容的更新时间等与实体相关的信息。

## Allow

```
Allow:GET,HEAD

```

首部字段Allow用于通知客户端能够支持Request-URI指定资源的所有HTTP方法。当服务器接收到不支持的HTTP方法时，会以状态码405  Method Not Allowed作为响应返回。与此同时，还会把能支持的HTTP方法写入首部字段Allow后返回。

## Content-Encoding

```
Content-Encoding:gzip

```

首部字段Content-Encoding会告知客户端服务器对实体的主体部分选用的内容编码方式。内容编码是指在不丢失实体信息的前提下所进行的压缩。

## Content-Language

```
Content-Language:zh-Cn

```

首部字段Content-Language会告知客户端，实体主体使用的自然语言（指中文或英文等语言）

## Content-Length

```
Content-Length:15000

```

首部字段Content-Length表明了实体主体部分的大小（单位是字节）。对实体主体进行内容编码传输时，不能再使用Content-Length首部字段。

## Content-Location

```
Content-Location:http://www.hackr.jp/index-ja.html

```

首部字段Content-Location给出与报文主体部分相对应的URI。和首部字段Location不同，Content-Location表示的是报文主体返回资源对应的URI。

## Content-MD5

```
Content-MD5:OGFKZUwHDGSGEWEewewrweiH==

```

Content-MD5的目的在于检查报文主体在传输过程中是否保持完整，以及确认传输到达。<br/>
客户端会对接收的报文主体执行相同的MD5算法，然后与首部字段Content-MD5的字段值进行比较,即可判断出报文主体的准确性。<br/>
注：采用这种方法，对内容上的偶发性改变是无从查证的，也无法检测出恶意篡改，因为Content-MD5也可重新计算然后被篡改。

## Content-Range

```
Content-Range:bytes 5001-10000/10000

```

针对范围请求，返回响应时使用的首部字段Content-Range,能告知客户端作为响应返回的实体的哪个部分符合范围请求。字段值以字节为单位，表示当前发送部分及整个实体大小。

## Content-Type

```
Content-Type:text/html;charset=UTF-8

```

首部字段Content-Type说明了实体主体内对象的媒体类型。和首部字段Accept一样，字段值用type/subtype形式赋值。

## Expires

```
Expires：Wed, 04  Jul  2012  08:26:05  GMT

```

首部字段Expires会将资源失效的日期告知客户端。<br/>
缓存服务器在接收到含有首部字段Expires的响应后，会以缓存来应答请求，在Expires字段值指定的时间之前，响应的副本会一直被保存。当超过指定的时间后，缓存服务器在请求发送过来时，会转向源服务器请求资源。

## Last-Modified

```
Last-Modified:Mon, 23 Oct 2017 10:01:51 GMT

```

首部字段Last-Modified指明资源最终修改的时间。一般来说，这个值就是Request-URI指定资源被修改的时间。

# 为Cookie服务的首部字段

为Cookie服务的首部字段如下：

|首部字段名|说明|首部类型|
|:---|:---|:---|
|Set-Cookie|开始状态管理所使用的Cookie信息|响应首部字段|
|Cookie|服务器接收到的Cookie信息|请求首部字段|

## Set-Cookie

```
Set-Cookie: BD_CK_SAM=deleted; expires=Thu, 01-Jan-1970 17:00:00 GMT; path=/; domain=.m.baidu.com

```

当服务器准备开始管理客户端的状态时，会事先告知各种信息。<br/>
下面的表格列举了Set-Cookie的字段值

|属性|说明
|:---|:---|
|NAME=VALUE|赋予Cookie的名称和其值
|expires=DATE|Cookie的有效期（若不明确指定则默认为浏览器关闭前为止）
|path=PATH|将服务器上的文件目录作为Cookie的适用对象（若不指定则默认为文档所在的文件目录）
|domain=域名|作为Cookie适用对象的域名（若不指定则默认为创建Cookie的服务器的域名）
|Secure|仅在HTTPS安全通信时才会发送Cookie
|HttpOnly|加以限制，使Cookie不能被JavaScript脚本访问

## Cookie

```
Cookie:status=enable

```

首部字段Cookie会告知服务器，当客户端想获得HTTP状态管理支持时，就会在请求中包含从服务器接收到的Cookie。接收到多个Cookie时，同样可以以多个Cookie形式发送。
