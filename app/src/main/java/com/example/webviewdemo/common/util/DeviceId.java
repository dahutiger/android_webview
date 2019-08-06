package com.example.webviewdemo.common.util;

import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;

import androidx.core.content.ContextCompat;

import com.example.webviewdemo.common.constant.SpConstant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

/**
 * 生成设备唯一ID：利用java UUID，随机一个ID，然后做MD5，保存MD5值作为设备唯一ID，是可变的。
 * 缓存策略：内存，SP，SDCard。
 * 读取：优先从内存读取，再次从SDCard读取，最后从SP读取。
 * 如果都没有读取到，则写入SDCard，SP。app第一次启动，由于没有权限，写入SDCard会失败，此时只写入SP。等到获取到权限了，再把SP写入SDCard
 */
public class DeviceId {
    
    // 保存的文件 采用隐藏文件的形式进行保存
    private static final String DEVICES_FILE_NAME = ".web_device";
    
    private static String deviceId;
    private static boolean readFromSdCard;
    
    public static String getDeviceId(Context context) {
        if (!TextUtils.isEmpty(deviceId) && readFromSdCard) {
            return deviceId;
        } else {
            deviceId = getDeviceIdFromStorage(context);
            return deviceId;
        }
    }
    
    /**
     * 获取设备唯一标识符
     *
     * @param context
     * @return
     */
    private static String getDeviceIdFromStorage(Context context) {
        String deviceId = readDeviceID(context);
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }
        
        UUID uuid = UUID.randomUUID();
        deviceId = uuid.toString().replace("-", "");
        if (!TextUtils.isEmpty(deviceId)) {
            String md5 = getMD5(deviceId, false);
            saveDeviceID(context, md5);
            saveToSp(md5);
            return md5;
        }
        return null;
    }
    
    /**
     * 读取固定的文件中的内容,这里就是读取sd卡中保存的设备唯一标识符
     *
     * @param context
     * @return
     */
    private static String readDeviceID(Context context) {
        File file = getDevicesDir(context);
        String deviceId;
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            Reader in = new BufferedReader(isr);
            StringBuilder result = new StringBuilder();
            int i;
            while ((i = in.read()) > -1) {
                result.append((char) i);
            }
            in.close();
            deviceId = result.toString();
            LogUtil.i("Get SDCard device id is " + deviceId);
            
            if (TextUtils.isEmpty(deviceId)) {
                result.append(readDeviceIdFromSp());
                readFromSdCard = false;
                deviceId = result.toString();
            } else {
                saveToSp(deviceId);
                readFromSdCard = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            deviceId = readDeviceIdFromSp();
            readFromSdCard = false;
        }
        
        if (!readFromSdCard && ContextCompat.checkSelfPermission(context, permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            saveDeviceID(context, deviceId);
        }
        return deviceId;
    }
    
    private static void saveToSp(String deviceId) {
        LogUtil.v("save SP device id is " + deviceId);
        SpUtil.putString(SpConstant.DEVICE_ID, deviceId);
    }
    private static String readDeviceIdFromSp() {
        String result = SpUtil.getString(SpConstant.DEVICE_ID, null);
        LogUtil.v("Get SP device id is " + result);
        return result;
    }
    
    /**
     * 保存内容到SD卡和SP中
     *
     * @param str
     * @param context
     */
    private static void saveDeviceID(Context context, String str) {
        File file = getDevicesDir(context);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            Writer out = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            out.write(str);
            LogUtil.i("Save SDCard device id is " + str);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 对特定的内容进行md5加密
     *
     * @param message   加密明文
     * @param upperCase 加密以后的字符串是是大写还是小写  true 大写  false 小写
     * @return md5
     */
    private static String getMD5(String message, boolean upperCase) {
        String md5str = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] input = message.getBytes();
            byte[] buff = md.digest(input);
            md5str = bytesToHex(buff, upperCase);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5str;
    }
    
    
    private static String bytesToHex(byte[] bytes, boolean upperCase) {
        StringBuilder md5str = new StringBuilder();
        int digital;
        for (int i = 0; i < bytes.length; i++) {
            digital = bytes[i];
            
            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        if (upperCase) {
            return md5str.toString().toUpperCase();
        }
        return md5str.toString().toLowerCase();
    }
    
    /**
     * 统一处理设备唯一标识 保存的文件的地址
     *
     * @param context
     * @return
     */
    private static File getDevicesDir(Context context) {
        File mCropFile;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            mCropFile = new File(Environment.getExternalStorageDirectory(), DEVICES_FILE_NAME);
        } else {
            mCropFile = new File(context.getFilesDir(), DEVICES_FILE_NAME);
        }
        return mCropFile;
    }
}
