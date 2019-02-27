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

/**
 * Disc cache limited by total cache size. If cache size exceeds specified limit then file with the most oldest last
 * usage date will be deleted.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 * @see LimitedDiscCache
 */
public class TotalSizeLimitedDiscCache extends LimitedDiscCache {

	private static final int MIN_NORMAL_CACHE_SIZE_IN_MB = 2;
	private static final int MIN_NORMAL_CACHE_SIZE = MIN_NORMAL_CACHE_SIZE_IN_MB * 1024 * 1024;

	/**
	 * @param cacheDir Directory for file caching. <b>Important:</b> Specify separate folder for cached files. It's
	 *            needed for right cache limit work.
	 * @param maxCacheSize Maximum cache directory size (in bytes). If cache size exceeds this limit then file with the
	 *            most oldest last usage date will be deleted.
	 */
	public TotalSizeLimitedDiscCache(File cacheDir, int maxCacheSize) {
		super(cacheDir, maxCacheSize);
		if (maxCacheSize < MIN_NORMAL_CACHE_SIZE) {
			Log.w("TotalSizeLimitedDiscCache", "You set too small disc cache size (less than "+MIN_NORMAL_CACHE_SIZE_IN_MB+"Mb)");
		}
	}

	@Override
	protected int getSize(File file) {
		return (int) file.length();
	}
}
