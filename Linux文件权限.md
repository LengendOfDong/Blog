# Linux文件权限

## Linux的安全性

用户权限是通过创建用户时分配的用户id(UserId,通常缩写为UID)来跟踪的。UID是数值，每个用户都有唯一的UID，但在登录系统时用的不是UID，而是登录名。<br/>
Linux系统使用特定的文件和工具来跟踪和管理系统上的用户账户。

## /etc/passwd文件

Linux系统使用一个专门的文件来将用户的登录名与UID值进行匹配。这个文件就是/etc/passwd文件，它包含了一些与用户有关的信息。<br/>
Linux为系统账户预留了500以下的uid值。为普通用户创建账户时，大多数Linux系统会从500开始，将第一个可用UID分配给这个账户。<br/>
/etc/passwd文件的字段包含了如下信息：

## /etc/shadow文件

/etc/shadow文件对Linux系统的密码管理提供了更多的控制。<br/>
在/etc/passwd文件中的每条记录都有9个字段：

## 添加新用户

## 删除用户

默认情况下，userdel只会删除/etc/passwd文件中的用户信息，而不会删除系统中属于该账户的任何文件。<br/>
如果加上-r参数，userdel会删除用户的HOME目录以及邮件目录。

## 修改用户

<th align="center">命令</th><th align="left">描述</th>
|------
<td align="center">usermod</td><td align="left">修改用户账户的字段，还可以指定主要组以及附加组的所属关系</td>
<td align="center">passwd</td><td align="left">修改已有用户的密码</td>
<td align="center">chpasswd</td><td align="left">从文件中读取登录名密码对，并更新密码</td>
<td align="center">chage</td><td align="left">修改密码的过期日期</td>
<td align="center">chfn</td><td align="left">修改用户账户的备注信息</td>
<td align="center">chsh</td><td align="left">修改用户账户的默认登录shell</td>

## /etc/group文件

与用户账户类似，组信息也保存在系统的一个文件中。/etc/group文件包含系统上用到的每个组的信息。<br/>
<img alt="在这里插入图片描述" data-src="https://img-blog.csdnimg.cn/20190913094800920.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70" src="https://csdnimg.cn/release/phoenix/write/assets/img_default.png"/><br/>
上图中，系统账户用的组通常会分配低于500的GID值，而用户组的GID则会从500开始分配。<br/>
/etc/group文件有4个字段：

## 创建新组

## 修改组

## 默认文件权限

umask 命令用来设置所创建文件和目录的默认权限

```
$ umask
0022
$

```

umask第一位代表特别的安全特性，叫做粘着位，后面的3位表示文件或目录对应的umask八进制值。<br/>
umask值只是个掩码，它会屏蔽掉不想授予该安全级别的权限。<br/>
要把umask值从对象的全权限值中减掉。对文件来说，全权限的值是666（所有用户都有读写权限），而对目录来说，则是777（所有用户都有读，写，执行权限）<br/>
文件一开始的权限是666，减去umask值022之后，剩下的权限就成了644.<br/>
同样道理，对于新创建的目录，则是用777减去022，即755的权限。

## 改变权限

1.使用八进制模式进行安全性设置<br/>
chmod命令用来改变文件和目录的安全性设置,格式如下：<br/>
chmod  options   mode  file<br/>
如chmod  760  newFile<br/>
2.使用符号模式进行安全性设置<br/>
[ugoa…][+ - =][rwxXstugo…]<br/>
第一个括号：

## 改变所属关系

Linux提供了两个命令来实现这个功能：<br/>
chown命令用来改变文件的属主<br/>
chgrp命令用来改变文件的默认属组<br/>
chown命令的格式如下：<br/>
chown  options  owner[.group]  file<br/>
如：<br/>
可用登录名或UID指定文件的新属主

```
chown dan newfile

```

同时改变文件的属主和属组

```
chown dan.shared  newfile

```

chgrp 命令可以更改文件或目录的默认属组

```
chgrp shared  newfile

```


