package com.sucheng.myrpc.client;

import com.sucheng.myrpc.codec.Decoder;
import com.sucheng.myrpc.codec.Encoder;
import com.sucheng.myrpc.common.utils.ReflectionUtils;
import com.sucheng.myrpc.registry.ServiceDiscovery;

import java.lang.reflect.Proxy;

public class RpcClient {
    private RpcClientConfig config;
    private Encoder encoder;
    private Decoder decoder;
    private ServiceDiscovery serviceDiscovery;

    private TransportSelector transportSelector;

    public RpcClient() {
        this(new RpcClientConfig());
    }

    public RpcClient(String registryUrl) {
        this(new RpcClientConfig(registryUrl));
    }

    public RpcClient(RpcClientConfig rpcClientConfig) {
        this.config = rpcClientConfig;
        this.encoder = ReflectionUtils.newInstance(this.config.getEncoderClass());
        this.decoder = ReflectionUtils.newInstance(this.config.getDecoderClass());
        this.serviceDiscovery = ReflectionUtils.newInstance(this.config.getDiscoveryClass());

        this.transportSelector = ReflectionUtils.newInstance(this.config.getTransportSelectorClass());
        this.transportSelector.init(
                this.config.getServers(),
                this.config.getConnectCount(),
                this.config.getTranportClass()
        );


    }

    // 获取接口的代理对象
    public <T> T getProxy(Class<T> interfaceClass) {
        // JDK动态代理
        Object object = Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[] {interfaceClass},
                new RemoteInvoker(interfaceClass, encoder, decoder, transportSelector)
        );
        return interfaceClass.cast(object);
    }
}
