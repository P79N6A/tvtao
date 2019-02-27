package com.yunos.tvtaobao.tvlive.activity;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.util.IntentDataUtil;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.bo.YGAcrVideoItem;
import com.yunos.tvtaobao.biz.request.bo.YGAttachInfo;
import com.yunos.tvtaobao.tvlive.R;
import com.yunos.tvtaobao.tvlive.bo.model.bean.YGVideoInfo;
import com.yunos.tvtaobao.tvlive.presenter.YGVideoPresenter;
import com.yunos.tvtaobao.tvlive.presenter.impl.YGVideoPresenterImpl;
import com.yunos.tvtaobao.tvlive.request.TvLiveRequest;
import com.yunos.tvtaobao.tvlive.tvtaomsg.TvTaobaoMsgService;
import com.yunos.tvtaobao.tvlive.tvtaomsg.po.TVTaoMessage;
import com.yunos.tvtaobao.tvlive.tvtaomsg.service.ITVTaoDiapatcher;
import com.yunos.tvtaobao.tvlive.utils.PowerMsgType;
import com.yunos.tvtaobao.tvlive.utils.VideoType;
import com.yunos.tvtaobao.tvlive.view.IYGVideoView;
import com.yunos.tvtaobao.tvlive.view.media.AndroidMediaController;
import com.yunos.tvtaobao.tvlive.view.media.IjkVideoView;
import com.yunos.tvtaobao.tvlive.widget.TimerToast;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by pan on 2017/1/22.
 */

public class TVBuyActivity extends BaseVideoActivity implements Handler.Callback, IYGVideoView {
    private static final String TAG = "TVBuyActivity";
    private static final int YGVIDEO_ACR_DATA_NOTIFY = 0;
    private RelativeLayout mRootLayout;
    private IjkVideoView yg_video;
    private AndroidMediaController controller;

//    private int type;
//    private String liveId, liveUrl, topic;
    private boolean isFullScreen;
    private int loadingType = 0; //0 可以切换,1 不可切换视频
    private Handler mHandler = new Handler(this);
    private Drawable loadingDw;
    private SharedPreferences.Editor editor;
    private List<YGVideoInfo> mYGVideoList = null;
    private TimerToast toast;
    private boolean UpAndDownKey = true;
    private boolean isFirstLoading = true;
    private int currentYGVideoPos = 0;
    private TvLiveRequest mBusinessRequest;
    //    private LiveDataController mLiveDataController;
    private Map<String, List<YGAcrVideoItem>> acrDataMap; //视频acr数据
    //    private TVDialogManager tvDialogManager;
//    private LiveListDialog liveListDialog;
    private FragmentManager fragmentManager = null;
    private YGVideoPresenter mYGVideoPresenter;
    private YGVideoInfo mcurrentYGVideoInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_yangguang_video);
        findViewBy();

        SharedPreferences sharedPreferences = getSharedPreferences("live", Context.MODE_APPEND);
        editor = sharedPreferences.edit();
        UpAndDownKey = sharedPreferences.getBoolean("UpAndDownKey", true);

//        mLiveDataController = LiveDataController.getInstance();
//        mBusinessRequest = TvLiveRequest.getBusinessRequest();
//        HomeVideoViewController.liveType = 3;
        mcurrentYGVideoInfo = (YGVideoInfo) IntentDataUtil.getObjectFromBundle(getIntent(),"videoInfo",null);
        isFullScreen = IntentDataUtil.getBoolean(getIntent(), "fullscreen", false);

        mYGVideoPresenter = new YGVideoPresenterImpl(this);
        if (isFullScreen)
            mYGVideoPresenter.playVideo();

//        AppDebug.e(TAG, "liveId = " + liveId + " ,type = " + type + " ,liveUrl = " + liveUrl + " ,topic = " + topic);
//        currentYGVideoPos = mLiveDataController.getCurrentYGVideoPos();
//        mYGVideoList = mLiveDataController.getYGVideoList();
//        if (type == 0 && !isFullScreen) {
//            mBusinessRequest.getYGLiveAttachInfo(liveId, new GetYGAttachInfoListener(new WeakReference<BaseActivity>(TVBuyActivity.this)));
//        }

