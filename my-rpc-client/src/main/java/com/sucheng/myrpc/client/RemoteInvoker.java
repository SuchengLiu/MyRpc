package com.sucheng.myrpc.client;

import com.sucheng.myrpc.Peer;
import com.sucheng.myrpc.Request;
import com.sucheng.myrpc.Response;
import com.sucheng.myrpc.ServiceDescriptor;
import com.sucheng.myrpc.codec.Decoder;
import com.sucheng.myrpc.codec.Encoder;
import com.sucheng.myrpc.common.utils.ReflectionUtils;
import com.sucheng.myrpc.loadbalance.LoadBalance;
import com.sucheng.myrpc.registry.ServiceDiscovery;
import com.sucheng.myrpc.transport.TransportClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 调用远程服务的代理类
 */
@Slf4j
public class RemoteInvoker implements InvocationHandler {
    // 客户端请求的接口
    private Class interfaceClass;
    // 使用的网络模块
    private TransportClient transportClient;
    private Encoder encoder;
    private Decoder decoder;
    private ServiceDiscovery serviceDiscovery;
    private String registryUrl;
    private LoadBalance loadBalance;
//    private TransportSelector transportSelector;

    public RemoteInvoker(
            Class interfaceClass,
            TransportClient transportClient,
            Encoder encoder,
            Decoder decoder,
            ServiceDiscovery serviceDiscovery,
            String registryUrl,
            LoadBalance loadBalance) {
        this.interfaceClass = interfaceClass;
        this.transportClient = transportClient;
        this.encoder = encoder;
        this.decoder = decoder;
        this.serviceDiscovery = serviceDiscovery;
        this.registryUrl = registryUrl;
        this.loadBalance = loadBalance;
    }

    /**
     * 代理对象调用任何方法时都会把方法类型和参数信息交给invoke函数
     * invoke函数负责给服务端发送请求，请求体中包含接口名，方法名和方法返回类型，参数类型和参数实际值
     * 服务端收到请求后，根据请求体中的信息找到对应服务（函数）,和提供服务的对象。通过反射调用对应的方法。
     * @param proxy 未使用
     * @param method 当前调用的方法
     * @param args 当前调用方法的参数
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy,
                         Method method,
                         Object[] args) throws Throwable {
        // 创建请求对象
        Request request = new Request();
        ServiceDescriptor serviceDescriptor = ServiceDescriptor.from(interfaceClass, method);
        request.setService(serviceDescriptor);
        request.setParameters(args);

        // 调用远程服务，并等待响应
        Response response = invokeRemote(request, serviceDescriptor, args);
        if(response == null || response.getCode() != 0) {
            throw new IllegalStateException("fail to invoke remote: " + response);
        }

        return response.getData();
    }

    private Response invokeRemote(Request request, ServiceDescriptor serviceDescriptor, Object[] args) {
        // 向注册中心订阅服务提供者列表
        List<Peer> peers = serviceDiscovery.subscribe(interfaceClass, registryUrl);
        // 基于负载均衡策略选择一个服务提供者
        Peer peer = loadBalance.doSelect(peers, serviceDescriptor, args);
        transportClient.connect(peer); // 只是设置了服务提供者的地址，并没有真的连接
        log.info("connect to provider: {}", peer);

        Response response;
        try{
//            client = transportSelector.select();

            // 发送到远程的数据
            byte[] outBytes = encoder.encode(request);
            // write函数才真正与服务器建立连接，并等待服务器返回结果
            InputStream received = transportClient.write(new ByteArrayInputStream(outBytes));
            byte[] inBytes = IOUtils.readFully(received, received.available());
            response = decoder.decode(inBytes, Response.class);
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
            response = new Response();
            response.setCode(1);
            response.setMessage("RpcClient got error: " + e.getClass() + " : " + e.getMessage());
        } finally {
//            if(client != null) {
//                transportSelector.release(client);
//            }
        }
        return response;
    }
}
