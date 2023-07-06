package com.sucheng.myrpc.server;

import com.sucheng.myrpc.Request;
import com.sucheng.myrpc.ServiceDescriptor;
import com.sucheng.myrpc.common.utils.ReflectionUtils;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class ServiceManagerTest {
    ServiceManager serviceManager;

    @Before
    public void init() {
        serviceManager = new ServiceManager();

        TestInterface bean = new TestClass();
        serviceManager.register(TestInterface.class, bean);
    }

    @Test
    public void register() {
        TestInterface bean = new TestClass();
        serviceManager.register(TestInterface.class, bean);
    }

    @Test
    public void lookup() {
        Method method = ReflectionUtils.getPublicMethods(TestInterface.class)[0];
        ServiceDescriptor serviceDescriptor = ServiceDescriptor.from(TestInterface.class, method);

        Request request = new Request();
        request.setService(serviceDescriptor);

        ServiceInstance serviceInstance = serviceManager.lookup(request);
        assertNotNull(serviceInstance);
    }
}