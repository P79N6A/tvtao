//package com.yunos.tvtaobao.biz.widget;
//
//
//import android.content.Context;
//import android.content.Intent;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.media.MediaPlayer.OnCompletionListener;
//import android.media.MediaPlayer.OnErrorListener;
//import android.media.MediaPlayer.OnInfoListener;
//import android.net.Uri;
//import android.os.Handler;
//import android.os.Message;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.KeyEvent;
//import android.view.MotionEvent;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.accessibility.AccessibilityEvent;
//import android.view.accessibility.AccessibilityNodeInfo;
//import android.widget.MediaController;
//import android.widget.MediaController.MediaPlayerControl;
//import android.widget.VideoView;
//
//import com.yunos.tv.core.common.AppDebug;
//
//import java.io.IOException;
//import java.lang.ref.WeakReference;
//import java.lang.reflect.Method;
//import java.util.Map;
//
///**
// * 复制了系统的VideoView的代码
// * 1.release做了try catch
// * 2.onWindowVisibilityChanged 做了忽略
// */
//public class TvtaobaoSystemVideoView extends SurfaceView implements MediaPlayerControl {
//
//    private static final String TAG = "CoverFlowVideoView";
//    private boolean mIgnoreChanged;
//    // settable by the client
////    private Uri         mUri;
//    private String         mUri;
//    private Map<String, String> mHeaders;
//    private int         mDuration;
//    // MetaData属性
//    public static final int PAUSE_AVAILABLE = 1; // Boolean
//    public static final int SEEK_BACKWARD_AVAILABLE = 2; // Boolean
//    public static final int SEEK_FORWARD_AVAILABLE = 3; // Boolean
//    public static final int SEEK_AVAILABLE = 4; // Boolean
//
//    public static final int SURFACE_STATE_UNINIT = -1;
//    public static final int SURFACE_STATE_CREATED = 0;
//    public static final int SURFACE_STATE_CHANGED = 1;
//    public static final int SURFACE_STATE_DESTROYED = 2;
//
//    // all possible internal states
//    private static final int STATE_ERROR              = -1;
//    private static final int STATE_IDLE               = 0;
//    private static final int STATE_PREPARING          = 1;
//    private static final int STATE_PREPARED           = 2;
//    private static final int STATE_PLAYING            = 3;
//    private static final int STATE_PAUSED             = 4;
//    private static final int STATE_PLAYBACK_COMPLETED = 5;
//
//    // MediaPlayer属性
//    private static final boolean METADATA_ALL = false;
//    private static final boolean BYPASS_METADATA_FILTER = false;
//
//    // mCurrentState is a VideoView object's current state.
//    // mTargetState is the state that a method caller intends to reach.
//    // For instance, regardless the VideoView object's current state,
//    // calling pause() intends to bring the object to a target state
//    // of STATE_PAUSED.
//    private int mCurrentState = STATE_IDLE;
//    private int mTargetState  = STATE_IDLE;
//
//    // All the stuff we need for playing and showing a video
//    private SurfaceHolder mSurfaceHolder = null;
//    private MediaPlayer mMediaPlayer = null;
//    private int         mVideoWidth;
//    private int         mVideoHeight;
//    private int         mSurfaceWidth;
//    private int         mSurfaceHeight;
//    private MediaController mMediaController;
//    private OnCompletionListener mOnCompletionListener;
//    private MediaPlayer.OnPreparedListener mOnPreparedListener;
//    private int         mCurrentBufferPercentage;
//    private OnErrorListener mOnErrorListener;
//    private OnInfoListener  mOnInfoListener;
//    private int         mSeekWhenPrepared;  // recording the seek position while preparing
//    private boolean     mCanPause;
//    private boolean     mCanSeekBack;
//    private boolean     mCanSeekForward;
//    private RetryHandler mRetryHandler;
//    public TvtaobaoSystemVideoView(Context context) {
//        super(context);
//        initVideoView();
//    }
//
//    public TvtaobaoSystemVideoView(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//        initVideoView();
//    }
//
//    public TvtaobaoSystemVideoView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        initVideoView();
//    }
//
//    public void setIgnoreChangedView(boolean ignore) {
//        mIgnoreChanged = ignore;
//    }
//
//    @Override
//    protected void onWindowVisibilityChanged(int visibility) {
//        AppDebug.i(TAG, "view onWindowVisibilityChanged=" + visibility + " mIgnoreChanged=" + mIgnoreChanged);
//        if (!mIgnoreChanged) {
//            super.onWindowVisibilityChanged(visibility);
//        }
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        AppDebug.i(TAG, "view onDetachedFromWindow mIgnoreChanged=" + mIgnoreChanged);
//        if (!mIgnoreChanged) {
//            super.onDetachedFromWindow();
//        }
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        //Log.i("@@@@", "onMeasure");
//        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
//        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
//        if (mVideoWidth > 0 && mVideoHeight > 0) {
//            if ( mVideoWidth * height  > width * mVideoHeight ) {
//                //Log.i("@@@", "image too tall, correcting");
//                height = width * mVideoHeight / mVideoWidth;
//            } else if ( mVideoWidth * height  < width * mVideoHeight ) {
//                //Log.i("@@@", "image too wide, correcting");
//                width = height * mVideoWidth / mVideoHeight;
//            } else {
//                //Log.i("@@@", "aspect ratio is correct: " +
//                        //width+"/"+height+"="+
//                        //mVideoWidth+"/"+mVideoHeight);
//            }
//        }
//        //Log.i("@@@@@@@@@@", "setting size: " + width + 'x' + height);
//        setMeasuredDimension(width, height);
//    }
//
//    @Override
//    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
//        super.onInitializeAccessibilityEvent(event);
//        event.setClassName(VideoView.class.getName());
//    }
//
//    @Override
//    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
//        super.onInitializeAccessibilityNodeInfo(info);
//        info.setClassName(VideoView.class.getName());
//    }
//
//    public int resolveAdjustedSize(int desiredSize, int measureSpec) {
//        int result = desiredSize;
//        int specMode = MeasureSpec.getMode(measureSpec);
//        int specSize =  MeasureSpec.getSize(measureSpec);
//
//        switch (specMode) {
//            case MeasureSpec.UNSPECIFIED:
//                /* Parent says we can be as big as we want. Just don't be larger
//                 * than max size imposed on ourselves.
//                 */
//                result = desiredSize;
//                break;
//
//            case MeasureSpec.AT_MOST:
//                /* Parent says we can be as big as we want, up to specSize.
//                 * Don't be larger than specSize, and don't be larger than
//                 * the max size imposed on ourselves.
//                 */
//                result = Math.min(desiredSize, specSize);
//                break;
//
//            case MeasureSpec.EXACTLY:
//                // No choice. Do what we are told.
//                result = specSize;
//                break;
//        }
//        return result;
//}
//
//    private void initVideoView() {
//        mVideoWidth = 0;
//        mVideoHeight = 0;
//        getHolder().addCallback(mSHCallback);
//        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        setFocusable(true);
//        setFocusableInTouchMode(true);
//        requestFocus();
//        mCurrentState = STATE_IDLE;
//        mTargetState  = STATE_IDLE;
//        mRetryHandler = new RetryHandler(this);
//    }
//
//    public void setRetryCount(int count) {
//        mRetryHandler.setRetryCount(count);
//    }
//
//    public void setVideoPath(String path) {
//        mUri = path;
//        mHeaders = null;
//        mSeekWhenPrepared = 0;
//        openVideo();
//        requestLayout();
//        invalidate();
//    }
//
//    public void startPlay() {
//        mSeekWhenPrepared = 0;
//        openVideo();
//        requestLayout();
//        invalidate();
//    }
//
//    /**
//     * @hide
//     */
//    public void setVideoURI(Uri uri, Map<String, String> headers) {
////        mUri = uri;
////        mHeaders = headers;
////        mSeekWhenPrepared = 0;
////        openVideo();
////        requestLayout();
////        invalidate();
//    }
//
//    public void stopPlayback() {
//        long start = System.currentTimeMillis();
//        AppDebug.i(TAG, "stopPlayback");
//        mRetryHandler.stopRetry();
//        if (mMediaPlayer != null) {
//            mMediaPlayer.stop();
//            mMediaPlayer.release();
//            mMediaPlayer = null;
//            mCurrentState = STATE_IDLE;
//            mTargetState  = STATE_IDLE;
//        }
//        AppDebug.i(TAG, "stopPlayback end time="+(System.currentTimeMillis() - start));
//    }
//
//    public void openVideo() {
//
//        AppDebug.i(TAG, "openVideo  --> mUri = " + mUri + "; mSurfaceHolder = " + mSurfaceHolder);
//
//        long start = System.currentTimeMillis();
//        if (mUri == null || mSurfaceHolder == null) {
//            // not ready for playback just yet, will try again later
//            return;
//        }
//        // Tell the music playback service to pause
//        // TODO: these constants need to be published somewhere in the framework.
//        Intent i = new Intent("com.android.music.musicservicecommand");
//        i.putExtra("command", "pause");
//        getContext().sendBroadcast(i);
//
//        // we shouldn't clear the target state, because somebody might have
//        // called start() previously
//        release(false);
//        try {
//            mMediaPlayer = new MediaPlayer();
//            mMediaPlayer.setOnPreparedListener(mPreparedListener);
//            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
//            mDuration = -1;
//            mMediaPlayer.setOnCompletionListener(mCompletionListener);
//            mMediaPlayer.setOnErrorListener(mErrorListener);
//            mMediaPlayer.setOnInfoListener(mOnInfoListener);
//            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
//            mCurrentBufferPercentage = 0;
//            mMediaPlayer.setDataSource(mUri);
//            mMediaPlayer.setDisplay(mSurfaceHolder);
//            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mMediaPlayer.setScreenOnWhilePlaying(true);
//            mMediaPlayer.prepareAsync();
//            // we don't set the target state here either, but preserve the
//            // target state that was there before.
//            mCurrentState = STATE_PREPARING;
//            attachMediaController();
//        } catch (IOException ex) {
//            Log.w(TAG, "Unable to open content: " + mUri, ex);
//            mCurrentState = STATE_ERROR;
//            mTargetState = STATE_ERROR;
//            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
//            return;
//        } catch (IllegalArgumentException ex) {
//            Log.w(TAG, "Unable to open content: " + mUri, ex);
//            mCurrentState = STATE_ERROR;
//            mTargetState = STATE_ERROR;
//            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
//            return;
//        }
//        AppDebug.i(TAG, "openVideo end time="+(System.currentTimeMillis() - start));
//
//    }
//
//    public void setMediaController(MediaController controller) {
//        if (mMediaController != null) {
//            mMediaController.hide();
//        }
//        mMediaController = controller;
//        attachMediaController();
//    }
//
//    private void attachMediaController() {
//        if (mMediaPlayer != null && mMediaController != null) {
//            mMediaController.setMediaPlayer(this);
//            View anchorView = this.getParent() instanceof View ?
//                    (View)this.getParent() : this;
//            mMediaController.setAnchorView(anchorView);
//            mMediaController.setEnabled(isInPlaybackState());
//        }
//    }
//
//    MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener =
//        new MediaPlayer.OnVideoSizeChangedListener() {
//            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
//                mVideoWidth = mp.getVideoWidth();
//                mVideoHeight = mp.getVideoHeight();
//                if (mVideoWidth != 0 && mVideoHeight != 0) {
//                    getHolder().setFixedSize(mVideoWidth, mVideoHeight);
//                    requestLayout();
//                }
//            }
//    };
//
//    MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
//        public void onPrepared(MediaPlayer mp) {
//            mCurrentState = STATE_PREPARED;
//
//            // Get the capabilities of the player for this stream
//            try {
//                Method methodMeta = mp.getClass().getMethod("getMetadata", boolean.class, boolean.class);
//                Object objMeta = methodMeta.invoke(mp, METADATA_ALL, BYPASS_METADATA_FILTER);
//
//                if (objMeta != null) {
//                    Class clsMeta = objMeta.getClass();
//                    Method mHas = clsMeta.getMethod("has", int.class);
//                    Method mGetBoolean = clsMeta.getMethod("getBoolean", int.class);
//
//                    mCanPause = !(Boolean) mHas.invoke(objMeta, PAUSE_AVAILABLE) || (Boolean) mGetBoolean.invoke(objMeta, PAUSE_AVAILABLE);
//                    mCanSeekBack = !(Boolean) mHas.invoke(objMeta, SEEK_BACKWARD_AVAILABLE)
//                            || (Boolean) mGetBoolean.invoke(objMeta, SEEK_BACKWARD_AVAILABLE);
//                    mCanSeekForward = !(Boolean) mHas.invoke(objMeta, SEEK_FORWARD_AVAILABLE)
//                            || (Boolean) mGetBoolean.invoke(objMeta, SEEK_FORWARD_AVAILABLE);
//
//                    Log.i(TAG, "objMeta:" + (objMeta == null) + ",mCanPause:" + mCanPause + ",mCanSeekBack:" + mCanSeekBack
//                            + ",mCanSeekForward:" + mCanSeekForward);
//                } else {
//                    mCanPause = mCanSeekBack = mCanSeekForward = true;
//                }
//
//            } catch (Exception e) {
//                mCanPause = mCanSeekBack = mCanSeekForward = true;
////                e.printStackTrace();
//            }
//
//            if (mOnPreparedListener != null) {
//                mOnPreparedListener.onPrepared(mMediaPlayer);
//            }
//            if (mMediaController != null) {
//                mMediaController.setEnabled(true);
//            }
//            mVideoWidth = mp.getVideoWidth();
//            mVideoHeight = mp.getVideoHeight();
//
//            int seekToPosition = mSeekWhenPrepared;  // mSeekWhenPrepared may be changed after seekTo() call
//            if (seekToPosition != 0) {
//                seekTo(seekToPosition);
//            }
//            if (mVideoWidth != 0 && mVideoHeight != 0) {
//                //Log.i("@@@@", "video size: " + mVideoWidth +"/"+ mVideoHeight);
//                getHolder().setFixedSize(mVideoWidth, mVideoHeight);
//                if (mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
//                    // We didn't actually change the size (it was already at the size
//                    // we need), so we won't get a "surface changed" callback, so
//                    // start the video here instead of in the callback.
//                    if (mTargetState == STATE_PLAYING) {
//                        start();
//                        if (mMediaController != null) {
//                            mMediaController.show();
//                        }
//                    } else if (!isPlaying() &&
//                               (seekToPosition != 0 || getCurrentPosition() > 0)) {
//                       if (mMediaController != null) {
//                           // Show the media controls when we're paused into a video and make 'em stick.
//                           mMediaController.show(0);
//                       }
//                   }
//                }
//            } else {
//                // We don't know the video size yet, but should start anyway.
//                // The video size might be reported to us later.
//                if (mTargetState == STATE_PLAYING) {
//                    start();
//                }
//            }
//        }
//    };
//
//    private OnCompletionListener mCompletionListener =
//        new OnCompletionListener() {
//        public void onCompletion(MediaPlayer mp) {
//            mRetryHandler.stopRetry();
//            mCurrentState = STATE_PLAYBACK_COMPLETED;
//            mTargetState = STATE_PLAYBACK_COMPLETED;
//            if (mMediaController != null) {
//                mMediaController.hide();
//            }
//            if (mOnCompletionListener != null) {
//                mOnCompletionListener.onCompletion(mMediaPlayer);
//            }
//        }
//    };
//
//    private OnErrorListener mErrorListener =
//        new OnErrorListener() {
//        public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
//            Log.d(TAG, "Error: " + framework_err + "," + impl_err);
//            mCurrentState = STATE_ERROR;
//            mTargetState = STATE_ERROR;
//            if (mRetryHandler.startRetry()) {
//                return true;
//            }
//            if (mMediaController != null) {
//                mMediaController.hide();
//            }
//
//            /* If an error handler has been supplied, use it and finish. */
//            if (mOnErrorListener != null) {
//                if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
//                    return true;
//                }
//            }
//            return true;
//        }
//    };
//
//    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener =
//        new MediaPlayer.OnBufferingUpdateListener() {
//        public void onBufferingUpdate(MediaPlayer mp, int percent) {
//            mCurrentBufferPercentage = percent;
//        }
//    };
//
//    /**
//     * Register a callback to be invoked when the media file
//     * is loaded and ready to go.
//     *
//     * @param l The callback that will be run
//     */
//    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l)
//    {
//        mOnPreparedListener = l;
//    }
//
//    /**
//     * Register a callback to be invoked when the end of a media file
//     * has been reached during playback.
//     *
//     * @param l The callback that will be run
//     */
//    public void setOnCompletionListener(OnCompletionListener l)
//    {
//        mOnCompletionListener = l;
//    }
//
//    /**
//     * Register a callback to be invoked when an error occurs
//     * during playback or setup.  If no listener is specified,
//     * or if the listener returned false, VideoView will inform
//     * the user of any errors.
//     *
//     * @param l The callback that will be run
//     */
//    public void setOnErrorListener(OnErrorListener l)
//    {
//        mOnErrorListener = l;
//    }
//
//    /**
//     * Register a callback to be invoked when an informational event
//     * occurs during playback or setup.
//     *
//     * @param l The callback that will be run
//     */
//    public void setOnInfoListener(OnInfoListener l) {
//        mOnInfoListener = l;
//    }
//
//    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback()
//    {
//        public void surfaceChanged(SurfaceHolder holder, int format,
//                                    int w, int h)
//        {
//            mSurfaceWidth = w;
//            mSurfaceHeight = h;
//            boolean isValidState =  (mTargetState == STATE_PLAYING);
//            boolean hasValidSize = (mVideoWidth == w && mVideoHeight == h);
//            if (mMediaPlayer != null && isValidState && hasValidSize) {
//                if (mSeekWhenPrepared != 0) {
//                    seekTo(mSeekWhenPrepared);
//                }
//                start();
//            }
//        }
//
//        public void surfaceCreated(SurfaceHolder holder)
//        {
//            mSurfaceHolder = holder;
//            AppDebug.i(TAG, "surfaceCreated");
//            mRetryHandler.stopRetry();
//            openVideo();
//        }
//
//        public void surfaceDestroyed(SurfaceHolder holder)
//        {
//            // after we return from this we can't use the surface any more
//            mSurfaceHolder = null;
//            if (mMediaController != null) mMediaController.hide();
//            AppDebug.i(TAG, "surfaceDestroyed");
//            mRetryHandler.stopRetry();
//            release(true);
//        }
//    };
//
//    /*
//     * release the media player in any state
//     */
//    private void release(boolean cleartargetstate) {
//        long start = System.currentTimeMillis();
//        if (mMediaPlayer != null) {
//            try {
//                mMediaPlayer.reset();
//                mMediaPlayer.release();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            mMediaPlayer = null;
//            mCurrentState = STATE_IDLE;
//            if (cleartargetstate) {
//                mTargetState  = STATE_IDLE;
//            }
//        }
//        AppDebug.i(TAG, "release end time="+(System.currentTimeMillis() - start));
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        if (isInPlaybackState() && mMediaController != null) {
//            toggleMediaControlsVisiblity();
//        }
//        return false;
//    }
//
//    @Override
//    public boolean onTrackballEvent(MotionEvent ev) {
//        if (isInPlaybackState() && mMediaController != null) {
//            toggleMediaControlsVisiblity();
//        }
//        return false;
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event)
//    {
//        boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK &&
//                                     keyCode != KeyEvent.KEYCODE_VOLUME_UP &&
//                                     keyCode != KeyEvent.KEYCODE_VOLUME_DOWN &&
//                                     keyCode != KeyEvent.KEYCODE_VOLUME_MUTE &&
//                                     keyCode != KeyEvent.KEYCODE_MENU &&
//                                     keyCode != KeyEvent.KEYCODE_CALL &&
//                                     keyCode != KeyEvent.KEYCODE_ENDCALL;
//        if (isInPlaybackState() && isKeyCodeSupported && mMediaController != null) {
//            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK ||
//                    keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
//                if (mMediaPlayer.isPlaying()) {
//                    pause();
//                    mMediaController.show();
//                } else {
//                    start();
//                    mMediaController.hide();
//                }
//                return true;
//            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
//                if (!mMediaPlayer.isPlaying()) {
//                    start();
//                    mMediaController.hide();
//                }
//                return true;
//            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
//                    || keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
//                if (mMediaPlayer.isPlaying()) {
//                    pause();
//                    mMediaController.show();
//                }
//                return true;
//            } else {
//                toggleMediaControlsVisiblity();
//            }
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    public int getAudioSessionId() {
//        return 0;
//    }
//
//    private void toggleMediaControlsVisiblity() {
//        if (mMediaController.isShowing()) {
//            mMediaController.hide();
//        } else {
//            mMediaController.show();
//        }
//    }
//
//    public void start() {
//        AppDebug.i(TAG, "start");
//        mRetryHandler.stopRetry();
//        if (isInPlaybackState()) {
//            mMediaPlayer.start();
//            mCurrentState = STATE_PLAYING;
//        }
//        mTargetState = STATE_PLAYING;
//    }
//
//    public void pause() {
//        long start = System.currentTimeMillis();
//        AppDebug.i(TAG, "pause");
//        mRetryHandler.stopRetry();
//        if (isInPlaybackState()) {
//            if (mMediaPlayer.isPlaying()) {
//                mMediaPlayer.pause();
//                mCurrentState = STATE_PAUSED;
//            }
//        }
//        mTargetState = STATE_PAUSED;
//        AppDebug.i(TAG, "pause end time="+(System.currentTimeMillis() - start));
//
//    }
//
//    public void suspend() {
//        AppDebug.i(TAG, "suspend");
//        mRetryHandler.stopRetry();
//        release(false);
//    }
//
//    public void resume() {
//        AppDebug.i(TAG, "resume");
//        mRetryHandler.stopRetry();
//        openVideo();
//    }
//
//    // cache duration as mDuration for faster access
//    public int getDuration() {
//        if (isInPlaybackState()) {
//            if (mDuration > 0) {
//                return mDuration;
//            }
//            mDuration = mMediaPlayer.getDuration();
//            return mDuration;
//        }
//        mDuration = -1;
//        return mDuration;
//    }
//
//    public int getCurrentPosition() {
//        if (isInPlaybackState()) {
//            return mMediaPlayer.getCurrentPosition();
//        }
//        return 0;
//    }
//
//    public void seekTo(int msec) {
//        AppDebug.i(TAG, "seekTo");
//        mRetryHandler.stopRetry();
//        if (isInPlaybackState()) {
//            mMediaPlayer.seekTo(msec);
//            mSeekWhenPrepared = 0;
//        } else {
//            mSeekWhenPrepared = msec;
//        }
//    }
//
//    public boolean isPlaying() {
//        return isInPlaybackState() && mMediaPlayer.isPlaying();
//    }
//
//    public int getBufferPercentage() {
//        if (mMediaPlayer != null) {
//            return mCurrentBufferPercentage;
//        }
//        return 0;
//    }
//
//    private boolean isInPlaybackState() {
//        return (mMediaPlayer != null &&
//                mCurrentState != STATE_ERROR &&
//                mCurrentState != STATE_IDLE &&
//                mCurrentState != STATE_PREPARING);
//    }
//
//    public boolean canPause() {
//        return mCanPause;
//    }
//
//    public boolean canSeekBackward() {
//        return mCanSeekBack;
//    }
//
//    public boolean canSeekForward() {
//        return mCanSeekForward;
//    }
//
//
//    private static class RetryHandler extends Handler {
//        private WeakReference<TvtaobaoSystemVideoView> mTvtaobaoSystemVideoViewRef;
//        private static final int DEFALUT_DELAY_RETRY_TIME = 1000;
//        private Handler mRetryHandler;
//        private int mRetryCount;
//        private int mRetryTotalCount;
//        RetryHandler(TvtaobaoSystemVideoView videoView){
//            mTvtaobaoSystemVideoViewRef = new WeakReference<TvtaobaoSystemVideoView>(videoView);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            AppDebug.i(TAG, "RetryHandler handleMessage mRetryCount="+mRetryCount+" mRetryTotalCount="+mRetryTotalCount);
//            if (msg.what == 0) {
//                TvtaobaoSystemVideoView videoView = mTvtaobaoSystemVideoViewRef.get();
//                if (videoView != null) {
//                    videoView.openVideo();
//                }
//                mRetryCount ++;
//            }
//            super.handleMessage(msg);
//        }
//
//        public void setRetryCount(int count){
//            AppDebug.i(TAG, "setRetryCount count="+count);
//            mRetryTotalCount = count;
//        }
//
//        public boolean startRetry(){
//            TvtaobaoSystemVideoView videoView = mTvtaobaoSystemVideoViewRef.get();
//            AppDebug.i(TAG, "startRetry mRetryCount="+mRetryCount+" mRetryTotalCount="+mRetryTotalCount+" videoView="+videoView);
//            if (videoView != null && mRetryCount < mRetryTotalCount) {
//                removeMessages(0);
//                sendEmptyMessageDelayed(0, DEFALUT_DELAY_RETRY_TIME);
//                return true;
//            }
//            return false;
//        }
//
//        public void stopRetry(){
//            AppDebug.i(TAG, "stopRetry");
//            removeMessages(0);
//            mRetryCount = 0;
//
//        }
//    }
//}
