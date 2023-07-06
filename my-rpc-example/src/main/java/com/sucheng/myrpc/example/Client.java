package com.sucheng.myrpc.example;

import com.sucheng.myrpc.client.RpcClient;

public class Client {
    public static void main(String[] args) {
        RpcClient client = new RpcClient();
        // 远程代理对象
        CalcService service = client.getProxy(CalcService.class);

        int res1 = service.add(1, 1);
        int res2 = service.minus(10, 9);

        System.out.println(res1);
        System.out.println(res2);
    }
}
