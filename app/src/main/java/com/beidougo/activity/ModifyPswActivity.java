package com.beidougo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beidougo.R;
import com.beidougo.utils.AnalysisUtils;
import com.beidougo.utils.MD5Utils;

public class ModifyPswActivity extends AppCompatActivity {
    private TextView tv_main_title;
    private TextView tv_back;
    private EditText et_original_psw,et_new_psw,et_new_psw_again;
    private Button btn_save;
    private String originalPsw,newPsw,newPswAgain;
    private String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_psw);
        //设置此界面为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();
        userName= AnalysisUtils.readLoginUserName(this);
    }
    /**
     * 获取界面控件并处理相关控件的点击事件
     */
    private void init(){
        tv_main_title=(TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText("修改密码");
        tv_back=(TextView) findViewById(R.id.tv_back);
        et_original_psw=(EditText) findViewById(R.id.et_original_psw);
        et_new_psw=(EditText) findViewById(R.id.et_new_psw);
        et_new_psw_again=(EditText) findViewById(R.id.et_new_psw_again);
        btn_save=(Button) findViewById(R.id.btn_save);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModifyPswActivity.this.finish();
            }
        });
        //保存按钮的点击事件
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEditString();
                if (TextUtils.isEmpty(originalPsw)) {
                    Toast.makeText(ModifyPswActivity.this, "请输入原始密码", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!MD5Utils.md5(originalPsw).equals(readPsw())) {
                    Toast.makeText(ModifyPswActivity.this, "输入的密码与原始密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                } else if(MD5Utils.md5(newPsw).equals(readPsw())){
                    Toast.makeText(ModifyPswActivity.this, "输入的新密码与原始密码不能一致", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(newPsw)) {
                    Toast.makeText(ModifyPswActivity.this, "请输入新密码", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(newPswAgain)) {
                    Toast.makeText(ModifyPswActivity.this, "请再次输入新密码", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!newPsw.equals(newPswAgain)) {
                    Toast.makeText(ModifyPswActivity.this, "两次输入的新密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(ModifyPswActivity.this, "新密码设置成功", Toast.LENGTH_SHORT).show();
                    //修改登录成功时保存在SharedPreferences中的密码
                    modifyPsw(newPsw);
                    Intent intent = new Intent(ModifyPswActivity.this, LoginActivity.class);
                    startActivity(intent);
                    SettingActivity.instance.finish();
                    ModifyPswActivity.this.finish();
                }
            }
        });
    }
    /**
     * 获取控件上的字符串
     */
    private void getEditString(){
        originalPsw=et_original_psw.getText().toString().trim();
        newPsw=et_new_psw.getText().toString().trim();
        newPswAgain=et_new_psw_again.getText().toString().trim();
    }
    /**
     * 修改登录成功时保存在SharedPreferences中的密码
     */
    private void modifyPsw(String newPsw){
        String md5Psw= MD5Utils.md5(newPsw);//把密码用MD5加密
        SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();//获取编辑器
        editor.putString(userName, md5Psw);//保存新密码
        editor.commit();//提交修改
    }
    /**
     *从SharedPreferences中读取原始密码
     */
    private String readPsw(){
        SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        String spPsw=sp.getString(userName, "");
        return spPsw;
    }
}
