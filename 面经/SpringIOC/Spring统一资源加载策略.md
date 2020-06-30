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

AbstractResource为Resource接口的默认实现，它实现了Resource接口的大部分的公共实现，作为Resource接口中的重中之重，定义如下：
```java
public abstract class AbstractResource implements Resource {

    /**
     * 判断文件是否存在，若判断过程产生异常（因为会调用SecurityManager来判断），就关闭对应的流
     */
    @Override
    public boolean exists() {
        try {
            return getFile().exists();
        }
        catch (IOException ex) {
            // Fall back to stream existence: can we open the stream?
            try {
                InputStream is = getInputStream();
                is.close();
                return true;
            }
            catch (Throwable isEx) {
                return false;
            }
        }
    }

    /**
     * 直接返回true，表示可读
     */
    @Override
    public boolean isReadable() {
        return true;
    }

    /**
     * 直接返回 false，表示未被打开
     */
    @Override
    public boolean isOpen() {
        return false;
    }

    /**
     *  直接返回false，表示不为 File
     */
    @Override
    public boolean isFile() {
        return false;
    }

    /**
     * 抛出 FileNotFoundException 异常，交给子类实现
     */
    @Override
    public URL getURL() throws IOException {
        throw new FileNotFoundException(getDescription() + " cannot be resolved to URL");
    }

    /**
     * 基于 getURL() 返回的 URL 构建 URI
     */
    @Override
    public URI getURI() throws IOException {
        URL url = getURL();
        try {
            return ResourceUtils.toURI(url);
        }
        catch (URISyntaxException ex) {
            throw new NestedIOException("Invalid URI [" + url + "]", ex);
        }
    }

    /**
     * 抛出 FileNotFoundException 异常，交给子类实现
     */
    @Override
    public File getFile() throws IOException {
        throw new FileNotFoundException(getDescription() + " cannot be resolved to absolute file path");
    }

    /**
     * 根据 getInputStream() 的返回结果构建 ReadableByteChannel
     */
    @Override
    public ReadableByteChannel readableChannel() throws IOException {
        return Channels.newChannel(getInputStream());
    }

    /**
     * 获取资源的长度
     *
     * 这个资源内容长度实际就是资源的字节长度，通过全部读取一遍来判断
     */
    @Override
    public long contentLength() throws IOException {
        InputStream is = getInputStream();
        try {
            long size = 0;
            byte[] buf = new byte[255];
            int read;
            while ((read = is.read(buf)) != -1) {
                size += read;
            }
            return size;
        }
        finally {
            try {
                is.close();
            }
            catch (IOException ex) {
            }
        }
    }

    /**
     * 返回资源最后的修改时间
     */
    @Override
    public long lastModified() throws IOException {
        long lastModified = getFileForLastModifiedCheck().lastModified();
        if (lastModified == 0L) {
            throw new FileNotFoundException(getDescription() +
                    " cannot be resolved in the file system for resolving its last-modified timestamp");
        }
        return lastModified;
    }


    protected File getFileForLastModifiedCheck() throws IOException {
        return getFile();
    }

    /**
     * 交给子类实现
     */
    @Override
    public Resource createRelative(String relativePath) throws IOException {
        throw new FileNotFoundException("Cannot create a relative resource for " + getDescription());
    }

    /**
     * 获取资源名称，默认返回 null
     */
    @Override
    @Nullable
    public String getFilename() {
        return null;
    }


    /**
     * 返回资源的描述
     */
    @Override
    public String toString() {
        return getDescription();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj == this ||
            (obj instanceof Resource && ((Resource) obj).getDescription().equals(getDescription())));
    }

    @Override
    public int hashCode() {
        return getDescription().hashCode();
    }

}
```
如果要实现自定义的Resource，应该继承AbstractResource抽象类，然后根据当前的具体资源特性覆盖相应的方法即可。

## 统一资源定位：ResourceLoader
Spring将资源的定义和资源的加载区分开了，Resource定义了统一的资源，资源的加载由ResourceLoader来统一定义。

org.springframework.core.io.ResourceLoader为Spring资源加载的统一抽象，具体的资源则由相应的实现类来完成，所以可以将ResourceLoader称作统一资源定位器。其定义如下：
```java
public interface ResourceLoader {
    String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;

    Resource getResource(String location);

    ClassLoader getClassLoader();
}
```
ResourceLoader接口提供两个方法：getResource()/getClassLoader().

