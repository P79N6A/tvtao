package com.yunos.tvtaobao.biz.request.bo;

import java.util.List;

/**
 * Created by pan on 16/9/29.
 */

public class TMallLiveShopList {

    /**
     * data : [{"gmtCreate":"1474871304000","gmtModified":"1475064403000","id":"38639","itemAddress":"//detail.tmall.com/item.htm?id=41060368715","itemId":"41060368715","onShelf":"true","picUrl":"//img.alicdn.com/tfscom/i4/i2/TB1UZnlNpXXXXbUXVXXXXXXXXXX_!!0-item_pic.jpg","price":"1355.0","title":"【润扬民乐】全泡桐专业素面演奏古筝 返璞归真 限量版 黑檀琴码"},{"gmtCreate":"1474871281000","gmtModified":"1474871281000","id":"38636","itemAddress":"//detail.tmall.com/item.htm?id=20233901149","itemId":"20233901149","onShelf":"true","picUrl":"//img.alicdn.com/tfscom/i4/i3/TB1ntCENpXXXXbhXFXXXXXXXXXX_!!0-item_pic.jpg","price":"1488.0","title":"【润扬民乐】国家专利 大师螺钿天然嵌贝演奏古筝 扬州演奏乐器"},{"gmtCreate":"1474871262000","gmtModified":"1474871262000","id":"38635","is1111":"true","itemAddress":"//detail.tmall.com/item.htm?id=16545417301","itemId":"16545417301","onShelf":"true","picUrl":"//img.alicdn.com/tfscom/i4/i4/TB1DKl.NpXXXXavXFXXXXXXXXXX_!!0-item_pic.jpg","price":"2998.0","title":"【润扬民乐】正宗金丝老楠木深雕九龙古筝 有收藏证书 进口鱼鳞松"},{"gmtCreate":"1474871229000","gmtModified":"1474871229000","id":"38630","itemAddress":"//detail.tmall.com/item.htm?id=43872614449","itemId":"43872614449","onShelf":"true","picUrl":"//img.alicdn.com/tfscom/i4/i3/TB1RW5aNpXXXXXQXFXXXXXXXXXX_!!0-item_pic.jpg","price":"1478.0","title":"润扬专业浮雕楠木实木回纹古筝 大师签名考级演奏乐器一级梧桐木"},{"gmtCreate":"1474871200000","gmtModified":"1474871200000","id":"38627","itemAddress":"//detail.tmall.com/item.htm?id=37862488621","itemId":"37862488621","onShelf":"true","picUrl":"//img.alicdn.com/tfscom/i2/i4/TB1X98.NpXXXXaJXFXXXXXXXXXX_!!0-item_pic.jpg","price":"799.0","title":"【润扬民乐】古筝百凤朝阳挖嵌琴 初学者考级 扬州专业演奏乐器"}]
     * endId : 38627
     * endTime : 1474871200000
     * startId : 38639
     * startTime : 1475064403000
     */

    private ModelBean model;

    public ModelBean getModel() {
        return model;
    }

    public void setModel(ModelBean model) {
        this.model = model;
    }

    public static class ModelBean {
        private String endId;
        private String endTime;
        private String startId;
        private String startTime;
        /**
         * gmtCreate : 1474871304000
         * gmtModified : 1475064403000
         * id : 38639
         * itemAddress : //detail.tmall.com/item.htm?id=41060368715
         * itemId : 41060368715
         * onShelf : true
         * picUrl : //img.alicdn.com/tfscom/i4/i2/TB1UZnlNpXXXXbUXVXXXXXXXXXX_!!0-item_pic.jpg
         * price : 1355.0
         * title : 【润扬民乐】全泡桐专业素面演奏古筝 返璞归真 限量版 黑檀琴码
         */

        private List<DataBean> data;

        public String getEndId() {
            return endId;
        }

        public void setEndId(String endId) {
            this.endId = endId;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getStartId() {
            return startId;
        }

        public void setStartId(String startId) {
            this.startId = startId;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {
            private String gmtCreate;
            private String gmtModified;
            private String id;
            private String itemAddress;
            private String itemId;
            private String onShelf;
            private String picUrl;
            private String price;
            private String title;
            private boolean favorate = false;

            public void setFavorate(boolean favorate) {
                this.favorate = favorate;
            }

            public boolean getFavorate() {
                return favorate;
            }

            public String getGmtCreate() {
                return gmtCreate;
            }

            public void setGmtCreate(String gmtCreate) {
                this.gmtCreate = gmtCreate;
            }

            public String getGmtModified() {
                return gmtModified;
            }

            public void setGmtModified(String gmtModified) {
                this.gmtModified = gmtModified;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getItemAddress() {
                return itemAddress;
            }

            public void setItemAddress(String itemAddress) {
                this.itemAddress = itemAddress;
            }

            public String getItemId() {
                return itemId;
            }

            public void setItemId(String itemId) {
                this.itemId = itemId;
            }

            public String getOnShelf() {
                return onShelf;
            }

            public void setOnShelf(String onShelf) {
                this.onShelf = onShelf;
            }

            public String getPicUrl() {
                return picUrl;
            }

            public void setPicUrl(String picUrl) {
                this.picUrl = picUrl;
            }

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }
    }
}
