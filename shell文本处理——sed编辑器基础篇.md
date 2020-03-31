# shell文本处理——sed编辑器基础篇

## 替换选项

1.替换标记<br/>
在替换的时候，会出现如下情况：

```
[root@ommleft zd]# more data4.txt
This is a test of the test script.
This is the second test of the test script.
[root@ommleft zd]# sed 's/test/trial/' data4.txt
This is a trial of the test script.
This is the second trial of the test script.

```

只能替换第一处匹配的情况，若要替换不同地方出现的文本，则必须使用替换标记。<br/>
替换标记使用方式如下：<br/>
s/pattern/replacement/flags<br/>
有4种可用的替换标记：<br/>
-数字，表明新文本将替换第几处模式匹配的地方

使用数字指定匹配行中位置，如下匹配行中的第二处

```
[root@ommleft zd]# sed 's/test/trial/2' data4.txt
This is a test of the trial script.
This is the second test of the trial script.

```

使用g匹配所有情况，如下所示：

```
[root@ommleft zd]# sed 's/test/trial/g' data4.txt
This is a trial of the trial script.
This is the second trial of the trial script.

```

使用p会打印出原来的行，感觉用处不大，举例如下：

```
[root@ommleft zd]# sed 's/test/trial/p' data4.txt
This is a trial of the test script.
This is a trial of the test script.
This is the second trial of the test script.
This is the second trial of the test script.

```

也可以同时使用多个替换标记,如下同时使用g和p：

```
[root@ommleft zd]# sed 's/test/trial/gp' data4.txt
This is a trial of the trial script.
This is a trial of the trial script.
This is the second trial of the trial script.
This is the second trial of the trial script.

```

使用w file,将替换结果写入文件中，如下将替换结果写入到test.txt中：

```
[root@ommleft zd]# sed 's/test/trial/w test.txt' data4.txt
This is a trial of the test script.
This is the second trial of the test script.

```

2.替换字符<br/>
在写脚本的时候经常会遇到一些不太方便在替换模式中使用的字符，如常用的正斜线（/）<br/>
如：sed  ‘s//bin/bash//bin/csh/’   /etc/passwd<br/>
上面例子中使用转义符进行转义，会造成困惑，可以使用其他字符来替换命令中的字符分隔符：

```
$ sed  's!/bin/bash!/bin/csh!'  /etc/passwd

```

在上例中，使用感叹号作为分隔符，路径就比较明显了。

## 使用地址

如果只将命令作用于特定行或者某些行，则必须用行寻址（line  addressing）.<br/>
在sed编辑中有两种形式的行寻址：

```
[address] command

```

也可以将特定地址的多个命令分组：

```
address {
    command1
    command2
    command3
}

```

1.数字方式的行寻址

```
[root@ommleft zd]# sed '2s/test/trail/' data4.txt
This is a test of the test script.
This is the second trail of the test script.

```

sed编辑器只修改了地址指定的第二行的文本。同时还可以设置行地址区间，例如修改第2，3行

```
[root@ommleft zd]# more data1.txt
The quick brown fox jumps over the lazy dog.
The quick brown fox jumps over the lazy dog.
The quick brown fox jumps over the lazy dog.
The quick brown fox jumps over the lazy dog.
The quick brown fox jumps over the lazy dog.
[root@ommleft zd]# sed '2,3s/dog/cat/' data1.txt 
The quick brown fox jumps over the lazy dog.
The quick brown fox jumps over the lazy cat.
The quick brown fox jumps over the lazy cat.
The quick brown fox jumps over the lazy dog.
The quick brown fox jumps over the lazy dog.

```

可以修改从某行到文末的所有行,当不知道文本有多少行的时候，此种方法使用起来非常方便：

```
[root@ommleft zd]# sed '3,$s/dog/cat/' data1.txt
The quick brown fox jumps over the lazy dog.
The quick brown fox jumps over the lazy dog.
The quick brown fox jumps over the lazy cat.
The quick brown fox jumps over the lazy cat.
The quick brown fox jumps over the lazy cat.

```

2.使用文本模式过滤器<br/>
通过文本模式可以使用字符串方式直接替换匹配的行

```
[root@ommleft zd]# grep zternc /etc/passwd
zternc:x:1005:1006::/home/zte/PM:/bin/bash
[root@ommleft zd]# sed '/zternc/s/bash/csh/' /etc/passwd
zternc:x:1005:1006::/home/zte/PM:/bin/csh

```

