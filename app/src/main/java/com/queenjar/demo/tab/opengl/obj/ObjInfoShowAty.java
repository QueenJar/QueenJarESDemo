package com.queenjar.demo.tab.opengl.obj;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.queenjar.demo.tab.BaseDemoAty;
import com.queenjar.helper.android.LogHelper;
import com.queenjar.helper.android.res.AssetsHelper;
import com.queenjar.module.objload.Obj3DLoadAider;
import com.queenjar.module.objload.Obj3DLoadResult;
import com.queenjar.module.objload.OnLoadListener;

public class ObjInfoShowAty extends BaseDemoAty {
    private static final String TAG = "ObjInfoShowAty";
    private LinearLayout mContentView;
    private TextView mInfoTV;
    private Button mChooseBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContentView = new LinearLayout(this);
        mContentView.setOrientation(LinearLayout.VERTICAL);
        mChooseBtn = new Button(this);
        mChooseBtn.setText("Choose From SDCard");
        mContentView.addView(mChooseBtn);
        mInfoTV = new TextView(this);
        mContentView.addView(mInfoTV);
        setContentView(mContentView);
        new Obj3DLoadAider().loadFromInputStreamAsync(AssetsHelper.getInputStream(this, "objs/changgui.obj"), new OnLoadListener() {
            @Override
            public void onLoadOK(final Obj3DLoadResult result) {
                LogHelper.d(TAG, LogHelper.getThreadName() + " result=" + result.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mInfoTV.setText(result.toDumpString());
                    }
                });
            }

            @Override
            public void onLoadFailed(String failedMsg) {
                LogHelper.d(TAG, LogHelper.getThreadName() + " failedMsg=" + failedMsg);
            }
        });
    }
}
