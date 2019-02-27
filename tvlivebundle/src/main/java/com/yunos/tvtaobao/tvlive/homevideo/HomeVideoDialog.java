package com.yunos.tvtaobao.tvlive.homevideo;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.tvlive.R;
import com.yunos.tvtaobao.tvlive.bo.model.bean.YGVideoInfo;
import com.yunos.tvtaobao.tvlive.presenter.VideoDialogPresenter;
import com.yunos.tvtaobao.tvlive.presenter.impl.VideoDialogPresenterImpl;
import com.yunos.tvtaobao.tvlive.utils.Tools;
import com.yunos.tvtaobao.tvlive.view.IvideoDialog;
import com.yunos.tvtaobao.tvlive.view.media.AndroidMediaController;
import com.yunos.tvtaobao.tvlive.view.media.IjkVideoView;

import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by huangdaju on 17/7/19.
 */

public class HomeVideoDialog extends Dialog implements IvideoDialog {
    private final String TAG = HomeVideoDialog.class.getSimpleName();
    private final int TMALL_LIVE_READY = 0x0000001;
    private final int TMALL_LIVE_ERROR = 0x0000002;
    private final int TMALL_LIVE_END = 0x0000003;
    private final int TMALL_ERROR_NOTIFY_CODE = 0x0000004;
    private final int TBAO_LIVE_ON = 0x0000005;
    private final int TMALL_LIVE_ON = 0x0000006;
    private final int YANGGUANG_VIDEO_ON = 0x00000008;
    private final int LIVE_LOAD_STATE = 0x0000007;

    private IjkVideoView mVideoView;
    private ImageView loading_layout;
    private RelativeLayout mLayout;
    private AndroidMediaController mMediaController;


    private boolean isFirstLoad = true;
    private String liveUrl;

    private int mnormalVideoWidth;
    private int mnormalVideoHight;
    private int mnormalVideoMarginTop;
    private int mnormalVideoMarginLeft;
    public boolean isShowByJS = false;
    public static boolean tmallLiveError = false;
    public static boolean isError = false;
    public static int liveType = 3; //0 无视频,1 天猫直播,2 淘宝直播,3 央广购物

    private List<YGVideoInfo> mYGVideoList;

    private VideoDialogPresenter mVideoDialogPresenter;

    public HomeVideoDialog(@NonNull Context context) {
        this(context, R.style.VideoDialog);
    }

