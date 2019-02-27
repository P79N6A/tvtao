package com.yunos.tvtaobao.newsku;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.yunos.tv.app.widget.round.RoundCornerImageView;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.widget.newsku.TradeType;
import com.yunos.tvtaobao.biz.widget.newsku.SkuActivity;
import com.yunos.tvtaobao.businessview.R;

/**
 * Created by wuhaoteng on 2018/10/15.
 * Desc：电淘购物车sku页面
 */

public class TvTaoSkuActivity extends SkuActivity<TvTaoSkuPresenter> {
    private static final String TAG = "TvTaoSkuActivity";

    @Override
    protected void initPresenter() {
        //电淘购物车
        String skuId = getIntent().getStringExtra(BaseConfig.INTENT_KEY_SKUID);
        String cartId = getIntent().getStringExtra("cartId");//todo
        String areaId = getIntent().getStringExtra(BaseConfig.INTENT_KEY_AREAID);
        AppDebug.e(TAG, TAG + ".start time : " + System.currentTimeMillis());
        AppDebug.i(TAG, TAG + ".onCreate itemId : " + itemId + " ,skuId : " + skuId +
                " ,cartId : " + cartId + " ,tradeType : " + tradeType + " ,areaId : " + areaId + " ,buyCount : " + buyCount);
        if (!TextUtils.isEmpty(skuId)) {
            mSkuPresenter.setDefaultSku(skuId);
        }
        mSkuPresenter.doDetailRequest(itemId, areaId);
    }


    @Override
    public void initView() {
        super.initView();
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mSkuPresenter.getSkuId())) {
                    if (mSkuPresenter.getPropsBean() != null && mSkuPresenter.getPropsBean().size() > 0) {
                        onShowError(getResources().getString(R.string.new_shop_choose_product_info));
                        return;
                    }
                }

                if (numChooseLayout.getNum() <= 0) {
                    onShowError(getResources().getString(R.string.new_shop_sku_num_exceed_kucun));
                    return;
                }

                switch (tradeType) {
                    case TradeType.EDIT_CART:
                        String prompt = getResources().getString(R.string.new_shop_change_sku_sucess);
                        onPromptDialog(R.drawable.coupon_apply_success_icon, prompt);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onDialogDismiss();

                                Intent intent = new Intent();
                                intent.putExtra(BaseConfig.INTENT_KEY_SKUID, mPresenter == null ? "" : mSkuPresenter.getSkuId());
                                intent.putExtra(BaseConfig.INTENT_KEY_BUY_COUNT, numChooseLayout == null ? 0 : numChooseLayout.getNum());
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }, 2000);

                        break;
                    case TradeType.ADD_CART:
                        ((TvTaoSkuPresenter) mPresenter).addCart(TvTaoSkuActivity.this, itemId, numChooseLayout.getNum(), mSkuPresenter.getSkuId());
                        break;
                    case TradeType.TAKE_OUT_ADD_CART:

                        break;
                    case TradeType.BUY:
                        break;
                }
            }
        });
        okBtn.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected TvTaoSkuPresenter createPresenter() {
        return new TvTaoSkuPresenter(this);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_sku;
    }


    @Override
    protected void addSelectedPropData(long propId, long valueId) {
        mSkuPresenter.addSelectedPropData(propId,valueId);
    }

    @Override
    protected void deleteSelectedPropData(long propId, long valueId) {
        mSkuPresenter.deleteSelectedPropData(propId);
    }
}
