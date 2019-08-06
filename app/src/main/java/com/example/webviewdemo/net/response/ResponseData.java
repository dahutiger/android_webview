package com.example.webviewdemo.net.response;

public class ResponseData<T> {
    
    private int code;
    private String msg;
    private T info;
    
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public String getMsg() {
        return msg;
    }
    
    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    public T getInfo() {
        return info;
    }
    
    public void setInfo(T info) {
        this.info = info;
    }
}
