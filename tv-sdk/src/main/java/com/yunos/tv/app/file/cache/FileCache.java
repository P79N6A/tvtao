package com.yunos.tv.app.file.cache;

import android.os.Handler;
import android.util.Log;

import com.yunos.tv.msg.MessageHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileCache {
	private static final String TAG = "FileCache";
	private static FileCache mFileCache;
	private MessageHandler mMessageHandler;
	private Handler mMainHandler;
	private LimitedAgeFileCountDiscCache mLimitedCache;
	private FileCacheConfig mFileCacheConfig;

	public static FileCache getInstance() {
		if (mFileCache == null) {
			mFileCache = new FileCache();
		}
		return mFileCache;
	}

	public void destroy() {
		if (mLimitedCache != null) {
			if (mFileCacheConfig != null && mFileCacheConfig.mClearFileOnDestroy) {
				mLimitedCache.clear();
			} else {
				mLimitedCache.clearData();
			}
		}
		mFileCache = null;
	}

	public void init(FileCacheConfig config) {
		mFileCacheConfig = config;
		if (config != null) {
			mLimitedCache = new LimitedAgeFileCountDiscCache(config.mCachDir, config.mMaxAge, config.mFileLimitCount);
			mLimitedCache.setSuffix(config.mSuffix);
		}
	}

	public void makeFile(final String key, final String content, final OnFileCacheDoneListener listener) {
		mMessageHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mLimitedCache != null) {
					File file = getFileInDiscCache(key);
					if (!file.exists()) {
						OutputStreamWriter output = null;
						try {
							file.createNewFile();
							output = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
							output.write(content);
							mLimitedCache.put(key, file);
							Log.i(TAG, "create new filePath=" + file.getPath());
						} catch (IOException e) {
							e.printStackTrace();
							if (file.exists()) {
								file.delete();
							}
						} finally {
							if (output != null) {
								try {
									output.close();
								} catch (IOException e) {
								}
								output = null;
							}
						}

					} else {
						Log.i(TAG, "exists file filePath=" + file.getPath());
					}
					final String filePath = file.exists() ? file.getPath() : null;

					mMainHandler.post(new Runnable() {
						@Override
						public void run() {
							if (listener != null) {
								listener.onFileCacheDone(filePath);
							}
						}
					});
				}
			}
		});
	}

	private FileCache() {
		mMainHandler = new Handler();
		mMessageHandler = MessageHandler.getInstance();
	}

	private File getFileInDiscCache(String key) {
		File file = mLimitedCache.get(key);
		File cacheDir = file.getParentFile();
		if (cacheDir == null || !cacheDir.exists()) {
			Log.i(TAG, "create cacheDir=" + cacheDir.getPath());
			cacheDir.mkdirs();
		}
		return file;
	}

	public static class FileCacheConfig {
		public File mCachDir;
		public long mMaxAge;
		public int mFileLimitCount;
		public boolean mClearFileOnDestroy;
		public String mSuffix;
	}

	public interface OnFileCacheDoneListener {
		public void onFileCacheDone(String filePath);
	}
}
