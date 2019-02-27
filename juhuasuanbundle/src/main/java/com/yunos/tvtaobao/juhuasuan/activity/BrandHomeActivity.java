package com.yunos.tvtaobao.juhuasuan.activity;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.taobao.statistic.CT;
import com.taobao.statistic.TBS;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.AppHandler;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.BrandMO;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CategoryMO;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CountList;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.Option;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.enumeration.OptionType;
import com.yunos.tvtaobao.juhuasuan.classification.ClassiFicationValuesDimen;
import com.yunos.tvtaobao.juhuasuan.common.NetWorkCheck;
import com.yunos.tvtaobao.juhuasuan.config.SystemConfig;
import com.yunos.tvtaobao.juhuasuan.pingpaituan.OnPingpaiItemHandleListener;
import com.yunos.tvtaobao.juhuasuan.pingpaituan.PinPaiViewPager;
import com.yunos.tvtaobao.juhuasuan.pingpaituan.PinPaiViewPager.*;
import com.yunos.tvtaobao.juhuasuan.pingpaituan.PingPaiDimension;
import com.yunos.tvtaobao.juhuasuan.pingpaituan.PingPaiItemRelativeLayout;
import com.yunos.tvtaobao.juhuasuan.pingpaituan.PingPaiPagerAdapter;
import com.yunos.tvtaobao.juhuasuan.pingpaituan.PingPaiTuanImageHandleUint;
import com.yunos.tvtaobao.juhuasuan.request.MyBusinessRequest;
import com.yunos.tvtaobao.juhuasuan.request.listener.JuBrandCateSelectDialogRetryRequestListener;
import com.yunos.tvtaobao.juhuasuan.request.listener.JuRetryRequestListener;
import com.yunos.tvtaobao.juhuasuan.util.DialogUtils;
import com.yunos.tvtaobao.juhuasuan.util.ModelTranslator;
import com.yunos.tvtaobao.juhuasuan.util.StringUtil;
import com.yunos.tvtaobao.juhuasuan.view.CategoryButton;

import java.util.Map;

public class BrandHomeActivity extends JuBaseActivity {

    private final String TAG = "BrandHomeActivity";
    private final String TBS_PAGE_TAG = "BrandHome";

    // 数据变量
    private CountList<BrandMO> mListData = null;
    private Item_Info mItem_Info = null;

    private boolean mInLoadingData = false;
    private boolean mFirstRequest = true;

    private ViewHolder viewHolder;

    private PingPaiPagerAdapter mClassificationPagerAdapter = null;
    private PinPaiViewPager mViewPager = null;

    private boolean mFirstStart = true;
    //是否已经退出
    private boolean mDestroy = false;

    private boolean mAlwaysKeyDown = false;

    private CountList<CategoryMO> mCategoryMOs = null;

    private CategoryMO mCurrentCategoryMO = null;

    private CategorySelecteDialog mCategorySelectDialog;

    private AppHandler<BrandHomeActivity> mMainHandler = new BrandHomeHandle(this);

    /**
     * 弹出分类对话框
     */
    protected View.OnClickListener mCategorySelectedListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            AppDebug.i(TAG, TAG + ".mCategorySelectedListener.onClick v=" + v);
            String controlName = Utils.getControlName(getFullPageName(), "Cate_Select", null);
            TBS.Page.buttonClicked(controlName);

            if (mDestroy) {
                return;
            }

            if (mCategorySelectDialog == null) {
                mCategorySelectDialog = new CategorySelecteDialog(BrandHomeActivity.this);
            }
            mCategorySelectDialog.show();

            if (mCurrentCategoryMO == null && mCategoryMOs != null && mCategoryMOs.size() > 0) {
                AppDebug.i(TAG, TAG + ".mCategorySelectedListener.onClick mCategoryMOs=" + mCategoryMOs);
                mCurrentCategoryMO = mCategoryMOs.get(0);
            }

