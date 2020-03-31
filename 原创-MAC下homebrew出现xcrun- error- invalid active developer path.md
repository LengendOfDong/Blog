# 原创：MAC下homebrew出现xcrun: error: invalid active developer path

# 问题发现

安装mysql发现问题 <br/>
<img alt="这里写图片描述" src="https://img-blog.csdn.net/20180225135901962?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvemhlbmdkb25nMTIzNDU=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70" title=""/>

# 问题分析

执行brew doctor检查原因 <br/>
<img alt="这里写图片描述" src="https://img-blog.csdn.net/2018022514003154?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvemhlbmdkb25nMTIzNDU=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70" title=""/>

按照指示执行git -C命令，发现问题不在于此 <br/>
<img alt="这里写图片描述" src="https://img-blog.csdn.net/20180225140219882?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvemhlbmdkb25nMTIzNDU=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70" title=""/>

接着执行xcode-select –install,安装xcode-select命令行工具(Command Line Tools),重新执行brew doctor: <br/>
<img alt="这里写图片描述" src="https://img-blog.csdn.net/20180225140534100?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvemhlbmdkb25nMTIzNDU=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70" title=""/>

# 问题解决

重新执行brew install mysql ,问题解决： <br/>
<img alt="这里写图片描述" src="https://img-blog.csdn.net/2018022514160671?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvemhlbmdkb25nMTIzNDU=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70" title=""/>

在网上看到很多执行git命令时也有这样的情况，以后也需要注意。
