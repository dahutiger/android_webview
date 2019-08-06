package com.example.webviewdemo.components.web;

import android.content.Intent;

import com.tencent.smtt.sdk.WebView;

public interface IWebViewListener {

    void onPageStarted(WebView webView, String url);

    void onPageFinished(WebView webView, String url);

    void onReceivedTitle(String title);

    void startActivity(Intent intent);

    void startActivityForResult(Intent intent, int requestCode);

}
