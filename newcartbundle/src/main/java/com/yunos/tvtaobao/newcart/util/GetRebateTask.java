package com.yunos.tvtaobao.newcart.util;

import android.os.AsyncTask;

import com.yunos.tvtaobao.newcart.entity.CartGoodsComponent;
import com.yunos.tvtaobao.newcart.ui.activity.NewShopCartListActivity;

import java.util.List;
import java.util.Map;

/**
 * Created by wuhaoteng on 2018/9/8.
 */

public class GetRebateTask extends AsyncTask<Map<String, List<CartGoodsComponent>>, Integer, String> {
    private NewShopCartListActivity.OnGetRebateListener mListener;

    public GetRebateTask(NewShopCartListActivity.OnGetRebateListener mListener) {
        this.mListener = mListener;
    }

    @Override
    protected String doInBackground(Map<String, List<CartGoodsComponent>>[] cartData) {
        String chooseRebate = RebateManager.getInstance().getChooseRebate(cartData[0]);
        return chooseRebate;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(mListener!=null){
            mListener.onReuslt(s);
        }

    }
}
