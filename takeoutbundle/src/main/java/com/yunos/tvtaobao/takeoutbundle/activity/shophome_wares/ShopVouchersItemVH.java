package com.yunos.tvtaobao.takeoutbundle.activity.shophome_wares;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.yunos.tvtaobao.biz.request.bo.VouchersList;
import com.yunos.tvtaobao.takeoutbundle.R;
import com.yunos.tvtaobao.takeoutbundle.listener.OnVouchersItemClickListener;
import com.yunos.tvtaobao.takeoutbundle.widget.focus.FocusBorderView;


/**
 * @Author：wuhaoteng
 * @Date:2018/12/17
 * @Desc：
 */
public class ShopVouchersItemVH extends RecyclerView.ViewHolder {
    private Context context;
    private View itemview;
    private TextView txtPrice;
    private TextView txtName;
    private TextView txtDescription;
    private TextView txtGetPackage;

    private boolean hasFocusd;

    private static final String STORE_ID_TYPE = "0";//storeId 类型，0 为淘宝店铺id（默认），1 为饿了么中后台店铺id）


    public ShopVouchersItemVH(Context context, View itemView) {
        super(itemView);
        this.context = context;
        this.itemview = itemView;
        txtPrice = (TextView) itemView.findViewById(R.id.txt_price);
        txtName = (TextView) itemView.findViewById(R.id.txt_name);
        txtDescription = (TextView) itemView.findViewById(R.id.txt_description);
        txtGetPackage = (TextView) itemView.findViewById(R.id.txt_get_package);
    }

    public void bindView(final OnVouchersItemClickListener itemClickListener, final VouchersList.ResultBean resultBean, int position) {
        if (resultBean != null) {
            txtPrice.setText("¥" + resultBean.getAmount());
            txtName.setText(resultBean.getName());
            txtDescription.setText(resultBean.getDescription());
            String status = resultBean.getStatus();
            boolean isTaken = false;
            if ("0".equals(status)) {
                //未领取
                isTaken = false;
                int dp_32 = (int) context.getResources().getDimension(R.dimen.dp_32);
                txtGetPackage.setPadding(0, 0, dp_32, 0);
                txtGetPackage.setText("立即\n领取");
            } else if ("1".equals(status)) {
                //已领取
                isTaken = true;
                txtGetPackage.setText("已领取");
                int dp_16 = (int) context.getResources().getDimension(R.dimen.dp_16);
                txtGetPackage.setPadding(0, 0, dp_16, 0);
            }

            final boolean finalIsTaken = isTaken;
            itemview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onClick(v, finalIsTaken, resultBean.getActivityId(), resultBean.getExchangeType(), STORE_ID_TYPE);
                    }
                }
            });

            if (hasFocusd == false) {
                if (position == 1) {
                    //position = 0是标题，所以1为第一项
                    itemview.requestFocus();
                    hasFocusd = true;
                }
            }
        }

    }
}