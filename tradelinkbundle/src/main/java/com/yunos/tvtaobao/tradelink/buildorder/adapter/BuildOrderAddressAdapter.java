package com.yunos.tvtaobao.tradelink.buildorder.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import com.yunos.tv.app.widget.focus.FocusGallery;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.bo.Address;
import com.yunos.tvtaobao.tradelink.R;

import java.util.List;

public class BuildOrderAddressAdapter extends BaseAdapter {

    protected final String TAG = "BuildOrderAddressAdapter";
    protected final int LOOPCOUNT_START = 1;
    private Context mContext;
    private List<Address> addresses;

    public BuildOrderAddressAdapter(Context context, List<Address> addresses) {
        mContext = context;
        this.addresses = addresses;
    }
    
    public void setData(List<Address> addresses) {
        this.addresses = addresses;
    }

    @Override
    public int getCount() {  
        int count = 0;
        if (addresses != null) {
            count = addresses.size();
        }
        if (count > LOOPCOUNT_START) {
            count = Integer.MAX_VALUE;
        }
        return count;
    }

    @Override
    public Address getItem(int position) {
        if (addresses != null) {
            int pos = getRealPosition(position);
            return addresses.get(pos);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewValue = getItemViewType(position);
        if (viewValue == ViewType.NORMAL_VIEW.getValue()) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.ytm_buildorder_address_item_layout, null);
                AddressHolder addressHolder = new AddressHolder(convertView);
                convertView.setTag(addressHolder);
            }
            AddressHolder holder = (AddressHolder) convertView.getTag();
            if (getAddressesSize() > 0) {
                Address address = addresses.get(getRealPosition(position));
                if (address != null) {
                  holder.addressName.setText(address.getFullName() + " " + address.getMobile());
                  holder.addressDetail.setText(address.getAddressDetail());
                  holder.addressCity.setText(address.getProvince() + " " + address.getCity() + " " + address.getArea());
               }
            }
        } else if (viewValue == ViewType.EDITADDRESS_VIEW.getValue()) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.ytm_buildorder_address_edit_layout, null);
            }
        }

        // 占满父view
        convertView
                .setLayoutParams(new FocusGallery.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if (getRealPosition(position) >= getAddressesSize() - 1) {
            // 只有最后面一个模板才是地址编辑
            return ViewType.EDITADDRESS_VIEW.getValue();
        }
        return ViewType.NORMAL_VIEW.getValue();
    }

    @Override
    public int getViewTypeCount() {
        return ViewType.size();
    }
    
    
    /**
     * 获取实际的pos值
     * @param position
     * @return
     */
    public int getRealPosition(int position) {
        int realposition = 0;
        if (addresses != null && addresses.size() > 0) {
            realposition = position % addresses.size();
        }
        AppDebug.i(TAG, "getRealPosition  realposition = " + realposition);
        return realposition;
    }
    
    
    /**
     * 获取  addresses 的大小
     * @return
     */
    public int getAddressesSize(){
        int size = 0;
        if (addresses != null) {
            size = addresses.size();
        }
        AppDebug.i(TAG, "getAddressesSize  size = " + size);
        return size;
    }
    

    public static final class AddressHolder {

        public final TextView addressName;
        public final TextView addressDetail;
        public final TextView addressCity;

        private AddressHolder(View v) {
            addressName = (TextView) v.findViewById(R.id.buildorder_address_name);
            addressDetail = (TextView) v.findViewById(R.id.buildorder_address_detailed);
            addressCity = (TextView) v.findViewById(R.id.buildorder_address_city);
        }
    }

    /**
     * 子View的类型
     */
    public static enum ViewType {
        NORMAL_VIEW(1), // 模板为： 显示地址信息
        EDITADDRESS_VIEW(2); //  模板为： 编辑地址

        private int value;

        private ViewType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static int size() {
            return ViewType.values().length;
        }
    }
}
