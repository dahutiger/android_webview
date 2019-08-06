package com.example.webviewdemo.common.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.example.webviewdemo.R;
import com.example.webviewdemo.common.util.UiUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 通用对话框，使用同AlertDialog
 */
public class CommonDialog extends Dialog {
    
    @BindView(R.id.iv_icon)
    protected ImageView mIconView;
    @BindView(R.id.tv_title)
    protected TextView mTitleView;
    @BindView(R.id.message)
    protected TextView mMessageView;
    
    @BindView(R.id.btn_right)
    protected Button mButtonPositive;
    @BindView(R.id.btn_left)
    protected Button mButtonNegative;
    
    private CharSequence mButtonPositiveText;
    private OnClickListener mPositiveButtonListener;
    
    private CharSequence mButtonNegativeText;
    private OnClickListener mNegativeButtonListener;
    
    private int mIconId = 0;
    private Drawable mIcon;
    private CharSequence mTitle;
    private CharSequence mMessage;
    private View mView;
    private int mViewLayoutResId;
    private int mMessageGravity = Gravity.CENTER_HORIZONTAL;
    
    public CommonDialog(@NonNull Context context) {
        this(context, R.style.CommonDialog);
    }
    
    public CommonDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }
    
    protected CommonDialog(@NonNull Context context, boolean cancelable, @Nullable DialogInterface.OnCancelListener cancelListener) {
        this(context, R.style.CommonDialog);
        setCancelable(cancelable);
        setOnCancelListener(cancelListener);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.dialog_common);
        ButterKnife.bind(this);
        
        setupView();
    }
    
    @OnClick({R.id.btn_left, R.id.btn_right})
    protected void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_left:
                if (mNegativeButtonListener != null) {
                    mNegativeButtonListener.onClick(this, DialogInterface.BUTTON_NEGATIVE);
                }
                break;
            case R.id.btn_right:
                if (mPositiveButtonListener != null) {
                    mPositiveButtonListener.onClick(this, DialogInterface.BUTTON_POSITIVE);
                }
                break;
        }
        dismiss();
    }
    
    public void setIcon(int resId) {
        mIcon = null;
        mIconId = resId;
        if (mIconView != null) {
            if (resId != 0) {
                mIconView.setImageResource(mIconId);
            } else {
                mIconView.setVisibility(View.GONE);
            }
        }
    }
    
    public void setIcon(Drawable icon) {
        mIcon = icon;
        mIconId = 0;
        if (mIconView != null) {
            if (icon != null) {
                mIconView.setImageDrawable(icon);
            } else {
                mIconView.setVisibility(View.GONE);
            }
        }
    }
    
    public void setTitle(CharSequence title) {
        mTitle = title;
        if (mTitleView != null) {
            mTitleView.setText(title);
            setupContentMargin();
        }
    }
    
    public void setMessage(CharSequence message) {
        mMessage = message;
        if (mMessageView != null) {
            mMessageView.setText(message);
            setupContentMargin();
        }
    }
    
    public void setMessageGravity(int gravity) {
        mMessageGravity = gravity;
        if (mMessageView != null) {
            mMessageView.setGravity(gravity);
        }
    }
    
    /**
     * Set the view resource to display in the dialog.
     */
    public void setView(int layoutResId) {
        mView = null;
        mViewLayoutResId = layoutResId;
    }
    
    /**
     * Set the view to display in the dialog.
     */
    public void setView(View view) {
        mView = view;
        mViewLayoutResId = 0;
    }
    
    public void setButton(int whichButton, CharSequence text, DialogInterface.OnClickListener listener) {
        switch (whichButton) {
            case DialogInterface.BUTTON_POSITIVE:
                mButtonPositiveText = text;
                mPositiveButtonListener = listener;
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                mButtonNegativeText = text;
                mNegativeButtonListener = listener;
                break;
            default:
                throw new IllegalArgumentException("Button does not exist");
        }
    }
    
    public Button getButton(int whichButton) {
        switch (whichButton) {
            case DialogInterface.BUTTON_POSITIVE:
                return mButtonPositive;
            case DialogInterface.BUTTON_NEGATIVE:
                return mButtonNegative;
            default:
                return null;
        }
    }
    
    private void setupView() {
        setupPanel();
        
        final boolean hasButtons = setupButtons();
        final View buttonPanel = findViewById(R.id.buttonPanel);
        if (!hasButtons) {
            buttonPanel.setVisibility(View.GONE);
        }
        
        final LinearLayout contentPanel = findViewById(R.id.contentPanel);
        final FrameLayout customPanel = findViewById(R.id.customPanel);
        final View customView;
        if (mView != null) {
            customView = mView;
        } else if (mViewLayoutResId != 0) {
            final LayoutInflater inflater = LayoutInflater.from(getContext());
            customView = inflater.inflate(mViewLayoutResId, customPanel, false);
        } else {
            customView = null;
        }
        
        final boolean hasCustomView = customView != null;
        if (!hasCustomView || !canTextInput(customView)) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                    WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        }
        
        if (hasCustomView) {
            contentPanel.setVisibility(View.GONE);
            final FrameLayout custom = findViewById(R.id.custom);
            custom.addView(customView, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            customPanel.setVisibility(View.GONE);
            setupContent();
        }
    }
    
    private void setupPanel() {
        final LinearLayout parentPanel = findViewById(R.id.parentPanel);
        final Point point = new Point();
        getWindow().getWindowManager().getDefaultDisplay().getSize(point);
        
        ViewGroup.LayoutParams params = parentPanel.getLayoutParams();
        params.width = (int) (Math.min(point.x, point.y) * 0.75);
        parentPanel.setLayoutParams(params);
    }
    
    private void setupContent() {
        if (mIconId != 0) {
            mIconView.setImageResource(mIconId);
        } else if (mIcon != null) {
            mIconView.setImageDrawable(mIcon);
        } else {
            mIconView.setVisibility(View.GONE);
        }
        
        if (!TextUtils.isEmpty(mTitle)) {
            mTitleView.setText(mTitle);
        } else {
            mTitleView.setVisibility(View.GONE);
        }
        
        if (!TextUtils.isEmpty(mMessage)) {
            mMessageView.setText(mMessage);
            if (mMessageGravity != Gravity.NO_GRAVITY) {
                mMessageView.setGravity(mMessageGravity);
            }
        } else {
            mMessageView.setVisibility(View.GONE);
        }
        
        setupContentMargin();
    }
    
    private void setupContentMargin() {
        final boolean hasTitle = !TextUtils.isEmpty(mTitle);
        final boolean hasMessage = !TextUtils.isEmpty(mMessage);
        
        LinearLayout.LayoutParams params;
        if (hasMessage) {
            params = (LinearLayout.LayoutParams) mMessageView.getLayoutParams();
            params.topMargin = hasTitle ? UiUtil.dp2px(12) : 0;
            mMessageView.setLayoutParams(params);
            if (mMessageGravity == Gravity.NO_GRAVITY) {
                mMessageView.setGravity(mMessage.length() > 14 ? Gravity.START : Gravity.CENTER_HORIZONTAL);
            }
        }
    }
    
    private boolean setupButtons() {
        int BIT_BUTTON_POSITIVE = 1;
        int BIT_BUTTON_NEGATIVE = 2;
        
        int whichButtons = 0;
        
        if (TextUtils.isEmpty(mButtonPositiveText)) {
            mButtonPositive.setVisibility(View.GONE);
        } else {
            mButtonPositive.setText(mButtonPositiveText);
            mButtonPositive.setVisibility(View.VISIBLE);
            whichButtons = whichButtons | BIT_BUTTON_POSITIVE;
        }
        
        if (TextUtils.isEmpty(mButtonNegativeText)) {
            mButtonNegative.setVisibility(View.GONE);
        } else {
            mButtonNegative.setText(mButtonNegativeText);
            mButtonNegative.setVisibility(View.VISIBLE);
            
            whichButtons = whichButtons | BIT_BUTTON_NEGATIVE;
        }
        
        return whichButtons != 0;
    }
    
    static boolean canTextInput(View v) {
        if (v.onCheckIsTextEditor()) {
            return true;
        }
        
        if (!(v instanceof ViewGroup)) {
            return false;
        }
        
        ViewGroup vg = (ViewGroup) v;
        int i = vg.getChildCount();
        while (i > 0) {
            i--;
            v = vg.getChildAt(i);
            if (canTextInput(v)) {
                return true;
            }
        }
        
        return false;
    }
    
    private static class Params {
        
        private Context mContext;
        
        private int mIconId = 0;
        private Drawable mIcon;
        private CharSequence mTitle;
        private CharSequence mMessage;
        private int mMessageGravity = -1;
        
        private CharSequence mPositiveButtonText;
        private OnClickListener mPositiveButtonListener;
        
        private CharSequence mNegativeButtonText;
        private OnClickListener mNegativeButtonListener;
        
        private boolean mCancelable;
        private OnCancelListener mOnCancelListener;
        
        private OnDismissListener mOnDismissListener;
        private OnKeyListener mOnKeyListener;
        
        private View mView = null;
        private int mViewLayoutResId;
        
        Params(Context context) {
            mContext = context;
            mCancelable = true;
        }
        
        private void apply(CommonDialog dialog) {
            if (mIcon != null) {
                dialog.setIcon(mIcon);
            }
            if (mIconId >= 0) {
                dialog.setIcon(mIconId);
            }
            if (mTitle != null) {
                dialog.setTitle(mTitle);
            }
            if (mMessage != null) {
                dialog.setMessage(mMessage);
            }
            if (mMessageGravity > 0) {
                dialog.setMessageGravity(mMessageGravity);
            }
            if (mPositiveButtonText != null) {
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, mPositiveButtonText,
                        mPositiveButtonListener);
            }
            if (mNegativeButtonText != null) {
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE, mNegativeButtonText,
                        mNegativeButtonListener);
            }
            if (mView != null) {
                dialog.setView(mView);
            } else if (mViewLayoutResId != 0) {
                dialog.setView(mViewLayoutResId);
            }
        }
        
    }
    
    public static class Builder {
        
        private final Params P;
        
        public Builder(Context context) {
            P = new Params(context);
        }
        
        public Context getContext() {
            return P.mContext;
        }
        
        public CommonDialog.Builder setTitle(@StringRes int titleId) {
            return setTitle(P.mContext.getString(titleId));
        }
        
        public CommonDialog.Builder setTitle(CharSequence title) {
            P.mTitle = title;
            return this;
        }
        
        public CommonDialog.Builder setMessage(@StringRes int messageId) {
            P.mMessage = P.mContext.getText(messageId);
            return this;
        }
        
        public CommonDialog.Builder setMessage(CharSequence message) {
            P.mMessage = message;
            return this;
        }
        
        public CommonDialog.Builder setMessageGravity(int gravity) {
            P.mMessageGravity = gravity;
            return this;
        }
        
        public Builder setIcon(@DrawableRes int iconId) {
            P.mIconId = iconId;
            return this;
        }
        
        public Builder setIcon(Drawable icon) {
            P.mIcon = icon;
            return this;
        }
        
        public CommonDialog.Builder setPositiveButton(@StringRes int textId, final OnClickListener listener) {
            P.mPositiveButtonText = P.mContext.getText(textId);
            P.mPositiveButtonListener = listener;
            return this;
        }
        
        public CommonDialog.Builder setPositiveButton(CharSequence text, final OnClickListener listener) {
            P.mPositiveButtonText = text;
            P.mPositiveButtonListener = listener;
            return this;
        }
        
        public CommonDialog.Builder setNegativeButton(@StringRes int textId, final OnClickListener listener) {
            P.mNegativeButtonText = P.mContext.getText(textId);
            P.mNegativeButtonListener = listener;
            return this;
        }
        
        public CommonDialog.Builder setNegativeButton(CharSequence text, final OnClickListener listener) {
            P.mNegativeButtonText = text;
            P.mNegativeButtonListener = listener;
            return this;
        }
        
        public CommonDialog.Builder setCancelable(boolean cancelable) {
            P.mCancelable = cancelable;
            return this;
        }
        
        public CommonDialog.Builder setOnCancelListener(OnCancelListener onCancelListener) {
            P.mOnCancelListener = onCancelListener;
            return this;
        }
        
        public CommonDialog.Builder setOnDismissListener(OnDismissListener onDismissListener) {
            P.mOnDismissListener = onDismissListener;
            return this;
        }
        
        public CommonDialog.Builder setOnKeyListener(OnKeyListener onKeyListener) {
            P.mOnKeyListener = onKeyListener;
            return this;
        }
        
        public CommonDialog.Builder setView(int layoutResId) {
            P.mView = null;
            P.mViewLayoutResId = layoutResId;
            return this;
        }
        
        public CommonDialog.Builder setView(View view) {
            P.mView = view;
            P.mViewLayoutResId = 0;
            return this;
        }
        
        public CommonDialog create() {
            // Context has already been wrapped with the appropriate theme.
            final CommonDialog dialog = new CommonDialog(P.mContext);
            P.apply(dialog);
            dialog.setCancelable(P.mCancelable);
            if (P.mCancelable) {
                dialog.setCanceledOnTouchOutside(true);
            }
            dialog.setOnCancelListener(P.mOnCancelListener);
            dialog.setOnDismissListener(P.mOnDismissListener);
            if (P.mOnKeyListener != null) {
                dialog.setOnKeyListener(P.mOnKeyListener);
            }
            return dialog;
        }
        
        public CommonDialog show() {
            final CommonDialog dialog = create();
            dialog.show();
            return dialog;
        }
    }
    
    
}
