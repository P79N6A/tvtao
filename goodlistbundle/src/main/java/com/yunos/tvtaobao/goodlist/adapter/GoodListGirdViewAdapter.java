/**
 *
 */
package com.yunos.tvtaobao.goodlist.adapter;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.adapter.TabGoodsBaseAdapter;
import com.yunos.tvtaobao.biz.request.bo.SearchedGoods;
import com.yunos.tvtaobao.goodlist.GoodListFocuseUnit.VISUALCONFIG;
import com.yunos.tvtaobao.goodlist.GoodListImageHandle;
import com.yunos.tvtaobao.goodlist.GoodListImageHandle.InfoDiaplayDrawable;
import com.yunos.tvtaobao.goodlist.R;
import com.yunos.tvtaobao.goodlist.view.GoodListItemFrameLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GoodListGirdViewAdapter extends TabGoodsBaseAdapter {

    public static final String TAG = "GoodListGirdViewAdapter";

    private LayoutInflater mInflater = null;

    private GoodListImageHandle mGoodListImageHandle = null;

    private ArrayList<SearchedGoods> mGoodsArrayList = null;

    private String mFrom;

    public void setFrom(String from) {
        this.mFrom = from;
    }

    public GoodListGirdViewAdapter(Context context, boolean haveHeaderView) {
        super(context, haveHeaderView);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        int count = 0;
        if (mGoodsArrayList != null) {
            count = mGoodsArrayList.size();
        }
        count = Math.max(count, 0);
        AppDebug.i(TAG, "getCount   mTabKey = " + mTabKey + ";  count = " + count);
        return count;
    }

    @Override
    public Object getItem(int position) {
        return onGetGoods(mTabKey, position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 设置图片处理工具
     *
     * @param goodListImageHandle
     */
    public void setGoodListImageHandle(GoodListImageHandle goodListImageHandle) {
        mGoodListImageHandle = goodListImageHandle;
    }

    /**
     * 设置此适配器所对应的数据列表
     *
     * @param goodsArrayList
     */
    public void onSetGoodsArrayList(ArrayList<SearchedGoods> goodsArrayList) {
        mGoodsArrayList = goodsArrayList;
    }

    /**
     * 根据 position 获取商品信息
     *
     * @param position
     * @return
     */
    public SearchedGoods onGetGoods(String tabkey, int position) {
        synchronized (mGoodsArrayList) {
            if (mGoodsArrayList == null || mGoodsArrayList.isEmpty()) {
                return null;
            }
            if (position < 0) {
                return null;
            }

            int listSize = mGoodsArrayList.size();
            if (position > listSize - 1) {
                return null;
            }

            SearchedGoods goodsTmail = null;
            try {
                goodsTmail = mGoodsArrayList.get(position);
            } catch (Exception e) {
                goodsTmail = null;
            }

            return goodsTmail;
        }
    }

    @Override
    public int getColumnsCounts() {
        return VISUALCONFIG.COLUMNS_COUNT;
    }

    @Override
    public Drawable getDefaultDisplayPicture() {
        if (mGoodListImageHandle != null) {
            return mGoodListImageHandle.getDefultDrawable();
        }
        return null;
    }

    @Override
    public Drawable getInfoDiaplayDrawable(String tabkey, int position) {
        InfoDiaplayDrawable drawable = null;
        if (mGoodListImageHandle != null) {
            drawable = mGoodListImageHandle.onGetInfoDiaplayDrawable(onGetGoods(tabkey, position));
        }
        return drawable;
    }

    @Override
    public String getNetPicUrl(String tabkey, int position) {

        SearchedGoods mItemGoods = onGetGoods(tabkey, position);
        if (mItemGoods == null) {
            return null;
        }

        String picUrl = mItemGoods.getImgUrl();
        picUrl = picUrl + "_230x230.jpg";
        if (mGoodListImageHandle != null) {
            picUrl = mItemGoods.getImgUrl() + mGoodListImageHandle.onGetGoodsPicSize();
        }

        return picUrl;
    }

    @Override
    public View getFillView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.ytsdk_goodlist_activity_item_framelayout, null);
        }

        if (convertView instanceof GoodListItemFrameLayout) {
            GoodListItemFrameLayout goodListItemFrameLayout = (GoodListItemFrameLayout) convertView;
            goodListItemFrameLayout.onInitVariableValue();
            if (mGoodsArrayList != null) {
                int size = mGoodsArrayList.size();
                if ((position < size) && (position >= 0)) {
                    SearchedGoods goods = mGoodsArrayList.get(position);
                    goodListItemFrameLayout.onSetItemGoods(goods);
                    if (SearchedGoods.TYPE_ZTC.equals(goods.getType())) {
                        Map<String, String> props = new HashMap<>();
                        props.put("name", getGoodsTitle(mTabKey, position));
                        props.put("uuid", CloudUUIDWrapper.getCloudUUID());
                        props.put("from_channel", mFrom);
                        Utils.utCustomHit("EXPORE_BUTTON_Z_P_Goods_" + position, props);
                    }

                }
            }
        }
        return convertView;
    }

    @Override
    public String getGoodsTitle(String tabkey, int position) {
        SearchedGoods goods = onGetGoods(tabkey, position);
        if (goods != null) {
            return goods.getTitle();
        }
        return null;
    }
}
