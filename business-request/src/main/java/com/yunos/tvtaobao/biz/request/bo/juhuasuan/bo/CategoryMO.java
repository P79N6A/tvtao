package com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo;


import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 类目
 * @author tianxiang
 * @date 2012-10-12 下午3:19:16
 */
public class CategoryMO extends BaseMO implements Parcelable {

    private static final long serialVersionUID = -7591145889378574389L;

    /**
     * 类目id
     */
    private String cid;

    /**
     * 父类目id，如果是没有父类目，则为0
     */
    private Long parentCid = 0L;

    /**
     * 类目名
     */
    private String name;

    /**
     * 类目类别（是普通商品的类目还是本地化的类目、D2C的类目） 普通商品0 本地化商品1
     */
    private Integer type = 0;
    /**
     * 表示
     */
    private String optStr;
    /**
     * 类目图标
     */
    private String icon;
    /**
     * 类目图标HighLight
     */
    public String iconHl;

    private ArrayList<CategoryMO> children;
    /**
     * 是否是全部类目？全部类目一般是客户端添加的。
     */
    private Boolean isAll = false;

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CategoryMO [cid=" + cid + ", parentCid=" + parentCid + ", name=" + name + ", type=" + type
                + ", optStr=" + optStr + ", icon=" + icon + ", iconHl=" + iconHl + ", children=" + children
                + ", isAll=" + isAll + "]";
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
     * @return the isAll
     */
    public Boolean getIsAll() {
        return isAll;
    }

    /**
     * @param isAll the isAll to set
     */
    public void setIsAll(Boolean isAll) {
        this.isAll = isAll;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public Long getParentCid() {
        return parentCid;
    }

    public void setParentCid(Long parentCid) {
        this.parentCid = parentCid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public static CategoryMO fromMTOP(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }

        CategoryMO c = new CategoryMO();
        c.setCid(obj.optString("cid"));
        c.setParentCid(obj.optLong("parentCid"));
        c.setName(obj.optString("name"));
        c.setType(obj.optInt("type"));
        c.setOptStr(obj.optString("optStr"));
        c.setIcon(obj.optString("icon"));
        c.setIconHl(obj.optString("icon1"));
        if (obj.has("children")) {
            ArrayList<CategoryMO> list = new ArrayList<CategoryMO>();
            JSONArray array = obj.getJSONArray("children");
            for (int i = 0; i < array.length(); i++) {
                list.add(CategoryMO.fromMTOP(array.getJSONObject(i)));
            }
            c.setChildren(list);
        }
        c.setIsAll(obj.optBoolean("isAll"));

        return c;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cid);
        dest.writeLong(parentCid.longValue());
        dest.writeString(name);
        dest.writeInt(type.intValue());
        dest.writeString(optStr);
        dest.writeString(icon);
        dest.writeString(iconHl);

        dest.writeList(children);
        dest.writeString(String.valueOf(isAll));
    }

    //    public static CategoryMO newInstanceFromParcel(Parcel source) {
    //        CategoryMO c = new CategoryMO();
    //
    //        c.cid = Long.valueOf(source.readLong());
    //        c.parentCid = Long.valueOf(source.readLong());
    //        c.name = source.readString();
    //        c.type = Integer.valueOf(source.readInt());
    //        c.children = new ArrayList<CategoryMO>();
    //        source.readList(c.children, ClassLoader.getSystemClassLoader());
    //        return c;
    //    }

    public CategoryMO() {
    }

    public CategoryMO(Parcel source) {
        cid = source.readString();
        parentCid = Long.valueOf(source.readLong());
        name = source.readString();
        type = Integer.valueOf(source.readInt());
        optStr = source.readString();
        icon = source.readString();
        iconHl = source.readString();

        children = new ArrayList<CategoryMO>();
        source.readList(children, getClass().getClassLoader());
        isAll = Boolean.valueOf(source.readString());
    }

    public ArrayList<CategoryMO> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<CategoryMO> children) {
        this.children = children;
    }

    public static final Creator<CategoryMO> CREATOR = new Creator<CategoryMO>() {

        @Override
        public CategoryMO createFromParcel(Parcel source) {

            return new CategoryMO(source);
        }

        @Override
        public CategoryMO[] newArray(int size) {
            return new CategoryMO[size];
        }
    };

    /**
     * @return the optStr
     */
    public String getOptStr() {
        return optStr;
    }

    /**
     * @param optStr the optStr to set
     */
    public void setOptStr(String optStr) {
        this.optStr = optStr;
    }

    /**
     * @return the isAll
     */
    public boolean isAll() {
        return isAll;
    }

    /**
     * @param isAll the isAll to set
     */
    public void setAll(boolean isAll) {
        this.isAll = isAll;
    }

}
