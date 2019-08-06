package com.example.webviewdemo.components;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.webviewdemo.R;
import com.example.webviewdemo.base.BaseActivity;
import com.example.webviewdemo.common.util.AppUtil;
import com.example.webviewdemo.common.util.DialogUtil;
import com.example.webviewdemo.common.util.NetUtil;
import com.example.webviewdemo.common.util.PermissionUtil;
import com.example.webviewdemo.net.RetrofitHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class LauncherActivity extends BaseActivity {
    
    private static final int RC_PERMISSION = 1;
    
    private static final long LAUNCHER_TIME = 2000;
    
    private Runnable mEnterApp = () -> {
        toMain();
    };
    
    private Handler mHandler = new Handler();
    private long mStartTime;
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED && PermissionUtil.isRuntime(permissions[i])) {
                showPermissionDeniedDialog();
                return;
            }
        }
        allPermissionGranted();
    }
    
    @BindView(R.id.launch_view)
    protected ImageView launchView;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        
        List<String> permissions = PermissionUtil.getPermissions();
        List<String> denied = new ArrayList<>();
        for (String permission : permissions) {
            if (!PermissionUtil.isGranted(permission)) {
                denied.add(permission);
            }
        }
        
        if (denied.size() > 0) {
            String[] deniedPermissions = new String[denied.size()];
            denied.toArray(deniedPermissions);
            ActivityCompat.requestPermissions(this, deniedPermissions, RC_PERMISSION);
        } else {
            allPermissionGranted();
        }
        
    }
    
    @Override
    public void onBackPressed() {
        // 不允许返回
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mEnterApp);
    }
    
    private void allPermissionGranted() {
        mStartTime = System.currentTimeMillis();
        RetrofitHelper.getInstance().addHeaders(NetUtil.getHeaders());
        afterUpdate();
    }
    
    private void afterUpdate() {
        final long interval = System.currentTimeMillis() - mStartTime;
        if (interval > LAUNCHER_TIME) {
            mEnterApp.run();
        } else {
            mHandler.postDelayed(mEnterApp, LAUNCHER_TIME - interval);
        }
    }
    
    private void toMain() {
        startActivity(new Intent(LauncherActivity.this, MainActivity.class));
        finish();
    }
    
    private void showPermissionDeniedDialog() {
        DialogUtil.showIconDialog(this, R.drawable.icon_dialog_prompt,
                getString(R.string.prompt), getString(R.string.permission_denied),
                getString(R.string.exit), getString(R.string.goto_setting),
                new DialogUtil.DialogCallBack() {
                    @Override
                    public void onClickLeftBtn(DialogInterface dialog) {
                        finish();
                    }
                    
                    @Override
                    public void onClickRightBtn(DialogInterface dialog) {
                        AppUtil.startSettingActivity(LauncherActivity.this);
                        finish();
                    }
                });
    }
    
}
