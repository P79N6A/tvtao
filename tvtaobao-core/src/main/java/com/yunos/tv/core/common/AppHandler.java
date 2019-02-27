package com.yunos.tv.core.common;


import android.os.Handler;

import java.lang.ref.WeakReference;

/**
 * 让handler和Activity解除引用
 */
public class AppHandler<T> extends Handler {

    private final WeakReference<T> mT; //WeakReference是弱引用，不会影响mT对象的回收

    public AppHandler(T t) {
        mT = new WeakReference<T>(t);
    }



    public T getT() {
        return mT.get();
    }
}
