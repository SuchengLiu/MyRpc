package com.sucheng.myrpc.registry;

import com.sucheng.myrpc.Peer;
import org.junit.Test;

import static org.junit.Assert.*;

public class ZookeeperServiceRegistryTest {

    @Test
    public void publish() {
        ZookeeperServiceRegistry zk = new ZookeeperServiceRegistry();
        zk.publish(TestInterface.class, new Peer("127.0.0.1", 3000), "127.0.0.1");
        zk.publish(TestInterface.class, new Peer("127.0.0.1", 4000), "127.0.0.1");
        zk.publish(TestInterface.class, new Peer("127.0.0.1", 5000), "127.0.0.1");
    }
}