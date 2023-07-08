package com.sucheng.myrpc.example;

import com.sucheng.myrpc.server.RpcServer;

public class Server2 {
    public static void main(String[] args) {
        RpcServer server = new RpcServer("127.0.0.1", 3001);
        server.register(CalcService.class, new CalcServiceImpl());
        server.start();
    }

}
