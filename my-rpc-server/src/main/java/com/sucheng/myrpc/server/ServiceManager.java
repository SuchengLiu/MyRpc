package com.sucheng.myrpc.server;

import com.sucheng.myrpc.Request;
import com.sucheng.myrpc.ServiceDescriptor;
import com.sucheng.myrpc.common.utils.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理rpc暴露的服务
 */
@Slf4j
public class ServiceManager {
    private Map<ServiceDescriptor, ServiceInstance> services;

    public ServiceManager() {
        this.services = new ConcurrentHashMap<>();
    }

    /**
     * 注册服务
     * @param interfaceClass 服务接口
     * @param bean 服务提供者，单例
     */
    public <T> void register(Class<T> interfaceClass, T bean) {
        // 接口中的公共方法
        Method[] methods = ReflectionUtils.getPublicMethods(interfaceClass);
        for(Method method : methods) {
            ServiceInstance serviceInstance = new ServiceInstance(bean, method);
            ServiceDescriptor serviceDescriptor = ServiceDescriptor.from(interfaceClass, method);
            services.put(serviceDescriptor, serviceInstance);
            log.info("register service: {} {}", serviceDescriptor.getClazz(), serviceDescriptor.getMethod());
        }
    }

    // 查找服务
    public ServiceInstance lookup(Request request) {
        ServiceDescriptor serviceDescriptor = request.getService();
        return services.get(serviceDescriptor);
    }
}
