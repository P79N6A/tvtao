/**
 * 
 */
package com.yunos.tvtaobao.biz.request.item;


import com.yunos.tvtaobao.biz.request.base.BaseMtopRequest;
import com.yunos.tvtaobao.biz.request.bo.TbItemDetail;

import org.json.JSONObject;

import java.util.Map;

/**
 * @author tianxiang
 * @date 2012-10-18 下午4:52:23
 */
public class GetTbItemDetailRequest extends BaseMtopRequest {

    private static final long serialVersionUID = 73580007519257859L;

    private static final String API = "mtop.wdetail.getItemDetail";

    private static final String KEY_ITEM_NUM_ID = "itemNumId";

    private Long itemNumId;

    public GetTbItemDetailRequest(Long itemNumId) {
        this.itemNumId = itemNumId;
    }

    @Override
    protected Map<String, String> getAppData() {
        addParams(KEY_ITEM_NUM_ID, String.valueOf(itemNumId));
        return null;
    }

    @Override
    public String getApi() {
        return API;
    }

    /**
     * 注意这里，指定了私有版本
     */
    @Override
    public String getApiVersion() {
        return "3.2";
    }

    @Override
    public boolean getNeedEcode() {
        return false;
    }

    @Override
    public boolean getNeedSession() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected TbItemDetail resolveResponse(JSONObject obj) throws Exception {
        return TbItemDetail.resolveFromMTOP(obj);
    }
}
