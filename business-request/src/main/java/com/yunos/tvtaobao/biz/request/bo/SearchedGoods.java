package com.yunos.tvtaobao.biz.request.bo;

import java.util.List;

/**
 * Created by rca on 17/6/9.
 */

public interface SearchedGoods {

    String TYPE_ZTC = "ztc";
    String TYPE_ITEM = "item";

    String getType();

    String getItemId();

    String getImgUrl();

    String getTitle();

    String getWmPrice();

    String getPrice();

    String getSold();

    String getPostFee();

    String getEurl();

    String getUri();

    String getS11();

    String getS11Pre();

    Boolean isPre();

    //买就返
    Boolean isBuyCashback();

    //店铺名
    String getNick();

    void setRebateBo(RebateBo rebateBo);

    RebateBo getRebateBo();

    String getSoldText();

    /**
     * 商品名称前的标签的图标url：天猫，天猫国际，天猫超市，飞猪等
     * @return
     */
    String getTagPicUrl();

    /**
     * 标签列表名称：广告标，包邮，预售立减，买就返红包，满88包邮等
     * @return
     */
    List<String> getTagNames();
}
