package com.joesuperm.custorm.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.joesuperm.custorm.R;

/**
 * @项目名称：517TelMeeting
 * @类名称：DropDownListView
 * @类描述：下拉刷新，上拉加载更多
 * @创建人：huaiying
 * @创建时间：2015年8月3日 上午8:43:00
 * @修改人：huaiying
 * @修改时间：2015年8月3日 上午8:43:00
 * @修改备注：
 * @version
 */
public class DropDownListView extends ListView implements OnScrollListener {
    
    /** 是否允许下拉刷新 */
    private boolean mIsDropDownStyle = true;
    
    /** 是否允许上拉加载更多 */
    private boolean mIsOnBottomStyle = true;
    
    /** 是否自动加载更多 */
    private boolean mIsAutoLoadOnBottom = false;
    
    private String mHeaderLoadDefaultText = "下拉可以刷新";
    
    private String mHeaderPullText = "下拉可以刷新";
    
    private String mHeaderReleaseText = "松开可以刷新";
    
    private String mHeaderLoadingText = "加载中.....";
    
    private String mFooterDefaultText = "加载中.....";
    
    private String mFooterLoadingText = "加载中.....";
    
    private String mFooterNoMoreText = "已加载全部数据";
    
    private String mFooterLoaded = "点击获取更多";
    
    private Context mContext;
    
    /** Header视图 **/
    private RelativeLayout mHeaderLayout;
    
    /** Header箭头图片 */
    private ImageView mHeaderImage;
    
    /** Header加载进度条 */
    private ProgressBar mHeaderProgressBar;
    
    private TextView mHeaderText;
    
    private TextView mHeaderSecondText;
    
    /** Footer视图 **/
    private LinearLayout mFooterLayout;
    
    /** Footer加载进度条 */
    private ProgressBar mFooterProgressBar;
    
    /** Footer点击按钮 */
    private Button mFooterButton;
    
    /** 刷新加载更多处理接口 */
    private OnHandleListener mOnHandleListener;
    
    /** 滚动监听接口 */
    private OnScrollListener mOnScrollListener;
    
    /** rate about drop down distance and header padding top when drop down **/
    private float mHeaderPaddingTopRate = 1.5f;
    
    /** min distance which header can release to loading **/
    private int mHeaderReleaseMinDistance;
    
    /** whether bottom listener has more **/
    private boolean mHasMore = true;
    
    /** whether show footer loading progress bar when loading **/
    private boolean mIsShowFooterProgressBar = true;
    
    /** whether show footer when no more data **/
    private boolean mIsShowFooterWhenNoMore = false;
    
    private int mCurrentScrollState;
    
    private int mCurrentHeaderStatus;
    
    /** whether reached top, when has reached top, don't show header layout **/
    private boolean mHasReachedTop = false;
    
    /** image flip animation **/
    private RotateAnimation mFlipAnimation;
    
    /** image reverse flip animation **/
    private RotateAnimation mReverseFlipAnimation;
    
    /** header layout original height **/
    private int mHeaderOriginalHeight;
    
    /** header layout original padding top **/
    private int mHeaderOriginalTopPadding;
    
    /** y of point which user touch down **/
    private float mActionDownPointY;
    
    /** whether is on bottom loading **/
    private boolean mIsOnBottomLoading = false;
    
    /**
     * 创建一个新的实例 DropDownListView.
     * 
     * @param context
     */
    public DropDownListView(Context context) {
        super(context);
        init(context);
    }
    
    /**
     * 创建一个新的实例 DropDownListView.
     * 
     * @param context
     * @param attrs
     */
    public DropDownListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    
    /**
     * 创建一个新的实例 DropDownListView.
     * 
     * @param context
     * @param attrs
     * @param defStyle
     */
    public DropDownListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
    
    private void init(Context context) {
        this.mContext = context;
        initDropDownStyle();
        initOnBottomStyle();
        // should set, to run onScroll method and so on
        super.setOnScrollListener(this);
    }
    
