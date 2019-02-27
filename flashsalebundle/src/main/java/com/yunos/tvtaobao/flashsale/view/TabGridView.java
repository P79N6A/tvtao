package com.yunos.tvtaobao.flashsale.view;


import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.taobao.statistic.CT;
import com.taobao.statistic.TBS;
import com.yunos.tv.app.widget.AdapterView;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.flashsale.AppManager;
import com.yunos.tvtaobao.flashsale.adapter.GoodsAdapter;
import com.yunos.tvtaobao.flashsale.bo.CategoryItem;
import com.yunos.tvtaobao.flashsale.bo.GoodsInfo;
import com.yunos.tvtaobao.flashsale.listener.ContextListener;
import com.yunos.tvtaobao.flashsale.listener.FlipperItemListener;
import com.yunos.tvtaobao.flashsale.listener.TabGridViewListener;
import com.yunos.tvtaobao.flashsale.request.RequestManager;
import com.yunos.tvtaobao.flashsale.utils.DateUtils;
import com.yunos.tvtaobao.flashsale.utils.TbsUtil;
import com.yunos.tvtaobao.flashsale.utils.req.ReqProcListener;
import com.yunos.tvtaobao.flashsale.utils.req.ReqStateInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TabGridView extends GoodsGridView implements TabGridViewListener, ReqProcListener {

    private final static String TAG = "TabGridView";
    private String mPeriodId;
    private ReqStateInfo mReqStateInfo;
    private RequestManager mRequestManager;
    private TabContentView mParent;
    private CategoryItem mCurItem;
    protected GoodsAdapter mAdapter;
    private Context mActivityContext;

    public TabGridView(Context contxt, TabContentView parent, CategoryItem item) {
        super(contxt);
        mActivityContext = contxt;
        mParent = parent;
        mCurItem = item;
        init();
    }

    private void init() {
        mRequestManager = AppManager.getInstance(super.getContext()).getRequestManager();
        mReqStateInfo = new ReqStateInfo(this);
        mAdapter = new GoodsAdapter(mActivityContext, this);
        mAdapter.setPageType(FlipperItemListener.TYPE_PERIOD_BUY);
        setAdapter(mAdapter);

        super.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                int size = mAdapter.getCount();
                if (position < size) {
                    GoodsInfo goodsInfo = (GoodsInfo) mAdapter.getItem(position);
                    if (null == goodsInfo) {
                        return;
                    }
                    /** 埋点数据 */
                    tbsClickEvent(goodsInfo, position);

                    if (NetWorkUtil.isNetWorkAvailable()) {
                        handlerItemClick(goodsInfo);
                    } else {
                        ContextListener l = mReqStateInfo.getContextListener();
                        if (null != l) {
                            l.showNetworkErrorDialog(false);
                        }
                    }
                }

            }
        });
    }

    public void setContextListener(ContextListener contextListener) {
        if (null != mReqStateInfo) {
            super.setContextListener(contextListener);
            mReqStateInfo.setContextListener(contextListener);
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
    }

    @Override
    public void onUnselect() {
        // TODO Auto-generated method stub
        super.removeAllViewsInLayout();
        // s.removeAllViews();
        mAdapter.setAdapter(null);
        stopFlip();
    }

    @Override
    public void onSelect() {
        // TODO Auto-generated method stub
        /** 请求数据 */
        forceSelectItem();
        mReqStateInfo.checkReq();
    }

    @Override
    public View getView() {
        // TODO Auto-generated method stub
        return this;
    }

    @Override
    public void onDestroy() {
        mReqStateInfo = null;
        MultReq req = mMultReq;
        if (null != req) {
            req.cancel();
            mMultReq = null;
        }
        super.removeAllViewsInLayout();
        mAdapter.clearData();
    }

    @Override
    public void loadingData(Object userData) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean loadingDataError(Object userData) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void loadingDataSuccess(Object userData, Object reqData) {
        // TODO Auto-generated method stub
        @SuppressWarnings("unchecked")
        List<GoodsInfo> data = ((List<GoodsInfo>) reqData);
        mAdapter.setAdapter(data);
        MultReq req = mMultReq;
        if (null != req) {
            req.cancel();
            mMultReq = null;
        }
            AppDebug.i(TAG, "loadingDataSuccess: success " + " mPeriodId:" + mPeriodId);
    }

    @Override
    public void excuteReq(Object userData) {
        // TODO Auto-generated method stub
            AppDebug.i(TAG, "excuteReq: create req " + " mPeriodId:" + mPeriodId);
        MultReq req = mMultReq;
        if (null != req) {
            req.cancel();
        }

        req = new MultReq();
        req.excuteReq();
        mMultReq = req;
    }

    @Override
    public boolean avaibleUpdate() {
        // TODO Auto-generated method stub
        return super.isShown();
    }

    @Override
    public void setObject(Object data, Object userData) {
        // TODO Auto-generated method stub
        mPeriodId = (String) data;
        onSelect();
    }

    public void forceReq() {
        MultReq req = mMultReq;
        if (null != req) {
            mMultReq.cancel();
            mMultReq = null;
        }
        mMultReq = new MultReq();
        mReqStateInfo.checkReq();
        AppDebug.i(TAG, "forceReq: create req " + " mPeriodId:" + mPeriodId);
    }

    private MultReq mMultReq;

    private class MultReq {

        private boolean mCancel = false;
        final private List<GoodsInfo> mData = new ArrayList<GoodsInfo>();

        public void cancel() {
            mCancel = true;
        }

        private RequestListener<List<GoodsInfo>> mSeckillListener = new RequestListener<List<GoodsInfo>>() {

            @Override
            public void onRequestDone(List<GoodsInfo> data, int resultCode, String msg) {
                // TODO Auto-generated method stub
                AppDebug.i(TAG, "getSeckillInfo onRequestDone" + " mPeriodId:" + mPeriodId + "start time: "
                            + mCurItem.getStartTime() + "mCancel: " + mCancel + "resultCode: " + resultCode);
                ReqStateInfo reqStateInfo = mReqStateInfo;
                if (mCancel || null == reqStateInfo) {
                    return;
                }
                mData.clear();
                if (RequestManager.CODE_SUCCESS == resultCode) {
                    if (null != data) {
                        mData.addAll(data);
                    }
                    // mRequestManager.getStockList(mPeriodId,
                    // mStockListListener);
                } else {
                    // mReqStateInfo.loadingDataError();
                }
                AppDebug.e(TAG,"获取整点抢购列表");
                mRequestManager.getStockList(mPeriodId, mStockListListener);
            }
        };

        private RequestListener<List<GoodsInfo>> mStockListListener = new RequestListener<List<GoodsInfo>>() {

            @Override
            public void onRequestDone(List<GoodsInfo> data, int resultCode, String msg) {
                // TODO Auto-generated method stub
                AppDebug.i(
                            TAG,
                            "getStockList onRequestDone" + " mPeriodId:" + mPeriodId + "start time: "
                                    + mCurItem.getStartTime() + "mCancel: " + mCancel + "resultCode: " + resultCode);
                ReqStateInfo reqStateInfo = mReqStateInfo;
                if (mCancel || null == reqStateInfo) {
                    return;
                }
                if (RequestManager.CODE_SUCCESS == resultCode) {
                    if (null != data) {
                        mData.addAll(data);
                    }
                    String startTime = mCurItem.getStartTime();
                    String endTime = mCurItem.getEndTime();
                    for (GoodsInfo info : mData) {
                        if (TextUtils.isEmpty(info.getStartTime())) {
                            info.setStartTime(startTime);
                        }
                        if (TextUtils.isEmpty(info.getEndTime())) {
                            info.setEndTime(endTime);
                        }

                    }
                    reqStateInfo.loadingDataSuccess(mData);
                } else {
                    reqStateInfo.loadingDataError(resultCode);
                }
            }
        };

        public void excuteReq() {
            if (mCancel) {
                return;
            }
            AppDebug.i(TAG, "excuteReq" + " mPeriodId:" + mPeriodId + "start time: " + mCurItem.getStartTime());
            /** 加载秒杀数据 */
            String startTime = mCurItem.getStartTime();
            // startTime = "20140602210000";
            mRequestManager.getSeckillInfo(startTime, mSeckillListener);
        }
    }

    private void tbsClickEvent(GoodsInfo info, int position) {
        CategoryItem qianggou = mParent.getQianggou();

        if (null == info || position < 0 || null == mCurItem || null == qianggou) {
            AppDebug.i("TabGridView", "tbsClickEvent: info:" + (null != info) + " position:" + position
                        + " mQianggou: " + (null != qianggou) + " mCurItem: " + (null != mCurItem));
            return;
        }

        String event = TbsUtil.getControlName(null, TbsUtil.CLICK_Home_P, 1 + position);
        Map<String, String> prop = TbsUtil.getTbsProperty(info, DateUtils.getTime(qianggou.getStartTime()),
                DateUtils.getTime(mCurItem.getStartTime()), DateUtils.getTime(mCurItem.getEndTime()),
                mCurItem.getStatus());


//        TBS.Adv.ctrlClicked(CT.Button, event, TbsUtil.getKeyValue(prop));

        prop.put("spm", SPMConfig.TAOQIANGGOU_MAIN_ITEM_P_NAME);
        Utils.utControlHit("Page_TaoQiangGou_Home", "Button-"+event, prop);
        Utils.updateNextPageProperties( SPMConfig.TAOQIANGGOU_MAIN_ITEM_P_NAME);


    }
}
