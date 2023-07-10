package com.sucheng.myrpc.transport;

/**
 * 网络通信的服务端
 * 1、启动并监听端口
 * 2、等待客户端连接，收到请求后处理
 * 3、关闭监听
 */
public interface TransportServer {

    void init(int port, RequestHandler handler);

    void start();

    void stop();
}
