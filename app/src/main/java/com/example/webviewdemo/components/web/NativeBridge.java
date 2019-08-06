package com.example.webviewdemo.components.web;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;

import com.example.webviewdemo.common.constant.AppConstant;
import com.example.webviewdemo.common.util.LogUtil;
import com.example.webviewdemo.components.capture.PickPhotoActivity;
import com.tencent.smtt.sdk.WebView;

public class NativeBridge {
    
    private static final int RC_PIC = 0x1;
    public static final String NAME = "native_picture";
    
    private Activity activity;
    private String callback;
    
    private WebView webView;
    
    public NativeBridge(Activity activity, WebView webView) {
        this.activity = activity;
        this.webView = webView;
    }
    
    @JavascriptInterface
    public void capture(String callback) {
        this.callback = callback;
        PickPhotoActivity.launchForResultOnlyTakePhoto(activity, "receipt", null, 0, 0, RC_PIC);
    }
    
    @JavascriptInterface
    public void gallery(String callback) {
        this.callback = callback;
        PickPhotoActivity.launchForResultOnlyGallery(activity, "receipt", null, 0, 0, RC_PIC);
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_PIC && resultCode == Activity.RESULT_OK) {
            String url = data.getStringExtra(AppConstant.EXT_DATA);
            LogUtil.d("url = " + url);
            
            Uri uri = Uri.parse(url);
            LogUtil.d("url = " + uri);
            String js = "javascript:" + callback + "('" + uri + "')";
            LogUtil.d("js = " + js);
            webView.evaluateJavascript(js, null);
        }
    }
}
