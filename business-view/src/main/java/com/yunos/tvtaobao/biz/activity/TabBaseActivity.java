package com.yunos.tvtaobao.biz.activity;


import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.taobao.statistic.CT;
import com.taobao.statistic.TBS;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.app.widget.AdapterView;
import com.yunos.tv.app.widget.AdapterView.OnItemClickListener;
import com.yunos.tv.app.widget.FlipGridView.OnFlipRunnableListener;
import com.yunos.tv.app.widget.focus.FocusFlipGridView;
import com.yunos.tv.app.widget.focus.FocusFlipGridView.OnFocusFlipGridViewListener;
import com.yunos.tv.app.widget.focus.FocusImageView;
import com.yunos.tv.app.widget.focus.FocusListView;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.common.DeviceJudge;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.adapter.TabGoodsBaseAdapter;
import com.yunos.tvtaobao.biz.listener.TabFocusFlipGridViewListener;
import com.yunos.tvtaobao.biz.listener.TabFocusListViewListener;
import com.yunos.tvtaobao.biz.listener.VerticalItemHandleListener;
import com.yunos.tvtaobao.biz.widget.TabFocusPositionManager;
import com.yunos.tvtaobao.biz.widget.TabGoodsItemView;
import com.yunos.tvtaobao.businessview.R;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class TabBaseActivity extends BaseActivity {

    protected static final int WHAT_KEYUPEVENT = 1000;
    protected static final int WHAT_TAB_LAYOUT_END = 1002;

    protected final String TAG = getClass().getSimpleName() + "@" + Integer.toHexString(hashCode());

    // 按键抬起时，发送按键处理的延时时间
    private final long KEY_DELAY = 500;
    protected final int ALL_VALUE = -5;

    // 左侧分类容器
    protected FocusListView mTabFocusListView;

    //右侧穿透栏
    protected FocusImageView fiv_pierce_home_focusd;
    protected FocusImageView fiv_pierce_my_focusd;
    protected FocusImageView fiv_pierce_cart_focusd;
    protected FocusImageView fiv_pierce_red_packet_focusd;
    protected FocusImageView fiv_pierce_block_focusd;
    protected FocusImageView fiv_pierce_contact_focusd;
    protected FocusImageView fiv_pierce_come_back_focusd;
    protected FocusImageView fiv_pierce_red_jifen_focusd;
    protected ImageView fiv_pierce_background;

    protected TextView tv_pierce_home;
    protected TextView tv_pierce_my;
    protected TextView tv_pierce_cart;
    protected TextView tv_pierce_red_packet;
    protected TextView tv_pierce_block;
    protected TextView tv_pierce_contact_focusd;
    protected TextView tv_pierce_come_back;
    protected TextView tv_pierce_red_jifen;

    protected ImageView iv_pierce_cart_active;

    // 顶部蒙版
    protected ImageView mShadowTop;

    // 低部蒙版
    protected ImageView mShadowBottom;

    // 暂无商品提示的IEW
    protected TextView mEmptyView;

    // girdview的容器
    protected FrameLayout mGoodsListDisplayContainer;

    // 界面主的PositionManager
    protected TabFocusPositionManager mTabFocusPositionManager;
    // 商品选中时的FocusDrawable
    protected StaticFocusDrawable mGoodsFocusDrawable;

    // 左侧TAB选中时的FocusDrawable
    protected StaticFocusDrawable mTabFocusDrawable;

    // GirdView 的 Padding
    protected Rect mGirdViewPadding;
    // GirdView 加入到  mGoodsListDisplayContainer 容器中的 Margin
    protected Rect mGirdViewMargin;

    // GirdView 的 Width 和 Height
    protected int mGirdViewWidth;
    protected int mGirdViewHeight;

    // 左侧TAB的 Listener
    private TabFocusListViewListener mTabFocusListViewListener;
    // GridView 的  Listener
    private TabFocusFlipGridViewListener mTabFocusFlipGridViewListener;

    // 存放GridViewView 
    private HashMap<String, FocusFlipGridView> mGoodsGirdViewMap;

    // 存放是否已经加载过数据
    private HashMap<String, Boolean> mTabGoodsFirstRequestMap;

    private Handler mHandler;

    // 当前选中的TAB号
    protected int mCurrentSelectTabNumBer;

    // 切换之前的 TAB号 
    protected int oldTabNumBer;

    // 切换后的TAB号 
    protected int newTabNumBer;

    // 焦点是否可以从菜单移到右边商品上
    private boolean mCanMoveRight;

    // 当前焦点是否在TAB上面
    private boolean mMenuFocusGain;

    private boolean mFirstRequestClassify;
    // 第一次请求网络数据
    private boolean mFirstRequestGoodsData;

    // TAB是否是第一次获得焦点
    private boolean mTabFirstGain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ytbv_common_havetab_layout);

        mTabFocusPositionManager = (TabFocusPositionManager) findViewById(R.id.common_havetab_mainLayout);

        initPierceViews();


        int defaultBack = getResources().getColor(R.color.ytbv_havetab_black);

        // 默认颜色填充背景
        onHandleCommonBackgroudColor(defaultBack);

        // 初始化变量值
        onInitTabBaseVariableValue();

        // 创建商品的Focus框
        onTabBaseCreatGoodsFocusDrawable();
    }

    /**
     * 初始化TabBaseActivity， 针对 有TAB的界面
     */
    public void onInitTabBaseActivity() {

        mHandler = new TabBaseHandler(new WeakReference<TabBaseActivity>(this));

        // 获取布局参数
        onTabBaseReadLayoutValue();

        // 重新调整布局，或者添加布局等处理
        onReAdjustCommonLayout(mTabFocusPositionManager);

        // 创建TAB的Focus
        onTabBaseCreatTabFocusDrawable();

        // 初始化FocusList
        setTabFocusListViewListen();

        // 开始让左侧TAB获得焦点 
        onStartRefreshTabFocusListView();

        //初始化右侧穿透栏焦点
        onRightPierceFocus();
    }

    /**
     * 获取默认的选项
     *
     * @return
     */
    protected int getDefaultSelection() {
        return 0;
    }

    /**
     * 初始化界面中的变量值
     */
    private void onInitTabBaseVariableValue() {

        mTabFirstGain = true;
        mMenuFocusGain = true;
        mCanMoveRight = true;
        mCurrentSelectTabNumBer = -1;
        oldTabNumBer = -1;
        newTabNumBer = -1;
        mGoodsFocusDrawable = null;
        mTabFocusDrawable = null;
        mFirstRequestClassify = true;
        mFirstRequestGoodsData = true;

        mGoodsGirdViewMap = new HashMap<String, FocusFlipGridView>();
        mGoodsGirdViewMap.clear();

        mTabGoodsFirstRequestMap = new HashMap<String, Boolean>();
        mTabGoodsFirstRequestMap.clear();
    }

    /**
     * 读取布局参数
     */
    private void onTabBaseReadLayoutValue() {

        mGirdViewPadding = new Rect();
        mGirdViewPadding.setEmpty();
        mGirdViewPadding.left = this.getResources().getDimensionPixelSize(R.dimen.dp_25);
        //mGirdViewPadding.left = this.getResources().getDimensionPixelSize(R.dimen.dp_50);//不加穿透参数
        mGirdViewPadding.top = this.getResources().getDimensionPixelSize(R.dimen.dp_110);

        mGirdViewPadding.right = this.getResources().getDimensionPixelSize(R.dimen.dp_46);
        mGirdViewPadding.bottom = this.getResources().getDimensionPixelSize(R.dimen.dp_90);

        mGirdViewMargin = new Rect();
        mGirdViewMargin.setEmpty();
        //mGirdViewMargin.left = this.getResources().getDimensionPixelSize(R.dimen.dp_13);//不加穿透参数
        mGirdViewMargin.left = this.getResources().getDimensionPixelSize(R.dimen.dp_0);
        mGirdViewMargin.top = this.getResources().getDimensionPixelSize(R.dimen.dp_0);

        mGirdViewWidth = this.getResources().getDimensionPixelSize(R.dimen.dp_1053);
        mGirdViewHeight = FrameLayout.LayoutParams.MATCH_PARENT;

        // 获取商品展示容器
        mGoodsListDisplayContainer = (FrameLayout) findViewById(R.id.common_gridview_container);
        mGoodsListDisplayContainer.setVisibility(View.VISIBLE);

        // 顶部蒙版
        mShadowTop = (ImageView) this.findViewById(R.id.common_top_mask_view);
        mShadowTop.setVisibility(View.INVISIBLE);

        // 底部蒙版
        mShadowBottom = (ImageView) this.findViewById(R.id.common_bottom_mask_view);

        // 暂无商品的提示
        mEmptyView = (TextView) this.findViewById(R.id.common_nodata_view);

        // 左边的分类
        mTabFocusListView = (FocusListView) this.findViewById(R.id.common_focuslistview);


    }

    private void initPierceViews() {
        //右边穿透
        fiv_pierce_home_focusd = (FocusImageView) this.findViewById(R.id.fiv_pierce_home_focusd);
        fiv_pierce_my_focusd = (FocusImageView) this.findViewById(R.id.fiv_pierce_my_focusd);
        fiv_pierce_cart_focusd = (FocusImageView) this.findViewById(R.id.fiv_pierce_cart_focusd);
        fiv_pierce_red_packet_focusd = (FocusImageView) this.findViewById(R.id.fiv_pierce_red_packet_focusd);
        fiv_pierce_block_focusd = (FocusImageView) this.findViewById(R.id.fiv_pierce_block_focusd);
        fiv_pierce_contact_focusd = (FocusImageView) this.findViewById(R.id.fiv_pierce_contact_focusd);
        fiv_pierce_come_back_focusd = (FocusImageView) this.findViewById(R.id.fiv_pierce_come_back_focusd);
        fiv_pierce_red_jifen_focusd = (FocusImageView) this.findViewById(R.id.fiv_pierce_red_jifen_focusd);

        fiv_pierce_background = (ImageView) findViewById(R.id.fiv_pierce_background);

        tv_pierce_home = (TextView) this.findViewById(R.id.tv_pierce_home);
        tv_pierce_my = (TextView) this.findViewById(R.id.tv_pierce_my);
        tv_pierce_cart = (TextView) this.findViewById(R.id.tv_pierce_cart);
        tv_pierce_red_packet = (TextView) this.findViewById(R.id.tv_pierce_red_packet);
        tv_pierce_block = (TextView) this.findViewById(R.id.tv_pierce_block);
        tv_pierce_contact_focusd = (TextView) this.findViewById(R.id.tv_pierce_contact_focusd);
        tv_pierce_come_back = (TextView) this.findViewById(R.id.tv_pierce_come_back);
        tv_pierce_red_jifen = (TextView) this.findViewById(R.id.tv_pierce_red_jifen);
        iv_pierce_cart_active = (ImageView) this.findViewById(R.id.iv_pierce_cart_active);
    }


    /**
     * 设置右侧穿投栏
     */
    private void onRightPierceFocus() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fiv_pierce_home_focusd.setFocusable(true);
                fiv_pierce_my_focusd.setFocusable(true);
                fiv_pierce_cart_focusd.setFocusable(true);
                fiv_pierce_red_packet_focusd.setFocusable(true);
                fiv_pierce_block_focusd.setFocusable(true);
                fiv_pierce_contact_focusd.setFocusable(true);
                fiv_pierce_come_back_focusd.setFocusable(true);
                fiv_pierce_red_jifen_focusd.setFocusable(true);
            }
        }, 1000);
    }

    /**
     * 设置左侧TAB的参数
     */
    private void setTabFocusListViewListen() {

        mTabFocusListView.setAnimateWhenGainFocus(false, false, false, false);
        mTabFocusListView.setFocusBackground(true);
        mTabFocusListView.setFlipScrollFrameCount(8);
        mTabFocusListView.setAdapter(getTabAdapter());
        mTabFocusListView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                AppDebug.i(TAG, "mTabFocusListView --> setOnFocusChangeListener --> onFocusChange --> v = " + v
                        + "; hasFocus = " + hasFocus);

                if (mTabFocusListViewListener != null) {
                    mTabFocusListViewListener.onFocusChange(v, hasFocus);
                }

                if (hasFocus) {
                    mMenuFocusGain = true;
                    // 重新设置选择器
                    if (mTabFocusPositionManager != null) {
                        mTabFocusPositionManager.setSelector(mTabFocusDrawable);
                    }
                    // 第一次获得焦点时的处理
                    //                    if (mTabFirstGain) {
                    //                        mTabFirstGain = false;
                    //                        oldTabNumBer = 0;
                    //                        newTabNumBer = -1;
                    //                        onHandleTabFoucus(getDefaultSelection(), true);
                    //                    }
                }
            }
        });

        // 设置分类中的监听
        mTabFocusListView.setOnItemSelectedListener(new ItemSelectedListener() {

            @Override
            public void onItemSelected(View select, int position, boolean isSelect, View fatherView) {
                AppDebug.v(TAG, TAG + ".mTabFocusListView.onItemSelected.selectView = " + select + ".position = "
                        + position + ".isSelect = " + isSelect + ".fatherView = " + fatherView);
                if (mTabFocusListViewListener != null) {
                    mTabFocusListViewListener.onItemSelected(select, position, isSelect, fatherView);
                }

                if (isSelect) {
                    // 选中时，记录新的TAB号
                    newTabNumBer = position;
                }
            }
        });

        mTabFocusListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppDebug.i(TAG, TAG + ".mTabFocusListView.onItemClick.AdapterView = " + parent + ".selectedView = "
                        + view + ".position = " + position + ".row_id = " + id);

                newTabNumBer = position;
                if (mHandler != null) {
                    if (mHandler.hasMessages(WHAT_KEYUPEVENT)) {
                        mHandler.removeMessages(WHAT_KEYUPEVENT);
                    }
                    mHandler.sendEmptyMessageDelayed(WHAT_KEYUPEVENT, KEY_DELAY);
                }

                if (mTabFocusListViewListener != null) {
                    mTabFocusListViewListener.onItemClick(parent, view, position, id);
                }
            }
        });
    }

    /**
     * 通知左侧TAB的适配器刷新数据， 并开始让左侧TAB获得焦点
     */
    private void onStartRefreshTabFocusListView() {
        // 刷新适配
        BaseAdapter baseAdapter = getTabAdapter();
        if (baseAdapter != null) {
            if ((mFirstRequestClassify) && (mTabFocusListView != null)) {
                mFirstRequestClassify = false;
                mTabFocusListView.setVisibility(View.VISIBLE);
            }
            baseAdapter.notifyDataSetChanged();
        }

        HandleFirstRequestGoodsData();
    }

    /**
     * 处理菜单选中光标
     */
    private void HandleFirstRequestGoodsData() {
        AppDebug.i(TAG, "HandleFirstRequestGoodsData --> mFirstRequestGoodsData = " + mFirstRequestGoodsData
                + "; mTabFocusListView = " + mTabFocusListView);
        if (mFirstRequestGoodsData && mTabFocusListView != null) {
            mFirstRequestGoodsData = false;
            mTabFocusListView.setSelection(getDefaultSelection());
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(WHAT_TAB_LAYOUT_END, 500);
            }
        }
    }

    /**
     * 左侧TAB请求焦点
     */
    private void focusPositionManagerrequestFocus() {
        AppDebug.i(TAG, "focusPositionManagerrequestFocus --> mTabFocusPositionManager = " + mTabFocusPositionManager
                + "; mTabFocusListView = " + mTabFocusListView);
        if (mTabFocusPositionManager != null) {
            mTabFocusPositionManager.resetFocused();
            mTabFocusListView.requestFocus();
        }

        if (mTabFirstGain) {
            mTabFirstGain = false;
            oldTabNumBer = 0;
            newTabNumBer = -1;
            onHandleTabFoucus(getDefaultSelection(), true);
        }
    }

    /**
     * 创建TAB的Focus框
     */
    private void onTabBaseCreatTabFocusDrawable() {
        if (getTabFocusDrawableId() != 0)
            mTabFocusDrawable = new StaticFocusDrawable(getResources().getDrawable(getTabFocusDrawableId()));
        AppDebug.i(TAG, "onTabBaseCreatTabFocusDrawable -->    mTabFocusDrawable  = " + mTabFocusDrawable);
        if (mTabFocusPositionManager != null) {
            mTabFocusPositionManager.setSelector(mTabFocusDrawable);
        }
    }

    /**
     * 创建商品的Focus框
     */
    private void onTabBaseCreatGoodsFocusDrawable() {
        mGoodsFocusDrawable = new StaticFocusDrawable(getResources().getDrawable(getGoodsFocusDrawableId()));
        AppDebug.i(TAG, "onTabBaseCreatGoodsFocusDrawable -->    mGoodsFocusDrawable  = " + mGoodsFocusDrawable);
        if (mTabFocusPositionManager != null) {
            mTabFocusPositionManager.setSelector(mGoodsFocusDrawable);
        }
    }

    /**
     * 根据tabKey创建 GridView
     *
     * @param position
     */
    private void onCreatGoodsListGridView(final String tabKey, final int position) {

        AppDebug.i(TAG, "onCreatGoodsListGridView -->    position  = " + position + "; tabKey = " + tabKey);

        if (mGoodsGirdViewMap == null) {
            return;
        }

        if (tabKey == null) {
            return;
        }

        if (mGirdViewPadding == null) {
            return;
        }

        if (mGoodsGirdViewMap.containsKey(tabKey)) {
            // 如果已经存在，则退出
            AppDebug.i(TAG, "onCreatGoodsListGridView -->   GoodListLifeUiGridView  is contains ");
            return;
        }

        // 创建对应的适配器 
        final TabGoodsBaseAdapter adapter = getTabGoodsAdapter(tabKey, position);

        // 获取 FocusFlipGridView
        final FocusFlipGridView goodListLifeUiGridView = getTabGoodsGridView(tabKey, position);

        if (DeviceJudge.isLowDevice()) {
            goodListLifeUiGridView.setDelayAnim(false);
        }

        // 设置 FocusFlipGridView 的参数
        goodListLifeUiGridView.setNumColumns(4);
        goodListLifeUiGridView.setFlipScrollFrameCount(5);
        goodListLifeUiGridView.setNeedAutoSearchFocused(false);
        goodListLifeUiGridView.setAnimateWhenGainFocus(false, true, true, true);
        int VerticalSpacing = this.getResources().getDimensionPixelSize(R.dimen.dp_20);
        goodListLifeUiGridView.setVerticalSpacing(VerticalSpacing);
        int HorizontalSpacing = this.getResources().getDimensionPixelSize(R.dimen.dp_16);
        goodListLifeUiGridView.setHorizontalSpacing(HorizontalSpacing);
        goodListLifeUiGridView.setStretchMode(GridView.NO_STRETCH);

        // 初始化GirdView，包括可以修改GirdView的布局参数，添加headerView
        initGirdViewInfo(goodListLifeUiGridView, adapter, tabKey, position);

        AppDebug.i(TAG, "onCreatGoodsListGridView --> mGirdViewPadding = " + mGirdViewPadding);

        // 根据 mGirdViewPadding 设定的值，设置 goodListLifeUiGridView 的 padding
        goodListLifeUiGridView.setPadding(mGirdViewPadding.left, mGirdViewPadding.top, mGirdViewPadding.right,
                mGirdViewPadding.bottom);

        // 设置绑定的商品列表View
        adapter.onSetFocusFlipGridView(goodListLifeUiGridView);

        // 设置主线程的handler
        adapter.onSetMainHandler(mHandler);

        // 获得焦点的监听
        goodListLifeUiGridView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AppDebug.i(TAG, TAG + ".goodListLifeUiGridView.onFocusChange ---> v = " + v + "; hasFocus = "
                        + hasFocus);
                if (hasFocus) {
                    mMenuFocusGain = false;
                    if (mTabFocusPositionManager != null) {
                        if (mGoodsFocusDrawable == null) {
                            mGoodsFocusDrawable = new StaticFocusDrawable(getResources().getDrawable(
                                    getGoodsFocusDrawableId()));
                        }
                        AppDebug.i(TAG, TAG + ".goodListLifeUiGridView.onFocusChange ---> mGoodsFocusDrawable = "
                                + mGoodsFocusDrawable);
                        mTabFocusPositionManager.setSelector(mGoodsFocusDrawable);
                    }
                }
                if (mTabFocusFlipGridViewListener != null) {
                    mTabFocusFlipGridViewListener.onFocusChange(goodListLifeUiGridView, tabKey, v, hasFocus);
                }
            }
        });

        // 滚动的监听
        goodListLifeUiGridView.setOnFocusFlipGridViewListener(new OnFocusFlipGridViewListener() {

            @Override
            public void onLayoutDone(boolean isFirst) {

                showGirdView(position);

                if (mTabFocusFlipGridViewListener != null) {
                    mTabFocusFlipGridViewListener.onLayoutDone(goodListLifeUiGridView, tabKey, isFirst);
                }

                if (DeviceJudge.isLowDevice()) {
                    // 针对低配版的处理
                }
            }

            @Override
            public void onOutAnimationDone() {
                goodListLifeUiGridView.setVisibility(View.GONE);
            }

            @Override
            public void onReachGridViewBottom() {
            }

            @Override
            public void onReachGridViewTop() {
            }

        });

        // 设置选中时的监听
        goodListLifeUiGridView.setOnItemSelectedListener(new ItemSelectedListener() {

            @Override
            public void onItemSelected(View selectview, int position, boolean isSelect, View parent) {

                AppDebug.i(TAG, "goodListLifeUiGridView--> onItemSelected -->  selectview = " + selectview
                        + "; position = " + position + ";  isSelect = " + isSelect);
                if (mTabFocusFlipGridViewListener != null) {
                    mTabFocusFlipGridViewListener.onItemSelected(goodListLifeUiGridView, tabKey, selectview, position,
                            isSelect, parent);
                }
                if (selectview instanceof TabGoodsItemView && selectview != null) {
                    HandleSelectView(selectview, position, isSelect);
                }
            }
        });

        // 设置获取View时，是否请求数据
        adapter.setOnVerticalItemHandleListener(new VerticalItemHandleListener() {

            @Override
            public boolean onGetview(ViewGroup container, String ordey, int position) {
                if (mTabFocusFlipGridViewListener != null) {
                    mTabFocusFlipGridViewListener.onGetview(goodListLifeUiGridView, tabKey, position,
                            mCurrentSelectTabNumBer);
                }
                return true;
            }
        });

        // 设置单击事件
        goodListLifeUiGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View selectedView, int position, long row) {
                AppDebug.i(TAG, TAG + ".goodListLifeUiGridView.onItemClick.AdapterView = " + adapterView + ".selectedView = "
                        + selectedView + ".position = " + position + ".row_id = " + row);
                if (mTabFocusFlipGridViewListener != null) {
                    boolean result = mTabFocusFlipGridViewListener.onItemClick(goodListLifeUiGridView, tabKey, adapterView,
                            selectedView, position, row);
                    if (result) {
                        return;
                    }
                }

                if (goodListLifeUiGridView.isScrolling()) {
                    goodListLifeUiGridView.forceResetFocusParams(mTabFocusPositionManager);
                }
                // 获取进入详情页面的商品ID号
                String itemId = getItemIdOfEnterdetail(goodListLifeUiGridView, tabKey, adapterView, selectedView, position, row);
                String eurl = getEurlOfEnterdetail(goodListLifeUiGridView, tabKey, adapterView, selectedView, position, row);
                String uri = getUriOfEnterDetail(goodListLifeUiGridView, tabKey, adapterView, selectedView, position, row);
                String title = getTitleOfEnterDetail(goodListLifeUiGridView, tabKey, adapterView, selectedView, position, row);
                String picUrl = getPicUrlOfEnterDetail(goodListLifeUiGridView, tabKey, adapterView, selectedView, position, row);
                if (!TextUtils.isEmpty(itemId))
                    //进入详情界面
                    enterDisplayDetail(itemId, null);
                else if (!TextUtils.isEmpty(eurl)) {
                    enterDisplayDetailAsync(title, picUrl, eurl);
                } else if (!TextUtils.isEmpty(uri)) {
                    enterDisplayDetailUri(uri);
                }

                // 统一处理单击商品时的埋点
                ListAdapter listAdapter = (android.widget.ListAdapter) adapterView.getAdapter();
                if (listAdapter instanceof TabGoodsBaseAdapter && listAdapter != null) {
                    TabGoodsBaseAdapter baseAdapter = (TabGoodsBaseAdapter) listAdapter;
                    if (baseAdapter != null) {
                        if (TextUtils.isEmpty(eurl)) {
                            AppDebug.i("getFullPageName", "getFullPageName" + getFullPageName());
                            if (!TextUtils.isEmpty(getFullPageName()) && getFullPageName().equals("TbGoodsSeachResult")) {

                                Map<String, String> p = new HashMap<String, String>();
                                p.put("itemId", itemId);
                                p.put("name", baseAdapter.getGoodsTitle(tabKey, position));
                                p.put("uuid", CloudUUIDWrapper.getCloudUUID());
                                p.put("from_channel", getmFrom());
                                p.put("spm", SPMConfig.GOODS_LIST_SPM_ITEM_P_NAME);

                                Utils.utControlHit(getFullPageName(), "Button-TbGoodsSeachResult_P_Goods_" + position, p);
                                Utils.updateNextPageProperties(SPMConfig.GOODS_LIST_SPM_ITEM_P_NAME);

                            } else {
                                TBS.Adv.ctrlClicked(CT.Button, getFullPageName() + "_P_Goods_" + position, "itemId=" + itemId,
                                        "name=" + baseAdapter.getGoodsTitle(tabKey, position),
                                        "uuid=" + CloudUUIDWrapper.getCloudUUID(), "from_channel=" + getmFrom());

                            }
                        } else {
                            //TODO
                            TBS.Adv.ctrlClicked(CT.Button, getFullPageName() + "_Z_P_Goods_" + position, "itemId=" + itemId,
                                    "name=" + baseAdapter.getGoodsTitle(tabKey, position),
                                    "uuid=" + CloudUUIDWrapper.getCloudUUID(), "from_channel=" + getmFrom());
                        }
                    }
                }
            }
        });

        // 设置滚动事件
        goodListLifeUiGridView.setOnFlipGridViewRunnableListener(new OnFlipRunnableListener() {

            @Override
            public void onFinished() {
                AppDebug.i(TAG, "setOnFlipGridViewRunnableListener   onFinished ... mMenuFocusGain = " + mMenuFocusGain);

                int selectPos = goodListLifeUiGridView.getSelectedItemPosition();
                // 处理选中时，释放图片资源
                adapter.onItemSelected(selectPos, true, goodListLifeUiGridView);
                adapter.setIsNotifyDataSetChanged(false);
                // 检查图片和商品的加载
                onSetCheckVisibleItemOfAdapter(goodListLifeUiGridView, adapter);

                if (!mMenuFocusGain) {
                    // 如果是goodListLifeUiGridView得到焦点 
                    View view = goodListLifeUiGridView.getSelectedView();
                    if (view instanceof TabGoodsItemView && view != null) {
                        HandleSelectView(view, selectPos, true);
                    }
                }

                if (mTabFocusFlipGridViewListener != null) {
                    mTabFocusFlipGridViewListener.onFinished(goodListLifeUiGridView, tabKey);
                }
            }

            @Override
            public void onFlipItemRunnable(float arg0, View arg1, int arg2) {
            }

            @Override
            public void onStart() {
                AppDebug.i(TAG, "setOnFlipGridViewRunnableListener.onStart ... ");
                if (mTabFocusFlipGridViewListener != null) {
                    mTabFocusFlipGridViewListener.onStart(goodListLifeUiGridView, tabKey);
                }
                // 取消所有图片的加载
                ImageLoaderManager.getImageLoaderManager(getApplicationContext()).cancelLoadAllTaskFor();
            }

        });

        // 设置 TabKey 
        adapter.onSetTabKey(tabKey);

        //设置 adapter
        goodListLifeUiGridView.setAdapter(adapter);
        goodListLifeUiGridView.setVisibility(View.GONE);

        if (mGoodsListDisplayContainer != null) {
            // 把 goodListLifeUiGridView 添加到 容器 mGoodsListDisplayContainer 中
            FrameLayout.LayoutParams contanerLp = new FrameLayout.LayoutParams(mGirdViewWidth, mGirdViewHeight);
            contanerLp.setMargins(mGirdViewMargin.left, mGirdViewMargin.top, 0, 0);
            // 居中 
            contanerLp.gravity = Gravity.CENTER_HORIZONTAL;
            mGoodsListDisplayContainer.addView(goodListLifeUiGridView, contanerLp);
            if (mTabFocusPositionManager != null) {
                mTabFocusPositionManager.setGridView(goodListLifeUiGridView);
            }
        }

        mGoodsGirdViewMap.put(tabKey, goodListLifeUiGridView);

        mTabGoodsFirstRequestMap.put(tabKey, true);
    }


    /**
     * 检查图片和商品信息的加载
     *
     * @param adapter
     */
    protected void onSetCheckVisibleItemOfAdapter(FocusFlipGridView fenLeiGoodsGridView, TabGoodsBaseAdapter adapter) {
        adapter.onSetCheckVisibleItem(true);
        adapter.onCheckVisibleItemAndLoadBitmap();
    }

    /**
     * 设置左侧TAB的Listener
     *
     * @param tabFocusListViewListener
     */
    public void setTabFocusListViewListener(TabFocusListViewListener tabFocusListViewListener) {
        mTabFocusListViewListener = tabFocusListViewListener;
    }

    /**
     * 设置 gridview的Listener
     *
     * @param tabFocusFlipGridViewListener
     */
    public void setTabFocusFlipGridViewListener(TabFocusFlipGridViewListener tabFocusFlipGridViewListener) {
        mTabFocusFlipGridViewListener = tabFocusFlipGridViewListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN && mTabFocusPositionManager != null) {
            mTabFocusPositionManager.setCanScroll(false);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyAction = event.getAction();
        int keyCode = event.getKeyCode();

        // 添加LOG，以便观察各值
        AppDebug.i(TAG, "dispatchKeyEvent --> keyCode = " + keyCode + "; mCanMoveRight = " + mCanMoveRight
                + "; mMenuFocusGain = " + mMenuFocusGain);

        if (event.getAction() == MotionEvent.ACTION_DOWN && mTabFocusPositionManager != null) {
            mTabFocusPositionManager.setCanScroll(true);
        }

        // 如果当前是菜单处获得焦点， 并且不能向右，则不作向右的处理
        if ((keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) && (!mCanMoveRight) && (mMenuFocusGain)) {
            return true;
        }

        boolean keyResult = super.dispatchKeyEvent(event);

        if (keyAction == KeyEvent.ACTION_UP) {
            // 如果是菜单获得焦点
            if (mMenuFocusGain) {
                // 并且有新的菜单项被选中,那么处理
                if (newTabNumBer != -1) {
                    if (mHandler != null) {
                        mCanMoveRight = false;
                        mHandler.sendEmptyMessageDelayed(WHAT_KEYUPEVENT, KEY_DELAY);
                    }
                } else {
                    mCanMoveRight = true;
                }

            }
        } else if (keyAction == KeyEvent.ACTION_DOWN) {
            if (mHandler != null) {
                mHandler.removeMessages(WHAT_KEYUPEVENT);
            }
        }
        return keyResult;
    }

    @Override
    protected void onDestroy() {

        onClearBufferData();

        super.onDestroy();

    }

    /**
     * 清除缓冲区数据
     */
    public void onClearBufferData() {

        if (mGoodsGirdViewMap != null) {
            mGoodsGirdViewMap.clear();
        }

        if (mTabGoodsFirstRequestMap != null) {
            mTabGoodsFirstRequestMap.clear();
        }

        if (mGoodsListDisplayContainer != null) {
            mGoodsListDisplayContainer.removeAllViews();
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 处理界面的背景色
     *
     * @param backgroudcolor
     */
    protected void onHandleCommonBackgroudColor(int backgroudcolor) {
        if (mTabFocusPositionManager != null) {
            mTabFocusPositionManager.setBackgroudColor(backgroudcolor);
        }
    }

    /**
     * 当按键发生KeyUp的处理
     */
    private void onHandleKeyUpEvent() {

        AppDebug.i(TAG, "onHandleKeyUpEvent -->    newTabNumBer  = " + newTabNumBer + "; oldTabNumBer = "
                + oldTabNumBer);

        if (newTabNumBer != -1) {

            // 旧TAB失去焦点
            onHandleTabFoucus(oldTabNumBer, false);

            // 新TAB获得焦点
            onHandleTabFoucus(newTabNumBer, true);

            oldTabNumBer = newTabNumBer;
            newTabNumBer = -1;
        }
    }

    /**
     * 处理分类被选中
     *
     * @param postion
     * @param hasFocus
     */
    private void onHandleTabFoucus(int postion, boolean hasFocus) {

        if (mGoodsGirdViewMap == null) {
            return;
        }

        final String tabKeyString = getTabKeyWordOfTabNumBer(postion);

        AppDebug.i(TAG, "onHandleTabFoucus -->   mCurrentSelectTabNumBer   = " + mCurrentSelectTabNumBer
                + ";  tabKeyString  = " + tabKeyString + "; hasFocus = " + hasFocus + "; postion = " + postion);

        if (TextUtils.isEmpty(tabKeyString)) {
            return;
        }

        // 检查并创建商品展示界面
        onCreatGoodsListGridView(tabKeyString, postion);

        if (hasFocus) {

            if (mCurrentSelectTabNumBer != postion) {

                // 已经切换分类的处理

                String oldtabText = null;
                if (mCurrentSelectTabNumBer >= 0) {
                    oldtabText = getTabKeyWordOfTabNumBer(mCurrentSelectTabNumBer);
                }
                FocusFlipGridView oldGirdview = null;
                if (!TextUtils.isEmpty(oldtabText)) {
                    oldGirdview = mGoodsGirdViewMap.get(oldtabText);
                }

                String currenttabText = null;
                if (postion >= 0) {
                    currenttabText = getTabKeyWordOfTabNumBer(postion);
                }
                FocusFlipGridView currentGirdview = null;
                if (!TextUtils.isEmpty(currenttabText)) {
                    currentGirdview = mGoodsGirdViewMap.get(currenttabText);
                }

                if (mTabFocusPositionManager != null && currentGirdview != null) {
                    mTabFocusPositionManager.setGridView(currentGirdview);
                }

                // 具体让子类实现切换后的逻辑
                handlerChangeTab(postion, currentGirdview, mCurrentSelectTabNumBer, oldGirdview, hasFocus);

            }
            // 保存当前TAB编号
            mCurrentSelectTabNumBer = postion;
            mCanMoveRight = true;
        }
    }

    /**
     * 不是当前currentIndex的商品girdview 隐藏
     *
     * @param currentIndex 如果是小于0，那么隐藏全部的GridView
     */
    protected void showGirdView(int currentIndex) {
        AppDebug.i(TAG, "hideGirdView --> currentIndex = " + currentIndex + "; mGoodsGirdViewMap = "
                + mGoodsGirdViewMap);
        if (mGoodsGirdViewMap != null) {
            Iterator<Map.Entry<String, FocusFlipGridView>> it = mGoodsGirdViewMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, FocusFlipGridView> entry = it.next();

                String tabKey = entry.getKey();
                FocusFlipGridView focusFlipGridView = entry.getValue();

                String currentKey = null;
                if (currentIndex >= 0) {
                    currentKey = getTabKeyWordOfTabNumBer(currentIndex);
                }
                boolean isCurrent = false;
                if (currentKey != null) {
                    isCurrent = currentKey.equals(tabKey);
                }
                AppDebug.i(TAG, "showGirdView --> focusFlipGridView = " + focusFlipGridView + "; isCurrent = "
                        + isCurrent);
                if (focusFlipGridView != null && !isCurrent) {
                    focusFlipGridView.setVisibility(View.GONE);
                }
            }
        }
    }


    protected abstract void enterDisplayDetailAsync(String title, String picUrl, String eurl);

    protected void enterDisplayDetailUri(String uri) {
        AppDebug.i("url", "url = " + uri);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(uri));
        intent.putExtra(CoreIntentKey.URI_FROM_APP_BUNDLE, getAppName());
        startActivity(intent);
        return;
    }

    /**
     * 进入详情页
     *
     * @param itemId 商品的ID号
     */
    protected void enterDisplayDetail(String itemId, Map<String, String> exParams) {

        AppDebug.i(TAG, "enterDisplayDetail itemId = " + itemId + "is");

        if (TextUtils.isEmpty(itemId)) {
            return;
        }
        if (!NetWorkUtil.isNetWorkAvailable()) {
            showNetworkErrorDialog(false);
            return;
        }

        try {
            StringBuilder urlBuilder = new StringBuilder("tvtaobao://home?app=taobaosdk&module=detail&itemId=");
            urlBuilder.append(itemId);
            if (exParams != null)
                for (String key : exParams.keySet()) {
                    urlBuilder.append(String.format("&%s=%s", key, exParams.get(key)));
                }
            String url = urlBuilder.toString();
            AppDebug.i("url", "url = " + url);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.putExtra(CoreIntentKey.URI_FROM_APP_BUNDLE, getAppName());
            startActivity(intent);
            return;
        } catch (Exception e) {
            String name = getResources().getString(R.string.ytbv_not_open);
            Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 处理选中的条目
     *
     * @param position
     * @param isSelect
     */
    protected void HandleSelectView(View selectview, int position, boolean isSelect) {

        AppDebug.i(TAG, "HandleSelectView  -->    position = " + position + "; isSelect  = " + isSelect
                + "; selectview = " + selectview);

        if (selectview instanceof TabGoodsItemView && selectview != null) {
            TabGoodsItemView item = (TabGoodsItemView) selectview;
            item.onItemSelected(isSelect, null);
        }
    }

    private static final class TabBaseHandler extends Handler {

        private final WeakReference<TabBaseActivity> weakReference;

        public TabBaseHandler(WeakReference<TabBaseActivity> weakReference) {
            this.weakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            TabBaseActivity tabBaseActivity = weakReference.get();
            if (tabBaseActivity != null) {
                switch (msg.what) {
                    case TabBaseActivity.WHAT_KEYUPEVENT:
                        tabBaseActivity.onHandleKeyUpEvent();
                        break;
                    case TabBaseActivity.WHAT_TAB_LAYOUT_END:
                        tabBaseActivity.focusPositionManagerrequestFocus();
                        break;
                }
            }
        }
    }

    @Override
    protected FocusPositionManager getFocusPositionManager() {
        return (FocusPositionManager) findViewById(R.id.common_havetab_mainLayout);
    }

    /**
     * 重新调整公共的布局, 或者其他的处理，包括布局的修改，或者添加
     *
     * @param tabFocusPositionManager
     */
    protected abstract void onReAdjustCommonLayout(TabFocusPositionManager tabFocusPositionManager);

    /**
     * 获取左侧TAB的适配器
     *
     * @return
     */
    protected abstract BaseAdapter getTabAdapter();

    /**
     * 获取girdview
     *
     * @param tabkey
     * @param position
     * @return
     */
    protected abstract FocusFlipGridView getTabGoodsGridView(String tabkey, final int position);

    /**
     * 获取girdview 的适配器
     *
     * @param tabkey
     * @param position
     * @return
     */
    protected abstract TabGoodsBaseAdapter getTabGoodsAdapter(String tabkey, final int position);

    /**
     * 初始化GirdView，包括可以修改GirdView的布局参数，添加headerView
     *
     * @param goodListLifeUiGridView
     * @param adapter
     * @param tabkey
     * @param position
     */
    protected abstract void initGirdViewInfo(FocusFlipGridView goodListLifeUiGridView, TabGoodsBaseAdapter adapter,
                                             String tabkey, final int position);

    /**
     * 根据TAB编号，获取tabkey
     *
     * @param tabNumBerPos
     * @return
     */
    protected abstract String getTabKeyWordOfTabNumBer(int tabNumBerPos);

    /**
     * 获取商品的ID号，为了进入详情页面
     *
     * @param focusFlipGridView
     * @param tabkey
     * @param arg0
     * @param arg1
     * @param position
     * @param arg3
     * @return
     */
    protected abstract String getItemIdOfEnterdetail(FocusFlipGridView focusFlipGridView, String tabkey,
                                                     AdapterView<?> arg0, View arg1, int position, long arg3);

    protected abstract String getEurlOfEnterdetail(FocusFlipGridView focusFlipGridView, String tabkey,
                                                   AdapterView<?> arg0, View arg1, int position, long arg3);

    protected abstract String getUriOfEnterDetail(FocusFlipGridView focusFlipGridView, String tabkey,
                                                  AdapterView<?> arg0, View arg1, int position, long arg3);

    protected abstract String getTitleOfEnterDetail(FocusFlipGridView focusFlipGridView, String tabkey,
                                                    AdapterView<?> arg0, View arg1, int position, long arg3);

    protected abstract String getPicUrlOfEnterDetail(FocusFlipGridView focusFlipGridView, String tabkey,
                                                     AdapterView<?> arg0, View arg1, int position, long arg3);

    /**
     * 处理切换分类
     *
     * @param currentTabNumber
     * @param currentGridview
     * @param oldTabNumber
     * @param oldGridview
     * @param change
     */
    protected abstract void handlerChangeTab(int currentTabNumber, FocusFlipGridView currentGridview, int oldTabNumber,
                                             FocusFlipGridView oldGridview, boolean change);

    /**
     * 获取商品 Focus的资源ID
     *
     * @return
     */
    protected abstract int getGoodsFocusDrawableId();

    /**
     * 获取TAB Focus的资源ID
     *
     * @return
     */
    protected abstract int getTabFocusDrawableId();

}
