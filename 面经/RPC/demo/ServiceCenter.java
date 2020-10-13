package com.zte.rpc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServiceCenter implements Server {
    private static final HashMap<String, Class> serviceRegistry = new HashMap<String, Class>();
    private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static boolean isRunning = false;

    private static int port;

    public ServiceCenter(int port) {
        ServiceCenter.port = port;
    }

    @Override
    public void start() throws IOException {
        ServerSocket server = new ServerSocket();
        server.bind(new InetSocketAddress(port));
        System.out.println("demo.Server Start.....");
        try {
            while (true) {
                executor.execute(new ServiceTask(server.accept()));
            }
        } finally {
            server.close();
        }
    }

    @Override
    public void register(Class serviceInterface, Class impl) {
        serviceRegistry.put(serviceInterface.getName(), impl);
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void stop() {
        isRunning = false;
        executor.shutdown();
    }


    private static class ServiceTask implements Runnable {
        Socket client = null;


        public ServiceTask(Socket client) {
            this.client = client;

        }

        @Override
        public void run() {
            ObjectInputStream input = null;
            ObjectOutputStream output = null;
            try {
                input = new ObjectInputStream(client.getInputStream());//获取客户端的传输过来的输入流
                String serviceName = input.readUTF();//获取接口类名
                String methodName = input.readUTF();//获取方法名
                Class<?>[] parameterTypes = (Class<?>[]) input.readObject();//获取参数类型
                Object[] arguments = (Object[]) input.readObject();//获取参数
                Class serviceClass = serviceRegistry.get(serviceName);//根据服务名找到对应的服务类
                if (serviceClass == null) {
                    throw new ClassNotFoundException(serviceName + "not found!");
                }
                Method method = serviceClass.getMethod(methodName, parameterTypes);//通过服务类反射获取对应的方法
                Object result = method.invoke(serviceClass.newInstance(), arguments);//将参数传入方法中

                output = new ObjectOutputStream(client.getOutputStream());
                output.writeObject(result);//通过输出流返回响应
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (client != null) {
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
