package com.sucheng.myrpc.client;

import com.sucheng.myrpc.codec.Decoder;
import com.sucheng.myrpc.codec.Encoder;
import com.sucheng.myrpc.common.utils.ReflectionUtils;

import java.lang.reflect.Proxy;

public class RpcClient {
    private RpcClientConfig config;
    private Encoder encoder;
    private Decoder decoder;
    private TransportSelector transportSelector;

    public RpcClient() {
        this(new RpcClientConfig());
    }

    public RpcClient(RpcClientConfig rpcClientConfig) {
        this.config = rpcClientConfig;

        this.encoder = ReflectionUtils.newInstance(this.config.getEncoderClass());
        this.decoder = ReflectionUtils.newInstance(this.config.getDecoderClass());
        this.transportSelector = ReflectionUtils.newInstance(this.config.getTransportSelectorClass());

        this.transportSelector.init(
                this.config.getServers(),
                this.config.getConnectCount(),
                this.config.getTranportClass()
        );
    }

    // 获取接口的代理对象
    public <T> T getProxy(Class<T> clazz) {
        // JDK动态代理
        return (T) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[] {clazz},
                new RemoteInvoker(clazz, encoder, decoder, transportSelector)
        );
    }
}
