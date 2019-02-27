package com.yunos.tvtaobao.tvlive.tvtaomsg.listener;

/**
 * Created by pan on 2017/6/21.
 */

public interface NotifyListener {

    void notifyUpdate();

    void notifyPromotion(String note);

    void notifyASRConfig();

    void notifyGlobalConfig();
}
