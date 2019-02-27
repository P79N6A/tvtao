package com.yunos.tvtaobao.takeoutbundle.activity;


import android.view.View;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.request.bo.ItemListBean;

import com.yunos.tvtaobao.biz.widget.newsku.SkuActivity;
import com.yunos.tvtaobao.takeoutbundle.R;
import com.yunos.tvtaobao.takeoutbundle.presenter.TakeOutSkuPresenter;


/**
 * Created by wuhaoteng on 2018/10/15.
 * Desc：外卖Sku页面
 */

public class TakeOutSkuActivity extends SkuActivity<TakeOutSkuPresenter> {
    private static final String TAG = "TakeOutSkuActivity";

    @Override
    protected void initPresenter() {
        ItemListBean itemListBean = (ItemListBean) getIntent().getSerializableExtra(BaseConfig.INTENT_KEY_TAKEOUT_SKU_DATA);
        mSkuPresenter.initSkuPropsList(itemListBean);
    }


    @Override
    public void initView() {
        super.initView();
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSkuPresenter.onChooseSkuComplete(numChooseLayout.getNum());
            }
        });
    }

    @Override
    protected TakeOutSkuPresenter createPresenter() {
        return new TakeOutSkuPresenter(this,this);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_take_out_sku;
    }

    @Override
    protected void addSelectedPropData(long propId, long valueId) {
        mSkuPresenter.addSelectedPropData(propId,valueId);
    }

    @Override
    protected void deleteSelectedPropData(long propId, long valueId) {
        mSkuPresenter.deleteSelectedPropData(propId,valueId);
    }
}
