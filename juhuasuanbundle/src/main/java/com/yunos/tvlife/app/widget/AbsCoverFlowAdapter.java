/*
 * Copyright 2013 Alibaba Group.
 */
package com.yunos.tvlife.app.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.yunos.tvlife.lib.DataCache;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 提供给{@link AbsCoverFlow}使用的适配器，有阴影和非阴影两种效果；
 */
public abstract class AbsCoverFlowAdapter extends BaseAdapter {

	public AbsCoverFlowAdapter() {
		super();
	}

	/** The Constant TAG. */
    private static final String TAG = AbsCoverFlowAdapter.class.getSimpleName();

    /** The width. */
    private float width = 0;

    /** The height. */
    private float height = 0;

    /** The bitmap map. */
    private final Map<Integer, WeakReference<Bitmap>> bitmapMap = new HashMap<Integer, WeakReference<Bitmap>>();
    
    private static final int MAX_COUNT = 20;
    
    protected final DataCache<Integer, Bitmap> mCache = new DataCache<Integer, Bitmap>(MAX_COUNT);

    /**
     * 设置Item宽度.
     * 
     * @param width
     *            Item宽度
     */
    public synchronized void setWidth(final float width) {
        this.width = width;
    }

    /**
     * 设置Item高度.
     * 
     * @param height
     *           Item高度
     */
    public synchronized void setHeight(final float height) {
        this.height = height;
    }

    @Override
    public final Bitmap getItem(final int position) {
    	
    	Bitmap bm = mCache.objectForKey(position);
    	if(bm != null){
    		return bm;
    	}else{
    		final Bitmap bitmap = createBitmap(position);
    		mCache.putObjectForKey(position, bitmap);
    		return bitmap;
    	}
    	/*
        final WeakReference<Bitmap> weakBitmapReference = bitmapMap.get(position);
        if (weakBitmapReference != null) {
            final Bitmap bitmap = weakBitmapReference.get();
            if (bitmap == null) {
                Log.v(TAG, "Empty bitmap reference at position: " + position + ":" + this);
            } else {
                Log.v(TAG, "Reusing bitmap item at position: " + position + ":" + this);
                return bitmap;
            }
        }
        Log.v(TAG, "Creating item at position: " + position + ":" + this);
        final Bitmap bitmap = createBitmap(position);
        bitmapMap.put(position, new WeakReference<Bitmap>(bitmap));
        Log.v(TAG, "Created item at position: " + position + ":" + this);
        return bitmap;
        */
    }

    /**
     * 创建每个Item的bitmap.
     * <p>阴影和非阴影的时候返回不同的bitmap</p>
     * @param position
     *            Item的位置
     * @return Bitmap 每个Item的bitmap
     */
    protected abstract Bitmap createBitmap(int position);
    
//    protected abstract Bitmap createReflectBitmap(int position);
    
    @Override
    public final synchronized long getItemId(final int position) {
        return position;
    }
    
    Matrix matrix = new Matrix();

    @Override
    public final synchronized ImageView getView(final int position, final View convertView, final ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            final Context context = parent.getContext();
            Log.v(TAG, "Creating Image view at position: " + position + ":" + this);
            imageView = new ImageView(context);
            imageView.setLayoutParams(new AbsCoverFlow.LayoutParams((int) width, (int) height));
        } else {
            Log.v(TAG, "Reusing view at position: " + position + ":" + this);
            imageView = (ImageView) convertView;
        }
        /*
        imageView.setScaleType(ScaleType.FIT_XY);
        imageView.setImageBitmap(getItem(position));
        imageView.setFocusable(false);
		*/
//		BitmapDrawable drawable = new BitmapDrawable(getItem(position));
        imageView.setScaleType(ScaleType.FIT_XY);
        Bitmap bm = getItem(position);
        BitmapDrawable drawable = new BitmapDrawable(bm);
        BitmapShader shader = new BitmapShader(bm, TileMode.CLAMP, TileMode.CLAMP);
//        drawable.setAntiAlias(true);
        Paint paint = drawable.getPaint();
        paint.setAntiAlias(true);
        matrix.setScale(width / bm.getWidth(), height / bm.getHeight(), 0.5f, 0.5f);
        shader.setLocalMatrix(matrix);
        paint.setShader(shader);
        imageView.setImageDrawable(drawable);
        return imageView;
    }

}
