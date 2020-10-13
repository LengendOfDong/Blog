```java
package com.zte.rpc;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

@SuppressWarnings("unchecked")
public class RPCClient<T> {
    public static <T> T getRemoteProxyObj(final Class<?> serviceInterface, final InetSocketAddress addr) {
        //1.将本地的接口调用转换成jdk的动态代理，在动态代理中实现接口的远程调用
        return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class<?>[]{serviceInterface},
                (proxy, method, args) -> {
                    Socket socket = null;
                    ObjectOutputStream output = null;
                    ObjectInputStream input = null;
                    try {
                        //2.创建Socket客户端，根据指定地址连接远程服务提供者
                        socket = new Socket();
                        socket.connect(addr);

                        //3.将远程服务调用所需的接口类、方法名、参数列表等编码后发送给服务提供者
                        output = new ObjectOutputStream(socket.getOutputStream());
                        output.writeUTF(serviceInterface.getName());//接口类
                        output.writeUTF(method.getName());//方法名
                        output.writeObject(method.getParameterTypes());//参数类型
                        output.writeObject(args); //参数

                        //4.同步阻塞等待服务器返回应答，获取应答后返回
                        input = new ObjectInputStream(socket.getInputStream());
                        return input.readObject();
                    } finally {
                        if (socket != null) {
                            socket.close();
                        }
                        if (output != null) {
                            output.close();
                        }
                        if (input != null) {
                            input.close();
                        }
                    }
                }
        );
    }
}
```
