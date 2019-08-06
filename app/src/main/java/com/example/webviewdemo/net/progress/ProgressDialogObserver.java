package com.example.webviewdemo.net.progress;

import android.content.Context;
import android.content.DialogInterface;

import com.example.webviewdemo.net.response.ResponseObserver;

public abstract class ProgressDialogObserver<T> extends ResponseObserver<T> implements DialogInterface.OnCancelListener {
    
    private IProgressDialogHandler mHandler;
    
    public ProgressDialogObserver() {
    }
    
    public ProgressDialogObserver(Context context) {
        this(new ProgressDialogHandlerImpl(context));
    }
    
    public ProgressDialogObserver(IProgressDialogHandler handler) {
        mHandler = handler;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        showProgressDialog();
    }
    
    @Override
    protected void onFinally() {
        super.onFinally();
        dismissProgressDialog();
    }
    
    private void showProgressDialog() {
        if (mHandler != null) {
            mHandler.showProgressDialog(this);
        }
    }
    
    private void dismissProgressDialog() {
        if (mHandler != null) {
            mHandler.dismissProgressDialog();
        }
    }
    
    @Override
    public void onCancel(DialogInterface dialogInterface) {
        if (!isDisposed()) {
            dispose();
        }
    }
}
