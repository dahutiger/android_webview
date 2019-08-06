package com.example.webviewdemo.common.util;

import com.example.webviewdemo.common.constant.NetConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * 网络工具类
 */
public class NetUtil {
    
    private static final Map<String, String> HEADERS = new HashMap<>();
    
    static {
        HEADERS.put(NetConstant.OSNAME, NetConstant.ANDROID);
        HEADERS.put(NetConstant.OSVERSION, DeviceUtil.getSystemVersion());
        
        HEADERS.put(NetConstant.PRODUCT, DeviceUtil.getProduct());
        HEADERS.put(NetConstant.MANUFACTURER, DeviceUtil.getManufacturer());
        HEADERS.put(NetConstant.BRAND, DeviceUtil.getBrand());
        HEADERS.put(NetConstant.MODEL, DeviceUtil.getModel());
        
        HEADERS.put(NetConstant.VERSION, String.valueOf(AppUtil.getVersionCode()));
    }
    
    public static Map<String, String> getHeaders() {
        return HEADERS;
    }
    
}
