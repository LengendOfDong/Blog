# 原创：linux 终端启动图形化程序界面时报错：No protocol specified

今天通过VNC远程连接时，执行脚本出现以下错误：

```
NO protocol specified
Exception in thread "main" java.lang.NOClassDefFoundError: Could not initialize class sun.awt.X11GraphicsEnvironment

```

看到‘X11GraphicsEnvironment’,猜测时图形界面的问题，故使用Putty来执行脚本，没有报错。

这是因为Xserver默认情况下不允许别的用户的图形程序的图形显示在当前屏幕上. 如果需要别的用户的图形显示在当前屏幕上, 则应以当前登陆的用户, 也就是切换身份前的用户执行如下命令

xhost +

远程访问时也会出现类似问题：

第一步：用root登陆linux

第二步：执行xhost +，并且提示“access control disabled, clients can connect from any host”才正确。
