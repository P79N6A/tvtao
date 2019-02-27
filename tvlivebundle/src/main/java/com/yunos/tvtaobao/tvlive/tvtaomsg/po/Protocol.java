package com.yunos.tvtaobao.tvlive.tvtaomsg.po;

import java.util.Arrays;

/**
 * 协议包
 * Created by zhiping on 16/12/21.
 */
public class Protocol {

    private int bizType;
    private int version;
    private int length;
    private byte[] data;

    public Protocol(){}
    public Protocol(int bizType,int version,byte[] data){
        this.bizType = bizType;
        this.version = version;
        this.length = data.length;
        this.data = data;
    }

    public int getBizType() {
        return bizType;
    }

    public void setBizType(int bizType) {
        this.bizType = bizType;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Protocol{" +
                "bizType=" + bizType +
                ", version=" + version +
                ", length=" + length +
                ", data=" + Arrays.toString(data) +
                '}';
    }

}
