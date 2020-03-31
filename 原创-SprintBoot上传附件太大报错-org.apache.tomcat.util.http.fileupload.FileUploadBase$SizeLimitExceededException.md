# 原创：SprintBoot上传附件太大报错：org.apache.tomcat.util.http.fileupload.FileUploadBase$SizeLimitExceededException

报错信息如下：

```
org.springframework.web.multipart.MultipartException: Could not parse multipart servlet request; nested exception is java.lang.IllegalStateException: org.apache.tomcat.util.http.fileupload.FileUploadBase$SizeLimitExceededException:the request was rejected because its size (553963927) exceeds the configured maximum (10485760)

```

报错原因：<br/>
本人项目中附件上传默认为10MB，但是现在需要上传500多MB的文件（报错原因中已经体现）<br/>
错误解决：<br/>
启动类原来代码如下：

```
@SpringBootApplication
public class IuirancliApplication {
	public static void main(String[] args) {
		SpringApplication.run(IuirancliApplication.class, args);
	}
}

```

修改后启动类代码如下：

```
@SpringBootApplication
public class IuirancliApplication {
	@Bean
	public MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		//Max size of one file
		factory.setMaxFileSize("1000MB"); //KB,MB
		/// Max Size of All files
		factory.setMaxRequestSize("1000MB");
		return factory.createMultipartConfig();
	}
	public static void main(String[] args) {
		SpringApplication.run(IuirancliApplication.class, args);
	}
}

```

就是在启动类加入了单个文件最大大小和总文件大小设置，加一个方法即可解决问题，亲测有效。
