package com.yunos.tvtaobao.juhuasuan.activity;


import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.util.IntentDataUtil;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.util.StringUtil;
import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.BrandMO;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CategoryMO;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CountList;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.Option;
import com.yunos.tvtaobao.juhuasuan.clickcommon.ToHomeCategory;
import com.yunos.tvtaobao.juhuasuan.clickcommon.ToOldCategoryItems;
import com.yunos.tvtaobao.juhuasuan.clickcommon.ToTaoBaoSdk;
import com.yunos.tvtaobao.juhuasuan.common.NetWorkCheck;
import com.yunos.tvtaobao.juhuasuan.common.NetWorkCheck.NetWorkConnectedCallBack;
import com.yunos.tvtaobao.juhuasuan.common.UrlKeyBaseConfig;
import com.yunos.tvtaobao.juhuasuan.config.ConstValues.HomeItemTypes;
import com.yunos.tvtaobao.juhuasuan.config.SystemConfig;
import com.yunos.tvtaobao.juhuasuan.request.MyBusinessRequest;
import com.yunos.tvtaobao.juhuasuan.request.listener.JuRetryRequestListener;
import com.yunos.tvtaobao.juhuasuan.util.DialogUtils;
import com.yunos.tvtaobao.juhuasuan.util.ModelTranslator;

import java.util.List;

public class PreloadActivity extends JuBaseActivity {

    public static final String TAG = "PreloadActivity";

    private Intent mIntent = null;

    // 在onpause中是否需要销毁该acitivity
    public boolean mNeedFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SystemConfig.init(this);
        AppDebug.i(TAG, TAG + ".onCreate is running");
        setTheme(R.style.style_launch);
        super.onCreate(savedInstanceState);

        addVersionCode();

