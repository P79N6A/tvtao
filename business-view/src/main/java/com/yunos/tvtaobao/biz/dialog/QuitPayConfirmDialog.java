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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.dialog.util.SnapshotUtil;
import com.yunos.tvtaobao.businessview.R;

import java.lang.ref.WeakReference;

/**
 * Created by zhujun on 10/5/16.
 */

public class QuitPayConfirmDialog extends Dialog {

    private String TAG = "QuitPayConfirmDialog";
    private DialogFocusPositionManager mOutermostLayout;
    private Activity mActivityContext;
    private Bitmap mBitmap;

    public QuitPayConfirmDialog(Context context, int theme) {
        super(context, theme);
    }

    public QuitPayConfirmDialog(Context context) {
        super(context);
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

        if (mActivityContext != null) {
            SnapshotUtil.getFronstedSreenShot(new WeakReference<Activity>(mActivityContext), 5, 0, screenShotListener);
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
//        Utils.utControlHit("Expose_STJumpcode_pop_out_payment", Utils.getProperties());
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
    }

    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {

        private Context context;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private String title;
        private boolean cancelable = true;

        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setPositiveButton(int positiveButtonText, OnClickListener listener) {
            this.positiveButtonText = (String) context.getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText, OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText, OnClickListener listener) {
            this.negativeButtonText = (String) context.getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText, OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public QuitPayConfirmDialog show() {
            QuitPayConfirmDialog dialog = create();
            dialog.show();
            return dialog;
        }

        /**
         * Create the custom dialog
         */
        public QuitPayConfirmDialog create() {

            final QuitPayConfirmDialog dialog = new QuitPayConfirmDialog(context, R.style.ytbv_CustomDialog);

            dialog.setContentView(R.layout.dialog_confirmquitpay);
            dialog.mOutermostLayout = (DialogFocusPositionManager) dialog.findViewById(R.id.super_parent);
            dialog.mOutermostLayout.setSelector(new StaticFocusDrawable(context.getResources().getDrawable(
                    R.drawable.ytbv_common_focus)));
            dialog.mOutermostLayout.initView();
            dialog.mOutermostLayout.requestFocus();

            if (context instanceof Activity) {
                dialog.mActivityContext = (Activity) context;
            }
            // set the confirm button
            Button positiveButton = (Button) dialog.findViewById(R.id.positiveButton);
            if (positiveButtonText != null) {
                positiveButton.setVisibility(View.VISIBLE);
                positiveButton.setText(positiveButtonText);
                positiveButton.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        if (positiveButtonClickListener != null) {
                            positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                        }
                    }
                });
            } else {
                // if no confirm button just set the visibility to GONE
                positiveButton.setVisibility(View.GONE);

            }

            // set the cancel button
            Button negativeButton = (Button) dialog.findViewById(R.id.negativeButton);
            if (negativeButtonText != null) {
                negativeButton.setVisibility(View.VISIBLE);
                negativeButton.setText(negativeButtonText);

                negativeButton.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        if (negativeButtonClickListener != null) {
                            negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                        }
                        dialog.dismiss();
                    }
                });
            } else {
                // if no confirm button just set the visibility to GONE
                negativeButton.setVisibility(View.GONE);

            }

            if (positiveButtonText == null && negativeButtonText == null) {
                dialog.findViewById(R.id.foot).setVisibility(View.GONE);
            }

            // set the content message
            if (message != null) {
                TextView msgTextView = ((TextView) dialog.findViewById(R.id.message));
                msgTextView.setText(message);
            }

            if (title != null) {
                TextView titleView = ((TextView) dialog.findViewById(R.id.title));
                titleView.setText(title);
            }

            //dialog.setContentView(layout);
            dialog.setCancelable(cancelable);
            positiveButton.requestFocus();
            return dialog;
        }
    }
}
