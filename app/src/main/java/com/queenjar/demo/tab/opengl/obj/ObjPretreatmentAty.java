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
import com.queenjar.helper.java.JFileHelper;
import com.queenjar.helper.java.basedata.StringHelper;
import com.queenjar.helper.java.nio.JBufferCacheHelper;
import com.queenjar.module.objload.Obj3DLoadAider;
import com.queenjar.module.objload.Obj3DLoadResult;
import com.queenjar.module.objload.OnLoadListener;

import java.io.File;
import java.io.InputStream;

/**
 * <pre>
 *     Obj文件预处理
 * </pre>
 * Created by QueenJar
 * Wechat: queenjar
 * Emial: queenjar@qq.com
 */
public class ObjPretreatmentAty extends BaseDemoAty {
    private static final String TAG = "ObjPretreatmentAty";
    private static final int ACTIVITY_REQUEST_CODE_File_PICK = 0;
    private Context mContext;
    private LinearLayout mContentView;
    private Button mChooseBtn;
    private TextView mResultView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mContentView = new LinearLayout(mContext);
        mContentView.setOrientation(LinearLayout.VERTICAL);
        setContentView(mContentView);
        mChooseBtn = new Button(mContext);
        mChooseBtn.setText("Choose OBJ File");
        mResultView = new TextView(mContext);
        mContentView.addView(mChooseBtn);
        mContentView.addView(mResultView);

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ACTIVITY_REQUEST_CODE_File_PICK == requestCode && resultCode == RESULT_OK && null != data) {
            //获取返回的数据，这里是android自定义的Uri地址
            Uri uri = data.getData();
            final String path = getPath(this, uri);
            if (path == null) {
                String result = "path is null";
                showResult(result);
            } else if (!path.toLowerCase().endsWith(".obj")) {
                String result = "path=" + path + " is not a OBJ file";
                showResult(result);
            } else {
                new Obj3DLoadAider().loadFromInputStreamAsync(JFileHelper.getInputStreamFromPath(path), new OnLoadListener() {
                    @Override
                    public void onLoadOK(Obj3DLoadResult result) {
                        try {
                            File srcFile = new File(path);
                            String ROOT_DIR = srcFile.getParentFile().getAbsolutePath();
                            StringHelper.getParentDirectory(path);
                            String fileName = srcFile.getName().toLowerCase().replace(".obj", "");
                            File dirFile = new File(ROOT_DIR);
                            if (!dirFile.exists()) {
                                dirFile.mkdirs();
                            }
                            JBufferCacheHelper.writeCache(result.getVertexXYZ(), ROOT_DIR + "/" + fileName + ".vxyz");
                            JBufferCacheHelper.writeCache(result.getNormalVectorXYZ(), ROOT_DIR + "/" + fileName + ".nxyz");
                            JBufferCacheHelper.writeCache(result.getTextureVertexST(), ROOT_DIR + "/" + fileName + ".tst");
                            String msg = "Pretreatment is OK,Look the file:" + "\n"
                                    + ROOT_DIR + " :\n"
                                    + fileName + ".vxyz \n"
                                    + fileName + ".nxyz \n"
                                    + fileName + ".tst \n";
                            showResult(msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLoadFailed(String failedMsg) {
                        LogHelper.d(TAG, LogHelper.getThreadName() + " failedMsg=" + failedMsg);
                        showResult(" failedMsg=" + failedMsg);
                    }
                });
            }
        }

    }

    private void showResult(final String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mResultView.setText(result);
            }
        });
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
