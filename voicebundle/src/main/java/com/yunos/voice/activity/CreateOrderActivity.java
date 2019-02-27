package com.yunos.voice.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.taobao.wireless.trade.mbuy.sdk.co.Component;
import com.taobao.wireless.trade.mbuy.sdk.co.ComponentTag;
import com.taobao.wireless.trade.mbuy.sdk.co.ComponentType;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.AddressComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.AddressOption;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.DeliveryMethodComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.ItemComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.ItemInfoComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.ItemPayComponent;
import com.taobao.wireless.trade.mbuy.sdk.engine.BuyEngine;
import com.tvtaobao.voicesdk.ASRNotify;
import com.tvtaobao.voicesdk.bo.DomainResultVo;
import com.tvtaobao.voicesdk.bo.PageReturn;
import com.tvtaobao.voicesdk.register.type.ActionType;
import com.tvtaobao.voicesdk.request.CancelOrderRequest;
import com.tvtaobao.voicesdk.request.CreateOrderWithDelayRequest;
import com.tvtaobao.voicesdk.request.QueryOrderRequest;
import com.tvtaobao.voicesdk.utils.LogPrint;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tv.core.pay.LoginHelperImpl;
import com.yunos.tv.core.util.ActivityPathRecorder;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tv.core.util.SharedPreferencesUtils;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.BuildOrderRequestBo;
import com.yunos.tvtaobao.biz.request.bo.ProductTagBo;
import com.yunos.tvtaobao.biz.request.bo.TBDetailResultV6;
import com.yunos.tvtaobao.biz.request.item.GetProductTagRequest;
import com.yunos.tvtaobao.payment.alipay.AlipayAuthManager;
import com.yunos.voice.R;
import com.yunos.voice.view.AutoTextView;
import com.yunos.voice.view.OrderPromptView;
import com.yunos.voice.view.OrderView;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pan on 2017/8/8.
 */

public class CreateOrderActivity extends BaseActivity {
    private final String TAG = "CreateOrderActivity";
    private LoginAuthReceived loginAuthReceived;

    private OrderView mOrderView;
    private OrderPromptView mPromptView;
    private TextView tvReply;
    private LinearLayout lytips;
    private RelativeLayout mLayout;
    private AutoTextView tvTips;

    private String itemId;
    private String price;
    private String skuId;
    private String outOrderId;
    private int quantity = 1;
    private final int RESULT_CODE_NOVICE_GUIDE = 10001;
    private static List<Component> componentList;

    private OrderHandler mHandler;

    private boolean needGoNoBuy = false;    //判断是否需要邮费，需要游戏，进行提醒
    private ProductTagBo mProductTagBo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onKeepActivityOnlyOne(CreateOrderActivity.class.getName());

        setContentView(R.layout.activity_order_info);
        mLayout = (RelativeLayout) findViewById(R.id.order_layout);
        mOrderView = new OrderView(new WeakReference<>(this));
        tvTips = findViewById(R.id.order_info_tip);
        tvReply = findViewById(R.id.order_info_reply);
        lytips = findViewById(R.id.order_info_tip_layout);

        componentList = new ArrayList<>();
        mHandler = new OrderHandler();

        Bundle bundle = getIntent().getExtras();
        itemId = bundle.getString("itemId");
        price = bundle.getString("price");
        skuId = bundle.getString("skuId");

