/** $
 * PROJECT NAME: TVTaobao
 * PACKAGE NAME: com.yunos.tvtaobao.activity.detail
 * FILE    NAME: DetailPanelTextListView.java
 * CREATED TIME: 2015年11月12日
 *    COPYRIGHT: Copyright(c) 2013 ~ 2015  All Rights Reserved.
 */
package com.yunos.tvtaobao.detailbundle.bean;

import android.text.SpannableString;

import com.yunos.tvtaobao.detailbundle.type.DetailModleType;

import java.io.Serializable;
import java.util.List;


public class DetailPanelData implements Serializable {

    private static final long serialVersionUID = 3328440635082012153L;

    //商品标题
    public SpannableString title;

    //双11subtitle
    public String subImageUrl;

    //月销量
    public String monthsold;

    //预售的定金名称
    public String prePriceName;

    //运费
    public String pressfee;

    //双11购物券
    public String double11CouponTag;

    //服务承诺
    public String servicepromise;

    //当前价格
    public String activityPrice;

    //原价
    public String originalPrice;

    //预售定金
    public String prePrice;

    //预售定金提示
    public String prePriceTip;

    //预售尾款提示
    public String lastPriceTip;

    //聚划算标签列表
    public List<String> showTagNames;

    //显示模式
    public DetailModleType detailModleType;

    //倒计时提示
    public String timerTip;

    //商品状态 0:未开始; 1:已开始
    public int status;

    //是否有店铺优惠券
    public boolean hasCoupon;

    //是否有海尔分享app
    public boolean hasHaierShareApp;

}
