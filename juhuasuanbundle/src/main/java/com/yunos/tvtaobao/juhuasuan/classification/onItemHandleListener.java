package com.yunos.tvtaobao.juhuasuan.classification;

import android.view.ViewGroup;


public interface onItemHandleListener {
    
    boolean onClickPageItem(ViewGroup container, ItemFrameLayout itemView, String tabelkey, int pageIndex, int itemIndex);
    boolean onRequestImageFunc(ViewGroup container, ItemFrameLayout itemView, String tabelkey, int pageIndex, int itemIndex);

    boolean onInstantiateItemOfHandle(ViewGroup container, String tabelkey, int newPageIndex);
    boolean onRemoveItemOfHandle(ViewGroup container, String tabelkey, int removePageIndex);
    
}
