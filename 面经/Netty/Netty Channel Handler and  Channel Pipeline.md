##  Channel的生命周期
ChannelUnregistered: Channel已经被创建，但还未被注册到EventLoop

ChannelRegistered: Channel被注册到EventLoop

ChannelActive: Channel处于活动状态（已经连接到它的远程节点），它现在可以接收和发送数据了

ChannelInActive:Channel 没有连接到远程节点

## ChannelHandler的生命周期


