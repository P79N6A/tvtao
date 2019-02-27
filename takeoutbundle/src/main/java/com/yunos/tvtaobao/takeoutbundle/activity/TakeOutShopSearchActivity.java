package com.yunos.tvtaobao.takeoutbundle.activity;


import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.powyin.scroll.adapter.AdapterDelegate;
import com.powyin.scroll.adapter.MultipleListAdapter;
import com.powyin.scroll.adapter.MultipleRecycleAdapter;
import com.tvlife.imageloader.utils.ClassicOptions;
import com.tvtaobao.voicesdk.ASRNotify;
import com.tvtaobao.voicesdk.base.SDKInitConfig;
import com.tvtaobao.voicesdk.bo.DomainResultVo;
import com.tvtaobao.voicesdk.interfaces.ASRSearchHandler;
import com.tvtaobao.voicesdk.request.NlpNewRequest;
import com.tvtaobao.voicesdk.tts.TTSUtils;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.app.widget.focus.FocusListView;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.core.AppInitializer;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.SystemConfig;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.AddBagBo;
import com.yunos.tvtaobao.biz.request.bo.ItemListBean;
import com.yunos.tvtaobao.biz.request.bo.ShopDetailData;
import com.yunos.tvtaobao.biz.request.bo.ShopSearchResultBean;
import com.yunos.tvtaobao.biz.request.bo.TakeOutBag;
import com.yunos.tvtaobao.biz.request.core.ServiceCode;
import com.yunos.tvtaobao.biz.request.core.ServiceResponse;
import com.yunos.tvtaobao.biz.request.item.ShopSearchRequest;
import com.yunos.tvtaobao.takeoutbundle.R;
import com.yunos.tvtaobao.takeoutbundle.adapter.ViewHolderCollection;
import com.yunos.tvtaobao.takeoutbundle.adapter.ViewHolderSearch;
import com.yunos.tvtaobao.takeoutbundle.adapter.ViewHolderSearchMore;
import com.yunos.tvtaobao.takeoutbundle.h5.plugin.TakeOutPayBackPlugin;
import com.yunos.tvtaobao.takeoutbundle.view.PromptPop;
import com.yunos.tvtaobao.takeoutbundle.view.SkuSelectDialog;
import com.yunos.tvtaobao.takeoutbundle.widget.CardView;
import com.yunos.tvtaobao.takeoutbundle.widget.FocusFrameLayout;
import com.yunos.tvtaobao.takeoutbundle.widget.SelectFocusListView;
import com.yunos.tvtaobao.takeoutbundle.widget.StateFocusListView;
import com.yunos.tvtaobao.takeoutbundle.widget.WeakDataHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mtopsdk.mtop.domain.MethodEnum;
import mtopsdk.mtop.domain.MtopResponse;
import mtopsdk.mtop.intf.MtopBuilder;

/**
 * Created by yuanqihui on 18/2/26.
 *
 * @describe 外卖店内搜索页
 */

public class TakeOutShopSearchActivity extends BaseActivity implements ASRSearchHandler {
    private static final String TAG = "TakeOutShopSearchActivity";
    private final static DecimalFormat format = new DecimalFormat("¥#.##");
    private String shopId;                                          //门店id
    private String outStoreId;
    private String serviceId;                                       //服务id
    private String scm;                                             //未知
    private String from;                                            //来源
    private ShopSearchResultBean shopSearchResultBean;                          //店铺数据
    private TakeOutBag mTakeOutBag;                                 //购物车数据

    private Location location;
    private boolean isShopOpen = false;                             //是否开张店铺
    private BusinessRequest businessRequest;                        //网络请求


    //    private FocusFrameLayout flVouchers;
    private FocusFrameLayout flDetail, flOrders, flUsers, flFirst;
    private ImageView ivFirstFocus, ivOrdersFocus, ivUserFocus, ivDetailFocus;
    private TextView tvDetailPop, tvOrdersPop, tvUsersPop, tvFirstPop;
    private TextView tvSearchWord; //搜索关键字
    // 原版焦点管理者
    private FocusPositionManager layout;

    // 没有更多商品了
    private ViewGroup shop_loading;

    // 商品分类列表
    private StateFocusListView good_kind;
    private MultipleListAdapter<ShopDetailData.ItemGenreWithItemsListBean> good_kind_adapter;

    // 商品列表
    private SelectFocusListView good_all;
    private static TextView shop_loading_notice;
    private View shop_empty;
    private MultipleListAdapter<Object> selectGoods;

    // 购物车
    private FocusFrameLayout collection_focus;
    private RecyclerView good_collection_recycle;
    private MultipleRecycleAdapter<TakeOutBag.CartItemListBean> good_collectionMultipleRecycleAdapter;
    private TextView collection_pay;
    private TextView good_collection_need_more;
    private TextView good_collection_pay_ori;
    private TextView collection_pay_dlive_amount;
    private ImageView good_collection_empty;
    private View[] good_collection_status = new View[3];
    private View collection_status_2_bac;

