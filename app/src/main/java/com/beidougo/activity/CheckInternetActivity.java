package com.beidougo.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.beidougo.R;
import com.beidougo.bean.CourseBean;
import com.yzq.zxinglibrary.common.Constant;

import java.util.List;

public class CheckInternetActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mKeywordsTextView;
    private ImageView mCleanKeyWords;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_internet);
        mKeywordsTextView = (TextView) findViewById(R.id.main_keywords);
        mKeywordsTextView.setOnClickListener(this);
        mCleanKeyWords = (ImageView)findViewById(R.id.clean_keywords);
        mCleanKeyWords.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_keywords:
                Intent intent = new Intent(this, InputTipsActivity.class);
                this.finish();
                startActivity(intent);
                break;

            case R.id.clean_keywords:
                mKeywordsTextView.setText("");
                mCleanKeyWords.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }
}
