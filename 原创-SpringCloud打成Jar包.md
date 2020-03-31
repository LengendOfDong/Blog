# 原创：SpringCloud打成Jar包

起初我都是通过在application.properties中配置参数来启动项目,但是这样有个弊端,就是同一个项目启动多次,只是端口或者环境变量不同的时候,就需要多个项目来完成<br/>
解决这个弊端的一个好办法就是用jar包启动,通过指令来修改配置<br/>
1.修改配置<br/>
原来的配置是这样:

```
&lt;build&gt;
		&lt;plugins&gt;
			&lt;plugin&gt;
				&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
				&lt;artifactId&gt;spring-boot-maven-plugin&lt;/artifactId&gt;
			&lt;/plugin&gt;
		&lt;/plugins&gt;
&lt;/build&gt;

```

现在的配置是这样:

```
&lt;build&gt;
		&lt;plugins&gt;
			&lt;plugin&gt;
				&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
				&lt;artifactId&gt;spring-boot-maven-plugin&lt;/artifactId&gt;
				&lt;version&gt;2.0.3.RELEASE&lt;/version&gt;
				&lt;executions&gt;
					&lt;execution&gt;
						&lt;goals&gt;
							&lt;goal&gt;repackage&lt;/goal&gt;
						&lt;/goals&gt;
					&lt;/execution&gt;
				&lt;/executions&gt;
			&lt;/plugin&gt;
		&lt;/plugins&gt;
	&lt;/build&gt;

```

对比一下,只是增加了version和executions<br/>
2.启动命令<br/>
在控制台中输入`mvn clean package`,打包成功<br/>
<img alt="springcloud  jar" src="https://img-blog.csdnimg.cn/20190317143510938.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/><br/>
3.运行jar包<br/>
这个时候就可以随意修改运行时的参数了<br/>
`java -jar xxx.jar --server.port=1111`<br/>
`java -jar xxx.jar --server.port=2222`
