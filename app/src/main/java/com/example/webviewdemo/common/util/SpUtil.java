package com.example.webviewdemo.common.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * SharedPreferences的封装类
 */
public class SpUtil {
    
    private static SharedPreferences sSharedPref = null;
    
    private static SharedPreferences getSharedPref() {
        if (sSharedPref == null) {
            synchronized (SpUtil.class) {
                if (sSharedPref == null) {
                    sSharedPref = AppUtil.getContext().getSharedPreferences(AppUtil.getPackageName(), Context.MODE_PRIVATE);
                }
            }
        }
        return sSharedPref;
    }
    
    public static void clear() {
        SharedPreferences.Editor editor = getSharedPref().edit();
        editor.clear();
        editor.apply();
    }
    
    public static void remove(String key) {
        SharedPreferences.Editor editor = getSharedPref().edit();
        editor.remove(key);
        editor.apply();
    }
    
    public static void putString(String key, String value) {
        SharedPreferences.Editor editor = getSharedPref().edit();
        editor.putString(key, value);
        editor.apply();
    }
    
    public static String getString(String key, String defValue) {
        return getSharedPref().getString(key, defValue);
    }
    
    public static void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPref().edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
    
    public static boolean getBoolean(String key, boolean defValue) {
        return getSharedPref().getBoolean(key, defValue);
    }
    
    public static void putInt(String key, int value) {
        SharedPreferences.Editor editor = getSharedPref().edit();
        editor.putInt(key, value);
        editor.apply();
    }
    
    public static void putLong(String key, long value) {
        SharedPreferences.Editor editor = getSharedPref().edit();
        editor.putLong(key, value);
        editor.apply();
    }
    
    public static int getInt(String key, int defValue) {
        return getSharedPref().getInt(key, defValue);
    }
    
    public static void putFloat(String key, float value) {
        SharedPreferences.Editor editor = getSharedPref().edit();
        editor.putFloat(key, value);
        editor.apply();
    }
    
    public static float getFloat(String key, float defValue) {
        return getSharedPref().getFloat(key, defValue);
    }
    
    public static long getLong(String key, long defValue) {
        return getSharedPref().getLong(key, defValue);
    }
    
    /**
     * 用于保存整个对象
     *
     * @param key
     * @param obj
     */
    public static void putObject(String key, Object obj) {
        putString(key, obj == null ? null : SerializeUtil.obj2String(obj));
    }
    
    /**
     * 读取整个对象
     *
     * @param key
     * @return
     */
    public static Object getObject(String key) {
        String str = getString(key, null);
        if (!TextUtils.isEmpty(str)) {
            try {
                return SerializeUtil.string2Obj(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    public static void commit() {
        SharedPreferences.Editor editor = getSharedPref().edit();
        editor.commit();
    }
    
}
