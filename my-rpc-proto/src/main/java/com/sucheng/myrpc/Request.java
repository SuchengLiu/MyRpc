package com.sucheng.myrpc;

/**
 * 表示RPC的一个请求
 */
public class Request {
    private ServiceDescriptor service;
    private Object[] parameters;

    public ServiceDescriptor getService() {
        return service;
    }

    public void setService(ServiceDescriptor service) {
        this.service = service;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
}
