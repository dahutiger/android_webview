package com.example.webviewdemo.common.util;

import android.os.Build;
import android.text.TextUtils;

/**
 * 系统工具类
 */
public class DeviceUtil {
    
    /**
     * 产品名称
     *
     * @return
     */
    public static String getProduct() {
        return Build.PRODUCT;
    }
    
    /**
     * 制造商
     *
     * @return
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }
    
    /**
     * 品牌
     *
     * @return
     */
    public static String getBrand() {
        return Build.BRAND;
    }
    
    /**
     * 型号
     *
     * @return
     */
    public static String getModel() {
        return Build.MODEL;
    }
    
    public static String[] getABIs() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return Build.SUPPORTED_ABIS;
        } else {
            if (!TextUtils.isEmpty(Build.CPU_ABI2)) {
                return new String[]{Build.CPU_ABI, Build.CPU_ABI2};
            }
            return new String[]{Build.CPU_ABI};
        }
    }
    
    /**
     * 系统版本
     *
     * @return
     */
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }
    
}
