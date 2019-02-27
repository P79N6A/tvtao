package com.yunos.tvtaobao.juhuasuan.pingpaituan;

import android.view.ViewGroup;


public interface OnPingpaiItemHandleListener {
    
    
    boolean onClickPageItem(ViewGroup container, PingPaiItemRelativeLayout itemView, String tabelkey, int pageIndex, int itemIndex);
    boolean onRequestImageFunc(ViewGroup container, PingPaiItemRelativeLayout itemView, String tabelkey, int pageIndex, int itemIndex);

    boolean onInstantiateItemOfHandle(ViewGroup container, String tabelkey, int newPageIndex);
    boolean onRemoveItemOfHandle(ViewGroup container, String tabelkey, int removePageIndex);

}
