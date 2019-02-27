package com.aliyun.base.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {

/*==============================================================
 * getScaleBitmap start
==============================================================*/
	/**
	 * 非等比，压缩或放大图片到指定尺寸。
	 * 
	 * @param bitmap
	 *            需要压缩的图片
	 * @param newWidth
	 *            图片新的宽度
	 * @param newHeight
	 *            图片新的高度
	 * @return
	 */
	public static Bitmap getScaleBitmap(Bitmap source, int targetWidth, int targetHeight) {
		int sourceWidth = source.getWidth();
		int sourceHeight = source.getHeight();
		float scaleWidth = ((float) targetWidth) / sourceWidth;
		float scaleHeight = ((float) targetHeight) / sourceHeight;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap result = Bitmap.createBitmap(source, 0, 0, sourceWidth, sourceHeight, matrix, true);
        source.recycle();
        source = null;
        return result;
	}
	
	/**
	 * 等比，压缩或放大图片到指定尺寸。返回的bitmap至少有一个边等于targetWidth或targetHeight。
	 * @param source
	 * @param targetWidth
	 * @param targetHeight
	 * @return
	 */
    public static Bitmap getScaleBitmapAdjust(Bitmap source, int targetWidth, int targetHeight) {  
    	if(source == null) {
    		return null;
    	}
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) targetWidth / sourceWidth);
        float scaleHeight = ((float) targetHeight / sourceHeight);
        if(scaleWidth < scaleHeight) {
        	if (scaleWidth < .99F || scaleWidth > 1F) {
        		matrix.postScale(scaleWidth, scaleWidth);
        	} else {
        		return source;
        	}
        } else{
        	if (scaleHeight < .99F || scaleHeight > 1F) {
        		matrix.postScale(scaleHeight, scaleHeight);
        	} else {
        		return source;
        	}
        }
        Bitmap result = Bitmap.createBitmap(source, 0, 0, sourceWidth, sourceHeight, matrix, true);
        source.recycle();
        source = null;
        return result;
    }
    
	/**
	 * 等比，压缩或放大图片到指定尺寸。返回的bitmap至少有一个边等于targetWidth或targetHeight。
	 * 
	 * @return
	 */
	public static Bitmap getScaleBitmapAdjust(Bitmap source, int targetWidth, int targetHeight, float centerX, float centerY) {
		if (source == null) {
    		return null;
    	}
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) targetWidth / sourceWidth);
        float scaleHeight = ((float) targetHeight / sourceHeight);
		if(scaleWidth < scaleHeight) {
        	if (scaleWidth < .99F || scaleWidth > 1F) {
        		matrix.postScale(scaleWidth, scaleWidth, centerX, centerY);
        	} else {
        		return source;
        	}
        } else{
        	if (scaleHeight < .99F || scaleHeight > 1F) {
        		matrix.postScale(scaleHeight, scaleHeight, centerX, centerY);
        	} else {
        		return source;
        	}
        }
		Bitmap result = Bitmap.createBitmap(source, 0, 0, sourceWidth, sourceHeight, matrix, true);
        source.recycle();
        source = null;
        return result;
	}

	/**
	 * 只压缩，不放大
	 * 最大边按照bound参数进行等比压缩。只要有一个边超过bound值，则进行等比压缩，否则返回原图。
	 * 使用Bitmap.createScaledBitmap方法 
	 * 
	 * @param source
	 * @param bound 
	 * @return
	 */
	public static Bitmap getCompressBitmap(Bitmap source, float bound) {
		if (source == null) {
			return null;
		}
		float width = source.getWidth();
		float height = source.getHeight();

		if (width > bound || height > bound) {
			float dstWidth = 0;
			float dstHeight = 0;
			if (width > height) {
				dstHeight = bound / width * height;
				dstWidth = bound;
			} else {
				dstWidth = bound / height * width;
				dstHeight = bound;
			}
			Bitmap temp = Bitmap.createScaledBitmap(source, (int) dstWidth, (int) dstHeight, false);
			source.recycle();
			source = null;
			return temp;
		}
		return source;
	}
