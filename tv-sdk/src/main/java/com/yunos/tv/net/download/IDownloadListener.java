package com.yunos.tv.net.download;

import com.yunos.tv.net.exception.DataErrorEnum;

public interface IDownloadListener {

	void downloadWait(Object key, OperationRequest downloadFileInfo);
	
	void downloadStart(Object key, OperationRequest downloadFileInfo);

	void downloadFinish(Object key, OperationRequest downloadFileInfo);
	
	void downloadCancel(Object key, OperationRequest downloadFileInfo);

	void downloadProgress(Object key, OperationRequest downloadFileInfo, int downloadProgress, int downloadspeed);

	void downloadError(Object key, OperationRequest downloadFileInfo, DataErrorEnum error);
	
}
