/**
 * 
 */
package com.yunos.tvtaobao.biz.request.bo;

import android.util.Log;

import com.yunos.tv.core.util.DataEncoder;
import com.yunos.tv.core.util.SystemUtil;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 参团结果
 * 
 * @author tianxiang
 * @date 2012-10-31 下午2:22:44
 */
public class JoinGroupResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6668095838315101627L;

	/**
	 * 跳转URL
	 */
	private String url;

	/**
	 * 传递聚划算购买消息的加密串
	 */
	private String key;

	/**
	 * 用户对应的订单编号
	 */
	private Long bizOrderId;

	private int resultCode;

	private String message;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Long getBizOrderId() {
		return bizOrderId;
	}

	public void setBizOrderId(Long bizOrderId) {
		this.bizOrderId = bizOrderId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public static JoinGroupResult fromMTOP(JSONObject dataObj) throws Exception {
		JoinGroupResult item = new JoinGroupResult();
		// item.setBizOrderId(dataObj.getLong("bizOrderId"));
		Log.v("", "the jhs key is:" + dataObj.optString("key"));
		item.setKey(DataEncoder.decodeUrl(dataObj.optString("key")));
		Log.v("", "the jhs key decode is:" + item.getKey());
		Log.v("",
				"the jhs key encode is:" + SystemUtil.encodeUrl(item.getKey()));
		item.setUrl(dataObj.optString("url"));
		item.setResultCode(dataObj.optInt("resultCode"));
		item.setMessage(dataObj.optString("message"));
		return item;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
