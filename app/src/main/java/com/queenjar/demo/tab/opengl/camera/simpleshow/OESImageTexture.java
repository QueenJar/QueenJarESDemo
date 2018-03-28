package com.queenjar.demo.tab.opengl.camera.simpleshow;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.queenjar.helper.java.LogHelper;
import com.queenjar.helper.java.nio.ArrayToBufferHelper;
import com.queenjar.helper.jopengl.ShaderHelper;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * <pre>
 *     OES外部纹理绘制类,此类需要在GL线程中初始化
 * </pre>
 * Created by QuennJar on 2018/3/28.
 * Wechat: queenjar
 */

public class OESImageTexture {
    private static final String TAG = "OESImageTexture";
    private static final String VERTEX_SHADER_CODE =
            "attribute vec4 aPosition;" +
                    "attribute vec4 inputTextureCoordinate;" +
                    "uniform mat4 textureTransform; " +
                    "varying vec2 textureCoordinate;" +
                    "void main()" +
                    "{" +
                    "gl_Position = aPosition;" +
                    "textureCoordinate = (textureTransform*inputTextureCoordinate).xy;" +
                    "}";

    private static final String FRAGMENT_SHADER_CODE =
            "#extension GL_OES_EGL_image_external : require\n" +    //这个第一句一定要
                    "precision mediump float;" +                        //设置精度
                    "varying vec2 textureCoordinate;\n" +
                    "uniform samplerExternalOES sTexture;\n" +       //OES外部纹理,使用采样器samplerExternalOES
                    "void main() {" +
                    "  gl_FragColor = texture2D( sTexture, textureCoordinate );\n" +
                    "}";
    private static float VERTEXT_COORDS[] = {
            -1.0f, 1.0f,
            -1.0f, -1.0f,
            1.0f, -1.0f,
            1.0f, 1.0f,
    };

    private static float TEXTURE_VERTICES[] = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
    };
    private static short drawOrder[] = {0, 1, 2, 0, 2, 3}; // order to draw vertices

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureCoorBuffer;
    private ShortBuffer mDrawOrderBuffer;
    private int mProgramID = -1;
    private int aPositionHandle = -1;
    private int aTextureCoorHandle = -1;
    private int uTextureTransformHandle = -1;
    private int uSampleTextureHandle = -1;

    public OESImageTexture() {
        //将数据封装到Buffer中
        mVertexBuffer = ArrayToBufferHelper.floatArrayToBuffer(VERTEXT_COORDS);
        mTextureCoorBuffer = ArrayToBufferHelper.floatArrayToBuffer(TEXTURE_VERTICES);
        mDrawOrderBuffer = ArrayToBufferHelper.shortArrayToBuffer(drawOrder);
        //获取program，及shader中的属性句柄
        mProgramID = ShaderHelper.getProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE);
        LogHelper.d(TAG, LogHelper.getThreadName() + " mProgramID=" + mProgramID);
        if (mProgramID == -1) {
            throw new RuntimeException("OESImageTexture mProgramID can not be -1");
        }
        aPositionHandle = GLES20.glGetAttribLocation(mProgramID, "aPosition");
        aTextureCoorHandle = GLES20.glGetAttribLocation(mProgramID, "inputTextureCoordinate");
        uTextureTransformHandle = GLES20.glGetUniformLocation(mProgramID, "textureTransform");
        uSampleTextureHandle = GLES20.glGetUniformLocation(mProgramID, "sTexture");
    }

    public void onDrawFrame(int oesTextureID, float[] oesTextureSTMatrix) {
        GLES20.glUseProgram(mProgramID);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, oesTextureID);
        GLES20.glUniform1i(uSampleTextureHandle, 0);

        GLES20.glEnableVertexAttribArray(aPositionHandle);//在用VertexAttribArray前必须先激活它
        //指定aPositionHandle的数据值可以在什么地方访问。 mVertexBuffer在内部（NDK）是个指针，指向数组的第一组值的内存
        GLES20.glVertexAttribPointer(aPositionHandle, 2, GLES20.GL_FLOAT, false, 2*4, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(aTextureCoorHandle);
        GLES20.glVertexAttribPointer(aTextureCoorHandle, 2, GLES20.GL_FLOAT, false, 2*4, mTextureCoorBuffer);

        GLES20.glUniformMatrix4fv(uTextureTransformHandle, 1, false, oesTextureSTMatrix, 0);
        //GLES20.GL_TRIANGLES（以无数小三角行的模式）去绘制出这个纹理图像
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, mDrawOrderBuffer);
        GLES20.glDisableVertexAttribArray(aPositionHandle);
        GLES20.glDisableVertexAttribArray(aTextureCoorHandle);

    }
}
