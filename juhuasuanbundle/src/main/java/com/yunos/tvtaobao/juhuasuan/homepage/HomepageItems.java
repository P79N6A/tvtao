package com.yunos.tvtaobao.juhuasuan.homepage;


import com.alibaba.fastjson.JSON;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CategoryMO;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.HomeCatesResponse;

import java.io.Serializable;
import java.util.List;

public class HomepageItems implements Serializable {

    private static final long serialVersionUID = -83757915725358794L;
    //    public List<CategoryMO> ListCategoryMo;

    public String JsonListCategoryMo;

    public HomeCatesResponse HomeCates;

    public static List<CategoryMO> convertJson(String json) {
        List<CategoryMO> list = JSON.parseArray(json, CategoryMO.class);
        return list;
    }
}
