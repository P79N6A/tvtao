package com.yunos.tv.net.download;

import com.yunos.tv.net.exception.DataErrorEnum;

public interface IDownloadControl {
	public void onStart(Object key, DownloadRequest info);

	public void onProgress(Object key, DownloadRequest info, int progress, int speed);

	public void onFinished(Object key, DownloadRequest info);
	
	public void onCancel(Object key, DownloadRequest info);

	public void onError(Object key, DownloadRequest info, DataErrorEnum error);

	public void onNetworkDisconnect();
}