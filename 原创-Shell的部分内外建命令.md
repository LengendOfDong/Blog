# 原创：Shell的部分内外建命令

## 内建命令

```
cd  is  a  shell  builtin

```

cd命令是内建命令，可以通过type命令来查看

```
echo   is  a   shell  builtin
echo  is  /bin/echo

```

echo既有内建命令也有外部命令，可以通过type  -a 方式查看

## 外部命令

外部命令可以通过which和type命令来找到

```
/bin/ps

```

ps就是一个外部命令<br/>
对于多种实现的命令，既有内建命令又有外部命令的命令，如pwd<br/>
若要使用外部命令pwd,则可以输入/bin/pwd

## 命令别名
