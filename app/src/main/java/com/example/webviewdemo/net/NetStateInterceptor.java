package com.example.webviewdemo.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class NetStateInterceptor implements Interceptor {
    
    private ConnectivityManager mConnectivityManager;
    
    public NetStateInterceptor(Context context) {
        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        if (isConnected()) {
            return chain.proceed(chain.request());
        }
        throw new ResponseException();
    }
    
    public boolean isConnected() {
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable();
    }
    
}
