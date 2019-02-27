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

public class CustomConfirmDialog extends Dialog {

    private String TAG = "QuitPayConfirmDialog";
    private Activity mActivityContext;

    public CustomConfirmDialog(Context context, int theme) {
        super(context, theme);
    }

    public CustomConfirmDialog(Context context) {
        super(context);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
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
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
//        private String title;
        private boolean cancelable = true;

        private DialogInterface.OnClickListener positiveButtonClickListener;
        private DialogInterface.OnClickListener negativeButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

//        public Builder setTitle(String title) {
//            this.title = title;
//            return this;
//        }

        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setPositiveButton(int positiveButtonText, DialogInterface.OnClickListener listener) {
            this.positiveButtonText = (String) context.getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText, DialogInterface.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText, DialogInterface.OnClickListener listener) {
            this.negativeButtonText = (String) context.getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText, DialogInterface.OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public CustomConfirmDialog show() {
            CustomConfirmDialog dialog = create();
            dialog.show();
            return dialog;
        }

        /**
         * Create the custom dialog
         */
        public CustomConfirmDialog create() {

            final CustomConfirmDialog dialog = new CustomConfirmDialog(context, com.yunos.tvtaobao.payment.R.style.payment_QRdialog);

            dialog.setContentView(R.layout.payment_dialog_common);

            if (context instanceof Activity) {
                dialog.mActivityContext = (Activity) context;
            }
            // set the confirm button
            TextView positiveButton = (TextView) dialog.findViewById(R.id.positive);
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
            TextView negativeButton = (TextView) dialog.findViewById(R.id.negative);
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

//            if (title != null) {
//                TextView titleView = ((TextView) dialog.findViewById(R.id.title));
//                titleView.setText(title);
//            }

            //dialog.setContentView(layout);
            dialog.setCancelable(cancelable);
            positiveButton.requestFocus();
            return dialog;
        }
    }
}
