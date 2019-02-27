package com.yunos.tv.net.download;

public class DownloadRequest extends OperationRequest {
	private String remoteUri;
	
	private String localUriCandidates;

	private String localUri;

	private long downloadedSize;

	private String name;
	
	private long limit_speed = 0;
	
	private Boolean isContinue = Boolean.FALSE;

	public DownloadRequest(String remoteUri, String localUriCandidates, String localUri, long downloadedSize, String source, String saveName,
			String name, long overTime) {
		super(saveName, source, name, System.currentTimeMillis(),overTime);
		this.name = name;
		this.localUriCandidates = localUriCandidates;
		this.remoteUri = remoteUri;
		this.localUri = localUri;
		this.downloadedSize = downloadedSize;
	}

	public DownloadRequest(String remoteUri, String localUriCandidates, String localUri, long downloadedSize, String source, String saveName,
			String name, long requestTime, long overTime) {
		super(saveName, source, name, requestTime, overTime);
		this.name = name;
		this.remoteUri = remoteUri;
		this.localUriCandidates = localUriCandidates;
		this.localUri = localUri;
		this.downloadedSize = downloadedSize;
	}

	public long getLimitSpeed() {
		return limit_speed;
	}

	public void setLimitSpeed(long limitspeed) {
		this.limit_speed = limitspeed;
	}
	
	public Boolean isContinue() {
		return isContinue;
	}

	public void setIsContinue(Boolean isContinue) {
		this.isContinue = isContinue;
	}

	public long getDownloadedSize() {
		return downloadedSize;
	}

	public void setDownloadedSize(long downloadedSize) {
		this.downloadedSize = downloadedSize;
	}

	public DownloadRequest(String pkName) {
		super(pkName);
	}

	public String getRemoteUri() {
		return remoteUri;
	}

	public void setRemoteUri(String remoteUri) {
		this.remoteUri = remoteUri;
	}

	public String getLocalUri() {
		return localUri;
	}

	public void setLocalUri(String localUri) {
		this.localUri = localUri;
	}

	public String getLocalUriCandidates() {
		return localUriCandidates;
	}

	public void setLocalUriCandidates(String localUriCandidates) {
		this.localUriCandidates = localUriCandidates;
	}

	public String getName() {
		return this.name;
	}

	public int getProgress() {
		if (this.totalSize <= 0)
			return 0;

		return (int) (this.downloadedSize * 100 / this.totalSize);
	}

}
