/**
 * Copyright (C) 2015 The ALI OS Project
 * Version Date Author
 * 2015-4-19 lizhi.ywp
 */
package com.yunos.tvtaobao.flashsale.request.item;


import com.alibaba.fastjson.JSON;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.flashsale.bo.GoodsInfo;
import com.yunos.tvtaobao.flashsale.request.RequestManager;
import com.yunos.tvtaobao.flashsale.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetStockInfoRequest extends BaseMtopRequest {

    private static final long serialVersionUID = -2947862794214655016L;
    private String mBatchId;
    private byte mType;

    public GetStockInfoRequest(String batchId, byte type) {
        mBatchId = batchId;
        mType = type;
    }

    @Override
    protected Map<String, String> getAppData() {
        if (mType == RequestManager.REQ_STOCK_LIST) {
            addParams("batchId", mBatchId);
        } else if (mType == RequestManager.REQ_STOCK_BY_BATCHID) {
            addParams("ids", mBatchId);
        } else if (mType == RequestManager.REQ_getSeckillInfo) {
            /** 添加 */
            addParams("datetime", mBatchId);
        } else {
            throw new IllegalArgumentException("Mtop API TYPE ERROR!");
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<GoodsInfo> resolveResponse(JSONObject obj) throws Exception {
        if (null == obj) {
            return null;
        }
        AppDebug.e(TAG,"抢购数据= "+obj.toString());
        JSONArray items = null;
        if (mType == RequestManager.REQ_getSeckillInfo) {
            items = obj.optJSONArray("result");
        } else {
            items = obj.optJSONArray("items");
        }

        if (null == items) {
            return null;
        }

        List<GoodsInfo> ret = null;
        if (mType == RequestManager.REQ_STOCK_LIST) {
            ret = (List<GoodsInfo>) JSON.parseArray(items.toString(), GoodsInfo.class);
            /** 过滤秒杀内容 */
            if (null != ret) {
                int size = ret.size();
                GoodsInfo info;
                for (int index = size - 1; index >= 0; index--) {
                    info = ret.get(index);
                    if (GoodsInfo.ITEM_TYPE_SECKILL_INFO == info.getType()) {
                        ret.remove(index);
                    }
                }
            }
        } else if (mType == RequestManager.REQ_STOCK_BY_BATCHID) {
            int size = items.length();
            if (size <= 0) {
                return null;
            }
            ret = new ArrayList<GoodsInfo>(size);
            GoodsInfo info;
            JSONObject tmp;

            for (int index = 0; index < size; index++) {
                tmp = items.getJSONObject(index);
                info = JsonUtils.resolveResponse(tmp);
                if (null != info && GoodsInfo.ITEM_TYPE_SECKILL_INFO != info.getType()) {
                    ret.add(info);
                }
            }
        } else if (mType == RequestManager.REQ_getSeckillInfo) {
            int size = items.length();
            if (size <= 0) {
                return null;
            }
            ret = new ArrayList<GoodsInfo>(size);
            GoodsInfo info;
            JSONObject tmp;

            for (int index = 0; index < size; index++) {
                tmp = items.getJSONObject(index);
                info = JsonUtils.resolveResponseBySeckill(tmp);
                if (null != info) {
                    ret.add(info);
                }
            }
        }

        return ret;
    }

    @Override
    protected String getApi() {
        if (mType == RequestManager.REQ_STOCK_LIST) {
            AppDebug.e(TAG,"-----type1");
            return "mtop.msp.qianggou.queryItemByBatchId";
        } else if (mType == RequestManager.REQ_STOCK_BY_BATCHID) {
            AppDebug.e(TAG,"-----type2");
            return "mtop.msp.qianggou.queryItemListByIds";
        } else if (mType == RequestManager.REQ_getSeckillInfo) {
            AppDebug.e(TAG,"-----type3");
            return "mtop.tvtaobao.querySeckillItem";
        }
        throw new IllegalArgumentException("Mtop API TYPE ERROR!");
    }

    @Override
    protected String getApiVersion() {
        if (mType == RequestManager.REQ_STOCK_LIST) {
            return "3.0";
        }

        return "1.0";
    }

    @Override
    public boolean getNeedEcode() {
        return false;
    }

    @Override
    public boolean getNeedSession() {
        return false;
    }
}
