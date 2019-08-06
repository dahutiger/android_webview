package com.example.webviewdemo.common.util;

import android.content.Context;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class UiUtil {

    private UiUtil() {
    }

    public static void setVisible(View... views) {
        setVisibility(View.VISIBLE, views);
    }

    public static void setGone(View... views) {
        setVisibility(View.GONE, views);
    }

    public static void setInvisible(View... views) {
        setVisibility(View.INVISIBLE, views);
    }

    private static void setVisibility(int visibility, View... views) {
        for (View view : views) {
            if (view != null && view.getVisibility() != visibility) {
                view.setVisibility(visibility);
            }
        }
    }

    public static boolean isVisible(View view) {
        return view != null && view.getVisibility() == View.VISIBLE;
    }

    public static boolean isGone(View view) {
        return view != null && view.getVisibility() == View.GONE;
    }

    public static int dp2px(float dp) {
        return ScreenUtil.dp2px(dp);
    }

    /**
     * 隐藏键盘
     *
     * @param view
     */
    public static void hideSoftInput(View view) {
        if (view == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) AppUtil.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void runOnUiThread(Runnable run) {
        if (isRunInMainThread()) {
            run.run();
        } else {
            AppUtil.getMainHandler().post(run);
        }
    }

    public static boolean isRunInMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

}
