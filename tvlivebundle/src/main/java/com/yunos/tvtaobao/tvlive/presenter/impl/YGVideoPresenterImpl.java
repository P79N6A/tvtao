package com.yunos.tvtaobao.tvlive.presenter.impl;

import android.content.Context;

import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.tvlive.bo.model.YGVideoModel;
import com.yunos.tvtaobao.tvlive.bo.model.bean.YGVideoInfo;
import com.yunos.tvtaobao.tvlive.bo.model.impl.YGVideoImpl;
import com.yunos.tvtaobao.tvlive.presenter.YGVideoPresenter;
import com.yunos.tvtaobao.tvlive.view.IYGVideoView;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by huangdaju on 17/7/19.
 */

public class YGVideoPresenterImpl implements YGVideoPresenter {

    private YGVideoModel mYGVideoModel;
    private IYGVideoView mIYGVideoView;

    public YGVideoPresenterImpl(IYGVideoView iygVideoView) {
        mYGVideoModel = new YGVideoImpl();
        mIYGVideoView = iygVideoView;
    }

    /**
     * presenter层作为中间层，持有view和model层的引用
     */
    @Override
    public void loadYGVideo(Context context) {
//        mYGVideoModel.loadYGVideo(new OnYGVideoListener(new WeakReference((BaseActivity)context)));
    }

    @Override
    public void playVideo() {
        mIYGVideoView.playVideo();
    }

    public class OnYGVideoListener extends BizRequestListener<List<YGVideoInfo>> {

        public OnYGVideoListener(WeakReference baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(List<YGVideoInfo> data) {

        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }
}
