package com.yunos.tvtaobao.biz.request.bo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * 物流详情
 * 
 * @author mty
 * @date 2012-11-16 下午04:57:57
 */
public class LogisticMO implements Serializable{
	/**
     * 
     */
    private static final long serialVersionUID = -5632146480126208121L;
    /**
	 * id
	 */
	private String outSid;
	/**
	 * 物流公司
	 */
	private String comName;
	/**
	 * 订单id
	 */
	private String tId;
	/**
	 * 状态
	 */
	private String status;
	/**
	 * 状态列表
	 */
	private ArrayList<Des> desList;

	public static class Des {
		public String time;
		public String desc;

		public Des(String time, String desc) {
			this.desc = desc;
			this.time = time;

		}

		@Override
		public String toString() {
			return "Des [time=" + time + ", desc=" + desc + "]";
		}
	}

	@Override
	public String toString() {
		return "LogisticMO [outSid=" + outSid + ", comName=" + comName
				+ ", tId=" + tId + ", status=" + status + ", desList="
				+ desList + "]";
	}

	public String gettId() {
		return tId;
	}

	public void settId(String tId) {
		this.tId = tId;
	}

	public String getOutSid() {
		return outSid;
	}

	public void setOutSid(String outSid) {
		this.outSid = outSid;
	}

	public String getComName() {
		return comName;
	}

	public void setComName(String comName) {
		this.comName = comName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ArrayList<Des> getDesList() {
		return desList;
	}

	public void addDesList(Des des) {
		if (des == null) {
			return;
		}
		if (desList == null) {
			desList = new ArrayList<Des>();
		}
		desList.add(0, des);
	}

}
