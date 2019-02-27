package com.yunos.tv.net.download;

import android.util.Log;

import com.yunos.tv.lib.FileUtil;
import com.yunos.tv.net.exception.DataErrorEnum;
import com.yunos.tv.net.exception.DataException;
import com.yunos.tv.net.http.HttpConnectionBuilder;
import com.yunos.tv.net.http.HttpConstant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class DownloadTask extends Thread {
	private static final String TAG = "DownloadTask";

	/**
	 * 默认512kb/s
	 */
	private final static long DEFAULT_LIMIT = 384 * 1024;
	public static final long RESERVED_SPACE = 30 * 1024 * 1024;
	public static final int BUFFER_SIZE = 64 * 1024;
	private DownloadRequest request;
	private Object key;

	private volatile boolean canceled = false;
	private volatile boolean stoped = false;
	private volatile boolean paused = false;
	private long updateTime = 1000;
	private long oldUpdateTime = 0;
	private IDownloadControl iDownloadControl;

	public DownloadTask(Object key, DownloadRequest request, long updateTime, IDownloadControl iDownloadControl) {
		this.request = request;
		this.key = key;
		this.updateTime = updateTime;
		this.iDownloadControl = iDownloadControl;
	}

	public DownloadTask(Object key, DownloadRequest request, IDownloadControl iDownloadControl) {
		this.request = request;
		this.key = key;
		this.iDownloadControl = iDownloadControl;
	}
	
	@Override
	public void run() {
		if (request == null) {
			return;
		}

		long currentUpdateTime = 0;
		int currentSize = 0;
		
		String localUriCandidates = request.getLocalUriCandidates();
	
			long startPosition = request.getDownloadedSize();
			request.setLocalUri(localUriCandidates);
			File file = new File(localUriCandidates);
			String filePath = file.getParent();
			String fileName = file.getName();
			File localFileTemp = new File(filePath, fileName + ".tmp");
			if (localFileTemp.exists()) {
				startPosition = localFileTemp.length();
				if (startPosition != request.getDownloadedSize()) {
					request.setDownloadedSize(startPosition);
					Log.d(TAG, "DownloadTask: run: DownloadedSize = " + request.getDownloadedSize() + ", actural size = "
							+ startPosition + ", but continue to download");
				}
			} else {
				File folder = new File(filePath);
				if (!folder.exists()) {
					folder.mkdirs();
				}
	
			}
	
			HttpURLConnection connection = null;
			try {
				connection = new HttpConnectionBuilder(request.getRemoteUri(), HttpConstant.GET).setReadTimeout(20 * 1000)
						.setProperty("User-Agent", "NetFox").build();
	
				if (startPosition > 0) {
					String start = "bytes=" + startPosition + "-";
					connection.setRequestProperty("RANGE", start);
				}
	
				long fileLength = connection.getContentLength();
				// long fileLength =
				// Long.parseLong(connection.getHeaderField("Content-Length"));
				if (fileLength <= 0) {
					Log.e(TAG, "remote file not exists.");
					this.iDownloadControl.onError(this.key, this.request, DataErrorEnum.DOWNLOAD_FILE_NOT_EXISTS);
					connection.disconnect();
					return;
				}
				try {
					if (!FileUtil.hasSpace(fileLength, RESERVED_SPACE, filePath)) {
						this.iDownloadControl.onError(this.key, this.request, DataErrorEnum.DOWNLOAD_LACK_OF_SPACE);
						connection.disconnect();
						return;
					}
				} catch (Exception e) {
					Log.e(TAG, "storage error 0.");
					if (this.iDownloadControl != null) {
						this.iDownloadControl.onError(this.key, this.request, DataErrorEnum.DOWNLOAD_STORAGE_FAILED);
					}
					connection.disconnect();
					return;
				}
				
				
	
				// connection.connect();
				if (startPosition > 0) {
					request.setTotalSize(startPosition + connection.getContentLength());
				} else {
					request.setTotalSize(connection.getContentLength());
				}
	
			} catch (DataException e) {
				Log.e(TAG, "net error.", e);
				if (this.iDownloadControl != null) {
					this.iDownloadControl.onError(this.key, this.request, DataErrorEnum.DOWNLOAD_NET_FAILED);
				}
				return;
			}

			if (this.iDownloadControl != null) {
				this.iDownloadControl.onStart(key, request);
			}
			
			InputStream in = null;
		
			try {
				byte[] buffer = new byte[BUFFER_SIZE];
				int length = 0;
	
				if (!localFileTemp.exists()) {
					localFileTemp.createNewFile();
				}
				in = connection.getInputStream();
			    FileOutputStream fileOut = new FileOutputStream(localFileTemp, true);
			    
				Log.d(TAG, " pKname = " + request.getSaveName() + " Thread.id = " + getId());
				while (!canceled && (length = in.read(buffer)) > 0) {
					
					fileOut.write(buffer, 0, length);								
					request.setDownloadedSize(request.getDownloadedSize() + length);
					
					if (System.currentTimeMillis() - currentUpdateTime > this.updateTime) {
						int downloadSzie = (int)(request.getDownloadedSize() - currentSize) / 1024;
						int downloadProgress = (int) (request.getDownloadedSize() * 100 / request.getTotalSize());										
						int downloadSpeed = (int) (downloadSzie / ((System.currentTimeMillis() - currentUpdateTime) / 1000));			
						
						if (this.iDownloadControl != null) {
							this.iDownloadControl.onProgress(this.key, this.request, downloadProgress, downloadSpeed);
						}
						currentUpdateTime = System.currentTimeMillis();
						currentSize = (int)request.getDownloadedSize();
					}
					
					oldUpdateTime = System.currentTimeMillis();
					long limit = request.getLimitSpeed() > 0 ? request.getLimitSpeed() : DEFAULT_LIMIT;
					long delayTime = (long) (length / ((double) limit / 1000) - (System.currentTimeMillis() - oldUpdateTime));	
					if (delayTime > 0) {
						try {
							Thread.sleep(delayTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}								
				
			} catch (IOException e) {	
				Log.e(TAG, "IOException= " + getId() + "   ********");
				stoped = Boolean.TRUE;
				if (this.iDownloadControl != null) {
					this.iDownloadControl.onError(this.key, this.request, DataErrorEnum.DOWNLOAD_NET_FAILED);
				}
				return;
			} catch (Exception e) {
				Log.e(TAG, "storage error.  Thread.id = " + getId() + "   ********");
				stoped = Boolean.TRUE;
				if (this.iDownloadControl != null) {
					this.iDownloadControl.onError(this.key, this.request, DataErrorEnum.DOWNLOAD_STORAGE_FAILED);
				}
				return;
			} finally {
				try {
					if (in != null) {
						in.close();
					}				
					connection.disconnect();
				} catch (IOException e) {
					Log.e(TAG, "storage error 2.");
					return;
				}
			}
			if (canceled) {
				this.iDownloadControl.onCancel(this.key, this.request);
			} else if (request.getTotalSize() == request.getDownloadedSize()) {
				File localFile = new File(filePath, fileName);
				if (localFile.exists()) {
					localFile.delete();
				}
				localFileTemp.renameTo(localFile);
				if (this.iDownloadControl != null) {
					this.iDownloadControl.onFinished(this.key, this.request);
				}
			}
			return;
	}

	public DownloadRequest getRequest() {
		return this.request;
	}

	public void cancel() {
		canceled = true;
	}

	@Deprecated
	public boolean isPaused() {
		return paused;
	}

	public boolean isStoped() {
		return stoped;
	}

	@Deprecated
	public void pause() {
		paused = Boolean.TRUE;
	}

	public int getProgress() {
		if (request.getTotalSize() <= 0)
			return 0;
		else
			return (int) (request.getDownloadedSize() * 100 / request.getTotalSize());
	}
}
