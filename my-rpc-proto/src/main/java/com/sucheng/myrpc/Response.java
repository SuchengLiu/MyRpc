package com.sucheng.myrpc;

/**
 * 表示RPC的一个响应
 */
public class Response {
    private int code; // 服务返回的编码，0-成功，非0-失败
    private String message; // 具体的错误信息
    private Object data; // 返回的数据

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
