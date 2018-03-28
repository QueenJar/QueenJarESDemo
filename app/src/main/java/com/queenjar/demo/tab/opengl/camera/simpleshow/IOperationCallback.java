package com.queenjar.demo.tab.opengl.camera.simpleshow;

import android.hardware.Camera;

/**
 * <pre>
 *     相机操作过程中的回调
 * </pre>
 * Created by QuennJar on 2018/3/28.
 * Wechat: queenjar
 */

public interface IOperationCallback {
    /**
     * <pre>
     * 在打开相机之前清空一些东西，这个主要是涉及到不同的view实现不同的显示方式
     * destroy some information if the camera is not null before open it
     * </pre>
     *
     * @param mCamera
     */
    void onStopBeforOpenCamera(Camera mCamera);

    /**
     * <pre>
     * 得到了当前的相机预览界面的宽和高
     * </pre>
     *
     * @param previewWidth
     * @param previewHeight
     */
    void onPreviewSizeGot(int previewWidth, int previewHeight);

    /**
     * <pre>
     * 在开启预览之前设置“画布”例如你是要设置setPreviewDisplay和setPreviewCallback还是绑定OpenGL的纹理ID
     * set the "canvas" before you start preview
     * such as call setPreviewDisplay and setPreviewCallback method or bind the OpenGL texture id
     * </pre>
     *
     * @param mCamera
     */
    void onSetListenerBeforeStartPreview(Camera mCamera);

    /**
     * <pre>
     * 销毁Camera相关信息
     * destroy the camera information
     * eg.
     *  mCamera.setPreviewCallback(null);
     * </pre>
     *
     * @param camera
     */
    void onDestroy(Camera camera);
}
