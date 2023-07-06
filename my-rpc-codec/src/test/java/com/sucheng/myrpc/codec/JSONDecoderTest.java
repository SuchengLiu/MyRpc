package com.sucheng.myrpc.codec;

import org.junit.Test;

import static org.junit.Assert.*;

public class JSONDecoderTest {

    @Test
    public void decode() {
        TestBean bean = new TestBean();
        bean.setName("sucheng");
        bean.setAge(24);

        Encoder encoder = new JSONEncoder();
        byte[] bytes = encoder.encode(bean);

        Decoder decoder = new JSONDecoder();
        System.out.println(decoder.decode(bytes, TestBean.class));
    }
}