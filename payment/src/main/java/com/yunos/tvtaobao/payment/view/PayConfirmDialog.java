package com.yunos.tvtaobao.payment.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yunos.tvtaobao.payment.R;

/**
 * Created by zhujun on 10/5/16.
 */

public class PayConfirmDialog extends Dialog {

    private String TAG = "successpayment";
    private Activity mActivityContext;

    public PayConfirmDialog(Context context, int theme) {
        super(context, theme);
    }

    public PayConfirmDialog(Context context) {
        super(context);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {

        private Context context;
        private CharSequence message;
        private String positiveButtonText;
        private boolean cancelable = true;

        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(CharSequence message) {
            this.message = message;
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

        public PayConfirmDialog show() {
            PayConfirmDialog dialog = create();
            dialog.show();
            return dialog;
        }

        /**
         * Create the custom dialog
         */
        public PayConfirmDialog create() {

            final PayConfirmDialog dialog = new PayConfirmDialog(context, com.yunos.tvtaobao.payment.R.style.payment_QRdialog);

            dialog.setContentView(R.layout.dialog_confirmpay);

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

            if (positiveButtonText == null) {
                dialog.findViewById(R.id.foot).setVisibility(View.GONE);
            }

            // set the content message
            if (message != null) {
                TextView msgTextView = ((TextView) dialog.findViewById(R.id.message));
                msgTextView.setText(message);
            }


            //dialog.setContentView(layout);
            dialog.setCancelable(cancelable);
            positiveButton.requestFocus();
            return dialog;
        }
    }
}
