# 简介
java.net.URL在JAVA SE中的定位为统一资源定位器（Uniform Resource Locator）,但是它的实现基本仅限于网络形式发布的资源的查找和定位。实际上的资源的定义非常广泛，除了网络形式的资源，还有以二进制形式存在的，以文件形式存在，以字节流形式存在的资源等等。它可以存在于任何场所，比如网络、文件系统、应用程序中。所以java.net.URL的局限性迫使Spring必须实现自己的资源加载策略，该资源加载策略需要满足如下需求：

1.职能划分清楚。资源的定义和资源的加载应该要有一个清晰的界限。

2.统一的抽象。统一的资源定义和资源加载策略。资源加载后要返回统一的抽象给客户端，客户端要对资源进行怎样的处理，应该由抽象资源接口来界定。

# 统一资源：Resource
org.springframework.core.io.Resource为Spring框架所有资源的抽象和访问接口，它继承org.springframework.core.io.InputStreamSource接口。作为所有资源的统一抽象，Source定义了一些通用的方法，由子类AbstractResource提供统一的默认实现。定义如下：
```java
public interface Resource extends InputStreamSource {

    /**
     * 资源是否存在
     */
    boolean exists();

    /**
     * 资源是否可读
     */
    default boolean isReadable() {
        return true;
    }

    /**
     * 资源所代表的句柄是否被一个stream打开了
     */
    default boolean isOpen() {
        return false;
    }

    /**
     * 是否为 File
     */
    default boolean isFile() {
        return false;
    }

    /**
     * 返回资源的URL的句柄
     */
    URL getURL() throws IOException;

    /**
     * 返回资源的URI的句柄
     */
    URI getURI() throws IOException;

    /**
     * 返回资源的File的句柄
     */
    File getFile() throws IOException;

    /**
     * 返回 ReadableByteChannel
     */
    default ReadableByteChannel readableChannel() throws IOException {
        return Channels.newChannel(getInputStream());
    }

    /**
     * 资源内容的长度
     */
    long contentLength() throws IOException;

    /**
     * 资源最后的修改时间
     */
    long lastModified() throws IOException;

    /**
     * 根据资源的相对路径创建新资源
     */
    Resource createRelative(String relativePath) throws IOException;

    /**
     * 资源的文件名
     */
    @Nullable
    String getFilename();

    /**
     * 资源的描述
     */
    String getDescription();

}
```
Resource根据资源的不同类型提供了不同的具体实现，如下：
- FileSystemResource：对java.io.File类型资源的封装，只要是跟File打交道的，基本上与FileSystemResource也可以打交道。支持文件和URL的形式，实现WritableResource接口，且从Spring FrameWork 5.0开始，FileSystemResource使用NIO API进行读/写交互。
- ByteArrayResource：对字节数组提供的数据的封装，如果通过InputStream形式访问该类型的资源，该实现会根据字节数组的数据构造一个相应的ByteArrayInputStream.
- UrlResource:对java.net.URL类型资源的封装。内部委派URL进行具体的资源操作。
- ClassPathResource:class path类型资源的实现。使用给定的ClassLoader或者给定的Class类加载资源。
- InputStreamResource:将给定的InputStream作为一种资源的Resource的实现类。

AbstractResource为Resource接口的默认实现，它实现了Resource接口的大部分的公共实现，作为Resource接口中的重中之重，