3.命令组合<br/>
如果需要在单行上执行多条命令，可以用花括号来讲多条命令组合在一起。

```
[root@ommleft zd]# sed '2{
&gt; s/test/trail/
&gt; s/This/That/
&gt; s/script/text/
&gt; }' data4.txt
This is a test of the test script.
That is the second trail of the test text.

```

或者用分号隔开

```
[root@ommleft zd]# sed '2{s/test/trail/;s/This/That/;s/script/text/}' data4.txt
This is a test of the test script.
That is the second trail of the test text.

```

## 删除行

如果需要删除文本流中的特定行，可以用删除命令d。

```
[root@ommleft zd]# more data2.txt
One line of test text.
Two line of test text.
Three line of test text.
[root@ommleft zd]# sed '2d' data2.txt
One line of test text.
Three line of test text.

```

另外文本模式匹配方式也可以用来删除

```
[root@ommleft zd]# sed '/Two/d' data2.txt 
One line of test text.
Three line of test text.

```

还可以跳跃式删除行，如下只删除第1行和第3行

```
[root@ommleft zd]# sed '1d;3d' data2.txt
Two line of test text.

```

使用时注意，有时匹配不到不会报错，而是一直删除下去，如下所示：

```
[root@ommleft zd]# sed '/One/,/Five/d' data2.txt
[root@ommleft zd]# 

```

上面例子中，当匹配到"One"之后，会打开行删除功能，之后由于找不到"Five"，会一直删除下去，导致输出为空

## 插入和附加文本

sed编辑器允许想数据流中插入和附加文本行：<br/>
1）插入（insert）命令（i）会在指定行前增加一个新行；<br/>
2）附加（append）命令（a）会在指定行后增加一个新行；<br/>
可以指定在某行前插入文本，如下所示

```
[root@ommleft zd]# sed '3i\This is an inserted line.' data2.txt
One line of test text.
Two line of test text.
This is an inserted line.
Three line of test text.

```

或者在某行后附加文本，如下所示：

```
[root@ommleft zd]# sed '3a\This is an appended line.' data2.txt
One line of test text.
Two line of test text.
Three line of test text.
This is an appended line.

```

如果想要在文末追加使用如下操作,无需知道最后一行的行号：

```
[root@ommleft zd]# sed '$a\This is an appended line.' data2.txt
One line of test text.
Two line of test text.
Three line of test text.
This is an appended line.

```

要插入或附加多行文本，就必须对要插入或附加的新文本中的每一行使用反斜线，如下所示：

```
[root@ommleft zd]# sed '1i\
&gt; This is one line of new line\
&gt; This is another line of new line.' data2.txt
This is one line of new line
This is another line of new line.
One line of test text.
Two line of test text.
Three line of test text.

```

指定的两行都会被添加到数据流中。

## 修改行

修改（change）命令允许修改数据流中整行文本的内容。它跟插入和附加的工作机制相同，都需要指定新行。

```
[root@ommleft zd]# sed '2c\Second line.' data2.txt
One line of test text.
Second line.
Three line of test text.

```

另外还支持文本模式寻址，如下利用文本模式来匹配第二行进行修改：

```
[root@ommleft zd]# sed '/Two/c\This is Second line.' data2.txt
One line of test text.
This is Second line.
Three line of test text.

```

使用地址区间来修改时，要注意：

```
[root@ommleft zd]# more data1.txt
The quick brown fox jumps over the lazy dog.
The quick brown fox jumps over the lazy dog.
The quick brown fox jumps over the lazy dog.
The quick brown fox jumps over the lazy dog.
The quick brown fox jumps over the lazy dog.
[root@ommleft zd]# sed '2,3c\This is changed.' data1.txt
The quick brown fox jumps over the lazy dog.
This is changed.
The quick brown fox jumps over the lazy dog.
The quick brown fox jumps over the lazy dog.

```

上例中没有逐一修改第二行和第三行，而是两行使用新行来修改，使用时需要注意。

## 转换命令

转换（transform）命令（y）是唯一可以处理单个字符的sed编辑器命令。转换命令格式如下：<br/>
[address]y/inchars/outchars/<br/>
转换命令会对inchars和outchars值进行一对一的映射。这个映射过程会一直持续到处理完指定字符。

