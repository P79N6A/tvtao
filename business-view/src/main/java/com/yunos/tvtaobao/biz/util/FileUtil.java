/**
 * $
 * PROJECT NAME: TVTaobao
 * PACKAGE NAME: com.yunos.tvtaobao.util
 * FILE NAME: ImageFileUtil.java
 * CREATED TIME: 2014-10-24
 * COPYRIGHT: Copyright(c) 2013 ~ 2014 All Rights Reserved.
 */
package com.yunos.tvtaobao.biz.util;


import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;

import com.yunos.tv.core.common.AppDebug;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件操作工具包
 */
public class FileUtil {

    /**
     * 获取bitmap
     * @param context
     * @param fileName
     * @return
     */
    public static Bitmap getBitmap(Context context, File fileName) {
        return BitmapFactory.decodeFile(fileName.getPath());
        //        Bitmap bitmap = null;
        //        try {
        //            FileInputStream fis = new FileInputStream(file);
        //            bitmap = BitmapFactory.decodeStream(fis);
        //        } catch (FileNotFoundException e) {
        //            e.printStackTrace();
        //        } catch (OutOfMemoryError e) {
        //            e.printStackTrace();
        //        }
        //        return bitmap;
    }

    public static void saveBitmap(Bitmap bm, String filePath) {
        if (bm == null || bm.isRecycled()) {
            return;
        }

        if (TextUtils.isEmpty(filePath)) {
            filePath = Environment.getExternalStorageDirectory().getPath() + "/temp.png";
        }
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getSdCardPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        if (sdDir != null) {
            return sdDir.toString();
        }
        return null;
    }

    /**
     * 读取文件内容(UTF-8格式)
     * @param context
     * @param file
     * @return
     */
    public static String read(Context context, String file) {

        String data = "";
        try {
            FileInputStream fin = new FileInputStream(file);
            int length = fin.available();

            byte[] buffer = new byte[length];
            fin.read(buffer);

            data = new String(buffer, "UTF-8");

            fin.close();
        } catch (Exception e) {
            AppDebug.d("FileUtil", "FileUtil.read:maybe no file can read!");
        }

        return data;
    }

    public static String readFromSdcard(Context context, String file) {
        String fullFilepath = file;
        String path = getSdCardPath();
        if (!TextUtils.isEmpty(path)) {
            fullFilepath = path + "/" + file;
        }
        return read(context, fullFilepath);
    }

    /**
     * 写文件(UTF-8格式)
     * @param context
     * @param file
     * @param content
     */
    public static void write(Context context, String file, String content) {
        try {

            FileOutputStream fout = new FileOutputStream(file);
            byte[] bytes = content.getBytes("UTF-8");

            fout.write(bytes);
            fout.close();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writetoSdcard(Context context, String file, String content) {
        String fullFilepath = file;
        String path = getSdCardPath();
        if (!TextUtils.isEmpty(path)) {
            fullFilepath = path + "/" + file;
        }
        write(context, fullFilepath, content);
    }

    /**
     * 扫描文件
     * 只扫单层文件夹或文件
     * @param file 待扫描的文件
     */
    public static List<String> scan(File file) {
        List<String> items = new ArrayList<String>();
        if (file.isDirectory())//是否为文件夹
        {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                items.add(files[i].getPath());
            }
        } else {
            items.add(file.toString());
        }
        return items;
    }

    /**
     * 删除文件
     * @param file
     */
    public static boolean deleteFile(File file) {
        if (!file.exists()) { // 判断文件是否存在
            return false;
        }
        if (file.isFile()) { // 判断是否是文件
            return file.delete(); // delete()方法 你应该知道 是删除的意思;
        } else if (file.isDirectory()) { // 否则如果它是一个目录
            File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
            for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
            }
            return file.delete();
        }
        return false;
    }

    /**
     * 创建目录
     * @param dir
     */
    public static void createDir(String dir) {
        File destDir = new File(dir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
    }

    /**
     * 获取raw下文本文件的内容
     * @param context
     * @param resId
     * @return
     */
    public static String getRawTextContent(Context context, int resId) {
        String strResContet = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        try {
            inputStream = context.getResources().openRawResource(resId);
            inputStreamReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(inputStreamReader);
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            strResContet = sb.toString();
        } catch (NotFoundException e) {
            AppDebug.e("getRawTextContent", "NotFoundException rescource not found! resourceId" + resId);
            e.printStackTrace();
        } catch (IOException e) {
            AppDebug.e("getRawTextContent", "IOException rescource read error! resourceId" + resId);
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return strResContet;
    }

    public static String getApplicationPath(Context context) {
        if (context == null) {
            return null;
        }

        Context appContext = context.getApplicationContext();
        if (appContext == null) {
            return null;
        }

        File file = appContext.getFilesDir();
        if (file == null) {
            return null;
        }

        return file.getAbsolutePath();
    }

    public static boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
