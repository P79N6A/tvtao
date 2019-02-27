package com.yunos.tvtaobao.goodlist.activity;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.taobao.statistic.CT;
import com.taobao.statistic.TBS;
import com.yunos.tv.app.widget.AdapterView;
import com.yunos.tv.app.widget.focus.FocusFlipGridView;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.common.DeviceJudge;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.PageMapConfig;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.config.TvOptionsChannel;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tv.core.util.ActivityPathRecorder;
import com.yunos.tv.core.util.ColorHandleUtil;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.activity.SdkHaveTabBaseActivity;
import com.yunos.tvtaobao.biz.adapter.TabGoodsBaseAdapter;
import com.yunos.tv.core.common.ActivityQueueManager;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.listener.NetworkOkDoListener;
import com.yunos.tvtaobao.biz.listener.TabFocusFlipGridViewListener;
import com.yunos.tvtaobao.biz.listener.TabFocusListViewListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.request.bo.GoodsSearchResult;
import com.yunos.tvtaobao.biz.request.bo.GoodsSearchZtcResult;
import com.yunos.tvtaobao.biz.request.bo.GoodsTmail;
import com.yunos.tvtaobao.biz.request.bo.RebateBo;
import com.yunos.tvtaobao.biz.request.bo.SearchedGoods;
import com.yunos.tvtaobao.biz.request.info.GlobalConfigInfo;
import com.yunos.tvtaobao.biz.request.ztc.ZTCUtils;
import com.yunos.tvtaobao.biz.widget.GoodListLifeUiGridView;
import com.yunos.tvtaobao.biz.widget.TabFocusPositionManager;
import com.yunos.tvtaobao.goodlist.GoodListFocuseUnit;
import com.yunos.tvtaobao.goodlist.GoodListFocuseUnit.CURRENTBACKVIEW;
import com.yunos.tvtaobao.goodlist.GoodListFocuseUnit.MenuView_Info;
import com.yunos.tvtaobao.goodlist.GoodListFocuseUnit.SORTFLAG;
import com.yunos.tvtaobao.goodlist.GoodListFocuseUnit.VISUALCONFIG;
import com.yunos.tvtaobao.goodlist.GoodListImageHandle;
import com.yunos.tvtaobao.goodlist.R;
import com.yunos.tvtaobao.goodlist.adapter.GoodListGirdMenuAdapter;
import com.yunos.tvtaobao.goodlist.adapter.GoodListGirdViewAdapter;
import com.yunos.tvtaobao.goodlist.view.GoodListItemFrameLayout;
import com.yunos.tvtaobao.goodlist.view.GoodListMenuFocusFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 商品列表页
 */
public class GoodListActivity extends SdkHaveTabBaseActivity {

    private final static String TAG = "GoodsSeachResult";

    /***
     * 注意：
     * 以下变量[mOrderby,mMenuLogo,mMenuLogo_opaque, mSortFlag, mTableList,mTBS_P,
     * mTBS_Name, mGridlistView_ID]必须满足 UI2.0 视觉图上顺序摆放，
     * 也就是：
     * 1： 默认
     * 2： 销量-->降序
     * 3：人气-->降序
     * 4：新品-->降序
     * 5： 价格-->升序
     * 6： 价格-->降序
     */


    //判断购物车是否有活动
    protected boolean hasActive = false;

    private final String DEFAULT = "s";

    private final String mOrderby[] = {DEFAULT, "d", "rq", "new", "p", "pd"};

    // 有透明度的LOGO
    private final int mMenuLogo[] = {R.drawable.ytsdk_ui2_goodlist_menulogo_default,
            R.drawable.ytsdk_ui2_goodlist_menulogo_saledescend, R.drawable.ytsdk_ui2_goodlist_menulogo_coefpdescend,
            R.drawable.ytsdk_ui2_goodlist_menulogo_ratesumdescend, R.drawable.ytsdk_ui2_goodlist_menulogo_bidascend,
            R.drawable.ytsdk_ui2_goodlist_menulogo_biddescend};

    // 没有透明度的LOGO
    private final int mMenuLogo_opaque[] = {R.drawable.ytsdk_ui2_goodlist_menulogo_default_opaque,
            R.drawable.ytsdk_ui2_goodlist_menulogo_saledescend_opaque,
            R.drawable.ytsdk_ui2_goodlist_menulogo_coefpdescend_opaque,
            R.drawable.ytsdk_ui2_goodlist_menulogo_ratesumdescend_opaque,
            R.drawable.ytsdk_ui2_goodlist_menulogo_bidascend_opaque,
            R.drawable.ytsdk_ui2_goodlist_menulogo_biddescend_opaque};

    private final int mSortFlag[] = {SORTFLAG.NO, SORTFLAG.DOWN, SORTFLAG.DOWN, SORTFLAG.DOWN, SORTFLAG.UP,
            SORTFLAG.DOWN};

    private final String mTableList[] = {"默认", "销量", "人气", "新品", "价格", "价格"};
    private final int mTableListId[] = {R.id.gridview1, R.id.gridview2, R.id.gridview3, R.id.gridview4, R.id.gridview5, R.id.gridview6};

    private final String mTBS_P[] = {"Default", "SaleDescend", "CoefpDescend", "NewDescend", "BidAscend", "BidDescend"};
    private final String mTBS_Name[] = {"默认", "销量降序", "人气降序", "新品降序", "价格升序", "价格降序"};

    /*****************************************************************************************************************************************/

    /**
     * 以下是系统的变量参数
     */

    // 关键字标题显示的最大字符数
    private final int TITLE_NUMBER_MAX = 15;

    private String mKeyWord = null;
    private String mCatMapId = null;
    private String mTab = null;

    private static Map<String, ArrayList<SearchedGoods>> mGoodsArrayList = null;

    // 存放View [k = 排序的关键字， v = 有关这一排序的信息]
    private HashMap<String, GoodListFocuseUnit.MenuView_Info> mGoodsMenuViewInfoMap = null;

    private GoodListImageHandle mImageHandle = null;

    private BusinessRequest mBusinessRequest = null;

    private boolean mHaveGoods = true;

    private Handler mHandler = null;

    private boolean mFirstStartRequestData = true;

    private TextView mTitleTextView = null;
    private GoodListGirdMenuAdapter mGoodListGirdMenuAdapter = null;

    private boolean mStateOfButtonGroup = true;

    // 标题文字的阴影和阴影颜色
    private int mTextShadowPix = 0;
    private int mTextShadowColor = ColorHandleUtil.ColorTransparency(Color.BLACK, 63);

    private String mTitleText = null;

    private int mFouce_View_Current;

    private GoodListFocuseUnit mGoodListFocuseUnit = null;

    private boolean mFirstFocusButtonGroup = true;

    private int mOldPosition = -1;

    // 针对背景状态
    private Drawable mCommonBackGround = null;
    private Drawable mGoodsListBackGround = null;
    private boolean mCommonShow = false;

    // 当前请求的GirdView编号
    private int mCurrentRequest_GirdViewNum;
    // 重连的监听
    private GoodListNetworkOkDoListener mGoodListNetworkOkDoListener;

    Map<String, String> lProperties = Utils.getProperties();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onKeepActivityOnlyOne();
        // 获取外部传入的数据
        Intent intent = getIntent();

        if (intent == null) {
            return;
        }

        // 清除缓冲
        onClearBufferData();

        mCatMapId = null;
        mKeyWord = null;
        mTitleText = null;
        mTab = null;

        // 搜索引擎
        mTab = intent.getStringExtra(BaseConfig.INTENT_KEY_SEARCH_TAB);

        // 获取搜索关键字
        mKeyWord = intent.getStringExtra(BaseConfig.INTENT_KEY_KEYWORDS);

