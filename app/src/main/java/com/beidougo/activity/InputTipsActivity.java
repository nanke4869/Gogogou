package com.beidougo.activity;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.beidougo.R;
import com.beidougo.bean.CourseBean;
import com.beidougo.utils.AnalysisUtils;
import com.yzq.zxinglibrary.common.Constant;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class InputTipsActivity extends Activity implements OnItemClickListener, View.OnClickListener, SearchView.OnQueryTextListener {
    private SearchView mSearchView;// 输入搜索关键字
    private ImageView mBack;
    private ListView mInputListView;
    private List<String> mCurrentTipList;
    private String[] str;
    private List<List<CourseBean>> cb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_tips);
        mInputListView = (ListView) findViewById(R.id.inputtip_list);
        mInputListView.setTextFilterEnabled(true);
        initSearchView();

        mInputListView.setOnItemClickListener(this);
        mBack = (ImageView) findViewById(R.id.back);
        mBack.setOnClickListener(this);
    }

    private void initSearchView() {
        mSearchView = (SearchView) findViewById(R.id.keyWord);
        try {
            InputStream is = getResources().getAssets().open("chaptertitle.xml");
            cb = AnalysisUtils.getCourseInfos(is);//getCourseInfos(is)方法在下面会有说明
        } catch (Exception e) {
            e.printStackTrace();
        }
        int ii=0;
        int jj=0;
        int counts=0;
        for(ii=0;ii<cb.size();ii++)
        {
            for(jj=0;jj<cb.get(ii).size();jj++)
            {
                counts++;
            }
        }
        str = new String[counts];
        counts=0;
        for(ii=0;ii<cb.size();ii++)
        {
            for(jj=0;jj<cb.get(ii).size();jj++)
            {
                str[counts]=cb.get(ii).get(jj).title;
                counts++;
            }
        }
        mInputListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,str ));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    mInputListView.setFilterText(newText);
                }else{
                    mInputListView.clearTextFilter();
                }
                return false;
            }
        });
        //设置SearchView默认为展开显示
        mSearchView.setIconified(false);
        mSearchView.onActionViewExpanded();
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setSubmitButtonEnabled(true);
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.back) {
            this.finish();
        }
    }

    public static boolean IsEmptyOrNullString(String s) {
        return (s == null) || (s.trim().length() == 0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        try {
            InputStream is = getResources().getAssets().open("chaptertitle.xml");
            cb = AnalysisUtils.getCourseInfos(is);//getCourseInfos(is)方法在下面会有说明
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (List<CourseBean> ccb:cb
                ) {
            for (CourseBean cbb:ccb
                    ) {
                if(cbb.id-1 == id)
                {
                    Intent intent = new Intent(this,
                            VideoListActivity.class);
                    intent.putExtra("id", cbb.id);
                    intent.putExtra("intro", cbb.intro);
                    intent.putExtra("imgTitle",cbb.imgTitle);
                    intent.putExtra("title",cbb.title);
                    startActivity(intent);
                }
            }

        }

//        Intent intent = new Intent(this,CheckInternetActivity.class);
//        this.finish();
//        startActivity(intent);

    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }
}