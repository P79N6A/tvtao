package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 商品评价
 * 
 * @author tianxiang
 * @date 2012-10-21 上午9:34:36
 */
public class ItemComment implements Serializable{

	/**
     * 
     */
    private static final long serialVersionUID = 1725661734124096938L;

    private String content;

	private String userNick;

	private Integer level1;

	private Integer level2;

	private String time;

	@Override
	public String toString() {
		return "ItemComment [content=" + content + ", userNick=" + userNick
				+ ", level1=" + level1 + ", level2=" + level2 + "]";
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getUserNick() {
		return userNick;
	}

	public void setUserNick(String userNick) {
		this.userNick = userNick;
	}
	
	public static ItemComment resolveFromTop(JSONObject dataObj)
			throws Exception {

		ItemComment comment = new ItemComment();

		if (!dataObj.isNull("displayUserNick")) {
			comment.setUserNick(dataObj.getString("displayUserNick"));
		}

		if (!dataObj.isNull("rateContent")) {
			comment.setContent(dataObj.getString("rateContent"));
		}

		if (!dataObj.isNull("dispalyRateLevel1")) {
			comment.setLevel1(dataObj.getInt("dispalyRateLevel1"));
		}

		if (!dataObj.isNull("dispalyRateLevel2")) {
			comment.setLevel2(dataObj.getInt("dispalyRateLevel2"));
		}

		if (!dataObj.isNull("rateDate")) {
			comment.setTime(dataObj.getString("rateDate"));
		}

		return comment;
	}

	public Integer getLevel1() {
		return level1;
	}

	public void setLevel1(Integer level1) {
		this.level1 = level1;
	}

	public Integer getLevel2() {
		return level2;
	}

	public void setLevel2(Integer level2) {
		this.level2 = level2;
	}
}
