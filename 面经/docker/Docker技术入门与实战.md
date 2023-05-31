# 一、初识Docker

在云计算时代，虚拟化技术无疑是整座信息技术大厦最核心的一块基石。

虚拟化既可以通过硬件模拟来实现，也可以通过操作系统来实现。

容器虚拟化技术充分利用操作系统本身的机制和特性，可以实现轻量级的虚拟化，其中Docker就是主要的代表。

Docker通过对应用组件的封装、分发、部署、运行等生命周期的管理，达到应用软件级别的“一次封装，到处运行”。

> 容器技术的准确描述：
>
> 容器有效地将由单个操作系统管理的资源划分到孤立的组中，以便更好地在孤立的组之间平衡有冲突的资源使用需求。与虚拟化相比，这样既不需要指令级模拟，也不需要即时编译。容器可以在核心CPU本地运行指令，而不需要任何专门的解释机制。此外，也避免了准虚拟化和系统调用替换中的复杂性。

## Docker容器虚拟化的好处

对开发和运维来说，最希望的就是一次性地创建或配置，可以在任意环境、任意时间让应用正常地运行。

Docker在开发和运维过程中，有如下几个方面的优势：

- 更快速的交付和部署

​	开发人员可以使用镜像快速构建一套标准的开发环境。而测试和运维人员可以直接使用相同环境来部署代码。

- 更高效的资源利用

 	内核级虚拟化，可以实现更高的性能，同时对资源的额外需求很低。

- 更轻松的迁移和扩展

​	可以在任意平台上运行，包括物理机/虚拟机/公有云/私有云/个人电脑/服务器

- 更简单的更新管理

​	使用Dockerfile，只需小小的配置修改，就可以替代以往大量的更新操作，并且所有修改都是以增量的方式来进行分发和更新。

## Docker与虚拟机比较

<font color=red> Docker容器很快，启动和停止可以在秒级实现。</font>
- Docker容器对系统资源需求很少，一台主机可以同时运行数千个Docker容器
- Docker通过类似Git的操作来方便用户获取/分发和更新应用镜像，指令简明，学习成本较低
- Docker通过Dockerfile配置文件来支持灵活的自动化创建和部署机制，提高工作效率。

传统虚拟机方式运行N个不同的应用就要启动N个虚拟机，而且每个虚拟机都需要单独分配内存/磁盘等资源，而Docker只需要启动N个隔离的容器，并将应用放到容器内即可。

Docker利用Linux系统上的多种防护机制实现了严格可靠的隔离，另外Docker还引入了安全选项和镜像签名机制，极大地提高了使用Docker的安全性。

| 特性       | 容器               | 虚拟机     |
| ---------- | ------------------ | ---------- |
| 启动速度   | 秒级               | 分钟级     |
| 硬盘使用   | 一般位MB           | 一般位GB   |
| 性能       | 接近原生           | 弱于       |
| 系统支持量 | 单机支持上千个容器 | 一般几十个 |
| 隔离性     | 安全隔离           | 完全隔离   |

通过图能够清晰地看出虚拟机和Docker之间的区别：
![虚拟机与Docker对比](../../img/虚拟机与Docker对比.png)

## 虚拟化与Docker

虚拟化在维基百科上的定义：
<font color=purple> 在计算机技术中，虚拟化是一种资源管理技术，是将计算机的各种实体资源，如服务器、网络、内存及存储等，予以抽象、转换后呈现出来，打破实体结构间的不可切割的障碍，使用户可以用比原本的组态更好的方式来应用这些资源。</font>

虚拟化技术的核心是对资源进行抽象，从大类上分，可分为基于硬件的虚拟化和基于软件的虚拟化。其中，真正意义上的基于硬件的虚拟化技术并不多见。
基于软件的虚拟化从对象所在的层次，又可以分为应用虚拟化和平台虚拟化（通常说的虚拟机技术即属于这个范畴）。

应用虚拟化：指的是一些模拟设备或Wine这样的软件。
平台虚拟化：可以细分为如下几个子类：

