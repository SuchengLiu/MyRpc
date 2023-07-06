package com.sucheng.myrpc.client;

import com.sucheng.myrpc.Request;
import com.sucheng.myrpc.Response;
import com.sucheng.myrpc.ServiceDescriptor;
import com.sucheng.myrpc.codec.Decoder;
import com.sucheng.myrpc.codec.Encoder;
import com.sucheng.myrpc.transport.TransportClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 调用远程服务的代理类
 */
@Slf4j
public class RemoteInvoker implements InvocationHandler {
    private Class clazz;
    private Encoder encoder;
    private Decoder decoder;
    private TransportSelector transportSelector;

    public RemoteInvoker(Class clazz,
                         Encoder encoder,
                         Decoder decoder,
                         TransportSelector transportSelector) {
        this.clazz = clazz;
        this.encoder = encoder;
        this.decoder = decoder;
        this.transportSelector = transportSelector;
    }

    /**
     *
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
        request.setService(ServiceDescriptor.from(clazz, method));
        request.setParameters(args);

        // 调用远程服务，并等待响应
        Response response = invokeRemote(request);
        if(response == null || response.getCode() != 0) {
            throw new IllegalStateException("fail to invoke remote: " + response);
        }

        return response.getData();
    }

    private Response invokeRemote(Request request) {
        Response response = null;
        TransportClient client = null;

        try{
            // 选择一个客户端
            client = transportSelector.select();

            // 发送到远程的数据
            byte[] outBytes = encoder.encode(request);
            // 从远程收到的数据
            InputStream received = client.write(new ByteArrayInputStream(outBytes));
            byte[] inBytes = IOUtils.readFully(received, received.available());
            response = decoder.decode(inBytes, Response.class);

        } catch (IOException e) {
            log.warn(e.getMessage(), e);
            response = new Response();
            response.setCode(1);
            response.setMessage("RpcClient got error: " + e.getClass() + " : " + e.getMessage());
        } finally {
            if(client != null) {
                transportSelector.release(client);
            }
        }
        return response;
    }
}
