# shell文本处理——正则表达式

## 正则表达式的类型

正则表达式是通过正则表达式引擎来实现的。正则表达式引擎是一套底层软件，负责解释正则表达式模式并使用这些模式进行文本匹配。<br/>
Linux中，有两种流行的正则表达式引擎：

## 纯文本

纯文本比较简单，就不多说了。只要注意几点：<br/>
1.正则表达式模式都区分大小写<br/>
2.正则表达式不用写出完整的单词，匹配到部分单词即可

## 特殊字符

正则表达式识别的特殊字符包括：<br/>
.*[]^${}+?|()<br/>
如果要使用某个特殊字符作为文本字符，就必须转义。

```
➜  Charpter20 git:(master) ✗ sed -n '/\$/p' data2
The cost is $4.00
➜  Charpter20 git:(master) ✗ cat data2
The cost is $4.00

```

## 锚字符

有两个特殊字符可以用来将模式锁定在数据流中的行首或行尾<br/>
1.锁定在行首<br/>
脱字符(^)定义从数据流中文本行的行首开始的模式。如果模式出现在行首之外的位置，正则表达式模式则无法匹配。

```
➜  Charpter20 git:(master) ✗ echo "The book store"|sed -n '/^book/p'
➜  Charpter20 git:(master) ✗ echo "Books are great"|sed -n '/^Book/p'
Books are great

```

如果将脱字符放到模式开头之外的其他位置，那么它就跟普通字符一样，不再是特殊字符了：

```
➜  Charpter20 git:(master) ✗ echo "This is^ a test"|sed -n '/s^/p'
This is^ a test

```

2.锁定在行尾<br/>
特殊字符美元符($)定义了行尾锚点。将这个特殊字符放在文本模式之后来指明数据行必须以该文本模式结尾。

```
➜  Charpter20 git:(master) ✗ echo "This is a good book"|sed -n '/book$/p'
This is a good book
➜  Charpter20 git:(master) ✗ echo "This is a good "|sed -n '/book$/p'

```

3.组合锚点<br/>
在一些情况下，可以在同一行中将行首锚点和行尾锚点组合在一起使用。<br/>
如删除数据流中的空白行，可以如下操作：

```
➜  Charpter20 git:(master) ✗ more data3
This is one test line.

This is another test line.
➜  Charpter20 git:(master) ✗ sed '/^$/d' data3
This is one test line.
This is another test line.

```

可以通过这种方式生成一个没有空行的文件，如下：

```
➜  Charpter20 git:(master) ✗ sed '/^$/d;w data4' data3
This is one test line.
This is another test line.
➜  Charpter20 git:(master) ✗ more data4
This is one test line.
This is another test line.

```

## 点号字符

点号代表任意字符

```
➜  Charpter20 git:(master) ✗ echo "This is a very nice hat"|sed -n '/.at/p'
This is a very nice hat

```

## 字符组

使用方括号来定义一个字符组。方括号包含所有希望出现在该字符组中的字符。<br/>
在不太确定某个字符的大小写时，字符组会很有用

```
➜  Charpter20 git:(master) ✗ echo "Yes"|sed -n '/[Yy]es/p'
Yes
➜  Charpter20 git:(master) ✗ echo "yes"|sed -n '/[Yy]es/p'
yes

```

## 排除型字符组

在正则表达式中，也可以反转字符组的作用，可以寻找组中没有的字符。

```
➜  Charpter20 git:(master) ✗ more data6
This is a test of a line.
The cat is sleeping.
That is a very nice hat.
This test is at line four.
at ten o'clock we'll go home.
➜  Charpter20 git:(master) ✗ sed -n '/[^ch]at/p' data6
This test is at line four.

```

## 区间

对于邮编使用区间的方式来进行过滤不满足规则的邮编，如下所示：

```
➜  Charpter20 git:(master) ✗ sed -n '/^[0-9][0-9][0-9][0-9][0-9]$/p' data8
60633
46201
22203
➜  Charpter20 git:(master) ✗ more data8
60633
46201
223001
4353
22203

```

还可以在单个字符组指定多个不连续的区间

