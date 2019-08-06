package com.example.webviewdemo.common.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * description 图片处理工具类
 */
public class ImageUtil {
    
    private static final String DOCUMENT_EXTERNAL_STORAGE = "com.android.externalstorage.documents";
    private static final String DOCUMENT_DOWNLOADS = "com.android.providers.downloads.documents";
    private static final String DOCUMENT_MEDIA = "com.android.providers.media.documents";
    private static final String DOCUMENT_GOOGLE_PHOTOS = "com.google.android.apps.photos.content";
    
    private static final int COMPRESS_MAX_LENGTH_DEFAULT = 1080;// 图片最大边长
    private static final long COMPRESS_MAX_SIZE_DEFAULT = 500 * 1024L;// 图片最大大小
    
    /**
     * 图片URI转成绝对路径
     *
     * @param context
     * @param uri
     * @return
     */
    public static String uriToPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }
            
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }
    
    private static boolean isExternalStorageDocument(Uri uri) {
        return DOCUMENT_EXTERNAL_STORAGE.equals(uri.getAuthority());
    }
    
    private static boolean isDownloadsDocument(Uri uri) {
        return DOCUMENT_DOWNLOADS.equals(uri.getAuthority());
    }
    
    private static boolean isMediaDocument(Uri uri) {
        return DOCUMENT_MEDIA.equals(uri.getAuthority());
    }
    
    private static boolean isGooglePhotosUri(Uri uri) {
        return DOCUMENT_GOOGLE_PHOTOS.equals(uri.getAuthority());
    }
    
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }
    
    /**
     * 压缩图片
     *
     * @param imageFile
     * @param outFile
     * @return
     * @throws IOException
     */
    public static File compressImage(File imageFile, File outFile) throws IOException {
        return compressImage(imageFile, outFile, COMPRESS_MAX_LENGTH_DEFAULT, COMPRESS_MAX_SIZE_DEFAULT);
    }
    
    /**
     * 压缩图片
     *
     * @param imageFile
     * @param outFile
     * @param maxLen
     * @param limitSize
     * @return
     * @throws IOException
     */
    public static File compressImage(File imageFile, File outFile, int maxLen, long limitSize) throws IOException {
        File parentFile = outFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(outFile);
            // write the compressed bitmap at the destination specified by outFile.
            Bitmap scaledBitmap = createScaledBitmapFromFile(imageFile, maxLen);
            int quality = calculateQuality(scaledBitmap, limitSize);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream);
            scaledBitmap.recycle();
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
        
        return outFile;
    }
    
    private static Bitmap createScaledBitmapFromFile(File imageFile, int maxLen) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        final int width = options.outWidth;
        final int height = options.outHeight;
        int reqWidth, reqHeight;
        if (width > height) {
            reqWidth = maxLen;
            reqHeight = (reqWidth * height) / width;
        } else {
            reqHeight = maxLen;
            reqWidth = (reqHeight * width) / height;
        }
        
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap scaledBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
//        scaledBitmap = Bitmap.createScaledBitmap(scaledBitmap, reqWidth, reqHeight, false);
        try {
            scaledBitmap = rotateBitmap(imageFile.getAbsolutePath(), scaledBitmap);
        } catch (IOException e) {
            LogUtil.e(e);
        }
        return scaledBitmap;
    }
    
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;
        
        
        if (height > reqHeight || width > reqWidth) {
            
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        
        return inSampleSize;
    }
    
    /**
     * 计算压缩到指定大小时，图片的质量
     *
     * @param bitmap
     * @param limitSize
     * @return
     */
    private static int calculateQuality(Bitmap bitmap, long limitSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        int nextQuality = 100;
        do {
            baos.reset();
            quality = nextQuality;
            LogUtil.d("quality: " + quality);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            nextQuality = Math.round(quality * 0.98f);
        } while (baos.size() > limitSize);
        
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return quality;
    }
    
    public static Bitmap rotateBitmap(String photoPath, Bitmap bitmap) throws IOException {
        ExifInterface ei = new ExifInterface(photoPath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
        
        Bitmap rotatedBitmap = null;
        LogUtil.d("orientation ==" + orientation);
        switch (orientation) {
            
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateBitmap(bitmap, 90);
                break;
            
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateBitmap(bitmap, 180);
                break;
            
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateBitmap(bitmap, 270);
                break;
            
            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
                break;
        }
        return rotatedBitmap;
    }
    
    private static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
    
}
