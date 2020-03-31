# 原创：Maven Package跳过测试的两种方法及区别

最近在工作中需要打包项目，直接使用了Maven Package的命令，但是在编译测试的时候耗时很长，这个时候就想着有没有能跳过测试的方法。

-DskipTests，不执行测试用例，但编译测试用例类生成相应的class文件至target/test-classes下。

-Dmaven.test.skip=true，不执行测试用例，也不编译测试用例类。

第一种方法：  使用 mvn package -DskipTests 跳过单元测试，但是会继续编译。如果没时间修改单元测试的bug，或者单元测试编译错误，则使用第二种方法。

```
&lt;plugin&gt;  
    &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;  
    &lt;artifactId&gt;maven-surefire-plugin&lt;/artifactId&gt;  
    &lt;version&gt;2.5&lt;/version&gt;  
    &lt;configuration&gt;  
        &lt;skipTests&gt;true&lt;/skipTests&gt;  
    &lt;/configuration&gt;  
&lt;/plugin&gt;

```

第二种方法：使用maven.test.skip，不但跳过单元测试的运行，也跳过测试代码的编译。

```
mvn package -Dmaven.test.skip=true 

```

也可以在pom.xml中加入：

```
&lt;plugin&gt;  
    &lt;groupId&gt;org.apache.maven.plugin&lt;/groupId&gt;  
    &lt;artifactId&gt;maven-compiler-plugin&lt;/artifactId&gt;  
    &lt;version&gt;2.1&lt;/version&gt;  
    &lt;configuration&gt;  
        &lt;skip&gt;true&lt;/skip&gt;  
    &lt;/configuration&gt;  
&lt;/plugin&gt;  
&lt;plugin&gt;  
    &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;  
    &lt;artifactId&gt;maven-surefire-plugin&lt;/artifactId&gt;  
    &lt;version&gt;2.5&lt;/version&gt;  
    &lt;configuration&gt;  
        &lt;skip&gt;true&lt;/skip&gt;  
    &lt;/configuration&gt;  
&lt;/plugin&gt;

```

默认idea的跳过测试采用的是第一种，所以仍然会编译代码，如果想不执行测试用例，也不编译测试用例类,那么必须采用第二种方式在pom中加上skip为true，这样才能跳过编译
