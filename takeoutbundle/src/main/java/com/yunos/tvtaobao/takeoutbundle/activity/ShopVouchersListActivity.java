package com.yunos.tvtaobao.takeoutbundle.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.TakeVouchers;
import com.yunos.tvtaobao.biz.request.bo.VouchersList;
import com.yunos.tvtaobao.biz.util.StringUtil;
import com.yunos.tvtaobao.biz.widget.CustomDialog;
import com.yunos.tvtaobao.takeoutbundle.R;
import com.yunos.tvtaobao.takeoutbundle.adapter.ShopVouchersAdapter;
import com.yunos.tvtaobao.takeoutbundle.listener.OnVouchersItemClickListener;
import com.yunos.tvtaobao.takeoutbundle.widget.focus.V7LinearLayoutManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author：wuhaoteng
 * @Date:2018/12/17
 * @Desc：领取代金券页
 */
public class ShopVouchersListActivity extends BaseActivity {
    private final static String STORE_ID = "store_id";
    private RecyclerView rvShopVouchers;
    private String storeId;
    private List<VouchersList.ResultBean> vouchersList;
    private ShopVouchersAdapter shopVouchersAdapter;
    private BusinessRequest businessRequest;
    //最近一次点击领取的优惠券id
    private String activityId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_voucherslist);
        businessRequest = BusinessRequest.getBusinessRequest();
        rvShopVouchers = (RecyclerView) findViewById(R.id.rv_shop_vouchers);
        storeId = getIntent().getStringExtra(STORE_ID);
        rvShopVouchers.setLayoutManager(new V7LinearLayoutManager(this));
        vouchersList = new ArrayList<>();
        shopVouchersAdapter = new ShopVouchersAdapter(this, vouchersList);
        shopVouchersAdapter.setItemClickListener(new OnVouchersItemClickListener() {
            @Override
            public void onClick(View view, boolean isTaken, String activityId, String exchangeType, String storeIdType) {
                //领券
                if (!isTaken) {
                    businessRequest.requestGetTakeOutVouchers(storeId, activityId, exchangeType, storeIdType,
                            new GetTakeOutVouchersRequest(new WeakReference<BaseActivity>(ShopVouchersListActivity.this)));
                    ShopVouchersListActivity.this.activityId = activityId;
                } else {
                    new CustomDialog.Builder(ShopVouchersListActivity.this)
                            .setType(1)
                            .setResultMessage("您已领取过啦")
                            .create()
                            .show();
                }
            }
        });
        shopVouchersAdapter.setHasStableIds(true);
        //禁用notifyDataSetChanged动画的方法来规避焦点丢失
        rvShopVouchers.setAnimation(null);
        rvShopVouchers.setAdapter(shopVouchersAdapter);


        businessRequest.requestTakeOutVouchersList(storeId, new TakeOutVouchersListRequest(new WeakReference<BaseActivity>(this)));
    }

    public static void startActivity(Context context, String storeId) {
        Intent intent = new Intent();
        intent.putExtra(STORE_ID, storeId);
        intent.setClass(context, ShopVouchersListActivity.class);
        context.startActivity(intent);
    }


    public class TakeOutVouchersListRequest extends BizRequestListener<VouchersList> {
        public TakeOutVouchersListRequest(WeakReference<BaseActivity> weakReference) {
            super(weakReference);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(VouchersList data) {
            if (data != null) {
                ShopVouchersListActivity.this.vouchersList.clear();
                List<VouchersList.ResultBean> result = data.getResult();
                if (result != null) {
                    for (VouchersList.ResultBean resultBean : result) {
                        String conditionType = resultBean.getConditionType();
                        if ("0".equals(conditionType)) {
                            //普通红包
                            ShopVouchersListActivity.this.vouchersList.add(resultBean);
                        }
                    }
                    shopVouchersAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }


    public class GetTakeOutVouchersRequest extends BizRequestListener<TakeVouchers> {
        public GetTakeOutVouchersRequest(WeakReference<BaseActivity> weakReference) {
            super(weakReference);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            new CustomDialog.Builder(ShopVouchersListActivity.this)
                    .setType(1)
                    .setResultMessage("领取失败，请确认是否\n绑定饿了么账号或重试")
                    .create()
                    .show();
            return false;
        }

        @Override
        public void onSuccess(TakeVouchers data) {
            for (VouchersList.ResultBean resultBean : vouchersList) {
                if (!StringUtil.isEmpty(activityId)) {
                    if (activityId.equals(resultBean.getActivityId())) {
                        //设置已经领取
                        resultBean.setStatus("1");
                    }
                }
            }
            shopVouchersAdapter.notifyDataSetChanged();

            new CustomDialog.Builder(ShopVouchersListActivity.this)
                    .setType(1)
                    .setResultMessage("成功领取")
                    .setHasIcon(true)
                    .create()
                    .show();

        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }

        @Override
        public boolean isShowErrorDialog() {
            return false;
        }
    }
}
