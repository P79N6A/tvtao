package com.yunos.tv.core.common;


public class FrequentLock {

    private final int DELAY_TIME = 1000;
    private boolean haveLock = false;
    private long lastTimeLock = 0L;

    public boolean isLock() {
        long current = System.currentTimeMillis();
        if (haveLock && lastTimeLock > 0L) {
            if (current - lastTimeLock > DELAY_TIME) {
                // 如果是超过DELAY_TIME这个时间，那么废弃这个标志，认为没有上锁
                clearLoack();
            }
        }
        if (haveLock) {
            lastTimeLock = System.currentTimeMillis();
            return true;
        }

        haveLock = true;
        lastTimeLock = System.currentTimeMillis();
        return false;
    }

    public void clearLoack() {
        haveLock = false;
        lastTimeLock = 0L;
    }

}
