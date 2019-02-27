package com.yunos.tvtaobao.biz.request.bo;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 类目
 * 
 * @author tianxiang
 * @date 2012-10-12 下午3:19:16
 */
public class CategoryMO implements Parcelable{
	/**
	 * 类目id
	 */
	private Long cid;

	/**
	 * 父类目id，如果是没有父类目，则为0
	 */
	private Long parentCid;

	/**
	 * 类目名
	 */
	private String name;

	/**
	 * 类目类别（是普通商品的类目还是本地化的类目、D2C的类目） 普通商品0 本地化商品1
	 */
	private Integer type;

	@Override
	public String toString() {
		return "JuCategoryMO [cid=" + cid + ", parentCid=" + parentCid
				+ ", name=" + name + ", type=" + type + "]";
	}

	public Long getCid() {
		return cid;
	}

	public void setCid(Long cid) {
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
		c.setCid(obj.optLong("cid"));
		c.setParentCid(obj.optLong("parentCid"));
		c.setName(obj.optString("name"));
		c.setType(obj.optInt("type"));

		return c;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(cid.longValue());
		dest.writeLong(parentCid.longValue());
		dest.writeString(name);
		dest.writeInt(type.intValue());
		
	}
	
	public static CategoryMO newInstanceFromParcel(Parcel source){
		CategoryMO c = new CategoryMO();
		
		c.cid = Long.valueOf(source.readLong());
		c.parentCid = Long.valueOf(source.readLong());
		c.name = source.readString();
		c.type = Integer.valueOf(source.readInt());		
		return c;
	}
	
	public static final Creator<CategoryMO> CREATOR = new Creator<CategoryMO>() {

		@Override
		public CategoryMO createFromParcel(Parcel source) {
			
			return newInstanceFromParcel(source);
		}

		@Override
		public CategoryMO[] newArray(int size) {
			return new CategoryMO[size];
		}
	};

}
