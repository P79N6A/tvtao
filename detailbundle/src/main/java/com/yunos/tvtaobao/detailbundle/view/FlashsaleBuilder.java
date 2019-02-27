package com.yunos.tvtaobao.detailbundle.view;


import android.app.AppOpsManager;
import android.content.Context;
import android.os.CountDownTimer;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.listener.OnReminderListener;
import com.yunos.tvtaobao.detailbundle.flash.AppManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class FlashsaleBuilder {

    private static String TAG = "FlashsaleBuilder";

    // 时间的格式
    private static String PATTERN = "yyyyMMddHHmmss";
    // 小时
    private static long HH = 3600000;
    // 分
    private static long MM = 60000;
    // 秒
    private static long SS = 1000;

    // 定时间隔
    private final long TIMER_DELAY = 1000;

    // 定时器
    private FlashsaleCountDownTimer mFlashsaleCountDownTimer;

    // 获取提醒操作的监听类
    private OnReminderListener mOnReminderListener;

    // 抢购ID
    private String itemQianGouId;

    // 提醒时间
    private String reminderTime;

    private Context mContext;

    public FlashsaleBuilder(Context context, long startmillisTime) {

        mContext = context;
        if (startmillisTime < 1) {
            // startTime 如果是空的，那么直接退出
            return;
        }

        mOnReminderListener = getOnReminderListener(context);
        AppDebug.i(TAG, "FlashsaleBuilder --> mOnReminderListener = " + mOnReminderListener);


        long millisInFuture = 0;
        long currenttmillisTime = getServerCurrentTime();

        //距离开始的倒计时
        if (currenttmillisTime < startmillisTime) {
            millisInFuture = startmillisTime - currenttmillisTime;
        }

        if (millisInFuture > 0) {
            mFlashsaleCountDownTimer = new FlashsaleCountDownTimer(millisInFuture, TIMER_DELAY);
        }
        AppDebug.i(TAG, "FlashsaleBuilder --> millisInFuture = " + millisInFuture + "; mFlashsaleCountDownTimer = "
                + mFlashsaleCountDownTimer);
    }

    /**
     * 通过反射，获取AppManager中的OnReminderListener
     *
     * @param context
     * @return
     */
    private OnReminderListener getOnReminderListener(Context context) {

        Class<?> appManager = null;
        try {

            appManager = Class.forName("com.yunos.tvtaobao.detailbundle.flash.AppManager");
            Method getInstance = appManager.getMethod("getInstance", new Class[]{android.content.Context.class});
            Method getOnReminderListener = appManager.getMethod("getOnReminderListener");
            Object ret = getInstance.invoke(appManager, context);
            return (OnReminderListener) getOnReminderListener.invoke(ret);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {

            AppManager manager = AppManager.getInstance(context);
            OnReminderListener onReminderListener = manager.getOnReminderListener();

            if (onReminderListener != null) {
                return onReminderListener;
            }
        }
//        Class<?> appManager = null;
//        try {
//
//            appManager = Class.forName("com.yunos.tvtaobao.detailbundle.flash.AppManager");
//            Method getInstance = appManager.getMethod("getInstance", new Class[] { android.content.Context.class });
//            Method getOnReminderListener = appManager.getMethod("getOnReminderListener");
//            Object ret = getInstance.invoke(appManager, context);
//            return (OnReminderListener) getOnReminderListener.invoke(ret);
//
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } finally {
//
//        }
        return null;
    }

    /**
     * 开启定时器
     */
    public void startTimer() {
        if (mFlashsaleCountDownTimer != null) {
            mFlashsaleCountDownTimer.start();
        }
    }

    /**
     * 销毁
     */
    public void onDestroy() {
        if (mFlashsaleCountDownTimer != null) {
            mFlashsaleCountDownTimer.cancel();
        }
    }

    public OnReminderListener getReminderListener() {
        if (mOnReminderListener == null) {
            mOnReminderListener = getOnReminderListener(mContext);
        }
        AppDebug.i(TAG, "FlashsaleBuilder --> getOnReminderListener = " + mOnReminderListener);
        return mOnReminderListener;
    }

    /**
     * 设置抢购的信息
     *
     * @param itemQianGouId
     * @param time
     */
    public void setFlashsaleInfo(String itemQianGouId, String time) {
        this.itemQianGouId = itemQianGouId;
        reminderTime = time;
    }

    /**
     * 设置提醒
     */
    public boolean addReminder() {
        byte result = OnReminderListener.RET_ERROR;
        mOnReminderListener = getReminderListener();
        if (mOnReminderListener != null) {
            result = mOnReminderListener.addReminder(itemQianGouId, reminderTime);
        }
        if (result == OnReminderListener.RET_SUCCESS) {
            return true;
        }
        return false;
    }

    /**
     * 取消提醒
     *
     * @param
     */
    public void removeReminder() {
        mOnReminderListener = getReminderListener();
        if (mOnReminderListener != null) {
            mOnReminderListener.removeReminder(itemQianGouId);
        }
    }

    /**
     * 是否设置了提醒
     *
     * @param
     */
    public boolean hasReminder() {
        mOnReminderListener = getReminderListener();
        if (mOnReminderListener != null) {
            return mOnReminderListener.hasReminder(itemQianGouId);
        }
        return false;
    }

    /**
     * 获取服务器的当前时间
     */
    public long getServerCurrentTime() {
        mOnReminderListener = getReminderListener();
        if (mOnReminderListener != null) {
            long serverCurrentTime = mOnReminderListener.getServerCurrentTime();
            AppDebug.i(TAG, "getServerCurrentTime --> serverCurrentTime = " + serverCurrentTime);
            if (serverCurrentTime < 0) {
                serverCurrentTime = System.currentTimeMillis();
            }
            return serverCurrentTime;
        }
        return System.currentTimeMillis();
    }

    /**
     * 设置定时监听
     *
     * @param l
     */
    public void setOnCountDownTimerListen(onCountDownTimerListen l) {
        if (mFlashsaleCountDownTimer != null) {
            mFlashsaleCountDownTimer.setListen(l);
        }
    }

    private static class FlashsaleCountDownTimer extends CountDownTimer {

        private onCountDownTimerListen monCountDownTimerListen;

        public FlashsaleCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (monCountDownTimerListen != null) {
                long millis = millisUntilFinished;
                long h = millis / HH;
                millis %= HH;
                long m = millis / MM;
                millis %= MM;
                long s = millis / SS;
                String h_str = h + "";
                if (h < 10) {
                    h_str = "0" + h;
                }
                String m_str = m + "";
                if (m < 10) {
                    m_str = "0" + m;
                }
                String s_str = s + "";
                if (s < 10) {
                    s_str = "0" + s;
                }

                List<String> timeList = new ArrayList<String>();
                timeList.add(h_str.substring(0, 1));
                timeList.add(h_str.substring(1));
                timeList.add(m_str.substring(0, 1));
                timeList.add(m_str.substring(1));
                timeList.add(s_str.substring(0, 1));
                timeList.add(s_str.substring(1));
                monCountDownTimerListen.onTick(timeList, millisUntilFinished);
            }
        }

        @Override
        public void onFinish() {
            if (monCountDownTimerListen != null) {
                monCountDownTimerListen.onFinish();
            }
        }

        public void setListen(onCountDownTimerListen l) {
            monCountDownTimerListen = l;
        }
    }

    /**
     * 定时器监听类
     */
    public interface onCountDownTimerListen {

        public boolean onTick(List<String> timeList, long millisUntilFinished);

        public boolean onFinish();
    }
}