    /**
     * 购物车 处理购物车内容滚动 和 点击
     */
    private FocusFrameLayout.OnFocusActionLister focusFrameLayout = new FocusFrameLayout.OnFocusActionLister() {
        boolean hasFocus;

        @Override
        public boolean onInterceptDirectionKeyEvent(int direction, KeyEvent keyEvent) {
            AppDebug.i(TAG, "onInterceptDirectionKeyEvent");
            switch (direction) {
                case View.FOCUS_DOWN:
                    // 默认有足够空间时 向上滚动2个item 距离
                    if (good_collection_recycle.canScrollVertically(1) && good_collection_recycle.getChildCount() > 0) {
                        good_collection_recycle.smoothScrollBy(0, good_collection_recycle.getChildAt(0).getHeight() * 2);
                    }
                    return true;
                case View.FOCUS_UP:
                    // 默认有足够空间时 向下滚动2个item 距离
                    if (good_collection_recycle.canScrollVertically(-1) && good_collection_recycle.getChildCount() > 0) {
                        good_collection_recycle.smoothScrollBy(0, -good_collection_recycle.getChildAt(0).getHeight() * 2);
                    }
                    return true;
            }
            return false;
        }

        @Override
        public void onFocusChanged(boolean gainFocus) {
            AppDebug.i(TAG, "onFocusChanged" + gainFocus);
            if (gainFocus != hasFocus) {
                hasFocus = gainFocus;

                if (collection_focus.isFocused()) {
                    collection_pay.setTextColor(getResources().getColor(R.color.white));
                    good_collection_pay_ori.setTextColor(0xffffc7a5);
                    collection_pay_dlive_amount.setTextColor(0xffffe8da);
                    collection_focus.getChildAt(0).setBackgroundResource(R.drawable.good_collection_bac_focusl);
                } else {
                    collection_pay.setTextColor(getResources().getColor(R.color.red_ff6000));
                    good_collection_pay_ori.setTextColor(0xff546274);
                    collection_pay_dlive_amount.setTextColor(0xff8396ae);
                    collection_focus.getChildAt(0).setBackgroundResource(R.drawable.bg_empty);
                }

                if (hasFocus && mTakeOutBag != null) {
                    if (mTakeOutBag.canBuy) {
                        collection_status_2_bac.setVisibility(View.VISIBLE);
                    } else {
                        collection_status_2_bac.setVisibility(View.GONE);
                    }
                } else {
                    collection_status_2_bac.setVisibility(View.GONE);
                }
            }
        }

        /**
         *
         * xxx.html?cartInfo=appkey*online*门店id_~【商品id_饿了么商品id_outIdType_skuId_skuname_数量_cartid_属性信息，例如(辣度:微辣;甜度:微甜)】|
         * 【商品id_饿了么商品id_outIdType_skuId_skuname_数量_cartid_属性信息，例如(辣度:微辣;甜度:微甜)】
         * xxx.html?cartInfo=appkey*online*门店id_~【商品id_饿了么商品id_outIdType_skuId_skuname_数量_cartid_属性信息，例如(辣度:微辣;甜度:微甜)】|
         * 【商品id_饿了么商品id_outIdType_skuId_skuname_数量_cartid_属性信息，例如(辣度:微辣;甜度:微甜)】
         * @param view
         */
        @Override
        public void onClick(View view) {
            int goodCount = 0;
            if (mTakeOutBag != null && mTakeOutBag.canBuy && isShopOpen) {
                StringBuilder stringBuilder = new StringBuilder();
                //   String test = "tvtaobao://home?module=common&page=http://debug003.waptest.taobao.net:3128/zebra-pages-yunos-96315/index-pc?cartInfo=";
                //   stringBuilder.append(test);
                stringBuilder.append("tvtaobao://home?module=common&page=https://tvos.taobao.com/wow/yunos/act/tvtaoboa-waimai-ord?cartInfo=");
                stringBuilder.append(Config.getChannel());
                stringBuilder.append("*online*");
                stringBuilder.append(shopId);
                //添加outStoreId
                if (!TextUtils.isEmpty(outStoreId)){
                    stringBuilder.append("_~*_");
                    stringBuilder.append(outStoreId);
                }
                stringBuilder.append("_~");

                boolean hasItem = false;
                for (int i = 0; mTakeOutBag.__cartItemList != null && i < mTakeOutBag.__cartItemList.size(); i++) {
                    TakeOutBag.CartItemListBean itemListBean = mTakeOutBag.__cartItemList.get(i);
                    if (itemListBean.quantity <= 0) {
                        continue;
                    }
                    goodCount += itemListBean.amount;

                    StringBuilder builder = new StringBuilder();
                    builder.append(itemListBean.itemId);
                    builder.append("_");
                    builder.append(itemListBean.outItemId);
                    builder.append("_");
                    builder.append(itemListBean.type);
                    builder.append("_");
                    builder.append(itemListBean.skuId);
                    builder.append("_");
                    builder.append(itemListBean.skuName == null ? "" : itemListBean.skuName);
                    builder.append("_");
                    builder.append(itemListBean.amount);
                    builder.append("_");
                    builder.append(itemListBean.cartId);
                    builder.append("_");

                    boolean hasSkuPar = false;
                    for (int j = 0; itemListBean.skuProperties != null && j < itemListBean.skuProperties.size(); j++) {
                        TakeOutBag.CartItemListBean.SkuPropertiesBean skuPropertiesBean = itemListBean.skuProperties.get(j);
                        String value = skuPropertiesBean.value;
                        if (value != null && value.length() > 0) {
                            builder.append(skuPropertiesBean.name);
                            builder.append(":");
                            builder.append(value);
                            builder.append(";");
                            hasSkuPar = true;
                        }
                    }

                    if (hasSkuPar) {
                        builder.setLength(builder.length() - 2);
                    } else {
                        builder.append("");
                    }
                    stringBuilder.append(builder.toString());
                    stringBuilder.append("|");
                    hasItem = true;
                }

                if (hasItem) {
                    stringBuilder.setLength(stringBuilder.length() - 1);
                }

                if (TextUtils.isEmpty(from)) {
                    stringBuilder.append("&from=").append(from);
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(stringBuilder.toString()));
                startActivity(intent);
            }

            // todo 埋点
            Map<String, String> properties = Utils.getProperties();
            properties.put("shop id", shopId);
            Utils.utControlHit(getFullPageName(), "Page_waimai_shop_cart_button_done_all", properties);

            // todo 埋点
            properties = Utils.getProperties();
            properties.put("item count", String.valueOf(goodCount));
            Utils.utControlHit(getFullPageName(), "Page_waimai_shop_cart_button_done_p" + goodCount, properties);

        }
    };

    /**
     * 购物车 处理购物车商品过多 伸长 与 压缩 逻辑;
     */
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        int maxHei = 0;
        int minHei = 0;
        Rect rectTop = new Rect();
        Rect rectButton = new Rect();

        @Override
        public void onGlobalLayout() {
            if (maxHei == 0) {
                maxHei = getResources().getDimensionPixelOffset(R.dimen.dp_160);
                minHei = getResources().getDimensionPixelOffset(R.dimen.dp_36);
            }
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) collection_focus.getLayoutParams();
            RecyclerView.Adapter adapter = good_collection_recycle.getAdapter();
            if (layoutParams == null) return;
            if (good_collection_recycle.getChildCount() == 0) return;
            if (adapter == null) return;

            View top = good_collection_recycle.getChildAt(0);
            View button = good_collection_recycle.getChildAt(good_collection_recycle.getChildCount() - 1);
            rectTop.set(0, 0, 0, 0);
            rectButton.set(0, 0, 0, 0);
            good_collection_recycle.offsetDescendantRectToMyCoords(top, rectTop);
            good_collection_recycle.offsetDescendantRectToMyCoords(button, rectButton);

            // 获取当前显示的item总高度
            int hei = rectTop.top + top.getHeight() - (rectButton.top);
            // 加入 未layout item高度  获取显示此列表的理想高度
            int noLayoutCount = adapter.getItemCount() - good_collection_recycle.getChildCount();
            hei += top.getHeight() * noLayoutCount;

            // 获取合适的margeTop值;
            hei -= good_collection_recycle.getHeight();
            hei = layoutParams.topMargin - hei;
            int target = maxHei < hei ? maxHei : hei;
            target = minHei > target ? minHei : target;

