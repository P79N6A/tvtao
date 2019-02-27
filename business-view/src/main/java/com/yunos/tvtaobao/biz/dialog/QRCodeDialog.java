/**
 * $
 * PROJECT NAME: BusinessView
 * PACKAGE NAME: com.yunos.tvtaobao.biz.dialog
 * FILE NAME: QRCodeDialog.java
 * CREATED TIME: 2015年3月23日
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 *
 */
package com.yunos.tvtaobao.biz.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.dialog.util.SnapshotUtil;
import com.yunos.tvtaobao.biz.dialog.util.SnapshotUtil.OnFronstedGlassSreenDoneListener;
import com.yunos.tvtaobao.businessview.R;

import java.lang.ref.WeakReference;

/**
 * Class Descripton.
 *
 * @author mi.cao
 * @data 2015年3月23日 下午3:03:52
 */
public class QRCodeDialog extends Dialog {

    private String TAG = "QRCodeDialog";
    private RelativeLayout mOutermostLayout;
    private Activity mActivityContext;
    private Bitmap mBitmap;

    public QRCodeDialog(Context context) {
        super(context);
    }

    public QRCodeDialog(Context context, int theme) {
        super(context, theme);
    }

    public QRCodeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private OnFronstedGlassSreenDoneListener screenShotListener = new OnFronstedGlassSreenDoneListener() {

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

        if (mActivityContext != null) {
            SnapshotUtil.getFronstedSreenShot(new WeakReference<Activity>(mActivityContext), 5, 0, screenShotListener);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
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
        private String mText;
        private Bitmap mBitmap;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setQRCodeText(String text) {
            mText = text;
            return this;
        }

        public Builder setQrCodeBitmap(Bitmap bitmap) {
            mBitmap = bitmap;
            return this;
        }

        /**
         * Create the custom dialog
         */
        public QRCodeDialog create() {

            final QRCodeDialog dialog = new QRCodeDialog(context, R.style.ytbv_QR_Dialog);

            dialog.setContentView(R.layout.ytbv_qrcode_layout_new);
            dialog.mOutermostLayout = (RelativeLayout) dialog.findViewById(R.id.qrcode_layout);
            if (context instanceof Activity) {
                dialog.mActivityContext = (Activity) context;
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
