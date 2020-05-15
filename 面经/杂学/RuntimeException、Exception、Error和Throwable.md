# RuntimeException、Exception、Error和Throwable
Throwable类是Java语言中所有错误或异常的超类，它的两个子类是Error和Exception

Error是Throwable的子类，用于指示合理的应用程序不应该试图捕获的严重问题。大多数这样的错误时异常条件。虽然ThreadDeath错误是一个”正规“条件，但它也是Error 的子类，因为大多数应用程序都不应该试图捕获它。在执行该方法期间，无需在其 throws 子句中声明可能抛出但是未能捕获的 Error 的任何子类，因为这些错误可能是再也不会发生的异常条件

Exception类及其子类是Throwable的一种形式，它指出了合理的应用程序想要捕获的条件。

RuntimeException是那些可能在Java虚拟机正常运行期间抛出的异常的超类，可能在执行方法期间但未被捕获的RuntimeException的任何子类都无需在throws子句中进行声明。它是Exception的子类。

方法重写时：在子类中一个重写的方法只能抛出父类中声明过的异常或者异常的子类。

以下代码有两个知识点：0为除数是否会抛出异常  以及 异常捕获
```java
public class ExceptionTest {
    public static void main(String[] args){
        try{
            double a = 0.0;
            double c = 0;
            double b = 0 / a;
            double d = 0 / c;
            //输出NaN
            System.out.println(b);
            //输出NaN
            System.out.println(d);
            int e = 0;
            double f = 0.0 / e;
            //输出为NaN
            System.out.println(f);
//            String s = null;
//            String t = s.concat(":123");
//            System.out.println(t);
//            throw new Exception("this is testing error");
        }catch (ArithmeticException e){
            System.out.println("math exception");
        }
        System.out.println("12344");
    }
}
```
在上面代码中，有两个知识点：

先说下0为除数的情况：

如果除数为int型的0时，就查看被除数是否是int型的0，如果被除数是int型的0,将会抛出ArithmeticException,如果不是int而是double,就像例子中的f，则返回NaN

如果除数自身就是0.0时，那么就会输出NaN

另一个知识点：首先非检查异常也可以被捕获，但通常我们不会也不应该去捕获这种异常，因为这种异常通常就是程序员的编码的原因造成，如NullPointerException，ArithmeticException等异常，这些都可以提前处理或避免掉。上例中，只是为了举例非检查异常也可以捕获。

程序员应该捕获的是”检查型异常“，当看到某个方法声明中可能抛出某个检查型异常，那么作为调用方必须考虑如何处理这个异常，否则编译器就是给出错误提示。

将上例中的注释去掉，则会抛出”空指针异常“，此时由于程序并没有catch到这个异常，所以整个程序将会终止。

最后，说下Error，其实Error也是可以捕获的，也就是说，Throwable类都可以捕获，不管是Error还是Exception,但是我们通常只对检查型异常进行捕获。

```java
public class ExceptionTest {
    public static void main(String[] args){
        try{
            double a = 0.0;
            double c = 0;
            double b = 0.0 / a;
            double d = 0 / c;
            System.out.println(b);
            System.out.println(d);
            throw new Error("123");
        }catch (Error e){
            System.out.println(e.getMessage());
        }
        System.out.println("12344");
    }
}

输出：
NaN
NaN
123
12344
```
