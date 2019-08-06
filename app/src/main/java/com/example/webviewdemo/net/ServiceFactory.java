package com.example.webviewdemo.net;

import java.util.HashMap;

/**
 * 用于存储接口对象，避免多次创建
 */
public class ServiceFactory {
    
    private static HashMap<String, Object> sServiceMap = new HashMap<>();
    
    public static <S> S getService(Class<S> clazz) {
        String key = clazz.getName();
        S service;
        if (sServiceMap.containsKey(key)) {
            service = (S) sServiceMap.get(key);
        } else {
            service = RetrofitHelper.getInstance().createService(clazz);
            sServiceMap.put(key, service);
        }
        return service;
    }
    
}
