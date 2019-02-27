package com.yunos.tvtaobao.biz.h5.plugin;


import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;

import com.google.zxing.WriterException;
import com.yunos.tv.blitz.BlitzPlugin;
import com.yunos.tv.blitz.data.BzResult;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.pay.YunOSOrderManager;
import com.yunos.tv.core.util.Utils;
import com.yunos.tv.paysdk.AliTVPayClient;
import com.yunos.tv.paysdk.AliTVPayResult;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.activity.TaoBaoBlitzActivity;
import com.yunos.tvtaobao.biz.dialog.QRDialog;
import com.yunos.tvtaobao.biz.dialog.QuitPayConfirmDialog;
import com.yunos.tvtaobao.biz.qrcode.QRCodeManager;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.businessview.R;
import com.yunos.tvtaobao.payment.alipay.AlipayPaymentManager;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TvTaoBaoPayPlugin {

    private static String TAG = "TvTaoBaoPayPlugin";

    private static String TITLE = "title";
    private static String PRICE = "price";
    private static String ALIPAYTRADENO = "trade_no";
    private static String TAOBAOORDERNO = "order_no";
    private static String ITEMID = "item_id";
    private static String PAYTYPE = "payType";
    private static String WAIMAI_TAOBAO_PAY = "waimaiTaobaoPay";
    private static String PREPAY = "prePay";
    private static String FROMCART = "fromCart";

    private static String RESULT = "result";
    private static String ERROR = "error";
    private static String KEY = "key";
    private static String VALUE = "value";


    private PayJsCallback mPayJsCallback;

    private WeakReference<TaoBaoBlitzActivity> mTaoBaoBlitzActivityReference;

    public TvTaoBaoPayPlugin(WeakReference<TaoBaoBlitzActivity> taoBaoBlitzActivity) {
        mTaoBaoBlitzActivityReference = taoBaoBlitzActivity;
        onInitPayPlugin();
    }


    private void onInitPayPlugin() {
        mPayJsCallback = new PayJsCallback(new WeakReference<TvTaoBaoPayPlugin>(this));
        BlitzPlugin.bindingJs("tvtaobao_pay", mPayJsCallback);
    }


    private boolean onHandleCallPay(String param, long cbData) {

        final String param_final = param;
        final long cbData_final = cbData;

        AppDebug.i(TAG, "onHandleCallPay --> param_final  =" + param_final + ";  cbData_final = " + cbData_final);

        TaoBaoBlitzActivity taoBaoBlitzActivity = null;
        if (mTaoBaoBlitzActivityReference != null && mTaoBaoBlitzActivityReference.get() != null) {
            taoBaoBlitzActivity = mTaoBaoBlitzActivityReference.get();
        }

        if (taoBaoBlitzActivity == null) {
            BzResult result = new BzResult();
            result.addData(RESULT, "false");
            String res = result.toJsonString();
            BlitzPlugin.responseJs(false, res, cbData_final);
            return true;
        }

        taoBaoBlitzActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                onHandleTvPay(param_final, cbData_final);
            }
        });

        return true;
    }

    private void onHandleTvPay(String param, long cbData) {
        final long cbData_addr = cbData;
        AppDebug.i(TAG, "onHandleTvPay --> param  =" + param + ";  cbData_addr = " + cbData_addr);
        JSONObject start_param;
        try {
            start_param = new JSONObject(param);
            String title = start_param.optString(TITLE);
            String price = start_param.optString(PRICE);
            String alipayTradeNo = start_param.optString(ALIPAYTRADENO);
            String taobaoOrderNo = start_param.optString(TAOBAOORDERNO);
            String item_id = start_param.optString(ITEMID);
            String payType = start_param.optString(PAYTYPE, "alipay");
            boolean waimaiTaobaoPay = start_param.optBoolean(WAIMAI_TAOBAO_PAY, false);
            boolean prepay = start_param.optBoolean(PREPAY, false);
            boolean fromCart = start_param.optBoolean(FROMCART, false);

            AppDebug.i(TAG, "onHandleTvPay --> title  =" + title + ";  price = " + price + "; alipayTradeNo = "
                    + alipayTradeNo + "; taobaoOrderNo = " + taobaoOrderNo);

            String order = "";
            if (!TextUtils.isEmpty(alipayTradeNo)) {
                if (!TextUtils.isEmpty(order)) {
                    order += "&";
                }
                order += ("orderNo=" + alipayTradeNo);
            }
            if (!TextUtils.isEmpty(title)) {
                if (!TextUtils.isEmpty(order)) {
                    order += "&";
                }
                order += ("subject=" + title);
            }

            if (!TextUtils.isEmpty(price)) {
                if (!TextUtils.isEmpty(order)) {
                    order += "&";
                }
                order += ("price=" + price);
            }

            order += "&orderType=trade";

            if (!TextUtils.isEmpty(taobaoOrderNo)) {
                if (!TextUtils.isEmpty(order)) {
                    order += "&";
                }
                order += ("taobaoOrderNo=" + taobaoOrderNo);
            }

            AppDebug.i(TAG, "onHandleTvPay --> order =  " + order);
            YunOSOrderManager orderManager = new YunOSOrderManager();
            orderManager.GenerateOrder(order);
            double priceVal = Double.valueOf(price) / 100.0f;

            if (GlobalConfig.instance != null && GlobalConfig.instance.taobaoPay) {//全局配置
                showQRDialog(taobaoOrderNo, price, cbData_addr, start_param.has(WAIMAI_TAOBAO_PAY));
            } else if (Config.isAgreementPay() || !waimaiTaobaoPay) {//新包支持协议支付或者旧包不指定taobaowaimaipay
                aliTVPay(orderManager, cbData_addr, priceVal, title, item_id, taobaoOrderNo, fromCart, prepay);
            } else {
                showQRDialog(taobaoOrderNo, price, cbData_addr, start_param.has(WAIMAI_TAOBAO_PAY));
            }
//            if (!waimaiTaobaoPay) {
//                AppDebug.i(TAG, "onHandleTvPay --> order =  " + order);
//                YunOSOrderManager orderManager = new YunOSOrderManager();
//                orderManager.GenerateOrder(order);
//                aliTVPay(orderManager, cbData_addr, title, item_id);
//            } else {
//                showQRDialog(taobaoOrderNo, price, cbData_addr);
//            }
//            doYunOSPay(order, cbData_addr, title, item_id);


        } catch (
                Exception e)

        {
            AppDebug.i(TAG, "onHandleTvPay --> e =  " + e.toString());
        }
    }

    private void showQRDialog(String orderNo, String price, final long callbackAddr, boolean isWaimai) {
        Bitmap qrBitmap = null;
        final BaseActivity activity = mTaoBaoBlitzActivityReference.get();
        int width = (int) mTaoBaoBlitzActivityReference.get().getResources().getDimensionPixelSize(R.dimen.dp_334);
        int height = width;
        try {
//            Bitmap bm = BitmapFactory.decodeResource(activity.getResources(), R.drawable.icontaobao);
            if (isWaimai)
                qrBitmap = QRCodeManager.create2DCode("http://h5.m.taobao.com/app/waimai/orderlist.html", width, height, null);
//            https://h5.m.taobao.com/app/waimai/index.html#/order
            else
                qrBitmap = QRCodeManager.create2DCode("http://h5.m.taobao.com/mlapp/olist.html?OrderListType=wait_to_pay", width, height, null);
//            qrBitmap = QRCodeManager.create2DCode("http://tm.m.taobao.com/list.htm?OrderListType=wait_to_pay", width, height, bm);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        Double realprice = Double.parseDouble(price);
        String priceLabel = null;
        if (realprice % 100 > 0) {
            priceLabel = String.format("%.2f", realprice / 100.0d);
        } else {
            priceLabel = String.format("%.0f", realprice / 100.0d);
        }

        final QRDialog dialog = new QRDialog.Builder(activity).setCancelable(true).setQrCodeBitmap(qrBitmap).setTitle(createSpannableTitle("打开【手机淘宝】扫码付款"))
                .setQRCodeText(onHandlerSpanned("订单合计" + priceLabel + "元")).create();
        ArrayList<String> ids = new ArrayList<String>();
        ids.add(orderNo);
        dialog.setBizOrderIds(ids);
        dialog.setDelegate(new QRDialog.QRDialogDelegate() {
            @Override
            public void QRDialogSuccess(QRDialog dialog, boolean success) {
                BlitzPlugin.responseJs(success, "{\"result\":\"true\",\"ret\":\"HY_SUCCESS\"}", callbackAddr);
            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                    @Override
                                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                                        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                                            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                                                Utils.utCustomHit("Expose_STJumpcode_pop_out_payment", Utils.getProperties());
                                                QuitPayConfirmDialog confirmDialog = new QuitPayConfirmDialog.Builder(activity).setTitle("退出支付").setMessage("支付未完成,是否确认退出?").setPositiveButton("确定退出", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        Utils.utControlHit("STJumpcode_pop_out_payment", "button_out", Utils.getProperties());
                                                        dialogInterface.dismiss();
                                                        dialog.dismiss();
                                                        BlitzPlugin.responseJs(true, "{\"result\":\"false\",\"ret\":\"HY_FAILED\"}", callbackAddr);
                                                    }
                                                }).setNegativeButton("继续支付", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        Utils.utControlHit("STJumpcode_pop_out_payment", "button_cancel", Utils.getProperties());
                                                        dialogInterface.dismiss();
                                                    }
                                                }).create();
                                                confirmDialog.show();
                                            }
                                            return true;
                                        }
                                        return false;
                                    }
                                }

        );
        dialog.show();
    }
