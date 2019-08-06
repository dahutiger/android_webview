package com.example.webviewdemo.common.constant;

/**
 * 服务端主机
 */
public class ServerHost {
    
    public static final String SERVER_DEV = "http://212.64.114.97:12000/";
    public static final String SERVER_STAGING = "http://carrier-api-uat.wulianshuntong.com/";
    public static final String SERVER_RELEASE = "http://carrier-api.wulianshuntong.com/";
    
    public static String getServerAddress() {
        return SERVER_DEV;
    }
    
    private ServerHost() {
        throw new RuntimeException("Can not invoke constructor!");
    }
    
}
