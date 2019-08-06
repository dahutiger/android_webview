package com.example.webviewdemo.components.capture;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.webviewdemo.R;
import com.example.webviewdemo.base.BaseActivity;
import com.example.webviewdemo.common.constant.AppConstant;
import com.example.webviewdemo.common.util.ImageUtil;
import com.example.webviewdemo.common.util.LogUtil;
import com.example.webviewdemo.common.util.PictureManager;
import com.example.webviewdemo.common.util.RxJavaUtil;
import com.example.webviewdemo.common.util.ToastUtil;
import com.example.webviewdemo.common.widget.CustomProgressDialog;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.observers.DisposableObserver;

/**
 * description 选择照片页面
 */
public class PickPhotoActivity extends BaseActivity implements View.OnClickListener {
    
    private static final String FORMAT_IMG_NAME = "IMG-%s.jpg";
    
    private static final String EXTRA_SAMPLE = "sample";// 样图
    protected static final String EXTRA_CROP_WIDTH = "crop_width";
    protected static final String EXTRA_CROP_HEIGHT = "crop_height";
    protected static final String EXTRA_FOLDER = "folder";
    public static final String EXTRA_PREFIX = "prefix";
    public static final String EXTRA_FILE_PATH = "extra_file_path";
    public static final String EXTRA_PICTURE_WAY = "extra_photo_way"; // 图片选择方式，0：全部，1：拍照，2：图库选择
    public static final String EXTRA_OPEN_CAMERA = "extra_open_camera"; // 直接打开相机:true/false
    
    public static final int PICTURE_WAY_ALL = 0;        // 图片选择方式，0：全部，1：拍照，2：图库选择
    public static final int PICTURE_WAY_CAPTURE = 1;    // 图片选择方式，0：全部，1：拍照，2：图库选择
    public static final int PICTURE_WAY_GALLERY = 2;    // 图片选择方式，0：全部，1：拍照，2：图库选择
    
    private static final int RC_CAPTURE = 0x1;
    private static final int RC_PICK = 0x2;
    private static final int RC_CROP = 0x3;
    
    private ProgressDialog mProgressDialog;
    
    private File mCaptureFile;
    private File mCropFile;
    private File mCompressedFile;
    private int mCropWidth;
    private int mCropHeight;
    private String mUploadFolder;
    private String prefix;
    
    protected View viewDivider;
    protected TextView tvPick;
    protected TextView tvCapture;
    protected ImageView mSampleIv;
    
