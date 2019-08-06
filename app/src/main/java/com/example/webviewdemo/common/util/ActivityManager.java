package com.example.webviewdemo.common.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * activity管理类，方便进行Activity的获取及销毁
 */
public class ActivityManager implements Application.ActivityLifecycleCallbacks {
    
    private static final ActivityManager INSTANCE = new ActivityManager();
    
    private final List<Activity> mActivityList = new ArrayList<>();
    private WeakReference<Activity> mCurrentActivityRef;
    
    private ActivityManager() {
    }
    
    public static ActivityManager getInstance() {
        return INSTANCE;
    }
    
    public void finishAllActivity() {
        for (Activity activity : mActivityList) {
            if (activity != null) {
                activity.finish();
            }
        }
    }
    
    public Activity getCurrentActivity() {
        if (mCurrentActivityRef != null && mCurrentActivityRef.get() != null) {
            return mCurrentActivityRef.get();
        }
        return null;
    }
    
    public Activity getBaseActivity() {
        if (!mActivityList.isEmpty()) {
            return mActivityList.get(0);
        }
        return null;
    }
    
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        synchronized (mActivityList) {
            mActivityList.add(activity);
        }
    }
    
    @Override
    public void onActivityStarted(Activity activity) {
    
    }
    
    @Override
    public void onActivityResumed(Activity activity) {
        mCurrentActivityRef = new WeakReference<>(activity);
    }
    
    @Override
    public void onActivityPaused(Activity activity) {
    
    }
    
    @Override
    public void onActivityStopped(Activity activity) {
    
    }
    
    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    
    }
    
    @Override
    public void onActivityDestroyed(Activity activity) {
        synchronized (mActivityList) {
            mActivityList.remove(activity);
        }
    }
}
