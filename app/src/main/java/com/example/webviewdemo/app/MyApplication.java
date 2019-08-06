package com.example.webviewdemo.app;

import android.app.Application;
import android.os.StrictMode;

import com.example.webviewdemo.common.constant.ServerHost;
import com.example.webviewdemo.common.util.ActivityManager;
import com.example.webviewdemo.common.util.AppUtil;
import com.example.webviewdemo.common.util.LogUtil;
import com.example.webviewdemo.common.util.NetUtil;
import com.example.webviewdemo.net.RetrofitHelper;
import com.tencent.smtt.sdk.QbSdk;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        onApplicationCreate(this);
    }

    private void onApplicationCreate(Application application) {
        initialize(application);

        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        initX5Environment();
    }

    private void initialize(Application application) {
        AppUtil.init(application);
        RetrofitHelper.getInstance().init(application, ServerHost.getServerAddress());
        RetrofitHelper.getInstance().addHeaders(NetUtil.getHeaders());

        // 这个方法体里放一些需要只在主进程中做初始化的组件（第三方库，统计等）
        if (AppUtil.isMainProcess()) {
        
        }

        application.registerActivityLifecycleCallbacks(ActivityManager.getInstance());
    }

    private void initX5Environment() {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean result) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                LogUtil.d(" onViewInitFinished is " + result);
            }

            @Override
            public void onCoreInitFinished() {
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }
    

}
