package com.example.webviewdemo.common.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class ScreenUtil {
    
    private static int screenWidth;
    private static int screenHeight;
    private static float density;
    
    private ScreenUtil() {
    }
    
    public static void init(Context context) {
        if (context == null) {
            return;
        }
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        density = dm.density;
    }
    
    public static int screenWidth() {
        return screenWidth;
    }
    
    public static int screenHeight() {
        return screenHeight;
    }
    
    public static int dp2px(float dp) {
        return (int) (dp * density + 0.5f);
    }
    
    public static int px2dp(float pxValue) {
        return (int) (pxValue / density + 0.5f);
    }
    
}
