package com.example.webviewdemo.common.util;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 对像序列化工具
 */
public class SerializeUtil {
    
    public static String obj2String(Object object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            String base64 = new String(Base64.encode(baos.toByteArray(), 0));
            oos.close();
            baos.close();
            
            return base64;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static Object string2Obj(String base64Str) {
        try {
            byte[] bytes = Base64.decode(base64Str.getBytes(), 0);
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object obj = ois.readObject();
            ois.close();
            bais.close();
            
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
