# 原创：Linux/Windows 下解压和打包jar包

**Linux系统和Windows系统下打包jar包相同**：<br/>
把当前目录下的所有文件打包成project.jar<br/>
jar -cvf   project.jar  ./

-c   创建jar包<br/>
-v   显示过程信息<br/>
-f    文件

**Linux下解压jar包**：

jar -xvf project.jar

解压到当前目录，不会创建新的文件夹，而是解压在当前文件夹

解压jar包到指定目录：<br/>
jar -xvf   project.jar   -C   /hello

-C  表示转到相应的目录下执行jar命令,相当于cd到那个目录，然后不带-C执行jar命令

**Windows下解压jar包**：<br/>
打开**git  bash**(CMD应该是不行的，亲测) ,解压jar包到当前目录：

unzip  project.jar

解压jar包到指定目录：

unzip  project.jar  -d  /hello
