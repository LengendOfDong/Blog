# java注解的自定义和使用
jdk1.5版本内置了三种标准的注解：
- @override,表示当前的方法定义将覆盖超类中的方法。
- @Deprecated，使用了这个注解的元素，编译器将发出警告，因为注解@Deprecated是不赞成使用的代码，被弃用的代码。
- @SuppressWarnings,关闭不当编辑器警告信息。

Java还提供了4种注解，专门负责新注解的创建：
- @Target:
> 表示该注解可以用于什么地方，可能的ElementType参数有：
>> CONSTRUCTOR：构造器的声明

>> FIELD：域声明（包括enum实例）

>> LOCAL_VARIABLE：局部变量声明

>> METHOD：方法声明

>> PACKAGE：包声明

>> PARAMETER：参数声明

>> TYPE：类、接口（包括注解类型）或enum声明



