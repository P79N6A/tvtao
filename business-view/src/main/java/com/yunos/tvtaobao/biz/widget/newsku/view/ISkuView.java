package com.yunos.tvtaobao.biz.widget.newsku.view;

import com.yunos.tvtaobao.biz.request.bo.SkuPriceNum;
import com.yunos.tvtaobao.biz.request.bo.TBDetailResultV6;
import com.yunos.tvtaobao.biz.base.IView;
import com.yunos.tvtaobao.biz.widget.newsku.widget.SkuItemLayout;

import java.util.List;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/6/4
 *     desc :
 *     version : 1.0
 * </pre>
 */

public interface ISkuView extends IView {
    void initTitle(String title);                                                                   //获取商品标题

    void initSkuView(List<TBDetailResultV6.SkuBaseBean.PropsBeanX> propsList);                      //初始化右侧sku信息

    void initSkuKuCunAndPrice(SkuPriceNum skuPriceNum);

    void showUnitBuy(int times);                                                                    //显示限购倍数

    void updateSkuKuCunAndPrice(SkuPriceNum skuPriceNum);                                           //更新库存和价格

    void updateImage(String url);                                                                   //切换sku时，更新图片

    void updateValueViewStatus(Long propId, Long valueId, SkuItemLayout.VALUE_VIEW_STATUS status);

    void onShowError(String prompt);                                                                //显示错误信息

    void onPromptDialog(int resource, String prompt);                                               //显示提示弹框

    void onDialogDismiss();                                                                         //关闭提示弹框
}
