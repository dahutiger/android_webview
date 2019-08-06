package com.example.webviewdemo.common.util;

import android.content.Context;
import android.os.Environment;

import com.example.webviewdemo.net.BaseObserver;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * description 本地图片管理类，主要用于拍照之后的压缩图片的管理
 */
public class PictureManager {
    
    private static final PictureManager INSTANCE = new PictureManager();
    
    private PictureManager() {
    }
    
    public static PictureManager getInstance() {
        return INSTANCE;
    }
    
    public File getCompressFilePath(Context context) {
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }
    
    public void asyncDelete(final Context context) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                File path = getCompressFilePath(context);
                if (path.exists() && path.isDirectory()) {
                    File[] files = getCompressFilePath(context).listFiles();
                    for (File file : files) {
                        if (file.exists() && file.isFile()) {
                            LogUtil.d("file ===" + file.getPath());
                            file.delete();
                        }
                    }
                }
                emitter.onNext("");
                emitter.onComplete();
            }
        })
                .compose(RxJavaUtil.<String>mainSchedulers())
                .subscribe(new BaseObserver<String>() {
                    @Override
                    public void onNext(String str) {
                    }
                });
    }
    
}
