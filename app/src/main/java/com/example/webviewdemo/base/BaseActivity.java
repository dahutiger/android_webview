package com.example.webviewdemo.base;

import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.webviewdemo.R;
import com.example.webviewdemo.common.util.RxJavaUtil;
import com.gyf.immersionbar.ImmersionBar;
import com.uber.autodispose.AutoDisposeConverter;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {
    
    protected <T> AutoDisposeConverter<T> bindLifecycle() {
        return RxJavaUtil.bindLifecycle(this);
    }
    
    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initImmersionBar();
        ButterKnife.bind(this);
    }
    
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initImmersionBar();
        ButterKnife.bind(this);
    }
    
    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        initImmersionBar();
        ButterKnife.bind(this);
    }
    
    private void initImmersionBar() {
        ImmersionBar
                .with(this)
                .statusBarDarkFont(true)
                .statusBarColor(R.color.white)
                .fitsSystemWindows(true)
                .init();
    }
}
