package com.yunos.tvtaobao.juhuasuan.activity.Category;


import android.content.Context;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.ItemMO;

import java.text.SimpleDateFormat;

/**
 * 一般的商品显示信息
 * 1.本商品展示信息
 * 2.带有是否可以前后聚焦的信息
 * 3.商品的图片跟其它信息是分开加载，为提高动画的速度
 * @author tim
 */
public class GoodsNormalItemViewInfo extends LinearLayout {

    private static final String TAG = "GoodsNormalItemViewInfo";
    private Context mContext;//上下文件
    private TextView mSellCount;
    private TextView mShowPrice;
    private TextView mOriginalPrice;
    private TextView mDiscount;
    private ImageView mSoldOverView;
    private int mDiscountTextSmallSize;
    private int mDiscountTextBigSize;
    private SimpleDateFormat mSimpleDateFormat;
    private String mSoldString;
    private String mStartSoldString;
    private String mDiscountString;
    private AbsoluteSizeSpan mDiscountTextBigSpan;
    private AbsoluteSizeSpan mDiscountTextSmallSpan;
    private AbsoluteSizeSpan mInfoTextBigSpan;
    private AbsoluteSizeSpan mInfoTextSmallSpan;

    public GoodsNormalItemViewInfo(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.jhs_category_goods_normal_item_info, this, true);
        initUI(context);
    }

    /**
     * 初始化商品
     * @param context
     */
    private void initUI(Context context) {
        mContext = context;
        mDiscountTextSmallSize = mContext.getResources().getDimensionPixelSize(R.dimen.jhs_page_goods_text_normal_size);
        mDiscountTextBigSize = mContext.getResources().getDimensionPixelSize(R.dimen.jhs_page_goods_text_middle_size);
        int infoSmallSize = mContext.getResources()
                .getDimensionPixelSize(R.dimen.jhs_page_goods_text_infor_normal_size);
        int infoBigSize = mContext.getResources().getDimensionPixelSize(R.dimen.jhs_page_goods_text_infor_big_size);
        mInfoTextBigSpan = new AbsoluteSizeSpan(infoBigSize);
        mInfoTextSmallSpan = new AbsoluteSizeSpan(infoSmallSize);
        mSellCount = (TextView) findViewById(R.id.itemCount_tv);
        mShowPrice = (TextView) findViewById(R.id.activityPrice_tv);
        mOriginalPrice = (TextView) findViewById(R.id.originalPrice_tv);
        mDiscount = (TextView) findViewById(R.id.discount_tv);
        mSoldOverView = (ImageView) findViewById(R.id.sold_image);
        mSimpleDateFormat = new SimpleDateFormat("MM月dd日HH:mm");
        mSoldString = mContext.getString(R.string.jhs_detail_sold_desc);
        mStartSoldString = mContext.getString(R.string.jhs_start_sold);
        mDiscountString = mContext.getString(R.string.jhs_discount_unit);
    }

    /**
     * 设置商品的数据
     * @param itemData
     */
    public void setGoodsItemData(final ItemMO itemData) {
        if (itemData == null) {
            return;
        }

        if (itemData.isAbleBuy()) {
            mSellCount.setText(mSoldString + itemData.getSoldCount());
            mDiscount.setBackgroundResource(R.drawable.jhs_img_discount_normal);
        } else if (itemData.isNotStart() && !itemData.isNoStock() && !itemData.isAbleBuy()) {
            //            java.util.Date dt = new Date(itemData.getOnlineStartTime());
            //            String sDateTime = mSimpleDateFormat.format(dt);
            //            mSellCount.setText(mStartSoldString + sDateTime);
            //            mSellCount.setTextColor(0xffffe374);
            mDiscount.setBackgroundResource(R.drawable.jhs_img_discount_start);
        } else {
            mSellCount.setText(mSoldString + itemData.getSoldCount());
            mDiscount.setBackgroundResource(R.drawable.jhs_img_discount_disable);
        }

        String discount = null;
        if (null == itemData.getDiscount() || itemData.getDiscount() == Double.NaN) {
            discount = Double.toString(itemData.getActivityPrice() / itemData.getOriginalPrice());
        } else {
            discount = Double.toString(itemData.getDiscount());
        }
        AppDebug.i("aaa", "aaaa discount = " + discount);
        if (discount.indexOf(".") == -1) {
            discount += ".00";
        }
        //String str[] = discount.split("\\.");
        //String discountText = str[0] + "." + str[1] + mDiscountString + " ";
        if (discount.equals("10.0")||discount.equals("10.00")){
            mDiscount.setVisibility(INVISIBLE);
        }else{
            mDiscount.setVisibility(VISIBLE);
            String str[] = discount.split("\\.");
            String discountText = str[0] + "." + str[1] + mDiscountString + " ";
            SpannableStringBuilder style = new SpannableStringBuilder(discountText);
            style.setSpan(mDiscountTextBigSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            style.setSpan(mDiscountTextSmallSpan, 1, discountText.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            mDiscount.setText(style);
        }

        mOriginalPrice.setText(Double.toString((itemData.getOriginalPrice() / 100.0)));
        mOriginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        mOriginalPrice.getPaint().setAntiAlias(true);
        String showPrice = getResources().getString(R.string.jhs_dollar_sign)
                + Double.toString(itemData.getActivityPrice() / 100.0) + " ";
        SpannableStringBuilder showPriceStyle = new SpannableStringBuilder(showPrice);
        showPriceStyle.setSpan(mInfoTextSmallSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        showPriceStyle.setSpan(mInfoTextBigSpan, 1, showPrice.length() - 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        showPriceStyle.setSpan(mInfoTextSmallSpan, showPrice.length() - 1, showPrice.length(),
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        mShowPrice.setText(showPriceStyle);

        try {
            if (itemData.isNotStart()) {
                mSoldOverView.setImageResource(R.drawable.jhs_not_start);
                mSoldOverView.setVisibility(View.VISIBLE);
                mShowPrice.setTextColor(mContext.getResources().getColor(R.color.jhs_active_price_start));
            } else if (itemData.isNoStock()) {
                mSoldOverView.setImageResource(R.drawable.jhs_sold_over);
                mSoldOverView.setVisibility(View.VISIBLE);
                mShowPrice.setTextColor(mContext.getResources().getColor(R.color.jhs_active_price_soldover));
            } else if (itemData.isOver()) {
                mSoldOverView.setImageResource(R.drawable.jhs_over);
                mSoldOverView.setVisibility(View.VISIBLE);
                mShowPrice.setTextColor(mContext.getResources().getColor(R.color.jhs_active_price_soldover));
            } else {
                mSoldOverView.setVisibility(View.GONE);
                mShowPrice.setTextColor(mContext.getResources().getColor(R.color.jhs_text_red));
            }
        } catch (Exception e) {
            e.printStackTrace(); // color有一定的概率会导致NotFoundException: Resource错误原因未明，所以先catch错误
        }
    }

}
