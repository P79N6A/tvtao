package com.yunos.tvtaobao.mytaobao.activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.yunos.tv.app.widget.AdapterView;
import com.yunos.tv.app.widget.AdapterView.OnItemClickListener;
import com.yunos.tv.app.widget.FlipGridView.OnFlipRunnableListener;
import com.yunos.tv.app.widget.focus.FocusFlipGridView;
import com.yunos.tv.app.widget.focus.FocusFlipGridView.OnFocusFlipGridViewListener;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.config.TvOptionsChannel;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.activity.CoreActivity;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.listener.NetworkOkDoListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.Collect;
import com.yunos.tvtaobao.biz.request.bo.CollectList;
import com.yunos.tvtaobao.biz.request.bo.CollectionsInfo;
import com.yunos.tvtaobao.biz.request.bo.NewCollection;
import com.yunos.tvtaobao.biz.widget.GridViewFocusPositionManager;
import com.yunos.tvtaobao.mytaobao.R;
import com.yunos.tvtaobao.mytaobao.adapter.CollectsAdapter;
import com.yunos.tvtaobao.mytaobao.adapter.NewCollectsAdapter;
import com.yunos.tvtaobao.mytaobao.view.ItemLayoutForCollect;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollectsActivity extends BaseActivity {

    private final String TAG = "Collects";
    private final String SPM = "a2o0j.11561564";

    private final int PAGE_SIZE = 30;
    private final int COLUMNS_COUNT = 5;

    private FocusFlipGridView mCollectsGrid;
    private GridViewFocusPositionManager mFocusPositionManager;

    private NewCollectsAdapter mAdapter;
    private BusinessRequest request = null;
    private int curPage = 1;
    private  String startTime = "0";
    private ArrayList<NewCollection> mCollectsData = null;

    // 是否还有下一页数据 
    private boolean hasNextPage = true;
    // 无数据时的提示 
    private View emptyTip = null;

    // 是否正在加载数据，用于防止重复加载同一页数据 
    private boolean isLoadingData = false;

    // 顶部阴影区域 
    private ImageView topShadowArea = null;

    // 是否是第一次加载数据 
    private boolean isFirstLoadData = true;

    private int mSelPosition = -1;

    private String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onKeepActivityOnlyOne(CollectsActivity.class.getName());
        setContentView(R.layout.ytsdk_activity_collects);
        registerLoginListener();
        from = getIntent().getStringExtra(CoreIntentKey.URI_FROM);
        TvOptionsConfig.setTvOptionsChannel(TvOptionsChannel.COLLECT);

        request = BusinessRequest.getBusinessRequest();
        isFirstLoadData = true;
        mSelPosition = -1;

        initView();
        initListener();
        refreshData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        from = getIntent().getStringExtra(CoreIntentKey.URI_FROM);
        refreshData();

    }

    /**
     * 初始化界面
     */
    private void initView() {
        mFocusPositionManager = (GridViewFocusPositionManager) this.findViewById(R.id.focus_flip_gridView);

        emptyTip = findViewById(R.id.empty_tip);
        mCollectsGrid = (FocusFlipGridView) findViewById(R.id.collects_grid);
        mFocusPositionManager.setGridView(mCollectsGrid);
        topShadowArea = (ImageView) findViewById(R.id.collects_top_shadow_area);
        emptyTip.setVisibility(View.GONE);

        onInitFocusFlipGridView();
    }

    private void onInitFocusFlipGridView() {

        mFocusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(
                R.drawable.ytbv_common_focus)));
        mCollectsGrid.setAnimateWhenGainFocus(false, false, false, false);

        mCollectsGrid.setNumColumns(COLUMNS_COUNT);

        int VerticalSpacing = this.getResources().getDimensionPixelSize(R.dimen.dp_32);
        int horizontalSpacing = this.getResources().getDimensionPixelSize(R.dimen.dp_12);
        mCollectsGrid.setVerticalSpacing(VerticalSpacing);
        mCollectsGrid.setHorizontalSpacing(horizontalSpacing);

        mCollectsGrid.setOnFocusFlipGridViewListener(new OnFocusFlipGridViewListener() {

            @Override
            public void onLayoutDone(boolean first) {

                if (first) {
                    mFocusPositionManager.requestFocus();
                }
            }

            @Override
            public void onOutAnimationDone() {

            }

            @Override
            public void onReachGridViewBottom() {

            }

            @Override
            public void onReachGridViewTop() {

            }

        });
    }

    /**
     * 初始化数据
     */
    protected void refreshData() {
        TvOptionsConfig.setTvOptionVoiceSystem(from);
        //从服务的获取数据
        getCollects(startTime);
    }

    /**
     * 刷新界面数据
     * @param collectList 服务端返回的结果列表
     */
    private void refreshView(List<NewCollection> collectList) {

        if (collectList == null) {
            return;
        }

//        CollectList collectList = (CollectList) data;
//        hasNextPage = collectList.isHavNextPage();

//        ArrayList<NewCollection> collects = collectList.getResultList();
        if (collectList == null) {
            hasNextPage = false;
            return;
        }

        if (collectList.isEmpty()) {
            hasNextPage = false;
        } else {
            resetPicSizeInPicUrl( collectList);
        }
        if (mCollectsData == null) {
            mCollectsData = new ArrayList<NewCollection>();
            mCollectsData.addAll(collectList);
        } else {
            //如果有数据则将数据放到集合中，重复的数据不放
            for (NewCollection c : collectList) {
                if (mCollectsData.indexOf(c) == -1) {
                    mCollectsData.add(c);
                }
            }
        }
        if (mAdapter == null) {
            mAdapter = new NewCollectsAdapter(this, mCollectsData);
            mFocusPositionManager.requestLayout();// 强制请求重新布局，因为这个时候在onLayout时会重新找聚焦的点
            mCollectsGrid.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }

        //如果无数据，则给出提示
        if (mCollectsData.isEmpty()) {
            emptyTip.setVisibility(View.VISIBLE);
            mCollectsGrid.setVisibility(View.GONE);
        } else {
            emptyTip.setVisibility(View.GONE);
            mCollectsGrid.setVisibility(View.VISIBLE);
        }

        mCollectsGrid.postInvalidate();// 刷新界面，防止gridview中item部分截断
    }

    private void getCollects(final String  startTime) {
        OnWaitProgressDialog(true);

        AppDebug.d(TAG, "get collects startTime =" + startTime + ".isLoadingData = " + isLoadingData);
        if (isLoadingData) {
            OnWaitProgressDialog(false);
            return;
        }
        isLoadingData = true;

//        request.getCollects(page, PAGE_SIZE, new CollectsBusinessRequestListener(new WeakReference<BaseActivity>(this),
//                page, isFirstLoadData));

        request.getNewCollects(startTime, PAGE_SIZE,new NewCollectsBusinessRequestListener(new WeakReference<BaseActivity>(this),
                 isFirstLoadData));
    }



    private  class NewCollectsBusinessRequestListener extends BizRequestListener<CollectionsInfo> {

        private final boolean isFirstLoadData;

        public NewCollectsBusinessRequestListener(WeakReference<BaseActivity> baseActivityRef,
                                               boolean isFirstLoadData) {
            super(baseActivityRef);
            this.isFirstLoadData = isFirstLoadData;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            final CollectsActivity collectsActivity = (CollectsActivity) mBaseActivityRef.get();
            if (collectsActivity != null) {
                AppDebug.v(collectsActivity.TAG, collectsActivity.TAG
                        + ".CollectsBusinessRequestListener.onError.resultCode = " + resultCode + ",msg = " + msg
                        + ".isFirstLoadData = " + isFirstLoadData);
                if (collectsActivity.mCollectsData == null) {
                    collectsActivity.emptyTip.setVisibility(View.VISIBLE);
                    collectsActivity.mCollectsGrid.setVisibility(View.GONE);
                }
                AppDebug.d(collectsActivity.TAG, "get collects error: " + msg);
                collectsActivity.isLoadingData = false;
                collectsActivity.OnWaitProgressDialog(false);
                if (resultCode == 1 && isFirstLoadData) {//网络未连接，且是第一次请求数据
                    collectsActivity.setNetworkOkDoListener(new NetworkOkDoListener() {

                        @Override
                        public void todo() {
                            // 数据更新后取消网络注册
                            if (collectsActivity != null) {
                                AppDebug.v(collectsActivity.TAG, collectsActivity.TAG
                                        + ".CollectsBusinessRequestListener.onError.refreshData");
                                collectsActivity.refreshData();
                                collectsActivity.setNetworkOkDoListener(null);
                            }
                        }
                    });
                }
            }
            return false;
        }

        @Override
        public void onSuccess(CollectionsInfo data) {
            CollectsActivity collectsActivity = (CollectsActivity) mBaseActivityRef.get();
            if (collectsActivity != null) {
                AppDebug.v(collectsActivity.TAG, collectsActivity.TAG
                        + ".CollectsBusinessRequestListener.onSuccess.data = " + data);
                if (data != null) {
                    //获取成功后并且有数据才更新当前页计数器
//                    if (data.getResultList() != null && data.getResultList().size() > 0) {
//                        collectsActivity.curPage = page;
//                    }
                    if(data.getPageInfo()!=null&&data.getPageInfo().getNextStartTime()!=null){
                        startTime = data.getPageInfo().getNextStartTime();
                    }

                    List<NewCollection> collectionList = data.getFavList();
                    collectsActivity.refreshView(collectionList);
                } else {
                    if (collectsActivity.mCollectsData == null) {
                        collectsActivity.emptyTip.setVisibility(View.VISIBLE);
                        collectsActivity.mCollectsGrid.setVisibility(View.GONE);
                    }
                }

                collectsActivity.isLoadingData = false;
                collectsActivity.isFirstLoadData = false;
                collectsActivity.OnWaitProgressDialog(false);
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            if (isFirstLoadData) {
                return true;
            }
            return false;
        }

    }



    private static class CollectsBusinessRequestListener extends BizRequestListener<CollectList> {

        private final int page;
        private final boolean isFirstLoadData;

        public CollectsBusinessRequestListener(WeakReference<BaseActivity> baseActivityRef, int page,
                                               boolean isFirstLoadData) {
            super(baseActivityRef);
            this.page = page;
            this.isFirstLoadData = isFirstLoadData;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            final CollectsActivity collectsActivity = (CollectsActivity) mBaseActivityRef.get();
            if (collectsActivity != null) {
                AppDebug.v(collectsActivity.TAG, collectsActivity.TAG
                        + ".CollectsBusinessRequestListener.onError.resultCode = " + resultCode + ",msg = " + msg
                        + ".isFirstLoadData = " + isFirstLoadData);
                if (collectsActivity.mCollectsData == null) {
                    collectsActivity.emptyTip.setVisibility(View.VISIBLE);
                    collectsActivity.mCollectsGrid.setVisibility(View.GONE);
                }
                AppDebug.d(collectsActivity.TAG, "get collects error: " + msg);
                collectsActivity.isLoadingData = false;
                collectsActivity.OnWaitProgressDialog(false);
                if (resultCode == 1 && isFirstLoadData) {//网络未连接，且是第一次请求数据
                    collectsActivity.setNetworkOkDoListener(new NetworkOkDoListener() {

                        @Override
                        public void todo() {
                            // 数据更新后取消网络注册
                            if (collectsActivity != null) {
                                AppDebug.v(collectsActivity.TAG, collectsActivity.TAG
                                        + ".CollectsBusinessRequestListener.onError.refreshData");
                                collectsActivity.refreshData();
                                collectsActivity.setNetworkOkDoListener(null);
                            }
                        }
                    });
                }
            }
            return false;
        }

        @Override
        public void onSuccess(CollectList data) {
            CollectsActivity collectsActivity = (CollectsActivity) mBaseActivityRef.get();
            if (collectsActivity != null) {
                AppDebug.v(collectsActivity.TAG, collectsActivity.TAG
                        + ".CollectsBusinessRequestListener.onSuccess.data = " + data);
                if (data != null) {
                    //获取成功后并且有数据才更新当前页计数器
                    if (data.getResultList() != null && data.getResultList().size() > 0) {
                        collectsActivity.curPage = page;
                    }
//                    collectsActivity.refreshView(data);
                } else {
                    if (collectsActivity.mCollectsData == null) {
                        collectsActivity.emptyTip.setVisibility(View.VISIBLE);
                        collectsActivity.mCollectsGrid.setVisibility(View.GONE);
                    }
                }

                collectsActivity.isLoadingData = false;
                collectsActivity.isFirstLoadData = false;
                collectsActivity.OnWaitProgressDialog(false);
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            if (isFirstLoadData) {
                return true;
            }
            return false;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initListener() {

        if (mCollectsGrid == null)
            return;

        mCollectsGrid.setOnItemSelectedListener(new ItemSelectedListener() {

            @Override
            public void onItemSelected(View view, int position, boolean selected, View parent) {

                //如果是选中并且焦点在第二行以上,立即显示上面的遮盖
                if (selected && position >= COLUMNS_COUNT && !topShadowArea.isShown()) {
                    topShadowArea.setVisibility(View.VISIBLE);
                }

                if (selected) {
                    mSelPosition = position;
                }

                //滚动到当前页的第6行时，获取下一页
                if (position >= COLUMNS_COUNT * 5 * curPage) {
                    int nextPage = (position / (PAGE_SIZE + 1)) + 2;
                    AppDebug.i(TAG, "position:" + position + ",hasNextPage:" + hasNextPage + ",isLoadingData:"
                            + isLoadingData + ",nextPage:" + nextPage + ",mCollectsData.size:" + mCollectsData.size());
                    if (PAGE_SIZE * nextPage > mCollectsData.size() && hasNextPage && !isLoadingData) {
                        AppDebug.i(TAG, "curPage:" + curPage);
//                        getCollects(curPage + 1);
                    }
                    if(!startTime.equals("0")){
                        getCollects(startTime);

                    }

                }

                if (view instanceof ItemLayoutForCollect) {
                    ItemLayoutForCollect itemLayout = (ItemLayoutForCollect) view;
                    if (itemLayout != null) {
                        itemLayout.setSelect(selected);
                    }
                }

                //标题focus时显示两行,反之显示一行
                //                ItemLayoutForCollect item = (ItemLayoutForCollect) view;
                //                if (item == null) {
                //                    return;
                //                }
                //                TextView mItemSummaryView = item.getItemSummaryView();
                //                if (mItemSummaryView == null) {
                //                    return;
                //                }
                //                if (selected) {
                //                    mItemSummaryView.setMaxLines(2);
                //                } else {
                //                    mItemSummaryView.setMaxLines(1);
                //                }

            }
        });
        mCollectsGrid.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                NewCollection collect = mCollectsData.get(arg2);
                //跳转到详情界面
                if (collect != null) {
                    // 如果滚动没有结束，则强制设置focus框
                    if (mCollectsGrid.isScrolling()) {
                        mCollectsGrid.forceResetFocusParams(mFocusPositionManager);
                    }

                    String itemNumId = collect.getFavId();
                    Intent intent = new Intent();
                    intent.setClassName(CollectsActivity.this, BaseConfig.SWITCH_TO_DETAIL_ACTIVITY);
                    intent.putExtra(BaseConfig.INTENT_KEY_ITEMID, itemNumId);
                    startActivity(intent);
                }
            }
        });

        //  设置滚动监听
        mCollectsGrid.setOnFlipGridViewRunnableListener(new OnFlipRunnableListener() {

            @Override
            public void onFinished() {

                if (mAdapter != null) {
                    mAdapter.setPauseWork(false);
                    mAdapter.notifyDataSetChanged();
                }

                //当滚动停下来时,才隐藏上面的遮盖
                //                if (mCollectsGrid.getLastVisiblePosition() <= (COLUMNS_COUNT * 2 - 1) && topShadowArea.isShown()) {
                //                    topShadowArea.setVisibility(View.INVISIBLE);
                //                }

                if ((mSelPosition < COLUMNS_COUNT) && (topShadowArea.isShown())) {
                    topShadowArea.setVisibility(View.INVISIBLE);
                } else if (mSelPosition >= COLUMNS_COUNT) {
                    topShadowArea.setVisibility(View.VISIBLE);
                }

                if (mCollectsGrid != null) {
                    View view = mCollectsGrid.getSelectedView();
                    if (view instanceof ItemLayoutForCollect) {
                        ItemLayoutForCollect itemLayout = (ItemLayoutForCollect) view;
                        if (itemLayout != null) {
                            itemLayout.setSelect(true);
                        }
                    }
                }

            }

            @Override
            public void onFlipItemRunnable(float arg0, View arg1, int arg2) {

            }

            @Override
            public void onStart() {
                if (mAdapter != null) {
                    mAdapter.setPauseWork(true);
                }
            }
        });

    }

    /**
     * 将图片URL中的大小换成指定大小
     */
    private void resetPicSizeInPicUrl(List<NewCollection> collects) {
        if (collects == null) {
            return;
        }
        int w = 230;
        //        int w = 240;
        //如果是1080P
        if (DeviceUtil.getScreenScaleFromDevice(this.getApplicationContext()) > 1.1f) {
            w = 350;
            //            w = 360;
        }
        String imgSize = w + "x" + w;
        for (NewCollection c : collects) {
            c.setItemPic(getPicUrl(c.getItemPic(), imgSize));
        }
    }

    /**
     * 将图片url末尾的大小换成指定的大小，如果已经 有大小了则替换，如果没有则直接添加
     * @param picUrl 图片原始url
     * @param sizeStr 指定大小，如240x240
     * @return 返回替换后的字符串
     */
    private String getPicUrl(String picUrl, String sizeStr) {
        //如果为空，则返回空
        if (TextUtils.isEmpty(picUrl)) {
            return "";
        }
        String url = picUrl;
        try {
            //正则表达式，用于匹配诸如.jpg_200x200.jpg, .png_23x23.jpg的字符串
            Pattern p = Pattern.compile("(_\\d+x\\d+\\.([a-zA-Z]+))");
            Matcher m = p.matcher(picUrl);
            String groupAll = "";
            String groupExt = "";
            if (m.find()) {
                //完整的匹配字符串,即形如_200x200.jpg的字符串
                groupAll = m.group(1);
                //完整匹配字符串中的二级匹配字符串，即文件扩展名
                groupExt = m.group(2);
            }
            if (groupAll.length() != 0 && groupExt.length() != 0) {
                //将字符串结尾的指定内容截掉，重新加上sizeStr的后缀
                url = picUrl.substring(0, picUrl.lastIndexOf(groupAll)) + "_" + sizeStr + ".jpg";
            } else {
                //如果没有匹配上，则直接添加size和扩展名
                //                Pattern p2 = Pattern.compile("\\.([a-zA-Z]+)");
                //                Matcher m2 = p2.matcher(picUrl);
                //                String groupExt2 = "";
                //                while (m2.find()) {
                //                    groupExt2 = m2.group(1);
                //                }
                url = picUrl + "_" + sizeStr + ".jpg";
            }
        } catch (Exception e) {
            e.printStackTrace();
            url = picUrl.replace("60x60", sizeStr);
        }
        return url;
    }

    @Override
    protected void onDestroy() {
        unRegisterLoginListener();
        onClearBufferData();
        onRemoveKeepedActivity(CollectsActivity.class.getName());
        super.onDestroy();
    }

    // 释放资源
    public void onClearBufferData() {

        if (mCollectsData != null) {
            mCollectsData.clear();
            mCollectsData = null;
        }

        if (mAdapter != null) {
            mAdapter.onDestroy();
            mAdapter = null;
        }

        if (mCollectsGrid != null) {
            mCollectsGrid.removeAllViewsInLayout();
            mCollectsGrid = null;
        }

        mFocusPositionManager = null;
        request = null;
    }

    @Override
    public String getPageName() {
        return TAG;
    }
    @Override
    public Map<String, String> getPageProperties() {

        Map<String, String> p = super.getPageProperties();
        p.put("spm-cnt", SPM+".0.0");
        return p;
    }
    @Override
    protected void onLoginCancel() {
        super.onLoginCancel();
        finish();
    }
}
