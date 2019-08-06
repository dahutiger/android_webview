package com.example.webviewdemo.net;

import java.io.IOException;

public class ResponseException extends IOException {
    
    private static final long serialVersionUID = -7458224171143367408L;
    
    private int code;
    private String msg;
    
    public ResponseException() {
        this(-1, "网络错误！");
    }
    
    public ResponseException(String msg) {
        this(-1, msg);
    }
    
    public ResponseException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    
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
}
