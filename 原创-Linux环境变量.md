# 原创：Linux环境变量

## 全局变量

查看全局变量，可以使用env或printenv命令

```
➜  ~ printenv
TERM_SESSION_ID=w0t0p0:2D8ACFEB-DE56-4295-B5A1-4A816EAA8E2F
SSH_AUTH_SOCK=/private/tmp/com.apple.launchd.X9fDLzbfUY/Listeners
Apple_PubSub_Socket_Render=/private/tmp/com.apple.launchd.YkaNEGB7E4/Render
COLORFGBG=15;0
ITERM_PROFILE=Default
XPC_FLAGS=0x0
LANG=zh_CN.UTF-8
PWD=/Users/apple
SHELL=/bin/zsh
SECURITYSESSIONID=186a8

```

查看个别环境变量的值，可以使用printenv命令

```
➜  ~ printenv HOME
/Users/apple

```

另外全局变量还可以作为命令行参数，如ls  $HOME

## 局部变量

在Linux系统中并没有一个只显示局部环境变量的命令。set命令会显示为某个特定进程设置的所有环境变量，包括局部变量、全局变量以及用户自定义变量。

```
➜  ~ set
'!'=0
'#'=0
'$'=401
'*'=(  )
-=569JNRXZghiklms
0=-zsh
'?'=0
@=(  )
ANDROID_HOME='/Users/*****/Library/Android/sdk'
ARGC=0
Apple_PubSub_Socket_Render=/private/tmp/com.apple.launchd.YkaNEGB7E4/Render
BG
CDPATH=''
COLORFGBG='15;0'
COLORTERM=truecolor
COLUMNS=101
COMMAND_MODE=unix2003
CPUTYPE=x86_64
EGID=20
EUID=501

```

set命令输出的内容明显比env的输出要多，另外输出的内容按照字母顺序排列。

## 设置用户定义变量

```
➜  ~ echo $my_variable

➜  ~ my_variable=HELLO
➜  ~ echo $my_variable
HELLO

```

```
$my_variable="I  am Global now"
$export  my_variable
$echo  $my_variable
I  am Global now

```

通过export命令来完成，变量名前面不需要加$<br/>
在子shell中改变全局变量的值，这种改变只在子shell中有效，即使使用export命令也不会被反映到父shell中。

## 删除环境变量

使用unset命令删除已经存在的环境变量

```
➜  ~ echo $my_variable
HELLO
➜  ~ unset my_variable
➜  ~ echo $my_variable

```

<mark>注</mark>：如果要用到变量，使用"$";如果要操作变量，不使用"$"<br/>
在子shell中对全局变量所做的增删改，都不能反映到父shell中。

## 设置PATH环境变量

将应用程序的命令目录加入到PATH环境变量之后

```
PATH=$PATH:/home/christine/Scripts

```

<mark>注</mark>：如果希望子shell也能找到程序的位置，一定要把修改后的PATH环境变量导出

## 定位系统环境变量

启动bash shell有3种方式：

1.登录shell<br/>
当登录Linux系统时，bash shell会作为登录shell启动。登录shell会从5个不同的启动文件里读取命令：<br/>
1)/etc/profile<br/>
2)$HOME/.bash_profile<br/>
3)$HOME/.bashrc<br/>
4)$HOME/.bash_login<br/>
5)$HOME/.profile<br/>
/etc/profile文件是系统上默认的bash shell的主启动文件。系统上的每个用户登录时都会执行这个启动文件。<br/>
不同的发行版执行/etc/profile时各不相同。但都会去执行/etc/profile.d中的所有文件，这是一个放置特定应用程序启动文件的地方。<br/>
剩下的启动文件都起着同一个作用：提供一个用户专属的启动文件来定义该用户所用到的环境变量。<br/>
shell会按照下列顺序，运行第一个被找到的文件，余下的则被忽略：

```
\$HOME/.bash_profile
\$HOME/.bash_login
\$HOME/.profile``

```

$HOME/.bashrc文件是在其他文件中运行的。

2.交互式shell进程<br/>
如果bash是作为交互式shell启动的，它就不会访问/etc/profile文件，只会检查用户HOME目录下的.bashrc文件。<br/>
.bashrc文件有两个作用：一是查看/etc目录下通用的bashrc文件，二是为用户提供一个定制自己的命令别名和私有函数的地方。

3.非交互式shell<br/>
系统执行shell脚本时用的是这种shell.<br/>
子shell可以继承父shell导出过的变量。

## 环境变量持久化

对于全局环境变量来说，最好是在/etc/profile.d目录中创建一个以.sh结尾的文件。把所有新的或修改过的全局环境变量设置放在这个文件中<br/>
在大多数发行版中，存储个人用户永久性bash shell变量的地方是$HOME/.bashrc文件。这一点适用于所有类型的shell进程。<br/>
对于alias命令，可放在$HOME/.bashrc启动文件中，使其效果永久化。
