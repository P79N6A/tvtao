package com.yunos.tvtaobao.search.model;

import com.yunos.tv.core.common.RequestListener;
import com.yunos.tvtaobao.biz.base.BaseModel;
import com.yunos.tvtaobao.biz.request.bo.GoodsSearchResultDo;
import com.yunos.tvtaobao.search.contract.SearchResultContract;

import java.util.List;

/**
 * Created by xtt
 * on 2018/12/6
 * desc
 */
public class SearchResultModel extends BaseModel implements SearchResultContract.Model {
    @Override
    public void getSearchGoodsResult(String q, Integer page_size, Integer page_no, int per, String sort,
                                     String cat, String flag,  List<String> list, boolean isFromCartToBuildOrder, boolean isFromBuildOrder,boolean isTmail, RequestListener<GoodsSearchResultDo> listener) {
        mBusinessRequest.requestSearchResult(q, page_size, page_no, per, sort, cat
                , flag, list, isFromCartToBuildOrder, isFromBuildOrder,isTmail, listener);

    }
}
