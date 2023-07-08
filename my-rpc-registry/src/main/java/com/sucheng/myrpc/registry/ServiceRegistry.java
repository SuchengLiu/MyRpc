package com.sucheng.myrpc.registry;

import com.sucheng.myrpc.Peer;

public interface ServiceRegistry {
    /**
     *
     * @param clazz 注册中心对应的服务接口
     * @param peer rpc服务器的端点
     * @param url 注册中心服务器的地址
     */
    void publish(Class clazz, Peer peer, String url);
}
