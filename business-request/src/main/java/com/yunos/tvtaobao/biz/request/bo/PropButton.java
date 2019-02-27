package com.yunos.tvtaobao.biz.request.bo;

import android.widget.Button;

import java.io.Serializable;


public class PropButton implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4715336720433647215L;
    
    /**
     * sku属性ID
     */
    private Long pvid;
    
    /**
     * sku属性对应按钮
     */
    private Button button;
    
    public PropButton(Long pvid, Button button) {
        this.pvid = pvid;
        this.button = button;
    }

    public Long getPvid() {
        return pvid;
    }

    public Button getButton() {
        return button;
    }
    
}
