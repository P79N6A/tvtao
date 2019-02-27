package com.yunos.tvtaobao.payment.alipay;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.KeyEvent;

import com.yunos.tvtaobao.payment.alipay.task.AgreementPayTask;
import com.yunos.tvtaobao.payment.analytics.Utils;
import com.yunos.tvtaobao.payment.view.AlipayQRDialog;
import com.yunos.tvtaobao.payment.view.CustomConfirmDialog;
import com.yunos.tvtaobao.payment.view.TaobaoPayQRDialog;

/**
 * Created by rca on 14/12/2017.
 */

public class AlipayPaymentManager {

    public interface AlipayAgreementPayListener {
        void paymentSuccess(double price, String alipayAccount);

        void paymentFailure(String message);

        void paymentCancel();
    }

    public static void doPay(final Context context, final double price, final String alipayUserId, final String taobaoOrderNo, boolean fromCart, boolean prePay, final AlipayAgreementPayListener listener) {
        AgreementPayTask payTask = new AgreementPayTask();
        payTask.setBizOrderId(taobaoOrderNo);
        payTask.setCheckDepositStatus(!fromCart || prePay);//购物车结算，则预付定金状态视为不成功，目前逻辑，后续完善,prePay字段目前不用，为了将来h5也会发起定金订单支付准备的字段
        payTask.setBuyerId(alipayUserId);
        payTask.setListener(new AgreementPayTask.AgreementPayListener() {
            @Override
            public void onPayMentSuccess(AgreementPayTask task, String alipayAccount) {
                if (listener != null) listener.paymentSuccess(price, alipayAccount);
            }

            @Override
            public void onPayMentFailure(AgreementPayTask task, final String errorMsg) {
                //TODO
                TaobaoPayQRDialog dialog = new TaobaoPayQRDialog.Builder(context).setErrordesc(errorMsg).setOrderPrice(price).setTaobaoOrderId(taobaoOrderNo).create();
                dialog.setDelegate(new TaobaoPayQRDialog.QRDialogDelegate() {
                    @Override
                    public void paymentComplete(TaobaoPayQRDialog dialog, boolean success) {
                        if (listener != null)
                            listener.paymentFailure(success ? null : errorMsg);//todo
                    }
                });
                dialog.show();
            }

            @Override
            public void onNeedAuth(final AgreementPayTask task, String userId) {
                AlipayQRDialog dialog = new AlipayQRDialog.Builder(context).setTaobaoOrderId(taobaoOrderNo).setOrderPrice(price).setAlipayUserId(userId).create();
                dialog.setDelegate(new AlipayQRDialog.QRDialogDelegate() {
                    @Override
                    public void QRDialogSuccess(AlipayQRDialog dialog, boolean success) {
                        dialog.dismiss();
                        task.resumePay();
                    }
                });
                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(final DialogInterface dialog, int keyCode, KeyEvent keyEvent) {
                        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                                CustomConfirmDialog confirmDialog = new CustomConfirmDialog.Builder(context).setMessage("支付未完成,是否确认退出?").setPositiveButton("确定退出", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        task.stop();
                                        dialog.dismiss();
                                        if (listener != null) listener.paymentCancel();
                                        Utils.utCustomHit("sure_exit", Utils.getProperties());
                                    }
                                }).setNegativeButton("继续支付", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        Utils.utCustomHit("cancel_exit", Utils.getProperties());
                                    }
                                }).create();
                                confirmDialog.show();
                            }
                            return true;
                        }
                        return false;
                    }
                });
                dialog.show();
            }
        });
        payTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
