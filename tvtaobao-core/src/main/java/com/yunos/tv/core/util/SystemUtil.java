package com.yunos.tv.core.util;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;

import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.RequestConfig;
import com.yunos.tv.core.config.SystemConfig;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 系统工具类
 * 
 * @author tianxiang
 * @date 2012-10-9 09:51:28
 */
public class SystemUtil {

    public static String[] week = new String[] { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };

    /**
     * 根据dip换算像素
     * 
     * @author tianxiang
     * @date 2012-10-9下午7:47:30
     */
    public static int dip2Pix(float data) {
        return Float.valueOf(SystemConfig.DENSITY * data).intValue();
    }

    /**
     * 根据像素换算dip
     * 
     * @date 2012-10-15上午11:14:40
     * @param data
     * @return
     */
    public static int pix2dip(float data) {
        return Float.valueOf(data / SystemConfig.DENSITY).intValue();
    }

    /**
     * 得到视图在屏幕上的x和y值
     * 
     * @date 2012-10-21上午10:32:24
     * @param v
     * @return 数组,0位置为x值,1位置为y值
     */
    public static int[] getLocationOnScreen(View v) {
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        return location;
    }

    /**
     * 得到视图在屏幕上的y值
     * 
     * @date 2012-10-21上午10:33:42
     * @param v
     * @return
     */
    public static int getYOnScreen(View v) {
        return getLocationOnScreen(v)[1];
    }

    /**
     * 得到视图在屏幕上的x值
     * 
     * @date 2012-10-21上午10:33:49
     * @param v
     * @return
     */
    public static int getXOnScreen(View v) {
        return getLocationOnScreen(v)[0];
    }

    /**
     * 得到视图在屏幕上的y值
     * 
     * @date 2012-10-21上午10:34:15
     * @param context
     * @param id
     *            视图id
     * @return
     */
    public static int getYOnScreen(Activity context, int id) {
        return getLocationOnScreen(context.findViewById(id))[1];
    }

    /**
     * 得到视图在上级布局的x和y值
     * 
     * @date 2012-10-21上午10:32:24
     * @param v
     * @return 数组,0位置为x值,1位置为y值
     */

    public static int[] getLocationInWindow(View v) {
        int[] location = new int[2];
        v.getLocationInWindow(location);
        return location;
    }

    /**
     * 得到视图在上级布局的x值
     * 
     * @date 2012-10-21上午10:35:11
     * @param v
     * @return
     */
    public static int getXInWindow(View v) {
        return getLocationInWindow(v)[0];
    }

    /**
     * 得到视图在上级布局的y值
     * 
     * @date 2012-10-21上午10:35:18
     * @param v
     * @return
     */
    public static int getYInWindow(View v) {
        return getLocationInWindow(v)[1];
    }

    /**
     * url编码
     * 
     * @author tianxiang
     * @date 2012-10-9 19:51:18
     */
    public static String encodeUrl(String str) {
        if (str == null) {
            return null;
        }

        try {
            str = URLEncoder.encode(str, SystemConfig.HTTP_PARAMS_ENCODING);
        } catch (UnsupportedEncodingException e1) {
            str = null;
        }

        return str;
    }

    /**
     * 根据图片名称得到完整的url
     * 
     * @date 2012-10-21上午10:36:42
     * @param imageName
     * @return
     */
    public static String mergeImageUrl(String imageName) {
        return getImageServer(imageName) + imageName;
    }

    /**
     * 根据图片名称得到图片服务器地址
     * 
     * @date 2012-10-21上午10:37:09
     * @param imageName
     * @return
     */
    public static String getImageServer(String imageName) {
        if (imageName == null) {
            AppDebug.e("SystemUtil", "getImageServer: imageName is null");
            return null;
        }
        int mod = Math.abs(imageName.hashCode()) % RequestConfig.getImageServer().length;
        return RequestConfig.getImageServer()[mod];
    }

