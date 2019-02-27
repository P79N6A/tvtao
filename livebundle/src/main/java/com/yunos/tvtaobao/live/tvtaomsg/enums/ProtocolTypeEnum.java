package com.yunos.tvtaobao.live.tvtaomsg.enums;

/**
 * 协议类型枚举
 * Created by zhiping on 16/12/20.
 */
public enum ProtocolTypeEnum {
    JSON("JSON",1),
    Protobuffer("Protobuffer",2);

    private int    type;
    private String name;
    private String cnName;

    private ProtocolTypeEnum(String cnName, int type){
        this.name = this.name();
        this.cnName = cnName;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getCnName() {
        return cnName;
    }
}
