package com.sucheng.myrpc.registry;

import com.sucheng.myrpc.Peer;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class ZookeeperServiceRegistry implements ServiceRegistry{
    // session超时时间
    final int sessionTimeout = 50000;
    // 用于等待 SyncConnected 事件触发后继续执行当前线程
    CountDownLatch latch = new CountDownLatch(1);

    final private String rpcServicePath = "/rpc-service";

    @Override
    public void publish(Class clazz, Peer peer, String url) {
        // 连接zookeeper服务器，返回连接后的zookeeper客户端
        ZooKeeper zooKeeper = connectZookeeper(url);
        if(zooKeeper != null && peer != null) {
            createNode(zooKeeper, clazz, peer);
        }
    }

    private ZooKeeper connectZookeeper(String url) {
        ZooKeeper zooKeeper = null;
        try {
            zooKeeper = new ZooKeeper(url, sessionTimeout, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                        // SyncConnected，表示与 ZooKeeper 服务器的连接已经建立完成。之后唤醒当前正在执行的线程
                        latch.countDown();
                    }
                }
            });
            latch.await(); // 使当前线程处于等待状态
        } catch (Exception e) {
            log.error("connect to zookeeper failed: " + e.getMessage());
        }
        if(zooKeeper != null) {
            // 检查/rpc-service路径对应的znode是否存在
            try {
                Stat stat = zooKeeper.exists(rpcServicePath, false);
                if(stat == null) {
                    // 如果不存在，创建一个永久的znode，即root节点：/rpc-service
                    zooKeeper.create(rpcServicePath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
            } catch (Exception e) {
                log.error("find zookeeper path failed: " + e.getMessage());
            }
        }
        return zooKeeper;
    }

    private void createNode(ZooKeeper zk, Class clazz, Peer peer){
        try {
            // 查找服务路径 如果不存在则创建
            String servicePath = rpcServicePath + "/" + clazz.getName();
            Stat stat = zk.exists(servicePath, false);
            if (stat == null) {
                // 如果不存在，创建一个永久的znode，即service节点：/rpc-service/服务接口名
                zk.create(servicePath, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            String providerPath = servicePath + "/provider";
            // 创建一个临时且有序的 ZNode
            String path = zk.create(providerPath, peer.toString().getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            log.info("create zookeeper node ({} -> {})", path, peer);
        } catch (Exception e) {
            log.error("create zookeeper node failed: " + e.getMessage());
        }
    }

}
