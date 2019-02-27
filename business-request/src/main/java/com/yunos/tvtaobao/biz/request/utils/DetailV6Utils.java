package com.yunos.tvtaobao.biz.request.utils;

import com.alibaba.fastjson.JSON;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.bo.MockData;
import com.yunos.tvtaobao.biz.request.bo.ResourceBean;
import com.yunos.tvtaobao.biz.request.bo.TBDetailResultV6;
import com.yunos.tvtaobao.biz.request.bo.Unit;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by dingbin on 2017/5/31.
 */

public class DetailV6Utils {


    /**
     * 获取apistack中的信息
     * @param tbDetailResultV6
     * @return
     */
    public static Unit getUnit(TBDetailResultV6 tbDetailResultV6){
        if (tbDetailResultV6.getApiStack()!=null) {
            List<TBDetailResultV6.ApiStackBean> apiStack = tbDetailResultV6.getApiStack();
            if (apiStack.get(0) != null) {
                String value = apiStack.get(0).getValue();
                Unit unit = JSON.parseObject(value, Unit.class);
                return unit;
            }
        }
        return null;

    }
    public static ResourceBean getResBean(String s) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(s);
            JSONObject resource = jsonObject.getJSONObject("resource");
            ResourceBean resourceBean = JSON.parseObject(resource.toString(), ResourceBean.class);;
            AppDebug.e("detail","resourceBean成功");
            return resourceBean;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static MockData getMockdata(TBDetailResultV6 tbDetailResultV6){
        if (tbDetailResultV6!=null){
            if (tbDetailResultV6.getMockData()!=null) {
                String mockData = tbDetailResultV6.getMockData();
                MockData mockData1 = JSON.parseObject(mockData, MockData.class);
                return mockData1;
            }
        }
        return null;
    }



}
