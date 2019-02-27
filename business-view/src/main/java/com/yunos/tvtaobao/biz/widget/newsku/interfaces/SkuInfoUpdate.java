package com.yunos.tvtaobao.biz.widget.newsku.interfaces;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/6/15
 *     desc :
 *     version : 1.0
 * </pre>
 */

public interface SkuInfoUpdate {
    void addSelectedPropData(long propId, long valueId);
    void deleteSelectedPropData(long propId, long valueId);
}
