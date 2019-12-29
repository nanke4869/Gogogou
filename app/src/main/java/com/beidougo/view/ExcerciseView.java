package com.beidougo.view;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.SyncStateContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.beidougo.R;
import com.beidougo.adapter.AdBannerAdapter;
import com.beidougo.adapter.CourseAdapter;
import com.beidougo.bean.CourseBean;

import java.util.List;

public class ExcerciseView extends Activity implements View.OnClickListener {
    private MarkerOptions markerOption;
    private View mExerciseView;
    MapView mMapView = null;
    public AMap aMap;
    private LatLng latlng = new LatLng(39.983456, 116.3154950);
    CameraUpdate mCameraUpdate;// = CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(31.238068, 121.501654),18,30,0));
    private Marker marker;
    private FragmentActivity mContext;
    private LayoutInflater mInflater;
    public static final String RETURN_INFO = "com.beidougo.view.ExcerciseView.info";
    public static final String RETURN_INFO1 = "com.beidougo.view.ExcerciseView.info1";
    public static final String RETURN_INFO2 = "com.beidougo.view.ExcerciseView.info2";
    private List<List<CourseBean>> cb;
    /*public ExcerciseView(FragmentActivity context) {
        mContext = context;
        // 为之后将Layout转化为view时用
        mInflater = LayoutInflater.from(mContext);
    }*/


    public View getView(FragmentActivity context) {
        mContext = context;
        // 为之后将Layout转化为view时用
        mInflater = LayoutInflater.from(mContext);
        mExerciseView = mInflater.inflate(R.layout.activity_excercise_view, null);
        return mExerciseView;
    }

    public void setmCameraUpdate(LatLng lat)
    {
        mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(lat,0,0,0));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excercise_view);
        // 调用 onCreate方法 对 MapView LayoutParams 设置
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);

        if (aMap == null) {
            aMap = mMapView.getMap();
            addMarkersToMap();
        }
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();
        //初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);
        //连续定位、蓝点会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
        myLocationStyle.interval(1000);
        myLocationStyle.showMyLocation(true);
        //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.strokeWidth(1);
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false
        LatLng lat = new LatLng(Double.parseDouble(getIntent().getStringArrayExtra(RETURN_INFO)[0]),Double.parseDouble(getIntent().getStringArrayExtra(RETURN_INFO)[1]));
        mCameraUpdate= CameraUpdateFactory.newCameraPosition(new CameraPosition(lat,15,0,0));
        aMap.moveCamera(mCameraUpdate);
    }



    private void addMarkersToMap() {
        markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .position(new LatLng(Double.parseDouble(getIntent().getStringArrayExtra(RETURN_INFO)[0]),Double.parseDouble(getIntent().getStringArrayExtra(RETURN_INFO)[1])))
                .title(getIntent().getStringExtra(RETURN_INFO1))
                .snippet(getIntent().getStringExtra(RETURN_INFO2))
                .draggable(true);
        marker = aMap.addMarker(markerOption);
        marker.showInfoWindow();
    }

    public void onMyLocationChange(android.location.Location location){
        //从location对象中获取经纬度信息，地址描述信息，建议拿到位置之后调用逆地理编码接口获取（获取地址描述数据章节有介绍）
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }
    private void changeCamera(CameraUpdate update) {
        aMap.animateCamera(update);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**
             * 点击“去中关村”按钮响应事件
             */
            /*case R.id.Zhongguancun:
                changeCamera(
                        CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                latlng, 18, 30, 30)));
                aMap.clear();
                aMap.addMarker(new MarkerOptions().position(latlng)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                break;*/
            default:
                break;
        }
    }
}
