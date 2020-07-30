# SAN和NAS
SAN : STORAGE AREA NETWORK    存储区域网络

NAS : NETWORK ATTACHED STORAGE  网络附加存储

NAS不一定是盘阵，一台普通的主机就可以做出NAS,只要它自己有磁盘和文件系统，而且对外提供访问其文件系统的接口（如NFS,CIFS等），它就是一台NAS。常用的windows文件共享服务器就是利用CIFS作为调用接口协议的NAS设备。一般来说NAS其实就是处于以太网上的一台利用NFS,CIFS等网络文件系统的共享服务器。至于将来会不会有FC网络上的文件提供者，也就是FC网络上的NAS，就等日后再说了。

注解：NFS(NETWORK FILE SYSTEM)  适用于LINUX&UNIX系统

      CIFS(Common Internet FILE SYSTEM) 适用于windows系统
      
SAN\NAS的区别：

可以这样来比作：SAN是一个网络上的磁盘；NAS是一个网络上的文件系统。其实根据SAN的定义，可知SAN其实是指一个网络，但是这个网络里包含着各种各样的元素，主机、适配器、网络交换机、磁盘阵列前端、盘阵后端、磁盘等。长时间以来，人们都习惯性的用SAN来特指FC，特指远端的磁盘。那么，一旦设计出了一种基于FC网络的NAS,而此时的SAN应该怎样称呼？所以，在说两者的区别时，用了一个比方，即把FC网络上的磁盘叫做SAN,把以太网络上的文件系统称为NAS，我们可以这样简单来理解。

普通台式机也可以充当NAS。NAS必须具备的物理条件有两条，第一，不管用什么方式，NAS必须可以访问卷或者物理磁盘；第二，NAS必须具有接入以太网的能力，也就是必须具有以太网卡。
