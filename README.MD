# Android WebView封装

## 说明
采用腾讯的X5内核（和Chromium内核使用上无区别），支持js调用，经纬度坐标获取，并实现了js调用本地方法打开相册和相机，并通过回调返回给h5页面。

---

###### 开发环境：
- IDE：Android Studio

- SDK：Android SDK 目标版本28，最低版本 21，JAVA SDK 8

- gradle：3.4.2

---

###### 第三方库：
- 网络：OKHTTP3 + Retrofit2

- 响应式：RxJava

- 依赖注入：ButterKnife

- Android官方依赖库：androidx系列

- json数据解析：Gson

- WebView：腾讯tbs（X5）内核

- Log库：timber

- 状态栏：immersionbar
