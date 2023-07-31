package com.sucheng.myrpc.server;

import com.sucheng.myrpc.Peer;
import com.sucheng.myrpc.Request;
import com.sucheng.myrpc.Response;
import com.sucheng.myrpc.codec.Decoder;
import com.sucheng.myrpc.codec.Encoder;
import com.sucheng.myrpc.common.utils.ReflectionUtils;
import com.sucheng.myrpc.registry.ServiceRegistry;
import com.sucheng.myrpc.transport.RequestHandler;
import com.sucheng.myrpc.transport.TransportServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
public class RpcServer {
    private RpcServerConfig config;
    private TransportServer net;
    private Encoder encoder;
    private Decoder decoder;
    private ServiceManager serviceManager;
    private ServiceInvoker serviceInvoker;
    private ServiceRegistry serviceRegistry;

    public RpcServer() {
        this(new RpcServerConfig());
    }

    public RpcServer(RpcServerConfig config) {
        this.config = config;
        this.net = ReflectionUtils.newInstance(config.getTransportClass());
        this.net.init(config.getPort(), this.handler);

        this.encoder = ReflectionUtils.newInstance(config.getEncoderClass());
        this.decoder = ReflectionUtils.newInstance(config.getDecoderClass());
        this.serviceManager = new ServiceManager();
        this.serviceInvoker = new ServiceInvoker();
        this.serviceRegistry = ReflectionUtils.newInstance(config.getRegistryClass());
    }

    public RpcServer(int port) {
        this(new RpcServerConfig(port));
    }

    public RpcServer(String ip, int port) {
        this(new RpcServerConfig(ip, port));
    }

    public <T> void register(Class<T> interfaceClass, T bean) {
        // 向rpc服务器注册服务接口和服务提供者
        serviceManager.register(interfaceClass, bean);
        // 向注册中心对应的服务接口注册当前rpc服务器
        serviceRegistry.publish(interfaceClass, new Peer(config.getIp(), config.getPort()), config.getRegistryUrl());
    }

    public void start() {
        net.start();
    }

    public void stop() {
        net.stop();
    }

    private RequestHandler handler = new RequestHandler() {
        @Override
        public void onRequest(InputStream receive, OutputStream toResponse) {
            Response response = new Response();

            try {
                // 解码数据
                byte[] inBytes = IOUtils.readFully(receive, receive.available());
                Request request = decoder.decode(inBytes, Request.class);
                log.info("get request: {}", request);
                // 在服务器上查询并调用服务
                ServiceInstance serviceInstance = serviceManager.lookup(request);
                Object ret = serviceInvoker.invoke(serviceInstance, request);
                // 返回结果
                response.setData(ret);

            } catch (Exception e) {
                log.warn(e.getMessage(), e);
                response.setCode(1);
                response.setMessage("RpcServer got error: " + e.getClass().getName() + " : " + e.getMessage());

            } finally {
                // 编码并发送数据
                byte[] outBytes = encoder.encode(response);
                try {
                    toResponse.write(outBytes);
                    log.info("response client");
                } catch (IOException e) {
                    log.warn(e.getMessage(), e);
                }
            }
        }
    };


}
