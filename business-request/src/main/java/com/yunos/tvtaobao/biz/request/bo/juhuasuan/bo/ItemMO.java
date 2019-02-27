package com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo;

import com.yunos.tvtaobao.biz.request.utils.VideoUrlUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

/**
 * 商品信息
 * @author tianxiang
 * @date 2012-10-8 12:33:54
 */
public class ItemMO implements Serializable {

    private enum ItemMoStateEnum {
        WAIT_FOR_START, AVAIL_BUY, EXIST_HOLDER, NO_STOCK, OUT_OF_TIME
    };

    private static final long serialVersionUID = -7971671584850072946L;

    /**
     * 商品id，同交易线商品id
     */
    private Long itemId;

    //聚划算商品ID
    private Long juId;

    /**
     * 商品长名称
     */
    private String longName;

    /**
     * 商品短名称
     */
    private String shortName;

    /**
     * 商品url
     */
    private String itemUrl;

    /**
     * 商品数量
     */
    private Integer itemCount;

    /**
     * 父类目
     */
    private Long parentCategory;

    /**
     * 子类目
     */
    private Long childCategory;

    /**
     * 店铺类型
     */
    private Integer shopType;

    /**
     * 是否包邮
     */
    private Integer payPostage;

    /**
     * 商品原价
     */
    private Long originalPrice;

    /**
     * 商品图片url
     */
    private String picUrl;
    
    /**
     * 商品图片url
     */
    private String picFullUrl;

    /**
     * 商品图片url 3:2的新宽图
     */
    private String picWideUrl;

    private byte[] picWideBytes;

    /**
     * 商品演示视频url，无演示视频则为null或length为0
     */
    private String videoUrl;

    /**
     * 团购价
     */
    private Long activityPrice;

    /**
     * 所在城市
     */
    private String city;

    /**
     * 宝贝描述
     */
    private String itemDesc;
    /**
     * 商品状态
     */
    private Integer itemStatus;
    /**
     * 商品保障
     */
    private String itemGuarantee;
    /**
     * 折扣率
     */
    private Double discount;

    /**
     * 审核意见
     */
    private String checkComment;

    /**
     * 平台id
     */
    private Long platformId;

    /**
     * 组id
     */
    private Long groupId;

    /**
     * 卖家id
     */
    private Long sellerId;

    /**
     * 卖家昵称
     */
    private String sellerNick;

    /**
     * 卖家星级
     */
    private Integer sellerCredit;

    /**
     * 存储forest的类目id
     */
    private Long categoryId;

    /**
     * 购买人数
     */
    private int soldCount;
    /**
     * 创建时间
     */
    private Date gmtCreate;
    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 限购数量
     */
    private int limitNum;

    /**
     * 商品是否在商品线被锁定（商家无法修改） 0代表解锁，1代表锁定
     */
    private Integer isLock;

    /**
     * 上架开始时间（取毫秒）
     */
    private Long onlineStartTime;
    /**
     * 上架结束时间（取毫秒）
     */
    private Long onlineEndTime;

    /**
     * 聚划算预览用key
     */
    private String key;

    /**
     * 显示标签
     */
    private String imageLabel;

    /**
     * 是否提前开团
     */
    private Boolean preOnline;

    // 标签名称
    private String pushName;

