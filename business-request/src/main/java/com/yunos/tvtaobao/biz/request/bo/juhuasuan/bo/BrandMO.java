package com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo;


import org.json.JSONException;
import org.json.JSONObject;

public class BrandMO extends BaseMO {

    private static final long serialVersionUID = -6777979610604470707L;
    private String code;
    private String name;
    private String juLogo;
    private String juBanner;
    private String juDiscount;
    private String juSlogo;
    private String juBrand_id;
    private Integer itemCount;

    /**
     * d注意这个时间是 {@link com.taobao.jusdk.ModelTranslator} 的转换来的，日期样式（Mon Jan 06 00:00:00 CST 2014）
     */
    private Long onlineStartTimeLong;
    private Long onlineEndTimeLong;
    /**
     * 主背景图：现在的策略是去第一个商品的第一个图
     */
    private String onlineEndTime;

    private String online;

    //    public CountList<ItemMO> indexedItemDOList;

    public static BrandMO fromTOP(JSONObject obj) throws JSONException {
        BrandMO item = new BrandMO();
        item.setCode(obj.getString("code"));
        item.setName(obj.optString("name"));
        item.setJuLogo(obj.optString("juLogo"));
        item.setJuBanner(obj.optString("juBanner"));
        item.setJuDiscount(obj.optString("juDiscount"));
        item.setJuSlogo(obj.optString("juSlogo"));
        item.setJuBrand_id(obj.optString("juBrand_id"));
        item.setItemCount(obj.optInt("itemCount"));
        item.setOnlineStartTimeLong(obj.optLong("onlineStartTimeLong"));
        item.setOnlineEndTimeLong(obj.optLong("onlineEndTimeLong"));
        item.setOnlineEndTime(obj.optString("onlineEndTime"));
        item.setOnline(obj.optString("online"));

        return item;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BrandMO [code=" + code + ", name=" + name + ", juLogo=" + juLogo + ", juBanner=" + juBanner
                + ", juDiscount=" + juDiscount + ", juSlogo=" + juSlogo + ", juBrand_id=" + juBrand_id + ", itemCount="
                + itemCount + ", onlineStartTimeLong=" + onlineStartTimeLong + ", onlineEndTimeLong="
                + onlineEndTimeLong + ", onlineEndTime=" + onlineEndTime + ", online=" + online + "]";
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the juLogo
     */
    public String getJuLogo() {
        return juLogo;
    }

    /**
     * @param juLogo the juLogo to set
     */
    public void setJuLogo(String juLogo) {
        this.juLogo = juLogo;
    }

    /**
     * @return the juBanner
     */
    public String getJuBanner() {
        return juBanner;
    }

    /**
     * @param juBanner the juBanner to set
     */
    public void setJuBanner(String juBanner) {
        this.juBanner = juBanner;
    }

    /**
     * @return the juDiscount
     */
    public String getJuDiscount() {
        return juDiscount;
    }

    /**
     * @param juDiscount the juDiscount to set
     */
    public void setJuDiscount(String juDiscount) {
        this.juDiscount = juDiscount;
    }

    /**
     * @return the juSlogo
     */
    public String getJuSlogo() {
        return juSlogo;
    }

    /**
     * @param juSlogo the juSlogo to set
     */
    public void setJuSlogo(String juSlogo) {
        this.juSlogo = juSlogo;
    }

    /**
     * @return the juBrand_id
     */
    public String getJuBrand_id() {
        return juBrand_id;
    }

    /**
     * @param juBrand_id the juBrand_id to set
     */
    public void setJuBrand_id(String juBrand_id) {
        this.juBrand_id = juBrand_id;
    }

    /**
     * @return the itemCount
     */
    public Integer getItemCount() {
        return itemCount;
    }

    /**
     * @param itemCount the itemCount to set
     */
    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    /**
     * @return the onlineStartTimeLong
     */
    public Long getOnlineStartTimeLong() {
        return onlineStartTimeLong;
    }

    /**
     * @param onlineStartTimeLong the onlineStartTimeLong to set
     */
    public void setOnlineStartTimeLong(Long onlineStartTimeLong) {
        this.onlineStartTimeLong = onlineStartTimeLong;
    }

    /**
     * @return the onlineEndTimeLong
     */
    public Long getOnlineEndTimeLong() {
        return onlineEndTimeLong;
    }

    /**
     * @param onlineEndTimeLong the onlineEndTimeLong to set
     */
    public void setOnlineEndTimeLong(Long onlineEndTimeLong) {
        this.onlineEndTimeLong = onlineEndTimeLong;
    }

    /**
     * @return the onlineEndTime
     */
    public String getOnlineEndTime() {
        return onlineEndTime;
    }

    /**
     * @param onlineEndTime the onlineEndTime to set
     */
    public void setOnlineEndTime(String onlineEndTime) {
        this.onlineEndTime = onlineEndTime;
    }

    /**
     * @return the online
     */
    public String getOnline() {
        return online;
    }

    /**
     * @param online the online to set
     */
    public void setOnline(String online) {
        this.online = online;
    }
}
