package com.yunos.tvtaobao.goodlist.activity;


import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.taobao.statistic.CT;
import com.taobao.statistic.TBS;
import com.yunos.tv.app.widget.AdapterView;
import com.yunos.tv.app.widget.focus.FocusFlipGridView;
import com.yunos.tv.app.widget.focus.FocusListView;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.core.CoreApplication;

import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tvtaobao.biz.activity.CoreActivity;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.common.DeviceJudge;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.util.ActivityPathRecorder;
import com.yunos.tv.core.util.ColorHandleUtil;
import com.yunos.tv.core.util.IntentDataUtil;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.activity.CoreActivity;
import com.yunos.tvtaobao.biz.activity.SdkHaveTabBaseActivity;
import com.yunos.tvtaobao.biz.adapter.TabGoodsBaseAdapter;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.listener.NetworkOkDoListener;
import com.yunos.tvtaobao.biz.listener.TabFocusFlipGridViewListener;
import com.yunos.tvtaobao.biz.listener.TabFocusListViewListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.Cat;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.request.bo.GoodsList;
import com.yunos.tvtaobao.biz.request.bo.GoodsListItem;
import com.yunos.tvtaobao.biz.request.bo.GoodsTmail;
import com.yunos.tvtaobao.biz.request.bo.SearchedGoods;
import com.yunos.tvtaobao.biz.request.bo.SellerInfo;
import com.yunos.tvtaobao.biz.request.bo.ShopCoupon;
import com.yunos.tvtaobao.biz.request.bo.shopDSRScore;
import com.yunos.tvtaobao.biz.request.info.GlobalConfigInfo;
import com.yunos.tvtaobao.biz.request.ztc.ZTCUtils;
import com.yunos.tvtaobao.biz.util.StringUtil;
import com.yunos.tvtaobao.biz.widget.GoodListLifeUiGridView;
import com.yunos.tvtaobao.biz.widget.TabFocusPositionManager;
import com.yunos.tvtaobao.goodlist.GoodListFocuseUnit;
import com.yunos.tvtaobao.goodlist.GoodListFocuseUnit.MenuView_Info;
import com.yunos.tvtaobao.goodlist.GoodListFocuseUnit.VISUALCONFIG;
import com.yunos.tvtaobao.goodlist.GoodListImageHandle;
import com.yunos.tvtaobao.goodlist.R;
import com.yunos.tvtaobao.goodlist.adapter.GoodListGirdViewAdapter;
import com.yunos.tvtaobao.goodlist.adapter.ShopClassifyAdapter;
import com.yunos.tvtaobao.goodlist.adapter.ShopCouponGridViewAdapter;
import com.yunos.tvtaobao.goodlist.view.ItemLayoutForShop;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ShopActivity extends SdkHaveTabBaseActivity {
    //判断购物车是否有活动
    protected boolean hasActive = false;

    private final static int WHAT_SHOP_CHANGE_CLASSIFY = 1001;

    private final static String TAG = "Shop";

    // 限制的列数
    //private final int COLUMNS_COUNT = 4;

    // 限制的分类数
    private final int CLASSFY_LIMIT = 10;

    // 卖家id
    private String mSellerId;

    // 网络请求
    private BusinessRequest mBusinessRequest;
    // 分类集合mBusinessRequest
    private ArrayList<Cat> mCats;

    // 存放View  
    private HashMap<String, MenuView_Info> mGoodsMenuViewInfoMap = null;

    //
    private Map<String, ArrayList<SearchedGoods>> mGoodsArrayList = null;

    // 店铺类型 淘宝 or 天猫
    private String mShopType = BaseConfig.SELLER_TAOBAO;

    // 描述相符度，与同行相比
    private TextView mDescription, mDescriptionScore;
    // 服务态度度，与同行相比
    private TextView mAttitude, mAttitudeScore;
    // 发货速度度，与同行相比
    private TextView mDeliverySpeed, mDeliverySpeedScore;
    // 店铺名字，所在地
    private TextView mShopName, mShopLocation;

    // 图片处理
    private GoodListImageHandle mGoodListImageHandle;

    // shopid
    private String mShopId;

    private Handler mHandler = null;

    // 是否在发送网络请求 ，grideView滚动 需要
    private boolean mIsRequest;

    // 左下角的LOGO
    private ImageView mShopLogoImageView;

    // 展示店铺 信息 的 layout容器
    private RelativeLayout mShopInfo;

    // 商品分数 滚动
    private ViewFlipper mFlipper;

    // 阴影
    private int mTextShadowPix = 1;

    // 分类文字 阴影
    private int mTextShadowColor = ColorHandleUtil.ColorTransparency(Color.BLACK, 25);

    // 分类适配器
    private ShopClassifyAdapter mShopClassifyAdapter = null;

    private GoodListFocuseUnit mGoodListFocuseUnit = new GoodListFocuseUnit();

    // 针对背景状态
    private Drawable mCommonBackGround = null;
    private Drawable mShopBackGround = null;
    private boolean mCommonShow = false;

    // 菜单只有一个全部时，使用
    private boolean mSingleAllFirst;

    private boolean mIsStartFlipper;
    private boolean mFlipperShow;

    // 重连的监听
    private ShopNetworkOkDoListener mShopNetworkOkDoListener;

    private int mTabFocusResId;
    private SellerInfo mSellerInfo;
    //进入来源,为空表示店铺按钮,如果不是从店铺按钮进来,默认光标在店铺优惠券上,未实现
    private String shopFrom = "";

    private List<ShopCoupon> mShopCouponList = new ArrayList<ShopCoupon>();
    private String couponTabKeyName = "优惠券";
    private String couponTabKey = "coupon";
    private ShopCouponGridViewAdapter mShopCouponGirdViewAdapter;

    private ArrayList<Integer> mTableListId = new ArrayList();

    private boolean mIsBackHome;

//    AdvtertiseReceiver advertiseReceiver = new AdvtertiseReceiver();

    Map<String, String> lProperties = Utils.getProperties();

//    private AppHolder appHolder;

    private GlobalConfig globalConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        onKeepActivityOnlyOne(ShopActivity.class.getName());

        Intent intent = this.getIntent();
        if (intent == null) {
            return;
        }

        globalConfig = GlobalConfigInfo.getInstance().getGlobalConfig();

        mIsBackHome = isBackHome();

        mShopType = intent.getStringExtra(BaseConfig.SELLER_TYPE);
        mSellerId = intent.getStringExtra(BaseConfig.SELLER_NUMID);
        mShopId = intent.getStringExtra(BaseConfig.SELLER_SHOPID);
        shopFrom = intent.getStringExtra(BaseConfig.SHOP_FROM);

        AppDebug.i(TAG, "onCreate --> shopType = " + mShopType + "; sellerId = " + mSellerId + ";  shopId = " + mShopId
                + ";shopFrom = " + shopFrom);

        // 初始化界面中的变量值
        onInitVariableValue();

        isHasActivityFromNet();

        onSetTabBaseActivityListener();

        // 初始化其他参数，并首次请求数据
        onInitParamAndRequest();

        onSetPierceActivityListener();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "shop");
            jsonObject.put("shopId", mShopId);
            jsonObject.put("commodityId", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 是否返回首页
     *
     * @return
     */
    protected boolean isBackHome() {
        boolean isbackhome = false;
        String isBackHomeValue = IntentDataUtil.getString(getIntent(), "isbackhome", null);
        String isJoin = IntentDataUtil.getString(getIntent(), CoreIntentKey.URI_JOIN, null);

        AppDebug.i(TAG, "isBackHome isBackHomeValue  = " + isBackHomeValue + " ,isJoin= " + isJoin);
        if (!TextUtils.isEmpty(isBackHomeValue)) {
            isbackhome = isBackHomeValue.toLowerCase().equals("true");
        }
        /**
         * 后面换成可以配置
         */
        if (!TextUtils.isEmpty(isJoin)) {
            if (globalConfig != null) {
                isbackhome = globalConfig.getAfp().isIsbackhome();
                AppDebug.e("TAG", "isBackHome===111" + isbackhome);
            } else {
                isbackhome = true;
                AppDebug.e("TAG", "isBackHome===222");
            }

            AppDebug.e("TAG", "isBackHome===");
        }
        AppDebug.i(TAG, "isBackHome isbackhome = " + isbackhome);
        return isbackhome;
    }

    @Override
    protected int getDefaultSelection() {
        if (mCats != null && !mCats.isEmpty()) {
            Cat cat = mCats.get(0);
            if (cat != null) {
                String id = cat.getId();
                if (TextUtils.equals(id, couponTabKey)) {
                    if (TextUtils.equals(shopFrom, "Coupon")) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
            }
        }
        return super.getDefaultSelection();
    }

    @Override
    protected void handleBackPress() {
        if (mIsBackHome) {
            String homeUri = "tvtaobao://home?module=main&" + CoreIntentKey.URI_FROM_APP + "=" + getAppName();
            Intent intent = new Intent();
            intent.putExtra(CoreActivity.INTENT_KEY_INHERIT_FLAGS, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setData(Uri.parse(homeUri));
            startActivity(intent);
            AppDebug.i(TAG, "onBackPressed mIsBackHome  = " + mIsBackHome + "; homeUri = " + homeUri + "; intent = "
                    + intent);
            return;
        }
        super.handleBackPress();
    }

    /**
     * 初始化界面中的变量值
     */
    private void onInitVariableValue() {
        mIsRequest = false;
        mTextShadowPix = 1;
        mCommonShow = false;
        mSingleAllFirst = true;
        mIsStartFlipper = false;
        mFlipperShow = false;
        mSellerInfo = null;

        mHandler = new ShopHandler(new WeakReference<ShopActivity>(this));

        // 创建网络断开监听
        mShopNetworkOkDoListener = new ShopNetworkOkDoListener(new WeakReference<ShopActivity>(this));

        mGoodListImageHandle = new GoodListImageHandle(this);
        mGoodListImageHandle.onInitPaint();

        mGoodsArrayList = new HashMap<String, ArrayList<SearchedGoods>>();
        mGoodsArrayList.clear();

        // 创建菜单的管理队列
        mGoodsMenuViewInfoMap = new HashMap<String, MenuView_Info>();
        mGoodsMenuViewInfoMap.clear();

        mBusinessRequest = BusinessRequest.getBusinessRequest();

//        fiv_pierce_home_focusd = (FocusImageView) this.findViewById(com.yunos.tvtaobao.businessview.R.id.fiv_pierce_home_focusd);
//
//        fiv_pierce_my_focusd = (FocusImageView) this.findViewById(com.yunos.tvtaobao.businessview.R.id.fiv_pierce_my_focusd);
//        fiv_pierce_cart_focusd = (FocusImageView) this.findViewById(com.yunos.tvtaobao.businessview.R.id.fiv_pierce_cart_focusd);
//        fiv_pierce_red_packet_focusd = (FocusImageView) this.findViewById(com.yunos.tvtaobao.businessview.R.id.fiv_pierce_red_packet_focusd);
//        fiv_pierce_block_focusd = (FocusImageView) this.findViewById(com.yunos.tvtaobao.businessview.R.id.fiv_pierce_block_focusd);
//        fiv_pierce_contact_focusd = (FocusImageView) this.findViewById(com.yunos.tvtaobao.businessview.R.id.fiv_pierce_contact_focusd);
//        fiv_pierce_come_back_focusd = (FocusImageView) this.findViewById(com.yunos.tvtaobao.businessview.R.id.fiv_pierce_come_back_focusd);
//        fiv_pierce_red_jifen_focusd = (FocusImageView) this.findViewById(com.yunos.tvtaobao.businessview.R.id.fiv_pierce_red_jifen_focusd);
//
//        tv_pierce_home = (TextView) this.findViewById(com.yunos.tvtaobao.businessview.R.id.tv_pierce_home);
//        tv_pierce_my = (TextView) this.findViewById(com.yunos.tvtaobao.businessview.R.id.tv_pierce_my);
//        tv_pierce_cart = (TextView) this.findViewById(com.yunos.tvtaobao.businessview.R.id.tv_pierce_cart);
//        tv_pierce_red_packet = (TextView) this.findViewById(com.yunos.tvtaobao.businessview.R.id.tv_pierce_red_packet);
//        tv_pierce_block = (TextView) this.findViewById(com.yunos.tvtaobao.businessview.R.id.tv_pierce_block);
//        tv_pierce_contact_focusd = (TextView) this.findViewById(com.yunos.tvtaobao.businessview.R.id.tv_pierce_contact_focusd);
//        tv_pierce_come_back = (TextView) this.findViewById(com.yunos.tvtaobao.businessview.R.id.tv_pierce_come_back);
//        tv_pierce_red_jifen = (TextView) this.findViewById(com.yunos.tvtaobao.businessview.R.id.tv_pierce_red_jifen);
//        iv_pierce_cart_active = (ImageView) this.findViewById(com.yunos.tvtaobao.businessview.R.id.iv_pierce_cart_active);
    }

    /**
     * 设置左侧TAB 和 GridView 的 Listener
     */

    private void onSetTabBaseActivityListener() {

        setTabFocusListViewListener(new TabFocusListViewListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AppDebug.i("Group", "onFocusChange ---> v = " + v + "; hasFocus = " + hasFocus);
                if (hasFocus) {
                    setFocusItemLayoutForShopBackgroudState(mTabFocusListView, View.INVISIBLE);
                    setTextViewEllipsize(mTabFocusListView, TruncateAt.MARQUEE);
                } else {
                    HandleTabMissFocus();
                }
            }

            @Override
            public void onItemSelected(View select, int position, boolean isSelect, View fatherView) {

                ItemLayoutForShop itemLayoutForShop = (ItemLayoutForShop) select;
                if (itemLayoutForShop != null) {
                    itemLayoutForShop.setTextViewFocusable(isSelect);
                }
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        setTabFocusFlipGridViewListener(new TabFocusFlipGridViewListener() {

            @Override
            public void onFocusChange(FocusFlipGridView focusFlipGridView, String tabkey, View v, boolean hasFocus) {
                AppDebug.i(TAG, "onFocusChange ---> v = " + v + "; hasFocus = " + hasFocus);
                if (hasFocus) {
                    setFocusItemLayoutForShopBackgroudState(mTabFocusListView, View.VISIBLE);
                    setTextViewEllipsize(mTabFocusListView, TruncateAt.END);
                }
            }

            @Override
            public void onLayoutDone(FocusFlipGridView focusFlipGridView, String tabkey, boolean isFirst) {
                AppDebug.i(TAG, "onLayoutDone ---> focusFlipGridView = " + focusFlipGridView + "; tabkey = " + tabkey
                        + "; isFirst = " + isFirst + "; mFlipperShow = " + mFlipperShow + "; mIsStartFlipper = "
                        + mIsStartFlipper + "; mSingleAllFirst = " + mSingleAllFirst);

                if (mFlipperShow) {
                    if (!mIsStartFlipper) {
                        mIsStartFlipper = true;
                        mFlipper.setFlipInterval(3000);
                        if (mShopInfo != null) {
                            mShopInfo.setVisibility(View.VISIBLE);
                        }
                    }
                }

                if (mSingleAllFirst) {
                    int size = -1;
                    if (mCats != null) {
                        size = mCats.size();
                    }

                    // 如果是少于2个，那么直接聚焦到商品卡位上
                    if (size < 2) {
                        mTabFocusPositionManager.requestFocus(focusFlipGridView, View.FOCUS_RIGHT);
                    }
                    // 只有“全部宝贝”一项时，第一次时GridView获得焦点，否则多次请求聚焦，会出现抖动！
                    mSingleAllFirst = false;
                }
            }

            @Override
            public void onItemSelected(FocusFlipGridView focusFlipGridView, String tabkey, View selectview,
                                       int position, boolean isSelect, View parent) {
                if (mGoodsMenuViewInfoMap != null) {
                    MenuView_Info menuView_Info = mGoodsMenuViewInfoMap.get(tabkey);
                    onHandleTopMask(position, menuView_Info);
                }
            }

            @Override
            public boolean onGetview(FocusFlipGridView focusFlipGridView, String tabkey, int position,
                                     int currentTabPosition) {
                onHandleGetview(focusFlipGridView, tabkey, position, currentTabPosition);
                return true;
            }

            @Override
            public boolean onItemClick(FocusFlipGridView focusFlipGridView, String tabkey, AdapterView<?> arg0,
                                       View arg1, int position, long arg3) {
                AppDebug.i(TAG, "onItemClick tabkey:" + tabkey + ",position:" + position);
                HandleItemClicked();
                if (couponTabKeyName.equals(tabkey)) {
                    ShopCoupon mShopCoupon = mShopCouponList.get(position);
                    if (mShopCoupon != null && mShopCoupon.getOwnNum() > 0) {
                        return true;
                    }
                    mBusinessRequest.applyShopCoupon(mSellerId, mShopCoupon.getActivityId(),
                            new ApplyCouponBusinessRequestListener(new WeakReference<BaseActivity>(ShopActivity.this),
                                    position));
                    return true;
                }
                return false;
            }

            @Override
            public void onFinished(FocusFlipGridView focusFlipGridView, String tabkey) {
                if (mGoodsMenuViewInfoMap != null) {
                    MenuView_Info menuView_Info = mGoodsMenuViewInfoMap.get(tabkey);
                    int seclectPos = focusFlipGridView.getSelectedItemPosition();
                    onHandleTopMask(seclectPos, menuView_Info);
                }
            }

            @Override
            public void onStart(FocusFlipGridView focusFlipGridView, String tabkey) {

            }
        });
    }

    /**
     * 设置TAB背景的显示状态
     *
     * @param fatherView
     * @param visibility
     */

    public void setFocusItemLayoutForShopBackgroudState(FocusListView fatherView, int visibility) {
        View slectview = fatherView.getSelectedView();
        if (slectview instanceof ItemLayoutForShop && slectview != null) {
            ItemLayoutForShop currentItemLayoutForShop = (ItemLayoutForShop) slectview;
            currentItemLayoutForShop.setBackgroudView(visibility);
        }

        AppDebug.i(TAG, "setFocusItemLayoutForShopBackgroudState ---> visibility = " + visibility + "; slectview = "
                + slectview);
    }

    @Override
    protected String getEurlOfEnterdetail(FocusFlipGridView focusFlipGridView, String tabkey, AdapterView<?> arg0, View arg1, int position, long arg3) {
        return null;
    }

    @Override
    protected String getUriOfEnterDetail(FocusFlipGridView focusFlipGridView, String tabkey, AdapterView<?> arg0, View arg1, int position, long arg3) {
        return null;
    }

    @Override
    protected String getTitleOfEnterDetail(FocusFlipGridView focusFlipGridView, String tabkey, AdapterView<?> arg0, View arg1, int position, long arg3) {
        return null;
    }

    @Override
    protected String getPicUrlOfEnterDetail(FocusFlipGridView focusFlipGridView, String tabkey, AdapterView<?> arg0, View arg1, int position, long arg3) {
        return null;
    }

    @Override
    protected void enterDisplayDetailAsync(String title, String picUrl, String eurl) {
        OnWaitProgressDialog(true);
        ZTCUtils.getZtcItemId(this, title, picUrl, eurl, new ZTCUtils.ZTCItemCallback() {
            @Override
            public void onSuccess(String id) {
                OnWaitProgressDialog(false);
                Map<String, String> exparams = new HashMap<String, String>();
                exparams.put("isZTC", "true");
                enterDisplayDetail(id, exparams);
            }

            @Override
            public void onFailure(String msg) {
                OnWaitProgressDialog(false);

            }
        });
    }

    /**
     * 改变TAB文字的跑马灯
     *
     * @param fatherView
     * @param where
     */
    public void setTextViewEllipsize(FocusListView fatherView, TruncateAt where) {

        View slectview = fatherView.getSelectedView();
        if (slectview instanceof ItemLayoutForShop && slectview != null) {
            ItemLayoutForShop currentItemLayoutForShop = (ItemLayoutForShop) slectview;
            TextView classifyView = currentItemLayoutForShop.getItemTextView();
            if (classifyView != null) {
                classifyView.setEllipsize(where);
                if (where == TruncateAt.MARQUEE) {
                    classifyView.setSelected(true);
                } else {
                    classifyView.setSelected(false);
                }
            }
            // 请求父视图重新布局
            slectview.requestLayout();
        }
    }

    /**
     * 初始化其他参数，并首次请求数据
     */
    private void onInitParamAndRequest() {

        Resources resources = this.getResources();
        if (TextUtils.isEmpty(mSellerId) && TextUtils.isEmpty(mShopId)) {
            showErrorDialog(resources.getString(R.string.ytsdk_empty_param), true);
        } else {
            getWapShopInfoRequest(mSellerId, mShopId);
        }
    }

    /**
     * 获取店铺分类
     *
     * @param sellerId
     * @param shopId
     */
    private void getCatInfoInShop(final String sellerId, final String shopId) {

        if (mBusinessRequest == null) {
            return;
        }

        OnWaitProgressDialog(true);

        mBusinessRequest.getCatInfoInShop(sellerId, shopId, new CatInfoInShopBusinessRequestListener(
                new WeakReference<BaseActivity>(this)));
    }

    /**
     * 获取店铺信息
     *
     * @param shopId
     * @param sellerId
     */
    private void getWapShopInfoRequest(String sellerId, String shopId) {

        if (mBusinessRequest == null) {
            return;
        }
        OnWaitProgressDialog(true);
        mBusinessRequest.getWapShopInfo(sellerId, shopId, new WapShopInfoBusinessRequestListener(
                new WeakReference<BaseActivity>(this)));
    }

    /**
     * 显示店铺信息,并运行上下翻滚的动画效果
     * showSellerInfo
     *
     * @param sellerInfo
     */
    protected void showSellerInfo(SellerInfo sellerInfo) {
        AppDebug.v(TAG, TAG + ".showSellerInfo.sellerInfo = " + sellerInfo);
        if (sellerInfo == null) {
            return;
        }
        mShopType = BaseConfig.SELLER_TAOBAO;
        String isMall = sellerInfo.getIsMall();
        if ((isMall != null) && (isMall.equals("true"))) {
            mShopType = BaseConfig.SELLER_TMALL;
        }

        // 根据类型改变界面
        showShopViewInfo();

        // 显示店铺以及服务描述
        if (!TextUtils.isEmpty(sellerInfo.getTitle())) {
            mShopName.setVisibility(View.VISIBLE);
            mShopName.setText(sellerInfo.getTitle());
            shopDSRScore shopdsrscore = sellerInfo.getShopDSRScore();
            if (shopdsrscore != null) {

                mDescription.setText(String.format(this.getString(R.string.ytsdk_description_is_consistent),
                        shopdsrscore.getMerchandisScore()));
                mDescription.setShadowLayer(0.5f, mTextShadowPix, mTextShadowPix, mTextShadowColor);

                mAttitude.setText(String.format(this.getString(R.string.ytsdk_service_attitude),
                        shopdsrscore.getServiceScore()));
                mAttitude.setShadowLayer(0.5f, mTextShadowPix, mTextShadowPix, mTextShadowColor);

                mDeliverySpeed.setText(String.format(this.getString(R.string.ytsdk_delivery_speed),
                        shopdsrscore.getConsignmentScore()));
                mDeliverySpeed.setShadowLayer(0.5f, mTextShadowPix, mTextShadowPix, mTextShadowColor);

                showShopScore(mDescriptionScore, shopdsrscore.getMg());
                showShopScore(mAttitudeScore, shopdsrscore.getSg());
                showShopScore(mDeliverySpeedScore, shopdsrscore.getCg());

            }
            if (!TextUtils.isEmpty(sellerInfo.getCity())) {
                mShopLocation.setVisibility(View.VISIBLE);
                mShopLocation
                        .setText(String.format(this.getString(R.string.ytsdk_shop_location), sellerInfo.getCity()));
            } else {
                mShopLocation.setVisibility(View.GONE);
            }
            /** 店铺描述相符 等 滚动 */
            mShopLocation.setShadowLayer(0.5f, mTextShadowPix, mTextShadowPix, mTextShadowColor);

            mFlipper.setFlipInterval(10);
            mFlipper.startFlipping();
            mFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.ytsdk_push_up_in));
            mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.ytsdk_push_up_out));

            mFlipperShow = true;
        }
    }

    /**
     * 显示店铺的分数
     *
     * @param tv
     * @param score
     */
    public void showShopScore(TextView tv, double score) {
        String high = this.getString(R.string.ytsdk_higher_the_same_industry);
        String low = this.getString(R.string.ytsdk_low_the_same_industry);
        String eql = this.getString(R.string.ytsdk_flat);
        // 高于同行业
        if (score > 0.5) {
            tv.setText(String.format(high, score));
            // 低于同行业
        } else if (score < -0.5) {
            tv.setText(String.format(low, Math.abs(score)));
        } else {
            tv.setText(eql);
        }
        tv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        tv.setShadowLayer(0.5f, mTextShadowPix, mTextShadowPix, mTextShadowColor);
    }

    /**
     * 显示 taobao or tmall 信息
     */
    private void showShopViewInfo() {

        if (!TextUtils.isEmpty(mShopType)) {

            if (mShopType.equals(BaseConfig.SELLER_TMALL)) {

                // 显示 tmall
                mShopBackGround = this.getResources().getDrawable(R.drawable.ytsdk_ui2_goodslist_bg);
                mTabFocusPositionManager.onInitBackgroud();
                mTabFocusPositionManager.setBackgroundDrawable(mShopBackGround);
                mTabFocusResId = R.drawable.ytsdk_ui2_shop_classify_foucs_tamll;
                mShopLogoImageView.setBackgroundResource(R.drawable.ytsdk_ui2_shop_logo_tmall);

            } else if (mShopType.equals(BaseConfig.SELLER_TAOBAO)) {

                // 显示taobao 信息
                mShopBackGround = this.getResources().getDrawable(R.drawable.ytsdk_ui2_goodslist_bg);
                mTabFocusPositionManager.onInitBackgroud();
                mTabFocusPositionManager.setBackgroundDrawable(mShopBackGround);
                mTabFocusResId = R.drawable.ytsdk_ui2_shop_classify_foucs_taobao;
                mShopLogoImageView.setBackgroundResource(R.drawable.ytsdk_ui2_shop_logo_taobao);
            }
        }

        mCommonBackGround = this.getResources().getDrawable(R.drawable.ytsdk_ui2_common_background);
    }

    /**
     * 根据类目的信息。请求数据
     *
     * @param position
     */
    protected void showShopItemList(final int position) {

        AppDebug.i(TAG, "showShopItemList  ---> position =  " + position + "; mIsRequest = " + mIsRequest);

        String menuName = null;
        Cat cat = null;
        if (mCats != null && position >= 0 && position < mCats.size()) {
            cat = mCats.get(position);
        }
        if (cat != null) {
            menuName = cat.getName();
        }

        AppDebug.i(TAG, "showShopItemList  ---> position =  " + position + "; menuName = " + menuName);

        final MenuView_Info menuView_Info = mGoodsMenuViewInfoMap.get(menuName);
        if (menuView_Info == null) {
            return;
        }

        if (!menuView_Info.haveGoods) {
            return;
        }

        mIsRequest = true;

        // 不允许按返回键取消进度条
        setProgressCancelable(false);

        OnWaitProgressDialog(true);
        if (couponTabKey.equals(cat.getId())) {
            onHandlRequestShopCouponList(mShopCouponList, menuView_Info, cat);
        } else {
            int count = 15;
            TabGoodsBaseAdapter adapter = (TabGoodsBaseAdapter) menuView_Info.goodListGirdViewAdapter;
            if (adapter != null && adapter.getColumnsCounts() > 0) {
                count = adapter.getColumnsCounts() * VISUALCONFIG.EACH_REQUEST_ROW_COUNT;// 每次索引个数为行数的整数倍
            }
            AppDebug.v(TAG, TAG + ".showShopItemList.count = " + count);
            mBusinessRequest.getShopItemList(mSellerId, mShopId, "hotsell", "", cat.getId(), "", "", "", String
                    .valueOf(count), menuView_Info.CurReqPageCount + "", new ShopItemListBusinessRequestListener(
                    new WeakReference<BaseActivity>(this), menuView_Info, cat));
        }

    }

    /**
     * 主要是作为数据结构的映射
     * 把 GoodsListItem 映射为 Goods
     *
     * @param key
     * @param goodslistitem
     */
    private void DataStructMapToGoods(String key, List<GoodsListItem> goodslistitem) {

        AppDebug.i(TAG, "DataStructMapToGoods --> key = " + key + ";mGoodsArrayList = " + mGoodsArrayList);

        if (mGoodsArrayList == null) {
            return;
        }

        ArrayList<SearchedGoods> arraylist = mGoodsArrayList.get(key);

        AppDebug.i(TAG, "DataStructMapToGoods --> key = " + key + ";arraylist = " + arraylist);

        if (arraylist == null) {
            return;
        }
        int len = goodslistitem.size();

        for (int index = 0; index < len; index++) {
            GoodsListItem goodsListItem = goodslistitem.get(index);

            GoodsTmail goods = new GoodsTmail();

            // 存放商品图片URL
            goods.setImgUrl(goodsListItem.getPicUrl());

            // 存放商品的名称
            goods.setTitle(goodsListItem.getTitle());

            // 存放月销量
            goods.setSold(String.valueOf(goodsListItem.getSold()));

            // 存放 价格
            goods.setPrice(goodsListItem.getReservePrice());

            // 存放无线专享价格
            goods.setWmPrice(goodsListItem.getSalePrice());

            // 保存商品的ID号
            goods.setItemId(goodsListItem.getAuctionId());

            // 运费
            goods.setPostFee(null);

            arraylist.add(goods);
        }

        AppDebug.i(TAG, "DataStructMapToGoods --> arraylist.size = " + arraylist.size() + "; len = " + len);
    }

    /**
     * 处理顶部蒙版，显示或隐藏
     *
     * @param position
     * @param save_viewpager_Info
     */

    private void onHandleTopMask(int position, MenuView_Info save_viewpager_Info) {

        if (mShadowTop == null) {
            return;
        }

        if (save_viewpager_Info == null) {
            return;
        }

        // 当选中第一行时，则隐藏蒙版，否则其他行则显示蒙版
        TabGoodsBaseAdapter tabGoodsBaseAdapter = (TabGoodsBaseAdapter) save_viewpager_Info.goodListGirdViewAdapter;
        int currentRow = position / tabGoodsBaseAdapter.getColumnsCounts();
        if (currentRow <= 0) {
            mShadowTop.setVisibility(View.INVISIBLE);
            save_viewpager_Info.Mask_display = false;
        } else {
            mShadowTop.setVisibility(View.VISIBLE);
            save_viewpager_Info.Mask_display = true;
        }

        // 刷新蒙版，为了解决滚动时产生的屏幕刷新遗留痕迹
        // mGoodListTopMask.invalidate();

        // 改变背景
        onShowCommonBackgroud(save_viewpager_Info);
    }

    /**
     * 检查当前的TAB 是否要加载新的商品数据
     *
     * @param container
     * @param ordey
     * @param position
     * @param currentTabPosition
     */
    private void onHandleGetview(ViewGroup container, String ordey, int position, int currentTabPosition) {

        AppDebug.i(TAG, "onGetview  --> onHandleGetview:  mIsRequest =  " + mIsRequest + ";position  = " + position
                + "; ordey = " + ordey);

        // 如果正在请求中，则返回
        if (mIsRequest) {
            return;
        }

        if (mGoodsMenuViewInfoMap == null) {
            return;
        }

        MenuView_Info menuView_Info = mGoodsMenuViewInfoMap.get(ordey);
        if (menuView_Info == null) {
            return;
        }

        if (menuView_Info.End_Request) {
            return;
        }

        TabGoodsBaseAdapter listAdapter = (TabGoodsBaseAdapter) menuView_Info.goodListGirdViewAdapter;
        if (listAdapter == null) {
            return;
        }

        int count = listAdapter.getCount();

        int lastVisible = (position / listAdapter.getColumnsCounts()) * listAdapter.getColumnsCounts();

        // 获取到的数据应该要多三行
        int saveCount = lastVisible + 3 * listAdapter.getColumnsCounts();

        AppDebug.i(TAG, "onGetview  --> onHandleGetview:  lastVisible =  " + lastVisible + ";saveCount  = " + saveCount
                + "; count = " + count);

        if (count > saveCount) {
            return;
        }

        showShopItemList(currentTabPosition);
    }

    /**
     * 改变背景
     */
    private void onShowCommonBackgroud(MenuView_Info save_viewpager_Info) {

        if (save_viewpager_Info == null) {
            return;
        }

        save_viewpager_Info.CommonState = save_viewpager_Info.Mask_display;

        if (mCommonShow == save_viewpager_Info.CommonState) {
            return;
        }
        mCommonShow = save_viewpager_Info.CommonState;

        if (mTabFocusPositionManager == null) {
            return;
        }

        if (mCommonShow) {
            mTabFocusPositionManager.setBackgroundDrawable(mCommonBackGround);
        } else {
            mTabFocusPositionManager.setBackgroundDrawable(mShopBackGround);
        }
    }

    @Override
    public void onBackPressed() {
        mIsRequest = false;
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {

        onClearAndDestroy();
        onRemoveKeepedActivity(ShopActivity.class.getName());

        super.onDestroy();

        Map<String, String> properties = Utils.getProperties();
        properties.put("built_in", "research");

        Utils.utUpdatePageProperties("Page_tool", properties);
        Utils.utPageDisAppear("Page_tool");
        ZTCUtils.destroy();
//        if (advertiseReceiver != null) {
//            unregisterReceiver(advertiseReceiver);
//            advertiseReceiver = null;
//        }
    }

    /**
     * 释放资源
     */
    private void onClearAndDestroy() {

        // 移除Handler
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }

        // 清除Cat数据
        if (mCats != null) {
            mCats.clear();
            mCats = null;
        }

        // 清除映射后的数据结构
        if (mGoodsArrayList != null) {
            Iterator<Map.Entry<String, ArrayList<SearchedGoods>>> iter = mGoodsArrayList.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, ArrayList<SearchedGoods>> entry = (Map.Entry<String, ArrayList<SearchedGoods>>) iter.next();
                ArrayList<SearchedGoods> val = (ArrayList<SearchedGoods>) entry.getValue();
                val.clear();
            }
            mGoodsArrayList.clear();
        }

        // 清除view的信息
        if (mGoodsMenuViewInfoMap != null) {
            Iterator<Map.Entry<String, MenuView_Info>> iter = mGoodsMenuViewInfoMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, MenuView_Info> entry = (Map.Entry<String, MenuView_Info>) iter.next();
                MenuView_Info val = (MenuView_Info) entry.getValue();

                if (val != null) {
                    if (val.lifeUiGridView != null) {
                        val.lifeUiGridView.removeAllViewsInLayout();
                    }
                }
            }
            mGoodsMenuViewInfoMap.clear();
        }

        if (mGoodListImageHandle != null) {
            mGoodListImageHandle.onDestroyAndClear();
        }

        if (mShopClassifyAdapter != null) {
            mShopClassifyAdapter.onDestroyAndClear();
        }
    }

    private void onCleanMenuViewInfo(MenuView_Info menuView_Info) {
        if (menuView_Info != null) {

            // 当前行
            menuView_Info.CurrentRows = 1;
            // 当前请求的页数
            menuView_Info.CurReqPageCount = 1;
            menuView_Info.mInLayout = true;
            // 切换菜单时，未请求数据
            menuView_Info.HasRequestData_ChangeMenu = false;

            // 顶层蒙版显示
            menuView_Info.Mask_display = false;

            menuView_Info.CommonState = menuView_Info.Mask_display;

            // 设置实际返回数为0
            menuView_Info.ReturnTotalResults = 0;

            // 设置当前请求还没有结束
            menuView_Info.End_Request = false;

            // 针对低配版设置的标志
            menuView_Info.checkVisibleItemOfLayoutDone = false;
        }
    }

    /**
     * 目的为了支持低配版
     *
     * @param oldPosition
     */
    public void onDestroyGridView(int oldPosition) {

        if ((mCats != null) && (mGoodsMenuViewInfoMap != null)) {

            if ((oldPosition < 0) || (oldPosition > mCats.size() - 1)) {
                return;
            }

            Cat cat = mCats.get(oldPosition);
            String menuText = cat.getName();
            MenuView_Info menuView_Info = null;
            if ((menuText != null) && (mGoodsMenuViewInfoMap.containsKey(menuText))) {
                menuView_Info = mGoodsMenuViewInfoMap.get(menuText);
            }
            if (menuView_Info != null) {
                if (mGoodsArrayList != null) {
                    ArrayList<SearchedGoods> goodsList = mGoodsArrayList.get(menuText);
                    if (goodsList != null) {
                        goodsList.clear();
                    }
                }
                if (menuView_Info.lifeUiGridView != null) {
                    menuView_Info.lifeUiGridView.removeAllViewsInLayout();
                    menuView_Info.lifeUiGridView.setVisibility(View.GONE);
                }
                onCleanMenuViewInfo(menuView_Info);
            }

        }
    }

    public void onHandlRequestCatInfoInShop(List<Cat> catList) {

        OnWaitProgressDialog(false);
        if (isHasDestroyActivity()) {
            return;
        }

        int size = 0;
        if ((catList != null) && (catList.size() > 0)) {
            size = catList.size();
        }

        // 对分类菜单作限制， 最多 CLASSFY_LIMIT 条 
        size = Math.min(size, CLASSFY_LIMIT);

        if (mCats == null) {
            mCats = new ArrayList<Cat>();
        }
        mCats.clear();

        // 把 全部分类 添加到队列前
        if (mShopCouponList != null && !mShopCouponList.isEmpty()) {
            Cat coupon = new Cat();
            coupon.setId(couponTabKey);
            coupon.setName(couponTabKeyName);
            mCats.add(coupon);
        }

        Cat allGoods = new Cat();
        allGoods.setId("");
        String allname = getString(R.string.ytsdk_shop_all_goods);
        allGoods.setName(allname);
        mCats.add(allGoods);

        // 添加其他分类
        List<Cat> cats = catList.subList(0, size);
        mCats.addAll(cats);

        // 清除不用的LIST变量
        if (cats != null) {
            cats.clear();
            cats = null;
        }

        AppDebug.i(TAG, "getCatInfoInShop --> getCatInfoInShop --> onRequestDone --> mShopClassifyAdapter = "
                + mShopClassifyAdapter);

        if ((mGoodsArrayList != null) && (mCats != null)) {
            int len = mCats.size();
            for (int i = 0; i < len; i++) {
                Cat cat = mCats.get(i);
                if (cat.getId().equals(couponTabKey)) {
                    continue;
                }
                ArrayList<SearchedGoods> array = new ArrayList<SearchedGoods>();
                array.clear();
                if ((cat != null) && (cat.getName() != null)) {
                    mGoodsArrayList.put(cat.getName(), array);
                }
            }
        }

        onInitTabBaseActivity();
    }

    /**
     * 判断店铺的标题是否不为空，不为空，则获取店铺的分类
     *
     * @param sellerInfo
     */
    public void onHandlRequestWapShopInfo(SellerInfo sellerInfo) {
        if (null != sellerInfo) {
            if (StringUtil.isEmpty(sellerInfo.getTitle())) {
                showErrorDialog(getString(R.string.ytsdk_server_error), true);
                return;
            }

            mSellerInfo = sellerInfo;
            if (TextUtils.isEmpty(mSellerId)) {
                mSellerId = mSellerInfo.getSellerId();
            }

            //如果是海尔VIP购物来源,不显示店铺优惠分类
            if (getmFrom() != null && getmFrom().toLowerCase().contains("tvhongbao")) {
                // 获取店铺的分类
                getCatInfoInShop(mSellerId, mShopId);
            } else {
                getShopCoupon();
            }
        }

    }

    private void getShopCoupon() {
        mBusinessRequest.getShopCoupon(mSellerId, new ShopCouponListBusinessRequestListener(
                new WeakReference<BaseActivity>(this)));
    }

    /**
     * 处理请求返回的结果
     *
     * @param goodsList
     * @param menuView_Info
     * @param cat
     */
    public void onHandlRequestShopItemList(GoodsList goodsList, MenuView_Info menuView_Info, Cat cat) {

        mIsRequest = false;
        // 允许按返回键取消进度条
        setProgressCancelable(true);

        OnWaitProgressDialog(false);

        if (isHasDestroyActivity()) {
            return;
        }

        int mResultsTotalCounts = 0;

        // 检查当前的应用返回的数据数
        if (goodsList != null) {
            List<GoodsListItem> itemsArray = goodsList.getItemsArray();

            if (itemsArray != null) {
                mResultsTotalCounts = itemsArray.size();
            }
        }

        // 返回的数据结果是0
        if (mResultsTotalCounts == 0) {

            // 如果是第一次请求数据, 那么认为当前关键字，或类目--没有商品
            if (menuView_Info.firstStartRequestData) {
                // 没有商品， 则提示
                menuView_Info.haveGoods = false;

                if (mHandler != null) {
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            if (mEmptyView != null) {
                                mEmptyView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }

            } else {
                // 再次请求也没有数据，认为结束
                menuView_Info.End_Request = true;
            }

            return;
        }

        menuView_Info.firstStartRequestData = false;

        if (goodsList != null) {
            List<GoodsListItem> itemsArray = goodsList.getItemsArray();
            if ((itemsArray == null) || (itemsArray.isEmpty())) {
                return;
            }

            // 数据结构映射
            DataStructMapToGoods(cat.getName(), goodsList.getItemsArray());

            menuView_Info.HasRequestData_ChangeMenu = true;

            menuView_Info.haveGoods = true;

            // 当前请求页加1，为下一次作准备
            menuView_Info.CurReqPageCount++;

            GoodListLifeUiGridView goodListLifeUiGridView = (GoodListLifeUiGridView) menuView_Info.lifeUiGridView;
            GoodListGirdViewAdapter goodListGirdViewAdapter = (GoodListGirdViewAdapter) menuView_Info.goodListGirdViewAdapter;

            int tabnumber = menuView_Info.Index;

            AppDebug.i(TAG, "onHandlRequestShopItemList --> goodListLifeUiGridView = " + goodListLifeUiGridView
                    + ";  goodListGirdViewAdapter = " + goodListGirdViewAdapter + "; tabnumber = " + tabnumber
                    + "; mCurrentSelectTabNumBer = " + mCurrentSelectTabNumBer);

            // 如果请求数据返回时，此数据所关联TAB编号与当前所在的TAB编号一样，那么显示gridview
            if (goodListLifeUiGridView != null && mCurrentSelectTabNumBer == tabnumber) {
                goodListLifeUiGridView.setVisibility(View.VISIBLE);
                if (goodListGirdViewAdapter != null) {
                    goodListGirdViewAdapter.notifyDataSetChanged();
                    goodListGirdViewAdapter.onCheckVisibleItemAndLoadBitmap();
                }
            }
        }
    }

    public void onHandlRequestShopCouponList(List<ShopCoupon> list, MenuView_Info menuView_Info, Cat cat) {

        mIsRequest = false;
        // 允许按返回键取消进度条
        setProgressCancelable(true);

        OnWaitProgressDialog(false);

        if (isHasDestroyActivity()) {
            return;
        }

        int mResultsTotalCounts = 0;

        // 检查当前的应用返回的数据数
        if (list != null) {
            mResultsTotalCounts = list.size();
        }

        // 返回的数据结果是0
        if (mResultsTotalCounts == 0) {

            // 如果是第一次请求数据, 那么认为当前关键字，或类目--没有商品
            if (menuView_Info.firstStartRequestData) {
                // 没有商品， 则提示
                menuView_Info.haveGoods = false;

                if (mHandler != null) {
                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            if (mEmptyView != null) {
                                mEmptyView.setText(getString(R.string.ytbv_no_coupon));
                                mEmptyView.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }

            } else {
                // 再次请求也没有数据，认为结束
                menuView_Info.End_Request = true;
            }

            return;
        }

        menuView_Info.firstStartRequestData = false;

        if (list != null && !list.isEmpty()) {

            menuView_Info.HasRequestData_ChangeMenu = true;

            menuView_Info.haveGoods = true;

            // 当前请求页加1，为下一次作准备
            menuView_Info.CurReqPageCount++;

            GoodListLifeUiGridView goodListLifeUiGridView = (GoodListLifeUiGridView) menuView_Info.lifeUiGridView;
            mShopCouponGirdViewAdapter = (ShopCouponGridViewAdapter) menuView_Info.goodListGirdViewAdapter;

            int tabnumber = menuView_Info.Index;

            AppDebug.i(TAG, "onHandlRequestShopItemList --> goodListLifeUiGridView = " + goodListLifeUiGridView
                    + ";  mShopCouponGirdViewAdapter = " + mShopCouponGirdViewAdapter + "; tabnumber = " + tabnumber
                    + "; mCurrentSelectTabNumBer = " + mCurrentSelectTabNumBer);

            // 如果请求数据返回时，此数据所关联TAB编号与当前所在的TAB编号一样，那么显示gridview
            if (goodListLifeUiGridView != null && mCurrentSelectTabNumBer == tabnumber) {
                goodListLifeUiGridView.setVisibility(View.VISIBLE);
                if (mShopCouponGirdViewAdapter != null) {
                    mShopCouponGirdViewAdapter.setCouponList(list);
                    mShopCouponGirdViewAdapter.notifyDataSetChanged();
                    //shopCouponGirdViewAdapter.onCheckVisibleItemAndLoadBitmap();
                    menuView_Info.End_Request = true;
                }
            }
            menuView_Info.End_Request = true;
        }
    }

    /**
     * 设置对应的currentTabNumber号处于跑马灯的效果
     *
     * @param currentTabNumber
     */
    private void checkTabEllipsize(int currentTabNumber) {

        if (mTabFocusListView == null) {
            return;
        }

        // 关闭全部view的跑马灯效果
        int count = mTabFocusListView.getChildCount();
        for (int index = 0; index < count; index++) {
            View v = mTabFocusListView.getChildAt(index);
            if (v instanceof ItemLayoutForShop && v != null) {
                ItemLayoutForShop itemLayoutForShop = (ItemLayoutForShop) v;
                TextView classifyView = itemLayoutForShop.getItemTextView();
                if (classifyView != null) {
                    classifyView.setEllipsize(TruncateAt.END);
                    classifyView.setMarqueeRepeatLimit(1);
                    classifyView.setSelected(false);
                }
            }
        }

        // 开启当前currentTabNumber 的跑马灯效果
        int firstView = mTabFocusListView.getFirstVisiblePosition();
        View view = mTabFocusListView.getChildAt(currentTabNumber - firstView);
        if (view instanceof ItemLayoutForShop && view != null) {
            ItemLayoutForShop itemLayoutForShop = (ItemLayoutForShop) view;
            TextView classifyView = itemLayoutForShop.getItemTextView();
            if (classifyView != null) {
                classifyView.setEllipsize(TruncateAt.MARQUEE);
                classifyView.setMarqueeRepeatLimit(1);
                classifyView.setSelected(true);
            }
        }
    }

    @Override
    public String getPageName() {
        return TAG;
    }

    @Override
    protected void refreshData() {

    }

    @Override
    protected String getTag() {
        return TAG;
    }

    /**
     * 重新调整公共的布局, 或者其他的处理，包括布局的修改，或者添加
     *
     * @param tabFocusPositionManager
     */

    @Override
    protected void onReAdjustCommonLayout(TabFocusPositionManager tabFocusPositionManager) {

        AppDebug.i(TAG, "onReAdjustCommonLayout  tabFocusPositionManager  = " + tabFocusPositionManager);
        LayoutInflater.from(this).inflate(R.layout.ytsdk_activity_shop_layout, tabFocusPositionManager, true);

        int focuslisttopMargin = this.getResources().getDimensionPixelSize(R.dimen.dp_90);
        FrameLayout.LayoutParams leftsidelp = (FrameLayout.LayoutParams) mTabFocusListView.getLayoutParams();
        leftsidelp.topMargin = focuslisttopMargin;

        mShopName = (TextView) this.findViewById(R.id.shop_name);

        mDescription = (TextView) this.findViewById(R.id.match_description);
        mDescriptionScore = (TextView) this.findViewById(R.id.match_description_score);

        mAttitude = (TextView) this.findViewById(R.id.attitude);
        mAttitudeScore = (TextView) this.findViewById(R.id.attitude_score);

        mDeliverySpeed = (TextView) this.findViewById(R.id.delivery_speed);
        mDeliverySpeedScore = (TextView) this.findViewById(R.id.delivery_speed_score);

        mShopLocation = (TextView) this.findViewById(R.id.location);

        mShopLogoImageView = (ImageView) this.findViewById(R.id.shop_logo_imageview);
        mShadowTop = (ImageView) this.findViewById(R.id.common_top_mask_view);

        mShopInfo = (RelativeLayout) this.findViewById(R.id.shop_info);
        mFlipper = (ViewFlipper) this.findViewById(R.id.flipper);
        mFlipper.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mShadowTop.setVisibility(View.INVISIBLE);

        // 显示店铺信息
        showSellerInfo(mSellerInfo);
    }

    /**
     * 获取左侧TAB的适配器
     *
     * @return
     */
    @Override
    protected BaseAdapter getTabAdapter() {

        // 获取 左侧TAB的适配器
        AppDebug.i(TAG, "getTabAdapter  mShopClassifyAdapter  = " + mShopClassifyAdapter);
        if (mShopClassifyAdapter == null) {
            mShopClassifyAdapter = new ShopClassifyAdapter(this);
            mShopClassifyAdapter.setClassify(mCats);
        }

        return mShopClassifyAdapter;
    }

    /**
     * 获取girdview
     *
     * @param tabkey
     * @param position
     * @return
     */

    @Override
    protected FocusFlipGridView getTabGoodsGridView(String tabkey, int position) {
        GoodListLifeUiGridView goodListLifeUiGridView = new GoodListLifeUiGridView(this);

        AppDebug.i(TAG, "getTabGoodsGridView  tabkey  = " + tabkey + "; position = " + position);

        return goodListLifeUiGridView;
    }

    /**
     * 获取girdview 的适配器
     *
     * @param tabkey
     * @param position
     * @return
     */

    @Override
    protected TabGoodsBaseAdapter getTabGoodsAdapter(String tabkey, int position) {

        AppDebug.i(TAG, "getTabGoodsAdapter  tabkey  = " + tabkey + "; position = " + position + "; mGoodsArrayList = "
                + mGoodsArrayList);
        Cat cat = mCats.get(position);
        if (cat.getId().equals(couponTabKey)) {
            return new ShopCouponGridViewAdapter(this, mShopCouponList);
        }

        AppDebug.e("TAG", "-=-=-=-=-=size" + mCats.size());
        for (int i = 0; i < mCats.size(); i++) {
            mTableListId.add(10000 + 1000 * 1 + i);
        }

        // 创建对应的适配器，并设置参数
        GoodListGirdViewAdapter adapter = new GoodListGirdViewAdapter(this, false);

        // 设置图片处理
        adapter.setGoodListImageHandle(mGoodListImageHandle);

        // 给适配器设置对应排序的商品信息
        if (mGoodsArrayList != null) {
            adapter.onSetGoodsArrayList(mGoodsArrayList.get(tabkey));
        }

        return adapter;
    }

    /**
     * 初始化GirdView，包括可以修改GirdView的布局参数，添加headerView
     *
     * @param goodListLifeUiGridView
     * @param adapter
     * @param tabkey
     * @param position
     */

    @Override
    protected void initGirdViewInfo(FocusFlipGridView goodListLifeUiGridView, TabGoodsBaseAdapter adapter,
                                    String tabkey, int position) {

        AppDebug.i(TAG, "initGirdViewInfo  tabkey  = " + tabkey + "; position = " + position);

        // 创建对应的参数记录信息
        MenuView_Info save_viewpager_Info = mGoodListFocuseUnit.getViewPagerInfo();

        // 当前行
        save_viewpager_Info.CurrentRows = 1;

        // 允许总的行数
        save_viewpager_Info.TotalRows = VISUALCONFIG.ROWS_TOTAL;

        // 当前请求的页数
        save_viewpager_Info.CurReqPageCount = 1;

        save_viewpager_Info.menuText = tabkey;

        save_viewpager_Info.mInLayout = true;

        // 切换菜单时，未请求数据
        save_viewpager_Info.HasRequestData_ChangeMenu = false;

        // 顶层蒙版显示
        save_viewpager_Info.Mask_display = false;

        save_viewpager_Info.CommonState = save_viewpager_Info.Mask_display;

        save_viewpager_Info.Index = position;
        save_viewpager_Info.lifeUiGridView = goodListLifeUiGridView;
        save_viewpager_Info.goodListGirdViewAdapter = adapter;

        // 关于TBS埋点的信息
        save_viewpager_Info.tbs_p = "_P_Cat";
        save_viewpager_Info.tbs_name = tabkey;

        // 设置实际返回数为0
        save_viewpager_Info.ReturnTotalResults = 0;

        // 设置当前请求还没有结束
        save_viewpager_Info.End_Request = false;

        goodListLifeUiGridView.setNumColumns(adapter.getColumnsCounts());

        mGoodsMenuViewInfoMap.put(tabkey, save_viewpager_Info);

        goodListLifeUiGridView.setNextFocusRightId(R.id.fiv_pierce_home_focusd);
    }

    /**
     * 根据TAB编号，获取tabkey
     *
     * @param tabNumBerPos
     * @return
     */

    @Override
    protected String getTabKeyWordOfTabNumBer(int tabNumBerPos) {
        if (mCats != null && tabNumBerPos >= 0 && tabNumBerPos < mCats.size()) {
            Cat cat = mCats.get(tabNumBerPos);
            if (cat != null) {
                return cat.getName();
            }
        }
        return null;
    }

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

    @Override
    protected String getItemIdOfEnterdetail(FocusFlipGridView focusFlipGridView, String tabkey, AdapterView<?> arg0,
                                            View arg1, int position, long arg3) {

        AppDebug.i(TAG, "getItemIdOfEnterdetail tabkey = " + tabkey + ";  position = " + position);

        if (mGoodsArrayList == null) {
            return null;
        }

        if (tabkey == null) {
            return null;
        }

        // 根据 顺序 ，取出 有关这一排序关键字的全部数据
        ArrayList<SearchedGoods> msortIndexMap = mGoodsArrayList.get(tabkey);
        if (msortIndexMap == null) {
            return null;
        }

        // 具体取出 标识为 itemIndex的 条目
        if (position >= msortIndexMap.size()) {
            return null;
        }

        SearchedGoods mItemGoods = msortIndexMap.get(position);
        if (mItemGoods == null) {
            return null;
        }

        return mItemGoods.getItemId();
    }

    @Override
    protected void enterDisplayDetail(String itemId, Map<String, String> exparams) {
        AppDebug.i(TAG, "enterDisplayDetail itemId = " + itemId);

        if (TextUtils.isEmpty(itemId)) {
            return;
        }
        if (!NetWorkUtil.isNetWorkAvailable()) {
            showNetworkErrorDialog(false);
            return;
        }

        try {
            String url = "tvtaobao://home?app=taobaosdk&module=detail&itemId=" + itemId;
            AppDebug.i("url", "url = " + url);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.putExtra(CoreIntentKey.URI_FROM_APP_BUNDLE, getAppName());
            if (mSellerInfo != null && !TextUtils.isEmpty(mSellerInfo.getTitle())) {
                setHuodong(mSellerInfo.getTitle());
            }
            startActivity(intent);
            return;
        } catch (Exception e) {
            String name = getResources().getString(R.string.ytbv_not_open);
            Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 处理Item点击事件
     */
    private void HandleItemClicked() {
        String keyword = getTabKeyWordOfTabNumBer(mCurrentSelectTabNumBer);
        String controlName = null;
        if (!TextUtils.isEmpty(keyword)) {
            if (keyword.equals(couponTabKeyName)) {
                controlName = Utils.getControlName(getFullPageName(), "Class_Coupon_Click", null);
            } else {
                controlName = Utils.getControlName(getFullPageName(), "Class_Click_P" + mCurrentSelectTabNumBer, null);
            }

            Map<String, String> p = getPageProperties();
            if (!TextUtils.isEmpty(keyword)) {
                p.put("class_name", keyword);
            }

            if (mShopCouponList != null && mShopCouponList.size() > 0) {
                p.put("coupon_amount", String.valueOf(mShopCouponList.size()));
            }
            Utils.utCustomHit(getFullPageName(), controlName, p);
        }
    }

    /**
     * TAB失去焦点时
     */
    private void HandleTabMissFocus() {
        String keyword = getTabKeyWordOfTabNumBer(mCurrentSelectTabNumBer);
        String controlName = null;
        if (!TextUtils.isEmpty(keyword)) {
            if (keyword.equals(couponTabKeyName)) {
                controlName = Utils.getControlName(getFullPageName(), "Class_Coupon_Select", null);
            } else {
                controlName = Utils.getControlName(getFullPageName(), "Class_Select_P" + mCurrentSelectTabNumBer, null);
            }

            Map<String, String> p = getPageProperties();
            if (!TextUtils.isEmpty(keyword)) {
                p.put("class_name", keyword);
            }

            if (mShopCouponList != null && mShopCouponList.size() > 0) {
                p.put("coupon_amount", String.valueOf(mShopCouponList.size()));
            }
            Utils.utCustomHit(getFullPageName(), controlName, p);
        }
    }

    /**
     * 处理切换分类
     *
     * @param currentTabNumber
     * @param currentGridview
     * @param oldTabNumber
     * @param oldGridview
     * @param change
     */

    @Override
    protected void handlerChangeTab(int currentTabNumber, FocusFlipGridView currentGridview, int oldTabNumber,
                                    FocusFlipGridView oldGridview, boolean change) {

        if (mTableListId != null && mTableListId.size() != 0) {
            AppDebug.e("TAG", "-=-=-=-=-=" + mTableListId.get(currentTabNumber));
            currentGridview.setId(mTableListId.get(currentTabNumber));

            setGridViewId(mTableListId.get(currentTabNumber));
        }

        checkTabEllipsize(currentTabNumber);

        String tabText = getTabKeyWordOfTabNumBer(currentTabNumber);
        if (TextUtils.isEmpty(tabText)) {
            return;
        }
        MenuView_Info current_Info = mGoodsMenuViewInfoMap.get(tabText);
        if (current_Info == null) {
            return;
        }

        // 处理埋点
        Map<String, String> p = getPageProperties();
        p.put("name", tabText);
        TBS.Adv.ctrlClicked(CT.Button, TAG + "_P_Cat", Utils.getKvs(p));

        // 判断是否请求数据
        if (!current_Info.HasRequestData_ChangeMenu) {

            if (DeviceJudge.MemoryType.HighMemoryDevice.equals(DeviceJudge.getMemoryType())) {
                // 针对高配版
                if (oldGridview != null) {
                    oldGridview.setVisibility(View.GONE);
                }
            } else {
                // 针对低配版
                onDestroyGridView(oldTabNumber);
            }

            if (NetWorkUtil.isNetWorkAvailable()) {

                // 如果是网络正常，那么移除监听
                removeNetworkOkDoListener();

                // 显示当前分类信息，delay 300，ms
                // 为了频繁切换产生的冲突问题，应用当前的mCurrentClassifyPosition构建一个MASSAGE，进行发送

                if (mHandler != null) {
                    Message message = mHandler.obtainMessage(WHAT_SHOP_CHANGE_CLASSIFY);
                    message.arg1 = currentTabNumber;
                    mHandler.sendMessageDelayed(message, 300);
                }

            } else {

                // 添加监听
                setNetworkOkDoListener(mShopNetworkOkDoListener);

                showNetworkErrorDialog(false);
            }

        } else {
            if (DeviceJudge.MemoryType.HighMemoryDevice.equals(DeviceJudge.getMemoryType())) {
                // 针对高配版
                if (oldGridview != null) {
                    oldGridview.stopFlip();
                    oldGridview.startOutAnimation();
                }

                if (current_Info.lifeUiGridView != null) {

                    current_Info.lifeUiGridView.bringToFront();
                    current_Info.lifeUiGridView.stopOutAnimation();
                    current_Info.lifeUiGridView.setVisibility(View.VISIBLE);
                    current_Info.lifeUiGridView.startInAnimation();

                }

            } else {
                // 针对低配版
                if (current_Info.lifeUiGridView != null) {
                    current_Info.lifeUiGridView.bringToFront();
                    current_Info.lifeUiGridView.setVisibility(View.VISIBLE);
                    if (current_Info.goodListGirdViewAdapter != null) {
                        ((TabGoodsBaseAdapter) current_Info.goodListGirdViewAdapter).onCheckVisibleItemAndLoadBitmap();
                    }

                }
                onDestroyGridView(oldTabNumber);
            }
        }

        if (current_Info.Mask_display) {
            mShadowTop.setVisibility(View.VISIBLE);
        } else {
            mShadowTop.setVisibility(View.GONE);
        }

        // 改变背景
        onShowCommonBackgroud(current_Info);

        if (current_Info.haveGoods) {
            mEmptyView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
            showGirdView(ALL_VALUE);
        }
    }

    /**
     * 获取商品 Focus的资源ID
     *
     * @return
     */
    @Override
    protected int getGoodsFocusDrawableId() {
        return R.drawable.ytbv_common_focus;
    }

    /**
     * 获取TAB Focus的资源ID
     *
     * @return
     */

    @Override
    protected int getTabFocusDrawableId() {
        return mTabFocusResId;
    }

    /**
     * 网络断开重新连接的监听类
     *
     * @author yunzhong.qyz
     */
    private static class ShopNetworkOkDoListener implements NetworkOkDoListener {

        private final WeakReference<ShopActivity> reference;

        public ShopNetworkOkDoListener(WeakReference<ShopActivity> reference) {
            this.reference = reference;
        }

        @Override
        public void todo() {
            ShopActivity shopActivity = reference.get();
            AppDebug.i(TAG, "todo --> shopActivity = " + shopActivity);
            if (shopActivity != null) {
                // 网络重连后，重新请求数据
                int currentRequest_TabPosition = shopActivity.mCurrentSelectTabNumBer;
                shopActivity.showShopItemList(currentRequest_TabPosition);
            }
        }
    }

    private static class CatInfoInShopBusinessRequestListener extends BizRequestListener<List<Cat>> {

        public CatInfoInShopBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef) {
            super(mBaseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            ShopActivity shopActivity = (ShopActivity) mBaseActivityRef.get();
            if (shopActivity != null) {
                shopActivity.OnWaitProgressDialog(false);
            }
            return false;
        }

        @Override
        public void onSuccess(List<Cat> data) {
            ShopActivity shopActivity = (ShopActivity) mBaseActivityRef.get();
            if (shopActivity != null) {
                shopActivity.OnWaitProgressDialog(false);
                shopActivity.onHandlRequestCatInfoInShop(data);
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return true;
        }
    }

    private static class ApplyCouponBusinessRequestListener extends BizRequestListener<JSONObject> {

        private int position;

        public ApplyCouponBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef, int position) {
            super(mBaseActivityRef);
            this.position = position;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            ShopActivity shopActivity = (ShopActivity) mBaseActivityRef.get();
            if (shopActivity != null) {
                shopActivity.OnWaitProgressDialog(false);
            }
            return false;
        }

        @Override
        public void onSuccess(JSONObject data) {
            ShopActivity shopActivity = (ShopActivity) mBaseActivityRef.get();
            if (shopActivity != null) {
                shopActivity.OnWaitProgressDialog(false);
                AppDebug.showToast(shopActivity, "领取成功");
                if (shopActivity.mShopCouponGirdViewAdapter != null) {
                    shopActivity.mShopCouponList.get(position).setOwnNum(1);
                    shopActivity.mShopCouponGirdViewAdapter.setCouponList(shopActivity.mShopCouponList);
                    shopActivity.mShopCouponGirdViewAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    private static class WapShopInfoBusinessRequestListener extends BizRequestListener<SellerInfo> {

        public WapShopInfoBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef) {
            super(mBaseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            ShopActivity shopActivity = (ShopActivity) mBaseActivityRef.get();
            if (shopActivity != null) {
                shopActivity.OnWaitProgressDialog(false);
            }
            return false;
        }

        @Override
        public void onSuccess(SellerInfo data) {
            ShopActivity shopActivity = (ShopActivity) mBaseActivityRef.get();
            if (shopActivity != null) {
                shopActivity.OnWaitProgressDialog(false);
                shopActivity.onHandlRequestWapShopInfo(data);
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return true;
        }

    }

    private static class ShopItemListBusinessRequestListener extends BizRequestListener<GoodsList> {

        private final MenuView_Info menuView_Info;
        private final Cat cat;

        public ShopItemListBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef,
                                                   MenuView_Info menuView_Info, Cat cat) {
            super(mBaseActivityRef);
            this.menuView_Info = menuView_Info;
            this.cat = cat;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            ShopActivity shopActivity = (ShopActivity) mBaseActivityRef.get();
            if (shopActivity != null) {
                shopActivity.mIsRequest = false;
                // 允许按返回键取消进度条
                shopActivity.setProgressCancelable(true);
                shopActivity.OnWaitProgressDialog(false);
            }
            return false;
        }

        @Override
        public void onSuccess(GoodsList data) {
            ShopActivity shopActivity = (ShopActivity) mBaseActivityRef.get();
            if (shopActivity != null) {
                shopActivity.onHandlRequestShopItemList(data, menuView_Info, cat);
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return true;
        }
    }

    private static class ShopCouponListBusinessRequestListener extends BizRequestListener<List<ShopCoupon>> {

        public ShopCouponListBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef) {
            super(mBaseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            ShopActivity shopActivity = (ShopActivity) mBaseActivityRef.get();
            if (shopActivity != null) {
                shopActivity.mIsRequest = false;
                // 允许按返回键取消进度条
                shopActivity.setProgressCancelable(true);
                shopActivity.OnWaitProgressDialog(false);

                // 获取店铺的分类
                shopActivity.getCatInfoInShop(shopActivity.mSellerId, shopActivity.mShopId);
            }
            return true;
        }

        @Override
        public void onSuccess(List<ShopCoupon> data) {
            ShopActivity shopActivity = (ShopActivity) mBaseActivityRef.get();
            if (shopActivity != null) {
                shopActivity.mIsRequest = false;
                // 允许按返回键取消进度条
                shopActivity.setProgressCancelable(true);
                shopActivity.OnWaitProgressDialog(false);

                shopActivity.mShopCouponList = data;

                // 获取店铺的分类
                shopActivity.getCatInfoInShop(shopActivity.mSellerId, shopActivity.mShopId);
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return true;
        }
    }

    private static final class ShopHandler extends Handler {

        private final WeakReference<ShopActivity> weakReference;

        public ShopHandler(WeakReference<ShopActivity> weakReference) {
            this.weakReference = weakReference;
        }

        public void handleMessage(android.os.Message msg) {

            ShopActivity shopActivity = weakReference.get();

            if (shopActivity != null) {

                switch (msg.what) {
                    case WHAT_SHOP_CHANGE_CLASSIFY:
                        shopActivity.showShopItemList(msg.arg1);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> p = super.getPageProperties();
        if (!TextUtils.isEmpty(mShopId)) {
            p.put("shop_id", mShopId);
        }
        if (!TextUtils.isEmpty(mSellerId)) {
            p.put("seller_id", mSellerId);
        }
        p.put(SPMConfig.SPM_CNT, "a2o0j.7984572.0.0");
        return p;
    }

    private void setGridViewId(int id) {
        fiv_pierce_home_focusd.setNextFocusLeftId(id);
        fiv_pierce_my_focusd.setNextFocusLeftId(id);
        fiv_pierce_cart_focusd.setNextFocusLeftId(id);
        fiv_pierce_red_packet_focusd.setNextFocusLeftId(id);
        fiv_pierce_block_focusd.setNextFocusLeftId(id);
        fiv_pierce_contact_focusd.setNextFocusLeftId(id);
        fiv_pierce_come_back_focusd.setNextFocusLeftId(id);
        fiv_pierce_red_jifen_focusd.setNextFocusLeftId(id);
    }

    private void onSetPierceActivityListener() {
        lProperties.put("built_in", "shop_home");

        setOnPierceItemFocusdListener();

        setOnPierceItemClickListener();

        Utils.utPageAppear("Page_tool", "Page_tool");
    }

    /**
     * 设置右侧穿透点击效果
     */
    private void setOnPierceItemClickListener() {
        fiv_pierce_home_focusd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lProperties.put("controlname", "home");
                Utils.utControlHit(getFullPageName(), "Page_tool_button_home", lProperties);
                Intent intent = new Intent();
                intent.setClassName(ShopActivity.this, BaseConfig.SWITCH_TO_HOME_ACTIVITY);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                //setFrom("");
                startActivity(intent);
            }
        });

        fiv_pierce_my_focusd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lProperties.put("controlname", "TbMyTaoBao");
                Utils.utControlHit(getFullPageName(), "Page_tool_button_TbMyTaoBao", lProperties);
                Intent intent = new Intent();
                intent.setClassName(ShopActivity.this, BaseConfig.SWITCH_TO_MYTAOBAO_ACTIVITY);
                intent.putExtra(ActivityPathRecorder.INTENTKEY_FIRST, true);
                //setFrom("");
                startActivity(intent);
            }
        });
        fiv_pierce_cart_focusd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lProperties.put("controlname", "Cart");
                Utils.utControlHit(getFullPageName(), "Page_tool_button_Cart", lProperties);
                Intent intent = new Intent();
                intent.setClassName(ShopActivity.this, BaseConfig.SWITCH_TO_SHOPCART_LIST_ACTIVITY);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra(ActivityPathRecorder.INTENTKEY_FIRST, true);
                //setFrom("");
                startActivity(intent);
            }
        });
        fiv_pierce_red_packet_focusd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lProperties.put("controlname", "red");
                Utils.utControlHit(getFullPageName(), "Page_tool_button_red", lProperties);
                Intent intent = new Intent();
                intent.setClassName(ShopActivity.this, BaseConfig.SWITCH_TO_COUPON_ACTIVITY);
                intent.putExtra(ActivityPathRecorder.INTENTKEY_FIRST, true);
                //setFrom("");
                startActivity(intent);

            }
        });
        fiv_pierce_block_focusd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lProperties.put("controlname", "coupon");
                Utils.utControlHit(getFullPageName(), "Page_tool_button_coupon", lProperties);
                Intent intent = new Intent();
                intent.setClassName(ShopActivity.this, BaseConfig.SWITCH_TO_COUPON_ACTIVITY);
                intent.putExtra(ActivityPathRecorder.INTENTKEY_FIRST, true);
                //setFrom("");
                startActivity(intent);

            }
        });
        fiv_pierce_contact_focusd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lProperties.put("controlname", "phone");
                Utils.utControlHit(getFullPageName(), "Page_tool_button_phone", lProperties);
                tv_pierce_contact_focusd.setText("周一至周五 9:30~18:30");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tv_pierce_contact_focusd.setText("客服:0571-85135297");
                    }
                }, 3000);
            }
        });

        fiv_pierce_red_jifen_focusd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lProperties.put("controlname", "integral");
                Utils.utControlHit(getFullPageName(), "Page_tool_button_integral", lProperties);
                Intent intent = new Intent();
                intent.setClassName(ShopActivity.this, BaseConfig.SWITCH_TO_POINT_ACTIVITY);
                startActivity(intent);
            }
        });
    }

    /**
     * 设置右侧穿透聚焦效果
     */
    private void setOnPierceItemFocusdListener() {
        AppDebug.e("TAG", "-------fiv_pierce_home_focusd--------用前");
        fiv_pierce_home_focusd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                lProperties.put("controlname", "home");
                Utils.utCustomHit(getFullPageName(), "tool_button_focus_home", lProperties);
                if (hasFocus) {
                    tv_pierce_home.setVisibility(View.VISIBLE);
                    mTabFocusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(
                            R.drawable.ytm_touming)));
                } else {
                    tv_pierce_home.setVisibility(View.INVISIBLE);
                }
            }
        });
        AppDebug.e("TAG", "-------fiv_pierce_home_focusd--------用后");

        fiv_pierce_my_focusd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                lProperties.put("controlname", "TbMyTaoBao");
                Utils.utCustomHit(getFullPageName(), "tool_button_focus_TbMyTaoBao", lProperties);
                if (hasFocus) {
                    tv_pierce_my.setVisibility(View.VISIBLE);
                    if (CoreApplication.getLoginHelper(ShopActivity.this).isLogin()) {
                        tv_pierce_my.setText(User.getNick());
                    } else {
                        String myTaobaoStr = getResources().getString(R.string.ytbv_pierce_my_taobao);
                        tv_pierce_my.setText(myTaobaoStr);
                    }
                    mTabFocusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(
                            R.drawable.ytm_touming)));
                } else {
                    tv_pierce_my.setVisibility(View.INVISIBLE);
                }
            }
        });

        fiv_pierce_cart_focusd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                lProperties.put("controlname", "Cart");
                Utils.utCustomHit(getFullPageName(), "tool_button_focus_Cart", lProperties);
                AppDebug.e(TAG, "ShopActivity---> 0");
                if (globalConfig != null) {
                    AppDebug.e(TAG, "ShopActivity---> 1");
                    GlobalConfig.ShopCartFlag shopCartFlag = globalConfig.getShopCartFlag();
                    AppDebug.e("TAG", "shopCartFlag==" + shopCartFlag);
                    if (shopCartFlag != null && shopCartFlag.isActing()) {
                        lProperties.put("name", "购物车气泡");
                        Utils.utControlHit(getFullPageName(), "Expose_tool_cartbubble", lProperties);
                        if (hasFocus) {
                            fiv_pierce_cart_focusd.setImageResource(R.drawable.goodlist_fiv_pierce_cart_focus);
                            mTabFocusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(
                                    R.drawable.ytm_touming)));
                            iv_pierce_cart_active.setVisibility(View.VISIBLE);
                        } else {
                            fiv_pierce_cart_focusd.setImageDrawable(null);
                            iv_pierce_cart_active.setVisibility(View.INVISIBLE);
                        }
                    }
                } else {
                    AppDebug.e(TAG, "ShopActivity---> 2");
                    if (hasFocus) {
                        mTabFocusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(
                                R.drawable.ytm_touming)));
                        AppDebug.e(TAG, "ShopActivity---> 3");
                        tv_pierce_cart.setVisibility(View.VISIBLE);
                    } else {
                        AppDebug.e(TAG, "ShopActivity---> 4");
                        tv_pierce_cart.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        fiv_pierce_red_packet_focusd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                lProperties.put("controlname", "red");
                Utils.utCustomHit(getFullPageName(), "tool_button_focus_red", lProperties);
                if (hasFocus) {
                    mTabFocusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(
                            R.drawable.ytm_touming)));
                    tv_pierce_red_packet.setVisibility(View.VISIBLE);
                } else {
                    tv_pierce_red_packet.setVisibility(View.INVISIBLE);

                }
            }
        });

        fiv_pierce_block_focusd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                lProperties.put("controlname", "coupon");
                Utils.utCustomHit(getFullPageName(), "tool_button_focus_coupon", lProperties);
                if (hasFocus) {
                    mTabFocusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(
                            R.drawable.ytm_touming)));
                    tv_pierce_block.setVisibility(View.VISIBLE);
                } else {
                    tv_pierce_block.setVisibility(View.INVISIBLE);

                }
            }
        });

        fiv_pierce_contact_focusd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                lProperties.put("controlname", "phone");
                Utils.utCustomHit(getFullPageName(), "tool_button_focus_phone", lProperties);
                if (hasFocus) {
                    mTabFocusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(
                            R.drawable.ytm_touming)));
                    tv_pierce_contact_focusd.setVisibility(View.VISIBLE);
                } else {
                    tv_pierce_contact_focusd.setVisibility(View.INVISIBLE);

                }
            }
        });

        fiv_pierce_come_back_focusd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    mTabFocusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(
                            R.drawable.ytm_touming)));
                    tv_pierce_come_back.setVisibility(View.VISIBLE);
                } else {
                    tv_pierce_come_back.setVisibility(View.INVISIBLE);

                }
            }
        });

        fiv_pierce_red_jifen_focusd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                lProperties.put("controlname", "integral");
                Utils.utCustomHit(getFullPageName(), "tool_button_focus_integral", lProperties);
                if (hasFocus) {
                    mTabFocusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(
                            R.drawable.ytm_touming)));
                    tv_pierce_red_jifen.setVisibility(View.VISIBLE);
                } else {
                    tv_pierce_red_jifen.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CoreApplication.getLoginHelper(ShopActivity.this).isLogin()) {
            tv_pierce_my.setText(User.getNick());
        } else {
            String myTaobaoStr = getResources().getString(R.string.ytbv_pierce_my_taobao);
            tv_pierce_my.setText(myTaobaoStr);
        }
    }

    //    private class GetHasActiviteRequestListener extends BizRequestListener<String> {
