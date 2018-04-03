package com.queenjar.demo.tab.opengl.obj;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.queenjar.demo.tab.BaseDemoAty;
import com.queenjar.helper.android.LogHelper;
import com.queenjar.helper.android.res.AssetsHelper;
import com.queenjar.helper.java.JFileHelper;
import com.queenjar.module.objload.Obj3DLoadAider;
import com.queenjar.module.objload.Obj3DLoadResult;
import com.queenjar.module.objload.OnLoadListener;

import java.io.InputStream;

public class ObjInfoShowAty extends BaseDemoAty {
    private static final String TAG = "ObjInfoShowAty";
    private static final int ACTIVITY_REQUEST_CODE_File_PICK = 0;
    private Context mContext;
    private LinearLayout mContentView;
    private TextView mInfoTV;
    private Button mChooseBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mContentView = new LinearLayout(this);
        mContentView.setOrientation(LinearLayout.VERTICAL);
        mChooseBtn = new Button(this);
        mChooseBtn.setText("Choose From SDCard");
        mContentView.addView(mChooseBtn);
        mInfoTV = new TextView(this);
        mContentView.addView(mInfoTV);
        setContentView(mContentView);
        mChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    startActivityForResult(Intent.createChooser(intent, "Select a Obj File to Upload"), ACTIVITY_REQUEST_CODE_File_PICK);
                } catch (android.content.ActivityNotFoundException ex) {
                    LogHelper.showToast(mContext, "Please install a File Manager.");
                }
            }
        });
        InputStream objIns = AssetsHelper.getInputStream(this, "objs/changgui.obj");
        loadObjIns(objIns);
    }

    private void loadObjIns(InputStream objIns) {
        new Obj3DLoadAider().loadFromInputStreamAsync(objIns, new OnLoadListener() {
            @Override
            public void onLoadOK(Obj3DLoadResult result) {
                LogHelper.d(TAG, LogHelper.getThreadName() + " result=" + result.toString());
                showResult(result.toDumpString());
            }

            @Override
            public void onLoadFailed(String failedMsg) {
                LogHelper.d(TAG, LogHelper.getThreadName() + " failedMsg=" + failedMsg);
            }
        });
    }

    private void showResult(final String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mInfoTV.setText(result);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ACTIVITY_REQUEST_CODE_File_PICK == requestCode && resultCode == RESULT_OK && null != data) {
            //获取返回的数据，这里是android自定义的Uri地址
            Uri uri = data.getData();
            String path = getPath(this, uri);
            if (path == null) {
                String result = "path is null";
                showResult(result);
            } else if (!path.toLowerCase().endsWith(".obj")) {
                String result = "path=" + path + " is not a OBJ file";
                showResult(result);
            } else {
                InputStream ins = JFileHelper.getInputStreamFromPath(path);
                loadObjIns(ins);
            }
        }
    }

    private String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }
}
