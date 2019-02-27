package com.yunos.tvtaobao.takeoutbundle.activity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.taobao.atlas.hack.AtlasHacks;
import android.taobao.atlas.runtime.RuntimeVariables;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;

import com.google.zxing.WriterException;
import com.tvtaobao.voicesdk.tts.TTSUtils;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.pay.YunOSOrderManager;
import com.yunos.tv.core.util.Utils;
import com.yunos.tv.paysdk.AliTVPayClient;
import com.yunos.tv.paysdk.AliTVPayResult;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.dialog.QRDialog;
import com.yunos.tvtaobao.biz.dialog.QuitPayConfirmDialog;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.qrcode.QRCodeManager;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.request.bo.TakeOutOrderInfoBase;
import com.yunos.tvtaobao.biz.request.bo.TakeOutOrderInfoDetails;
import com.yunos.tvtaobao.biz.request.bo.TakeOutOrderListData;
import com.yunos.tvtaobao.biz.util.StringUtil;
import com.yunos.tvtaobao.payment.alipay.AlipayPaymentManager;
import com.yunos.tvtaobao.takeoutbundle.R;
import com.yunos.tvtaobao.takeoutbundle.adapter.TOOrderListAdapter;
import com.yunos.tvtaobao.takeoutbundle.view.OrderDeliveryPopWindow;
import com.yunos.tvtaobao.takeoutbundle.view.TOInnerFocusHorizontalListView;
import com.yunos.tvtaobao.takeoutbundle.view.TOOrderListItemFocusLayout;
import com.yunos.tvtaobao.takeoutbundle.view.TOOrderListPositionManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TakeOutOrderListActivity extends BaseActivity {

    private static final String TAG = "TOOrderListActivity";

    private TOOrderListPositionManager focusPositionManager;
    private TOOrderListAdapter orderListAdapter;
    private TOInnerFocusHorizontalListView focusHListView;
    private int pageNo = 1;

    private View root;

    private boolean isRequestNextData; // 是否还在请求下次的数据
    private boolean isDataDone; // 数据是否加载完毕.
    OrderDeliveryPopWindow popWindow;

    private BusinessRequest businessRequest = BusinessRequest.getBusinessRequest();
    private String from;
    private String v_from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onKeepActivityOnlyOne(TakeOutOrderListActivity.class.getName());
        // api 小于17 atlas 没有进行 ContextThemeWrapper_mResources 赋值
        try {

            if (android.os.Build.VERSION.SDK_INT >= 17){

                AtlasHacks.ContextThemeWrapper_mResources.set(this, RuntimeVariables.delegateResources);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        setContentView(R.layout.activity_take_out_order_list);

        root = findViewById(R.id.to_order_detail_list_root_layout);

        focusHListView = (TOInnerFocusHorizontalListView) findViewById(R.id.take_out_order_list_view);
        focusPositionManager = (TOOrderListPositionManager) findViewById(R.id.to_order_detail_list_root_layout);
        focusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(
                R.drawable.to_focus_round)));
        focusHListView.setFlingScrollMaxStep(100);
        focusHListView.setFlipScrollFrameCount(10);

        focusPositionManager.requestFocus();


        isDataDone = false;
        isRequestNextData = false;

        refreshData();

        focusHListView.setOnItemSelectedListener(new ItemSelectedListener() {
            @Override
            public void onItemSelected(View v, int position, boolean isSelected, View view) {
                if (v instanceof TOOrderListItemFocusLayout) {
                    orderListAdapter.selectItemView(isSelected);
                }

                if (isSelected) {
                    checkLoadNextListData(position);
                }
            }
        });
        from = getIntent().getStringExtra(CoreIntentKey.URI_FROM);
        v_from = getIntent().getStringExtra("v_from");//记录来源，传递到前端下单
//        tts = getIntent().getStringExtra("tts");
//        tips = getIntent().getStringArrayListExtra("tips");
//        if (tipsDialog == null) {
//            tipsDialog = new TipsDialog(TakeOutOrderListActivity.this);
//        }
//        if (!TextUtils.isEmpty(tts) || tips != null) {
//            tipsDialog.setTts(tts);
//            tipsDialog.setTips(tips);
//            tipsDialog.show();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onRemoveKeepedActivity(TakeOutOrderListActivity.class.getName());
        if (orderListAdapter != null) {
            orderListAdapter.cancelTimer();
        }
    }

    @Override
    public String getFullPageName() {
        return "Page_waimai_Order";
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (popWindow != null && popWindow.isShowing()) {
                popWindow.dismiss();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showDelivery(String mainOrderId) {
        if (popWindow == null) {
            popWindow = new OrderDeliveryPopWindow(this);
        }
        popWindow.showDetailPopWindow(root);
        popWindow.updateData(mainOrderId, this);
    }

    /**
     * 检查是否要更后续的数据
     *
     * @param currentSelectedPos
     */
    private void checkLoadNextListData(int currentSelectedPos) {
        // 10项数据...
        if ((orderListAdapter.getDataSize() - currentSelectedPos) <= 10 && !isRequestNextData
                && !isDataDone) {
            isRequestNextData = true;
            pageNo++;
            refreshData();
        }
    }

    private Handler handler = new Handler();

    public void updateOrderData(TakeOutOrderListData orderListData) {
        if (orderListAdapter == null) {
            orderListAdapter = new TOOrderListAdapter(this, focusHListView, focusPositionManager);
            orderListAdapter.updateData(orderListData);
            focusHListView.setAdapter(orderListAdapter);
        }

        if (pageNo > 1) {
            orderListAdapter.appendData(orderListData);
        } else {
            orderListAdapter.updateData(orderListData);

            handler.post(new Runnable() {
                public void run() {
                    focusHListView.setSelection(0);
                    focusPositionManager.requestFocus(focusHListView, View.FOCUS_RIGHT);
                }
            });
        }

        if ("voice_system".equals(from) || "voice_application".equals(from)) {
            TTSUtils.getInstance().showDialog(this, 1);
//            TakeOutProgressControl.showDialog();
        }
    }

    private class GetTakeOutOrderListListener extends BizRequestListener<TakeOutOrderListData> {

        public GetTakeOutOrderListListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            isRequestNextData = false;
            return false;
        }

        @Override
        public void onSuccess(TakeOutOrderListData data) {
            if (data != null) {
                isRequestNextData = false;

                //TODO 数据加载完，没有更多了.
                //isDataDone = true;

                TakeOutOrderListActivity takeOutOrderListActivity =
                        (TakeOutOrderListActivity) mBaseActivityRef.get();
                if (takeOutOrderListActivity != null) {
                    takeOutOrderListActivity.updateOrderData(data);
                }
            } else {
                isRequestNextData = false;
                //TODO 线上空白/ErrorView
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    // 支付订单 Start
    @Override
    public void refreshData() {
        if (pageNo == 1) {
            isDataDone = false;
        }
        isRequestNextData = true;
        businessRequest.getTakeOutOrderList(pageNo,
                new GetTakeOutOrderListListener(new WeakReference<BaseActivity>(this)));
    }

    AliTVPayClient.IPayCallback payCallback = new AliTVPayClient.IPayCallback() {

        @Override
        public void onPayProcessEnd(AliTVPayResult arg0) {
            if (arg0 == null) {
                return;
            }

            if (arg0.getPayResult()) {
                // 支付成功，刷新订单状态.
                pageNo = 1;
                refreshData();
            } else {
                showError("支付失败");
            }
        }
    };

    /**
     * 订单状态行为，支付
     *
     * @param infoBase
     */
    public void payOrder(final TakeOutOrderInfoBase infoBase) {
        AppDebug.e(TAG, "Pay Order = " + infoBase.getTbMainOrderId());

        // 获取订单详情中的支付宝订单号...
        businessRequest.getTakeOutOrderDetail(infoBase.getTbMainOrderId(),
                new BizRequestListener<TakeOutOrderInfoDetails>(new WeakReference<BaseActivity>(this)) {
                    @Override
                    public boolean onError(int resultCode, String msg) {
                        showError("支付失败");
                        return false;
                    }

                    @Override
                    public void onSuccess(TakeOutOrderInfoDetails data) {
                        if (data == null || data.getDetails4Trade() == null ||
                                StringUtil.isEmpty(data.getDetails4Trade().getAlipayNo())) {
                            showError("支付失败");
                            return;
                        }

                        boolean checkConfig = GlobalConfig.instance != null && GlobalConfig.instance.taobaoPay;
                        if (checkConfig) {
                            String price = String.valueOf(infoBase.getTotalFee());
                            showQRDialog(data.getDetails4Trade().getTbOrderId(), price);
                        } else {
                            if (Config.isAgreementPay()) {//协议支付
                                String payNo = data.getDetails4Trade().getAlipayNo();
                                double price = infoBase.getTotalFee() / 100.0f;

                                String alipayTradeNo = String.valueOf(payNo);
                                String taobaoOrderNo = String.valueOf(infoBase.getTbMainOrderId());
                                AlipayPaymentManager.doPay(TakeOutOrderListActivity.this, price, null, taobaoOrderNo, false, false, new AlipayPaymentManager.AlipayAgreementPayListener() {
                                    @Override
                                    public void paymentSuccess(double price, String account) {
                                        OnWaitProgressDialog(false);
                                        pageNo = 1;
                                        refreshData();
                                    }

                                    @Override
                                    public void paymentFailure(String errorMsg) {
                                        OnWaitProgressDialog(false);
                                        showError("支付失败");
                                    }

                                    @Override
                                    public void paymentCancel() {
                                        OnWaitProgressDialog(false);
                                    }
                                });

                            } else {//旧版支付
                                String payNo = data.getDetails4Trade().getAlipayNo();

                                YunOSOrderManager orderManager = new YunOSOrderManager();
                                final String subject = infoBase.getItemShowTitle();// 商品名称
                                String price = String.valueOf(infoBase.getTotalFee());
                                String alipayTradeNo = String.valueOf(payNo);
                                String taobaoOrderNo = String.valueOf(infoBase.getTbMainOrderId());
                                orderManager.GenerateOrder("orderNo=" + alipayTradeNo + "&subject=" + subject + "&price=" + price
                                        + "&orderType=trade" + "&taobaoOrderNo=" + taobaoOrderNo);

                                String order = orderManager.getOrder();
                                String sign = orderManager.getSign();

                                Bundle bundle = new Bundle();
                                bundle.putString("provider", "alipay");

                                try {
                                    AliTVPayClient payer = new AliTVPayClient();
                                    payer.aliTVPay(TakeOutOrderListActivity.this, order, sign, bundle, payCallback);
                                } catch (Exception e) {
                                    showError("支付失败");
                                }
                            }

                        }

                    }

                    @Override
                    public boolean ifFinishWhenCloseErrorDialog() {
                        return false;
                    }
                });
    }

    /**
     * 展示手淘扫码
     */
    private void showQRDialog(String orderNo, String price) {
        Bitmap qrBitmap = null;
        int width = (int) getResources().getDimensionPixelSize(R.dimen.dp_334);
        int height = width;
        try {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.icontaobao);
            qrBitmap = QRCodeManager.create2DCode("http://h5.m.taobao.com/app/waimai/orderlist.html", width, height, bm);
//            https://h5.m.taobao.com/app/waimai/index.html#/order
//            qrBitmap = QRCodeManager.create2DCode("http://h5.m.taobao.com/mlapp/olist.html?OrderListType=wait_to_pay", width, height, bm);
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

        final QRDialog dialog = new QRDialog.Builder(this).setCancelable(true).setQrCodeBitmap(qrBitmap).setTitle(createSpannableTitle("打开【手机淘宝】扫码付款")).
                setQRCodeText(createSpannable("订单合计" + priceLabel + "元")).create();
        ArrayList<String> ids = new ArrayList<String>();
        ids.add(orderNo);
        dialog.setBizOrderIds(ids);
        dialog.setDelegate(new QRDialog.QRDialogDelegate() {
            @Override
            public void QRDialogSuccess(QRDialog dialog, boolean success) {
                pageNo = 1;
                refreshData();
            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                    @Override
                                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                                        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                                            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                                                Utils.utCustomHit("Expose_STJumpcode_pop_out_payment", Utils.getProperties());
                                                QuitPayConfirmDialog confirmDialog = new QuitPayConfirmDialog.Builder(TakeOutOrderListActivity.this).setTitle("退出支付").setMessage("支付未完成,是否确认退出?").setPositiveButton("确定退出", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        Utils.utControlHit("STJumpcode_pop_out_payment", "button_out", Utils.getProperties());
                                                        dialogInterface.dismiss();
                                                        dialog.dismiss();
                                                        showError("取消支付");
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

    /**
     * 创建spannableString
     */
    private SpannableStringBuilder createSpannable(String src) {
        SpannableStringBuilder style = new SpannableStringBuilder(src);
        int foregroundColor = 0xffff6c0f;
        Pattern p = Pattern.compile("(.)*\\d+");
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

    /**
     * 提示支付失败
     */
    public void showError(String message) {

        showErrorDialog(message, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    return true;
                }
                return false;
            }
        });
    }
    // 支付订单 End


    public String getFrom() {
        if (TextUtils.isEmpty(v_from)) {
            if (TextUtils.isEmpty(from)) {
                return "";
            }
            return from;
        }
        return v_from;
    }
}
