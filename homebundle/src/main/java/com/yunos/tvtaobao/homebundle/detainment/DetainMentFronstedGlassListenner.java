package com.yunos.tvtaobao.homebundle.detainment;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;

import com.yunos.tvtaobao.biz.dialog.util.SnapshotUtil.OnFronstedGlassSreenDoneListener;

import java.lang.ref.WeakReference;

public class DetainMentFronstedGlassListenner implements OnFronstedGlassSreenDoneListener {

    private WeakReference<View> mViewReference = null;
    private Bitmap mBitmap = null;

    public DetainMentFronstedGlassListenner(WeakReference<View> v) {
        mViewReference = v;
    }

    @Override
    public void onFronstedGlassSreenDone(Bitmap bitmap) {
        mBitmap = bitmap;
        if (mViewReference != null && mViewReference.get() != null) {
            View view = mViewReference.get();
            if (mBitmap != null && !mBitmap.isRecycled()) { 
                Drawable[] array = new Drawable[2];
                array[0] = new BitmapDrawable(mBitmap);
                array[1] = new ColorDrawable(view.getResources().getColor(com.yunos.tvtaobao.businessview.R.color.ytbv_shadow_color_50));
                LayerDrawable la = new LayerDrawable(array);
                view.setBackgroundDrawable(la);                
            } 
        }
    }

    public void onRecycleBitmap() {
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
        }
        mBitmap = null;
    }
    
    public void onDestroy(){
        onRecycleBitmap();
        mViewReference = null;
    }

}
