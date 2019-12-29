package com.beidougo.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.beidougo.R;
import com.beidougo.bean.CourseBean;
import com.beidougo.bean.VideoBean;
import com.beidougo.utils.AnalysisUtils;
import com.beidougo.utils.DBUtils;
import com.beidougo.view.ExcerciseView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class VideoListActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_intro, tv_video, tv_chapter_intro;
    private ListView lv_video_list;
    private ScrollView sv_chapter_intro;
    ExcerciseView mExerciseView;
    private List<List<CourseBean>> cb;
    private int chapterId;
    private String intro;
    private double posx;
    private double posy;
    private String title;
    private String imgtitle;
    private DBUtils db;
    private ImageView bcImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        // 设置此界面为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        bcImage=(ImageView)findViewById(R.id.tv_img);
        // 从课程界面传递过来的章节id
        chapterId = getIntent().getIntExtra("id", 0);
        // 从课程界面传递过来的章节简介

        intro = getIntent().getStringExtra("intro");
        title=getIntent().getStringExtra("title");
        imgtitle=getIntent().getStringExtra("imgTitle");
        try {
            InputStream is = getResources().getAssets().open("chaptertitle.xml");
            cb = AnalysisUtils.getCourseInfos(is);//getCourseInfos(is)方法在下面会有说明
        } catch (Exception e) {
            e.printStackTrace();
        }
        int counts=0;
        for(int i=0;i<cb.size();i++)
        {
            for(int j=0;j<cb.get(i).size();j++)
            {
                if(cb.get(i).get(j).id==chapterId)
                {
                    posx=Double.parseDouble(cb.get(i).get(j).psx);
                    posy=Double.parseDouble(cb.get(i).get(j).psy);
                }
                counts++;
            }
        }
        switch(chapterId)
        {
            case 1:
                bcImage.setImageResource(R.drawable.chapter_1_icon);
                break;
            case 2:
                bcImage.setImageResource(R.drawable.chapter_2_icon);
                break;
            case 3:
                bcImage.setImageResource(R.drawable.chapter_3_icon);
                break;
            case 4:
                bcImage.setImageResource(R.drawable.chapter_4_icon);
                break;
            case 5:
                bcImage.setImageResource(R.drawable.chapter_5_icon);
                break;
            case 6:
                bcImage.setImageResource(R.drawable.chapter_6_icon);
                break;
            case 7:
                bcImage.setImageResource(R.drawable.chapter_7_icon);
                break;
            case 8:
                bcImage.setImageResource(R.drawable.chapter_8_icon);
                break;
            case 9:
                bcImage.setImageResource(R.drawable.chapter_9_icon);
                break;
            case 10:
                bcImage.setImageResource(R.drawable.chapter_10_icon);
                break;
        }
        /*posx = getIntent().getDoubleExtra("psx",39.983456);
        posy = getIntent().getDoubleExtra("psy",116.3154950);*/
        // 创建数据库工具类的对象
        db = DBUtils.getInstance(VideoListActivity.this);
//        initData();
         init();
    }
    private void init() {
        tv_intro = (TextView) findViewById(R.id.tv_intro);
        tv_video = (TextView) findViewById(R.id.tv_video);
        lv_video_list = (ListView) findViewById(R.id.lv_video_list);
        tv_chapter_intro = (TextView) findViewById(R.id.tv_chapter_intro);
        sv_chapter_intro= (ScrollView) findViewById(R.id.sv_chapter_intro);
        //lv_video_listA.setdapter(adapter);
        tv_intro.setOnClickListener(this);
        tv_video.setOnClickListener(this);
        //adapter.setData(videoList);
        tv_chapter_intro.setText(intro);
        tv_intro.setBackgroundColor(Color.parseColor("#30B4FF"));
        tv_video.setBackgroundColor(Color.parseColor("#FFFFFF"));
        tv_intro.setTextColor(Color.parseColor("#FFFFFF"));
        tv_video.setTextColor(Color.parseColor("#000000"));
    }


    /**
     * 控件的点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_intro:// 简介
                lv_video_list.setVisibility(View.GONE);
                sv_chapter_intro.setVisibility(View.VISIBLE);
                tv_intro.setBackgroundColor(Color.parseColor("#30B4FF"));
                tv_video.setBackgroundColor(Color.parseColor("#FFFFFF"));
                tv_intro.setTextColor(Color.parseColor("#FFFFFF"));
                tv_video.setTextColor(Color.parseColor("#000000"));
                break;
            case R.id.tv_video:
                /*lv_video_list.setVisibility(View.VISIBLE);
                sv_chapter_intro.setVisibility(View.GONE);
                tv_intro.setBackgroundColor(Color.parseColor("#FFFFFF"));
                tv_video.setBackgroundColor(Color.parseColor("#30B4FF"));
                tv_intro.setTextColor(Color.parseColor("#000000"));
                tv_video.setTextColor(Color.parseColor("#FFFFFF"));*/
                //todo: turn to this production's position
                mExerciseView = new ExcerciseView();
                Intent intent = new Intent(this,ExcerciseView.class);
                intent.putExtra(ExcerciseView.RETURN_INFO,new String[]{String.valueOf(posx), String.valueOf(posy)});
                intent.putExtra(ExcerciseView.RETURN_INFO1,title);
                intent.putExtra(ExcerciseView.RETURN_INFO2,imgtitle);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private String read(InputStream in) {
        BufferedReader reader = null;
        StringBuilder sb = null;
        String line=null;
        try {
            sb = new StringBuilder();//实例化一个StringBuilder对象
            //用InputStreamReader把in这个字节流转换成字符流BufferedReader
            reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine())!=null){//从reader中读取一行的内容判断是否为空
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            try {
                if (in != null)
                    in.close();
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}