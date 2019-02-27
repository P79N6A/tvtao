package com.yunos.tvtaobao.biz.updatesdk;

import android.os.Parcel;
import android.os.Parcelable;

public class AppOperateReqInfo implements Parcelable {
	/** 应用编号 用于唯一标识一个应用 */
	protected String	appId;
	/** 应用名称 */
	protected String	appName;
	/** 应用包名 */
	protected String	packageName;
	/**
	 * 操作指令 可选值： CREATE：新增 UPDATE：更新 DELETE：删除
	 */
	protected String	operate;
	
	/**
	 * apk 文件路径
	 */
	protected String	apkPath;
	
	

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("appId                    = ").append(appId).append("\n");
		sb.append("appName                  = ").append(appName).append("\n");
		sb.append("packageName              = ").append(packageName).append("\n");
		sb.append("operate                  = ").append(operate).append("\n");
		sb.append("apkPath                  = ").append(apkPath).append("\n");
		return sb.toString();
	}

	public AppOperateReqInfo() {}
	
	public AppOperateReqInfo(String appId, String appName,String pkgName, String operate) {
		this.appId = appId;
		this.operate = operate;
		this.appName = appName;
		this.packageName = pkgName;
		this.operate = operate;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getOperate() {
		return operate;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}
	
	public String getApkPath() {
		return apkPath;
	}

	public void setApkPath(String apkPath) {
		this.apkPath = apkPath;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(appId);
		dest.writeString(appName);
		dest.writeString(packageName);
		dest.writeString(operate);
		dest.writeString(apkPath);
	}

	public AppOperateReqInfo(Parcel source) {
		this.appId = source.readString();
		this.appName = source.readString();
		this.packageName = source.readString();
		this.operate = source.readString();
		this.apkPath = source.readString();
	}

	public static final Creator<AppOperateReqInfo>	CREATOR	= new Creator<AppOperateReqInfo>() {

																			@Override
																			public AppOperateReqInfo createFromParcel(
																					Parcel source) {
																				return new AppOperateReqInfo(source);
																			}

																			@Override
																			public AppOperateReqInfo[] newArray(int size) {
																				return new AppOperateReqInfo[size];
																			}

																		};

}
