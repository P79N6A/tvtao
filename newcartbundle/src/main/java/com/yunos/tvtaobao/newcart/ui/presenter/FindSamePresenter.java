package com.yunos.tvtaobao.newcart.ui.presenter;

import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.bo.FindSameContainerBean;
import com.yunos.tvtaobao.biz.request.bo.RebateBo;
import com.yunos.tvtaobao.biz.base.BasePresenter;
import com.yunos.tvtaobao.newcart.ui.contract.IFindSameContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by zhoubo on 2018/7/11.
 * describe 找相似P层
 */

public class FindSamePresenter extends BasePresenter<IFindSameContract.IFindSameModel, IFindSameContract.IFindSameView> {

    public FindSamePresenter(IFindSameContract.IFindSameModel model, IFindSameContract.IFindSameView findSameView) {
        super(model, findSameView);
    }

    public void getFindSame(int pageSize, int currentPage, String catId, String nid, BaseActivity baseActivity) {
        mModel.getFindSameGoods(pageSize, currentPage, catId, nid, new FindSameListener(new WeakReference<BaseActivity>(baseActivity)));
    }

    public void getFindSameRebate( String itemIdArray, List<String> list,FindSameContainerBean findSameContainerBean, BaseActivity baseActivity){

        try{
            JSONObject object = new JSONObject();
            object.put("umToken", Config.getUmtoken(baseActivity));
            object.put("wua", Config.getWua(baseActivity));
            object.put("isSimulator", Config.isSimulator(baseActivity));
            object.put("userAgent", Config.getAndroidSystem(baseActivity));
            String extParams = object.toString();
            mModel.getFindSameGoodsRebate(itemIdArray,list,extParams,new GetRebateBusinessRequestListener(findSameContainerBean,new WeakReference<BaseActivity>(baseActivity)));

        }catch (JSONException e){
            e.printStackTrace();
        }
      }


    private class GetRebateBusinessRequestListener extends BizRequestListener<List<RebateBo>> {

        private FindSameContainerBean findSameContainerBean;
        public GetRebateBusinessRequestListener( FindSameContainerBean findSameContainerBean,WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
            this.findSameContainerBean = findSameContainerBean;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(List<RebateBo> data) {
            mRootView.showProgressDialog(false);
            mRootView.showFindsameRebateResult(findSameContainerBean,data);


        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }


    private class FindSameListener extends BizRequestListener<FindSameContainerBean> {
        public FindSameListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(FindSameContainerBean data) {

            mRootView.showFindsameResult(data);
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

}