            // 如果当前layout.topMargin不合适； 重新设置
            if (layoutParams.topMargin != target) {
                layoutParams.topMargin = target;
                collection_focus.setLayoutParams(layoutParams);
            }
        }
    };


    /**
     * js 桥 控制清空购物车
     */
    private TakeOutPayBackPlugin takeOutPayBackPlugin = new TakeOutPayBackPlugin() {
        @Override
        public void onCall(String param, long cbData) {
            AppDebug.e(TAG, ":::" + param + " " + cbData);
            try {
                JSONObject jsonObject = new JSONObject(param);
                boolean create = jsonObject.getBoolean("createOrder");
                if (create) {
                    List<TakeOutBag.CartItemListBean> dataList = mTakeOutBag.__cartItemList;
                    JSONArray array = new JSONArray();
                    for (TakeOutBag.CartItemListBean itemListBean : dataList) {
                        if (itemListBean.itemId != null) {
                            try {
                                jsonObject = new JSONObject();
                                jsonObject.put("itemId", itemListBean.itemId);
                                jsonObject.put("skuId", itemListBean.skuId);
                                jsonObject.put("quantity", itemListBean.amount);
                                jsonObject.put("cartId", itemListBean.cartId);
                                array.put(jsonObject);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    final String json = array.toString();
                    if (!TextUtils.isEmpty(json)) {
                        // 网络默认main 线程执行 需要切换js 线程
                        getWindow().getDecorView().post(new Runnable() {
                            @Override
                            public void run() {
                                businessRequest.requestTakeOutUpdate(shopId,
                                        latitude, longitude, "3",
                                        json, new GetShopHomeBagListener(new WeakReference<BaseActivity>(TakeOutShopSearchActivity.this), false, true));
                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private String orderType = "price";
    private int pageSize = 1000;
    private int pageNo = 1;
    private ShopDetailData shopDetailData;
    private ShopSearchResultBean mShopSearchData;
    private String tts;
    private ArrayList<String> tipsList;
    private String keyword;
    private String shopStatus;
    private String asr;
    String uuid = null;
    String osVersion = null;
    String model = null;
    String androidVersion = null;
    String appPackage = null;
    String appKey = null;
    String versionCode = null;
    String versionName = null;
    private String longitude;
    private String latitude;
    private FocusFrameLayout flTakeoutShopHome;
    private TextView tvShopHomePop;
    private ImageView ivShopHomeFocus;
    private static String fullPageName;
    private String ttsText;
    private static View shop_search_empty;
    private View shop_title;
    private ASRNotify mNotify;
    private int page = 1;
    private boolean fromHome;
    private boolean showDialog;
    private ImageView shopIcon;
    private TextView shopName;
    private CardView card_view;
    private String shopNameStr;
    private String shopLogoStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onKeepActivityOnlyOne(TakeOutShopSearchActivity.class.getName());
        setContentView(R.layout.activity_take_out_shop_search_result);
        mNotify = ASRNotify.getInstance();
        shopId = getIntent().getStringExtra(BaseConfig.INTENT_KEY_SHOPID);
        keyword = getIntent().getStringExtra("keyword");
        asr = getIntent().getStringExtra("asr");
        shopNameStr = getIntent().getStringExtra("shopName");
        shopLogoStr = getIntent().getStringExtra("shopLogo");
        if (WeakDataHolder.getInstance() != null) {
            shopDetailData = (ShopDetailData) WeakDataHolder.getInstance().getData("shopDetailData");
            mShopSearchData = (ShopSearchResultBean) WeakDataHolder.getInstance().getData("shopSearchData");
        }
//        if (mShopSearchData == null) {
//            mShopSearchData = (ShopSearchResultBean) getIntent().getSerializableExtra("shopSearchData");
//        }
//        shopDetailData = (ShopDetailData) getIntent().getSerializableExtra("shopDetailData");
        fromHome = getIntent().getBooleanExtra("fromHome", false);
        from = getIntent().getStringExtra("v_from");
        isShopOpen = getIntent().getBooleanExtra("isShopOpen", false);
//     添加outStoreId
        if (shopDetailData!=null&&shopDetailData.getStoreDetailDTO()!=null){
            outStoreId=shopDetailData.getStoreDetailDTO().getOutId();
        }
        shopStatus = getIntent().getStringExtra("shopStatus");
        scm = getIntent().getStringExtra("scm");
        tts = getIntent().getStringExtra("tts");
        ttsText = getIntent().getStringExtra("ttsText");
        tipsList = getIntent().getStringArrayListExtra("tips");
        if (fromHome) {
            TTSUtils.getInstance().showDialog(this, 1);
        }

        if (!TextUtils.isEmpty(shopId)) {
            AppDebug.e(TAG, "shopId = " + shopId);
        }
        AppDebug.e(TAG, "mShopSearchData = " + mShopSearchData);
        if (!TextUtils.isEmpty(shopStatus)) {
            switch (shopStatus) {
                case "BOOKING":    // 预定
                    isShopOpen = true;
                    break;
                case "RESTING":    // 休息
                    isShopOpen = false;
                    break;
                case "":
                case "SELLING":
                case "WILLRESTING":
                    isShopOpen = true;
                    break;
            }
        }
        businessRequest = BusinessRequest.getBusinessRequest();
        uuid = CloudUUIDWrapper.getCloudUUID();
        osVersion = SystemConfig.getSystemVersion();
        model = Build.MODEL;
        androidVersion = Build.VERSION.RELEASE;
        appPackage = getBaseContext().getPackageName();
        appKey = Config.getChannel();
        versionCode = AppInfo.getAppVersionNum() + "";
        versionName = AppInfo.getAppVersionName();
        initLocation();
        // findView
        layout = (FocusPositionManager) findViewById(R.id.focus_manager_search);
        layout.setSelector(new StaticFocusDrawable(new ColorDrawable(0xffffff)));
        shop_title = findViewById(R.id.shop_title);
        shopIcon = (ImageView) findViewById(R.id.shop_icon_pic);
        shopName = (TextView) findViewById(R.id.shop_name);
        shop_loading = (ViewGroup) findViewById(R.id.shop_loading_search);
        good_collection_need_more = (TextView) findViewById(R.id.good_collection_need_more_search);
        good_collection_pay_ori = (TextView) findViewById(R.id.collection_pay_ori_search);
        collection_pay = (TextView) findViewById(R.id.collection_pay_search);
        collection_pay_dlive_amount = (TextView) findViewById(R.id.collection_pay_dlive_amount_search);
        good_collection_status[0] = findViewById(R.id.collection_status_0_search);
        good_collection_status[1] = findViewById(R.id.collection_status_1_search);
        good_collection_status[2] = findViewById(R.id.collection_status_2_search);
        collection_status_2_bac = findViewById(R.id.collection_status_2_bac_search);
//        flVouchers = (FocusFrameLayout) this.findViewById(R.id.fl_vouchers);
        flTakeoutShopHome = (FocusFrameLayout) this.findViewById(R.id.fl_takeout_shophome);
        flDetail = (FocusFrameLayout) this.findViewById(R.id.fl_detail_search);
        flOrders = (FocusFrameLayout) this.findViewById(R.id.fl_orders_search);
        flUsers = (FocusFrameLayout) this.findViewById(R.id.fl_users_search);
        flFirst = (FocusFrameLayout) this.findViewById(R.id.fl_first_search);
        tvShopHomePop = (TextView) this.findViewById(R.id.tv_takeout_shophome_pop_search);
        tvDetailPop = (TextView) this.findViewById(R.id.tv_detail_pop_search);
        tvOrdersPop = (TextView) this.findViewById(R.id.tv_orders_pop_search);
        tvUsersPop = (TextView) this.findViewById(R.id.tv_users_pop_search);
        tvFirstPop = (TextView) this.findViewById(R.id.tv_first_pop_search);
        ivShopHomeFocus = (ImageView) this.findViewById(R.id.iv_takeout_shophome_focus_search);
        ivDetailFocus = (ImageView) this.findViewById(R.id.iv_detail_focus_search);
        ivOrdersFocus = (ImageView) this.findViewById(R.id.iv_orders_focus_search);
        ivUserFocus = (ImageView) this.findViewById(R.id.iv_users_focus_search);
        ivFirstFocus = (ImageView) this.findViewById(R.id.iv_first_focus_search);
        collection_focus = (FocusFrameLayout) findViewById(R.id.collection_focus_search);
        good_collection_recycle = (RecyclerView) findViewById(R.id.good_collection_recycle_search);
        good_collection_empty = (ImageView) findViewById(R.id.good_collection_empty_search);
        shop_loading_notice = (TextView) findViewById(R.id.shop_loading_notice_search);
        shop_empty = findViewById(R.id.shop_empty_search);
        shop_search_empty = findViewById(R.id.shop_empty_search);
        tvSearchWord = (TextView) findViewById(R.id.tv_search_word);
        card_view = (CardView) findViewById(R.id.card_view);
        // 处理商品列表 and 提示商品没有更多了
        good_all = (SelectFocusListView) findViewById(R.id.good_all_search);
//        good_all.setOnPageOverScrollLisntener(pageOverScrollListener);
        good_all.setFlipScrollFrameCount(2);
        ViewGroup group = (ViewGroup) shop_loading.getParent();
        group.removeView(shop_loading);
        shop_loading.getViewTreeObserver().addOnGlobalLayoutListener(onFootViewLayoutListener);
        group = (ViewGroup) shop_title.getParent();
        group.removeView(shop_title);
        good_all.setDeepMode(true);
        shop_loading.setLayoutParams(new FocusListView.LayoutParams(-1, -2));
        good_all.addFooterView(shop_loading);
        shop_title.setLayoutParams(new FocusListView.LayoutParams(-1, -2));
        good_all.addHeaderView(shop_title);
        shop_loading.getViewTreeObserver().addOnGlobalLayoutListener(onFootViewLayoutListener);

        // 购物车
        collection_focus.setOnFocusActionLister(focusFrameLayout);
        good_collection_recycle.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        good_collectionMultipleRecycleAdapter = MultipleRecycleAdapter.getByViewHolder(TakeOutShopSearchActivity.this, ViewHolderCollection.class);
        good_collection_recycle.setLayoutManager(new LinearLayoutManager(TakeOutShopSearchActivity.this, LinearLayoutManager.VERTICAL, true));
        good_collection_recycle.setAdapter(good_collectionMultipleRecycleAdapter);
        good_collection_pay_ori.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        if (selectGoods == null) {
            selectGoods = MultipleListAdapter.getByViewHolder(TakeOutShopSearchActivity.this, ViewHolderSearch.class, ViewHolderSearchMore.class);
        }

        selectGoods.setOnLoadMoreListener(new AdapterDelegate.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                page += 1;
                getSearchResult(page, keyword);
            }
        });
        if (!TextUtils.isEmpty(keyword)) {
            tvSearchWord.setText("店铺内搜索词：" + keyword);
        }
        card_view.setRadio(getResources().getDimensionPixelOffset(R.dimen.dp_10));
        if (mShopSearchData != null) {
            loadPageData(mShopSearchData);
            if (shopDetailData != null && shopDetailData.getStoreDetailDTO() != null && !TextUtils.isEmpty(shopDetailData.getStoreDetailDTO().getName())) {
                shopName.setText(shopDetailData.getStoreDetailDTO().getName());
            }
            if (shopDetailData != null && shopDetailData.getStoreDetailDTO() != null && !TextUtils.isEmpty(shopDetailData.getStoreDetailDTO().getShopLogo())) {
                ImageLoaderManager.getImageLoaderManager(TakeOutShopSearchActivity.this).displayImage(shopDetailData.getStoreDetailDTO().getShopLogo(), shopIcon, ClassicOptions.dio565);
            } else {
                shopIcon.setImageResource(R.drawable.icon_shop_no_logo);
            }
        } else {
            if (!TextUtils.isEmpty(asr)) {
                requestAsr();
            } else {
                if (!TextUtils.isEmpty(keyword)) {
                    getSearchResult(1, keyword);
                } else {
                    showErrorDialog("缺少搜索词keyword", true);
                }
            }

            if (!TextUtils.isEmpty(shopNameStr)) {
                shopName.setText(shopNameStr);
            }
            if (!TextUtils.isEmpty(shopLogoStr)) {
                ImageLoaderManager.getImageLoaderManager(TakeOutShopSearchActivity.this).displayImage(shopLogoStr, shopIcon,ClassicOptions.dio565);
            } else {
                shopIcon.setImageResource(R.drawable.icon_shop_no_logo);
            }
        }
        // js 桥
        //TODO 外卖购物车下单之后，返回刷新。需要测试。
//        takeOutPayBackPlugin.onCreate(savedInstanceState);

        layout.requestFocus();
        initToolsView();


    }

    /**
     * 初始化进入店铺页时的经纬度信息。
     * 优先获取intent传入的值，其次获取用户本地保存的地址，最后获取语音传入的地址
     */
    private void initLocation() {
        String loc = getIntent().getStringExtra("location");
        if (TextUtils.isEmpty(loc)) {
            loc = SharePreferences.getString("location");
        }

        if (TextUtils.isEmpty(loc)) {
            loc = SDKInitConfig.getLocation();
        }

        if (!TextUtils.isEmpty(loc)) {
            location = JSON.parseObject(loc, Location.class);
            latitude = location.x;
            longitude = location.y;
        }

        if (TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude)) {
            showErrorDialog("获取地址失败", true);
        }
    }

    private void requestAsr() {
        ServiceResponse<DomainResultVo> response = normalLoad(new NlpNewRequest(asr, null), false, 0);
        if (response != null) {
            DomainResultVo domainResultVo = response.getData();
            TTSUtils.getInstance().setDomainResult(domainResultVo);
            if (domainResultVo != null && domainResultVo.getResultVO() != null && domainResultVo.getResultVO().getKeywords() != null) {
                tvSearchWord.setText("店铺内搜索词：" + domainResultVo.getResultVO().getKeywords());
                getSearchResult(1, domainResultVo.getResultVO().getKeywords());
                showDialog = false;
            } else if (domainResultVo != null && domainResultVo.getResultVO() != null && domainResultVo.getResultVO().getSecondCategoryName() != null) {
                tvSearchWord.setText("店铺内搜索词：" + domainResultVo.getResultVO().getSecondCategoryName());
                getSearchResult(1, domainResultVo.getResultVO().getSecondCategoryName());
                showDialog = false;
            } else {
                if (showDialog) {
                    TTSUtils.getInstance().showDialog(this, 0);
                }
            }
        }
    }

    private void getSearchResult(int page, String keyword) {
        businessRequest.requestSearchResult(shopId, keyword, orderType, pageSize, page,
                new GetShopSearchResultlListener(new WeakReference<BaseActivity>(TakeOutShopSearchActivity.this), 1, null));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNotify.setHandlerSearch(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppDebug.i("searchASRNotify", "setHandler(null)");
        mNotify.setHandlerSearch(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onRemoveKeepedActivity(TakeOutShopSearchActivity.class.getName());
        SkuSelectDialog.getSkuDialogInstance(this, getFullPageName()).destroySkuDialog();
//        takeOutPayBackPlugin.onDestroy();
        PromptPop.destory();
        WeakDataHolder.getInstance().clearData("shopDetailData");
        WeakDataHolder.getInstance().clearData("shopSearchData");
    }

    private <T> ServiceResponse<T> normalLoad(BaseMtopRequest request, boolean post, int timeout) {
        ServiceResponse<T> serviceResponse = null;
        request.setParamsData();
        AppDebug.i(TAG, TAG + ".normalLoad.request = " + request + ", post = " + post);
        MtopBuilder builder = AppInitializer.getMtopInstance().build(request, request.getTTid());
        builder.setSocketTimeoutMilliSecond(timeout);
        if (post) {// post方法
            builder.reqMethod(MethodEnum.POST);
        }
        builder.useWua();//临时添加
        MtopResponse mtopResponse = builder.syncRequest();
        if (Config.isDebug()) {
            AppDebug.i(TAG, TAG + ".normalLoad, mtopResponse = " + mtopResponse);
        }

        if (mtopResponse != null) {
            try {
                if (mtopResponse.getBytedata() != null) {
                    serviceResponse = request.resolveResponse(new String(mtopResponse.getBytedata()));
                } else {
                    String retCode = mtopResponse.getRetCode();
                    AppDebug.i(TAG, TAG + ".normalLoad.retCode = " + retCode);
                    if (!TextUtils.isEmpty(retCode)) {
                        ServiceCode serviceCode = ServiceCode.valueOf(retCode);
                        if (serviceCode != null) {
                            serviceResponse = new ServiceResponse<T>();
                            serviceResponse.update(serviceCode.getCode(), retCode, serviceCode.getMsg());
                        }
                    }
                }
            } catch (Exception e1) {
                serviceResponse = new ServiceResponse<T>();
                serviceResponse.update(ServiceCode.DATA_PARSE_ERROR);
                e1.printStackTrace();
            }
        } else {
            serviceResponse = new ServiceResponse<T>();
            serviceResponse.update(ServiceCode.HTTP_ERROR);
        }

        // 如果serviceResponse仍然为null
        if (serviceResponse == null) {
            AppDebug.i(TAG, TAG + ".normalLoad.custom serviceResponse");
            serviceResponse = new ServiceResponse<T>();
            serviceResponse.update(ServiceCode.API_ERROR);
        }
        AppDebug.i(TAG, TAG + ".normalLoad, serviceResponse = " + serviceResponse);

        return serviceResponse;
    }

    @Override
    public boolean onSearch(String asr, String txt, String spoken, List<String> tips, DomainResultVo.OtherCase otherCase) {
        if (!TextUtils.isEmpty(asr)) {
            ServiceResponse<ShopSearchResultBean> response = BusinessRequest.getBusinessRequest().normalLoad(new ShopSearchRequest(shopId, asr, orderType, pageSize, pageNo), false, 0);
            ShopSearchResultBean data = response.getData();
            if (data == null || data.getItemInfoList() == null) {
                return false;
            } else {
                showDialog = true;
                if (showDialog) {
                    TTSUtils.getInstance().showDialog(TakeOutShopSearchActivity.this, 1);
                }
                loadPageData(data);
                tvSearchWord.setText("店铺内搜索词：" + asr);
                return true;
            }
        }
        return false;
    }

    public void goToShopHome() {
        if (fromHome) {

        } else {
            String url = "tvtaobao://home?app=takeout&module=takeouthome&serviceId=62&shopId=" + shopId + "&scm=" + scm;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
        Utils.utControlHit(getFullPageName(), "Page_waimai_shop_sidebar_button_my", properties);
        finish();
    }

//    public boolean onASRNotify(String object, String tts, List<String> tips) {
//        AppDebug.e(TAG, "asr = " + object);
////        tipsDialog.dismiss();
//
//        tvSearchWord.setText("店铺内搜索词：" + object);
//        ttsText = tts;
//        tipsList = (ArrayList<String>) tips;
//        keyword = object;
//        return true;
//    }

    /**
     * 店铺信息回调
     * 店铺数据需要分页
     */
    public static class GetShopSearchResultlListener extends BizRequestListener<ShopSearchResultBean> {
        int mIndex;
        ShopSearchResultBean mShopSearchData;

        public GetShopSearchResultlListener(WeakReference<BaseActivity> baseActivityRef, int index, ShopSearchResultBean preData) {
            super(baseActivityRef);
            this.mIndex = index;
            this.mShopSearchData = preData;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            AppDebug.e(TAG, "resultCode = " + resultCode + ",msg = " + msg);
            return false;
        }

        @Override
        public void onSuccess(ShopSearchResultBean data) {
            TakeOutShopSearchActivity activity = (TakeOutShopSearchActivity) mBaseActivityRef.get();

            if (mShopSearchData == null) {
                mShopSearchData = data;
            }

            if (data != null) {
                if (data.getItemInfoList() == null) {
                    activity.loadPageData(null);
                    shop_search_empty.setVisibility(View.VISIBLE);
                    shop_loading_notice.setVisibility(View.GONE);
                    if (activity.showDialog) {
                        TTSUtils.getInstance().showDialog(activity, 0);
                    }
                } else {
                    if (activity.showDialog) {
                        TTSUtils.getInstance().showDialog(activity, 1);
                    }
                    activity.loadPageData(mShopSearchData);
                }
            } else {
                activity.loadPageData(null);
                shop_search_empty.setVisibility(View.VISIBLE);
                shop_loading_notice.setVisibility(View.GONE);
            }

        }


        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    final static Map<String, String> properties = Utils.getProperties();

    /**
     * 商品列表 处理底部加载更多VIew 高度自适应
     */
    private ViewTreeObserver.OnGlobalLayoutListener onFootViewLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        View contentView;
        ViewGroup.LayoutParams layoutParams;

        @Override
        public void onGlobalLayout() {
            int half;
            lab:
            {
                ListAdapter adapter = good_all.getAdapter();
                if (adapter == null) {
                    half = (good_all.getHeight() - shop_title.getHeight());
                    break lab;
                }

                int itemHei = 0;
                for (int i = 0; i < good_all.getChildCount(); i++) {
                    View current = good_all.getChildAt(i);
                    if (current != shop_title && current != shop_loading) {
                        itemHei = current.getHeight();
                        break;
                    }
                }
                int itemCount = adapter.getCount() - 2;
                itemCount = itemCount > 0 ? itemCount : 0;
                int needHei = itemHei * itemCount + shop_title.getHeight();
                half = (good_all.getHeight() - itemHei) / 2;
                half = half > (good_all.getHeight() - needHei) ? half : good_all.getHeight() - needHei;
            }

            if (contentView == null) {
                contentView = (View) shop_loading_notice.getParent();
                layoutParams = contentView.getLayoutParams();
            }

            if (layoutParams.height != half) {
                layoutParams.height = half;
                contentView.setLayoutParams(layoutParams);
            }
        }
    };

    /**
     * 侧边栏跳转
     */
    private void initToolsView() {
//        properties = Utils.getProperties();
        properties.put("shop id", shopId);

        properties.put("uuid", CloudUUIDWrapper.getCloudUUID());

        //店铺首页按钮
        flTakeoutShopHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDebug.e(TAG, "flTakeoutShopHome.onClick");
                if (fromHome) {

                } else {
                    String url = "tvtaobao://home?app=takeout&module=takeouthome&serviceId=62&shopId=" + shopId + "&scm=" + scm;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
                Utils.utControlHit(getFullPageName(), "Page_waimai_shop_sidebar_button_my", properties);
                finish();

            }
        });
        flTakeoutShopHome.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ivShopHomeFocus.setBackgroundResource(R.drawable.icon_takeout_shophome);
                    tvShopHomePop.setVisibility(View.VISIBLE);
                } else {
                    ivShopHomeFocus.setBackgroundResource(0);
                    tvShopHomePop.setVisibility(View.INVISIBLE);
                }

            }
        });
        //详情按钮
        flDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullPageName = getFullPageName();
                AppDebug.e(TAG, "flDetail.onClick");
                if (shopDetailData == null) {
                    initShopHomeDetail();
                    return;
                } else {
                    Intent intent = new Intent(TakeOutShopSearchActivity.this, TakeOutShopDetailActivity.class);
                    intent.putExtra("storeDetailDTOBean", shopDetailData.getStoreDetailDTO());
                    intent.putExtra("pageName", getFullPageName());
                    Utils.utControlHit(getFullPageName(), "Page_waimai_shop_sidebar_button_details", properties);
                    startActivity(intent);
                }
            }
        });
        flDetail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ivDetailFocus.setBackgroundResource(R.drawable.icon_takeout_detail);
                    tvDetailPop.setVisibility(View.VISIBLE);
                } else {
                    ivDetailFocus.setBackgroundResource(0);
                    tvDetailPop.setVisibility(View.INVISIBLE);
                }

            }
        });

        //订单按钮
        flOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDebug.e(TAG, "flOrders.onClick");
                String url = "tvtaobao://home?app=takeout&module=takeOutOrderList";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                Utils.utControlHit(getFullPageName(), "Page_waimai_shop_sidebar_button_order", properties);
                startActivity(intent);
            }
        });

        flOrders.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ivOrdersFocus.setBackgroundResource(R.drawable.icon_takeout_orders);
                    tvOrdersPop.setVisibility(View.VISIBLE);
                } else {
                    ivOrdersFocus.setBackgroundResource(0);
                    tvOrdersPop.setVisibility(View.INVISIBLE);

                }
            }
        });

        //我的按钮
        flUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDebug.e(TAG, "flUsers.onClick");
                String url = "tvtaobao://home?module=common&page=http://tvos.taobao.com/wow/yunos/act/tvtaobao-waimai-user";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                Utils.utControlHit(getFullPageName(), "Page_waimai_shop_sidebar_button_my", properties);
                startActivity(intent);
            }
        });
        flUsers.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ivUserFocus.setBackgroundResource(R.drawable.icon_takeout_user);
                    tvUsersPop.setVisibility(View.VISIBLE);
                } else {
                    ivUserFocus.setBackgroundResource(0);
                    tvUsersPop.setVisibility(View.INVISIBLE);
                }
            }
        });

        //首页按钮
        flFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDebug.e(TAG, "flFirst.onClick");
                String url = "tvtaobao://home?module=common&page=http://tvos.taobao.com/wow/yunos/act/tvtaobao-waimai";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                Utils.utControlHit(getFullPageName(), "Page_waimai_shop_sidebar_button_waimaihome", properties);
                startActivity(intent);

            }
        });

        flFirst.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ivFirstFocus.setBackgroundResource(R.drawable.icon_takeout_first);
                    tvFirstPop.setVisibility(View.VISIBLE);
                } else {
                    ivFirstFocus.setBackgroundResource(0);
                    tvFirstPop.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    private void initShopHomeDetail() {
        businessRequest.requestShopHomeDetail(shopId, serviceId, longitude,
                latitude, "[\"1\"]", "1", null,
                new GetShopHomeDetailListener(new WeakReference<BaseActivity>(TakeOutShopSearchActivity.this), 1, null));
    }

    /**
     * 店铺信息回调
     * 店铺数据需要分页
     */
    public static class GetShopHomeDetailListener extends BizRequestListener<ShopDetailData> {
        int mIndex;
        ShopDetailData mShopDetailData;

        public GetShopHomeDetailListener(WeakReference<BaseActivity> baseActivityRef, int index, ShopDetailData preData) {
            super(baseActivityRef);
            this.mIndex = index;
            this.mShopDetailData = preData;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            AppDebug.e(TAG, "resultCode = " + resultCode + ",msg = " + msg);
            return false;
        }

        @Override
        public void onSuccess(ShopDetailData data) {
            TakeOutShopSearchActivity activity = (TakeOutShopSearchActivity) mBaseActivityRef.get();
            if (mShopDetailData == null) {
                mShopDetailData = data;
            } else {
                mShopDetailData.marge(data);
            }
            if (mShopDetailData != null && mShopDetailData.getStoreDetailDTO() != null) {
                Intent intent = new Intent(activity, TakeOutShopDetailActivity.class);
                intent.putExtra("storeDetailDTOBean", mShopDetailData.getStoreDetailDTO());
                intent.putExtra("pageName", fullPageName);
                Utils.utControlHit(fullPageName, "Page_waimai_shop_sidebar_button_details", properties);
                activity.startActivity(intent);
            }
        }


        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    /**
     * 拿到购物车列表后 调整种类列表显示信息  和  购物车显示信息
     *
     * @param good
     * @param bagListBackToTop 是否调整购物车显示到第一条
     */
    private void adjustCollectionsAndKinds(TakeOutBag good, boolean bagListBackToTop) {
        if (good == null) return;
        mTakeOutBag = good;

        // todo 保留原列表
        mTakeOutBag.__cartItemList = new ArrayList<>();
        if (mTakeOutBag.cartItemList != null) {
            mTakeOutBag.__cartItemList.addAll(mTakeOutBag.cartItemList);
        }

        // todo 用于UI展示 商品不能购买了
        Iterator<TakeOutBag.CartItemListBean> itemListBeanIterator = good.cartItemList != null ? good.cartItemList.iterator() : null;
        while (itemListBeanIterator != null && itemListBeanIterator.hasNext()) {
            TakeOutBag.CartItemListBean next = itemListBeanIterator.next();
            if (next.quantity <= 0) {
                itemListBeanIterator.remove();
            }
        }

        // todo 用于UI展示 增加快餐盒项
        if (good.packingFee > 0 && good.cartItemList != null) {
            TakeOutBag.CartItemListBean bean = new TakeOutBag.CartItemListBean();
            bean.title = "快餐盒";
            bean.amount = 0;
            bean.totalPromotionPrice = good.packingFee;

            Iterator<TakeOutBag.CartItemListBean> iterator = good.cartItemList == null ? null : good.cartItemList.iterator();
            while (iterator != null && iterator.hasNext()) {
                TakeOutBag.CartItemListBean next = iterator.next();
                if (next != null && next.packingFee > 0) {
                    bean.amount++;
                }
            }
            bean.amount = bean.amount > 0 ? bean.amount : 1;
            good.cartItemList.add(0, bean);
        }

        // todo 种类列表 商品列表  更新商品数量 种类中选中商品数量
//        List<ShopDetailData.ItemGenreWithItemsListBean> dataList = good_kind_adapter.getDataList();
//        Collections.reverse(dataList);
        LinkedList<TakeOutBag.CartItemListBean> linkedList = good.cartItemList == null ? null : new LinkedList<TakeOutBag.CartItemListBean>(good.cartItemList);
//        for (ShopDetailData.ItemGenreWithItemsListBean itemGenreWithItemsListBean : dataList) {
//            itemGenreWithItemsListBean.totleGoodCount = 0;
        if (shopSearchResultBean.getItemInfoList() == null) {

        }
        for (ItemListBean itemListBean : shopSearchResultBean.getItemInfoList()) {
            itemListBean.__intentCount = 0;
            String itemId = itemListBean.getItemId();
            if (itemId == null || linkedList == null) {
                continue;
            }
            for (TakeOutBag.CartItemListBean bean : linkedList) {
                if (bean != null && itemId.equals(bean.itemId)) {
                    itemListBean.__intentCount += bean.amount;
                    if (!bean.__isCompare) {
//                            itemGenreWithItemsListBean.totleGoodCount += bean.amount;
                        bean.__isCompare = true;
                    }
                }

            }
        }
//        }
//        good_kind_adapter.notifyDataSetChanged();
        if (selectGoods != null) {
            selectGoods.notifyDataSetChanged();
        }


        // todo 购物车 列表数据 价格数据 邮费数据
        good_collectionMultipleRecycleAdapter.clearData();
        good_collectionMultipleRecycleAdapter.loadData(good.cartItemList);
        if (bagListBackToTop) {
            good_collection_recycle.smoothScrollToPosition(0);
        }

        collection_pay.setText(format.format(good.totalPromotionPrice / 100f));
        good_collection_pay_ori.setText(format.format(good.totalPrice / 100f));

        if (collection_focus.isFocused()) {
            collection_pay.setTextColor(getResources().getColor(R.color.white));
            good_collection_pay_ori.setTextColor(0xffffc7a5);
            collection_pay_dlive_amount.setTextColor(0xffffe8da);
        } else {
            collection_pay.setTextColor(getResources().getColor(R.color.red_ff6000));
            good_collection_pay_ori.setTextColor(0xff546274);
            collection_pay_dlive_amount.setTextColor(0xff8396ae);
        }

        if (good.totalPromotionPrice < good.totalPrice) {
            good_collection_pay_ori.setVisibility(View.VISIBLE);
        } else {
            good_collection_pay_ori.setVisibility(View.GONE);
        }
        if (good.agentFee <= 0) {
            collection_pay_dlive_amount.setText("免配送费");
        } else {
            collection_pay_dlive_amount.setText("另需配送费" + format.format(good.agentFee / 100f));
        }
        good_collection_need_more.setVisibility(View.GONE);

        // todo 购物车不为空 or 购物车为空
        if (good.cartItemList != null && good.cartItemList.size() > 0) {
            good_collection_empty.setVisibility(View.GONE);
            good_collection_status[1].setVisibility(View.GONE);
            good_collection_status[2].setVisibility(View.VISIBLE);
            if (good.canBuy) {
                if (collection_focus.isFocused()) {
                    collection_status_2_bac.setVisibility(View.VISIBLE);
                } else {
                    collection_status_2_bac.setVisibility(View.GONE);
                }
            } else {
                collection_status_2_bac.setVisibility(View.GONE);
                if (good.buttonText != null && good.buttonText.length() > 0) {
                    good_collection_need_more.setVisibility(View.VISIBLE);
                    good_collection_need_more.setText(good.buttonText);
                }
            }
        } else {
            good_collection_empty.setVisibility(View.VISIBLE);
            good_collection_status[1].setVisibility(View.VISIBLE);
            good_collection_status[2].setVisibility(View.GONE);
            ((TextView) good_collection_status[1]).setText(format.format(good.deliverAmount / 100f) + "起送");
        }


    }

    /**
     * 增删改商品 清空购物车 再来一单 刷新本地购物车
     *
     * @param addGood 是否是新增item
     */
    public void notifyCollectionChange(boolean addGood) {
        BusinessRequest.getBusinessRequest().getTakeOutBag(shopId,
                longitude, latitude,
                "", new GetShopHomeBagListener(new WeakReference<BaseActivity>(this), addGood, false));
    }

    /**
     * 加载店铺数据
     *
     * @param shopDetailData
     */
    private void loadPageData(ShopSearchResultBean shopDetailData) {
        this.shopSearchResultBean = shopDetailData;
        List<ItemListBean> goodList = new ArrayList<>();
        List<Object> objectList = new ArrayList<>();
        if (shopDetailData != null) {
            goodList = shopDetailData.getItemInfoList();
            if (!TextUtils.isEmpty(shopDetailData.getHaveNext()) && shopDetailData.getHaveNext().equals("false")) {
                shop_loading_notice.setVisibility(View.VISIBLE);
            }
            objectList.addAll(goodList);
            objectList.add(new Object());
            shop_search_empty.setVisibility(View.GONE);
        }
        if (selectGoods == null) {
            selectGoods = MultipleListAdapter.getByViewHolder(TakeOutShopSearchActivity.this, ViewHolderSearch.class, ViewHolderSearchMore.class);
        }

        selectGoods.loadData(objectList);

        good_all.setAdapter(selectGoods);

        // todo 刷新购物车
        if (CoreApplication.getLoginHelper(this).isLogin()) {
            notifyCollectionChange(false);
        }
    }

    /**
     * 删除购物车商品
     *
     * @param data
     */
    public void updateBag(ItemListBean data) {
        //todo 先去购物车里面拿到 对等的item  有Bug的
        List<TakeOutBag.CartItemListBean> dataList = good_collectionMultipleRecycleAdapter.getDataList();
        TakeOutBag.CartItemListBean select = null;
        for (TakeOutBag.CartItemListBean itemListBean : dataList) {
            if (itemListBean.itemId != null && itemListBean.itemId.equals(data.getItemId())) {
                select = itemListBean;
                break;
            }
        }
        if (select == null) return;

        String json = null;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("itemId", select.itemId);
            jsonObject.put("skuId", select.skuId);
            jsonObject.put("quantity", select.amount - 1);
            jsonObject.put("cartId", select.cartId);
            JSONArray array = new JSONArray();
            array.put(jsonObject);
            json = array.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        BusinessRequest.getBusinessRequest().requestTakeOutUpdate(data.getStoreId(),
                latitude, longitude, select.amount == 1 ? "2" : "1",
                json, new GetShopHomeBagListener(new WeakReference<BaseActivity>(this), false, false));
    }


    /**
     * 添加商品进购物车
     *
     * @param data
     */
    public void addGoodToBag(ItemListBean data) {
        if (data == null || shopSearchResultBean == null) {
            return;
        }

        String skuId = data.getSkuList() != null && data.getSkuList().size() > 0 ? data.getSkuList().get(0).getSkuId() : null;

        BusinessRequest.getBusinessRequest().requestTakeOutAddBag(data.getItemId(), skuId, "1", "{\"lifeShopId\":\"" +
                        shopId + "\",\"skuProperty\":\"\"}", "taolife_client",
                new AddBagListener(new WeakReference<BaseActivity>(this)));
    }


    /**
     * 获取购物车回调  and  修改购物车回调
     */
    private class GetShopHomeBagListener extends BizRequestListener<TakeOutBag> {

        public GetShopHomeBagListener(WeakReference<BaseActivity> baseActivityRef, boolean frushListBackToTop, boolean retry) {
            super(baseActivityRef);
            backToTop = frushListBackToTop;
            this.mRetry = retry;
        }

        private boolean backToTop;
        private boolean mRetry;

        @Override
        public boolean onError(int resultCode, String msg) {
            TakeOutShopSearchActivity activity = (TakeOutShopSearchActivity) mBaseActivityRef.get();
            if (activity != null && mRetry) {
                activity.notifyCollectionChange(false);
            }
            return false;
        }

        @Override
        public void onSuccess(TakeOutBag data) {
            TakeOutShopSearchActivity activity = (TakeOutShopSearchActivity) mBaseActivityRef.get();
            if (activity != null && data != null) {
                activity.adjustCollectionsAndKinds(data, backToTop);
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    /**
     * 添加商品回调
     */
    private static class AddBagListener extends BizRequestListener<AddBagBo> {
        public AddBagListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(AddBagBo data) {
            TakeOutShopSearchActivity activity = (TakeOutShopSearchActivity) mBaseActivityRef.get();
            if (activity != null) {
                activity.notifyCollectionChange(true);
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }

    }


    @Override
    public String getPageName() {
        return "Voice_FoodSearch_shop";
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> properties = Utils.getProperties();

        properties.put("uuid", CloudUUIDWrapper.getCloudUUID());

        properties.put("from", TextUtils.isEmpty(from) ? "android_native_take_out_shop_main" : from);
        properties.put("appkey", Config.getAppKey());
        if (User.isLogined()) {
            properties.put("is_login", "1");
        } else {
            properties.put("is_login", "0");
        }
        properties.put("shop id", shopId);
        return properties;
    }

    public static class Location {
        /**
         * address : 乐佳国际 2号楼
         * area : 余杭区
         * areaCode : 330110
         * available : true
         * city : 杭州市
         * cityCode : 330100
         * fullAddress : true
         * id : 7319083501
         * mobile : 15179238435
         * name : 王林
         * prov : 浙江省
         * status : 0
         * street : 五常街道
         * streetCode : 330110005
         * x : 30.279712
         * y : 120.008787
         * userName : 街角小林
         */

        public String address;
        public String area;
        public String areaCode;
        public String available;
        public String city;
        public String cityCode;
        public String fullAddress;
        public String id;
        public String mobile;
        public String name;
        public String prov;
        public String status;
        public String street;
        public String streetCode;
        public String x;
        public String y;
        public String userName;
    }


}



