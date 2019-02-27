package com.yunos.tvtaobao.newcart.ui.contract;

import com.yunos.tv.core.common.RequestListener;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.bo.FindSameContainerBean;
import com.yunos.tvtaobao.biz.request.bo.RebateBo;
import com.yunos.tvtaobao.biz.base.IModel;
import com.yunos.tvtaobao.biz.base.IView;

import java.util.List;

/**
 * Created by zhoubo on 2018/7/11.
 * describe 找相似
 */

public interface IFindSameContract {

    interface IFindSameView extends IView {

        void initViewConfig(FindSameContainerBean findSameList);

        void showFindsameResult(FindSameContainerBean findSameBeanList);

        void showFindsameRebateResult(FindSameContainerBean findSameContainerBean,List<RebateBo> data);

    }

    interface IFindSameModel extends IModel {
        void getFindSameGoods(int pageSize, int currentPage, String catid, String nid,BizRequestListener<FindSameContainerBean> listener);
        void getFindSameGoodsRebate( String itemIdArray,List<String> list,String extParams,RequestListener<List<RebateBo>> listener);

    }

}
