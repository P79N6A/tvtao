package com.aliyun.base.info;

public class UpdateInfo {
	
	private boolean isToUpdate;
	
	private String updateUrl;
	
	private int versionCode;
	
	private String versionName;
	
	private String description;
	
	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public boolean isToUpdate() {
		return isToUpdate;
	}

	public void setToUpdate(boolean isToUpdate) {
		this.isToUpdate = isToUpdate;
	}

	public String getUpdateUrl() {
		return updateUrl;
	}

	public void setUpdateUrl(String updateUrl) {
		this.updateUrl = updateUrl;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