    private float dp2px(float dpValue) {
        return dpValue * mContext.getResources().getDisplayMetrics().density;
    }
    
    /**
     * init drop down style, only init once
     */
    private void initDropDownStyle() {
        if (mHeaderLayout != null) {
            if (mIsDropDownStyle) {
                addHeaderView(mHeaderLayout);
            }
            else {
                removeHeaderView(mHeaderLayout);
            }
            return;
        }
        if (!mIsDropDownStyle) {
            return;
        }
        
        mHeaderReleaseMinDistance = (int) dp2px(20);
        mFlipAnimation = new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(250);
        mFlipAnimation.setFillAfter(true);
        mReverseFlipAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(250);
        mReverseFlipAnimation.setFillAfter(true);
        
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mHeaderLayout = (RelativeLayout) inflater.inflate(R.layout.drop_down_list_header, this, false);
        mHeaderText = (TextView) mHeaderLayout.findViewById(R.id.drop_down_list_header_default_text);
        mHeaderImage = (ImageView) mHeaderLayout.findViewById(R.id.drop_down_list_header_image);
        mHeaderProgressBar = (ProgressBar) mHeaderLayout.findViewById(R.id.drop_down_list_header_progress_bar);
        mHeaderSecondText = (TextView) mHeaderLayout.findViewById(R.id.drop_down_list_header_second_text);
        mHeaderLayout.setClickable(true);
        mHeaderText.setText(mHeaderLoadDefaultText);
        
        addHeaderView(mHeaderLayout);
        measureHeaderLayout(mHeaderLayout);
        mHeaderOriginalHeight = mHeaderLayout.getMeasuredHeight();
        mHeaderOriginalTopPadding = mHeaderLayout.getPaddingTop();
        // 将Header缩回去，不显示并且设置当前状态可以刷新
        mHeaderLayout.setPadding(0, -mHeaderOriginalHeight, 0, 0);
        mCurrentHeaderStatus = HEADER_STATUS_DROP_DOWN_TO_LOAD;
    }
    
