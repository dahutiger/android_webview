package com.example.webviewdemo.net.response;

import com.example.webviewdemo.net.BaseObserver;

public abstract class ResponseObserver<T> extends BaseObserver<ResponseData<T>> {
    
    @Override
    public final void onNext(ResponseData<T> responseData) {
//        if (responseData.getCode() == ResponseCode.SUCCESS) {
//            onSuccess(responseData);
//        } else {
//            onError(new ResponseException(responseData.getCode(), responseData.getMsg()));
//        }
        onSuccess(responseData);
    }
    
    protected abstract void onSuccess(ResponseData<T> responseData);
}