- <font color=red>完全虚拟化：虚拟机模拟完整的底层硬件环境和特权指令的执行过程，客户操作系统无需进行修改。例如VMware Workstation/VirtualBox/QEMU等</font>
- <font color=green>硬件辅助虚拟化：利用硬件（主要是CPU）辅助支持（目前x86体系结构上可用的硬件辅助虚拟化技术包括Intel-V和AMD-V）处理敏感指令来实现完全虚拟化的功能，客户操作系统无需修改，例如VMware Workstation/Xen/KVM</font>
- <font color=skyBlue>部分虚拟化：只针对部分硬件资源进行虚拟化，客户操作系统需要进行修改。现在有些虚拟化技术的早期版本仅支持部分虚拟化。</font>
- <font color=chartreuse> 超虚拟化：部分硬件接口以软件的形式提供给客户机操作系统，客户操作系统需要进行修改，例如早期的Xen</font> 
- <font color=blue>操作系统级虚拟化：内核通过创建多个虚拟的操作系统实例（内核和库）来隔离不同的进程。容器相关技术即在这个范畴。</font>
  Docker容器是在操作系统层面上实现虚拟化，直接复用本地主机的操作系统，因此更加轻量级。

### 在Windows上运行docker

Docker 实质上是在已经运行的 Linux 下制造了一个隔离的文件环境，因此它执行的效率几乎等同于所部署的 Linux 主机。
因此，Docker 必须部署在 Linux 内核的系统上。如果其他系统想部署 Docker 就必须安装一个虚拟 Linux 环境。
在 Windows 上部署 Docker 的方法都是先安装一个虚拟机，并在安装 Linux 系统的的虚拟机中运行 Docker。
Docker Desktop 是 Docker 在 Windows 10 和 macOS 操作系统上的官方安装方式，这个方法依然属于先在虚拟机中安装 Linux 然后再安装 Docker 的方法。

# 二、Docker的核心概念和安装

## Docker镜像

 Docker镜像类似于虚拟机镜像，可以将它理解为一个面向Docker引擎的只读模板，包含了文件系统。
 镜像是创建Docker容器的基础，通过版本管理和增量的文件系统，Docker提供了一套十分简单的机制来创建和更新现有的镜像，用户甚至可以直接从网上下载一个已经做好的应用镜像，并通过简单的命令就可以直接使用。

## Docker容器

 Docker容器类似于一个轻量级的沙箱，Docker利用容器来运行和隔离应用。
 容器是从镜像创建的应用运行实例，可以将其启动、开始、停止、删除，而这些容器都是相互隔离、互不可见的。
 镜像自身是只读的，容器从镜像启动时，Docker会在镜像的最上层创建一个可写层，镜像本身将保持不变。

## Docker仓库

 Docker仓库类似于代码仓库，是Docker集中存放镜像文件的场所。
 注册服务器是存放仓库的地方，其上往往存放这多个仓库，每个仓库集中存放某一类镜像，通过不同的标签来进行区分。
 例如存放Ubuntu操作系统镜像的仓库，称为Ubuntu仓库。

 当用户创建了自己的镜像之后可以使用push命令将它上传到指定的公有或者私有仓库。这样用户下次在另外一台机器上使用该镜像时，只需将其从仓库上pull下来就可以了。

# 三、镜像

## 获取镜像

使用docker pull命令从网络上下载镜像。格式为docker  pull  NAME[:TAG]
如果不指定TAG，则默认会选择latest标签，即下载仓库中最新版本的镜像。
镜像文件一般由若干层组成，下载过程中会获取输出镜像的各层信息。层其实是AUFS(高级联合文件系统)中的重要概念，是实现增量保存与更新的基础。

## 查看镜像信息

docker images 命令可以列出本地主机上已有的镜像

![docker images](../../img/docker_image.png)

在列出信息中，可以看到几个字段信息：

- 来自于哪个仓库，比如ubuntu仓库

- 镜像的标签信息，比如14.04

