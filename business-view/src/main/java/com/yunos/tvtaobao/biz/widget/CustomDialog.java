package com.yunos.tvtaobao.biz.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunos.tvtaobao.biz.dialog.DialogFocusLeftRightPositionManager;
import com.yunos.tvtaobao.businessview.R;


/**
 */
public class CustomDialog extends Dialog {
    private DialogFocusLeftRightPositionManager mOutermostLayout;

    public CustomDialog(Context context, int style) {
        super(context, style);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    public CustomDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public void show() {
        super.show();
    }

    public static class Builder {

        private Context context;
        private String message;
        private String resultMessage;
        private int dialogType;
        private int dialogIcon = 0;
        private String positiveButtonText;
        private String negativeButtonText;
        private boolean cancelable = true;
        private boolean hasIcon;
        private int style = R.style.RoundCornerDialog;
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;


        public Builder(Context context) {
            this.context = context;
        }

        //        type 1:没有按钮 2:有按钮
        public Builder setType(int type) {
            this.dialogType = type;
            return this;
        }

        //        没有按钮dialog中的图片
        public Builder setIcon(int icon) {
            this.dialogIcon = icon;
            return this;
        }

        //        没有按钮dialog中的图片
        public Builder setHasIcon(boolean hasIcon) {
            this.hasIcon = hasIcon;
            return this;
        }

        //       没有按钮dialog中的文案
        public Builder setResultMessage(String message) {
            this.resultMessage = message;
            return this;
        }

        //        有按钮dialog中的文案
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }


        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }



        public Builder setPositiveButton(String positiveButtonText, OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }


        public Builder setNegativeButton(String negativeButtonText, OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setStyle(int style) {
            this.style = style;
            return this;

        }

        //        public CustomDialog show() {
//            CustomDialog dialog = create();
//            dialog.show();
//            return dialog;
//        }

        /**
         * Create the custom dialog
         */
        public CustomDialog create() {

            final CustomDialog dialog = new CustomDialog(context, style);
            if (dialogType == 1) {
                dialog.setContentView(R.layout.layout_normal_dialog);
                ImageView ivDialogIcon = (ImageView) dialog.findViewById(R.id.iv_icon);
                if (dialogIcon != 0) {
                    ivDialogIcon.setImageResource(dialogIcon);
                }
                if (hasIcon) {
                    ivDialogIcon.setVisibility(View.VISIBLE);
                } else {
                    ivDialogIcon.setVisibility(View.GONE);
                }
                TextView dialogResult = (TextView) dialog.findViewById(R.id.tv_result);
                if (!TextUtils.isEmpty(resultMessage)) {
                    dialogResult.setText(resultMessage);
                }
                dialog.setOnShowListener(new OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (context instanceof Activity && !((Activity) context).isFinishing())
                                    dialog.dismiss();
                            }
                        }, 2000);
                    }
                });
            } else if (dialogType == 2) {
                dialog.setContentView(R.layout.layout_button_dialog);
//                dialog.mOutermostLayout = (DialogFocusLeftRightPositionManager) dialog.findViewById(R.id.super_parent);
//                dialog.mOutermostLayout.setSelector(new StaticFocusDrawable(context.getResources().getDrawable(R.drawable.ytbv_common_focus)));
//                dialog.mOutermostLayout.initView();
//                dialog.mOutermostLayout.requestFocus();

                final TextView positiveButton = (TextView) dialog.findViewById(R.id.positiveButton);
                if (positiveButtonText != null) {
                    positiveButton.setVisibility(View.VISIBLE);
                    positiveButton.setText(positiveButtonText);
                    positiveButton.requestFocus();
                    positiveButton.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            if (positiveButtonClickListener != null) {
                                positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                            }
                        }
                    });
                    positiveButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean hasFocus) {
                            if (hasFocus) {
                                positiveButton.setBackgroundResource(R.drawable.gradient_dialog_right_focus);
                                positiveButton.setTextColor(context.getResources().getColor(R.color.ytm_white));
                            } else {
                                positiveButton.setBackgroundResource(R.color.transparent);
                                positiveButton.setTextColor(context.getResources().getColor(R.color.new_cart_grey));
                            }
                        }
                    });
                } else {
                    positiveButton.setVisibility(View.GONE);
                }

                final TextView negativeButton = (TextView) dialog.findViewById(com.yunos.tvtaobao.businessview.R.id.negativeButton);
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
                    negativeButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View view, boolean hasFocus) {
                            if (hasFocus) {
                                negativeButton.setBackgroundResource(R.drawable.gradient_dialog_left_focus);
                                negativeButton.setTextColor(context.getResources().getColor(R.color.ytm_white));
                            } else {
                                negativeButton.setBackgroundResource(R.color.transparent);
                                negativeButton.setTextColor(context.getResources().getColor(R.color.new_cart_grey));
                            }
                        }
                    });
                } else {
                    negativeButton.setVisibility(View.GONE);
                }

                if (positiveButtonText == null && negativeButtonText == null) {
                    dialog.findViewById(R.id.foot).setVisibility(View.GONE);
                }

                if (message != null) {
                    TextView msgTextView = ((TextView) dialog.findViewById(com.yunos.tvtaobao.businessview.R.id.message));
                    msgTextView.setText(message);
                }

            }

            dialog.setCancelable(cancelable);

            return dialog;
        }
    }
}