package com.sucheng.myrpc.client;

import com.sucheng.myrpc.Peer;
import com.sucheng.myrpc.codec.Decoder;
import com.sucheng.myrpc.codec.Encoder;
import com.sucheng.myrpc.codec.JSONDecoder;
import com.sucheng.myrpc.codec.JSONEncoder;
import com.sucheng.myrpc.loadbalance.ConsistentHashLoadBalance;
import com.sucheng.myrpc.loadbalance.LoadBalance;
import com.sucheng.myrpc.registry.ServiceDiscovery;
import com.sucheng.myrpc.registry.ZookeeperServiceDiscovery;
import com.sucheng.myrpc.transport.HTTPTransportClient;
import com.sucheng.myrpc.transport.TransportClient;

import java.util.Arrays;
import java.util.List;

public class RpcClientConfig {
    // 使用的网络模块
    private Class<? extends TransportClient> tranportClass = HTTPTransportClient.class;
    // 使用的序列化模块
    private Class<? extends Encoder> encoderClass = JSONEncoder.class;
    private Class<? extends Decoder> decoderClass = JSONDecoder.class;
    // 注册中心
    private Class<? extends ServiceDiscovery> discoveryClass = ZookeeperServiceDiscovery.class;
    // 注册中心ip
    private String registryUrl = "127.0.0.1";
    // 负载均衡策略
    private Class<? extends LoadBalance> loadBalanceClass = ConsistentHashLoadBalance.class;

//    // 路由选择策略
//    private Class<? extends TransportSelector> transportSelectorClass = RandomTransportSelector.class;
//    // 可以连接的服务器端点
//    private List<Peer> servers = Arrays.asList(
//            new Peer("127.0.0.1", 3000)
//    );
//    // 每个server连接client的数量
//    private int connectCount = 1;

    public RpcClientConfig() {}

    public RpcClientConfig(String registryUrl) {
        this.registryUrl = registryUrl;
    }

    public Class<? extends TransportClient> getTranportClass() {
        return tranportClass;
    }

    public void setTranportClass(Class<? extends TransportClient> tranportClass) {
        this.tranportClass = tranportClass;
    }

    public Class<? extends Encoder> getEncoderClass() {
        return encoderClass;
    }

    public void setEncoderClass(Class<? extends Encoder> encoderClass) {
        this.encoderClass = encoderClass;
    }

    public Class<? extends Decoder> getDecoderClass() {
        return decoderClass;
    }

    public void setDecoderClass(Class<? extends Decoder> decoderClass) {
        this.decoderClass = decoderClass;
    }

    public Class<? extends ServiceDiscovery> getDiscoveryClass() {
        return discoveryClass;
    }

    public void setDiscoveryClass(Class<? extends ServiceDiscovery> discoveryClass) {
        this.discoveryClass = discoveryClass;
    }

    public String getRegistryUrl() {
        return registryUrl;
    }

    public void setRegistryUrl(String registryUrl) {
        this.registryUrl = registryUrl;
    }

    public Class<? extends LoadBalance> getLoadBalanceClass() {
        return loadBalanceClass;
    }

    public void setLoadBalanceClass(Class<? extends LoadBalance> loadBalanceClass) {
        this.loadBalanceClass = loadBalanceClass;
    }
}
