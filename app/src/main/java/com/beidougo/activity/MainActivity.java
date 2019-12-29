package com.beidougo.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.beidougo.R;
import com.beidougo.bean.CourseBean;
import com.beidougo.utils.AnalysisUtils;
import com.beidougo.view.CourseView;
import com.beidougo.view.ExcerciseView;
import com.beidougo.view.MyInfoView;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    /**
     * 视图
     */
    private CourseView mCourseView;
    private ExcerciseView mExercisesView;
    private MyInfoView mMyInfoView;
    /**
     * 中间内容栏
     */
    private FrameLayout mBodyLayout;
    /**
     * 底部按钮栏
     */
    public LinearLayout mBottomLayout;
    /**
     * 底部按钮
     */
    private View mCourseBtn;
    private View mExercisesBtn;
    private View mMyInfoBtn;
    private TextView tv_course;
    private TextView tv_exercises;
    private TextView tv_myInfo;
    private ImageView iv_course;
    private ImageView iv_exercises;
    private ImageView iv_myInfo;
    private ImageButton ib_back;
    private TextView tv_main_title;
    private RelativeLayout rl_title_bar;
    private String mKeyWords = "";// 要输入的poi搜索关键字
    private TextView mKeywordsTextView;
    private ImageView mCleanKeyWords;
    private ImageView scan;
    private int REQUEST_CODE_SCAN = 111;
    private TextView tv_back;
    private List<List<CourseBean>> cb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置此界面为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED||ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        mCleanKeyWords = (ImageView)findViewById(R.id.clean_keywords);
        mCleanKeyWords.setOnClickListener(this);
        init();
        initBottomBar();
        setListener();
        setInitStatus();

        mKeyWords = "";
    }
    /**
     * 获取界面上的UI控件
     */
    private void init() {
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        //tv_main_title.setText("北斗“GO”");
        rl_title_bar = (RelativeLayout) findViewById(R.id.title_bar);
        scan = (ImageView)findViewById(R.id.scan_button_image) ;
        scan.setOnClickListener(this);
        rl_title_bar.setBackgroundColor(Color.parseColor("#ffffFF"));
        initBodyLayout();
        mKeywordsTextView = (TextView) findViewById(R.id.main_keywords);
        mKeywordsTextView.setOnClickListener(this);


    }
    /**
     * 获取底部导航栏上的控件
     */
    private void initBottomBar() {
        mBottomLayout = (LinearLayout) findViewById(R.id.main_bottom_bar);
        mCourseBtn = findViewById(R.id.bottom_bar_course_btn);
        mExercisesBtn = findViewById(R.id.bottom_bar_exercises_btn);
        mMyInfoBtn = findViewById(R.id.bottom_bar_myinfo_btn);
        tv_course = (TextView) findViewById(R.id.bottom_bar_text_course);
        tv_exercises = (TextView) findViewById(R.id.bottom_bar_text_exercises);
        tv_myInfo = (TextView) findViewById(R.id.bottom_bar_text_myinfo);
        iv_course = (ImageView) findViewById(R.id.bottom_bar_image_course);
        iv_exercises = (ImageView) findViewById(R.id.bottom_bar_image_exercises);
        iv_myInfo = (ImageView) findViewById(R.id.bottom_bar_image_myinfo);
    }
    private void initBodyLayout() {
        mBodyLayout = (FrameLayout) findViewById(R.id.main_body);
    }
    /**
     * 控件的点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //购物的点击事件
            case R.id.bottom_bar_course_btn:
                clearBottomImageState();
                selectDisplayView(0);
                break;
            //地图的点击事件
            case R.id.bottom_bar_exercises_btn:
                clearBottomImageState();
                selectDisplayView(1);
                break;
            //我的点击事件
            case R.id.bottom_bar_myinfo_btn:
                clearBottomImageState();
                selectDisplayView(2);
                if (mMyInfoView != null) {
                    mMyInfoView.setLoginParams(readLoginStatus());
                }
                break;
            case R.id.main_keywords:
                Intent intent = new Intent(this, InputTipsActivity.class);
                startActivity(intent);
                break;
            case R.id.clean_keywords:
                mKeywordsTextView.setText("");
                mCleanKeyWords.setVisibility(View.GONE);
                break;
            case R.id.scan_button_image:
                Intent intent1 = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent1, REQUEST_CODE_SCAN);
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data!=null){
            //从设置界面或登录界面传递过来的登录状态
            boolean isLogin=data.getBooleanExtra("isLogin",false);
            if(isLogin){//登录成功时显示课程界面
                clearBottomImageState();
                selectDisplayView(0);
            }
            if (mMyInfoView != null) {//登录成功或退出登录时根据isLogin设置我的界面
                mMyInfoView.setLoginParams(isLogin);
            }
        }

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                try {
                    InputStream is = getResources().getAssets().open("chaptertitle.xml");
                    cb = AnalysisUtils.getCourseInfos(is);//getCourseInfos(is)方法在下面会有说明
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String content = data.getStringExtra(Constant.CODED_CONTENT);
                for (List<CourseBean> ccb:cb
                     ) {
                    for (CourseBean cbb:ccb
                         ) {
                        if(cbb.id== Integer.parseInt(content))
                        {
                            Intent intent = new Intent(this,
                                    VideoListActivity.class);
                            intent.putExtra("id", cbb.id);
                            intent.putExtra("intro", cbb.intro);
                            intent.putExtra("imgTitle",cbb.imgTitle);
                            intent.putExtra("title",cbb.title);
                            startActivity(intent);
                        }
//                        else{
//                            Intent intent = new Intent(this,MainActivity.class);
//                            startActivity(intent);
//                        }
                    }
                }
            }
        }
    }

    /**
     * 设置底部三个按钮的点击监听事件
     */
    private void setListener() {
        for (int i = 0; i < mBottomLayout.getChildCount(); i++) {
            mBottomLayout.getChildAt(i).setOnClickListener(this);
        }
    }
    /**
     * 清除底部按钮的选中状态
     */
    private void clearBottomImageState() {
        tv_course.setTextColor(Color.parseColor("#666666"));
        tv_exercises.setTextColor(Color.parseColor("#666666"));
        tv_myInfo.setTextColor(Color.parseColor("#666666"));
        iv_course.setImageResource(R.drawable.main_course_icon);
        iv_exercises.setImageResource(R.drawable.main_exercises_icon);
        iv_myInfo.setImageResource(R.drawable.main_my_icon);
        for (int i = 0; i < mBottomLayout.getChildCount(); i++) {
            mBottomLayout.getChildAt(i).setSelected(false);
        }
    }
    /**
     * 设置底部按钮选中状态
     */
    public void setSelectedStatus(int index) {
        switch (index) {
            case 0:
                mCourseBtn.setSelected(true);
                iv_course.setImageResource(R.drawable.main_course_icon_selected);
                tv_course.setTextColor(Color.parseColor("#0097F7"));
                rl_title_bar.setVisibility(View.VISIBLE);
                //tv_main_title.setText("北斗“GO”");
                break;
            case 1:
                mExercisesBtn.setSelected(true);
                iv_exercises
                        .setImageResource(R.drawable.main_exercises_icon_selected);
                tv_exercises.setTextColor(Color.parseColor("#0097F7"));
                rl_title_bar.setVisibility(View.VISIBLE);
                //tv_main_title.setText("地图");
                break;
            case 2:
                mMyInfoBtn.setSelected(true);
                iv_myInfo.setImageResource(R.drawable.main_my_icon_selected);
                tv_myInfo.setTextColor(Color.parseColor("#0097F7"));
                rl_title_bar.setVisibility(View.GONE);

        }
    }
    /**
     * 移除不需要的视图
     */
    private void removeAllView() {
        for (int i = 0; i < mBodyLayout.getChildCount(); i++) {
            mBodyLayout.getChildAt(i).setVisibility(View.GONE);
        }
    }
    /**
     * 设置界面view的初始化状态
     */
    private void setInitStatus() {
        clearBottomImageState();
        setSelectedStatus(0);
        createView(0);
    }
    /**
     * 显示对应的页面
     */
    private void selectDisplayView(int index) {
        removeAllView();
        createView(index);
        setSelectedStatus(index);
    }
    /**
     * 选择视图
     */
    private void createView(int viewIndex) {
        switch (viewIndex) {
            case 0:
                //购物界面
                if (mCourseView == null) {
                    mCourseView = new CourseView(this);
                    mBodyLayout.addView(mCourseView.getView());
                } else {
                    mCourseView.getView();
                }
                mCourseView.showView();
                break;
            case 1:
                //地图界面

               if (mExercisesView == null) {
                    mExercisesView = new ExcerciseView();
                    //mBodyLayout.addView(mExercisesView.getView(this));
                   clearBottomImageState();
                   setSelectedStatus(0);
                   selectDisplayView(0);
                    Intent intent = new Intent(MainActivity.this,ExcerciseView.class);
                    intent.putExtra(ExcerciseView.RETURN_INFO,new String[]{"32.041817", "118.784114"});
                    startActivity(intent);

                } else {
                   mExercisesView.setmCameraUpdate(new LatLng(31.238068, 121.501654));
                   clearBottomImageState();
                   setSelectedStatus(0);
                   selectDisplayView(0);
                   Intent intent = new Intent(MainActivity.this,ExcerciseView.class);
                   intent.putExtra(ExcerciseView.RETURN_INFO,new String[]{"32.041817", "118.784114"});
                   startActivity(intent);
                }
                break;
            case 2:
                //我的界面
                if (mMyInfoView == null) {
                    mMyInfoView = new MyInfoView(this);
                    mBodyLayout.addView(mMyInfoView.getView());
                } else {
                    mMyInfoView.getView();
                }
                mMyInfoView.showView();
                break;
        }
    }
    protected long exitTime;//记录第一次点击时的时间
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出北斗go",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                MainActivity.this.finish();
                if (readLoginStatus()) {
                    //如果退出此应用时是登录状态，则需要清除登录状态，同时需清除登录时的用户名
                    clearLoginStatus();
                }
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    /**
     * 获取SharedPreferences中的登录状态
     */
    private boolean readLoginStatus() {
        SharedPreferences sp = getSharedPreferences("loginInfo",
                Context.MODE_PRIVATE);
        boolean isLogin = sp.getBoolean("isLogin", false);
        return isLogin;
    }
    /**
     * 清除SharedPreferences中的登录状态
     */
    private void clearLoginStatus() {
        SharedPreferences sp = getSharedPreferences("loginInfo",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();//获取编辑器
        editor.putBoolean("isLogin", false);//清除登录状态
        editor.putString("loginUserName", "");//清除登录时的用户名
        editor.commit();//提交修改
    }
}
