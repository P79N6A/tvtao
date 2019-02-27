package com.yunos.tvtaobao.mytaobao.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yunos.tv.app.widget.focus.FocusFlipGridView;
import com.yunos.tvtaobao.biz.request.bo.Address;
import com.yunos.tvtaobao.mytaobao.R;

import java.util.List;

public class AddressGridViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<Address> addresses;
    public int currentSelectedPosition = -1;
    public int defaultAddressPosition = -1;

    public AddressGridViewAdapter(Context context, List<Address> addresses) {
        mContext = context;
        this.addresses = addresses;
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            if (address != null && address.getStatus() == Address.DEFAULT_DELIVER_ADDRESS_STATUS) {
                currentSelectedPosition = 0;
                defaultAddressPosition = 0;
            }
        }
    }

    @Override
    public int getCount() {
        if (addresses == null || addresses.isEmpty()) {
            return 1;
        }
        return addresses.size() + 1;
    }

    @Override
    public Address getItem(int position) {
        if (addresses == null || position == addresses.size()) {
            return null;
        }

        if (addresses.isEmpty()) {
            return null;
        }

        return addresses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.ytsdk_itemlayout_for_address, null);
            FocusFlipGridView.LayoutParams lp = new FocusFlipGridView.
                    LayoutParams(mContext.getResources().getDimensionPixelSize(R.dimen.dp_354), mContext.getResources().getDimensionPixelSize(R.dimen.dp_237));
            convertView.setLayoutParams(lp);
        }

        AddressHolder holder = AddressHolder.get(convertView);

        if (position == getCount() - 1) {
            holder.background.setBackgroundResource(R.drawable.ytsdk_ui2_item_phone_normal);
            holder.name.setText("");
            holder.phoneNum.setText("");
            holder.addressDetail.setText("");
            holder.addressCity.setText("");
            if (addresses == null) {
                setManageBackground(holder, true);
            } else {
                setManageBackground(holder, false);
            }

        } else {
            Address address = addresses.get(position);
            holder.name.setText(address.getFullName());
            holder.phoneNum.setText(address.getMobile());
            holder.addressDetail.setText(address.getAddressDetail());
            holder.addressCity.setText(address.getProvince() + " " + address.getCity() + " " + address.getArea());

            if (currentSelectedPosition == position) {
                if (position == defaultAddressPosition) {
                    setBackground(holder, true, true);
                } else {
                    setBackground(holder, true, false);
                }
            } else {
                setBackground(holder, false, false);
            }
        }

        return convertView;
    }

    public static final class AddressHolder {

        public final View background;
        public final TextView name;
        public final TextView phoneNum;
        public final TextView addressDetail;
        public final TextView addressCity;
        public final TextView setDefault;

        private AddressHolder(View v) {
            background = v.findViewById(R.id.background);
            name = (TextView) v.findViewById(R.id.name);
            phoneNum = (TextView) v.findViewById(R.id.phone_num);
            addressDetail = (TextView) v.findViewById(R.id.address_detail);
            addressCity = (TextView) v.findViewById(R.id.address_city);
            setDefault = (TextView) v.findViewById(R.id.set_default);
            v.setTag(this);
        }

        public static AddressHolder get(View v) {
            if (v.getTag() instanceof AddressHolder) {
                return (AddressHolder) v.getTag();
            }
            return new AddressHolder(v);
        }
    }

    public void update() {
        this.notifyDataSetChanged();
    }

    /**
     * 根据状态设置背景图
     * @param holder
     * @param selected
     * @param focused
     */
    public void setBackground(AddressHolder holder, boolean selected, boolean focused) {
        if (selected) {
            holder.name.setTextColor(mContext.getResources().getColor(R.color.ytm_black));
            holder.phoneNum.setTextColor(mContext.getResources().getColor(R.color.ytm_black));
            holder.addressDetail.setTextColor(mContext.getResources().getColor(R.color.ytm_black));
            holder.setDefault.setVisibility(View.INVISIBLE);
            if (focused) {
                holder.background.setBackgroundResource(R.drawable.ytsdk_ui2_address_chose_focus);
            } else {
                holder.background.setBackgroundResource(R.drawable.ytsdk_ui2_address_chose);
            }
        } else {
            holder.name.setTextColor(mContext.getResources().getColor(R.color.ytsdk_my_address_name));
            holder.phoneNum.setTextColor(mContext.getResources().getColor(R.color.ytsdk_my_address_name));
            holder.addressDetail.setTextColor(mContext.getResources().getColor(R.color.ytsdk_my_address_name));
            if (focused) {
                holder.background.setBackgroundResource(R.drawable.ytsdk_ui2_address_unchose_focus);
                holder.setDefault.setVisibility(View.VISIBLE);
            } else {
                holder.background.setBackgroundResource(R.drawable.ytsdk_ui2_address_unchose);
                holder.setDefault.setVisibility(View.INVISIBLE);
            }

        }

    }


    /**
     * 设置收货地址管理背景图
     * @param holder
     * @param selected
     */
    public void setManageBackground(AddressHolder holder, boolean selected) {
        if (selected) {
            holder.background.setBackgroundResource(R.drawable.ytsdk_ui2_item_phone_focus);
        } else {
            holder.background.setBackgroundResource(R.drawable.ytsdk_ui2_item_phone_normal);
        }
    }
}
