package com.yunos.tvtaobao.biz.dialog.util;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Handler;
import android.widget.FrostedGlass;

import com.yunos.tv.core.common.AppDebug;

import java.lang.ref.WeakReference;

/**
 * 毛玻璃处理
 *
 * @author yunzhong.qyz
 */
public final class SnapshotUtil {

    private static String TAG = "SnapshotUtil";
    private static boolean Shotting = false;

    public static synchronized void getFronstedSreenShot(final WeakReference<Activity> mBaseActivityRef,
                                                         final int radius, float fronstedScale, final OnFronstedGlassSreenDoneListener listener) {

        AppDebug.i(TAG, "getFronstedSreenShot -->  radius = " + radius + "; fronstedScale = " + fronstedScale
                + "; Shotting = " + Shotting);

        final Handler mHandle = new Handler();
        if (Shotting) {
            mHandle.post(new MainHandleRunnable(mBaseActivityRef, listener, null));
            return;
        }
        Shotting = true;
        AppDebug.v(TAG, TAG + ".getFronstedSreenShot.1 ");
        new Thread(new SnapshotRunnable(mHandle, mBaseActivityRef, listener)).start();
    }

    /**
     * 截图的Runnable
     */
    private static class SnapshotRunnable implements Runnable {

        private final WeakReference<Activity> mReference;
        private final Handler mHandle;
        private final OnFronstedGlassSreenDoneListener listener;

        public SnapshotRunnable(Handler mHandle, final WeakReference<Activity> mBaseActivityRef,
                                OnFronstedGlassSreenDoneListener listener) {
            mReference = mBaseActivityRef;
            this.mHandle = mHandle;
            this.listener = listener;
        }

        @Override
        public void run() {
            Bitmap postBmp = null;
            try {
                FrostedGlass fg = new FrostedGlass();
                if (mReference != null && mReference.get() != null) {
                    postBmp = fg.getFrostedGlassBitmap(mReference.get());
                    AppDebug.i(TAG, "SnapshotRunnable -->  mReference = " + mReference + "; postBmp = " + postBmp);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            Shotting = false;
            AppDebug.i(TAG, "SnapshotRunnable -->  listener = " + listener + "; postBmp = " + postBmp + "; mHandle = "
                    + mHandle);
            if (mHandle != null) {
                mHandle.post(new MainHandleRunnable(mReference, listener, postBmp));
            }
        }
    }

    /**
     * 主线程的Runnable
     */
    private static class MainHandleRunnable implements Runnable {

        private final OnFronstedGlassSreenDoneListener listener;
        private final WeakReference<Activity> mReference;
        private final Bitmap bm;

        public MainHandleRunnable(WeakReference<Activity> mBaseActivityRef, OnFronstedGlassSreenDoneListener listener,
                                  Bitmap bm) {
            this.listener = listener;
            this.bm = bm;
            mReference = mBaseActivityRef;
        }

        @Override
        public void run() {
            AppDebug.i(TAG, "MainHandleRunnable -->  listener = " + listener + "; bm = " + bm);
            if (listener != null) {
                listener.onFronstedGlassSreenDone(bm);
            }
        }
    }

    public interface OnFronstedGlassSreenDoneListener {

        void onFronstedGlassSreenDone(Bitmap bitmap);
    }

    /**
     * 将给定的bitmap高斯模糊
     *
     * @param bitmap
     * @param radius
     * @param listener
     */
    public static synchronized void getFronstedBitmap(final Bitmap bitmap, final int radius, final OnFronstedGlassSreenDoneListener listener) {

        final Handler mHandle = new Handler();
        if (Shotting) {
            mHandle.post(new Runnable() {

                @Override
                public void run() {
                    if (listener != null) {
                        listener.onFronstedGlassSreenDone(null);
                    }
                }

            });
            return;
        }

        Shotting = true;

        new Thread(new Runnable() {

            @Override
            public void run() {
                Bitmap bmp = bitmap;
                if (bmp != null && !bmp.isRecycled()) {

                    try {
                        int bitmap_w = bmp.getWidth();
                        int bitmap_h = bmp.getHeight();

                        Matrix matrix = new Matrix();
                        matrix.reset();
                        matrix.postScale(0.25f, 0.25f);
                        Bitmap bmpStack = Bitmap.createBitmap(bmp, 0, 0, bitmap_w, bitmap_h, matrix, true);

                        FrostedGlass fg = new FrostedGlass();
                        fg.stackBlur(bmpStack, radius);
                        final Bitmap postBmp = bmpStack;
                        mHandle.post(new Runnable() {

                            @Override
                            public void run() {
                                if (listener != null) {
                                    listener.onFronstedGlassSreenDone(postBmp);
                                }
                            }
                        });
                    } catch (Throwable e) {
                        if (listener!=null){
                            listener.onFronstedGlassSreenDone(null);
                        }
                        AppDebug.e(TAG, "FrostedGlass error:" + e);
                    }
                } else {
                    mHandle.post(new Runnable() {

                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onFronstedGlassSreenDone(null);
                            }
                        }
                    });
                }
                Shotting = false;
            }
        }).start();
    }