```
➜  Charpter20 git:(master) ✗ sed -n '/[a-ch-m]at/p' data6
The cat is sleeping.
That is a very nice hat.

```

## 特殊的字符组

除了定义自己的字符组外，BRE还包含了一些特殊的字符组，可用来匹配特定类型的字符。下表介绍了可用的BRE特殊的字符组。

<th align="center">选项</th><th align="center">描述</th>
|------
<td align="center">[[:alpha:]]</td><td align="center">匹配任意字母字符，不管是大写还是小写</td>
<td align="center">[[:alnum:]]</td><td align="center">匹配任意字母数字字符0<sub>9、A</sub>Z或a~z</td>
<td align="center">[[:blank:]]</td><td align="center">匹配空格或制表符</td>
<td align="center">[[:digit:]]</td><td align="center">匹配0~9之间的数字</td>
<td align="center">[[:lower:]]</td><td align="center">匹配小写字母字符a~z</td>
<td align="center">[[:print:]]</td><td align="center">匹配任意可打印字符</td>
<td align="center">[[:punct:]]</td><td align="center">匹配标点符号</td>
<td align="center">[[:space:]]</td><td align="center">匹配任意空白字符：空格、制表符、NL、FF、VT和CR</td>
<td align="center">[[:upper:]]</td><td align="center">匹配任意大写字母字符A~Z</td>

```
[root@ommleft zd]# echo "abc"|sed -n '/[[:digit:]]/p'
[root@ommleft zd]# echo "abc"|sed -n '/[[:alnum:]]/p'
abc
[root@ommleft zd]# echo "abc123"|sed -n '/[[:alnum:]]/p'
abc123
[root@ommleft zd]# echo "This is , a test"|sed -n '/[[:punct:]]/p'
This is , a test

```

## 扩展正则表达式

gawk程序可以使用大多数扩展正则表达式模式符号，并且能提供一些额外过滤功能，而这些功能都是sed编辑器所不具备的。但正因为如此，gawk程序在处理数据流时通常比较慢。

## 问号

问号表明前面的字符可以出现0次或1次，但只限于此。

```
[root@ommleft zd]# echo "bt"|gawk '/be?t/{print $0}'
bt
[root@ommleft zd]# echo "bet"|gawk '/be?t/{print $0}'
bet
[root@ommleft zd]# echo "beet"|gawk '/be?t/{print $0}'
[root@ommleft zd]# 

```

## 加号

加号类似于星号的另一个模式符号，但跟问号也有不同。加号表明前面的字符可以出现1次或多次，但必须至少出现1次。

```
[root@ommleft zd]# echo "beeet"|gawk '/be+t/{print $0}'
beeet
[root@ommleft zd]# echo "bt"|gawk '/be+t/{print $0}'
[root@ommleft zd]# 

```

## 花括号

花括号允许为可重复的正则表达式指定一个上限。这通常称为间隔。

```
[root@ommleft zd]# echo "bet"|gawk '/be{1}t/{print $0}'
bet
[root@ommleft zd]# echo "beet"|gawk '/be{1}t/{print $0}'
[root@ommleft zd]#

```

## 管道符号

管道符号允许你在检查数据流时，用逻辑OR方式指定正则表达式引擎要用的两个或多个模式。如果任何一个模式匹配了数据流文本，文本就通过了测试。<br/>
使用管道符号的格式如下：<br/>
expr1|expr2|…

```
[root@ommleft zd]# echo "The cat is asleep"|gawk '/cat|dog/{print $0}'
The cat is asleep
[root@ommleft zd]# echo "The dog is asleep"|gawk '/cat|dog/{print $0}'
The dog is asleep
[root@ommleft zd]# echo "The sleep is asleep"|gawk '/cat|dog/{print $0}'
[root@ommleft zd]# 

```

## 表达式分组

正则表达式可以用圆括号进行分组。

```
[root@ommleft zd]# echo "Sat"|gawk '/Sat(urday)?/{print $0}'
Sat
[root@ommleft zd]# echo "Saturday"|gawk '/Sat(urday)?/{print $0}'
Saturday

```
