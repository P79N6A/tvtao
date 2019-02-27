package com.yunos.tvtaobao.takeoutbundle.activity;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tvlife.imageloader.core.assist.FailReason;
import com.tvlife.imageloader.core.listener.ImageLoadingListener;
import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.app.widget.round.RoundCornerImageView;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.request.bo.ShopDetailData;
import com.yunos.tvtaobao.takeoutbundle.R;
import com.yunos.tvtaobao.takeoutbundle.adapter.DetailActivityAdapter;

import java.util.Map;

/**
 * Created by chenjiajuan on 17/12/15.
 *
 * @describe 店铺详情页 公告与活动
 */
public class TakeOutShopDetailActivity extends BaseActivity {
    private static String TAG = "TakeOutShopDetailActivity";
    private RoundCornerImageView ivShoLogo, ivServiceLicense, ivLicens;
    private ImageView ivStarLevel;
    private TextView tvShopName, tvPhone, tvAddress, tvServingTime, tvNotice, tvDeliver;
    private ImageLoaderManager imageLoaderManager;
    private RecyclerView ryActivityList;
    private DetailActivityAdapter adapter;
    private ShopDetailData.StoreDetailDTOBean storeDetailDTOBean;
    private String pageName;
    private TextView tvLicens,tvServiceLicense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onKeepActivityOnlyOne(TakeOutShopDetailActivity.class.getName());
        setContentView(R.layout.activity_take_out_shop_detail);
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            AppDebug.e(TAG," bundle is null");
            return;
        }
        storeDetailDTOBean = (ShopDetailData.StoreDetailDTOBean) bundle.get("storeDetailDTOBean");
        if (storeDetailDTOBean == null) {
            Toast.makeText(this, "获取商家数据失败！", Toast.LENGTH_SHORT).show();
            return;
        }
        pageName = getIntent().getStringExtra("pageName");
        AppDebug.e(TAG, pageName);
        utShopDetailExpose();
        ivShoLogo = (RoundCornerImageView) this.findViewById(R.id.iv_shop_logo);
        tvShopName = (TextView) this.findViewById(R.id.tv_shop_name);
        ivStarLevel = (ImageView) this.findViewById(R.id.iv_starLevel);
        ryActivityList = (RecyclerView) this.findViewById(R.id.ry_activity_list);
        tvPhone = (TextView) this.findViewById(R.id.tv_phone);
        tvAddress = (TextView) this.findViewById(R.id.tv_address);
        tvServingTime = (TextView) this.findViewById(R.id.tv_serving_time);
        ivServiceLicense = (RoundCornerImageView) this.findViewById(R.id.iv_serviceLicense);
        ivLicens = (RoundCornerImageView) this.findViewById(R.id.iv_licens);
        tvNotice = (TextView) this.findViewById(R.id.tv_notice);
        tvDeliver = (TextView) this.findViewById(R.id.tv_deliver);
        tvLicens=(TextView) this.findViewById(R.id.tv_licens);
        tvServiceLicense=(TextView) this.findViewById(R.id.tv_serviceLicense);
        imageLoaderManager = ImageLoaderManager.getImageLoaderManager(this);
        setDetailData();

    }


    /**
     * 设置店铺详情数据
     */
    private void setDetailData() {
        if (storeDetailDTOBean == null) {
            return;
        }
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        //商家logo
        if (!TextUtils.isEmpty(storeDetailDTOBean.getShopLogo())){
            AppDebug.e(TAG,"storeDetailDTOBean.shopLogo = "+storeDetailDTOBean.getShopLogo());
            imageLoaderManager.loadImage(storeDetailDTOBean.getShopLogo(), ivShoLogo, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    ivShoLogo.setImageResource(R.drawable.icon_shop_no_logo);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    ivShoLogo.setImageResource(R.drawable.icon_shop_no_logo);
                }
            });
        }else {
            ivShoLogo.setImageResource(R.drawable.icon_shop_no_logo);
        }
        //商家名称
        if (!TextUtils.isEmpty(storeDetailDTOBean.getName())) {
            tvShopName.setText(storeDetailDTOBean.getName());
        }
        //星级
        if (!TextUtils.isEmpty(storeDetailDTOBean.getStarPicUrl())) {
            imageLoaderManager.loadImage(storeDetailDTOBean.getStarPicUrl(), ivStarLevel, null);
        }
        //配送费
        String agentFree = storeDetailDTOBean.getAgentFee();
        String agentFree2 = "";
        if (!TextUtils.isEmpty(agentFree)) {
            AppDebug.e(TAG, "agentFree = " + agentFree);
            agentFree2 = "配送¥" + Integer.parseInt(agentFree) / 100;
        }
        //起送费
        String deliverAmount = storeDetailDTOBean.getDeliverAmount();
        String deliverAmount2 = "";
        if (!TextUtils.isEmpty(deliverAmount)) {
            AppDebug.e(TAG, "deliverAmount = " + deliverAmount);
            deliverAmount2 = "起送¥" + Integer.parseInt(deliverAmount) / 100;
        }

        //人均消费
        String perCapitaPrice = storeDetailDTOBean.getPerCapitaPrice();
        String price = "人均¥" + Integer.parseInt(perCapitaPrice) / 100;
        String deliver = deliverAmount2 + "  |  " +agentFree2+ "  |  " + price;

        tvDeliver.setText(deliver);
        //公告
        if (storeDetailDTOBean.getAttributes() != null) {
            if (!TextUtils.isEmpty(storeDetailDTOBean.getAttributes().getNotice()) &&
                    (!storeDetailDTOBean.getAttributes().getNotice().equals(" "))) {
                tvNotice.setText(storeDetailDTOBean.getAttributes().getNotice());

            } else {
                if (!TextUtils.isEmpty(storeDetailDTOBean.getName())) {
                    tvNotice.setText("欢迎光临" + storeDetailDTOBean.getName());
                }
            }
        } else {
            if (!TextUtils.isEmpty(storeDetailDTOBean.getName())) {
                tvNotice.setText("欢迎光临" + storeDetailDTOBean.getName());
            }
        }
        tvNotice.setFocusable(true);
        tvNotice.requestFocus();
        //商家活动
        adapter = new DetailActivityAdapter(this, storeDetailDTOBean);
        ryActivityList.setAdapter(adapter);
        ryActivityList.setLayoutManager(manager);
        int space = (int) getResources().getDimension(R.dimen.dp_12);
        ryActivityList.addItemDecoration(new RecycleItemDecoration(space));

        //联系电话
        if (storeDetailDTOBean.getPhoneList() != null && storeDetailDTOBean.getPhoneList().size() > 0) {
            if (!TextUtils.isEmpty(storeDetailDTOBean.getPhoneList().get(0))) {
                tvPhone.setText(storeDetailDTOBean.getPhoneList().get(0));
            }
        }
        //商家地址
        if (!TextUtils.isEmpty(storeDetailDTOBean.getAddressText())) {
            tvAddress.setText(storeDetailDTOBean.getAddressText());
        }
        //服务时间
        if (storeDetailDTOBean.getServingTime() != null && storeDetailDTOBean.getServingTime().size() > 0) {
            if (!TextUtils.isEmpty(storeDetailDTOBean.getServingTime().get(0))) {
                tvServingTime.setText(storeDetailDTOBean.getServingTime().get(0));
            }
        }
        //营业执照
        if (!TextUtils.isEmpty(storeDetailDTOBean.getLicenseImage())) {
            ivLicens.setVisibility(View.VISIBLE);
            imageLoaderManager.loadImage(storeDetailDTOBean.getLicenseImage(), ivLicens, null);
        } else {
            ivLicens.setVisibility(View.GONE);
            tvLicens.setVisibility(View.INVISIBLE);
        }
        //许可证
        if (!TextUtils.isEmpty(storeDetailDTOBean.getServiceLicenseImage())) {
            ivServiceLicense.setVisibility(View.VISIBLE);
            imageLoaderManager.loadImage(storeDetailDTOBean.getServiceLicenseImage(), ivServiceLicense, null);
        } else {
            ivServiceLicense.setVisibility(View.GONE);
            tvServiceLicense.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onRemoveKeepedActivity(TakeOutOrderDetailActivity.class.getName());
        utShopDetailBack();
    }


    /**
     * 详情页面曝光事件
     */
    private void utShopDetailExpose() {
        Map<String, String> properties = Utils.getProperties();
        if (storeDetailDTOBean != null) {
            properties.put("shop id", storeDetailDTOBean.getShopId());
        }

        properties.put("spm", SPMConfig.WAIMAI_SHOP_DETAILS_EXPOSE);
        properties.put("uuid", CloudUUIDWrapper.getCloudUUID());

        Utils.utCustomHit(pageName, "Expose_waimai_shop_grant_shopdetails", properties);
    }


    /**
     * 详情页退出事件
     */
    private void utShopDetailBack() {
        Map<String, String> properties = Utils.getProperties();

        properties.put("uuid", CloudUUIDWrapper.getCloudUUID());

        if (storeDetailDTOBean != null) {
            properties.put("shop id", storeDetailDTOBean.getShopId());
        }
        properties.put("spm", SPMConfig.WAIMAI_SHOP_DETAILS_KEY_BACK);
        Utils.utControlHit(pageName, "Page_waimai_shop_grant_shopdetails_key_back", properties);
    }

    public class RecycleItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public RecycleItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = space;
        }
    }
}
