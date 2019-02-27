package com.yunos.tvtaobao.takeoutbundle.activity.shophome_wares;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import com.yunos.tvtaobao.biz.focus_impl.FocusFakeListView;
import com.yunos.tvtaobao.biz.request.bo.ItemListBean;
import com.yunos.tvtaobao.biz.request.bo.ShopDetailData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GuoLiDong on 2018/10/20.
 */
public class ShopGoodsListAdapter extends FocusFakeListView.Adapter {
    private static final String TAG = ShopGoodsListAdapter.class.getSimpleName();
    private List<Object> dataList = new ArrayList<>();
    private Context context;

    public ShopGoodsListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public FocusFakeListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return new NoMoreItemVH(context, parent);
        } else if (viewType == 2) {
            return new ShopInfoItemVH(context, parent);
        } else if (viewType == 3) {
            return new ShopGoodItemVH(context, parent);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(FocusFakeListView.ViewHolder holder, int position) {
        if (holder != null) {
            if (holder instanceof NoMoreItemVH) {
                if (dataList.get(position) instanceof NoMoreItemVH.Data) {
                    ((NoMoreItemVH) holder).fillWith((NoMoreItemVH.Data) dataList.get(position));
                } else {
                    throw new RuntimeException(TAG + " data type is incorrect !!!");
                }
            } else if (holder instanceof ShopInfoItemVH) {
                if (dataList.get(position) instanceof ShopDetailData) {
                    ((ShopInfoItemVH) holder).fillWith((ShopDetailData) dataList.get(position));
                } else {
                    throw new RuntimeException(TAG + " data type is incorrect !!!");
                }
            } else if (holder instanceof ShopGoodItemVH) {
                if (dataList.get(position) instanceof ItemListBean) {
                    ((ShopGoodItemVH) holder).fillWith((ItemListBean) dataList.get(position));
                } else {
                    throw new RuntimeException(TAG + " data type is incorrect !!!");
                }
            } else {
                throw new RuntimeException(TAG + " undefine view holder !!!");
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (dataList.get(position) instanceof NoMoreItemVH.Data) {
            return 1;
        } else if (dataList.get(position) instanceof ShopDetailData) {
            return 2;
        } else if (dataList.get(position) instanceof ItemListBean) {
            return 3;
        }
        throw new RuntimeException("undefine view type !! ");
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void loadData(ShopDetailData shopDetailData
            , List<ItemListBean> itemList
            , boolean isLastKind) {
        final List<Object> tmpList = new ArrayList<>();
        if (shopDetailData != null) {
            tmpList.add(shopDetailData);
        }
        tmpList.addAll(itemList);
        if (isLastKind) {
            tmpList.add(new NoMoreItemVH.Data());
        }
        dataList = tmpList;
        notifyDataSetChanged();
    }
}
