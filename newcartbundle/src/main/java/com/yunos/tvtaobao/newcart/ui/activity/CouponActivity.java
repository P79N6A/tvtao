package com.yunos.tvtaobao.newcart.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.ShopCoupon;
import com.yunos.tvtaobao.newcart.R;
import com.yunos.tvtaobao.biz.base.BaseMVPActivity;
import com.yunos.tvtaobao.newcart.ui.adapter.CouponAdapter;
import com.yunos.tvtaobao.newcart.ui.contract.CouponContract;
import com.yunos.tvtaobao.newcart.ui.model.CouponModel;
import com.yunos.tvtaobao.newcart.ui.presenter.CouponPresenter;
import com.yunos.tvtaobao.biz.widget.TvRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CouponActivity extends BaseMVPActivity<CouponPresenter> implements CouponContract.View {
    private final String TAG = "cart_coupons";

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
    private TextView tvShopName;


    public static void launch(Activity activity, String mSellerId, String title) {
        Intent intent = new Intent(activity, CouponActivity.class);
        intent.putExtra("mSellerId", mSellerId);
        intent.putExtra("title", title);
        activity.startActivity(intent);
//        activity.overridePendingTransition(R.anim.botton_to_top_fade_in, R.anim.no_move);
    }

    @Override
    protected String getAppTag() {
        return "Tb";
    }

    @Override
    public String getPageName() {
        return TAG;
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> p = super.getPageProperties();
        p.put("spm-cnt", SPMConfig.NEW_SHOP_CART_COUPON_SPM);
        return p;
    }

    public void initView() {
        mShopCouponList = new ArrayList<ShopCoupon>();
        tvEmpty = (TextView) findViewById(R.id.tv_empty);
        tvShopName = (TextView) findViewById(R.id.tv_shop_name);
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
        title = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(title)) {
            tvShopName.setText(title);
        }
        couponAdapter = new CouponAdapter(CouponActivity.this, mShopCouponList, mSellerId);
        mRecyclerView.setAdapter(couponAdapter);
        registerLoginListener();
    }

    @Override
    public void initData() {
        mPresenter.getShopCoupon(this, mSellerId);
    }

    @Override
    protected CouponPresenter createPresenter() {
        return new CouponPresenter(new CouponModel(), this);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.newcart_activity_coupon;
    }

    @Override
    public void showProgressDialog(boolean show) {
        OnWaitProgressDialog(show);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                finish();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void setEmptyShow(boolean show) {
        if (show) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }

    }

    @Override
    public void setCouponData(List<ShopCoupon> data) {
        mShopCouponList.addAll(data);
        couponAdapter.setData(mShopCouponList);
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                if (mRecyclerView.getChildAt(0) != null)
                    mRecyclerView.getChildAt(0).requestFocus();
            }
        });


    }
}
