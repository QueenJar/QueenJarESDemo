package com.queenjar.demo.tab.opengl.camera.simpleshow;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.queenjar.demo.tab.BaseDemoAty;
import com.queenjar.helper.android.LogHelper;
import com.queenjar.helper.jopengl.GLCommonUtils;
import com.queenjar.helper.jopengl.TextureHelper;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * <pre>
 *  简单的使用GLSurfaceView显示相机的Demo
 * </pre>
 * Created by QuennJar on 2018/3/22.
 * Wechat: queenjar
 */
public class SimpleGLCameraAty extends BaseDemoAty implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    private static final String TAG = "SimpleGLCameraAty";
    private StageView mSimpleCameraView;
    /**
     * <pre>
     *     GL外部纹理ID。
     *     一方面作为SurfaceTexture的外部纹理ID与之绑定，便于ST做数据处理，
     *     另一方面与GL的program绑定（传入GL线程作为纹理使用）
     * </pre>
     */
    private int mOESTextureID = -1;
    /**
     * SurfaceTexture对图像流的处理不直接显示，而是转为GL的外部纹理，所以创建的时候经常与GL的Texture ID一起构造
     */
    private SurfaceTexture mSurfaceTexture;
    private OESImageTexture mOESImageTexture;
    private float[] mOESTextureSTMatrix = new float[16];//用于SurfaceTexture绑定的外部纹理的纹理矩阵，得靠它来更新纹理的坐标
    private boolean mIsPaused = false;
    private int mSurfaceWidth = 0;
    private int mSurfaceHeight = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSimpleCameraView = new StageView(this);
        setContentView(mSimpleCameraView);
//        mSimpleCameraView.requestRender();
        mSimpleCameraView.setRenderer(this);
        mSimpleCameraView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);//请求一次才渲染一次
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSimpleCameraView.onResume();
        if (mIsPaused && mSurfaceWidth != 0) {//onPause关闭之后，onResume再来打开
            CameraManager.getInstance().openCamera(mSurfaceWidth, mSurfaceHeight);
            CameraManager.getInstance().startPreview();
        }
        mIsPaused = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSimpleCameraView.onPause();
        mIsPaused = true;
        CameraManager.getInstance().stopCamera();//关闭相机
    }

    @Override
    protected void onDestroy() {
        mSimpleCameraView.onDestroy();
        CameraManager.getInstance().stopCamera();//关闭相机
        super.onDestroy();
    }

    /******************************GLSurfaceView.Renderer*******************************************************/
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(1, 0, 0, 1);//设置清屏颜色为红色
        boolean supportOES = GLCommonUtils.checkIfContextSupportsOESFrameBufferObject(gl10);//是否支持OES
        LogHelper.d(TAG, LogHelper.getThreadName() + " supportOES=" + supportOES);
        if (!supportOES) {
            throw new RuntimeException("the device is not support OES FBO");
        }
        mOESTextureID = TextureHelper.genExternalOESTextureID();//用于硬件解码转成RGB值的纹理ID
        mSurfaceTexture = new SurfaceTexture(mOESTextureID);
        mSurfaceTexture.setOnFrameAvailableListener(this);//当ST的一帧数据好了之后就会回调
        mOESImageTexture = new OESImageTexture();
        CameraManager.getInstance().setOperationCallback(new IOperationCallback() {
            @Override
            public void onStopBeforOpenCamera(Camera mCamera) {
                LogHelper.d(TAG, LogHelper.getThreadName());
            }

            @Override
            public void onPreviewSizeGot(int previewWidth, int previewHeight) {
                LogHelper.d(TAG, LogHelper.getThreadName() + " previewWidth=" + previewWidth + " previewHeight=" + previewHeight);
            }

            @Override
            public void onSetListenerBeforeStartPreview(Camera mCamera) {
                try {
                    mCamera.setPreviewTexture(mSurfaceTexture);//将预览数据返回给SurfaceTexture
                } catch (IOException e) {
                    e.printStackTrace();
                    LogHelper.e(TAG, LogHelper.getThreadName(), e);
                }
            }

            @Override
            public void onDestroy(Camera camera) {
                try {
                    camera.setPreviewTexture(null);
                } catch (IOException e) {
                    e.printStackTrace();
                    LogHelper.e(TAG, LogHelper.getThreadName(), e);
                }
            }
        });
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        GLES20.glViewport(0, 0, width, height);
        CameraManager.getInstance().openCamera(width, height);//打开相机
        CameraManager.getInstance().startPreview();//开启预览
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_STENCIL_BUFFER_BIT);
        mSurfaceTexture.updateTexImage();//更新纹理
        mSurfaceTexture.getTransformMatrix(mOESTextureSTMatrix);
        mOESImageTexture.onDrawFrame(mOESTextureID, mOESTextureSTMatrix);
    }

    /******************************SurfaceTexture.OnFrameAvailableListener*******************************************************/
    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        if (mSimpleCameraView != null) {//当ST有一帧数据更新后，GLView请求渲染更新
            mSimpleCameraView.requestRender();
        }
    }
}
