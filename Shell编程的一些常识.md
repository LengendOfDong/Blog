# Shell编程的一些常识

虽然写过些shell脚本了，但是对于很多知识还是记忆不深刻。在看书的过程中，正好看到了相关常识，在此记录下以备后用。

## 执行数学运算

1.expr命令

```
➜  /etc expr 1 + 5
6
➜  /etc expr 1 * 5
expr: syntax error
➜  /etc expr 1 \* 5
5

```

注：1）数字与符号之间要有空格<br/>
2）个别符号需要转义，如上例中的星号（*）

2.使用方括号<br/>
在bash中，在将一个数学运算结果赋给某个变量时，可以用美元符合方括号（$[ operation ]）将数学表达式围起来。

```
➜  /etc var1=$[1 + 5]
➜  /etc echo $var1
6
➜  /etc var2=$[$var1 * 2]
➜  /etc echo $var2
12

```

在使用方括号来计算公式时，不用担心shell会误解乘号或者其他符号，这是一大优点。

## 浮点数运算

有时候会遇到浮点数算术操作，最常见的方案是用内建的bash计算器，叫做bc.

```
➜  /etc bc -q
3.44 / 5
0
scale=4
3.44 / 5
.6880
quit

```

上例中，-q是不显示bash计算器的欢迎信息。scale变量用来设置计算结果保留的小数位数。<br/>
在shell中的运用方式：

```
variable=$(echo "options; expression"|bc)

```

第一部分options允许设置变量，如果不止一个变量，可以使用分号将其分开。<br/>
expression参数定义了通过bc执行的数学表达式。例子如下：

```
➜  /etc var1=$(echo "scale=4;3.44 / 5"|bc)
➜  /etc echo $var1
.6880

```

## 数值比较

数值比较可以说是shell编程中最为常见的了，直接影响着流程的走向，因此对于数值比较知识点的掌握也极其重要了。

<th align="center">比较</th><th align="center">描述</th>
|------
<td align="center">n1 -eq n2</td><td align="center">检查n1是否与n2相等</td>
<td align="center">n1 -ge n2</td><td align="center">检查n1是否大于或等于n2</td>
<td align="center">n1 -gt n2</td><td align="center">检查n1是否大于n2</td>
<td align="center">n1 -le n2</td><td align="center">检查n1是否小于或者等于n2</td>
<td align="center">n1 -lt n2</td><td align="center">检查n1是否小于n2</td>
<td align="center">n1 -ne n2</td><td align="center">检查n1是否不等于n2</td>

## 字符串比较

条件还允许比较字符串值。

<th align="center">比较</th><th align="center">描述</th>
|------
<td align="center">str1 = str2</td><td align="center">检查str1是否和str2相同</td>
<td align="center">str1 != str2</td><td align="center">检查str1是否和str2不同</td>
<td align="center">str1 &lt; str2</td><td align="center">检查str1是否比str2小</td>
<td align="center">str1 &gt; str2</td><td align="center">检查str1是否比str2大</td>
<td align="center">-n str1</td><td align="center">检查str1的长度是否非0</td>
<td align="center">-z str1</td><td align="center">检查str1的长度是否为0</td>

## 文件比较

文件比较在shell编程中也是用的最多的比较形式。用于测试Linux文件系统上文件和目录的状态。

<th align="center">比较</th><th align="center">描述</th>
|------
<td align="center">-d file</td><td align="center">检查file是否存在并是一个目录</td>
<td align="center">-e file</td><td align="center">检查file是否存在</td>
<td align="center">-f file</td><td align="center">检查file是否存在并是一个文件</td>
<td align="center">-r file</td><td align="center">检查file是否存在并可读</td>
<td align="center">-s file</td><td align="center">检查file是否存在并非空</td>
<td align="center">-w file</td><td align="center">检查file是否存在并可写</td>
<td align="center">-x file</td><td align="center">检查file是否存在并可执行</td>
<td align="center">-O file</td><td align="center">检查file是否存在并属于当前用户所有</td>
<td align="center">-G file</td><td align="center">检查file是否存在并且默认组与当前用户相同</td>
<td align="center">file1 -nt file2</td><td align="center">检查file1是否比file2新</td>
<td align="center">file1 -ot file2</td><td align="center">检查file1是否比file2旧</td>

## 复合条件测试

if-then语句允许使用布尔逻辑来组合测试，两种布尔运算符可用：

## if-then的高级特性

bash-shell提供了两项可在if-then语句中使用的高级特性：

1.使用双括号<br/>
双括号的命令格式如下：<br/>
((  expression ))<br/>
expression可以是任意的数学赋值或比较表达式。<br/>
2.使用双方括号<br/>
双方括号的命令格式如下：<br/>
[[ expression ]]<br/>
双方括号提供了模式匹配的功能。<br/>
如： [[ $USR == r* ]]可用来判断用户是否是以字母r开头

## CASE命令

通过case命令不用写出冗长的if-then-else语句，而可以通过列表的方式来检查变量的多个值。<br/>
格式如下：

```
case variable in
pattern1 | pattern2) commands1;;
pattern3) commands2;;
*) commands;;
esac

```

case命令会将指定的变量与不同模式进行比较。如果变量和模式是匹配的，那么SHELL会执行为该模式指定的命令。可以通过竖线操作符在一行中分隔多个模式。星号(*)会捕获所有与已知模式不匹配的值。仔细一看，这个和JAVA的case非常相似。
