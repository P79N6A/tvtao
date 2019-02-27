/**
 * 
 */
package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 卖家服务评分
 * 
 * @author tianxiang
 * @date 2012-10-18 下午4:02:34
 */
public class EvaluateInfo extends BaseMO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5492984001691665562L;

	// 服务标题
	private String title;

	// 服务评分
	private Double score;

	/**
	 * 负值为低于行业标准的百分值，正值为高于行业标准的百分值，如-2.57 表示低于行业标准2.57%，
	 * 
	 * 2.09表示高于行业标准2.09%，正负0.5之间判断为持平（不包括正负0.5）
	 */
	private Double highGap;

	// 参预该项评分的次数，如：800
	private Integer evaluatorCount;

	public static EvaluateInfo resolveFromMTOP(JSONObject obj)
			throws JSONException {
		if (obj == null) {
			return null;
		}

		EvaluateInfo e = new EvaluateInfo();
		if (!obj.isNull("title")) {
			e.setTitle(obj.getString("title"));
		}

		if (!obj.isNull("score")) {
			e.setScore(obj.getDouble("score"));
		}

		if (!obj.isNull("highGap")) {
			e.setHighGap(obj.getDouble("highGap"));
		}

		if (!obj.isNull("evaluatorCount")) {
			e.setEvaluatorCount(obj.getInt("evaluatorCount"));
		}

		return e;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Double getHighGap() {
		return highGap;
	}

	public void setHighGap(Double highGap) {
		this.highGap = highGap;
	}

	public Integer getEvaluatorCount() {
		return evaluatorCount;
	}

	public void setEvaluatorCount(Integer evaluatorCount) {
		this.evaluatorCount = evaluatorCount;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}
}
