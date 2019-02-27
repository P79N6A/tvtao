package com.aliyun.image.filtershow.filters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.lang.reflect.Method;

public class FrostedGlass extends android.widget.FrostedGlass{
    
    /**
     * 将指定的Bitmap图片毛玻璃化(带指定线程的handler)
     * @param bmp
     * @param radius
     * @param threadHandler
     * @param listener
     */
    public void getFronstedGlassBitmap(final Bitmap bmp, final int radius, Handler threadHandler,
    		final OnFronstedGlassSreenDoneListener listener) {
    	if(bmp == null){
    		if(listener != null){
            	listener.onFronstedGlassSreenDone(null);
            }
    		return;
    	}
    	final Handler handle = new Handler();
    	threadHandler.post(new Runnable() {
			@Override
			public void run() {
			    stackBlur(bmp, radius);
		        handle.post(new Runnable() {
					@Override
					public void run() {
			            if(listener != null){
			            	listener.onFronstedGlassSreenDone(bmp);
			            }						
					}
				});
			}
		});
    }
    
    /**
     * 将指定的Bitmap图片毛玻璃化
     * @param bmp
     * @param radius
     * @param listener
     */
    public void getFronstedGlassBitmap(final Bitmap bmp, final int radius,
    		final OnFronstedGlassSreenDoneListener listener) {
    	if(bmp == null){
    		if(listener != null){
            	listener.onFronstedGlassSreenDone(null);
            }
    		return;
    	}
    	final Handler handle = new Handler();
     	new Thread(new Runnable() {
			@Override
			public void run() {
			    stackBlur(bmp, radius);
		        handle.post(new Runnable() {
					@Override
					public void run() {
			            if(listener != null){
			            	listener.onFronstedGlassSreenDone(bmp);
			            }						
					}
				});
			}
		}).start();
        
    }
    


