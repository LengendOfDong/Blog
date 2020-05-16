# java注解的自定义和使用
jdk1.5版本内置了三种标准的注解：
- @override,表示当前的方法定义将覆盖超类中的方法。
- @Deprecated，使用了这个注解的元素，编译器将发出警告，因为注解@Deprecated是不赞成使用的代码，被弃用的代码。
- @SuppressWarnings,关闭不当编辑器警告信息。

Java还提供了4种注解，专门负责新注解的创建：
- @Target:
```
 表示该注解可以用于什么地方，可能的ElementType参数有：
 
 CONSTRUCTOR：构造器的声明

 FIELD：域声明（包括enum实例）

 LOCAL_VARIABLE：局部变量声明

 METHOD：方法声明

 PACKAGE：包声明

 PARAMETER：参数声明

 TYPE：类、接口（包括注解类型）或enum声明
 
 ANNOTATION_TYPE:注解类型声明
```
- @Retention:
```java
表示需要在什么级别保存该注解信息。可选的RetentionPolicy参数包括：

SOURCE：注解将被编译器丢弃

CLASS：注解在class文件中可用，但会被VM丢弃

RUNTIME：VM将在运行期间保留注解，因此可以通过反射机制读取注解的信息
```
- @Document:
```java
将注解包含在Javadoc中
```

- @Inherited
```java
允许子类继承父类中的注解
```

# 注解的作用范围：
```java
@Documented
@Retention(value=RUNTIME)
@Target(value=ANNOTATION_TYPE)
public @interface Retention
```
指示注释类型的注释要保留多久。如果注释类型声明中不存在 Retention 注释，则保留策略默认为 RetentionPolicy.CLASS。

只有元注释类型直接用于注释时，Target元注释才有效。如果元注释类型用作另一种注释类型的成员，则无效。

- CLASS
编译器将把注释记录在类文件中，但在运行时 VM 不需要保留注释。
- RUNTIME
编译器将把注释记录在类文件中，在运行时 VM 将保留注释，因此可以反射性地读取。
- SOURCE
编译器要丢弃的注释。
@Retention注解可以在定义注解时为编译程序提供注解的保留策略。





