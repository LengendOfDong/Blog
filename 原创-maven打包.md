# 原创：maven打包

在SpringBoot工程中,通过jar包的方式启动项目很方便,一个项目可以灵活改变配置启动<br/>
比如:<br/>
`java -jar demo.jar --server.port=9001`<br/>
`java -jar demo.jar --server.port=9002`<br/>
这就要用到maven来打包,过程分为两步<br/>
第一步:在pom中,加入插件

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

第二步:在控制台输入`mvn clean package`,打包成功<br/>
<img alt="mvn clean package" src="https://img-blog.csdnimg.cn/20190327005428520.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/><br/>
注:执行此命令要在有pom.xml的文件夹下,否则就会报以下错误,找不到pom.xml<br/>
通常在一个项目的根目录下就会有pom.xml<br/>
<img alt="no Pom" src="https://img-blog.csdnimg.cn/20190327005912519.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/>
