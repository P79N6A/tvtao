package com.yunos.tvtaobao.biz.request.bo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by libin on 16/9/18.
 */

public class TBaoShopBean implements Serializable{

    /**
     * buyCount : 3
     * favored : false
     * groupNum : 0
     * itemId : 538389192351
     * itemName : 西西小可2016秋季女装新款定制韩版贴标上衣中长款拉链开衫外套
     * itemPic : //gw.alicdn.com/bao/uploaded/i2/13410985/TB2dXQ_aqa5V1Bjy0FaXXaXvpXa_!!13410985.jpg
     * itemPrice : 218.40
     * itemUrl : http://taoke.mdaren.taobao.com/item.htm?itemId=538389192351&accountId=13410985&bizType=taolive
     */

    private List<ItemListBean.GoodsListBean> hotList;
    /**
     * goodsIndex : 11
     * goodsList : [{"buyCount":"492","favored":"false","groupNum":"0","itemId":"538307490078","itemName":"西西小可*2016秋装新款韩版毛呢外套女装宽松粉色大衣中长款包邮","itemPic":"//gw.alicdn.com/bao/uploaded/i3/13410985/TB2p8W.aGi5V1BjSszbXXb0hVXa_!!13410985.jpg","itemPrice":"298.00","itemUrl":"http://taoke.mdaren.taobao.com/item.htm?itemId=538307490078&accountId=13410985&bizType=taolive"}]
     */

    private List<ItemListBean> itemList;

    public List<ItemListBean.GoodsListBean> getHotList() {
        return hotList;
    }

    public void setHotList(List<ItemListBean.GoodsListBean> hotList) {
        this.hotList = hotList;
    }

    public List<ItemListBean> getItemList() {
        return itemList;
    }

    public void setItemList(List<ItemListBean> itemList) {
        this.itemList = itemList;
    }

    public static class ItemListBean implements Serializable{
        private String goodsIndex;
        /**
         * buyCount : 492
         * favored : false
         * groupNum : 0
         * itemId : 538307490078
         * itemName : 西西小可*2016秋装新款韩版毛呢外套女装宽松粉色大衣中长款包邮
         * itemPic : //gw.alicdn.com/bao/uploaded/i3/13410985/TB2p8W.aGi5V1BjSszbXXb0hVXa_!!13410985.jpg
         * itemPrice : 298.00
         * itemUrl : http://taoke.mdaren.taobao.com/item.htm?itemId=538307490078&accountId=13410985&bizType=taolive
         */

        private List<GoodsListBean> goodsList;

        public String getGoodsIndex() {
            return goodsIndex;
        }

        public void setGoodsIndex(String goodsIndex) {
            this.goodsIndex = goodsIndex;
        }

        public List<GoodsListBean> getGoodsList() {
            return goodsList;
        }

        public void setGoodsList(List<GoodsListBean> goodsList) {
            this.goodsList = goodsList;
        }

        public static class GoodsListBean {
            private String buyCount;
            private String favored;
            private String groupNum;
            private String itemId;
            private String itemName;
            private String itemPic;
            private String itemPrice;
            private String itemUrl;

            public String getBuyCount() {
                return buyCount;
            }

            public void setBuyCount(String buyCount) {
                this.buyCount = buyCount;
            }

            public String getFavored() {
                return favored;
            }

            public void setFavored(String favored) {
                this.favored = favored;
            }

            public String getGroupNum() {
                return groupNum;
            }

            public void setGroupNum(String groupNum) {
                this.groupNum = groupNum;
            }

            public String getItemId() {
                return itemId;
            }

            public void setItemId(String itemId) {
                this.itemId = itemId;
            }

            public String getItemName() {
                return itemName;
            }

            public void setItemName(String itemName) {
                this.itemName = itemName;
            }

            public String getItemPic() {
                return itemPic;
            }

            public void setItemPic(String itemPic) {
                this.itemPic = itemPic;
            }

            public String getItemPrice() {
                return itemPrice;
            }

            public void setItemPrice(String itemPrice) {
                this.itemPrice = itemPrice;
            }

            public String getItemUrl() {
                return itemUrl;
            }

            public void setItemUrl(String itemUrl) {
                this.itemUrl = itemUrl;
            }
        }
    }
}
