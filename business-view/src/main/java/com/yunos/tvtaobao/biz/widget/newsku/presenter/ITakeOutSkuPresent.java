package com.yunos.tvtaobao.biz.widget.newsku.presenter;

import com.yunos.tvtaobao.biz.base.IPresenter;
import com.yunos.tvtaobao.biz.request.bo.ItemListBean;

/**
 * Created by wuhaoteng on 2018/10/15.
 */

public interface ITakeOutSkuPresent extends IPresenter {
    void initSkuPropsList(ItemListBean itemListBean);

    void onChooseSkuComplete(int chooseNum);
}
