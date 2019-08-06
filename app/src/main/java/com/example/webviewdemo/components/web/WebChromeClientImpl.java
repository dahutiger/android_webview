package com.example.webviewdemo.components.web;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;

import com.example.webviewdemo.R;
import com.example.webviewdemo.common.widget.CommonDialog;
import com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

public class WebChromeClientImpl extends WebChromeClient {

    private static final int RC_CHOOSE_FILE = 0x1234;

    private ValueCallback<Uri> mUploadFile;
    private ValueCallback<Uri[]> mUploadFiles;

    private IWebViewListener mListener;

    public WebChromeClientImpl(IWebViewListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener must not be null");
        }
        mListener = listener;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_CHOOSE_FILE) {
            if (mUploadFiles != null) {
                Uri[] uris = null;
                if (data != null && resultCode == Activity.RESULT_OK) {
                    String dataString = data.getDataString();
                    ClipData clipData = data.getClipData();
                    if (clipData != null) {
                        uris = new Uri[clipData.getItemCount()];
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            uris[i] = clipData.getItemAt(i).getUri();
                        }
                    }
                    if (dataString != null) {
                        uris = new Uri[]{Uri.parse(dataString)};
                    }
                }
                mUploadFiles.onReceiveValue(uris);
                mUploadFiles = null;
            } else if (mUploadFile != null) {
                Uri uri = (data == null || resultCode != Activity.RESULT_OK) ? null : data.getData();
                mUploadFile.onReceiveValue(uri);
                mUploadFile = null;
            }
        }
    }

    @Override
    public void onReceivedTitle(WebView webView, String title) {
        mListener.onReceivedTitle(title);
    }

    @Override
    public boolean onJsAlert(WebView webView, String url, String message, final JsResult result) {
        new CommonDialog.Builder(webView.getContext())
                .setTitle(R.string.prompt)
                .setMessage(message)
                .setPositiveButton(R.string.confirm, null)
                .setOnDismissListener(dialog -> result.confirm())
                .show();
        return true;
    }

    @Override
    public boolean onJsConfirm(WebView webView, String url, String message, final JsResult result) {
        new CommonDialog.Builder(webView.getContext())
                .setTitle(R.string.prompt)
                .setMessage(message)
                .setNegativeButton(R.string.cancel, (dialog, which) -> result.cancel())
                .setPositiveButton(R.string.confirm, (dialog, which) -> result.confirm())
                .setOnCancelListener(dialog -> result.cancel())
                .show();
        return true;
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissionsCallback callback) {
        callback.invoke(origin, true, false);
        super.onGeolocationPermissionsShowPrompt(origin, callback);
    }

    // For Android  > 4.1.1
    public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
        mUploadFile = uploadFile;
        openFileChooserActivity();
    }

    // For Android  >= 5.0
    public boolean onShowFileChooser(com.tencent.smtt.sdk.WebView webView,
                                     ValueCallback<Uri[]> filePathCallback,
                                     WebChromeClient.FileChooserParams fileChooserParams) {
        mUploadFiles = filePathCallback;
        openFileChooserActivity();
        return true;
    }

    private void openFileChooserActivity(String... acceptTypes) {
        String acceptType = "image/*";
        if (acceptTypes != null && acceptTypes.length > 0) {
            acceptType = acceptTypes[0];
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(acceptType);
        mListener.startActivityForResult(Intent.createChooser(intent, "Chooser"), RC_CHOOSE_FILE);
    }

}
