package com.sucheng.myrpc.loadbalance;

import com.sucheng.myrpc.Peer;
import com.sucheng.myrpc.ServiceDescriptor;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class ConsistentHashLoadBalance implements LoadBalance{
    // key:服务接口名.方法名，value:selector
    private ConcurrentHashMap<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();

    @Override
    public Peer doSelect(List<Peer> peers, ServiceDescriptor serviceDescriptor, Object[] args) {
        // 生成Peer列表的hash码
        int identityHashCode = peers.hashCode();
        // 获取服务对应的selector
        String service = serviceDescriptor.getClazz() + "." + serviceDescriptor.getMethod();
        ConsistentHashSelector selector = selectors.get(service);
        if(selector == null || selector.identityHashCode != identityHashCode) {
            // 若没有找到selector或selector变更了，则会新建selector
            selector = new ConsistentHashSelector(peers, identityHashCode);
            selectors.put(service, selector);
        }
        // 找到selector后便会调用selector的select方法
        return selector.select(serviceDescriptor, args);
    }

    /**
     * 一致性哈希负载均衡算法的映射关系的实现类
     */
    private static final class ConsistentHashSelector{
        // 存储Hash值与节点映射关系的TreeMap
        private final TreeMap<Long, Peer> virtualInvokers;
        // 每个provider都创建replicaNumber个节点
        private final int replicaNumber;
        // 用来识别Peer列表是否发生变更的Hash码
        private final int identityHashCode;

        private ConsistentHashSelector(List<Peer> peers, int identityHashCode) {
            this.virtualInvokers = new TreeMap<>();
            this.replicaNumber = 160;
            this.identityHashCode = identityHashCode;
            // 遍历所有provider，计算出其地址（ip+port）对应的md5码，并按照配置的节点数目replicaNumber的值来初始化服务节点和所有虚拟节点
            for(Peer peer : peers) {
                String address = peer.toString();
                for(int i=0; i<replicaNumber / 4; i++) {
                    byte[] digest = DigestUtils.md5Hex((address + i).getBytes()).getBytes();
                    // 在每次获得摘要之后，还会对该摘要进行4次数位级别的散列，目的是为了加强散列效果
                    for(int h=0; h<4; h++) {
                        long m = hash(digest, h);
                        virtualInvokers.put(m, peer);
                    }
                }
            }
        }

        private long hash(byte[] digest, int number) {
            return (((long) (digest[3 + number * 4] & 0xFF) << 24)
                    | ((long) (digest[2 + number * 4] & 0xFF) << 16)
                    | ((long) (digest[1 + number * 4] & 0xFF) << 8)
                    | (digest[number * 4] & 0xFF))
                    & 0xFFFFFFFFL;
        }

        public Peer select(ServiceDescriptor serviceDescriptor, Object[] args) {
            // 参数相同的请求分配到同一机器上，所以这里的key就是服务接口名+方法名+参数名
            String key = toKey(serviceDescriptor, args);
            byte[] digest = DigestUtils.md5Hex(key.getBytes()).getBytes();
            return selectFromKey(hash(digest, 0));
        }

        private String toKey(ServiceDescriptor serviceDescriptor, Object[] args) {
            StringBuilder sb = new StringBuilder();
            sb.append(serviceDescriptor.getClazz());
            sb.append(serviceDescriptor.getMethod());
            for(Object arg : args) {
                sb.append(arg);
            }
            return sb.toString();
        }

        private Peer selectFromKey(long hash) {
            // TreeMap的方法ceilingEntry(hash)，用来获取大于等于传入值的首个元素
            Map.Entry<Long, Peer> entry = virtualInvokers.ceilingEntry(hash);
            if(entry == null) {
                // 当没有比传入ceilingEntry()方法中的值大的元素的时候，就获取整个TreeMap的第一个元素
                // 取服务Hash值大于请求Hash值的第一个服务作为实际的调用服务
                entry = virtualInvokers.firstEntry();
            }
            return entry.getValue();
        }

    }
}
