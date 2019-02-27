/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.tvlife.imageloader.core;


import android.graphics.Bitmap;

import com.tvlife.imageloader.core.assist.FailReason;
import com.tvlife.imageloader.core.assist.FailReason.FailType;
import com.tvlife.imageloader.core.assist.LoadedFrom;
import com.tvlife.imageloader.core.display.BitmapDisplayer;
import com.tvlife.imageloader.core.imageaware.ImageAware;
import com.tvlife.imageloader.core.listener.ImageLoadingListener;
import com.tvlife.imageloader.utils.L;

/**
 * Displays bitmap in {@link com.nostra13.universalimageloader.core.imageaware.ImageAware}. Must be
 * called on UI thread.
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see ImageLoadingListener
 * @see BitmapDisplayer
 * @since 1.3.1
 */
final class DisplayBitmapTask implements Runnable {

    private static final String LOG_DISPLAY_IMAGE_IN_IMAGEAWARE = "DisplayBitmapTask --> Display image in ImageAware (loaded from %1$s) [%2$s]";
    private static final String LOG_TASK_CANCELLED_IMAGEAWARE_REUSED = "DisplayBitmapTask --> ImageAware is reused for another image. Task is cancelled. [%s]";
    private static final String LOG_TASK_CANCELLED_IMAGEAWARE_COLLECTED = "DisplayBitmapTask --> ImageAware was collected by GC. Task is cancelled. [%s]";

    private final Bitmap bitmap;
    private final String imageUri;
    private final ImageAware imageAware;
    private final String memoryCacheKey;
    private final BitmapDisplayer displayer;
    private final ImageLoadingListener listener;
    private final ImageLoaderEngine engine;
    private final LoadedFrom loadedFrom;
    private final boolean isdisplayshow;

    private final boolean isCacheInMemory;

    private boolean loggingEnabled;

    public DisplayBitmapTask(Bitmap bitmap, ImageLoadingInfo imageLoadingInfo, ImageLoaderEngine engine,
            LoadedFrom loadedFrom, boolean loggingEnabled) {
        this.bitmap = bitmap;
        imageUri = imageLoadingInfo.uri;
        imageAware = imageLoadingInfo.imageAware;
        memoryCacheKey = imageLoadingInfo.memoryCacheKey;
        displayer = imageLoadingInfo.options.getDisplayer();
        isdisplayshow = imageLoadingInfo.options.isDisplayShow();

        isCacheInMemory = imageLoadingInfo.options.isCacheInMemory();

        listener = imageLoadingInfo.listener;
        this.engine = engine;
        this.loadedFrom = loadedFrom;

        this.loggingEnabled = loggingEnabled;
    }

    @Override
    public void run() { 
        
            if (loggingEnabled) {
                L.d(LOG_DISPLAY_IMAGE_IN_IMAGEAWARE, loadedFrom, memoryCacheKey);
            }
            
            boolean iscollected = imageAware.isCollected();
            if (iscollected && loggingEnabled) {
                  L.d(LOG_TASK_CANCELLED_IMAGEAWARE_COLLECTED, memoryCacheKey);
            }
            

            if (isdisplayshow && !iscollected) {
                // 如果需要显示，并且VIEW 没有回收，那么调用 display
                displayer.display(bitmap, imageAware, loadedFrom, loggingEnabled);
            } 

            if ((bitmap != null) && (!bitmap.isRecycled())) {
                listener.onLoadingComplete(imageUri, imageAware.getWrappedView(), bitmap);
            } else {
                FailReason failReason = new FailReason(FailType.BITMAP_RECYCLE, null);
                listener.onLoadingFailed(imageUri, imageAware.getWrappedView(), failReason);
            } 
            
            engine.cancelDisplayTaskFor(imageAware); 
    }

    /**
     * Checks whether memory cache key (image URI) for current ImageAware is
     * actual
     */
    private boolean isViewWasReused() {
        if (isCacheInMemory) {
            String currentCacheKey = engine.getLoadingUriForView(imageAware);
            return !memoryCacheKey.equals(currentCacheKey);
        }
        return false;
    }
}
