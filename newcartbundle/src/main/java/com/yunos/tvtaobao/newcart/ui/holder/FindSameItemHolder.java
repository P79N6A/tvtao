package com.yunos.tvtaobao.newcart.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunos.tvtaobao.newcart.R;

/**
 * Created by zhoubo on 2018/7/12.
 * zhoubo on 2018/7/12 10:14
 * describition 找相似ViewHolder
 */

public class FindSameItemHolder extends RecyclerView.ViewHolder {
    public ImageView ivItemDes,ivFindSameStatus,ivRebateIcon,goodlist_grid_item_return_red_packet;
    public TextView tvItemPaid, tvItemGoods, tvItemPrice,tvRebateCoupon;
    public FrameLayout layoutFindSame;
    public RelativeLayout layoutRebate;
    public FindSameItemHolder(View itemView) {
        super(itemView);
        ivItemDes = (ImageView) itemView.findViewById(R.id.iv_item_des);
        tvItemPaid = (TextView) itemView.findViewById(R.id.tv_item_paid_num);
        tvItemGoods = (TextView) itemView.findViewById(R.id.tv_item_goods_des);
        tvItemPrice = (TextView) itemView.findViewById(R.id.tv_item_goods_price);
         ivFindSameStatus = (ImageView) itemView.findViewById(R.id.iv_findsame_status);
        layoutFindSame = (FrameLayout) itemView.findViewById(R.id.layout_find_same);
        tvRebateCoupon = (TextView) itemView.findViewById(R.id.tv_rebate_coupon);
        ivRebateIcon = (ImageView) itemView.findViewById(R.id.iv_rebate_icon);
        layoutRebate = (RelativeLayout) itemView.findViewById(R.id.layout_rebate_findsame);
        goodlist_grid_item_return_red_packet= (ImageView) itemView.findViewById(R.id.goodlist_grid_item_return_red_packet);
    }
}
