package com.yunos.tvtaobao.answer.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.media.ijkmediaplayer.AndroidMediaController;
import com.media.ijkmediaplayer.IjkVideoView;
import com.yunos.tvtaobao.answer.R;
import com.yunos.tv.core.common.AppDebug;

import java.lang.ref.WeakReference;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * <pre>
 *     author : pan
 *     QQ : 1065475118
 *     time   : 2018/01/10
 *     desc   : 直播问答项目
 *              前端进行答题的形式，客户端以一种浮层的形式去播放直播。
 *     version: 1.0
 * </pre>
 */

public class HQVideoDialog extends Dialog {
    private static String TAG = "HQVideoDialog";
    private RelativeLayout mLayout;
    private IjkVideoView ijkVideoView;
    private AndroidMediaController mediaController;
    private VideoHandler videoHandler;
    private String currentUrl = null;

    public HQVideoDialog(@NonNull Context context) {
        this(context, R.style.VideoDialog);
    }

    public HQVideoDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        WindowManager.LayoutParams l = getWindow().getAttributes();
        //0.0f完全不暗，即背景是可见的 ，1.0f时候，背景全部变黑暗。
        l.dimAmount = 0.0f;
        l.gravity = Gravity.LEFT | Gravity.TOP;
        //设置背景全部变暗的效果
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        getWindow().setAttributes(l);

        this.setContentView(R.layout.dialog_hq_video);
        initView();
    }

    private void initView() {
        mLayout = (RelativeLayout) findViewById(R.id.hqlive_layout_rl);
        ijkVideoView = (IjkVideoView) findViewById(R.id.hqlive_videoview);
        mediaController = (AndroidMediaController) findViewById(R.id.hqlive_media_controller);
        ijkVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                AppDebug.d(TAG, "onPrepared  iMediaPlayer : " + iMediaPlayer);
                if (mediaController.getVisibility() == View.VISIBLE) {
                    mediaController.setVisibility(View.GONE);
                }
            }
        });

        ijkVideoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int i1) {
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

        ijkVideoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                AppDebug.d(TAG, "onError iMediaPlayer : " + iMediaPlayer + " ,i : " + i + " ,i1 : " + i1);
                if (videoHandler == null) {
                    videoHandler = new VideoHandler(new WeakReference<HQVideoDialog>(HQVideoDialog.this));
                }
                videoHandler.sendEmptyMessageDelayed(0, 5 * 1000);
                return false;
            }
        });
    }

    public void play(String url) {
        AppDebug.i(TAG, TAG + ".play url : " + url);
        this.currentUrl = url;
        ijkVideoView.setVideoPath(url);
        ijkVideoView.start();
    }

    @Override
    public void dismiss() {
        ijkVideoView.release();
        if (videoHandler != null) {
            videoHandler.removeCallbacksAndMessages(null);
            videoHandler = null;
        }
        super.dismiss();
    }

    /**
     * 网页通信，获取设置视频宽高
     *
     */
    public void setVideoAreaSizeForJS(int width, int height, int marginTop, int marginLeft) {
        int mnormalVideoWidth = compatiblePx(width * 2 / 3);
        int mnormalVideoHight = compatiblePx(height * 2 / 3);
        int mnormalVideoMarginLeft = compatiblePx(marginLeft * 2 / 3);
        int mnormalVideoMarginTop = compatiblePx(marginTop * 2 / 3);
        setVideoAreaSize(mnormalVideoWidth, mnormalVideoHight, mnormalVideoMarginTop, mnormalVideoMarginLeft);
    }

    private void setVideoAreaSize(int width, int height, int marginTop, int marginLeft) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mLayout.getLayoutParams();
        lp.gravity = Gravity.LEFT | Gravity.TOP;
        lp.width = width;
        lp.height = height;
        lp.leftMargin = marginLeft;
        lp.topMargin = marginTop;

        mLayout.setLayoutParams(lp);
    }

    private int compatiblePx(int defaultPx) {
        int widthPixels = getContext().getResources().getDisplayMetrics().widthPixels;
        if (widthPixels == 1280) {
            return defaultPx;
        } else {
            float scale = widthPixels / 1280f;
            return (int) (defaultPx * scale + 0.5f);
        }
    }

    private static class VideoHandler extends Handler {

        private WeakReference<HQVideoDialog> hqVideoDialogWR;
        public VideoHandler(WeakReference<HQVideoDialog> hqVideoDialogWR) {
            this.hqVideoDialogWR = hqVideoDialogWR;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AppDebug.i(TAG, "Handler hqVideoDialogWR : " + hqVideoDialogWR + " ,hqVideoDialog : " + hqVideoDialogWR.get());
            if (hqVideoDialogWR != null && hqVideoDialogWR.get() != null) {
                HQVideoDialog hqVideoDialog = hqVideoDialogWR.get();
                hqVideoDialog.play(hqVideoDialog.currentUrl);
            }
        }
    }
}
