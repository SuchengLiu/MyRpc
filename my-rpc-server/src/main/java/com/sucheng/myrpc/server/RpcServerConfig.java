package com.sucheng.myrpc.server;

import com.sucheng.myrpc.codec.Decoder;
import com.sucheng.myrpc.codec.Encoder;
import com.sucheng.myrpc.codec.JSONDecoder;
import com.sucheng.myrpc.codec.JSONEncoder;
import com.sucheng.myrpc.transport.HTTPTransportServer;
import com.sucheng.myrpc.transport.TransportServer;

public class RpcServerConfig {
    // 使用的网络模块
    private Class<? extends TransportServer> transportClass = HTTPTransportServer.class;
    // 使用的序列化模块
    private Class<? extends Encoder> encoderClass = JSONEncoder.class;
    private Class<? extends Decoder> decoderClass = JSONDecoder.class;
    // rpc-server监听的端口
    private int port = 3000;

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
}
