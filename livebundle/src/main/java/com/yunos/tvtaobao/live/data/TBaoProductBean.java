package com.yunos.tvtaobao.live.data;

import com.yunos.tvtaobao.biz.request.bo.TBaoShopBean;

import java.util.List;

/**
 * Created by pan on 16/12/12.
 */

public class TBaoProductBean {
    private String goodsIndex;
    private TBaoShopBean.ItemListBean.GoodsListBean goodsListBean;

    public String getGoodsIndex() {
        return goodsIndex;
    }

    public void setGoodsIndex(String goodsIndex) {
        this.goodsIndex = goodsIndex;
    }

    public TBaoShopBean.ItemListBean.GoodsListBean getGoodsListBean() {
        return goodsListBean;
    }

    public void setGoodsListBean(TBaoShopBean.ItemListBean.GoodsListBean goodsList) {
        this.goodsListBean = goodsList;
    }
}
