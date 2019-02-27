package com.yunos.tv.app.widget;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

public class LocalCache {
	
	private static String dataCachePath = null;
	
	public static File getDataCachePath(Context context) {
		if (dataCachePath == null) {
			//path: /data/data/com.mypackage.path/app_local_cache
			dataCachePath = context.getDir("local_cache",  Context.MODE_WORLD_WRITEABLE).getAbsolutePath();
		}
		File file = new File(dataCachePath);
		Log.v("TraceActivity", "dataCachePath = " + dataCachePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}

	public static void writeData(Context context, Object obj, String cacheFileName) {
		File file = null;
		FileOutputStream fileout = null;
		ObjectOutputStream objOutstream = null;
		try {
			file = new File(getDataCachePath(context), cacheFileName);
			if (!file.exists()) {
				file.createNewFile();
			}

			fileout = new FileOutputStream(file);
			objOutstream = new ObjectOutputStream(fileout);
			objOutstream.writeObject(obj);
			objOutstream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (objOutstream != null) {
					objOutstream.close();
					objOutstream = null;
				}
				if (fileout != null) {
					fileout.close();
					fileout = null;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

	public static Object readData(Context context, String cacheFileName) {
		FileInputStream filein = null;
		ObjectInputStream objInstream = null;
		try {
			File file = new File(getDataCachePath(context), cacheFileName);
			if (!file.exists()) {
				//Log.v("TraceActivity", "file not exists");
				return null;
			}
			
			//Log.v("TraceActivity", "file exists");
			filein = new FileInputStream(file);
			objInstream = new ObjectInputStream(filein);
			Object object = objInstream.readObject();
			
			//Log.v("TraceActivity", "readData object = " + object);
			return object;
		} catch (Exception e) {
			e.printStackTrace();
			
			Log.v("TraceActivity", "readData Exception");
			return null;
		} finally {
			
			//Log.v("TraceActivity", "readData Exception finally");
			
			
			try {
				if (filein != null) {
					filein.close();
					filein = null;
				}
				if (objInstream != null) {
					objInstream.close();
					objInstream = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @param context
	 * @param assetFileName  必须是"path/filename"或"filename"
	 * @return
	 */
	public static Object readAsset(Context context, String assetFileName) {
		ObjectInputStream objInstream = null;
		InputStream is = null;
		try {
			is = context.getResources().getAssets().open(assetFileName);
			if (is == null) {
				return null;
			}

			objInstream = new ObjectInputStream(is);
			return objInstream.readObject();
		} catch (Exception e) {
			return null;
		} finally {
			try {
				if (objInstream != null) {
					objInstream.close();
					objInstream = null;
				}
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (IOException e) {}
		}
	}
	
	/**
	 * 
	 * @param context
	 * @param path 如果是assets目录下的，就传""
	 * @param name
	 * @return
	 */
	public static boolean isInAsset(Context context, String path, String name) {
		try {
			String[] files = context.getAssets().list(path);
			if (files != null) {
				List<String> list = Arrays.asList(files);
				return list.contains(name);
			}
		} catch (IOException e) {
		}
		return false;
	}
	

}
