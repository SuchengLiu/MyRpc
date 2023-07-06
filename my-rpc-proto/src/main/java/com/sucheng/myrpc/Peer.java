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
        return "Peer{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
