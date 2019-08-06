package com.example.webviewdemo.base;

import java.io.Serializable;

public class BaseBean implements Serializable {
    private static final long serialVersionUID = -5231391170451181438L;
    
    private String errorCode;
    private String errorMessage;
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
}