        String from = intent.getStringExtra(CoreIntentKey.URI_FROM);
        //如果来自语音，说明是来自语音查看更多页面
        if ("voice_application".equals(from)) {
            TvOptionsConfig.setTvOptionVoiceSystem(from);
            TvOptionsConfig.setTvOptionsChannel(TvOptionsChannel.VOICE_VIEW_MORE);
        }

        if (mKeyWord != null) {
            mTitleText = mKeyWord;
        } else {
            // 如果搜索关键是空，则获取类目的ID
            mCatMapId = intent.getStringExtra(BaseConfig.INTENT_KEY_CATEGORY_ID);

            // 类目ID不为空，则获取类目的标题
            if (mCatMapId != null) {
                mTitleText = intent.getStringExtra(BaseConfig.INTENT_KEY_CATEGORY_NAME);
            }
        }

        AppDebug.i(TAG, "mKeyWord = " + mKeyWord + ", mCatMapId = " + mCatMapId + ", mTitleText = " + mTitleText
                + ", mTab = " + mTab);

        // 两者都为空，则暂定为结束
        if ((mKeyWord == null) && (mCatMapId == null)) {
            this.finish();
            return;
        }


        // 初始化界面的变量值
        onInitVariableValue();

        onSetTabBaseActivityListener();

        onInitTabBaseActivity();

        isHasActivityFromNet();

        onSetPierceActivityListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 得到是否有活动
     */
    private void isHasActivityFromNet() {
        GlobalConfig globalConfig = GlobalConfigInfo.getInstance().getGlobalConfig();
        if (globalConfig != null) {
            GlobalConfig.ShopCartFlag shopCartFlag = globalConfig.getShopCartFlag();
            if (shopCartFlag != null && shopCartFlag.isActing()) {
                fiv_pierce_background.setBackgroundResource(R.drawable.goodlist_fiv_pierce_bg_d11);
//                fiv_pierce_cart_focusd.setImageResource(R.drawable.goodlist_fiv_pierce_cart_focus);
                hasActive = shopCartFlag.isActing();
                String sideBarImgUrl = shopCartFlag.getSideBarIcon();
                ImageLoaderManager.getImageLoaderManager(GoodListActivity.this).displayImage(sideBarImgUrl, iv_pierce_cart_active);
                iv_pierce_cart_active.setVisibility(View.VISIBLE);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        iv_pierce_cart_active.setVisibility(View.INVISIBLE);
                    }
                }, 5000);
                //判断是否有活动
                lProperties.put("name", "购物车气泡");
                Utils.utControlHit(getFullPageName(), "Expose_tool_cartbubble", lProperties);

            }
        }
    }

    private void onSetPierceActivityListener() {

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
                lProperties.put("spm", SPMConfig.GOODS_LIST_SPM_FUCTION_HOME);
                Utils.utControlHit(getFullPageName(), "Page_tool_button_home", lProperties);
                Utils.updateNextPageProperties(SPMConfig.GOODS_LIST_SPM_FUCTION_HOME);

                Intent intent = new Intent();
                intent.setClassName(GoodListActivity.this, BaseConfig.SWITCH_TO_HOME_ACTIVITY);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                //setFrom("");
                startActivity(intent);
            }
        });

        fiv_pierce_my_focusd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lProperties.put("controlname", "TbMyTaoBao");
                lProperties.put("spm", SPMConfig.GOODS_LIST_SPM_FUCTION_MY);
                Utils.utControlHit(getFullPageName(), "Page_tool_button_TbMyTaoBao", lProperties);
                Utils.updateNextPageProperties(SPMConfig.GOODS_LIST_SPM_FUCTION_MY);
                Intent intent = new Intent();
                intent.setClassName(GoodListActivity.this, BaseConfig.SWITCH_TO_MYTAOBAO_ACTIVITY);
                intent.putExtra(ActivityPathRecorder.INTENTKEY_FIRST, true);
                //setFrom("");
                startActivity(intent);
            }
        });
        fiv_pierce_cart_focusd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lProperties.put("controlname", "Cart");
                lProperties.put("spm", SPMConfig.GOODS_LIST_SPM_FUCTION_CART);
                Utils.utControlHit(getFullPageName(), "Page_tool_button_Cart", lProperties);
                Utils.updateNextPageProperties(SPMConfig.GOODS_LIST_SPM_FUCTION_CART);
                Intent intent = new Intent();
                intent.setClassName(GoodListActivity.this, BaseConfig.SWITCH_TO_SHOPCART_LIST_ACTIVITY);
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
                lProperties.remove("spm");
                Utils.utControlHit(getFullPageName(), "Page_tool_button_red", lProperties);
                Intent intent = new Intent();
                if (GlobalConfig.instance != null && GlobalConfig.instance.coupon.isShowH5Coupon()) {
                    String page = PageMapConfig.getPageUrlMap().get("coupon");
                    if (!TextUtils.isEmpty(page)) {
                        intent.setData(Uri.parse("tvtaobao://home?module=common&page=" + page));
                    } else {
                        intent.setClassName(GoodListActivity.this, BaseConfig.SWITCH_TO_COUPON_ACTIVITY);
                    }
                } else
                    intent.setClassName(GoodListActivity.this, BaseConfig.SWITCH_TO_COUPON_ACTIVITY);
                intent.putExtra(ActivityPathRecorder.INTENTKEY_FIRST, true);

                //setFrom("");
                startActivity(intent);

            }
        });
//        卡券包
        fiv_pierce_block_focusd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lProperties.put("controlname", "coupon");
                lProperties.put("spm", SPMConfig.GOODS_LIST_SPM_FUCTION_COUPON);
                Utils.utControlHit(getFullPageName(), "Page_tool_button_coupon", lProperties);
                Utils.updateNextPageProperties(SPMConfig.GOODS_LIST_SPM_FUCTION_COUPON);
                Intent intent = new Intent();
                if (GlobalConfig.instance != null && GlobalConfig.instance.coupon.isShowH5Coupon()) {
                    String page = PageMapConfig.getPageUrlMap().get("coupon");
                    if (!TextUtils.isEmpty(page)) {
                        intent.setData(Uri.parse("tvtaobao://home?module=common&page=" + page));
                    } else {
                        intent.setClassName(GoodListActivity.this, BaseConfig.SWITCH_TO_COUPON_ACTIVITY);
                    }
                } else
                    intent.setClassName(GoodListActivity.this, BaseConfig.SWITCH_TO_COUPON_ACTIVITY);
                intent.putExtra(ActivityPathRecorder.INTENTKEY_FIRST, true);
                //setFrom("");
                startActivity(intent);

            }
        });

//        客服
        fiv_pierce_contact_focusd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lProperties.put("controlname", "phone");
                lProperties.put("spm", SPMConfig.GOODS_LIST_SPM_FUCTION_CC);
                Utils.updateNextPageProperties(SPMConfig.GOODS_LIST_SPM_FUCTION_CC);
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

