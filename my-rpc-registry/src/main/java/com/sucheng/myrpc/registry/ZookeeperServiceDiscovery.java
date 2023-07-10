package com.sucheng.myrpc.registry;

import com.sucheng.myrpc.Peer;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

/**
 * 基于Zookeeper实现的服务订阅机制
 * 先从本地缓存获取服务的提供者列表，获取不到再从zookeeper拉取
 * zookeeper的watcher会监控服务的子节点的变化，若子节点有变化，则重新获取最新子节点
 * 将zookeeper拉取的子节点信息转换为服务提供至列表，并存入本地缓存
 */
@Slf4j
public class ZookeeperServiceDiscovery implements ServiceDiscovery{
    // session超时时间
    final int sessionTimeout = 5000;
    // 本地缓存map，map可能存在并发安全问题 同时读写(subscribe读，watcher写)
    Map<Class, List<Peer>> localServiceMap = new ConcurrentHashMap<>();
    // 用于等待 SyncConnected 事件触发后继续执行当前线程
    CountDownLatch latch = new CountDownLatch(1);

    final private String rpcServicePath = "/rpc-service";

    @Override
    public List<Peer> subscribe(Class clazz, String url) {
        // 首先查找本地缓存
        // 每次subscribe获取到的都是最新值
        List<Peer> peers = localServiceMap.get(clazz);
        if(peers == null) {
            // 本地缓存为空 从zookeeper里查找
            ZooKeeper zooKeeper = connectZookeeper(url);
            watchNode(zooKeeper, clazz);
            // watchNode将peers放进hashmap了
            peers = localServiceMap.get(clazz);
        }
        return peers;
    }

    private ZooKeeper connectZookeeper(String url) {
        ZooKeeper zooKeeper = null;
        try {
            zooKeeper = new ZooKeeper(url, sessionTimeout, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                        // SyncConnected，表示与 ZooKeeper 服务器的连接已经建立完成。之后唤醒当前正在执行的线程
                        latch.countDown();
                    }
                }
            });
            latch.await(); // 使当前线程处于等待状态
        } catch (Exception e) {
            log.error("connect to zookeeper failed: " + e.getMessage());
        }
        return zooKeeper;
    }

    private void watchNode(ZooKeeper zk, Class clazz) {
        try {
            String servicePath = rpcServicePath + "/" + clazz.getName();
            List<String> nodeList = zk.getChildren(servicePath, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
                        // 若子节点有变化，则重新调用该方法（为了获取最新子节点）
                        watchNode(zk, clazz);
                    }
                }
            });
            List<Peer> dataList = new ArrayList<>(); // 用于存放所有子节点中的数据
            for(String node : nodeList) {
                byte[] data = zk.getData(servicePath + "/" + node, false, null);
                dataList.add(Peer.parseString(new String(data)));
            }
            log.info("server {} provider {}", clazz.getName(), dataList);
            localServiceMap.put(clazz, dataList);
        } catch (Exception e) {
            log.error("failed find provider: {}", e.getMessage());
        }
    }
}
