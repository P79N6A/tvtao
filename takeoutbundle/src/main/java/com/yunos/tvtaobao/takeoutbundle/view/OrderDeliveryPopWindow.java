package com.yunos.tvtaobao.takeoutbundle.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.view.ViewGroup.LayoutParams;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.TakeOutOrderDeliveryData;
import com.yunos.tvtaobao.biz.request.bo.TakeOutOrderInfoDetails;
import com.yunos.tvtaobao.biz.util.StringUtil;
import com.yunos.tvtaobao.takeoutbundle.R;
import com.yunos.tvtaobao.takeoutbundle.activity.TakeOutOrderListActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * @describe  物流浮层
 */
public class OrderDeliveryPopWindow extends PopupWindow {

    private View detailView;
    private ViewFlipper viewFlipper;
    private TextView name;
    private TextView mobile;
    private TextView distance;
    private String addressX;
    private String addressY;
    private String manX;
    private String manY;
    private View rightInfo;
    private View rightDetail;
    TextView phone;

    BusinessRequest businessRequest = BusinessRequest.getBusinessRequest();

    public OrderDeliveryPopWindow(Context context) {

        LayoutInflater inflater = LayoutInflater.from(context);
        detailView = inflater.inflate(R.layout.order_delivery_pop, null);
        name = (TextView) detailView.findViewById(R.id.take_out_delivery_name);
        mobile = (TextView) detailView.findViewById(R.id.take_out_delivery_mobile);
        distance = (TextView) detailView.findViewById(R.id.take_out_delivery_distance);
        rightInfo = detailView.findViewById(R.id.order_delivery_right_info);
        rightDetail = detailView.findViewById(R.id.order_delivery_right_details);
        phone = (TextView) detailView.findViewById(R.id.order_delivery_right_phone);

        viewFlipper = new ViewFlipper(context);
        viewFlipper.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

        viewFlipper.addView(detailView);
        viewFlipper.setFlipInterval(6000000);
        this.setContentView(viewFlipper);
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        this.setBackgroundDrawable(dw);
        this.update();
    }

    public void showDetailPopWindow(View parent) {
        name.setText("");
        mobile.setText("");
        distance.setText("");

        addressX = addressY = "";
        manX = manY = "";

        if (isShowing()) {
            return;
        }

        this.showAtLocation(parent, Gravity.NO_GRAVITY, 0, 0);
        viewFlipper.startFlipping();
    }

    public void updateData(final String orderId, final TakeOutOrderListActivity activity) {

        // 获取配送地址中的经纬度.
        businessRequest.getTakeOutOrderDetail(orderId,
                new BizRequestListener<TakeOutOrderInfoDetails>(new WeakReference<BaseActivity>(activity)) {
                    @Override
                    public boolean onError(int resultCode, String msg) {
                        //OrderDeliveryPopWindow.this.dismiss();
                        return false;
                    }

                    @Override
                    public void onSuccess(TakeOutOrderInfoDetails data) {
                        if (data != null && data.getDetails4Address() != null) {
                            addressX = data.getDetails4Address().getPositionX();
                            addressY = data.getDetails4Address().getPositionY();

                            getDelivery(orderId, activity, data.getContactInfo().getStorePhone());
                        }
                    }

                    @Override
                    public boolean ifFinishWhenCloseErrorDialog() {
                        return false;
                    }
                });
    }

    private void getDelivery(String orderId, final TakeOutOrderListActivity activity, final String phoneValue) {
        // 获取骑手信息.
        businessRequest.getTakeOutOrderDelivery(orderId,
                new BizRequestListener<TakeOutOrderDeliveryData>(new WeakReference<BaseActivity>(activity)) {
                    @Override
                    public boolean onError(int resultCode, String msg) {
                        //OrderDeliveryPopWindow.this.dismiss();
                        return false;
                    }

                    @Override
                    public void onSuccess(TakeOutOrderDeliveryData data) {
                        if (data != null) {
                            if (StringUtil.isEmpty(data.getDeliveryName()) || StringUtil.isEmpty(data.getDeliveryMobile())) {
                                rightInfo.setVisibility(View.VISIBLE);
                                rightDetail.setVisibility(View.GONE);

                                phone.setText(phoneValue);
                            } else {
                                rightDetail.setVisibility(View.VISIBLE);
                                rightInfo.setVisibility(View.GONE);
                                manX = data.getLatitude();
                                manY = data.getLongitude();

                                name.setText(data.getDeliveryName());
                                mobile.setText(data.getDeliveryMobile());

                                showDistance();
                            }
                        }
                    }

                    @Override
                    public boolean ifFinishWhenCloseErrorDialog() {
                        return false;
                    }
                });
    }

    private void showDistance() {
        if (!StringUtil.isEmpty(manX) && !StringUtil.isEmpty(manY) &&
                !StringUtil.isEmpty(addressX) && !StringUtil.isEmpty(addressY)) {
            businessRequest.getDistanceFromGaode(addressY + "," + addressX, manY + "," + manX,
                    new RequestListener<String>() {
                        @Override
                        public void onRequestDone(String data, int resultCode, String msg) {
                            try {
                                JSONObject jsonObject = new JSONObject(data);
                                if (jsonObject != null && !jsonObject.isNull("data")) {
                                    JSONObject dataObj = jsonObject.getJSONObject("data");

                                    JSONArray pathArray = dataObj.optJSONArray("paths");
                                    if (pathArray != null && pathArray.length() > 0) {
                                        JSONObject pathObj = pathArray.getJSONObject(0);
                                        if (pathObj != null && !pathObj.isNull("distance")) {
                                            distance.setText(pathObj.optString("distance"));
                                            return;
                                        }
                                    }
                                }
                                //OrderDeliveryPopWindow.this.dismiss();
                            }
                            catch (Exception e) {
                                //OrderDeliveryPopWindow.this.dismiss();
                            }
                        }
                    });
        }
    }
}
