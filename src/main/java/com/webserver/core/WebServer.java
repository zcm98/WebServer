package com.webserver.core;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * ClassName: WebServer
 * Function:  WebServer主类
 * Date:      2019/11/4 14:13
 * author     Kenny
 * version    V1.0
 */
public class WebServer {
    private ServerSocket server;
    /**
     * 线程池
     */
    private ExecutorService threadPool;

    /**
     * 构造方法,初始化使用
     */
    public WebServer() {
        try {
            server = new ServerSocket(8088);
            threadPool = new ThreadPoolExecutor(50,100,200L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(),new ThreadPoolExecutor.AbortPolicy());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            while (true) {
                System.out.println("等待客户端连接...");
                Socket socket = server.accept();
                System.out.println("一个客户端连接了!");
                //启动一个线程处理该客户端交互
                ClientHandler handler = new ClientHandler(socket);
                threadPool.execute(handler);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        WebServer server = new WebServer();
        server.start();
    }
}
