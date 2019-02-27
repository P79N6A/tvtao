package com.yunos.tvtaobao.newcart.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunos.tvtaobao.newcart.R;

/**
 * Created by zhoubo on 2018/7/16.
 * zhoubo on 2018/7/16 10:12
 * describition 找相似顶部
 */

public class FindSameHeaderHolder extends RecyclerView.ViewHolder{

    //找相似物品描述|价格描述
    public TextView tvFinsSameDes,tvFindSamePrice;

    //找相似物品图片描述
    public ImageView ivFindSameDes;

    public FindSameHeaderHolder(View itemView) {
        super(itemView);
        tvFinsSameDes = (TextView) itemView.findViewById(R.id.tv_findsame_des);
        tvFindSamePrice = (TextView) itemView.findViewById(R.id.tv_findsame_price);
        ivFindSameDes = (ImageView) itemView.findViewById(R.id.iv_findsame_basic);
    }
}
