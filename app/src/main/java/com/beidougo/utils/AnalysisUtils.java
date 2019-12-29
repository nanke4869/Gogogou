package com.beidougo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Xml;
import android.widget.ImageView;
import com.beidougo.bean.CourseBean;

import org.xmlpull.v1.XmlPullParser;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
public class AnalysisUtils {


    /**
     * 解析每个产品信息
     */
    public static List<List<CourseBean>> getCourseInfos(InputStream is) throws Exception {
        XmlPullParser parser=Xml.newPullParser();
        parser.setInput(is, "utf-8");
        List<List<CourseBean>> courseInfos=null;
        List<CourseBean> courseList=null;
        CourseBean courseInfo=null;
        int count=0;
        int type=parser.getEventType();
        while (type!=XmlPullParser.END_DOCUMENT) {
            switch (type) {
                case XmlPullParser.START_TAG:
                    if("infos".equals(parser.getName())){
                        courseInfos=new ArrayList<List<CourseBean>>();
                        courseList=new ArrayList<CourseBean>();
                    }else if("course".equals(parser.getName())){
                        courseInfo=new CourseBean();
                        String ids=parser.getAttributeValue(0);
                        courseInfo.id=Integer.parseInt(ids);
                    }else if("imgtitle".equals(parser.getName())){
                        String imgtitle=parser.nextText();
                        courseInfo.imgTitle=imgtitle;
                    }else if("title".equals(parser.getName())){
                        String title=parser.nextText();
                        courseInfo.title=title;
                    }else if("intro".equals(parser.getName())){
                        String intro=parser.nextText();
                        courseInfo.intro=intro;
                    }else if("psx".equals(parser.getName())){
                        String psx=parser.nextText();
                        courseInfo.psx=psx;
                    }else if("psy".equals(parser.getName())){
                        String psy=parser.nextText();
                        courseInfo.psy=psy;
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if("course".equals(parser.getName())){
                        count++;
                        courseList.add(courseInfo);
                        if(count%2==0){// 课程界面每两个数据是一组放在List集合中
                            courseInfos.add(courseList);
                            courseList=null;
                            courseList=new ArrayList<CourseBean>();
                        }
                        courseInfo=null;
                    }
                    break;
            }
            type=parser.next();
        }
        return courseInfos;
    }
    /**
     * 从SharedPreferences中读取登录用户名
     */
    public static String readLoginUserName(Context context){
        SharedPreferences sp=context.getSharedPreferences("loginInfo", Context.MODE_PRIVATE);
        String userName=sp.getString("loginUserName", "");
        return userName;
    }
}
