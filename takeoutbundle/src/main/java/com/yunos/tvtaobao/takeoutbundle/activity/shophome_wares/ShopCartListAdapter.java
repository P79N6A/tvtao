package com.yunos.tvtaobao.takeoutbundle.activity.shophome_wares;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.yunos.tvtaobao.biz.focus_impl.FakeListView;
import com.yunos.tvtaobao.biz.focus_impl.FocusFakeListView;
import com.yunos.tvtaobao.biz.request.bo.TakeOutBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by GuoLiDong on 2018/10/26.
 */
public class ShopCartListAdapter extends FakeListView.Adapter {

    public  enum ITEM_TYPE {
        ITEM_TYPE_NORMOL,
        ITEM_TYPE_BUTTO
    }
    List<TakeOutBag.CartItemListBean> dataList = new ArrayList<>();

    Context context;

    public ShopCartListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public FakeListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == ITEM_TYPE.ITEM_TYPE_NORMOL.ordinal()){
            return new ShopCartItemVH(context, parent);
        }else {
            return new ShopCartItemButtomVH(context, parent);
        }
    }

    @Override
    public void onBindViewHolder(FakeListView.ViewHolder holder, int position) {
        if (holder instanceof ShopCartItemVH) {
            ((ShopCartItemVH) holder).loadData(dataList.get(position));
        }
    }


    @Override
    public int getItemCount() {
        return dataList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==dataList.size()){
            return ITEM_TYPE.ITEM_TYPE_BUTTO.ordinal();
        }else {
            return ITEM_TYPE.ITEM_TYPE_NORMOL.ordinal();

        }
    }

    public void clear() {
        dataList.clear();
        notifyDataSetChanged();
    }

    public void loadData(List<TakeOutBag.CartItemListBean> cartItemList) {
        if (cartItemList != null) {
            //Collections.reverse(cartItemList);
            dataList = cartItemList;
            notifyDataSetChanged();
        }
    }

    public List<TakeOutBag.CartItemListBean> getDataList() {
        return dataList;
    }
}
