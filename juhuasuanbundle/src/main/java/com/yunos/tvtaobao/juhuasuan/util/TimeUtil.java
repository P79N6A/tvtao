package com.yunos.tvtaobao.juhuasuan.util;


import com.yunos.tv.core.common.AppDebug;

public class TimeUtil {

    /**
     * 时间规则，比较服务器时间，判断是取服务器时间还是取客户端时间
     * @param serverCurrentTime
     * @return
     */
    public static long getCurrentTime(long serverCurrentTime) {
        long clientCurrentTime = System.currentTimeMillis();
        AppDebug.i("TimeUtil", "TimeUtil.getCurrentTime serverCurrentTime=" + serverCurrentTime
                + ", clientCurrentTime=" + clientCurrentTime);
        if (serverCurrentTime > clientCurrentTime || Math.abs(serverCurrentTime - clientCurrentTime) > 10) {
            AppDebug.i("TimeUtil", "TimeUtil.getCurrentTime serverCurrentTime=" + serverCurrentTime);
            return serverCurrentTime;
        }

        AppDebug.i("TimeUtil", "TimeUtil.getCurrentTime clientCurrentTime=" + clientCurrentTime);
        return clientCurrentTime;
    }
}
