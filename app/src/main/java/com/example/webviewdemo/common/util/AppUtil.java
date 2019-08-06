package com.example.webviewdemo.common.util;

import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.List;

public class AppUtil {
    
    private static Context mContext;
    private static Handler sMainHandler;
    
    private AppUtil() {
    }
    
    public static void init(Context context) {
        if (context == null) {
            throw new NullPointerException("Context is null");
        }
        mContext = context.getApplicationContext();
        ScreenUtil.init(mContext);
    }
    
    public static Handler getMainHandler() {
        if (sMainHandler == null) {
            synchronized (AppUtil.class) {
                if (sMainHandler == null) {
                    sMainHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
        return sMainHandler;
    }
    
    public static Context getContext() {
        return mContext;
    }
    
    public static Object getSystemService(@NonNull String name) {
        return mContext.getSystemService(name);
    }
    
    public static Resources getResources() {
        return mContext.getResources();
    }
    
    public static String getPackageName() {
        return mContext.getPackageName();
    }
    
    /**
     * 获取版本名
     */
    public static String getVersionName() {
        return getVersionName(getPackageName());
    }
    
    /**
     * 获取版本名
     */
    public static String getVersionName(final String packageName) {
        if (!TextUtils.isEmpty(packageName)) {
            try {
                PackageManager pm = mContext.getPackageManager();
                PackageInfo pi = pm.getPackageInfo(packageName, 0);
                return pi == null ? null : pi.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
    
    /**
     * 获取版本号
     */
    public static int getVersionCode() {
        return getVersionCode(getPackageName());
    }
    
    /**
     * 获取版本号
     */
    public static int getVersionCode(final String packageName) {
        if (!TextUtils.isEmpty(packageName)) {
            try {
                PackageManager pm = mContext.getPackageManager();
                PackageInfo pi = pm.getPackageInfo(packageName, 0);
                return pi == null ? -1 : pi.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
    
    /**
     * 是否为主进程
     *
     * @return
     */
    public static boolean isMainProcess() {
        android.app.ActivityManager am = ((android.app.ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (android.app.ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 关闭app，并清除数据
     */
    public static void closeApp() {
        ActivityManager.getInstance().finishAllActivity();
    }
    
    public static void installApk(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
        getContext().startActivity(intent);
    }
    
    public static void startSettingActivity(Context context) {
        Intent detailIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        detailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        detailIntent.setData(uri);
        context.startActivity(detailIntent);
    }
    
}