    public HomeVideoDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        WindowManager.LayoutParams l = getWindow().getAttributes();
        //0.0f完全不暗，即背景是可见的 ，1.0f时候，背景全部变黑暗。
        l.dimAmount = 0.0f;
        l.gravity = Gravity.LEFT | Gravity.TOP;
        //设置背景全部变暗的效果
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        getWindow().setAttributes(l);
        onInitVideoDialog(context);
    }

    public HomeVideoDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        onInitVideoDialog(context);
    }

    private void onInitVideoDialog(Context context) {
        this.setContentView(R.layout.view_video);
        mVideoView = (IjkVideoView) findViewById(R.id.vv_live);
        loading_layout = (ImageView) findViewById(R.id.loading_layout);
        mLayout = (RelativeLayout) findViewById(R.id.vv_live_lay);
        mMediaController = (AndroidMediaController) findViewById(R.id.media_controller);
        mVideoDialogPresenter = new VideoDialogPresenterImpl(context, this);


        mVideoView.setOnErrorListener(new tv.danmaku.ijk.media.player.IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(tv.danmaku.ijk.media.player.IMediaPlayer iMediaPlayer, int i, int i1) {
                AppDebug.e(TAG, " onErrorListener  " + iMediaPlayer);
//                if (liveType == 1) {
//                    if (mLiveDataController.getTMallItemsBean() != null) {
//                        long eTime = Long.parseLong(Tools.getTime(mLiveDataController.getTMallItemsBean().getEnd_time())) * 1000;
//                        //结束时间大于现在的时间,还没有播放结束
//                        AppDebug.e(TAG, " onErrorListener  " + (eTime > System.currentTimeMillis()) + "   isFirstLoad = " + isFirstLoad);
//                        if (eTime > System.currentTimeMillis() && isFirstLoad) {
//                            mHandler.sendEmptyMessageDelayed(TMALL_ERROR_NOTIFY_CODE, 20 * 1000);
//                            isFirstLoad = false;
//                            loading_layout.setVisibility(View.VISIBLE);
//                            loading_layout.setImageResource(R.drawable.live_anchor_back);
//                        } else {
//                            getMallLiveList();
//                            loading_layout.setVisibility(View.VISIBLE);
//                            loading_layout.setImageResource(R.drawable.live_end);
//                        }
//                    }
//                } else if (liveType == 3) {
//                    playNextVideo();
//                }
                return false;
            }
        });


        mVideoView.setOnPreparedListener(new tv.danmaku.ijk.media.player.IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(tv.danmaku.ijk.media.player.IMediaPlayer iMediaPlayer) {
                if (loading_layout != null)
                    loading_layout.setVisibility(View.GONE);
                if (mMediaController != null)
                    mMediaController.hide();
//                isFirstLoad = true;
//                if (mHandler != null)
//                    mHandler.removeMessages(LIVE_LOAD_STATE);
            }
        });

        mVideoView.setOnInfoListener(new tv.danmaku.ijk.media.player.IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(tv.danmaku.ijk.media.player.IMediaPlayer iMediaPlayer, int what, int i1) {
                boolean handled = false;
                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        AppDebug.d(TAG, "onInfo MediaPlayer.MEDIA_INFO_BUFFERING_START");
                        handled = true;
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        AppDebug.d(TAG, "onInfo MediaPlayer.MEDIA_INFO_BUFFERING_END");
                        handled = true;
                        break;
                }
                return handled;
            }
        });

        mVideoView.setOnCompletionListener(new tv.danmaku.ijk.media.player.IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(tv.danmaku.ijk.media.player.IMediaPlayer iMediaPlayer) {
                AppDebug.e(TAG, "onCompletion MediaPlayer.播放结束");
//                if (liveType == 1) {
//                    broadcastToBlize(TMALL_LIVE_END);
//                } else if (liveType == 3) {
//                    playNextVideo();
//                }
            }
        });
    }


    /**
     * 设置视频的位置和宽高
     *
     * @param width
     * @param height
     * @param marginTop
     * @param marginLeft
     */
    public void setVideoAreaSize(int width, int height, int marginTop, int marginLeft) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mLayout.getLayoutParams();
        lp.gravity = Gravity.LEFT | Gravity.TOP;
        lp.width = width;
        lp.height = height;
        lp.leftMargin = marginLeft;
        lp.topMargin = marginTop;

        mLayout.setLayoutParams(lp);
    }

    @Override
    public void stopVideo() {
        mVideoView.pause();
        mVideoView.switchOff();
    }

    @Override
    public void playVideo(int videoType, String url) {
        liveType = videoType;
        AppDebug.e(TAG, "showVideo liveType : " + liveType + ",liveUrl:" + url);
        mVideoView.switchOn();
        mVideoView.setVideoURI(Uri.parse(url));
        mVideoView.start();
    }

    @Override
    public void showError() {

    }

    @Override
    public void fullScreen(int liveType) {

    }


    /**
     * 网页设置视频宽高
     *
     * @param width
     * @param height
     * @param marginTop
     * @param marginLeft
     */
    @Override
    public void changeVideoSize(int width, int height, int marginTop, int marginLeft) {
        this.mnormalVideoWidth = Tools.compatiblePx(getContext(), width * 2 / 3);
        this.mnormalVideoHight = Tools.compatiblePx(getContext(), height * 2 / 3);
        this.mnormalVideoMarginLeft = Tools.compatiblePx(getContext(), marginLeft * 2 / 3);
        this.mnormalVideoMarginTop = Tools.compatiblePx(getContext(), marginTop * 2 / 3);
        setVideoAreaSize(mnormalVideoWidth, mnormalVideoHight, mnormalVideoMarginTop, mnormalVideoMarginLeft);
    }

    @Override
    public void changeLiveType(int liveType) {

    }

    @Override
    public void addYGVideoData(List<YGVideoInfo> data) {
        if (mYGVideoList == null) {
            mYGVideoList = data;
        } else {
            mYGVideoList.clear();
            mYGVideoList.addAll(data);
        }
    }
}