    /**
     * bitmap转成二进制数据
     * 
     * @date 2012-10-18上午9:51:07
     * @param bitmap
     * @return
     */
    public static byte[] bitmapToBytes(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // 将Bitmap压缩成PNG编码，质量为100%存储
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        // 除了PNG还有很多常见格式，如jpeg等。
        return os.toByteArray();
    }

    public static byte[] drawableToBytes(BitmapDrawable d) {
        return bitmapToBytes(d.getBitmap());
    }

    /**
     * 二进制数据转为drawable对象
     * 
     * @date 2012-10-18上午9:52:57
     * @param bytes
     * @return
     */
    public static Drawable bytesToDrawable(byte[] bytes) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
        return new BitmapDrawable(bitmap);
    }

    /**
     * 得到系统颜色
     * 
     * @date 2012-10-19下午12:25:54
     * @param context
     * @param id
     * @return
     */
    public static int getColor(Context context, int id) {
        return context.getResources().getColor(id);
    }

    /**
     * 将s转化为int
     * 
     * @date 2012-11-12上午09:32:59
     * @param s
     * @return
     */
    public static int convertToInt(String s) {
        int i = -1;
        if (s != null && !"".equals(s)) {
            try {
                i = Integer.parseInt(s);
            } catch (Exception e) {
                e.printStackTrace();
                i = -1;
            }
        }
        return i;
    }

    /**
     * 将s转化为long
     * 
     * @date 2012-11-12上午09:32:59
     * @param s
     * @return
     */
    public static long convertToLong(String s) {
        long i = -1;
        if (s != null && !"".equals(s)) {
            try {
                i = Long.parseLong(s);
            } catch (Exception e) {
                e.printStackTrace();
                i = -1;
            }
        }
        return i;
    }

    /**
     * 将string转为date
     * 
     * @date 2012-11-12上午09:32:59
     * @param s
     * @return
     */
    public static Date convertStringToDate(String s) {
        if (s != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = sdf.parse(s);
                return date;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 将date转为String fomat为null默认yyyy-MM-dd HH:mm:ss
     * 
     * @param date
     * @param fomat
     *            如yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String convertDateToString(Date date, String fomat) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(fomat == null ? "yyyy-MM-dd HH:mm:ss" : fomat);
            return sdf.format(date);
        }
        return "";
    }

    /**
     * 提供精确的加法运算。
     * 
     * @param v1
     *            被加数
     * @param v2
     *            加数
     * @return 两个参数的和
     */
    public static double bigAdd(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return (b1.add(b2)).doubleValue();
    }

    /**
     * 提供精确的减法运算。
     * 
     * @param v1
     *            被减数
     * @param v2
     *            减数
     * @return 两个参数的差
     */
    public static double bigSub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return (b1.subtract(b2)).doubleValue();
    }
    
    /**
     * 获取url中最后的文件名
     * @param url
     * @return
     */
    public static String getUrlFileName(String url) {
        String fileName = null;
        if (!TextUtils.isEmpty(url)) {
            int pointIndex = url.lastIndexOf(".");
            int lineIndex = url.lastIndexOf("/") + 1;
            if (pointIndex < lineIndex) {
                pointIndex = url.lastIndexOf("?");
                if (pointIndex < lineIndex) {
                    pointIndex = url.length();
                }
            }
            fileName = url.substring(lineIndex, pointIndex);
        }
        return fileName;
    }

    /**
     * 检查指定apk是否已经安装
     * @param context       上下文
     * @param packageName   apk包名
     * @return
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        boolean installed =false;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed =true;
        } catch(PackageManager.NameNotFoundException e) {
            //捕捉到异常,说明未安装
            installed =false;
        }
        return installed;
    }

    /**
     * get system unique id
     *
     * @return
     */
    public static String getUuid() {
        return CloudUUIDWrapper.getCloudUUID();
    }

    /**
     * 获取系统当前运行的应用包名
     *
     * @param context
     *
     * @return
     */
    public static String getTopPackageName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return cn.getPackageName();
    }
}
