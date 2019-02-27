/**
 * 
 */
package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 宝贝属性值
 * 
 * @author tianxiang
 * @date 2012-10-18 下午4:14:25
 */
public class PropValue implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8766003133839210515L;

	// 宝贝属性值id
	private Long valueId;

	// 宝贝属性值名称 如：红色，灰色
	private String name;

	// 宝贝属性值别名 如：白色+盒子
	private String valueAlias;

	// /属性值图片链接,如果为空，则没有对应图片
	private String imgUrl;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValueAlias() {
		return valueAlias;
	}

	public void setValueAlias(String valueAlias) {
		this.valueAlias = valueAlias;
	}

	public String getContent() {
		return getValueAlias() == null ? getName() : getValueAlias();
	}

	public static PropValue resolveFromMTOP(JSONObject obj)
			throws JSONException {
		if (obj == null) {
			return null;
		}

		PropValue pv = new PropValue();
		if (!obj.isNull("imgUrl")) {
			pv.setImgUrl(obj.getString("imgUrl"));
		}

		if (!obj.isNull("name")) {
			pv.setName(obj.getString("name"));
		}

		if (!obj.isNull("valueAlias")) {
			pv.setValueAlias(obj.getString("valueAlias"));
		}

		if (!obj.isNull("valueId")) {
			pv.setValueId(obj.getLong("valueId"));
		}

		return pv;
	}

	public Long getValueId() {
		return valueId;
	}

	public void setValueId(Long valueId) {
		this.valueId = valueId;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
}
