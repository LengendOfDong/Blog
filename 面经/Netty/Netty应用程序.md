# Netty服务器
所有的Netty服务器都需要以下两个部分：
- 至少一个ChannelHandler，该组件实现了服务器对从客户端接收的数据的处理，即它的业务逻辑
- 引导，这是配置服务器的启动代码，至少，它会将服务器绑定到它要监听连接请求的端口上。

服务器实现的重要步骤：
- EchoServerHandler实现了业务逻辑
- main（）方法中引导了服务器

引导过程中所需要的步骤如下：
- 创建一个ServerBootStrap的实例以引导和绑定服务器
- 创建并分配一个NioEventLoopGroup实例以进行事件的处理，如接受新连接以及读写数据。
- 指定服务器绑定的本地的InetSocketAddress
- 使用一个EchoServerHandler的实例初始化每一个新的Channel
- 调用ServerBootStrap.bind()方法以绑定服务器

