package com.yunos.tvtaobao.live.controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.Display;
import android.view.View;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.TBaoLiveListBean;
import com.yunos.tvtaobao.biz.request.bo.TMallLiveBean;
import com.yunos.tvtaobao.biz.request.item.GetLiveListRequest;
import com.yunos.tvtaobao.live.request.TMallLiveListRequest;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by pan on 2017/3/16.
 */

public class LiveDataController {
    private final String TAG = LiveDataController.class.getName();
    private static LiveDataController controller;
    private BusinessRequest mBusinessRequest;
    private List<TBaoLiveListBean> mTBaoLiveListBean = null;
    private TBaoLiveListBean mTBaoItemsBean = null;
    private List<TMallLiveBean> mTMallLiveListBean = null;
    private TMallLiveBean mTMallItemsBean = null;
    private int currentTBaoLivePos = 0;
    private int currentTMallLivePos = 0;
    private int currentYGVideoPos = 0;
    private String liveUrl = null;

    private LiveDataController(){}

    public static LiveDataController getInstance() {
        if (controller == null) {
            synchronized (LiveDataController.class) {
                if (controller == null) {
                    controller = new LiveDataController();
                    controller.initData();
                }
            }
        }
        return controller;
    }

    private void initData() {
        mBusinessRequest = BusinessRequest.getBusinessRequest();
    }

    private Bitmap bmp;
    public void setBackGroundBitmap(Activity activity) {
        // 获取windows中最顶层的view
        AppDebug.w(TAG, "1 current time " + System.currentTimeMillis());
        View view = activity.getWindow().getDecorView();
        view.buildDrawingCache();

        // 获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;
        Display display = activity.getWindowManager().getDefaultDisplay();

        // 获取屏幕宽和高
        int widths = display.getWidth();
        int heights = display.getHeight();

        // 允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);

        // 去掉状态栏
        bmp = Bitmap.createBitmap(view.getDrawingCache(), 0,
                statusBarHeights, widths, heights - statusBarHeights);

        // 销毁缓存信息
        view.destroyDrawingCache();
        AppDebug.w(TAG, "2 current time " + System.currentTimeMillis());
    }

    public Bitmap getBackGroundBitmap() {
        return bmp;
    }
    /**
     * 请求淘宝直播
     */
    public void requestTBaoLiveList(Context context, LiveRequestCallBack callBack) {
        mBusinessRequest.baseRequest(new GetLiveListRequest(), new GetTBaoLiveListListener(new WeakReference<BaseActivity>((BaseActivity) context), callBack), false);
    }

    /**
     * 天猫直播列表
     *
     */
    public void requestTMallLiveList(Context context, LiveRequestCallBack callBack) {
        mBusinessRequest.baseRequest(new TMallLiveListRequest(), new GetTMallLiveListListener(new WeakReference<BaseActivity>((BaseActivity) context), callBack), false);
    }

    private class GetTMallLiveListListener extends BizRequestListener<List<TMallLiveBean>> {

        private LiveRequestCallBack callBack;
        private GetTMallLiveListListener(WeakReference<BaseActivity> baseActivityRef, LiveRequestCallBack callBack) {
            super(baseActivityRef);
            this.callBack = callBack;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            if (callBack != null)
                return callBack.error(msg);
            return false;
        }

        @Override
        public void onSuccess(List<TMallLiveBean> data) {
            if (callBack != null)
                callBack.success(data);

            setTMallLiveListBean(data);
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    private class GetTBaoLiveListListener extends BizRequestListener<List<TBaoLiveListBean>> {

        private LiveRequestCallBack callBack;
        private GetTBaoLiveListListener(WeakReference<BaseActivity> baseActivityRef, LiveRequestCallBack callBack) {
            super(baseActivityRef);
            this.callBack = callBack;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            if (callBack != null)
                return callBack.error(msg);
            return false;
        }

        @Override
        public void onSuccess(List<TBaoLiveListBean> data) {
            if (callBack != null)
                callBack.success(data);

            setTBaoLiveListBean(data);
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    /**
     * 清除数据
     */
    public void clear() {
        mTBaoItemsBean = null;
        currentTBaoLivePos = 0;
    }

    public List<TBaoLiveListBean> getTBaoLiveListBean() {
        return mTBaoLiveListBean;
    }

    public void setTBaoLiveListBean(List<TBaoLiveListBean> tBaoLiveListBean) {
        this.mTBaoLiveListBean = tBaoLiveListBean;
    }

    public TBaoLiveListBean getTBaoItemsBean() {
        return mTBaoItemsBean;
    }

    public void setTBaoItemsBean(TBaoLiveListBean tBaoItemsBean) {
        this.mTBaoItemsBean = tBaoItemsBean;
    }

    public List<TMallLiveBean> getTMallLiveListBean() {
        return mTMallLiveListBean;
    }

    public void setTMallLiveListBean(List<TMallLiveBean> tMallLiveListBean) {
        this.mTMallLiveListBean = tMallLiveListBean;
    }

    public TMallLiveBean getTMallItemsBean() {
        return mTMallItemsBean;
    }

    public void setTMallItemsBean(TMallLiveBean tMallItemsBean) {
        this.mTMallItemsBean = tMallItemsBean;
    }

    public int getCurrentYGVideoPos() {
        return currentYGVideoPos;
    }

    public void setCurrentYGVideoPos(int currentYGVideoPos) {
        this.currentYGVideoPos = currentYGVideoPos;
    }

    public int getCurrentTBaoLivePos() {
        return currentTBaoLivePos;
    }

    public void setCurrentTBaoLivePos(int currentTBaoLivePos) {
        this.currentTBaoLivePos = currentTBaoLivePos;
    }

    public int getCurrentTMallLivePos() {
        return currentTMallLivePos;
    }

    public void setCurrentTMallLivePos(int currentTMallLivePos) {
        this.currentTMallLivePos = currentTMallLivePos;
    }

    public String getLiveUrl() {
        return liveUrl;
    }

    public void setLiveUrl(String liveUrl) {
        this.liveUrl = liveUrl;
    }
}
