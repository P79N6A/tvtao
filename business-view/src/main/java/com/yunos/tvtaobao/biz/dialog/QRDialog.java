package com.yunos.tvtaobao.biz.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.dialog.util.SnapshotUtil;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.OrderDetailMO;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yunos.tvtaobao.businessview.R;

/**
 * Created by zhujun on 9/29/16.
 */

public class QRDialog extends Dialog {
    private String TAG = "STJumpcode";
    private RelativeLayout mOutermostLayout;
    private Activity mActivityContext;
    private Bitmap mBitmap;
    private volatile int currentID = 0;

    private Handler mHandler;

    private long timeMillis = 0L;

    private static final long EXPIRE_TIME = 180000L;

    private ArrayList<String> bizOrderIds;
    private OrderDetailMO[] results;

    public interface QRDialogDelegate {
        void QRDialogSuccess(QRDialog dialog, boolean success);
    }

    private QRDialogDelegate mDelegate;

    public void setDelegate(QRDialogDelegate delegate) {
        this.mDelegate = delegate;
    }

    private Runnable queryCurrentRunnable = new Runnable() {
        @Override
        public void run() {
            queryCurrent();
        }
    };
    private Runnable queryNextRunnable = new Runnable() {
        @Override
        public void run() {
            currentID++;
            queryCurrent();
        }
    };

    public QRDialog(Context context) {
        super(context);
    }

    public QRDialog(Context context, int theme) {
        super(context, theme);
        mHandler = new Handler();
    }

