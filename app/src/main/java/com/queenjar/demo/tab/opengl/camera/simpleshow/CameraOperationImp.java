package com.queenjar.demo.tab.opengl.camera.simpleshow;

import android.graphics.ImageFormat;
import android.hardware.Camera;

import com.queenjar.helper.android.LogHelper;

/**
 * <pre>
 *     相机操作的具体类，可以打开相机，开启预览，关闭预览等
 * </pre>
 * Created by QuennJar on 2018/3/28.
 * Wechat: queenjar
 */

public class CameraOperationImp implements ICameraOperation {
    private static final String TAG = "CameraOperationImp";
    private IOperationCallback mOperationCallback;
    private Camera mCamera;
    private boolean mIsPreviewing = false;
    private boolean mIsFrontCamera = true;//是否是打开前置摄像头

    @Override
    public void startPreview() {
        LogHelper.d(TAG, LogHelper.getThreadName() + " mIsPreviewing=" + mIsPreviewing);
        if (mIsPreviewing) {
            return;
        }
        if (mCamera == null) {
            throw new RuntimeException(TAG + LogHelper.getThreadName() + " mCamera==null");
        }
//        mCamera.setPreviewCallback(mPreviewCallback);
//        try {
//            mCamera.setPreviewDisplay(mSurfaceHolder);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        if (mOperationCallback != null) {
            mOperationCallback.onSetListenerBeforeStartPreview(mCamera);
        }
        mCamera.startPreview();
        mIsPreviewing = true;
    }

    @Override
    public void openCamera(int viewWidth, int viewHeight) {
        if (mCamera != null) {//关闭已经打开的
//            mCamera.setPreviewCallback(null);
            if (mOperationCallback != null) {
                mOperationCallback.onStopBeforOpenCamera(mCamera);
            }
            mIsPreviewing = false;
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if (mIsFrontCamera) {//是否打开前置摄像头
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } else {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
        if (mCamera == null) {//打开失败
            throw new RuntimeException("openCamera failed !");
        }
        Camera.Parameters parameters = mCamera.getParameters();
        Camera.Size preivewSize = CameraHelper.getOptimalPreviewSize(mCamera, viewWidth, viewHeight);
        if (mOperationCallback != null) {
            mOperationCallback.onPreviewSizeGot(preivewSize.width, preivewSize.height);
        }
        parameters.setPreviewSize(preivewSize.width, preivewSize.height);
        Camera.Size pictureSize = CameraHelper.getOptimalPictureSize(mCamera, preivewSize.width, preivewSize.height);
        parameters.setPictureSize(pictureSize.width, pictureSize.height);
        parameters.setPreviewFormat(ImageFormat.NV21);

        if (!mIsFrontCamera) {
            // 自动聚焦模式
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        try {
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            LogHelper.d(TAG, "Setting parameter failed in Open Camera!");
        }
    }

    @Override
    public void stopCamera() {
        LogHelper.d(TAG, LogHelper.getThreadName());
        if (mCamera != null) {
            // TODO
//            mCamera.setPreviewCallback(null);
            if (mOperationCallback != null) {
                mOperationCallback.onDestroy(mCamera);
            }
            mCamera.stopPreview();
            mIsPreviewing = false;
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void setOperationCallback(IOperationCallback callback) {
        mOperationCallback = callback;
    }
}