- 镜像的ID号（唯一）

- 创建时间

- 镜像大小

使用docker  tag命令为本地镜像添加新的标签，通过镜像ID可以看出它们实际上是同一个镜像文件。

![docker tag](../../img/docker_tag.png)

<font color=blue>使用docker inspect命令可以获取该镜像的详细信息</font>

## 搜寻镜像

docker search命令可以搜索远端仓库中共享的镜像，返回很多包含关键字的镜像，其中包括镜像名字，描述，星级（表示该镜像受欢迎程度），是否官方创建，是否自动创建等。

默认的输出结果将按照星级评价进行排序。

## 删除镜像

使用docker  rmi命令可以删除镜像，命令格式为docker  rmi  IMAGE[image ...]，其中IMAGE可以为标签或ID

当同一个镜像存在多个标签时，docker  rmi 命令只是删除了该镜像多个标签中的指定标签而已，并不影响镜像文件。

当镜像只剩下一个标签的时候就要注意了，此时再使用docker  rmi命令会彻底删除该镜像。

### 使用镜像ID删除镜像

使用docker rmi 命令后面跟上镜像的ID时，会先尝试删除所有指向该镜像的标签，然后删除该镜像文件本身。

当有该镜像创建的容器存在时，镜像文件默认时无法被删除的。

正确的做法是，先删除依赖该镜像的所有容器，再来删除镜像。

- sudo  docker  rm   容器ID
- sudo  docker   rmi  镜像ID

## 创建镜像

创建镜像的方法有三种：基于已有镜像的容器创建，基于本地模板导入，基于Dockerfile创建

### 基于已有镜像的容器创建

该方法主要是使用docker  commit 命令，其命令格式为docker  commit  [OPTIONS] CONTAINER  [REPOSITORY[:TAG]],主要选项包括：

- -a, --author="" 作者信息
- -m,  --message="" 提交消息
- -p,  --pause=true, 提交时暂停容器运行

使用如下命令创建新的镜像，docker  images中会有test镜像出现

![提交新镜像](../../img/提交新镜像.png)

### 基于本地模板导入

从OPENVZ网站下载模板：https://download.openvz.org/template/precreated/

![本地模板导入](../../img/本地模板导入.png)

从OPENVZ网站上下载ubuntu 16.04模板

执行sudo cat ubuntu-16.04-x86_64.tar.gz |docker import - ubuntu:16.04报错：	

- sudo groupadd docker     #添加docker用户组
- sudo gpasswd -a $USER docker     #将登陆用户加入到docker用户组中
- newgrp docker     #更新用户组
- docker ps    #测试docker命令是否可以使用sudo正常使用

再执行cat ubuntu-16.04-x86_64.tar.gz |docker import - ubuntu:16.04不再报错

执行docker   images 后发现多了一个ubuntu 16.04的镜像

## 存储和载入镜像

可以使用docker  save 和 docker   load命令来存储和载入镜像

![存储和载入镜像](../../img/存储和载入镜像.png)

## 上传镜像

可以使用docker push命令上传镜像到仓库，默认上传到DockerHub官方仓库，命令格式为docker push  NAME[:TAG]

第一步：

centos7系统登录方式：

```dockerfile
docker   login
```

centos8及以上系统登录方式：

```dockerfile
podman login docker.io
```

<font  color=red>注意：podman login 和 podman login docker.io是不同的</font>

![podman login](..\..\img\podman_login.png)

第二步：给镜像打标签

centos7  打标签方式

```dockerfile
docker tag 29bad2f8a84d zheng1dong2/new-repo:1.0
```

centos8及以上打标签方式

```dockerfile
podman tag  29bad2f8a84d  docker.io/zheng1dong2/new-repo:1.0
```

<font color=red>注意：29bad2f8a84d是镜像id,  zheng1dong2是dockerhub上注册的账号名称，new-repo是在dockerhub上创建的仓库名称，1.0是tag标签，这里podman 打标签需要加上docker.io</font>

否则可能出现如下问题：

