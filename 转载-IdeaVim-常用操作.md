# 转载：IdeaVim-常用操作

作者:[六月的余晖](http://www.cnblogs.com/zhaozihan/) <br/>
出处:[http://www.cnblogs.com/zhaozihan/](http://www.cnblogs.com/zhaozihan/) <br/>
有删减，大部分摘自以上文章，亲测有效

# IdeaVim简介

IdeaVim是IntelliJ IDEA的一款插件，他提高了我们写代码的速度，对代码的跳转，查找也很友好。

#### 具体操作

i模式即为编辑模式，按下字母i开启就可以打字。

从i模式切换为Vim，按下键盘的Esc键切回Vim。

上：k , 下：j , 左： h , 右：l

例如：“hello world,I’m wrting”,当光标在h时，连按w光标依次显示为：w &gt; , &gt; I &gt; ’ &gt; m &gt; w &gt; “

例如：“hello world,I’m wrting”,当光标在h时，连按大写的W光标依次显示为：w &gt; I &gt; w

情况与w类似。

例如：“hello world,I’m wrting”,当光标在h时，输入fw，光标跳转到字符world的w字符处。

例如：“hello world,I’m wrting”,当光标在d时，输入Fw，光标跳转到字符world的w字符处。

例如：“hello world,I’m wrting”,当光标在h时，输入td，光标跳转到字符world的l字符处。

例如：“hello world,I’m wrting”,当光标在d时，输入Tw，光标跳转到字符world的o字符处。

粘贴至光标的下一行，例如：“hello world,I’m wrting”,当光标在此行时，输入yyp，当前行复制并粘贴，下面又多出一行。

粘贴10次

例如：当光标在第一行hello的h字符处，输入vjj <br/>
<img alt="这里写图片描述" src="https://images2015.cnblogs.com/blog/917807/201701/917807-20170118171916234-1112423646.png" title=""/>

删除当前行在内以下的5行。

从光标所在位置处逐个正向删除

例如：“hello world,I’m wrting”,当光标在h时，输入dfw，结果为“orld,I’m wrting”。

在ideaVim中取消Vim模式可以使用tools-&gt;Vim Emulator
