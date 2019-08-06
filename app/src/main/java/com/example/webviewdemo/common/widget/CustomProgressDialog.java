package com.example.webviewdemo.common.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.example.webviewdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 自定义的加载等待框
 */
public class CustomProgressDialog extends ProgressDialog {

    @BindView(R.id.tv_message)
    protected TextView tvMessage;

    public CustomProgressDialog(Context context) {
        this(context, R.style.CustomProgressDialog);
    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
        getWindow().setGravity(Gravity.CENTER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_dialog_custom);
        ButterKnife.bind(this);
    }

    public void setContentText(@StringRes int resId) {
        if (!isShowing()) {
            show();
        }
        tvMessage.setText(resId);
    }

}
