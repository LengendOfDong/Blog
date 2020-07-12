# 获取Document对象
在XmlBeanDefinitionReader.doLoadDocument()方法中做了两件事情，一是调用getValidationModeForResource()获取XML的验证模式，二是调用DocumentLoader.loadDocument()获取Document对象。获取Document的策略由接口DocumentLoader定义，如下：
```java
public interface DocumentLoader {
    Document loadDocument(
            InputSource inputSource, EntityResolver entityResolver,
            ErrorHandler errorHandler, int validationMode, boolean namespaceAware)
            throws Exception;

}
```
DocumentLoader中只有一个方法loadDocument()，该方法接受五个参数：“inputSource:加载Document的Resource源”  “entitiyResolver：解析文件的解析器”   “errorHandler：处理加载Document对象的过程的错误” “validationMode：验证模式” “namespaceAware:命名空间支持”， 如果要提供对XML名称空间的支持，则为true，该方法用DocumentLoader的默认实现类DefaultDocumentLoader实现，如下：
```java
public Document loadDocument(InputSource inputSource, EntityResolver entityResolver,
            ErrorHandler errorHandler, int validationMode, boolean namespaceAware) throws Exception {

        DocumentBuilderFactory factory = createDocumentBuilderFactory(validationMode, namespaceAware);
        if (logger.isDebugEnabled()) {
            logger.debug("Using JAXP provider [" + factory.getClass().getName() + "]");
        }
        DocumentBuilder builder = createDocumentBuilder(factory, entityResolver, errorHandler);
        return builder.parse(inputSource);
    }
```
首先调用createDocumentBuilderFactory()创建DocumentBuilderFactory，再通过该factory创建DocumentBuilder，最后解析InputSource返回Document对象。

## EntityResolver
loadDocument()获取Document对象时，有一个参数entityResolver，该参数是通过getEntityResolver获取的。

>getEntityResolver返回指定的解析器，如果没有指定，则构造一个未指定的默认解析器

```java
protected EntityResolver getEntityResolver() {
        if (this.entityResolver == null) {
            ResourceLoader resourceLoader = getResourceLoader();
            if (resourceLoader != null) {
                this.entityResolver = new ResourceEntityResolver(resourceLoader);
            }
            else {
                this.entityResolver = new DelegatingEntityResolver(getBeanClassLoader());
            }
        }
        return this.entityResolver;
    }
```
如果ResourceLoader不为null,则根据指定的ResourceLoader创建一个ResourceEntityResolver。如果 ResourceLoader 为null，则创建 一个 DelegatingEntityResolver，该 Resolver 委托给默认的 BeansDtdResolver 和 PluggableSchemaResolver 。

1.ResourceEntityResolver：继承自 EntityResolver ，通过 ResourceLoader 来解析实体的引用。

2.DelegatingEntityResolver：EntityResolver的实现，分别代理了dtd的BeansDtdResolver 和 xml schema的 PluggableSchemaResolver

3.BeansDtdResolver：spring bean dtd解析器，EntityResolver的实现，用来从classpath或者jar文件加载dtd.

4.PluggableSchemaResolver：使用一系列Map文件将Schema url解析到本地classpath资源

getEntityResolver()返回EntityResolver，这个EntityResolver到底是什么呢？

>If a SAX application needs to implement customized handling for external entities, it must implement this interface and register an instance with the SAX driver using the setEntityResolver method.
就是说：如果 SAX 应用程序需要实现自定义处理外部实体，则必须实现此接口并使用 setEntityResolver() 向 SAX 驱动器注册一个实例。 如下：

```java
public class MyResolver implements EntityResolver {
     public InputSource resolveEntity (String publicId, String systemId){
       if (systemId.equals("http://www.myhost.com/today")){
         MyReader reader = new MyReader();
         return new InputSource(reader);
       } else {
            // use the default behaviour
            return null;
       }
     }
   }
```
> EntityResolver的作用就是应用本身可以提供一个如何寻找验证文件的方法，即自定义实现。接口声明如下：
```java
public interface EntityResolver {
    public abstract InputSource resolveEntity (String publicId,String systemId)
        throws SAXException, IOException;
}
```
接口方法接收两个参数 publicId 和 systemId，并返回 InputSource 对象。两个参数声明如下：