        mIntent = getIntent();
        AppDebug.i(TAG, TAG + ".onCreate mIntent=" + mIntent);
        AppDebug.i(TAG, TAG + ".onCreate SysteConfig.DENSITY_DPI=" + SystemConfig.DENSITY_DPI);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppDebug.i(TAG, TAG + ".onResume is running");
        onHandleIntent();
    }

    /**
     * 显示版本信息
     */
    private void addVersionCode() {
        PackageManager pm = getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(getPackageName(), 0);

            FrameLayout layout = new FrameLayout(this);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            layout.setLayoutParams(params);
            TextView versionCode = new TextView(this);
            versionCode.setTextColor(Color.parseColor("#ffffff"));
            versionCode.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.sp_18));
            versionCode.setText(pi.versionName);
            FrameLayout.LayoutParams textparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            textparams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            textparams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.dp_25);
            layout.addView(versionCode, textparams);

            setContentView(layout);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void toHomeActivity() {
        //mHandler.sendEmptyMessage(0);
        AppDebug.i(TAG, TAG + ".toHomeActivity");
        ToHomeCategory.toHomeActivity(this);
        mNeedFinish = true;
    }

    @Override
    protected void onPause() {
        AppDebug.v(TAG, TAG + ".onPause.mNeedFinish = " + mNeedFinish);
        if (mNeedFinish) {
            finish();
            mNeedFinish = false;
        }
        super.onPause();
    }

    /**
     * 处理 Intent
     *
     * @return true goHome,false detail.list
     */
    private boolean onHandleIntent() {
        boolean network = NetWorkUtil.isNetWorkAvailable();
        AppDebug.i(TAG, TAG + ".onHandleIntent ==> NetWork.isAvailable=" + network);
        if (!network) {
            NetWorkCheck.netWorkError(this, new NetWorkConnectedCallBack() {

                @Override
                public void connected() {
                    onHandleIntent();
                }
            });
            return false;
        }
        if (mIntent == null) {
            return false;
        }
        //        dispatchViewModule();//分发
        gotoModuleActivity();
        //        gotoModuleActivity(module);
        return false;
    }

    /**
     * 跳转到相应的模块中
     */
    private void gotoModuleActivity() {
        String host = IntentDataUtil.getHost(mIntent);
        String type = IntentDataUtil.getString(mIntent, UrlKeyBaseConfig.INTENT_KEY_TYPE, "");
        String from = IntentDataUtil.getString(mIntent, UrlKeyBaseConfig.INTENT_KEY_FROM, "");
        AppDebug.i(TAG, TAG + ".gotoModuleActivity host=" + host + ", type : " + type + ", from : " + from);

        if (null == host) {
            toHomeActivity();
        } else if (host.equalsIgnoreCase(UrlKeyBaseConfig.INTENT_HOST_DETAIL)) { //如果host=detail，即老版本详情调用方式
            AppDebug.i(TAG, TAG + ".gotoModuleActivity to detail");
            CoreApplication.toast(CoreApplication.getApplication().getResources().getString(R.string.jhs_invalid_parameter));
            finish();
        } else if (host.equalsIgnoreCase(UrlKeyBaseConfig.INTENT_HOST_HOME)
                || host.equalsIgnoreCase(UrlKeyBaseConfig.INTENT_HOST_HOME2)) { //如果host=home,即新版本外部调用接口
            HomeItemTypes homeItemType = HomeItemTypes.valueOfs(type);
            if (null == homeItemType) {
                toHomeActivity();
            } else {
                String cateId = IntentDataUtil.getString(mIntent, UrlKeyBaseConfig.INTENT_KEY_CATEID, null);
                Long juId = IntentDataUtil.getLong(mIntent, UrlKeyBaseConfig.INTENT_KEY_ITEMID, -1L);
                boolean isFirstActivity = getIntent().getBooleanExtra(CoreIntentKey.URI_IS_FIRST_ACTIVITY, false);
                AppDebug.i(TAG, TAG + ".gotoModuleActivity homeItemType=" + homeItemType.name() + ", cateId = "
                        + cateId + ", juId = " + juId + ", isFirstActivity = " + isFirstActivity);
                switch (homeItemType) {
                    case HOME:
                        toHomeActivity();
                        break;
                    case JMP:
                        ToOldCategoryItems.category(this, HomeItemTypes.JMP, cateId, 0);//Intent.FLAG_ACTIVITY_CLEAR_TASK
                        break;
                    case PPT:
                        Intent pptIntent = new Intent(this, BrandHomeActivity.class);
                        pptIntent.putExtra(CoreIntentKey.URI_IS_FIRST_ACTIVITY, isFirstActivity);
                        //                        pptIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(pptIntent);
                        break;
                    case PPTDETAIL:
                        BrandMO brandModel = new BrandMO();
                        String id = IntentDataUtil.getString(mIntent, UrlKeyBaseConfig.INTENT_KEY_OPT_ID, "");
                        AppDebug.i(TAG, "opt id: " + id);
                        if (StringUtil.isEmpty(id)) {
                            CoreApplication.toast(CoreApplication.getApplication().getResources().getString(R.string.jhs_invalid_parameter));
                            break;
                        }
                        brandModel.setCode("activitySignId:" + id + ";");
                        Intent detailIntent = new Intent(this, BrandDetailActivity.class);
                        //                        detailIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        detailIntent.putExtra("data", brandModel);
                        detailIntent.putExtra(CoreIntentKey.URI_IS_FIRST_ACTIVITY, isFirstActivity);
                        startActivity(detailIntent);
                        break;
                    case CATE:
                        ToOldCategoryItems.category(this, HomeItemTypes.CATE, cateId, 0);
                        break;
                    case CATEGORY:
                        final String cateName = IntentDataUtil.getString(mIntent, UrlKeyBaseConfig.INTENT_KEY_CATENAME,
                                null);
                        AppDebug.e("TAG", "类目:============" + cateName);
                        if (StringUtil.isEmpty(cateName)) {
                            toHomeActivity();
                            return;
                        } else {
                            checkIsUsefulCate(cateName);
                        }
                        break;
                    case ORDER:
                        ToTaoBaoSdk.order(this);
                        break;
                    default:
                        CoreApplication.toast(CoreApplication.getApplication().getResources().getString(R.string.jhs_invalid_parameter));
                        finish();
                        break;
                }
                mNeedFinish = true;
            }
        } else {
            toHomeActivity();
        }
    }

    /**
     * 是否是一个有用的类目
     * 优先判断是否是一个普通类目，如果是则进入普通类目商品列表页面
     * 否则再判断是否是一个本地商品类目，如果是则进入本地商品类目商品列表，
     * 否则进入首页
     *
     * @param cateName
     */
    private void checkIsUsefulCate(final String cateName) {
        //        JuBusinessRequest.getBusinessRequest().requestCategory(PreloadActivity.this, 0,
        //                new CategoryBusinessRequestListener(this, cateName));
        MyBusinessRequest.getInstance().requestCategory(0, new JuRetryRequestListener<CountList<Option>>(this) {

            @Override
            public void onSuccess(BaseActivity baseActivity, CountList<Option> data) {
                PreloadActivity activity = null;
                if (baseActivity instanceof PreloadActivity) {
                    activity = (PreloadActivity) baseActivity;
                }
                if (null == activity || null == data) {
                    finish();
                    return;
                }

                List<CategoryMO> cates = ModelTranslator.translateCategory(data);
                String cateId1 = null;
                String cateId2 = null;
                for (CategoryMO cate : cates) {
                    if (cateName.equals(cate.getName())) {
                        cateId1 = cate.getCid();
                        break;
                    } else if (null == cateId1 && cate.getName().indexOf(cateName) != -1) {
                        cateId1 = cate.getCid();
                    } else if (null == cateId2 && cateName.indexOf(cate.getName()) != -1) {
                        cateId2 = cate.getCid();
                    }
                }
                if (null == cateId1) {
                    cateId1 = cateId2;
                }
                ToOldCategoryItems.category(activity, HomeItemTypes.CATE, cateId1, 0);//Intent.FLAG_ACTIVITY_CLEAR_TASK

            }
        });
    }

    /**
     * 错误提示,为外部进入使用
     *
     * @param msg
     */
    private void normalBusinessError(String msg) {
        if (msg == null) {
            return;
        }
        DialogUtils.show(this, msg, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
    }

    @Override
    protected boolean isTbs() {
        return false;
    }

    @Override
    public boolean isIgnored() {
        return true;
    }


}
