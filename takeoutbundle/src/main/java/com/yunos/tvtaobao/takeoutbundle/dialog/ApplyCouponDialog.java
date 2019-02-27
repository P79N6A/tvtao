package com.yunos.tvtaobao.takeoutbundle.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tvlife.imageloader.core.ImageLoader;
import com.tvlife.imageloader.core.assist.FailReason;
import com.tvlife.imageloader.core.listener.ImageLoadingListener;
import com.yunos.tvtaobao.biz.request.bo.TakeoutApplyCoupon;
import com.yunos.tvtaobao.biz.request.item.TakeOutCouponStyle;
import com.yunos.tvtaobao.biz.util.StringUtil;
import com.yunos.tvtaobao.takeoutbundle.R;
import com.yunos.tvtaobao.takeoutbundle.adapter.ApplyCouponAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author：wuhaoteng
 * @Date:2018/12/25
 * @Desc：
 */
public class ApplyCouponDialog extends Dialog {
    private Context context;
    private RecyclerView couponRv;
    private ImageView headerImg;
    private ImageView linkButtonImg;
    private TextView totalAmountTxt;
    private List<TakeoutApplyCoupon.CouponsBean> coupons;
    private ApplyCouponAdapter adapter;

    public ApplyCouponDialog(Context context) {
        super(context, R.style.take_out_apply_conpon_dialog);
        this.context = context;
        initView(context);
    }

    private void initView(Context context) {
        setContentView(R.layout.dilaog_takeout_apply_coupon);
        couponRv = (RecyclerView) findViewById(R.id.rv_coupon);
        headerImg = (ImageView) findViewById(R.id.img_header);
        linkButtonImg = (ImageView) findViewById(R.id.img_link_button);
        totalAmountTxt = (TextView) findViewById(R.id.txt_total_amount);
        coupons = new ArrayList<>();
        adapter = new ApplyCouponAdapter(context, coupons);
        couponRv.setLayoutManager(new LinearLayoutManager(context));
        couponRv.setAdapter(adapter);
    }

    /**
     * 初始化红包数据
     */
    public void setData(List<TakeoutApplyCoupon.CouponsBean> coupons, TakeOutCouponStyle style) {
        if (coupons == null) {
            return;
        }
        setRvHeight(coupons.size());
        setStyle(style);

        float amount = 0f;
        for (TakeoutApplyCoupon.CouponsBean couponsBean : coupons) {
            String subAmount = couponsBean.getAmount();
            float parseFloat = Float.parseFloat(subAmount);
            amount = amount + parseFloat;
        }

        totalAmountTxt.setText(String.valueOf(amount/100));

        //刷新recyclerview
        this.coupons.clear();
        this.coupons.addAll(coupons);
        adapter.notifyDataSetChanged();
    }

    /**
     * 设置红包样式
     */
    private void setStyle(TakeOutCouponStyle style) {
        String bgColor = style.getBgColor();
        String bgPic = style.getBgPic();
        String buttonPic = style.getButtonPic();
        final String buttonUrl = style.getButtonUrl();
        ImageLoader.getInstance().loadImage(bgPic, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                headerImg.setImageDrawable(context.getResources().getDrawable(R.drawable.bg_take_out_apply_coupon_header));
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                headerImg.setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

        if (!StringUtil.isEmpty(buttonPic) && !StringUtil.isEmpty(buttonUrl)) {
            ImageLoader.getInstance().displayImage(buttonPic, linkButtonImg);
            linkButtonImg.setVisibility(View.VISIBLE);
            linkButtonImg.setFocusable(true);
            linkButtonImg.requestFocus();
            linkButtonImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setData(Uri.parse(buttonUrl));
                    context.startActivity(intent);
                }
            });
        } else {
            linkButtonImg.setVisibility(View.GONE);
        }

        GradientDrawable drawable = (GradientDrawable) couponRv.getBackground();
        drawable.setColor(Color.parseColor(bgColor));
        couponRv.setBackgroundDrawable(drawable);

    }

    /**
     * 设置红包列表高度
     * 列表高度随红包个数动态变化
     */
    private void setRvHeight(int size) {
        if (size == 1) {
            int dp_168 = (int) context.getResources().getDimension(R.dimen.dp_168);
            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp_168);
            couponRv.setLayoutParams(lp);
        } else if (size == 2) {
            int dp_314 = (int) context.getResources().getDimension(R.dimen.dp_314);
            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp_314);
            couponRv.setLayoutParams(lp);
        } else {
            int dp_370 = (int) context.getResources().getDimension(R.dimen.dp_370);
            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp_370);
            couponRv.setLayoutParams(lp);
        }
    }


    /**
     * 监听上下键，红包列表滑动指定距离
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int distance = (int) context.getResources().getDimension(R.dimen.dp_264);
            int dy = 0;
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    dy = distance;
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    dy = -distance;
                    break;

                default:
                    break;
            }

            couponRv.scrollBy(0, dy);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void show() {
        super.show();
        /**
         * 设置宽度全屏，要设置在show的后面
         */
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        getWindow().getDecorView().setPadding(0, 0, 0, 0);

        getWindow().setAttributes(layoutParams);
    }
}

