package com.sucheng.myrpc.loadbalance;

import com.sucheng.myrpc.Peer;
import com.sucheng.myrpc.ServiceDescriptor;

import java.util.List;

public interface LoadBalance {
    Peer doSelect(List<Peer> peers, ServiceDescriptor serviceDescriptor, Object[] args);
}
