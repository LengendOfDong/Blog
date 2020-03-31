# shell source sh bash与./的区别

# source的使用

```
source FileName

```

作用:在当前bash环境下读取并执行FileName中的命令。该filename文件可以无"执行权限"<br/>
注：该命令通常用命令“.”来替代。

```
source .bash_profile
. .bash_profile
两者等效

```

source通常用来生效刚刚修改的文件

# sh与bash的使用

```
sh FileName
 bash FileName

```

作用:在当前bash环境下读取并执行FileName中的命令。该filename文件可以无"执行权限"<br/>
注：两者在执行文件时的不同，是分别用自己的shell来跑文件。

# ./的使用

<img alt="在这里插入图片描述" data-src="https://img-blog.csdnimg.cn/20200109101618427.png" src="https://csdnimg.cn/release/phoenix/write/assets/img_default.png"/><br/>
作用:打开一个子shell来读取并执行FileName中命令。<br/>
注：运行一个shell脚本时会启动另一个命令解释器.<br/>
注：需要加权限才可以执行。

# source sh bash 与./的区别

如下例子中，展示source 、sh、bash与./的区别：<br/>
结论一:脚本加上执行权限后，./**.sh, sh ./**.sh与bash ./*.sh相同，此三种执行脚本的方式都是重新启动一个子shell,在子shell中执行此脚本。

结论二: source  *.sh和.  *.sh的执行方式是等价的，即两种执行方式都是在当前shell进程中执行此脚本，而不是重新启动一个shell 而在子shell进程中执行此脚本。<br/>
<img alt="在这里插入图片描述" data-src="https://img-blog.csdnimg.cn/2020010910011516.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70" src="https://csdnimg.cn/release/phoenix/write/assets/img_default.png"/><br/>
验证依据：没有被export导出的变量（即非环境变量）是不能被子shell继承的<br/>
<img alt="在这里插入图片描述" data-src="https://img-blog.csdnimg.cn/20200109102437144.png" src="https://csdnimg.cn/release/phoenix/write/assets/img_default.png"/>