    /**
     * 属性
     */
    private String attributes;

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ItemMO [itemId=" + itemId + ", juId=" + juId + ", longName=" + longName + ", shortName=" + shortName
                + ", itemUrl=" + itemUrl + ", itemCount=" + itemCount + ", parentCategory=" + parentCategory
                + ", childCategory=" + childCategory + ", shopType=" + shopType + ", payPostage=" + payPostage
                + ", originalPrice=" + originalPrice + ", picUrl=" + picUrl + ", picWideUrl=" + picWideUrl
                + ", picWideBytes=" + Arrays.toString(picWideBytes) + ", videoUrl=" + videoUrl + ", activityPrice="
                + activityPrice + ", city=" + city + ", itemStatus=" + itemStatus + ", itemGuarantee=" + itemGuarantee
                + ", discount=" + discount + ", checkComment=" + checkComment + ", platformId=" + platformId
                + ", groupId=" + groupId + ", sellerId=" + sellerId + ", sellerNick=" + sellerNick + ", sellerCredit="
                + sellerCredit + ", categoryId=" + categoryId + ", soldCount=" + soldCount + ", gmtCreate=" + gmtCreate
                + ", gmtModified=" + gmtModified + ", limitNum=" + limitNum + ", isLock=" + isLock
                + ", onlineStartTime=" + onlineStartTime + ", onlineEndTime=" + onlineEndTime + ", key=" + key
                + ", imageLabel=" + imageLabel + ", preOnline=" + preOnline + ", pushName=" + pushName
                + ", attributes=" + attributes + ", itemDesc=" + itemDesc + "]";
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getItemUrl() {
        return itemUrl;
    }

    public void setItemUrl(String itemUrl) {
        this.itemUrl = itemUrl;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    public Long getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(Long parentCategory) {
        this.parentCategory = parentCategory;
    }

    public Long getChildCategory() {
        return childCategory;
    }

    public void setChildCategory(Long childCategory) {
        this.childCategory = childCategory;
    }

    public Integer getShopType() {
        return shopType;
    }

    public void setShopType(Integer shopType) {
        this.shopType = shopType;
    }

    public Integer getPayPostage() {
        return payPostage;
    }

    public void setPayPostage(Integer payPostage) {
        this.payPostage = payPostage;
    }

    public Long getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Long originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getPicFullUrl() {
        return picFullUrl;
    }

    public void setPicFullUrl(String picFullUrl) {
        this.picFullUrl = picFullUrl;
    }

    public String getPicWideUrl() {
        return picWideUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setPicWideUrl(String picWideUrl) {
        this.picWideUrl = picWideUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Long getActivityPrice() {
        return activityPrice;
    }

    public void setActivityPrice(Long activityPrice) {
        this.activityPrice = activityPrice;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public Integer getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(Integer itemStatus) {
        this.itemStatus = itemStatus;
    }

    public String getItemGuarantee() {
        return itemGuarantee;
    }

    public void setItemGuarantee(String itemGuarantee) {
        this.itemGuarantee = itemGuarantee;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public String getCheckComment() {
        return checkComment;
    }

    public void setCheckComment(String checkComment) {
        this.checkComment = checkComment;
    }

    public Long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerNick() {
        return sellerNick;
    }

    public void setSellerNick(String sellerNick) {
        this.sellerNick = sellerNick;
    }

    public Integer getSellerCredit() {
        return sellerCredit;
    }

    public void setSellerCredit(Integer sellerCredit) {
        this.sellerCredit = sellerCredit;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public int getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(int soldCount) {
        this.soldCount = soldCount;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public int getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(int limitNum) {
        this.limitNum = limitNum;
    }

    public Integer getIsLock() {
        return isLock;
    }

    public void setIsLock(Integer isLock) {
        this.isLock = isLock;
    }

    public Long getOnlineStartTime() {
        return onlineStartTime;
    }

    public void setOnlineStartTime(Long onlineStartTime) {
        this.onlineStartTime = onlineStartTime;
    }

    public Long getOnlineEndTime() {
        return onlineEndTime;
    }

    public void setOnlineEndTime(Long onlineEndTime) {
        this.onlineEndTime = onlineEndTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getImageLabel() {
        return imageLabel;
    }

    public void setImageLabel(String imageLabel) {
        this.imageLabel = imageLabel;
    }

    public Boolean getPreOnline() {
        return preOnline;
    }

    public void setPreOnline(Boolean preOnline) {
        this.preOnline = preOnline;
    }

    public String getPushName() {
        return pushName;
    }

    public void setPushName(String pushName) {
        this.pushName = pushName;
    }

    /**
     * 是否未开始
     * @return
     */
    public boolean isNotStart() {
        return itemStatus == ItemMoStateEnum.WAIT_FOR_START.ordinal();
    }

    /**
     * 是否已结束
     * @return
     */
    public boolean isOver() {
        return itemStatus == ItemMoStateEnum.OUT_OF_TIME.ordinal();
    }

    /**
     * 是否卖完了
     * @return
     */
    public boolean isNoStock() {
        return itemStatus == ItemMoStateEnum.NO_STOCK.ordinal() || itemStatus == ItemMoStateEnum.EXIST_HOLDER.ordinal();
    }

    /**
     * 是否可购买
     * @return
     */
    public boolean isAbleBuy() {
        return itemStatus == ItemMoStateEnum.AVAIL_BUY.ordinal();
    }



    public static ItemMO fromMTOP(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }
        // 无用字段categoryId,itemCount
        ItemMO item = null;
        try {
            item = new ItemMO();
            item.setJuId(obj.optLong("juId"));
            item.setActivityPrice(obj.optLong("activityPrice"));
            if (obj.has("categoryId")) {
                item.setCategoryId(obj.optLong("categoryId"));
            }
            if (obj.has("childCategory")) {
                item.setChildCategory(obj.optLong("childCategory"));
            }
            if (obj.has("city")) {
                item.setCity(obj.optString("city"));
            }
            item.setDiscount(obj.optDouble("discount"));
            item.setGroupId(obj.optLong("groupId"));
            item.setIsLock(obj.optInt("isLock"));
            item.setItemCount(obj.optInt("itemCount"));
            item.setItemGuarantee(obj.optString("itemGuarantee"));
            item.setItemId(obj.optLong("itemId"));
            item.setItemStatus(obj.optInt("itemStatus", -1));
            item.setLimitNum(obj.optInt("limitNum"));
            item.setLongName(obj.optString("longName"));
            item.setOnlineEndTime(obj.optLong("onlineEndTime"));
            item.setOnlineStartTime(obj.optLong("onlineStartTime"));
            item.setOriginalPrice(obj.optLong("originalPrice"));
            item.setParentCategory(obj.optLong("parentCategory"));
            item.setPayPostage(obj.optInt("payPostage"));
            item.setPicUrl(obj.optString("picUrl"));
            item.setPicFullUrl(obj.optString("fullPicUrl"));
            item.setPicWideUrl(obj.optString("picWideUrl"));

            if (obj.has("videoURL")) {
                item.setVideoUrl(VideoUrlUtil.toM3u8(obj.getString("videoURL")));
            } else {
                item.setVideoUrl("");
            }
            String[] urls = { "http://cloud.video.taobao.com/play/u/916927465/p/1/e/2/t/1/d//fv/10520505.m3u8",
                    "http://chyd-alivod.wasu.tv/data9/ott/344/2013-08/05/1375688697845_881704.m3u8",
                    "http://chyd-alivod.wasu.tv/data9/ott/344/2013-08/05/1375688681871_994786.m3u8",
                    "http://chyd-alivod.wasu.tv/data9/ott/344/2013-08/05/1375688609065_225617.m3u8",
                    "http://chyd-alivod.wasu.tv/data9/ott/344/2013-03/19/1363645123127_454344.m3u8",
                    "http://chyd-alivod.wasu.tv/data9/ott/344/2013-04/28/1367131771833_320555.m3u8",
                    "http://chyd-alivod.wasu.tv/data9/ott/344/2013-08/05/1375681540365_129665.m3u8",
                    "http://chyd-alivod.wasu.tv/data9/ott/344/2013-08/05/1375681528463_594388.m3u8",
                    "http://chyd-alivod.wasu.tv/data9/ott/344/2013-08/05/1375681450431_295144.m3u8",
                    "http://chyd-alivod.wasu.tv/data9/ott/344/2013-03/15/1363318048722_914408.m3u8",
                    "http://chyd-alivod.wasu.tv/data8/ott/340/2012-12/06/1354785613310_815508.m3u8",
                    "http://chyd-alivod.wasu.tv/data8/ott/344/2012-12/06/1354785613231_972457.m3u8",
                    "http://chyd-alivod.wasu.tv/data7/ott1/340/2012-11/27/1354008293948_502287.m3u8",
                    "http://chyd-alivod.wasu.tv/data9/ott/344/2013-04/19/1366348611883_909390.m3u8",
                    "http://chyd-alivod.wasu.tv/data8/ott/340/2012-12/05/1354701577528_867977.m3u8",
                    "http://chyd-alivod.wasu.tv/data9/ott/344/2013-03/15/1363342317891_459789.m3u8",
                    "http://chyd-alivod.wasu.tv/data8/ott/340/2012-12/28/1356686366242_874419.m3u8",
                    "http://chyd-alivod.wasu.tv/data9/ott/344/2013-03/12/1363064081441_267119.m3u8" };

            //item.setVideoUrl(urls[(int) (Math.random() * urls.length)]);
            //item.setVideoUrl(urls[0]);

            //item.setVideoUrl(obj.optString("video_url"));
            item.setPlatformId(obj.optLong("platformId"));
            item.setPreOnline(obj.optBoolean("preOnline"));
            item.setSellerId(obj.optLong("sellerId"));
            item.setSellerNick(obj.optString("sellerNick"));
            item.setShopType(obj.optInt("shopType"));
            item.setShortName(obj.optString("shortName"));
            item.setSoldCount(obj.optInt("soldCount"));

            if (obj.has("sellerCredit")) {
                item.setSellerCredit(obj.getInt("sellerCredit"));
            }
            if (obj.has("attributes")) {
                item.setAttributes(obj.getString("attributes"));
            }
            if (obj.has("checkComment")) {
                item.setCheckComment(obj.getString("checkComment"));
            }

            if (obj.has("imageLabel")) {
                item.setImageLabel(obj.getString("imageLabel"));
            }
            if (obj.has("itemDesc")) {
                item.setItemDesc(obj.getString("itemDesc"));
            }
            if (obj.has("itemUrl")) {
                item.setItemUrl(obj.getString("itemUrl"));
            }

            if (obj.has("key")) {
                item.setKey(obj.getString("key"));
            }
            if (obj.has("pushName")) {
                item.setPushName(obj.getString("pushName"));
            }
            item = checkJuItem(item);
        } catch (Exception e) {
            item = null;
            e.printStackTrace();
        }

        return item;
    }

    /**
     * top的商品列表，聚透，品牌团
     * @date 2012-12-18下午4:18:47
     * @param obj
     * @return
     * @throws JSONException
     */
    public static ItemMO fromTOP(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }
        // 无用字段categoryId,itemCount
        ItemMO item = null;
        try {
            item = new ItemMO();
            item.setJuId(obj.optLong("juId"));
            item.setItemId(obj.optLong("item_id"));
            item.setPicUrl(obj.optString("pic_url"));
            item.setVideoUrl("http://cloud.video.taobao.com/play/u/916927465/p/1/e/4/t/1/d//fv/10520505.m3u8");
            //item.setVideoUrl(obj.optString("video_url"));
            item.setLongName(obj.optString("long_name"));
            item.setShortName(obj.optString("short_name"));
            item.setItemUrl(obj.optString("item_url"));
            item.setItemCount(obj.optInt("item_count"));
            item.setParentCategory(obj.optLong("parent_category"));
            item.setChildCategory(obj.optLong("child_category"));
            item.setShopType(obj.optInt("shop_type"));
            item.setPayPostage(obj.optInt("pay_postage"));
            item.setOriginalPrice(obj.optLong("original_price"));
            item.setPicWideUrl(obj.optString("pic_wide_url"));
            item.setActivityPrice(obj.optLong("activity_price"));
            item.setCity(obj.optString("city"));
            item.setItemDesc(obj.optString("item_desc"));
            item.setItemStatus(obj.optInt("item_status"));
            item.setItemGuarantee(obj.optString("item_guarantee"));
            item.setDiscount(obj.optDouble("discount"));
            item.setCheckComment(obj.optString("check_comment"));
            item.setPlatformId(obj.optLong("platform_id"));
            item.setGroupId(obj.optLong("group_id"));
            item.setSellerId(obj.optLong("seller_id"));
            item.setSellerNick(obj.optString("seller_nick"));
            item.setSellerCredit(obj.optInt("seller_credit"));
            item.setCategoryId(obj.optLong("category_id"));
            item.setSoldCount(obj.optInt("sold_count"));
            item.setLimitNum(obj.optInt("limit_num"));
            item.setOnlineStartTime(obj.optLong("online_start_time"));
            item.setOnlineEndTime(obj.optLong("online_end_time"));
            item.setKey(obj.optString("key"));
            item.setImageLabel(obj.optString("image_label"));
            item.setPreOnline(obj.optBoolean("pre_online"));
            item.setPushName(obj.optString("push_name"));
        } catch (Exception e) {
            item = null;
            e.printStackTrace();
        }

        return item;
    }

    private static ItemMO checkJuItem(ItemMO item) {
        if (item == null) {
            return null;
        }

        /*
         * if (item.getItemStatus() == -1) {
         * long currTime = ServerTimeSynchronizer.getCurrentTime();
         * if (currTime >= item.getOnlineStartTime()
         * && currTime <= item.getOnlineEndTime()) {
         * item.setItemStatus(1);
         * } else if (currTime < item.getOnlineStartTime()) {
         * item.setItemStatus(0);
         * } else {
         * item.setItemStatus(4);
         * }
         * }
         */

        if (item.getItemStatus() > 4 || item.getItemStatus() < 0
                || (item.getItemStatus() == 1 && (item.getOnlineEndTime() <= item.getOnlineStartTime()))) {
            item.setItemStatus(4);
        }

        return item;
    }

    public byte[] getPicWideBytes() {
        return picWideBytes;
    }

    public void setPicWideBytes(byte[] picWideBytes) {
        this.picWideBytes = picWideBytes;
    }

    public void updateItemData(ItemMO data) {
        if (data == null) {
            return;
        }
        setActivityPrice(data.getActivityPrice());
        setCategoryId(data.getCategoryId());
        setChildCategory(data.getChildCategory());
        setCity(data.getCity());
        setDiscount(data.getDiscount());
        setGroupId(data.getGroupId());
        setIsLock(data.getIsLock());
        setItemCount(data.getItemCount());
        setItemGuarantee(data.getItemGuarantee());
        setItemStatus(data.getItemStatus());
        setLimitNum(data.getLimitNum());
        setLongName(data.getLongName());
        setOnlineEndTime(data.getOnlineEndTime());
        setOnlineStartTime(data.getOnlineStartTime());
        setOriginalPrice(data.getOriginalPrice());
        setParentCategory(data.getParentCategory());
        setPayPostage(data.getPayPostage());
        setPicUrl(data.getPicUrl());
        setPicWideUrl(data.getPicWideUrl());
        setVideoUrl(data.getVideoUrl());
        setPlatformId(data.getPlatformId());
        setPreOnline(data.getPreOnline());
        setSellerId(data.getSellerId());
        setSellerNick(data.getSellerNick());
        setShopType(data.getShopType());
        setShortName(data.getShortName());
        setSoldCount(data.getSoldCount());
        setSellerCredit(data.getSellerCredit());
        setAttributes(data.getAttributes());
        setCheckComment(data.getCheckComment());
        setImageLabel(data.getImageLabel());
        setItemDesc(data.getItemDesc());
        setItemUrl(data.getItemUrl());
        setKey(data.getKey());
        setPushName(data.getPushName());
    }

    public Long getJuId() {
        return juId;
    }

    public void setJuId(Long juId) {
        this.juId = juId;
    }
}
