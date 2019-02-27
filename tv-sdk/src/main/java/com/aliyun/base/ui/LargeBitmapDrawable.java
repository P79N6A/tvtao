/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aliyun.base.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;

import java.lang.ref.WeakReference;

public abstract class LargeBitmapDrawable extends Drawable {

    private static final int DEFAULT_PAINT_FLAGS =
            Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG;
    private BitmapState mBitmapState;
   // private Bitmap mBitmap;
    
    private WeakReference<Bitmap> mObject;
    private int mTargetDensity;

    private final Rect mDstRect = new Rect();   // Gravity.apply() sets this

    private boolean mApplyGravity;
    private boolean mRebuildShader;
    private boolean mMutated;
    
     // These are scaled to match the target density.
    private int mBitmapWidth = -1;
    private int mBitmapHeight = -1;
    
    
    public abstract void loadBitmap();
    
    public Bitmap getBitmap() {
    	if (mObject != null && mObject.get() != null) {
    		return mObject.get();
    	} else {
    		loadBitmap();
    	}
    	return null;
    }
    
    public void setBitmap(Bitmap bitmap) {
        mObject = new WeakReference<Bitmap>(bitmap);
        if (bitmap != null) {
            computeBitmapSize();
        } else {
            mBitmapWidth = mBitmapHeight = -1;
        }
    }
    
    public LargeBitmapDrawable() {
        mBitmapState = new BitmapState((Bitmap) null);
        mBitmapState.mTargetDensity = mTargetDensity;
    }

    public LargeBitmapDrawable(Resources res) {
        mBitmapState = new BitmapState((Bitmap) null);
        mBitmapState.mTargetDensity = mTargetDensity;
    }

    public LargeBitmapDrawable(Bitmap bitmap) {
        this(new BitmapState(bitmap), null);
    }

    public LargeBitmapDrawable(String filepath) {
        this(new BitmapState(BitmapFactory.decodeFile(filepath)), null);
        if (getBitmap() == null) {
            Log.w("BitmapDrawable", "BitmapDrawable cannot decode " + filepath);
        }
    }

    public LargeBitmapDrawable(Resources res, String filepath) {
        this(new BitmapState(BitmapFactory.decodeFile(filepath)), null);
        mBitmapState.mTargetDensity = mTargetDensity;
        if (getBitmap() == null) {
            Log.w("BitmapDrawable", "BitmapDrawable cannot decode " + filepath);
        }
    }

    public final Paint getPaint() {
        return mBitmapState.mPaint;
    }


    private void computeBitmapSize() {
    	//Log.i("LargeBitmapDrawable", "---------computeBitmapSize------------");
    	Bitmap bitmap = getBitmap();
        mBitmapWidth = bitmap.getScaledWidth(mTargetDensity);
        mBitmapHeight = bitmap.getScaledHeight(mTargetDensity);
    }



    /**
     * Set the density scale at which this drawable will be rendered. This
     * method assumes the drawable will be rendered at the same density as the
     * specified canvas.
     *
     * @param canvas The Canvas from which the density scale must be obtained.
     *
     * @see Bitmap#setDensity(int)
     * @see Bitmap#getDensity()
     */
    public void setTargetDensity(Canvas canvas) {
        setTargetDensity(canvas.getDensity());
    }

    /**
     * Set the density scale at which this drawable will be rendered.
     *
     * @param metrics The DisplayMetrics indicating the density scale for this drawable.
     *
     * @see Bitmap#setDensity(int)
     * @see Bitmap#getDensity()
     */
    public void setTargetDensity(DisplayMetrics metrics) {
        mTargetDensity = metrics.densityDpi;
        if (getBitmap() != null) {
            computeBitmapSize();
        }
    }

    /**
     * Set the density at which this drawable will be rendered.
     *
     * @param density The density scale for this drawable.
     *
     * @see Bitmap#setDensity(int)
     * @see Bitmap#getDensity()
     */
    public void setTargetDensity(int density) {
        mTargetDensity = density == 0 ? DisplayMetrics.DENSITY_DEFAULT : density;
        if (getBitmap() != null) {
            computeBitmapSize();
        }
    }

    /** Get the gravity used to position/stretch the bitmap within its bounds.
     * See android.view.Gravity
     * @return the gravity applied to the bitmap
     */
    public int getGravity() {
        return mBitmapState.mGravity;
    }
    
    /** Set the gravity used to position/stretch the bitmap within its bounds.
        See android.view.Gravity
     * @param gravity the gravity
     */
    public void setGravity(int gravity) {
        mBitmapState.mGravity = gravity;
        mApplyGravity = true;
    }

    public void setAntiAlias(boolean aa) {
        mBitmapState.mPaint.setAntiAlias(aa);
    }
    
    @Override
    public void setFilterBitmap(boolean filter) {
    	//Log.i("LargeBitmapDrawable", "---------setFilterBitmap------------");
        mBitmapState.mPaint.setFilterBitmap(filter);
    }

    @Override
    public void setDither(boolean dither) {
    	//Log.i("LargeBitmapDrawable", "---------setDither------------");
        mBitmapState.mPaint.setDither(dither);
    }

    public Shader.TileMode getTileModeX() {
        return mBitmapState.mTileModeX;
    }

    public Shader.TileMode getTileModeY() {
        return mBitmapState.mTileModeY;
    }

    public void setTileModeX(Shader.TileMode mode) {
        setTileModeXY(mode, mBitmapState.mTileModeY);
    }

    public final void setTileModeY(Shader.TileMode mode) {
        setTileModeXY(mBitmapState.mTileModeX, mode);
    }

