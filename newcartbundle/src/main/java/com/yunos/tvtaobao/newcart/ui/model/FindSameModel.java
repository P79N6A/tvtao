package com.yunos.tvtaobao.newcart.ui.model;

import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.bo.FindSameContainerBean;
import com.yunos.tvtaobao.biz.request.bo.RebateBo;
import com.yunos.tvtaobao.biz.base.BaseModel;
import com.yunos.tvtaobao.newcart.ui.contract.IFindSameContract;

import java.util.List;

/**
 * Created by zhoubo on 2018/7/11.
 * zhoubo on 2018/7/11 15:30
 * describition 找相似Model层
 */

public class FindSameModel extends BaseModel implements IFindSameContract.IFindSameModel {

    /**
     *  请求相似物品接口
     * @param pageSize 请求的页面数量
     * @param currentPage 当前页
     * @param catid catid
     * @param listener 回调到View层
     */
    @Override
    public void getFindSameGoods(int pageSize, int currentPage, String catid, String nid, BizRequestListener<FindSameContainerBean> listener) {
        mBusinessRequest.findSame(pageSize,currentPage,catid,nid,listener);
    }

    @Override
    public void getFindSameGoodsRebate( String itemIdArray, List<String> list,String extParams, RequestListener<List<RebateBo>> listener) {
        mBusinessRequest.requestRebateMoney(itemIdArray,list,false,false,true,extParams,listener);
    }


}
