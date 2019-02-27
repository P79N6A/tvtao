package com.aliyun.base.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ScreenShot {
	static final String TAG = "ScreenShot";

	// 获取指定Activity的截屏，保存到png文件
	private static Bitmap takeScreenShot(Activity activity) {
		// View是你需要截图的View
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1 = view.getDrawingCache();

		// 获取状态栏高度
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		Log.i("TAG", "" + statusBarHeight);

		// 获取屏幕长和高
		int width = activity.getWindowManager().getDefaultDisplay().getWidth();
		int height = activity.getWindowManager().getDefaultDisplay().getHeight();
		// 去掉标题栏
		// Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);
		Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
		view.destroyDrawingCache();
		return b;
	}

	// 保存到sdcard
	private static void savePic(Bitmap b, String strFileName) {
		Log.d(TAG, "savpic : " + strFileName);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(strFileName);
			if (null != fos) {
				b.compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.flush();
				fos.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 程序入口
	public static void shoot(Activity a, String path) {
		ScreenShot.savePic(ScreenShot.takeScreenShot(a), path);
		Toast.makeText(a, "截屏成功,图片保存在 " + path, Toast.LENGTH_SHORT).show();
	}

	// 程序入口
	public static void shoot(Activity a) {
		if (Environment.getExternalStorageDirectory() != null) {
			shoot(a, Environment.getExternalStorageDirectory() + "/" + System.currentTimeMillis() + ".png");
		} else {
			Toast.makeText(a, "请插入存储卡", Toast.LENGTH_SHORT).show();
		}
	}
}
