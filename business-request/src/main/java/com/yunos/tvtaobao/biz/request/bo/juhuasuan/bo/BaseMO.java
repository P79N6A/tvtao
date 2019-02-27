package com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public abstract class BaseMO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7950946206282680445L;

	public static JSONObject getDataElement(String json) {
		if (json == null) {
			return null;
		}

		JSONObject data = null;
		try {
			data = new JSONObject(json).getJSONObject("data");
		} catch (JSONException e) {
		}

		return data;
	}

	public static JSONArray getDataResultElement(String json) {
		JSONObject data = getDataElement(json);
		if (data == null) {
			return null;
		}

		JSONArray result = null;
		try {
			result = data.getJSONArray("result");
		} catch (JSONException e) {
		}

		return result;
	}
}
