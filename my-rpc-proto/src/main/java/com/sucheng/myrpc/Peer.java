package com.sucheng.myrpc;


/**
 * 表示网络传输的一个端点
 */
public class Peer {
    private String host;
    private int port;

    public Peer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "http://" + host + ":" + port;
    }

    public static Peer parseString(String url) {
        String[] splits = url.split(":");
        String host = splits[1].substring(2);
        int port = Integer.parseInt(splits[2]);
        return new Peer(host, port);
    }
}
