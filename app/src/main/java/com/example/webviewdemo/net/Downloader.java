package com.example.webviewdemo.net;

import com.example.webviewdemo.common.util.FileIOUtil;
import com.example.webviewdemo.net.response.ProgressResponseBody;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 下载器
 */
public class Downloader {
    
    public interface DownloadListener {
        
        void onProgress(int progress);
        
        void onDownloadFinished(String path);
        
        void onDownloadFailed(String error);
        
    }
    
    public static void download(final String url, final String destPath, final DownloadListener listener) throws Exception {
        final ProgressResponseBody.ProgressListener progressListener = new ProgressResponseBody.ProgressListener() {
            @Override
            public void update(long bytesRead, long contentLength, boolean done) {
                if (listener != null) {
                    listener.onProgress((int) (bytesRead * 100 / contentLength));
                }
            }
        };
        
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response originalResponse = chain.proceed(chain.request());
                        return originalResponse.newBuilder()
                                .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                                .build();
                    }
                })
                .build();
        
        Request request = new Request.Builder()
                .url(url)
                .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (listener != null) {
                    listener.onDownloadFailed("下载失败！");
                }
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                boolean success = FileIOUtil.writeFileFromIS(destPath, response.body().byteStream());
                if (success) {
                    if (listener != null) {
                        listener.onDownloadFinished(destPath);
                    }
                } else {
                    if (listener != null) {
                        listener.onDownloadFailed("保存文件失败！");
                    }
                }
            }
        });
    }
    
}
