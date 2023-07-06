package com.sucheng.myrpc.transport;

import com.sucheng.myrpc.Peer;

import java.io.InputStream;

/**
 * 网络通信的客户端
 * 1、建立连接
 * 2、发送数据，并等待响应
 * 3、关闭连接
 */
public interface TransportClient {

    void connect(Peer peer);

    InputStream write(InputStream data);

    void close();
}
