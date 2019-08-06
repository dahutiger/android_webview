package com.example.webviewdemo.common.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;

import com.example.webviewdemo.common.widget.CommonDialog;

public class DialogUtil {
    
    public static class DialogCallBack {
        
        public void onClickLeftBtn(DialogInterface dialog) {
        
        }
        
        public void onClickRightBtn(DialogInterface dialog) {
        
        }
        
    }
    
    /**
     * 显示带图标的对话框
     *
     * @param activity
     * @param iconId
     * @param title
     * @param message
     * @param leftBtn
     * @param rightBtn
     * @param callBack
     * @return
     */
    public static CommonDialog showIconDialog(Activity activity, int iconId, CharSequence title, CharSequence message,
                                              CharSequence leftBtn, CharSequence rightBtn, final DialogCallBack callBack) {
        return showIconDialog(activity, iconId, title, message, Gravity.NO_GRAVITY, leftBtn, rightBtn, callBack);
    }
    
    /**
     * 显示带图标的对话框
     *
     * @param activity
     * @param iconId
     * @param title
     * @param message
     * @param messageGravity // Message对齐方式
     * @param leftBtn
     * @param rightBtn
     * @param callBack
     * @return
     */
    public static CommonDialog showIconDialog(Activity activity, int iconId, CharSequence title, CharSequence message, int messageGravity,
                                              CharSequence leftBtn, CharSequence rightBtn, final DialogCallBack callBack) {
        CommonDialog.Builder builder = new CommonDialog.Builder(activity);
        builder.setIcon(iconId);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setMessageGravity(messageGravity);
        
        if (!TextUtils.isEmpty(leftBtn)) {
            builder.setNegativeButton(leftBtn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (callBack != null) {
                        callBack.onClickLeftBtn(dialog);
                    }
                }
            });
        }
        if (!TextUtils.isEmpty(rightBtn)) {
            builder.setPositiveButton(rightBtn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (callBack != null) {
                        callBack.onClickRightBtn(dialog);
                    }
                }
            });
        }
        return builder.show();
    }
    
}
