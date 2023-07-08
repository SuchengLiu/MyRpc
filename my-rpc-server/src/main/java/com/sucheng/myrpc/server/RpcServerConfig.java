package com.sucheng.myrpc.server;

import com.sucheng.myrpc.codec.Decoder;
import com.sucheng.myrpc.codec.Encoder;
import com.sucheng.myrpc.codec.JSONDecoder;
import com.sucheng.myrpc.codec.JSONEncoder;
import com.sucheng.myrpc.registry.ServiceRegistry;
import com.sucheng.myrpc.registry.ZookeeperServiceRegistry;
import com.sucheng.myrpc.transport.HTTPTransportServer;
import com.sucheng.myrpc.transport.TransportServer;


public class RpcServerConfig {
    // 使用的网络模块
    private Class<? extends TransportServer> transportClass = HTTPTransportServer.class;
    // 使用的序列化模块
    private Class<? extends Encoder> encoderClass = JSONEncoder.class;
    private Class<? extends Decoder> decoderClass = JSONDecoder.class;
    // 注册中心
    private Class<? extends ServiceRegistry> registryClass = ZookeeperServiceRegistry.class;
    private String registryUrl = "127.0.0.1";
    // rpc-server默认端点
    private String ip = "127.0.0.1";
    private int port = 3000;

    public RpcServerConfig() {}

    public RpcServerConfig(int port) {
        this.port = port;
    }

    public RpcServerConfig(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public Class<? extends TransportServer> getTransportClass() {
        return transportClass;
    }

    public void setTransportClass(Class<? extends TransportServer> transportClass) {
        this.transportClass = transportClass;
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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Class<? extends ServiceRegistry> getRegistryClass() {
        return registryClass;
    }

    public void setRegistryClass(Class<? extends ServiceRegistry> registryClass) {
        this.registryClass = registryClass;
    }

    public String getRegistryUrl() {
        return registryUrl;
    }

    public void setRegistryUrl(String registryUrl) {
        this.registryUrl = registryUrl;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