    private boolean openCamera = false;// 是否直接打开相机
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.d("requestCode = " + requestCode + ";resultCode = " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RC_CAPTURE:
                    handleCaptureResult(data);
                    break;
                case RC_PICK:
                    handlePickResult(data);
                    break;
                case RC_CROP:
                    handleCropResult(data);
                    break;
                default:
                    break;
            }
        } else {
            if (openCamera) {
                finish();
                return;
            }
        }
    }
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d("onCreate--getLayoutId = " + getLayoutId());
        setContentView(getLayoutId());
        
        Intent intent = getIntent();
        mUploadFolder = intent.getStringExtra(EXTRA_FOLDER);
        prefix = intent.getStringExtra(EXTRA_PREFIX);
        LogUtil.d("prefix = " + prefix);
        if (TextUtils.isEmpty(mUploadFolder)) {
            mUploadFolder = AppConstant.UPLOAD_FOLDER_DEFAULT;
        }
        mCropWidth = intent.getIntExtra(EXTRA_CROP_WIDTH, 0);
        mCropHeight = intent.getIntExtra(EXTRA_CROP_HEIGHT, 0);
        int pictureWay = intent.getIntExtra(EXTRA_PICTURE_WAY, PICTURE_WAY_ALL);
        openCamera = intent.getBooleanExtra(EXTRA_OPEN_CAMERA, false);
        final int sampleId = intent.getIntExtra(EXTRA_SAMPLE, -1);
        
        LogUtil.d("pictureWay = " + pictureWay);
        
        // 初始化默认UI控件
        if (getLayoutId() == R.layout.activity_pick_photo) {
            mSampleIv = findViewById(R.id.iv_sample);
            viewDivider = findViewById(R.id.view_divider);
            tvPick = findViewById(R.id.tv_pick);
            tvPick.setOnClickListener(this);
            tvCapture = findViewById(R.id.tv_capture);
            tvCapture.setOnClickListener(this);
            TextView tvCancel = findViewById(R.id.tv_cancel);
            tvCancel.setOnClickListener(this);
            
            initPictureWay(pictureWay);
            
            if (sampleId > 0) {
                mSampleIv.setImageResource(sampleId);
            }
            
            if (openCamera && pictureWay == PICTURE_WAY_CAPTURE) {
                capture();
                tvCapture.setVisibility(View.GONE);
                tvCancel.setVisibility(View.GONE);
            } else if (pictureWay == PICTURE_WAY_GALLERY) {
                tvCapture.setVisibility(View.GONE);
                tvCancel.setVisibility(View.GONE);
                pick();
            }
        }
        
    }
    
    /**
     * replace by getLayoutId()
     *
     * @param layoutResID
     */
    @Deprecated
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }
    
    protected int getLayoutId() {
        return R.layout.activity_pick_photo;
    }
    
    private void initPictureWay(int pictureWay) {
        switch (pictureWay) {
            case PICTURE_WAY_CAPTURE:
                tvPick.setVisibility(View.GONE);
                viewDivider.setVisibility(View.GONE);
                break;
            case PICTURE_WAY_GALLERY:
                tvCapture.setVisibility(View.GONE);
                viewDivider.setVisibility(View.GONE);
                break;
            case PICTURE_WAY_ALL:
            default:
                break;
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseFiles();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
    
    private void releaseFiles() {
        if (mCaptureFile != null && mCaptureFile.exists()) {
            mCaptureFile.delete();
        }
        mCaptureFile = null;
        
        if (mCropFile != null && mCropFile.exists()) {
            mCropFile.delete();
        }
        mCropFile = null;
        
        if (mCompressedFile != null && mCompressedFile.exists()) {
//            mCompressedFile.delete();
        }
//        mCompressedFile = null;
    }
    
    @Override
    public void onClick(View view) {
        releaseFiles();
        switch (view.getId()) {
            case R.id.tv_capture:
                capture();
                break;
            case R.id.tv_pick:
                pick();
                break;
            case R.id.tv_cancel:
                finish();
                break;
            default:
                break;
        }
    }
    
    public void capture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mCaptureFile = createImageFile();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCaptureFile));
        intent.putExtra("return-data", false);
        startActivityForResult(intent, RC_CAPTURE);
    }
    
    public void pick() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, RC_PICK);
    }
    
    private void cropImage(File sourceImage) {
        try {
            mCropFile = createImageFile();
            Uri imageOut = Uri.fromFile(mCropFile);
            Uri imageIn = Uri.fromFile(sourceImage);
            
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(imageIn, "image/*");
            intent.putExtra("crop", true);
            intent.putExtra("scale", true);
            intent.putExtra("scaleUpIfNeeded", true);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", mCropWidth);
            intent.putExtra("outputY", mCropHeight);
            intent.putExtra("noFaceDetection", true);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageOut);
            
            startActivityForResult(intent, RC_CROP);
        } catch (Exception ex) {
            mCropFile = null;
            ToastUtil.showShort(R.string.crop_photo_failed);
            LogUtil.e("requestCropImage error: " + sourceImage.getAbsolutePath());
        }
    }
    
    private void compressImage(final File imgFile) {
        showProgressDialog();
        Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> emitter) throws Exception {
                mCompressedFile = createImageFile();
                emitter.onNext(ImageUtil.compressImage(imgFile, mCompressedFile));
                emitter.onComplete();
            }
        }).compose(RxJavaUtil.<File>mainSchedulers())
                .as(this.<File>bindLifecycle())
                .subscribe(new DisposableObserver<File>() {
                    @Override
                    public void onNext(File file) {
                        uploadImage(file);
                    }
                    
                    @Override
                    public void onError(Throwable e) {
                        hideProgressDialog();
                        ToastUtil.showShort(R.string.compress_photo_failed);
                    }
                    
                    @Override
                    public void onComplete() {
                    
                    }
                });
    }
    
    private void uploadImage(File imgFile) {
        showProgressDialog();
    
        // TODO: 2019-07-26 图片上传，需要用到顺丰OSS存储
        setResult(imgFile.getAbsolutePath());
//        UploadCallBack uploadCallBack = new UploadCallBack() {
//            @Override
//            public void onProgress(long bytesWritten, long totalSize) {
//            }
//
//            @Override
//            public void onSuccess(String url) {
//                releaseFiles();
//                hideProgressDialog();
//                if (!TextUtils.isEmpty(url)) {
//                    setResult(url);
//                } else {
//                    onFailure("Failure: callback success but result url invalid. url=" + url);
//                }
//            }
//
//            @Override
//            public void onFailure(String exception) {
//                releaseFiles();
//                hideProgressDialog();
//                ToastUtil.showShort(exception);
//            }
//        };
//
//        if (CosXmlHelper.uploadImage(imgFile.getAbsolutePath(), mUploadFolder, prefix, uploadCallBack) == null) {
//            uploadCallBack.onFailure("Failure: create oss task failed");
//        }
    }
    
    private void setResult(String url) {
        Intent data = new Intent();
        data.putExtra(AppConstant.EXT_DATA, url);
        data.putExtra(EXTRA_PREFIX, prefix);
        data.putExtra(EXTRA_FILE_PATH, mCompressedFile.getAbsolutePath());
        setResult(RESULT_OK, data);
        finish();
    }
    
    private File createImageFile() {
        File path = PictureManager.getInstance().getCompressFilePath(this);
        String name = String.format(FORMAT_IMG_NAME, String.valueOf(System.currentTimeMillis()));
        return new File(path, name);
    }
    
    private boolean isNeedCrop() {
        return mCropWidth > 0 && mCropHeight > 0;
    }
    
    private void handleCaptureResult(Intent data) {
        if (mCaptureFile == null || !mCaptureFile.exists()) {
            ToastUtil.showShort(R.string.capture_failed);
            return;
        }
        
        handleImage(mCaptureFile);
    }
    
    private void handlePickResult(Intent data) {
        Uri uri = data.getData();
        if (uri == null) {
            ToastUtil.showShort(R.string.pick_photo_failed);
            return;
        }
        
        String imagePath = ImageUtil.uriToPath(this, uri);
        if (TextUtils.isEmpty(imagePath)) {
            ToastUtil.showShort(R.string.pick_photo_failed);
            return;
        }
        
        handleImage(new File(imagePath));
    }
    
    private void handleImage(File imgFile) {
        if (isNeedCrop()) {
            cropImage(imgFile);
            return;
        }
        compressImage(imgFile);
    }
    
    private void handleCropResult(Intent data) {
        if (mCropFile == null || !mCropFile.exists()) {
            ToastUtil.showShort(R.string.crop_photo_failed);
            return;
        }
        compressImage(mCropFile);
    }
    
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new CustomProgressDialog(this);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(false);
            ((CustomProgressDialog) mProgressDialog).setContentText(R.string.img_uploading);
        }
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }
    
    private void hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
    
    public static void launchForResult(Activity activity, int requestCode) {
        launchForResult(activity, AppConstant.UPLOAD_FOLDER_DEFAULT, null, 0, 0, requestCode);
    }
    
    public static void launchForResult(Activity activity, String folder, String prefix, int cropWidth, int cropHeight, int requestCode) {
        launchForResult(activity, folder, prefix, cropWidth, cropHeight, requestCode, PICTURE_WAY_ALL);
    }
    
    public static void launchForResultByTakePhoto(Activity activity, String folder, String prefix, int cropWidth, int cropHeight, int requestCode) {
        launchForResult(activity, folder, prefix, cropWidth, cropHeight, requestCode, PICTURE_WAY_CAPTURE);
    }
    
    public static void launchForResultOnlyTakePhoto(Activity activity, String folder, String prefix, int cropWidth, int cropHeight, int requestCode) {
        launchForResult(activity, folder, prefix, cropWidth, cropHeight, requestCode, PICTURE_WAY_CAPTURE, -1, true);
    }
    
    public static void launchForResultOnlyGallery(Activity activity, String folder, String prefix, int cropWidth, int cropHeight, int requestCode) {
        launchForResult(activity, folder, prefix, cropWidth, cropHeight, requestCode, PICTURE_WAY_GALLERY, -1, true);
    }
    
    public static void launchForResult(Activity activity, String folder, String prefix, int cropWidth, int cropHeight, int requestCode, int way) {
        launchForResult(activity, folder, prefix, cropWidth, cropHeight, requestCode, way, -1, false);
    }
    
    /**
     * 上传图片
     *
     * @param activity
     * @param folder      图片在服务器中的保存目录
     * @param prefix      图片在服务器中的名称中的前缀。例如：业务ID
     * @param cropWidth   裁剪的参数：宽度
     * @param cropHeight  裁剪的参数：高度
     * @param requestCode
     * @param way         图片获取方式：拍照/图库选择
     * @param sample      样例图
     * @param openCamera  是否直接打开相机
     */
    public static void launchForResult(Activity activity, String folder, String prefix, int cropWidth, int cropHeight, int requestCode, int way, int sample, boolean openCamera) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(activity, PickPhotoActivity.class);
        intent.putExtra(EXTRA_FOLDER, folder);
        intent.putExtra(EXTRA_PREFIX, prefix);
        intent.putExtra(EXTRA_CROP_WIDTH, cropWidth);
        intent.putExtra(EXTRA_CROP_HEIGHT, cropHeight);
        intent.putExtra(EXTRA_PICTURE_WAY, way);
        intent.putExtra(EXTRA_SAMPLE, sample);
        intent.putExtra(EXTRA_OPEN_CAMERA, openCamera);
        activity.startActivityForResult(intent, requestCode);
    }
    
    
    public static void launchForResult(Fragment fragment, int requestCode) {
        launchForResult(fragment, AppConstant.UPLOAD_FOLDER_DEFAULT, null, 0, 0, requestCode);
    }
    
    public static void launchForResult(Fragment fragment, String folder, String prefix, int cropWidth, int cropHeight, int requestCode) {
        launchForResult(fragment, folder, prefix, cropWidth, cropHeight, requestCode, PICTURE_WAY_ALL);
    }
    
    public static void launchForResultByTakePhoto(Fragment fragment, String folder, String prefix, int cropWidth, int cropHeight, int requestCode) {
        launchForResult(fragment, folder, prefix, cropWidth, cropHeight, requestCode, PICTURE_WAY_CAPTURE, -1, true);
    }
    
    public static void launchForResult(Fragment fragment, String folder, String prefix, int cropWidth, int cropHeight, int requestCode, int way) {
        launchForResult(fragment, folder, prefix, cropWidth, cropHeight, requestCode, way, -1, false);
    }
    
    /**
     * 上传图片
     *
     * @param fragment
     * @param folder      图片在服务器中的保存目录
     * @param prefix      图片在服务器中的名称中的前缀。例如：业务ID
     * @param cropWidth   裁剪的参数：宽度
     * @param cropHeight  裁剪的参数：高度
     * @param requestCode
     * @param way         图片获取方式：拍照/图库选择
     * @param sample      样例图
     * @param openCamera  是否直接打开相机
     */
    public static void launchForResult(Fragment fragment, String folder, String prefix, int cropWidth, int cropHeight, int requestCode, int way, int sample, boolean openCamera) {
        if (fragment == null) {
            return;
        }
        Intent intent = new Intent(fragment.getActivity(), PickPhotoActivity.class);
        intent.putExtra(EXTRA_FOLDER, folder);
        intent.putExtra(EXTRA_PREFIX, prefix);
        intent.putExtra(EXTRA_CROP_WIDTH, cropWidth);
        intent.putExtra(EXTRA_CROP_HEIGHT, cropHeight);
        intent.putExtra(EXTRA_PICTURE_WAY, way);
        intent.putExtra(EXTRA_SAMPLE, sample);
        intent.putExtra(EXTRA_OPEN_CAMERA, openCamera);
        fragment.startActivityForResult(intent, requestCode);
    }
    
}
