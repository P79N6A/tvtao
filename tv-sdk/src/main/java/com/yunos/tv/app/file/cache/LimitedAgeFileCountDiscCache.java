/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.yunos.tv.app.file.cache;

import android.util.Log;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Cache which deletes files which were loaded more than defined time. Cache size is unlimited.
 * @see BaseDiscCache
 */
public class LimitedAgeFileCountDiscCache extends BaseDiscCache {
	private final String TAG = "LimitedAgeFileCountDiscCache";
	private final long maxFileAge;
	private final AtomicInteger mCacheCount;
	private final int mCountLimit;
	private final Map<File, Long> loadingDates = Collections.synchronizedMap(new HashMap<File, Long>());	
	
	/**
	 * @param cacheDir Directory for file caching
	 * @param maxAge Max file age (in seconds). If file age will exceed this value then it'll be removed on next
	 *            treatment (and therefore be reloaded).
	 */
	public LimitedAgeFileCountDiscCache(File cacheDir, long maxAge, int countLimit) {
		super(cacheDir);
		mCountLimit = countLimit;
		mCacheCount = new AtomicInteger();
		this.maxFileAge = maxAge * 1000; // to milliseconds
		calculateCacheSizeAndFillUsageMap();
	}

	@Override
	public void put(String key, File file) {
		Log.i(TAG, "put file="+file.getPath()+" cachedCount="+mCacheCount.get());
		int curCacheSize = mCacheCount.get() + 1;
		while (curCacheSize > mCountLimit) {
			int freedSize = removeNext();
			if (freedSize == 0) break; // cache is empty (have nothing to delete)
			curCacheSize = mCacheCount.addAndGet(-freedSize);
		}
		mCacheCount.addAndGet(1);
		
		long currentTime = System.currentTimeMillis();
		file.setLastModified(currentTime);
		loadingDates.put(file, currentTime);
	}

	@Override
	public File get(String key) {
		File file = super.get(key);
		if (file.exists()) {
			boolean cached;
			Long loadingDate = loadingDates.get(file);
			if (loadingDate == null) {
				cached = false;
				loadingDate = file.lastModified();
			} else {
				cached = true;
			}
			long createTime = System.currentTimeMillis() - loadingDate;
			Log.i(TAG, "file createTime="+ createTime);
			if (createTime > maxFileAge) {
				Log.i(TAG, "time out delete file="+file.getPath());
				boolean result = file.delete();
				if(!result){
					Log.e(TAG, "delete file fail "+ file.getPath());
					return null;
				}
				loadingDates.remove(file);
				mCacheCount.addAndGet(-1);
			} else if (!cached) {
				Log.i(TAG, "cached file="+file.getPath());
				loadingDates.put(file, loadingDate);
				mCacheCount.addAndGet(1);
			}
		}
		return file;
	}
	
	@Override
	public void clear() {
		clearData();
		super.clear();
	}
	
	public void clearData(){
		Log.i(TAG, "clearData");
		loadingDates.clear();
		mCacheCount.set(0);
	}
	
	private void calculateCacheSizeAndFillUsageMap() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int size = 0;
				File[] cachedFiles = cacheDir.listFiles();
				if (cachedFiles != null) { // rarely but it can happen, don't know why
					for (File cachedFile : cachedFiles) {
						size ++;
						loadingDates.put(cachedFile, cachedFile.lastModified());
					}
					mCacheCount.set(size);
				}
			}
		}).start();
	}


	/** Remove next file and returns it's size */
	private int removeNext() {
		if (loadingDates.isEmpty()) {
			return 0;
		}

		Long oldestUsage = null;
		File mostLongUsedFile = null;
		Set<Entry<File, Long>> entries = loadingDates.entrySet();
		synchronized (loadingDates) {
			for (Entry<File, Long> entry : entries) {
				if (mostLongUsedFile == null) {
					mostLongUsedFile = entry.getKey();
					oldestUsage = entry.getValue();
				} else {
					Long lastValueUsage = entry.getValue();
					if (lastValueUsage < oldestUsage) {
						oldestUsage = lastValueUsage;
						mostLongUsedFile = entry.getKey();
					}
				}
			}
		}

		if (mostLongUsedFile.delete()) {
			Log.i(TAG, "out of limit count remove file="+mostLongUsedFile.getPath());
			loadingDates.remove(mostLongUsedFile);
		}
		return 1;
	}
}