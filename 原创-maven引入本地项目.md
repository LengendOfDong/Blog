# 原创：maven引入本地项目

在项目中有时会用到本地的另一个项目,这个时候需要通过Maven引入本地项目<br/>
假设A项目需要调用B项目中的类,将B项目引入到A项目中的pom中<br/>
具体操作如下:<br/>
第一步:设置B项目的pom<br/>
<img alt="B pom" src="https://img-blog.csdnimg.cn/20190327010755948.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/><br/>
第二步:在项目根目录下执行`mvn install`,将项目引入到本地maven库中,如下图,可以在本地maven中看到项目路径,说明项目已经加到本地maven库中<br/>
<img alt="本地maven" src="https://img-blog.csdnimg.cn/20190327011021490.png"/><br/>
注意在执行`mvn install`时,查看B项目pom中是否有编译插件,有可能会影响`mvn install`的执行,导致引入后调用不了类(编译有问题)<br/>
第三步:配置A项目的pom,通过Maven这个桥梁将B项目引入<br/>
<img alt="A pom" src="https://img-blog.csdnimg.cn/20190327011229272.png"/><br/>
接下来就可以在A项目中调用B项目的类了<br/>
<img alt="调用B的类" src="https://img-blog.csdnimg.cn/20190327011601354.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/>
