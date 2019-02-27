package com.yunos.tvtaobao.payresult;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.item.RenderPaySuccessRequest;
import com.yunos.tvtaobao.newcart.R;
import com.yunos.tvtaobao.newcart.ui.adapter.GuessLikeAdapter;
import com.yunos.tvtaobao.biz.widget.TvRecyclerView;
import com.yunos.tvtaobao.payment.analytics.Utils;


import java.lang.ref.WeakReference;
import java.util.Map;

public class PayResultActivity extends BaseActivity {
    public static final String SWITCH_TO_HOME_ACTIVITY = "com.yunos.tvtaobao.homebundle.activity.HomeActivity";

    private PayResultDataFetcher mPayResultDataFetcher;

    private TextView mTvPayResult;
    private TextView mTvPayMoney;
    private TextView mTvPayAccount;
    private TvRecyclerView mShopCartEmptyRecyclerView;
    private GuessLikeAdapter mGuessLikeAdapter;

    private boolean mSuccess;
    private double mPrice;
    private String mStatusCode;
    private BusinessRequest mBusinessRequest = null;
    private String orderGoodsIds;
    private String orderIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_result);
        String success = getStringExtra("success", "false");
        mSuccess = Boolean.valueOf(success);
        String price = getStringExtra("price", "0");
        mPrice = Double.valueOf(price);
        mStatusCode = getStringExtra("status", null);
        orderGoodsIds = getStringExtra("orderGoodsIds", "");
        orderIds = getStringExtra("orderIds", "");
//        orderGoodsIds="44107846426,573523675846,574119535964,561522890797,573472535346,540702189276";
        initView();
        initGuessLikeData();
        queryOrderIds();
        wetherShowDialog();//获得买就返红包页面
    }

    private void queryOrderIds() {
        if (!TextUtils.isEmpty(orderIds) && mSuccess) {
            RenderPaySuccessRequest request = new RenderPaySuccessRequest(orderIds);
            BusinessRequest.getBusinessRequest().baseRequest(request,
                    new RenderPayRequestListener(new WeakReference<BaseActivity>(PayResultActivity.this))
                    , true);
        }
    }

    private void wetherShowDialog() {

        mBusinessRequest = BusinessRequest.getBusinessRequest();
        mBusinessRequest.requestBuyToCashback(orderGoodsIds,
                new GetBuyToCashbackListener(new WeakReference<BaseActivity>(this)));
    }

    private class GetBuyToCashbackListener extends BizRequestListener<Map<String, String>> {

        public GetBuyToCashbackListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            AppDebug.e(TAG, "resultCode = " + resultCode + ",msg = " + msg);
            return false;
        }

        @Override
        public void onSuccess(Map<String, String> buyRebateMap) {
            AppDebug.e(TAG, "Map<String,String> = " + buyRebateMap.toString());
            if (buyRebateMap != null) {
                boolean contains = buyRebateMap.containsValue("true");
                if (contains) {
                    Intent startIntent = new Intent();
                    startIntent.setData(Uri.parse("buy_rebate://yunos_tvtaobao_buy_rebate"));
                    PayResultActivity.this.startActivity(startIntent);
                }
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    private void initView() {
        mTvPayResult = (TextView) findViewById(R.id.tv_pay_result);
        mTvPayMoney = (TextView) findViewById(R.id.tv_pay_money);
        mTvPayAccount = (TextView) findViewById(R.id.tv_pay_account);
        mShopCartEmptyRecyclerView = (TvRecyclerView) findViewById(R.id.recyclerview_guesslike);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 5);
        mShopCartEmptyRecyclerView.setLayoutManager(gridLayoutManager);
        mGuessLikeAdapter = new GuessLikeAdapter(this, new GuessLikeAdapter.OnItemListener() {
            @Override
            public void onItemSelected(int position) {
            }
        }, true);
        mShopCartEmptyRecyclerView.setAdapter(mGuessLikeAdapter);
        mShopCartEmptyRecyclerView.requestFocus();

        if (mSuccess) {
            mTvPayResult.setText("成功支付 ");
            String priceText = String.format("¥%.0f", mPrice);
            if (mPrice - (int) mPrice > 0) {
                priceText = String.format("¥%.2f", mPrice);
            }
            mTvPayMoney.setText(priceText);
            mTvPayMoney.setVisibility(View.VISIBLE);
            mTvPayAccount.setVisibility(View.VISIBLE);
        } else {
            mTvPayResult.setText("支付失败");
            mTvPayMoney.setVisibility(View.GONE);
            String message = mapStatusCode(mStatusCode);
            if (!TextUtils.isEmpty(message)) {
                mTvPayAccount.setText(message);
                mTvPayAccount.setVisibility(View.VISIBLE);
            } else
                mTvPayAccount.setVisibility(View.INVISIBLE);
        }

    }

    private String mapStatusCode(String code) {
        //TODO
        return code;
    }

    private String getStringExtra(String key, String defaultValue) {
        Intent intent = getIntent();
        if (null == intent) {
            return defaultValue;
        }
        String value = defaultValue;
        if (intent.hasExtra(key)) {
            value = intent.getStringExtra(key);
        } else {
            Bundle bundle = intent.getExtras();
            if (null != bundle && bundle.containsKey(key)) {
                value = bundle.getString(key, defaultValue);
            } else {
                Uri uri = intent.getData();
                if (uri != null) {
                    String valueStr = uri.getQueryParameter(key);
                    if (null != valueStr && valueStr.length() > 0) {
                        try {
                            value = valueStr;
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
        return value;
    }

    private double getDoubleExtra(String key, double defaultValue) {
        Intent intent = getIntent();
        if (null == intent) {
            return defaultValue;
        }
        double value = defaultValue;
        if (intent.hasExtra(key)) {
            value = intent.getDoubleExtra(key, defaultValue);
        } else {
            Bundle bundle = intent.getExtras();
            if (null != bundle && bundle.containsKey(key)) {
                value = bundle.getDouble(key, defaultValue);
            } else {
                Uri uri = intent.getData();
                if (uri != null) {
                    String valueStr = uri.getQueryParameter(key);
                    if (null != valueStr && valueStr.length() > 0) {
                        try {
                            value = Double.parseDouble(valueStr);
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
        return value;
    }


    @Override
    public String getPageName() {
        return "Page_Pay_success";
    }

    @Override
    public Map<String, String> getPageProperties() {
        if (!TextUtils.isEmpty(CloudUUIDWrapper.getCloudUUID())) {
            return Utils.getProperties();
        }
        return super.getPageProperties();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void initGuessLikeData() {
        OnWaitProgressDialog(true);
        mPayResultDataFetcher = new PayResultDataFetcher(this, mGuessLikeAdapter);
        mPayResultDataFetcher.doRequest(new OnAllDataFetchListener() {
            @Override
            public void onFetchAccount(String account) {
                mTvPayAccount.setText(String.format("使用账号（%s）", account));
            }

            @Override
            public void onAllComplete() {
                OnWaitProgressDialog(false);

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPayResultDataFetcher != null) {
            mPayResultDataFetcher.close();
        }
    }

    interface OnAllDataFetchListener {
        void onFetchAccount(String account);

        void onAllComplete();
    }

    static class RenderPayRequestListener extends BizRequestListener<String> {

        public RenderPayRequestListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            AppDebug.d("TAG", "renderPaySuccess fail," + msg);
            return true;
        }

        @Override
        public void onSuccess(String data) {
            AppDebug.d("TAG", "renderPaySuccess success");
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }

    }
}
