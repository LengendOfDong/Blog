# Ride启动报错：KeyError: 'APPDATA'

## 报错信息：

Traceback (most recent call last):<br/>
File “D:\Install\Python27\lib\site-packages\robotide_**init**_.py”, line 83, in main<br/>
<em>run(inpath, not noupdatecheck, debug_console)<br/>
File "D:\Install\Python27\lib\site-packages\robotide_**init**</em>.py", line 102, in <em>run<br/>
from robotide.application import RIDE<br/>
File "D:\Install\Python27\lib\site-packages\robotide\application_**init**</em>.py", line 16, in <br/>
from .application import RIDE, Project<br/>
File “D:\Install\Python27\lib\site-packages\robotide\application\application.py”, line 23, in <br/>
from robotide.controller import Project<br/>
File “D:\Install\Python27\lib\site-packages\robotide\controller_**init**_.py”, line 16, in <br/>
from .project import Project<br/>
File “D:\Install\Python27\lib\site-packages\robotide\controller\project.py”, line 20, in <br/>
from robotide.context import LOG<br/>
File “D:\Install\Python27\lib\site-packages\robotide\context_**init**_.py”, line 35, in <br/>
os.environ[‘APPDATA’], ‘RobotFramework’, ‘ride’)<br/>
File “D:\Install\Python27\lib\os.py”, line 425, in **getitem**<br/>
return self.data[key.upper()]<br/>
KeyError: ‘APPDATA’

Use --help to get usage information.

## 问题分析：

查看“D:\Install\Python27\lib\site-packages\robotide\context_**init**_.py”文件<br/>
if IS_WINDOWS:<br/>
SETTINGS_DIRECTORY = os.path.join(<br/>
os.environ[‘APPDATA’], ‘RobotFramework’, ‘ride’)<br/>
else:<br/>
SETTINGS_DIRECTORY = os.path.join(<br/>
os.path.expanduser(’~/.robotframework’), ‘ride’)

缺少os.environ[‘APPDATA’]

## 问题处理：

卸载python27,然后重新安装python27,重启之后解决此问题。<br/>
估计是安装了nodejs的原因导致了环境变量的修改
