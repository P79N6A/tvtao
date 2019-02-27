package com.yunos.tvtaobao.newcart.itemview;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taobao.wireless.trade.mcart.sdk.co.biz.GroupPromotion;
import com.taobao.wireless.trade.mcart.sdk.co.biz.ItemLogo;
import com.tvlife.imageloader.core.DisplayImageOptions;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tvtaobao.newcart.R;


/**
 * Created by linmu on 2018/6/13.
 * 标签栏
 */

public class NewShopCartLabelView extends RelativeLayout {

    private ImageView ivTagTvTb;
    private TextView tvTagTvTb;
    // 下载管理器
    private ImageLoaderManager mImageLoaderManager;
    private DisplayImageOptions mImageOptions; // 图片加载的参数设置


    public NewShopCartLabelView(Context context) {
        super(context);
        initView(context);
    }

    public NewShopCartLabelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public NewShopCartLabelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void initView(Context context) {
        View view = inflate(getContext(), R.layout.layout_new_shop_cart_item_label, this);
        view.findViewById(R.id.iv_tag_tv_tb);
        tvTagTvTb = (TextView) findViewById(R.id.tv_tag_tv_tb);
        ivTagTvTb = (ImageView) findViewById(R.id.iv_tag_tv_tb);
        mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(context);
        mImageOptions = new DisplayImageOptions.Builder().cacheOnDisc(true).cacheInMemory(true).build();


    }


    public void setIvTagTvTb(String pic) {
        if (!TextUtils.isEmpty(pic)) {
            mImageLoaderManager.displayImage(pic, ivTagTvTb, mImageOptions);
        }

    }

    public void setTvTagTvTb(String text) {
        if (!TextUtils.isEmpty(text)) {
            tvTagTvTb.setText(text);

        }
    }

    public void setTvTagTvTbColor(String textColor) {
        if (!TextUtils.isEmpty(textColor)) {
            tvTagTvTb.setTextColor(Color.parseColor("#" + textColor));
        } else {
            tvTagTvTb.setTextColor(getResources().getColor(R.color.new_shop_cart_label_txt));
        }
    }

    public void setData(GroupPromotion groupPromotion) {
        AppDebug.e("GroupPromotion",groupPromotion.toString());
        if (groupPromotion == null) {
            tvTagTvTb.setText("");
            return;
        }
        if (!TextUtils.isEmpty(groupPromotion.getTitle())) {
            tvTagTvTb.setVisibility(VISIBLE);
            tvTagTvTb.setText(groupPromotion.getTitle());
        }else {
            tvTagTvTb.setVisibility(GONE);
        }
        if (!TextUtils.isEmpty(groupPromotion.getPic())) {
            ivTagTvTb.setVisibility(VISIBLE);
            mImageLoaderManager.displayImage(groupPromotion.getPic(), ivTagTvTb, mImageOptions);
        }else {
            ivTagTvTb.setVisibility(GONE);
        }


    }

    public void setData(ItemLogo itemLogo) {
        if (itemLogo == null) {
            tvTagTvTb.setText("");
            return;
        }
        ItemLogo.ItemLogoField field = itemLogo.fields;
        if (field != null) {
            if (!TextUtils.isEmpty(field.title)) {
                tvTagTvTb.setText(field.title);
            }
            if (!TextUtils.isEmpty(field.iconUrl)) {
                mImageLoaderManager.displayImage(field.iconUrl, ivTagTvTb, mImageOptions);
            }
        }

    }

    public void setDataTVTb(String pic, String rebate) {
        if (rebate == null) {
            tvTagTvTb.setText("");
            tvTagTvTb.setVisibility(GONE);
        }else {
            tvTagTvTb.setVisibility(VISIBLE);
            tvTagTvTb.setTextColor(getResources().getColor(R.color.ytbv_color_rebate));
            tvTagTvTb.setPadding(getResources().getDimensionPixelSize(R.dimen.dp_4), 0, 0, 0);
            tvTagTvTb.setText(rebate);
        }
        if (!TextUtils.isEmpty(pic)) {
            ivTagTvTb.setVisibility(VISIBLE);
            mImageLoaderManager.displayImage(pic+"", ivTagTvTb, mImageOptions);
        }else {
            ivTagTvTb.setVisibility(GONE);
        }
    }


}
