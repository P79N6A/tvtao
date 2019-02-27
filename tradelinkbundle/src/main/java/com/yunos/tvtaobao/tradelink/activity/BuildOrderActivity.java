package com.yunos.tvtaobao.tradelink.activity;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.tvtaobao.voicesdk.bo.DomainResultVo;
import com.tvtaobao.voicesdk.bo.PageReturn;
import com.tvtaobao.voicesdk.register.type.ActionType;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.FocusTextView;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.TradeBaseActivity;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.request.bo.BuildOrderPreSale;
import com.yunos.tvtaobao.biz.request.bo.BuildOrderRequestBo;
import com.yunos.tvtaobao.biz.request.bo.ProductTagBo;
import com.yunos.tvtaobao.tradelink.R;
import com.yunos.tvtaobao.tradelink.buildorder.OrderBuider;
import com.yunos.tvtaobao.tradelink.buildorder.view.ViewBuilder;
import com.yunos.tvtaobao.tradelink.listener.QRCodeKeyListener;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BuildOrderActivity extends TradeBaseActivity {
    private final String TAG = "BuildOrderActivity";
    // 界面管理部分
    public ViewBuilder mViewBuilder;
    private FocusPositionManager mFocusPositionManager;
    // 第一次请求的标记
    public boolean mFirstRequestOfBuildOrder;

    // 外部传入的请求对象
    public BuildOrderRequestBo mBuildOrderRequestBo;

    //外部传入预售对象信息

    public BuildOrderPreSale mBuildOrderPreSale;
    private boolean isSeck;
    private ProductTagBo mProductTagBo;
    private TextView tvRebateMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDebug.i(TAG, "onCreate... ");
        onKeepActivityOnlyOne(BuildOrderActivity.class.getName());
        setContentView(R.layout.ytm_activity_buildorder);
//        initRebateMoney();
        mViewBuilder = new ViewBuilder(this);
        registerLoginListener();

        mFirstRequestOfBuildOrder = true;

        //判断登录状态
        if (!CoreApplication.getLoginHelper(CoreApplication.getApplication()).isLogin()) {
            setLoginActivityStartShowing();
            CoreApplication.getLoginHelper(CoreApplication.getApplication()).startYunosAccountActivity(this, false);
            return;
        }

        buildOrderRequest();
    }

    private void initRebateMoney() {
        tvRebateMoney = (TextView) findViewById(R.id.rebate_money);
        Bundle bundle = getIntent().getExtras();
        mProductTagBo = (ProductTagBo) bundle.get("mProductTagBo");
        if (mProductTagBo != null && mProductTagBo.getCouponType() != null && mProductTagBo.getCouponType().equals("1")) {
            if (!TextUtils.isEmpty(mProductTagBo.getCoupon())) {
                String stringRebateMoney = mProductTagBo.getCoupon();
                int rebateMoney = Integer.parseInt(stringRebateMoney);
                if (rebateMoney > 0) {
                    DecimalFormat df = new DecimalFormat("0.00");//格式化小数
                    float num = (float) rebateMoney / 100;
                    String result = df.format(num);//返回的是String类型
                    String detailRebateInfo = "最高再返";
                    if (!TextUtils.isEmpty(mProductTagBo.getCouponMessage())) {
                        detailRebateInfo = mProductTagBo.getCouponMessage();
                    }
                    if (detailRebateInfo.length() <= 3) {
                        tvRebateMoney.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.dp_28));
                        tvRebateMoney.setText(detailRebateInfo + result + "元");

                    } else if (detailRebateInfo.length() == 4) {
                        tvRebateMoney.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.dp_24));
                        tvRebateMoney.setText(detailRebateInfo + result + "元");

                    } else if (detailRebateInfo.length() > 4) {
                        tvRebateMoney.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.dp_20));
                        if (detailRebateInfo.length() == 8) {
                            if (stringRebateMoney.length() >= 3) {
                                tvRebateMoney.setText(detailRebateInfo + "\n" + result + "元");
                            } else {
                                tvRebateMoney.setText(detailRebateInfo + result + "元");
                            }
                        } else if (detailRebateInfo.length() == 9) {
                            tvRebateMoney.setText(detailRebateInfo + "\n" + result + "元");
                        } else if (detailRebateInfo.length() > 10) {
                            tvRebateMoney.setText(detailRebateInfo.substring(0, 10) + "\n"
                                    + detailRebateInfo.substring(10) + result + "元");
                        } else {
                            tvRebateMoney.setText(detailRebateInfo + result + "元");
                        }

                    } else {
                        tvRebateMoney.setText(detailRebateInfo + result + "元");
                    }

                    tvRebateMoney.setVisibility(View.VISIBLE);
                } else {
                    if (!TextUtils.isEmpty(mProductTagBo.getCouponMessage()) && mProductTagBo.isPre()) {
                        tvRebateMoney.setVisibility(View.VISIBLE);
                        String detailRebateInfo = mProductTagBo.getCouponMessage();
                        if (detailRebateInfo.length() <= 4) {
                            tvRebateMoney.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.dp_28));
                            tvRebateMoney.setText(detailRebateInfo);
                        } else if (detailRebateInfo.length() == 5) {
                            tvRebateMoney.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.dp_24));
                            tvRebateMoney.setText(detailRebateInfo);
                        } else if (detailRebateInfo.length() > 5 && detailRebateInfo.length() <= 11) {
                            tvRebateMoney.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.dp_20));
                            tvRebateMoney.setText(detailRebateInfo);
                        } else if (detailRebateInfo.length() > 11) {
                            tvRebateMoney.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.dp_20));
                            tvRebateMoney.setText(detailRebateInfo.substring(0, 11) + "\n"
                                    + detailRebateInfo.substring(11));
                        } else {
                            tvRebateMoney.setText(detailRebateInfo);
                        }

                    } else {
                        tvRebateMoney.setVisibility(View.GONE);
                        tvRebateMoney.setText("");
                        findViewById(R.id.buildorder_button_wrapper).setBackgroundColor(Color.parseColor("#eb7413"));
                    }
                }
            } else {
                if (!TextUtils.isEmpty(mProductTagBo.getCouponMessage()) && mProductTagBo.isPre()) {
                    tvRebateMoney.setVisibility(View.VISIBLE);
                    String detailRebateInfo = mProductTagBo.getCouponMessage();
                    if (detailRebateInfo.length() <= 4) {
                        tvRebateMoney.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.dp_28));
                        tvRebateMoney.setText(detailRebateInfo);
                    } else if (detailRebateInfo.length() == 5) {
                        tvRebateMoney.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.dp_24));
                        tvRebateMoney.setText(detailRebateInfo);
                    } else if (detailRebateInfo.length() > 5 && detailRebateInfo.length() <= 11) {
                        tvRebateMoney.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.dp_20));
                        tvRebateMoney.setText(detailRebateInfo);
                    } else if (detailRebateInfo.length() > 11) {
                        tvRebateMoney.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.dp_20));
                        tvRebateMoney.setText(detailRebateInfo.substring(0, 11) + "\n"
                                + detailRebateInfo.substring(11));
                    } else {
                        tvRebateMoney.setText(detailRebateInfo);
                    }
                } else {
                    tvRebateMoney.setVisibility(View.GONE);
                    tvRebateMoney.setText("");
                    findViewById(R.id.buildorder_button_wrapper).setBackgroundColor(Color.parseColor("#eb7413"));
                }
            }

        } else {
            tvRebateMoney.setVisibility(View.GONE);
            tvRebateMoney.setText("");
            findViewById(R.id.buildorder_button_wrapper).setBackgroundColor(Color.parseColor("#eb7413"));

        }
    }

    @Override
    protected PageReturn onVoiceAction(DomainResultVo object) {
        switch (object.getIntent()) {
            case ActionType.CREATE_ORDER:
            case ActionType.CONFIRM:
                mViewBuilder.gotoSubmitBuildOrder();
                PageReturn pageReturn = new PageReturn();
                pageReturn.isHandler = true;
                pageReturn.feedback = "正在为您下单";
                return pageReturn;
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        onRemoveKeepedActivity(BuildOrderActivity.class.getName());
        super.onDestroy();
    }

    /**
     * 初始化Focus
     */
    public void onInitBuildOrder() {
        mFocusPositionManager = (FocusPositionManager) findViewById(R.id.buildorder_mainlayout);
        mFocusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(
                R.drawable.ytbv_common_focus)));
        mFocusPositionManager.resetFocused();

        FocusTextView focusListView_buy = (FocusTextView) this.findViewById(R.id.buildorder_button);
        mFocusPositionManager.setFirstFocusChild(focusListView_buy);
        mFocusPositionManager.requestFocus(focusListView_buy, View.FOCUS_DOWN);

        mViewBuilder.onChangeSubmitButton(true);
        mViewBuilder.changeArrowResId(false);
    }

    /**
     * 显示二维码的对话框
     *
     * @param qRCodeKeyListener 二维码的返回键监听
     * @param bgTransparent     当前界面背景是否透明
     */
    public void showItemQRCode(String qrtext, final QRCodeKeyListener qRCodeKeyListener, boolean bgTransparent) {
        String fromBusiness = mBuildOrderRequestBo.getFrom();
        AppDebug.i(TAG, "showItemQRCode -->   fromBusiness1 = " + fromBusiness + "; qrtext = " + qrtext);
        if (false) {
            FocusPositionManager focusPositionManager = (FocusPositionManager) findViewById(R.id.buildorder_mainlayout);
            focusPositionManager.setBackgroundColor(Color.TRANSPARENT);
        }
        String text = getResources().getString(R.string.ytbv_qr_buy_nottv);
        if (!TextUtils.isEmpty(qrtext)) {
            text = qrtext;
        }
        if (TextUtils.equals(fromBusiness, BaseConfig.ORDER_FROM_ITEM)) {
            AppDebug.i(TAG, "showItemQRCode -->  fromBusiness2 = " + fromBusiness);
            Bitmap icon = null;
            icon = BitmapFactory.decodeResource(getResources(), R.drawable.tradelink_qr_code_icon_taobao);
            showItemQRCodeFromItemId(text, mBuildOrderRequestBo.getItemId(), icon, true,
                    new DialogInterface.OnKeyListener() {

                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                dialog.dismiss();
                                if (qRCodeKeyListener != null) {
                                    return qRCodeKeyListener.onQRCodeKey(dialog, keyCode, event);
                                }
                                return true;
                            }
                            return false;
                        }
                    }, false);
            // 设置埋点
            String controlName = Utils.getControlName(getFullPageName(), "QRCode_dialog", null);
            Map<String, String> p = Utils.getProperties();
            if (!TextUtils.isEmpty(fromBusiness)) {
                p.put("item_cart", fromBusiness);
            }
            Utils.utCustomHit(getFullPageName(), controlName, p);
        } else if (TextUtils.equals(fromBusiness, BaseConfig.ORDER_FROM_CART)) {
            AppDebug.i(TAG, "showItemQRCode -->  fromBusiness3 = " + fromBusiness);
            showItemQRCodeFromUrl(text, BaseConfig.CART_URL, null, true,
                    new DialogInterface.OnKeyListener() {

                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                dialog.dismiss();
                                if (qRCodeKeyListener != null) {
                                    return qRCodeKeyListener.onQRCodeKey(dialog, keyCode, event);
                                }
                                return true;
                            }
                            return false;
                        }
                    });
            // 设置埋点
            String controlName = Utils.getControlName(getFullPageName(), "QRCode_dialog", null);
            Map<String, String> p = Utils.getProperties();
            if (!TextUtils.isEmpty(fromBusiness)) {
                p.put("item_cart", fromBusiness);
            }
            Utils.utCustomHit(getFullPageName(), controlName, p);
        }
    }

    /**
     * 接收外部传进来的数据，并请求数据
     */
    private void buildOrderRequest() {
        mBuildOrderRequestBo = (BuildOrderRequestBo) getIntent().getExtras().get("mBuildOrderRequestBo");
        if (mBuildOrderRequestBo == null) {
            String itemId = getIntent().getExtras().getString("itemId");
            String skuId = getIntent().getExtras().getString("skuId");
            String cartId = getIntent().getExtras().getString("cartId");
            String buyParam = getIntent().getExtras().getString("buyParam");
            String from = getIntent().getExtras().getString("from");
            AppDebug.d("test", "itemID" + itemId);
            AppDebug.d("test", "skuID" + skuId);
            AppDebug.d("test", "cartId" + cartId);

            if (mBuildOrderRequestBo == null) mBuildOrderRequestBo = new BuildOrderRequestBo();
            if (!TextUtils.isEmpty(itemId)) mBuildOrderRequestBo.setItemId(itemId);
            if (!TextUtils.isEmpty(skuId)) mBuildOrderRequestBo.setSkuId(skuId);
            if (!TextUtils.isEmpty(cartId)) mBuildOrderRequestBo.setCartIds(cartId);
            if (!TextUtils.isEmpty(buyParam)) mBuildOrderRequestBo.setBuyParam(buyParam);
            mBuildOrderRequestBo.setBuyParam(buyParam);
            mBuildOrderRequestBo.setQuantity(1);
            mBuildOrderRequestBo.setBuyNow(true);
        }
        AppDebug.i(TAG, "buildOrderRequest -->  mBuildOrderRequestBo = " + mBuildOrderRequestBo);
        mBuildOrderPreSale = (BuildOrderPreSale) getIntent().getExtras().get("mBuildOrderPreSale");
        if (mBuildOrderPreSale != null) {
            AppDebug.e(TAG, "buildOrderPreSale --> mBuildOrderPreSale = " + mBuildOrderPreSale.toString());
        }

        mViewBuilder.buildOrderRequest(mBuildOrderRequestBo, mBuildOrderPreSale);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppDebug.i("LoginForResult", "login for result, requestCode:" + requestCode + ", resultCode:" + resultCode);

        // 支付成功后关闭
        if (resultCode == Activity.RESULT_OK && requestCode == BaseConfig.ACTIVITY_REQUEST_PAY_DONE_FINISH_CODE) {
            mViewBuilder.payDone(true);
            finish();
        }

        //无论登录成功与否都退出
        if (requestCode == BaseConfig.loginRequestCode || requestCode == BaseConfig.forceLoginRequestCode) {
            finish();
        }
    }

    @Override
    protected void refreshData() {
        if (mViewBuilder != null) {
            mViewBuilder.onInitAddress();
        }
        buildOrderRequest();
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> p = super.getPageProperties();
        p.putAll(Utils.getProperties());
        OrderBuider orderbuider = mViewBuilder.getOrderBuider();
        if (orderbuider != null && orderbuider.mGoodsDisplayInfo != null) {
            // 多个商品时商品id为id的组合，商品名称取店铺名称即可，因为名称会根据是否多个商品进行调整
            if (!TextUtils.isEmpty(orderbuider.mGoodsDisplayInfo.getmItemIdTbs())) {
                p.put("item_id", orderbuider.mGoodsDisplayInfo.getmItemIdTbs());
            }
            if (!TextUtils.isEmpty(orderbuider.mGoodsDisplayInfo.getShopName())) {
                p.put("name", orderbuider.mGoodsDisplayInfo.getShopName());
            }
            //TODO check
            if (!TextUtils.isEmpty(orderbuider.mGoodsDisplayInfo.getItemNames())) {
                p.put("item_name", orderbuider.mGoodsDisplayInfo.getItemNames());
            }
            p.put("is_prebuy", "" + orderbuider.prePay);
            p.put("from_in", mFROM);

        }

        return p;
    }

    /**
     * 登录取消
     */
    @Override
    protected void onLoginCancel() {
        finish();
    }

    protected String getAppTag() {
        //沿用以前淘宝SDK中的埋点
        return "Tb";
    }

    @Override
    public String getPageName() {
        //沿用以前淘宝SDK中的埋点
        return "OrderSubmit";
    }

    public void utControlHit(String controlName, Map<String, String> map) {
        Map<String, String> params = getPageProperties();
        if (map != null) {
            params.putAll(map);
        }
        com.yunos.tv.core.util.Utils.utControlHit(getFullPageName(), controlName, map);
    }

    public void leavePage() {
        exitUT();
    }

    @Override
    protected FocusPositionManager getFocusPositionManager() {
        return mFocusPositionManager;
    }

    @Override
    public boolean isUpdateBlackList() {
        return true;
    }
}
