package com.yunos.tvtaobao.biz.request.base;

import anetwork.channel.Param;

/**
 * Created by vincent on 1/16/17.
 */

public class PostParam implements Param {

    private String key = "";
    private String value = "";

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public PostParam(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
