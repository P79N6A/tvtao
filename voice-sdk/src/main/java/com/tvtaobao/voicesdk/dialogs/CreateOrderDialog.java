package com.tvtaobao.voicesdk.dialogs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ali.auth.third.core.model.Session;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.WriterException;
import com.taobao.wireless.trade.mbuy.sdk.co.Component;
import com.taobao.wireless.trade.mbuy.sdk.co.ComponentTag;
import com.taobao.wireless.trade.mbuy.sdk.co.ComponentType;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.AddressComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.AddressOption;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.DeliveryMethodComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.ItemComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.ItemInfoComponent;
import com.taobao.wireless.trade.mbuy.sdk.engine.BuyEngine;
import com.tvtaobao.voicesdk.ASRNotify;
import com.tvtaobao.voicesdk.R;
import com.tvtaobao.voicesdk.bo.DomainResultVo;
import com.tvtaobao.voicesdk.bo.PageReturn;
import com.tvtaobao.voicesdk.dialogs.base.BaseDialog;
import com.tvtaobao.voicesdk.register.type.ActionType;
import com.tvtaobao.voicesdk.request.CancelOrderRequest;
import com.tvtaobao.voicesdk.request.CreateOrderWithDelayRequest;
import com.tvtaobao.voicesdk.request.QueryOrderRequest;
import com.tvtaobao.voicesdk.utils.DialogManager;
import com.tvtaobao.voicesdk.utils.LogPrint;
import com.tvtaobao.voicesdk.utils.QRCodeUtil;
import com.tvtaobao.voicesdk.view.AutoTextView;
import com.tvtaobao.voicesdk.view.RoundImageView;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tv.core.pay.LoginHelperImpl;
import com.yunos.tv.core.util.ActivityPathRecorder;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tv.core.util.SharedPreferencesUtils;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.BuildOrderRequestBo;
import com.yunos.tvtaobao.biz.request.bo.ProductTagBo;
import com.yunos.tvtaobao.biz.request.bo.TBDetailResultV6;
import com.yunos.tvtaobao.biz.request.item.GetProductTagRequest;
import com.yunos.tvtaobao.payment.alipay.AlipayAuthManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pan on 2017/11/1.
 */

public class CreateOrderDialog extends BaseDialog {
    private String TAG = "CreateOrderDialog";

    private LinearLayout mUserLayout, orderLayout, errorPromptLayout, mReplyLayout;
    private TextView mTitle, mPrice, mPostage, mTotalPrice, mUserInfo, mLoginAuthPrompt, mRebateTxt;
    private RoundImageView mImage;
    private AutoTextView mReply;
    private RelativeLayout mQRCodeLayout;
    private ImageView mQRCode, mRebateImgTag;

    private List<Component> mComponents;
    private OrderHandler mHandler;
    private QRLoginListener qrLoginListener;
    private QRAuthListener qrAuthListener;
    private String promptText = null;

    private String itemId;
    private String price;
    private String skuId;
    private String outOrderId;
    private int quantity = 1;
    private String rebate;

    private ImageLoaderManager imageManager;

    private Context mContext;

    private List<String> tips = new ArrayList<>();

    private boolean needGoNoBuy = false;    //判断是否需要邮费，需要游戏，进行提醒
    private ProductTagBo mProductTagBo;

    public CreateOrderDialog(Context context) {
        super(context);
        this.mContext = context;

        this.mHandler = new OrderHandler();
        this.imageManager = ImageLoaderManager.getImageLoaderManager(context);
        this.setContentView(R.layout.dialog_create_order);

        initView();


    }

