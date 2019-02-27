package com.yunos.tvtaobao.goodlist.adapter;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunos.tv.app.widget.AbsBaseListView;
import com.yunos.tv.app.widget.GridView;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.adapter.TabGoodsBaseAdapter;
import com.yunos.tvtaobao.biz.request.bo.ShopCoupon;
import com.yunos.tvtaobao.goodlist.R;

import java.util.List;

public class ShopCouponGridViewAdapter extends TabGoodsBaseAdapter {

    private final String TAG = "CouponGridViewAdapter";
    private Context mContext;
    private List<ShopCoupon> couponList;
    public int currentSelectedPosition = -1;
    public int defaultAddressPosition = -1;

    public ShopCouponGridViewAdapter(Context context, List<ShopCoupon> couponList) {
        super(context, false);
        mContext = context;
        this.couponList = couponList;
        if (couponList != null && couponList.size() > 0) {
            currentSelectedPosition = 0;
            defaultAddressPosition = 0;
        }
    }


    public void setCouponList(List<ShopCoupon> couponList) {
        this.couponList = couponList;
    }

    @Override
    public int getCount() {
        if (couponList == null || (couponList != null && couponList.isEmpty())) {
            return 0;
        }
        return couponList.size();
    }

    @Override
    public ShopCoupon getItem(int position) {
        if (couponList == null || (couponList != null && couponList.isEmpty())) {
            return null;
        }

        return couponList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.ytsdk_itemlayout_for_coupon, null);
            GridView.LayoutParams lp = new AbsBaseListView.LayoutParams(mContext.getResources().getDimensionPixelSize(R.dimen.dp_180), mContext.getResources().getDimensionPixelSize(R.dimen.dp_240));
            convertView.setLayoutParams(lp);
        }

        Holder holder = Holder.get(convertView);

        ShopCoupon coupon = getItem(position);
        if (coupon != null) {
            String discount = coupon.getDiscountFee();
//            if (!TextUtils.isEmpty(discount) && discount.length() > 3) {
//                discount = discount.substring(0, discount.length() - 3);
//            }
            holder.discount.setText(discount);
            holder.supplier.setText(Html.fromHtml(coupon.getBonusName().toString()));
            holder.condition.setText(coupon.getDesc());
            holder.indate.setText(coupon.getValidTime());
            holder.buttonText.setText("立即领取");
            setBackground(holder, discount);
            setCouponType(holder, coupon.getType());
            if (coupon.getOwnNum() > 0) {
                holder.applied.setVisibility(View.VISIBLE);
            } else {
                holder.applied.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    public static final class Holder {

        public final View background;
        public final TextView discount;
        public final TextView supplier;
        public final TextView condition;
        public final TextView indate;
        public final ImageView typeView;
        public final TextView buttonText;
        public final View applied;

        private Holder(View v) {
            background = v.findViewById(R.id.background);
            discount = (TextView) v.findViewById(R.id.discount);
            supplier = (TextView) v.findViewById(R.id.supplier);
            condition = (TextView) v.findViewById(R.id.condition);
            indate = (TextView) v.findViewById(R.id.indate);
            typeView = (ImageView) v.findViewById(R.id.description_limit);
            buttonText = (TextView) v.findViewById(R.id.use_coupon);
            applied = v.findViewById(R.id.applied);
            v.setTag(this);
        }

        public static Holder get(View v) {
            if (v.getTag() instanceof Holder) {
                return (Holder) v.getTag();
            }
            return new Holder(v);
        }
    }

    public void update() {
        this.notifyDataSetChanged();
    }

    /**
     * 设置背景
     *
     * @param holder
     */
    private void setBackground(Holder holder, String discount) {
        if (TextUtils.isEmpty(discount)) {
            return;
        }
        int price = getIntegerFromString(discount);
        AppDebug.i(TAG, "setBackground original=" + discount + " price=" + price);
        if (price >= 200) {
            holder.background.setBackgroundResource(R.drawable.ytsdk_ui2_coupon_200);
        } else if (price < 200 && price >= 100) {
            holder.background.setBackgroundResource(R.drawable.ytsdk_ui2_coupon_100);
        } else if (price < 100 && price >= 10) {
            holder.background.setBackgroundResource(R.drawable.ytsdk_ui2_coupon_10);
        } else {
            holder.background.setBackgroundResource(R.drawable.ytsdk_ui2_coupon_other);
        }
    }

    /**
     * 设置优惠券类型
     *
     * @param holder
     * @param type
     */
    private void setCouponType(Holder holder, int type) {

        if (holder == null) {
            return;
        }
        if (type == 1) {
            holder.typeView.setImageResource(R.drawable.ytsdk_ui2_coupon_unlimt_icn);
        } else if (type == 0) {
            holder.typeView.setImageResource(R.drawable.ytsdk_ui2_coupon_limt_icn);
        } else {
            holder.typeView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 将字符串int型
     *
     * @param string
     * @return
     */
    private int getIntegerFromString(String string) {
        StringBuffer priceString = new StringBuffer();
        int price = 0;
        try {
            int length = string.length();
            for (int i = 0; i < length; i++) {
                char c = string.charAt(i);
                if (c >= '0' && c <= '9') {
                    priceString.append(c);
                } else {
                    if (priceString.length() > 0) {
                        break;
                    }
                }
            }
            if (priceString.length() > 0) {
                price = Integer.valueOf(priceString.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return price;
    }

    @Override
    public String getGoodsTitle(String tabkey, int position) {
        //TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getColumnsCounts() {
        return 5;
    }

    @Override
    public Drawable getDefaultDisplayPicture() {
        //TODO Auto-generated method stub
        return null;
    }

    @Override
    public Drawable getInfoDiaplayDrawable(String tabkey, int position) {
        //TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getNetPicUrl(String tabkey, int position) {
        //TODO Auto-generated method stub
        return null;
    }

    @Override
    public View getFillView(int position, View convertView, ViewGroup parent) {
        //TODO Auto-generated method stub
        return null;
    }
}
