package com.example.webviewdemo.net.progress;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;

import com.example.webviewdemo.common.widget.CustomProgressDialog;

import java.lang.ref.WeakReference;

public class ProgressDialogHandlerImpl implements IProgressDialogHandler {
    
    private static final int DELAY_MILLIS = 6000;
    
    private Runnable mDelayCancel = new Runnable() {
        @Override
        public void run() {
            if (mProgressDialog != null) {
                mProgressDialog.setOnCancelListener(mOnCancelListener);
                mProgressDialog.setCancelable(true);
                mProgressDialog.setCanceledOnTouchOutside(true);
            }
        }
    };
    
    private WeakReference<Context> mContextRef;
    private ProgressDialog mProgressDialog;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private DialogInterface.OnCancelListener mOnCancelListener;
    
    public ProgressDialogHandlerImpl(Context context) {
        mContextRef = new WeakReference<>(context);
    }
    
    protected ProgressDialog createProgressDialog(Context context) {
        return new CustomProgressDialog(context);
    }
    
    @Override
    public void showProgressDialog(DialogInterface.OnCancelListener listener) {
        mHandler.removeCallbacks(mDelayCancel);
        mOnCancelListener = listener;
        if (mProgressDialog == null) {
            Context context = mContextRef.get();
            if (context != null) {
                mProgressDialog = createProgressDialog(context);
            }
        }
        
        if (mProgressDialog != null) {
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            mHandler.postDelayed(mDelayCancel, DELAY_MILLIS);
        }
    }
    
    @Override
    public void dismissProgressDialog() {
        mHandler.removeCallbacks(mDelayCancel);
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = null;
    }
}
