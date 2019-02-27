package com.yunos.tvtaobao.takeoutbundle.adapter;

import android.app.Activity;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.powyin.scroll.adapter.AdapterDelegate;
import com.powyin.scroll.adapter.PowViewHolder;
import com.yunos.tvtaobao.biz.request.bo.TakeOutBag;
import com.yunos.tvtaobao.takeoutbundle.R;

import java.text.DecimalFormat;

/**
 * Created by haoxiang on 2017/12/21. 购物车 item
 */

public class ViewHolderCollection extends PowViewHolder<TakeOutBag.CartItemListBean> {
    private final static DecimalFormat format = new DecimalFormat("¥#.##");
    private TextView good_name;
    private TextView good_count;
    private TextView good_price;
    private TextView good_ori_price;
    private TextView good_sku;
    private StringBuilder stringBuilder = new StringBuilder();

    public ViewHolderCollection(Activity activity, ViewGroup viewGroup) {
        super(activity, viewGroup);
        good_name = findViewById(R.id.good_name);
        good_count = findViewById(R.id.good_count);
        good_price = findViewById(R.id.good_price);
        good_ori_price = findViewById(R.id.good_ori_price);
        good_sku = findViewById(R.id.good_sku);
        good_ori_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }

    @Override
    protected int getItemViewRes() {
        return R.layout.item_takeout_good_search_collection;
    }

    @Override
    public void loadData(AdapterDelegate<? super TakeOutBag.CartItemListBean> multipleAdapter, TakeOutBag.CartItemListBean data, int position) {

        good_name.setText(mData.title);
        good_count.setText(String.valueOf("x" + mData.amount));

        mData.totalPromotionPrice = mData.totalPromotionPrice == 0 ? mData.totalPrice : mData.totalPromotionPrice;

        good_price.setText(format.format((mData.totalPromotionPrice) / 100f));
        good_ori_price.setText(format.format((mData.totalPrice) / 100f));
        if (mData.totalPromotionPrice < mData.totalPrice) {
            good_ori_price.setVisibility(View.GONE);
        } else {
            good_ori_price.setVisibility(View.GONE);
        }

        stringBuilder.setLength(0);
        if (!TextUtils.isEmpty(mData.skuName)) {
            stringBuilder.append(mData.skuName);
            stringBuilder.append(", ");
        }
        for (int i = 0; mData.skuProperties != null && i < mData.skuProperties.size(); i++) {
            TakeOutBag.CartItemListBean.SkuPropertiesBean skuPropertiesBean = mData.skuProperties.get(i);
            String value = skuPropertiesBean.value;
            if(value!=null && value.equals(mData.skuName)){ // 解决sku 重复显示 bug #13793663
                continue;
            }
            if (value != null && value.length() > 0) {
                stringBuilder.append(value);
                stringBuilder.append(", ");
            }
        }
        if (stringBuilder.length() > 1) {
            stringBuilder.setLength(stringBuilder.length() - 2);
            good_sku.setVisibility(View.VISIBLE);
            good_sku.setText(stringBuilder.toString());
        } else {
            good_sku.setVisibility(View.GONE);
        }
    }


}
