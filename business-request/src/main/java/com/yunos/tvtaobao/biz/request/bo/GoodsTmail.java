/**
 * $
 * PROJECT NAME: business-request
 * PACKAGE NAME: com.yunos.tvtaobao.biz.request.bo
 * FILE NAME: GoodsTmail.java
 * CREATED TIME: 2015年11月13日
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
package com.yunos.tvtaobao.biz.request.bo;


import android.text.Html;
import android.text.TextUtils;

import com.yunos.tv.core.common.AppDebug;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;
import com.alibaba.fastjson.JSON;

/**
 * 搜索天猫的商品数据
 * Class Descripton.
 *
 * @author mi.cao
 * @data 2015年11月13日 下午4:17:09
 */

public class GoodsTmail implements Serializable, SearchedGoods {

    /**
     *
     */
    private static final long serialVersionUID = 8908397788800721428L;

    /**
     * 商品id
     */
    private String item_id;
    /**
     * 商品标题
     */
    private String title;
    /**
     * detail链接
     */
    private String url;
    /**
     * 图片地址
     */
    private String img;
    /**
     * sku最优价格
     */
    private String price;
    /**
     * 无线专享价
     */
    private String wm_price;
    /**
     * 月销量
     */
    private String sold;
    /**
     * 邮费
     */
    private String post_fee;
    /**
     * 店铺id
     */
    private String shop_id;
    /**
     * 评论数
     */
    private String comment_num;
    /**
     * 商品标签
     */
    private String auction_tag;
    /**
     * 店铺名称
     */
    private String shop_name;
    /**
     * 国家代码
     */
    private String country_code;


    private String type;

    private String uri;

    private String s11;

    private String s11_pre;


    public void setPre(boolean pre) {
        this.pre = pre;
    }

    public void setBuyCashback(boolean buyCashback) {
        this.buyCashback = buyCashback;
    }

    private boolean pre;

    private boolean buyCashback;

    private String nick;

    private String tagPicUrl;

    private List<String> tagNames;

    private String soldText;

    public String getSoldText() {
        return soldText;
    }

    public void setSoldText(String soldText) {
        this.soldText = soldText;
    }