    private void initView() {
        orderLayout = (LinearLayout) findViewById(R.id.voice_card_order_layout);
        errorPromptLayout = (LinearLayout) findViewById(R.id.voice_card_error_prompt);
        mReplyLayout = (LinearLayout) findViewById(R.id.voice_card_order_reply_layout);
        mReply = (AutoTextView) findViewById(R.id.voice_card_order_reply);
        mImage = (RoundImageView) findViewById(R.id.voice_card_order_image);
        mImage.setRound(true, true, false, false);
        mQRCode = (ImageView) findViewById(R.id.voice_card_qrcode);
        mTitle = (TextView) findViewById(R.id.voice_card_order_title);
        mPrice = (TextView) findViewById(R.id.voice_card_order_price);
        mPostage = (TextView) findViewById(R.id.voice_card_order_postage);
        mTotalPrice = (TextView) findViewById(R.id.voice_card_order_total_price);
        mUserInfo = (TextView) findViewById(R.id.voice_card_order_address);
        mUserLayout = (LinearLayout) findViewById(R.id.voice_card_order_address_layout);
        mQRCodeLayout = (RelativeLayout) findViewById(R.id.voice_card_qrcode_layout);
        mLoginAuthPrompt = (TextView) findViewById(R.id.voice_card_qrcode_prompt);
        mRebateImgTag = (ImageView) findViewById(R.id.img_rebate_tag);
        mRebateTxt = (TextView) findViewById(R.id.txt_rebate);
    }

