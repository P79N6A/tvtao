package com.yunos.tvtaobao.biz.request.bo;

import java.util.List;

import static com.yunos.tvtaobao.biz.request.bo.ItemListBean.Status.outStock;
import static com.yunos.tvtaobao.biz.request.bo.ItemListBean.Status.rest;

/**
 * Created by haoxiang on 2017/12/26.
 */

public class TakeOutBag {

    public List<CartItemListBean> __cartItemList;
    /**
     * agentFee : 400
     * canBuy : true
     * cartItemList : [{"amount":1,"cartId":705072540736,"checkoutMode":0,"createTime":1514253012000,"itemId":561086426913,"outItemId":1310823263,"packingFee":0,"pic":"//gw.alicdn.com///gw.alicdn.com/TLife/1513354801646/TB1sQYwixTI8KJjSspicjHM4FXa","quantity":9994,"reducePrice":0,"skuId":3511317260089,"skuName":"红豆奶茶","soldMode":"1","title":"~0添加·奶茶 珍珠/红豆+77松塔X3","totalPrice":1500,"totalPromotionPrice":0,"type":1,"unitPrice":1500,"valid":true},{"amount":1,"cartId":704903617823,"checkoutMode":0,"createTime":1514181684000,"itemId":558581995026,"outItemId":1273442297,"packingFee":0,"pic":"//gw.alicdn.com///gw.alicdn.com/TLife/1509439436063/TB1blL6XZnI8KJjSsziImf8QpXa","quantity":9973,"reducePrice":0,"skuId":0,"soldMode":"1","title":"古早黑糖·手工酸奶","totalPrice":1500,"totalPromotionPrice":0,"type":1,"unitPrice":1500,"valid":true},{"amount":3,"cartId":704650377970,"checkoutMode":0,"createTime":1514181472000,"itemId":560128341454,"outItemId":1299595189,"packingFee":0,"pic":"//gw.alicdn.com///gw.alicdn.com/TLife/1508230980356/TB1QUPziWagSKJjy0Fbfp1.mVXa","quantity":9998,"reducePrice":0,"skuId":0,"soldMode":"1","title":"乐悠下午茶~焦糖玛奇朵+77松塔","totalPrice":6000,"totalPromotionPrice":0,"type":1,"unitPrice":2000,"valid":true},{"amount":1,"cartId":704649306708,"checkoutMode":0,"createTime":1514181304000,"itemId":560035932327,"outItemId":1299590677,"packingFee":0,"pic":"//gw.alicdn.com///gw.alicdn.com/TLife/1509600533104/TB1agC3asnI8KJjSspeZFkwIpXa","quantity":9995,"reducePrice":0,"skuId":0,"skuProperties":[{"name":"华夫饼口味","value":"芒果"}],"soldMode":"1","title":"乐悠下午茶~焦糖玛奇朵+华夫饼","totalPrice":2000,"totalPromotionPrice":0,"type":1,"unitPrice":2000,"valid":true},{"amount":1,"cartId":704900971211,"checkoutMode":0,"createTime":1514181187000,"itemId":560032160295,"outItemId":1299534162,"packingFee":0,"pic":"//gw.alicdn.com///gw.alicdn.com/TLife/1508226733791/TB1brM7m3MPMeJjy1Xb3A7wxVXa","quantity":9999,"reducePrice":0,"skuId":0,"soldMode":"1","title":"乐悠下午茶~乳酸菌饮料+松塔","totalPrice":1500,"totalPromotionPrice":0,"type":1,"unitPrice":1500,"valid":true},{"amount":4,"cartId":704982074153,"checkoutMode":0,"createTime":1514181071000,"itemId":560124301207,"outItemId":1299524643,"packingFee":0,"pic":"//gw.alicdn.com///gw.alicdn.com/TLife/1508226172656/TB1U874X5sF4uJjSZFtTUpHwVXa","quantity":9992,"reducePrice":0,"skuId":3664016343708,"skuName":"黑糖","skuProperties":[{"name":"华夫饼口味","value":"芒果"}],"soldMode":"1","title":"乐悠下午茶~手工酸奶+华夫饼","totalPrice":7200,"totalPromotionPrice":0,"type":1,"unitPrice":1800,"valid":true},{"amount":8,"cartId":704980436597,"checkoutMode":0,"createTime":1514180976000,"itemId":560124301207,"outItemId":1299524644,"packingFee":0,"pic":"//gw.alicdn.com///gw.alicdn.com/TLife/1508226172656/TB1U874X5sF4uJjSZFtTUpHwVXa","quantity":9999,"reducePrice":0,"skuId":3664016343709,"skuName":"蔓越莓","skuProperties":[{"name":"华夫饼口味","value":"芒果"}],"soldMode":"1","title":"乐悠下午茶~手工酸奶+华夫饼","totalPrice":14400,"totalPromotionPrice":0,"type":1,"unitPrice":1800,"valid":true},{"amount":1,"cartId":704980016048,"checkoutMode":0,"createTime":1514180778000,"itemId":560035528417,"outItemId":1299594065,"packingFee":0,"pic":"//gw.alicdn.com///gw.alicdn.com/TLife/1508230660416/TB1JWYPiWagSKJjy0FgPYERqFXa","quantity":9994,"reducePrice":0,"skuId":0,"soldMode":"1","title":"乐悠下午茶~现磨卡布奇诺+松塔","totalPrice":2000,"totalPromotionPrice":0,"type":1,"unitPrice":2000,"valid":true},{"amount":1,"cartId":704977475733,"checkoutMode":0,"createTime":1514180714000,"itemId":563129122034,"outItemId":1339149388,"packingFee":0,"pic":"//gw.alicdn.com///gw.alicdn.com/TLife/1513824928513/TB17rgujf2H8KJjy0FcRV5DlFXa","quantity":10000,"reducePrice":0,"skuId":3710375955888,"skuName":"法国纯牛奶","skuProperties":[{"name":"酱包","value":"番茄酱"},{"name":"是否加热","value":"加热（生菜可能会黄）"}],"soldMode":"1","title":"意式\u201c好色的\u201d沙拉套餐","totalPrice":2000,"totalPromotionPrice":0,"type":1,"unitPrice":2000,"valid":true},{"amount":10,"cartId":704892970924,"checkoutMode":0,"createTime":1514180424000,"itemId":560188130456,"outItemId":1299573937,"packingFee":0,"pic":"//gw.alicdn.com///gw.alicdn.com/TLife/1511140178139/TB1zSCod_nI8KJjy0FfBD7doVXa","priceDesc":"含8份原价商品","quantity":9985,"reducePrice":1000,"skuId":0,"skuProperties":[{"name":"华夫饼口味","value":"香蕉"}],"soldMode":"1","title":"乐悠下午茶~现磨拿铁咖啡+华夫饼","totalPrice":20000,"totalPromotionPrice":18000,"type":1,"unitPrice":2000,"valid":true}]
     * deliverAmount : 2000
     * inDeliverRange : true
     * outStoreId : 157405237
     * packingFee : 0
     * payType : online
     * storeId : 175938221
     * tips : {"secondShowText":"会员减免配送费","secondUrl":"http://h5.m.taobao.com/takeout/market/vipCard.html","showText":"您还有未享受的权益","type":6}
     * title : 乐优乐丽手工酸奶（利尔达店）
     * totalPrice : 58100
     * totalPromotionPrice : 56100
     */

