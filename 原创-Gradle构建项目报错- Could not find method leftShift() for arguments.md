# 原创：Gradle构建项目报错： Could not find method leftShift() for arguments

# build.gradle文件内容

```
task hello &lt;&lt; {
	println "Hello World!"
}

```

执行gradle build,报错Could not find method leftShift() for arguments

# 报错原因

其中 &lt;&lt; 在gradle 在5.1 之后废弃了  ，可以通`gradle -v`来查看版本号

# 解决方法

去掉`&lt;&lt;`即可

```
task hello &lt;&lt; {
	println "Hello World!"
}

```