    @Override
    public void show() {
        super.show();
        DialogManager.getManager().pushDialog(this);
        checkLoginAuth();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        AlipayAuthManager.dispose();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    /**
     * 检查登陆授权
     * 如果没有登陆，需要进行支付宝登陆。
     * 如果没有授权，需要进行支付宝授权。
     */
    public void checkLoginAuth() {
        if (!LoginHelperImpl.getJuLoginHelper().isLogin()) {
            if (qrLoginListener == null) {
                qrLoginListener = new QRLoginListener();
            }
            AlipayAuthManager.doAuthLogin(qrLoginListener);
        } else {
            AlipayAuthManager.authCheck(new AlipayAuthManager.AuthCheckListener() {
                @Override
                public void onAuthCheckResult(final boolean isAuth, String alipayId) {
                    Message message = new Message();
                    message.what = 1;
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isAuth", isAuth);
                    bundle.putString("alipayId", alipayId);
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
            });
        }
    }

    @Override
    public PageReturn onASRNotify(DomainResultVo object) {
        PageReturn pageReturn = super.onASRNotify(object);
        switch (object.getIntent()) {
            case ActionType.CANCEL_ORDER:
                if (!TextUtils.isEmpty(outOrderId)) {
                    cancelOrder(outOrderId);

                    pageReturn.isHandler = true;
                    pageReturn.feedback = "正在为您取消订单";
                }
                break;
            case ActionType.CONTINUE_TO_BUY:
                if (needGoNoBuy) {
                    needGoNoBuy = false;
                    if (mProductTagBo != null) {
                        gotoCreateOrderWithDelay(mProductTagBo.getOutPreferentialId(), mProductTagBo.getTagId(),
                                TvOptionsConfig.getTvOptions(), itemId, skuId, deliveryId, 1);
                    }
                    Map<String, String> map = getProperties();
                    map.put("itemId", itemId);
                    Utils.utCustomHit("VoiceCard_order_postage_continue", map);

                    pageReturn.isHandler = true;
                    pageReturn.feedback = "正在为您取消订单";
                }
                break;
        }

        return pageReturn;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setRebate(String rebate) {
        this.rebate = rebate;
        if (mRebateImgTag != null && mRebateTxt != null) {
            if (rebate != null && rebate != "") {
                mRebateImgTag.setVisibility(View.VISIBLE);
                mRebateTxt.setVisibility(View.VISIBLE);
                mRebateTxt.setText("预估" + " ¥ " + rebate);
            } else {
                mRebateImgTag.setVisibility(View.GONE);
                mRebateTxt.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 去获取用户默认地址
     *
     * @param itemId
     * @param skuId
     */
    private void buildOrder(String itemId, String skuId, int quantity) {
        mReplyLayout.setVisibility(View.VISIBLE);
        BuildOrderRequestBo buildOrderRequestBo = new BuildOrderRequestBo();
        LogPrint.w(TAG, "createOrder    mSkuId = " + skuId + ", mItemId = " + itemId + ", mBuyCount = " + quantity);
        buildOrderRequestBo.setBuyNow(true);
        buildOrderRequestBo.setSkuId(skuId);
        buildOrderRequestBo.setItemId(itemId);
        buildOrderRequestBo.setQuantity(quantity);
        buildOrderRequestBo.setFrom("item");
        buildOrderRequestBo.setExtParams(buildOrderExParams());
        BusinessRequest.getBusinessRequest().buildOrder(buildOrderRequestBo, false, new BuildOrderListener());
    }

    /**
     * 去进行延迟下单请求。目前在地址请求完成之后去请求
     *
     * @param itemId
     * @param skuId
     * @param deliverId
     */
    private void gotoCreateOrderWithDelay(String outPreferentialId, String tagId, String tvOptions, String itemId, String skuId, String deliverId, int quantity) {
        BusinessRequest.getBusinessRequest().baseRequest(new CreateOrderWithDelayRequest(outPreferentialId, tagId, tvOptions, itemId, skuId, deliverId, quantity), new CreateOrderWithDelay(), true);
    }

    /**
     * 查询下单状态
     *
     * @param outOrderId
     */
    private void queryDelayOrderInfo(String outOrderId) {
        BusinessRequest.getBusinessRequest().baseRequest(new QueryOrderRequest(outOrderId), new QueryOrderListener(), true);
    }

    /**
     * 取消队列中的将下单的队列
     *
     * @param outOrderId
     */
    private void cancelOrder(String outOrderId) {
        BusinessRequest.getBusinessRequest().baseRequest(new CancelOrderRequest(outOrderId), new CancelOrderListener(), true);
    }

    /**
     * 通过进入详情页路径，获取商品标签
     *
     * @param itemId
     * @param list
     */
    public void requestProductTag(String itemId, List<String> list, boolean isZTC, String source, boolean isPre, String amount,Context context){
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

    private class OrderHandler extends Handler {
        private String qrUrl;
        private Bitmap bitmap;
        private String alipayId;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    queryDelayOrderInfo(outOrderId);
                    break;
                case 1:
                    boolean isAuth = msg.getData().getBoolean("isAuth");
                    if (isAuth) {
                        anaylisysTaoke(itemId);
                        BusinessRequest.getBusinessRequest().requestGetItemDetailV6New(itemId, null, new DetailListener());
                    } else {
                        //TODO
                        alipayId = msg.getData().getString("alipayId");
                        if (qrAuthListener == null) {
                            qrAuthListener = new QRAuthListener();
                        }
                        AlipayAuthManager.doAuth(alipayId, qrAuthListener);
                    }
                    break;
                case 2:
                    mQRCodeLayout.setVisibility(View.VISIBLE);
                    qrUrl = msg.getData().getString("qrUrl");
                    bitmap = getQRCode(qrUrl);
                    mQRCode.setImageBitmap(bitmap);
                    setReply(spanAlipay("亲，请打开【支付宝】扫码登录"));
                    break;
                case 3:
                    if (qrLoginListener == null) {
                        qrLoginListener = new QRLoginListener();
                    }
                    AlipayAuthManager.doAuthLogin(qrLoginListener);
                    break;
                case 4:
                    mQRCodeLayout.setVisibility(View.VISIBLE);
                    qrUrl = msg.getData().getString("qrUrl");
                    bitmap = getQRCode(qrUrl);
                    mQRCode.setImageBitmap(bitmap);
                    setReply(spanAlipay("亲，请打开【支付宝】扫码付款"));
                    break;
                case 5:
                    if (qrAuthListener == null) {
                        qrAuthListener = new QRAuthListener();
                    }
                    AlipayAuthManager.doAuth(alipayId, qrAuthListener);
                    break;
            }
        }
    }

    private void setReply(CharSequence statusMsg) {
        mLoginAuthPrompt.setText(statusMsg);
        playTTS(statusMsg.toString());

        Utils.utCustomHit("VoiceCard_expore_zhifubao", getProperties());
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
                    intent.setClassName(mContext, "com.yunos.tvtaobao.tradelink.activity.SkuActivity");
                    intent.putExtra("mTBDetailResultVO", data);
                    mContext.startActivity(intent);
                    dismiss();
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
     * 获取订单信息
     */
    class BuildOrderListener implements RequestListener<String> {

        @Override
        public void onRequestDone(String data, int resultCode, String msg) {
            LogPrint.d(TAG, TAG + ".BuildOrderListener code : " + resultCode + " ,msg : " + msg);
            if (resultCode == 200) {
                if (resolverDataFromMTop(data)) {
                    if (Double.parseDouble(postage) > 0) {
                        needGoNoBuy = true;

                        setPrompt("该商品需要" + postage + "元邮费，请问是否继续购买?");
                    } else {
                        if (mProductTagBo != null) {
                            gotoCreateOrderWithDelay(mProductTagBo.getOutPreferentialId(), mProductTagBo.getTagId(), TvOptionsConfig.getTvOptions(), itemId, skuId, deliveryId, quantity);
                        }
                    }
                } else {
                    errorPromptLayout.setVisibility(View.VISIBLE);
                    setPrompt(reason);

                    Map<String, String> map = getProperties();
                    map.put("errormessage", reason);
                    map.put("itemId", itemId);
                    Utils.utCustomHit("VoiceCard_order_beforeorder", map);
                }
            } else {
                if (resultCode == 102) {
                    CoreApplication.getLoginHelper(mContext).startYunosAccountActivity(mContext, false);
                    dismiss();
                    return;
                }

                if (resultCode == 334) {
                    //TODO 没有收获地址
                    msg = "亲，您还没有填写收货地址。请先去手机淘宝上填写收货地址";
                }

                if (resultCode == 106) {
                    msg = "亲，您下单过于频繁，请稍后再试";
                }

                errorPromptLayout.setVisibility(View.VISIBLE);
                tips.clear();
                tips.add(msg);
                setPrompt(tips);

                Map<String, String> map = getProperties();
                map.put("errormessage", msg);
                map.put("itemId", itemId);
                Utils.utCustomHit("VoiceCard_order_beforeorder", map);
            }
        }
    }

    /**
     * 延迟下单监听
     */
    class CreateOrderWithDelay implements RequestListener<Object> {

        @Override
        public void onRequestDone(Object data, int resultCode, String msg) {
            LogPrint.d(TAG, TAG + ".CreateOrderWithDelay code : " + resultCode + " ,msg : " + msg);
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

                        tips.clear();
                        tips.add("正在为您安排下单，您还想买什么？");
                        tips.add("想再考虑一下？" + delayTime + "秒内可以说“取消订单”");
                        setPrompt(tips);
                    } else {
                        //TODO
                        tips.clear();
                        tips.add("该商品暂不支持电视上下单，您可以打开手机淘宝购买");
                        setPrompt(tips);
                    }
                }
            } else {
                //TODO
//                mView.createOrderPrompt("下单失败，" + msg);
                tips.clear();
                tips.add(msg);
                setPrompt(tips);
            }
        }
    }

    class QueryOrderListener implements RequestListener<String> {

        @Override
        public void onRequestDone(String data, int resultCode, String msg) {
            LogPrint.d(TAG, TAG + ".QueryOrderListener code : " + resultCode + " ,msg : " + msg);
            if (resultCode == 200) {
                LogPrint.d(TAG, TAG + ".QueryOrderListener data : " + data);
                if (data.equals("2")) {
                    dismiss();

                    Map<String, String> map = getProperties();
                    map.put("feedback", "success");
                    Utils.utCustomHit("VoiceCard_order_feedback", map);
                } else if (data.equals("1")) {
                    tips.clear();
                    tips.add("支付遇到了些小问题，请打开手机淘宝，进入未付款订单完成支付");
                    setPrompt(tips);

                    Map<String, String> map = getProperties();
                    map.put("feedback", "failure");
                    Utils.utCustomHit("VoiceCard_order_feedback", map);
                } else if (data.equals("query")) {
                    mHandler.sendEmptyMessageDelayed(0, 2 * 1000);
                } else {
                    tips.clear();
                    tips.add(data);
                    setPrompt(tips);

                    Map<String, String> map = getProperties();
                    map.put("feedback", "failure");
                    Utils.utCustomHit("VoiceCard_order_feedback", map);
                }

            } else {
                mHandler.sendEmptyMessageDelayed(0, 2 * 1000);
            }
        }
    }

    class CancelOrderListener implements RequestListener<String> {

        @Override
        public void onRequestDone(String data, int resultCode, String msg) {
            LogPrint.d(TAG, TAG + ".CancelOrderListener resultCode : " + resultCode + " ,msg : " + msg);
            mHandler.removeMessages(0);
            LogPrint.d(TAG, TAG + ".CancelOrderListener data : " + data);
            if (data.equals("true")) {
                outOrderId = null;

                tips.clear();
                tips.add("成功取消订单，您还想买什么？");
                setPrompt(tips);

                Utils.utCustomHit("VoiceCard_order_cancel_success", getProperties());
            } else {
                tips.clear();
                tips.add("取消下单失败，已为您下单！");
                setPrompt(tips);

                Utils.utCustomHit("VoiceCard_order_cancel_error", getProperties());
            }
        }
    }

    /**
     * 接收外部传进来的数据，并请求预订单数据
     */
    String userInfo;
    String areaInfo;
    String titleInfo;
    String imgUrl;
    String postage = "0.00";
    String deliveryId;
    String reason;

    private boolean resolverDataFromMTop(String obj) {
        if (TextUtils.isEmpty(obj)) {
            return false;
        }

        BuyEngine mBuyEngine = new BuyEngine();
        JSONObject jobj = JSON.parseObject(obj);
        mComponents = mBuyEngine.parse(jobj);
        LogPrint.e(TAG, "CheckOutOrderReason----size : " + mComponents.size());
        for (int i = 0; i < mComponents.size(); i++) {

            Component component = mComponents.get(i);
            ComponentTag tag = ComponentTag.getComponentTagByDesc(component.getTag());
            if (component.getType() == ComponentType.BIZ && tag == ComponentTag.ADDRESS) {
                AddressComponent addressComponent = (AddressComponent) component;
                String selectedId = addressComponent.getSelectedId();
                if (!TextUtils.isEmpty(selectedId)) {
                    AddressOption option = addressComponent.getOptionById(selectedId);
                    userInfo = option.getFullName() + "   " + option.getMobile();
                    areaInfo = option.getAddressDetail();
                    deliveryId = option.getId();
                }
            }

            if (component.getType() == ComponentType.BIZ && tag == ComponentTag.ITEM) {
                ItemComponent itemComponent = (ItemComponent) component;
                reason = itemComponent.getReason();
            }

            if (component.getType() == ComponentType.BIZ && tag == ComponentTag.ITEM_INFO) {
                ItemInfoComponent itemInfoComponent = (ItemInfoComponent) component;
                if (titleInfo == null)
                    titleInfo = itemInfoComponent.getTitle();
                if (imgUrl == null)
                    imgUrl = itemInfoComponent.getPic();
                if (imgUrl.startsWith("/")) {
                    imgUrl = "http:" + imgUrl;
                }
                if (price == null)
                    price = itemInfoComponent.getPrice();
            }

//            if (component.getType() == ComponentType.BIZ && tag == ComponentTag.ORDER_PAY) {
//                OrderPayComponent orderPayComponent = (OrderPayComponent) component;
//                price = orderPayComponent.getPrice();
//            }

            if (component.getType() == ComponentType.BIZ && tag == ComponentTag.DELIVERY_METHOD) {
                DeliveryMethodComponent deliveryMethodComponent = (DeliveryMethodComponent) component;
                String selectedId = deliveryMethodComponent.getSelectedId();
                if (!TextUtils.isEmpty(selectedId)) {
                    postage = deliveryMethodComponent.getOptionById(selectedId).getPrice();
                }
            }
        }

        LogPrint.e(TAG, "resolverDataFromMTop----postage : " + postage + " ,price : " + price);
        //添加内容
        addData();

        if (!TextUtils.isEmpty(reason)) {
            tips.clear();
            tips.add(reason);
            setPrompt(tips);
            return false;
        }

        return true;
    }

    private void addData() {
        if (TextUtils.isEmpty(userInfo) && TextUtils.isEmpty(areaInfo)) {
            mUserLayout.setVisibility(View.GONE);
        } else {
            mUserInfo.setText(userInfo + "\n" + areaInfo);
        }

        imageManager.displayImage(imgUrl, mImage);
        mTitle.setText(titleInfo);

        DecimalFormat format = new DecimalFormat("0.##");
        if (!TextUtils.isEmpty(price)) {
            mPrice.setText("单价：¥ " + format.format(Double.parseDouble(price)));
        } else {
            mPrice.setText("单价：¥ 0.00");
        }

        mPostage.setText("邮费：¥ " + format.format(Double.parseDouble(postage)));

        if (!TextUtils.isEmpty(price)) {
            mTotalPrice.setText("¥ " + format.format(Double.parseDouble(price) + Double.parseDouble(postage)));
        } else {
            mTotalPrice.setText("¥ 0.00");
        }

        orderLayout.setVisibility(View.VISIBLE);
    }

    public void setPrompt(String reply) {
        tips.clear();
        tips.add(reply);
        mReply.autoScroll(tips);

        playTTS(reply);
    }

    public void setPrompt(List<String> reply) {
        if (reply != null && reply.size() > 0) {
            mReply.autoScroll(reply);

            ASRNotify.getInstance().playTTS(reply.get(0));
        }
    }

    private class QRLoginListener implements AlipayAuthManager.AuthLoginListener {
        @Override
        public void onAuthQrGenerated(final String qrUrl) {
            LogPrint.i(TAG, TAG + ".QRLoginListener onQrCodeUrlGenerated");
            Message message = new Message();
            message.what = 2;
            Bundle bundle = new Bundle();
            bundle.putString("qrUrl", qrUrl);
            message.setData(bundle);
            mHandler.sendMessage(message);
        }

        @Override
        public void onAuthSuccess() {
            LogPrint.i(TAG, TAG + ".QRLoginListener onAuthSuccess");
        }

        @Override
        public void onAuthFailure() {
            LogPrint.i(TAG, TAG + ".QRLoginListener onAuthFailure");
            mHandler.sendEmptyMessage(3);
        }

        @Override
        public void onAuthLoginResult(boolean success, Session session) {
            LogPrint.i(TAG, TAG + ".QRLoginListener onAuthLoginResult success : " + success);
            if (success) {
                anaylisysTaoke(itemId);

                mQRCodeLayout.setVisibility(View.GONE);
                BusinessRequest.getBusinessRequest().requestGetItemDetailV6New(itemId, null, new DetailListener());
            }
        }
    }

    private class QRAuthListener implements AlipayAuthManager.AuthListener {

        @Override
        public void onAuthQrGenerated(final String qrUrl) {
            Message message = new Message();
            message.what = 4;
            Bundle bundle = new Bundle();
            bundle.putString("qrUrl", qrUrl);
            message.setData(bundle);
            mHandler.sendMessage(message);
        }

        @Override
        public void onAuthSuccess() {
            LogPrint.i(TAG, TAG + ".QRAuthListener onAuthSuccess");
            anaylisysTaoke(itemId);

            mQRCodeLayout.setVisibility(View.GONE);
            BusinessRequest.getBusinessRequest().requestGetItemDetailV6New(itemId, null, new DetailListener());
        }

        @Override
        public void onAuthFailure() {
            LogPrint.i(TAG, TAG + ".QRAuthListener onAuthFailure");
            mHandler.sendEmptyMessage(5);
        }
    }

    private Bitmap getQRCode(String url) {
        LogPrint.i(TAG, "getQRCode url : " + url);
        Bitmap qrBitmap = null;
        try {
//            Drawable drawable = mContext.getResources().getDrawable(R.drawable.icon_zhifubao);
//            Bitmap icon = null;
//            if (drawable != null) {
//                BitmapDrawable bd = (BitmapDrawable) drawable;
//                icon = bd.getBitmap();
//            }
            Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_zhifubao);
            ViewGroup.LayoutParams para = mQRCode.getLayoutParams();
            LogPrint.e("SSD", "layout width : " + para.width + "height : " + para.height);

            qrBitmap = QRCodeUtil.create2DCode(url, para.width, para.height, icon);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return qrBitmap;
    }

    private SpannableStringBuilder spanAlipay(String src) {
        SpannableStringBuilder style = new SpannableStringBuilder(src);
        int foregroundColor = 0xff00a0e9;
        Pattern p = Pattern.compile("【支付宝】");
        Matcher m = p.matcher(src);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            style.setSpan(new ForegroundColorSpan(foregroundColor), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        return style;
    }

    /**
     * 淘客打点监听
     */
    private class TaokeBussinessRequestListener implements RequestListener<org.json.JSONObject> {

        @Override
        public void onRequestDone(org.json.JSONObject data, int resultCode, String msg) {
            if (resultCode == 200) {
                long historyTime = System.currentTimeMillis() + 604800000;//7天
                SharedPreferencesUtils.saveTvBuyTaoKe(mContext, historyTime);
            }
        }
    }

    /**
     * 自定义淘客详情页打点
     */
    private void anaylisysTaoke(String itemId) {
        //淘客登录打点

        long historyTime = SharedPreferencesUtils.getTaoKeLogin(mContext);
        long currentTime = System.currentTimeMillis();

        if (currentTime >= historyTime) {
            BusinessRequest.getBusinessRequest().requestTaokeLoginAnalysis(User.getNick(), new TaokeBussinessRequestListener());
        }

        String stbId = DeviceUtil.initMacAddress(CoreApplication.getApplication());
        BusinessRequest.getBusinessRequest().requestTaokeDetailAnalysis(stbId, User.getNick(), itemId, null, null, null);
    }

    /**
     * 打开新手引导
     */
    private void openNoviceGuide(String itemId, String skuId, int quantity) {
        boolean isShowNoviceGuide = SharePreferences.getBoolean("isShowNoviceGuide", true);
        //是否需要展示新手引导
        if (isShowNoviceGuide) {
            dismiss();

            NoviceGuideDialog noviceGuideDialog = new NoviceGuideDialog(mContext);
            noviceGuideDialog.setData(itemId, rebate, price);
            noviceGuideDialog.show();
        } else {
            requestRebateTag();
        }
    }


    /**
     * 请求返利
     */
    private void requestRebateTag() {
        List<String> pathList;
        if (mContext instanceof ActivityPathRecorder.PathNode) {
            pathList = ActivityPathRecorder.getInstance().getCurrentPath((ActivityPathRecorder.PathNode) mContext);
        } else {
            pathList = new ArrayList<>();
        }
        requestProductTag(itemId,pathList,false,"",false,price,mContext);
    }


    /**
     * 打标活动标签请求监听
     */
    private class GetProductTagListener implements RequestListener<ProductTagBo> {
        @Override
        public void onRequestDone(ProductTagBo data, int resultCode, String msg) {
            AppDebug.e("打标数据请求成功", "ProductTagBo data" + data);
            if (data == null) {
                return;
            }
            mProductTagBo = data;
            int rebateMoney = Integer.parseInt(data.getCoupon());
            if (rebateMoney > 0) {
                DecimalFormat df = new DecimalFormat("0.00");//格式化小数
                float num = (float) rebateMoney / 100;
                String result = df.format(num);//返回的是String类型
                setRebate(result);
                buildOrder(itemId, skuId, quantity);
            }

        }
    }


    private String buildOrderExParams() {
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

                if (mProductTagBo.getTagId() != null) {
                    obj.put("tagId", mProductTagBo.getTagId());
                }

                array.put(obj);
                org.json.JSONObject subOrders = new org.json.JSONObject();
                subOrders.put("subOrders", array);
                object.put("tvtaoExtra", buildTvtaoExParams());
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
     */
    private String buildTvtaoExParams() {
        org.json.JSONObject jsonObject = new org.json.JSONObject();
        try {
            if (mProductTagBo != null) {
                jsonObject.put("outPreferentialId", mProductTagBo.getOutPreferentialId());
                jsonObject.put("tagId", mProductTagBo.getTagId());
            }
            jsonObject.put("tvOptions",TvOptionsConfig.getTvOptions());
            jsonObject.put("appKey", Config.getChannel());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
