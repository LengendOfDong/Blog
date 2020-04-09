# HTTP方法
URI（Uniform Resource Identifier）称为统一资源标识符
URL（Uniform Resource Locator）称为统一资源定位符
URN(Uniform Resource Name)称为统一资源名称
URI包含URL和URN。

客户端发送的请求报文第一行为请求行，包含了方法字段。

## HTTP方法
- GET：获取资源
网络中绝大部分使用的是GET方法

- HEAD：获取报文首部
主要用于确认URL的有效性以及资源更新的日期时间等。

- POST：传输实体主体
POST主要用来传输数据，而GET主要用来获取资源。

- PUT：上传文件
由于自身不带验证机制，任何人都可以上传文件，因此存在安全性问题，一般不使用此方法。

- PATCH:对资源进行部分修改
PUT也可以用于修改资源，但是只能完全替代原始资源，PATCH允许部分修改。

- DELETE:删除文件
与PUT功能相反，并且同样不带验证机制。

- OPTIONS：查询支持的方法
查询指定的URL支持的方法
会返回==Allow:GET/POST/HEAD/OPTIONS这样的内容

- CONNECT:要求在与代理服务器通信时建立隧道
使用SSL（Secure Socket Layer,安全套接层）和TLS(Transport Layer Security,传输层安全）协议把通信内容加密后经网络隧道传输。

- TRACE:追踪路径
服务器会将通信路径返回给客户端

