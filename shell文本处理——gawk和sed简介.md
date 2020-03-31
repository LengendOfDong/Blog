# shell文本处理——gawk和sed简介

## gawk程序

gawk程序时Unix中的原始awk程序的GNU版本。在gawk编程语言中，可以做如下的事情：

2.使用数据字段变量<br/>
默认情况下，gawk会将如下变量分配给它在文本行中发现的数据字段：

```
[root@ommleft zd]# cat data2.txt
One line of test text.
Two line of test text.
Three line of test text.

```

```
[root@ommleft zd]# gawk '{print $1}' data2.txt
One
Two
Three

```

使用字段分隔符来读取文件，可以使用-F选项指定

```
[root@ommleft zd]# gawk -F: '{print $1}' /etc/passwd
root
bin
daemon
adm
lp
sync
shutdown

```

3.从程序脚本中使用多个命令<br/>
要在命令行上的程序脚本中使用多条命令，可以在命令之间放个分号即可。

```
[root@ommleft zd]# echo "My name is Rich"|gawk '{$4="Christine";print $0}'
My name is Christine

```

4.从文件中读取程序<br/>
gawk编辑器允许将程序存储到文件中，然后在命令行中引用。

```
[root@ommleft zd]# more script.gawk
{print $1 "'s home directory is " $6}
[root@ommleft zd]# gawk -F: -f script.gawk /etc/passwd
root's home directory is /root
bin's home directory is /bin
daemon's home directory is /sbin
adm's home directory is /var/adm
lp's home directory is /var/spool/lpd

```

5.gawk BEGIN<br/>
gawk中的BEGIN关键字会强制gawk在读取数据前执行BEGIN关键字后指定的程序脚本。

```
[root@ommleft zd]# gawk 'BEGIN{print "Hello World!"}'
Hello World!

```

读取文本并显示

```
[root@ommleft zd]# cat data3.txt
Line 1
Line 2
Line 3

```

```
root@ommleft zd]# gawk 'BEGIN {print "The data3 file contents:"}{print $0}' data3.txt
The data3 file contents:
Line 1
Line 2
Line 3

```

6.gawk END<br/>
与BEGIN关键字相似，END关键字允许指定一个程序脚本，gawk会在读完数据之后执行。

```
[root@ommleft zd]# gawk 'BEGIN{print "The data3 file contents:"}
{print $0}
END{print "End of File"}' data3.txt
The data3 file contents:
Line 1
Line 2
Line 3
End of File

```

## sed编辑器

sed编辑器被称作流编辑器（stream editor），sed编辑器会执行如下操作：<br/>
1）一次从输入中读取一行数据。<br/>
2）根据所提供的编辑器命令匹配数据<br/>
3）按照命令修改流中的数据<br/>
4）将新的数据输出到STDOUT<br/>
sed命令的格式如下：<br/>
sed options  script   file<br/>
sed命令选项如下：<br/>
|选项|描述|<br/>
|-e command|将指定的命令添加到已有命令中|<br/>
|-f  file|将file中指定的命令添加到已有命令中|<br/>
|-n |不产生命令输出，使用print命令完成输出|

1.在命令行定义编辑器命令

```
[root@ommleft zd]# echo "This is a test"|sed 's/test/trial/' 
This is a trial

```

还可以对文件数据进行处理，输出STDOUT,但并不会修改文件中的数据

```
[root@ommleft zd]# cat data1.txt
The quick brown fox jumps over the lazy dog.
The quick brown fox jumps over the lazy dog.
The quick brown fox jumps over the lazy dog.
The quick brown fox jumps over the lazy dog.
The quick brown fox jumps over the lazy dog.

```

```
[root@ommleft zd]# sed 's/dog/cat/' data1.txt
The quick brown fox jumps over the lazy cat.
The quick brown fox jumps over the lazy cat.
The quick brown fox jumps over the lazy cat.
The quick brown fox jumps over the lazy cat.
The quick brown fox jumps over the lazy cat.

```

2.在命令行使用多个编辑器命令<br/>
要在sed命令行上执行多个命令时，只要用-e选项就可以了。

```
[root@ommleft zd]# sed -e 's/brown/green/;s/dog/cat/' data1.txt
The quick green fox jumps over the lazy cat.
The quick green fox jumps over the lazy cat.
The quick green fox jumps over the lazy cat.
The quick green fox jumps over the lazy cat.
The quick green fox jumps over the lazy cat.

```

3.从文件中读取编辑器命令

```
[root@ommleft zd]# cat script.sed
s/brown/green/
s/fox/elephant/
s/dog/cat/
[root@ommleft zd]# sed -f script.sed data1.txt
The quick green elephant jumps over the lazy cat.
The quick green elephant jumps over the lazy cat.
The quick green elephant jumps over the lazy cat.
The quick green elephant jumps over the lazy cat.
The quick green elephant jumps over the lazy cat.

```
