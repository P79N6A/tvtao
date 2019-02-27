package com.yunos.tvtaobao.live.controller;

/**
 * Created by pan on 2017/3/16.
 */

public interface LiveRequestCallBack<T> {
    void success(T data);
    boolean error(String msg);
}
