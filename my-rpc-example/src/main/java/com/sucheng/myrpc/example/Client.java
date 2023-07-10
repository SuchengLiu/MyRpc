package com.sucheng.myrpc.example;

import com.sucheng.myrpc.client.RpcClient;

public class Client {
    public static void main(String[] args) {
        RpcClient client = new RpcClient();
        // 客户端根据代理对象调用服务
        CalcService service = client.getProxy(CalcService.class);

        int res1 = service.minus(10000, 9999);
        int res2 = service.add(1, 1);
        int res3 = service.minus(333, 330);

        System.out.println(res1);
        System.out.println(res2);
        System.out.println(res3);
    }
}
