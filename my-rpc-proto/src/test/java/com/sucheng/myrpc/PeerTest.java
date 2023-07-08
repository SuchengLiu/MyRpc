package com.sucheng.myrpc;

import org.junit.Test;

import static org.junit.Assert.*;

public class PeerTest {

    @Test
    public void parseString() {
        Peer peer = new Peer("127.0.0.1", 1234);
        System.out.println(peer.toString());
        System.out.println(Peer.parseString(peer.toString()));
    }
}