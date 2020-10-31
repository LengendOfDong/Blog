# Netty内置的传输
Netty内置了一些可拆箱即用的传输。并不是它们所有的传输都支持每一种协议，所以需要选择一个和应用程序所使用的协议相容的传输。

NIO： 使用java.nio.channels包作为基础，基于选择器的方式。

Epoll: 由JNI驱动的epoll()和非阻塞IO。这个传输支持只有在Linux上可用的多种特性，如SO_REUSEPORT,比NIO传输更快，而且是完全非阻塞的。

OIO：使用java.net包作为基础，使用阻塞流。

Local：可以在VM内部通过管道进行通信的本地传输。

Embedded： Embedded传输，允许使用ChannelHandler而又不需要一个真正的基于网络的传输，这在测试你的ChannelHandler实现时非常有用。


