package com.yunos.tvtaobao.detailbundle.bean;

import android.text.SpannableString;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yunos.tvtaobao.detailbundle.type.DetailModleType;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dingbin on 2017/8/24.
 */

public class NewDetailPanelData implements Serializable {
    //id
    public String mItemID;
    //店铺名称
    public SpannableString title;
//    public String shopName;
//商品名称
    public String goodTitle;
    //现价
    public String nowPriceTitle;
    //现价
    public String nowPrice;
    //原价
    public String oldPriceTitle;
    //原价
    public String oldPrice;
    //原价是否要下划线
    public String  oldPriceLineThrough = "true";

    //原价是否要下划线

    public String tag1;

    //
    public String soldNum;

    //预售总价Tite
    public String presellPriceTitle = "";
    //预售总价
    public String presellPrice;
    //运费
    public String postage;
    //服务
    public List<String> services;

    //优惠信息
    public String coupon;
    //电淘积分信息
    public String jifen;

    //天猫国际进口税
    public String tax;
    //商品状态 0:未开始; 1:已开始
    public int status;

    //是否有店铺优惠券
    public boolean hasCoupon;
    //模式
    public DetailModleType detailModleType;

    //聚划算标签
    public String slogo;

    //预售发货
    public String deliverGoods;

    //促销
    public String salesPromotion;
    //促销
    public String salesPromotionIconText;

    //预售尾款提示
    public String lastPriceTip;

    //阿里妈妈优惠券
    public String AliCoupon;

    //第一张头图的url
    public String toutuUrl;

    //飞猪出签率
    public String rightDesc;

    //飞猪flayerTitle
    public String flayerTitle;

    //飞猪里程
    public String mileageTitle;

    //预售商品已预订的数量
    public String orderedItemAmount;

    //
    public String depositPriceDesc;

    //双十一运营panel标签
    public String marketingIconPanel;


    //双十一运营panel标签
    public Boolean marketingIconPanelShowAll = false;


    //购物津贴icon
    public String couponIcon;

    //购物津贴Text
    public String couponText;

    //天猫超市商品重量
    public String weight;






}