//
//
//    private void doYunOSPay(String order, long cbAddr, String title, String itemId) {
//        YunOSOrderManager orderManager = new YunOSOrderManager();
//        orderManager.GenerateOrder(order);
//        aliTVPay(orderManager, cbAddr, title, itemId);
//    }

    /**
     * 创建spannableString
     */
    private SpannableStringBuilder createSpannableTitle(String src) {
        SpannableStringBuilder style = new SpannableStringBuilder(src);
        int foregroundColor = 0xffff6c0f;
        Pattern p = Pattern.compile("【手机淘宝】");
        Matcher m = p.matcher(src);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            style.setSpan(new ForegroundColorSpan(foregroundColor), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        return style;
    }

    private SpannableStringBuilder onHandlerSpanned(String src) {
        SpannableStringBuilder style = new SpannableStringBuilder(src);
        int foregroundColor = 0xffff6c0f;
        Pattern p = Pattern.compile("\\d+(.)*\\d+");
        Matcher m = p.matcher(src);
        while (m.find()) {
            int start = m.start();
            if (start > 0) {
                String jianhao = src.substring(start - 1, start);
                if ("-".equals(jianhao)) {
                    start--;
                }
            }
            int end = m.end();
            style.setSpan(new ForegroundColorSpan(foregroundColor), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        return style;
    }


    private void aliTVPay(YunOSOrderManager orderManager, final long cbData_addr, double price, final String title,
                          final String item_id, String orderId, boolean fromCart, boolean prePay) {
        String order = orderManager.getOrder();
        String sign = orderManager.getSign();

        Bundle bundle = new Bundle();
        bundle.putString("provider", "alipay");

        final BzResult result = new BzResult();

        TaoBaoBlitzActivity taoBaoBlitzActivity = null;
        if (mTaoBaoBlitzActivityReference != null && mTaoBaoBlitzActivityReference.get() != null) {
            taoBaoBlitzActivity = mTaoBaoBlitzActivityReference.get();
        }

        final TaoBaoBlitzActivity mTaoBaoBlitzActivity = taoBaoBlitzActivity;
        if (mTaoBaoBlitzActivity == null) {
            result.addData(RESULT, "false");
            String res = result.toJsonString();
            BlitzPlugin.responseJs(false, res, cbData_addr);
            return;
        }
        if (Config.isAgreementPay()) {
            mTaoBaoBlitzActivity.OnWaitProgressDialog(true);
            AlipayPaymentManager.doPay(mTaoBaoBlitzActivity, price, null, orderId, fromCart, prePay, new AlipayPaymentManager.AlipayAgreementPayListener() {
                @Override
                public void paymentSuccess(double price, String account) {
                    mTaoBaoBlitzActivity.OnWaitProgressDialog(false);
                    String controlName = Utils.getControlName(mTaoBaoBlitzActivity.getFullPageName(), "Pay", null);
                    Map<String, String> lProperties = Utils.getProperties();
                    if (!TextUtils.isEmpty(title)) {
                        lProperties.put("title", title);
                    }

                    if (!TextUtils.isEmpty(item_id)) {
                        lProperties.put("item_id", item_id);
                    }


                    lProperties.put("result", "success");
                    result.addData(RESULT, "true");
                    result.setSuccess();
                    String res = result.toJsonString();
                    BlitzPlugin.responseJs(true, res, cbData_addr);
                    Utils.utCustomHit(mTaoBaoBlitzActivity.getFullPageName(), controlName, lProperties);
                }

                @Override
                public void paymentFailure(String msg) {
                    mTaoBaoBlitzActivity.OnWaitProgressDialog(false);
                    String controlName = Utils.getControlName(mTaoBaoBlitzActivity.getFullPageName(), "Pay", null);
                    Map<String, String> lProperties = Utils.getProperties();
                    if (!TextUtils.isEmpty(title)) {
                        lProperties.put("title", title);
                    }

                    if (!TextUtils.isEmpty(item_id)) {
                        lProperties.put("item_id", item_id);
                    }


                    lProperties.put("result", "fail");
                    result.addData(RESULT, "false");
                    result.setSuccess();
                    String res = result.toJsonString();
                    BlitzPlugin.responseJs(true, res, cbData_addr);
                    Utils.utCustomHit(mTaoBaoBlitzActivity.getFullPageName(), controlName, lProperties);
                }

                @Override
                public void paymentCancel() {
                    mTaoBaoBlitzActivity.OnWaitProgressDialog(false);
                    String controlName = Utils.getControlName(mTaoBaoBlitzActivity.getFullPageName(), "Pay", null);
                    Map<String, String> lProperties = Utils.getProperties();
                    if (!TextUtils.isEmpty(title)) {
                        lProperties.put("title", title);
                    }

                    if (!TextUtils.isEmpty(item_id)) {
                        lProperties.put("item_id", item_id);
                    }


                    lProperties.put("result", "fail");
                    result.addData(RESULT, "false");
                    result.setSuccess();
                    String res = result.toJsonString();
                    BlitzPlugin.responseJs(true, res, cbData_addr);
                    Utils.utCustomHit(mTaoBaoBlitzActivity.getFullPageName(), controlName, lProperties);
                }
            });
        } else {
            AliTVPayClient payer = new AliTVPayClient();
            try {
                payer.aliTVPay(mTaoBaoBlitzActivity, order, sign, bundle, new AliTVPayClient.IPayCallback() {

                    @Override
                    public void onPayProcessEnd(AliTVPayResult arg0) {

                        AppDebug.i(TAG, TAG + ".aliTVPay.onPayProcessEnd.arg0 = " + arg0);
                        if (arg0 == null) {
                            result.addData(RESULT, "false");
                            String res = result.toJsonString();
                            BlitzPlugin.responseJs(false, res, cbData_addr);
                            return;
                        }
                        String controlName = Utils.getControlName(mTaoBaoBlitzActivity.getFullPageName(), "Pay", null);
                        Map<String, String> lProperties = Utils.getProperties();
                        if (!TextUtils.isEmpty(title)) {
                            lProperties.put("title", title);
                        }

                        if (!TextUtils.isEmpty(item_id)) {
                            lProperties.put("item_id", item_id);
                        }

                        AppDebug.i(TAG, TAG + ".aliTVPay.onPayProcessEnd.getPayResult = " + arg0.getPayResult());

                        if (arg0.getPayResult()) {
                            lProperties.put("result", "success");
                            result.addData(RESULT, "true");
                        } else {
                            lProperties.put("result", "fail");
                            result.addData(RESULT, "false");
                        }
                        result.setSuccess();
                        String res = result.toJsonString();
                        BlitzPlugin.responseJs(true, res, cbData_addr);
                        Utils.utCustomHit(mTaoBaoBlitzActivity.getFullPageName(), controlName, lProperties);
                    }
                });
            } catch (Exception e) {
                // 提示支付初始化失败
                String payfail = mTaoBaoBlitzActivity.getResources().getString(R.string.ytm_pay_initfail);

                final String exception = e.toString();

                AppDebug.e(TAG, TAG + ".aliTVPay.payfail = " + payfail);

                mTaoBaoBlitzActivity.showErrorDialog(payfail, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        result.addData(ERROR, exception);
                        result.addData(RESULT, "false");
                        String res = result.toJsonString();
                        BlitzPlugin.responseJs(false, res, cbData_addr);

                    }
                }, new DialogInterface.OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {

                            dialog.dismiss();
                            result.addData(ERROR, exception);
                            result.addData(RESULT, "false");
                            String res = result.toJsonString();
                            BlitzPlugin.responseJs(false, res, cbData_addr);

                            return true;
                        }
                        return false;
                    }
                });
            }
        }

    }

    private static class PayJsCallback implements BlitzPlugin.JsCallback {

        private WeakReference<TvTaoBaoPayPlugin> mReference;

        public PayJsCallback(WeakReference<TvTaoBaoPayPlugin> reference) {
            mReference = reference;
        }

        @Override
        public void onCall(String param, long cbData) {
            AppDebug.i(TAG, "onCall --> param  =" + param + ";  cbData = " + cbData);
            if (mReference != null && mReference.get() != null) {
                TvTaoBaoPayPlugin plugin = mReference.get();
                plugin.onHandleCallPay(param, cbData);
            }
        }
    }


}
