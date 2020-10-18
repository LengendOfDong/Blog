# Channel、EventLoop和ChannelFuture
Channel:Socket

EventLoop:控制流、多线程处理、并发

ChannelFuture:异步通知

## Channel接口
基本的I/O操作（bind(),connect()、read()和write()依赖于底层网络传输所提供的原语），