        LogPrint.i(TAG, TAG + ".onCreate itemId : " + itemId);
        checkLoginAuth();
    }

    /**
     * 检查登陆授权
     * 如果没有登陆，需要进行支付宝登陆。
     * 如果没有授权，需要进行支付宝授权。
     */
    public void checkLoginAuth() {
        if (!LoginHelperImpl.getJuLoginHelper().isLogin()) {
            Intent intent = new Intent();
            intent.setClassName(AppInfo.getPackageName(), "com.yunos.voice.activity.LoginAuthActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("QRCodeType", "login");
            intent.putExtra("from", "search");
            startActivity(intent);
            addLoginAuthReceived();
        } else {
            AlipayAuthManager.authCheck(new AlipayAuthManager.AuthCheckListener() {
                @Override
                public void onAuthCheckResult(final boolean isAuth, String alipayId) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isAuth) {
                                dealWithQuickOrder();
                            } else {
                                Intent intent = new Intent();
                                intent.setClassName(AppInfo.getPackageName(), "com.yunos.voice.activity.LoginAuthActivity");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("QRCodeType", "auth");
                                intent.putExtra("from", "search");
                                startActivity(intent);
                                addLoginAuthReceived();
                            }
                        }
                    });
                }
            });
        }
    }

    private boolean isRegisterReceived = false;

    protected void addLoginAuthReceived() {
        if (!isRegisterReceived) {
            isRegisterReceived = true;
            if (loginAuthReceived == null) {
                loginAuthReceived = new LoginAuthReceived();
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.yunos.tvtaobao.VOICE.ALIPAY.LOGINAUTH");
            intentFilter.addAction("com.yunos.tvtaobao.VOICE.ALIPAY.AUTH");
            registerReceiver(loginAuthReceived, intentFilter);
        }
    }

    /**
     * 移除登录授权的状态监听
     */
    protected void removeLoginAuthReceived() {
        if (isRegisterReceived) {
            unregisterReceiver(loginAuthReceived);
            isRegisterReceived = false;
        }
    }

    public class LoginAuthReceived extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogPrint.e(TAG, "LoginAuthReceived " + intent.getAction());
            if ("com.yunos.tvtaobao.VOICE.ALIPAY.LOGINAUTH".equals(intent.getAction())
                    || "com.yunos.tvtaobao.VOICE.ALIPAY.AUTH".equals(intent.getAction())) {
                String status = intent.getStringExtra("status");
                if ("success".equals(status)) {
                    dealWithQuickOrder();
                    removeLoginAuthReceived();
                } else {
                    removeLoginAuthReceived();
                    finish();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeLoginAuthReceived();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    @Override
    protected PageReturn onVoiceAction(DomainResultVo object) {
        LogPrint.i(TAG, TAG + ".onVoiceAction action : " + object.getIntent());
        PageReturn pageReturn = new PageReturn();
        switch (object.getIntent()) {
            case ActionType.CANCEL_ORDER:
                if (!TextUtils.isEmpty(outOrderId)) {
                    cancelOrder(outOrderId);

                    pageReturn.isHandler = true;
                    pageReturn.feedback = "正在为您取消订单";
                }
                break;
            case ActionType.CONTINUE_TO_BUY:
                needGoNoBuy = false;
                if(mProductTagBo!=null){
                    gotoCreateOrderWithDelay(mProductTagBo.getOutPreferentialId(),mProductTagBo.getTagId(),
                            TvOptionsConfig.getTvOptions(),itemId, skuId, deliveryId, quantity);
                }

                Map<String, String> map = getProperties();
                map.put("itemId", itemId);
                Utils.utCustomHit(getPageName(), "Voice_order_postage_continue", map);

                pageReturn.isHandler = true;
                pageReturn.feedback = "正在帮您购买";
                break;
        }

        return pageReturn;
    }


    /**
     * 判断sku数，大于1跳转到skuActivity，
     */
    private void dealWithQuickOrder() {
        mLayout.setVisibility(View.VISIBLE);
        OnWaitProgressDialog(true);
        LogPrint.i(TAG, TAG + ".dealWithQuickOrder");
        anaylisysTaoke(itemId);
        if (skuId != null) {
            requestRebateTag();
        } else {
            BusinessRequest.getBusinessRequest().requestGetItemDetailV6New(itemId, null, new DetailListener());
        }
    }

    /**
     * 显示提示词，以上下滚动的形式展示
     *
     * @param tips 提示词
     */
    private void showTips(List<String> tips) {
        if (lytips.getVisibility() == View.GONE) {
            lytips.setVisibility(View.VISIBLE);
        }

        tvTips.autoScroll(tips);
    }

    /**
     * 隐藏提示内容
     */
    private void hiddenTips() {
        if (lytips.getVisibility() == View.VISIBLE) {
            tvTips.setText("");
            lytips.setVisibility(View.GONE);
        }
    }

    /**
     * 设置展示内容，以及TTS播放
     *
     * @param tts_text 播放内容
     */
    private void playTTS(String tts_text) {
        tvReply.setText(tts_text);

        ASRNotify.getInstance().playTTS(tts_text);
    }

    /**
     * 去获取用户默认地址
     *
     * @param itemId 商品id
     * @param skuId  skuid
     */
    private void buildOrder(String itemId, String skuId, int quantity) {
        BuildOrderRequestBo buildOrderRequestBo = new BuildOrderRequestBo();
        LogPrint.w(TAG, TAG + ".buildOrder mSkuId = " + skuId + ", mItemId = " + itemId + ", mBuyCount = " + quantity);
        buildOrderRequestBo.setBuyNow(true);
        buildOrderRequestBo.setSkuId(skuId);
        buildOrderRequestBo.setItemId(itemId);
        buildOrderRequestBo.setQuantity(quantity);
        buildOrderRequestBo.setFrom("item");
        buildOrderRequestBo.setExtParams(buildOrderExParams());
        boolean hasAddCart = getIntent().getBooleanExtra("hasAddCart", false);
        BusinessRequest.getBusinessRequest().buildOrder(buildOrderRequestBo,hasAddCart, new BuildOrderListener());
    }

    /**
     * 去进行延迟下单请求。目前在地址请求完成之后去请求
     *
     * @param deliverId 地址id
     */
    private void gotoCreateOrderWithDelay(String outPreferentialId,String tagId,String tvOptions,String itemId, String skuId, String deliverId, int quantity) {
        BusinessRequest.getBusinessRequest().baseRequest(new CreateOrderWithDelayRequest(outPreferentialId,tagId,tvOptions,itemId, skuId, deliverId, quantity), new CreateOrderWithDelay(), true);
    }

    /**
     * 查询下单状态
     *
     * @param outOrderId 订单号
     */
    private void queryDelayOrderInfo(String outOrderId) {
        BusinessRequest.getBusinessRequest().baseRequest(new QueryOrderRequest(outOrderId), new QueryOrderListener(), true);
    }

    /**
     * 取消队列中的将下单的队列
     */
    private void cancelOrder(String outOrderId) {
        BusinessRequest.getBusinessRequest().baseRequest(new CancelOrderRequest(outOrderId), new CancelOrderListener(), true);
    }

    /**
     * 展示错误
     *
     * @param type 0 : 地址错误,  1 : 统一去手淘购买,  2 : 未付款订单
     */
    private void showErrorPrompt(int type) {
        mOrderView.hiddenOrderInfo();//隐藏商品信息
        hiddenTips();
        mPromptView = new OrderPromptView(new WeakReference<>(this));

        switch (type) {
            case 0:
                mPromptView.showNotAddress();

                playTTS("您还没有设置收货地址，请先扫码到手机淘宝设置收货地址！");
                break;
            case 1:
                mPromptView.showBuyToCellPhone(itemId);

                playTTS("亲，该商品请到手机淘宝上完成购买");
                break;
            case 2:
                mPromptView.showWaitPay();

                playTTS("支付遇到了些小问题，请打开手机淘宝，进入未付款订单完成支付");
                break;
        }
    }

    private class OrderHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    queryDelayOrderInfo(outOrderId);
                    break;
            }
        }
    }

    ;

    /**
     * 获取订单信息
     */
    class BuildOrderListener implements RequestListener<String> {

        @Override
        public void onRequestDone(String data, int resultCode, String msg) {
            LogPrint.e(TAG, TAG + ".BuildOrderListener code : " + resultCode + " ,msg : " + msg);
            mOrderView.showBackGround();
            OnWaitProgressDialog(false);
            if (resultCode == 200) {
                if (resolverDataFromMTop(data)) {
                    if (Double.parseDouble(postage) > 0) {
                        needGoNoBuy = true;

                        playTTS("该商品需要" + postage + "元邮费，请问是否继续购买?");
                    } else {
                        if(mProductTagBo!=null){
                            gotoCreateOrderWithDelay(mProductTagBo.getOutPreferentialId(),mProductTagBo.getTagId(),
                                    TvOptionsConfig.getTvOptions(),itemId, skuId, deliveryId, quantity);
                        }
                    }
                } else {
                    showErrorPrompt(1);

                    Map<String, String> map = getProperties();
                    map.put("errormessage", reason);
                    map.put("itemId", itemId);
                    Utils.utCustomHit(getPageName(), "Voice_order_beforeorder", map);
                }
            } else {
                Map<String, String> map = getProperties();
                map.put("errormessage", msg);
                map.put("itemId", itemId);
                Utils.utCustomHit(getPageName(), "Voice_order_beforeorder", map);

                if (resultCode == 334) {
                    showErrorPrompt(0);
                    return;
                }

                showErrorPrompt(1);
            }
        }
    }

    /**
     * 延迟下单监听
     */
    class CreateOrderWithDelay implements RequestListener<Object> {

        @Override
        public void onRequestDone(Object data, int resultCode, String msg) {
            LogPrint.e(TAG, TAG + ".CreateOrderWithDelay code : " + resultCode + " ,msg : " + msg);
            if (resultCode == 200) {
                JSONObject object = JSONObject.parseObject(data.toString());

                JSONObject alipay = object.getJSONObject("alipay");
                String authState = alipay.getString("authState");
                LogPrint.e(TAG, TAG + ".CreateOrderWithDelay authState : " + authState);
                if (authState != null && authState.equals("COMPLATE_AUTH")) {
                    if (object.containsKey("outOrderId")) {
                        outOrderId = object.getString("outOrderId");
                        String delayTime = object.getString("delayTime");
                        int delay = Integer.parseInt(delayTime);
                        LogPrint.e(TAG, TAG + ".CreateOrderWithDelay outOrderId : " + outOrderId + " ,delay : " + delay);

                        mHandler.sendEmptyMessageDelayed(0, delay * 1000);

                        playTTS("已为您安排下单，祝您购物愉快！您还想买什么？");
                        List<String> tips = new ArrayList<>();
                        tips.add("下错单了？" + delayTime + "秒内可以对我说“取消订单”");
                        showTips(tips);
                        return;
                    }
                }
                showErrorPrompt(1);
            } else {
//                mView.createOrderPrompt("下单失败，" + msg);
                playTTS(msg);
            }
        }
    }

    class QueryOrderListener implements RequestListener<String> {

        @Override
        public void onRequestDone(String data, int resultCode, String msg) {
            LogPrint.e(TAG, TAG + ".QueryOrderListener code : " + resultCode + " ,msg : " + msg);
            if (resultCode == 200) {
                LogPrint.e(TAG, TAG + ".QueryOrderListener data : " + data);
                if (data.equals("2")) {

                    Map<String, String> map = getProperties();
                    map.put("feedback", "success");
                    Utils.utCustomHit(getPageName(), "Voice_order_feedback", map);

                    finish();
                } else if (data.equals("1")) {
                    Map<String, String> map = getProperties();
                    map.put("feedback", "failure");
                    Utils.utCustomHit(getPageName(), "Voice_order_feedback", map);

                    showErrorPrompt(2);
                } else if (data.equals("query")) {
                    mHandler.sendEmptyMessageDelayed(0, 2 * 1000);
                } else {
                    Map<String, String> map = getProperties();
                    map.put("feedback", "failure");
                    Utils.utCustomHit(getPageName(), "Voice_order_feedback", map);

                    playTTS(data);
                }

            } else {
                mHandler.sendEmptyMessageDelayed(0, 2 * 1000);
            }
        }
    }

    class CancelOrderListener implements RequestListener<String> {

        @Override
        public void onRequestDone(String data, int resultCode, String msg) {
            mHandler.removeMessages(0);
            if (data.equals("true")) {
                playTTS("成功取消订单，您还想买什么？");

                outOrderId = null;
                Toast.makeText(CreateOrderActivity.this, "取消下单成功！", Toast.LENGTH_SHORT).show();

                Utils.utCustomHit(getPageName(), "Voice_order_cancel_success", getProperties());
            } else {
                Toast.makeText(CreateOrderActivity.this, "取消下单失败，已为您下单！", Toast.LENGTH_SHORT).show();

                Utils.utCustomHit(getPageName(), "Voice_order_cancel_error", getProperties());
            }
        }
    }

    /**
     * 获取商品详情，主要是为了获取skuId
     */
    class DetailListener implements RequestListener<TBDetailResultV6> {

        @Override
        public void onRequestDone(TBDetailResultV6 data, int resultCode, String msg) {
            LogPrint.e(TAG, TAG + ".DetailListener code : " + resultCode + " ,msg : " + msg);
            if (resultCode == 200) {
                List<TBDetailResultV6.SkuBaseBean.SkusBean> ppathIdmap = data.getSkuBase().getSkus();

                if (ppathIdmap == null || ppathIdmap.size() == 0) {
                    openNoviceGuide(data.getItem().getItemId(), null, quantity);
                } else if (ppathIdmap.size() > 1) {
                    Intent intent = new Intent();
                    intent.setClassName(CreateOrderActivity.this, BaseConfig.SWITCH_TO_SKU_ACTIVITY);
                    intent.putExtra("mTBDetailResultVO", data);
                    startActivity(intent);
                    OnWaitProgressDialog(false);
                    finish();
                } else {
                    LogPrint.e(TAG, ".DetailListener skuSize : " + ppathIdmap.size());
                    for (int i = 0; i < ppathIdmap.size(); i++) {
                        skuId = ppathIdmap.get(i).getSkuId();
                        openNoviceGuide(data.getItem().getItemId(), skuId, quantity);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 接收外部传进来的数据，并请求预订单数据
     */
    String userinfo;
    String areainfo;
    String titleinfo;
    String imgUrl;
    String skuinfo;
    String postage = "0.00";
    String deliveryId;
    String reason;

    private boolean resolverDataFromMTop(String obj) {
        if (TextUtils.isEmpty(obj)) {
            return false;
        }

        BuyEngine mBuyEngine = new BuyEngine();
        JSONObject jobj = JSON.parseObject(obj);
        componentList = mBuyEngine.parse(jobj);
        LogPrint.e(TAG, "CheckOutOrderReason----size : " + componentList.size());
        for (int i = 0; i < componentList.size(); i++) {

            Component component = componentList.get(i);
            ComponentTag tag = ComponentTag.getComponentTagByDesc(component.getTag());
            if (component.getType() == ComponentType.BIZ && tag == ComponentTag.ADDRESS) {
                AddressComponent addressComponent = (AddressComponent) component;
                String selectedId = addressComponent.getSelectedId();
                if (!TextUtils.isEmpty(selectedId)) {
                    AddressOption option = addressComponent.getOptionById(selectedId);
                    userinfo = option.getFullName() + "   " + option.getMobile();
                    areainfo = option.getAddressDetail();
                    deliveryId = option.getId();
                }
            }

            if (component.getType() == ComponentType.BIZ && tag == ComponentTag.ITEM) {
                ItemComponent itemComponent = (ItemComponent) component;
                reason = itemComponent.getReason();
            }

            if (component.getType() == ComponentType.BIZ && tag == ComponentTag.ITEM_INFO) {
                ItemInfoComponent itemInfoComponent = (ItemInfoComponent) component;
                if (titleinfo == null)
                    titleinfo = itemInfoComponent.getTitle();
                if (imgUrl == null)
                    imgUrl = itemInfoComponent.getPic();
                if (imgUrl.startsWith("/"))
                    imgUrl = "http:" + imgUrl;
                skuinfo = itemInfoComponent.getSkuInfo();

            }

            if (component.getType() == ComponentType.BIZ && tag == ComponentTag.ITEM_PAY) {
                ItemPayComponent itemPayComponent = (ItemPayComponent) component;
                if (price == null)
                    price = itemPayComponent.getAfterPromotionPrice();
            }

            if (component.getType() == ComponentType.BIZ && tag == ComponentTag.DELIVERY_METHOD) {
                DeliveryMethodComponent deliveryMethodComponent = (DeliveryMethodComponent) component;
                String selectedId = deliveryMethodComponent.getSelectedId();
                if (!TextUtils.isEmpty(selectedId)) {
                    postage = deliveryMethodComponent.getOptionById(selectedId).getPrice();
                }
            }
        }

        LogPrint.e(TAG, "resolverDataFromMTop----itemId : " + itemId + " ,postage : " + postage + " ,price : " + price);
        //添加内容
        mOrderView.addData(userinfo, areainfo, titleinfo, imgUrl, skuinfo, price, postage);
        if (!TextUtils.isEmpty(reason)) {
            return false;
        }

        return true;
    }

    /**
     * 获取统计最简的Properties
     *
     * @return
     */
    private Map<String, String> getProperties() {
        return getProperties(null);
    }

    private Map<String, String> getProperties(String asr) {
        Map<String, String> p = new HashMap<>();
        String uuid = CloudUUIDWrapper.getCloudUUID();
        if (!TextUtils.isEmpty(uuid)) {
            p.put("uuid", uuid);
        }

        p.put("channel", Config.getChannelName());

        if (!TextUtils.isEmpty(asr)) {
            p.put("asr", asr);
        }
        return p;
    }

    @Override
    public String getPageName() {
        return "Voice_order_" + Config.getChannelName();
    }

    /**
     * 自定义淘客详情页打点
     */
    private void anaylisysTaoke(String itemId) {
        //淘客登录打点

        long historyTime = SharedPreferencesUtils.getTaoKeLogin(CreateOrderActivity.this);
        long currentTime = System.currentTimeMillis();

        if (currentTime >= historyTime) {
            BusinessRequest.getBusinessRequest().requestTaokeLoginAnalysis(User.getNick(), new TaokeBussinessRequestListener());
        }

        String stbId = DeviceUtil.initMacAddress(CoreApplication.getApplication());
        BusinessRequest.getBusinessRequest().requestTaokeDetailAnalysis(stbId, User.getNick(), itemId, null, null, null);
    }

    /**
     * 淘客打点监听
     */
    private class TaokeBussinessRequestListener implements RequestListener<org.json.JSONObject> {

        @Override
        public void onRequestDone(org.json.JSONObject data, int resultCode, String msg) {
            if (resultCode == 200) {
                long historyTime = System.currentTimeMillis() + 604800000;//7天
                SharedPreferencesUtils.saveTvBuyTaoKe(CreateOrderActivity.this, historyTime);
            }
        }
    }

    /**
     * 打开新手引导
     */
    private void openNoviceGuide(String itemId, String skuId, int quantity) {
        boolean isShowNoviceGuide = SharePreferences.getBoolean("isShowNoviceGuide", true);
        //是否需要展示新手引导
        if (isShowNoviceGuide) {
            Intent intent = new Intent();
            intent.setClass(this, NoviceGuideActivity.class);
            intent.putExtra("tobuy", true);
            startActivityForResult(intent, RESULT_CODE_NOVICE_GUIDE);
        } else {
            requestRebateTag();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogPrint.e(TAG, "onActivityResult requestCode : " + requestCode + " ,resultCode : " + resultCode);
        if (resultCode == 9990 && requestCode == RESULT_CODE_NOVICE_GUIDE) {
            requestRebateTag();
        } else {
            finish();
        }
    }


    /**
     * 请求返利
     * */
    private void requestRebateTag(){
        List<String> pathList = ActivityPathRecorder.getInstance().getCurrentPath(this);
        requestProductTag(itemId,pathList,false,"",false,price,this);
    }


    /**
     * 通过进入详情页路径，获取商品标签
     *
     * @param itemId
     * @param list
     */
    public void requestProductTag(String itemId, List<String> list, boolean isZTC, String source, boolean isPre, String amount,Context context) {

        try{
            org.json.JSONObject object = new org.json.JSONObject();
            object.put("umToken", Config.getUmtoken(context));
            object.put("wua", Config.getWua(context));
            object.put("isSimulator", Config.isSimulator(context));
            object.put("userAgent", Config.getAndroidSystem(context));
            String extParams = object.toString();
            BusinessRequest.getBusinessRequest().baseRequest(new GetProductTagRequest(itemId, list, isZTC, source, isPre, amount,extParams), new GetProductTagListener(), false, true);

        }catch (JSONException e){
            e.printStackTrace();
        }
         }

    /**
     * 打标活动标签请求监听
     */
    private  class GetProductTagListener implements RequestListener<ProductTagBo> {
        @Override
        public void onRequestDone(ProductTagBo data, int resultCode, String msg) {
            AppDebug.e("打标数据请求成功", "ProductTagBo data" + data);
            if(data == null){
                return;
            }
            mProductTagBo = data;
            int rebateMoney = Integer.parseInt(data.getCoupon());
            if (rebateMoney > 0) {
                DecimalFormat df = new DecimalFormat("0.00");//格式化小数
                float num = (float) rebateMoney / 100;
                String result = df.format(num);//返回的是String类型
                mOrderView.setRebate(result);
                buildOrder(itemId, skuId, quantity);
            }

        }
    }



    private String buildOrderExParams(){
        String extParams = null;
        if (mProductTagBo != null) {
            try {
                org.json.JSONObject object = null;
                object = new org.json.JSONObject();
                JSONArray array = new JSONArray();
                org.json.JSONObject obj = new org.json.JSONObject();
                obj.put("itemId", mProductTagBo.getItemId());
                obj.put("lastTraceKeyword", mProductTagBo.getLastTraceKeyword());
                obj.put("isZTC", false);
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

                if(mProductTagBo.getTagId() !=null){
                    obj.put("tagId",mProductTagBo.getTagId());
                }

                array.put(obj);
                org.json.JSONObject subOrders = new org.json.JSONObject();
                subOrders.put("subOrders", array);
                object.put("tvtaoExtra",buildTvtaoExParams());
                object.put("TvTaoEx", subOrders);
                extParams = object.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return extParams;
    }

    /**
     * 返利相关参数
     * */
    private String buildTvtaoExParams(){
        org.json.JSONObject jsonObject = new org.json.JSONObject();
        try {
            if(mProductTagBo != null){
                jsonObject.put("outPreferentialId",mProductTagBo.getOutPreferentialId());
                jsonObject.put("tagId",mProductTagBo.getTagId());
            }
            jsonObject.put("tvOptions",TvOptionsConfig.getTvOptions());
            jsonObject.put("appKey", Config.getChannel());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
