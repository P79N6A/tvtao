package com.yunos.tvtaobao.takeoutbundle.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yunos.tvtaobao.biz.request.bo.TakeoutApplyCoupon;
import com.yunos.tvtaobao.takeoutbundle.R;
import com.yunos.tvtaobao.takeoutbundle.activity.shophome_wares.ApplyCouponHolder;

import java.util.List;

/**
 * @Author：wuhaoteng
 * @Date:2018/12/25
 * @Desc：
 */
public class ApplyCouponAdapter extends RecyclerView.Adapter<ApplyCouponHolder> {
    private Context context;
    private List<TakeoutApplyCoupon.CouponsBean> coupons;
    private static final int TYPE_BOTTOM = 0x00000001;
    private static final int TYPE_NORMAL = 0x00000002;

    public ApplyCouponAdapter(Context context, List<TakeoutApplyCoupon.CouponsBean> coupons) {
        this.context = context;
        this.coupons = coupons;
    }

    @NonNull
    @Override
    public ApplyCouponHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == TYPE_BOTTOM) {
            view = LayoutInflater.from(context).inflate(R.layout.item_take_out_apply_coupon_bottom, viewGroup, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_take_out_apply_coupon, viewGroup, false);
        }
        ApplyCouponHolder applyCouponHolder = new ApplyCouponHolder(view);
        return applyCouponHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ApplyCouponHolder applyCouponHolder, int position) {
        applyCouponHolder.bindView(coupons.get(position));
    }

    @Override
    public int getItemCount() {
        return coupons.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == coupons.size() - 1) {
            return TYPE_BOTTOM;
        } else {
            return TYPE_NORMAL;
        }
    }
}