//        loadingState();
        initADVDialog();


    }

    private void initADVDialog() {
//        tvDialogManager = new TVDialogManager(this);
//        //通过接口去设置相关的初始化参数
//        tvDialogManager.setRootView(mRootLayout);
////        tvDialogManager.SetFragmentTransaction(fragmentTransaction);
//        tvDialogManager.SetContext(this);
//        tvDialogManager.SetWindowsType(0);  //显示adv的信息...
    }

    /**
     * 视频播放
     */
    @Override
    public void playVideo() {
        if (mcurrentYGVideoInfo != null) {
            yg_video.switchOn();
            yg_video.setVideoURI(Uri.parse(mcurrentYGVideoInfo.getVideoUrl()));
            yg_video.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (yg_video != null && !yg_video.isPlaying() && liveUrl != null) {
//            initVideoView(liveUrl);
//        }
//        //获取央广购物打点信息。直播通过ACCS下发，点播通过mTop请求
//        if (type == 0) {
//            subscribeTopic(liveId, topic);
//        } else {
//            requestACRData(liveId);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (topic != null && !topic.equals(""))
//            unSubscribeTopic(topic);
//        if (yg_video != null)
//            yg_video.pause();
//        mHandler.removeCallbacksAndMessages(null);

    }

    @Override
    public void finish() {
        yg_video.switchOff();
        yg_video.release(true);

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("ygvideoInfo",mcurrentYGVideoInfo);
        intent.putExtras(bundle);
        this.setResult(VideoType.TV_BUY_VIDEO, intent);
//        yg_video.suspend();
//        tvDialogManager.onDestroy();
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (toast != null)
            toast.hide();
        clearViews();
//        Tools.runGc();
    }

    private void clearViews() {


//        if (liveListDialog != null)
//            liveListDialog.destory();
        if (acrDataMap != null)
            acrDataMap.clear();
        acrDataMap = null;
        toast = null;
        yg_video = null;
        controller = null;
        yg_video_info_prompt = null;
        mHandler = null;
        mBusinessRequest = null;
        editor = null;
    }

    private void videoListener() {
        yg_video.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                AppDebug.e(TAG, "yg_video is Error");
                autoChangeLive();
                return false;
            }
        });

        yg_video.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                AppDebug.e(TAG, "yg_video is Prepared");
                loadingType = 0;
                controller.hide();
            }
        });

        yg_video.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                AppDebug.e(TAG, "yg_video is Completion");
                autoChangeLive();
            }
        });
    }

    private void findViewBy() {
        mRootLayout = (RelativeLayout) findViewById(R.id.mRootLayout);
        yg_video = (IjkVideoView) findViewById(R.id.yg_video);
        controller = (AndroidMediaController) findViewById(R.id.media_controller);
        yg_video_info_prompt = (ImageView) findViewById(R.id.yg_video_info_prompt);
        loadingDw = this.getResources().getDrawable(R.drawable.yangguang_video_loading_img);
        yg_video_info_prompt.setImageDrawable(loadingDw);
        mHandler.sendEmptyMessageDelayed(PowerMsgType.LIVE_LOADING_NOTIFY, 2000);

        videoListener();
    }

    /**
     * 请求央广点播视频的打点信息
     *
     * @param liveId 视频Id
     */
    private void requestACRData(String liveId) {
        if (acrDataMap != null && acrDataMap.containsKey(liveId)) {
            AnalysisData(acrDataMap.get(liveId));
        } else {
            mBusinessRequest.getYGVideoItems(liveId, new GetYGVideoItemsListener(new WeakReference<BaseActivity>(TVBuyActivity.this), liveId));
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
//        AppDebug.d(TAG, "isPaying " + tvDialogManager.isPaying());
//        if (tvDialogManager.isPaying())
//            return true;
//        if (tvDialogManager.hasFragment())
//            return super.dispatchKeyEvent(event);
//
//        if (event.getAction() == KeyEvent.ACTION_DOWN) {
//            if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
//                if (liveListDialog == null)
//                    liveListDialog = LiveListDialog.getInstance(this);
//                liveListDialog.show(null);
//                return true;
//            }
//
//            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
//                UpDownKeyChangeLive(false);
//            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
//                UpDownKeyChangeLive(true);
//            }
//        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 按上下键进行切换视频
     *
     * @param isKeyDown true 向下键，false 向上键
     */
    private void UpDownKeyChangeLive(boolean isKeyDown) {
        AppDebug.d(TAG, "UpDownKeyChangeLive loadingType = " + loadingType);
        if (loadingType == 1)
            return;

        if (mYGVideoList != null) {
            int mYGVideoListSize = mYGVideoList.size();
            boolean isINData = currentYGVideoPos < mYGVideoListSize;
            if (isKeyDown) {
                if (currentYGVideoPos < mYGVideoListSize - 1 && isINData) {
                    currentYGVideoPos = currentYGVideoPos + 1;

                    changeLive(currentYGVideoPos, mYGVideoList.get(currentYGVideoPos));
                } else {
                    currentYGVideoPos = 0;
                    changeLive(currentYGVideoPos, mYGVideoList.get(currentYGVideoPos));
                }
            } else {
                if (currentYGVideoPos > 0 && isINData) {
                    currentYGVideoPos = currentYGVideoPos - 1;

                    changeLive(currentYGVideoPos, mYGVideoList.get(currentYGVideoPos));
                } else {
                    currentYGVideoPos = mYGVideoListSize - 1;
                    changeLive(currentYGVideoPos, mYGVideoList.get(currentYGVideoPos));
                }
            }

            if (UpAndDownKey) {
                editor.putBoolean("UpAndDownKey", false);
                editor.apply();
            }
        }
    }

    /**
     * 自动播放下一个视频
     */
    private void autoChangeLive() {
        if (mYGVideoList == null)
            return;

        if (currentYGVideoPos >= mYGVideoList.size() - 1) {
            if (mYGVideoList.size() > 0) {
                currentYGVideoPos = 0;
                changeLive(currentYGVideoPos, mYGVideoList.get(currentYGVideoPos));
            }
        } else {
            UpDownKeyChangeLive(true);
        }
    }

    /**
     * loading状态，设置controller显示，并设置无法进行按钮点击
     */
    private void loadingState() {
        loadingType = 1;
        controller.show();
    }

    /**
     * 订阅直播，用于发送广告
     *
     * @param topic
     */
    private void subscribeTopic(final String liveId, final String topic) {
        TvTaobaoMsgService.registerTVLive(TVBuyActivity.this, TvTaobaoMsgService.TYPE_YANGGUANG_LIVE, topic, new ITVTaoDiapatcher() {
            @Override
            public void Diapatcher(TVTaoMessage taoMessage) {
                if (taoMessage.topic.equals(topic)) {
                    AppDebug.d(TAG, "subscribeTopic Diapatcher data ====> " + taoMessage.tid);
                    mBusinessRequest.getYGLiveAttachInfo(liveId, new GetYGAttachInfoListener(new WeakReference<BaseActivity>(TVBuyActivity.this)));
                }
            }

            @Override
            public void Error(Map<String, String> map, int Error) {

            }
        });
    }

    /**
     * 取消订阅
     *
     * @param topic
     */
    private void unSubscribeTopic(String topic) {
        TvTaobaoMsgService.unRegisterTVLive(TVBuyActivity.this, TvTaobaoMsgService.TYPE_YANGGUANG_LIVE, topic);
    }

    public int getLoadingType() {
        return loadingType;
    }

    /**
     * 分析央广点播打点数据，进行发送显示
     */
    private void AnalysisData(List<YGAcrVideoItem> data) {
        if (data == null || data.size() == 0)
            return;

        int dataSize = data.size();
        for (int i = 0; i < dataSize; i++) {
            AppDebug.d(TAG, "AnalysisData startAt >> " + data.get(i).getStartAt());

            Message msg = new Message();
            msg.what = YGVIDEO_ACR_DATA_NOTIFY;
            Bundle bundle = new Bundle();
            bundle.putString("tid", data.get(i).getTid());
            bundle.putString("duration", data.get(i).getDuration());
            bundle.putString("hotline", data.get(i).getHotline());
            bundle.putString("thirdItemId", data.get(i).getThirdItemId());
            msg.setData(bundle);
            mHandler.sendMessageDelayed(msg, Integer.parseInt(data.get(i).getStartAt()) * 1000);
        }
    }

    private void removeHandler() {
        mHandler.removeMessages(YGVIDEO_ACR_DATA_NOTIFY);
    }

    /**
     * 切换视频
     *
     * @param videoBean
     */
    public void changeLive(int currentPos, YGVideoInfo videoBean) {
        if (loadingType == 1)
            return;
//        tvDialogManager.hideadvDialog();
        String topic = mcurrentYGVideoInfo.getTopic();
        String liveId = mcurrentYGVideoInfo.getId();
        int type = mcurrentYGVideoInfo.getType();
        if (topic != null && !topic.equals(""))
            unSubscribeTopic(topic);

        removeHandler();
        loadingState();
        initVideoView(videoBean.getVideoUrl());
        type = videoBean.getType();
        liveId = videoBean.getId();
        if (videoBean.getType() == 0) {
            topic = videoBean.getTopic();
            subscribeTopic(liveId, topic);
            mBusinessRequest.getYGLiveAttachInfo(liveId, new GetYGAttachInfoListener(new WeakReference<BaseActivity>(TVBuyActivity.this)));
        } else {
            topic = "";
            requestACRData(liveId);
        }
//        mLiveDataController.setYGVideoBean(videoBean);
//        mLiveDataController.setCurrentYGVideoPos(currentPos);
    }

    /**
     * 加载视频
     *
     * @param hlsUrl
     */
    private void initVideoView(String hlsUrl) {
        if (hlsUrl != null && yg_video != null) {
//            mLiveDataController.setLiveUrl(hlsUrl);
            controller.show();
//            yg_video.resume();
//            yg_video.release(true);
            yg_video.setVideoURI(Uri.parse(hlsUrl));
//            yg_video.resume();
            yg_video.start();
        }
    }

    /**
     * 隐藏loading的图片
     */
    private void hiddenCover() {
        AlphaAnimation animation = new AlphaAnimation(1f, 0f);
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                yg_video_info_prompt.setVisibility(View.GONE);
                if (UpAndDownKey && isFirstLoading) {
                    if (toast == null)
                        toast = TimerToast.makeText(TVBuyActivity.this, "[上下键]可切换视频", 5 * 1000);
                    toast.show();
                    isFirstLoading = false;
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        yg_video_info_prompt.startAnimation(animation);
    }

    @Override
    public boolean handleAsrResult(String key) {
        if (key.contains("下一个")) {
            autoChangeLive();
        } else if (key.contains("上一个")) {
            UpDownKeyChangeLive(false);
        }
        return true;
    }

    private class GetYGVideoItemsListener extends BizRequestListener<List<YGAcrVideoItem>> {

        private String liveId;

        private GetYGVideoItemsListener(WeakReference<BaseActivity> baseActivityRef, String liveId) {
            super(baseActivityRef);
            this.liveId = liveId;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(List<YGAcrVideoItem> data) {
            AppDebug.e(TAG, "GetYGVideoItemsListener data size : " + data.size());
            if (acrDataMap == null)
                acrDataMap = new HashMap<>();

            acrDataMap.put(liveId, data);
            AnalysisData(data);
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    private class GetYGAttachInfoListener extends BizRequestListener<YGAttachInfo> {

        private GetYGAttachInfoListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(YGAttachInfo data) {
            if (data == null)
                return;

            if (mcurrentYGVideoInfo == null) {
                AppDebug.e(TAG, "GetYGAttachInfoListener liveUrl " + data.getVideoUrl());
                String liveUrl = data.getVideoUrl();
                initVideoView(liveUrl);
//                mcurrentYGVideoInfo = data;
            }

            openAngleTag(0, data.getTid(), null, data.getHotline(), data.getThirdItemId());
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case PowerMsgType.LIVE_LOADING_NOTIFY:
                hiddenCover();
                break;
            case YGVIDEO_ACR_DATA_NOTIFY:
                Bundle bundle = msg.getData();
                openAngleTag(1, bundle.getString("tid"), bundle.getString("duration"),
                        bundle.getString("hotline"), bundle.getString("thirdItemId"));
                break;
        }
        return false;
    }

    private void openAngleTag(int type, String tid, String duration, String hotline, String thirdItemId) {
        AppDebug.d(TAG, "handle Message start === tid = " + tid + "   ,duration = " + duration);
        AppDebug.d(TAG, "mLive_Flag  " + type + "   phoneNumber  :  " + hotline + "thirdItemid : " + thirdItemId);
//        tvDialogManager.setPhoneAndNumber(hotline, thirdItemId);
//        tvDialogManager.mLive_Flag = type;
//        if (duration == null) {
//            tvDialogManager.Running_Adv(tid, 0);
//            //tvDialogManager.getItemDetailV5(tid);
//        } else {
//            tvDialogManager.Running_Adv(tid, Long.parseLong(duration));
//        }
        AppDebug.d(TAG, "handle Message tid = " + tid + "   ,duration = " + duration + "  ,hotline = " + hotline + "  ,thirdItemId = " + thirdItemId);
    }

//    @Override
//    public void onFragmentInteraction(int taskId) {
//        AppDebug.d(TAG, "taskId: " + taskId);
//    }


}
