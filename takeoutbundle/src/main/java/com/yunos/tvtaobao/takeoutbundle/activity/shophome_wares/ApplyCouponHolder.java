package com.yunos.tvtaobao.takeoutbundle.activity.shophome_wares;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.yunos.tvtaobao.biz.request.bo.TakeoutApplyCoupon;
import com.yunos.tvtaobao.takeoutbundle.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author：wuhaoteng
 * @Date:2018/12/25
 * @Desc：
 */
public class ApplyCouponHolder extends RecyclerView.ViewHolder {

    private TextView txtPrice;
    private TextView txtName;
    private TextView txtScope;
    private TextView txtDescription;

    private void findViews(View itemView) {
        txtPrice = (TextView) itemView.findViewById(R.id.txt_price);
        txtName = (TextView) itemView.findViewById(R.id.txt_name);
        txtScope = (TextView) itemView.findViewById(R.id.txt_scope);
        txtDescription = (TextView) itemView.findViewById(R.id.txt_description);
    }


    public ApplyCouponHolder(@NonNull View itemView) {
        super(itemView);
        findViews(itemView);
    }

    public void bindView(TakeoutApplyCoupon.CouponsBean couponsBean) {
        try {
            String amount = couponsBean.getAmount();
            txtPrice.setText("¥" + Float.parseFloat(amount)/100);
            txtName.setText(couponsBean.getConditionText());
            txtScope.setText(couponsBean.getScope());
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startTime = null;
            Date endTime = null;
            startTime = sDateFormat.parse(couponsBean.getStartTime());
            endTime = sDateFormat.parse(couponsBean.getEndTime());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            txtDescription.setText("有效期：" + sdf.format(startTime) + "～" + sdf.format(endTime));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
