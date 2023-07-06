package com.sucheng.myrpc.server;

import java.lang.reflect.Method;

/**
 * 表示一个具体的服务
 */
public class ServiceInstance {
    private Object target;
    private Method method;

    public ServiceInstance(Object target, Method method) {
        this.target = target;
        this.method = method;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
