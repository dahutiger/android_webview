package com.example.webviewdemo.common.util;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntegerRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;

public class ResourcesUtil {
    
    private static Resources sResources = AppUtil.getResources();
    
    public static int getInteger(@IntegerRes int id) {
        return sResources.getInteger(id);
    }
    
    public static String getString(@StringRes int id) {
        return sResources.getString(id);
    }
    
    public static String getString(@StringRes int id, Object... formatArgs) {
        return sResources.getString(id, formatArgs);
    }
    
    public static int getColor(@ColorRes int id) {
        return ContextCompat.getColor(AppUtil.getContext(), id);
    }
    
    public static ColorStateList getColorStateList(@ColorRes int id) {
        return ContextCompat.getColorStateList(AppUtil.getContext(), id);
    }
    
    public static float getDimension(@DimenRes int id) {
        return sResources.getDimension(id);
    }
    
    public static int getDimensionPixelSize(@DimenRes int id) {
        return sResources.getDimensionPixelSize(id);
    }
    
    public static Drawable getDrawable(@DrawableRes int id) {
        return ContextCompat.getDrawable(AppUtil.getContext(), id);
    }
    
    public static Drawable getDrawableByName(String name) {
        int id = sResources.getIdentifier(name, "drawable", AppUtil.getPackageName());
        if (id == 0) {
            return null;
        }
        return getDrawable(id);
    }
    
    public static InputStream getAssets(String fileName) {
        try {
            return sResources.getAssets().open(fileName);
        } catch (IOException e) {
            LogUtil.e(e);
        }
        return null;
    }
    
}
