/**
 * 
 */
package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 担保信息
 * 
 * @author tianxiang
 * @date 2012-10-18 下午4:07:51
 */
public class Guarantee implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1372428280992334857L;

	// 标志名称，如"正品保障","7天退换"
	private String title;

	// 标志图标url,如"http://a.tbcdn.cn/mw/s/hi/tbtouch/icons/trade/xb_quality_item.png"
	private String icon;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public static Guarantee resolveFromMTOP(JSONObject obj)
			throws JSONException {
		if (obj == null) {
			return null;
		}

		Guarantee g = new Guarantee();

		if (!obj.isNull("icon")) {
			g.setIcon(obj.getString("icon"));
		}

		if (!obj.isNull("title")) {
			g.setTitle(obj.getString("title"));
		}

		return g;
	}

}
