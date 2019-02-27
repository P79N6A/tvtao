package com.yunos.tvtaobao.takeoutbundle.activity;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.tvlife.imageloader.utils.ClassicOptions;
import com.tvtaobao.voicesdk.ASRNotify;
import com.tvtaobao.voicesdk.base.SDKInitConfig;
import com.tvtaobao.voicesdk.bo.DomainResultVo;
import com.tvtaobao.voicesdk.interfaces.ASRSearchHandler;
import com.tvtaobao.voicesdk.tts.TTSUtils;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.focus_impl.ConsumerB;
import com.yunos.tvtaobao.biz.focus_impl.FakeListView;
import com.yunos.tvtaobao.biz.focus_impl.FocusArea;
import com.yunos.tvtaobao.biz.focus_impl.FocusConsumer;
import com.yunos.tvtaobao.biz.focus_impl.FocusFakeListView;
import com.yunos.tvtaobao.biz.focus_impl.FocusNode;
import com.yunos.tvtaobao.biz.focus_impl.FocusUtils;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.ItemListBean;
import com.yunos.tvtaobao.biz.request.bo.ShopDetailData;
import com.yunos.tvtaobao.biz.request.bo.ShopSearchResultBean;
import com.yunos.tvtaobao.biz.request.bo.TakeOutBag;
import com.yunos.tvtaobao.biz.request.bo.VouchersMO;
import com.yunos.tvtaobao.biz.widget.CustomDialogShow;
import com.yunos.tvtaobao.biz.widget.newsku.TradeType;
import com.yunos.tvtaobao.takeoutbundle.R;
import com.yunos.tvtaobao.takeoutbundle.activity.shophome_wares.DataCenter;
import com.yunos.tvtaobao.takeoutbundle.activity.shophome_wares.GoodsKindListAdapter;
import com.yunos.tvtaobao.takeoutbundle.activity.shophome_wares.RightMenuViewHolder;
import com.yunos.tvtaobao.takeoutbundle.activity.shophome_wares.ShopCartItemVH;
import com.yunos.tvtaobao.takeoutbundle.activity.shophome_wares.ShopCartListAdapter;
import com.yunos.tvtaobao.takeoutbundle.activity.shophome_wares.ShopGoodItemVH;
import com.yunos.tvtaobao.takeoutbundle.activity.shophome_wares.ShopGoodsListAdapter;
import com.yunos.tvtaobao.takeoutbundle.h5.plugin.TakeOutPayBackPlugin;
import com.yunos.tvtaobao.takeoutbundle.view.PromptPop;
import com.yunos.tvtaobao.takeoutbundle.view.SkuSelectDialog;
import com.yunos.tvtaobao.takeoutbundle.view.VouchersDialog;
import com.yunos.tvtaobao.takeoutbundle.widget.CardView;
import com.yunos.tvtaobao.takeoutbundle.widget.ShopCartActionView;
import com.yunos.tvtaobao.takeoutbundle.widget.WeakDataHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by chenjiajuan on 17/12/10.
 *
 * @describe 店铺首页
 */

public class TakeOutShopHomeActivity extends BaseActivity implements ASRSearchHandler {
    private static final int TAKE_OUT_SHOP_CART_ACTIVITY_FOR_RESULT_CODE = 1;
    private static final String TAG = "TakeOutShopHomeActivity";
    private final static DecimalFormat format = new DecimalFormat("¥ #.##");
    private static String shopId;                                          //门店id
    private String serviceId;                                       //服务id
    private static String scm;                                             //未知
    private String from;                                            //来源
    private static String outStoreId;

    private DataCenter.Location location;
    private static boolean isShopOpen = false;                             //是否开张店铺
    private BusinessRequest businessRequest;                        //网络请求

    private DataCenter dataCenter;
    public static boolean canAddClick = true;

    private FocusArea flVouchers;

    // 右侧导航栏
    RightMenuViewHolder rightMenuViewHolder;

    // 店铺信息
    private CardView card_view;
    private ImageView shop_icon_pic;
    private TextView tvVoiceTips;               //语音提示

    // 商品分类列表
    private FocusFakeListView good_kind;
    private GoodsKindListAdapter good_kind_adapter;

    // 商品列表
    private FocusFakeListView good_all;
    private ShopGoodsListAdapter goodsListAdapter;
    private View shop_empty;

    // 购物车
    private FrameLayout right_area;
    private ImageView right_area_focus_status;
    private FocusArea collection_focus;
    private FocusFakeListView good_collection_recycle;
    private ShopCartListAdapter good_collectionMultipleRecycleAdapter;

