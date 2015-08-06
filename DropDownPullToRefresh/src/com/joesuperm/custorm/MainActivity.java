/**
 * @项目名称：DropDownPullToRefresh
 * @文件名：MainActivity.java
 * @版本信息：
 * @日期：2015年2月16日
 * @Copyright 2015 www.517na.com Inc. All rights reserved.
 */
package com.joesuperm.custorm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.joesuperm.custorm.view.DropDownListView;
import com.joesuperm.custorm.view.DropDownListView.OnHandleListener;

/**
 * @项目名称：DropDownPullToRefresh
 * @类名称：MainActivity
 * @类描述：
 * @创建人：huaiying
 * @创建时间：2015年2月16日 下午2:30:02
 * @修改人：huaiying
 * @修改时间：2015年2月16日 下午2:30:02
 * @修改备注：
 * @version
 */
public class MainActivity extends Activity {
    private DropDownListView mDropDownListView = null;
    
    private ArrayList<String> mListDataSource = null;
    
    private ArrayAdapter<String> mAdapter = null;
    
    // private String[] mStrings = { "第1项", "第2项", "第3项",
    // "第4项","第5项","第6项","第7项","第8项","第9项","第10项","第11项","第12项","第13项","第14项","第15项","第16项"};
    private String[] mStrings = { "第1项", "第2项", "第3项", "第4项", "第5项" };
    
    // private String[] mStrings = { "第1项"};
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDropDownListView = (DropDownListView) findViewById(R.id.dropDownListView1);
        mDropDownListView.setAutoLoadMore(true);
        mDropDownListView.setDropDownEnable(true);
        mDropDownListView.setLoadMoreEnable(true);
        mDropDownListView.setShowFooterWhenNoMore(true);
        mListDataSource = new ArrayList<String>();
        mListDataSource.addAll(Arrays.asList(mStrings));
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mListDataSource);
        mDropDownListView.setAdapter(mAdapter);
        mDropDownListView.setOnHandleListener(new OnHandleListener() {
            
            @Override
            public void onRefresh() {
                new GetDataTask(true).execute();
                
            }
            
            @Override
            public void onLoadMore() {
                new GetDataTask(false).execute();
                
            }
        });
        
    }
    
    private class GetDataTask extends AsyncTask<Void, Void, String[]> {
        
        private boolean isDropDown;
        
        public GetDataTask(boolean isDropDown) {
            this.isDropDown = isDropDown;
        }
        
        @Override
        protected String[] doInBackground(Void... params) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                ;
            }
            return mStrings;
        }
        
        @Override
        protected void onPostExecute(String[] result) {
            
            if (isDropDown) {
                List<String> list = new ArrayList<String>();
                list.add("Added after drop down");
                for (int i = 1; i <= 5; i++) {
                    list.add("刷新的第" + i + "项");
                }
                mListDataSource.addAll(0, list);
                mAdapter.notifyDataSetChanged();
                // 下拉刷新完成后需要调用onDropDownComplete方法。
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault());
                mDropDownListView.onDropDownComplete("上次刷新时间:" + dateFormat.format(new Date()));
            }
            else {
                List<String> list = new ArrayList<String>();
                list.add("Added after on bottom");
                for (int i = 1; i <= 5; i++) {
                    list.add("加载的第" + i + "项");
                }
                mListDataSource.addAll(list);
                mAdapter.notifyDataSetChanged();
                if ((int) Math.random() * 10 == 5) {
                    mDropDownListView.setHasMore(false);
                }
                // 上拉加载更多完成后需要调用onBottomComplete
                mDropDownListView.onBottomComplete();
            }
            
            super.onPostExecute(result);
        }
    }
}
