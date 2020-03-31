# 原创：git add 撤销

git add 添加 多余文件<br/>
这样的错误是由于， 有的时候 可能

git add . （空格+ 点） 表示当前目录所有文件，不小心就会提交其他文件

git add 如果添加了错误的文件的话

撤销操作

git status 先看一下add 中的文件<br/>
git reset HEAD 如果后面什么都不跟的话 就是上一次add 里面的全部撤销了<br/>
git reset HEAD XXX/XXX/XXX.java 就是对某个文件进行撤销了
