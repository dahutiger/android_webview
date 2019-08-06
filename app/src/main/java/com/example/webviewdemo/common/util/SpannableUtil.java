package com.example.webviewdemo.common.util;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.widget.EditText;

public class SpannableUtil {
    
    public static CharSequence makeBoldSpannable(String text, int start, int end) {
        if (TextUtils.isEmpty(text) || start > text.length() || end > text.length() || start >= end) {
            return text;
        }
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    /**
     * 给输入框的默认文字设置不同的字号
     *
     * @param et
     * @param res
     */
    public static void setHint(EditText et, String res) {
        SpannableString ss = new SpannableString(res);
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(15, true);
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        et.setHint(new SpannedString(ss));
    }
    
}
