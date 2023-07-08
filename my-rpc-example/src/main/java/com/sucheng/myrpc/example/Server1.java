package com.sucheng.myrpc.example;

import com.sucheng.myrpc.server.RpcServer;

public class Server1 {
    public static void main(String[] args) {
        RpcServer server = new RpcServer("127.0.0.1", 3000);
        server.register(CalcService.class, new CalcServiceImpl());
        server.start();
    }
}
