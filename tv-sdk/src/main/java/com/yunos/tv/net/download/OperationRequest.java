package com.yunos.tv.net.download;

import java.text.DecimalFormat;

public class OperationRequest {

	protected String saveName;
	protected String name;	
	protected String source;
	protected long requestTime;
	protected long overTime;
	protected long totalSize;
	protected Object info;

	public OperationRequest() {
	}

	public OperationRequest(String saveName) {
		this.saveName = saveName;
	}

	public OperationRequest(String saveName, String source,String name, long requestTime, long overTime) {
		this.source = source;
		this.name = name;
		this.saveName = saveName;
		this.requestTime = requestTime;	
		this.overTime = overTime;
	}

	public Object getInfo() {
		return info;
	}

	public void setInfo(Object info) {
		this.info = info;
	}
	
	public long getOverTime() {
		return overTime;
	}

	public void setOverTime(long overTime) {
		this.overTime = overTime;
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getSaveName() {
		return saveName;
	}

	public void setSaveName(String saveName) {
		this.saveName = saveName;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public long getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(long requestTime) {
		this.requestTime = requestTime;
	}

	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	public String getSizeShow() {
		float size = (float) totalSize/ (1024 * 1024);
		DecimalFormat df = new DecimalFormat("0.##");
		return String.valueOf(df.format(size)) + "M";
	}

}
