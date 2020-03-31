# 原创：python ==与is的区别

Python中有很多运算符，今天我们就来讲讲is和==两种运算符在应用上的本质区别是什么。

在讲is和==这两种运算符区别之前，首先要知道Python中对象包含的三个基本要素，分别是：id(身份标识)、type(数据类型)和value(值)。

is和==都是对对象进行比较判断作用的，但对对象比较判断的内容并不相同。下面来看看具体区别在哪。

==比较操作符和is同一性运算符区别

==是python标准操作符中的比较操作符，用来比较判断两个对象的value(值)是否相等，例如下面两个字符串间的比较：<br/>
例1.

> 
<p>a = ‘cheesezh’<br/>
b = ‘cheesezh’<br/>
a == b<br/>
True</p>


is也被叫做同一性运算符，这个运算符比较判断的是对象间的唯一身份标识，也就是id是否相同。通过对下面几个list间的比较，你就会明白is同一性运算符的工作原理：<br/>
例2.

> 
<p>x = y = [4,5,6]<br/>
z = [4,5,6]<br/>
x == y<br/>
True<br/>
x == z<br/>
True<br/>
x is y<br/>
True<br/>
x is z<br/>
False<br/>
print id(x)<br/>
3075326572<br/>
print id(y)<br/>
3075326572<br/>
print id(z)<br/>
3075328140</p>


前三个例子都是True，这什么最后一个是False呢？x、y和z的值是相同的，所以前两个是True没有问题。至于最后一个为什么是False，看看三个对象的id分别是什么就会明白了。

下面再来看一个例子，例3中同一类型下的a和b的（a==b）都是为True，而（a is b）则不然。<br/>
例3.

> 
<p>a = 1 #a和b为数值类型<br/>
b = 1<br/>
a is b<br/>
True<br/>
id(a)<br/>
14318944<br/>
id(b)<br/>
14318944<br/>
a = ‘cheesezh’ #a和b为字符串类型<br/>
b = ‘cheesezh’<br/>
a is b<br/>
True<br/>
id(a)<br/>
42111872<br/>
id(b)<br/>
42111872<br/>
a = (1,2,3) #a和b为元组类型<br/>
b = (1,2,3)<br/>
a is b<br/>
False<br/>
id(a)<br/>
15001280<br/>
id(b)<br/>
14790408<br/>
a = [1,2,3] #a和b为list类型<br/>
b = [1,2,3]<br/>
a is b<br/>
False<br/>
id(a)<br/>
42091624<br/>
id(b)<br/>
42082016<br/>
a = {‘cheese’:1,‘zh’:2} #a和b为dict类型<br/>
b = {‘cheese’:1,‘zh’:2}<br/>
a is b<br/>
False<br/>
id(a)<br/>
42101616<br/>
id(b)<br/>
42098736<br/>
a = set([1,2,3])#a和b为set类型<br/>
b = set([1,2,3])<br/>
a is b<br/>
False<br/>
id(a)<br/>
14819976<br/>
id(b)<br/>
14822256</p>


通过例3可看出，只有数值型和字符串型的情况下，a is b才为True，当a和b是tuple，list，dict或set型时，a is b为False。

这个id就像是指针，如果指向同一个对象，id就相同。如果id不同，但是内容相同，则== 则为true。
