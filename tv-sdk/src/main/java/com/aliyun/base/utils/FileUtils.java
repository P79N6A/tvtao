package com.aliyun.base.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
	public static final String TAG = "FileUtils";

	public static int getDirectorySize(File directory) {
		if (directory.listFiles() == null)
			return 0;
		int size = 0;
		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				size += getDirectorySize(file);
			} else {
				size += file.length();
			}
		}
		return size;
	}

	public static void copyFile(File src, File dst) {
		if (!src.isFile() || !src.exists() || (dst.exists() && !dst.isFile())) {
			Log.w(TAG, "copyFile cancel!");
			return;
		}

		FileInputStream srcFis = null;
		FileOutputStream dstFos = null;
		try {
			srcFis = new FileInputStream(src);
			File p = dst.getParentFile();
			if (!p.exists()) {
				p.mkdirs();
			}
			if (!dst.exists()) {
				dst.createNewFile();
			}
			dstFos = new FileOutputStream(dst);
			byte[] b = new byte[16 * 1024];
			int len = 0;
			while ((len = srcFis.read(b)) != -1) {
				dstFos.write(b, 0, len);
			}
			dstFos.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (dstFos != null) {
					dstFos.close();
					dstFos = null;
				}
				if (srcFis != null) {
					srcFis.close();
					srcFis = null;
				}
			} catch (IOException e) {
			}
		}
	}

	public static void copyDir(File src, File dst) {
		if (!src.isDirectory() || !dst.isDirectory()) {
			return;
		}
		if (!dst.exists()) {
			dst.mkdirs();
		}
		for (File tFile : src.listFiles()) {
			if (tFile.isDirectory()) {
				copyDir(tFile, new File(dst, tFile.getName()));
			} else {
				copyFile(tFile, new File(dst, tFile.getName()));
			}
		}
	}

	public static void removeDir(File file) {
		if (file.exists()) {
			for (File tFile : file.listFiles()) {
				if (tFile.isDirectory())
					removeDir(tFile);
				else {
					tFile.delete();
				}
			}
			file.delete();
		}
	}

	/**
	 * 读取raw文件
	 *
	 * @param context
	 * @param rawId
	 *            传R.raw.my_raw
	 */
	public static String rawRead(Context context, int rawId) {
		String result = null;
		InputStream is = null;
		try {
			is = context.getResources().openRawResource(rawId);

			int len = is.available();
			byte[] buffer = new byte[len];

			is.read(buffer);
//			result = EncodingUtils.getString(buffer, "utf-8");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;

	}

	/**
	 * 读取assets文件
	 *
	 * @param context
	 * @param fileName
	 *            assets下文件，如"test/my_assets_test.txt"
	 */
	public static String readAssets(Context context, String fileName) {
		String result = null;
		InputStream is = null;
		try {
			is = context.getResources().getAssets().open(fileName);
			int len = is.available();
			byte[] buffer = new byte[len];

			is.read(buffer);
//			result = EncodingUtils.getString(buffer, "utf-8");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 拷贝asset文件到dst路径
	 *
	 * @param context
	 *            上下文环境
	 * @param assetFile
	 *            asset下的apk
	 * @param dstFile
	 *            安装路径
	 * @return
	 */
	public static boolean copyFileFromAssets(Context context, String assetFile, String dstFile) {
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			is = context.getAssets().open(assetFile);

			File file = new File(dstFile);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			fos = new FileOutputStream(file);

			byte[] temp = new byte[5 * 1024];
			int len = 0;
			while ((len = is.read(temp)) != -1) {
				fos.write(temp, 0, len);
			}
			fos.flush();

			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
					fos = null;
				}
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	/**
	 * 读取sdcard文件
	 *
	 * @param context
	 * @param fileName
	 *            sdcard文件,如"/sdcard/test/my_sdcard_test.txt"
	 */
	public static String sdcardRead(Context context, String fileName) {
		String result = null;
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(fileName);

			int len = fis.available();
			byte[] buffer = new byte[len];

			fis.read(buffer);
//			result = EncodingUtils.getString(buffer, "UTF-8");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
					fis = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;

	}

	/**
	 * 检查path目录下空闲空间大小
	 *
	 * @param path
	 *            The path to check
	 * @return The space available in bytes
	 */
	public static long getUsableSpace(File path) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			return path.getUsableSpace();
		}
		final StatFs stats = new StatFs(path.getPath());
		return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
	}

	// getExternalCacheDir: /mnt/sdcard/Android/data/com.example.test/cache
	public static File getExternalCacheDir(Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			return context.getExternalCacheDir();
		}

		// Before Froyo we need to construct the external cache dir ourselves
		final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
		return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
	}

	/**
	 * sd卡是否可以卸载
	 *
	 * @return
	 */
	public static boolean isExternalStorageRemovable() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			return Environment.isExternalStorageRemovable();
		}
		return true;
	}

}
