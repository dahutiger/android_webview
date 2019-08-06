package com.example.webviewdemo.components.web;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.example.webviewdemo.R;
import com.example.webviewdemo.base.BaseActivity;
import com.example.webviewdemo.common.util.LogUtil;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import butterknife.BindView;

public class WebViewActivity extends BaseActivity implements IWebViewListener {

    private static final String URL = "url";

    private static final String APP_CACHE_PATH = "appcache";
    private static final String DATABASE_PATH = "databases";
    private static final String GEO_LOCATION_DATABASE_PATH = "geolocation";

    @BindView(R.id.webView)
    protected WebView mWebView;

    private WebChromeClientImpl mWebChromeClient;
    private NativeBridge nativeBridge;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mWebChromeClient != null) {
            mWebChromeClient.onActivityResult(requestCode, resultCode, data);
        }
        if (nativeBridge != null) {
            nativeBridge.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        final String url = getIntent().getStringExtra(URL);

        initView();
        loadUrl(url);
    }

    private void initView() {
        mWebChromeClient = new WebChromeClientImpl(this);
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.setWebViewClient(new WebViewClientImpl(this));
    
        nativeBridge = new NativeBridge(this, mWebView);
        mWebView.addJavascriptInterface(nativeBridge, NativeBridge.NAME);

        // 相关设置
        WebSettings webSetting = mWebView.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(getDir(APP_CACHE_PATH, 0).getPath());
        webSetting.setDatabasePath(getDir(DATABASE_PATH, 0).getPath());
        webSetting.setGeolocationDatabasePath(getDir(GEO_LOCATION_DATABASE_PATH, 0).getPath());
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);

        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }

    private void loadUrl(String url) {
        mWebView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onPause() {
        mWebView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        try {
            // 先清空，再删除
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            ViewGroup parent = (ViewGroup) mWebView.getParent();
            parent.removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        } catch (Exception e) {
            LogUtil.e(e);
        }
        super.onDestroy();
    }

    @Override
    public void onPageStarted(WebView webView, String url) {

    }

    @Override
    public void onPageFinished(WebView webView, String url) {

    }

    @Override
    public void onReceivedTitle(String title) {

    }

    public static void start(Context context, String url) {
        Intent starter = new Intent(context, WebViewActivity.class);
        starter.putExtra(URL, url);
        context.startActivity(starter);
    }

}
