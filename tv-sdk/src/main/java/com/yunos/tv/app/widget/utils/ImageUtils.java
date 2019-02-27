package com.yunos.tv.app.widget.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {
	private static Paint paint;
	
	//通过resource的getDrawable(R.drawable.id).getBitmap()    
	public static Bitmap getBitmapByResource(Context context, int id) {
		/* 得到Resrouces资源对象 */    
		Resources resources = context.getResources();
		/* 得到资源中的Drawable对象 */
		Drawable drawable = resources.getDrawable(id);
		Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

		resources = null;
		drawable = null;

		return bitmap;
	}
	
	//保存bitmap图片到SD卡文件中
	public static void saveImageToSdcard(String path, Bitmap bitmap) {
		if (null == path || "".equals(path)) {
			return;
		}
		File file = new File(path);
		FileOutputStream fos = null;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//放大缩小图片  
    public static Bitmap zoomBitmap(Bitmap bitmap,int w,int h){  
    	if(bitmap == null) 
    		return null;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidht = ((float)w / width);
        float scaleHeight = ((float)h / height);
        matrix.postScale(scaleWidht, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return newbmp;
    }
    
    public static float px2dip(Context context, int px) {
		return (int) (px / getDensity(context) + 0.5f);
	}
	private static DisplayMetrics dm = null;
	private static DisplayMetrics getDm(Context context) {
		if (dm == null) {
			WindowManager mWm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			Display display = mWm.getDefaultDisplay();
			dm = new DisplayMetrics();
			display.getMetrics(dm);
		}
		return dm;
	}
    
	public static float getDensity(Context context) {
		return getDm(context).density;
	}
	public static Bitmap getScaleBitmap(Bitmap bitmap, int newWidth, int newHeight) {
		Bitmap BitmapOrg = bitmap;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		if (scaleWidth <= 0) {
			scaleWidth = 1.0f;
		}
		if (scaleHeight <= 0) {
			scaleHeight = 1.0f;
		}
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		return Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);
	}
    
    //将Drawable转化为Bitmap
  	 public static Bitmap drawableToBitmap(Drawable drawable){
  	    	int width = drawable.getIntrinsicWidth();
  	    	int height = drawable.getIntrinsicHeight();
  	    	Bitmap bitmap = Bitmap.createBitmap(width, height,
  	    			drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
  	                        : Config.RGB_565);
  	    	Canvas canvas = new Canvas(bitmap);
  	    	drawable.setBounds(0,0,width,height);
  	    	drawable.draw(canvas);
  	    	return bitmap;

  	  }

  	 //获得圆角图片的方法
  	 public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx){

  		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
  				.getHeight(), Config.ARGB_8888);
  		Canvas canvas = new Canvas(output);

  		final int color = 0xff424242;
  		final Paint paint = new Paint();
  		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
  		final RectF rectF = new RectF(rect);

  		paint.setAntiAlias(true);
  		canvas.drawARGB(0, 0, 0, 0);
  		paint.setColor(color);
  		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

  		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
  		canvas.drawBitmap(bitmap, rect, rect, paint);

  		return output;
  	}

  	//获得带倒影的图片方法
	public static Bitmap createReflectBitmap(int yShift, int reflectedImageHeight, final Bitmap originalImage) {

        final int width = originalImage.getWidth();
        final Matrix matrix = new Matrix();
        final Bitmap bitmapWithReflection = Bitmap.createBitmap(width, reflectedImageHeight,Config.ARGB_8888);

        final Canvas canvas = new Canvas(bitmapWithReflection);

        matrix.reset();
        matrix.preScale(1, -1);
    	matrix.postTranslate(0, originalImage.getHeight() + yShift);
        canvas.drawBitmap( originalImage, matrix, null);

        final Paint paint = new Paint();
        final LinearGradient shader = new LinearGradient(0, 0, 0,
                bitmapWithReflection.getHeight(), 0x90FFFFFF, 0x00FFFFFF, TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

        canvas.drawRect(0, 0, width, bitmapWithReflection.getHeight(), paint);

        return bitmapWithReflection;
    }

public static Bitmap createReflectBitmap(int reflectedImageHeight, final Bitmap originalImage) {

        final int width = originalImage.getWidth();
        final Matrix matrix = new Matrix();
        final Bitmap bitmapWithReflection = Bitmap.createBitmap(width, reflectedImageHeight,Config.ARGB_8888);

        final Canvas canvas = new Canvas(bitmapWithReflection);

        matrix.reset();
        matrix.preScale(1, -1);
    	matrix.postTranslate(0, originalImage.getHeight());
        canvas.drawBitmap( originalImage, matrix, null);

        final Paint paint = new Paint();
        final LinearGradient shader = new LinearGradient(0, 0, 0,
                bitmapWithReflection.getHeight(), 0x70FFFFFF, 0x00FFFFFF, TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

        canvas.drawRect(0, 0, width, bitmapWithReflection.getHeight(), paint);

        return bitmapWithReflection;
    }

	public static Bitmap decodeBitmap(Resources res, int resId, int targetWidth, int targetHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inDither = false;
		BitmapFactory.decodeResource(res, resId, options);// 获取这个图片的宽和高，此时返回bitmap为空
		return BitmapFactory.decodeResource(res, resId, getScaleOptions(targetWidth, targetHeight, options));
	}

	private static BitmapFactory.Options getScaleOptions(int targetWidth, int targetHeight, BitmapFactory.Options options) {

		options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight); // 参考BitmapFun
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Config.ARGB_8888;// 节约内存，默认是Bitmap.Config.ARGB_8888
		options.inTempStorage = new byte[16 * 1024];
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inDither = false;
		try {
			BitmapFactory.Options.class.getField("inNativeAlloc").setBoolean(options, true);
		} catch (Exception e) {
		}
		return options;
	}

	/**
	 * 层叠bitmap
	 * @param source
	 * @param faceBitmap 上面那层
	 * @param paint
	 * 参考		mPaint = new Paint();
				mPaint.setDither(true);// 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
				mPaint.setAntiAlias(true);// 非锯齿效果
				mPaint.setFilterBitmap(true);// 如果该项设置为true，则图像在动画进行中会滤掉对Bitmap图像的优化操作，加快显示速度
	 * @return
	 */
	public static Bitmap getOverLapImage(Bitmap source, Bitmap faceBitmap, Paint paint) {
		int targetWidth = source.getWidth();
		int targetHeight = source.getHeight();

		Bitmap newBitmap = source.copy(Config.RGB_565, true);
		source.recycle();
		source = null;
		Canvas canvas = new Canvas(newBitmap);
		
		int sourceWidth = faceBitmap.getWidth();
		int sourceHeight = faceBitmap.getHeight();
		
		float scaleWidth = ((float) targetWidth) / sourceWidth;
		float scaleHeight = ((float) targetHeight) / sourceHeight;
		Matrix matrix = new Matrix();
		matrix.setScale(scaleWidth, scaleHeight);
		canvas.drawBitmap(faceBitmap, matrix, paint);
		
		return newBitmap;
	}
	
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            final float totalPixels = width * height;
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }
	
}
