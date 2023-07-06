package com.sucheng.myrpc.client;

import com.sucheng.myrpc.Peer;
import com.sucheng.myrpc.codec.Decoder;
import com.sucheng.myrpc.codec.Encoder;
import com.sucheng.myrpc.codec.JSONDecoder;
import com.sucheng.myrpc.codec.JSONEncoder;
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
    // 路由选择策略
    private Class<? extends TransportSelector> transportSelectorClass = RandomTransportSelector.class;
    // 每个server与client的最大连接数量
    private int connectCount = 1;
    // 可以连接的服务器端点
    private List<Peer> servers = Arrays.asList(
            new Peer("127.0.0.1", 3000)
    );

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

    public Class<? extends TransportSelector> getTransportSelectorClass() {
        return transportSelectorClass;
    }

    public void setTransportSelectorClass(Class<? extends TransportSelector> transportSelectorClass) {
        this.transportSelectorClass = transportSelectorClass;
    }

    public int getConnectCount() {
        return connectCount;
    }

    public void setConnectCount(int connectCount) {
        this.connectCount = connectCount;
    }

    public List<Peer> getServers() {
        return servers;
    }

    public void setServers(List<Peer> servers) {
        this.servers = servers;
    }
}
