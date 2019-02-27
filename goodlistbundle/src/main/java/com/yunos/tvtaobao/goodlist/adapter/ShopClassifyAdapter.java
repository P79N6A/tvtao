/**
 * 
 */
package com.yunos.tvtaobao.goodlist.adapter;


import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.bo.Cat;
import com.yunos.tvtaobao.goodlist.R;
import com.yunos.tvtaobao.goodlist.view.ItemLayoutForShop;

import java.util.ArrayList;

/**
 * @author yunzhong.qyz
 */
public class ShopClassifyAdapter extends BaseAdapter {

    private String TAG = "ShopClassifyAdapter";

    private ArrayList<Cat> mCats;

    private boolean mFirstGetView = true;
    private ItemLayoutForShop mCurrentItemLayoutForShop = null;

    private ItemLayoutForShop mFirstItemLayoutForShop = null;

    private ShopStoreFirstGetviewOverListener mShopStoreFirstGetviewOverListener = null;

    private LayoutInflater mLayoutInflater = null;

    private Context mContext;

    /**
     * @param context
     */
    public ShopClassifyAdapter(Context context) {
        mContext = context;
        mFirstGetView = true;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    /**
     * 设置数据源
     * @param cats
     */
    public void setClassify(ArrayList<Cat> cats) {
        mCats = cats;
    }

    /**
     * 通知更新
     */
    public void onNotifyDataSetChanged() {
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {

        if (mCats != null) {
            return mCats.size();
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(getLayoutID(), null);
        }

        fillView(position, convertView, parent);

        return convertView;
    }

    private void fillView(int position, View convertView, ViewGroup parent) {

        ItemLayoutForShop itemLayoutForShop = (ItemLayoutForShop) convertView;
        if (itemLayoutForShop == null) {
            return;
        }

        if (mCats == null) {
            return;
        }

        if (mCats.isEmpty()) {
            return;
        }

        int catSize = mCats.size();
        int position_Temp = position + 1;
        if (position_Temp > catSize) {
            return;
        }

        Cat cat = mCats.get(position);
        if (cat == null) {
            return;
        }
        TextView classifyView = (TextView) itemLayoutForShop.getItemTextView();
        if (classifyView == null) {
            return;
        }
        classifyView.setText(cat.getName());
        classifyView.setEllipsize(TruncateAt.END);
        //        classifyView.setEllipsize(TruncateAt.);
        classifyView.setSingleLine(true);

        itemLayoutForShop.setCat(cat);
        itemLayoutForShop.setPosition(position);

        AppDebug.i(TAG, "fillView --> itemLayoutForShop = " + itemLayoutForShop + "; position = " + position
                + "; getName = " + cat.getName() + "; parent.getChildCount = " + parent.getChildCount());

        if ((position == 0) && (mFirstGetView)) {
            mFirstItemLayoutForShop = itemLayoutForShop;
        }

        if (mFirstGetView) {
            if (catSize > 1) {
                // 如果菜单个数 大于 1 个， 则 当     position >= 1 取第一个  
                if ((position == 1) && (mShopStoreFirstGetviewOverListener != null)) {
                    mFirstGetView = false;
                    mCurrentItemLayoutForShop = mFirstItemLayoutForShop;
                    mShopStoreFirstGetviewOverListener.onShopStoreFirstGetviewOverHandle(this, mFirstItemLayoutForShop);
                }
            } else {
                // 如果 菜单 个数为 1个： 
                if (mShopStoreFirstGetviewOverListener != null) {
                    mCurrentItemLayoutForShop = mFirstItemLayoutForShop;
                    mShopStoreFirstGetviewOverListener.onShopStoreFirstGetviewOverHandle(this, mFirstItemLayoutForShop);
                }
            }
        }
    }

    private int getLayoutID() {
        return R.layout.ytsdk_shop_activity_classify_item;
    }

    /**
     * 销毁处理
     */
    public void onDestroyAndClear() {
    }

    public ItemLayoutForShop getCurrentItemLayoutForShop() {
        return mCurrentItemLayoutForShop;
    }

    public void setShopStoreFirstGetviewOverListener(ShopStoreFirstGetviewOverListener l) {
        mShopStoreFirstGetviewOverListener = l;
    }

    public interface ShopStoreFirstGetviewOverListener {

        public void onShopStoreFirstGetviewOverHandle(ShopClassifyAdapter shopClassifyAdapter,
                                                      ItemLayoutForShop firstItemLayoutForShop);
    }

}
