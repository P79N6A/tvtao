package com.yunos.tvtaobao.detailbundle.evaluate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.ShopCoupon;
import com.yunos.tvtaobao.detailbundle.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CouponActivity extends BaseActivity {
    private final String TAG = "Page_TbDetail_Coupon";

    private TvRecyclerView mRecyclerView;

    // 网络请求
    private BusinessRequest mBusinessRequest;
    private List<ShopCoupon> mShopCouponList;
    private String mSellerId;
    private String mItemID;
    private String title;

    private CouponAdapter couponAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView tvEmpty;

    public static void launch(Activity activity, String mSellerId, String mItemID, String title) {
        Intent intent = new Intent(activity, CouponActivity.class);
        intent.putExtra("mSellerId", mSellerId);
        intent.putExtra("mItemID", mItemID);
        intent.putExtra("title", title);


        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);
        registerLoginListener();
        initView();
        getShopCoupon();
    }

    @Override
    public String getPageName() {
        return TAG;
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> p = super.getPageProperties();
        if (!TextUtils.isEmpty(mItemID)) {
            p.put("item_id", mItemID);
        }
        if (!TextUtils.isEmpty(getAppName()) && !TextUtils.isEmpty(AppInfo.getAppVersionName())) {
            p.put("from_app", getAppName() + AppInfo.getAppVersionName());
        }
        if (!TextUtils.isEmpty(title)) {
            p.put("item_name", title);
        }
        if (CoreApplication.getLoginHelper(CoreApplication.getApplication()).isLogin()) {
            p.put("is_login", "1");
        } else {
            p.put("is_login", "0");
        }

        p.put(SPMConfig.SPM_CNT, "a2o0j.67556coupon.0.0");

        return p;
    }

    private void initView() {
        mShopCouponList = new ArrayList<ShopCoupon>();
        tvEmpty = (TextView) findViewById(R.id.tv_empty);
        mRecyclerView = (TvRecyclerView) findViewById(R.id.rv_coupon);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // 设置布局管理器
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                if (mRecyclerView.getChildAt(0) != null)
                    mRecyclerView.getChildAt(0).requestFocus();
            }
        });

        mBusinessRequest = BusinessRequest.getBusinessRequest();
        mSellerId = getIntent().getStringExtra("mSellerId");
        mItemID = getIntent().getStringExtra("mItemID");
        title = getIntent().getStringExtra("title");

        couponAdapter = new CouponAdapter(CouponActivity.this, mShopCouponList, mSellerId);
        mRecyclerView.setAdapter(couponAdapter);
    }


    private void getShopCoupon() {
        mBusinessRequest.getShopCoupon(mSellerId, new ShopCouponListBusinessRequestListener(
                new WeakReference<BaseActivity>(this)));
    }

    private class ShopCouponListBusinessRequestListener extends BizRequestListener<List<ShopCoupon>> {

        public ShopCouponListBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef) {
            super(mBaseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            CouponActivity couponActivity = (CouponActivity) mBaseActivityRef.get();
            if (couponActivity != null) {
                // 允许按返回键取消进度条
                couponActivity.setProgressCancelable(true);
                couponActivity.OnWaitProgressDialog(false);
            }
            return true;
        }

        @Override
        public void onSuccess(List<ShopCoupon> data) {
            CouponActivity couponActivity = (CouponActivity) mBaseActivityRef.get();
            if (couponActivity != null) {
                // 允许按返回键取消进度条
                couponActivity.setProgressCancelable(true);
                couponActivity.OnWaitProgressDialog(false);
                if (data != null && data.size() > 0) {
                    tvEmpty.setVisibility(View.GONE);

                    for (int i = 0; i < data.size(); i++) {
                        for (int j = 0; j < data.size() - i - 1; j++) {
                            if (Integer.parseInt(data.get(j).getDiscountFee()) > Integer.parseInt(data.get(j + 1).getDiscountFee())) {
                                ShopCoupon shopCoupon = data.get(j);
                                data.set(j, data.get(j + 1));
                                data.set(j + 1, shopCoupon);
                            }
                        }
                    }
                    mShopCouponList.addAll(data);
                    couponAdapter.setData(mShopCouponList);
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mRecyclerView.getChildAt(0) != null)
                                mRecyclerView.getChildAt(0).requestFocus();
                        }
                    });

                } else {
                    tvEmpty.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return true;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK||event.getKeyCode() == KeyEvent.KEYCODE_ESCAPE) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                finish();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
