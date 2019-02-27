package com.yunos.tvtaobao.tradelink.buildorder.adapter;


import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.tradelink.R;

import java.util.List;

public class GallryListSelectAdapter extends BaseAdapter {

    protected static String TAG = "SelectViewHolderAdapter";
    public static final int LOOPCOUNT_START = 1;
    private Context mContext;
    private List<String> mOptionName;

    private boolean loop = true;

    public GallryListSelectAdapter(Context context) {
        mContext = context;
    }

    /**
     * 设置数据源
     *
     * @param optionName
     */
    public void setOptionName(List<String> optionName) {
        mOptionName = optionName;
        AppDebug.i(TAG, "setOptionName --> mOptionName = " + mOptionName);
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    /**
     * 获取实际的pos值
     *
     * @param position
     * @return
     */
    public int getRealPosition(int position) {
        int realposition = 0;
        if (null != mOptionName && mOptionName.size() > 0) {
            realposition = position % mOptionName.size();
        }
        AppDebug.i(TAG, "getRealPosition  realposition = " + realposition);
        return realposition;
    }


    @Override
    public int getCount() {
        if (!loop)
            return mOptionName == null ? 0 : mOptionName.size();
        int count = 0;
        if (mOptionName != null) {
            count = mOptionName.size();
        }
        if (count > LOOPCOUNT_START) {
            count = Integer.MAX_VALUE;
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        if (mOptionName != null) {
            int pos = getRealPosition(position);
            return mOptionName.get(pos);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return getRealPosition(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new TextView(mContext);
        }

        if (mOptionName != null) {
            TextView tV = (TextView) convertView;
            tV.setText(mOptionName.get(getRealPosition(position)));
            tV.setTextColor(Color.WHITE);
            tV.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.dp_20));

            AppDebug.i(TAG, "getView  text = " + mOptionName.get(getRealPosition(position)));
        }
        return convertView;
    }
}
