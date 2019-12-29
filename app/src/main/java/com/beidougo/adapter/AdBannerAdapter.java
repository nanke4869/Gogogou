package com.beidougo.adapter;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import java.util.ArrayList;
import java.util.List;
import com.beidougo.bean.CourseBean;
import com.beidougo.fragment.AdBannerFragment;
import com.beidougo.view.CourseView;
public class AdBannerAdapter extends FragmentStatePagerAdapter implements
        OnTouchListener {
    private Handler mHandler;
    private List<CourseBean> cadl;
    public AdBannerAdapter(FragmentManager fm) {
        super(fm);
        cadl = new ArrayList<CourseBean>();
    }
    public AdBannerAdapter(FragmentManager fm, Handler handler) {
        super(fm);
        mHandler = handler;
        cadl = new ArrayList<CourseBean>();
    }
    /**
     *  设置数据更新界面
     */
    public void setDatas(List<CourseBean> cadl) {
        this.cadl = cadl;
        //notifyDataSetChanged();
    }
    @Override
    public Fragment getItem(int index) {
        Bundle args = new Bundle();
        if (cadl.size() > 0)
            args.putString("ad", cadl.get(index % cadl.size()).icon);
        return AdBannerFragment.newInstance(args);
    }
    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }
    /**
     * 返回数据集的真实容量大小
     */
    public int getSize() {
        return cadl == null ? 0 : cadl.size();
    }
    @Override
    public int getItemPosition(Object object) {
        // 防止刷新结果显示列表的时候出现缓存数据,重载这个函数 使之默认返回POSITION_NONE
        return POSITION_NONE;
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mHandler.removeMessages(CourseView.MSG_AD_SLID);
        return false;
    }
}
