# 转载：OSGI MANIFEST.MF

/META-INF/MANIFEST.MF文件中定义了Bundle的元数据信息。

```
Bundle-SymbolicName: 设置Bundle在OSGI容器中的全局唯一标示符。	
	说明：
		1)这个标记是Bundle元数据信息中唯一一个必须设置的标记。
		2)程序根据Bundle-SymbolicName和Bundle-Version在OSGI容器中找到一个独一无二的Bundle。
		3)OSGI容器中不允许有个两个Bundle-SymbolicName和Bundle-Version都相同的Bundle存在。
	参数：
		singleton：表示Bundle是单例的，默认为false。
		fragment-attachment：设置Fragment Bundle是否可以附加到该Bundle中，值可以为：always、never、resolve-time(解析过程中)，默认为always。
	举例：
		Bundle-SymbolicName: com.jxn.osgi.example.impl;singleton:=true
		
Bundle-Version: 设置Bundle的版本号。
	默认：0.0.0
	举例：Bundle-Version: 1.0.0
	
Fragment-Host: 如果该Bundle是一个Fragment Bundle，则该标记表示它的宿主Bundle。
	取值：Bundle-SymbolicName的值。
	参数：Bundle-Version的值。
	
Bundle-ActivationPolicy: 设置Bundle的加载策略。
	默认：Bundle在服务器启动的时候就被激活。
	取值：
		lazy：Bundle将在其它Bundle请求加载该Bundle中的资源时才会被激活。
	
Bundle-Activator: 设置Bundle的启动器类(Activator)。
	说明：
		1)该启动器类必须实现org.osgi.framework.BundleActivator接口。
		2)在bundle启动和停止时会分别调用该类的start()方法和stop()方法。
		3)启动器类(Activator)常用于Bundle启动时注册和初始化服务。
	举例：Bundle-Activator: com.jxn.osgi.example.impl.Activator
		
Bundle-ClassPath: 设置Bundle的类路径。
	说明：该路径应为Bundle内部的合法路径，如果有多个ClassPath，则使用逗号分隔。
	举例：Bundle-ClassPath: ., bin/
		
Bundle-ManifestVersion: 设置Bundle遵循OSGI规范的版本。
	取值：
		1：遵循OSGI R3规范
		2：遵循OSGI R4/R5规范
	举例：Bundle-ManifestVersion: 2
		
Bundle-RequiredExecutionEnvironment: 设置Bundle所需的执行环境。
	说明：如果支持多种执行环境，则使用逗号分隔。
	举例：Bundle-RequiredExecutionEnvironment: J2SE-1.5
		
Bundle-Vendor: 设置Bundle的发行者信息。
	举例：Bundle-Vendor: JXN
		
Bundle-NativeCode: 如果Bundle中需要使用JNI加载其它语言实现的本地代码，则必须使用该标记进行说明。
	参数：
		osname：操作系统名称。
		osversion：操作系统版本。
		processor：处理器。
		language：语言。
		
	举例：Bundle-NativeCode: /lib/http.DLL; osname=QNX; osversion=3.1
	
Import-Package: 设置Bundle中需要导入的包。	
	说明：如果需要导入的包不存在，则会导致Bundle解析失败。
	举例：
		Import-Package: com.jxn.osgi.example.data.model,
						com.jxn.osgi.example.data.service,
						com.jxn.osgi.example.data.types,
						com.jxn.osgi.example.service.context,
						com.jxn.osgi.example.service.model,
						com.jxn.osgi.example.service.role,
						org.osgi.framework,
						org.springframework.osgi.extensions.annotation,
						org.springframework.transaction,
						org.springframework.transaction.annotation

Export-Package: 设置Bundle中可以被导出的包。
	说明：可以根据类名、版本等过滤
	参数：
		include：包中需要导出的资源。
		exclude：包中禁止导出的资源。
		uses：说明导出包的依赖关系
	举例：
		Export-Package: com.jxn.osgi.example.service.context, 
						com.jxn.osgi.example.service.role;include="IO*";exclude="*QueryRole"

DynamicImport-Package: 设置运行时动态导入的包。
	说明：
		1)如果动态导入的包不存在，那么也不会影响Bundle的正常解析
		2)只有真正使用到需要动态导入的包中的类时，如果该包中的类不存在，此时才会抛出ClassNotFoundException异常。

Require-Bundle: 设置Bundle所依赖的其它Bundle。
	说明：相当于把依赖的Bundle中所有声明为导出的包都导入了。
	举例：
		Require-Bundle: com.jxn.osgi.example.service, com.jxn.osgi.example.data
	
注意：
	1)Bundle-Name、Bundle-Category、Bundle-ContactAddress、Bundle-Copyright、Bundle-Description、Bundle-DocRUL等标记只供人工阅读，OSGI框架并不会使用它们

```
