package com.sucheng.myrpc.codec;

import org.junit.Test;

import static org.junit.Assert.*;

public class JSONEncoderTest {

    @Test
    public void encode() {
        TestBean bean = new TestBean();
        bean.setName("sucheng");
        bean.setAge(24);

        Encoder encoder = new JSONEncoder();
        byte[] bytes = encoder.encode(bean);

        assertNotNull(bytes);
    }
}