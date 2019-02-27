package com.tvlife.imageloader.cache.memory.impl;


import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.util.Log;

import com.tvlife.imageloader.cache.memory.MemoryCacheAware;

/**
 * 移除缓冲中，图片被释放的LruMemoryCache
 * 在 android 4.0 以下的版本 不考虑其回收机制
 * @author yunzhong.qyz
 */
public class LruMemoryCacheByRecycle implements MemoryCacheAware<String, Bitmap> {

    private final String                        TAG   = "LruMemoryCacheByRecycle";

    //    private final LinkedHashMap<String, WeakReference<Bitmap>> map;

    private final LinkedHashMap<String, Bitmap> map;

    private final int                           maxSize;
    /** Size of this cache in bytes */
    private int                                 size;

    private boolean                             DEBUG = false;

    /** @param maxSize Maximum sum of the sizes of the Bitmaps in this cache */
    public LruMemoryCacheByRecycle(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        this.maxSize = maxSize;
        //        this.map = new LinkedHashMap<String, WeakReference<Bitmap>>(0, 0.75f, true);
        this.map = new LinkedHashMap<String, Bitmap>(0, 0.75f, true);
    }

    /**
     * Returns the Bitmap for {@code key} if it exists in the cache. If a Bitmap was returned, it is moved to the head
     * of the queue. This returns null if a Bitmap is not cached.
     */
    @Override
    public final Bitmap get(String key) {

        if (DEBUG) {
            Log.i(TAG, "get ---> key = " + key);
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        synchronized (this) {
            return map.get(key); 
        }
    }

    /** Caches {@code Bitmap} for {@code key}. The Bitmap is moved to the head of the queue. */
    @Override
    public final boolean put(String key, Bitmap value) {

        if (DEBUG) {
            Log.i(TAG, "put ---> key = " + key + "; value = " + value);
        }

        if (key == null || value == null) {
            throw new NullPointerException("key == null || value == null");
        }

        synchronized (this) {
            size += sizeOf(key, value);

            Bitmap previous = map.put(key, value);

            if (previous != null) {
                size -= sizeOf(key, previous);
            }
            recycleBitmap(previous); 
        }

        trimToSize(maxSize);
        return true;
    }

    /**
     * Remove the eldest entries until the total of remaining entries is at or below the requested size.
     * @param maxSize the maximum size of the cache before returning. May be -1 to evict even 0-sized elements.
     */
    private void trimToSize(int maxSize) {
        while (true) {
            String key = null;
            Bitmap value = null;
            synchronized (this) {

                if (DEBUG) {
                    Log.i(TAG, "size = " + size + ";  map.isEmpty() = " + map.isEmpty());
                }

                if (size < 0 || (map.isEmpty() && size != 0)) {
                      throw new IllegalStateException(getClass().getName() + ".sizeOf() is reporting inconsistent results!");
                }

                if (size <= maxSize || map.isEmpty()) {
                    break;
                }

                Map.Entry<String, Bitmap> toEvict = map.entrySet().iterator().next();
                if (toEvict == null) {
                    break;
                }
                key = toEvict.getKey();
                value = toEvict.getValue();
                map.remove(key);
                size -= sizeOf(key, value);
                recycleBitmap(value); 
            }
        }
    }

    /** Removes the entry for {@code key} if it exists. */
    @Override
    public final void remove(String key) {

        if (DEBUG) {
            Log.i(TAG, "remove ---> key = " + key);
        }

        if (key == null) {
            throw new NullPointerException("key == null");
        }

        synchronized (this) {
            Bitmap previous = map.remove(key);
            if (previous != null) {
                size -= sizeOf(key, previous);
            }
            recycleBitmap(previous); 
        }
    }

    @Override
    public Collection<String> keys() {
        synchronized (this) {
            return new HashSet<String>(map.keySet());
        }
    }

    @Override
    public void clear() {

        if (DEBUG) {
            Log.i(TAG, "clear --->");
        }

        trimToSize(-1); // -1 will evict 0-sized elements

        if (map != null) {
            map.clear();
        }
    }

    /**
     * Returns the size {@code Bitmap} in bytes.
     * <p/>
     * An entry's size must not change while it is in the cache.
     */
    private int sizeOf(String key, Bitmap value) {

        if (value == null) {
            return 0;
        }

        return value.getRowBytes() * value.getHeight();
    }

    @Override
    public synchronized final String toString() {
        return String.format("LruCache[maxSize=%d]", maxSize);
    }

    /**
     * 释放图片资源
     * @param bm
     */
    private void recycleBitmap(Bitmap bm) {
        if (DEBUG) {
            Log.i(TAG, "recycleBitmap  ---> bm = " + bm);

        }
        if (bm != null) {
            bm.recycle();
            bm = null;
        }
    }

}