getResource（）根据所提供资源的路径location返回Resource实例，但是它不确保该Resource一定存在，需要调用Resource.exist()方法判断。该方法支持以下模式的资源加载：
- URL位置资源，如“file:C:/test.dat”
- ClassPath位置资源，如“classpath:test.dat”
- 相对路径资源，如“WEB-INF/test.dat”，此时返回的Resource实例根据实现不同而不同。

该方法的主要实现是在其子类DefaultResourceLoader中实现。

getClassLoader(）返回ClassLoader实例，对于想要获取ResourceLoader使用的ClassLoader用户来说，可以直接调用该方法来获取，在分析Resource时，提到了一个类ClassPathResource，这个类是可以根据指定的ClassLoader来加载资源的。

作为Spring统一的资源加载器，它提供了统一的抽象，具体的实现由相应的子类来负责实现。

## DefaultResourceLoader
DefaultResourceLoader 是 ResourceLoader 的默认实现，它接收 ClassLoader 作为构造函数的参数或者使用不带参数的构造函数，在使用不带参数的构造函数时，使用的 ClassLoader 为默认的 ClassLoader（一般为Thread.currentThread().getContextClassLoader()），可以通过 ClassUtils.getDefaultClassLoader()获取。当然也可以调用 setClassLoader()方法进行后续设置。

```java
public DefaultResourceLoader() {
        this.classLoader = ClassUtils.getDefaultClassLoader();
    }

    public DefaultResourceLoader(@Nullable ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public void setClassLoader(@Nullable ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    @Nullable
    public ClassLoader getClassLoader() {
        return (this.classLoader != null ? this.classLoader : ClassUtils.getDefaultClassLoader());
    }
```
ResourceLoader中最核心的方法为getResource()，它根据提供的location返回相应的Resource,而DefaultResourceLoader对该方法提供了核心实现（它的两个子类都没有提供覆盖该方法，可以断定ResourceLoader的资源加载策略就封装在DefaultResourceLoader中）
```java
public Resource getResource(String location) {
        Assert.notNull(location, "Location must not be null");

        for (ProtocolResolver protocolResolver : this.protocolResolvers) {
            Resource resource = protocolResolver.resolve(location, this);
            if (resource != null) {
                return resource;
            }
        }

        if (location.startsWith("/")) {
            return getResourceByPath(location);
        }
        else if (location.startsWith(CLASSPATH_URL_PREFIX)) {
            return new ClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()), getClassLoader());
        }
        else {
            try {
                // Try to parse the location as a URL...
                URL url = new URL(location);
                return (ResourceUtils.isFileURL(url) ? new FileUrlResource(url) : new UrlResource(url));
            }
            catch (MalformedURLException ex) {
                // No URL -> resolve as resource path.
                return getResourceByPath(location);
            }
        }
    }
```
首先通过 ProtocolResolver 来加载资源，成功返回 Resource，否则调用如下逻辑：

1. 若 location 以 / 开头，则调用 getResourceByPath()构造 ClassPathContextResource 类型资源并返回。
2. 若 location 以 classpath: 开头，则构造 ClassPathResource 类型资源并返回，在构造该资源时，通过 getClassLoader()获取当前的 ClassLoader。
3. 构造 URL ，尝试通过它进行资源定位，若没有抛出 MalformedURLException 异常，则判断是否为 FileURL , 如果是则构造 FileUrlResource 类型资源，否则构造 UrlResource。若在加载过程中抛出 MalformedURLException 异常，则委派 getResourceByPath() 实现资源定位加载。

ProtocolResolver ，用户自定义协议资源解决策略，作为 DefaultResourceLoader 的 SPI，它允许用户自定义资源加载协议，而不需要继承 ResourceLoader 的子类。在介绍 Resource 时，提到如果要实现自定义 Resource，我们只需要继承 DefaultResource 即可，但是有了 ProtocolResolver 后，我们不需要直接继承 DefaultResourceLoader，改为实现 ProtocolResolver 接口也可以实现自定义的 ResourceLoader。 ProtocolResolver 接口，仅有一个方法 Resource resolve(String location, ResourceLoader resourceLoader)，该方法接收两个参数：资源路径location，指定的加载器 ResourceLoader，返回为相应的 Resource 。在 Spring 中你会发现该接口并没有实现类，它需要用户自定义，自定义的 Resolver 如何加入 Spring 体系呢？调用 DefaultResourceLoader.addProtocolResolver() 即可
```java
public void addProtocolResolver(ProtocolResolver resolver) {
        Assert.notNull(resolver, "ProtocolResolver must not be null");
        this.protocolResolvers.add(resolver);
    }
```

## FileSystemResourceLoader
