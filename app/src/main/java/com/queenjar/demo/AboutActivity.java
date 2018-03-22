package com.queenjar.demo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView contentView = new TextView(this);
        contentView.setText("AboutActivity");
        setContentView(contentView);
    }
}
