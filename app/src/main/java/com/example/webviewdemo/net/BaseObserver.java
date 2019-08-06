package com.example.webviewdemo.net;

import com.example.webviewdemo.BuildConfig;
import com.example.webviewdemo.common.util.ToastUtil;

import io.reactivex.observers.DisposableObserver;

/**
 * Response结果订阅
 */

public abstract class BaseObserver<T> extends DisposableObserver<T> {
    
    @Override
    public final void onComplete() {
        onFinally();
        if (!isDisposed()) {
            dispose();
        }
    }
    
    @Override
    public final void onError(Throwable e) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace();
        }
        if (!(e instanceof ResponseException)) {
            e.printStackTrace();
            e = new ResponseException();
        }
        
        ResponseException exception = (ResponseException) e;
        onFailure(exception);
        onFinally();
        if (!isDisposed()) {
            dispose();
        }
    }
    
    protected void onFailure(ResponseException e) {
        ToastUtil.showShort(e.getMsg());
    }
    
    protected void onFinally() {
    
    }
    
}
