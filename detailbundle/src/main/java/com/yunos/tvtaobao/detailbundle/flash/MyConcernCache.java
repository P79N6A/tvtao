/**
 * Copyright (C) 2015 The ALI OS Project
 * <p>
 * Version     Date            Author
 * <p>
 * 2015-4-19       lizhi.ywp
 */
package com.yunos.tvtaobao.detailbundle.flash;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.text.TextUtils;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.listener.OnReminderListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class MyConcernCache {
    private final static String TAG = "MyConcernCache";
    public final static int MAX_REMIND_COUNT = 40;

    final private List<MyconcernInfo> mMyconcerList = new ArrayList<MyconcernInfo>();
    final private HashMap<String, MyconcernInfo> mMyconcernMap = new HashMap<String, MyconcernInfo>();
    final private Object mSyn = new Object();
    /** 关注数据库相关 */
    private byte mQueryState = DatabaseListener.QUERY_UNDO;
    private ContentResolver mResolver;
    private ExecutorService mExecutorService;
    private Context mContext;
    private Handler mMainHandler;


    public static class MyconcernInfo {
        /**抢购id*/
        public String mItemId;
        public long mRemindTime;

        // public long mStart_time;
        public MyconcernInfo() {
        }

        public MyconcernInfo(String itemId, long remindTime) {
            mItemId = itemId;
            mRemindTime = remindTime;
        }
    }

    public MyConcernCache(AppManager manager) {
        mExecutorService = manager.getExecutorService();
        mContext = manager.getContext();
        mMainHandler = manager.getMainHanlder();
        mResolver = mContext.getContentResolver();
    }

    public boolean queryCacheById(byte type, String itemId) {
        synchronized (mSyn) {
            return mMyconcernMap.containsKey(itemId);
        }
    }

    public byte getReminderState() {
        int size = mMyconcerList.size();
        if (size > MAX_REMIND_COUNT - 1) {
            return OnReminderListener.RET_FULL;
        }
        return OnReminderListener.RET_SUCCESS;
    }

    public boolean hasReminder(String itemId) {
        if (TextUtils.isEmpty(itemId)) {
            return false;
        }
        synchronized (mSyn) {
            return mMyconcernMap.containsKey(itemId);
        }
    }

    private byte addToMyconcernCache(MyconcernInfo myconcernInfo) {
        if (null == myconcernInfo) {
            return OnReminderListener.RET_ERROR;
        }
        String key = myconcernInfo.mItemId;

        synchronized (mSyn) {
            int size = mMyconcerList.size();
            if (size > MAX_REMIND_COUNT - 1) {
                return OnReminderListener.RET_FULL;
            }
            if (mMyconcernMap.containsKey(key)) {
                AppDebug.i(TAG, "has existed : " + myconcernInfo.mItemId);
                return OnReminderListener.RET_EXIST;
            }
            addMyconcerList(myconcernInfo);
            mMyconcernMap.put(key, myconcernInfo);
            AppDebug.d(TAG, "myconcernInfo.itemid = " + myconcernInfo.mItemId + "myconcernInfo.remindTime = "
                    + myconcernInfo.mRemindTime);
            return OnReminderListener.RET_SUCCESS;
        }
    }

    public byte insert(MyconcernInfo myconcernInfo) {
        byte ret = addToMyconcernCache(myconcernInfo);

        if (ret == OnReminderListener.RET_SUCCESS) {
            inserDb(myconcernInfo);
        }
        return ret;
    }

    public boolean removeFromMyconcernCache(String itemId) {
        if (TextUtils.isEmpty(itemId)) {
            return false;
        }
        synchronized (mSyn) {
            MyconcernInfo myconcernInfo = mMyconcernMap.remove(itemId);
            if (null != myconcernInfo) {
                mMyconcerList.remove(myconcernInfo);
                return true;
            }
        }
        return false;
    }

    public boolean remove(String itemId) {
        if (removeFromMyconcernCache(itemId)) {
            removeDb(itemId);
            return true;
        }
        return false;
    }

    public void resetMyconcernCache(List<MyconcernInfo> cache) {
        synchronized (mSyn) {
            mMyconcernMap.clear();
            mMyconcerList.clear();
            if (null != cache) {
                String key;
                for (MyconcernInfo info : cache) {
                    key = info.mItemId;
                    if (!TextUtils.isEmpty(key) && !mMyconcernMap.containsKey(key)) {
                        addMyconcerList(info);
                        mMyconcernMap.put(key, info);
                    }
                }
            }
        }
    }

    private void addMyconcerList(MyconcernInfo myconcernInfo) {
        int size = mMyconcerList.size();
        MyconcernInfo info;
        long insetRemindTime = myconcernInfo.mRemindTime;

        /** 降序排列 */
        for (int i = 0; i < size; i++) {
            info = mMyconcerList.get(i);
            if (insetRemindTime > info.mRemindTime) {
                /**不需要等号，使得相同时间的商品添加在前面，在倒计时的时候就不需要重复设置提醒时间*/
                mMyconcerList.add(i, myconcernInfo);
                return;
            }
        }
        mMyconcerList.add(myconcernInfo);
    }

    /**
     * 删除过时的关注
     *
     * @param zeroTime
     */
    private boolean delOutTimeConcern(long zeroTime) {
        boolean delete = false;

        synchronized (mSyn) {
            int index = 0;
            int size = mMyconcerList.size();
            MyconcernInfo info;

            /** 为降序排列 */
            for (index = size - 1; index >= 0; index--) {
                info = mMyconcerList.get(index);

                if (zeroTime < info.mRemindTime) {
                    break;
                }
                mMyconcernMap.remove(info.mItemId);
                mMyconcerList.remove(index);
                delete = true;
            }
        }
        return delete;
    }

    /**
     * 获取关注的列表信息
     *
     * @param list
     *            保存的结果
     */
    public List<MyconcernInfo> getMyconcerList(List<MyconcernInfo> list) {
        synchronized (mSyn) {
            if (null != list) {
                list.clear();
            } else {
                list = new ArrayList<MyconcernInfo>();
            }
            int size = mMyconcerList.size();
            for (int index = size - 1; index >= 0; index--) {
                list.add(mMyconcerList.get(index));
            }
        }
        return list;
    }

    /**
     * 获取下个闹钟的时间
     *
     * @param currentTime
     *            保存的结果
     */
    public MyconcernInfo getNextAlarm(long currentTime) {
        /** 检测当前数据 */
        synchronized (mSyn) {
            int size = mMyconcerList.size();

            for (int index = size - 1; index >= 0; index--) {
                MyconcernInfo myconcernInfo = mMyconcerList.get(index);
                if (currentTime < myconcernInfo.mRemindTime) {
//                    AppDebug.i(TAG,
//                            "getNextAlarm: "
//                                    + DateUtils.timestamp2String(myconcernInfo.mRemindTime)
//                                    + " item id: " + myconcernInfo.mItemId);
                    return myconcernInfo;
                }
            }
        }
        return null;
    }

    public int size() {
        synchronized (mSyn) {
            return mMyconcerList.size();
        }
    }

    public void autoClear() {
        /** cache data */
        final long zeroTime = getTimeAtZero(mContext);
        if (delOutTimeConcern(zeroTime)) {
            mExecutorService.submit(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    /** 清除过时的数据 */
                    mResolver.delete(MyConcernProvider.CONTENT_URI,
                            MyConcernSQLite.REMIND_TIME + " < " + zeroTime,
                            null);
                }
            });
        }
    }

    public static long getTimeAtZero(Context context) {
        long curTime = AppManager.getInstance(context).getTimerManager().getCurTime();
        Date date = new Date(curTime);
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);
        return date.getTime();
    }
    private synchronized List<MyconcernInfo> loadData() {
        /** 读取当前数据, 需要降序查找 */
        Cursor cursor = mResolver.query(MyConcernProvider.CONTENT_URI, null,
                null, null, null);
        List<MyconcernInfo> myconcernList = new ArrayList<MyconcernInfo>();
        if (null != cursor) {
            try {
                if (cursor.moveToFirst()) {
                    AppDebug.d(TAG,"cursor.size = " + cursor.getCount());
                    do {
                        MyconcernInfo info = new MyconcernInfo();
                        info.mItemId = cursor.getString(cursor
                                .getColumnIndex(MyConcernSQLite.ITEM_ID));
                        info.mRemindTime = cursor.getLong(cursor
                                .getColumnIndex(MyConcernSQLite.REMIND_TIME));
                        myconcernList.add(info);
                    } while (cursor.moveToNext());
                }
            } finally {
                if (null != cursor) {
                    cursor.close();
                }
            }

        }

        return myconcernList;
    }

    private void inserDb(final MyconcernInfo myconcernInfo) {
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                final ContentValues values = new ContentValues();
                values.put(MyConcernSQLite.ITEM_ID, myconcernInfo.mItemId);
                values.put(MyConcernSQLite.REMIND_TIME,
                        myconcernInfo.mRemindTime);
                mResolver.insert(MyConcernProvider.CONTENT_URI, values);
            }
        });
    }

    private void removeDb(final String itemId) {
        if (TextUtils.isEmpty(itemId)) {
            return;
        }
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                AppDebug.d(TAG, "del sql = " + MyConcernSQLite.ITEM_ID + " = '"
                        + itemId + "'");
                mResolver.delete(MyConcernProvider.CONTENT_URI,
                        MyConcernSQLite.ITEM_ID + " = '" + itemId + "'", null);
            }
        });
    }

    public void checkLoadingData() {
        boolean needLoading = false;

        synchronized (mSyn) {
            if (mQueryState == DatabaseListener.QUERY_UNDO) {
                needLoading = true;
            }
        }
        if (needLoading) {
            queryMyconcernList(null);
        }
    }

    public void synQueryMyconcernList() {
        boolean needLoading = false;

        synchronized (mSyn) {
            if (mQueryState == DatabaseListener.QUERY_UNDO) {
                mQueryState = DatabaseListener.QUERY_DOING;
                needLoading = true;
            }
        }
        if (needLoading) {
            List<MyconcernInfo> cache = loadData();
            /** 更新数据 */
            resetMyconcernCache(cache);
            synchronized (mSyn) {
                mQueryState = DatabaseListener.QUERY_DONE;
            }
        } else {
            /** 10秒 */
            long delayTimeout = 10000;
            while (mQueryState == DatabaseListener.QUERY_DOING) {
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                delayTimeout -= 30;
                if (delayTimeout < 0) {
                    break;
                }
            }
        }
    }

    /**
     * 查询数据中的我的关注列表
     *
     * @param databaseListener
     */
    public void queryMyconcernList(final DatabaseListener databaseListener) {
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                synQueryMyconcernList();
                if (null != databaseListener) {
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            databaseListener.onQueryDone(mQueryState);
                        }
                    });
                }
            }
        });
    }

    public byte getQueryState() {
        return mQueryState;
    }
}
