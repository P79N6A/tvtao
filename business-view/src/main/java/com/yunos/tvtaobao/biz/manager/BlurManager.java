package com.yunos.tvtaobao.biz.manager;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.Log;
import android.widget.FrostedGlass;


import com.yunos.tvtaobao.businessview.R;

import java.util.ArrayList;
import java.util.List;

public class BlurManager {

    private static final String TAG = "BlurManager";

    private BlurManager() {
    }

    private static class Singleton {

        private static BlurManager instance = new BlurManager();
    }

    public static BlurManager getInstance() {
        return Singleton.instance;
    }

    public boolean blurInYunOS(Activity activity) throws Throwable {
        FrostedGlass forstedGlassEffect = new FrostedGlass();
        Bitmap forstedglassBitmap = forstedGlassEffect.getFrostedGlassBitmap(activity);
        if (forstedglassBitmap != null) {
            Log.d(TAG, TAG + ".blurInYunOS.successfully screenshot");
            List<Drawable> drawableList = new ArrayList<Drawable>();
            drawableList.add(new BitmapDrawable(activity.getResources(), forstedglassBitmap));
            ColorDrawable colorDrawable = new ColorDrawable(activity.getResources().getColor(
                    R.color.bs_up_update_black_50)); // 压黑50%
            drawableList.add(colorDrawable);
            Drawable[] layers = new Drawable[drawableList.size()];
            drawableList.toArray(layers);
            LayerDrawable layerDrawable = new LayerDrawable(layers);
            activity.getWindow().setBackgroundDrawable(layerDrawable);
            return true;
        }
        Log.w(TAG, TAG + ".blurInYunOS.cannot screenshot");
        return false;
    }
}
