package com.yunos.tvtaobao.juhuasuan.clickcommon;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tvtaobao.juhuasuan.activity.JuBaseActivity;
import com.yunos.tvtaobao.juhuasuan.common.NetWorkCheck;

//import com.yunos.tvtaobao.blitz.account.LoginHelper;
//import com.yunos.tvtaobao.biz.account.LoginHelper;

/**
 * 调用淘宝SDK
 *
 * @author hanqi
 */
public class ToTaoBaoSdk {

    /**
     * 进入TaobaoSdk的订单界面
     *
     * @param context
     * @param itemId
     * @return true: 能够进入 false: 不能进入
     */

    public static boolean toSureJoin(Activity context, long itemId, String JuKey, String from) {
        if (itemId <= 0) {
            return false;
        }
        if (!NetWorkUtil.isNetWorkAvailable()) {
            NetWorkCheck.netWorkError(context);
            return false;
        }
        if (!User.isLogined()) {
            CoreApplication.getLoginHelper(context).startYunosAccountActivity(context, true);
            return false;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("tvtaobao://home?module=sureJoin&itemId=" + String.valueOf(itemId) + "&from=" + from));
        context.startActivity(intent);
        return true;
    }

    /**
     * 前往淘宝SDK订单页面
     *
     * @param context
     */
    public static void order(Context context) {
//        TYIDManager mTYIDManager;
        boolean loginStatus = false;
        try {
//            mTYIDManager = TYIDManager.get(CoreApplication.getApplication());
//            loginStatus = mTYIDManager.yunosGetLoginState();
            loginStatus = CoreApplication.getLoginHelper(CoreApplication.getApplication()).isLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (loginStatus) {
            Activity activity = (Activity) context;
            CoreApplication.getLoginHelper(CoreApplication.getApplication()).startYunosAccountActivity(activity, false);
            AppDebug.i("jhsToOrder", "startLoginActivity loginStatus = " + loginStatus + ",forceLogin=true");
            return;
        }

        String orderUri = "tvtaobao://home?module=orderList";
        //        String huodong = null;
        //        String from = null;
        //        if (context instanceof CoreActivity) {
        //            CoreActivity activity = (CoreActivity) context;
        //            from = activity.getmFrom();
        //            huodong = activity.getmHuoDong();
        //        } else if (context instanceof CoreFragmentActivity) {
        //            CoreFragmentActivity activity = (CoreFragmentActivity) context;
        //            from = activity.getmFrom();
        //            huodong = activity.getmHuoDong();
        //        }
        //        if (StringUtil.isEmpty(from)) {
        //            orderUri += "&" + CoreIntentKey.URI_FROM + "=" + from;
        //        }
        //        if (StringUtil.isEmpty(from)) {
        //            orderUri += "&" + CoreIntentKey.URI_HUODONG + "=" + huodong;
        //        }
        Intent orderIntent = new Intent(Intent.ACTION_VIEW);
        orderIntent.setData(Uri.parse(orderUri));
        context.startActivity(orderIntent);
        AppDebug.i("ToTaoBaoSdk", "ToTaoBaoSdk.order orderUri=" + orderUri);
        if (context instanceof JuBaseActivity) {
            ((JuBaseActivity) context).afterApiLoad(true, null, orderIntent);
        }
    }
}
