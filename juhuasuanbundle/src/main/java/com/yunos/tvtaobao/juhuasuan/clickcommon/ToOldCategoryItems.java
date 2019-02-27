package com.yunos.tvtaobao.juhuasuan.clickcommon;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.dialog.WaitProgressDialog;
import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tvtaobao.juhuasuan.activity.HomeActivity;
import com.yunos.tvtaobao.juhuasuan.activity.HomeCategoryActivity;
import com.yunos.tvtaobao.juhuasuan.activity.JuBaseActivity;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CategoryMO;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CountList;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.Option;
import com.yunos.tvtaobao.juhuasuan.config.ConstValues;
import com.yunos.tvtaobao.juhuasuan.config.SystemConfig;
import com.yunos.tvtaobao.juhuasuan.request.MyBusinessRequest;
import com.yunos.tvtaobao.juhuasuan.request.listener.JuRetryRequestListener;
import com.yunos.tvtaobao.juhuasuan.util.DialogUtils;
import com.yunos.tvtaobao.juhuasuan.util.ModelTranslator;
import com.yunos.tvtaobao.juhuasuan.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 调用老版本的类目商品列表页面
 * @author hanqi
 */
public class ToOldCategoryItems {

    private static final String TAG = "OldCategoryItems";

    private static boolean mCategoryListDone = false;

    /******** 资源 ******/
    private static MyBusinessRequest mBusinessRequest;

    private static Intent mIntent;

    public static void category(Activity context, ConstValues.HomeItemTypes type, String cateId) {
        category(context, type, cateId, 0);
    }

    public static void category(Activity context, ConstValues.HomeItemTypes type, String cateId, int flag) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra("type", type.name());
        intent.putExtra("cateId", cateId);
        boolean isFirstActivity = context.getIntent().getBooleanExtra(CoreIntentKey.URI_IS_FIRST_ACTIVITY, false);
        intent.putExtra(CoreIntentKey.URI_IS_FIRST_ACTIVITY, isFirstActivity);
        if (flag != 0) {
            intent.setFlags(flag);
        }
        context.startActivity(intent);
    }

    /**
     * 普通分类调用
     * @param activity
     * @param cateId
     */
    public static void category(JuBaseActivity activity, String cateId) {
        WaitProgressDialog mDialog = new WaitProgressDialog(activity);
        mDialog.show();
        mIntent.putExtra(HomeActivity.INTENT_QUANBU_ENABLED, SystemConfig.QUANBU_ENABLED);
        if (!StringUtil.isEmpty(cateId)) {
            mIntent.putExtra("cateId", cateId);
        }

        getCategoryList(activity, mDialog);
    }

    static {
        mBusinessRequest = MyBusinessRequest.getInstance();
        mIntent = new Intent();
    }

    /**
     * 获取目录列表
     */
    private static void getCategoryList(JuBaseActivity activity, final WaitProgressDialog dialog) {
        mBusinessRequest.requestCategory(0, new JuRetryRequestListener<CountList<Option>>(activity) {

            @Override
            public void onSuccess(BaseActivity baseActivity, CountList<Option> data) {

                boolean mErrorOccurred = false;
                if (data != null) {
                    List<CategoryMO> categoryList = ModelTranslator.translateCategory(data);
                    mIntent.putParcelableArrayListExtra(HomeActivity.INTENT_CATEGORY_LIST,
                            (ArrayList<? extends Parcelable>) categoryList);
                    mCategoryListDone = true;
                    startActivity(baseActivity, dialog);
                } else {
                    mErrorOccurred = true;
                }

                if (mErrorOccurred) {
                    if (baseActivity instanceof JuBaseActivity) {
                    }
                    dialog.dismiss();
                    DialogUtils.show(baseActivity, R.string.jhs_server_error);
                }
            }

            @Override
            public boolean onUserError(BaseActivity baseActivity, int resultCode, String msg) {
                if (baseActivity instanceof JuBaseActivity) {
                }
                return super.onUserError(baseActivity, resultCode, msg);
            }

        });
    }

    /**
     * 前往activity
     * @param context
     */
    private static void startActivity(Context context, WaitProgressDialog dialog) {
        AppDebug.i(TAG, TAG + ".startActivity mCategoryListDone=" + mCategoryListDone);
        if (mCategoryListDone) {
            if (null != dialog) {
                dialog.dismiss();
            }
            mCategoryListDone = false;
            mIntent.setClass(context, HomeActivity.class);
            Intent sIntent = (Intent) mIntent.clone();
            if (SystemConfig.DIPEI_BOX && context instanceof HomeCategoryActivity) {
                sIntent.putExtra(HomeActivity.INTENT_FROM_HOME_CATE_ACTIVITY, true);
            }
            context.startActivity(sIntent);
            mIntent = new Intent();
            if (context instanceof JuBaseActivity) {
            }
        }
    }
}
