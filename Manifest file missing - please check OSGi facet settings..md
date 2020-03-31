# Manifest file missing - please check OSGi facet settings.

问题描述：build project发现如下错误<br/>
<img alt="在这里插入图片描述" data-src="https://img-blog.csdnimg.cn/20191113113639101.png" src="https://csdnimg.cn/release/phoenix/write/assets/img_default.png"/><br/>
问题原因：<br/>
<img alt="在这里插入图片描述" data-src="https://img-blog.csdnimg.cn/20191113113749693.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70" src="https://csdnimg.cn/release/phoenix/write/assets/img_default.png"/><br/>
解决办法：修改osgi的配置，修改为通过配置创建<br/>
<img alt="在这里插入图片描述" data-src="https://img-blog.csdnimg.cn/20191113113816205.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70" src="https://csdnimg.cn/release/phoenix/write/assets/img_default.png"/>
