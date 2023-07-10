package com.sucheng.myrpc.registry;

import com.sucheng.myrpc.Peer;

import java.util.List;

public interface ServiceDiscovery {
    /**
     * 向注册中心订阅服务
     * @param clazz 要订阅的服务接口
     * @param url 注册中心服务器的地址
     * @return
     */
    List<Peer> subscribe(Class clazz, String url);
}