    public static void fastBlur(final Bitmap sentBitmap, final int radius, final OnFronstedGlassSreenDoneListener listener) {


        if (Shotting) {
            if (listener != null) {
                listener.onFronstedGlassSreenDone(null);
            }
            return;
        }

        Shotting = true;

        new Thread(new Runnable() {

            @Override
            public void run() {
                Bitmap bmp = sentBitmap;
                if (bmp != null && !bmp.isRecycled()) {

                    try {

                        int bitmap_w = bmp.getWidth();
                        int bitmap_h = bmp.getHeight();

                        Matrix matrix = new Matrix();
                        matrix.reset();
                        matrix.postScale(0.25f, 0.25f);
                        bmp = Bitmap.createBitmap(bmp, 0, 0, bitmap_w, bitmap_h, matrix, true);
                        Bitmap bitmap = bmp.copy(bmp.getConfig(), true);

                        if (radius < 1) {
                            return;
                        }

                        int w = bitmap.getWidth();
                        int h = bitmap.getHeight();

                        int[] pix = new int[w * h];
                        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

                        int wm = w - 1;
                        int hm = h - 1;
                        int wh = w * h;
                        int div = radius + radius + 1;

                        int r[] = new int[wh];
                        int g[] = new int[wh];
                        int b[] = new int[wh];
                        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
                        int vmin[] = new int[Math.max(w, h)];

                        int divsum = (div + 1) >> 1;
                        divsum *= divsum;
                        int dv[] = new int[256 * divsum];
                        for (i = 0; i < 256 * divsum; i++) {
                            dv[i] = (i / divsum);
                        }

                        yw = yi = 0;

                        int[][] stack = new int[div][3];
                        int stackpointer;
                        int stackstart;
                        int[] sir;
                        int rbs;
                        int r1 = radius + 1;
                        int routsum, goutsum, boutsum;
                        int rinsum, ginsum, binsum;

                        for (y = 0; y < h; y++) {
                            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
                            for (i = -radius; i <= radius; i++) {
                                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                                sir = stack[i + radius];
                                sir[0] = (p & 0xff0000) >> 16;
                                sir[1] = (p & 0x00ff00) >> 8;
                                sir[2] = (p & 0x0000ff);
                                rbs = r1 - Math.abs(i);
                                rsum += sir[0] * rbs;
                                gsum += sir[1] * rbs;
                                bsum += sir[2] * rbs;
                                if (i > 0) {
                                    rinsum += sir[0];
                                    ginsum += sir[1];
                                    binsum += sir[2];
                                } else {
                                    routsum += sir[0];
                                    goutsum += sir[1];
                                    boutsum += sir[2];
                                }
                            }
                            stackpointer = radius;

                            for (x = 0; x < w; x++) {

                                r[yi] = dv[rsum];
                                g[yi] = dv[gsum];
                                b[yi] = dv[bsum];

                                rsum -= routsum;
                                gsum -= goutsum;
                                bsum -= boutsum;

                                stackstart = stackpointer - radius + div;
                                sir = stack[stackstart % div];

                                routsum -= sir[0];
                                goutsum -= sir[1];
                                boutsum -= sir[2];

                                if (y == 0) {
                                    vmin[x] = Math.min(x + radius + 1, wm);
                                }
                                p = pix[yw + vmin[x]];

                                sir[0] = (p & 0xff0000) >> 16;
                                sir[1] = (p & 0x00ff00) >> 8;
                                sir[2] = (p & 0x0000ff);

                                rinsum += sir[0];
                                ginsum += sir[1];
                                binsum += sir[2];

                                rsum += rinsum;
                                gsum += ginsum;
                                bsum += binsum;

                                stackpointer = (stackpointer + 1) % div;
                                sir = stack[(stackpointer) % div];

                                routsum += sir[0];
                                goutsum += sir[1];
                                boutsum += sir[2];

                                rinsum -= sir[0];
                                ginsum -= sir[1];
                                binsum -= sir[2];

                                yi++;
                            }
                            yw += w;
                        }
                        for (x = 0; x < w; x++) {
                            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
                            yp = -radius * w;
                            for (i = -radius; i <= radius; i++) {
                                yi = Math.max(0, yp) + x;

                                sir = stack[i + radius];

                                sir[0] = r[yi];
                                sir[1] = g[yi];
                                sir[2] = b[yi];

                                rbs = r1 - Math.abs(i);

                                rsum += r[yi] * rbs;
                                gsum += g[yi] * rbs;
                                bsum += b[yi] * rbs;

                                if (i > 0) {
                                    rinsum += sir[0];
                                    ginsum += sir[1];
                                    binsum += sir[2];
                                } else {
                                    routsum += sir[0];
                                    goutsum += sir[1];
                                    boutsum += sir[2];
                                }

                                if (i < hm) {
                                    yp += w;
                                }
                            }
                            yi = x;
                            stackpointer = radius;
                            for (y = 0; y < h; y++) {
                                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                                rsum -= routsum;
                                gsum -= goutsum;
                                bsum -= boutsum;

                                stackstart = stackpointer - radius + div;
                                sir = stack[stackstart % div];

                                routsum -= sir[0];
                                goutsum -= sir[1];
                                boutsum -= sir[2];

                                if (x == 0) {
                                    vmin[y] = Math.min(y + r1, hm) * w;
                                }
                                p = x + vmin[y];

                                sir[0] = r[p];
                                sir[1] = g[p];
                                sir[2] = b[p];

                                rinsum += sir[0];
                                ginsum += sir[1];
                                binsum += sir[2];

                                rsum += rinsum;
                                gsum += ginsum;
                                bsum += binsum;

                                stackpointer = (stackpointer + 1) % div;
                                sir = stack[stackpointer];

                                routsum += sir[0];
                                goutsum += sir[1];
                                boutsum += sir[2];

                                rinsum -= sir[0];
                                ginsum -= sir[1];
                                binsum -= sir[2];

                                yi += w;
                            }
                        }

                        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
                        final Bitmap postBmp = bitmap;
                        if (listener != null) {
                            listener.onFronstedGlassSreenDone(postBmp);
                        }

                    } catch (Throwable e) {
                        if (listener != null) {
                            listener.onFronstedGlassSreenDone(null);
                        }
                        AppDebug.e(TAG, "FrostedGlass error:" + e);
                    }
                } else {
                    if (listener != null) {
                        listener.onFronstedGlassSreenDone(null);
                    }
                }
                Shotting = false;
            }
        }).start();
    }
}