            mCategorySelectDialog.setSelected(mCurrentCategoryMO);
        }
    };

    /**
     * 选中某个分类
     */
    protected View.OnClickListener mCategoryItemSelectedListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            Object obj = v.getTag();
            String cateName = null;
            if (obj != null && obj instanceof CategoryMO) {
                CategoryMO category = (CategoryMO) obj;
                cateName = category.getName();
            } else {
                cateName = getString(R.string.jhs_quanbu);
            }
            String controlName = Utils.getControlName(getFullPageName(), "Cate", null, cateName);
            TBS.Adv.ctrlClicked(CT.Button, controlName);

            // 网络检测
            if (!NetWorkUtil.isNetWorkAvailable()) {
                NetWorkCheck.netWorkError(BrandHomeActivity.this);
                return;
            }

            OnWaitProgressDialog(true);

            JuRetryRequestListener<CountList<Option>> getranksBusinessRequestListener = new JuRetryRequestListener<CountList<Option>>(
                    BrandHomeActivity.this) {

                @Override
                public void onSuccess(BaseActivity baseActivity, CountList<Option> data) {
                    BrandHomeActivity activity = null;
                    if (baseActivity instanceof BrandHomeActivity) {
                        activity = (BrandHomeActivity) baseActivity;
                    }
                    if (null == activity) {
                        return;
                    }
                    activity.mListData.clear();
                    if (null != data) {
                        activity.mListData.addAll(ModelTranslator.translateBrand(data));
                    }
                    activity.onCategoryUpdate();
                    activity.OnWaitProgressDialog(false);
                }
            };

            if (obj != null && obj instanceof CategoryMO) {
                mCurrentCategoryMO = (CategoryMO) obj;
                String optStr = null;
                AppDebug.i(TAG, TAG + ".mCategoryItemSelectedListener.onClick mCurrentCategoryMO=" + mCurrentCategoryMO);
                if (!getString(R.string.jhs_quanbu).equals(mCurrentCategoryMO.getName())) {
                    optStr = mCurrentCategoryMO.getOptStr();
                }
                MyBusinessRequest.getInstance().requestGetOption(OptionType.Brand.getplatformId(), optStr,
                        mItem_Info.mCurrentPages, mItem_Info.mPageSizes, getranksBusinessRequestListener);
            }

            mCategorySelectDialog.hide();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.style_launch);
        super.onCreate(savedInstanceState);

        viewHolder = new ViewHolder();
        setContentView(viewHolder.viewContents);

        //        // 获取屏幕尺寸
        //        PingPaiDimension.getScreenTypeFromDevice(SystemUtil.getMagnification());

        // 布局尺寸处理
        PingPaiDimension.onInitValue();

        // 初始化适配值
        ClassiFicationValuesDimen.onInitValuesDimen();

        //        // 检查是否缩放
        //        ClassiFicationValuesDimen.onAdaptationScreen(PingPaiDimension.Display_Scale);

        // 初始化绘图工具
        PingPaiTuanImageHandleUint.onInitPaint();

        // 初始化缓冲区
        onInitDataBuffer();

        // 请求数据
        onRequestExecute(mItem_Info);

        if (SystemConfig.DIPEI_BOX) {
            setBackToHome(true);
        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyAction = event.getAction();
        int keyKeyCode = event.getKeyCode();

        if (keyAction == KeyEvent.ACTION_DOWN) {
            if ((keyKeyCode == KeyEvent.KEYCODE_DPAD_LEFT) || (keyKeyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
                mAlwaysKeyDown = true;
            }
        } else if (keyAction == KeyEvent.ACTION_UP) {
            mAlwaysKeyDown = false;
        }

        return super.dispatchKeyEvent(event);
    }

    private void onInitDataBuffer() {

        mListData = new CountList<BrandMO>();
        mListData.clear();

        mItem_Info = new Item_Info();

        mInLoadingData = false;
        mFirstRequest = true;

        mFirstStart = true;

        mAlwaysKeyDown = false; 
    }

    private void onClearBufferData() {

        if (null != mCategorySelectDialog) {
            mCategorySelectDialog.dismiss();
            mCategorySelectDialog = null;
        } 
        
        if (mClassificationPagerAdapter != null) {
            mClassificationPagerAdapter.onClearAndDestroy();

            mClassificationPagerAdapter = null;
        }

        if (mListData != null) {
            mListData.clear();
            mListData = null;
        }

        mViewPager = null;

        mItem_Info = null;

        PingPaiTuanImageHandleUint.recycle();

    }

    // 界面布局
    private void onInitLayout() {
        mViewPager = new PinPaiViewPager(this);

        // 绑定Viewpager的数据
        onBindViewpagerData();

        mViewPager.setAdapter(mClassificationPagerAdapter);

        int Params_W = PingPaiDimension.SCREEN_WIDTH;
        int Params_H = PingPaiDimension.SCREEN_HEIGHT;

        FrameLayout.LayoutParams goodlistLp = new FrameLayout.LayoutParams(Params_W, Params_H);

        goodlistLp.setMargins(0, PingPaiDimension.PAGE_MARGIN_TOP, 0, 0); // -
                                                                          // PingPaiDimension.ITEM_SHADOW

        if (viewHolder.pingpaiMainFramelayout != null) {
            viewHolder.pingpaiMainFramelayout.addView(mViewPager, goodlistLp);
        }

        // 设置页滚动监听
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                AppDebug.i(TAG, TAG + ".mViewPager.onPageSelected position = " + position);
                Message mes = mMainHandler.obtainMessage(Item_Info.UPDATE_DISPLAY_PAGE_MSG, position, position);
                mMainHandler.sendMessageDelayed(mes, 100);

                if (mClassificationPagerAdapter != null) {
                    mClassificationPagerAdapter.upDataBitmapInfo(position);
                }

                // mPageCount++;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

                // 不是处于滚动状态，并且没有连续按下的情况下，刷新界面
                if ((PinPaiViewPager.SCROLL_STATE_SETTLING != state) && (!mAlwaysKeyDown)) {
                    if (mClassificationPagerAdapter != null) {
                        mClassificationPagerAdapter.onUpdataHandleLoading();
                    }
                }
            }

        });

        viewHolder.pingpaiDefaultDisplayRelativeLayout.setVisibility(View.GONE);

        onCategoryUpdate();
    }

    protected void onCategoryUpdate() {
        if (mCategoryMOs != null && mCategoryMOs.size() > 0) {
            if (mCurrentCategoryMO == null) {
                onInitTitleRightLayout(mCategoryMOs.size());
            } else {
                onInitTitleRightLayout(mCurrentCategoryMO, mCategoryMOs.size());
            }
        }
        onBindViewpagerData();

        Message mesTotalPage = mMainHandler.obtainMessage(Item_Info.UPDATE_DISPLAY_PAGE_TOTAL_MSG,
                mItem_Info.mTotalPage, mItem_Info.mTotalPage);
        mMainHandler.sendMessageDelayed(mesTotalPage, 150);

        Message mescurrentPage = mMainHandler.obtainMessage(Item_Info.UPDATE_DISPLAY_PAGE_MSG,
                mItem_Info.mCurrentPages, mItem_Info.mCurrentPages);
        mMainHandler.sendMessageDelayed(mescurrentPage, 100);
    }

    protected void onBindViewpagerData() {

        if (mClassificationPagerAdapter != null) {
            mItem_Info.mCurrentPages = 0;
            if (mListData.size() % Item_Info.PAGE_COUNT == 0) {
                mItem_Info.mTotalPage = mListData.size() / Item_Info.PAGE_COUNT;
            } else {
                mItem_Info.mTotalPage = mListData.size() / Item_Info.PAGE_COUNT + 1;
            }
        }

        if (null == mClassificationPagerAdapter) {
            mClassificationPagerAdapter = new PingPaiPagerAdapter(this);
            // 对各条目设置监听事件
            mClassificationPagerAdapter.onSetOnItemHandleListener(new OnPingpaiItemHandleListener() {

                @Override
                public boolean onClickPageItem(ViewGroup container, PingPaiItemRelativeLayout itemView,
                                               String tabelkey, int pageIndex, int itemIndex) {
                    onClickGoodsItem(itemView);
                    return false;
                }

                @Override
                public boolean onRequestImageFunc(ViewGroup container, PingPaiItemRelativeLayout itemView,
                                                  String tabelkey, int pageIndex, int itemIndex) {
                    return false;
                }

                @Override
                public boolean onInstantiateItemOfHandle(ViewGroup container, String tabelkey, int newPageIndex) {
                    if (onNeedRequestData(newPageIndex)) {

                    }

                    return false;
                }

                @Override
                public boolean onRemoveItemOfHandle(ViewGroup container, String tabelkey, int removePageIndex) {
                    return false;
                }

            });
        }

        mClassificationPagerAdapter.onSetViewPagerInfo(mItem_Info);

        mClassificationPagerAdapter.onSetActivity(this);

        mClassificationPagerAdapter.setListData(mListData);

        if (mViewPager != null) {
            mViewPager.setAdapter(mClassificationPagerAdapter);
        }
        mClassificationPagerAdapter.notifyDataSetChanged();
        AppDebug.i(TAG, TAG + ".onBindViewpagerData mClassificationPagerAdapter.getCount()="
                + mClassificationPagerAdapter.getCount());
        if (mClassificationPagerAdapter.getCount() > 0) {
            viewHolder.pingpaiDefaultDisplayRelativeLayout.setVisibility(View.GONE);
        } else {
            viewHolder.pingpaiDefaultDisplayRelativeLayout.setVisibility(View.VISIBLE);
        }
    }

    protected void onInitTitleRightLayout(int cateCount) {

        onInitTitleRightLayout(null, cateCount);
    }

    protected void onInitTitleRightLayout(CategoryMO category, int cateCount) {
        if (viewHolder.typeTextView != null) {
            String text;
            if (null != category) {
                text = category.getName();
            } else {
                text = getString(R.string.jhs_quanbu);
            }
            viewHolder.typeTextView.setText(text);
        }
        if (viewHolder.numTextView != null && mListData != null) {
            viewHolder.numTextView.setText(mListData.size() + getString(R.string.jhs_pinpai_num));
        }

        AppDebug.i(TAG, TAG + ".onInitTitleRightLayout category=" + category + ", cateCount=" + cateCount);
        viewHolder.categoryAllImageView.setOnClickListener(mCategorySelectedListener);
        viewHolder.titleRightRelativeLayout.setVisibility(View.VISIBLE);
    }

    //

    /**
     * 点击条目, 并且进入到品牌团单个页面
     * @param pingPaiItemRelativeLayout
     *            选中的条目
     */
    private void onClickGoodsItem(final PingPaiItemRelativeLayout pingPaiItemRelativeLayout) {

        AppDebug.i(TAG, "onClickGoodsItem  pingPaiItemRelativeLayout  = " + pingPaiItemRelativeLayout);

        if (pingPaiItemRelativeLayout == null)
            return;

        BrandMO itemData = pingPaiItemRelativeLayout.onGetBrandModel();

        AppDebug.i(TAG, "onClickGoodsItem  itemData  = " + itemData);

        if (itemData == null) {
            return;
        }

        Intent detailIntent = new Intent(this, BrandDetailActivity.class);
        if (null != mCurrentCategoryMO) {
            detailIntent.putExtra("fromCate", mCurrentCategoryMO.getName());
        } else {
            detailIntent.putExtra("fromCate", getString(R.string.jhs_quanbu));
        }
        detailIntent.putExtra("data", itemData);
        startActivity(detailIntent);
        //统计 聚划算品牌团各屏的点击情况
        if (null != mItem_Info) {
            String controlName = Utils.getControlName(getFullPageName(), "P", mItem_Info.mCurrentViewPage);
            Map<String, String> p = Utils.getProperties();
            if (null != mCurrentCategoryMO) {
                if (!StringUtil.isEmpty(mCurrentCategoryMO.getName())) {
                    p.put("cate_name", mCurrentCategoryMO.getName());
                }
            } else {
                p.put("cate_name", getString(R.string.jhs_quanbu));
            }
            TBS.Adv.ctrlClicked(CT.ListItem, controlName, Utils.getKvs(p));
        }
    }
 
    /**
     * 请求数据
     * @param itemInfo
     */
    private void onRequestExecute(Item_Info itemInfo) {
        if (mInLoadingData) {
            return;
        }

        mInLoadingData = true;

        int mRequestPageSize = itemInfo.mPageSizes;
        int currReqPageCount = itemInfo.mCurrentPages;

        AppDebug.i(TAG, "onRequestExecute  mRequestPageSize  = " + mRequestPageSize);
        AppDebug.i(TAG, "onRequestExecute  currReqPageCount  = " + currReqPageCount);

        OnWaitProgressDialog(true);

        getCategories(currReqPageCount, mRequestPageSize);
    }

    private void getBrands(int currReqPageCount, int requestpage) {
        AppDebug.i(TAG, TAG + ".getBrands currReqPageCount=" + currReqPageCount + ", requestpage=" + requestpage);
        String optStr = null;
        if (mCurrentCategoryMO != null) {
            optStr = mCurrentCategoryMO.getOptStr();
        }
        OnWaitProgressDialog(true);
        //        JuBusinessRequest.getBusinessRequest().requestGetOption(this, OptionType.Brand.getplatformId(), optStr,
        //                currReqPageCount, requestpage, new OptionBusinessRequestListener(this, currReqPageCount, requestpage));
        MyBusinessRequest.getInstance().requestGetOption(OptionType.Brand.getplatformId(), optStr, currReqPageCount,
                requestpage, new JuRetryRequestListener<CountList<Option>>(this) {

                    @Override
                    public void onSuccess(BaseActivity baseActivity, CountList<Option> data) {
                        BrandHomeActivity activity = null;
                        if (baseActivity instanceof BrandHomeActivity) {
                            activity = (BrandHomeActivity) baseActivity;
                        }
                        if (null == activity) {
                            return;
                        }
                        activity.OnWaitProgressDialog(false);
                        activity.mInLoadingData = false;

                        if (data == null || data.size() < 1 || activity.mListData == null) {
                            activity.mMainHandler.sendEmptyMessage(Item_Info.SHOW_BACKGROUND_CONNECT);
                            return;
                        }
                        CountList<BrandMO> mBrands = ModelTranslator.translateBrand(data);
                        activity.mListData.addAll(mBrands);
                        // 更新下一次要加载的页
                        activity.mItem_Info.mCurrentPages++;
                        activity.mItem_Info.mTotalPage = activity.mListData.size() / Item_Info.PAGE_COUNT;

                        Message mes = activity.mMainHandler.obtainMessage(Item_Info.UPDATE_DISPLAY_PAGE_TOTAL_MSG,
                                activity.mItem_Info.mTotalPage, activity.mItem_Info.mTotalPage);
                        activity.mMainHandler.sendMessageDelayed(mes, 150);
                        AppDebug.i(activity.TAG, "onHandleOfRequestDone  mTotalPage  = "
                                + activity.mItem_Info.mTotalPage);

                        if (activity.mFirstRequest) {
                            activity.mFirstRequest = false;
                            activity.onInitLayout();
                        }
                    }

                    @Override
                    public boolean onUserError(BaseActivity baseActivity, int resultCode, String msg) {
                        BrandHomeActivity activity = null;
                        if (baseActivity instanceof BrandHomeActivity) {
                            activity = (BrandHomeActivity) baseActivity;
                        }
                        if (activity != null) { 
                            activity.OnWaitProgressDialog(false);
                        }                        
                        return super.onUserError(baseActivity, resultCode, msg);
                    } 
                });
    }

    private void getCategories(final int currReqPageCount, final int requestpage) {
        //        AppDebug.i(TAG, TAG + ".getCategories currReqPageCount=" + currReqPageCount + ", requestpage=" + requestpage);
        //        JuBusinessRequest.getBusinessRequest().requestGetOption(this, OptionType.Brand.getplatformId(),
        //                "frontCatId:-1;", 0, 200, new CategoriesBusinessRequestListener(this, currReqPageCount, requestpage));
        MyBusinessRequest.getInstance().requestGetOption(OptionType.Brand.getplatformId(), "frontCatId:-1;", 0, 200,
                new JuRetryRequestListener<CountList<Option>>(this, true) {

                    @Override
                    public void onSuccess(BaseActivity baseActivity, CountList<Option> data) {
                        BrandHomeActivity activity = null;
                        if (baseActivity instanceof BrandHomeActivity) {
                            activity = (BrandHomeActivity) baseActivity;
                        }
                        if (null == activity) {
                            return;
                        }
                        activity.OnWaitProgressDialog(false);
                        if (null != data) {
                            activity.mCategoryMOs = ModelTranslator.translateCategory(data);
                            activity.getBrands(currReqPageCount, requestpage);
                        } else {
                            final BrandHomeActivity brandHomeActivity = activity;
                            DialogUtils.show(brandHomeActivity, brandHomeActivity.getString(R.string.jhs_no_data),
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            brandHomeActivity.finish();
                                        }
                                    });
                        }

                    }

                    @Override
                    public boolean onUserError(BaseActivity baseActivity, int resultCode, String msg) {
                        BrandHomeActivity activity = null;
                        if (baseActivity instanceof BrandHomeActivity) {
                            activity = (BrandHomeActivity) baseActivity;
                        }
                        if (activity != null) {
                            activity.OnWaitProgressDialog(false);
                        } 
                        return super.onUserError(baseActivity, resultCode, msg);
                    } 
                });
    }

    /**
     * 判断是否需加载数据
     * @param newPageIndex
     * @return
     */
    private boolean onNeedRequestData(int newPageIndex) {

        if (mListData == null)
            return false;

        if (mItem_Info == null)
            return false;

        int currSavePageSize = mListData.size() / Item_Info.PAGE_COUNT;
        int needPageSize = newPageIndex + mItem_Info.FACT_RETURN_PAGE_COUNT;

        AppDebug.i(TAG, "needRequestData,--->  needPageSize = " + needPageSize);
        AppDebug.i(TAG, "needRequestData,--->  currSavePageSize = " + currSavePageSize);

        if (needPageSize > currSavePageSize) {
            return true;
        }

        return false;

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mDestroy = true;
        onClearBufferData();

        super.onDestroy();
    }

    @Override
    public String getPageName() {
        return TBS_PAGE_TAG;
    }

    /**
     * Class Descripton.
     * @version
     * @author hanqi
     * @data 2014-11-18 上午11:00:37
     */
    private static final class BrandHomeHandle extends AppHandler<BrandHomeActivity> {

        /**
         * @param t
         */
        private BrandHomeHandle(BrandHomeActivity t) {
            super(t);
        }

        public void handleMessage(Message msg) {
            BrandHomeActivity activity = getT();
            if (null == activity) {
                return;
            }
            ViewHolder holder = activity.getVewHolder();
            switch (msg.what) {
                case Item_Info.UPDATE_DISPLAY_PAGE_MSG:
                    // 更新页数
                    int position = (Integer) msg.arg1;
                    AppDebug.i(activity.TAG, "Item_Info.UPDATE_DISPLAY_PAGE_MSG  position = " + position);
                    if (null == activity.mItem_Info) {
                        return;
                    }
                    activity.mItem_Info.mCurrentViewPage = position + 1;
                    holder.currentNumTextView.setText("" + activity.mItem_Info.mCurrentViewPage);
                    holder.pingpaiRelativeLayout.setVisibility(View.VISIBLE);
                    break;
                case Item_Info.UPDATE_DISPLAY_PAGE_TOTAL_MSG:
                    int totalpage = (Integer) msg.arg1;
                    AppDebug.i(activity.TAG, "Item_Info.UPDATE_DISPLAY_PAGE_TOTAL_MSG  totalpage = " + totalpage);
                    // 更新总页数显示
                    holder.allNumTextView.setText("" + totalpage);
                    holder.pingpaiRelativeLayout.setVisibility(View.VISIBLE);
                    break;
                case Item_Info.UPDATE_DISPLAY_REQUEST_LAYOUT:
                    // 请求数据
                    activity.onRequestExecute(activity.mItem_Info);
                    break;

                case Item_Info.HIDE_WAIPROGRESSDIALOG:
                    // 隐藏Dialog
                    activity.OnWaitProgressDialog(false);
                    break;

                case Item_Info.SHOW_BACKGROUND_CONNECT:
                    // 更新背景内容
                    if (activity.mFirstStart) {
                        activity.mFirstStart = false;

                        int size = 0;
                        CountList<BrandMO> brands = activity.getListData();
                        if (brands != null) {
                            size = brands.size();
                        }

                        if (size < 1) {
                            holder.pingpaiDefaultDisplayRelativeLayout.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
            }

        }
    }

    public class Item_Info {

        public static final int UPDATE_DISPLAY_PAGE_MSG = 1;

        public static final int UPDATE_DISPLAY_PAGE_TOTAL_MSG = UPDATE_DISPLAY_PAGE_MSG + 1;

        public static final int UPDATE_DISPLAY_REQUEST_LAYOUT = UPDATE_DISPLAY_PAGE_MSG + 2;

        public static final int HIDE_WAIPROGRESSDIALOG = UPDATE_DISPLAY_PAGE_MSG + 4;

        public static final int SHOW_BACKGROUND_CONNECT = UPDATE_DISPLAY_PAGE_MSG + 5;

        // 允许连续按的页数
        public static final int KEY_PAGE = 3;

        // 请求的页数
        public static final int EACH_REQUEST_PAGE_COUNT = 100;

        // 界面显示中：每页放几个条目
        public static final int PAGE_COUNT = 4;

        // 每次请求时,实际返回的页数
        public int FACT_RETURN_PAGE_COUNT = 0;

        // 当前请求第几页的数据，[注意： 此处的页数跟页面显示的页数不同]
        public int mCurrentPages = 1;

        // 当前页请求的容量
        public int mPageSizes = EACH_REQUEST_PAGE_COUNT * PAGE_COUNT;

        // 总共显示多少页
        public int mTotalPage = 0;

        public int mCurrentViewPage = 1;

    }

    public class CategorySelecteDialog extends Dialog {

        private static final int MAX_LENGTH = 6;

        private int TOP_MARGIN = getResources().getDimensionPixelSize(R.dimen.dp_20);

        private int LEFT_MARGIN = getResources().getDimensionPixelSize(R.dimen.dp_10);

        private int BUTTON_WIDTH = getResources().getDimensionPixelSize(R.dimen.dp_216);

        private int BUTTON_HEIGHT = getResources().getDimensionPixelSize(R.dimen.dp_90);

        private int BUTTON_WIDTH_TOTAL = getResources().getDimensionPixelSize(R.dimen.dp_236);

        private int BUTTON_HEIGHT_TOTAL = getResources().getDimensionPixelSize(R.dimen.dp_110);

        // 默认一行的Item个数
        private int mEachLineCount = 5;

        private Context mContext;

        private FrameLayout linCategorysFrameLayout;
        private View loadProgressView;

        public CategorySelecteDialog(Context context, int theme) {
            super(context, theme);
            mContext = context;
        }

        public CategorySelecteDialog(Context context) {
            super(context, R.style.DialogNoTitleStyleFullscreen);
            mContext = context;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.jhs_dialog_select_category);

            linCategorysFrameLayout = (FrameLayout) findViewById(R.id.lin_categorys);
            loadProgressView = findViewById(R.id.load_progress);

            if (mCategoryMOs == null || mCategoryMOs.size() <= 0) {
                //                JuBusinessRequest.getBusinessRequest().requestGetOption(BrandHomeActivity.this,
                //                        OptionType.Brand.getplatformId(), "frontCatId:-1;", 0, 200,
                //                        new DialogOptionBusinessRequestListener(this));
                MyBusinessRequest.getInstance()
                        .requestGetOption(
                                OptionType.Brand.getplatformId(),
                                "frontCatId:-1;",
                                0,
                                200,
                                new JuBrandCateSelectDialogRetryRequestListener<CountList<Option>>(
                                        BrandHomeActivity.this, this) {

                                    @Override
                                    public void onSuccess(BaseActivity baseActivity, CountList<Option> data) {
                                        Dialog dialog = getDialog();
                                        CategorySelecteDialog categorySelecteDialog = null;
                                        if (null != dialog && dialog instanceof CategorySelecteDialog) {
                                            categorySelecteDialog = (CategorySelecteDialog) dialog;
                                        }
                                        if (null == categorySelecteDialog) {
                                            return;
                                        }
                                        if (null != data) {
                                            categorySelecteDialog.updateData(ModelTranslator.translateCategory(data));
                                        }
                                    }
                                });
            } else {
                updateData(mCategoryMOs);
            }
        }

        // 刷新类目信息
        public void updateData(CountList<CategoryMO> categorys) {
            AppDebug.i(TAG, TAG + ".CategorySelecteDialog.updateData categorys=" + categorys);
            linCategorysFrameLayout.removeAllViews();

            mCategoryMOs = checkAndAddAllButton(categorys);

            loadProgressView.setVisibility(View.GONE);

            int size = mCategoryMOs.size();

            int position = 0;

            if (size > mEachLineCount && size < mEachLineCount * 2) {
                if (size % 2 == 0) {
                    mEachLineCount = size / 2;
                } else {
                    mEachLineCount = size / 2 + 1;
                }
            }

            int line = 0, row = 0;

            for (int i = 0; i < mCategoryMOs.size(); i++) {

                CategoryMO category = mCategoryMOs.get(i);

                FrameLayout.LayoutParams categoryparams = new FrameLayout.LayoutParams(BUTTON_WIDTH, BUTTON_HEIGHT);

                position = i + 1;

                // 列数
                line = i % mEachLineCount;

                // 行数
                row = i / mEachLineCount;

                CategoryButton button = newCategoryButton(position, category);

                categoryparams.leftMargin = LEFT_MARGIN + BUTTON_WIDTH_TOTAL * line;
                categoryparams.topMargin = TOP_MARGIN + BUTTON_HEIGHT_TOTAL * row;

                Log.d(TAG,
                        " ====================>>>> line " + line + " row " + row + " " + i + " mEachLineCount "
                                + mEachLineCount + " size " + categorys.size() + "; mCategoryMOs.size  = "
                                + mCategoryMOs.size());

                linCategorysFrameLayout.addView(button, categoryparams);
            }

            // 刷新parent视图
            ViewGroup.LayoutParams Pparams = linCategorysFrameLayout.getLayoutParams();
            Pparams.height = TOP_MARGIN + BUTTON_HEIGHT_TOTAL * (row + 1) + TOP_MARGIN;
            Pparams.width = LEFT_MARGIN + BUTTON_WIDTH_TOTAL * mEachLineCount + LEFT_MARGIN;
            linCategorysFrameLayout.setLayoutParams(Pparams);
            linCategorysFrameLayout.setVisibility(View.VISIBLE);

        }

        private CountList<CategoryMO> checkAndAddAllButton(CountList<CategoryMO> categorys) {
            CountList<CategoryMO> cates = new CountList<CategoryMO>();
            cates.clear();

            cates.addAll(categorys);

            CategoryMO cateall = new CategoryMO();
            cateall.setName(getString(R.string.jhs_quanbu));
            cateall.setCid("0");

            cates.add(0, cateall);

            return cates;
        }

        private CategoryButton newCategoryButton(int position, CategoryMO category) {
            CategoryButton button = new CategoryButton(mContext);
            button.setTextColor(Color.rgb(0xFF, 0xFF, 0xFF));
            button.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sp_32));
            if (category != null) {
                button.setText(category.getName());
                button.setTag(category);
            } else {
                button.setText(getString(R.string.jhs_quanbu));
                button.setTag(null);
            }
            button.setMaxEms(MAX_LENGTH);
            button.setTag(R.id.tag_position, position);
            //            button.setId(position); //这会导致找不到资源的错误提示
            button.setOnClickListener(mCategoryItemSelectedListener);
            return button;
        }

        public void setSelected(CategoryMO category) {
            if (category == null) {
                return;
            }

            if (category != null && linCategorysFrameLayout != null) {
                for (int i = 0; i < linCategorysFrameLayout.getChildCount(); i++) {
                    View view = linCategorysFrameLayout.getChildAt(i);
                    Object obj = view.getTag();
                    if (obj != null && obj instanceof CategoryMO) {
                        CategoryMO cate = (CategoryMO) obj;
                        if (cate.getCid() != null && cate.getCid().equals(category.getCid())) {
                            view.requestFocus();
                            if (view instanceof CategoryButton) {
                                ((CategoryButton) view).setFocusStatusView();
                            }
                            break;
                        }
                    }
                }
            }
        }

    }

    //    private int getDP(int value) {
    //        return (int) (value * SystemUtil.getMagnification());
    //    }

    public ViewHolder getVewHolder() {
        return viewHolder;
    }

    public CountList<BrandMO> getListData() {
        return mListData;
    }

    public class ViewHolder {

        ViewGroup viewContents;
        FrameLayout pingpaiMainFramelayout;
        TextView currentNumTextView;
        View pingpaiRelativeLayout;
        TextView allNumTextView;
        View pingpaiDefaultDisplayRelativeLayout;
        View titleRightRelativeLayout;
        TextView typeTextView;
        ImageView categoryAllImageView;
        TextView numTextView;
        View loadProgressView;

        public ViewHolder() {
            viewContents = (ViewGroup) getLayoutInflater().inflate(R.layout.jhs_pinpaituan_activity, null);
            pingpaiMainFramelayout = (FrameLayout) viewContents.findViewById(R.id.pingpai_mainFramelayout);
            currentNumTextView = ((TextView) viewContents.findViewById(R.id.current_num));
            pingpaiRelativeLayout = viewContents.findViewById(R.id.pingpai_relative);
            allNumTextView = ((TextView) viewContents.findViewById(R.id.all_num));
            pingpaiDefaultDisplayRelativeLayout = viewContents.findViewById(R.id.pingpai_default_display);
            titleRightRelativeLayout = viewContents.findViewById(R.id.title_right);
            typeTextView = (TextView) viewContents.findViewById(R.id.type);
            categoryAllImageView = (ImageView) viewContents.findViewById(R.id.category_all);
            numTextView = (TextView) viewContents.findViewById(R.id.num);

            loadProgressView = viewContents.findViewById(R.id.load_progress);
        }
    }
}