/*==============================================================
 * getScaleBitmap end
==============================================================*/

	
/*==============================================================
 * 边读边压缩，使用BitmapFactory.Options
 * decodeBitmap begin
==============================================================*/
	/**
	 * 以最省内存的方式读取本地资源的图片
	 * 根据显示器分辨率适当压缩
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap decodeBitmap(Context context, int resId) {
		InputStream is = null;
		try {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inJustDecodeBounds = true;
			opt.inDither = false;
			is = context.getResources().openRawResource(resId);
			BitmapFactory.decodeStream(is, null, opt);
			DisplayMetrics dm = context.getResources().getDisplayMetrics();
			Bitmap bitmap = BitmapFactory.decodeStream(is, null, getScaleOptions(dm.widthPixels, dm.heightPixels, opt));
			return bitmap;
		} finally {
			try {
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (IOException e) {
			}
		}
	}
	
	/**
	 * 以最省内存的方式读取本地资源的图片，根据屏幕分辨率进行适屏
	 * 
	 * 参考http://onewayonelife.iteye.com/blog/1158698
	 * decodeStream最大的秘密在于其直接调用 JNI >> nativeDecodeAsset() 来完成decode，  
     * 无需再使用java层的createBitmap，从而节省了java层的空间
     * 
	 * @param context
	 * @param path
	 * @return
	 */
	public static Bitmap decodeBitmap(Context context, String path) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return decodeBitmap(path, dm.widthPixels, dm.heightPixels);
	}
	
	/**
	 * 以最省内存的方式读取本地资源的图片，不进行压缩
	 * @param path
	 * @return
	 */
	public static Bitmap decodeBitmap(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inDither = false;
		options.inPreferredConfig = Config.RGB_565;
		options.inPurgeable = true;
		options.inInputShareable = true;
		try {
			BitmapFactory.Options.class.getField("inNativeAlloc").setBoolean(options,true);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return BitmapFactory.decodeFile(path, options);
	}

	/**
	 * 边读边压缩，使用BitmapFactory.Options.inSampleSize
	 * 不一定压缩图片，除非原图宽/2>widthPx或者原图高/2>heightPx，不然inSampleSize不起作用
	 *
	 * @param path 本地图片
	 * @param widthPx
	 * @param heightPx
	 * @return
	 */
	public static Bitmap decodeBitmap(String path, int widthPx, int heightPx) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		//If set to true, the decoder will return null (no bitmap), but the out... fields will still be set
		options.inJustDecodeBounds = true;
		options.inDither = false;
		BitmapFactory.decodeFile(path, options);// 获取这个图片的宽和高，此时返回bitmap为空
		return BitmapFactory.decodeFile(path, getScaleOptions(widthPx, heightPx, options));
	}

	/**
	 * 边读边压缩，使用BitmapFactory.Options.inSampleSize
	 * @param path
	 * @param widthPx
	 * @param heightPx
	 * @param inPreferredConfig
	 * @return
	 */
	public static Bitmap decodeBitmap(String path, int widthPx, int heightPx, Config inPreferredConfig) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inDither = false;
		BitmapFactory.decodeFile(path, options);
		options = getScaleOptions(widthPx, heightPx, options);
		options.inPreferredConfig = inPreferredConfig;
		return BitmapFactory.decodeFile(path, options);
	}

	/**
	 * 边读边压缩，使用BitmapFactory.Options.inSampleSize
	 * @param is 此函数不负责流的关闭
	 * @param widthPx
	 * @param heightPx
	 * @return
	 */
	public static Bitmap decodeBitmap(InputStream is, int widthPx, int heightPx) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inDither = false;
		BitmapFactory.decodeStream(is, null, options);
		return BitmapFactory.decodeStream(is, null, getScaleOptions(widthPx, heightPx, options));
	}

	/**
	 * 边读边压缩，使用BitmapFactory.Options.inSampleSize
	 * @param data
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap decodeBitmap(byte[] data, int width, int height) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inDither = false;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		return BitmapFactory.decodeByteArray(data, 0, data.length, getScaleOptions(width, height, options));
	}

	/**
	 * 边读边压缩取得bitmap，再使用ThumbnailUtils取缩略图
	 * @param path
	 * @param widthPx
	 * @param heightPx
	 * @return
	 */
	public static Bitmap getThumbnail(String path, int widthPx, int heightPx) {
		Bitmap bitmapTmp = decodeBitmap(path, widthPx, heightPx);
		Bitmap bitmap = ThumbnailUtils.extractThumbnail(bitmapTmp, widthPx, heightPx, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		bitmapTmp.recycle();
		bitmapTmp = null;
		return bitmap;
	}

	/**
	 * 返回BitmapFactory.Options
	 * 参考：http://developer.android.com/reference/android/graphics/BitmapFactory.Options.html#inSampleSize
	 *
	 * 	// borrowed from:
		// https://github.com/thest1/LazyList/blob/master/src/com/fedorvlasov/lazylist/ImageLoader.java
	 *
	 * 边读边压缩方式，如果scaleWidth或scaleHeight都<=1，则scale=1，不会压缩
	 *
	 * @param targetWidth
	 * @param targetHeight
	 * @param options
	 * @return
	 */
	private static BitmapFactory.Options getScaleOptions(int targetWidth, int targetHeight, BitmapFactory.Options options) {
		// 计算缩放比
		int scale = 1;
		int width_tmp = options.outWidth, height_tmp = options.outHeight;

		while (true) {
			if (width_tmp / 2 <= targetWidth || height_tmp / 2 <= targetHeight) {
				break;
			}
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		if (width_tmp > targetWidth || height_tmp > targetHeight) {
			int scaleWidth = Math.round(width_tmp / (float) targetWidth);
			int scaleHeight = Math.round(height_tmp / (float) targetHeight);
			int max = scaleWidth > scaleHeight ? scaleWidth : scaleHeight;// max在[1,2)区间范围内
			if (scale > 1) {
				scale += max;
			} else {
				scale = max;
			}
		}

		/**
		 * 关于inSampleSize:
		 * Any value <= 1 is treated the same as 1
		 * 如果这个值为2，则取出的缩略图的宽和高都是原始图片的1/2，图片大小就为原始大小的1/4。
		 * 最好是2的n次幂值，powers of 2 are often faster/easier for the decoder to honor
		 */
		options.inSampleSize = scale;
		options.inPreferredConfig = Config.RGB_565;// 节约内存，默认是Bitmap.Config.ARGB_8888
		/**
		 * 如果 inPurgeable 设为True的话表示使用BitmapFactory创建的Bitmap用于存储Pixel的内存空间在系统内存不足时可以被回收，
	     * 在应用需要再次访问Bitmap的Pixel时（如绘制Bitmap或是调用getPixel），系统会再次调用BitmapFactory decoder重新生成Bitmap的Pixel数组
	     * 参考http://www.imobilebbs.com/wordpress/archives/1611
		 */
//		options.inTargetDensity =
		options.inTempStorage = new byte[64 * 1024];
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inJustDecodeBounds = false;
		options.inDither = false;
		//inNativeAlloc配合inPurgeable使用，把不使用内存算到vm中
		try {
			BitmapFactory.Options.class.getField("inNativeAlloc").setBoolean(options,true);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return options;
	}
/*==============================================================
 *  decodeBitmap end
==============================================================*/


/*==============================================================
 *  一些特效 ，倒影，圆角
==============================================================*/
	/**
	 * 创建倒影图片，不连原图
	 *
	 * @param originalBitmap
	 * @param ratio
	 *            输出bitmap的高度是原图高度几分之几
	 * @return
	 */
	public static Bitmap createReflectedImage(Bitmap originalBitmap, float ratio) {
		int width = originalBitmap.getWidth();
		int height = originalBitmap.getHeight();

		int outHeight = Math.round(height * ratio);

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
		Bitmap reflectionBitmap = Bitmap.createBitmap(originalBitmap, 0, height - outHeight, width, outHeight, matrix, false);
		originalBitmap.recycle();
		originalBitmap = null;

		Bitmap dstbitmap = Bitmap.createBitmap(width, outHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(dstbitmap);
		canvas.drawBitmap(reflectionBitmap, 0, 0, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, 0, 0, dstbitmap.getHeight(), 0xffffffff, 0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, 0, dstbitmap.getWidth(), dstbitmap.getHeight(), paint);

		reflectionBitmap.recycle();
		reflectionBitmap = null;
		return dstbitmap;
	}

	/**
	 * 创建一个倒影图片，连带原图
	 *
	 * @param originalBitmap
	 * @param ratio
	 *            倒影部分bitmap的高度是原图高度几分之几
	 * @param reflectionGap
	 *            倒影上面的间隙
	 * @return
	 */
	public static Bitmap createReflectionImageWithOrigin(Bitmap originalBitmap, float ratio, int reflectionGap) {
		// 图片与倒影间隔距离
		int width = originalBitmap.getWidth();
		int height = originalBitmap.getHeight();

		int outHeight = Math.round(height * ratio);

		Matrix matrix = new Matrix();
		// 图片缩放，x轴变为原来的1倍，y轴为-1倍,实现图片的反转
		matrix.preScale(1, -1);

		// 创建反转后的图片Bitmap对象，图片高是原图的ratio
		Bitmap reflectionBitmap = Bitmap.createBitmap(originalBitmap, 0, (height - outHeight), width, outHeight, matrix, false);
		// 创建标准的Bitmap对象，宽和原图一致,图片高是原图的ratio
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + outHeight + reflectionGap), Config.ARGB_8888);

		// 构造函数传入Bitmap对象，为了在图片上画图
		Canvas canvas = new Canvas(bitmapWithReflection);
		// 画原始图片
		canvas.drawBitmap(originalBitmap, 0, 0, null);

		// 画间隔矩形
		Paint deafalutPaint = new Paint();
		deafalutPaint.setColor(Color.WHITE);
		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

		// 画倒影图片
		canvas.drawBitmap(reflectionBitmap, 0, height + reflectionGap, null);

		// 实现倒影效果
		Paint paint = new Paint();
		/**
		 * 参数一:为渐变起初点坐标x位置， 参数二:为y轴位置， 参数三和四:分辨对应渐变终点， 最后参数为平铺方式.
		 * TileMode.MIRROR
		 * 这里设置为镜像Gradient是基于Shader类，所以我们通过Paint的setShader方法来设置这个渐变
		 */
		LinearGradient shader = new LinearGradient(0, originalBitmap.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionGap,
				0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to scale porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

		// 覆盖效果// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);
		reflectionBitmap.recycle();
		reflectionBitmap = null;
		originalBitmap.recycle();
		originalBitmap = null;
		return bitmapWithReflection;
	}

	/**
	 * 返回一个带圆角的图片
	 *
	 * @param bitmap
	 *            指定的图片
	 * @param pixels
	 *            圆角的半径
	 * @return 返回生成的图片
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, pixels, pixels, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		bitmap.recycle();
		bitmap = null;
		return output;
	}

	/*==============================================================
	 *  others
	==============================================================*/
	/**
	 * 此函数只能解析contentUri，不能解析fileUri
	 *
	 * 两种Uri:
	 * (1) fileUri: Uri uri = Uri.fromFile(); // file:///mnt/sdcard/mm.jpg
			uri.getPath():/mnt/sdcard/mm.jpg
	 * uri.getPath()可以得到sd卡上的真实路径，不需要getRealPathFromURI()
	 *
	 * (2) contentUri: 选取照片返回的Intent intent;
	 * intent.getData()是一个Uri //  content://media/external/images/media/495
	 * getRealPathFromURI: /mnt/sdcard/DCIM/Camera/2012-06-09 14.41.46.jpg
	 * uri.getPath(): /external/images/media/495
	 * 不是sd上的真实路径，需要通过getRealPathFromURI()
	 *
	 * 补充，两个uri都可能通过 Bitmap bitmp =
	 * MediaStore.Images.Media.getBitmap(this.getContentResolver(), pickUri);//
	 * 通过uri获取bitmap对象
	 *
	 * 其实是去数据库查询data值
	 */
	public static String getRealPathFromURI(Activity activity, Uri contentUri) {
		Cursor cursor = activity.managedQuery(contentUri, new String[] { MediaStore.Images.Media.DATA }, null, null, null);
		if (cursor == null || cursor.getCount() == 0) {
//			Toast.makeText(this, "找不到路径", Toast.LENGTH_SHORT).show();
			Log.i("aabb", "===========not find pic");
			return null;
		}
		int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
		cursor.moveToNext();
		String picPath = cursor.getString(column_index);
		cursor.close();
		return picPath;
	}

	/**
	 * 获取bitmap大小
	 * @param bitmap
	 * @return bitmap.getRowBytes() * bitmap.getHeight();
	 */
	public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        // Pre HONEYCOMB_MR1
        return bitmap.getRowBytes() * bitmap.getHeight();
	}

	/**
	 * Drawable转变Bitmap
	 *
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawable2Bitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
				drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 : Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		// canvas.setBitmap(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}
	
	/**
	 * bitmap转为bytes数组
	 * @param bitmap
	 * @return
	 */
	private byte[] bitmap2Bytes(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
	
	/**
	 * 保存图片到sd卡
	 * 
	 * @param localPath
	 * @param bitmap
	 */
	public static void writeBitmap2Local(Bitmap bitmap, String localPath) throws Exception {
		if (null == localPath || "".equals(localPath)) {
			return;
		}
		File file = new File(localPath);
		FileOutputStream fos = null;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			throw e;
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
}
