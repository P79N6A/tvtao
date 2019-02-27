package com.yunos.tvtaobao.detailbundle.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.tvtaobao.voicesdk.bo.DomainResultVo;
import com.tvtaobao.voicesdk.bo.PageReturn;
import com.tvtaobao.voicesdk.register.type.ActionType;
import com.tvtaobao.voicesdk.utils.LogPrint;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.ActivityQueueManager;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.common.DeviceJudge;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tv.core.util.ActivityPathRecorder;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tv.core.util.IntentDataUtil;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tv.core.util.SharedPreferencesUtils;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.activity.CoreActivity;
import com.yunos.tvtaobao.biz.activity.TaoBaoBlitzActivity;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.FeiZhuBean;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.request.bo.JoinGroupResult;
import com.yunos.tvtaobao.biz.request.bo.MockData;
import com.yunos.tvtaobao.biz.request.bo.ProductTagBo;
import com.yunos.tvtaobao.biz.request.bo.TBDetailResultV6;
import com.yunos.tvtaobao.biz.request.bo.Unit;
import com.yunos.tvtaobao.biz.request.info.GlobalConfigInfo;
import com.yunos.tvtaobao.biz.request.utils.DetailV6Utils;
import com.yunos.tvtaobao.biz.request.ztc.ZTCUtils;
import com.yunos.tvtaobao.detailbundle.R;
import com.yunos.tvtaobao.detailbundle.bean.FlashsaleGoodsInfo;
import com.yunos.tvtaobao.detailbundle.bean.NewDetailPanelData;
import com.yunos.tvtaobao.detailbundle.bean.SkuType;
import com.yunos.tvtaobao.detailbundle.flash.DateUtils;
import com.yunos.tvtaobao.detailbundle.resconfig.IResConfig;
import com.yunos.tvtaobao.detailbundle.type.DetailModleType;
import com.yunos.tvtaobao.detailbundle.view.DetailBuilder;
import com.yunos.tvtaobao.detailbundle.view.DetailFocusPositionManager;
import com.yunos.tvtaobao.detailbundle.view.FlashsaleBuilder;
import com.yunos.tvtaobao.detailbundle.view.NewDetailScrollInfoView;
import com.yunos.tvtaobao.detailbundle.view.NewDetailView;
import com.yunos.tvtaobao.detailbundle.view.NewRightPanel;
import com.yunos.tvtaobao.detailbundle.view.NewTimerTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NewDetailActivity extends TaoBaoBlitzActivity {
    public static final String TAG = "Page_TbDetail";
    //    埋点渠道参数
    //预售
    public static final String BUSINESS_YUSHOU = "yushou";
    //聚划算
    public static final String BUSINESS_JUHUASUAN = "juhuasuan";
    //天猫超市
    public static final String BUSINESS_TIANMAOCHAOSHI = "tianmaochaoshi";
    //天猫国际
    public static final String BUSINESS_TIANMAOGUOJI = "tianmaoguoji";
    //其他
    public static final String BUSINESS_QITA = "qita";
    //  埋点商品状态参数
    //预售
    public static final String STATUS_FUDINGJING = "fudingjin";
    //聚划算
    public static final String STATUS_MASHANGQING = "mashangqiang";
    //天猫超市
    public static final String STATUS_QITA = "qita";

    //    埋点渠道参数
    private String business = "qita";
    //  埋点商品状态参数
    private String goodsStatus = "qita";

    // 左上角图标的位置
    private final String ICO_SPACE = "  ";

    // 是否可购买，防止用户连续按OK键，直接进入购买页面
    private boolean canBuy = false;

    //详情数据对象
    public TBDetailResultV6 tbDetailResultV6;

    //打标标签对象
    private ProductTagBo mProductTagBo;

    // 网络请求
    private BusinessRequest mBusinessRequest;

    // UI界面
    public NewDetailView mDetailView;
    private FlashsaleBuilder mFlashsaleBuilder;

    // 详情页面的数据处理
    private DetailBuilder mDetailBuilder;

    // 商品ID号
    public String mItemId = null;

    // 资源配置[天猫，或者淘宝]
    private IResConfig resConfig;

    //扩展参数,如淘抢购商品显示渠道专享价
    private String extParams;

    // 是否是直通车商品
    private boolean isZTC;

    // 详细来源
    private String source;

    // 界面类型
    private DetailModleType mModleType;

    // 淘抢购
    private FlashsaleGoodsInfo mFlashsaleGoodsInfo;

    //    //聚焦
//    private DetailFocusPositionManager focusPositionManager;
    private boolean isFeizhu;


//    public Handler mHandler = new Handler();

    private String uriPrice;

    private GlobalConfig globalConfig;

    private boolean mIsBackHome;

    private String tag_path;
    private Unit unit;

    //图文详情
    private NewDetailScrollInfoView mDetailScrollInfoView;
    //右侧的panel
    public NewRightPanel mRightPanel;

    //头图Image
    private String toutuImg = "";


    private boolean isPre = false;
    private String nowPrice;

    private String cartFrom;

    boolean isSimpleOn = SharePreferences.getBoolean("isSimpleOn", false);//容灾开关，全局变量里配置生效

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DeviceJudge.getMemoryType() != DeviceJudge.MemoryType.HighMemoryDevice) {
            ActivityQueueManager.getInstance().pushActivity(NewDetailActivity.class.getName(), this, 1);
        } else {
            ActivityQueueManager.getInstance().pushActivity(NewDetailActivity.class.getName(), this, 2);
        }
        AppDebug.e(TAG, "NewDetailActivity------");
        setContentView(R.layout.activity_new_detail);
        globalConfig = GlobalConfigInfo.getInstance().getGlobalConfig();
        mIsBackHome = isBackHome();

        mModleType = DetailModleType.NORMAL;

        String channleCode = getIntent().getStringExtra(CoreIntentKey.URI_CHANNEL_CODE);
        String channelName = getIntent().getStringExtra(CoreIntentKey.URI_CHANNEL_NAME);
        if (channleCode != null) {
            TvOptionsConfig.setTvOptionsChannel(channleCode);
        }
        String from = getIntent().getStringExtra(CoreIntentKey.URI_FROM);
        if ("voice_application".equals(from) || "voice_system".equals(from)) {
            //直接用语音打开详情页的情况，不经过任何频道
            TvOptionsConfig.setTvOptionVoiceSystem(from);
        }
        mItemId = getIntent().getStringExtra(BaseConfig.INTENT_KEY_ITEMID);
        extParams = getIntent().getStringExtra(BaseConfig.INTENT_KEY_EXTPARAMS);
        uriPrice = getIntent().getStringExtra(BaseConfig.INTENT_KEY_PRICE);
        cartFrom = getIntent().getStringExtra(BaseConfig.INTENT_KEY_CARTFROM);
        String ztcSource = getIntent().getStringExtra(BaseConfig.INTENT_KEY_ISZTC);
        if (TextUtils.isEmpty(ztcSource)) {
            isZTC = false;
        } else {
            isZTC = ztcSource.equals("true") ? true : false;
        }

        source = getIntent().getStringExtra(BaseConfig.INTENT_KEY_SOURCE);
        tag_path = ActivityPathRecorder.getInstance().getCurrentPath(this) + "";

        AppDebug.d("ActivityPathRecorder", "recorded path:" + tag_path);

        //淘抢购
        String taoqianggou = getIntent().getStringExtra(BaseConfig.INTENT_KEY_FLASHSALE_ID);
        if (!TextUtils.isEmpty(taoqianggou)) {
//            if (TextUtils.equals(status, FlashsaleGoodsInfo.STATUS_FUTURE)) {
            mFlashsaleGoodsInfo = new FlashsaleGoodsInfo();
            mFlashsaleGoodsInfo.itemId = mItemId;
            mFlashsaleGoodsInfo.qianggouId = getIntent().getStringExtra(BaseConfig.INTENT_KEY_FLASHSALE_ID);
            mFlashsaleGoodsInfo.price = getIntent().getStringExtra(BaseConfig.INTENT_KEY_FLASHSALE_PRICE);
            //根据status状态，确定这个time是startTime还是endTime
            mFlashsaleGoodsInfo.time = getIntent().getStringExtra(BaseConfig.INTENT_KEY_FLASHSALE_TIMER);
            //status状态，0：过去 1：现在 2：将来
            mFlashsaleGoodsInfo.status = getIntent().getStringExtra(BaseConfig.INTENT_KEY_FLASHSALE_STATUS);
            try {
                mFlashsaleGoodsInfo.time = DateUtils.dateToStamp(mFlashsaleGoodsInfo.time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
//            }
            mModleType = DetailModleType.QIANGOU;
        }
        if (TextUtils.isEmpty(mItemId) && getIntent().hasExtra("data")) {
            processWvtIntent();
        } else {
            initData();
        }

    }

    @Override
    public void interceptBack(boolean isIntercept) {

    }


    private void initData() {
        AppDebug.i(TAG, "onCreate ---> mItemID = " + mItemId + "; extParams = " + extParams
                + "; mFlashsaleGoodsInfo = " + mFlashsaleGoodsInfo);
        if (TextUtils.isEmpty(mItemId)) {
            finish();
            return;
        }
        onInitDetailValue();
        setDetailViewListener();
        requestLoadDetail();
        setTimeDoneListener();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", "commodity");
            jsonObject.put("shopId", "");
            jsonObject.put("commodityId", mItemId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AppDebug.e("TAG", "上报数据===" + jsonObject.toString());
        final String isJoin = IntentDataUtil.getString(getIntent(), CoreIntentKey.URI_JOIN, null);
    }


    private void processWvtIntent() {
        OnWaitProgressDialog(true);
        String data = getIntent().getStringExtra("data");
        AppDebug.d("test", "encrypt data:" + data);
        String decrypt = Encrypt.decryptAESCipherText(data, "AES_KEY_ZTC_0605");
        ZTCUtils.getZtcItemId(this, decrypt, new ZTCUtils.ZTCItemCallback() {
            @Override
            public void onSuccess(String id) {
                OnWaitProgressDialog(false);
                mItemId = id;
                isZTC = true;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initData();
                    }
                });
            }

            @Override
            public void onFailure(String msg) {
                OnWaitProgressDialog(false);
                showErrorDialog("找不到商品", true);
            }
        });
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
                AppDebug.e(TAG, "isBackHome===111" + isbackhome);
            } else {
                isbackhome = true;
                AppDebug.e(TAG, "isBackHome===222");
            }

            AppDebug.e(TAG, "isBackHome===");
        }
        AppDebug.i(TAG, "isBackHome isbackhome = " + isbackhome);
        return isbackhome;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDetailScrollInfoView != null) {
            mDetailScrollInfoView.onCleanAndDestroy();
        }
        if (mFlashsaleBuilder != null) {
            mFlashsaleBuilder.onDestroy();
        }
        if (mDetailView != null) {
            mDetailView.onDestroy();
        }
        if (mRightPanel != null) {
            mRightPanel.onDestroy();
        }