    private TextView collection_pay;
    private TextView good_collection_pay_ori;
    private TextView collection_pay_dlive_amount;
    private ImageView good_collection_empty;
    private View img_shop_closed_tag;
    private TextView txt_start_price;
    private TextView txt_start_price_tag;
    private LinearLayout shop_cart_container;
    private ShopCartActionView mShopCartActionView;


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
                    if (dataCenter != null) {
                        dataCenter.clear(new DataCenter.CallBackImpl() {
                            @Override
                            public void onSuccess(Object data) {
                                super.onSuccess(data);
                                qryShoppingCart();
                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private ASRNotify mNotify;
    private static String ttsText;
    private static List<String> tipsList = new ArrayList<>();
    private static int tipsShowCount;
    private static String spokenText;
    private ShopDetailData shopDetailData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_out_shop_home);
        EventBus.getDefault().register(this);

        getFocusManager().enable(true);

        shopId = getIntent().getStringExtra(BaseConfig.INTENT_KEY_SHOPID);
        serviceId = getIntent().getStringExtra(BaseConfig.INTENT_KEY_SERVIECID);
        serviceId = TextUtils.isEmpty(serviceId) ? "62" : serviceId;
        scm = getIntent().getStringExtra(BaseConfig.INTENT_KEY_SCM);
        from = getIntent().getStringExtra("v_from");//语音跳转进来
        if (TextUtils.isEmpty(from)) {
            from = getIntent().getStringExtra(BaseConfig.INTENT_KEY_FROM);
        }
        if ("voice_system".equals(from) || "voice_application".equals(from)) {
            TTSUtils.getInstance().showDialog(this, 1);
        }
        tipsShowCount = SharePreferences.getInt("tipsShowCount");
        String loc = SharePreferences.getString("location");

        if (TextUtils.isEmpty(loc)) {
            loc = getIntent().getStringExtra("location");
        }
        if (loc != null) {
            location = JSON.parseObject(loc, DataCenter.Location.class);
        }

        dataCenter = new DataCenter(this, shopId, serviceId, location);

        if (!TextUtils.isEmpty(shopId)) {
            AppDebug.e(TAG, "shopId = " + shopId);
        }

        if (!TextUtils.isEmpty(serviceId)) {
            AppDebug.e(TAG, "serviceId = " + serviceId);
        }
        if (!TextUtils.isEmpty(scm)) {
            AppDebug.e(TAG, "scm = " + scm);
        }
        if (!TextUtils.isEmpty(from)) {
            AppDebug.e(TAG, " from = " + from);
        }
        mNotify = ASRNotify.getInstance();
        businessRequest = BusinessRequest.getBusinessRequest();

        shop_icon_pic = (ImageView) findViewById(R.id.shop_icon_pic);
        good_collection_pay_ori = (TextView) findViewById(R.id.collection_pay_ori);
        collection_pay = (TextView) findViewById(R.id.collection_pay);
        collection_pay_dlive_amount = (TextView) findViewById(R.id.collection_pay_dlive_amount);
        img_shop_closed_tag = findViewById(R.id.img_shop_closed_tag);
        txt_start_price = (TextView) findViewById(R.id.txt_start_price);
        txt_start_price_tag = (TextView) findViewById(R.id.txt_start_price_tag);
        shop_cart_container = (LinearLayout) findViewById(R.id.shop_cart_container);
        flVouchers = (FocusArea) this.findViewById(R.id.fl_vouchers);
        collection_focus = (FocusArea) findViewById(R.id.collection_focus);
        right_area = (FrameLayout) findViewById(R.id.right_area);
        right_area_focus_status = (ImageView) findViewById(R.id.right_area_focus_status);
        good_collection_recycle = (FocusFakeListView) findViewById(R.id.good_collection_recycle);
        good_collection_empty = (ImageView) findViewById(R.id.good_collection_empty);
        shop_empty = findViewById(R.id.shop_empty);
        tvVoiceTips = (TextView) findViewById(R.id.tv_voice_tips);
        card_view = (CardView) findViewById(R.id.card_view);
        if (tipsShowCount > 3) {
            tvVoiceTips.setVisibility(View.GONE);
        }

        // 处理商品种类列表
        good_kind = (FocusFakeListView) findViewById(R.id.good_differ);
        good_kind.setStyle(FakeListView.Style.edgeLimit);
        good_kind_adapter = new GoodsKindListAdapter(this);
        good_kind.setAdapter(good_kind_adapter);

        // 处理商品列表 and 提示商品没有更多了
        good_all = (FocusFakeListView) findViewById(R.id.good_all);
        good_all.setStyle(FakeListView.Style.edgeLimit);
        good_all.setGravity(Gravity.TOP);
        good_all.setItemMargin((int) getResources().getDimension(R.dimen.dp_16));
        goodsListAdapter = new ShopGoodsListAdapter(this);
        good_all.setAdapter(goodsListAdapter);

        // 处理优惠券
        flVouchers.setVisibility(View.GONE);

        // 购物车
        collection_focus.setFocusConsumer(new ConsumerB() {
            @Override
            public boolean onFocusEnter() {
                right_area_focus_status.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public boolean onFocusLeave() {
                right_area_focus_status.setVisibility(View.GONE);
                return true;
            }
        });

        good_collection_recycle.setFocusConsumer(new ConsumerB() {
            @Override
            public boolean onFocusClick() {
                if (shopDetailData != null) {
                    String shopStatus = shopDetailData == null || shopDetailData.getStoreDetailDTO() == null ? "" : shopDetailData.getStoreDetailDTO().getShopStatus();
                    if ("RESTING".equals(shopStatus)) {
                        CustomDialogShow.onPromptDialog(getString(R.string.shop_resting_hint), TakeOutShopHomeActivity.this);
                        return true;

                    }
                }

                if (good_collectionMultipleRecycleAdapter != null &&
                        good_collectionMultipleRecycleAdapter.getDataList().size() == 0) {
                    CustomDialogShow.onPromptDialog(getString(R.string.shop_cart_empty_hint), TakeOutShopHomeActivity.this);
                    return true;

                }
                return true;
            }
        });
        collection_focus.getNode().setRoutineCallBack(new FocusNode.RCBImpl() {
            @Override
            public void onPreFindNext() {
                FocusArea focusArea = mShopCartActionView.getmContainerView();
                if (focusArea != null) {
                    if (collection_focus.getNode().getFocusEnterCount() == 0) {
                        focusArea.getNode().setPriority(System.currentTimeMillis());
                    } else {
                        focusArea.getNode().setPriority(0);
                    }
                }
            }
        });
        good_collectionMultipleRecycleAdapter = new ShopCartListAdapter(this);
        good_collection_recycle.setAdapter(good_collectionMultipleRecycleAdapter);
        good_collection_recycle.setItemMargin((int) getResources().getDimension(R.dimen.dp_4));
        good_collection_recycle.setReverseLayoutFlag(true);
        good_collection_recycle.setStyle(FakeListView.Style.edgeLimit);
        good_collection_recycle.setGravity(Gravity.BOTTOM);
        good_collection_recycle.setLayoutDoneListener(new FakeListView.LayoutDoneListener() {
            @Override
            public void onLayoutDone(int allItemsHeight, int viewHeight) {
                ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) right_area.getLayoutParams();
                float minMarginTop = getResources().getDimension(R.dimen.dp_32);
                float maxMarginTop = getResources().getDimension(R.dimen.dp_156);
                float nowMarginTop = lp.topMargin;
                if (allItemsHeight > viewHeight) {
                    int range = (int) (nowMarginTop - minMarginTop);
                    if (range > 0) {
                        int diff = allItemsHeight - viewHeight;
                        if (diff > range) {
                            diff = range;
                        }
                        lp.topMargin -= diff;
                        right_area.setLayoutParams(lp);
                    }
                } else if (allItemsHeight < viewHeight) {
                    int range = (int) (maxMarginTop - nowMarginTop);
                    if (range > 0) {
                        int diff = viewHeight - allItemsHeight;
                        if (diff > range) {
                            diff = range;
                        }
                        lp.topMargin += diff;
                        right_area.setLayoutParams(lp);
                    }
                }
            }
        });
        good_collection_pay_ori.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

        //购物车去结算区域
        mShopCartActionView = (ShopCartActionView) findViewById(R.id.shop_cart_action_view);

        // js 桥
        takeOutPayBackPlugin.onCreate(savedInstanceState);

        // 拉取店铺数据
        dataCenter.qryShopHomeDetail(new DataCenter.CallBackImpl() {
            @Override
            public void onSuccess(Object data) {
                super.onSuccess(data);
                loadPageData(dataCenter.getShopDetailData());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showVoiceTips();
                    }
                }, 4000);
            }
        });