    @Override
    public List<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(List<String> tagNames) {
        this.tagNames = tagNames;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEurl() {
        return null;
    }

    @Override
    public String getType() {
        return type;
    }

    /**
     * 设置商品ID
     *
     * @param item_id
     */
    public void setItemId(String item_id) {
        this.item_id = item_id;
    }

    /**
     * 设置商品标题
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 商品详情url
     *
     * @param url
     */
    public void setDetailUrl(String url) {
        this.url = url;
    }

    /**
     * 商品图片url
     *
     * @param img
     */
    public void setImgUrl(String img) {
        this.img = img;
    }

    /**
     * 商品sku最优价格
     *
     * @param price
     */
    public void setPrice(String price) {
        this.price = price;
    }

    /**
     * 商品无线专享价格
     *
     * @param wm_price
     */
    public void setWmPrice(String wm_price) {
        this.wm_price = wm_price;
    }

    /**
     * 商品月销量
     *
     * @param sold
     */
    public void setSold(String sold) {
        this.sold = sold;
    }

    /**
     * 商品邮费
     *
     * @param post_fee
     */
    public void setPostFee(String post_fee) {
        this.post_fee = post_fee;
    }

    /**
     * 商品店铺id
     *
     * @param shop_id
     */
    public void setShopId(String shop_id) {
        this.shop_id = shop_id;
    }

    /**
     * 商品评论数
     *
     * @param comment_num
     */
    public void setCommentNum(String comment_num) {
        this.comment_num = comment_num;
    }

    /**
     * 商品标签
     *
     * @param auction_tag
     */
    public void setAuctionTag(String auction_tag) {
        this.auction_tag = auction_tag;
    }

    /**
     * 店铺名称
     *
     * @param shop_name
     */
    public void setShopName(String shop_name) {
        this.shop_name = shop_name;
    }

    /**
     * 国家代码
     *
     * @param country_code
     */
    public void setCountryCode(String country_code) {
        this.country_code = country_code;
    }

    /**
     * 获取商品ID
     */
    public String getItemId() {
        return item_id;
    }

    /**
     * 获取商品标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 商品详情url
     */
    public String getDetailUrl() {
        return url;
    }

    /**
     * 商品图片url
     */
    public String getImgUrl() {
        return img;
    }

    /**
     * 商品sku最优价格
     */
    public String getPrice() {
        return price;
    }

    /**
     * 商品无线专享价格
     */
    public String getWmPrice() {
        return wm_price;
    }

    /**
     * 商品月销量
     */
    public String getSold() {
        return sold;
    }

    /**
     * 商品邮费
     */
    public String getPostFee() {
        return post_fee;
    }

    /**
     * 商品店铺id
     */
    public String getShopId() {
        return shop_id;
    }

    /**
     * 商品评论数
     */
    public String getCommentNum() {
        return comment_num;
    }

    /**
     * 商品标签
     */
    public String getAuctionTag() {
        return auction_tag;
    }

    /**
     * 店铺名称
     */
    public String getShopName() {
        return shop_name;
    }

    /**
     * 国家代码
     */
    public String getCountryCode() {
        return country_code;
    }

    @Override
    public String toString() {
        String text = "[ item_id = " + item_id + ", title = " + title + ", url = " + url + ", img = " + img
                + ", price = " + price + ", wm_price = " + wm_price + ", sold = " + sold + ", post_fee = " + post_fee
                + ", shop_id = " + shop_id + ", shop_name = " + shop_name + ", comment_num = " + comment_num
                + ", auction_tag = " + auction_tag + ", country_code = " + country_code + " ]";
        return text;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public String getS11() {
        return s11;
    }

    @Override
    public String getS11Pre() {
        return s11_pre;
    }

    @Override
    public Boolean isPre() {
        return pre;
    }

    @Override
    public Boolean isBuyCashback() {
        return buyCashback;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    @Override
    public String getNick() {
        return nick;
    }

    /**
     * 返利
     */
    private RebateBo rebateBo;

    @Override
    public void setRebateBo(RebateBo rebateBo) {
        this.rebateBo = rebateBo;
    }

    @Override
    public RebateBo getRebateBo() {
        return rebateBo;
    }

    @Override
    public String getTagPicUrl() {
        return tagPicUrl;
    }

    public void setTagPicUrl(String tagPicUrl) {
        this.tagPicUrl = tagPicUrl;
    }

    /**
     * 解析商品数据
     *
     * @return
     */
    public static GoodsTmail resolveFromJson(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }

        GoodsTmail goods = new GoodsTmail();
        if (!obj.isNull("title")) {
            String title = obj.getString("title");
            //替换换行符
            title = title.replaceAll("\r\n", " ").replaceAll("\n", " ").replaceAll("\r", " ");
            title = Html.fromHtml(title).toString();
            goods.setTitle(title);
        }

        if (!obj.isNull("uri")) {
            goods.uri = (obj.getString("uri"));
        }

        if (!obj.isNull("type")) {
            goods.setType(obj.getString("type"));
        }

        if (!obj.isNull("tid")) {
            goods.setItemId(obj.getString("tid"));
        }

        if (!obj.isNull("price")) {
            goods.setPrice(obj.getString("price"));
        }

        if (!obj.isNull("discount")) {
            goods.setWmPrice(obj.getString("discount"));
        }

        if (!obj.isNull("sold")) {// 如果销量为空，或者销量第一个字符不是数字，则销量置为0
            String sold = obj.getString("sold");
            sold = sold.trim();
            if (TextUtils.isEmpty(sold)) {
                sold = "0";
            } else {
                String temp = sold.substring(0, 1);
                if (!TextUtils.isDigitsOnly(temp)) {
                    sold = "0";
                }
            }
            goods.setSold(sold);
        }

        if (!obj.isNull("url")) {
            goods.setDetailUrl(obj.getString("url"));
        }

        if (!obj.isNull("shopId")) {
            goods.setShopId(obj.getString("shopId"));
        }

        if (!obj.isNull("shopTitle")) {
            goods.setShopName(obj.getString("shopTitle"));
        }

        if (!obj.isNull("post")) {
            goods.setPostFee(obj.getString("post"));
        }

        if (!obj.isNull("picPath")) {
            goods.setImgUrl(obj.getString("picPath"));
        } else if (!obj.isNull("picUrl")) {
            goods.setImgUrl(obj.getString("picUrl"));
        }

        if (!obj.isNull("commentCount")) {
            goods.setCommentNum(obj.getString("commentCount"));
        }

        if (!obj.isNull("auctionTags")) {
            goods.setAuctionTag(obj.getString("auctionTags"));
        }

        if (!obj.isNull("countryCode")) {
            goods.setCountryCode(obj.getString("countryCode"));
        }

        if (!obj.isNull("s11")) {
            goods.s11 = obj.getString("s11");
        }

        if (!obj.isNull("s11_pre")) {
            goods.s11_pre = obj.getString("s11_pre");
        }

        if (!obj.isNull("pre")) {
            goods.pre = obj.getBoolean("pre");
        }

        if (!obj.isNull("buyCashback")) {
            goods.buyCashback = obj.getBoolean("buyCashback");
        }

        if (!obj.isNull("nick")) {
            goods.setNick(obj.getString("nick"));
        }

        if (!obj.isNull("tagPicUrl")) {
            goods.setTagPicUrl(obj.getString("tagPicUrl"));
        }

        if (!obj.isNull("tagNames")) {
            goods.setTagNames(JSON.parseArray(obj.optString("tagNames"),String.class));
        }

        if (!obj.isNull("soldText")) {
            goods.setSoldText(obj.getString("soldText"));
        }

        AppDebug.v("GoodsTmail", "GoodsTmail.resolveFromJson.goods = " + goods);
        return goods;
    }
}
