package com.yunos.tvtaobao.payment.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.yunos.tvtaobao.payment.R;
import com.yunos.tvtaobao.payment.alipay.request.GetOrderDetailRequest;
import com.yunos.tvtaobao.payment.analytics.Utils;
import com.yunos.tvtaobao.payment.qrcode.QRCodeManager;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mtopsdk.mtop.common.DefaultMtopCallback;
import mtopsdk.mtop.common.MtopFinishEvent;
import mtopsdk.mtop.common.MtopProgressEvent;
import mtopsdk.mtop.intf.Mtop;

/**
 * Created by rca on 22/12/2017.
 */

public class TaobaoPayQRDialog extends Dialog {
    private String TAG = "TaobaoQRDialog";

    private TextView mQRTitle1;
    private TextView mQRTitle2;
    private ImageView qrCodeImageView;
    private TextView messageTv;
    private TextView errorTv;


    private Handler mHandler;

    private long timeMillis = 0L;

    private int type = 0;

    private static final long EXPIRE_TIME = 180000L;

    private double price;
    private String taobaoOrderId;


    public interface QRDialogDelegate {
        void paymentComplete(TaobaoPayQRDialog dialog, boolean success);
    }

    private QRDialogDelegate mDelegate;

    public void setDelegate(QRDialogDelegate delegate) {
        this.mDelegate = delegate;
    }


    public TaobaoPayQRDialog(Context context) {
        super(context);
    }

    public TaobaoPayQRDialog(Context context, int theme) {
        super(context, theme);
        mHandler = new Handler();
    }

