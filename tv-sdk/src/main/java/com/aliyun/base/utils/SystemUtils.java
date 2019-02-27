package com.aliyun.base.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.Service;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class SystemUtils {
	public static final String TAG = "SystemUtils";
	
	/**
	 * 案桌上不能用
	 * @return
	 */
//	private boolean isSystemIdle() {
//		Runtime runtime = Runtime.getRuntime();
//		long maxMemory = runtime.maxMemory();
//		long allocatedMemory = runtime.totalMemory();
//		long freeMemory = runtime.freeMemory();
//
//		System.out.println("MaxMemoery:" + maxMemory);
//		System.out.println("AllocatedMemory:" + allocatedMemory);
//		System.out.println("freeMemeroy:" + freeMemory);
//
//		ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
//
//		if (mxBean.getThreadCount() > 175)
//			return false;
//
//		System.out.println(mxBean.getThreadCount());
//		return true;
//	}
	
	/**
	 * 获取权限
	 * 
	 * @param permission
	 *            权限
	 * @param path
	 *            路径
	 */
	public static void chmod(String permission, String path) {
		try {
			String command = "chmod " + permission + " " + path;
			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(command);
			if (process != null) {
				Log.i(TAG, command + " ok!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 应用程序运行命令获取 root权限，设备必须已破解(获得ROOT权限)
	 *   
	 *   调用示例：
	 *  	传入参数"chmod 777 " + "data"
	 *   
	 *   示例二：
	 *  String[] command={"su","-c","/data"}
		Process=Runtime.getRuntime().exec(command);
		这样可访问data文件

	 * 
	 * @param command
	 *            命令：String
	 *            apkRoot="chmod 777 "+getPackageCodePath();//RootCommand
	 *            (apkRoot);
	 * @return 应用程序是/否获取Root权限
	 */
	public static boolean rootCommand(String command) {
		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
			}
		}
		return true;
	}

	public static MemoryInfo getMemoryInfo(Context context) {
		final ActivityManager activityManager = (ActivityManager) context.getSystemService(Service.ACTIVITY_SERVICE);
		MemoryInfo info = new MemoryInfo();
		activityManager.getMemoryInfo(info);
		// Log.e("####", "系统剩余内存:" + (info.availMem << 20) + "M");
		// Log.e("####", "系统是否处于低内存运行：" + info.lowMemory);
		// Log.e("####", "当系统剩余内存低于" + (info.threshold << 20) + "M 时就看成低内存运行");
		return info;
	}

	// public static boolean isLowMemory (Context context) {
	// return getMemoryInfo(context).lowMemory;
	// }
	//
	public static int getCpuUseAngle() throws Exception {
		File file = new File("/proc/stat");
		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		StringTokenizer token = new StringTokenizer(br.readLine());
		token.nextToken();
		long user1 = Long.parseLong(token.nextToken());
		long nice1 = Long.parseLong(token.nextToken());
		long sys1 = Long.parseLong(token.nextToken());
		long idle1 = Long.parseLong(token.nextToken());
		long iowait1 = Long.parseLong(token.nextToken());
		long irq1 = Long.parseLong(token.nextToken());
		long softirq1 = Long.parseLong(token.nextToken());
		br.close();

		long used1 = user1 + nice1 + sys1 + iowait1 + irq1 + softirq1;
		long total1 = used1 + idle1;

		Thread.sleep(1000);

		br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		token = new StringTokenizer(br.readLine());
		token.nextToken();
		long user2 = Long.parseLong(token.nextToken());
		long nice2 = Long.parseLong(token.nextToken());
		long sys2 = Long.parseLong(token.nextToken());
		long idle2 = Long.parseLong(token.nextToken());
		long iowait2 = Long.parseLong(token.nextToken());
		long irq2 = Long.parseLong(token.nextToken());
		long softirq2 = Long.parseLong(token.nextToken());

		long used2 = user2 + nice2 + sys2 + iowait2 + irq2 + softirq2;
		long total2 = used2 + idle2;

		if (null != br) {
			try {
				br.close();
			} catch (Exception e) {
			}
			br = null;
		}
		return (int) ((used2 - used1) / (total2 - total1) * 100);
	}
}
