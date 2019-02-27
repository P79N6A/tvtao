package com.yunos.tv.app.widget.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

public abstract class LocationAdapter<T> extends BaseAdapter {

    protected Context mContext;
    protected LayoutInflater mInflater;
    protected T mLocationData = null;

    public LocationAdapter(Context context, T locationData) {
        mContext = context;
        if (mContext == null) {
            throw new NullPointerException("SpecificLocationAdapter context must not be null ");
        }
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mLocationData = locationData;
        if (mLocationData == null) {
            throw new NullPointerException("SpecificLocationAdapter location data must not be null ");
        }
    }

    public abstract LocationItem getItem(int position);

}
