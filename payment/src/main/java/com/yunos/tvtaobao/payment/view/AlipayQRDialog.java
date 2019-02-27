package com.yunos.tvtaobao.payment.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.yunos.tvtaobao.payment.utils.UtilsDistance;
import com.yunos.tvtaobao.payment.alipay.task.AlipayAuthLoginTask;
import com.yunos.tvtaobao.payment.alipay.task.AlipayAuthTask;
import com.yunos.tvtaobao.payment.analytics.Utils;
import com.yunos.tvtaobao.payment.qrcode.QRCodeManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yunos.tvtaobao.payment.R;

/***
 * dialog to show alipay auth process
 */
public class AlipayQRDialog extends Dialog implements AlipayAuthTask.AlipayAuthTaskListener {
    private String TAG = "QRDialog";

    private TextView mQRTitle1;
    private TextView mQRTitle2;
    private ImageView qrCodeImageView;
    private TextView messageTv1;
//    private TextView messageTv2;
//    private TextView messageTv3;


    private Handler mHandler;

    private long timeMillis = 0L;

    private int type = 0;

    private static final long EXPIRE_TIME = 180000L;

    private String alipayUserId;
    private double price;
    private String taobaoOrderId;


    public interface QRDialogDelegate {
        void QRDialogSuccess(AlipayQRDialog dialog, boolean success);
    }

    private QRDialogDelegate mDelegate;

    public void setDelegate(QRDialogDelegate delegate) {
        this.mDelegate = delegate;
    }


    public AlipayQRDialog(Context context) {
        super(context);
    }

    public AlipayQRDialog(Context context, int theme) {
        super(context, theme);
        mHandler = new Handler();
    }

    public AlipayQRDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mHandler = new Handler();
    }

    @Override
    public void show() {
        try {
            //先将背景置为透明的，然后后续再改变
            super.show();
            Utils.utCustomHit("Expore_Pay_Authorization", Utils.getProperties());
            timeMillis = System.currentTimeMillis();
            generateQRCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AlipayAuthTask authTask;

    private void generateQRCode() {
        if (authTask != null) {
            authTask.cancel(true);
        }
        authTask = new AlipayAuthTask(getContext());
        authTask.setAlipayUserId(alipayUserId);
        authTask.setListener(this);
        authTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Utils.utCustomHit("Expore_Pay_Authorization", Utils.getProperties());
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDelegate = null;

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

    private boolean isExpire() {
        long curr = System.currentTimeMillis();
        if (curr - timeMillis > EXPIRE_TIME)
            return true;
        return false;
    }


    @Override
    public void dismiss() {
        super.dismiss();
        if (authTask != null)
            authTask.cancel(true);
        authTask = null;

    }

    @Override
    public void onReceivedAlipayAuthStateNotify(AlipayAuthTask.AlipayAuthTaskResult result) {
        if (result.getStep() == AlipayAuthLoginTask.STEP_GEN && result.getStatus() == AlipayAuthLoginTask.STATUS_SUCCESS) {
            final String url = (String) result.getObject();
            try {
                Bitmap alipayLogo = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.payment_icon_alipay);
                Bitmap bitmap = QRCodeManager.create2DCode(url, UtilsDistance.dp2px(getContext(), 324),
                        UtilsDistance.dp2px(getContext(), 324), alipayLogo);
                qrCodeImageView.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        } else if (result.getStep() == AlipayAuthLoginTask.STEP_QUERY && result.getStatus() == AlipayAuthLoginTask.STATUS_SUCCESS) {
            if (mDelegate != null) {
                mDelegate.QRDialogSuccess(this, result.getStatus() == AlipayAuthLoginTask.STATUS_SUCCESS);
            }
            dismiss();
        }
    }

    public static class Builder {

        private Context context;
        private double orderPrice;
        private String alipayUserId;
        private String taobaoOrderId;


        public Builder(Context context) {
            this.context = context;
        }


        public AlipayQRDialog.Builder setAlipayUserId(String userId) {
            alipayUserId = userId;
            return this;
        }

        public AlipayQRDialog.Builder setTaobaoOrderId(String orderId) {
            taobaoOrderId = orderId;
            return this;
        }

        public AlipayQRDialog.Builder setOrderPrice(double price) {
            this.orderPrice = price;
            return this;
        }


        /**
         * Create the custom dialog
         */
        public AlipayQRDialog create() {

            AlipayQRDialog dialog = new AlipayQRDialog(context, com.yunos.tvtaobao.payment.R.style.payment_QRdialog);

            dialog.setContentView(R.layout.payment_dialog_alipayqrcode);
            dialog.mQRTitle1 = (TextView) dialog.findViewById(R.id.tv_qrcode_title_1);
            dialog.mQRTitle2 = (TextView) dialog.findViewById(R.id.tv_qrcode_title_2);
            dialog.qrCodeImageView = (ImageView) dialog.findViewById(R.id.qrcode_image);
            dialog.messageTv1 = (TextView) dialog.findViewById(R.id.message1);
//            dialog.messageTv2 = (TextView) dialog.findViewById(R.id.message2);
//            dialog.messageTv3 = (TextView) dialog.findViewById(R.id.message3);
            String price = String.format("订单合计 ¥ %.0f 元", orderPrice);
            if (orderPrice - (int) orderPrice > 0) {
                price = String.format("订单合计 ¥ %.2f 元", orderPrice);
            }

            dialog.mQRTitle1.setText(dialog.spanPrice(price));
            String alipayTitle = "请使用【支付宝】完成付款";
            dialog.mQRTitle2.setText(dialog.spanAlipay(alipayTitle));

//            String msg2 = "【支付宝】扫码开通快捷支付";
//            dialog.messageTv2.setText(dialog.spanAlipay(msg2));
//            String msg3 = "【支付宝】全程担保交易安全";
//            dialog.messageTv3.setText(dialog.spanAlipay(msg3));


            dialog.alipayUserId = alipayUserId;
            dialog.taobaoOrderId = taobaoOrderId;
            dialog.price = orderPrice;

            dialog.setCancelable(false);
            return dialog;
        }
    }
}
