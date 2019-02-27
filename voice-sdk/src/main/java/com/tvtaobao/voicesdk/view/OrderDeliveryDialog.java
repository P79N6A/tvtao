package com.tvtaobao.voicesdk.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.tvtaobao.voicesdk.ASRNotify;
import com.tvtaobao.voicesdk.R;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.TakeOutOrderDeliveryData;
import com.yunos.tvtaobao.biz.request.bo.TakeOutOrderInfoDetails;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @describe 物流浮层
 */
public class OrderDeliveryDialog extends Activity {
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
    private TextView tvResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_delivery_pop);
        name = (TextView) findViewById(R.id.take_out_delivery_name);
        mobile = (TextView) findViewById(R.id.take_out_delivery_mobile);
        distance = (TextView) findViewById(R.id.take_out_delivery_distance);
        rightInfo = findViewById(R.id.order_delivery_right_info);
        tvResult = (TextView) findViewById(R.id.tv_result);
        rightDetail = findViewById(R.id.order_delivery_right_details);
        phone = (TextView) findViewById(R.id.order_delivery_right_phone);

//        this.setWidth(LayoutParams.MATCH_PARENT);
//        this.setHeight(LayoutParams.MATCH_PARENT);
//        this.setFocusable(true);
//        ColorDrawable dw = new ColorDrawable(0x00000000);
//        this.setBackgroundDrawable(dw);
//        this.update();
        name.setText("");
        mobile.setText("");
        distance.setText("");

        addressX = addressY = "";
        manX = manY = "";
        String tbMainOrderId = getIntent().getStringExtra("tbMainOrderId");
        if (!TextUtils.isEmpty(tbMainOrderId)) {
            updateData(tbMainOrderId);
        }
    }

    public void updateData(final String orderId) {
// 获取配送地址中的经纬度.
        businessRequest.getTakeOutOrderDetail(orderId, new RequestListener<TakeOutOrderInfoDetails>() {
            @Override
            public void onRequestDone(TakeOutOrderInfoDetails data, int resultCode, String msg) {
                if (data != null && data.getDetails4Address() != null) {
                    addressX = data.getDetails4Address().getPositionX();
                    addressY = data.getDetails4Address().getPositionY();

                    getDelivery(orderId, data.getContactInfo().getStorePhone());
                } else {
                    if (!TextUtils.isEmpty(msg)) {
                        tvResult.setText(msg);
                        ASRNotify.getInstance().playTTS(msg);
                    }
                    rightDetail.setVisibility(View.GONE);
                    rightInfo.setVisibility(View.GONE);
                    tvResult.setVisibility(View.VISIBLE);

                }
            }
        });

    }

    private void getDelivery(String orderId, final String phoneValue) {
        // 获取骑手信息.
        businessRequest.getTakeOutOrderDelivery(orderId, new RequestListener<TakeOutOrderDeliveryData>() {
            @Override
            public void onRequestDone(TakeOutOrderDeliveryData data, int resultCode, String msg) {
                if (data != null) {
                    if (TextUtils.isEmpty(data.getDeliveryName()) || TextUtils.isEmpty(data.getDeliveryMobile())) {
                        rightInfo.setVisibility(View.VISIBLE);
                        rightDetail.setVisibility(View.GONE);
                        tvResult.setVisibility(View.GONE);
                        phone.setText(phoneValue);
                    } else {
                        rightDetail.setVisibility(View.VISIBLE);
                        rightInfo.setVisibility(View.GONE);
                        tvResult.setVisibility(View.GONE);
                        manX = data.getLatitude();
                        manY = data.getLongitude();

                        name.setText(data.getDeliveryName());
                        mobile.setText(data.getDeliveryMobile());

                        showDistance();
                    }
                }
            }
        });
    }

    private void showDistance() {
        if (!TextUtils.isEmpty(manX) && !TextUtils.isEmpty(manY) &&
                !TextUtils.isEmpty(addressX) && !TextUtils.isEmpty(addressY)) {
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
                                            ASRNotify.getInstance().playTTS("骑手距您还有" + pathObj.optString("distance") + "米");
                                            return;
                                        }
                                    }
                                }
                                //OrderDeliveryPopWindow.this.dismiss();
                            } catch (Exception e) {
                                //OrderDeliveryPopWindow.this.dismiss();
                            }
                        }
                    });
        }
    }
}