    public void setTileModeXY(Shader.TileMode xmode, Shader.TileMode ymode) {
        final BitmapState state = mBitmapState;
        if (state.mPaint.getShader() == null ||
                state.mTileModeX != xmode || state.mTileModeY != ymode) {
            state.mTileModeX = xmode;
            state.mTileModeY = ymode;
            mRebuildShader = true;
        }
    }

    @Override
    public int getChangingConfigurations() {
    	//Log.i("LargeBitmapDrawable", "---------getChangingConfigurations------------");
        return super.getChangingConfigurations() | mBitmapState.mChangingConfigurations;
    }
    
    @Override
    protected void onBoundsChange(Rect bounds) {
    	//Log.i("LargeBitmapDrawable", "---------onBoundsChange------------");
        super.onBoundsChange(bounds);
        mApplyGravity = true;
    }

    @Override
    public void draw(Canvas canvas) {
        Bitmap bitmap = getBitmap();
        if (bitmap != null) {
            final BitmapState state = mBitmapState;
            if (mRebuildShader) {
                Shader.TileMode tmx = state.mTileModeX;
                Shader.TileMode tmy = state.mTileModeY;

                if (tmx == null && tmy == null) {
                    state.mPaint.setShader(null);
                } else {
                    Shader s = new BitmapShader(bitmap,
                            tmx == null ? Shader.TileMode.CLAMP : tmx,
                            tmy == null ? Shader.TileMode.CLAMP : tmy);
                    state.mPaint.setShader(s);
                }
                mRebuildShader = false;
                copyBounds(mDstRect);
            }

            Shader shader = state.mPaint.getShader();
            if (shader == null) {
                if (mApplyGravity) {
                    Gravity.apply(state.mGravity, mBitmapWidth, mBitmapHeight,
                            getBounds(), mDstRect);
                    mApplyGravity = false;
                }
                canvas.drawBitmap(bitmap, null, mDstRect, state.mPaint);
            } else {
                if (mApplyGravity) {
                    mDstRect.set(getBounds());
                    mApplyGravity = false;
                }
                canvas.drawRect(mDstRect, state.mPaint);
            }
        }
    }

    @Override
    public void setAlpha(int alpha) {
        mBitmapState.mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mBitmapState.mPaint.setColorFilter(cf);
    }

    /**
     * A mutable BitmapDrawable still shares its Bitmap with any other Drawable
     * that comes from the same resource.
     *
     * @return This drawable.
     */
    @Override
    public Drawable mutate() {
    	//Log.i("LargeBitmapDrawable", "---------mutate------------");
        if (!mMutated && super.mutate() == this) {
            mBitmapState = new BitmapState(mBitmapState);
            mMutated = true;
        }
        return this;
    }

    @Override
    public int getIntrinsicWidth() {
    	//Log.i("LargeBitmapDrawable", "---------getIntrinsicWidth------------" + mBitmapWidth);
        return mBitmapWidth;
    }

    @Override
    public int getIntrinsicHeight() {
    	//Log.i("LargeBitmapDrawable", "---------getIntrinsicHeight------------" + mBitmapHeight);
        return mBitmapHeight;
    }

    @Override
    public int getOpacity() {
        if (mBitmapState.mGravity != Gravity.FILL) {
            return PixelFormat.TRANSLUCENT;
        }
       // Log.i("LargeBitmapDrawable", "---------getOpacity------------");
        Bitmap bm = getBitmap();
        return (bm == null || bm.hasAlpha() || mBitmapState.mPaint.getAlpha() < 255) ?
                PixelFormat.TRANSLUCENT : PixelFormat.OPAQUE;
    }

    @Override
    public final ConstantState getConstantState() {
        mBitmapState.mChangingConfigurations = super.getChangingConfigurations();
        return mBitmapState;
    }

    final static class BitmapState extends ConstantState {
        Bitmap mBitmap;
        int mChangingConfigurations;
        int mGravity = Gravity.FILL;
        Paint mPaint = new Paint(DEFAULT_PAINT_FLAGS);
        Shader.TileMode mTileModeX;
        Shader.TileMode mTileModeY;
        int mTargetDensity = DisplayMetrics.DENSITY_DEFAULT;

        BitmapState(Bitmap bitmap) {
            mBitmap = bitmap;
        }

        BitmapState(BitmapState bitmapState) {
            this(bitmapState.mBitmap);
            mChangingConfigurations = bitmapState.mChangingConfigurations;
            mGravity = bitmapState.mGravity;
            mTileModeX = bitmapState.mTileModeX;
            mTileModeY = bitmapState.mTileModeY;
            mTargetDensity = bitmapState.mTargetDensity;
            mPaint = new Paint(bitmapState.mPaint);
        }

        @Override
        public Drawable newDrawable() {
        	//Log.i("BitmapState", "-------newDrawable-------------");
            return null;
        }
        
        @Override
        public Drawable newDrawable(Resources res) {
        	//Log.i("BitmapState", "-------newDrawable-------------");
            return null;
        }
        
        @Override
        public int getChangingConfigurations() {
            return mChangingConfigurations;
        }
    }

    private LargeBitmapDrawable(BitmapState state, Resources res) {
        mBitmapState = state;
        if (res != null) {
            mTargetDensity = res.getDisplayMetrics().densityDpi;
        } else if (state != null) {
            mTargetDensity = state.mTargetDensity;
        } else {
            mTargetDensity = DisplayMetrics.DENSITY_DEFAULT;
        }
        setBitmap(state.mBitmap);
    }
}
