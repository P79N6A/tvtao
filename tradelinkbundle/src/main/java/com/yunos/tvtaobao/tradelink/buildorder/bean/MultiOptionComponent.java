package com.yunos.tvtaobao.tradelink.buildorder.bean;

public interface MultiOptionComponent<T> {

    T getComponentAt(int index);

    int getComponentCount();

    /////////////for entry view holders////

    String getEntryTitle();

    String getEntryDescription();

    String getEntryTip();

    ////////////for detail picker dialog///

    String getDetailButtonTip();

    String getDetailTitle();

    String getDetailSubtitle();

    String getDetailSubtitle2();

    ///////////////////////////////////////

    void applyChanges();

    void discardChanges();


}