    /**
     * init on bottom style, only init once
     */
    private void initOnBottomStyle() {
        if (mFooterLayout != null) {
            if (mIsOnBottomStyle) {
                addFooterView(mFooterLayout);
            }
            else {
                removeFooterView(mFooterLayout);
            }
            return;
        }
        if (!mIsOnBottomStyle) {
            return;
        }
        
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFooterLayout = (LinearLayout) inflater.inflate(R.layout.drop_down_list_footer, this, false);
        mFooterButton = (Button) mFooterLayout.findViewById(R.id.drop_down_list_footer_button);
        mFooterButton.setDrawingCacheBackgroundColor(0);
        mFooterButton.setEnabled(true);
        mFooterButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                onBottom();
            }
        });
        mFooterProgressBar = (ProgressBar) mFooterLayout.findViewById(R.id.drop_down_list_footer_progress_bar);
        mFooterProgressBar.setVisibility(View.GONE);
        // 在Footer外嵌套一层，是为了控制其是否显示有效
        LinearLayout footerParent = new LinearLayout(mContext);
        footerParent.addView(mFooterLayout);
        addFooterView(footerParent);
        mFooterLayout.setVisibility(View.GONE);
    }
    
    /**
     * @return isDropDownStyle
     */
    public boolean isDropDownEnable() {
        return mIsDropDownStyle;
    }
    
    /**
     * @param isDropDownStyle
     */
    public void setDropDownEnable(boolean isDropDownStyle) {
        if (this.mIsDropDownStyle != isDropDownStyle) {
            this.mIsDropDownStyle = isDropDownStyle;
            initDropDownStyle();
        }
    }
    
    /**
     * @return isOnBottomStyle
     */
    public boolean isLoadMoreEnable() {
        return mIsOnBottomStyle;
    }
    
    /**
     * @param isOnBottomStyle
     */
    public void setLoadMoreEnable(boolean isOnBottomStyle) {
        if (this.mIsOnBottomStyle != isOnBottomStyle) {
            this.mIsOnBottomStyle = isOnBottomStyle;
            initOnBottomStyle();
        }
        
    }
    
    /**
     * @return isAutoLoadOnBottom
     */
    public boolean isAutoLoadOnBottom() {
        return mIsAutoLoadOnBottom;
    }
    
    /**
     * set whether auto load when on bottom
     * 
     * @param isAutoLoadOnBottom
     */
    public void setAutoLoadMore(boolean isAutoLoadOnBottom) {
        this.mIsAutoLoadOnBottom = isAutoLoadOnBottom;
    }
    
    /**
     * get whether show footer loading progress bar when loading
     * 
     * @return the isShowFooterProgressBar
     */
    public boolean isShowFooterProgressBar() {
        return mIsShowFooterProgressBar;
    }
    
    /**
     * set whether show footer loading progress bar when loading
     * 
     * @param isShowFooterProgressBar
     */
    public void setShowFooterProgressBar(boolean isShowFooterProgressBar) {
        this.mIsShowFooterProgressBar = isShowFooterProgressBar;
    }
    
    /**
     * get isShowFooterWhenNoMore
     * 
     * @return the isShowFooterWhenNoMore
     */
    public boolean isShowFooterWhenNoMore() {
        return mIsShowFooterWhenNoMore;
    }
    
    /**
     * set isShowFooterWhenNoMore
     * 
     * @param isShowFooterWhenNoMore
     *            the isShowFooterWhenNoMore to set
     */
    public void setShowFooterWhenNoMore(boolean isShowFooterWhenNoMore) {
        this.mIsShowFooterWhenNoMore = isShowFooterWhenNoMore;
    }
    
    /**
     * get footer button
     * 
     * @return
     */
    public Button getFooterButton() {
        return mFooterButton;
    }
    
    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        if (mIsDropDownStyle) {
            setSecondPositionVisible();
        }
    }
    
    @Override
    public void setOnScrollListener(AbsListView.OnScrollListener listener) {
        mOnScrollListener = listener;
    }
    
    /**
     * @param onDropDownListener
     */
    public void setOnHandleListener(OnHandleListener onDropDownListener) {
        this.mOnHandleListener = onDropDownListener;
    }
    
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mIsDropDownStyle) {
            return super.onTouchEvent(event);
        }
        mHasReachedTop = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mActionDownPointY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getY() > mActionDownPointY) {
                    adjustHeaderPadding(event);
                }
                mCurrentScrollState = SCROLL_STATE_TOUCH_SCROLL;
                break;
            case MotionEvent.ACTION_UP:
                if (!isVerticalScrollBarEnabled()) {
                    setVerticalScrollBarEnabled(true);
                }
                /**
                 * set status when finger leave screen if first item visible and
                 * header status is not HEADER_STATUS_LOADING
                 * <ul>
                 * <li>if current header status is
                 * HEADER_STATUS_RELEASE_TO_LOAD, call onDropDown.</li>
                 * <li>if current header status is
                 * HEADER_STATUS_DROP_DOWN_TO_LOAD, then set header status to
                 * HEADER_STATUS_CLICK_TO_LOAD and hide header layout.</li>
                 * </ul>
                 */
                if (getFirstVisiblePosition() == 0 && mCurrentHeaderStatus != HEADER_STATUS_LOADING && event.getY() > mActionDownPointY) {
                    switch (mCurrentHeaderStatus) {
                        case HEADER_STATUS_RELEASE_TO_LOAD:
                            onDropDown();
                            break;
                        case HEADER_STATUS_DROP_DOWN_TO_LOAD:
                            setSecondPositionVisible();
                            break;
                        default:
                            break;
                    }
                }
                break;
            default:
                
        }
        return super.onTouchEvent(event);
    }
    
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        calculateScroll(firstVisibleItem);
        // if isOnBottomStyle and isAutoLoadOnBottom and hasMore, then call
        // onBottom function auto
        
        if (mIsOnBottomStyle && mHasMore) {
            if (firstVisibleItem > 0 && totalItemCount > 0 && (firstVisibleItem + visibleItemCount == totalItemCount)) {
                // 判断条目满屏后才显示footer
                mFooterLayout.setVisibility(View.VISIBLE);
                if (mIsAutoLoadOnBottom) {
                    onBottom();
                }
            }
        }
        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }
    
    /**
     * @description
     * @date 2015年8月3日
     * @param firstVisibleItem
     */
    private void calculateScroll(int firstVisibleItem) {
        if (mIsDropDownStyle) {
            if (mCurrentScrollState == SCROLL_STATE_TOUCH_SCROLL && mCurrentHeaderStatus != HEADER_STATUS_LOADING) {
                /**
                 * when state of ListView is SCROLL_STATE_TOUCH_SCROLL(ListView
                 * is scrolling and finger is on screen) and header status is
                 * not HEADER_STATUS_LOADING
                 * <ul>
                 * if header layout is visiable,
                 * <li>if height of header is higher than a fixed value, then
                 * set header status to HEADER_STATUS_RELEASE_TO_LOAD.</li>
                 * <li>else set header status to
                 * HEADER_STATUS_DROP_DOWN_TO_LOAD.</li>
                 * </ul>
                 * <ul>
                 * if header layout is not visiable,
                 * <li>set header status to HEADER_STATUS_CLICK_TO_LOAD.</li>
                 * </ul>
                 */
                
                mHeaderImage.setVisibility(View.VISIBLE);
                int pointBottom = mHeaderOriginalHeight + mHeaderReleaseMinDistance;
                if (mHeaderLayout.getBottom() >= pointBottom) {
                    setHeaderStatusReleaseToLoad();
                }
                else if (mHeaderLayout.getBottom() < pointBottom) {
                    setHeaderStatusDropDownToLoad();
                }
            }
            else if (mCurrentScrollState == SCROLL_STATE_FLING && firstVisibleItem == 0 && mCurrentHeaderStatus != HEADER_STATUS_LOADING) {
                /**
                 * when state of ListView is SCROLL_STATE_FLING(ListView is
                 * scrolling but finger has leave screen) and first item(header
                 * layout) is visible and header status is not
                 * HEADER_STATUS_LOADING, then hide first item, set second item
                 * visible and set hasReachedTop true.
                 */
                setSecondPositionVisible();
                mHasReachedTop = true;
            }
            else if (mCurrentScrollState == SCROLL_STATE_FLING && mHasReachedTop) {
                /**
                 * when state of ListView is SCROLL_STATE_FLING(ListView is
                 * scrolling but finger has leave screen) and hasReachedTop is
                 * true(it's because flick back), then hide first item, set
                 * second item visible.
                 */
                setSecondPositionVisible();
            }
        }
    }
    
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mIsDropDownStyle) {
            mCurrentScrollState = scrollState;
            
            if (mCurrentScrollState == SCROLL_STATE_IDLE) {
                mHasReachedTop = false;
            }
        }
        
        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }
    
    /**
     * drop down begin, adjust view status
     */
    private void onDropDownBegin() {
        if (mIsDropDownStyle) {
            setHeaderStatusLoading();
        }
    }
    
    /**
     * on drop down loading, you can call it by manual, but you should manual
     * call onBottomComplete at the same time.
     */
    public void onDropDown() {
        if (mCurrentHeaderStatus != HEADER_STATUS_LOADING && mIsDropDownStyle && mOnHandleListener != null) {
            onDropDownBegin();
            mOnHandleListener.onRefresh();
        }
    }
    
    /**
     * drop down complete, restore view status
     * 
     * @param secondText
     *            display below header text, if null, not display
     */
    public void onDropDownComplete(CharSequence secondText) {
        if (mIsDropDownStyle) {
            setHeaderSecondText(secondText);
            onDropDownComplete();
        }
    }
    
    /**
     * set header second text
     * 
     * @param secondText
     *            secondText display below header text, if null, not display
     */
    public void setHeaderSecondText(CharSequence secondText) {
        if (mIsDropDownStyle) {
            if (secondText == null) {
                mHeaderSecondText.setVisibility(View.GONE);
            }
            else {
                mHeaderSecondText.setVisibility(View.VISIBLE);
                mHeaderSecondText.setText(secondText);
            }
        }
    }
    
    /**
     * drop down complete, restore view status
     */
    public void onDropDownComplete() {
        if (mIsDropDownStyle) {
            if (mHeaderLayout.getBottom() > 0) {
                invalidateViews();
                setSecondPositionVisible();
            }
            mHeaderProgressBar.setVisibility(View.GONE);
            // 将Header缩回去
            mHeaderLayout.setPadding(0, -mHeaderOriginalHeight, 0, 0);
            mCurrentHeaderStatus = HEADER_STATUS_DROP_DOWN_TO_LOAD;
        }
    }
    
    /**
     * on bottom begin, adjust view status
     */
    private void onBottomBegin() {
        if (mIsOnBottomStyle) {
            if (mIsShowFooterProgressBar) {
                mFooterProgressBar.setVisibility(View.VISIBLE);
            }
            mFooterButton.setText(mFooterLoadingText);
            mFooterButton.setEnabled(false);
        }
    }
    
    /**
     * on bottom loading, you can call it by manual, but you should manual call
     * onBottomComplete at the same time.
     */
    public void onBottom() {
        if (mIsOnBottomStyle && !mIsOnBottomLoading) {
            mIsOnBottomLoading = true;
            onBottomBegin();
            mOnHandleListener.onLoadMore();
        }
    }
    
    /**
     * on bottom load complete, restore view status
     */
    public void onBottomComplete() {
        if (mIsOnBottomStyle) {
            if (mIsShowFooterProgressBar) {
                mFooterProgressBar.setVisibility(View.GONE);
            }
            if (!mHasMore) {
                mFooterButton.setText(mFooterNoMoreText);
                mFooterButton.setEnabled(false);
                if (!mIsShowFooterWhenNoMore) {
                    removeFooterView(mFooterLayout);
                }
            }
            else {
                mFooterButton.setText(mFooterLoaded);
                mFooterButton.setEnabled(true);
            }
            mIsOnBottomLoading = false;
        }
    }
    
    /**
     * OnDropDownListener, called when header released
     * 
     * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a>
     *         2012-5-31
     */
    public interface OnHandleListener {
        
        /**
         * called when header released
         */
        void onRefresh();
        
        /**
         * called when refresh completed.
         */
        void onLoadMore();
    }
    
    /**
     * set second position visible(index is 1), because first position is header
     * layout
     */
    public void setSecondPositionVisible() {
        if (getAdapter() != null && getAdapter().getCount() > 0 && getFirstVisiblePosition() == 0) {
            setSelection(1);
        }
        mHeaderLayout.setPadding(0, -mHeaderOriginalHeight, 0, 0);
    }
    
    /**
     * set whether has more. if hasMore is false, onBottm will not be called
     * when listView scroll to bottom
     * 
     * @param hasMore
     */
    public void setHasMore(boolean hasMore) {
        this.mHasMore = hasMore;
    }
    
    /**
     * get whether has more
     * 
     * @return
     */
    public boolean isHasMore() {
        return mHasMore;
    }
    
    /**
     * get header layout view
     * 
     * @return
     */
    public RelativeLayout getHeaderLayout() {
        return mHeaderLayout;
    }
    
    /**
     * get footer layout view
     * 
     * @return
     */
    public LinearLayout getFooterLayout() {
        return mFooterLayout;
    }
    
    /**
     * get rate about drop down distance and header padding top when drop down
     * 
     * @return headerPaddingTopRate
     */
    public float getHeaderPaddingTopRate() {
        return mHeaderPaddingTopRate;
    }
    
    /**
     * set rate about drop down distance and header padding top when drop down
     * 
     * @param headerPaddingTopRate
     */
    public void setHeaderPaddingTopRate(float headerPaddingTopRate) {
        this.mHeaderPaddingTopRate = headerPaddingTopRate;
    }
    
    /**
     * get min distance which header can release to loading
     * 
     * @return headerReleaseMinDistance
     */
    public int getHeaderReleaseMinDistance() {
        return mHeaderReleaseMinDistance;
    }
    
    /**
     * set min distance which header can release to loading
     * 
     * @param headerReleaseMinDistance
     */
    public void setHeaderReleaseMinDistance(int headerReleaseMinDistance) {
        this.mHeaderReleaseMinDistance = headerReleaseMinDistance;
    }
    
    /***
     * get header default text, default is
     * R.string.drop_down_list_header_default_text
     * 
     * @return
     */
    public String getHeaderDefaultText() {
        return mHeaderLoadDefaultText;
    }
    
    /**
     * set header default text, default is
     * R.string.drop_down_list_header_default_text
     * 
     * @param headerDefaultText
     */
    public void setHeaderDefaultText(String headerDefaultText) {
        this.mHeaderLoadDefaultText = headerDefaultText;
        if (mHeaderText != null) {
            mHeaderText.setText(headerDefaultText);
        }
    }
    
    /**
     * get header pull text, default is R.string.drop_down_list_header_pull_text
     * 
     * @return
     */
    public String getHeaderPullText() {
        return mHeaderPullText;
    }
    
    /**
     * set header pull text, default is R.string.drop_down_list_header_pull_text
     * 
     * @param headerPullText
     */
    public void setHeaderPullText(String headerPullText) {
        this.mHeaderPullText = headerPullText;
    }
    
    /**
     * get header release text, default is
     * R.string.drop_down_list_header_release_text
     * 
     * @return
     */
    public String getHeaderReleaseText() {
        return mHeaderReleaseText;
    }
    
    /**
     * set header release text, default is
     * R.string.drop_down_list_header_release_text
     * 
     * @param headerReleaseText
     */
    public void setHeaderReleaseText(String headerReleaseText) {
        this.mHeaderReleaseText = headerReleaseText;
    }
    
    /**
     * get header loading text, default is
     * R.string.drop_down_list_header_loading_text
     * 
     * @return
     */
    public String getHeaderLoadingText() {
        return mHeaderLoadingText;
    }
    
    /**
     * set header loading text, default is
     * R.string.drop_down_list_header_loading_text
     * 
     * @param headerLoadingText
     */
    public void setHeaderLoadingText(String headerLoadingText) {
        this.mHeaderLoadingText = headerLoadingText;
    }
    
    /**
     * get footer default text, default is
     * R.string.drop_down_list_footer_default_text
     * 
     * @return
     */
    public String getFooterDefaultText() {
        return mFooterDefaultText;
    }
    
    /**
     * set footer default text, default is
     * R.string.drop_down_list_footer_default_text
     * 
     * @param footerDefaultText
     */
    public void setFooterDefaultText(String footerDefaultText) {
        this.mFooterDefaultText = footerDefaultText;
        if (mFooterButton != null && mFooterButton.isEnabled()) {
            mFooterButton.setText(footerDefaultText);
        }
    }
    
    /**
     * get footer loading text, default is
     * R.string.drop_down_list_footer_loading_text
     * 
     * @return
     */
    public String getFooterLoadingText() {
        return mFooterLoadingText;
    }
    
    /**
     * set footer loading text, default is
     * R.string.drop_down_list_footer_loading_text
     * 
     * @param footerLoadingText
     */
    public void setFooterLoadingText(String footerLoadingText) {
        this.mFooterLoadingText = footerLoadingText;
    }
    
    /**
     * get footer no more text, default is
     * R.string.drop_down_list_footer_no_more_text
     * 
     * @return
     */
    public String getFooterNoMoreText() {
        return mFooterNoMoreText;
    }
    
    /**
     * set footer no more text, default is
     * R.string.drop_down_list_footer_no_more_text
     * 
     * @param footerNoMoreText
     */
    public void setFooterNoMoreText(String footerNoMoreText) {
        this.mFooterNoMoreText = footerNoMoreText;
    }
    
    /**
     * status which you can drop down and then release to excute
     * onDropDownListener, when height of header layout lower than a value
     **/
    public static final int HEADER_STATUS_DROP_DOWN_TO_LOAD = 2;
    
    /**
     * status which you can release to excute onDropDownListener, when height of
     * header layout higher than a value
     **/
    public static final int HEADER_STATUS_RELEASE_TO_LOAD = 3;
    
    /** status which is loading **/
    public static final int HEADER_STATUS_LOADING = 4;
    
    /**
     * set header status to {@link #HEADER_STATUS_DROP_DOWN_TO_LOAD}
     */
    private void setHeaderStatusDropDownToLoad() {
        if (mCurrentHeaderStatus != HEADER_STATUS_DROP_DOWN_TO_LOAD) {
            mHeaderImage.setVisibility(View.VISIBLE);
            
            mHeaderImage.clearAnimation();
            mHeaderImage.startAnimation(mReverseFlipAnimation);
            mHeaderProgressBar.setVisibility(View.GONE);
            mHeaderText.setText(mHeaderPullText);
            
            if (isVerticalFadingEdgeEnabled()) {
                setVerticalScrollBarEnabled(false);
            }
            
            mCurrentHeaderStatus = HEADER_STATUS_DROP_DOWN_TO_LOAD;
        }
    }
    
    /**
     * set header status to {@link #HEADER_STATUS_RELEASE_TO_LOAD}
     */
    private void setHeaderStatusReleaseToLoad() {
        if (mCurrentHeaderStatus != HEADER_STATUS_RELEASE_TO_LOAD) {
            mHeaderImage.setVisibility(View.VISIBLE);
            mHeaderText.setVisibility(View.VISIBLE);
            mHeaderImage.clearAnimation();
            mHeaderImage.startAnimation(mFlipAnimation);
            mHeaderProgressBar.setVisibility(View.GONE);
            mHeaderText.setText(mHeaderReleaseText);
            
            mCurrentHeaderStatus = HEADER_STATUS_RELEASE_TO_LOAD;
        }
    }
    
    /**
     * set header status to {@link #HEADER_STATUS_LOADING}
     */
    private void setHeaderStatusLoading() {
        if (mCurrentHeaderStatus != HEADER_STATUS_LOADING) {
            resetHeaderPadding();
            
            mHeaderImage.setVisibility(View.GONE);
            mHeaderImage.clearAnimation();
            mHeaderProgressBar.setVisibility(View.VISIBLE);
            mHeaderText.setText(mHeaderLoadingText);
            mCurrentHeaderStatus = HEADER_STATUS_LOADING;
            setSelection(0);
        }
    }
    
    /**
     * adjust header padding according to motion event
     * 
     * @param ev
     */
    private void adjustHeaderPadding(MotionEvent ev) {
        // adjust header padding according to motion event history
        int pointerCount = ev.getHistorySize();
        if (isVerticalFadingEdgeEnabled()) {
            setVerticalScrollBarEnabled(false);
        }
        for (int i = 0; i < pointerCount; i++) {
            if (mCurrentHeaderStatus == HEADER_STATUS_DROP_DOWN_TO_LOAD || mCurrentHeaderStatus == HEADER_STATUS_RELEASE_TO_LOAD) {
                mHeaderLayout.setPadding(mHeaderLayout.getPaddingLeft(),
                                         (int) (((ev.getHistoricalY(i) - mActionDownPointY) - mHeaderOriginalHeight) / mHeaderPaddingTopRate),
                                         mHeaderLayout.getPaddingRight(),
                                         mHeaderLayout.getPaddingBottom());
            }
        }
    }
    
    /**
     * reset header padding
     */
    private void resetHeaderPadding() {
        mHeaderLayout.setPadding(mHeaderLayout.getPaddingLeft(),
                                 mHeaderOriginalTopPadding,
                                 mHeaderLayout.getPaddingRight(),
                                 mHeaderLayout.getPaddingBottom());
    }
    
    /**
     * measure header layout
     * 
     * @param child
     */
    private void measureHeaderLayout(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        }
        else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }
    
}
