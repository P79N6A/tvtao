package com.yunos.tv.net.download;

import android.util.Log;

import com.yunos.tv.net.exception.DataErrorEnum;
import com.yunos.tv.net.network.NetworkManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class DownloadManager implements IDownloadControl {
	LinkedList<Object> queue = new LinkedList<Object>();
	Map<Object, DownloadRequest> mapData = new HashMap<Object, DownloadRequest>();
	List<DownloadRequest> downloadWaitList = new LinkedList<DownloadRequest>();
	Map<Object, DownloadTask> downloading = new HashMap<Object, DownloadTask>();
	List<DownloadRequest> downloadingList = new LinkedList<DownloadRequest>();
	Set<Object> cancelingSet = new HashSet<Object>();
	int taskNum = 1;
	long updateTime = 1500;

	public DownloadRequest getDownloadRequest() {
		return downloadingList.get(0);
	}

	IDownloadListener listener;
	
	public DownloadManager(int taskNum, IDownloadListener listener) {
		this.listener = listener;
		this.taskNum = taskNum;
	}

	public void setTaskNum(int taskNum) {
		this.taskNum = taskNum;
	}

	public void start(Object key, DownloadRequest request) {
		DownloadTask task = null;
		synchronized (this) {
			if (this.downloading.size() < this.taskNum) {
				task = new DownloadTask(key, request, this.updateTime, this);
				this.downloading.put(key, task);
				this.downloadingList.add(request);
			} else {
				this.queue.add(key);
				this.mapData.put(key, request);
				this.downloadWaitList.add(request);
			}
		}
		if (task != null) {
			task.start();
		} else if (this.listener != null) {
			this.listener.downloadWait(key, request);
		}
	}

	public void stop(Object key) {
		synchronized (this) {
			if (this.downloading.containsKey(key)) {
				DownloadTask task = this.downloading.get(key);
				task.cancel();
				this.downloading.remove(key);
				this.downloadingList.remove(task.getRequest());
			} else if (this.queue.contains(key)) {
				DownloadRequest info = this.mapData.get(key);
				this.mapData.remove(key);
				this.downloadWaitList.remove(info);
				this.queue.remove(key);
			}
		}
	}

	public void cancel(Object key) {
		Boolean isDownloading = isDwonloading(key);
		if (isDownloading) {
			DownloadTask task = downloading.get(key);
			if (task != null) {
				if (task.isAlive()) {
					cancelingSet.add(key);
					task.cancel();
				} else {
					this.listener.downloadCancel(key, new OperationRequest(String.valueOf(key)));
				}
			} else {
				this.listener.downloadCancel(key, new OperationRequest(String.valueOf(key)));
			}
			this.nextDownload(key);
		} else {
			if (this.queue.contains(key)) {
				DownloadRequest info = this.mapData.get(key);
				this.mapData.remove(key);
				this.downloadWaitList.remove(info);
				this.queue.remove(key);
			} 
			this.listener.downloadCancel(key, new OperationRequest(String.valueOf(key)));
		}
	}

	public boolean isWaiting(Object key) {
		synchronized (this) {
			if (this.queue.contains(key)) {
				return true;
			}

			return false;
		}
	}

	public int getProgress(Object key) {
		if (this.downloading.containsKey(key)) {
			return this.downloading.get(key).getProgress();
		}

		return 0;
	}

	public boolean isDwonloading(Object key) {
		synchronized (this) {
			if (this.downloading.containsKey(key)) {
				return true;
			}

			return false;
		}
	}

	public boolean isDwonloadingActive(Object key) {
		DownloadTask task = downloading.get(key);
		if (task == null || !task.isAlive() || task.isStoped()) {
			return false;
		}
		return true;
	}

	public void nextDownload(Object key) {
		synchronized (this) {
			if (this.downloading.containsKey(key)) {
				DownloadTask task = this.downloading.get(key);
				this.downloading.remove(key);
				this.downloadingList.remove(task.getRequest());
			}

			if (this.downloading.size() >= this.taskNum) {
				return;
			}
			if (this.queue.size() > 0) {
				key = this.queue.poll();
				DownloadRequest nextRequest = this.mapData.get(key);
				this.mapData.remove(key);
				this.downloadWaitList.remove(nextRequest);
				DownloadTask task = new DownloadTask(key, nextRequest, this.updateTime, this);
				this.downloading.put(key, task);
				this.downloadingList.add(nextRequest);
				task.start();
			}
		}
	}

	@Override
	public void onCancel(Object key, DownloadRequest info) {
		if (this.listener != null && cancelingSet.contains(key)) {
			this.listener.downloadCancel(key, new OperationRequest(String.valueOf(key)));
			cancelingSet.remove(key);
		}
	}

	public List<DownloadRequest> getDownloadingList() {
		return new CopyOnWriteArrayList<DownloadRequest>(this.downloadingList);
	}

	public List<DownloadRequest> getDownloadWaitList() {
		return new CopyOnWriteArrayList<DownloadRequest>(this.downloadWaitList);
	}

	@Override
	public void onStart(Object key, DownloadRequest request) {
		this.listener.downloadStart(key, request);
	}

	@Override
	public void onProgress(Object key, DownloadRequest request, int progress, int speed) {
		this.listener.downloadProgress(key, request, progress, speed);
	}

	@Override
	public void onFinished(Object key, DownloadRequest request) {
		//this.nextDownload(key);
		this.listener.downloadFinish(key, request);
	}

	private Map<Object, Integer> retryCount = new HashMap<Object, Integer>();
	private final static int RETRY_NUM = 5;

	@Override
	public void onError(Object key, DownloadRequest request, DataErrorEnum error) {
		Log.d("DownloadTask", " onError  error= " + error);
		if (error == DataErrorEnum.DOWNLOAD_STORAGE_FAILED) {
			DownloadTask task = downloading.get(key);
			Log.d("DownloadTask",
					" onError  error= " + error + " , task.isAlive() = " + task.isAlive() + "task.isPaused() = " + task.isStoped());
			boolean isRuning = task != null && task.isAlive() && !task.isStoped();
			if (isRuning) {
				return;
			}
		} else if (error == DataErrorEnum.DOWNLOAD_NET_FAILED) {
			if (!NetworkManager.instance().isNetworkConnected()) {
				return;
			}
			try {
				Thread.sleep(10 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			continueDownload(true);
			return;
		} else if (error == DataErrorEnum.DOWNLOAD_FILE_NOT_EXISTS) {
			if (!NetworkManager.instance().isNetworkConnected()) {
				return;
			}
			if (!retryCount.containsKey(key) || retryCount.get(key) < RETRY_NUM) {
				try {
					Thread.sleep(10 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continueDownload(true);
				Integer count = retryCount.get(key) == null ? 0 : retryCount.get(key);
				retryCount.put(key, count + 1);
				return;
			} else {
				retryCount.remove(key);
				this.nextDownload(key);
			}
		} else if (error != DataErrorEnum.DOWNLOAD_LACK_OF_SPACE) {
			this.nextDownload(key);
		}
		this.listener.downloadError(key, request, error);
	}

	public void continueDownload(boolean forceRestart) {
		synchronized (this) {
			if (downloadingList.size() > 0) {
				for (DownloadRequest req : downloadingList) {
					DownloadTask taskBefore = this.downloading.get(req.getSaveName());
					Log.d("DownloadTask",
							" continueDownload forceRestart = " + forceRestart + ",taskBefore.isAlive = " + taskBefore.isAlive());
					if (forceRestart) {
						taskBefore.cancel();
						DownloadTask task = new DownloadTask(req.getSaveName(), req, this.updateTime, this);
						this.downloading.put(req.getSaveName(), task);
						task.start();
					} else if (taskBefore == null || !taskBefore.isAlive()) {
						DownloadTask task = new DownloadTask(req.getSaveName(), req, this.updateTime, this);
						this.downloading.put(req.getSaveName(), task);
						task.start();
					}
				}
				return;
			}
		}
	}

	public void downloadCancel() {
		synchronized (this) {
			for (Map.Entry<Object, DownloadTask> entry : downloading.entrySet()) {
				DownloadTask task = entry.getValue();
				task.cancel();
			}
		}
	}
	
	@Override
	public void onNetworkDisconnect() {
		// synchronized (this) {
		// for (Map.Entry<Object, DownloadTask> entry : downloading.entrySet())
		// {
		// Object key = entry.getKey();
		// DownloadTask task = entry.getValue();
		// DownloadRequest request = task.getRequest();
		// mapData.put(key, request);
		// queue.add(key);
		// task.pause();
		// }
		// downloadWaitList.addAll(downloadingList);
		// downloading.clear();
		// downloadingList.clear();
		// }
	}

}
