package com.yunos.tvtaobao.juhuasuan.util;


import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.yunos.tvtaobao.juhuasuan.config.SystemConfig;
import com.yunos.tvtaobao.juhuasuan.request.config.RequestConfig;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.core.ServerTimeSynchronizer;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 系统工具类
 * @author tianxiang
 * @date 2012-10-9 09:51:28
 */
public class SystemUtil {

    public static String[] week = new String[] { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
    //屏幕宽度倍率，如在相同densityDpi的情况下，1080p是720p的1.5倍
    private static Double windowWidthMaginfication;

    /**
     * 获取相对于默认屏幕宽度的放大倍数，默认720p，1080就是放大1.5倍
     * @return
     */
    public static double getMagnification() {
        if (null == windowWidthMaginfication) {
            initMagnification();
        }
        AppDebug.i("SystemUtil", "SystemUtil.getMagnification windowWidthMaginfication=" + windowWidthMaginfication);
        return windowWidthMaginfication;
    }

    public static void initMagnification() {
        if (SystemConfig.SCREEN_HEIGHT!=null&&SystemConfig.SCREEN_HEIGHT > 0) {
            windowWidthMaginfication = SystemConfig.SCREEN_HEIGHT / 720.0;
        } else {
            windowWidthMaginfication = 1.0;
        }
        AppDebug.i("SystemUtil", "SystemUtil.initMagnification SCREEN_HEIGHT=" + SystemConfig.SCREEN_HEIGHT
                + ", DENSITY_DPI=" + SystemConfig.DENSITY_DPI + ", windowWidthMaginfication="
                + windowWidthMaginfication + ", DENSITY=" + SystemConfig.DENSITY);
    }

    /**
     * 获取某android版本号
     * @return
     */
    public static int getAndroidSDKVersion() {
        int version = -1;
        try {
            version = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
        }
        return version;
    }

    /**
     * 获取某应用版本号
     * @param context
     * @param packageName
     *            应用包名
     * @param flags
     * @return
     */
    public static int getAppVersionCode(Context context, String packageName, int flags) {
        PackageManager packageManager = context.getPackageManager();

        Integer versionCode = -1;
        try {
            //0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(packageName, flags);
            versionCode = packInfo.versionCode;
        } catch (NameNotFoundException e) {
            AppDebug.i("SystemUtil", "SystemUtil.getAppVersionCode packageName is not exists!");
        }
        return versionCode;
    }

    /**
     * 检测是否已经安装了某应用
     * @param context
     * @param packageName
     * @return
     */
    public static boolean checkPackage(Context context, String packageName) {
        if (packageName == null || "".equals(packageName)) {
            return false;
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            packageManager.getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 根据dip换算像素
     * @author tianxiang
     * @date 2012-10-9下午7:47:30
     */
    public static int dip2Pix(float data) {
        return Float.valueOf(SystemConfig.DENSITY * data).intValue();
    }

    /**
     * 根据像素换算dip
     * @date 2012-10-15上午11:14:40
     * @param data
     * @return
     */
    public static int pix2dip(float data) {
        return Float.valueOf(data / SystemConfig.DENSITY).intValue();
    }

    /**
     * 得到视图在屏幕上的x和y值
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
     * @date 2012-10-21上午10:33:42
     * @param v
     * @return
     */
    public static int getYOnScreen(View v) {
        return getLocationOnScreen(v)[1];
    }

    /**
     * 得到视图在屏幕上的x值
     * @date 2012-10-21上午10:33:49
     * @param v
     * @return
     */
    public static int getXOnScreen(View v) {
        return getLocationOnScreen(v)[0];
    }

    /**
     * 得到视图在屏幕上的y值
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
     * @date 2012-10-21上午10:35:11
     * @param v
     * @return
     */
    public static int getXInWindow(View v) {
        return getLocationInWindow(v)[0];
    }

    /**
     * 得到视图在上级布局的y值
     * @date 2012-10-21上午10:35:18
     * @param v
     * @return
     */
    public static int getYInWindow(View v) {
        return getLocationInWindow(v)[1];
    }

    /**
     * url编码
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
     * @date 2012-10-21上午10:36:42
     * @param imageName
     * @return
     */
    public static String mergeImageUrl(String imageName) {
        return getImageServer(imageName) + imageName;
    }

    /**
     * 根据图片名称得到图片服务器地址
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

    //	/**
    //	 * 对字符串集合按拼音排序
    //	 * 
    //	 * @date 2012-10-21上午10:37:58
    //	 * @param list
    //	 * @return 按字母分类的map
    //	 */
    //	public static Map<String, List<String>> sortByPinyin(List<String> list) {
    //
    //		Collections.sort(list, new Comparator<String>() {
    //
    //			@Override
    //			public int compare(String str1, String str2) {
    //				char c1 = str1.charAt(0);
    //				char c2 = str2.charAt(0);
    //				return concatPinyinStringArray(
    //						PinyinHelper.toHanyuPinyinStringArray(c1)).compareTo(
    //						concatPinyinStringArray(PinyinHelper
    //								.toHanyuPinyinStringArray(c2)));
    //			}
    //
    //			private String concatPinyinStringArray(String[] pinyinArray) {
    //				StringBuffer pinyinSbf = new StringBuffer();
    //				if ((pinyinArray != null) && (pinyinArray.length > 0)) {
    //					for (int i = 0; i < pinyinArray.length; i++) {
    //						pinyinSbf.append(pinyinArray[i]);
    //					}
    //				}
    //				return pinyinSbf.toString();
    //			}
    //
    //		});
    //
    //		Map<String, List<String>> map = new LinkedHashMap<String, List<String>>();
    //		for (String str : list) {
    //			char head = (PinyinHelper.toHanyuPinyinStringArray(str.charAt(0))[0])
    //					.charAt(0);
    //			List<String> tempList = map.get(String.valueOf(head));
    //			if (tempList == null) {
    //				tempList = new ArrayList<String>();
    //				map.put(String.valueOf(head), tempList);
    //			}
    //			tempList.add(str);
    //		}
    //
    //		return map;
    //	}

    // private static View DATA_LOADING_TIP;
    // private static View NET_ERROR_TIP;
    // private static View NO_DATA_TIP;
    //
    // public static View getDataLoadingTip(Context context) {
    // if (DATA_LOADING_TIP == null) {
    // LayoutInflater inflater = LayoutInflater.from(context);
    // DATA_LOADING_TIP = inflater
    // .inflate(R.layout.data_loading_tip, null);
    // }
    //
    // return DATA_LOADING_TIP;
    // }
    //
    // public static View getNetErrorTip(Context context) {
    // if (NET_ERROR_TIP == null) {
    // LayoutInflater inflater = LayoutInflater.from(context);
    // NET_ERROR_TIP = inflater.inflate(R.layout.net_error_tip, null);
    // }
    //
    // return NET_ERROR_TIP;
    // }
    //
    // public static View getNoDataTip(Context context) {
    // if (NO_DATA_TIP == null) {
    // LayoutInflater inflater = LayoutInflater.from(context);
    // NO_DATA_TIP = inflater.inflate(R.layout.no_data_tip, null);
    // }
    //
    // return NO_DATA_TIP;
    // }

    /**
     * bitmap转成二进制数据
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
     * @date 2012-10-19下午12:25:54
     * @param context
     * @param id
     * @return
     */
    public static int getColor(Context context, int id) {
        return context.getResources().getColor(id);
    }

    public static Date getJoinGroupExpireDate() {
        /*
         * Calendar c = Calendar.getInstance();
         * c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) + 30);
         * return c.getTime();
         */
        //返回服务器时间
        return new Date(ServerTimeSynchronizer.getCurrentTime() + 30 * 60 * 1000);
    }

    //	public static Intent jumpToWebViwe(Context from, Integer flag,
    //			String content) {
    //		Intent intent = new Intent(from, WebViewActivity.class);
    //		Bundle bundle = new Bundle();
    //		bundle.putInt("flag", flag);
    //		bundle.putString("content", content);
    //		intent.putExtras(bundle);
    //
    //		return intent;
    //	}

    /**
     * 将s转化为int
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
     * // * 产生一条分割线
     * // *
     * // * @date 2012-11-18下午2:51:51
     * // * @param context
     * // * @return
     * //
     */
    //	public static View getSplitLine(Context context) {
    //		View splitLine = new View(context);
    //		splitLine.setLayoutParams(new ViewGroup.LayoutParams(
    //				ViewGroup.LayoutParams.MATCH_PARENT, SystemUtil.dip2Pix(1)));
    //		splitLine.setBackgroundColor(SystemUtil.getColor(context,
    //				R.color.CommonStroke));
    //		return splitLine;
    //	}

    /**
     * 将date转为String fomat为null默认yyyy-MM-dd HH:mm:ss
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
}
