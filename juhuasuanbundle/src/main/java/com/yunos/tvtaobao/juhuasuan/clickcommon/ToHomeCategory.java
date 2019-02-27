package com.yunos.tvtaobao.juhuasuan.clickcommon;


import android.app.Activity;
import android.content.Intent;

import com.yunos.tvtaobao.juhuasuan.activity.HomeCategoryActivity;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;

public class ToHomeCategory {

    public static final String TAG = "ToHomeCategory";

    private Activity mContext;

    public static ToHomeCategory toHome;

    private ToHomeCategory(Activity context) {
        mContext = context;
    }

    public static void toHomeActivity(Activity context) {
        if (toHome == null) {
            toHome = new ToHomeCategory(context);
        }
        //        toHome.getHomeCategoryItems();

        AppDebug.i(TAG, TAG + ".toHomeActivity");
        Intent intent = new Intent(context, HomeCategoryActivity.class);
        boolean isFirstActivity = context.getIntent().getBooleanExtra(CoreIntentKey.URI_IS_FIRST_ACTIVITY, false);
        intent.putExtra(CoreIntentKey.URI_IS_FIRST_ACTIVITY, isFirstActivity);
        //        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static boolean isFinished() {
        return toHome == null || toHome.mContext == null;
    }

    public static void clean() {
        if (toHome != null) {
            toHome.mContext = null;
            toHome = null;
            System.gc();
        }
    }
}
