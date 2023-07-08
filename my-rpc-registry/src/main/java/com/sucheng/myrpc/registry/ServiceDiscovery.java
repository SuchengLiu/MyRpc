package com.sucheng.myrpc.registry;

import com.sucheng.myrpc.Peer;

import java.util.List;

public interface ServiceDiscovery {
    List<Peer> subscribe(Class clazz, String url);
}