    public QRDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mHandler = new Handler();
    }

    public void setBizOrderIds(ArrayList<String> bizOrderIds) {
        this.bizOrderIds = bizOrderIds;
    }

    private SnapshotUtil.OnFronstedGlassSreenDoneListener screenShotListener = new SnapshotUtil.OnFronstedGlassSreenDoneListener() {

        @Override
        public void onFronstedGlassSreenDone(Bitmap bmp) {
            AppDebug.v(TAG, TAG + ".onFronstedGlassSreenDone.bmp = " + bmp);
            mBitmap = bmp;
            if (mOutermostLayout != null) {
                if ((mBitmap != null) && (!mBitmap.isRecycled())) {
                    Drawable[] array = new Drawable[2];
                    array[0] = new BitmapDrawable(mBitmap);
                    array[1] = new ColorDrawable(mActivityContext.getResources().getColor(R.color.ytbv_shadow_color_50));
                    LayerDrawable la = new LayerDrawable(array);
                    mOutermostLayout.setBackgroundDrawable(la);
                } else {
                    mOutermostLayout.setBackgroundColor(mActivityContext.getResources().getColor(
                            R.color.ytbv_shadow_color_80));
                }
            }
        }
    };

    @Override
    public void show() {
        //先将背景置为透明的，然后后续再改变
        if (mOutermostLayout != null) {
            mOutermostLayout.setBackgroundDrawable(null);
        }
        super.show();
        timeMillis = System.currentTimeMillis();
        if (bizOrderIds != null && bizOrderIds.size() > 0) {
            results = new OrderDetailMO[bizOrderIds.size()];
            queryIDs();
        }

        if (mActivityContext != null) {
            SnapshotUtil.getFronstedSreenShot(new WeakReference<Activity>(mActivityContext), 5, 0, screenShotListener);
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Utils.utPageAppear(TAG, TAG);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }

    private void queryCurrent() {
        if (bizOrderIds == null)
            return;
        if (currentID < 0 || currentID >= bizOrderIds.size())
            return;
        String bizid = bizOrderIds.get(currentID);
        BusinessRequest.getBusinessRequest().requestOrderDetail(Long.parseLong(bizid), new BizRequestListener<OrderDetailMO>(new WeakReference<BaseActivity>((BaseActivity) mActivityContext)) {
            @Override
            public boolean onError(int resultCode, String msg) {
                queryAgain();
                return true;
            }

            @Override
            public void onSuccess(OrderDetailMO data) {
                results[currentID] = data;
                if ("WAIT_BUYER_PAY".equals(data.getOrderInfo().getOrderStatusCode())) {
                    queryAgain();
                } else {
                    queryNext();
                }
            }

            @Override
            public boolean ifFinishWhenCloseErrorDialog() {
                return false;
            }
        });
    }

    private void queryIDs() {
        results = new OrderDetailMO[bizOrderIds.size()];
        currentID = 0;
        queryCurrent();
    }

    private void queryAgain() {
        if (isExpire())
            return;
        mHandler.removeCallbacks(queryCurrentRunnable);
        mHandler.removeCallbacks(queryNextRunnable);
        mHandler.postDelayed(queryCurrentRunnable, 5000);

    }

    private void queryNext() {
        if (isExpire())
            return;
        mHandler.removeCallbacks(queryNextRunnable);
        mHandler.removeCallbacks(queryCurrentRunnable);
        boolean allQueried = currentID == bizOrderIds.size() - 1;
        if (allQueried) {
            if (results.length > 0 && results[results.length - 1] != null) {
                if ("WAIT_BUYER_PAY".equals(results[results.length - 1].getOrderInfo().getOrderStatusCode()))
                    allQueried = false;
            }
        }
        double sum = 0;
        if (allQueried) {
            for (OrderDetailMO mo : results) {
                if (checkPaid(mo.getOrderInfo().getOrderStatusCode())) {
                    sum += Double.parseDouble(mo.getOrderInfo().getTotalPrice());

                } else {
                    return;//停止轮询
                }
            }


            final QRDialogDelegate delegate = mDelegate;
            boolean isInteger = sum == (int) sum;
            String sumText = isInteger ? String.format("成功付款 %.0f 元", sum) : String.format("成功付款 %.2f 元", sum);
            PayConfirmDialog payConfirmDialog = new PayConfirmDialog.Builder(mActivityContext).setCancelable(false)
                    .setMessage(onHandlerSpanned(sumText)).setPositiveButton("按OK键完成", new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (delegate != null)
                                delegate.QRDialogSuccess(QRDialog.this, true);
                            dialogInterface.dismiss();
                        }
                    }).create();
            payConfirmDialog.show();
            dismiss();
        } else {
            mHandler.postDelayed(queryNextRunnable, 5000);
        }


    }

    private SpannableStringBuilder onHandlerSpanned(String src) {
        SpannableStringBuilder style = new SpannableStringBuilder(src);
        int foregroundColor = 0xffff6c0f;
        Pattern p = Pattern.compile("\\d+(.)*\\d*");
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

    private boolean checkPaid(String statusCode) {
        if ("TRADE_FINISHED".equals(statusCode))
            return true;
        if ("WAIT_SELLER_SEND_GOODS".equals(statusCode))
            return true;
        if ("BUYER_PAYED_DEPOSIT".equals(statusCode) && bizOrderIds != null && bizOrderIds.size() == 1)//todo
            return true;
        if ("WAIT_BUYER_CONFIRM_GOODS".equals(statusCode))
            return true;
        return false;
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
        Utils.utUpdatePageProperties(TAG, Utils.getProperties());
        Utils.utPageDisAppear(TAG);
        mHandler.removeCallbacks(queryCurrentRunnable);
        mHandler.removeCallbacks(queryNextRunnable);
        if (mOutermostLayout != null) {
            mOutermostLayout.setBackgroundDrawable(null);
        }

        if ((mBitmap != null) && (!mBitmap.isRecycled())) {
            mBitmap.recycle();
            mBitmap = null;
        }

        mActivityContext = null;
        mOutermostLayout = null;
    }

    public static class Builder {

        private Context context;
        private boolean cancelable = true;
        private CharSequence mText;
        private CharSequence mTitle;
        private Bitmap mBitmap;

        public Builder(Context context) {
            this.context = context;
        }

        public QRDialog.Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public QRDialog.Builder setQRCodeText(CharSequence text) {
            mText = text;
            return this;
        }

        public QRDialog.Builder setQrCodeBitmap(Bitmap bitmap) {
            mBitmap = bitmap;
            return this;
        }

        public QRDialog.Builder setTitle(CharSequence title) {
            this.mTitle = title;
            return this;
        }

        /**
         * Create the custom dialog
         */
        public QRDialog create() {

            final QRDialog dialog = new QRDialog(context, R.style.ytbv_QR_Dialog);

            dialog.setContentView(R.layout.ytm_activity_buildorder_qrdialog);
            dialog.mOutermostLayout = (RelativeLayout) dialog.findViewById(R.id.qrcode_layout);
            if (context instanceof Activity) {
                dialog.mActivityContext = (Activity) context;
            }

            TextView titleView = (TextView) dialog.findViewById(R.id.title);
            if (titleView != null && !TextUtils.isEmpty(mTitle)) {
                titleView.setText(mTitle);
            }

            TextView textView = (TextView) dialog.findViewById(R.id.qrcode_text);
            if (textView != null && !TextUtils.isEmpty(mText)) {
                textView.setText(mText);
            }

            ImageView imageView = (ImageView) dialog.findViewById(R.id.qrcode_image);
            if (imageView != null && mBitmap != null) {
                imageView.setImageBitmap(mBitmap);
            }

            dialog.setCancelable(cancelable);
            return dialog;
        }
    }
}
