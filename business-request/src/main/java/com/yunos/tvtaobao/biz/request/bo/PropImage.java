package com.yunos.tvtaobao.biz.request.bo;

import java.io.Serializable;

/**
 * SKU属性图片
 * @author shengchun
 *
 */
public class PropImage implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3718608175947206234L;
    
    /**
     * 属性ID
     */
    private Long pid;
    
    /**
     * 当前选中属性对应商品图片
     */
    private String itemImage;
    
    public PropImage(Long pid, String image) {
        this.pid = pid;
        this.itemImage = image;
    }

    public String getItemImage() {
        return itemImage;
    }
    
    public Long getPid() {
        return pid;
    }
}
