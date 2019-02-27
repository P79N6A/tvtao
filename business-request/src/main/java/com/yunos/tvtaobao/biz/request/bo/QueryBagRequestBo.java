/**
 * $
 * PROJECT NAME: business-request
 * PACKAGE NAME: com.yunos.tvtaobao.biz.request.bo
 * FILE NAME: BuildOrderRequest.java
 * CREATED TIME: 2015-2-26
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
package com.yunos.tvtaobao.biz.request.bo;


import java.io.Serializable;

public class QueryBagRequestBo implements Serializable {

    private static final long serialVersionUID = 3162775385145816370L;

    /**
     * 查询类型,0 - 全部商品 ; 2 - 降价商品 ; 4 - 库存紧张商品 不填则默认0
     */
    private int extStatus = 0;

    /**
     * 网络类型,0 - "unknow"; 2 - "2g"; 3 - "3g"; 1 - "wifi"; 4 - "23g"
     */
    private int netType = 0;

    /**
     * 分页信息
     */
    private String p = null;

    /**
     * 是否分页 true：分页请求，false：不分页请求
     */
    private boolean isPage = false;
    
    /**
     * 是否执行服务端控制参数,如启用压缩,{"gzip":true,"remoteCheck":false}
     */
    private String feature;
    
    /**
     * 来源标记,可只显示对应来源的购物车数据,如天猫超市:tsm_client_native
     */
    private String cartFrom;

    public int getExtStatus() {
        return extStatus;
    }

    public void setExtStatus(int extStatus) {
        this.extStatus = extStatus;
    }

    public int getNetType() {
        return netType;
    }

    public void setNetType(int netType) {
        this.netType = netType;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public boolean isPage() {
        return isPage;
    }

    public void setPage(boolean isPage) {
        this.isPage = isPage;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getCartFrom() {
        return cartFrom;
    }

    public void setCartFrom(String cartFrom) {
        this.cartFrom = cartFrom;
    }

    @Override
    public String toString() {
        return "QueryBagRequestBo{" +
                "extStatus=" + extStatus +
                ", netType=" + netType +
                ", p='" + p + '\'' +
                ", isPage=" + isPage +
                ", feature='" + feature + '\'' +
                ", cartFrom='" + cartFrom + '\'' +
                '}';
    }
}
