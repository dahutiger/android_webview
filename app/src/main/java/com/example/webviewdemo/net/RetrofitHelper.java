package com.example.webviewdemo.net;

import android.content.Context;

import com.example.webviewdemo.BuildConfig;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 需先调用init方法
 */

public class RetrofitHelper {
    
    /**
     * 连接超时时间
     */
    private static final int CONN_TIME_OUT = 15;
    /**
     * 写入超时
     */
    private static final int WRITE_TIME_OUT = 30;
    /**
     * 读取超时
     */
    private static final int READ_TIME_OUT = 30;
    
    private static final RetrofitHelper INSTANCE = new RetrofitHelper();
    
    private Retrofit mRetrofit;
    private Retrofit.Builder mRetrofitBuilder;
    private OkHttpClient.Builder mClientBuilder;
    private HeaderInterceptor mHeaderInterceptor;
    
    private RetrofitHelper() {
    }
    
    public static RetrofitHelper getInstance() {
        return INSTANCE;
    }
    
    private Retrofit.Builder createRetrofitBuilder(OkHttpClient client) {
        return new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
    }
    
    private OkHttpClient.Builder createClientBuilder(Context context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        
        builder.connectTimeout(CONN_TIME_OUT, TimeUnit.SECONDS);
        builder.writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS);
        builder.readTimeout(READ_TIME_OUT, TimeUnit.SECONDS);
        
        builder.addInterceptor(new NetStateInterceptor(context));
        mHeaderInterceptor = new HeaderInterceptor();
        builder.addInterceptor(mHeaderInterceptor);
        builder.cookieJar(new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context)));
        
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }
        return builder;
    }
    
    /**
     * 初始化
     *
     * @param context
     * @param baseUrl
     */
    public void init(Context context, String baseUrl) {
        mClientBuilder = createClientBuilder(context.getApplicationContext());
        mRetrofitBuilder = createRetrofitBuilder(mClientBuilder.build());
        setBaseUrl(baseUrl);
    }
    
    public void setBaseUrl(String baseUrl) {
        if (baseUrl == null) {
            return;
        }
        mRetrofit = mRetrofitBuilder.baseUrl(baseUrl).build();
    }
    
    public void addInterceptor(Interceptor interceptor) {
        if (interceptor == null) {
            return;
        }
        mClientBuilder.addInterceptor(interceptor);
        mRetrofitBuilder.client(mClientBuilder.build());
        mRetrofit = mRetrofitBuilder.build();
    }
    
    public void addHeaders(Map<String, String> headers) {
        if (mHeaderInterceptor != null) {
            mHeaderInterceptor.addHeaders(headers);
        }
    }
    
    public void addHeaders(String name, String value) {
        if (mHeaderInterceptor != null) {
            mHeaderInterceptor.addHeader(name, value);
        }
    }
    
    public Map<String, String> getHeader() {
        if (mHeaderInterceptor != null) {
            return mHeaderInterceptor.getHeaders();
        }
        return null;
    }
    
    public String removeHeader(String name) {
        if (mHeaderInterceptor != null) {
            return mHeaderInterceptor.removeHeader(name);
        }
        return null;
    }
    
    /**
     * 创建接口实类
     *
     * @param clazz
     * @param <S>
     * @return
     */
    public <S> S createService(Class<S> clazz) {
        return mRetrofit.create(clazz);
    }
    
}