//        if (mHandler != null) {
//            mHandler.removeCallbacksAndMessages(null);
//        }
        ZTCUtils.destroy();
        ActivityQueueManager.getInstance().onRemoveDestroyActivityFromList(NewDetailActivity.class.getName(), this);
        System.gc();
    }

    @Override
    public String getPageName() {
        return TAG;
    }

    @Override
    protected void refreshData() {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        canBuy = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppDebug.i(TAG, "NewDetailActivity resultCode=" + resultCode + " requestCode=" + requestCode);
        // 支付成功后关闭
        if (resultCode == Activity.RESULT_OK && requestCode == BaseConfig.ACTIVITY_REQUEST_PAY_DONE_FINISH_CODE) {
            if (data != null) {
                boolean payResult = data.getBooleanExtra("pay_result", false);
                // 支付成功，或支付失败创建订单成功
                if (payResult) {
                    finish();
                }
            }
        }
    }

    /**
     * 初始化详情页面的变量值
     */
    private void onInitDetailValue() {
        mBusinessRequest = BusinessRequest.getBusinessRequest();
        mDetailView = new NewDetailView(new WeakReference<NewDetailActivity>(this));
        mRightPanel = new NewRightPanel(new WeakReference<NewDetailActivity>(this));

        if (globalConfig != null) {
            if (globalConfig.getShopCartFlag() != null) {
                mRightPanel.showMagicalMCart(globalConfig.getShopCartFlag().isActing());
            }
            if (globalConfig.getDetail_goods_info() != null) {
                if (globalConfig.getDetail_goods_info().getDetailShare() != null) {
                    mRightPanel.setIvShare11Address(globalConfig.getDetail_goods_info().getDetailShare());
                }
                if (globalConfig.getDetail_goods_info().getDetailTopButton() != null) {
                    mDetailView.setIvTopButtonAddress(globalConfig.getDetail_goods_info().getDetailTopButton());
                }
                if (globalConfig.getDetail_goods_info().getDetailTopButton() != null) {
                    mDetailView.setIvTopButtonAddress(globalConfig.getDetail_goods_info().getDetailTopButton());
                }
                if (globalConfig.getDetail_goods_info().getShowAllPrice() != null) {
                    mDetailView.setIsShowAllPrice(globalConfig.getDetail_goods_info().getShowAllPrice());
                }

            }
        }

        mDetailScrollInfoView = new NewDetailScrollInfoView(new WeakReference<Activity>(this));
//        mBusinessRequest.requestProductTag(mItemId, ActivityPathRecorder.getInstance().getCurrentPath(this), isZTC, source, new GetProductTagListener(new WeakReference<BaseActivity>(this)));
//        if (getModleType() == DetailModleType.QIANGOU && mFlashsaleGoodsInfo != null) {
//            if (!TextUtils.isEmpty(mFlashsaleGoodsInfo.time)) {
//                long startmillisTime = 0L;
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
//                try {
//                    Date date = simpleDateFormat.parse(mFlashsaleGoodsInfo.time);
//                    if (date != null) {
//                        startmillisTime = date.getTime();
//                    }
//
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                mFlashsaleBuilder = new FlashsaleBuilder(this, startmillisTime);
//                mFlashsaleBuilder.setFlashsaleInfo(mFlashsaleGoodsInfo.qianggouId, mFlashsaleGoodsInfo.time);
//            }
//        }

    }

    /**
     * 获取界面展示的类型
     *
     * @return
     */
    public DetailModleType getModleType() {
        AppDebug.i(TAG, "getModleType:" + mModleType);
        return mModleType;
    }

    /**
     * 检查网络状态
     *
     * @return
     */
    private boolean checkNetwork() {
        boolean result;
        if (!NetWorkUtil.isNetWorkAvailable()) {
            result = false;
            showNetworkErrorDialog(false);
        } else {
            removeNetworkOkDoListener();
            result = true;
        }
        return result;
    }

    /**
     * 获取FocusPositionManager
     *
     * @return
     */
    public DetailFocusPositionManager getFocusPositionManager() {
        return null;
    }

    /**
     * 初始化埋点信息
     *
     * @return
     */
    public Map<String, String> initTBSProperty(String spm) {
        Map<String, String> p = Utils.getProperties();
        if (!TextUtils.isEmpty(mItemId)) {
            p.put("item_id", mItemId);
        }
        if (tbDetailResultV6 != null && tbDetailResultV6.getItem() != null) {
            if (!TextUtils.isEmpty(tbDetailResultV6.getItem().getTitle())) {
                p.put("item_name", tbDetailResultV6.getItem().getTitle());
            }
        }
        if (!TextUtils.isEmpty(getAppName()) && !TextUtils.isEmpty(AppInfo.getAppVersionName())) {
            p.put("from_app", getAppName() + AppInfo.getAppVersionName());
        }
        if (CoreApplication.getLoginHelper(CoreApplication.getApplication()).isLogin()) {
            p.put("is_login", "1");
        } else {
            p.put("is_login", "0");
        }

        if (!TextUtils.isEmpty(business)) {
            p.put("business", business);
        }
        if (!TextUtils.isEmpty(goodsStatus)) {
            p.put("status", goodsStatus);
        }

        if (!TextUtils.isEmpty(spm)) {
            p.put("spm", spm);

        }
        return p;
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> p = super.getPageProperties();
        if (!TextUtils.isEmpty(mItemId)) {
            p.put("item_id", mItemId);
        }
        if (tbDetailResultV6 != null && tbDetailResultV6.getItem() != null
                && !TextUtils.isEmpty(tbDetailResultV6.getItem().getTitle())) {
            p.put("item_name", tbDetailResultV6.getItem().getTitle());
        }
        p.put(SPMConfig.SPM_CNT, "a2o0j.7984570.0.0");
        if (CoreApplication.getLoginHelper(CoreApplication.getApplication()).isLogin()) {
            p.put("is_login", "1");
        } else {
            p.put("is_login", "0");
        }
        if (!TextUtils.isEmpty(getAppName()) && !TextUtils.isEmpty(AppInfo.getAppVersionName())) {
            p.put("from_app", getAppName() + AppInfo.getAppVersionName());
        }
        return p;
    }

//    // 添加收藏
//    public boolean manageFav() {
//        OnWaitProgressDialog(true);
//        String func = "";
//        if (isFav) {
//            func = "delAuction";
//        } else {
//            func = "addAuction";
//        }
//
//        mBusinessRequest.manageFav(mItemId, func, new ManageFavBusinessRequestListener(new WeakReference<BaseActivity>(
//                this)));
//        return true;
//    }

    public void setBuyButton() {
        if (!checkNetwork()) {
            return;
        }
        if (mDetailBuilder == null) {
            AppDebug.e(TAG, TAG + ".setBuyButton data is null");
            return;
        }
        if (mDetailBuilder.isSupportBuy()) {
            buy();
        } else {
//            Utils.utControlHit(getPageName(), "Button_Gocart", initTBSProperty());
            Utils.utControlHit(getPageName(), "Button_Cart", initTBSProperty(SPMConfig.NEW_DETAIL_BUTTON_CART));
            Utils.updateNextPageProperties(SPMConfig.NEW_DETAIL_BUTTON_CART);

            addCart();
        }
    }

    public void setAddCartButton() {
        Utils.utControlHit(getPageName(), "Button_Cart", initTBSProperty(SPMConfig.NEW_DETAIL_BUTTON_CART));
        Utils.updateNextPageProperties(SPMConfig.NEW_DETAIL_BUTTON_CART);
        if (!CoreApplication.getLoginHelper(NewDetailActivity.this).isLogin()) {
            setLoginActivityStartShowing();
            CoreApplication.getLoginHelper(NewDetailActivity.this).startYunosAccountActivity(NewDetailActivity.this,
                    false);
            return;
        }
        addCart();
    }

    public void setScanQrCodetButton() {
        Utils.utControlHit(getPageName(), "Button_Shop", initTBSProperty(SPMConfig.NEW_DETAIL_BUTTON_SHOP));
        Utils.updateNextPageProperties(SPMConfig.NEW_DETAIL_BUTTON_SHOP);

        if (mDetailBuilder != null && mDetailBuilder.isSuperMarket) {
//            Utils.utControlHit(getPageName(), "Button_Market", initTBSProperty());
            gotoChaoshi();
        } else {
//            Utils.utControlHit(getPageName(), "Button_Shop", initTBSProperty());
            gotoShopIndex(null);

        }
    }

    private void setDetailViewListener() {
//        mDetailView.setBuyButtonListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!checkNetwork()) {
//                    return;
//                }
//                buy();
//            }
//        });
//
//        mDetailView.setAddCartButtonListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Utils.utControlHit(getPageName(), "Button_Cart", initTBSProperty());
//
//                addCart();
//
//            }
//        });
//        mDetailView.setScanQrCodetButtonListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });

//        mRightPanel.ivEvaluation.setOnClickListener(this);
    }

    // 加入购物,
    private void addCart() {
        if (mDetailBuilder == null) {
            AppDebug.e(TAG, TAG + ".addCart data is null");
            return;
        }
        //不支持购买直接返回
        if (!mDetailBuilder.isSupportAddCart() || mModleType == DetailModleType.HUAFEICHONGZHI) {
            showNotbuyDialog();
            return;
        }
        if (!mDetailBuilder.isSupportBuy() && mModleType == DetailModleType.SECKKILL) {
            showNotbuyDialog();
            return;

        } else if (mDetailBuilder.isSupportBuy() && mModleType == DetailModleType.SECKKILL) {
            showQRCode(getString(R.string.ytbv_qr_buy_item), false);
            return;
        }

        Intent intent = new Intent();
        intent.setClassName(this, BaseConfig.SWITCH_TO_SKU_ACTIVITY);
        intent.putExtra(BaseConfig.INTENT_KEY_REQUEST_TYPE, SkuType.ADD_CART);
        intent.putExtra("mTBDetailResultVO", tbDetailResultV6);
        intent.putExtra(BaseConfig.INTENT_KEY_EXTPARAMS, extParams);
        if (!TextUtils.isEmpty(cartFrom))
            intent.putExtra(BaseConfig.INTENT_KEY_CARTFROM, cartFrom);
        intent.putExtra(BaseConfig.INTENT_KEY_ISZTC, isZTC);
        if (source != null)
            intent.putExtra(BaseConfig.INTENT_KEY_SOURCE, source);
        intent.putExtra(BaseConfig.INTENT_KEY_PRICE, uriPrice);
        if (mProductTagBo != null) {
            intent.putExtra("mProductTagBo", mProductTagBo);
        }
        startActivity(intent);
    }

    //    // 进入店铺
    private boolean gotoShopIndex(String flag) {

        OnWaitProgressDialog(false);
        Intent intent = new Intent();
        GlobalConfig globalConfig = GlobalConfigInfo.getInstance().getGlobalConfig();
        if (globalConfig == null || !globalConfig.isBlitzShop()) {
            intent.setClassName(this, BaseConfig.SWITCH_TO_SHOP_ACTIVITY);
        } else {
            intent.setClassName(this, BaseConfig.SWITCH_TO_SHOP_BLIZ_ACTIVITY);
        }
        if (tbDetailResultV6 == null || tbDetailResultV6.getSeller() == null) {
            return true;
        }
        intent.putExtra(BaseConfig.SELLER_SHOPID, String.valueOf(tbDetailResultV6.getSeller().getShopId()));
        String sellerType = resConfig.getGoodsType() == IResConfig.GoodsType.TAOBAO ? BaseConfig.SELLER_TAOBAO
                : BaseConfig.SELLER_TMALL;
        intent.putExtra(BaseConfig.SELLER_TYPE, sellerType);
        if (!tag_path.contains("module=shop")) //判断是否从店铺会场进入
            intent.putExtra(ActivityPathRecorder.INTENTKEY_FIRST, true);
        if ("Coupon".equals(flag)) {
            intent.putExtra(BaseConfig.SHOP_FROM, flag);
        }
        startActivity(intent);
        return true;
    }


