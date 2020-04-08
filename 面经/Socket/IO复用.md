# I/O复用
select/poll/epoll都是I/O多路复用的具体实现，select出现最早，之后是poll,再是epoll。

##  比较
### 1.功能
select和poll的功能基本相同，不过在一些实现细节上有所不同。
- select会修改描述符，而poll不会
- select的描述符类型使用数组实现，FD_SETSIZE大小默认为1024，因此默认只能监听少于1024个描述符。如果要监听更多描述符的话，需要修改FD_SETSIZE之后重新编译；而poll没有描述符数量的限制。
- poll提供了更多的事件类型，并且对描述符的重复利用上比select高。
- 如果一个线程对某个描述符调用了select或者poll,另一个线程关闭了该描述符，会导致调用结果不确定

### 2.速度
select和poll速度都比较慢，
