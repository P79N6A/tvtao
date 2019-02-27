package com.yunos.tvtaobao.takeoutbundle.activity.shophome_wares;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunos.tvtaobao.biz.focus_impl.FocusFakeListView;
import com.yunos.tvtaobao.takeoutbundle.R;
import com.yunos.tvtaobao.takeoutbundle.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by GuoLiDong on 2018/10/20.
 */
public class NoMoreItemVH extends FocusFakeListView.ViewHolder {
    Context context;
    Unbinder unbinder;
    @BindView(R2.id.shop_loading_notice)
    TextView shopLoadingNotice;

    public NoMoreItemVH(Context context, ViewGroup parent) {
        super(LayoutInflater.from(context).inflate(R.layout.no_more_item, parent,false));
        this.context = context;
        unbinder = ButterKnife.bind(this,itemView);
    }

    public void fillWith(Data itemData){
        shopLoadingNotice.setVisibility(View.VISIBLE);
    }

    public static class Data{}
}
