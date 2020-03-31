# 原创：Python  from  找不到module

from  car  import  ElectricCar 报错,如下图：<br/>
<img alt="在这里插入图片描述" src="https://img-blog.csdnimg.cn/20190809000905900.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/><br/>
找不到car,是因为Pycharm的路径是到项目的根目录，如下图<br/>
<img alt="在这里插入图片描述" src="https://img-blog.csdnimg.cn/20190809001536999.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/><br/>
此时有两种方法：<br/>
1.方法一：设置charpter9为Sources<br/>
<img alt="在这里插入图片描述" src="https://img-blog.csdnimg.cn/20190809001815205.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/><br/>
2.方法二：写全路径，即from  charpter9.car  import  ElectricCar<br/>
<img alt="在这里插入图片描述" src="https://img-blog.csdnimg.cn/2019080900194952.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3poZW5nZG9uZzEyMzQ1,size_16,color_FFFFFF,t_70"/>
