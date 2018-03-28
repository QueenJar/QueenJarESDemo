package com.queenjar.demo.tab.opengl.camera.simpleshow;

/**
 * <pre>
 *
 * </pre>
 *  <p style="color:red">
 * 需要照相需要权 uses-permission android:name="android.permission.CAMERA"
 * </p>
 * Created by QuennJar on 2018/3/28.
 * Wechat: queenjar
 */

public class CameraManager implements ICameraOperation {
    private ICameraOperation mCameraOperation;

    /**************************ICameraOperation************************************************/
    @Override
    public void startPreview() {
        mCameraOperation.startPreview();
    }

    @Override
    public void openCamera(int viewWidth, int viewHeight) {
        mCameraOperation.openCamera(viewWidth, viewHeight);
    }

    @Override
    public void stopCamera() {
        mCameraOperation.stopCamera();
    }

    @Override
    public void setOperationCallback(IOperationCallback callback) {
        mCameraOperation.setOperationCallback(callback);
    }

    /**************************Single Instance************************************************/
    private static CameraManager sCameraManager;
    private static Object LOCK = new Object();

    private CameraManager() {
        mCameraOperation = new CameraOperationImp();
    }

    public static CameraManager getInstance() {
        if (sCameraManager != null) {
            return sCameraManager;
        }
        synchronized (LOCK) {
            if (sCameraManager == null) {
                sCameraManager = new CameraManager();
            }
        }
        return sCameraManager;
    }
}