```
[root@ommleft zd]# more data5.txt
This is line number 1.
This is line number 2.
This is line number 3.
This is line number 4.
This is line number 1 again.
This is yet another line.
This is the last line in the file.
[root@ommleft zd]# sed 'y/123/789/' data5.txt
This is line number 7.
This is line number 8.
This is line number 9.
This is line number 4.
This is line number 7 again.
This is yet another line.
This is the last line in the file.

```

当inchars和outchars长度不同时，会报错：

```
[root@ommleft zd]# sed 'y/12/789/' data5.txt
sed:-e 表达式 #1，字符9：‘y'命令的字符串长度不同

```

## 打印

sed命令中用来输出数据流打印信息：<br/>
1）p命令用来打印文本行<br/>
2）等号（=）命令用来打印行号<br/>
3）l (小写的L)命令用来列出行<br/>
1.打印行<br/>
用来打印已有的行

```
[root@ommleft zd]# sed '/Two/p' data2.txt
One line of test text.
Two line of test text.
Two line of test text.
Three line of test text.

```

通常在文本模式进行匹配时，与-n同时使用,隐藏其他不相关的行，只显示匹配的行：

```
[root@ommleft zd]# sed -n '/Two/p' data2.txt
Two line of test text.

```

如果想要在替换之前查看原来的行，可以使用如下操作：

```
[root@ommleft zd]# sed -n '/text/{
&gt; p
&gt; s/line/String/p
&gt; }' data2.txt
One line of test text.
One String of test text.
Two line of test text.
Two String of test text.
Three line of test text.
Three String of test text.

```

上例中，对于匹配到“text”的行进行替换，使用此方式，可以方便比较文本的变化。<br/>
2.打印行号<br/>
可以通过等号（=）来打印行号，可以查看匹配文本的行号

```
[root@ommleft zd]# sed -n '/Two/{
&gt; =
&gt; p
&gt; }' data2.txt
2
Two line of test text.

```

3.列出行<br/>
列出（list）命令（l）可以打印数据流中的文本和不可打印的ASCII字符。

```
[root@ommleft zd]# more data6.txt
THis	line	contains	tabs.
[root@ommleft zd]# sed -n 'l' data6.txt
THis\tline\tcontains\ttabs.$

```

上例中制表符的位置使用\t来显示。行尾的美元符表示换行符。

## 使用sed处理文件

1.写入文件<br/>
w命令用来向文件中写入行。该命令格式如下：<br/>
[address]w  filename<br/>
filename可以是相对路径或者绝对路径

```
[root@ommleft zd]# sed -n '1,3w /home/omm/zd/test3.txt' data3.txt
[root@ommleft zd]# more test3.txt
Line 1
Line 2
Line 3

```

若是想要通过公用的文本创建一个数据文件，可以使用此方法来生成。<br/>
比如想使用公共文件/etc/passwd生成root用户的所有配置

```
[root@ommleft zd]# sed -n '/^root/w root.txt' /etc/passwd
[root@ommleft zd]# more root.txt
root:x:0:0:root:/root:/bin/bash

```

2.从文件读取数据<br/>
读取（read)命令（r）允许将一个独立文件中的数据插入到数据流中<br/>
读取命令的格式如下：<br/>
[address]r filename<br/>
filename参数指定了数据文件的绝对路径或者相对路径。sed编辑器会将文件中的文本插入到指定地址之后。

```
[root@ommleft zd]# more data3.txt
Line 1
Line 2
Line 3
[root@ommleft zd]# more data2.txt
One line of test text.
Two line of test text.
Three line of test text.
[root@ommleft zd]# sed '/Line 2/r data2.txt' data3.txt
Line 1
Line 2
One line of test text.
Two line of test text.
Three line of test text.
Line 3

```

上例中将data2.txt中的文本插入到匹配文本行（Line 2）之后，当然也可以使用`sed '2r data2.txt' data3.txt`来进行插入。<br/>
使用读取数据和删除数据来进行替换文本内容：

```
[root@ommleft zd]# more notice.std 
Would the following people:
List
please report to the ship's captain.
[root@ommleft zd]# more root.txt
root:x:0:0:root:/root:/bin/bash
[root@ommleft zd]# sed '/List/{
&gt; r root.txt
&gt; d
&gt; }' notice.std
Would the following people:
root:x:0:0:root:/root:/bin/bash
please report to the ship's captain.

```

可以看到，使用读取文件命令（r root.txt）和删除文本命令（d）将"List"处的文本替换为了root.txt中的内容，此处List起到了占位符的作用。