```dockerfile
[root@localhost .ssh]# podman push zheng1dong2/new-repo:1.0
Getting image source signatures
Copying blob b362758f4793 [--------------------------------------] 8.0b / 190.9MiB
Copying blob 7b9106e4f33d [--------------------------------------] 8.0b / 2.0KiB
Copying blob 4849f19ea97e [--------------------------------------] 8.0b / 9.0KiB
Copying blob fb7792cec03a [--------------------------------------] 8.0b / 13.5KiB
Copying blob 9a044e4cd3a9 [--------------------------------------] 8.0b / 21.6MiB
Copying blob 50d50cf31f6c [--------------------------------------] 8.0b / 5.0KiB
Copying blob 6727c67c3a66 [--------------------------------------] 8.0b / 3.5KiB
Copying blob e29819798e50 [--------------------------------------] 8.0b / 5.5KiB
Copying blob 6339e3d757ac [--------------------------------------] 8.0b / 2.0KiB
Copying blob 194fcbcfcb81 [--------------------------------------] 8.0b / 787.8MiB
Copying blob 97148cd18852 [--------------------------------------] 8.0b / 2.5KiB
Error: Error copying image to the remote destination: Error writing blob: Error initiating layer upload to /v2/zheng1dong2/new-repo/blobs/uploads/ in registry-1.docker.io: errors:
denied: requested access to the resource is denied
unauthorized: authentication required

```

第三步：上传镜像到docker  hub

centos7 系统：

```dockerfile
docker push zheng1dong2/new-repo:1.0
```

centos8及以上系统：

```dockerfile
podman push docker.io/zheng1dong2/new-repo:1.0
```

打开hub.docker.com，进入自己的空间查看刚上传的镜像：

![new-repo](..\..\img\new-repo.png)

# 四、容器

## 创建容器

使用docker  create 命令新建一个容器

docker create 命令新建的容器处于停止状态，可以使用docker  start 命令启动。

启动容器有两种方式，一种是基于镜像新建一个容器并启动，另外一个是将在终止状态的容器重新启动。

docker run 等价于docker   create命令 加上  docker  start命令。

当利用docker  run来创建并启动容器时，Docker在后台运行的标准操作包括：

- 检查本地是否存在指定的镜像，不存在就从公有仓库下载（这一步基本能保证镜像是有的）
- 利用镜像创建并启动一个容器
- 分配一个文件系统，并在只读的镜像层外面挂载一层可读写层。
- 从宿主主机配置的网桥接口中桥接一个虚拟接口到容器中去。
- 从地址池配置一个IP地址给容器。
- 执行用户指定的应用程序。
- 执行完毕后容器被终止。

docker  run  -it    ubuntu:14.04   /bin/bash  能够启动一个bash终端，允许用户交互。

其中 -t 选项让Docker  分配一个伪终端并绑定到容器的标准输入上， -i  则让容器的标准输入保持打开。

### 守护态运行

docker run -d   ubuntu /bin/sh -c  "while true; do echo hello world; sleep 1; done"

![守护态运行](../../img/守护态运行.png)

要获取容器的输出信息，可以通过docker   logs命令查看。

## 终止容器

可以使用docker  stop来终止一个运行中的容器， 命令的格式为 docker    stop  [-t | --time[=10]]

它首先会向容器发送SIGTERM信号，等待一段时间后默认为10秒，再发送SIGKILL信号终止容器。

- docker   ps  -a  -q：查看处于终止状态的容器的ID信息
- docker   start   容器ID：启动终止状态的容器
- docker   restart  容器ID：将一个运行态的容器终止，然后再重新启动它

## 进入容器

### attach命令

docker  attach 是Docker自带的命令

![docker_attach](../../img/docker_attach.png)

多个窗口同时attach到同一个容器时，所有窗口都会同步显示，当某个窗口因命令阻塞时，其他窗口也无法执行操作。

### exec命令

进入一个容器并启动一个bash:

docker   exec  -it    容器ID   /bin/bash

