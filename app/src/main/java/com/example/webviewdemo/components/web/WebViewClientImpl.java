package com.example.webviewdemo.components.web;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class WebViewClientImpl extends WebViewClient {

    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static final String FILE = "file";

    private IWebViewListener mListener;

    public WebViewClientImpl(IWebViewListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener must not be null");
        }
        mListener = listener;
    }

    @Override
    public void onPageStarted(WebView webView, String url, Bitmap bitmap) {
        super.onPageStarted(webView, url, bitmap);
        mListener.onPageStarted(webView, url);
    }

    @Override
    public void onPageFinished(WebView webView, String url) {
        super.onPageFinished(webView, url);
        mListener.onPageFinished(webView, url);
        mListener.onReceivedTitle(webView.getTitle());
    }

    @Override
    public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
        sslErrorHandler.proceed();
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String url) {
        return onInterceptUrl(webView.getContext(), url);
    }

    private boolean onInterceptUrl(Context context, String url) {
        if (TextUtils.isEmpty(url)) {
            return true;
        }

        Uri uri = Uri.parse(url);
        String scheme = uri.getScheme();
        if (TextUtils.isEmpty(scheme)) {
            return true;
        }

        Intent intent = null;
        scheme = scheme.toLowerCase();
        switch (scheme) {
            case HTTP:
            case HTTPS:
            case FILE:
                break;
            default:
                intent = new Intent(Intent.ACTION_VIEW, uri);
                break;
        }

        if (intent != null) {
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                mListener.startActivity(intent);
            }
            return true;
        }

        return false;
    }

}
