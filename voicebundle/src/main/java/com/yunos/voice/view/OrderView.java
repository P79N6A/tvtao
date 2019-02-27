package com.yunos.voice.view;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tvtaobao.voicesdk.utils.LogPrint;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.voice.R;
import com.yunos.voice.activity.CreateOrderActivity;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;

/**
 * Created by pan on 2017/8/11.
 */

public class OrderView {
    private final String TAG = "OrderView";
    private LinearLayout mLayout, mAddressLayout,mRebateInfoLayout;
    private RelativeLayout mOrderLayout;
    private TextView mUserInfo, mAreaInfo, mTitle, mSkuInfo, mPrice, mPostage, mTotalPrice,mRebateTxt;
    private RoundImageView mProductImg;
    private ImageView mHeadImg;

    private WeakReference<CreateOrderActivity> mWeakReference;

    private DecimalFormat format;

    public OrderView(WeakReference<CreateOrderActivity> weakReference) {
        this.mWeakReference = weakReference;

        initView();
        format = new DecimalFormat("0.##");
    }

    private void initView() {
        if (mWeakReference != null && mWeakReference.get() != null) {
            CreateOrderActivity mActivity = mWeakReference.get();
            mOrderLayout = mActivity.findViewById(R.id.order_layout);
            mHeadImg = mActivity.findViewById(R.id.voice_chat_head);
            mLayout = mActivity.findViewById(R.id.order_info_layout);
            mAddressLayout = mActivity.findViewById(R.id.order_info_address_layout);
            mUserInfo = mActivity.findViewById(R.id.order_info_userinfo);
            mAreaInfo = mActivity.findViewById(R.id.order_info_areainfo);
            mTitle = mActivity.findViewById(R.id.order_info_title);
            mSkuInfo = mActivity.findViewById(R.id.order_info_skuinfo);
            mPrice = mActivity.findViewById(R.id.order_info_price);
            mPostage = mActivity.findViewById(R.id.order_info_postage);
            mTotalPrice = mActivity.findViewById(R.id.order_info_total_price);
            mProductImg = mActivity.findViewById(R.id.order_info_total_image);
            mRebateInfoLayout = mActivity.findViewById(R.id.layout_rebate_info);
            mRebateTxt = mActivity.findViewById(R.id.txt_rebate);
            mProductImg.setRound(true, true, true, true);
        }
    }

    public void setRebate(String rebate){
        if(mRebateInfoLayout!=null&&mRebateTxt!=null){
            if(!TextUtils.isEmpty(rebate)){
                mRebateInfoLayout.setVisibility(View.VISIBLE);
                mRebateTxt.setText("预估" + " ¥ " + rebate);
            }else {
                mRebateInfoLayout.setVisibility(View.GONE);
            }
        }
    }

    public void addData(String userInfo, String areaText, String productName, String picUrl, String skuInfo,
                        String price, String postage) {
        if (TextUtils.isEmpty(userInfo) && TextUtils.isEmpty(areaText)) {
            mAddressLayout.setVisibility(View.GONE);
        } else {
            mUserInfo.setText(userInfo);
            mAreaInfo.setText(areaText);
        }

        mTitle.setText(productName);
        if (TextUtils.isEmpty(skuInfo)) {
            mSkuInfo.setVisibility(View.GONE);
        } else {
            mSkuInfo.setVisibility(View.VISIBLE);
            mSkuInfo.setText(skuInfo.replace(":","："));
        }

        if (mProductImg.getVisibility() == View.GONE)
            mProductImg.setVisibility(View.VISIBLE);

        ImageLoaderManager.getImageLoaderManager(mWeakReference.get()).displayImage(picUrl, mProductImg);

        Double _price = Double.parseDouble(price);
        Double _postage = Double.parseDouble(postage);
        if (!TextUtils.isEmpty(price)) {
            mPrice.setText("单价：" + format.format(_price) + "元");
        } else {
            mPrice.setText("单价：0元");
        }

        if (!TextUtils.isEmpty(postage) && Double.parseDouble(postage) > 0) {
            mPostage.setText("邮费：" + format.format(_postage) + "元");
        } else {
            mPostage.setText("邮费：包邮");
        }

        if (!TextUtils.isEmpty(price)) {
            mTotalPrice.setText("¥ " + format.format( _price + _postage ));
        } else {
            mTotalPrice.setText("¥ " +"0.00");
        }
        mLayout.setVisibility(View.VISIBLE);

    }

    public void hiddenOrderInfo() {
        if (mLayout.getVisibility() == View.VISIBLE) {
            mLayout.setVisibility(View.GONE);
        }
    }

    public void showBackGround() {
        mOrderLayout.setBackgroundResource(R.drawable.bg_full_screen_search);
        mHeadImg.setVisibility(View.VISIBLE);
    }
}
