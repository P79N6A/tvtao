/**
 * Copyright (C) 2015 The ALI OS Project
 * Version Date Author
 * 2015-4-4 lizhi.ywp
 */
package com.yunos.tvtaobao.flashsale.utils;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.listener.OnReminderListener;
import com.yunos.tvtaobao.flashsale.AppManager;
import com.yunos.tvtaobao.flashsale.R;
import com.yunos.tvtaobao.flashsale.bo.CategoryItem;
import com.yunos.tvtaobao.flashsale.bo.GoodsInfo;
import com.yunos.tvtaobao.flashsale.cache.MyConcernCache;
import com.yunos.tvtaobao.flashsale.view.AnimationDialog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CommUtil {


    private static String TAG = "CommUtil";
    /**
     * 判断是否是url，目前只支持一下类型： com|net|cn|gov.cn|org|name|com.cn|
     * net.cn|org.cn|info|biz|cc|tv|hk|mobi
     */
    public static final String URL_MATCHER = "((http://)|(https://)){0,1}[\\w-\\.]+\\.(com|net|cn|gov\\.cn|org|name|com\\.cn|net\\.cn|org\\.cn|info|biz|cc|tv|hk|mobi)/{0,1}.*";


    /**
     * 在url不是以http开头时，比如taobao.com需要加上http，否则浏览器识别不出加载不出内容。 url格式化
     * @author mty
     * @param s
     * @return
     */
    public static String fomatUrl(String s) {
        return s == null ? null : (s.startsWith("http://") || s.startsWith("https://")) ? s : "http://" + s;
    }

    /**
     * 是否是url
     * @author mty
     * @param s
     * @return
     */
    public static boolean isUrl(String s) {
        return s == null ? false : s.matches(URL_MATCHER);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     * @param dipValue
     * @param dipValue
     *            （DisplayMetrics类中属性density）
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     * @param context
     * @param spValue
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 拼接两个列表数据
     * @param firstList
     * @param secondList
     * @return
     */
    public static List<GoodsInfo> concatGoodsList(List<GoodsInfo> firstList, List<GoodsInfo> secondList) {
        List<GoodsInfo> goodsList = new ArrayList<GoodsInfo>();

        if (null != firstList) {
            goodsList.addAll(firstList);
        }
        if (null != secondList) {
            goodsList.addAll(secondList);
        }
        return goodsList;
    }

    /**
     * 把商品信息转化成我的关注信息
     * @param goodsInfo
     * @return
     */
    public static MyConcernCache.MyconcernInfo toMyconcernInfo(GoodsInfo goodsInfo) {
        if (null == goodsInfo) {
            return null;
        }

        MyConcernCache.MyconcernInfo myconcernInfo = new MyConcernCache.MyconcernInfo();

        myconcernInfo.mItemId = goodsInfo.getSeckillId();
        myconcernInfo.mRemindTime = DateUtils.string2Timestamp(goodsInfo.getStartTime()) - AppConfig.AHEAD_OF_TIME;
        // - goodsInfo.getRemindTime() * 1000;
        return myconcernInfo;
    }

    /**
     * 判断gridview的当前选中项是否在最左侧那列
     * @param count
     *            gridview总数
     * @param column
     *            gridview 列数
     * @param curPos
     *            当前选中的位置
     * @return
     */
    public static boolean isLastLeft(int count, int column, int curPos) {
        if (count <= 0 || column <= 0) {
            return true;
        }
        curPos %= column;
        if (curPos == 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断gridview的当前选中项是否在最右侧那列
     * @param count
     *            gridview总数
     * @param column
     *            gridview 列数
     * @param curPos
     *            当前选中的位置
     * @return
     */
    public static boolean isLastRight(int count, int column, int curPos) {
        if (count <= 0 || column <= 0 || (curPos + 1) >= count) {
            return true;
        }
        curPos %= column;
        if ((curPos + 1) == column) {
            return true;
        }

        return false;
    }

    /**
     * 判断gridview的当前选中项是否在最上面那一行
     * @param count
     *            gridview总数
     * @param column
     *            gridview 列数
     * @param curPos
     *            当前选中的位置
     * @return
     */
    public static boolean isVisibleTopMask(int count, int column, int pageMaxRow, int curPos) {
        if (count <= 0 || column <= 0) {
            return false;
        }

        int rowsCount = (count + 1) / column;
        // 如果总行数小于每页可见的最大行数
        if (rowsCount < pageMaxRow) {
            return false;
        }
        // 当选中第一行时，则隐藏蒙版，否则其他行则显示蒙版
        int currentRow = curPos / column;

        return currentRow > 0;
    }

    /**
     * 判断gridview的当前选中项是否在最低那一行
     * @param count
     *            gridview总数
     * @param column
     *            gridview 列数
     * @param pageMaxRow
     *            每一页最多能显示多少行
     * @param curPos
     *            当前选中的位置
     * @return
     */
    public static boolean isVisibleBottomMask(int count, int column, int pageMaxRow, int curPos) {
        if (count <= 0 || column <= 0) {
            return false;
        }

        int rowsCount = (count + 1) / column;
        // 如果总行数小于每页可见的最大行数
        if (rowsCount < AppConfig.ONE_PAGE_ROWS_GRIDVIEW) {
            return false;
        }
        // 当选中第一行时，则隐藏蒙版，否则其他行则显示蒙版
        int currentRow = curPos / column;

        return currentRow == rowsCount;
    }


    /**
     * 打开提示信息，一个提醒到达
     */
    public static void openDetail(Context context, String itemId, long remindingTime) {

        if (AppConfig.DEBUG) {
            AppDebug.i(TAG,"openGoodsDetail ****, remindingTime = " + remindingTime);
        }
        long realTime = remindingTime + AppConfig.AHEAD_OF_TIME;
        Map<String, String> prop = Utils.getProperties();
        prop.put("time", DateUtils.timestamp2String(realTime));
        Utils.utCustomHit(Utils.utGetCurrentPage(), TbsUtil.CUSTOM_PopUp, prop);

        //		Intent intent = new Intent();
        //		intent.putExtra("remind_info_head",
        //				context.getResources().getString(R.string.str_remind_info_head));
        //		intent.putExtra("remind", realTime);

        String remindTimeStr = DateUtils.timestamp2String(realTime, "HH:mm");
        AppDebug.d(TAG,"remindTimeStr = " + remindTimeStr);

        //		intent.putExtra(
        //				"remind_info_time",
        //				remindTimeStr
        //						+ context.getResources().getString(
        //								R.string.str_remind_info_time));
        //		intent.putExtra("remind_info_tip",
        //				context.getResources().getString(R.string.str_remind_info_tip));
        //		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //		intent.setClass(context, AnimationActivity.class);
        //		context.startActivity(intent);

        AnimationDialog animationDialog = new AnimationDialog(context);
        animationDialog.dialogShow("", "", "",
                remindTimeStr + context.getResources().getString(R.string.str_remind_info_time), context.getResources()
                        .getString(R.string.str_remind_info_tip), "", realTime);

    }


    public static byte getStatusType(String status) {
        if (TextUtils.equals(CategoryItem.STATUS_CURRENT, status)) {
            return CategoryItem.STATUS_TYPE_CURRENT;
        } else if (TextUtils.equals(CategoryItem.STATUS_FUTRUE, status)) {
            return CategoryItem.STATUS_TYPE_FUTRUE;
        }
        return CategoryItem.STATUS_TYPE_PAST;
    }

    public static Object getAppManager(Context context) {
        Class<?> appManager = null;
        try {
            appManager = Class.forName("com.yunos.flashsale.AppManager");
            Method getInstance = appManager.getMethod("getInstance", new Class[] { android.content.Context.class });
            return getInstance.invoke(appManager, context);

        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

        }
        return null;
    }

    public static OnReminderListener getOnReminderListener(Context context) {
        Class<?> appManager = null;
        try {

            appManager = Class.forName("com.yunos.flashsale.AppManager");
            Method getInstance = appManager.getMethod("getInstance", new Class[] { android.content.Context.class });
            Method getOnReminderListener = appManager.getMethod("getOnReminderListener");
            Object ret = getInstance.invoke(appManager, context);
            return (OnReminderListener) getOnReminderListener.invoke(ret);

        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

        }
        return null;
    }

    /**
     * 时间转为毫秒数
     * @param dateStr
     *            yyyyMMddHHmmss
     * @return
     */
    final public static long string2Timestamp(String dateStr) {
        return string2Timestamp(dateStr, "yyyyMMddHHmmss");
    }

    final public static long string2Timestamp(String dateStr, String format) {
        if (TextUtils.isEmpty(dateStr)) {
            return 0;
        }
        long temp = 0;
        try {
            Date date = null;
            date = new SimpleDateFormat(format).parse(dateStr);
            if (date != null) {
                temp = date.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public static void enterDetail(GoodsInfo info, Context context) {
        boolean needToast = true;

        if (null != info) {
            int type = info.getType();
            String itemId = info.getItemId();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(CoreIntentKey.URI_FROM_APP_BUNDLE, AppManager.getAppName());
            intent.putExtra(CoreIntentKey.URI_FROM, AppManager.getAppName());
            intent.putExtra("frominner", true);
            StringBuilder sb = new StringBuilder(100);

            if (type == GoodsInfo.ITEM_TYPE_SECKILL_INFO) {
                sb.append("tvtaobao://home?app=seckill&module=detail");
                sb.append("&itemId=");
                sb.append(itemId);

                //        sb.append("&stock=");
                //        int count = info.getRemainingNum();
                //        if( !info.isFuture() ){
                //          count += info.getSoldNum();
                //        }
                //        sb.append(count);
            } else {
                sb.append("tvtaobao://home?app=taobaosdk&module=detail");

                byte status = getDetailStatus(info, context);
                if (status == 1 && !TextUtils.isEmpty(info.getEndTime())) {
                    sb.append("&time=");
                    sb.append(info.getEndTime());
                } else if (!TextUtils.isEmpty(info.getStartTime())) {
                    sb.append("&time=");
                    sb.append(info.getStartTime());
                }
                if (!TextUtils.isEmpty(info.getSeckillId())) {
                    sb.append("&status=");
                    sb.append(status);
                    sb.append("&qianggouId=");
                    sb.append(info.getSeckillId());
                }
                if (info.getSalePrice() > 0) {
                    sb.append("&price=");
                    sb.append(info.getSalePrice());
                }
                sb.append("&itemId=");
                sb.append(itemId);
            }

            try {
                // /**将goodsInfo数据也传递过去*/
                // Bundle bundle = new Bundle();
                // bundle.putSerializable("goodsInfo", info);
                // intent.putExtras(bundle);
                String url = sb.toString();
                if (AppConfig.DEBUG) {
                    // LogUtil.i("enterDetail", "url = " + url);
                }
                intent.setData(Uri.parse(url));
                intent.putExtra("extParams", "{\"umpChannel\":\"qianggou\",\"u_channel\":\"qianggou\"}");
                context.startActivity(intent);
                needToast = false;
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }

        }

        if (needToast) {
            String name = context.getResources().getString(R.string.ytbv_not_open);
            Toast.makeText(context, name, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 从格式会2015042710270000格式中获取小时、分钟和秒数 返回10:27
     * @param format
     *            时间字符串
     * @return
     */
    public static String getTime(String format) {
        int size = null != format ? format.length() : 0;
        if (size > 6) {
            return format.substring(size - 6, size - 4) + ":" + format.substring(size - 4, size - 2);
        }
        return "";
    }

    /**
     * 传递给详情页面的状态信息
     * @param info
     *            商品信息
     * @param context
     *            上下文
     * @return 0：过去 1：现在 2：将来
     */
    public static byte getDetailStatus(GoodsInfo info, Context context) {
        if (null != info && null != context) {
            if (info.isFuture()) {
                return 2;
            } else {
                long endTime = CommUtil.string2Timestamp(info.getEndTime());
                long curTime = AppManager.getInstance(context).getTimerManager().getCurTime();

                return (byte) ((curTime >= endTime) ? 0 : 1);
            }
        }
        return (byte) 0;
    }

}
