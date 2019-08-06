package com.example.webviewdemo.net.progress;

import android.content.DialogInterface;

public interface IProgressDialogHandler {
    
    void showProgressDialog(DialogInterface.OnCancelListener listener);
    
    void dismissProgressDialog();
    
}