1. publicId：被引用的外部实体的公共标识符，如果没有提供，则返回null

2. systemId：被引用的外部实体的系统标识符 这两个参数的实际内容和具体的验证模式有关系。如下

3. XSD 验证模式
        publicId：null
        systemId：http://www.springframework.org/schema/beans/spring-beans.xsd
        
4. DTD 验证模式
        publicId：-//SPRING//DTD BEAN 2.0//EN
        systemId：http://www.springframework.org/dtd/spring-beans.dtd 如下：

我们知道在 Spring 中使用 DelegatingEntityResolver 为 EntityResolver 的实现类，resolveEntity() 实现如下：
```java
 public InputSource resolveEntity(String publicId, @Nullable String systemId) throws SAXException, IOException {
        if (systemId != null) {
            if (systemId.endsWith(DTD_SUFFIX)) {
                return this.dtdResolver.resolveEntity(publicId, systemId);
            }
            else if (systemId.endsWith(XSD_SUFFIX)) {
                return this.schemaResolver.resolveEntity(publicId, systemId);
            }
        }
        return null;
    }
```
不同的验证模式使用不同的解析器解析，如果是DTD验证模式则使用BeansDtdResolver来进行解析，如果是XSD则使用PluggableSchemaResolver来进行解析。BeansDtdResolver的解析过程如下：
```java
public InputSource resolveEntity(String publicId, @Nullable String systemId) throws IOException {
        if (logger.isTraceEnabled()) {
            logger.trace("Trying to resolve XML entity with public ID [" + publicId +
                    "] and system ID [" + systemId + "]");
        }
        if (systemId != null && systemId.endsWith(DTD_EXTENSION)) {
            int lastPathSeparator = systemId.lastIndexOf('/');
            int dtdNameStart = systemId.indexOf(DTD_NAME, lastPathSeparator);
            if (dtdNameStart != -1) {
                String dtdFile = DTD_NAME + DTD_EXTENSION;
                if (logger.isTraceEnabled()) {
                    logger.trace("Trying to locate [" + dtdFile + "] in Spring jar on classpath");
                }
                try {
                    Resource resource = new ClassPathResource(dtdFile, getClass());
                    InputSource source = new InputSource(resource.getInputStream());
                    source.setPublicId(publicId);
                    source.setSystemId(systemId);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Found beans DTD [" + systemId + "] in classpath: " + dtdFile);
                    }
                    return source;
                }
                catch (IOException ex) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Could not resolve beans DTD [" + systemId + "]: not found in classpath", ex);
                    }
                }
            }
        }
or wherever.
        return null;
    }
```
从上面代码中我们可以看到加载DTD类型的BeansDtdResolver.resolveEntity() 只是对 systemId 进行了简单的校验（从最后一个 / 开始，内容中是否包含 spring-beans），然后构造一个 InputSource 并设置 publicId、systemId，然后返回。 PluggableSchemaResolver 的解析过程如下:
```java
public InputSource resolveEntity(String publicId, @Nullable String systemId) throws IOException {
        if (logger.isTraceEnabled()) {
            logger.trace("Trying to resolve XML entity with public id [" + publicId +
                    "] and system id [" + systemId + "]");
        }

        if (systemId != null) {
            String resourceLocation = getSchemaMappings().get(systemId);
            if (resourceLocation != null) {
                Resource resource = new ClassPathResource(resourceLocation, this.classLoader);
                try {
                    InputSource source = new InputSource(resource.getInputStream());
                    source.setPublicId(publicId);
                    source.setSystemId(systemId);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Found XML schema [" + systemId + "] in classpath: " + resourceLocation);
                    }
                    return source;
                }
                catch (FileNotFoundException ex) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Couldn't find XML schema [" + systemId + "]: " + resource, ex);
                    }
                }
            }
        }
        return null;
    }
```
