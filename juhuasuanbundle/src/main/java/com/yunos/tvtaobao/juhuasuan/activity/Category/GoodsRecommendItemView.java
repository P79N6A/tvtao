package com.yunos.tvtaobao.juhuasuan.activity.Category;


import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.ItemMO;

/**
 * 显示推荐页的商品信息
 * @author tim
 */
public class GoodsRecommendItemView extends LinearLayout {

    //private static final String TAG = "GoodsRecommendItemView";
    private TextView mSellCount;
    private TextView mShowPrice;
    private TextView mOriginalPrice;
    private View mDiscountLayout;
    private TextView mDiscount;
    private TextView mDiscount2;
    private Context mContext;
    private String mSoldString;
    private String mDiscountString;

    public GoodsRecommendItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.jhs_category_goods_recommend_item, this, true);
        initUI(context);
    }

    /**
     * 初始化商品
     * @param context
     */
    private void initUI(Context context) {
        mContext = context;
        mSellCount = (TextView) findViewById(R.id.itemCount_tv);
        mShowPrice = (TextView) findViewById(R.id.activityPrice_tv);
        mOriginalPrice = (TextView) findViewById(R.id.originalPrice_tv);
        mDiscountLayout = findViewById(R.id.discount_layout);
        mDiscount = (TextView) findViewById(R.id.discount_tv1);
        mDiscount2 = (TextView) findViewById(R.id.discount_tv2);
        mSoldString = mContext.getString(R.string.jhs_detail_sold_desc);
        mDiscountString = mContext.getString(R.string.jhs_discount_unit);

    }

    /**
     * 设置商品的数据
     * @param itemData
     */
    public void setGoodsItemData(final ItemMO itemData, int position) {
        if (itemData == null) {
            return;
        }

        if (itemData.isAbleBuy()) {
            mSellCount.setText(mSoldString + itemData.getSoldCount());
            mDiscountLayout.setBackgroundResource(R.drawable.jhs_recommend_img_discount_normal);
        } else if (itemData.isNotStart() && !itemData.isNoStock() && !itemData.isAbleBuy()) {
            //            java.util.Date dt = new Date(itemData.getOnlineStartTime());
            //            String sDateTime = mSimpleDateFormat.format(dt);
            //            mSellCount.setText(mStartSoldString + sDateTime);
            //            mSellCount.setTextColor(0xffffe374);
            mDiscountLayout.setBackgroundResource(R.drawable.jhs_recommend_img_discount_start);
        } else {
            mSellCount.setText(mSoldString + itemData.getSoldCount());
            mDiscountLayout.setBackgroundResource(R.drawable.jhs_recommend_img_discount_disable);
        }
        String discount = null;
        AppDebug.i("aaa", "aaaa itemData.getDiscount() = " + itemData.getDiscount());
        if (null == itemData.getDiscount() || itemData.getDiscount() == Double.NaN) {
            discount = Double.toString(itemData.getActivityPrice() / itemData.getOriginalPrice());
        } else {
            discount = Double.toString(itemData.getDiscount());
        }

        AppDebug.i("aaa", "aaaa discount = " + discount);
        if (discount.indexOf(".") == -1) {
            discount += ".00";
        }
        String str[] = discount.split("\\.");
        mDiscount.setText(str[0]);
        mDiscount2.setText("." + str[1] + mDiscountString);
        mOriginalPrice.setText(Double.toString((itemData.getOriginalPrice() / 100.00)));
        mOriginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        mOriginalPrice.getPaint().setAntiAlias(true);
        mShowPrice.setText(Double.toString(itemData.getActivityPrice() / 100.00));
    }
}
