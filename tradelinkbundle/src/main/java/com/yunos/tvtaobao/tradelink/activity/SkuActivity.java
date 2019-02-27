package com.yunos.tvtaobao.tradelink.activity;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.taobao.statistic.CT;
import com.taobao.statistic.TBS;
import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvlife.imageloader.core.display.RoundedBitmapDisplayer;
import com.tvlife.imageloader.core.listener.SimpleImageLoadingListener;
import com.tvtaobao.voicesdk.bo.DomainResultVo;
import com.tvtaobao.voicesdk.bo.PageReturn;
import com.tvtaobao.voicesdk.register.type.ActionType;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.SystemConfig;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tv.core.util.ActivityPathRecorder;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.TradeBaseActivity;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.BuildOrderPreSale;
import com.yunos.tvtaobao.biz.request.bo.BuildOrderRequestBo;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.request.bo.JoinGroupResult;
import com.yunos.tvtaobao.biz.request.bo.MockData;
import com.yunos.tvtaobao.biz.request.bo.ProductTagBo;
import com.yunos.tvtaobao.biz.request.bo.SearchResult;
import com.yunos.tvtaobao.biz.request.bo.SkuPriceNum;
import com.yunos.tvtaobao.biz.request.bo.TBDetailResultV6;
import com.yunos.tvtaobao.biz.request.bo.Unit;
import com.yunos.tvtaobao.biz.request.core.ServiceCode;
import com.yunos.tvtaobao.biz.request.utils.DetailV6Utils;
import com.yunos.tvtaobao.biz.widget.newsku.TradeType;
import com.yunos.tvtaobao.tradelink.R;
import com.yunos.tvtaobao.tradelink.resconfig.IResConfig;
import com.yunos.tvtaobao.tradelink.resconfig.TaobaoResConfig;
import com.yunos.tvtaobao.tradelink.resconfig.TmallResConfig;
import com.yunos.tvtaobao.tradelink.sku.ContractItemLayout;
import com.yunos.tvtaobao.tradelink.sku.SkuEngine;
import com.yunos.tvtaobao.tradelink.sku.SkuEngine.OnSkuPropLayoutListener;
import com.yunos.tvtaobao.tradelink.sku.SkuEngine.PropData;
import com.yunos.tvtaobao.tradelink.sku.SkuPropItemLayout;
import com.yunos.tvtaobao.tradelink.sku.SkuPropItemLayout.OnBuyCountChangedListener;
import com.yunos.tvtaobao.tradelink.sku.SkuPropItemLayout.OnBuyCountClickedListener;
import com.yunos.tvtaobao.tradelink.sku.SkuPropItemLayout.OnPropViewFocusListener;
import com.yunos.tvtaobao.tradelink.sku.SkuPropItemLayout.VALUE_VIEW_STATUS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SkuActivity extends TradeBaseActivity {


    public static int SHOP_CART_ACTIVITY_FOR_RESULT_CODE = 1; // 购物车调用页返回的结果代码
    // 购买数量界面属性ID
    private int mBuyNumViewPropId = -Integer.MAX_VALUE;
    // 接口请求
    private BusinessRequest mBusinessRequest;
    // 界面对象
    private ViewHolder mViewHolder;
    // 商品信息
    //private TBDetailResultVO mTBDetailResultVO;
    private TBDetailResultV6 tbDetailResultV6;
    //打标活动信息
    private ProductTagBo mProductTagBo;
    // SkuEngine
    private SkuEngine mSkuEngine;
    // 图片下载管理
    private ImageLoaderManager mImageLoaderManager;
    private DisplayImageOptions options;
    // 需要显示上下蒙版
    private boolean mNeedShowMask;
    // 当sku数量大于4时，显示上下蒙版
    private int mShowMaskSkuNum = 4;
    //进入该页面的类型
    private String mType;
    private String mSkuId;
    private String mItemId;
    private String tagId;
    // 是否为聚划算商品，只有当我自己去请求数据时判断。
    private boolean mIsJuhuasuan;
    // 限购数量
    private long mLimitCount;
    // 当前价格
    private String mCurPrice;
    // 购买数量
    private int mBuyCount;
    //加倍购买，倍数
    private int unitBuy = 1;
    // 是sku商品
    private boolean mSku;
    // 扩展参数,如淘抢购商品显示渠道专享价
    private String extParams;
    //    // 是直通车商品
    private boolean isZTC;
    // 详细来源
    private String source;
    // 当前显示的商品图片的url
    private String mImageUrl;

    private String uriPrice;
    private boolean isSeckKill;

    private String cartFrom;


    // 资源配置[天猫，或者淘宝]
    private IResConfig resConfig;

    private Animation mAnimationIn; // 进入时的动画
    private Animation mAnimationOut; // 消失时的动画
    private SkuPropItemLayout buyCountView;
    private String mSubPrice;//预售商品，预售价格
    private boolean isPreSale;//接口是否有预售商品标记
    private BuildOrderPreSale buildOrderPreSale;
    private String tvtao_extra;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onKeepActivityOnlyOne(SkuActivity.class.getName());
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }

        initparameter();
        mType = bundle.getString(BaseConfig.INTENT_KEY_SKU_TYPE);
        cartFrom = bundle.getString(BaseConfig.INTENT_KEY_CARTFROM);
        AppDebug.e(TAG, "进入页面的类型" + mType);
        if (TextUtils.isEmpty(mType)) {
            mType = TradeType.BUY;
        }
        mBuyCount = (int) bundle.getLong(BaseConfig.INTENT_KEY_BUY_COUNT, 1);
        mSkuId = bundle.getString(BaseConfig.INTENT_KEY_SKUID);
        extParams = getIntent().getStringExtra(BaseConfig.INTENT_KEY_EXTPARAMS);
        tbDetailResultV6 = (TBDetailResultV6) bundle.get("mTBDetailResultVO");
        mProductTagBo = (ProductTagBo) bundle.get("mProductTagBo");
        isZTC = bundle.getBoolean(BaseConfig.INTENT_KEY_ISZTC, false);
        source = bundle.getString(BaseConfig.INTENT_KEY_SOURCE);
        String itemId = bundle.getString(BaseConfig.INTENT_KEY_ITEMID);
        uriPrice = getIntent().getStringExtra(BaseConfig.INTENT_KEY_PRICE);
        tvtao_extra = getIntent().getStringExtra("tvtao_extra");
        try {
            if (mProductTagBo != null) {
                JSONObject object = null;
                if (extParams == null) {
                    object = new JSONObject();
                } else {
                    object = new JSONObject(extParams);
                }
                JSONArray array = new JSONArray();
                JSONObject obj = new JSONObject();
                obj.put("itemId", mProductTagBo.getItemId());
                obj.put("lastTraceKeyword", mProductTagBo.getLastTraceKeyword());
                obj.put(BaseConfig.INTENT_KEY_ISZTC, isZTC);
                if (source != null) {
                    obj.put(BaseConfig.INTENT_KEY_SOURCE, source);
                }
                if (mProductTagBo.getOutPreferentialId() != null) {
                    obj.put("outPreferentialId", mProductTagBo.getOutPreferentialId());
                }
                if (mProductTagBo.getPointSchemeId() != null) {
                    obj.put("pointSchemeId", mProductTagBo.getPointSchemeId());
                }

                if (mProductTagBo.getCoupon() != null) {
                    obj.put("couponAmount", mProductTagBo.getCoupon());
                }

                if (mProductTagBo.getCart() != null) {
                    obj.put("cart", mProductTagBo.getCart());
                }
                if (mProductTagBo.getCouponType() != null) {
                    obj.put("couponType", mProductTagBo.getCouponType());
                }
                if (mProductTagBo.getTagId() != null) {
                    obj.put("tagId", mProductTagBo.getTagId());
                    tagId = mProductTagBo.getTagId();
                }

                array.put(obj);
                JSONObject subOrders = new JSONObject();
                subOrders.put("subOrders", array);
                object.put("tvtaoExtra", buildAddBagExParams());
                object.put("TvTaoEx", subOrders);
                extParams = object.toString();

            } else {
                if (!TextUtils.isEmpty(tvtao_extra)) {
                    JSONObject object = new JSONObject();
                    object.put("tvtaoExtra", tvtao_extra);
                    extParams = object.toString();


                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (Config.isDebug()) {
            AppDebug.v(TAG, TAG + ".onCreate.mTBDetailResultVO = " + tbDetailResultV6 + ", itemId = " + itemId
                    + ", mSkuId = " + mSkuId + ", mBuyCount = " + mBuyCount + ", extParams = " + extParams
                    + ", mType = " + mType);
        }
        isPreSale = false;
        if (tbDetailResultV6 != null) {
            initView();// 直接显示界面
        } else if (!TextUtils.isEmpty(itemId)) {
            try {//加载详情数据
                AppDebug.e(TAG, "从编辑进来 itemid= " + itemId);
                requestGoodsDetail(Long.parseLong(itemId));
            } catch (Exception e) {
                AppDebug.e(TAG, TAG + "." + e.getMessage());
                finish();
            }
        } else {
            AppDebug.e(TAG, TAG + ".onCreate.params error");
            finish();
        }

        //        mType = "buy";//editCart addCart buy
        //        mSkuId = "68148368593";
        //        mItemId = "43354671726";
        //        mBuyCount = 2;
        //        requestGoodsDetail(Long.parseLong(mItemId));//16282470083 43354671726 41589658467
    }

    @Override
    protected void onResume() {
        super.onResume();

        AppDebug.v(TAG, TAG + ".onResume, loginIsCanceled = " + loginIsCanceled());
        // 如果登录对话框被取消，则显示二维码界面
        if (loginIsCanceled()) {
            showQRCode(true);
        } else {
            showQRCode(false);
        }
    }

    @Override
    protected PageReturn onVoiceAction(DomainResultVo object) {
        PageReturn pageReturn = new PageReturn();
        switch (object.getIntent()) {
            case ActionType.CONFIRM:
                mViewHolder.mSkuDoneTextView.performClick();

                pageReturn.isHandler = true;
                pageReturn.feedback = "进入下一步";
                break;
            case ActionType.SETTLE_ACCOUNTS:
                gotoShopCart();

                pageReturn.isHandler = true;
                pageReturn.feedback = "正在帮您打开购物车";
                break;
            case ActionType.CONTINUE_TO_VISITING:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 300);

                pageReturn.isHandler = true;
                pageReturn.feedback = "好的";
                break;
        }
        return pageReturn;
    }

    @Override
    protected void onDestroy() {
        mSkuEngine = null;

        if (mViewHolder != null && mViewHolder.mSkuPropLayout != null) {
            int count = mViewHolder.mSkuPropLayout.getChildCount();
            for (int i = 0; i < count; i++) {
                View prop = mViewHolder.mSkuPropLayout.getChildAt(i);
                if (prop != null && prop instanceof SkuPropItemLayout)
                    ((SkuPropItemLayout) prop).onDestroy();
            }
            mViewHolder.mSkuPropLayout.removeAllViews();
            mViewHolder.mSkuPropLayout = null;
            mViewHolder = null;
        }
        buildOrderPreSale = null;
        onRemoveKeepedActivity(SkuActivity.class.getName());
        unRegisterLoginListener();
        super.onDestroy();
    }

    private SkuEngine.OnSkuPropLayoutListener mOnSkuPropLayoutListener = new OnSkuPropLayoutListener() {

        @Override
        public void updateSkuProp(long propId, long valueId, VALUE_VIEW_STATUS status) {
            SkuPropItemLayout layout = getPropLayoutById(propId);
            if (layout != null) {
                layout.updateValueViewStatus(propId, valueId, status);
            }
            // 属性变换时 取消二维码显示
            showQRCode(false);
        }

        @Override
        public void updateSkuKuCunAndrPrice(String skuId) {
            SkuActivity.this.updateSkuKuCunAndrPrice(skuId);
            if (buyCountView != null) {
                buyCountView.setCurBuyCount(1);
                AppDebug.e(TAG, "更新数量");
            }


        }

        @Override
        public void updateItemImage(PropData propData) {
            if (propData != null && !TextUtils.isEmpty(propData.imageUrl)) {
                setItemImage(propData.imageUrl);
            }
        }
    };

    /**
     * 当登入成功时
     */
    @Override
    protected void onLogin() {
        AppDebug.v(TAG, TAG + ",onLogin");
        showQRCode(false);
    }

    /**
     * 初始化参数
     */
    private void initparameter() {
        mSku = false;
        mIsJuhuasuan = false;
        mBuyCount = 1;
        mLimitCount = -1;// 假设限购数量为-1，即不限购
        unitBuy = 1;//1单倍购买，即不加倍
        mBusinessRequest = BusinessRequest.getBusinessRequest();
        mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(this);
        boolean beta = GlobalConfig.instance != null && GlobalConfig.instance.isBeta();
        options = new DisplayImageOptions.Builder().displayer(new RoundedBitmapDisplayer(0)).cacheInMemory(!beta)
                .cacheOnDisc(beta).showImageForEmptyUri(R.drawable.ytm_sku_image_default)
                .showImageOnFail(R.drawable.ytm_sku_image_default).bitmapConfig(Bitmap.Config.RGB_565).build();

        mAnimationIn = AnimationUtils.loadAnimation(this, R.anim.ytm_slide_in_left);
        mAnimationOut = AnimationUtils.loadAnimation(this, R.anim.ytm_slide_out_left);

        registerLoginListener();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        mViewHolder = new ViewHolder();
        setContentView(mViewHolder.mSkuRootLayout);

        if (tbDetailResultV6 == null || tbDetailResultV6.getItem() == null) {
            return;
        }
        if (DetailV6Utils.getUnit(tbDetailResultV6) != null) {
            Unit unit = DetailV6Utils.getUnit(tbDetailResultV6);
            if (unit.getFeature() != null) {
                if (unit.getFeature().getHasSku() != null && !unit.getFeature().getHasSku().equals("")) {
                    if (unit.getFeature().getHasSku().equals("true") && tbDetailResultV6.getSkuBase() != null
                            && tbDetailResultV6.getSkuBase().getProps() != null && tbDetailResultV6.getSkuBase().getProps().size() > 0) {
                        mSku = true;
                    } else {
                        mSku = false;
                    }
                }
            }
            if (unit != null && unit.getVertical() != null && unit.getVertical().getPresale() != null) {
                isPreSale = true;
                if (unit.getPrice() != null && unit.getPrice().getSubPrice() != null && unit.getPrice().getSubPrice().getPriceText() != null) {
                    mSubPrice = unit.getPrice().getSubPrice().getPriceText();
                } else {
                    mSubPrice = null;
                }
                buildOrderPreSale = new BuildOrderPreSale();
                Unit.VerticalBean.PresaleBean presale = unit.getVertical().getPresale();
                buildOrderPreSale.setEndTime(presale.getEndTime());
                buildOrderPreSale.setStartTime(presale.getStartTime());
                buildOrderPreSale.setExtraText(presale.getExtraText());
                buildOrderPreSale.setOrderedItemAmount(presale.getOrderedItemAmount());
                buildOrderPreSale.setStatus(presale.getStatus());
                buildOrderPreSale.setTip(presale.getTip()); //为了统一订单页的格式，把详情接口拿到的支付尾款时间带入订单页面，不取订单接口返回的值。
                buildOrderPreSale.setText(presale.getText());

            } else {
                mSubPrice = null;
            }
        } else {
            MockData mockdata = DetailV6Utils.getMockdata(tbDetailResultV6);
            if (mockdata != null && mockdata.getFeature() != null && mockdata.getFeature().isHasSku()) {
                mSku = true;
            } else {
                if (tbDetailResultV6.getFeature() != null) {
                    if (tbDetailResultV6.getFeature().getSecKill() != null) {
                        isSeckKill = true;
                    }
                    if (tbDetailResultV6.getFeature().getHasSku() != null && tbDetailResultV6.getFeature().getHasSku().equals("true")) {
                        AppDebug.e(TAG, "秒杀商品有sku");
                        mSku = true;
                    } else {
                        mSku = false;
                    }
                }
            }
        }
        TBDetailResultV6.SellerBean seller = tbDetailResultV6.getSeller();
        if (seller != null && !TextUtils.isEmpty(seller.getSellerType())) {
            if (seller.getSellerType().toUpperCase().equals("B")) {// tamll
                resConfig = new TmallResConfig(this);
            } else {
                resConfig = new TaobaoResConfig(this);
            }
        } else {
            // 默认是淘宝商品
            resConfig = new TaobaoResConfig(this);
        }

        getLimitCount(); // 判断是否有限购数量
        mItemId = tbDetailResultV6.getItem().getItemId();
        mSkuEngine = new SkuEngine(tbDetailResultV6);
        mSkuEngine.setOnSkuPropLayoutListener(mOnSkuPropLayoutListener);

        //标题
        if (!TextUtils.isEmpty(tbDetailResultV6.getItem().getTitle())) {
            mViewHolder.mSkuItemTitle.setText(tbDetailResultV6.getItem().getTitle());
        }
        //主图
        if (tbDetailResultV6.getItem().getImages() != null && tbDetailResultV6.getItem().getImages().size() > 0) {
            setItemImage(tbDetailResultV6.getItem().getImages().get(0));
        }

        // 价格
        if (DetailV6Utils.getUnit(tbDetailResultV6) != null) {
            Unit unit = DetailV6Utils.getUnit(tbDetailResultV6);
            if (unit.getPrice() != null) {
                if (unit.getPrice().getPrice() != null) {
                    mCurPrice = unit.getPrice().getPrice().getPriceText();

                }
            }
        } else {
            MockData mockdata = DetailV6Utils.getMockdata(tbDetailResultV6);
            if (mockdata != null) {
                if (mockdata.getPrice().getPrice().getPriceText() != null) {
                    mCurPrice = mockdata.getPrice().getPrice().getPriceText();
                }
            }
        }
        if (isSeckKill) {
            if (tbDetailResultV6.getPrice() != null) {
                if (tbDetailResultV6.getPrice().getPrice() != null) {
                    if (tbDetailResultV6.getPrice().getPrice().getPriceText() != null) {
                        mCurPrice = tbDetailResultV6.getPrice().getPrice().getPriceText();
                    }
                }
            }
        }

        //解析加倍购买的倍数
        initUnitBuy();

        setItemPrice();


        // 如果是sku商品
        if (mSku) {
            addSkuView();
        } else {
            AppDebug.i(TAG, TAG + ".initView no sku mType = " + mType);
            addContractView();
            addKuCunView();
            mViewHolder.mSkuDoneTextView.requestFocus();
            if (mType.equals(TradeType.EDIT_CART)) { // 如果是编辑，聚焦在购买数量上
                SkuPropItemLayout itemLayout = getPropLayoutById(mBuyNumViewPropId);
                AppDebug.v(TAG, TAG + ".itemLayout = " + itemLayout);
                if (itemLayout != null) {
                    itemLayout.requestFocus();
                }
            }
        }
    }

    /**
     * 增加sku界面
     */
    private void addSkuView() {
        if (mSkuEngine == null || tbDetailResultV6 == null || tbDetailResultV6.getSkuBase() == null || tbDetailResultV6.getSkuBase().getProps() == null) {
            return;
        }
        List<TBDetailResultV6.SkuBaseBean.PropsBeanX> props1 = tbDetailResultV6.getSkuBase().getProps();
        // sku界面
        for (int i = 0; i < props1.size(); i++) {
            if (props1.get(i) == null || props1.get(i).getPid() == null || props1.get(i).getValues() == null
                    || props1.get(i).getValues().size() == 0) {// 该属性无效
                continue;
            }
            LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            SkuPropItemLayout prop = new SkuPropItemLayout(this, Long.parseLong(props1.get(i).getPid()), true, mSkuEngine);
            prop.setSkuPropName(props1.get(i).getName());
            prop.setSkuPropView(props1.get(i).getValues());
            prop.setOnPropViewFocusListener(mOnPropViewFocusListener);
            if (i > 0) {
                params.topMargin = (int) getResources().getDimension(R.dimen.dp_66);
            }
            mViewHolder.mSkuPropLayout.addView(prop, params);
        }

        // 增加默认选择
        setDefaultSkuSelected();

        addContractView();

        // 增加库存界面
        addKuCunView();

        boolean hasFocus = false;
        if (mType.equals(TradeType.BUY) || mType.equals(TradeType.ADD_CART)) {// 购买或加入购物车时
            Map<Long, PropData> selectMap = mSkuEngine.getSelectedPropDataMap();
            if (selectMap != null && selectMap.size() > 0) {// 如果有选中
                if (Config.isDebug()) {
                    AppDebug.i(TAG, TAG + ".addSkuView.selectMap = " + selectMap + ".skuProps = " + props1);
                }

                if (selectMap.size() == props1.size()) {// 如果全部选中，聚焦在下一步
                    Handler handler = new Handler();
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            mViewHolder.mSkuPropScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                            mViewHolder.mSkuDoneTextView.requestFocus();
                            if (mNeedShowMask) {
                                mViewHolder.mSkuPropTopMask.setVisibility(View.VISIBLE);
                                mViewHolder.mSkuPropBottomMask.setVisibility(View.GONE);
                            }
                        }
                    });
                    hasFocus = true;
                } else {// 如果没有全部选中，聚焦在未选中的属性行
                    //SkuProp skuProp = mSkuEngine.getUnSelectProp();
                    TBDetailResultV6.SkuBaseBean.PropsBeanX unSelectProp = mSkuEngine.getUnSelectProp();
                    AppDebug.v(TAG, TAG + ".addSkuView.skuProp = " + unSelectProp);
                    if (unSelectProp != null) {
                        int count = mViewHolder.mSkuPropLayout.getChildCount();
                        for (int i = 0; i < count; i++) {
                            SkuPropItemLayout view = (SkuPropItemLayout) mViewHolder.mSkuPropLayout.getChildAt(i);
                            if (view != null && Config.isDebug()) {
                                AppDebug.v(TAG, TAG + ".addSkuView.view.getPropId() = " + view.getPropId()
                                        + ",skuProp.propId = " + unSelectProp.getPid());
                            }
                            if (view != null && view.getPropId() == Long.parseLong(unSelectProp.getPid())) {
                                hasFocus = true;
                                view.requestFocus();
                                break;
                            }

                        }
                    }
                }
            }
        }

        if (!hasFocus) {
            mViewHolder.mSkuPropLayout.requestFocus();
        }
    }

    private void addContractView() {
        if (tbDetailResultV6.getContractData() != null && tbDetailResultV6.getContractData().size() > 0) {
            List<TBDetailResultV6.ContractData.VersionData> versionDataList = new ArrayList<>();
            for (TBDetailResultV6.ContractData contractData : tbDetailResultV6.getContractData()) {
                TBDetailResultV6.ContractData.VersionData data = contractData.versionData;
                if (data.noShopCart)
                    continue;
                versionDataList.add(data);
            }
            if (versionDataList.size() > 0) {
                mViewHolder.contractItemLayout = new ContractItemLayout(this);
                mViewHolder.contractItemLayout.setSkuPropName("购买方式");
                mViewHolder.contractItemLayout.setSkuPropView(versionDataList);
                mViewHolder.contractItemLayout.setOnPropViewFocusListener(mOnPropViewFocusListener);
                LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.topMargin = (int) getResources().getDimension(R.dimen.dp_66);
                mViewHolder.mSkuPropLayout.addView(mViewHolder.contractItemLayout, params);
            }


        }
    }

    /**
     * 增加库存界面
     */
    private void addKuCunView() {
        if (mSkuEngine == null || tbDetailResultV6 == null) {
            return;
        }
        buyCountView = new SkuPropItemLayout(this, mBuyNumViewPropId, false, mSkuEngine);
        buyCountView.setSkuPropName(getString(R.string.ytm_sku_buy_num_text));
        buyCountView.setOnPropViewFocusListener(mOnPropViewFocusListener);
        buyCountView.setOnBuyCountChangedListener(new OnBuyCountChangedListener() {

            @Override
            public void OnBuyCountChanged(int buyCount) {
                if (Config.isDebug()) {
                    AppDebug.v(TAG, TAG + ".addKuCunView.buyCount = " + buyCount + ", mCurPrice = " + mCurPrice);
                }

                mBuyCount = buyCount;
                setItemPrice();
                // sku数量变化时 取消QR的显示
                showQRCode(false);
            }
        });
        buyCountView.setOnBuyCountClickedListener(new OnBuyCountClickedListener() {

            @Override
            public void OnBuyCountClicked() {
                // 当购买数量被点击时跳转到下一步的按钮上。
                mViewHolder.mSkuDoneTextView.requestFocus();
            }
        });
        // 如果sku全部已经选择，则更新库存
        if (mSku && !TextUtils.isEmpty(mSkuId)) {
            SkuPriceNum skuPriceNum = resolvePriceNum(mSkuId);
            if (skuPriceNum == null || skuPriceNum.getQuantity() <= 0) {
                SkuPriceNum skuPriceNum1 = resolvePriceNum("0");
                AppDebug.e(TAG, "skuPriceNum1 = " + skuPriceNum1);
                if (tbDetailResultV6.getApiStack() != null && tbDetailResultV6.getApiStack().get(0).getValue() != null && skuPriceNum1.getQuantity() != 0) {
                    if (skuPriceNum1 != null && skuPriceNum1.getLimit() == 0) {
                        buyCountView.setKuCunNum(skuPriceNum1.getQuantity(), 0);
                    } else
                        buyCountView.setKuCunNum(skuPriceNum1.getQuantity(), skuPriceNum1.getLimit());
                } else {
                    if (isSeckKill) {
                        Log.e(TAG, "skuactivity秒杀");
                        if (skuPriceNum1 != null && skuPriceNum1.getLimit() == 0) {
                            Log.e(TAG, "skuactivity秒杀 限购为0");
                            buyCountView.setKuCunNum(skuPriceNum1.getQuantity(), 0);
                        } else {
                            buyCountView.setKuCunNum(skuPriceNum1.getQuantity(), skuPriceNum1.getLimit());
                        }
                    }
                    //buyCountView.setKuCunNum(0, mLimitCount);
                    Log.e("test", "test.quantity11 = 0");
                }
            } else {
                if (skuPriceNum.getLimit() == 0) {
                    buyCountView.setKuCunNum(skuPriceNum.getQuantity(), 0);
                } else
                    buyCountView.setKuCunNum(skuPriceNum.getQuantity(), skuPriceNum.getLimit());
            }

        } else {
            SkuPriceNum skuPriceNum2 = resolvePriceNum("0");
            if (skuPriceNum2.getQuantity() != 0) {
                if (skuPriceNum2.getLimit() == 0) {
                    buyCountView.setKuCunNum(skuPriceNum2.getQuantity(), 0);
                } else
                    buyCountView.setKuCunNum(skuPriceNum2.getQuantity(), skuPriceNum2.getLimit());
            } else {
                buyCountView.setKuCunNum(0, mLimitCount);
                Log.e("test", "test.quantity22 = 0");
            }
        }

        if (unitBuy > 1) {
            buyCountView.setUnitBuy(unitBuy);
            mBuyCount = mBuyCount / unitBuy;
        }

        buyCountView.setCurBuyCount(mBuyCount);

        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.topMargin = (int) getResources().getDimension(R.dimen.dp_66);
        mViewHolder.mSkuPropLayout.addView(buyCountView, params);

        if (mViewHolder.mSkuPropLayout.getChildCount() > mShowMaskSkuNum) {
            mNeedShowMask = true;
            mViewHolder.mSkuPropBottomMask.setVisibility(View.VISIBLE);
        } else {
            mNeedShowMask = false;
            mViewHolder.mSkuPropBottomMask.setVisibility(View.GONE);
        }
    }

    /**
     * 解析单位购买。
     * 倍购
     */
    private void initUnitBuy() {
        if (tbDetailResultV6.getApiStack() != null) {
            try {
                String value = tbDetailResultV6.getApiStack().get(0).getValue();
                if (value != null) {
                    JSONObject object = new JSONObject(value);
                    JSONObject skuCore = object.getJSONObject("skuCore");
                    JSONObject skuItem = skuCore.getJSONObject("skuItem");
                    if (skuItem.has("unitBuy") && Integer.parseInt(skuItem.getString("unitBuy")) > 0) {
                        unitBuy = Integer.parseInt(skuItem.getString("unitBuy"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private SkuPriceNum resolvePriceNum(String skuid) {
        if (tbDetailResultV6.getApiStack() != null) {
            String value = tbDetailResultV6.getApiStack().get(0).getValue();
            try {
                if (value != null) {
                    JSONObject jsonObject = new JSONObject(value);
                    JSONObject skuCore = jsonObject.getJSONObject("skuCore");
                    JSONObject sku2info = skuCore.getJSONObject("sku2info");
                    if (sku2info.has(skuid)) {
                        JSONObject jsonObject1 = sku2info.getJSONObject(skuid);
                        SkuPriceNum skuPriceNum = JSON.parseObject(jsonObject1.toString(), SkuPriceNum.class);
                        AppDebug.e(TAG, "SkuPriceNum从apistack中拿");
                        return skuPriceNum;
                    } else {
                        if (tbDetailResultV6.getMockData() != null) {
                            AppDebug.e(TAG, "1SkuPriceNum从mockdata中拿");
                            String mockData = tbDetailResultV6.getMockData();
                            if (mockData != null) {
                                try {
                                    JSONObject mjsonObject = new JSONObject(mockData);
                                    JSONObject mskuCore = mjsonObject.getJSONObject("skuCore");
                                    JSONObject msku2info = mskuCore.getJSONObject("sku2info");
                                    SkuPriceNum skuPriceNum = null;
                                    if (msku2info.has(skuid)) {
                                        JSONObject jsonObject1 = msku2info.getJSONObject(skuid);
                                        skuPriceNum = JSON.parseObject(jsonObject1.toString(), SkuPriceNum.class);
                                    }
                                    return skuPriceNum;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    return null;

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (tbDetailResultV6.getMockData() != null) {
                AppDebug.e(TAG, "2SkuPriceNum从mockdata中拿");
                String mockData = tbDetailResultV6.getMockData();
                if (mockData != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(mockData);
                        JSONObject skuCore = jsonObject.getJSONObject("skuCore");
                        JSONObject sku2info = skuCore.getJSONObject("sku2info");
                        SkuPriceNum skuPriceNum = null;
                        if (sku2info.has(skuid)) {
                            JSONObject jsonObject1 = sku2info.getJSONObject(skuid);
                            skuPriceNum = JSON.parseObject(jsonObject1.toString(), SkuPriceNum.class);
                        }
                        return skuPriceNum;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                if (isSeckKill) {
                    if (tbDetailResultV6.getSkuKore() != null) {
                        AppDebug.e(TAG, "tbDetailResultV6.getSkuKore()!=null");
                        return resolveSeckSkucore(tbDetailResultV6.getSkuKore(), skuid);
                    }
                }
            }
        }
        return null;
    }

    private SkuPriceNum resolveSeckSkucore(String s, String skuid) {
        if (s.equals(""))
            return null;
        try {
            JSONObject jsonObject = new JSONObject(s);
            if (jsonObject.has("sku2info")) {
                JSONObject sku2info = jsonObject.getJSONObject("sku2info");
                if (sku2info.has(skuid)) {
                    JSONObject ss = sku2info.getJSONObject(skuid);
                    SkuPriceNum skuPriceNum = JSON.parseObject(ss.toString(), SkuPriceNum.class);
                    AppDebug.e(TAG, skuPriceNum.toString());
                    return skuPriceNum;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 增加默认的sku选择
     */
    private void setDefaultSkuSelected() {
        if (mSkuEngine == null || tbDetailResultV6 == null || tbDetailResultV6.getSkuBase() == null
                || tbDetailResultV6.getSkuBase().getProps() == null) {
            return;
        }
        List<TBDetailResultV6.SkuBaseBean.PropsBeanX> props = tbDetailResultV6.getSkuBase().getProps();
        Map<String, String> defaultPropMap = null;
        if (!TextUtils.isEmpty(mSkuId)) {
            defaultPropMap = mSkuEngine.getDefaultPropFromSkuId(mSkuId);
        } else {
            //如果第一次进入，默认选择sku。当进入时没有已选择的skuId，并且只有一种sku需要选择时，默认选择第一个有效的skuId
            if (props.size() == 1) {
                Map<String, String> vaildSku = mSkuEngine.getPPathIdmap();
                Iterator<Map.Entry<String, String>> iterator = vaildSku.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> next = iterator.next();
                    String skuid = next.getValue();
                    defaultPropMap = mSkuEngine.getDefaultPropFromSkuId(skuid);
                }
            }
        }

        for (int i = 0; i < props.size(); i++) {
            if (props.get(i) == null || props.get(i).getPid() == null || props.get(i).getValues() == null
                    || props.get(i).getValues().size() == 0) {// 该属性无效
                continue;
            }

            List<TBDetailResultV6.SkuBaseBean.PropsBeanX.ValuesBean> values = props.get(i).getValues();
            int size = values.size();
            Long valueId = null;
            if (size == 1) {// 只有一个属性值，则该属性值为默认
                valueId = Long.parseLong(values.get(0).getVid());
            } else if (size > 1) { // 有多个属性值时，检测是否有默认的属性
                String defaultValueId = mSkuEngine.getDefaultValueIdFromPropId(String.valueOf(props.get(i).getPid()),
                        defaultPropMap);
                if (defaultValueId != null) {
                    try {
                        valueId = Long.valueOf(defaultValueId);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }

            AppDebug.v(TAG, TAG + ".addSkuView.defaultValueId = " + valueId);
            if (valueId != null) {// 说明存在默认值
                SkuPropItemLayout prop = (SkuPropItemLayout) mViewHolder.mSkuPropLayout.getChildAt(i);
                if (prop != null) {
                    prop.setDefaultSelectSku(Long.parseLong(props.get(i).getPid()), valueId);
                }
            }
        }
    }

    /**
     * 设置商品主图
     *
     * @param imageUrl
     */
    private void setItemImage(String imageUrl) {
        if (Config.isDebug()) {
            AppDebug.v(TAG, TAG + ".setItemImage.imageUrl = " + imageUrl);
        }

        mImageUrl = imageUrl;// 保存最后一次需要显示的图片url，保证显示的图片为最新的
        String theImageUrl = null;
        if (!TextUtils.isEmpty(imageUrl) && mViewHolder.mSkuItemImage != null) {
            boolean beta = GlobalConfig.instance != null && GlobalConfig.instance.isBeta();
            if (SystemConfig.DENSITY > 1.0) {
                theImageUrl = imageUrl + (beta ? "_480x480.jpg" : "_960x960.jpg");
            } else {
                theImageUrl = imageUrl + (beta ? "_320x320.jpg" : "_640x640.jpg");
            }
            if (Config.isDebug()) {
                AppDebug.v(TAG, TAG + ".setItemImage.theImageUrl = " + theImageUrl);
            }

            mImageLoaderManager.displayImage(theImageUrl, mViewHolder.mSkuItemImage, options,
                    new SimpleImageLoadingListener() {

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            if (!imageUri.contains(mImageUrl)) {
                                if (Config.isDebug()) {
                                    AppDebug.i(TAG, TAG + ".setItemImage.onLoadingComplete.imageUri = " + imageUri
                                            + ". mImageUrl = " + mImageUrl);
                                }
                                return;
                            }
                            ImageView imageView = (ImageView) view;
                            int width = loadedImage.getWidth();
                            int height = loadedImage.getHeight();
                            Bitmap newBitmap = null;
                            if (width > height) {
                                newBitmap = Bitmap.createBitmap(loadedImage, (width - height) / 2, 0, height, height);
                            } else if (width < height) {
                                newBitmap = Bitmap.createBitmap(loadedImage, 0, 0, width, width);
                            }
                            if (newBitmap != null) {
                                imageView.setImageBitmap(newBitmap);
                            } else {
                                imageView.setImageBitmap(loadedImage);
                            }

                            super.onLoadingComplete(imageUri, view, loadedImage);
                        }
                    });
        }
    }

    /**
     * 设置价格
     *
     * @param
     */
    private void setItemPrice() {
        AppDebug.v(TAG, TAG + ".setItemPrice.price = " + mCurPrice);

        if (TextUtils.isEmpty(mCurPrice)) {
            return;
        }

        String price = null;
        if (unitBuy > 1) {
            price = String.valueOf(Double.valueOf(mCurPrice) * unitBuy);
        } else {
            price = mCurPrice;
        }
        boolean bAllSelected = false;// 如果所有属性已经选择，或者非sku商品
        Map<Long, PropData> selectMap = mSkuEngine.getSelectedPropDataMap();
        if (mSku) {
            //List<SkuProp> skuProps = tbDetailResultV6.skuModel.skuProps;
            List<TBDetailResultV6.SkuBaseBean.PropsBeanX> props = tbDetailResultV6.getSkuBase().getProps();
            if (selectMap != null && selectMap.size() > 0 && selectMap.size() == props.size()) {
                bAllSelected = true;
            }
        } else {
            bAllSelected = true;
        }

        if (bAllSelected && mBuyCount > 1) {// 如果所有属性都已经选择
            try {
                String point = null;
                int index = mCurPrice.indexOf(".");
                if (index >= 0) {//取出小数点
                    point = "0.";
                    int pointLen = mCurPrice.length() - index - 1;// 小数点位数
                    for (int i = 0; i < pointLen; i++) {
                        point += "0";
                    }
                }
                double fPrice = Double.valueOf(mCurPrice);
                if (unitBuy > 1) {
                    fPrice *= mBuyCount * unitBuy;
                } else {
                    fPrice *= mBuyCount;
                }

                if (!TextUtils.isEmpty(point)) {
                    //构造方法的字符格式,保持原有小数点个数
                    DecimalFormat decimalFormat = new DecimalFormat(point);
                    price = decimalFormat.format(fPrice);//format 返回的是字符串
                } else {
                    price = String.valueOf(fPrice);
                }
                AppDebug.v(TAG, TAG + ".setItemPrice.fPrice = " + fPrice + ", price = " + price + ".point = " + point);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (!TextUtils.isEmpty(uriPrice)) {
            price = uriPrice;
        }
        AppDebug.e(TAG, "isPreSale = " + isPreSale);
        String text = "";
        if (isPreSale && !TextUtils.isEmpty(mSubPrice)) {
            text = getString(R.string.ytm_sku_presale);
        } else {
            text = getString(R.string.ytm_sku_jiesuan_text);
            mViewHolder.mSkuPreSaleTextView.setVisibility(View.INVISIBLE);
        }
        text += price;
        SpannableString ss = new SpannableString(text);
        int start = 0;
        int end = text.indexOf("¥") + 1;
        ss.setSpan(new AbsoluteSizeSpan((int) getResources().getDimension(R.dimen.sp_20)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        start = end;
        if (price.contains(".")) {
            end = text.indexOf(".");
        } else {
            end = text.length();
        }
        if (price.length() > 10) {
            ss.setSpan(new AbsoluteSizeSpan((int) getResources().getDimension(R.dimen.sp_34)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (price.length() > 16) {
            ss.setSpan(new AbsoluteSizeSpan((int) getResources().getDimension(R.dimen.sp_28)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            ss.setSpan(new AbsoluteSizeSpan((int) getResources().getDimension(R.dimen.sp_40)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
//        ss.setSpan(new AbsoluteSizeSpan((int) getResources().getDimension(R.dimen.sp_28)),start,end,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (price.contains(".")) {
            int start2 = text.indexOf(".");
            int end2 = text.length();
            ss.setSpan(new AbsoluteSizeSpan((int) getResources().getDimension(R.dimen.sp_20)), start2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        mViewHolder.mSkuPriceTextView.setText(ss);
        if (isPreSale) {
            if (!TextUtils.isEmpty(mSubPrice)) {
                mViewHolder.mSkuPreSaleTextView.setText("预售价  ¥" + mSubPrice);
                mViewHolder.mSkuPreSaleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.sp_20));
                mViewHolder.mSkuPreSaleTextView.setVisibility(View.VISIBLE);
                if (buildOrderPreSale != null) {
                    buildOrderPreSale.setPriceText(mCurPrice);//设置定金
                }
            }
        }
    }

    /**
     * 更新库存和价格
     *
     * @param skuId
     */
    private void updateSkuKuCunAndrPrice(String skuId) {
        AppDebug.v(TAG, TAG + ".updateSkuKuCunAndrPrice.skuId = " + skuId);
        mSkuId = skuId;
        if (TextUtils.isEmpty(skuId) || tbDetailResultV6 == null || tbDetailResultV6.getSkuBase() == null
                || tbDetailResultV6.getSkuBase().getProps() == null) {
            AppDebug.e(TAG, TAG + ".updateSkuKuCunAndrPrice param error");
            return;
        }

        SkuPriceNum skuPriceNum = resolvePriceNum(skuId);
        if (skuPriceNum != null && skuPriceNum.getQuantity() != 0) {
            if (skuPriceNum != null && skuPriceNum.getPrice() != null) {
                if (skuPriceNum.getPrice().getPriceText() != null) {
                    mCurPrice = skuPriceNum.getPrice().getPriceText();
                }
                if (isPreSale) {
                    if (skuPriceNum.getSubPrice() != null && skuPriceNum.getSubPrice().getPriceText() != null) {
                        mSubPrice = skuPriceNum.getSubPrice().getPriceText();
                    }

                }

            }
            setItemPrice();
            int quantity = skuPriceNum.getQuantity();
            SkuPropItemLayout layout = getPropLayoutById(mBuyNumViewPropId);
            if (layout != null) {
                if (skuPriceNum.getLimit() == 0) {
                    layout.setKuCunNum(skuPriceNum.getQuantity(), 0);
                } else {
                    layout.setKuCunNum(quantity, skuPriceNum.getLimit());
                    layout.setCurBuyCount(1);
                }
            }
        }


    }

    /**
     * 获取属性界面对象
     *
     * @param propId
     * @return
     */
    private SkuPropItemLayout getPropLayoutById(long propId) {
        if (mViewHolder.mSkuPropLayout == null) {
            return null;
        }

        for (int i = 0; i < mViewHolder.mSkuPropLayout.getChildCount(); i++) {
            View prop = mViewHolder.mSkuPropLayout.getChildAt(i);
            if (prop instanceof SkuPropItemLayout)
                if (prop != null && ((SkuPropItemLayout) prop).getPropId() == propId) {
                    return ((SkuPropItemLayout) prop);
                }
        }

        return null;
    }

    /**
     * 进入购买界面
     */
    private void gotoOrder() {
        BuildOrderRequestBo buildOrderRequestBo = new BuildOrderRequestBo();
        AppDebug.v(TAG, TAG + ".gotoBuy.mSkuId = " + mSkuId + ".mItemId = " + mItemId + ", mBuyCount = " + mBuyCount);
        buildOrderRequestBo.setBuyNow(true);
        buildOrderRequestBo.setSkuId(mSkuId);
        buildOrderRequestBo.setItemId(mItemId);
        buildOrderRequestBo.setQuantity(mBuyCount * unitBuy);
        buildOrderRequestBo.setFrom(BaseConfig.ORDER_FROM_ITEM);
        buildOrderRequestBo.setExtParams(extParams);
        buildOrderRequestBo.setTagId(tagId);

        Intent intent = new Intent();
        intent.putExtra("mBuildOrderRequestBo", buildOrderRequestBo);
        AppDebug.e(TAG, "mBuildOrderRequestBo = " + buildOrderRequestBo.toString());
        if (isPreSale) {
            //计算尾款
            if (buildOrderPreSale != null && !TextUtils.isEmpty(mSubPrice)) {
                intent.putExtra("mBuildOrderPreSale", buildOrderPreSale);
            }
        }
        if (mProductTagBo != null) {
            intent.putExtra("mProductTagBo", mProductTagBo);
        }
        intent.setClass(this, BuildOrderActivity.class);
        AppDebug.v(TAG, TAG + ".gotoBuy.mIsJuhuasuan = " + mIsJuhuasuan);
        if (mIsJuhuasuan) {// 参团
            OnWaitProgressDialog(true);
            mBusinessRequest.requestJoinGroup(mItemId, new GetJoinGroupRequestListener(new WeakReference<BaseActivity>(
                    this), intent));
        } else {
            startActivityForResult(intent, BaseConfig.ACTIVITY_REQUEST_PAY_DONE_FINISH_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 进入购物车
     */
    private void gotoShopCart() {
        if (!NetWorkUtil.isNetWorkAvailable()) {
            showNetworkErrorDialog(false);
            return;
        }

        Intent intent = new Intent();
        intent.setClassName(SkuActivity.this, BaseConfig.SWITCH_TO_SHOPCART_LIST_ACTIVITY);
        intent.putExtra(ActivityPathRecorder.INTENTKEY_FIRST, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (!TextUtils.isEmpty(cartFrom)) {
            intent.putExtra(BaseConfig.INTENT_KEY_CARTFROM, cartFrom);
        }
        startNeedLoginActivity(intent);
        finish();
    }

    /**
     * 判断是否为聚划算商品
     */
    private void checkIsJhs() {
        mIsJuhuasuan = false;

        if (DetailV6Utils.getUnit(tbDetailResultV6) != null) {
            if (DetailV6Utils.getUnit(tbDetailResultV6).getVertical() != null) {
                if (DetailV6Utils.getUnit(tbDetailResultV6).getVertical().getJhs() != null) {
                    mIsJuhuasuan = true;
                }
            }
        }
    }


    private String getLimitCount() {
        if (tbDetailResultV6.getApiStack() != null) {
            TBDetailResultV6.ApiStackBean apiStackBean = tbDetailResultV6.getApiStack().get(0);
            String value = apiStackBean.getValue();
            try {
                JSONObject jsonObject = new JSONObject(value);
                JSONObject skuCore = jsonObject.getJSONObject("skuCore");
                JSONObject sku2info = skuCore.getJSONObject("sku2info");
                JSONObject jsonObject1 = sku2info.getJSONObject("0");
                String limit = jsonObject1.optString("limit", null);
                if (limit != null) {
                    mLimitCount = Long.parseLong(limit);
                    return limit;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return "";
            }
        }
        return "";
    }

//    /**
//     * 判断是否为限购商品
//     */
//    private void checkLimitCount() {
//        if (tbDetailResultV6 != null && tbDetailResultV6.itemControl != null) {
//            if (tbDetailResultV6.itemControl.unitControl != null
//                    && tbDetailResultV6.itemControl.unitControl.limitCount != null) {// 通用对象中获取限购数量
//                mLimitCount = tbDetailResultV6.itemControl.unitControl.limitCount;
//            } else if (tbDetailResultV6.itemControl.skuControl != null
//                    && tbDetailResultV6.itemControl.skuControl.size() > 0) {// 每个sku有自己的限购数量，一般都是相同的（待定）
//                Iterator<Entry<String, UnitControl>> iter = tbDetailResultV6.itemControl.skuControl.entrySet()
//                        .iterator();
//                while (iter.hasNext()) {
//                    Map.Entry<String, UnitControl> entry = iter.next();
//                    UnitControl unitControl = entry.getValue();
//                    if (unitControl != null && unitControl.limitCount != null) {
//                        mLimitCount = unitControl.limitCount;
//                        break;// 只取第一个（假设所有sku的限购是相同的）
//                    }
//                }
//            }
//        }
//
//        AppDebug.v(TAG, TAG + ".checkLimitCount mLimitCount = " + mLimitCount);
//    }

    /**
     * 加入购物车成功后对话框点击信息
     *
     * @param p
     * @return
     */
    private Map<String, String> initDlgProperties(Map<String, String> p) {
        if (tbDetailResultV6 != null && tbDetailResultV6.getItem() != null) {
            if (!TextUtils.isEmpty(tbDetailResultV6.getItem().getItemId())) {
                p.put("item_id", tbDetailResultV6.getItem().getItemId());
            }

            if (!TextUtils.isEmpty(tbDetailResultV6.getItem().getTitle())) {
                p.put("name", tbDetailResultV6.getItem().getTitle());
            }
        }

        if (CoreApplication.getLoginHelper(CoreApplication.getApplication()).isLogin()) {
            p.put("is_login", "1");
        } else {
            p.put("is_login", "0");
        }

        return p;
    }

    /**
     * 统计埋点信息
     */
    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> p = super.getPageProperties();
        if (tbDetailResultV6 != null && tbDetailResultV6.getItem() != null) {
            if (!TextUtils.isEmpty(tbDetailResultV6.getItem().getItemId())) {
                p.put("item_id", tbDetailResultV6.getItem().getItemId());
            }

            if (!TextUtils.isEmpty(tbDetailResultV6.getItem().getTitle())) {
                p.put("name", tbDetailResultV6.getItem().getTitle());
            }
        }

        if (!TextUtils.isEmpty(mSkuId)) {
            p.put("sku", mSkuId);
        }

        String user_id = User.getUserId();
        if (!TextUtils.isEmpty(user_id)) {
            p.put("user_id", user_id);
        }

        return p;
    }

    /**
     * 当加入购物车成功时
     */
    private void tbOnSuccessAddCart() {
        if (tbDetailResultV6 == null) {
            return;
        }

        Map<String, String> p = getPageProperties();
        if (tbDetailResultV6.getApiStack() != null) {
            if (tbDetailResultV6.getApiStack().get(0).getValue() != null) {
                SkuPriceNum skuPriceNum = resolvePriceNum("0");
                if (skuPriceNum.getQuantity() != 0) {
                    p.put("ites_amount", String.valueOf(skuPriceNum.getQuantity()));
                }
            }
        } else {
            SkuPriceNum skuPriceNum = resolvePriceNum("0");
            if (skuPriceNum.getQuantity() != 0) {
                p.put("ites_amount", String.valueOf(skuPriceNum.getQuantity()));
            }
        }
        if (tbDetailResultV6.getSeller() != null) {
            if (tbDetailResultV6.getSeller().getShopId() != null) {
                p.put("shop_id", String.valueOf(tbDetailResultV6.getSeller().getShopId()));
            }
            if (tbDetailResultV6.getSeller().getUserId() != null) {
                p.put("seller_id", String.valueOf(tbDetailResultV6.getSeller().getUserId()));
            }
        }

        String controlName = Utils.getControlName(getFullPageName(), "Add_Cart", null);
        Utils.utCustomHit(getFullPageName(), controlName, p);
    }

    /**
     * 当加入购物车成功时显示对话框
     */
    private void showTheDlgOnAddCartSuccess() {

        showSuccessAddCartDialog(true, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Map<String, String> p = Utils.getProperties();
                p = initDlgProperties(p);
                String controlName = Utils.getControlName(getFullPageName(), "Immediately_Buy", null, "Click");
                TBS.Adv.ctrlClicked(CT.Button, controlName, Utils.getKvs(p));
                gotoShopCart();
            }
        }, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Map<String, String> p = Utils.getProperties();
                p = initDlgProperties(p);
                String controlName = Utils.getControlName(getFullPageName(), "Keep_Visit", null, "Click");
                TBS.Adv.ctrlClicked(CT.Button, controlName, Utils.getKvs(p));
                finish();
            }
        }, new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Map<String, String> p = Utils.getProperties();
                    p = initDlgProperties(p);
                    String controlName = Utils.getControlName(getFullPageName(), "back", null, "Click");
                    TBS.Adv.ctrlClicked(CT.Button, controlName, Utils.getKvs(p));

                }
                return false;
            }
        });
    }

    /**
     * 当购物车已满时，弹出对话框
     */
    private void showTheDlgOnCartFull(String msg) {
        showErrorDialog(msg, getString(R.string.ytm_sku_cart_full_buy), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Map<String, String> p = Utils.getProperties();
                p = initDlgProperties(p);
                String controlName = Utils.getControlName(getFullPageName(), "cart_fulled_Immediately_Buy", null,
                        "Click");
                TBS.Adv.ctrlClicked(CT.Button, controlName, Utils.getKvs(p));

                gotoShopCart();
            }
        }, new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Map<String, String> p = Utils.getProperties();
                    p = initDlgProperties(p);
                    String controlName = Utils.getControlName(getFullPageName(), "cart_fulled_back", null, "Click");
                    TBS.Adv.ctrlClicked(CT.Button, controlName, Utils.getKvs(p));
                }
                return false;
            }
        });
    }

    /**
     * 显示二维码扫描
     */
    private void showQRCode() {
        if (tbDetailResultV6 == null || tbDetailResultV6.getSeller() == null || tbDetailResultV6.getSeller().getSellerType() == null
                || resConfig == null) {
            return;
        }

//        Drawable drawable = resConfig.getQRCodeIcon();
//        Bitmap icon = null;
//        if (drawable != null) {
//            BitmapDrawable bd = (BitmapDrawable) drawable;
//            icon = bd.getBitmap();
//        }
        showItemQRCodeFromItemId(getString(R.string.ytbv_qr_buy_item), mItemId, null, true, null, false);
    }

    /**
     * 显示二维码
     *
     * @param show
     */
    private void showQRCode(boolean show) {
        if (mViewHolder == null || resConfig == null) {
            return;
        }
        //不展示二维码
        if (show) {
//            if (mViewHolder.mSkuQrCode.getVisibility() != View.VISIBLE) {
//                mViewHolder.mSkuQrCode.setVisibility(View.VISIBLE);
//                mViewHolder.mSkuQrCode.startAnimation(mAnimationIn);
//                TextView qrcode_text = (TextView) mViewHolder.mSkuQrCode.findViewById(R.id.qrcode_text);
//                ImageView qrcode_image = (ImageView) mViewHolder.mSkuQrCode.findViewById(R.id.qrcode_image);
//                String itemUrl = "http://m.tb.cn/ZQzcgg?id=" + mItemId + "&orderMarker=v:w-ostvuuid*"
//                        + CloudUUIDWrapper.getCloudUUID() + ",w-ostvclient*tvtaobao" + "&w-ostvuuid=" + CloudUUIDWrapper.getCloudUUID() + "&w-ostvclient=tvtaobao";
//                AppDebug.e(TAG, itemUrl);
//                Drawable drawable = resConfig.getQRCodeIcon();
//                Bitmap icon = null;
//                if (drawable != null) {
//                    BitmapDrawable bd = (BitmapDrawable) drawable;
//                    icon = bd.getBitmap();
//                }
//                Bitmap qrBitmap = getQrCodeBitmap(itemUrl, icon);
//                qrcode_image.setImageBitmap(qrBitmap);
//                qrcode_text.setText(getString(R.string.ytm_sku_qr_code_title));
//
//                Map<String, String> p = getPageProperties();
//                String controlName = Utils.getControlName(getFullPageName(), "Exposure_QRCode", null);
//                Utils.utCustomHit(getFullPageName(), controlName, p);
//            }
        } else {
            if (mViewHolder.mSkuQrCode.getVisibility() == View.VISIBLE) {
                mViewHolder.mSkuQrCode.setVisibility(View.GONE);
                mViewHolder.mSkuQrCode.startAnimation(mAnimationOut);
            }
        }
    }

    /**
     * 加载商品详情
     *
     * @param itemId
     */
    private void requestGoodsDetail(long itemId) {
        if (itemId < 0) {
            finish();
            return;
        }
        OnWaitProgressDialog(true);
        String params = Utils.jsonString2HttpParam(extParams);
        if (Config.isDebug()) {
            AppDebug.v(TAG, TAG + ".requestGoodsDetail.itemId = " + itemId + ".extParams = " + extParams + ",params = "
                    + params);
        }
//        mBusinessRequest.requestGetItemDetailV5(String.valueOf(itemId), params, new GetGoodsDetailRequestListener(
//                new WeakReference<BaseActivity>(this)));
        mBusinessRequest.requestGetItemDetailV6New(String.valueOf(itemId), params,
                new GetGoodsDetailRequestListener(new WeakReference<BaseActivity>(this)));


    }

    @Override
    protected String getAppTag() {
        return "Tb";
    }

    @Override
    public String getPageName() {
        return "SureJoin";
    }

    private class ViewHolder {

        private View mSkuRootLayout = LayoutInflater.from(SkuActivity.this).inflate(R.layout.ytm_sku_activity, null);

        private ImageView mSkuItemImage = (ImageView) mSkuRootLayout.findViewById(R.id.sku_item_image);
        private TextView mSkuItemTitle = (TextView) mSkuRootLayout.findViewById(R.id.sku_item_title);
        private RelativeLayout mSkuQrCode = (RelativeLayout) mSkuRootLayout.findViewById(R.id.sku_qr_code);
        private LinearLayout mSkuPropLayout = (LinearLayout) mSkuRootLayout.findViewById(R.id.sku_prop_layout);//存放sku属性的线性布局
        private TextView mSkuDoneTextView = (TextView) mSkuRootLayout.findViewById(R.id.sku_done_text_view);
        private TextView mSkuPriceTextView = (TextView) mSkuRootLayout.findViewById(R.id.sku_price);
        private TextView mSkuPreSaleTextView = (TextView) mSkuRootLayout.findViewById(R.id.sku_price_presale);
        private ImageView mSkuPropTopMask = (ImageView) mSkuRootLayout.findViewById(R.id.sku_prop_top_mask);
        private ImageView mSkuPropBottomMask = (ImageView) mSkuRootLayout.findViewById(R.id.sku_prop_bottom_mask);
        private ScrollView mSkuPropScrollView = (ScrollView) mSkuRootLayout.findViewById(R.id.sku_prop_scrollview);

        private ContractItemLayout contractItemLayout = null;

        public ViewHolder() {
            mSkuQrCode.setBackgroundColor(getResources().getColor(R.color.ytm_qr_code_bg));
            mSkuDoneTextView.setNextFocusRightId(R.id.sku_done_text_view);
            mSkuDoneTextView.setOnFocusChangeListener(new OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    AppDebug.v(TAG, TAG + ".v = " + v + ", hasFocus = " + hasFocus);

                    if (v == mSkuDoneTextView) {
                        if (hasFocus) {
                            mSkuDoneTextView.setBackgroundResource(R.drawable.ytm_background_focus);
                            mSkuDoneTextView.setTextColor(getResources().getColor(android.R.color.white));
                        } else {
                            mSkuDoneTextView.setBackgroundColor(getResources().getColor(R.color.ytm_button_normal));
                            mSkuDoneTextView.setTextColor(getResources().getColor(
                                    R.color.ytm_sku_done_unfocus_text_color));
                        }
                    }
                }
            });
            mSkuDoneTextView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    //首先检查是否选择了所有商品
                    if (tbDetailResultV6 == null) {
                        AppDebug.e(TAG, "确定购买按钮点击，但是tbDetailResultV6=null返回");
                        return;
                    }
//            if (DetailV6Utils.getUnit(tbDetailResultV6)!=null){
//                if (DetailV6Utils.getUnit(tbDetailResultV6).getVertical()!=null){
//                    if (DetailV6Utils.getUnit(tbDetailResultV6).getVertical().getPresale()!=null){
//                        showQRCode();
//                        return;
//                    }
//                }
//            }
                    if (tbDetailResultV6 != null && tbDetailResultV6.getTrade() != null && tbDetailResultV6.getTrade().getRedirectUrl() != null) {
                        if (tbDetailResultV6.getTrade().getRedirectUrl().contains("trip")) {
                            showQRCode();
                            return;

                        }
                    }
//                    if (mType.equals(SkuType.BUY)) {
//                        //降级商品或者不支持购买时提示二维码购买
//                        if (tbDetailResultV6.itemControl != null) {
//                            if (!TextUtils.isEmpty(tbDetailResultV6.itemControl.degradedItemUrl)
//                                    || (tbDetailResultV6.itemControl.unitControl != null && !tbDetailResultV6.itemControl.unitControl.buySupport)) {
//                                showQRCode();
//                                return;
//                            }
//                        }
//                    }

                    boolean bAllSelected = false;// 如果所有属性已经选择，或者非sku商品
                    boolean needChooseContract = mViewHolder.contractItemLayout != null && mViewHolder.contractItemLayout.getSelectedVersionData() == null;
                    Map<Long, PropData> selectMap = mSkuEngine.getSelectedPropDataMap();
                    if (mSku) {
                        List<TBDetailResultV6.SkuBaseBean.PropsBeanX> props = tbDetailResultV6.getSkuBase().getProps();

                        if (selectMap != null && selectMap.size() > 0 && selectMap.size() == props.size()) {
                            bAllSelected = true;
                        }

                    } else {
                        bAllSelected = true;
                    }

                    Map<String, String> p = getPageProperties();
                    String controlName = Utils.getControlName(getFullPageName(), "Next", null, "Click");

                    if (bAllSelected && !needChooseContract) {// 如果所以属性都已经选择
                        AppDebug.e(TAG, "所有属性已经选择");
                        if (!CoreApplication.getLoginHelper(SkuActivity.this).isLogin()) {
                            setLoginActivityStartShowing();
                            CoreApplication.getLoginHelper(SkuActivity.this).startYunosAccountActivity(SkuActivity.this,
                                    false);
                            p.put("is_login", "0");
                            TBS.Adv.ctrlClicked(CT.Button, controlName, Utils.getKvs(p));
                            return;
                        }
                        p.put("is_login", "1");

                        if (mSku) {// 如果是sku商品，判断该商品库存是否为0
                            if (TextUtils.isEmpty(mSkuId)) {
                                mSkuId = mSkuEngine.getSkuId();
                            }
                            AppDebug.e(TAG, TAG + ".mSkuDoneTextView.setOnClickListener.mSkuId = " + mSkuId);
                            if (TextUtils.isEmpty(mSkuId) || tbDetailResultV6.getSkuBase().getProps() == null) {
                                return;
                            }
                            //SkuPriceAndQuanitiy skuPriceAndQuanity = mTBDetailResultVO.skuModel.skus.get(mSkuId);
                            SkuPriceNum skuPriceNum = resolvePriceNum(mSkuId);
                            AppDebug.e(TAG, TAG + ".mSkuDoneTextView.setOnClickListener.skuPriceAndQuanity = "
                                    + skuPriceNum);
                            if (skuPriceNum == null
                                    || skuPriceNum.getQuantity() <= 0) {
                                Toast.makeText(SkuActivity.this,
                                        SkuActivity.this.getString(R.string.ytm_sku_zero_cucun), Toast.LENGTH_SHORT)
                                        .show();
                                return;
                            }
                        }
                        if (mViewHolder.contractItemLayout != null && mViewHolder.contractItemLayout.getSelectedVersionData() != null) {

                            try {
                                JSONObject jsonObject = TextUtils.isEmpty(extParams) ? new JSONObject() : new JSONObject(extParams);
                                String versionCode = mViewHolder.contractItemLayout.getSelectedVersionData().versionCode;
                                String planId = mViewHolder.contractItemLayout.getSelectedVersionData().planId;
                                jsonObject.put("合约机的key", versionCode);
                                if (!TextUtils.isEmpty(planId)) {
                                    String wttparam = planId + "_0_0_0_0_0_" + versionCode;
                                    jsonObject.put("alicom_wtt_param", wttparam);
                                }
                                jsonObject.put("alicom_wtt_cart", "1");

                                extParams = jsonObject.toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (mType.equals(TradeType.BUY)) {
                            p.put("type", TradeType.BUY.toString());
                            //如果来源是海尔Vip购物,需要先领取优惠券
                            if (getmFrom() != null && getmFrom().toLowerCase().contains("tvhongbao")) {
                                OnWaitProgressDialog(true);
                                mBusinessRequest.requestDoItemCoupon(mItemId, new DoItemCouponListener(
                                        new WeakReference<BaseActivity>(SkuActivity.this)));
                            } else {
                                gotoOrder();
                            }
                        } else if (mType.equals(TradeType.ADD_CART)) {
                            p.put("type", TradeType.ADD_CART.toString());
                            OnWaitProgressDialog(true);

                            mBusinessRequest.addBag(mItemId, mBuyCount * unitBuy, mSkuId, extParams,
                                    new GetAddCartRequestListener(new WeakReference<BaseActivity>(SkuActivity.this)));
                        } else if (mType.equals(TradeType.EDIT_CART)) {
                            if (!NetWorkUtil.isNetWorkAvailable()) {
                                showNetworkErrorDialog(false);
                                return;
                            }

                            p.put("type", TradeType.EDIT_CART.toString());
                            Intent intent = new Intent();
                            intent.putExtra(BaseConfig.INTENT_KEY_SKUID, mSkuId);
                            intent.putExtra(BaseConfig.INTENT_KEY_BUY_COUNT, mBuyCount * unitBuy);

                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        }
                        p.put("all_select", "true");
                        TBS.Adv.ctrlClicked(CT.Button, controlName, Utils.getKvs(p));
                    } else {
                        //SkuProp skuProp = mSkuEngine.getUnSelectProp();
                        if (!bAllSelected) {
                            TBDetailResultV6.SkuBaseBean.PropsBeanX unSelectProp = mSkuEngine.getUnSelectProp();
                            if (unSelectProp != null) {
                                p.put("all_select", "false");
                                p.put("info_id", String.valueOf(unSelectProp.getPid()));
                                p.put("info", unSelectProp.getName());
                                Utils.utControlHit(controlName, p);
                                Toast.makeText(SkuActivity.this,
                                        unSelectProp.getName() + SkuActivity.this.getString(R.string.ytm_sku_no_select),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                if (Config.isDebug()) {
                                    AppDebug.e(TAG, TAG + ".mSkuDoneTextView.setOnClickListener params error");
                                }
                                return;
                            }
                        } else {
                            Toast.makeText(SkuActivity.this,
                                    "请选择购买方式",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    /**
     * 属性组 聚焦后的事件
     */
    private OnPropViewFocusListener mOnPropViewFocusListener = new OnPropViewFocusListener() {

        @Override
        public void OnPorpViewFocus(View view, boolean focus) {
            if (mViewHolder != null && mNeedShowMask && focus && view != null) {
                int index = 0;
                int count = mViewHolder.mSkuPropLayout.getChildCount();
                for (int i = 0; i < count; i++) {
                    if (view == mViewHolder.mSkuPropLayout.getChildAt(i)) {
                        index = i;
                        break;
                    }
                }

                if (index == 0) {
                    mViewHolder.mSkuPropTopMask.setVisibility(View.GONE);
                    mViewHolder.mSkuPropBottomMask.setVisibility(View.VISIBLE);
                } else if (index != 0 && index < count - mShowMaskSkuNum) {
                    mViewHolder.mSkuPropBottomMask.setVisibility(View.VISIBLE);
                } else if (index >= mShowMaskSkuNum && index != count - 1) {
                    mViewHolder.mSkuPropTopMask.setVisibility(View.VISIBLE);
                } else if (index == count - 1) {
                    mViewHolder.mSkuPropTopMask.setVisibility(View.VISIBLE);
                    mViewHolder.mSkuPropBottomMask.setVisibility(View.GONE);
                }
            }
        }
    };

    /**
     * 获取商品详情数据
     * Class Descripton.
     *
     * @author mi.cao
     * @data 2015年2月27日 下午6:06:17
     */
    private static class GetGoodsDetailRequestListener extends BizRequestListener<TBDetailResultV6> {

        public GetGoodsDetailRequestListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            SkuActivity activity = (SkuActivity) mBaseActivityRef.get();
            if (activity != null) {
                if (activity.isFinishing()) {
                    return true;
                }
                if (Config.isDebug()) {
                    AppDebug.i(activity.TAG, activity.TAG + ".GetGoodsDetailRequestListener.onError.resultCode = "
                            + resultCode + ".msg = " + msg);
                }
                activity.OnWaitProgressDialog(false);
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                activity.finish();
            }
            return true;
        }

        @Override
        public void onSuccess(TBDetailResultV6 data) {
            SkuActivity activity = (SkuActivity) mBaseActivityRef.get();
            if (activity != null) {
                if (activity.isFinishing()) {
                    return;
                }
                if (Config.isDebug()) {
                    AppDebug.i(activity.TAG, activity.TAG + ".GetGoodsDetailRequestListener.onSuccess.data = " + data);
                }

                activity.OnWaitProgressDialog(false);
                activity.tbDetailResultV6 = data;
                activity.initView();
                activity.checkIsJhs();// 检测是否聚划算商品
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    /**
     * 参团请求
     * Class Descripton.
     *
     * @author mi.cao
     * @data 2015年3月13日 下午3:48:26
     */
    private static class GetJoinGroupRequestListener extends BizRequestListener<JoinGroupResult> {

        private final Intent intent;

        public GetJoinGroupRequestListener(WeakReference<BaseActivity> mBaseActivityRef, Intent intent) {
            super(mBaseActivityRef);
            this.intent = intent;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            SkuActivity activity = (SkuActivity) mBaseActivityRef.get();
            if (activity != null) {
                if (activity.isFinishing()) {
                    return true;
                }
                if (Config.isDebug()) {
                    AppDebug.i(activity.TAG, activity.TAG + ".GetJoinGroupRequestListener.onError.resultCode = "
                            + resultCode + ".msg = " + msg);
                }

                activity.OnWaitProgressDialog(false);
            }
            return false;
        }

        @Override
        public void onSuccess(JoinGroupResult data) {
            SkuActivity activity = (SkuActivity) mBaseActivityRef.get();
            if (activity != null) {
                if (activity.isFinishing()) {
                    return;
                }
                if (Config.isDebug()) {
                    AppDebug.i(activity.TAG, activity.TAG + ".GetJoinGroupRequestListener.onSuccess.data = " + data);
                }

                activity.OnWaitProgressDialog(false);
                activity.startActivityForResult(intent, BaseConfig.ACTIVITY_REQUEST_PAY_DONE_FINISH_CODE);
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    /**
     * 加入购物车
     * Class Descripton.
     *
     * @author mi.cao
     * @data 2015年3月13日 下午6:01:02
     */
    private static class GetAddCartRequestListener extends BizRequestListener<ArrayList<SearchResult>> {

        public GetAddCartRequestListener(WeakReference<BaseActivity> mBaseActivityRef) {
            super(mBaseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            SkuActivity activity = (SkuActivity) mBaseActivityRef.get();
            if (activity != null) {
                if (Config.isDebug()) {
                    AppDebug.i(activity.TAG, activity.TAG + ".GetAddCartRequestListener.onError.resultCode = "
                            + resultCode + ".msg = " + msg);
                }
                activity.OnWaitProgressDialog(false);
                if (resultCode == ServiceCode.ADD_CART_FAILURE.getCode()) {// 购物车满99件时
                    activity.showTheDlgOnCartFull(msg);
                    return true;
                }
            }
            return false;
        }

        @Override
        public void onSuccess(ArrayList<SearchResult> data) {
            SkuActivity activity = (SkuActivity) mBaseActivityRef.get();
            if (activity != null) {
                if (Config.isDebug()) {
                    AppDebug.i(activity.TAG, activity.TAG + ".GetAddCartRequestListener.onSuccess.data = " + data);
                }
                activity.tbOnSuccessAddCart();
                activity.OnWaitProgressDialog(false);
                activity.showTheDlgOnAddCartSuccess();
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    private static class DoItemCouponListener extends BizRequestListener<String> {

        public DoItemCouponListener(WeakReference<BaseActivity> mBaseActivityRef) {
            super(mBaseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            SkuActivity activity = (SkuActivity) mBaseActivityRef.get();
            if (activity != null) {
                activity.OnWaitProgressDialog(false);
                if (resultCode == ServiceCode.BONUS_BALANCES_INSUFFICIENT.getCode()) {
                    Toast.makeText(activity.getApplicationContext(), "阿里V代金券余额不足", Toast.LENGTH_SHORT).show();
                }
                activity.gotoOrder();
            }
            return true;
        }

        @Override
        public void onSuccess(String data) {
            SkuActivity activity = (SkuActivity) mBaseActivityRef.get();
            if (activity != null) {
                activity.OnWaitProgressDialog(false);
                activity.gotoOrder();
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    @Override
    public boolean isUpdateBlackList() {
        return true;
    }

    /**
     * 返利相关参数（主要关注tvOptions），加购商品的tvOptions服务端持久化存储
     */
    private String buildAddBagExParams() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (mProductTagBo != null) {
                jsonObject.put("outPreferentialId", mProductTagBo.getOutPreferentialId());
                jsonObject.put("tagId", mProductTagBo.getTagId());
            }

            if (mType.equals(TradeType.ADD_CART)) {
                //如果是加购操作
                TvOptionsConfig.setTvOptionsCart(true);
            }
            jsonObject.put("tvOptions", TvOptionsConfig.getTvOptions());
            TvOptionsConfig.setTvOptionsCart(false);
            jsonObject.put("appKey", Config.getChannel());
//            jsonObject.put("appKey","142857");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

}
