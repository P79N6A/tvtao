package com.aliyun.base.info;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.StatFs;

import com.aliyun.base.utils.FileUtils;

import java.io.File;
import java.util.List;

public class BaseAppInfo {
	public static final int MAX_ROOT_SIZE = 100 * 1024 * 1024;// 100m

	public static final String ROOT_DIR = "aliyunos";
	
	private static String appRootPath;

	private static String imagePath;

	public static int versionCode = 0;

	public static String versionName = "0.0.1";
	
	public static boolean isDebug = true;
	
	public static void init(Context context) {
		ApplicationInfo appInfo = context.getApplicationInfo();
		int appFlags = appInfo.flags;
		if ((appFlags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
			isDebug = true;
		} else {
			isDebug = false;
		}
		
		PackageManager packageManager = context.getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
			versionName = packageInfo.versionName;
			versionCode = packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 判断是否在后台运行
	 * @param context
	 * @return
	 */
	public static boolean isAppBackRun(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
//		List<ActivityManager.RunningAppProcessInfo> list = activityManager.getRunningAppProcesses();//得到当前所有运行的进程信息。
		//ActivityManager.RunningAppProcessInfo中就有进程的id，名字以及该进程包括的所有apk包名列表等。
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			if (context.getPackageName().equals(tasksInfo.get(0).topActivity.getPackageName())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断是否在前台运行
	 * @param context
	 * @return
	 */
	public static boolean isAppForegroundRun(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		if (appProcesses == null) {
			return false;
		}
		for (RunningAppProcessInfo appProcess : appProcesses) {
			// The name of the process that this object is associated with.
			if (appProcess.processName.equals(context.getApplicationContext().getPackageName())
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}
		return false;
	}

	public static String getRootPath(Context context) {
		if (appRootPath == null) {
//			Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);获取系统默认的dcim目录
			appRootPath = Environment.getExternalStorageDirectory().getPath() + "/" + ROOT_DIR + "/" + context.getPackageName() + "/";
			File rootPath = new File(appRootPath);
			if (rootPath.exists()) {
				if (FileUtils.getDirectorySize(rootPath) > MAX_ROOT_SIZE) {
					FileUtils.removeDir(rootPath);
					if (!rootPath.mkdirs()) {
						appRootPath = null;
					}
				}
			} else {
				if (!rootPath.mkdirs()) {
					appRootPath = null;
				}
			}
			if (appRootPath == null) {
				File file = context.getDir("images", Context.MODE_WORLD_WRITEABLE);
				appRootPath = file.getAbsolutePath();
			}
		}
		return appRootPath;
	}

	public static String getImagePath(Context context) {
		if (imagePath == null) {
			imagePath = getRootPath(context) + "images/";
			File imgFile = new File(imagePath);
			if (!imgFile.exists()) {
				if (!imgFile.mkdirs()) {
					imagePath = null;
				}
			}
		}
		return imagePath;
	}

	public static long getSDCardAvailable() {
		File path = Environment.getExternalStorageDirectory();
		StatFs statFs = new StatFs(path.getPath());
		long blockSize = statFs.getBlockSize();
		// long totalBlocks = statFs.getBlockCount();
		long availableBlocks = statFs.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	public static long getAvailableDataSize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	public static long getTotalDataSize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	public static boolean isSDCardFull() {
		if (getSDCardAvailable() == 0) {
			return true;
		}
		return false;
	}

	public void releasAll() {
		if (appRootPath != null) {
			File rootPath = new File(appRootPath);
			if (rootPath.exists() && rootPath.isDirectory()) {
				FileUtils.removeDir(rootPath);
			}
		}
	}

}
