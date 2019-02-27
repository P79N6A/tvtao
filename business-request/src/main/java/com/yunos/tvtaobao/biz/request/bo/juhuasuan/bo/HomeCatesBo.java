package com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo;


import org.json.JSONException;
import org.json.JSONObject;

public class HomeCatesBo extends BaseMO {

    private static final long serialVersionUID = 5000582262085167573L;
    private String name;
    private String e_name;
    private String cid;
    private String type;
    private String bgcolor;
    private String icon;
    private String iconHl;
    private Boolean visible;

    public static HomeCatesBo resolveFromMTOP(JSONObject obj) throws JSONException {
        if (null == obj) {
            return null;
        }
        HomeCatesBo item = new HomeCatesBo();
        item.setName(obj.optString("name"));
        item.setE_name(obj.optString("e_name"));
        item.setCid(obj.optString("cid"));
        item.setType(obj.optString("type"));
        item.setBgcolor(obj.optString("bgcolor"));
        item.setIcon(obj.optString("icon"));
        item.setIconHl(obj.optString("iconHl"));
        item.setVisible(obj.optBoolean("visible"));
        return item;
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
     * @return the e_name
     */
    public String getE_name() {
        return e_name;
    }

    /**
     * @param e_name the e_name to set
     */
    public void setE_name(String e_name) {
        this.e_name = e_name;
    }

    /**
     * @return the cid
     */
    public String getCid() {
        return cid;
    }

    /**
     * @param cid the cid to set
     */
    public void setCid(String cid) {
        this.cid = cid;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the bgcolor
     */
    public String getBgcolor() {
        return bgcolor;
    }

    /**
     * @param bgcolor the bgcolor to set
     */
    public void setBgcolor(String bgcolor) {
        this.bgcolor = bgcolor;
    }

    /**
     * @return the icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @param icon the icon to set
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * @return the iconHl
     */
    public String getIconHl() {
        return iconHl;
    }

    /**
     * @param iconHl the iconHl to set
     */
    public void setIconHl(String iconHl) {
        this.iconHl = iconHl;
    }

    /**
     * @return the visible
     */
    public Boolean getVisible() {
        return visible;
    }

    /**
     * @param visible the visible to set
     */
    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "HomeCatesBo [name=" + name + ", e_name=" + e_name + ", cid=" + cid + ", type=" + type + ", bgcolor="
                + bgcolor + ", icon=" + icon + ", iconHl=" + iconHl + ", visible=" + visible + "]";
    }

}
