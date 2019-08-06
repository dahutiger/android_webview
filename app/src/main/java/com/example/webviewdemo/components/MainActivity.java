package com.example.webviewdemo.components;

import android.os.Bundle;

import com.example.webviewdemo.R;
import com.example.webviewdemo.base.BaseActivity;
import com.example.webviewdemo.components.web.WebViewActivity;

public class MainActivity extends BaseActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebViewActivity.start(this, "file:///android_asset/index.html");
    }
    
}
