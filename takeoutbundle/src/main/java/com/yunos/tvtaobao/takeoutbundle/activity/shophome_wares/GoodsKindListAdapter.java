package com.yunos.tvtaobao.takeoutbundle.activity.shophome_wares;

import android.content.Context;
import android.view.ViewGroup;

import com.yunos.tvtaobao.biz.focus_impl.FakeListView;
import com.yunos.tvtaobao.biz.focus_impl.FocusFakeListView;
import com.yunos.tvtaobao.biz.request.bo.ShopDetailData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GuoLiDong on 2018/10/21.
 */
public class GoodsKindListAdapter extends FakeListView.Adapter {
    private static final String TAG = GoodsKindListAdapter.class.getSimpleName();

    Context context;
    List<GoodKindVH.Data> list = new ArrayList<>();

    public GoodsKindListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public FakeListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GoodKindVH(context, parent);
    }

    @Override
    public void onBindViewHolder(FakeListView.ViewHolder holder, int position) {
        if (holder instanceof GoodKindVH && list.get(position) instanceof GoodKindVH.Data) {
            ((GoodKindVH) holder).fillWith(list.get(position));
        } else {
            throw new RuntimeException(TAG + " holder and data not fit ! ");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public ShopDetailData.ItemGenreWithItemsListBean getDataAt(int position) {
        if (position >= 0 && position < list.size()) {
            return list.get(position).data;
        }
        return null;
    }

    public void hintOnPos(int newPos, int oldPos) {
        if (newPos >= 0 && newPos < list.size()
                && oldPos >= 0 && oldPos < list.size()) {
            if (oldPos != newPos) {
                GoodKindVH.Data tmp = list.get(newPos);
                if (tmp != null && tmp.data != null) {
                    tmp.data.focusStatus = ShopDetailData.ItemGenreWithItemsListBean.FocusStatus.TEMPLETE;
                    notifyItemChanged(newPos);
                }

                tmp = list.get(oldPos);
                if (tmp != null && tmp.data != null) {
                    tmp.data.focusStatus = ShopDetailData.ItemGenreWithItemsListBean.FocusStatus.NO;
                    notifyItemChanged(oldPos);
                }
            } else {
                // 选中当前的就行了
                GoodKindVH.Data tmp = list.get(newPos);
                if (tmp != null && tmp.data != null) {
                    tmp.data.focusStatus = ShopDetailData.ItemGenreWithItemsListBean.FocusStatus.TEMPLETE;
                    notifyItemChanged(newPos);
                }
            }
        }
    }

    public void loadData(List<ShopDetailData.ItemGenreWithItemsListBean> itemGenreWithItemsList
            , final FocusFakeListView recyclerView) {
        final List<GoodKindVH.Data> tmpList = new ArrayList<>();
        for (int i = 0; itemGenreWithItemsList != null && i < itemGenreWithItemsList.size(); i++) {
            final GoodKindVH.Data tmp = new GoodKindVH.Data();
            tmp.data = itemGenreWithItemsList.get(i);
            tmpList.add(tmp);
        }
        if (!recyclerView.isNotifyDoing()
                && !recyclerView.isScrollDoing()){
            list = tmpList;
            notifyDataSetChanged();
        }
    }
}
