package com.yunos.tvtaobao.search.contract;

import com.yunos.tv.core.common.RequestListener;
import com.yunos.tvtaobao.biz.base.IModel;
import com.yunos.tvtaobao.biz.base.IView;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.bo.GoodsSearchResultDo;
import com.yunos.tvtaobao.biz.request.bo.GoodsSearchZtcResult;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by xtt
 * on 2018/12/6
 * desc
 */
public interface SearchResultContract {

    //对于经常使用的关于UI的方法可以定义到IView中
    interface View extends IView {
        void setSearchResultData(GoodsSearchResultDo data);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,如是否使用缓存
    interface Model extends IModel {
        void getSearchGoodsResult(String q, Integer page_size, Integer page_no, int per, String sort, String cat,
                                  String flag, List<String> list, boolean isFromCartToBuildOrder, boolean isFromBuildOrder,boolean isTmail,
                                  RequestListener<GoodsSearchResultDo> listener);

    }
}
