package com.yunos.tvtaobao.detailbundle.resconfig;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.yunos.tvtaobao.detailbundle.R;


public class TaobaoResConfig extends AbstractResConfig {

    public TaobaoResConfig(Context context) {
        super(context);

    }

    @Override
    public GoodsType getGoodsType() {

        return GoodsType.TAOBAO;
    }

    @Override
    public int getColor() {

        return mContext.getResources().getColor(R.color.ytm_orange);
    }

    @Override
    public Drawable getBuyIcon() {
        if (isGreenStatus) {
            return mContext.getResources().getDrawable(R.drawable.ytsdk_ui2_detail_taobao_icon_green);
        } else if (canBuy) {
            return mContext.getResources().getDrawable(R.drawable.ytsdk_ui2_detail_taobao_icon);
        } else {
            return mContext.getResources().getDrawable(R.drawable.ytsdk_ui2_detail_taobao_icon_gray);
        }

    }

    @Override
    public Drawable getBuyBgIcon() {
        if (isGreenStatus) {
            return mContext.getResources().getDrawable(R.drawable.ytsdk_ui2_detail_taobao_green);
        } else if (canBuy) {
            return mContext.getResources().getDrawable(R.drawable.ytsdk_ui2_detail_taobao);
        } else {
            return mContext.getResources().getDrawable(R.drawable.ytsdk_ui2_detail_taobao_gray);
        }
    }

    @Override
    public Drawable getBuyBgFocusIcon() {
        if (isGreenStatus) {
            return mContext.getResources().getDrawable(R.drawable.ytsdk_ui2_detail_taobao_green);
        } else if (canBuy) {
            return mContext.getResources().getDrawable(R.drawable.ytsdk_ui2_detail_taobao_focus);
        } else {
            return mContext.getResources().getDrawable(R.drawable.ytsdk_ui2_detail_taobao_gray);
        }
    }

    @Override
    public Drawable getScrollIcon() {

        return mContext.getResources().getDrawable(R.drawable.ytsdk_ui2_detail_scrollbar);
    }

    @Override
    public Drawable getButtonSelectedDraw() {

        return mContext.getResources().getDrawable(R.drawable.ytsdk_ui2_detail_button_focus);
    }

    @Override
    public Drawable getCartIcon() {

        return mContext.getResources().getDrawable(R.drawable.ytsdk_ui2_detail_tmall_cart_normal);
    }

    @Override
    public Drawable getCartFocusIcon() {

        return mContext.getResources().getDrawable(R.drawable.ytsdk_ui2_detail_cart_focus);
    }

    @Override
    public Drawable getScanCodeIcon() {

        return mContext.getResources().getDrawable(R.drawable.ytsdk_ui2_detail_tmall_scan_code_normal);
    }

    @Override
    public Drawable getScanCodeFocusIcon() {

        return mContext.getResources().getDrawable(R.drawable.ytsdk_ui2_detail_scan_code_focus);
    }

    @Override
    public Drawable getQRCodeIcon() {

        return mContext.getResources().getDrawable(R.drawable.ytm_qr_code_icon_taobao);
    }

    @Override
    public Drawable getFavIcon() {

        return mContext.getResources().getDrawable(R.drawable.ytsdk_ui2_detail_tmall_collect_normal);
    }

    @Override
    public Drawable getFavFocusIcon() {

        return mContext.getResources().getDrawable(R.drawable.ytsdk_ui2_detail_collect_focus);
    }

    @Override
    public Drawable getShopIndexIcon() {

        return mContext.getResources().getDrawable(R.drawable.ytsdk_ui2_detail_tmall_shop_normal);
    }

    @Override
    public Drawable getShopIndexFocusIcon() {

        return mContext.getResources().getDrawable(R.drawable.ytsdk_ui2_detail_shop_focus);
    }

    @Override
    public ColorStateList getFontColor() {

        return mContext.getResources().getColorStateList(R.color.ytm_orange);
    }

    @Override
    public Drawable getRelevantSelectDraw() {

        return mContext.getResources().getDrawable(R.drawable.ytsdk_ui2_detail_relevant_focus);
    }

    @Override
    public Context getContext() {

        return mContext;
    }

    @Override
    public int getGoodsEvaluateVisible() {

        return View.GONE;
    }

    @Override
    public ColorStateList getModuleNameFontColor() {

        return mContext.getResources().getColorStateList(R.color.ytsdk_detail_module_name);
    }

    @Override
    public ColorStateList getModuleNameFontFocusColor() {

        return mContext.getResources().getColorStateList(R.color.ytm_orange);
    }

    @Override
    public Drawable getCommentItemSelectDraw() {

        return mContext.getResources().getDrawable(R.drawable.ytsdk_ui2_detail_word_bg);
    }

    @Override
    public Drawable getEvaluateFocusDraw() {

        return mContext.getResources().getDrawable(R.drawable.ytsdk_detail_comment_border_focus_shape);
    }

    @Override
    public Drawable getEvaluateDraw() {

        return mContext.getResources().getDrawable(R.drawable.ytsdk_detail_button_shape);
    }

    @Override
    public Drawable getButtonNormalDraw() {

        return mContext.getResources().getDrawable(R.drawable.ytsdk_ui2_detail_tmall_button_normal);
    }

    @Override
    public ColorStateList getDetailStatusTextColor() {
        if (canBuy) {
            return mContext.getResources().getColorStateList(R.color.ytm_white);
        } else {
            return mContext.getResources().getColorStateList(R.color.ytsdk_detail_status_text);
        }
    }

    @Override
    public ColorStateList getActivityPriceColor() {
        if (canBuy) {
            return mContext.getResources().getColorStateList(R.color.ytm_white);
        } else {
            return mContext.getResources().getColorStateList(R.color.ytsdk_detail_activity_price);
        }
    }

    @Override
    public ColorStateList getDetailOriginalPriceTextColor() {
        if (canBuy) {
            return mContext.getResources().getColorStateList(R.color.ytm_white);
        } else {
            return mContext.getResources().getColorStateList(R.color.ytsdk_detail_original_price_text);
        }
    }

    @Override
    public ColorStateList getDetailOriginalPriceTitleTextColor() {
        if (canBuy) {
            return mContext.getResources().getColorStateList(R.color.ytm_white);
        } else {
            return mContext.getResources().getColorStateList(R.color.ytsdk_detail_original_price_title_text);
        }
    }

    @Override
    public int getBuyTextShadowColor() {
        // TODO Auto-generated method stub
        if (canBuy) {
            return mContext.getResources().getColor(R.color.ytsdk_detail_buy_shadow);

        } else {
            return mContext.getResources().getColor(R.color.ytsdk_detail_buy_shadow_gray);
        }
    }

}
