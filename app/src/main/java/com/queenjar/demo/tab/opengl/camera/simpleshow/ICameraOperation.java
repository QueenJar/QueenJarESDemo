package com.queenjar.demo.tab.opengl.camera.simpleshow;

/**
 * <pre>
 *     相机操作的一些接口（比较简单，像缩放，焦点等没有加，因为只是想简单的实现预览）
 * </pre>
 * Created by QuennJar on 2018/3/28.
 * Wechat: queenjar
 */

public interface ICameraOperation {
    /**
     * <pre>
     * 相机开始预览
     * the camera start to preview
     * </pre>
     */
    void startPreview();

    /**
     * <pre>
     * 打开相机，如果不打卡的话预览会直接报错
     * open camera,
     * the program will throw a RuntimeException without opened the camera
     * when you invoke method {@link #startPreview()}
     * </pre>
     *
     * @param viewWidth  width of the View you display
     * @param viewHeight height of the View you display
     */
    void openCamera(int viewWidth, int viewHeight);

    /**
     * <pre>
     * 销毁Camera相关信息
     * destroy the camera information
     * </pre>
     */
    void stopCamera();

    /**
     * <pre>
     * 设置操作时的回调，因为不同的view绑定的内容不同，所以在这个回调里操作
     * callback when operation since different view will bind different "canvas"
     * </pre>
     *
     * @param callback
     */
    void setOperationCallback(IOperationCallback callback);

}
