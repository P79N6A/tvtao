package com.yunos.tvtaobao.takeoutbundle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yunos.tvtaobao.biz.request.bo.VouchersList;
import com.yunos.tvtaobao.takeoutbundle.R;
import com.yunos.tvtaobao.takeoutbundle.activity.shophome_wares.ShopVouchersHeaderVH;
import com.yunos.tvtaobao.takeoutbundle.activity.shophome_wares.ShopVouchersItemVH;
import com.yunos.tvtaobao.takeoutbundle.listener.OnVouchersItemClickListener;

import java.util.List;

/**
 * @Author：wuhaoteng
 * @Date:2018/12/17
 * @Desc：
 */
public class ShopVouchersAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<VouchersList.ResultBean> vouchersList;
    private OnVouchersItemClickListener itemClickListener;
    private static final int VIEWTYPE_HEADER = 0;
    private static final int VIEWTYPE_ITEM = 1;


    public ShopVouchersAdapter(Context context, List<VouchersList.ResultBean> vouchersList) {
        this.context = context;
        this.vouchersList = vouchersList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEWTYPE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_shop_vouchers_header, parent, false);
            ShopVouchersHeaderVH shopVouchersHeaderVH = new ShopVouchersHeaderVH(context, view);
            return shopVouchersHeaderVH;
        } else if (viewType == VIEWTYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_shop_vouchers, parent, false);
            ShopVouchersItemVH shopVouchersItemVH = new ShopVouchersItemVH(context, view);
            return shopVouchersItemVH;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ShopVouchersItemVH) {
            ((ShopVouchersItemVH) holder).bindView(itemClickListener, vouchersList.get(position - 1), position);
        }
    }

    @Override
    public int getItemCount() {
        return vouchersList.size() + 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEWTYPE_HEADER;
        } else {
            return VIEWTYPE_ITEM;
        }
    }

    public void setItemClickListener(OnVouchersItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