## 删除容器

docker   rm命令删除处于终止状态的容器，命令格式为docker   rm   [OPTIONS]  CONTAINER  [CONTAINER...]

支持的选项包括：

- -f,  --force = false 强行终止并删除一个运行中的容器
- -l ,  --link = false 删除容器的连接， 但保留容器
- -v, --volumes = false  删除容器挂载的数据卷

不建议使用-f，这样运行中的容器会突然中断

## 导入和导出容器

导出容器是指导出一个已经创建的容器到一个文件，不管此时这个容器是否处于运行态，可以使用docker  export命令，该命令格式为 docker   export   CONTAINER

```dockerfile
docker  export  -o  test_for_fun.tar   ce5
```

导入容器可以使用docker import  命令导入，成为镜像。

```dockerfile
docker  import   test_for_fun.tar  -  test/ubuntu:v1.0
```

docker  save与docker   export的区别：

docker save用于导出镜像到文件，包含镜像元数据和历史信息；
docker export用于将当前容器状态导出至文件，类似快照，所以不包含元数据及历史信息，体积更小，此外从容器快照导入时也可以重新指定标签和元数据信息；

## 查看容器

### 查看容器详情

docker  container  inspect  test

会返回容器id, 创建时间，路径， 状态，镜像，配置等在内的各项信息

### 查看容器内进程

docker  top  容器ID

该命令会打印出容器内的进程信息，包括PID，用户，时间，命令等。

### 查看统计信息

docker  stats  容器ID

会显示CPU，内存，存储，网络等使用情况的统计信息

## 其他容器命令

### 复制文件

cp命令支持在容器和主机之间复制文件，命令格式为docker  cp  [OPTIONS]   container:SRC_PATH  DEST_PATH

支持的选项包括：

- -a ,  -archive: 打包模式，复制文件会带有原始的uid/gid信息
- -L，-follow-link:跟随软连接，当源路径为软连接时，默认只复制链接信息，使用该选项会复制链接的目标内容

例如，将本地的路径data复制到test容器(容器id,  d0f开头)的/tmp路径下

```dockerfile
docker  [container]  cp  data  d0f:/tmp/
```

### 查看变更

docker   [container]   diff    容器ID

### 查看端口映射

docker  container   port  容器id

### 更新配置

docker  container  update  容器id

例如，限制总配额为1秒，容器test(d0f)所占用时间为10%

```dockerfile
docker   update  [OPTIONS]  --cpu-quota   1000000   d0f
```

支持的选项包括：

□ -blkio-weigh uint16 ：更新块 IO 限制， 10~lOOO ，默认值为 0，代表着无限制；

□ -cpu-period   int：限制 CPU 调度器 CFS (Completely Fair Scheduler) 使用时间，

单位为微秒，最小 1000;

□ -cpu-quo int：限制 CPU 调度器 CFS 配额，单位为微秒，最小 1000;

□ -cpu-rt－ period int：限制 CPU 调度器的实时周期，单位为微秒；

□ -cpu-rt－ run ime int：限制 CPU 调度器的实时运行时，单位为微秒；

□ -c, -cpu-shares int：限制 CPU 使用份额；

□ -cpus decimal: 限制 CPU 个数；

□ -cpuset－cpus string: 允许使用的 CPU 核，如 0-3, 0,1; 

□ -cpuset－ mems string: 允许使用的内存块，如 0-3, 0,1; 

□ -kernel-memory bytes: 限制使用的内核内存；

□ -m, -memory bytes ：限制使用的内存；

□ -memory-reservation bytes: 内存软限制；

□ -memory-swap bytes ：内存加上缓存区的限制，－1 表示为对缓冲区无限制；

□ -restart string: 容器退出后的重启策略。

# 五、仓库

仓库是集中存放镜像的地方

仓库是一个具体的项目或目录，dl.dockerpool.com/ubuntu中，dl.dockerpool.com是注册服务器，ubuntu是仓库名。

仓库又分为公共仓库和私有仓库

## Docker Hub













