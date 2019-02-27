package com.yunos.tvtaobao.juhuasuan.clickcommon;


import android.content.Context;
import android.content.Intent;

import com.yunos.tvtaobao.juhuasuan.activity.BrandDetailActivity;
import com.yunos.tvtaobao.juhuasuan.activity.BrandHomeActivity;
import com.yunos.tvtaobao.juhuasuan.activity.HomeActivity;
import com.yunos.tvtaobao.juhuasuan.activity.HomeCategoryActivity;
import com.yunos.tvtaobao.juhuasuan.activity.JuBaseActivity;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.BrandMO;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.HomeCatesBo;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.HomeItemsBo;
import com.yunos.tvtaobao.juhuasuan.config.SystemConfig;


/**
 * 调用品牌团页面
 * @author hanqi
 */
public class ToPinPaiTuan {

    /**
     * 前往品牌团列表页
     * @param context
     */
    public static void ppt(Context context) {
        Intent pptIntent = new Intent(context, BrandHomeActivity.class);
        if (SystemConfig.DIPEI_BOX && context instanceof HomeCategoryActivity) {
            pptIntent.putExtra(HomeActivity.INTENT_FROM_HOME_CATE_ACTIVITY, true);
        }
        context.startActivity(pptIntent);
        if (context instanceof JuBaseActivity) {
            ((JuBaseActivity) context).afterApiLoad(true, null, pptIntent);
        }
    }

    /**
     * 前往品牌团详情页（品牌团商品列表页）
     * @param activity
     * @param homeCate
     * @param homeItem
     */
    public static void pptDetail(Context activity, HomeCatesBo homeCate, HomeItemsBo homeItem) {
        Intent pptDetailIntent = new Intent(activity, BrandDetailActivity.class);
        if (null != homeCate) {
            pptDetailIntent.putExtra("homeCate", homeCate);
        } else if (null != homeItem) {
            pptDetailIntent.putExtra("homeItem", homeItem);
        }
        if (SystemConfig.DIPEI_BOX && activity instanceof HomeCategoryActivity) {
            pptDetailIntent.putExtra(HomeActivity.INTENT_FROM_HOME_CATE_ACTIVITY, true);
        }
        activity.startActivity(pptDetailIntent);
        if (activity instanceof JuBaseActivity) {
            ((JuBaseActivity) activity).afterApiLoad(true, null, pptDetailIntent);
        }
    }

    /**
     * 前往品牌团详情页（品牌团商品列表页）
     * @param activity
     */
    public static void pptDetail(Context activity, BrandMO brandModel) {
        Intent detailIntent = new Intent(activity, BrandDetailActivity.class);
        detailIntent.putExtra("data", brandModel);
        activity.startActivity(detailIntent);
        if (activity instanceof JuBaseActivity) {
            ((JuBaseActivity) activity).afterApiLoad(true, null, detailIntent);
        }
    }
}