    public int agentFee;
    public boolean canBuy;
    public String buttonText;
    public int deliverAmount;
    public boolean inDeliverRange;
    public int outStoreId;
    public int packingFee;
    public String payType;
    public int storeId;
    public TipsBean tips;
    public String title;
    public int totalPrice;
    public int totalPromotionPrice;
    public List<CartItemListBean> cartItemList;

    public static class TipsBean {
        /**
         * secondShowText : 会员减免配送费
         * secondUrl : http://h5.m.taobao.com/takeout/market/vipCard.html
         * showText : 您还有未享受的权益
         * type : 6
         * categoryId: "-3" 缺少必选商品时返回
         */

        public String secondShowText;
        public String secondUrl;
        public String showText;
        public int type;
        public String categoryId;
    }

    public static class CartItemListBean {
        public boolean __isCompare;

        public enum Status {
            normal,           // 正常可购买 0选中
            edit,             // 编辑状态
            outStock,         // 没货了
            rest,             // 商家休息中
        }


        /**
         * amount : 1
         * cartId : 705072540736
         * checkoutMode : 0
         * createTime : 1514253012000
         * itemId : 561086426913
         * limitQuantity:1,
         * outItemId : 1310823263
         * packingFee : 0
         * pic : //gw.alicdn.com///gw.alicdn.com/TLife/1513354801646/TB1sQYwixTI8KJjSspicjHM4FXa
         * quantity : 9994
         * reducePrice : 0
         * skuId : 3511317260089
         * skuName : 红豆奶茶
         * soldMode : 1
         * title : ~0添加·奶茶 珍珠/红豆+77松塔X3
         * totalPrice : 1500
         * totalPromotionPrice : 0
         * type : 1
         * unitPrice : 1500
         * valid : true
         * skuProperties : [{"name":"华夫饼口味","value":"芒果"}]
         * priceDesc : 含8份原价商品
         */

        public int amount;
        public long cartId;
        public int checkoutMode;
        public long createTime;
        public String itemId;
        public String limitQuantity;
        public String outItemId;
        public int packingFee;
        public String pic;
        public int quantity;
        public int reducePrice;
        public long skuId;
        public String skuName;
        public String soldMode;
        public String title;
        public int totalPrice;
        public int totalPromotionPrice;
        public int type;
        public int unitPrice;
        public boolean valid;
        public String priceDesc;
        public List<SkuPropertiesBean> skuProperties;
        public boolean isPackingFee;

        public static class SkuPropertiesBean {
            /**
             * name : 华夫饼口味
             * value : 芒果
             */

            public String name;
            public String value;
        }

        public ItemListBean.Status getGoodStatus() {


            if (quantity <= 0) {
                return outStock;
            }

//            if (__isRest) {
//                return rest;
//            }

            if (amount > 0) {
                return ItemListBean.Status.edit;
            }

            return ItemListBean.Status.normal;
        }
    }
}
