# DropDownPullToRefresh
下拉刷新，上拉加载更多，可支持自动加载更多和点击加载更多以及单独支持下拉刷新和上拉加载更多  

#  使用方法
    
    mDropDownListView = (DropDownListView) findViewById(R.id.dropDownListView1);
        //设置自动加载更多
        mDropDownListView.setAutoLoadMore(true);
        //设置允许下拉刷新
        mDropDownListView.setDropDownEnable(true);
        //设置允许加载更多
        mDropDownListView.setLoadMoreEnable(true);
        //设置当没有数据的时候是否显示“已加载全部数据”
        mDropDownListView.setShowFooterWhenNoMore(true);
        //初始化数据源
        mListDataSource = new ArrayList<String>();
        mListDataSource.addAll(Arrays.asList(mStrings));
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mListDataSource);
        //设置适配器
        mDropDownListView.setAdapter(mAdapter);
        //设置刷新、加载处理器
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

# 下拉状态DEMO  

![ABC](https://raw.githubusercontent.com/JoeSuperM/DropDownPullToRefresh/master/DropDownPullToRefresh/demo01.png) 
  
# 上拉自动加载状态  

![ABC](https://raw.githubusercontent.com/JoeSuperM/DropDownPullToRefresh/master/DropDownPullToRefresh/demo02.png) 
  
# 点击加载更多  

![ABC](https://raw.githubusercontent.com/JoeSuperM/DropDownPullToRefresh/master/DropDownPullToRefresh/demo03.png) 
