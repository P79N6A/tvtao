package com.yunos.tvtaobao.juhuasuan.common;


import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.IntentFilter;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.dialog.TvTaoBaoDialog;
import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.DialogParams;
import com.yunos.tvtaobao.juhuasuan.core.NetWorkChangeBroadcastReceiver;
import com.yunos.tvtaobao.juhuasuan.util.DialogUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 当断开网络时，检测网络，如网络连上了，再回调
 * @author hanqi
 */
public class NetWorkCheck {

    private static final String TAG = "NetWorkCheck";
    private static Map<String, NetWorkChangeBroadcastReceiver> receivers = new HashMap<String, NetWorkChangeBroadcastReceiver>();
    private static Object lock = new Object();

    /**
     * 网络连接错误的统一处理
     * @param context
     */
    public static void netWorkError(final Context context) {
        netWorkError(context, null);
    }

    /**
     * 网络连接错误的统一处理
     * @param context
     * @param callBack
     *            检测到网路重新连接后的回调
     */
    public static void netWorkError(final Context context, final NetWorkConnectedCallBack callBack) {

        TvTaoBaoDialog dialog = DialogUtils.get(DialogParams.makeParams(context)
                .setMsgResId(R.string.jhs_network_error_goto_set).setPositiveButtonTextResId(R.string.jhs_setting)
                .setPositiveButtonListener(new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        com.yunos.tv.core.util.Utils.startNetWorkSettingActivity(context,
                                context.getString(R.string.jhs_open_setting_activity_error));
                    }
                }).setPositiveButtonClickDismiss(false));

        String key = context.getClass().getName();
        synchronized (lock) {
            NetWorkChangeBroadcastReceiver receiver = receivers.get(key);
            if (null == receiver) {
                dialog.show();
                receiver = new NetWorkChangeBroadcastReceiver();
            } else {
                TvTaoBaoDialog oldDialog = receiver.getDialog();
                if (!oldDialog.isShowing()) {
                    dialog.show();
                    receiver.setDialog(dialog);
                }
                receiver.setCallBack(callBack);
                AppDebug.i(TAG, TAG + ".netWorkError receiver=" + receiver + ", callBack=" + callBack);
                return;
            }

            receiver.setCallBack(callBack);
            receiver.setDialog(dialog);
            IntentFilter filter = new IntentFilter();
            filter.addAction(NetWorkChangeBroadcastReceiver.CONNECTIVITY_CHANGE_ACTION);
            filter.setPriority(1000);
            receivers.put(key, receiver);
            AppDebug.i(TAG, TAG + ".netWorkError context=" + context.getClass().getName() + ", callBack=" + callBack);
            context.registerReceiver(receiver, filter);
        }

    }

    public static void unRegisterReceiver(Context context) {
        String key = context.getClass().getName();
        synchronized (lock) {
            NetWorkChangeBroadcastReceiver receiver = receivers.get(key);
            if (null != receiver) {
                context.unregisterReceiver(receiver);
                receivers.remove(key);
            }
        }
    }

    public interface NetWorkConnectedCallBack {

        public void connected();
    }
}
