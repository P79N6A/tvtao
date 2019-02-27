package com.aliyun.base.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

public class ApkUtils {
	/**
	 * 是否安装某个程序 
	 * @return
	 */
	public static boolean checkApkExist(Context context, String packageName) {
		PackageManager manager = context.getPackageManager();
		List<PackageInfo> pkgList = manager.getInstalledPackages(0);
		for (int i = 0; i < pkgList.size(); i++) {
			PackageInfo pI = pkgList.get(i);
			if (pI.packageName.equalsIgnoreCase(packageName))
				return true;
		}

		return false;
	}
}