//    // 打开图文详情
//    public void showRichText() {
//        if (isShowFullDesc) {
//            return;
//        }
//        isShowFullDesc = true;
//        Intent intent = new Intent(this, DetailFullDescActivity.class);
//        intent.putExtra("mTBDetailResultVO", tbDetailResultV6);
//        Asrintent.putExtra("extParams", extParams);
//        intent.putExtra(SDKInitConfig.INTENT_KEY_ITEMID, mItemId);
//        intent.putExtra(SDKInitConfig.INTENT_KEY_PRICE, uriPrice);
//        intent.putExtra("buyText", buyText);
//        if (mProductTagBo!=null){
//            intent.putExtra("mProductTagBo", mProductTagBo);
//        }
//        startActivity(intent);
//    }

    // 打开评论界面
    private void showEvaluate() {
        Intent intent = new Intent(this, DetailEvaluateActivity.class);
        intent.putExtra("mTBDetailResultVO", tbDetailResultV6);
        intent.putExtra(BaseConfig.INTENT_KEY_ITEMID, mItemId);
        startActivity(intent);
    }

    // 打开超市界面
    private void gotoChaoshi() {
        Intent intent = new Intent();
        intent.setClassName(this, BaseConfig.SWITCH_TO_CHAOSHI_ACTIVITY);
        startActivity(intent);
    }

    /**
     * 不能购买时，单击按钮，弹出对话框提示
     */
    private void showNotbuyDialog() {

        String message = "";
        Unit tbDetailResultV6Util = DetailV6Utils.getUnit(tbDetailResultV6);
        if (tbDetailResultV6Util != null) {
            if (tbDetailResultV6Util.getTrade() != null) {
                if (tbDetailResultV6Util.getTrade().getHintBanner() != null) {
                    if (tbDetailResultV6Util.getTrade().getHintBanner().getText() != null) {
                        message = tbDetailResultV6Util.getTrade().getHintBanner().getText();
                    } else {
                        message = getString(R.string.ytsdk_confirm_cannot_buy);
                    }
                } else {
                    message = getString(R.string.ytsdk_confirm_cannot_buy);
                }
            } else {
                message = getString(R.string.ytsdk_confirm_cannot_buy);
            }
            showErrorDialog(message, false);
        } else {
            if (DetailV6Utils.getMockdata(tbDetailResultV6) != null) {
                MockData mockdata = DetailV6Utils.getMockdata(tbDetailResultV6);
                if (!mockdata.getTrade().isBuyEnable()) {
                    message = getString(R.string.ytsdk_confirm_cannot_buy);
                    showErrorDialog(message, false);
                }
            } else {
                if (mModleType == DetailModleType.SECKKILL) {
                    message = getString(R.string.ytsdk_confirm_cannot_buy);
                    showErrorDialog(message, false);

                }
            }
        }
    }

    /**
     * 是否支持购买
     *
     * @return
     */
    private boolean isBuySupport() {
        if (tbDetailResultV6 != null) {
            Unit tbDetailResultV6Util = DetailV6Utils.getUnit(tbDetailResultV6);
            if (tbDetailResultV6Util != null) {
                if (tbDetailResultV6Util.getTrade().getBuyEnable() != null && tbDetailResultV6Util.getTrade().getBuyEnable().equals("false")) {
                    return false;
                }
            } else {
                MockData tbDetailResultV6MockData = DetailV6Utils.getMockdata(tbDetailResultV6);
                if (tbDetailResultV6MockData != null) {
                    if (!tbDetailResultV6MockData.getTrade().isBuyEnable()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 天猫超市且不支持购买
     *
     * @return
     */
    private boolean isBuySupportNotSuperMarket() {

        if (mDetailBuilder.isSuperMarket && !isBuySupport()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 是否显示淘宝积分
     *
     * @return
     */
    private void isVisbilePoint() {
        //如果商品不支持直接购买或者是降级商品,则不显示积分
        AppDebug.d(TAG, "isValidateblackFilter:" + isValidateblackFilter + "isShowPoint:" + isShowPoint + "isBuySupportNotSuperMarket:" + isBuySupportNotSuperMarket() + "isBuySupportNotSuperMarket:");
//            if (isValidateblackFilter || !isBuySupportNotSuperMarket()/* || !TextUtils.isEmpty(mTBDetailResultVO.itemControl.degradedItemUrl)*/){
//                mDetailView.setTaobaoPointVisibilityState(GONE);
//            }else{
//                mDetailView.setTaobaoPointVisibilityState(VISIBLE);
//            }
        if (isFeizhu || mModleType == DetailModleType.HUAFEICHONGZHI) {
            mDetailView.setTaobaoPointVisibilityState(View.GONE);
            AppDebug.e(TAG, "积分不可见，飞猪或者预售或者话费充值");
            return;
        }
        if (isValidateblackFilter) {
            mDetailView.setTaobaoPointVisibilityState(View.GONE);
            AppDebug.e(TAG, "积分不可见isValidateblackFilter");
            return;
        }
        if (tbDetailResultV6.getTrade() != null && !TextUtils.isEmpty(tbDetailResultV6.getTrade().getRedirectUrl()) && !mDetailBuilder.isSuperMarket) {
            mDetailView.setTaobaoPointVisibilityState(View.GONE);
            AppDebug.e(TAG, "积分不可见降级或isSuperMarket");
            return;
        }
        if (!isShowPoint) {
            mDetailView.setTaobaoPointVisibilityState(View.GONE);
            AppDebug.e(TAG, "积分不可见!isShowPoint");
            return;
        }
        //海尔商品积分不可见
        String appkey = SharePreferences.getString("device_appkey", "");
        String brandName = SharePreferences.getString("device_brandname", "");
        Log.d(TAG, TAG + ".isVisbilePoint appkey : " + appkey + " ,brandName : " + brandName);

        mDetailView.setTaobaoPointVisibilityState(View.VISIBLE);
        if (appkey.equals("10004416") && brandName.equals("海尔")) {
            AppDebug.e(TAG, "海尔商品积分!isShowPoint");
            mDetailView.setTaobaoPointVisibilityState(View.GONE);
        }

    }

    /**
     * 购买流程
     *
     * @return
     */
    private boolean buy() {
        if (tbDetailResultV6 == null) {
            AppDebug.e(TAG, TAG + ",buy detail date is null");
            return true;
        }
        if (addcartview == View.GONE && goShopView == View.GONE) {

//            Utils.utControlHit(getPageName(), "Button_Goshop", initTBSProperty());
            Utils.utControlHit(getPageName(), "Button_Shop", initTBSProperty(SPMConfig.NEW_DETAIL_BUTTON_SHOP));
            Utils.updateNextPageProperties(SPMConfig.NEW_DETAIL_BUTTON_SHOP);
            if (mDetailBuilder.isSuperMarket) {
                gotoChaoshi();
            } else {
                gotoShopIndex(null);
            }
            return true;
        }
        if (mModleType == DetailModleType.SECKKILL) {//秒杀商品现在的逻辑如果可以买就弹二维码，如果不可以就提示不可购买
            if (tbDetailResultV6.getTrade().getBuyEnable() != null && tbDetailResultV6.getTrade().getBuyEnable().equals("true")) {
                showQRCode(getString(R.string.ytbv_qr_buy_item), false);
            } else {
                showNotbuyDialog();
            }
            return true;
        }
        //不支持购买直接返回
        if (!canBuy || !isBuySupport()) {
            if (getModleType() == DetailModleType.JUHUASUAN) {
                showErrorDialog(buyText, false);
                return true;
            }
        }
        if (!canBuy || !isBuySupport()) {
            showNotbuyDialog();
            return true;
        }

        Map<String, String> p = getPageProperties();
        if (mModleType == DetailModleType.PRESALE) {
            p.put("is_prebuy", "true");
        }
        //飞猪商品提示扫码购买
        if (isFeizhu || mModleType == DetailModleType.HUAFEICHONGZHI) {
            String controlName = Utils.getControlName(getFullPageName(), "QRCode_dlg", null);
            String text = getString(R.string.ytbv_qr_buy_item);
            if (mModleType == DetailModleType.PRESALE) {
                text = getString(R.string.ytbv_qr_buy_pre_item);
                controlName = Utils.getControlName(getFullPageName(), "QRCode_dlg_prepay", null);
            }

            showQRCode(text, isFeizhu);
            Utils.utCustomHit(getFullPageName(), controlName, p);
            return true;
        }

        // 统计购按钮


//        if (buyText.equals("立即付定金")) {
//            Utils.utControlHit(getPageName(), "Button_Deposit", initTBSProperty());
//        } else if (buyText.equals("马上抢")) {
//            Utils.utControlHit(getPageName(), "Button_Limit", initTBSProperty());
//        } else {
//
//            Utils.utControlHit(getPageName(), "Button_Buy", initTBSProperty());
//        }

        Utils.utControlHit(getPageName(), "Button_Buy", initTBSProperty(SPMConfig.NEW_DETAIL_BUTTON_BUY));
        Utils.updateNextPageProperties(SPMConfig.NEW_DETAIL_BUTTON_BUY);

        boolean isJuhuasuan = false;
        Unit tbDetailResultV6Util = DetailV6Utils.getUnit(tbDetailResultV6);
        if (tbDetailResultV6Util != null) {
            if (tbDetailResultV6Util.getVertical() != null) {
                if (tbDetailResultV6Util.getVertical().getJhs() != null)
                    isJuhuasuan = true;
            }
        }
        // 跳转到选择商品页面,如果是聚划算商品,先参团
        sureJoin(isJuhuasuan);

        // 购买之前先进行预下单
        //creatOrder(isJuhuasuan);

        return true;
    }

    /**
     * 根据商品的ID号请求数据
     */
    private void requestLoadDetail() {
        OnWaitProgressDialog(true);
        //淘抢购的extParams为"umpChannel=qianggou&u_channel=qianggou"
        String params = Utils.jsonString2HttpParam(extParams);
        if (Config.isDebug()) {
            AppDebug.v(TAG, TAG + ".requestLoadDetail.itemId = " + mItemId + ".extParams = " + extParams + ",params = "
                    + params);
        }
        getNewDetail();
//        mBusinessRequest.requestGetItemDetailV6New(mItemId, params, new GetItemDetailOfV6BusinessRequestListener(new WeakReference<BaseActivity>(this)));
//        // 请求详情页面的内容
//        mBusinessRequest.requestGetFullItemDesc(mItemId, new GetFullItemDescBusinessRequestListener(
//                new WeakReference<BaseActivity>(this)));
    }

    /**
     * 自定义淘客详情页打点
     */
    private void anaylisysTaoke() {
        if (CoreApplication.getLoginHelper(getApplicationContext()).isLogin()) {

            //淘客登录打点

            long historyTime = SharedPreferencesUtils.getTaoKeLogin(NewDetailActivity.this);
            long currentTime = System.currentTimeMillis();
            AppDebug.d(TAG, "TaokeLoginAnalysis  currentTime =  " + currentTime + "historyTime = " + historyTime);
            if (currentTime >= historyTime) {
                mBusinessRequest.requestTaokeLoginAnalysis(User.getNick(), new TaokeLoginBussinessRequestListener(new WeakReference<BaseActivity>(this)));
            }

            String sellerId = tbDetailResultV6.getSeller().getUserId() + "";
            String ShopType = "";
            if (mDetailBuilder.getResConfig().getGoodsType() == IResConfig.GoodsType.TMALL) {
                ShopType = "B"; //天猫
            } else {
                ShopType = "C";//淘宝
            }
            AppDebug.d(TAG, "anaylisysTaoke User.sellerId " + sellerId);
            if (!TextUtils.isEmpty(sellerId)) {
                String stbId = DeviceUtil.initMacAddress(getApplicationContext());
                mBusinessRequest.requestTaokeDetailAnalysis(stbId, User.getNick(), mItemId, ShopType, sellerId, new TaokeBussinessRequestListener(new WeakReference<BaseActivity>(this)));
            }
        }
    }

    public static long getDistanceDays(String date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        long days = 0;
        try {
            Date time = df.parse(date);//String转Date
            Date now = new Date();//获取当前时间
            long time1 = time.getTime();
            long time2 = now.getTime();
            long diff = time1 - time2;
            days = diff / (1000 * 60 * 60 * 24);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return days;//正数表示在当前时间之后，负数表示在当前时间之前
    }


//    /**
//     * 检查商品是否已被收藏
//     *
//     * @param itemId
//     */
//    private void checkFav(String itemId) {
//        AppDebug.e(TAG, "isFav" + itemId);
//        mBusinessRequest.checkFav(itemId, new CheckFavBusinessRequestListener(new WeakReference<BaseActivity>(this)));
//    }


    /**
     * 检查商品的积分是否进入黑名单
     */
    private void checkTaobaoPointValidateblackfilter(String categoryId) {

        String itemId = tbDetailResultV6.getItem().getItemId();
        AppDebug.e(TAG, TAG + ".categoryId data is null" + categoryId + ":" + itemId);
        mBusinessRequest.getTaobaoPointValidateblackfilter(categoryId, itemId, new GetItemDetailValidateblackfilterListener(new WeakReference<BaseActivity>(this)));
    }


    /**
     * 获取后台类目信息
     */
    private void getstdCats() {
        String categoryId = tbDetailResultV6.getItem().getCategoryId();
        AppDebug.e(TAG, TAG + ".categoryId data is null" + categoryId);
        if (categoryId != null)
            mBusinessRequest.getstdcats(categoryId, new getstdCatsRequestListener(new WeakReference<BaseActivity>(this)));
    }


    /**
     * 显示二维码扫描
     */
    private void showQRCode(String text, boolean isfeizhu) {
        if (resConfig == null) {
            AppDebug.e(TAG, TAG + ".showQRCode data is null");
            return;
        }

        Bitmap icon = null;
        Drawable drawable = resConfig.getQRCodeIcon();
        if (drawable != null) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            icon = bd.getBitmap();
        }

        AppDebug.v(TAG, TAG + ".showQRCode.mItemId = " + mItemId + ", icon = " + icon);
        showItemQRCodeFromItemId(text, mItemId, icon, true, null, isfeizhu);
    }

    /**
     * 跳转到选择商品页面,如果是聚划算商品,先参团
     */
    private void sureJoin(boolean isJuhuasuan) {
        if (isHasDestroyActivity()) {
            return;
        }

        Intent intent = new Intent();
        intent.setClassName(this, BaseConfig.SWITCH_TO_SKU_ACTIVITY);
        intent.putExtra("mTBDetailResultVO", tbDetailResultV6);
        intent.putExtra("extParams", extParams);
        intent.putExtra("isZTC", isZTC);
        if (source != null)
            intent.putExtra(BaseConfig.INTENT_KEY_SOURCE, source);
        intent.putExtra(BaseConfig.INTENT_KEY_PRICE, uriPrice);
        if (mProductTagBo != null) {
            intent.putExtra("mProductTagBo", mProductTagBo);
        }

        if (!isJuhuasuan) {
            startActivityForResult(intent, BaseConfig.ACTIVITY_REQUEST_PAY_DONE_FINISH_CODE);
            OnWaitProgressDialog(false);
        } else {
            // 参团
            AppDebug.e(TAG, "参团啦");
            mBusinessRequest.requestJoinGroup(mItemId, new JoinGroupBusinessRequestListener(
                    new WeakReference<BaseActivity>(this), intent));
        }
    }

    private void fillFeizhuView() {
        AppDebug.e(TAG, "根据返回的商品详情填充view");
        if (tbDetailResultV6 == null || tbDetailResultV6.getItem() == null || feiZhuBean == null) {
            return;
        }

        if (mDetailView == null) {
            return;
        }

        final NewDetailPanelData detailPanelData = new NewDetailPanelData();

        //setDetailFlashsaleBuilderListen();

        String showname = "";
        if (tbDetailResultV6.getSeller() != null) {
            if (tbDetailResultV6.getSeller().getShopName() != null) {
                showname = tbDetailResultV6.getSeller().getShopName();
            }
        }


        // 快递费用
        String deliveryFee = "";
        AppDebug.e(TAG, "飞猪快递费用不显示" + deliveryFee);
        // 商品标题
        String title = tbDetailResultV6.getItem().getTitle();
        AppDebug.e(TAG, "商品标题=" + title);


        // 月销量
        int totalSold = 0;

        if (feiZhuBean != null && !TextUtils.isEmpty(feiZhuBean.getSoldCount())) {
            totalSold = Integer.parseInt(feiZhuBean.getSoldCount());
            AppDebug.e("feizhu", "详情页展示销量" + totalSold);
        }

        // 服务承诺
        List<String> guarantees = new ArrayList<>();
        //飞猪接口中的服务承诺

        if (feiZhuBean != null) {
            if (feiZhuBean.getService() != null) {
                guarantees = feiZhuBean.getService();
            }
        }

        // 活动价格
        String price = "";
        if (feiZhuBean.getNewPrice() != null) {
            price = feiZhuBean.getNewPrice();
            AppDebug.e(TAG, "活动价格=" + price);
        }
        // 原价
        String originalPrice = "";
        if (feiZhuBean.getOldPrice() != null) {
            originalPrice = feiZhuBean.getOldPrice();
            AppDebug.e(TAG, "原价=" + originalPrice);
        }

        //  商品状态
        if (feiZhuBean != null) {
            if (!feiZhuBean.getBuyText().equals("")) {
                buyText = feiZhuBean.getBuyText();
            } else {
                buyText = "立即购买";
            }
        }
        AppDebug.e(TAG, "商品状态=" + buyText);
        if (mDetailView != null) {
            SpannableString ss = new SpannableString(ICO_SPACE + showname);
            Drawable d;

            d = getResources().getDrawable(R.drawable.shop_feizhu_icon);
            if (d != null) {
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
                ss.setSpan(span, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }

            if (feiZhuBean != null) {

                //飞猪出签率
                String rightDesc = "";
                if (feiZhuBean.getRightDesc() != null) {
                    rightDesc = feiZhuBean.getRightDesc();
                    if (rightDesc.contains("出签率")) {
                        rightDesc = rightDesc.replace("出签率", "出签率:");
                    }
                    detailPanelData.rightDesc = rightDesc;
                }

                //飞猪里程标签内容flayerTitle
                if (feiZhuBean.getMileageTitle() != null) {
                    detailPanelData.mileageTitle = feiZhuBean.getMileageTitle();
                    if (feiZhuBean.getFlayerTitle() != null) {
                        detailPanelData.flayerTitle = feiZhuBean.getFlayerTitle();
                    }
                }
            }

            detailPanelData.title = ss;
            detailPanelData.goodTitle = title;
            detailPanelData.soldNum = getResources().getString(R.string.ytsdk_detail_sold_desc) + totalSold + getString(R.string.ytsdk_unit);
            detailPanelData.postage = deliveryFee;
            detailPanelData.services = guarantees;

            if (!TextUtils.isEmpty(uriPrice)) {
                detailPanelData.nowPrice = uriPrice;
            } else {
                detailPanelData.nowPrice = price;
            }
            detailPanelData.oldPrice = originalPrice;
            detailPanelData.detailModleType = mModleType;
            detailPanelData.hasCoupon = mDetailBuilder.isHasCoupon();


            if (tbDetailResultV6.getItem() != null && tbDetailResultV6.getItem().getImages() != null && tbDetailResultV6.getItem().getImages().get(0) != null) {
                String imgUrl = tbDetailResultV6.getItem().getImages().get(0);
                if (imgUrl.startsWith("//")) {
                    imgUrl = "http:" + imgUrl;
                }
                toutuImg = "<p align=\"middle\"><img  src=\"" + imgUrl + "\" width=\"790\"/></p>";
                detailPanelData.toutuUrl = tbDetailResultV6.getItem().getImages().get(0);
            }

            if (globalConfig != null && globalConfig.getDetail_goods_info() != null && globalConfig.getDetail_goods_info().getDetailPanel() != null) {
                detailPanelData.marketingIconPanel = globalConfig.getDetail_goods_info().getDetailPanel();
            }
            //图文详情
            mBusinessRequest.requestGetFullItemDesc(mItemId, new GetFullItemDescBusinessRequestListener(
                    new WeakReference<BaseActivity>(this)));
            detailPanelData.mItemID = mItemId;
            mDetailView.setGoodsInfo(detailPanelData);
            mRightPanel.setTitle(detailPanelData.goodTitle);
            mRightPanel.setDetailPanelData(detailPanelData);

            nowPrice = detailPanelData.nowPrice;

//            mDetailView.setGoodsBuyButton(buyText, isBuySupport());
            mDetailView.setGoodsBuyButtonText(DetailModleType.FEIZHU, "", "", buyText, isBuySupport());

        }


        if (mDetailView != null && mDetailBuilder != null) {

            // 处理二维码按钮和加入购物车的显示状态
            int addcartview = View.GONE;
            if (mDetailBuilder.isSupportAddCart()) {
                addcartview = View.VISIBLE;
            }
            int goShopView = View.VISIBLE;
            mDetailView.setButtonVisibilityState(addcartview, goShopView);
        }

        if (tbDetailResultV6 != null && tbDetailResultV6.getSeller() != null && tbDetailResultV6.getSeller().getUserId() != null) {
            String sellerId = String.valueOf(tbDetailResultV6.getSeller().getUserId());
            mRightPanel.getShopCoupon(mBusinessRequest, sellerId);
        }
        mRightPanel.getEvaluationNumData(mBusinessRequest);
        mBusinessRequest.requestProductTag(mItemId, ActivityPathRecorder.getInstance().getCurrentPath(this),
                isZTC, source, isPre, detailPanelData.nowPrice, this, new GetProductTagListener(new WeakReference<BaseActivity>(this)));

    }

    String buyText = null;
    int goShopView = View.VISIBLE;
    int addcartview = View.VISIBLE;
    String typeStatus = "";
    String typeTime = "";
    boolean jhsAndMarket = false;


    /**
     * 根据返回的商品详细填充view
     */
    private void fillView() {
        AppDebug.e(TAG, "根据返回的商品详情填充view");
        if (tbDetailResultV6 == null || tbDetailResultV6.getItem() == null) {
            return;
        }

        if (mDetailView == null) {
            return;
        }

        final NewDetailPanelData detailPanelData = new NewDetailPanelData();
        Unit tbDetailResultV6Util = DetailV6Utils.getUnit(tbDetailResultV6);
        if (tbDetailResultV6Util != null) {
            unit = tbDetailResultV6Util;
        } else {
            return;
        }
        //埋点渠道参数：商品只传一个渠道 预售>聚划算>天猫超市>天猫国际>其他
        if (unit != null && unit.getVertical() != null) {
            if (unit.getVertical().getPresale() != null) {
                business = BUSINESS_YUSHOU;
                isPre = true;
            } else if (unit.getVertical().getJhs() != null) {
                business = BUSINESS_JUHUASUAN;
            } else if (unit.getVertical().getSupermarket() != null) {
                business = BUSINESS_TIANMAOCHAOSHI;
            } else if (unit.getVertical().getInter() != null) {
                business = BUSINESS_TIANMAOGUOJI;
            } else {
                business = BUSINESS_QITA;
            }
        }


        //聚划算商品
        if (unit != null && unit.getVertical() != null) {

            if (unit.getVertical().getJhs() != null) {
                mModleType = DetailModleType.JUHUASUAN;
                if (unit.getVertical().getJhs().getStatus() != null && unit.getVertical().getJhs().getStatus().equals("0")) {
                    mFlashsaleBuilder = new FlashsaleBuilder(getApplicationContext(), Long.parseLong(unit.getVertical().getJhs().getStartTime()));
                } else {
                    mFlashsaleBuilder = new FlashsaleBuilder(getApplicationContext(), Long.parseLong(unit.getVertical().getJhs().getEndTime()));
                }
            }
        }
        //天猫国际，进口税
        String tax = "";
        if (unit != null && unit.getVertical() != null) {
            if (unit.getVertical().getInter() != null) {
                mModleType = DetailModleType.TIANMAOGUOJI;
                if (unit.getVertical().getInter().getTariff() != null) {
                    if (unit.getVertical().getInter().getTariff().getValue() != null) {
                        tax = unit.getVertical().getInter().getTariff().getValue();
                    }
                }
            }
        }
        detailPanelData.tax = tax;

        // 快递费用
        String deliveryFee = "";
        if (unit != null) {
            if (unit.getDelivery() != null && unit.getDelivery().getPostage() != null) {
                deliveryFee = unit.getDelivery().getPostage();
            } else {
                deliveryFee = "免运费";
            }

            if (unit.getDelivery() != null && unit.getDelivery().getTo() != null) {
                deliveryFee = deliveryFee + "(至 " + unit.getDelivery().getTo() + ")";
            }

        } else if (tbDetailResultV6.getDelivery() != null) {
            if (tbDetailResultV6.getDelivery().getPostage() != null) {
                deliveryFee = tbDetailResultV6.getDelivery().getPostage();
            }
        }
        String showname = "";
        if (tbDetailResultV6.getSeller() != null) {
            if (tbDetailResultV6.getSeller().getShopName() != null) {
                showname = tbDetailResultV6.getSeller().getShopName();
            }
        }

        AppDebug.e(TAG, "快递费用=" + deliveryFee);
        // 商品标题
        String title = tbDetailResultV6.getItem().getTitle();
        AppDebug.e(TAG, "商品标题=" + title);
        // 月销量
        int totalSold = 0;
        if (unit != null) {
            if (tbDetailResultV6.getItem() != null && unit.getItem().getSellCount() != null) {
                try {
                    totalSold = Integer.parseInt(unit.getItem().getSellCount());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        AppDebug.e(TAG, "月销量=" + totalSold);

        //发货
        String deliverGoods = "";
        if (unit != null) {
            if (unit.getVertical() != null && unit.getVertical().getPresale() != null && unit.getVertical().getPresale().getExtraText() != null) {
                deliverGoods = unit.getVertical().getPresale().getExtraText();
            }
        }
        AppDebug.e(TAG, "发货=" + deliverGoods);
        detailPanelData.deliverGoods = deliverGoods;


        // 服务承诺
        List<String> guarantees = new ArrayList<>();
        if (unit != null) {
            if (unit.getConsumerProtection() != null) {
                if (unit.getConsumerProtection().getItems() != null) {
                    List<Unit.ConsumerProtectionBean.ItemsBeanX> items = unit.getConsumerProtection().getItems();
                    for (int i = 0; i < items.size(); i++) {

                        if (items.get(i) != null && !TextUtils.isEmpty(items.get(i).getTitle())) {
                            String titleConsumerProtection = items.get(i).getTitle();
                            if (!titleConsumerProtection.contains("花呗") && !titleConsumerProtection.contains("蚂蚁")
                                    && !titleConsumerProtection.contains("分期")) {
                                guarantees.add(items.get(i).getTitle());
                                if (guarantees.size() > 2) {
                                    break;
                                }
                            }
                        }
                    }

                }
            }
        }

        // 活动价格
        String price = "";
        if (unit != null) {
            if (unit.getPrice() != null) {
                if (unit.getPrice().getPrice() != null) {
                    price = unit.getPrice().getPrice().getPriceText();
                }
            }
        } else {
            if (mModleType == DetailModleType.SECKKILL) {
                if (tbDetailResultV6.getPrice() != null) {
                    if (tbDetailResultV6.getPrice().getPrice() != null) {
                        if (tbDetailResultV6.getPrice().getPrice().getPriceText() != null) {
                            price = tbDetailResultV6.getPrice().getPrice().getPriceText();
                        }
                    }
                }
            }
        }
        AppDebug.e(TAG, "活动价格=" + price);

        // 原价
        String originalPrice = "";
        String originalPriceLineThrough = "true";
        if (unit != null) {
            if (unit.getPrice() != null) {
                if (unit.getPrice().getExtraPrices() != null && unit.getPrice().getExtraPrices().size() > 0) {
                    originalPrice = unit.getPrice().getExtraPrices().get(0) == null ? "" : unit.getPrice().getExtraPrices().get(0).getPriceText();
                    originalPriceLineThrough = unit.getPrice().getExtraPrices().get(0) == null ? "" : unit.getPrice().getExtraPrices().get(0).getLineThrough();
                }
            }
        }
        AppDebug.e(TAG, "原价=" + originalPrice);


        //String timeTip = "";
        //  商品状态：先拿buytext，如果有就显示，如果没有且buyenable为false就为暂不支持购买，没有且为true就为马上购买
        if (unit != null) {
            if (tbDetailResultV6 != null && unit.getTrade() != null) {
                if (unit.getTrade().getBuyText() != null) {
                    buyText = unit.getTrade().getBuyText();
                } else if (unit.getTrade().getBuyText() == null && !TextUtils.isEmpty(unit.getTrade().getBuyEnable()) && unit.getTrade().getBuyEnable().equals("true")) {
                    buyText = "立即购买";
                    AppDebug.e(TAG, "立即购买 ");
                } else {
                    AppDebug.e(TAG, "暂不支持购买");
                    buyText = "暂不支持购买";
                }
            }
        } else {
            if (mModleType == DetailModleType.SECKKILL) {
                if (tbDetailResultV6.getTrade() != null) {
                    if (tbDetailResultV6.getTrade().getBuyEnable() != null) {
                        if (tbDetailResultV6.getTrade().getBuyEnable().equals("true")) {
                            buyText = "立即购买";
                        } else {
                            buyText = "暂不支持购买";
                        }
                        AppDebug.e(TAG, "秒杀是否可购买= " + buyText);
                    }
                }

            }


        }

        if (getModleType() == DetailModleType.QIANGOU) {
            AppDebug.e(TAG, "抢购=");
            if (mFlashsaleGoodsInfo != null) {
                if (mFlashsaleGoodsInfo.status.equals("2")) {
                    //为开团
                    detailPanelData.status = 0;
                    typeStatus = "0";
                } else if (mFlashsaleGoodsInfo.status.equals("1")) {
                    detailPanelData.status = 1;
                    typeStatus = "1";
                }
                typeTime = mFlashsaleGoodsInfo.time;
            }

            if (unit != null && unit.getConsumerProtection() != null && unit.getConsumerProtection().getChannel() != null) {
                if (unit.getConsumerProtection().getChannel().getTitle() != null) {
                    detailPanelData.slogo = unit.getConsumerProtection().getChannel().getTitle();
                }
            }

        } else if (getModleType() == DetailModleType.JUHUASUAN) {
            AppDebug.e(TAG, "聚划算=");
            if (unit != null && unit.getPrice() != null && unit.getPrice().getPrice() != null) {
                price = unit.getPrice().getPrice().getPriceText();
            }

            if (unit.getPrice().getExtraPrices() != null) {
                if (!unit.getPrice().getExtraPrices().isEmpty() && unit.getPrice().getExtraPrices().size() > 0) {

                    originalPrice = unit.getPrice().getExtraPrices().get(0) == null ? "" : unit.getPrice().getExtraPrices().get(0).getPriceText();
                    originalPriceLineThrough = unit.getPrice().getExtraPrices().get(0) == null ? "" : unit.getPrice().getExtraPrices().get(0).getLineThrough();

                }
            }
            detailPanelData.status = Integer.parseInt(unit == null ? "0" : unit.getVertical() == null ? "0" : unit.getVertical().getJhs() == null ? "0" : unit.getVertical().getJhs().getStatus());
            typeStatus = (unit == null ? "" : unit.getVertical() == null ? "" : unit.getVertical().getJhs() == null ? "" : unit.getVertical().getJhs().getStatus());
            if (detailPanelData.status == 0) {
                typeTime = (unit == null ? "" : unit.getVertical() == null ? "" : unit.getVertical().getJhs() == null ? "" : unit.getVertical().getJhs().getStartTime());
            } else if (detailPanelData.status == 1) {
                typeTime = (unit == null ? "" : unit.getVertical() == null ? "" : unit.getVertical().getJhs() == null ? "" : unit.getVertical().getJhs().getEndTime());
            }
            if (detailPanelData.status != 1 && unit.getTrade() != null) {
                AppDebug.e(TAG, "聚划算staus不=1" + detailPanelData.status);
                //商品状态, 0:即将开始，1：可购买，2：有占座，3：卖光了，4：团购已结束
                switch (detailPanelData.status) {
                    case 0:
                        buyText = "即将开始";
                        break;
                    case 2:
                        buyText = "您还有机会,有买家未付款";
                        break;
                    case 3:
                        buyText = "卖光了";
                        goShopView = View.GONE;
                        addcartview = View.GONE;

                        break;
                    case 4:
                        buyText = "已结束";
                        break;
                }
                unit.getTrade().setBuyEnable("false");
                unit.getTrade().setCartEnable("false");
                mDetailBuilder.setSupportAddCart(false);
                mDetailBuilder.setCanScanQrcode(false);
            }
            if (detailPanelData.status == 1 && unit.getTrade() != null) {
                buyText = "马上抢";
                unit.getTrade().setBuyEnable("true");
                unit.getTrade().setCartEnable("true");
                mDetailBuilder.setSupportAddCart(true);
                mDetailBuilder.setCanScanQrcode(true);
            }
            if (unit.getTrade() != null) {
                if (unit.getTrade().getHintBanner() != null) {
                    if (unit.getTrade().getHintBanner().getText() != null) {
                        buyText = unit.getTrade().getHintBanner().getText();
                        unit.getTrade().setBuyEnable("false");
                        unit.getTrade().setCartEnable("false");
                        mDetailBuilder.setSupportAddCart(false);
                        mDetailBuilder.setCanScanQrcode(false);
                    }
                }
            }
        }

        if (mDetailView != null) {
            detailPanelData.soldNum = getResources().getString(R.string.ytsdk_detail_sold_desc) + totalSold + getString(R.string.ytsdk_unit);
            detailPanelData.goodTitle = title;
            detailPanelData.postage = deliveryFee;
            detailPanelData.services = guarantees;
            AppDebug.e(TAG, "detailPanelData.activityPrice = " + detailPanelData.nowPrice);
            detailPanelData.oldPrice = originalPrice;
            detailPanelData.oldPriceLineThrough = originalPriceLineThrough;
            detailPanelData.detailModleType = mModleType;
            //detailPanelData.timerTip = timeTip;
            detailPanelData.hasCoupon = mDetailBuilder.isHasCoupon();
            try {
                if (unit.getPrice() != null) {
                    if (unit.getPrice().getDepositPriceTip() != null) {
                        detailPanelData.depositPriceDesc = unit.getPrice().getDepositPriceTip();
                    }
                    if (unit.getPrice().getPrice() != null) {
                        if (unit.getPrice().getPrice().getPriceText() != null) {
                            detailPanelData.nowPrice = unit.getPrice().getPrice().getPriceText();//定金
                            if (!TextUtils.isEmpty(unit.getPrice().getPrice().getPriceText())) {
                                detailPanelData.nowPriceTitle = unit.getPrice().getPrice().getPriceTitle();
                            }
                        }
                    }

                    if (unit.getPrice().getSubPrice() != null) {
                        if (unit.getPrice().getSubPrice().getPriceText() != null) {
                            detailPanelData.presellPrice = unit.getPrice().getSubPrice().getPriceText();//预售总价
                            if (!TextUtils.isEmpty(unit.getPrice().getSubPrice().getPriceTitle())) {
                                detailPanelData.presellPriceTitle = unit.getPrice().getSubPrice().getPriceTitle();
                                detailPanelData.presellPrice = unit.getPrice().getSubPrice().getPriceText();
                            }
                        }
                    }
                    if (unit.getPrice().getExtraPrices() != null) {//划线价格
                        if (unit.getPrice().getExtraPrices().size() > 0 && unit.getPrice().getExtraPrices().get(0) != null) {
                            detailPanelData.oldPrice = unit.getPrice().getExtraPrices().get(0).getPriceText();
                            detailPanelData.oldPriceLineThrough = unit.getPrice().getExtraPrices().get(0).getLineThrough();
                            if (!TextUtils.isEmpty(unit.getPrice().getExtraPrices().get(0).getPriceTitle())) {
                                detailPanelData.oldPriceTitle = unit.getPrice().getExtraPrices().get(0).getPriceTitle();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!TextUtils.isEmpty(uriPrice)) {
                detailPanelData.nowPrice = uriPrice;
            } else {
                detailPanelData.nowPrice = price;
            }


            if (unit != null && unit.getVertical().getPresale() != null) {//预售商品
                mModleType = DetailModleType.PRESALE;
                isPre = true;
                detailPanelData.detailModleType = mModleType;
                if (!mDetailBuilder.isSupportAddCart()) {
                    addcartview = View.GONE;
                }
                if (unit.getVertical().getPresale().getStatus() != null) {
                    try {
                        detailPanelData.status = Integer.parseInt(unit.getVertical().getPresale().getStatus());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    typeStatus = unit.getVertical().getPresale().getStatus();
                    typeTime = unit.getVertical().getPresale().getText();
//                    if (detailPanelData.status == 2) {
//                        if (unit.getVertical().getPresale().getStartTime() != null) {
//                            typeTime = unit.getVertical().getPresale().getStartTime();
//                        }
//                    }
//                    else if (detailPanelData.status == 1) {
//                        if (unit.getVertical().getPresale().getEndTime() != null) {
//                            typeTime = unit.getVertical().getPresale().getEndTime();
//                        }
//                    }
                }

                if (unit.getVertical().getPresale().getTip() != null) {
                    detailPanelData.lastPriceTip = unit.getVertical().getPresale().getTip();//支付尾款时间
                    mDetailView.setTvPaymentTimeText(detailPanelData.lastPriceTip);
                }

                //预售商品已预订的数量
                if (unit.getVertical().getPresale().getOrderedItemAmount() != null) {
                    detailPanelData.orderedItemAmount = unit.getVertical().getPresale().getOrderedItemAmount();
                }
            }

            if (mModleType == DetailModleType.JUHUASUAN) {
                List<String> strings = new ArrayList<>();
                for (int i = 0; unit.getConsumerProtection() != null && unit.getConsumerProtection().getItems() != null && i < unit.getConsumerProtection().getItems().size(); i++) {
                    String title1 = unit.getConsumerProtection().getItems().get(i).getTitle();
                    if (title1 != null)
                        strings.add(title1);
                }
                detailPanelData.services = strings;

                //聚划算标签
                if (unit != null && unit.getConsumerProtection() != null && unit.getConsumerProtection().getChannel() != null) {
                    if (unit.getConsumerProtection().getChannel().getTitle() != null) {
                        detailPanelData.slogo = unit.getConsumerProtection().getChannel().getTitle();
                    }
                }
            }


            //促销
            String salesPromotion = "";
            if (unit != null && unit.getPrice() != null && unit.getPrice().getShopProm() != null && unit.getPrice().getShopProm().size() > 0) {
                if (unit.getPrice().getShopProm().get(0) != null) {
                    if (unit.getPrice().getShopProm().get(0).getContent() != null && unit.getPrice().getShopProm().get(0).getContent().size() > 0) {
                        salesPromotion = unit.getPrice().getShopProm().get(0).getContent().get(0);
                        if (unit.getPrice().getShopProm().get(0).getIconText() != null) {
                            detailPanelData.salesPromotionIconText = unit.getPrice().getShopProm().get(0).getIconText();
                        }
                    }
                }
            } else {
                if (unit.getResource() != null && unit.getResource().getShopProm() != null && unit.getResource().getShopProm().get(0) != null) {
                    salesPromotion = unit.getResource().getShopProm().get(0).getContent().get(0);
                    if (unit.getResource().getShopProm().get(0).getIconText() != null) {
                        detailPanelData.salesPromotionIconText = unit.getResource().getShopProm().get(0).getIconText();
                    }
                }
            }
            detailPanelData.salesPromotion = salesPromotion;


            //天猫超市
            if (unit != null && unit.getVertical() != null && unit.getVertical().getSupermarket() != null) {
                mModleType = DetailModleType.SUPERMARKET;
                detailPanelData.detailModleType = mModleType;
                if (unit != null && unit.getVertical() != null && unit.getVertical().getJhs() != null) {
                    mModleType = DetailModleType.JUHUASUAN;
                    detailPanelData.detailModleType = mModleType;
                    jhsAndMarket = true;
                }
                if (unit.getVertical().getSupermarket().getWeight() != null) {
                    detailPanelData.weight = unit.getVertical().getSupermarket().getWeight();
                }

                buyText = "加入购物车";

                showname = "";
            }

            SpannableString ss = new SpannableString(ICO_SPACE + showname);
            Drawable d = null;
            if (mDetailBuilder.getResConfig() != null
                    && mDetailBuilder.getResConfig().getGoodsType() == IResConfig.GoodsType.TMALL) {
                // 如果是天猫的商品，那么就设置天猫的图标
                d = getResources().getDrawable(R.drawable.shop_tianmao_icon);
            } else {
                // 否则设置淘宝的图标
                d = getResources().getDrawable(R.drawable.shop_taobao_icon);
            }
            if (getModleType() == DetailModleType.TIANMAOGUOJI) {
                //如果是天猫国际
                d = getResources().getDrawable(R.drawable.shop_tianmao_internation_icon);
            } else if (getModleType() == DetailModleType.SUPERMARKET) {
                //如果是天猫超市
                d = getResources().getDrawable(R.drawable.shop_tianmao_supermarket_icon);
            }

            if (jhsAndMarket) {
                d = getResources().getDrawable(R.drawable.shop_tianmao_supermarket_icon);
            }

            if (d != null) {
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
                ss.setSpan(span, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }


            detailPanelData.title = ss;

            if (tbDetailResultV6.getItem() != null && tbDetailResultV6.getItem().getImages() != null && tbDetailResultV6.getItem().getImages().get(0) != null) {
                String imgUrl = tbDetailResultV6.getItem().getImages().get(0);
                if (imgUrl.startsWith("//")) {
                    imgUrl = "http:" + imgUrl;
                }
                toutuImg = "<p align=\"middle\"><img src=\""
                        + imgUrl + "\" width=\"790\"/></p>";
                Log.d("toutuImg", toutuImg);
                detailPanelData.toutuUrl = tbDetailResultV6.getItem().getImages().get(0);

            }
            if (globalConfig != null && globalConfig.getDetail_goods_info() != null && globalConfig.getDetail_goods_info().getDetailPanel() != null) {
                detailPanelData.marketingIconPanel = globalConfig.getDetail_goods_info().getDetailPanel();
                if (globalConfig.getDetail_goods_info().getShowAll() != null) {
                    detailPanelData.marketingIconPanelShowAll = globalConfig.getDetail_goods_info().getShowAll();
                }
            }

            if (unit != null) {
                if (unit.getResource() != null && unit.getResource().getCoupon() != null
                        && unit.getResource().getCoupon().getCouponList() != null && unit.getResource().getCoupon().getCouponList().size() > 0) {
                    if (unit.getResource().getCoupon().getCouponList().get(0).getTitle() != null && !(unit.getResource().getCoupon().getCouponList().get(0).getTitle().equals("领取优惠券"))) {
                        detailPanelData.couponText = unit.getResource().getCoupon().getCouponList().get(0).getTitle();
                        if (unit.getResource().getCoupon().getCouponList().get(0).getIcon() != null) {
                            detailPanelData.couponIcon = unit.getResource().getCoupon().getCouponList().get(0).getIcon();
                        }
                    }

                }
            }


            //图文详情
            mBusinessRequest.requestGetFullItemDesc(mItemId, new GetFullItemDescBusinessRequestListener(
                    new WeakReference<BaseActivity>(this)));
            detailPanelData.mItemID = mItemId;
            mDetailView.setGoodsInfo(detailPanelData);
            mRightPanel.setTitle(detailPanelData.goodTitle);
            mRightPanel.setDetailPanelData(detailPanelData);

            nowPrice = detailPanelData.nowPrice;
            mBusinessRequest.requestProductTag(mItemId, ActivityPathRecorder.getInstance().getCurrentPath(this),
                    isZTC, source, isPre, detailPanelData.nowPrice, this, new GetProductTagListener(new WeakReference<BaseActivity>(this)));


            if (unit != null && unit.getTrade().getBuyEnable() != null && unit.getTrade().getBuyEnable().equals("false")) {
                if (unit.getTrade().getHintBanner() != null) {
                    if (unit.getTrade().getHintBanner().getText() != null) {
                        buyText = unit.getTrade().getHintBanner().getText();
                    } else {
                        buyText = "暂不支持购买";
                        AppDebug.e(TAG, "暂不支持购买111");
                    }
                }
            }
            mDetailView.setGoodsBuyButtonText(getModleType(), typeStatus, typeTime, buyText, isBuySupport());

            //  埋点商品状态参数
            if (buyText.contains("付定金")) {
                goodsStatus = STATUS_FUDINGJING;

            } else if (buyText.equals("马上抢")) {
                goodsStatus = STATUS_MASHANGQING;
            } else {
                goodsStatus = STATUS_QITA;

            }
        }


        if (mDetailView != null && mDetailBuilder != null) {

            // 处理二维码按钮和加入购物车的显示状态
            if (mDetailBuilder.isSupportAddCart()) {
                addcartview = View.VISIBLE;
            }
            if (!mDetailBuilder.isSupportAddCart() && !mDetailBuilder.isSupportBuy()) {
                addcartview = View.GONE;
                if (unit != null && unit.getTrade() != null) {
                    if (unit.getTrade().getHintBanner() != null) {
                        if (unit.getTrade().getHintBanner().getText() != null) {
                            buyText = unit.getTrade().getHintBanner().getText();
                        } else {
                            buyText = "暂不支持购买";
                            AppDebug.e(TAG, "暂不支持购买222");
                        }
                    } else {
                        if (getModleType() == DetailModleType.JUHUASUAN || getModleType() == DetailModleType.PRESALE
                                || getModleType() == DetailModleType.QIANGOU || getModleType() == DetailModleType.SUPERMARKET) {
                            //聚划算，预售，淘抢购，天猫超市有自己的buytext
                            AppDebug.e(TAG, "暂不支持购买333");
                        } else
                            buyText = "暂不支持购买";
                    }
                }
            }

            //如果是天猫超市的商品,只显示加入购物车按钮
            if (mDetailBuilder.isSuperMarket) {
                addcartview = View.GONE;
                //即不支持购买也不支持加购物车
                if (!mDetailBuilder.isSupportAddCart() && !mDetailBuilder.isSupportBuy()) {
                    addcartview = View.GONE;
                    if (unit.getTrade() != null) {
                        if (unit.getTrade().getHintBanner() != null) {
                            if (unit.getTrade().getHintBanner().getText() != null) {
                                buyText = unit.getTrade().getHintBanner().getText();
                            } else {
                                buyText = "暂不支持购买";
                                AppDebug.e(TAG, "暂不支持购买222");
                            }
                        } else {
                            buyText = "暂不支持购买";
                        }
                    }
                    mDetailView.setGoodsBuyButtonText(DetailModleType.SUPERMARKET, "", "", buyText, mDetailBuilder.isSupportBuy());
//                    mDetailView.setGoodsBuyButton(buyText, mDetailBuilder.isSupportAddCart());
                } else if (!mDetailBuilder.isSupportAddCart() && mDetailBuilder.isSupportBuy()) {
                    mDetailView.setGoodsBuyButtonText(DetailModleType.SUPERMARKET, "", "", "立即购买", mDetailBuilder.isSupportAddCart());
                } else {
                    mDetailView.setGoodsBuyButtonText(DetailModleType.SUPERMARKET, "", "", "加入购物车", mDetailBuilder.isSupportAddCart());

//                    mDetailView.setGoodsBuyButton("加入购物车", mDetailBuilder.isSupportAddCart());
                }
                // 设置添加购物车按钮的监听
//                mDetailView.setBuyButtonListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        if (!checkNetwork()) {
//                            return;
//                        }
//                        if (mDetailBuilder.isSupportBuy()) {
//                            buy();
//                        } else {
////                            TBS.Adv.ctrlClicked(CT.Button, Utils.getControlName(getFullPageName(), "Button_Gocart", null),
////                                    Utils.getKvs(initTBSProperty()));
//                            Utils.utControlHit(getPageName(), "Button_Gocart", initTBSProperty());
//
//                            addCart();
//                        }
//                    }
//                });
            }

            mDetailView.setModleType(getModleType());
            mDetailView.setStatus(typeStatus);
            mDetailView.setButtonVisibilityState(addcartview, goShopView);


        }


//        // 商品图片
//        if (mDetailView != null) {
//            mDetailView.showPicList(tbDetailResultV6.getItem().getImages());
//        }


        if (tbDetailResultV6 != null && tbDetailResultV6.getSeller() != null && tbDetailResultV6.getSeller().getUserId() != null) {
            String sellerId = String.valueOf(tbDetailResultV6.getSeller().getUserId());
            mRightPanel.getShopCoupon(mBusinessRequest, sellerId);
        }
        mRightPanel.getEvaluationNumData(mBusinessRequest);
    }


    /**
     * 根据返回的打标信息，填充活动标签图
     */
    private void fillTagView(ProductTagBo productTagBo) {
        this.mProductTagBo = productTagBo;
        if (mDetailView != null) {
            mDetailView.setTagInfo(productTagBo, isPre);
        }
    }

//    @Override
//    public void onClick(View view) {
////        Utils.utControlHit(getPageName(), "Button_Evaluate", initTBSProperty());
////        showEvaluate();
//
//        Map<String, String> map = initTBSProperty();
//        map.put("spm", "a2o0j.7984570.LzxTbD.Lzx01Evaluate");
//        Utils.utControlHit(getPageName(), "Button_Evaluate", map);
//        Utils.updateNextPageProperties("a2o0j.7984570.LzxTbD.Lzx01Evaluate");
//        showEvaluate();
//    }


    /**
     * 打标活动标签请求监听
     */
    private static class GetProductTagListener extends BizRequestListener<ProductTagBo> {
        public GetProductTagListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(ProductTagBo data) {
            NewDetailActivity detailActivity = (NewDetailActivity) mBaseActivityRef.get();
            if (data != null) {
                data.setItemId(detailActivity.mItemId);
                data.setPre(detailActivity.isPre);
            }
            AppDebug.e("打标数据请求成功", "dddd" + data);
            detailActivity.fillTagView(data);

            if (data == null || "true".equals(data.getPointBlacklisted())) {
                detailActivity.mDetailView.setTaobaoPointVisibilityState(View.GONE);
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    /**
     * 参团请求的监听类
     */
    private static class JoinGroupBusinessRequestListener extends BizRequestListener<JoinGroupResult> {

        private final Intent intent;

        public JoinGroupBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef, Intent intent) {
            super(mBaseActivityRef);
            this.intent = intent;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            NewDetailActivity detailActivity = (NewDetailActivity) mBaseActivityRef.get();
            if (detailActivity != null) {
                detailActivity.OnWaitProgressDialog(false);
            }
            return false;
        }

        @Override
        public void onSuccess(JoinGroupResult data) {
            AppDebug.e(TAG, "参团成功啦");
            NewDetailActivity detailActivity = (NewDetailActivity) mBaseActivityRef.get();
            if (detailActivity != null) {
                detailActivity.OnWaitProgressDialog(false);
                detailActivity.startActivityForResult(intent, BaseConfig.ACTIVITY_REQUEST_PAY_DONE_FINISH_CODE);
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }

    }

    //是否加入黑名单
    private boolean isValidateblackFilter = false;
    public boolean isShowPoint = false; //是否显示积分;

    /**
     * 获取商品详情淘宝积分黑名单
     */

    private static class GetItemDetailValidateblackfilterListener extends BizRequestListener<Boolean> {

        public GetItemDetailValidateblackfilterListener(WeakReference<BaseActivity> mBaseActivityRef) {
            super(mBaseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            AppDebug.e(TAG, "获取商品详情淘宝积分黑名单请求失败");
            //NOTE:如果黑名单接口调用不成功，不交由底层进行出错处理（不显示那个错误的对话框)
            //return false;
            return true;
        }

        @Override
        public void onSuccess(Boolean data) {
            AppDebug.e(TAG, "获取商品详情淘宝积分黑名单请求成功" + data);
            NewDetailActivity detailActivity = (NewDetailActivity) mBaseActivityRef.get();
            if (detailActivity != null) {
                detailActivity.isValidateblackFilter = data;
                detailActivity.isVisbilePoint();
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    @Override
    protected void onResume() {
        //isShowFullDesc = false;
        super.onResume();
        try {
            mRightPanel.getPoint(mBusinessRequest);
            if (!TextUtils.isEmpty(mItemId) && CoreApplication.getLoginHelper(CoreApplication.getApplication()).isLogin()) {
                mRightPanel.getIsCollection(mBusinessRequest, mItemId);
            }
            if (CoreApplication.getLoginHelper(this) != null && CoreApplication.getLoginHelper(this).isLogin()) {
                mRightPanel.tvMyTaobao.setText(User.getNick());
            } else {
                String myTaobaoStr = getResources().getString(R.string.ytbv_pierce_my_taobao);
                mRightPanel.tvMyTaobao.setText(myTaobaoStr);
            }

            mBusinessRequest.requestProductTag(mItemId, ActivityPathRecorder.getInstance().getCurrentPath(this),
                    isZTC, source, isPre, nowPrice, this, new GetProductTagListener(new WeakReference<BaseActivity>(this)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected PageReturn onVoiceAction(DomainResultVo object) {
        LogPrint.d(TAG, TAG + ".onVoiceAction action " + object.getIntent());
        PageReturn pageReturn = new PageReturn();
        switch (object.getIntent()) {
            case ActionType.TO_BUY:
            case ActionType.BUY_NOW:
                buy();

                pageReturn.isHandler = true;
                pageReturn.feedback = "正在为您跳转购买";
                break;
            case ActionType.ADD_CART:
                addCart();

                pageReturn.isHandler = true;
                pageReturn.feedback = "正在为您加入购物车";
                break;
            case ActionType.GOODS_EVALUATION:
                showEvaluate();

                pageReturn.isHandler = true;
                pageReturn.feedback = "正在打开评价";
                break;
            case ActionType.GOODS_SHARE:
                mRightPanel.ivShare.performClick();

                pageReturn.isHandler = true;
                pageReturn.feedback = "正在跳转分享页面";
                break;
            case ActionType.GOODS_COUPON:
                mRightPanel.ivCoupon.performClick();

                pageReturn.isHandler = true;
                pageReturn.feedback = "正在打开优惠券";
                break;
            case ActionType.GOODS_SHOP:
                gotoShopIndex(null);

                pageReturn.isHandler = true;
                pageReturn.feedback = "正在打开店铺";
                break;
            case ActionType.GOODS_COLLECTION:
                mBusinessRequest.addCollection(mItemId, new AddCollectionRequestListener(new WeakReference(this)));

                pageReturn.isHandler = true;
                pageReturn.feedback = "正在收藏商品";
                break;
            default:
                break;
        }

        return pageReturn;
    }

    @Override
    public void onBackPressed() {
        //详情页不走super.onBackPressed()的逻辑，因为BzBaseActivity的this.mBlitzContext.isLastPage()的返回值在某些设备上不稳定，具体原因还不清楚
        this.handleBackPress();
    }


    private static class TaokeBussinessRequestListener extends BizRequestListener<JSONObject> {

        public TaokeBussinessRequestListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return true;
        }

        @Override
        public void onSuccess(JSONObject data) {

        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }


    /**
     * 淘客登录打点监听
     */
    private class TaokeLoginBussinessRequestListener extends BizRequestListener<JSONObject> {

        public TaokeLoginBussinessRequestListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return true;
        }

        @Override
        public void onSuccess(JSONObject data) {
            long nextHistoryTime = System.currentTimeMillis() + 604800000;//7天
            SharedPreferencesUtils.saveTvBuyTaoKe(NewDetailActivity.this, nextHistoryTime);
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    //宝贝加入收藏的监听
    public static class AddCollectionRequestListener extends BizRequestListener<String> {

        public AddCollectionRequestListener(WeakReference baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }
        @Override
        public void onSuccess(String data) {
            NewDetailActivity activity = (NewDetailActivity) mBaseActivityRef.get();
            if (activity == null) {
                return;
            }

            AppDebug.d(TAG, "msg----------------:" + data);
            if (TextUtils.equals(data, "true")) {
                Toast.makeText(mBaseActivityRef.get(), "已为您收藏", Toast.LENGTH_SHORT).show();
                activity.mRightPanel.manFavText(true);
            } else {
                Toast.makeText(mBaseActivityRef.get(), "收藏宝贝", Toast.LENGTH_SHORT).show();
                activity.mRightPanel.manFavText(false);
            }
        }
        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    private static class getstdCatsRequestListener extends BizRequestListener<String> {

        public getstdCatsRequestListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return true;
        }

        @Override
        public void onSuccess(String data) {
            NewDetailActivity detailActivity = (NewDetailActivity) mBaseActivityRef.get();
            if (detailActivity != null && data != null) {
                detailActivity.checkTaobaoPointValidateblackfilter(data);
            }

        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    private static class GetItemDetailOfFeiZhuRequestListener extends BizRequestListener<FeiZhuBean> {


        public GetItemDetailOfFeiZhuRequestListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            AppDebug.e(TAG, "飞猪数据请求失败");
            return false;
        }

        @Override
        public void onSuccess(FeiZhuBean data) {
            AppDebug.e(TAG, "飞猪数据请求成功");
            NewDetailActivity mNewDetailActivity = (NewDetailActivity) mBaseActivityRef.get();
            if (data != null) {
                mNewDetailActivity.feiZhuBean = data;
                mNewDetailActivity.onHandlerFeizhuRequest(data);
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    private FeiZhuBean feiZhuBean;

//    /**
//     * 6.0接口请求---dingbin
//     */
//    private class GetItemDetailOfV6BusinessRequestListener extends BizRequestListener<TBDetailResultV6> {
//
//        public GetItemDetailOfV6BusinessRequestListener(WeakReference<BaseActivity> baseActivityRef) {
//            super(baseActivityRef);
//        }
//
//        @Override
//        public boolean onError(int resultCode, String msg) {
//            return false;
//        }
//
//        @Override
//        public void onSuccess(TBDetailResultV6 data) {
//            AppDebug.e("TBDetailResultVO_v6数据请求成功", "");
//            if (data == null || data.getItem() == null) {
//                //商品不存在或者过期
//                showErrorDialog("商品不存在", true);
//                return;
//
//            }
//            if (data != null)
//                tbDetailResultV6 = data;
//            NewDetailActivity detailActivity = (NewDetailActivity) mBaseActivityRef.get();
//            if (detailActivity != null && data != null && data.getTrade() != null && data.getTrade().getRedirectUrl() != null && data.getTrade().getRedirectUrl().contains("trip")) {
//                //飞猪商品
//                AppDebug.d(TAG, "飞猪商品，去请求飞猪详情");
//                isFeizhu = true;
//                mBusinessRequest.requestGetFeiZhuItemDetail(mItemId, new GetItemDetailOfFeiZhuRequestListener(new WeakReference<BaseActivity>(NewDetailActivity.this)));
//
//            } else if (detailActivity != null && data != null && DetailV6Utils.getUnit(data) != null) {//普通商品
//                detailActivity.onHandlerReuqstV6(data);
//                isFeizhu = false;
//            } else if (detailActivity != null && data != null && data.getFeature() != null) {//秒杀
//                mModleType = DetailModleType.SECKKILL;
//                detailActivity.onHandlerReuqstV6(data);
//            }
//
//        }
//
//        @Override
//        public boolean ifFinishWhenCloseErrorDialog() {
//            return false;
//        }
//
//
//    }

    private void onHandlerFeizhuRequest(FeiZhuBean feiZhuBean) {
        OnWaitProgressDialog(false);
        if (tbDetailResultV6 == null || feiZhuBean == null) {
            showErrorDialog(getResources().getString(R.string.ytsdk_detail_resolve_err), true);
            return;
        }
        if (tbDetailResultV6.getItem() == null) {
            showErrorDialog(getResources().getString(R.string.ytsdk_detail_resolve_err), true);
            return;
        }
//        if (focusPositionManager != null) {
//            focusPositionManager.focusShow();
//        }

        // 淘宝sdk商品详情页人数（UV）、次数（PV）、时长, 2001:PAGE//Page:CitySet//Kvs:item_id=,name=,
        Map<String, String> p = getPageProperties();
        if (tbDetailResultV6 != null && tbDetailResultV6.getItem() != null) {
            if (!TextUtils.isEmpty(tbDetailResultV6.getItem().getTitle())) {
                p.put("name", tbDetailResultV6.getItem().getTitle());
            }
        }
        Utils.utUpdatePageProperties(getFullPageName(), p);

        if (mDetailBuilder == null) {
            mDetailBuilder = new DetailBuilder(getApplicationContext());
        }
        // 检查mTBDetailResultVO数据
        mDetailBuilder.onCheckResultVO(tbDetailResultV6, feiZhuBean);

        // 获取资源配置
        resConfig = mDetailBuilder.getResConfig();

        anaylisysTaoke();

        // 根据返回的商品详细填充view
        fillFeizhuView();

//        // 检查商品是否收藏
//        checkFav(mItemId);

        // 获取后台类目
        getstdCats();

        if (mFlashsaleBuilder != null) {
            mFlashsaleBuilder.startTimer();
        }


    }

    /**
     * 6.0接口数据处理，参照5.0
     *
     * @param data
     */
    private void onHandlerReuqstV6(TBDetailResultV6 data) {
        OnWaitProgressDialog(false);
        if (data == null) {
            AppDebug.e(TAG, "详情接口返回的对象为null");
            showErrorDialog(getResources().getString(R.string.ytsdk_detail_resolve_err), true);
            return;
        }
        if (data.getItem() == null) {
            AppDebug.e(TAG, "详情接口返回的item为null");
            showErrorDialog(getResources().getString(R.string.ytsdk_detail_resolve_err), true);
            return;
        }
//        if (focusPositionManager != null) {
//            focusPositionManager.focusShow();
//        }

        this.tbDetailResultV6 = data;
        // 淘宝sdk商品详情页人数（UV）、次数（PV）、时长, 2001:PAGE//Page:CitySet//Kvs:item_id=,name=,
        Map<String, String> p = getPageProperties();
        if (tbDetailResultV6 != null && tbDetailResultV6.getItem() != null) {
            if (!TextUtils.isEmpty(tbDetailResultV6.getItem().getTitle())) {
                p.put("name", tbDetailResultV6.getItem().getTitle());
            }
        }
        Utils.utUpdatePageProperties(getFullPageName(), p);

        if (mDetailBuilder == null) {
            mDetailBuilder = new DetailBuilder(getApplicationContext());
        }
        // 检查mTBDetailResultVO数据
        mDetailBuilder.onCheckResultVO(tbDetailResultV6, feiZhuBean);

        // 获取资源配置
        resConfig = mDetailBuilder.getResConfig();

        anaylisysTaoke();

        // 根据返回的商品详细填充view
        fillView();

//
//        // 检查商品是否收藏
//        checkFav(mItemId);

        // 获取后台类目
        getstdCats();


        if (mFlashsaleBuilder != null) {
            mFlashsaleBuilder.startTimer();
        }
    }

    private final int DISTANCE = 150;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d("hasFocus =====", mDetailView.mDetailBuyView.tv_buy.hasFocus() + "");
        if (mDetailView.mDetailBuyView.btn_cart.hasFocus() || mDetailView.mDetailBuyView.tv_buy.hasFocus() || mDetailView.mDetailBuyView.btn_qrd.hasFocus()) {
            int keyAction = event.getAction();
            if (keyAction == KeyEvent.ACTION_DOWN
                    && (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP)) {
                int dy = 0;
                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        dy = DISTANCE;
                        break;
                    case KeyEvent.KEYCODE_DPAD_UP:
                        dy = -DISTANCE;
                        break;

                    default:
                        break;
                }

                this.scrollBy(0, dy);
                if (isSimpleOn && DeviceJudge.MemoryType.LowMemoryDevice.equals(DeviceJudge.getMemoryType())) {
                    mDetailScrollInfoView.onListScroll(dy);
                } else {
                    mDetailScrollInfoView.onInitScrollbar();
                }
                return true;
            }
        } else if (mRightPanel.ivCollection.hasFocus()) {
            int keyAction = event.getAction();
            if (keyAction == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                int dy = 0;
                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        dy = DISTANCE;
                        break;
                    default:
                        break;
                }

                this.scrollBy(0, dy);
                if (isSimpleOn && DeviceJudge.MemoryType.LowMemoryDevice.equals(DeviceJudge.getMemoryType())) {
                    mDetailScrollInfoView.onListScroll(dy);
                } else {
                    mDetailScrollInfoView.onInitScrollbar();
                }
                return true;
            }
        }
        return super.dispatchKeyEvent(event);

    }

    /**
     * 清除html的body标签默认内外边距
     * <style>
     * body{
     * margin:0;
     * padding:0;
     * }
     * </style>
     *
     * @param html
     * @return
     */
    private String clearHtmlBodyMarginPadding(String html) {
        if (TextUtils.isEmpty(html)) {
            return "";
        }

        int bodyIndex = html.indexOf("<body>");
        if (bodyIndex >= 0) {
            String replaceStyleBody = "<body style='margin:0px;padding:0px;background-color:white;'>" + toutuImg;
            html = html.replace("<body>", replaceStyleBody);
        } else {
            html = "<body style='margin:0px;padding:0px;background-color:white;'>" + toutuImg + html;
        }

        if (isSimpleOn && DeviceJudge.MemoryType.LowMemoryDevice.equals(DeviceJudge.getMemoryType())) {
            //插入商品属性
            if (mDetailScrollInfoView != null) {
                mDetailScrollInfoView.initFootView(tbDetailResultV6);
            }
        } else {
            //插入商品属性
            int bodyLastIndex = html.indexOf("</body>");
            if (bodyLastIndex >= 0) {
                if (mDetailScrollInfoView != null) {
                    html = html.replace("</body>", mDetailScrollInfoView.getPropsHtml(tbDetailResultV6) + "</body>");
                }
            } else if (mDetailScrollInfoView != null) {
                html = html + mDetailScrollInfoView.getPropsHtml(tbDetailResultV6) + "</body>";
            }
        }

        //插入页面配置
        String replaceContent = "<meta name='viewport' content='width=790'></meta><meta charset='utf-8'></meta><style>img {vertical-align:bottom;}u {display:inline;}iframe {width:0px;height:0px}</style>";
        if (html.indexOf("<head>") != -1) {
            html = html.replace("<head>", "<head>" + replaceContent);
        } else if (html.indexOf("<html>") != -1) {
            html = html.replace("<html>", "<html>" + replaceContent);
        } else {
            html = replaceContent + html;
        }

//        Pattern pattern = Pattern.compile("<img");
//        Matcher matcher = pattern.matcher(html);
//        html=matcher.replaceFirst(toutuImg);

        return html;
    }


    /**
     * 商品详情的内容
     */
    private static class GetFullItemDescBusinessRequestListener extends BizRequestListener<String> {

        public GetFullItemDescBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef) {
            super(mBaseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(String data) {
            NewDetailActivity mNewDetailActivity = (NewDetailActivity) mBaseActivityRef.get();
            if (mNewDetailActivity != null) {

                mNewDetailActivity.OnWaitProgressDialog(false);
                if (!TextUtils.isEmpty(data)) {
                    // 加载商品详情的内容
                    String detailFullHtml = mNewDetailActivity.clearHtmlBodyMarginPadding(data);
                    //FileUtil.writeFileSdcardFile("/sdcard/detail.txt", detailFullHtml);
                    if (mNewDetailActivity.mDetailScrollInfoView != null) {
                        mNewDetailActivity.mDetailScrollInfoView.loadDataWithBaseURL("about:blank",
                                detailFullHtml, "text/html", "UTF-8", "");
                    }
                }
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return true;
        }

    }


    // 打开图文详情
    public void showRichText() {
        Intent intent = new Intent(this, DetailFullDescActivity.class);
        intent.putExtra("mTBDetailResultVO", tbDetailResultV6);
        intent.putExtra("extParams", extParams);
        intent.putExtra(BaseConfig.INTENT_KEY_ITEMID, mItemId);
        intent.putExtra(BaseConfig.INTENT_KEY_PRICE, uriPrice);
        intent.putExtra("buyText", buyText);
        if (mProductTagBo != null) {
            intent.putExtra("mProductTagBo", mProductTagBo);
        }
        startActivity(intent);
    }

    public void getNewDetail() {
        BusinessRequest.getBusinessRequest().requestGetItemDetailV6New(mItemId, null, new DetailListener(new WeakReference<BaseActivity>(this)));
//
//        String host = "https://acs.m.taobao.com/gw/mtop.taobao.detail.getdetail/6.0/?data=";
//        String tag = "%7B%22itemNumId%22%3A%22";
//        String tag2 = "%22%2C%22detail_v%22%3A%223.1.0%22%7D";
//        OkHttpClient okHttpClient = new OkHttpClient();
//        String url = host + tag + mItemId + tag2 + "&ttid=142857@taobao_iphone_7.10.3";
//        final Request request = new Request.Builder().url(url).build();
//        Call call = okHttpClient.newCall(request);
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//                String result = response.body().string();
//                try {
//                    final TBDetailResultV6 data = resolveResult(result);
//                    AppDebug.e("TBDetailResultVO_v6数据请求成功", "");
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (data == null || data.getItem() == null) {
//                                //商品不存在或者过期
//                                showErrorDialog("商品不存在", true);
//                                return;
//                            }
//                            if (data != null)
//                                tbDetailResultV6 = data;
//                            if (data != null && data.getTrade() != null && data.getTrade().getRedirectUrl() != null && data.getTrade().getRedirectUrl().contains("trip")) {
//                                //飞猪商品
//                                AppDebug.d(TAG, "飞猪商品，去请求飞猪详情");
//                                isFeizhu = true;
//                                mBusinessRequest.requestGetFeiZhuItemDetail(mItemId, new GetItemDetailOfFeiZhuRequestListener(new WeakReference<BaseActivity>(NewDetailActivity.this)));
//
//                            } else if (data != null && DetailV6Utils.getUnit(data) != null) {//普通商品
//                                onHandlerReuqstV6(data);
//                                isFeizhu = false;
//                            } else if (data != null && data.getFeature() != null) {//秒杀
//                                mModleType = DetailModleType.SECKKILL;
//                                onHandlerReuqstV6(data);
//                            }
//
//                        }
//                    });
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                Log.d("okhttp", result);
//            }
//        });

    }

    public TBDetailResultV6 resolveResult(String result) throws Exception {
        if (result == null) {
            return null;
        }

        JSONObject jsonObject = new JSONObject(result);
        JSONObject obj = jsonObject.getJSONObject("data");

        TBDetailResultV6 tbDetailResultV6 = JSON.parseObject(obj.toString(), TBDetailResultV6.class);
        List<com.taobao.detail.domain.base.Unit> units = resolveDomainUnit(obj);
        if (units != null)
            tbDetailResultV6.setDomainList(units);
        //合约机
        if (tbDetailResultV6.getApiStack() != null && tbDetailResultV6.getApiStack().size() > 0) {
            tbDetailResultV6.setContractData(resolveContractData(tbDetailResultV6.getApiStack().get(0)));
        }
        TBDetailResultV6.Feature feature = resolveSeckKillFeature(obj);
        TBDetailResultV6.Delivery delivery = rexolveSeckKillDelivery(obj);
        TBDetailResultV6.PriceBeanX priceBeanX = resolveSeckKillPrice(obj);
        String s = resolveSeckKillSkuCore(obj);

        if (feature != null)
            tbDetailResultV6.setFeature(feature);
        if (delivery != null)
            tbDetailResultV6.setDelivery(delivery);

        if (priceBeanX != null) {
            tbDetailResultV6.setPrice(priceBeanX);
        }
        if (!s.equals("")) {
            tbDetailResultV6.setSkuKore(s);
        }
        return tbDetailResultV6;
    }

    private List<TBDetailResultV6.ContractData> resolveContractData(TBDetailResultV6.ApiStackBean apiStackBean) {
        try {
            JSONObject object = new JSONObject(apiStackBean.getValue());
            if (object.has("skuVertical")) {
                JSONObject skuVertical = object.getJSONObject("skuVertical");
                if (!skuVertical.has("contractData")) {
                    return null;
                }
                JSONArray contract = skuVertical.getJSONArray("contractData");
                List<TBDetailResultV6.ContractData> result = new ArrayList<>();
                for (int i = 0; i < contract.length(); i++) {
                    JSONObject contractJson = contract.getJSONObject(i);
                    TBDetailResultV6.ContractData contractData = new TBDetailResultV6.ContractData();
                    contractData.versionData = TBDetailResultV6.ContractData.VersionData.resolveVersionData(contractJson.getJSONObject("version"));
                    result.add(contractData);
                }
                return result;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    private TBDetailResultV6.PriceBeanX resolveSeckKillPrice(JSONObject data) {
        try {
            if (data.has("price")) {
                TBDetailResultV6.PriceBeanX priceBeanX = new TBDetailResultV6.PriceBeanX();
                JSONObject price = data.getJSONObject("price");
                if (price.has("price")) {
                    JSONObject price1 = price.getJSONObject("price");
                    if (price1 != null) {
                        TBDetailResultV6.PriceBeanX.PriceBean priceBean = new TBDetailResultV6.PriceBeanX.PriceBean();
                        if (price1.has("priceText")) {
                            priceBean.setPriceText(price1.getString("priceText"));
                        }
                        if (price1.has("priceTitle")) {
                            priceBean.setPriceTitle(price1.getString("priceTitle"));
                        }
                        priceBeanX.setPrice(priceBean);
                        return priceBeanX;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return null;

    }

    private String resolveSeckKillSkuCore(JSONObject data) {

        try {
            if (data.has("skuCore")) {
                JSONObject data1 = data.getJSONObject("skuCore");
                //if (data1.has("sku2info")){
                //JSONObject sku2info = data1.getJSONObject("sku2info");
                if (data1 != null) {
                    return data1.toString();
                }
                //}
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

    private TBDetailResultV6.Feature resolveSeckKillFeature(JSONObject data) {
        try {
            if (data.has("feature")) {
                TBDetailResultV6.Feature feature = new TBDetailResultV6.Feature();
                JSONObject featureBean = data.getJSONObject("feature");
                if (featureBean.has("secKill")) {
                    String secKill = featureBean.getString("secKill");
                    feature.setSecKill(secKill);

                }
                if (featureBean.has("hasSku")) {
                    String hasSku = featureBean.getString("hasSku");
                    feature.setHasSku(hasSku);
                }
                return feature;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    private TBDetailResultV6.Delivery rexolveSeckKillDelivery(JSONObject data) {

        if (data.has("delivery")) {
            try {
                TBDetailResultV6.Delivery delivery = new TBDetailResultV6.Delivery();
                JSONObject deliveryBean = data.getJSONObject("delivery");
                if (deliveryBean.has("postage")) {
                    String postage = deliveryBean.getString("postage");
                    delivery.setPostage(postage);
                    return delivery;
                }
                return null;

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

        }
        return null;

    }

    private List<com.taobao.detail.domain.base.Unit> resolveDomainUnit(JSONObject data) {

        try {
            JSONObject props = data.getJSONObject("props");
            JSONArray groupProps = props.getJSONArray("groupProps");
            JSONObject jsonObject1 = (JSONObject) groupProps.get(0);
            JSONArray jsonArray = jsonObject1.getJSONArray("基本信息");
            List<com.taobao.detail.domain.base.Unit> list = new ArrayList<com.taobao.detail.domain.base.Unit>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject2 = (JSONObject) jsonArray.get(i);
                Iterator keys = jsonObject2.keys();
                while (keys.hasNext()) {
                    com.taobao.detail.domain.base.Unit unit = new com.taobao.detail.domain.base.Unit();
                    String next = (String) keys.next();
                    String value = jsonObject2.optString(next);
                    unit.name = next;
                    unit.value = value;
                    list.add(unit);
                }
            }
            for (int i = 0; i < list.size(); i++) {
                AppDebug.e("props数据", list.get(i).name);

            }
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    class TimeDoneRefreshListener implements NewTimerTextView.TimeDoneListener {
        @Override
        public void refreshDetail() {
            getNewDetail();

        }
    }

    //设置时间倒计时结束的监听
    public void setTimeDoneListener() {
        mDetailView.setTimeDoneListener(new TimeDoneRefreshListener());
    }

    private static class DetailListener extends BizRequestListener<TBDetailResultV6> {
        public DetailListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(TBDetailResultV6 data) {
            if (mBaseActivityRef == null && mBaseActivityRef.get() == null) {
                return;
            }
            NewDetailActivity activity = (NewDetailActivity) mBaseActivityRef.get();
            if (data == null || data.getItem() == null) {
                //商品不存在或者过期
                activity.showErrorDialog("商品不存在", true);
                return;
            }
            if (data != null)
                activity.tbDetailResultV6 = data;
            if (data != null && data.getTrade() != null && data.getTrade().getRedirectUrl() != null && data.getTrade().getRedirectUrl().contains("trip")) {
                //飞猪商品
                AppDebug.d(TAG, "飞猪商品，去请求飞猪详情");
                activity.isFeizhu = true;
                activity.mBusinessRequest.requestGetFeiZhuItemDetail(data.getItem().getItemId(), new GetItemDetailOfFeiZhuRequestListener(new WeakReference<BaseActivity>(activity)));

            } else if (data != null && DetailV6Utils.getUnit(data) != null) {//普通商品
                activity.onHandlerReuqstV6(data);
                activity.isFeizhu = false;
            } else if (data != null && data.getFeature() != null) {//秒杀
                activity.mModleType = DetailModleType.SECKKILL;
                activity.onHandlerReuqstV6(data);
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    @Override
    protected String getAppTag() {
        return "";
    }

}