    public TaobaoPayQRDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mHandler = new Handler();
    }

    @Override
    public void show() {
        //if(getOwnerActivity()!=null&&!getOwnerActivity().isFinishing()&&!getOwnerActivity().isDestroyed()){
        //正常情况下也会有ownerActivity为null的情况，改为try catch,避免正常情况下也不弹提示窗
        try {
            super.show();
            Utils.utCustomHit("Expore_Pay_Pay", Utils.getProperties());
            timeMillis = System.currentTimeMillis();
        }catch (Exception e){

        }
        //TODO
    }

    private void generateQRCode() {
        int width = (int) getContext().getResources().getDimensionPixelSize(R.dimen.dp_334);
        int height = width;
        try {
            Bitmap bm = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.icon_taobao_qr_small);

            Bitmap qrBitmap = QRCodeManager.create2DCode("http://h5.m.taobao.com/mlapp/olist.html?OrderListType=wait_to_pay", width, height, bm);
            qrCodeImageView.setImageBitmap(qrBitmap);
//            qrBitmap = QRCodeManager.create2DCode("http://tm.m.taobao.com/list.htm?OrderListType=wait_to_pay", width, height, bm);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        generateQRCode();
        Utils.utCustomHit("Expore_Pay_QRcode", Utils.getProperties());
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDelegate = null;

    }

    private boolean isExpire() {
        long curr = System.currentTimeMillis();
        if (curr - timeMillis > EXPIRE_TIME)
            return true;
        return false;
    }

    private void queryAgain() {
        if (isExpire())
            return;
        mHandler.removeCallbacks(queryCurrentRunnable);
        mHandler.postDelayed(queryCurrentRunnable, 5000);
    }

    private Runnable queryCurrentRunnable = new Runnable() {
        @Override
        public void run() {
            queryCurrent();
        }
    };

    private void queryCurrent() {
        if (TextUtils.isEmpty(taobaoOrderId))
            return;
        GetOrderDetailRequest request = new GetOrderDetailRequest(taobaoOrderId);
        Mtop.instance(getContext()).build(request, null).useWua().addListener(new DefaultMtopCallback() {
            @Override
            public void onDataReceived(MtopProgressEvent event, Object context) {
                super.onDataReceived(event, context);
            }

            @Override
            public void onFinished(MtopFinishEvent event, Object context) {
                super.onFinished(event, context);
                if (event.getMtopResponse().isApiSuccess()) {
                    JSONObject data = event.getMtopResponse().getDataJsonObject();
                    String statusCode = GetOrderDetailRequest.getResponseStatus(data);
                    if (!"WAIT_BUYER_PAY".equals(statusCode)) {
                        notifyComplete(statusCode);
                        dismiss();
                    } else {
                        queryAgain();
                    }
                }
            }

        }).asyncRequest();
    }

    private void notifyComplete(String statusCode) {
        if (mDelegate != null) {
            mDelegate.paymentComplete(this, checkPaid(statusCode));
        }
    }

    private boolean checkPaid(String statusCode) {
        if ("TRADE_FINISHED".equals(statusCode))
            return true;
        if ("WAIT_SELLER_SEND_GOODS".equals(statusCode))
            return true;
        if ("BUYER_PAYED_DEPOSIT".equals(statusCode))
            return true;
        if ("WAIT_BUYER_CONFIRM_GOODS".equals(statusCode))
            return true;
        return false;
    }

    private SpannableStringBuilder spanPrice(String src) {
        SpannableStringBuilder style = new SpannableStringBuilder(src);
        int foregroundColor = 0xffff5500;
        Pattern p = Pattern.compile("¥ \\d+(.)*\\d*");
        Matcher m = p.matcher(src);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            style.setSpan(new ForegroundColorSpan(foregroundColor), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        return style;
    }

    private SpannableStringBuilder spanTaobao(String src) {
        SpannableStringBuilder style = new SpannableStringBuilder(src);
        int foregroundColor = 0xffff5500;
        Pattern p = Pattern.compile("【手机淘宝】");
        Matcher m = p.matcher(src);
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            style.setSpan(new ForegroundColorSpan(foregroundColor), start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        return style;
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            notifyComplete("UserCancel");
            dismiss();
        }
        return super.onKeyUp(keyCode, event);
    }

    public static class Builder {

        private Context context;
        private double orderPrice;
        private String taobaoId;
        private String errordesc;


        public Builder(Context context) {
            this.context = context;
        }

        public Builder setOrderPrice(double orderPrice) {
            this.orderPrice = orderPrice;
            return this;
        }

        public Builder setTaobaoOrderId(String taobaoOrderId) {
            this.taobaoId = taobaoOrderId;
            return this;
        }

        public Builder setErrordesc(String errordesc) {
            this.errordesc = errordesc;
            return this;
        }

        /**
         * Create the custom dialog
         */
        public TaobaoPayQRDialog create() {

            TaobaoPayQRDialog dialog = new TaobaoPayQRDialog(context, com.yunos.tvtaobao.payment.R.style.payment_QRdialog);

            dialog.setContentView(R.layout.payment_dialog_taobaoqrcode);
            dialog.mQRTitle1 = (TextView) dialog.findViewById(R.id.tv_qrcode_title_1);
            dialog.mQRTitle2 = (TextView) dialog.findViewById(R.id.tv_qrcode_title_2);
            dialog.qrCodeImageView = (ImageView) dialog.findViewById(R.id.qrcode_image);
            dialog.messageTv = (TextView) dialog.findViewById(R.id.message1);
            dialog.errorTv = (TextView) dialog.findViewById(R.id.errordesc);

            dialog.price = orderPrice;
            dialog.taobaoOrderId = taobaoId;

            String price = String.format("订单合计 ¥ %.0f 元", orderPrice);
            if (orderPrice - (int) orderPrice > 0) {
                price = String.format("订单合计 ¥ %.2f 元", orderPrice);
            }

            dialog.mQRTitle2.setText(dialog.spanPrice(price));

            dialog.errorTv.setText(errordesc);

            String taobaoMessage = "打开【手机淘宝】扫码付款";
            dialog.messageTv.setText(dialog.spanTaobao(taobaoMessage));

            dialog.setCancelable(false);
            return dialog;
        }
    }


}