    /**
     * 将指定的Bitmap图片毛玻璃化
     * 
     * @param bmp
     * @param radius
     * @param listener
     */
    public void getFronstedGlassBitmap1(final Bitmap bmp, final int radius,
            final float scale, final OnFronstedGlassSreenDoneListener listener) {
        if (bmp == null || bmp.isRecycled() || bmp.getHeight() == 0 || bmp.getWidth() == 0) {
            if (listener != null) {
                listener.onFronstedGlassSreenDone(null);
            }
            return;
        }

        final Handler handle = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
            	if (bmp == null || bmp.isRecycled()){
            		if (listener != null) {
                        listener.onFronstedGlassSreenDone(null);
                    }
                    return;
            	}
            	Bitmap scaled = Bitmap.createScaledBitmap(bmp, 480, 270, true);
                Bitmap waitFrost;
                // if (scale > 0 && scale < 1) {
                // Matrix m = new Matrix();
                // m.postScale(scale, scale);
                // scaled = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                // bmp.getHeight(), m,
                // false);
                // }

                if (scaled != null && scaled != bmp) {
                    waitFrost = scaled;
                } else {
                    waitFrost = bmp.copy(Config.ARGB_8888, true);
                }

                final Bitmap dest = waitFrost;
                stackBlur(dest, radius);
                handle.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onFronstedGlassSreenDone(dest);
                        }
                    }
                });
            }
        }).start();

    }

    
    /**
     * 同步取得截图片
     * @param bmp
     * @param radius
     * @param scale
     * @return
     */
    public static Bitmap getFronstedGlassBitmapSync(Bitmap bmp, final int radius, float scale) {
    	return getFronstedGlassBitmapSync(bmp, radius, scale, true);
    }
    
    public static Bitmap getFronstedGlassBitmapSync(Bitmap bmp, final int radius, float scale, boolean recycle) {
    	FrostedGlass fg = new FrostedGlass();
    	
    	if (bmp == null || bmp.isRecycled() || bmp.getHeight() == 0 || bmp.getWidth() == 0) {
    		return null;
    	}
    	if (scale > 0 && scale < 1) {
    		Matrix m = new Matrix();
    		m.postScale(scale, scale);
    		Bitmap b = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, false);
    		if (recycle) {
    			bmp.recycle();
    			bmp = null;
    		}
    		fg.stackBlur(b, radius);
    		return b;
    	}
    	
    	fg.stackBlur(bmp, radius);
    	return bmp;
    }
    
    
    /**
     * 取得屏幕的毛玻璃效果图片（带指定线程的handler）
     * 注：需要权限
     * 	<uses-permission android:name="android.permission.READ_FRAME_BUFFER" />
     * @param context
     * @param radius 模糊的像素越大越模糊
     * @param fronstedScale 模糊后的图片尺寸跟屏幕的缩放比例(>1缩小比例，<1为放大比例)
     * @param threadHandler 指定线程的handler
     * @param listener 完成后的回调
     */
    public void getFronstedGlassSreenShot(final Context context, final int radius, float fronstedScale,
     		Handler threadHandler, final OnFronstedGlassSreenDoneListener listener) {
     	final Handler handle = new Handler();
 		long before = System.currentTimeMillis();
         final Bitmap screenbitmap = getScreenShot(context, fronstedScale);
         final long screenShotSpend = System.currentTimeMillis() - before;
         threadHandler.post(new Runnable() {
 			@Override
 			public void run() {
 				Bitmap bmp = screenbitmap;
 		        if (screenbitmap != null && !screenbitmap.isRecycled()) {
 		    		long before = System.currentTimeMillis();
 			        stackBlur(bmp, radius);
 		            long frostedGlassSpend = System.currentTimeMillis() - before;
 		            //Log.i(TAG, "getFronstedGlassSreenShot screenShotSpend="+screenShotSpend+" frostedGlassSpend="+frostedGlassSpend);
 		        }
 	        	final Bitmap postBmp = bmp;
 		        handle.post(new Runnable() {
 					@Override
 					public void run() {
 			            if(listener != null){
 			            	listener.onFronstedGlassSreenDone(postBmp);
 			            }						
 					}
 				});
 			}
 		});
     }
    
    
    
   /**
    * 取得屏幕的毛玻璃效果图片
    * 注：需要权限
    * 	<uses-permission android:name="android.permission.READ_FRAME_BUFFER" />
    * @param context
    * @param radius 模糊的像素越大越模糊
    * @param fronstedScale 模糊后的图片尺寸跟屏幕的缩放比例(>1缩小比例，<1为放大比例)
    * @param listener 完成后的回调
    */
   public void getFronstedGlassSreenShot(final Context context, final int radius, float fronstedScale,
    		final OnFronstedGlassSreenDoneListener listener) {
    	final Handler handle = new Handler();
		long before = System.currentTimeMillis();
        final Bitmap screenbitmap = getScreenShot(context, fronstedScale);
        final long screenShotSpend = System.currentTimeMillis() - before;
     	new Thread(new Runnable() {
			@Override
			public void run() {
				Bitmap bmp = screenbitmap;
		        if (screenbitmap != null && !screenbitmap.isRecycled()) {
		    		long before = System.currentTimeMillis();
			        stackBlur(bmp, radius);
		            long frostedGlassSpend = System.currentTimeMillis() - before;
		            //Log.i(TAG, "getFronstedGlassSreenShot screenShotSpend="+screenShotSpend+" frostedGlassSpend="+frostedGlassSpend);
		        }
	        	final Bitmap postBmp = bmp;
		        handle.post(new Runnable() {
					@Override
					public void run() {
			            if(listener != null){
			            	listener.onFronstedGlassSreenDone(postBmp);
			            }						
					}
				});
			}
		}).start();
        
    }
   
   /**
    * 截取屏幕的图片
    * @param context
    * @param fronstedScale
    * @return
    */
   public static Bitmap getScreenShot(Context context, float fronstedScale) {
       WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
       DisplayMetrics dm = new DisplayMetrics();
       windowManager.getDefaultDisplay().getMetrics(dm);
       Bitmap bp = null;
       try { 
    	   int sdkVersion = Build.VERSION.SDK_INT;
    	   Class<?> surface = null;
           if (sdkVersion > 17) {
        	   surface = Class.forName("android.view.SurfaceControl");
           } 
           else{
        	   surface = Class.forName("android.view.Surface");
           }
           if(surface != null){
        	   Method m = surface.getMethod("screenshot", new Class[] {int.class, int.class});
        	   bp = (Bitmap) m.invoke(surface, new Object[] {(int)(dm.widthPixels / fronstedScale), (int)(dm.heightPixels / fronstedScale)});
           }
       } catch (Exception e) { 
    	   //Log.e(TAG, "getScreenShot e=" + e);
	   } 

       if (bp == null || bp.getByteCount() == 0) {
           return null;
       }
       Bitmap dest = getIfClipedBitmap(bp);
       if(dest.equals(bp) == false){
    	   bp.recycle();
       }
       return dest;
   }
   
   /**
    * 兼容部分芯片以1080P分辨率截屏的问题，通过判断像素，截取纯黑的部分
    */
   private static Bitmap getIfClipedBitmap(Bitmap src) {
       Bitmap dest = src;

       if (isNeedClip(src)) {
           int destWidth = src.getWidth() - getClipWidth(src);
           int destHeight = src.getHeight() - getClipHeight(src);
           dest = Bitmap.createBitmap(src, 0, 0, destWidth <= 0 ? src.getWidth() : destWidth, destHeight <= 0 ? src.getHeight() : destHeight);
       }

       return dest;
   }

   private static int getClipWidth(Bitmap bitmap) {
       int cliphWidth = 0;
       int w = bitmap.getWidth();
       int colorValue;
       for (int i = 0; i < w; i++) {
           colorValue = bitmap.getPixel(w - 1 - i, 0);
           if (colorValue != 0xFF000000) {
               cliphWidth = i + 1;
               break;
           }
       }

       return cliphWidth;
   }

   private static int getClipHeight(Bitmap bitmap) {
       int clipHeght = 0;
       int h = bitmap.getHeight();

       int colorValue;

       for (int j = 0; j < h; j++) {
           colorValue = bitmap.getPixel(0, h - 1 - j);
           if (colorValue != 0xFF000000) {
               clipHeght = j + 1;
               break;
           }
       }
       return clipHeght;
   }


   private static boolean isNeedClip(Bitmap bitmap) {
       boolean isNotBlackColor = false;

       int w = bitmap.getWidth();
       int h = bitmap.getHeight();

       int colorValue;

       for (int j = 0; j < h; j++) {
           colorValue = bitmap.getPixel(w - 1, h - 1 - j);
           if (colorValue != 0xFF000000) {
               isNotBlackColor = true;
               break;
           }
       }

       if (isNotBlackColor) {
           return false;
       }

       for (int i = 0; i < w; i++) {
           colorValue = bitmap.getPixel(w - 1 - i, h - 1);
           if (colorValue != 0xFF000000) {
               isNotBlackColor = true;
               break;
           }
       }

       if (isNotBlackColor) {
           return false;
       }

       return true;
   }
   
   public interface OnFronstedGlassSreenDoneListener{
	   public void onFronstedGlassSreenDone(Bitmap bmp);
   }
}
