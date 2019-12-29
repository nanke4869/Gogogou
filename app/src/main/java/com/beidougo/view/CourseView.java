package com.beidougo.view;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.beidougo.R;
import com.beidougo.adapter.AdBannerAdapter;
import com.beidougo.adapter.CourseAdapter;
import com.beidougo.bean.CourseBean;
import com.beidougo.utils.AnalysisUtils;
public class CourseView {
    private ListView lv_list;
    private CourseAdapter adapter;
    private List<List<CourseBean>> cbl;
    private FragmentActivity mContext;
    private LayoutInflater mInflater;
    private View mCurrentView;
    private ViewPager adPager;// 广告
    private View adBannerLay;// 广告条容器
    private AdBannerAdapter ada;// 适配器
    public static final int MSG_AD_SLID = 002;// 广告自动滑动
    private ViewPagerIndicator vpi;// 小圆点
    private MHandler mHandler;// 事件捕获
    private List<CourseBean> cadl;
    public CourseView(FragmentActivity context) {
        mContext = context;
        // 为之后将Layout转化为view时用
        mInflater = LayoutInflater.from(mContext);
    }
    private void createView() {
        mHandler = new MHandler();
        initAdData();//广告
        getCourseData();//产品
        initView();
        new AdAutoSlidThread().start();
    }
    /**
     * 事件捕获
     */
    class MHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case MSG_AD_SLID:
                    if (ada.getCount() > 0) {
                        adPager.setCurrentItem(adPager.getCurrentItem() + 1);
                    }
                    break;
            }
        }
    }
    /**
     * 广告自动滑动
     */
    class AdAutoSlidThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mHandler != null)
                    mHandler.sendEmptyMessage(MSG_AD_SLID);
            }
        }
    }
    /**
     * 初始化控件
     */
    private void initView() {
        mCurrentView = mInflater.inflate(R.layout.main_view_course, null);
        lv_list = (ListView) mCurrentView.findViewById(R.id.lv_list);
        adapter = new CourseAdapter(mContext);
        adapter.setData(cbl);
        lv_list.setAdapter(adapter);
        adPager = (ViewPager) mCurrentView.findViewById(R.id.vp_advertBanner);
        adPager.setLongClickable(false);
        ada = new AdBannerAdapter(mContext.getSupportFragmentManager(),
                mHandler);
        adPager.setAdapter(ada);// 给ViewPager设置适配器
        adPager.setOnTouchListener(ada);
        vpi = (ViewPagerIndicator) mCurrentView
                .findViewById(R.id.vpi_advert_indicator);// 获取广告条上的小圆点
        vpi.setCount(ada.getSize());// 设置小圆点的个数
        adBannerLay = mCurrentView.findViewById(R.id.rl_adBanner);
        adPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if (ada.getSize() > 0) {
                    //由于index数据在滑动时是累加的，因此用index % ada.getSize()来标记滑动到的当前位置
                    vpi.setCurrentPosition(position % ada.getSize());
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        resetSize();
        if (cadl != null) {
            if (cadl.size() > 0) {
                vpi.setCount(cadl.size());
                vpi.setCurrentPosition(0);
            }
            ada.setDatas(cadl);
        }
    }
    /**
     * 计算控件大小
     */
    private void resetSize() {
        int sw = getScreenWidth(mContext);
        int adLheight = sw / 2;// 广告条高度
        ViewGroup.LayoutParams adlp = adBannerLay.getLayoutParams();
        adlp.width = sw;
        adlp.height = adLheight;
        adBannerLay.setLayoutParams(adlp);
    }
    /**
     * 读取屏幕宽
     */
    public static int getScreenWidth(Activity context) {
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = context.getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        return metrics.widthPixels;
    }
    /**
     * 初始化广告中的数据
     */
    private void initAdData() {
        cadl = new ArrayList<CourseBean>();
        for (int i = 0; i < 3; i++) {
            CourseBean bean = new CourseBean();
            bean.id=(i + 1);
            switch (i) {
                case 0:
                    bean.icon="banner_1";
                    break;
                case 1:
                    bean.icon="banner_2";
                    break;
                case 2:
                    bean.icon="banner_3";
                    break;
                default:
                    break;
            }
            cadl.add(bean);
        }
    }
    /**
     * 获取产品信息
     */
    private void getCourseData() {
        try {
            InputStream is = mContext.getResources().getAssets().open("chaptertitle.xml");
            cbl = AnalysisUtils.getCourseInfos(is);//getCourseInfos(is)方法在下面会有说明
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取当前在导航栏上方显示对应的View
     */
    public View getView() {
        if (mCurrentView == null) {
            createView();
        }
        return mCurrentView;
    }
    /**
     * 显示当前导航栏上方所对应的view界面
     */
    public void showView() {
        if (mCurrentView == null) {
            createView();
        }
        mCurrentView.setVisibility(View.VISIBLE);
    }
}