//
//        public GetHasActiviteRequestListener(WeakReference<BaseActivity> baseActivityRef) {
//            super(baseActivityRef);
//        }
//
//        @Override
//        public boolean onError(int resultCode, String msg) {
//            return false;
//        }
//
//        @Override
//        public void onSuccess(String data) {
//            AppDebug.e("TAG", "data" + data);
//
//            HasActivieBean hasActivieBean = GsonUtil.parseJson(data, new TypeToken<HasActivieBean>() {
//            });
//            hasActive = hasActivieBean.getShop_cart_flag().isIs_acting();
//
//            activeName = hasActivieBean.getUpgrade().getContent();
//
//            String side_bar_icon_url = hasActivieBean.getShop_cart_flag().getSide_bar_icon();
//
//            ImageLoaderManager.getImageLoaderManager(ShopActivity.this).displayImage(side_bar_icon_url,iv_pierce_cart_active);
//
//            //判断是否有活动
//            if (hasActive) {
//                lProperties.put("name",activeName);
//                Utils.utControlHit(getFullPageName(),"Expore_tool_cartbubble",lProperties);
//                iv_pierce_cart_active.setVisibility(View.VISIBLE);
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        iv_pierce_cart_active.setVisibility(View.INVISIBLE);
//                    }
//                }, 5000);
//            }
//        }
//
//        @Override
//        public boolean ifFinishWhenCloseErrorDialog() {
//            return false;
//        }
//    }

    /**
     * 得到是否有活动
     */
    private void isHasActivityFromNet() {
        GlobalConfig globalConfig = GlobalConfigInfo.getInstance().getGlobalConfig();
        if (globalConfig != null) {
            GlobalConfig.ShopCartFlag shopCartFlag = globalConfig.getShopCartFlag();
            if (shopCartFlag != null && shopCartFlag.isActing()) {

                hasActive = shopCartFlag.isActing();
                fiv_pierce_background.setBackgroundResource(R.drawable.goodlist_fiv_pierce_bg_d11);
//                fiv_pierce_cart_focusd.setImageResource(R.drawable.goodlist_fiv_pierce_cart_focus);
                String sideBarImgUrl = shopCartFlag.getSideBarIcon();
                ImageLoaderManager.getImageLoaderManager(ShopActivity.this).displayImage(sideBarImgUrl, iv_pierce_cart_active);
                iv_pierce_cart_active.setVisibility(View.VISIBLE);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        iv_pierce_cart_active.setVisibility(View.INVISIBLE);
                    }
                }, 5000);
                //判断是否有活动"
                lProperties.put("name", "购物车气泡");
                Utils.utControlHit(getFullPageName(), "Expose_tool_cartbubble", lProperties);

            }
        }
    }

    @Override
    public Uri getCurrentUri() {
        if (super.getCurrentUri() == null) {
            return generateDefaultUri();
        }
        return super.getCurrentUri();
    }

    /**
     * tvtaobao://home?app=taobaosdk&module=shop&shopId=60129786&from=店铺分会场-ONLY官方旗舰店
     *
     * @return
     */
    private Uri generateDefaultUri() {
        String s = String.format("tvtaobao://home?module=shop&shopId=%s&from=%s",
                getIntent().getStringExtra(BaseConfig.SELLER_SHOPID),
                getmFrom());
        return Uri.parse(s);
    }
}
