# java使 FileWriter FileReader 无编码格式，OutputStreamWriter InputStreamReader可设置编码格式

原文链接：https://blog.csdn.net/qq_27093465/article/details/72730796

使用Java中的 FileWriter FileReader 可以传个文件路径，然后就可以简单的实现，文件的读和写。<br/>
但是这个实现是太简单了，会有问题的。

简单的代码操作，如我的这篇博文里面的转存文件的代码就是使用这2个类来实现的。

java修改文件名-renameTo()方法的使用实例，复制一个文件或者叫转存一个文件

上面的转存代码，经过findbugs分析，有如下的提示：

具体如下：<br/>
Found reliance on default encoding: new java.io.FileWriter(String)<br/>
Reliance on default encoding<br/>
Found a call to a method which will perform a byte to String (or String to byte) conversion, and will assume that the default platform encoding is suitable. This will cause the application behaviour to vary between platforms. Use an alternative API and specify a charset name or Charset object explicitly.

然后，翻译一下就是下图：

FileWriter FileReader 是不带编码格式的，默认使用本机器的默认编码，那么就会因为编码问题，而bug的。

怎么让 FileWriter FileReader 他们带上编码格式呢？

还是拿转存文件的代码来修改。具体修改如下。

```
private static boolean copyFile(String src, String des) {
    InputStreamReader fr = null;
    OutputStreamWriter fw = null;
    try {
        fr = new InputStreamReader(new FileInputStream(src),"UTF-8");//读
        fw = new OutputStreamWriter(new FileOutputStream(des), "UTF-8");//写
        char[] buf = new char[1024];//缓冲区
        int len;
        while ((len = fr.read(buf)) != -1) {
            fw.write(buf, 0, len);//读几个写几个
        }
        return true;
    } catch (IOException e) {
        LOG.error(e.getMessage());
        return false;
    } finally {
        if (fr != null) {
            try {
                fr.close();
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }
        if (fw != null) {
            try {
                fw.flush();
                fw.close();
            } catch (IOException e) {
                LOG.error(e.getMessage());
            }
        }
    }
}

```

重点就是把<br/>
FileWriter FileReader   换成了  OutputStreamWriter InputStreamReader

new InputStreamReader(new FileInputStream(src),“UTF-8”);//读<br/>
new OutputStreamWriter(new FileOutputStream(des), “UTF-8”);//写

这就带上了编码格式了。
