package com.yunos.tvtaobao.takeoutbundle.listener;



/**
 * Created by chenjiajuan on 17/12/22.
 *
 * @describe  浮层返回监听
 */

public interface OnDialogReturnListener {
    void addBagSuccess();
    void addBagFailure(int resultCode, String msg);
}
