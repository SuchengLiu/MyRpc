package com.sucheng.myrpc.server;

import com.sucheng.myrpc.Request;
import com.sucheng.myrpc.common.utils.ReflectionUtils;

/**
 * 调用具体服务
 */
public class ServiceInvoker {
    public Object invoke(ServiceInstance serviceInstance, Request request) {
        return ReflectionUtils.invoke(
                serviceInstance.getTarget(),
                serviceInstance.getMethod(),
                request.getParameters()
        );
    }
}
