package com.queenjar.demo.tab.opengl.camera;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.queenjar.demo.tab.BaseDemoAty;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * <pre>
 *  简单的使用GLSurfaceView显示相机的Demo
 * </pre>
 * Created by QuennJar on 2018/3/22.
 * Wechat: queenjar
 */

public class SimpleGLCameraAty extends BaseDemoAty {
    private StageView mSimpleCameraView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSimpleCameraView = new StageView(this);
        setContentView(mSimpleCameraView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSimpleCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSimpleCameraView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSimpleCameraView.onDestroy();
    }

    private GLSurfaceView.Renderer mGLRenderer = new GLSurfaceView.Renderer() {
        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int i, int i1) {

        }

        @Override
        public void onDrawFrame(GL10 gl10) {

        }
    };

    class StageView extends GLSurfaceView {

        public StageView(Context context) {
            super(context);
            init(context);
        }

        public StageView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        private void init(Context context) {
            this.setEGLContextClientVersion(2); // 设置使用OPENGL ES2.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                setPreserveEGLContextOnPause(true);//如果没有这一句，那onPause之后再onResume屏幕将会是黑屏滴,调用这句是为了保存EGLContext
            }
            setRenderer(mGLRenderer);
            setRenderMode(RENDERMODE_WHEN_DIRTY);//请求一次才渲染一次（Renderer调用onDrawFrame）
        }

        public void onDestroy() {
            super.onDetachedFromWindow();
        }
    }
}
