package com.sucheng.myrpc.registry;

import org.junit.Test;

import static org.junit.Assert.*;

public class ZookeeperServiceDiscoveryTest {

    @Test
    public void subscribe() {
        ZookeeperServiceDiscovery zk = new ZookeeperServiceDiscovery();
        zk.subscribe(TestInterface.class, "127.0.0.1");
    }
}