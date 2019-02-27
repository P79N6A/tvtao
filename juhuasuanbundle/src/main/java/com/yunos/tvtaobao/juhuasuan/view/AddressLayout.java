package com.yunos.tvtaobao.juhuasuan.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.bo.Address;

import java.util.List;

public class AddressLayout extends LinearLayout implements OnFocusChangeListener {

    public final static int INVALID_POSITION = -1;

    private final String TAG = "AddressLayout";

    private OnItemClickListener mOnItemClickListener;
    private OnItemSelectedListener mOnItemSelectedListener;
    private View oldAddressView;
    private View currentAddressView;
    private AddressListAdapter adapter;

    public AddressLayout(Context context) {
        super(context);
    }

    public AddressLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AddressLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public Address getSelectAddress() {
        return adapter.getSelectiItem();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
            return onClick(getFocusedChild());
        }
        boolean ret = super.dispatchKeyEvent(event);
        invalidate();
        return ret;

    }

    public boolean onClick(View currView) {
        if (null != currView) {
            AppDebug.i(TAG, "scroll dispatchKeyEvent callOnClick");
            setCurrentAddressVeiw(currView);
            boolean ret = false;
            if (currView instanceof AddressView) {
                ret = ((AddressView) currView).callAddressOnClick();
            }
            onFocusChange(currView, true);
            if (mOnItemClickListener != null) {
                int position = getPositionWithChild(currView);
                ret = mOnItemClickListener.onItemClick(this, currView, position, adapter.getItemId(position));
            }
            return ret;
        }
        return false;
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        AppDebug.i(TAG, "scroll dispatchTouchEvent is runing! action = " + event.getAction());
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:

                View view = getPointChild(event.getX(), event.getY());
                boolean ret = onClick(view);
                if (ret) {
                    return ret;
                }
                break;

            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    public View getPointChild(float x, float y) {
        if (getChildCount() <= 0) {
            return null;
        }
        for (int i = 0; i < getChildCount(); i++) {
            AddressView addressView = (AddressView) getChildAt(i);
            if (addressView.getLeft() <= x && addressView.getRight() >= x && addressView.getTop() <= y
                    && addressView.getBottom() >= y) {
                return addressView;
            }
        }
        return null;
    }

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setCurrentAddressVeiw(View view) {
        oldAddressView = currentAddressView;
        currentAddressView = view;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!(v instanceof AddressView)) {
            return;
        }
        if (null != oldAddressView) {
            oldAddressView.setBackgroundResource(R.drawable.jhs_sku_choose_n);
        }
        if (hasFocus) {
            v.setSelected(true);
            if (currentAddressView != null && currentAddressView == v) {
                currentAddressView.setBackgroundResource(R.drawable.jhs_sku_choose_focus_h);
            } else {
                if (null != currentAddressView) {
                    currentAddressView.setBackgroundResource(R.drawable.jhs_sku_choose_focus);
                }
                v.setBackgroundResource(R.drawable.jhs_sku_choose_h);
            }
            if (null != mOnItemSelectedListener) {
                mOnItemSelectedListener.onItemSelected(this, v, getPositionWithChild(v));
            }
        } else {
            v.setSelected(false);
            if (currentAddressView != null && currentAddressView == v) {
                currentAddressView.setBackgroundResource(R.drawable.jhs_sku_choose_focus);
            } else {
                if (null != currentAddressView) {
                    currentAddressView.setBackgroundResource(R.drawable.jhs_sku_choose_focus);
                }
                v.setBackgroundResource(R.drawable.jhs_sku_choose_n);
            }
        }
    }

    public int getPositionWithChild(View childView) {
        if (null == childView) {
            return INVALID_POSITION;
        }
        if (getChildCount() <= 0) {
            return INVALID_POSITION;
        }
        for (int i = 0; i < getChildCount(); i++) {
            if (childView == getChildAt(i)) {
                return i;
            }
        }
        return INVALID_POSITION;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener l) {
        mOnItemSelectedListener = l;
    }

    public interface OnItemSelectedListener {

        public void onItemSelected(AddressLayout parent, View view, int position);
    }

    public interface OnItemClickListener {

        public boolean onItemClick(AddressLayout parent, View view, int position, long id);
    }

    public class AddressListAdapter extends BaseAdapter {

        private Context mContext;
        private List<Address> mAddresses;
        private int mCount = 0;
        private int selectPosition = INVALID_POSITION;
        private FrameLayout.LayoutParams frameLayoutParamsFirst;
        private FrameLayout.LayoutParams frameLayoutParams;

        public AddressListAdapter(Context context, List<Address> addresses) {
            mContext = context;
            mAddresses = addresses;
            mCount = mAddresses.size();

            for (int i = 0; i < mCount; i++) {
                Address address = addresses.get(i);
                if (address.getStatus() == Address.DEFAULT_DELIVER_ADDRESS_STATUS) {
                    setSelectPosition(i);
                    break;
                }
            }
            frameLayoutParamsFirst = new FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT);
            frameLayoutParamsFirst.setMargins(-20, -20, -20, -20);
            frameLayoutParams = new FrameLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT);
            frameLayoutParams.setMargins(-21, -20, -20, -20);
        }

        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public Address getItem(int position) {
            return mAddresses.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public Integer getSelectPosition() {
            return selectPosition;
        }

        public String getSelectionId() {
            return getSelectiItem().getDeliverId();
        }

        public Address getSelectiItem() {
            return getItem(getSelectPosition());
        }

        public void setSelectPosition(Integer selectPosition) {
            this.selectPosition = selectPosition;
        }

        protected class AddressViewHolder {

            TextView usernameTextView;
            TextView addressProvinceTextView;
            TextView addressCityTextView;
            TextView addressAreaTextView;
            TextView addressDetailTextView;
            TextView addressMobileTextView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AddressViewHolder holder;
            if (null == convertView) {
                holder = new AddressViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.jhs_order_submit_activity_address, null);

                holder.usernameTextView = (TextView) convertView.findViewById(R.id.address_username);
                holder.addressProvinceTextView = (TextView) convertView.findViewById(R.id.address_province);
                holder.addressCityTextView = (TextView) convertView.findViewById(R.id.address_city);
                holder.addressAreaTextView = (TextView) convertView.findViewById(R.id.address_area);
                holder.addressDetailTextView = (TextView) convertView.findViewById(R.id.address_detail);
                holder.addressMobileTextView = (TextView) convertView.findViewById(R.id.address_mobile);

                convertView.setTag(holder);
            } else {
                holder = (AddressViewHolder) convertView.getTag();
            }

            Address address = getItem(position);

            //收货人姓名
            holder.usernameTextView.setText(address.getFullName());

            //省
            holder.addressProvinceTextView.setText(address.getProvince());

            //城市
            holder.addressCityTextView.setText(address.getCity());

            //区、镇
            holder.addressAreaTextView.setText(address.getArea());

            //详细地址
            holder.addressDetailTextView.setText(address.getAddressDetail());

            //手机
            holder.addressMobileTextView.setText(address.getMobile());

            //给选中边框增加margin
            if (convertView instanceof AddressView) {
                AddressView addressView = (AddressView) convertView;
                if (position == 0) {
                    addressView.setBackgroundLayoutParams(frameLayoutParamsFirst);
                } else {
                    addressView.setBackgroundLayoutParams(frameLayoutParams);
                }
            }

            if (INVALID_POSITION != selectPosition && position == selectPosition) {
                holder.usernameTextView.setTextColor(getResources().getColor(R.color.jhs_address_username_current));
                //convertView.setBackgroundResource(R.drawable.sku_choose_focus);
                setCurrentAddressVeiw(convertView);
            } else {
                holder.usernameTextView.setTextColor(getResources().getColor(R.color.jhs_address_username));
            }

            convertView.setOnFocusChangeListener(AddressLayout.this);

            return convertView;
        }
    }

}
