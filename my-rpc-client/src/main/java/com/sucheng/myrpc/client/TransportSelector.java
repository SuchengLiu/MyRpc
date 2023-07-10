package com.sucheng.myrpc.client;

import com.sucheng.myrpc.Peer;
import com.sucheng.myrpc.transport.TransportClient;

import java.util.List;

/**
 * 路由选择
 */
@Deprecated
public interface TransportSelector {
    /**
     * 初始化selector
     * @param peers 可以连接的server端点的信息
     * @param count 每个server连接client的数量
     * @param clazz client实现类的class
     */
    void init(List<Peer> peers, int count, Class<? extends TransportClient> clazz);

    /**
     *  选择一个Transport与server交互
     * @return 网络通信的客户端实例
     */
    TransportClient select();

    /**
     *
     * @param client 释放用完的client
     */
    void release(TransportClient client);

    /**
     * 关闭selector
     */
    void close();

}
