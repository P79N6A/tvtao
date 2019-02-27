package com.yunos.tv.app.file.cache;

import android.util.Log;

import java.io.File;

/**
 * Base disc cache. Implements common functionality for disc cache.
 */
public abstract class BaseDiscCache implements DiscCacheAware {

	protected File cacheDir;

	private FileNameGenerator fileNameGenerator;
	private String mSuffix;
	public BaseDiscCache(File cacheDir) {
		this.cacheDir = cacheDir;
		this.fileNameGenerator = new FileNameGenerator();
	}

	public void setSuffix(String suffix){
		mSuffix = suffix;
	}
	
	
	@Override
	public File get(String key) {
		String fileName = fileNameGenerator.generateHashCode(key);
		if(mSuffix != null){
			fileName += mSuffix;
		}
		return new File(cacheDir, fileName);
	}

	@Override
	public void clear() {
		Log.i("BaseDiscCache", "clear cached file");
		File[] files = cacheDir.listFiles();
		if (files != null) {
			for (File f : files) {
				boolean result = f.delete();
				if(!result){
					Log.e("BaseDiscCache", "delete file fail "+ f.getPath());
				}
			}
		}
	}
}