package com.queenjar.demo.tab.opengl.camera.simpleshow;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Build;

/**
 * <pre>
 *  GLSurfaceView子类，主要是用于设置GL使用的版本和设置onPause之后保存上下文
 *  当然，以后可能还有其他的一些操作也可以加在这个类里，例如设置这个View透明之类的
 * </pre>
 * Created by QuennJar on 2018/3/28.
 * Wechat: queenjar
 */

public class StageView extends GLSurfaceView {
    public StageView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.setEGLContextClientVersion(2); // 设置使用OPENGL ES2.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setPreserveEGLContextOnPause(true);//如果没有这一句，那onPause之后再onResume屏幕将会是黑屏滴,调用这句是为了保存EGLContext
        }
    }

    public void onDestroy() {
        super.onDetachedFromWindow();
    }
}
