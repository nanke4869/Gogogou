package com.beidougo.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beidougo.R;
import com.beidougo.bean.UserBean;
import com.beidougo.utils.AnalysisUtils;
import com.beidougo.utils.DBUtils;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_back;
    private TextView tv_main_title;
    private TextView tv_nickName, tv_signature, tv_user_name, tv_sex;
    private RelativeLayout rl_nickName, rl_sex, rl_signature,
            rl_title_bar;
    private static final int CHANGE_NICKNAME = 1;//修改昵称的自定义常量
    private static final int CHANGE_SIGNATURE = 2;//修改个性签名的自定义常量
    private String spUserName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        //设置此界面为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //从SharedPreferences中获取登录时的用户名
        spUserName = AnalysisUtils.readLoginUserName(this);
        init();
        initData();
        setListener();
    }
    /**
     * 初始化控件
     */
    private void init() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_main_title.setText("个人资料");
        rl_title_bar = (RelativeLayout) findViewById(R.id.title_bar);
        rl_title_bar.setBackgroundColor(Color.parseColor("#30B4FF"));
        rl_nickName = (RelativeLayout) findViewById(R.id.rl_nickName);
        rl_sex = (RelativeLayout) findViewById(R.id.rl_sex);
        rl_signature = (RelativeLayout) findViewById(R.id.rl_signature);
        tv_nickName = (TextView) findViewById(R.id.tv_nickName);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_signature = (TextView) findViewById(R.id.tv_signature);
    }
    /**
     * 获取数据
     */
    private void initData() {
        UserBean bean = null;
        bean = DBUtils.getInstance(this).getUserInfo(spUserName);
        // 首先判断一下数据库是否有数据
        if (bean == null) {
            bean = new UserBean();
            bean.userName=spUserName;
            bean.nickName="南柯";
            bean.sex="女";
            bean.signature="APTX4869";
            //保存用户信息到数据库
            DBUtils.getInstance(this).saveUserInfo(bean);
        }
        setValue(bean);
    }
    /**
     * 为界面控件设置值
     */
    private void setValue(UserBean bean) {
        tv_nickName.setText(bean.nickName);
        tv_user_name.setText(bean.userName);
        tv_sex.setText(bean.sex);
        tv_signature.setText(bean.signature);
    }
    /**
     * 设置控件的点击监听事件
     */
    private void setListener() {
        tv_back.setOnClickListener(this);
        rl_nickName.setOnClickListener(this);
        rl_sex.setOnClickListener(this);
        rl_signature.setOnClickListener(this);
    }
    /**
     * 控件的点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back://返回键的点击事件
                this.finish();
                break;
            case R.id.rl_nickName://昵称的点击事件
                String name = tv_nickName.getText().toString();//获取昵称控件上的数据
                Bundle bdName = new Bundle();
                bdName.putString("content", name);//传递界面上的昵称数据
                bdName.putString("title", "昵称");
                bdName.putInt("flag", 1);//flag传递1时表示是修改昵称
                enterActivityForResult(ChangeUserInfoActivity.class,
                        CHANGE_NICKNAME, bdName);//跳转到个人资料修改界面
                break;
            case R.id.rl_sex://性别的点击事件
                String sex = tv_sex.getText().toString();//获取性别控件上的数据
                sexDialog(sex);
                break;
            case R.id.rl_signature://签名的点击事件
                String signature = tv_signature.getText().toString();//获取签名控件上的数据
                Bundle bdSignature = new Bundle();
                bdSignature.putString("content", signature);//传递界面上的签名数据
                bdSignature.putString("title", "签名");
                bdSignature.putInt("flag", 2);//flag传递2时表示是修改签名
                enterActivityForResult(ChangeUserInfoActivity.class,
                        CHANGE_SIGNATURE, bdSignature);//跳转到个人资料修改界面
                break;
            default:
                break;
        }
    }
    /**
     * 设置性别的弹出框
     */
    private void sexDialog(String sex){
        int sexFlag=0;
        if("男".equals(sex)){
            sexFlag=0;
        }else if("女".equals(sex)){
            sexFlag=1;
        }
        final String items[]={"男","女"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle("性别"); //设置标题
        builder.setSingleChoiceItems(items,sexFlag,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {//第二个参数是默认选中的哪个项
                dialog.dismiss();
                Toast.makeText(UserInfoActivity.this,items[which],Toast.LENGTH_SHORT).show();
                setSex(items[which]);
            }
        });
        builder.create().show();
    }
    /**
     * 更新界面上的性别数据
     */
    private void setSex(String sex){
        tv_sex.setText(sex);
        // 更新数据库中的性别字段
        DBUtils.getInstance(UserInfoActivity.this).updateUserInfo("sex",
                sex, spUserName);
    }
    /**
     * 回传数据
     */
    private String new_info;//最新数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHANGE_NICKNAME://个人资料修改界面回传过来的昵称数据
                if (data != null) {
                    new_info = data.getStringExtra("nickName");
                    if (TextUtils.isEmpty(new_info) || new_info == null) {
                        return;
                    }
                    tv_nickName.setText(new_info);
                    // 更新数据库中的昵称字段
                    DBUtils.getInstance(UserInfoActivity.this).updateUserInfo(
                            "nickName", new_info, spUserName);
                }
                break;
            case CHANGE_SIGNATURE://个人资料修改界面回传过来的签名数据
                if (data != null) {
                    new_info = data.getStringExtra("signature");
                    if (TextUtils.isEmpty(new_info) || new_info == null) {
                        return;
                    }
                    tv_signature.setText(new_info);
                    // 更新数据库中的签名字段
                    DBUtils.getInstance(UserInfoActivity.this).updateUserInfo(
                            "signature", new_info, spUserName);
                }
                break;
        }
    }
    /**
     * 获取回传数据时需使用的跳转方法，
     * 第一个参数to表示需要跳转到的界面，第二个参数requestCode表示一个请求码，第三个参数b表示跳转时传递的数据
     */
    public void enterActivityForResult(Class<?> to, int requestCode, Bundle b) {
        Intent i = new Intent(this, to);
        i.putExtras(b);
        startActivityForResult(i, requestCode);
    }
}