//        积分
        fiv_pierce_red_jifen_focusd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lProperties.put("controlname", "integral");
                lProperties.put("spm", SPMConfig.GOODS_LIST_SPM_FUCTION_POINT);
                Utils.utControlHit(getFullPageName(), "Page_tool_button_integral", lProperties);
                Utils.updateNextPageProperties(SPMConfig.GOODS_LIST_SPM_FUCTION_POINT);

                Intent intent = new Intent();
                intent.setClassName(GoodListActivity.this, BaseConfig.SWITCH_TO_POINT_ACTIVITY);
                startActivity(intent);
            }
        });
    }

    /**
     * 设置右侧穿透聚焦效果
     */
    private void setOnPierceItemFocusdListener() {
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

        fiv_pierce_my_focusd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                lProperties.put("controlname", "TbMyTaoBao");
                Utils.utCustomHit(getFullPageName(), "tool_button_focus_TbMyTaoBao", lProperties);
                if (hasFocus) {
                    tv_pierce_my.setVisibility(View.VISIBLE);
                    if (User.isLogined()) {
                        tv_pierce_my.setText(User.getNick());
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
                AppDebug.e(TAG, "GoodsListActivity---> 0");
                GlobalConfig globalConfig = GlobalConfigInfo.getInstance().getGlobalConfig();
                if (globalConfig != null) {
                    AppDebug.e(TAG, "GoodsListActivity---> 2");
                    GlobalConfig.ShopCartFlag shopCartFlag = globalConfig.getShopCartFlag();
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
                    AppDebug.e(TAG, "GoodsListActivity---> 3");

                    if (hasFocus) {
                        mTabFocusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(
                                R.drawable.ytm_touming)));
                        AppDebug.e(TAG, "GoodsListActivity---> 4");
                        tv_pierce_cart.setVisibility(View.VISIBLE);
                    } else {
                        AppDebug.e(TAG, "GoodsListActivity---> 5");
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

    /**
     * 保持页面只有一个，如果要打开新的页面就需要将应用里面之前的页面关闭掉,
     * 在该类中单独调用，因为不继承于MainBaseActivity
     */
    private void onKeepActivityOnlyOne() {
        ActivityQueueManager manager = ActivityQueueManager.getInstance();
        manager.onDestroyActivityOfList(GoodListActivity.class.getName());
        manager.onAddDestroyActivityToList(GoodListActivity.class.getName(), this);
    }

    /**
     * 从列表中移除该页面
     * 在该类中单独调用，因为不继承于MainBaseActivity
     */
    private void onRemoveKeepedActivity() {
        ActivityQueueManager manager = ActivityQueueManager.getInstance();
        manager.onRemoveDestroyActivityFromList(GoodListActivity.class.getName(), this);
    }

    @Override
    protected void onDestroy() {
        onClearBufferData();
        onRemoveKeepedActivity();
        ZTCUtils.destroy();
        super.onDestroy();
        Map<String, String> properties = Utils.getProperties();
        properties.put("built_in", "research");

        Utils.utUpdatePageProperties("Page_tool", properties);
        Utils.utPageDisAppear("Page_tool");
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> p = super.getPageProperties();
        if (!TextUtils.isEmpty(mTitleText)) {
            p.put("name", mTitleText);
        }
        if (!TextUtils.isEmpty(mCatMapId)) {
            p.put("cate_map_id", mCatMapId);
        }
        p.put(SPMConfig.SPM_CNT, SPMConfig.GOODS_LIST_SPM + ".0.0");

        return p;
    }

    /**
     * 初始化埋点信息
     *
     * @return
     */
    public Map<String, String> initTBSProperty() {

        Map<String, String> p = super.getPageProperties();
        if (!TextUtils.isEmpty(mTitleText)) {
            p.put("name", mTitleText);
        }
        if (!TextUtils.isEmpty(mCatMapId)) {
            p.put("cate_map_id", mCatMapId);
        }

        return p;

    }


    /**
     * 初始化界面变量
     */
    public void onInitVariableValue() {
        mCurrentRequest_GirdViewNum = 0;
        mGoodListFocuseUnit = new GoodListFocuseUnit();
        mHandler = new KeyUpEventHandler(new WeakReference<GoodListActivity>(this));

        // 创建网络断开监听
        mGoodListNetworkOkDoListener = new GoodListNetworkOkDoListener(new WeakReference<GoodListActivity>(this));

        mCommonBackGround = this.getResources().getDrawable(R.drawable.ytsdk_ui2_common_background);
        mGoodsListBackGround = this.getResources().getDrawable(R.drawable.ytsdk_ui2_goodslist_bg);
        mCommonShow = false;

        mBusinessRequest = BusinessRequest.getBusinessRequest();
        OnWaitProgressDialog(false);

        mGoodsArrayList = new HashMap<String, ArrayList<SearchedGoods>>();
        mGoodsArrayList.clear();

        for (int i = 0; i < mOrderby.length; i++) {
            ArrayList<SearchedGoods> goosInit = new ArrayList<SearchedGoods>();
            goosInit.clear();
            mGoodsArrayList.put(mOrderby[i], goosInit);
        }

        mImageHandle = new GoodListImageHandle(this);
        mImageHandle.onInitPaint();

        mGoodsMenuViewInfoMap = new HashMap<String, MenuView_Info>();
        mGoodsMenuViewInfoMap.clear();

        mHaveGoods = true;
        mFirstFocusButtonGroup = true;

        mStateOfButtonGroup = true;
        mFouce_View_Current = CURRENTBACKVIEW.GOODS_VIEW;
    }

    /**
     * 设置左侧TAB 和 GridView 的 Listener
     */

    private void onSetTabBaseActivityListener() {

        setTabFocusListViewListener(new TabFocusListViewListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AppDebug.i(TAG, "TabFocusListViewListener.onFocusChange ---> v = " + v + "; hasFocus = " + hasFocus);
                if (hasFocus) {
                    if (mFirstFocusButtonGroup) {
                        mFirstFocusButtonGroup = false;
                    }

                    onHandleBankground(true);

                    setFocusMenuFrameLayoutBackgroudState(View.INVISIBLE);

                } else {
                    // 统计代码，统计用户关注左侧菜单对应商品的次数，以焦点离开左侧菜单，进入右侧商品计算
                    int position = mTabFocusListView.getSelectedItemPosition();
                    if (position < 0 || position >= mTBS_Name.length) {
                        return;
                    }
                    String rank = mTBS_Name[position];
                    String controlName = Utils.getControlName(getFullPageName(), "Rank", null);
                    Map<String, String> p = Utils.getProperties();
                    if (!TextUtils.isEmpty(mTitleText)) {
                        p.put("keyword", mTitleText);
                    }
                    if (!TextUtils.isEmpty(mCatMapId)) {
                        p.put("cate_map_id", mCatMapId);
                    }
                    p.put("rank_name", rank);
                    Utils.utCustomHit(getFullPageName(), controlName, p);
                }
            }

            @Override
            public void onItemSelected(View select, int position, boolean isSelect, View fatherView) {
                //让左侧Tab最后一个避免跳到右侧
                if (position == mMenuLogo.length - 1) {
                    mTabFocusListView.setNextFocusDownId(R.id.common_focuslistview);
                }
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        setTabFocusFlipGridViewListener(new TabFocusFlipGridViewListener() {

            @Override
            public void onFocusChange(FocusFlipGridView focusFlipGridView, String tabkey, View v, boolean hasFocus) {
                AppDebug.i(TAG, "TabFocusFlipGridViewListener.onFocusChange ---> v = " + v + "; hasFocus = " + hasFocus
                        + ", tabkey = " + tabkey + ".focusFlipGridView = " + focusFlipGridView);
                if (hasFocus) {
                    setFocusMenuFrameLayoutBackgroudState(View.VISIBLE);
                    onHandleBankground(false);
                }
            }

            @Override
            public void onLayoutDone(FocusFlipGridView focusFlipGridView, String tabkey, boolean isFirst) {

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
                // 获取View的处理
                // 先检查，如果当前存储的数据部满足将要显示的数据，那么则请求信的数据
                if (onNeedRequestData(tabkey, position)) {
                    requestSearch(tabkey);
                }
                return true;
            }

            @Override
            public boolean onItemClick(FocusFlipGridView focusFlipGridView, String tabkey, AdapterView<?> arg0,
                                       View arg1, int position, long arg3) {
                AppDebug.i(TAG, TAG + ".onSetTabBaseActivityListener.onItemClick.AdapterView = " + arg0
                        + ".selectedView = " + arg1 + ".position = " + position + ".row_id = " + arg3
                        + ".focusFlipGridView = " + focusFlipGridView);

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

    public void onSetCurrentFouceView(int view) {
        mFouce_View_Current = view;
    }

    // 此方法仅适用于2.1以上

    @Override
    public void onBackPressed() {
        AppDebug.i(TAG, "onBackPressed...  mFouce_View_Current = " + mFouce_View_Current + "; mTabFocusListView = "
                + mTabFocusListView);
        super.onBackPressed();
    }

    /**
     * 设置当前焦点
     *
     * @param gain
     */
    private void onHandleBankground(boolean gain) {

        AppDebug.i(TAG, "onHandleBankground...  gain = " + gain + "; mStateOfButtonGroup = " + mStateOfButtonGroup);

        if (gain) {
            onSetCurrentFouceView(CURRENTBACKVIEW.MENU_VIEW);
        } else {
            onSetCurrentFouceView(CURRENTBACKVIEW.GOODS_VIEW);
        }
    }

    /**
     * 改变左侧TAB的背景显示状态
     *
     * @param visibility
     */
    public void setFocusMenuFrameLayoutBackgroudState(int visibility) {

        GoodListMenuFocusFrameLayout mCurrentFocusMenuFrameLayout = null;
        if (mTabFocusListView != null) {
            View seletView = mTabFocusListView.getSelectedView();
            if (seletView instanceof GoodListMenuFocusFrameLayout) {
                mCurrentFocusMenuFrameLayout = (GoodListMenuFocusFrameLayout) seletView;
            }
        }

        AppDebug.i(TAG, "setFocusMenuFrameLayoutBackgroudState ---> visibility = " + visibility
                + "; mCurrentFocusMenuFrameLayout = " + mCurrentFocusMenuFrameLayout);

        if (mCurrentFocusMenuFrameLayout == null)
            return;

        mCurrentFocusMenuFrameLayout.setBackgroudView(visibility);
    }

    /**
     * 改变界面背景
     *
     * @param save_viewpager_Info
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
            mTabFocusPositionManager.setBackgroundDrawable(mGoodsListBackGround);
        }
    }

    /**
     * 改变顶部蒙版状态
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
        int currentRow = position / VISUALCONFIG.COLUMNS_COUNT;
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
     * 清除缓冲区数据
     */
    public void onClearBufferData() {

        OnWaitProgressDialog(false);

        if (mGoodsArrayList != null) {
            Iterator<Map.Entry<String, ArrayList<SearchedGoods>>> iter = mGoodsArrayList.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, ArrayList<SearchedGoods>> entry = (Map.Entry<String, ArrayList<SearchedGoods>>) iter.next();
                // String key = (String) entry.getKey();
                ArrayList<SearchedGoods> val = (ArrayList<SearchedGoods>) entry.getValue();
                if (val != null) {
                    val.clear();
                }
            }

            mGoodsArrayList.clear();
            mGoodsArrayList = null;
        }

        if (mGoodsMenuViewInfoMap != null) {
            Iterator<Map.Entry<String, MenuView_Info>> iter = mGoodsMenuViewInfoMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, MenuView_Info> entry = (Map.Entry<String, MenuView_Info>) iter.next();
                //                String key = (String) entry.getKey();
                MenuView_Info val = (MenuView_Info) entry.getValue();

                if (val != null) {
                    GoodListGirdViewAdapter goodListGirdViewAdapter = (GoodListGirdViewAdapter) val.goodListGirdViewAdapter;
                    if (goodListGirdViewAdapter != null) {
                        goodListGirdViewAdapter.onClearAndDestroy();
                    }
                    if (val.lifeUiGridView != null) {
                        val.lifeUiGridView.removeAllViewsInLayout();
                    }
                }

            }
            mGoodsMenuViewInfoMap.clear();
        }

        if (mImageHandle != null) {
            mImageHandle.onDestroyAndClear();
        }

        if (mGoodListGirdMenuAdapter != null) {
            mGoodListGirdMenuAdapter.onDestroy();
        }

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 判断是否需要再次请求新的数据
     *
     * @param menuText
     * @param position
     * @return
     */
    private boolean onNeedRequestData(String menuText, int position) {

        if (menuText == null)
            return false;

        MenuView_Info save_viewpager_Info = null;
        if (mGoodsMenuViewInfoMap != null) {
            save_viewpager_Info = mGoodsMenuViewInfoMap.get(menuText);
        }

        if (save_viewpager_Info == null)
            return false;

        AppDebug.i(TAG, "needRequestData,--->  save_viewpager_Info.End_Request = " + save_viewpager_Info.End_Request
                + "; position = " + position);

        // 如果请求已经结束，则不在请求信的数据
        if (save_viewpager_Info.End_Request) {
            return false;
        }

        ArrayList<SearchedGoods> arrayList = null;
        if (mGoodsArrayList != null) {
            arrayList = mGoodsArrayList.get(save_viewpager_Info.orderby);
        }

        if (arrayList == null)
            return true;

        int arrayListSize = arrayList.size();

        // 如果保存的数据已经满足最大的需求数，那么不需要求数据
        if (arrayListSize >= VISUALCONFIG.GOODS_MAX_TOTAL) {
            return false;
        }

        // 当前保存的行数
        int currSaveRowSize = arrayListSize / VISUALCONFIG.COLUMNS_COUNT;

        // 显示所需要的行数
        int needRowSize = (position / VISUALCONFIG.COLUMNS_COUNT) + VISUALCONFIG.ROWS_SPACE;

        AppDebug.i(TAG, "needRequestData,--->  currSaveRowSize = " + currSaveRowSize);
        AppDebug.i(TAG, "needRequestData,--->  needRowSize = " + needRowSize);

        if (needRowSize >= currSaveRowSize) {
            return true;
        }

        return false;

    }

    /**
     * 查找下一个要请求数据的商品列表展示VIEW
     *
     * @param currentTypeId
     * @return
     */
    private String onFindSdkViewPagerFromMap(int currentTypeId) {
        String menuText = null;

        if (mGoodsMenuViewInfoMap == null)
            return null;

        if (mOrderby == null) {
            return null;
        }

        if (currentTypeId >= mOrderby.length) {
            return null;
        }

        String order = mOrderby[currentTypeId];

        Iterator<Map.Entry<String, MenuView_Info>> iter = mGoodsMenuViewInfoMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, MenuView_Info> entry = (Map.Entry<String, MenuView_Info>) iter.next();
            String key = (String) entry.getKey();
            MenuView_Info val = (MenuView_Info) entry.getValue();

            if (val != null) {
                if (order.equals(val.orderby)) {
                    menuText = key;
                    break;
                }
            }
        }
        return menuText;
    }

    /**
     * 请求新的数据
     *
     * @param menuText
     */
    private void requestSearch(final String menuText) {

        // 如果没有商品，则返回
        if (!mHaveGoods) {
            return;
        }

        // 如果还在加载中，则返回
        //        if (mInLoadingData) {
        //            return;
        //        }

        if (mBusinessRequest == null)
            return;

        if (menuText == null) {
            return;
        }

        if (mGoodsMenuViewInfoMap == null) {
            return;
        }

        final MenuView_Info save_viewpager_Info = mGoodsMenuViewInfoMap.get(menuText);

        if (save_viewpager_Info == null)
            return;

        final int mRequestPageSize = VISUALCONFIG.EACH_REQUEST_ROW_COUNT * VISUALCONFIG.COLUMNS_COUNT;

        int currReqPageCount = save_viewpager_Info.CurReqPageCount;

        final String kw = save_viewpager_Info.keyWords;
        final String sort = save_viewpager_Info.orderby;
        final String catmap = save_viewpager_Info.catMap;

        AppDebug.i(TAG, "requestSearch  kw = " + kw + ";  sort  =  " + sort + ";  catmap = " + catmap);
        AppDebug.i(TAG, "requestSearch  mRequestPageSize  = " + mRequestPageSize);
        AppDebug.i(TAG, "requestSearch  currReqPageCount  = " + currReqPageCount);

        // 不允许按返回键取消进度条
        setProgressCancelable(false);


        OnWaitProgressDialog(true);
//        mBusinessRequest.requestSearchMtop(kw, mRequestPageSize, currReqPageCount, sort, catmap, null,
//                new MyWeakRefBusinessRequestListener(new WeakReference<BaseActivity>(this), save_viewpager_Info, sort));
        String flag = "r";

        GlobalConfig config = GlobalConfigInfo.getInstance().getGlobalConfig();

        if (config != null && config.getZtcConfig() != null) {
            AppDebug.d("test", "global ztc config flag:" + config.getZtcConfig().flag);
            flag = TextUtils.isEmpty(config.getZtcConfig().flag) ? flag : config.getZtcConfig().flag;
        }
        AppDebug.d("test", "search q:" + kw);
        if ("d".equals(sort) || "p".equals(sort) || "pd".equals(sort)) {
            flag = "";
        }
        String orderBy = translateSort(sort);

//        if ((!TextUtils.isEmpty(kw) || !TextUtils.isEmpty(catmap)) && (TextUtils.isEmpty(sort) || DEFAULT.equals(sort)))//按类目和关键字类目，默认排序，则请求新接口
//        {
        mBusinessRequest.requestSearchMtopZTC(TextUtils.isEmpty(kw) ? mTitleText : kw, mRequestPageSize, currReqPageCount, 4, orderBy, catmap, null, null, NetWorkUtil.getIpAddress(), flag, 0,
                new MyWeakRefBusinessRequestListenerZTC(new WeakReference<BaseActivity>(this), save_viewpager_Info, sort));
//        } else
//            mBusinessRequest.requestSearchMtop(kw, mRequestPageSize, currReqPageCount, sort, catmap, null,
//                    new MyWeakRefBusinessRequestListener(new WeakReference<BaseActivity>(this), save_viewpager_Info, sort));
    }

    private String translateSort(String sort) {
        if (TextUtils.isEmpty(sort))
            return null;
        if ("s".equals(sort))
            return null;
        if ("d".equals(sort)) {
            return "sales";
        }
        if ("rq".equals(sort)) {
            return "popular";
        }
        if ("new".equals(sort)) {
            return "new";
        }
        if ("p".equals(sort)) {
            return "price:asc";
        }
        if ("pd".equals(sort)) {
            return "price:des";
        }
        return null;
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

        if ((mOrderby != null) && (mGoodsMenuViewInfoMap != null)) {

            if ((oldPosition < 0) || (oldPosition > mOrderby.length - 1)) {
                return;
            }

            String menuText = mOrderby[oldPosition];
            MenuView_Info menuView_Info = null;
            if ((menuText != null) && (mGoodsMenuViewInfoMap.containsKey(menuText))) {
                menuView_Info = mGoodsMenuViewInfoMap.get(menuText);
            }

            if (menuView_Info != null) {

                if (menuView_Info.lifeUiGridView != null) {
                    int first = menuView_Info.lifeUiGridView.getFirstVisiblePosition();
                    int last = menuView_Info.lifeUiGridView.getLastVisiblePosition();
                    if (last <= first) {
                        return;
                    }

                    for (int i = first; i <= last; i++) {
                        GoodListItemFrameLayout goodListItemFrameLayout = (GoodListItemFrameLayout) menuView_Info.lifeUiGridView
                                .getChildAt(i - first);
                        // 隐藏信息内容
                        //goodListItemFrameLayout.onHideInfoImageView();
                        // 设置默认图
                        goodListItemFrameLayout.onSetPosition(i);
                        goodListItemFrameLayout.onSetGoodsListDefaultDrawable(
                                getResources().getDrawable(R.drawable.ytsdk_touming), i);
                    }
                    menuView_Info.lifeUiGridView.setVisibility(View.GONE);

                }

                if (mGoodsArrayList != null) {
                    ArrayList<SearchedGoods> goodsList = mGoodsArrayList.get(menuText);
                    goodsList.clear();
                }

                if (menuView_Info.lifeUiGridView != null) {
                    menuView_Info.lifeUiGridView.removeAllViewsInLayout();
                    menuView_Info.lifeUiGridView.setVisibility(View.GONE);
                }
                onCleanMenuViewInfo(menuView_Info);
            }

        }
    }

    // 关掉界面并清除数据
    public void onFinsh() {
        finish();
    }

    @Override
    public String getPageName() {
        return TAG;
    }

    @Override
    protected void refreshData() {

    }

    private void onResponseSuccess(GoodsSearchZtcResult data, MenuView_Info save_viewpager_Info, String sort) {
        // 允许按返回键取消进度条
//        setProgressCancelable(true);
//        OnWaitProgressDialog(false);
        if (isHasDestroyActivity() || data == null) {
            return;
        }

        SearchedGoods[] businessgoods = data.getGoodList();

        int mResultsTotalCounts = 0;

        if (businessgoods != null) {
            mResultsTotalCounts = businessgoods.length;
        }

        // 返回的数据结果是0
        if (mResultsTotalCounts == 0) {
            // 如果是第一次请求数据, 那么认为当前关键字，或类目--没有商品
            if (mFirstStartRequestData) {
                // 没有商品， 则提示
                mHaveGoods = false;
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
            }
            // 再次请求也没有数据，认为结束
            else {
                save_viewpager_Info.End_Request = true;
            }
            return;
        }

        save_viewpager_Info.HasRequestData_ChangeMenu = true;

        // 增加实际返回的结果数
        save_viewpager_Info.ReturnTotalResults += mResultsTotalCounts;

        // 如果实际返回数大于最大值，则结束
        if (save_viewpager_Info.ReturnTotalResults >= VISUALCONFIG.GOODS_MAX_TOTAL) {
            save_viewpager_Info.End_Request = true;
        }

//        ArrayList<SearchedGoods> msortIndexMap = null;
//        if (mGoodsArrayList != null) {
//            msortIndexMap = mGoodsArrayList.get(sort);
//        }
//
//        if ((businessgoods != null) && (msortIndexMap != null)) {
        if ((businessgoods != null)) {

            List<SearchedGoods> goodsList = Arrays.asList(businessgoods);
            try {
                JSONArray jsonArray = new JSONArray();
                for (int i = 0; i < goodsList.size(); i++) {
                    SearchedGoods goods = goodsList.get(i);
                    String itemId = goods.getItemId();
                    String itemS11Pre = goods.getS11Pre();
                    String priceWmTemp = goods.getWmPrice();// 无线端价格，如果没有则显示sku最优价格
                    String priceTemp = goods.getPrice(); //sku最优价格

                    if (TextUtils.isEmpty(priceWmTemp)) {
                        priceWmTemp = priceTemp;
                    }
                    if (priceWmTemp != null) {
                        priceWmTemp = priceWmTemp.split("元")[0];
                    }

                    AppDebug.e(TAG, "Rebate itemId = " + itemId + ";itemS11Pre = " + itemS11Pre + ";price =" + priceWmTemp);
                    JSONObject object = new JSONObject();
                    object.put("itemId", itemId);
                    object.put("isPre", itemS11Pre);
                    object.put("price", priceWmTemp);
                    jsonArray.put(object);
                }
                AppDebug.e(TAG, "Rebate" + jsonArray.toString());

                JSONObject object = new JSONObject();
                object.put("umToken", Config.getUmtoken(this));
                object.put("wua", Config.getWua(this));
                object.put("isSimulator", Config.isSimulator(this));
                object.put("userAgent", Config.getAndroidSystem(this));
                String extParams = object.toString();

                mBusinessRequest.requestRebateMoney(jsonArray.toString()
                        , ActivityPathRecorder.getInstance().getCurrentPath(this)
                        , false, false, true, extParams, new GetRebateBusinessRequestListener(new WeakReference<BaseActivity>(this), goodsList, sort, save_viewpager_Info));

            } catch (JSONException e) {
                e.printStackTrace();
            }

//            // 把请求到的数据全部添加到 msortIndexMap 中
//            msortIndexMap.addAll(goodsList);

            // 释放原来的数据
            businessgoods = null;
            goodsList = null;

            save_viewpager_Info.CurReqPageCount++;

        }

//        GoodListLifeUiGridView goodListLifeUiGridView = (GoodListLifeUiGridView) save_viewpager_Info.lifeUiGridView;
//        GoodListGirdViewAdapter goodListGirdViewAdapter = (GoodListGirdViewAdapter) save_viewpager_Info.goodListGirdViewAdapter;
//        int tabnumber = save_viewpager_Info.Index;
//
//        AppDebug.i(TAG, "onRequestDone --> goodListLifeUiGridView = " + goodListLifeUiGridView
//                + ";  goodListGirdViewAdapter = " + goodListGirdViewAdapter + "; tabnumber = " + tabnumber
//                + "; mCurrentSelectTabNumBer = " + mCurrentSelectTabNumBer);
//
//        // 如果请求数据返回时，此数据所关联TAB编号与当前所在的TAB编号一样，那么显示gridview
//        if (goodListLifeUiGridView != null && mCurrentSelectTabNumBer == tabnumber) {
//            goodListLifeUiGridView.setVisibility(View.VISIBLE);
//            if (goodListLifeUiGridView.getChildCount() == 0) {
//                onSetCheckVisibleItemOfAdapter(goodListLifeUiGridView, goodListGirdViewAdapter);
////                goodListGirdViewAdapter.onSetCheckVisibleItem(true);
////                goodListGirdViewAdapter.onCheckVisibleItemAndLoadBitmap();
//            }
//            //// TODO: 12/10/2017
//        }
        // 刚启动APP时先请求数据
        if (mFirstStartRequestData) {
            mFirstStartRequestData = false;
        }
    }

    /**
     * 处理网路请求返回后的结果
     *
     * @param data
     * @param save_viewpager_Info
     * @param sort
     */
    private void onResponseSuccess(GoodsSearchResult data, MenuView_Info save_viewpager_Info, String sort) {
        // 允许按返回键取消进度条
        setProgressCancelable(true);
        OnWaitProgressDialog(false);
        if (isHasDestroyActivity() || data == null) {
            return;
        }

        GoodsTmail[] businessgoods = data.getGoodList();

        int mResultsTotalCounts = 0;

        if (businessgoods != null) {
            mResultsTotalCounts = businessgoods.length;
        }

        // 返回的数据结果是0
        if (mResultsTotalCounts == 0) {
            // 如果是第一次请求数据, 那么认为当前关键字，或类目--没有商品
            if (mFirstStartRequestData) {
                // 没有商品， 则提示
                mHaveGoods = false;
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
            }
            // 再次请求也没有数据，认为结束
            else {
                save_viewpager_Info.End_Request = true;
            }
            return;
        }

        save_viewpager_Info.HasRequestData_ChangeMenu = true;

        // 增加实际返回的结果数
        save_viewpager_Info.ReturnTotalResults += mResultsTotalCounts;

        // 如果实际返回数大于最大值，则结束
        if (save_viewpager_Info.ReturnTotalResults >= VISUALCONFIG.GOODS_MAX_TOTAL) {
            save_viewpager_Info.End_Request = true;
        }

        ArrayList<SearchedGoods> msortIndexMap = null;
        if (mGoodsArrayList != null) {
            msortIndexMap = mGoodsArrayList.get(sort);
        }

        if ((businessgoods != null) && (msortIndexMap != null)) {

            List<GoodsTmail> goodsList = Arrays.asList(businessgoods);

            // 把请求到的数据全部添加到 msortIndexMap 中
            msortIndexMap.addAll(goodsList);

            // 释放原来的数据
            businessgoods = null;
            goodsList = null;

            save_viewpager_Info.CurReqPageCount++;

        }

        GoodListLifeUiGridView goodListLifeUiGridView = (GoodListLifeUiGridView) save_viewpager_Info.lifeUiGridView;
        GoodListGirdViewAdapter goodListGirdViewAdapter = (GoodListGirdViewAdapter) save_viewpager_Info.goodListGirdViewAdapter;
        int tabnumber = save_viewpager_Info.Index;

        AppDebug.i(TAG, "onRequestDone --> goodListLifeUiGridView = " + goodListLifeUiGridView
                + ";  goodListGirdViewAdapter = " + goodListGirdViewAdapter + "; tabnumber = " + tabnumber
                + "; mCurrentSelectTabNumBer = " + mCurrentSelectTabNumBer);

        // 如果请求数据返回时，此数据所关联TAB编号与当前所在的TAB编号一样，那么显示gridview
        if (goodListLifeUiGridView != null && mCurrentSelectTabNumBer == tabnumber) {
            goodListLifeUiGridView.setVisibility(View.VISIBLE);
        }
        // 刚启动APP时先请求数据
        if (mFirstStartRequestData) {
            mFirstStartRequestData = false;
        }
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

        LayoutInflater.from(this).inflate(R.layout.ytsdk_activity_goodslist_layout, tabFocusPositionManager, true);

        tabFocusPositionManager.onInitBackgroud();
        tabFocusPositionManager.setBackgroundResource(R.drawable.ytsdk_ui2_goodslist_bg);

        // 获取标题
        mTitleTextView = (TextView) findViewById(R.id.goodlist_title);

        if ((mTitleTextView != null) && (mTitleText != null)) {

            // 对于 阴影，不进行适配
            mTextShadowPix = 3;

            int len = mTitleText.length();

            if (len > TITLE_NUMBER_MAX) {
                mTitleText = mTitleText.substring(0, TITLE_NUMBER_MAX) + "...";
            }

            mTitleTextView.setShadowLayer(0.5f, mTextShadowPix, mTextShadowPix, mTextShadowColor);
            mTitleTextView.setText(mTitleText);
        }
    }

    /**
     * 获取左侧TAB的适配器
     *
     * @return
     */

    @Override
    protected BaseAdapter getTabAdapter() {
        if (mGoodListGirdMenuAdapter == null) {
            mGoodListGirdMenuAdapter = new GoodListGirdMenuAdapter(this);
            mGoodListGirdMenuAdapter.setMenuLogoRes(mMenuLogo);
            mGoodListGirdMenuAdapter.setMenuLogoOpaqueRes(mMenuLogo_opaque);
            mGoodListGirdMenuAdapter.setTableList(mTableList);
        }
        return mGoodListGirdMenuAdapter;
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

        AppDebug.i(TAG, "getTabGoodsGridView goodListLifeUiGridView = " + goodListLifeUiGridView);

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

        // 创建对应的适配器，并设置参数 
        GoodListGirdViewAdapter adapter = new GoodListGirdViewAdapter(this, false);

        // 设置图片处理
        adapter.setGoodListImageHandle(mImageHandle);

        // 给适配器设置对应排序的商品信息
        if (mGoodsArrayList != null) {
            adapter.onSetGoodsArrayList(mGoodsArrayList.get(tabkey));
        }

        AppDebug.i(TAG, "getTabGoodsAdapter adapter = " + adapter);

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

        AppDebug.i(TAG, "initGirdViewInfo  goodListLifeUiGridView  = " + goodListLifeUiGridView + "; adapter = "
                + adapter + "; tabkey = " + tabkey + "; position = " + position);

        // 创建对应的参数记录信息
        MenuView_Info save_viewpager_Info = mGoodListFocuseUnit.getViewPagerInfo();

        // 当前行
        save_viewpager_Info.CurrentRows = 1;

        // 允许总的行数
        save_viewpager_Info.TotalRows = VISUALCONFIG.ROWS_TOTAL;

        // 当前请求的页数
        save_viewpager_Info.CurReqPageCount = 1;

        // 绑定的排序
        save_viewpager_Info.orderby = tabkey;

        save_viewpager_Info.mInLayout = true;

        // 切换菜单时，未请求数据
        save_viewpager_Info.HasRequestData_ChangeMenu = false;

        // 顶层蒙版显示
        save_viewpager_Info.Mask_display = false;

        save_viewpager_Info.CommonState = save_viewpager_Info.Mask_display;

        // 对应的中文名字
        save_viewpager_Info.table = mTableList[position];

        // 对应的箭头
        save_viewpager_Info.sortFlag = mSortFlag[position];

        // 对应的类目ID
        save_viewpager_Info.catMap = mCatMapId;

        // 对应的搜索引擎
        save_viewpager_Info.tab = mTab;

        // 对应的关键字
        save_viewpager_Info.keyWords = mKeyWord;
        save_viewpager_Info.Index = position;
        save_viewpager_Info.lifeUiGridView = goodListLifeUiGridView;
        save_viewpager_Info.goodListGirdViewAdapter = adapter;

        // 关于TBS埋点的信息
        save_viewpager_Info.tbs_p = mTBS_P[position];
        save_viewpager_Info.tbs_name = mTBS_Name[position];

        // 设置实际返回数为0
        save_viewpager_Info.ReturnTotalResults = 0;

        // 设置当前请求还没有结束
        save_viewpager_Info.End_Request = false;

        mGoodsMenuViewInfoMap.put(tabkey, save_viewpager_Info);

        goodListLifeUiGridView.setNextFocusRightId(R.id.fiv_pierce_home_focusd);
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


    /**
     * 根据TAB编号，获取tabkey
     *
     * @param tabNumBerPos
     * @return
     */

    @Override
    protected String getTabKeyWordOfTabNumBer(int tabNumBerPos) {

        AppDebug.i(TAG, "getTabKeyWordOfTabNumBer tabNumBerPos = " + tabNumBerPos);

        if ((mOrderby != null) && (tabNumBerPos >= 0) && (tabNumBerPos < mOrderby.length)) {
            return mOrderby[tabNumBerPos];
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
    protected String getUriOfEnterDetail(FocusFlipGridView focusFlipGridView, String tabkey, AdapterView<?> arg0, View arg1, int position, long arg3) {
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

        return mItemGoods.getUri();
    }

    @Override
    protected String getTitleOfEnterDetail(FocusFlipGridView focusFlipGridView, String tabkey, AdapterView<?> arg0, View arg1, int position, long arg3) {
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

        return mItemGoods.getTitle();
    }

    @Override
    protected String getEurlOfEnterdetail(FocusFlipGridView focusFlipGridView, String tabkey, AdapterView<?> arg0, View arg1, int position, long arg3) {
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

        return mItemGoods.getEurl();
    }

    @Override
    protected String getPicUrlOfEnterDetail(FocusFlipGridView focusFlipGridView, String tabkey, AdapterView<?> arg0, View arg1, int position, long arg3) {
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

        return mItemGoods.getImgUrl();
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
                if (GoodListActivity.this == null || GoodListActivity.this.isFinishing()) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        OnWaitProgressDialog(false);
                        String name = getResources().getString(com.yunos.tvtaobao.businessview.R.string.ytbv_not_open);
                        Toast.makeText(GoodListActivity.this, name, Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    /**
     * 获取TAB Focus的资源ID
     *
     * @return
     */
    @Override
    protected int getTabFocusDrawableId() {
        return R.drawable.ytsdk_ui2_goodlist_left_focus;
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

        currentGridview.setId(mTableListId[currentTabNumber]);

        setGridViewId(mTableListId[currentTabNumber]);

        AppDebug.i(TAG, "handlerChangeTab currentTabNumber = " + currentTabNumber + ";  currentGridview = "
                + currentGridview + "; oldTabNumber = " + oldTabNumber + "; oldGridview = " + oldGridview
                + "; change = " + change);

        String tabText = getTabKeyWordOfTabNumBer(currentTabNumber);
        if (TextUtils.isEmpty(tabText)) {
            return;
        }
        MenuView_Info current_Info = mGoodsMenuViewInfoMap.get(tabText);
        if (current_Info == null) {
            return;
        }

        // 触发埋点
        Map<String, String> p = initTBSProperty();
        if (!TextUtils.isEmpty(current_Info.tbs_name)) {
            p.put("name", current_Info.tbs_name);
        }
        p.put("spm", SPMConfig.GOODS_LIST_SPM_LIST_P_NAME);

//        TBS.Adv.ctrlClicked(CT.Button, Utils.getControlName(getFullPageName(), "P", null, current_Info.tbs_p),
//                Utils.getKvs(p));

        Utils.utControlHit(getFullPageName(), "Button-TbGoodsSeachResult_P_" + current_Info.tbs_p, p);


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

                requestSearch(onFindSdkViewPagerFromMap(current_Info.Index));
            } else {

                // 保存当前请求的GirdView编号
                mCurrentRequest_GirdViewNum = current_Info.Index;

                // 添加监听
                setNetworkOkDoListener(mGoodListNetworkOkDoListener);

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
            mShadowTop.setVisibility(View.INVISIBLE);
        }

        // 改变背景
        onShowCommonBackgroud(current_Info);

        View currentview = null;
        if (mTabFocusListView != null) {
            int firstnum = mTabFocusListView.getFirstVisiblePosition();
            currentview = mTabFocusListView.getChildAt(currentTabNumber - firstnum);
        }

        AppDebug.i(TAG, "handlerChangeTab currentTabNumber = " + currentTabNumber + ";  currentview = " + currentview);

        if (currentview instanceof GoodListMenuFocusFrameLayout && currentview != null) {
            GoodListMenuFocusFrameLayout menuView = (GoodListMenuFocusFrameLayout) currentview;
            if (menuView != null) {
                menuView.setMenuChangeOfSelectState(true);
            }
        }

        // 只有当前选中的TAB与之前的不相同，才改变状态 
        View oldview = null;
        if (mTabFocusListView != null) {
            int firstnum = mTabFocusListView.getFirstVisiblePosition();
            oldview = mTabFocusListView.getChildAt(oldTabNumber - firstnum);
        }

        AppDebug.i(TAG, "handlerChangeTab oldTabNumber = " + oldTabNumber + ";  oldview = " + oldview);

        if (oldview instanceof GoodListMenuFocusFrameLayout && oldview != null) {
            GoodListMenuFocusFrameLayout menuView = (GoodListMenuFocusFrameLayout) oldview;
            if (menuView != null) {
                menuView.setMenuChangeOfSelectState(false);
            }
        }
    }

    private static final class KeyUpEventHandler extends Handler {

        WeakReference<GoodListActivity> weakReference;

        public KeyUpEventHandler(WeakReference<GoodListActivity> weakReference) {
            this.weakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            GoodListActivity goodListActivity = weakReference.get();
            if (goodListActivity != null) {
            }
        }
    }

    private static class MyWeakRefBusinessRequestListenerZTC extends BizRequestListener<GoodsSearchZtcResult> {

        private final MenuView_Info save_viewpager_Info;
        private final String sort;

        public MyWeakRefBusinessRequestListenerZTC(WeakReference<BaseActivity> mBaseActivityRef,
                                                   MenuView_Info save_viewpager_Info, String sort) {

            super(mBaseActivityRef);
            this.save_viewpager_Info = save_viewpager_Info;
            this.sort = sort;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            GoodListActivity goodListActivity = (GoodListActivity) mBaseActivityRef.get();
            if (goodListActivity != null) {
                AppDebug.v(TAG, TAG + ".onError.resultCode = " + resultCode + ".msg = " + msg);
                // 允许按返回键取消进度条
                goodListActivity.setProgressCancelable(true);
                goodListActivity.OnWaitProgressDialog(false);
            }
            return false;
        }

        @Override
        public void onSuccess(GoodsSearchZtcResult data) {
            GoodListActivity goodListActivity = (GoodListActivity) mBaseActivityRef.get();
            if (goodListActivity != null) {
                AppDebug.v(TAG, TAG + ".onSuccess.data = " + data);
                goodListActivity.onResponseSuccess(data, save_viewpager_Info, sort);
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return true;
        }

    }

    private static class MyWeakRefBusinessRequestListener extends BizRequestListener<GoodsSearchResult> {

        private final MenuView_Info save_viewpager_Info;
        private final String sort;

        public MyWeakRefBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef,
                                                MenuView_Info save_viewpager_Info, String sort) {

            super(mBaseActivityRef);
            this.save_viewpager_Info = save_viewpager_Info;
            this.sort = sort;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            GoodListActivity goodListActivity = (GoodListActivity) mBaseActivityRef.get();
            if (goodListActivity != null) {
                AppDebug.v(TAG, TAG + ".onError.resultCode = " + resultCode + ".msg = " + msg);
                // 允许按返回键取消进度条
                goodListActivity.setProgressCancelable(true);
                goodListActivity.OnWaitProgressDialog(false);
            }
            return false;
        }

        @Override
        public void onSuccess(GoodsSearchResult data) {
            GoodListActivity goodListActivity = (GoodListActivity) mBaseActivityRef.get();
            if (goodListActivity != null) {
                AppDebug.v(TAG, TAG + ".onSuccess.data = " + data);
                goodListActivity.onResponseSuccess(data, save_viewpager_Info, sort);
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return true;
        }

    }


    private class GetRebateBusinessRequestListener extends BizRequestListener<List<RebateBo>> {
        private List<SearchedGoods> goodsList;
        private String sort;
        MenuView_Info save_viewpager_Info;


        public GetRebateBusinessRequestListener(WeakReference<BaseActivity> baseActivityRef, List<SearchedGoods> goodsList, String sort, MenuView_Info save_viewpager_Info) {
            super(baseActivityRef);
            this.goodsList = goodsList;
            this.sort = sort;
            this.save_viewpager_Info = save_viewpager_Info;

        }

        @Override
        public boolean onError(int resultCode, String msg) {
            AppDebug.e(TAG, "resultCode = " + resultCode + ",msg = " + msg);
            GoodListActivity goodListActivity = (GoodListActivity) mBaseActivityRef.get();
            if (goodListActivity != null) {
                AppDebug.v(TAG, TAG + ".onError.resultCode = " + resultCode + ".msg = " + msg);
                // 允许按返回键取消进度条
                goodListActivity.setProgressCancelable(true);
                goodListActivity.OnWaitProgressDialog(false);
            }
            return false;
        }

        @Override
        public void onSuccess(List<RebateBo> data) {
            AppDebug.e(TAG, "List<RebateBo> = " + data.toString());
            setProgressCancelable(true);
            OnWaitProgressDialog(false);
            if (data != null && data.size() > 0) {
                for (RebateBo rebateBo : data) {
                    for (SearchedGoods searchedGoods : goodsList) {
                        if (rebateBo != null && searchedGoods != null && rebateBo.getItemId().equals(searchedGoods.getItemId())) {
                            searchedGoods.setRebateBo(rebateBo);
                            break;
                        }
                    }
                }
            }
            ArrayList<SearchedGoods> msortIndexMap = null;
            if (mGoodsArrayList != null) {
                msortIndexMap = mGoodsArrayList.get(sort);
            }

            // 把请求到的数据全部添加到 msortIndexMap 中
            msortIndexMap.addAll(goodsList);


            GoodListLifeUiGridView goodListLifeUiGridView = (GoodListLifeUiGridView) save_viewpager_Info.lifeUiGridView;
            GoodListGirdViewAdapter goodListGirdViewAdapter = (GoodListGirdViewAdapter) save_viewpager_Info.goodListGirdViewAdapter;
            int tabnumber = save_viewpager_Info.Index;

            AppDebug.i(TAG, "onRequestDone --> goodListLifeUiGridView = " + goodListLifeUiGridView
                    + ";  goodListGirdViewAdapter = " + goodListGirdViewAdapter + "; tabnumber = " + tabnumber
                    + "; mCurrentSelectTabNumBer = " + mCurrentSelectTabNumBer);

            // 如果请求数据返回时，此数据所关联TAB编号与当前所在的TAB编号一样，那么显示gridview
            if (goodListLifeUiGridView != null && mCurrentSelectTabNumBer == tabnumber) {
                goodListLifeUiGridView.setVisibility(View.VISIBLE);
                if (goodListLifeUiGridView.getChildCount() == 0) {
                    onSetCheckVisibleItemOfAdapter(goodListLifeUiGridView, goodListGirdViewAdapter);
//                goodListGirdViewAdapter.onSetCheckVisibleItem(true);
//                goodListGirdViewAdapter.onCheckVisibleItemAndLoadBitmap();
                }
                //// TODO: 12/10/2017
            }


        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    /**
     * 网络断开重新连接的监听类
     *
     * @author yunzhong.qyz
     */
    private static class GoodListNetworkOkDoListener implements NetworkOkDoListener {

        private final WeakReference<GoodListActivity> reference;

        public GoodListNetworkOkDoListener(WeakReference<GoodListActivity> reference) {
            this.reference = reference;
        }

        @Override
        public void todo() {
            GoodListActivity goodListActivity = reference.get();
            AppDebug.i(TAG, "todo --> goodListActivity = " + goodListActivity);
            if (goodListActivity != null) {
                // 网络重连后，重新请求数据
                int currentRequest_GirdViewNum = goodListActivity.mCurrentRequest_GirdViewNum;
                String menuText = goodListActivity.onFindSdkViewPagerFromMap(currentRequest_GirdViewNum);
                if (!TextUtils.isEmpty(menuText)) {
                    goodListActivity.requestSearch(menuText);
                }
            }
        }
    }

    /**
     * 进入搜索结果界面
     */
    private void gotoSearchResult(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            AppDebug.v(TAG, TAG + ". gotoSearchResult.keyword == null");
            return;
        }

        try {
            String url = "tvtaobao://home?module=goodsList&keywords=" + keyword + "&tab=mall&"
                    + CoreIntentKey.URI_FROM_APP + "=" + getAppName();

            AppDebug.v(TAG, TAG + ", gotoSearchResult.uri = " + url);
            Intent intent = new Intent();

            //设置外部来源
            setFrom("sound");

            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            AppDebug.v(TAG, TAG + ". gotoSearchResult." + getString(R.string.ytm_home_start_activity_error));
            Toast.makeText(GoodListActivity.this, getString(R.string.ytm_home_start_activity_error), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

}
