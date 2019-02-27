package com.yunos.tvtaobao.biz.widget.newsku.bo;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/6/15
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class PropData {
    public String propKey;// key值
    public long propId; // 属性ID
    public long valueId; // 值ID
    public String imageUrl; // 主图url
    public boolean selected = false;
    public boolean enable = false;

    @Override
    public String toString() {
        return "{ propKey = " + propKey + ", propId = " + propId + ", valueId = " + valueId + ", selected = "
                + selected + ", enable = " + enable + ", imageUrl = " + imageUrl + "}";
    }
}