        //layout.requestFocus();
        initToolsView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        canAddClick = true;
        mNotify.setHandlerSearch(this);
        if (!isFirstResume()) {
            qryShoppingCart();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNotify.setHandlerSearch(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        SkuSelectDialog.getSkuDialogInstance(this, getFullPageName()).destroySkuDialog();
        takeOutPayBackPlugin.onDestroy();
        PromptPop.destory();
        if (rightMenuViewHolder != null) {
            rightMenuViewHolder.destroy();
        }
    }

    /**
     * 侧边栏跳转
     */
    private void initToolsView() {
        final Map<String, String> properties = Utils.getProperties();
        properties.put("shop id", shopId);
        properties.put("uuid", CloudUUIDWrapper.getCloudUUID());

        if (rightMenuViewHolder == null) {
            rightMenuViewHolder = new RightMenuViewHolder(this, (ViewGroup) findViewById(R.id.menu_place_holder));

            //详情按钮
            final RightMenuViewHolder.MenuItem menuItem1 = new RightMenuViewHolder.MenuItem();
            menuItem1.tipTxt = "详情";
            menuItem1.menuItemView = new ImageView(this);
            menuItem1.menuItemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                    , (int) getResources().getDimension(R.dimen.dp_101))); // dp_101
            ((ImageView) (menuItem1.menuItemView)).setAdjustViewBounds(true);
            ((ImageView) (menuItem1.menuItemView)).setImageResource(R.drawable.icon_takeout_detail_unselect);
            menuItem1.focusConsumer = new FocusConsumer() {
                @Override
                public boolean onFocusEnter() {
                    ((ImageView) (menuItem1.menuItemView)).setImageResource(R.drawable.icon_takeout_detail);
                    return false;
                }

                @Override
                public boolean onFocusLeave() {
                    ((ImageView) (menuItem1.menuItemView)).setImageResource(R.drawable.icon_takeout_detail_unselect);
                    return false;
                }

                @Override
                public boolean onFocusClick() {
                    AppDebug.e(TAG, "flDetail.onClick");
                    if (dataCenter.getShopDetailData() == null || dataCenter.getShopDetailData().getStoreDetailDTO() == null) {
                        PromptPop.getInstance(TakeOutShopHomeActivity.this).showPromptWindow("获取商家信息失败");
                        return false;
                    }
                    Intent intent = new Intent(TakeOutShopHomeActivity.this, TakeOutShopDetailActivity.class);
                    intent.putExtra("storeDetailDTOBean", dataCenter.getShopDetailData().getStoreDetailDTO());
                    intent.putExtra("pageName", getFullPageName());
                    properties.put("spm", SPMConfig.WAIMAI_SHOP_SLIDE_DETAILS);
                    Utils.utControlHit(getFullPageName(), "Page_waimai_shop_sidebar_button_details", properties);
                    Utils.updateNextPageProperties(SPMConfig.WAIMAI_SHOP_SLIDE_DETAILS);

                    startActivity(intent);
                    return true;
                }

                @Override
                public boolean onFocusDraw(Canvas canvas) {
                    return false;
                }
            };

            //订单按钮
            final RightMenuViewHolder.MenuItem menuItem2 = new RightMenuViewHolder.MenuItem();
            menuItem2.tipTxt = "外卖订单";
            menuItem2.menuItemView = new ImageView(this);
            menuItem2.menuItemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                    , (int) getResources().getDimension(R.dimen.dp_101))); // dp_101
            ((ImageView) (menuItem2.menuItemView)).setAdjustViewBounds(true);
            ((ImageView) (menuItem2.menuItemView)).setImageResource(R.drawable.icon_takeout_orders_unselect);
            menuItem2.focusConsumer = new FocusConsumer() {
                @Override
                public boolean onFocusEnter() {
                    ((ImageView) (menuItem2.menuItemView)).setImageResource(R.drawable.icon_takeout_orders);
                    return false;
                }

                @Override
                public boolean onFocusLeave() {
                    ((ImageView) (menuItem2.menuItemView)).setImageResource(R.drawable.icon_takeout_orders_unselect);
                    return false;
                }

                @Override
                public boolean onFocusClick() {
                    AppDebug.e(TAG, "flOrders.onClick");
                    String url = "tvtaobao://home?app=takeout&module=takeOutOrderList";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    properties.put("spm", SPMConfig.WAIMAI_SHOP_SLIDE_ORDER);
                    Utils.utControlHit(getFullPageName(), "Page_waimai_shop_sidebar_button_order", properties);
                    Utils.updateNextPageProperties(SPMConfig.WAIMAI_SHOP_SLIDE_ORDER);

                    startActivity(intent);
                    return true;
                }

                @Override
                public boolean onFocusDraw(Canvas canvas) {
                    return false;
                }
            };

            //我的按钮
            final RightMenuViewHolder.MenuItem menuItem3 = new RightMenuViewHolder.MenuItem();
            menuItem3.tipTxt = "我的外卖";
            menuItem3.menuItemView = new ImageView(this);
            menuItem3.menuItemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                    , (int) getResources().getDimension(R.dimen.dp_101))); // dp_101
            ((ImageView) (menuItem3.menuItemView)).setAdjustViewBounds(true);
            ((ImageView) (menuItem3.menuItemView)).setImageResource(R.drawable.icon_takeout_user_unselect);
            menuItem3.focusConsumer = new FocusConsumer() {
                @Override
                public boolean onFocusEnter() {
                    ((ImageView) (menuItem3.menuItemView)).setImageResource(R.drawable.icon_takeout_user);
                    return false;
                }

                @Override
                public boolean onFocusLeave() {
                    ((ImageView) (menuItem3.menuItemView)).setImageResource(R.drawable.icon_takeout_user_unselect);
                    return false;
                }

                @Override
                public boolean onFocusClick() {
                    AppDebug.e(TAG, "flUsers.onClick");
                    String url = "tvtaobao://home?module=common&page=http://tvos.taobao.com/wow/yunos/act/tvtaobao-waimai-user";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    properties.put("spm", SPMConfig.WAIMAI_SHOP_SLIDE_MY);
                    Utils.utControlHit(getFullPageName(), "Page_waimai_shop_sidebar_button_my", properties);
                    Utils.updateNextPageProperties(SPMConfig.WAIMAI_SHOP_SLIDE_MY);
                    startActivity(intent);
                    return true;
                }

                @Override
                public boolean onFocusDraw(Canvas canvas) {
                    return false;
                }
            };

            //首页按钮
            final RightMenuViewHolder.MenuItem menuItem4 = new RightMenuViewHolder.MenuItem();
            menuItem4.tipTxt = "返回首页";
            menuItem4.menuItemView = new ImageView(this);
            menuItem4.menuItemView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                    , (int) getResources().getDimension(R.dimen.dp_57))); // dp_101
            ((ImageView) (menuItem4.menuItemView)).setAdjustViewBounds(true);
            ((ImageView) (menuItem4.menuItemView)).setImageResource(R.drawable.icon_takeout_first_unselect);
            menuItem4.focusConsumer = new FocusConsumer() {
                @Override
                public boolean onFocusEnter() {
                    ((ImageView) (menuItem4.menuItemView)).setImageResource(R.drawable.icon_takeout_first);
                    return false;
                }

                @Override
                public boolean onFocusLeave() {
                    ((ImageView) (menuItem4.menuItemView)).setImageResource(R.drawable.icon_takeout_first_unselect);
                    return false;
                }

                @Override
                public boolean onFocusClick() {
                    AppDebug.e(TAG, "flFirst.onClick");
                    String url = "tvtaobao://home?module=common&page=http://tvos.taobao.com/wow/yunos/act/tvtaobao-waimai";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    properties.put("spm", SPMConfig.WAIMAI_SHOP_SLIDE_GO_HOME);
                    Utils.utControlHit(getFullPageName(), "Page_waimai_shop_sidebar_button_waimaihome", properties);
                    Utils.updateNextPageProperties(SPMConfig.WAIMAI_SHOP_SLIDE_GO_HOME);
                    startActivity(intent);
                    return true;
                }

                @Override
                public boolean onFocusDraw(Canvas canvas) {
                    return false;
                }
            };
            rightMenuViewHolder.fillMenu(menuItem1, menuItem2, menuItem3, menuItem4);
        }
    }

    /**
     * 加载店铺数据
     *
     * @param shopDetailData
     */
    private void loadPageData(ShopDetailData shopDetailData) {
        this.shopDetailData = shopDetailData;
        if (shopDetailData.getStoreDetailDTO() != null)
            this.outStoreId = shopDetailData.getStoreDetailDTO().getOutId();
        if (shopDetailData != null && shopDetailData.getVoucher() != null &&
                !TextUtils.isEmpty(shopDetailData.getVoucher().getStatus())
                && shopDetailData.getVoucher().getStatus().equals("1")) {
            flVouchers.setVisibility(View.VISIBLE);
            flVouchers.setFocusConsumer(new ConsumerB() {
                @Override
                public boolean onFocusEnter() {
                    flVouchers.startAnimation(AnimationUtils.loadAnimation(TakeOutShopHomeActivity.this,
                            R.anim.tv_vouchers_big));
                    flVouchers.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_vouchers_focus));
                    return true;
                }

                @Override
                public boolean onFocusLeave() {
                    flVouchers.clearAnimation();
                    flVouchers.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_vouchers_unfocus));
                    return true;
                }

                @Override
                public boolean onFocusClick() {
                    if (!TextUtils.isEmpty(shopId)) {
                        //代金券点击事件
                        utVouchersClick();
                        ShopVouchersListActivity.startActivity(TakeOutShopHomeActivity.this, shopId);
                    }
                    return true;
                }
            });
            //代金券按钮-曝光事件
            utVouchersExposure();
        } else {
            flVouchers.setVisibility(View.GONE);
            flVouchers.setFocusable(false);
        }


        // todo 店铺状态 休息等
        String shopStatus = shopDetailData == null || shopDetailData.getStoreDetailDTO() == null ? "" : shopDetailData.getStoreDetailDTO().getShopStatus();
        shopStatus = shopStatus == null ? "" : shopStatus;
        switch (shopStatus) {
            case "BOOKING":    // 预定
                shop_cart_container.setVisibility(View.VISIBLE);
                isShopOpen = true;
                break;
            case "RESTING":    // 休息
                mShopCartActionView.setData(null, null, null);
                mShopCartActionView.setVisibility(View.GONE);
                img_shop_closed_tag.setVisibility(View.VISIBLE);
                isShopOpen = false;
                shop_cart_container.setVisibility(View.GONE);

                if (shopDetailData == null || shopDetailData.getItemGenreWithItemsList() == null) {
                    break;
                }
                for (ShopDetailData.ItemGenreWithItemsListBean itemGenreWithItemsListBean : shopDetailData.getItemGenreWithItemsList()) {
                    List<ItemListBean> itemList = itemGenreWithItemsListBean == null ? null : itemGenreWithItemsListBean.getItemList();
                    if (itemList != null) {
                        for (ItemListBean itemListBean : itemList) {
                            itemListBean.setIsRest(true);
                        }
                    }
                }
                break;
            case "":
            case "SELLING":
            case "WILLRESTING":
                shop_cart_container.setVisibility(View.VISIBLE);
                img_shop_closed_tag.setVisibility(View.GONE);
                isShopOpen = true;
                break;
        }

        // 展示商品种类列表 or 店铺空空
        if (shopDetailData != null && shopDetailData.getItemGenreWithItemsList() != null
                && shopDetailData.getItemGenreWithItemsList().size() > 0) {

            good_all.setNoNextListener(new FocusFakeListView.NoNextListener() {
                @Override
                public void onNoNext(int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        int oldPos = good_kind.getPositions().getNow();
                        int newPos = oldPos + 1;
                        good_kind_adapter.hintOnPos(newPos, oldPos);
                        good_kind.selectOn(newPos, true, new Runnable() {
                            @Override
                            public void run() {
                                good_all.getPositions().clear();
                                good_all.postOANALTask(new Runnable() {
                                    @Override
                                    public void run() {
                                        good_all.selectOn(1, new Runnable() {
                                            @Override
                                            public void run() {
                                                getFocusManager().focusOn(good_all.getNode());
                                            }
                                        });
                                    }
                                });
                            }
                        }, new Runnable() {
                            @Override
                            public void run() {
                                AppDebug.d(TAG, TAG + " good_kind innerFocusOn " + good_kind.getPositions().getNow());
                                good_kind.innerFocusOn(good_kind.getPositions().getNow());
                            }
                        });
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        int oldPos = good_kind.getPositions().getNow();
                        int newPos = oldPos - 1;
                        good_kind_adapter.hintOnPos(newPos, oldPos);
                        good_kind.selectOn(newPos, true, new Runnable() {
                            @Override
                            public void run() {
                                good_all.getPositions().clear();
                                good_all.postOANALTask(new Runnable() {
                                    @Override
                                    public void run() {
                                        good_all.selectOn(1, new Runnable() {
                                            @Override
                                            public void run() {
                                                getFocusManager().focusOn(good_all.getNode());
                                            }
                                        });
                                    }
                                });
                            }
                        }, new Runnable() {
                            @Override
                            public void run() {
                                AppDebug.d(TAG, TAG + " good_kind innerFocusOn " + good_kind.getPositions().getNow());
                                good_kind.innerFocusOn(good_kind.getPositions().getNow());
                            }
                        });
                    }
                }
            });
            good_kind_adapter.loadData(shopDetailData.getItemGenreWithItemsList(), good_kind);
            good_kind.getPositions().addObserver(new Observer() {
                @Override
                public void update(Observable o, Object arg) {
                    if (o instanceof FocusFakeListView.Positions) {
                        final int pos = ((FocusFakeListView.Positions) o).getNow();
                        final ShopDetailData.ItemGenreWithItemsListBean tmp = good_kind_adapter.getDataAt(pos);
                        if (tmp != null) {
                            if (good_kind.getNode().isNodeHasFocus()) {
                                // todo 埋点
                                Map<String, String> properties = Utils.getProperties();
                                properties.put("shop id", shopId);
                                properties.put("spm", SPMConfig.WAIMAI_SHOP + ".list.floor_name_p");
                                Utils.utControlHit(getFullPageName(), "Focus_waimai_shop_floor_name_p" + pos, properties);
                            }

                            // todo 埋点
                            Map<String, String> properties = Utils.getProperties();
                            properties.put("shop id", shopId);
                            properties.put("spm", SPMConfig.WAIMAI_SHOP + ".list.floor_name_p");
                            Utils.utControlHit(getFullPageName(), "Page_waimai_shop_floor_name_p" + pos, properties);

                            goodsListAdapter.loadData(dataCenter.getShopDetailData()
                                    , tmp.getItemList()
                                    , pos == good_kind_adapter.getItemCount() - 1);
                            if (pos == good_kind_adapter.getItemCount() - 1) {
                                good_all.getPositions().setUnSelectableItem(0, goodsListAdapter.getItemCount() - 1);
                            } else {
                                good_all.getPositions().setUnSelectableItem(0);
                            }
                            good_all.selectOn(1, true, null, null);
                        }
                    }
                }
            });
            good_kind.setFocusConsumer(new ConsumerB() {
                @Override
                public boolean onFocusLeave() {
                    int nowPos = good_kind.getPositions().getNow();
                    good_kind_adapter.hintOnPos(nowPos, nowPos);
                    return true;
                }
            });

            shop_empty.setVisibility(View.GONE);

        } else {
            shop_empty.setVisibility(View.VISIBLE);
        }

        card_view.setRadio(getResources().getDimensionPixelOffset(R.dimen.dp_10));
        if (shopDetailData.getStoreDetailDTO() != null && !TextUtils.isEmpty(shopDetailData.getStoreDetailDTO().getShopLogo())) {
            ImageLoaderManager.getImageLoaderManager(TakeOutShopHomeActivity.this).displayImage(shopDetailData.getStoreDetailDTO().getShopLogo(), shop_icon_pic, ClassicOptions.dio565);
        } else {
            shop_icon_pic.setImageResource(R.drawable.icon_shop_no_logo);
        }

        // todo 刷新购物车
        qryShoppingCart();

        //聚焦在商品列表的第一个商品上
        good_kind.postOANALTask(new Runnable() {
            @Override
            public void run() {
                good_kind.selectOn(0, new Runnable() {
                    @Override
                    public void run() {
                        //getFocusManager().focusOn(good_kind.getNode());

                        int oldPos = good_kind.getPositions().getNow();
                        int newPos = 0;
                        good_kind_adapter.hintOnPos(newPos, oldPos);
                        good_kind.selectOn(newPos, new Runnable() {
                            @Override
                            public void run() {
                                good_all.getPositions().clear();
                                good_all.postOANALTask(new Runnable() {
                                    @Override
                                    public void run() {
                                        good_all.selectOn(1, new Runnable() {
                                            @Override
                                            public void run() {
                                                getFocusManager().focusOn(good_all.getNode());
                                            }
                                        });
                                    }
                                });
                            }
                        }, new Runnable() {
                            @Override
                            public void run() {
                                good_kind.innerFocusOn(good_kind.getPositions().getNow());
                                good_all.selectOn(1, new Runnable() {
                                    @Override
                                    public void run() {
                                        getFocusManager().focusOn(good_all.getNode());
                                    }
                                });
                            }
                        });

                    }
                });
            }
        });
    }

    /**
     * 刷新本地购物车
     */
    public void qryShoppingCart() {
        if (dataCenter != null) {
            dataCenter.qryTakeOutBag(new DataCenter.CallBackImpl() {
                @Override
                public void onSuccess(final Object data) {
                    super.onSuccess(data);
                    if (data != null && data instanceof TakeOutBag) {
                        final TakeOutBag takeOutBag = (TakeOutBag) data;

                        good_kind.postOANALTask(new Runnable() {
                            @Override
                            public void run() {
                                good_kind.refocusOnIfNeed(getFocusManager());
                            }
                        });
                        good_kind_adapter.notifyDataSetChanged();

                        good_all.postOANALTask(new Runnable() {
                            @Override
                            public void run() {
                                good_all.refocusOnIfNeed(getFocusManager());
                            }
                        });
                        goodsListAdapter.notifyDataSetChanged();

                        // 购物车 列表数据 价格数据 邮费数据
                        if (takeOutBag.cartItemList != null && !takeOutBag.cartItemList.isEmpty()) {
                            if (takeOutBag.cartItemList != null
                                    && !takeOutBag.cartItemList.isEmpty()) {
                                if (takeOutBag.cartItemList.get(0).isPackingFee) {
                                    good_collection_recycle.getPositions().setUnSelectableItem(0, takeOutBag.cartItemList.size());
                                } else {
                                    good_collection_recycle.getPositions().setUnSelectableItem(takeOutBag.cartItemList.size());
                                }
                            }
                            good_collection_recycle.postOBNALTask(new Runnable() {
                                @Override
                                public void run() {
                                    Integer pos = (Integer) dataCenter.getStateRecord().get("good_collection_recycle_refocus_on_candidate_pos");
                                    if (pos != null) {
                                        ShopCartItemVH.refocusOnReduce = true;
                                        FocusUtils.focusLeaveToLeaf(good_collection_recycle.getNode().getInnerNode());
                                    }
                                }
                            });
                            good_collection_recycle.postOANALTask(new Runnable() {
                                @Override
                                public void run() {
                                    Integer pos = (Integer) dataCenter.getStateRecord().remove("good_collection_recycle_refocus_on_candidate_pos");
                                    if (pos != null) {
                                        good_collection_recycle.refocusOnIfNeed(getFocusManager(), pos);
                                    } else {
                                        good_collection_recycle.refocusOnIfNeed(getFocusManager());
                                    }
                                }
                            });
                            good_collectionMultipleRecycleAdapter.loadData(takeOutBag.cartItemList);
                            ItemListBean bean = (ItemListBean) dataCenter.getStateRecord().remove("new_good_add_in_shop_cart");
                            if (bean != null && bean.__intentCount == 1) {
                                good_collection_recycle.selectOn(takeOutBag.cartItemList.size() - 1, new Runnable() {
                                    @Override
                                    public void run() {
                                        good_collection_recycle.innerFocusOn(takeOutBag.cartItemList.size() - 1);
                                    }
                                });
                            }
                        } else {
                            good_collectionMultipleRecycleAdapter.clear();
                        }

                        collection_pay.setText(format.format(takeOutBag.totalPromotionPrice / 100f));
                        good_collection_pay_ori.setText(" " + format.format(takeOutBag.totalPrice / 100f));

                        if (takeOutBag.totalPromotionPrice < takeOutBag.totalPrice) {
                            good_collection_pay_ori.setVisibility(View.VISIBLE);
                        } else {
                            good_collection_pay_ori.setVisibility(View.GONE);
                        }
                        if (takeOutBag.agentFee <= 0) {
                            collection_pay_dlive_amount.setText("免配送费");
                        } else {
                            collection_pay_dlive_amount.setText("另需配送费 " + format.format(takeOutBag.agentFee / 100f));
                        }

                        TakeOutBag.TipsBean tips = takeOutBag.tips;
                        String tipsResult = null;
                        if (tips != null) {
                            String showText = tips.showText;
                            if (showText != null && !showText.equals("您还有未享受的权益")) {
                                tipsResult = tips.showText.replace("￥", " ¥ ");
                            }
                        }

                        mShopCartActionView.setData("去结算", tipsResult, new ShopCartActionView.OnActionListener() {
                            @Override
                            public void onActionCallBack() {
                                gotoBill();
                            }
                        });
                        mShopCartActionView.setVisibility(View.VISIBLE);


                        // 购物车不为空 or 购物车为空
                        if (takeOutBag.cartItemList != null && takeOutBag.cartItemList.size() > 0) {
                            good_collection_empty.setVisibility(View.GONE);
                            collection_pay.setVisibility(View.VISIBLE);
                            good_collection_pay_ori.setVisibility(View.VISIBLE);
                            txt_start_price.setVisibility(View.GONE);
                            txt_start_price_tag.setVisibility(View.GONE);
                            if (!takeOutBag.canBuy) {
                                if (tips != null && tips.categoryId != null) {
                                    String categoryId = tips.categoryId;
                                    if ("-3".equals(categoryId)) {
                                        //缺少必选商品
                                        if (tips.showText != null) {
                                            mShopCartActionView.setData("去选择", tipsResult, new ShopCartActionView.OnActionListener() {
                                                @Override
                                                public void onActionCallBack() {
                                                    if (good_kind_adapter != null && dataCenter != null && good_kind != null
                                                            && good_all != null) {
                                                        ShopDetailData shopDetailData = dataCenter.getShopDetailData();
                                                        if (shopDetailData != null) {
                                                            List<ShopDetailData.ItemGenreWithItemsListBean> dataList = shopDetailData.getItemGenreWithItemsList();
                                                            if (dataList != null) {
                                                                for (int i = 0; i < dataList.size(); i++) {
                                                                    String id = dataList.get(i).getId();
                                                                    if ("-3".equals(id)) {
                                                                        //id为-3代表是必选品
                                                                        int oldPos = good_kind.getPositions().getNow();
                                                                        int newPos = i;
                                                                        good_kind_adapter.hintOnPos(newPos, oldPos);
                                                                        good_kind.selectOn(newPos, new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                good_all.getPositions().clear();
                                                                                good_all.postOANALTask(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        good_all.selectOn(1, new Runnable() {
                                                                                            @Override
                                                                                            public void run() {
                                                                                                getFocusManager().focusOn(good_all.getNode());
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                });
                                                                            }
                                                                        }, new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                good_kind.innerFocusOn(good_kind.getPositions().getNow());
                                                                                good_all.selectOn(1, new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        getFocusManager().focusOn(good_all.getNode());
                                                                                    }
                                                                                });
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                            mShopCartActionView.setVisibility(View.VISIBLE);
                                        }
                                    }
                                } else if (takeOutBag.buttonText != null && takeOutBag.buttonText.length() > 0) {
                                    //不能下单的原因（如未到起送价或者超出配送范围）
                                    String buttonTextResult = takeOutBag.buttonText.replace("￥", " ¥ ");
                                    mShopCartActionView.setData(buttonTextResult, null, new ShopCartActionView.OnActionListener() {
                                        @Override
                                        public void onActionCallBack() {
                                            boolean inDeliverRange = takeOutBag.inDeliverRange;
                                            if (!inDeliverRange) {
                                                CustomDialogShow.onPromptDialog(getString(R.string.out_of_deliver_range), TakeOutShopHomeActivity.this);
                                            } else {
                                                CustomDialogShow.onPromptDialog(getString(R.string.not_reach_start_price_hint), TakeOutShopHomeActivity.this);
                                            }
                                        }
                                    });
                                    mShopCartActionView.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            mShopCartActionView.setData(null, null, null);
                            mShopCartActionView.setVisibility(View.GONE);
                            good_collection_empty.setVisibility(View.VISIBLE);
                            collection_pay.setVisibility(View.GONE);
                            good_collection_pay_ori.setVisibility(View.GONE);
                            if (takeOutBag.buttonText != null && takeOutBag.buttonText.length() > 0) {
                                txt_start_price.setVisibility(View.VISIBLE);
                                if (!takeOutBag.inDeliverRange) {
                                    txt_start_price_tag.setVisibility(View.GONE);
                                } else {
                                    txt_start_price_tag.setVisibility(View.VISIBLE);
                                }
                                txt_start_price.setText(takeOutBag.buttonText.replace("起送", "").replaceAll("￥", "¥ "));
                            } else {
                                txt_start_price.setVisibility(View.GONE);
                                txt_start_price_tag.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * 代金券点击事件
     */
    private void utVouchersClick() {
        Map<String, String> properties = Utils.getProperties();
        properties.put("shop id", shopId);
        Utils.utControlHit(getFullPageName(), "Page_waimai_shop_grant_voucher_button_get", properties);
    }

    /**
     * 代金券曝光事件
     */
    private void utVouchersExposure() {
        Map<String, String> properties = Utils.getProperties();
        properties.put("shop id", shopId);
        Utils.utCustomHit(getFullPageName(), "Focus_waimai_shop_grant_voucher_button_get", properties);
    }

    private String orderType = "price";
    private int pageSize = 1000;
    private int pageNo = 1;
    private static String keyword;

    @Override
    public boolean onSearch(String asr, String txt, String spoken, List<String> tips, DomainResultVo.OtherCase otherCase) {
        if (!TextUtils.isEmpty(asr)) {
            getSearchResult(asr);
        } else {
            TTSUtils.getInstance().showDialog(this, 0);
        }
        keyword = asr;
        ttsText = txt;
        spokenText = spoken;
        tipsList.clear();
        tipsList.addAll(tips);
//        searchResultListener = (onSearchResultListener) TakeOutShopHomeActivity.this;
        return true;
    }

    /**
     * 加入购物车(一条一条加入)
     */
    private void addItemToCollection(ItemListBean mData) {
        if (mData.__intentCount == 0) {
            // 首次加入购物车
            dataCenter.getStateRecord().put("new_good_add_in_shop_cart", mData);
        }
        if ((mData.getMultiAttr() != null && mData.getMultiAttr().getAttrList() != null && mData.getMultiAttr().getAttrList().size() > 0)
                || (mData.getSkuList() != null && mData.getSkuList().size() > 1)) {
            // 含有多选参数
            try {
                Intent intent = new Intent();
                intent.setClass(this, TakeOutSkuActivity.class);
                intent.putExtra(BaseConfig.INTENT_KEY_ITEMID, mData.getItemId());
                intent.putExtra(BaseConfig.INTENT_KEY_SKU_TYPE, TradeType.TAKE_OUT_ADD_CART);
                intent.putExtra(BaseConfig.INTENT_KEY_BUY_COUNT, 1);
                intent.putExtra(BaseConfig.INTENT_KEY_TAKEOUT_SKU_DATA, mData);
                startActivityForResult(intent, TAKE_OUT_SHOP_CART_ACTIVITY_FOR_RESULT_CODE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 简单商品
            dataCenter.add(mData, new DataCenter.CallBackImpl() {
                @Override
                public void onSuccess(Object data) {
                    super.onSuccess(data);
                    qryShoppingCart();
                }
            });
        }
    }

    private void getSearchResult(String keyword) {
        businessRequest.requestSearchResult(shopId, keyword, orderType, pageSize, pageNo,
                new GetShopSearchResultListener(new WeakReference<BaseActivity>(TakeOutShopHomeActivity.this), 1, null));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == TAKE_OUT_SHOP_CART_ACTIVITY_FOR_RESULT_CODE) {
                qryShoppingCart();
            }
        }
    }

    /**
     * 店铺信息回调
     * 店铺数据需要分页
     */
    private class GetShopSearchResultListener extends BizRequestListener<ShopSearchResultBean> {
        int mIndex;
        ShopSearchResultBean mShopSearchData;

        public GetShopSearchResultListener(WeakReference<BaseActivity> baseActivityRef, int index, ShopSearchResultBean preData) {
            super(baseActivityRef);
            this.mIndex = index;
            this.mShopSearchData = preData;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            TakeOutShopHomeActivity activity = (TakeOutShopHomeActivity) mBaseActivityRef.get();
            AppDebug.e(TAG, "resultCode = " + resultCode + ",msg = " + msg);
            return false;
        }

        @Override
        public void onSuccess(ShopSearchResultBean data) {
            TakeOutShopHomeActivity activity = (TakeOutShopHomeActivity) mBaseActivityRef.get();

            if (mShopSearchData == null) {
                mShopSearchData = data;
            }
            AppDebug.e(TAG, "searchResult = " + data);
            if (data != null) {
                if (data.getItemInfoList() == null) {
                    TTSUtils.getInstance().showDialog(activity, 0);
                } else {
                    String prefix = "tvtaobao://home?app=takeout&module=takeoutShopSearch&shopId=" + shopId + "&keyword=" + keyword + "&scm=" + scm + "&tts=" + spokenText + "&ttsText" + ttsText;
                    Intent intent = new Intent();
                    WeakDataHolder.getInstance().saveData("shopDetailData", dataCenter.getShopDetailData());
                    WeakDataHolder.getInstance().saveData("shopSearchData", data);
                    intent.putExtra("fromHome", true);
                    intent.putExtra("isShopOpen", isShopOpen);
                    intent.putExtra("v_from", "voice_application");//语音
                    intent.putStringArrayListExtra("tips", (ArrayList<String>) tipsList);
                    intent.setData(Uri.parse(prefix));
                    activity.startActivity(intent);
                }
                return;
            } else {
//                shop_search_empty.setVisibility(View.VISIBLE);
            }

        }


        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    private void showVoiceTips() {
        SharePreferences.put("tipsShowCount", tipsShowCount + 1);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1.0f, 0.0f);
        valueAnimator.setDuration(1500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                tvVoiceTips.setAlpha(value);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                tvVoiceTips.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        valueAnimator.start();
    }

    class GetTakeOutVouchersListener extends BizRequestListener<VouchersMO> {

        public GetTakeOutVouchersListener(WeakReference<BaseActivity> weakReference) {
            super(weakReference);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            if (mBaseActivityRef.get() != null) {
                PromptPop.getInstance(mBaseActivityRef.get()).showPromptWindow(mBaseActivityRef.get().
                        getResources().getString(R.string.vouchers_error));
            }
            return true;
        }

        @Override
        public void onSuccess(VouchersMO data) {
            TakeOutShopHomeActivity activity = (TakeOutShopHomeActivity) mBaseActivityRef.get();
            if (activity != null) {
                if (data != null && data.getDetails() != null && data.getDetails().size() > 0) {
                    VouchersDialog.getInstance(activity).setVouchersMO(activity.getFullPageName(), data);
                    activity.flVouchers.setVisibility(View.GONE);
                    //切换到新的焦点
                    int oldPos = good_kind.getPositions().getNow();
                    int newPos = 0;
                    good_kind_adapter.hintOnPos(newPos, oldPos);
                    good_kind.selectOn(newPos, new Runnable() {
                        @Override
                        public void run() {
                            good_all.getPositions().clear();
                            good_all.postOANALTask(new Runnable() {
                                @Override
                                public void run() {
                                    good_all.selectOn(1, new Runnable() {
                                        @Override
                                        public void run() {
                                            getFocusManager().focusOn(good_all.getNode());
                                        }
                                    });
                                }
                            });
                        }
                    }, new Runnable() {
                        @Override
                        public void run() {
                            good_kind.innerFocusOn(good_kind.getPositions().getNow());
                            good_all.selectOn(1, new Runnable() {
                                @Override
                                public void run() {
                                    getFocusManager().focusOn(good_all.getNode());
                                }
                            });
                        }
                    });
                } else {
                    String info = data != null && !TextUtils.isEmpty(data.getDesc()) ? data.getDesc() : mBaseActivityRef.get().getResources().getString(R.string.vouchers_error);
                    PromptPop.getInstance(mBaseActivityRef.get()).showPromptWindow(info);
                }
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    @Override
    public String getPageName() {
        return "Page_waimai_shop";
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
        properties.put(SPMConfig.SPM_CNT, SPMConfig.WAIMAI_SHOP);
        return properties;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGoodItemEvent(final ShopGoodItemVH.Event event) {
        final ItemListBean mData = event.getData();
        int mPosition = event.getPos();
        // todo 埋点
        Map<String, String> map = Utils.getProperties();
        map.put("item id", mData.getItemId());
        Utils.utControlHit(getFullPageName(), "Page_waimai_shop_pit_name_p" + mPosition, map);
        AppDebug.e("ViewHolder", "focus");

        if (event == ShopGoodItemVH.Event.Add) {
            switch (mData.getGoodStatus()) {
                case edit:
                case normal:
                    if (mData != null) {
                        int limitQuantity = mData.__limitQuantity;
                        //多sku，不展示或提示库存，点加号就打开sku页
                        if ("true".equals(mData.getHasSku())) {
                            addItemToCollection(mData);
                        } else {
                            //没有sku
                            //不限购 或 限购数量 > 当前数量
                            if (limitQuantity == 0 || limitQuantity > mData.__intentCount) {
                                if (mData.getStock() <= mData.__intentCount) {
                                    CustomDialogShow.onPromptDialog(getString(R.string.take_out_sku_num_exceed_kucun), TakeOutShopHomeActivity.this);
                                } else {
                                    addItemToCollection(mData);
                                }
                            } else {
                                CustomDialogShow.onPromptDialog(String.format(getString(R.string.take_out_sku_num_exceed_limit), String.valueOf(limitQuantity)), TakeOutShopHomeActivity.this);
                            }
                        }

                    }
                    break;
            }

            // todo 埋点
            Map<String, String> properties = Utils.getProperties();
            properties.put("item id", mData.getItemId());
            properties.put("spm", SPMConfig.WAIMAI_SHOP_ADD_CART);
            Utils.utControlHit(getFullPageName(), "Page_waimai_shop_button_increase", properties);
            AppDebug.e("ViewHolder", "focus");

        } else if (event == ShopGoodItemVH.Event.Reduce) {
            switch (mData.getGoodStatus()) {
                case edit:
                    dataCenter.subtract(mData, new DataCenter.CallBackImpl() {
                        @Override
                        public void onSuccess(Object data) {
                            super.onSuccess(data);
                            qryShoppingCart();
                        }
                    });
                    break;
            }

            // todo 埋点
            Map<String, String> properties = Utils.getProperties();
            properties.put("item id", mData.getItemId());
            Utils.utControlHit(getFullPageName(), "Page_waimai_shop_button_decrease", properties);
            AppDebug.e("ViewHolder", "focus");
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCartItemEvent(final ShopCartItemVH.Event event) {
        final TakeOutBag.CartItemListBean mData = event.getData();
        int mPosition = event.getPos();
        // todo 埋点
        Map<String, String> map = Utils.getProperties();
        map.put("item id", mData.itemId);
        Utils.utControlHit(getFullPageName(), "Page_waimai_shop_pit_name_p" + mPosition, map);
        AppDebug.e("ViewHolder", "focus");

        if (event == ShopCartItemVH.Event.Add) {
            switch (mData.getGoodStatus()) {
                case edit:
                case normal:
                    if (mData != null) {
                        if (!TextUtils.isEmpty(mData.limitQuantity)) {
                            int limitQuantity = Integer.parseInt(mData.limitQuantity);
                            //不限购 或 限购数量>当前数量
                            if (limitQuantity == 0 || limitQuantity > mData.amount) {
                                if (mData.quantity <= mData.amount) {
                                    CustomDialogShow.onPromptDialog(getString(R.string.take_out_sku_num_exceed_kucun), TakeOutShopHomeActivity.this);
                                } else {
                                    dataCenter.add(mData, new DataCenter.CallBackImpl() {
                                        @Override
                                        public void onSuccess(Object data) {
                                            super.onSuccess(data);
                                            qryShoppingCart();
                                        }
                                    });
                                }
                            } else {
                                CustomDialogShow.onPromptDialog(String.format(getString(R.string.take_out_sku_num_exceed_limit), String.valueOf(limitQuantity)), TakeOutShopHomeActivity.this);
                            }
                        } else {
                            if (mData.quantity <= mData.amount) {
                                CustomDialogShow.onPromptDialog(getString(R.string.take_out_sku_num_exceed_kucun), TakeOutShopHomeActivity.this);
                            } else {
                                dataCenter.add(mData, new DataCenter.CallBackImpl() {
                                    @Override
                                    public void onSuccess(Object data) {
                                        super.onSuccess(data);
                                        qryShoppingCart();
                                    }
                                });
                            }
                        }

                    }
                    break;
            }

            // todo 埋点
            Map<String, String> properties = Utils.getProperties();
            properties.put("item id", mData.itemId);
            properties.put("spm", SPMConfig.WAIMAI_SHOP_CART_INCREASE);
            Utils.utControlHit(getFullPageName(), "Page_waimai_shop_cart_increase", properties);
            AppDebug.e("ViewHolder", "focus");

        } else if (event == ShopCartItemVH.Event.Reduce) {
            switch (mData.getGoodStatus()) {
                case edit:
                    dataCenter.subtract(mData, new DataCenter.CallBackImpl() {
                        @Override
                        public void onSuccess(Object data) {
                            super.onSuccess(data);
                            if (mData.amount == 1) {
                                dataCenter.getStateRecord().put("good_collection_recycle_refocus_on_candidate_pos"
                                        , good_collection_recycle.getCandidatePos(event.getPos()));
                            }
                            qryShoppingCart();
                        }
                    });
                    break;
            }

            // todo 埋点
            Map<String, String> properties = Utils.getProperties();
            properties.put("item id", mData.itemId);
            properties.put("spm", SPMConfig.WAIMAI_SHOP_CART_DECREASE);
            Utils.utControlHit(getFullPageName(), "Page_waimai_shop_cart_decrease", properties);
            AppDebug.e("ViewHolder", "focus");
        }
    }

    public void gotoBill() {
        int goodCount = 0;
        if (dataCenter != null && dataCenter.getTakeOutBag() != null && dataCenter.getTakeOutBag().canBuy && isShopOpen) {
            StringBuilder stringBuilder = new StringBuilder();
            //   String test = "tvtaobao://home?module=common&page=http://debug003.waptest.taobao.net:3128/zebra-pages-yunos-96315/index-pc?cartInfo=";
            //   stringBuilder.append(test);
            stringBuilder.append("tvtaobao://home?module=common&page=https://tvos.taobao.com/wow/yunos/act/tvtaoboa-waimai-ord?cartInfo=");
            stringBuilder.append(Config.getChannel());
            stringBuilder.append("*online*");
            stringBuilder.append(shopId);
            //添加outStoreId
            if (!TextUtils.isEmpty(outStoreId)) {
                stringBuilder.append("_~*_");
                stringBuilder.append(outStoreId);
            }
            stringBuilder.append("_~");

            boolean hasItem = false;
            for (int i = 0; dataCenter.getTakeOutBag().__cartItemList != null && i < dataCenter.getTakeOutBag().__cartItemList.size(); i++) {
                TakeOutBag.CartItemListBean itemListBean = dataCenter.getTakeOutBag().__cartItemList.get(i);
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
                        builder.append("; ");
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

            if (!TextUtils.isEmpty(from)) {
                stringBuilder.append("&v_from=").append(from);
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(stringBuilder.toString()));
            startActivity(intent);
        }

        // todo 埋点
        Map<String, String> properties = Utils.getProperties();
        properties.put("shop id", shopId);
        properties.put("spm", SPMConfig.WAIMAI_SHOP_SKU_SELECT_GOTO_CART);
        Utils.utControlHit(getFullPageName(), "Page_waimai_shop_cart_button_done_all", properties);

        // todo 埋点
        properties = Utils.getProperties();
        properties.put("item count", String.valueOf(goodCount));
        properties.put("spm", SPMConfig.WAIMAI_SHOP_SKU_SELECT_GOTO_CART);
        Utils.utControlHit(getFullPageName(), "Page_waimai_shop_cart_button_done_p" + goodCount, properties);
    }
}
