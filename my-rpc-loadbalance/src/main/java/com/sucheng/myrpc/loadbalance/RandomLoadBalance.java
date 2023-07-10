package com.sucheng.myrpc.loadbalance;

import com.sucheng.myrpc.Peer;
import com.sucheng.myrpc.ServiceDescriptor;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance implements LoadBalance{
    Random random;

    @Override
    public Peer doSelect(List<Peer> peers, ServiceDescriptor serviceDescriptor, Object[] args) {
        if(random == null) {
            random = new Random();
        }
        return peers.get(random.nextInt(peers.size()));
    }
}
