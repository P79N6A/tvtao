package com.yunos.tvtaobao.tvlive.view.media;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.yunos.tv.core.common.AppDebug;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.MediaInfo;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;

/**
 * Created by zhujun on 10/04/2017.
 */

public class IjkMediaPlayerWrapper implements IMediaPlayer {
    private IjkMediaPlayer mPlayer;

    private final static Object sInitLock = new Object();

    private static IjkMediaPlayerWrapper instance;

    private static boolean supportH264 = false;

    public static IjkMediaPlayerWrapper getInstance(boolean supportH264) {
        IjkMediaPlayerWrapper.supportH264 = supportH264;
        if (instance == null) {
            synchronized (sInitLock) {
                if (instance == null)
                    instance = new IjkMediaPlayerWrapper();
            }
        }
        return instance;
    }

    private IjkMediaPlayerWrapper() {
        init();
    }

    private void init() {
        AppDebug.d("test", "IjkMediaPlayerWrapper init " + this);
        mPlayer = new IjkMediaPlayer();
        mPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_SILENT);

        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", supportH264 ? 1 : 0);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 0);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 0);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);

        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);

    }


    @Override
    public void setDisplay(SurfaceHolder surfaceHolder) {
        mPlayer.setDisplay(surfaceHolder);
    }

    @Override
    public void setDataSource(Context context, Uri uri) throws
            IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        reset();
        mPlayer.setDataSource(context, uri);
    }

    @Override
    public void setDataSource(Context context, Uri uri, Map<String, String> map) throws
            IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        reset();
        mPlayer.setDataSource(context, uri, map);
    }

    @Override
    public void setDataSource(FileDescriptor fileDescriptor) throws
            IOException, IllegalArgumentException, IllegalStateException {
        reset();
        mPlayer.setDataSource(fileDescriptor);
    }

    @Override
    public void setDataSource(String s) throws
            IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        reset();
        mPlayer.setDataSource(s);
    }

    @Override
    public String getDataSource() {
        return mPlayer.getDataSource();
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        mPlayer.prepareAsync();
    }

    @Override
    public void start() throws IllegalStateException {
        AppDebug.d("test", "mPlayer start");
        mPlayer.start();
    }

    @Override
    public void stop() throws IllegalStateException {
        mPlayer.stop();
    }

    @Override
    public void pause() throws IllegalStateException {
        mPlayer.pause();
    }

    @Override
    public void setScreenOnWhilePlaying(boolean b) {
        mPlayer.setScreenOnWhilePlaying(b);
    }

    @Override
    public int getVideoWidth() {
        return mPlayer.getVideoWidth();
    }

    @Override
    public int getVideoHeight() {
        return mPlayer.getVideoHeight();
    }

    @Override
    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    @Override
    public void seekTo(long l) throws IllegalStateException {
        mPlayer.seekTo(l);
    }

    @Override
    public long getCurrentPosition() {
        return mPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return mPlayer.getDuration();
    }

    @Override
    public void release() {
        //TODO
        mPlayer.release();
    }

    @Override
    public void reset() {
        mPlayer.reset();
        mPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_SILENT);

        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", supportH264 ? 1 : 0);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 0);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 0);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);

        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
        mPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
    }

    @Override
    public void setVolume(float v, float v1) {
        mPlayer.setVolume(v, v1);
    }

    @Override
    public int getAudioSessionId() {
        return mPlayer.getAudioSessionId();
    }

    @Override
    public MediaInfo getMediaInfo() {
        return mPlayer.getMediaInfo();
    }

    @Override
    public void setLogEnabled(boolean b) {
        mPlayer.setLogEnabled(b);
    }

    @Override
    public boolean isPlayable() {
        return mPlayer.isPlayable();
    }

    @Override
    public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
        mPlayer.setOnPreparedListener(onPreparedListener);
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener onCompletionListener) {
        mPlayer.setOnCompletionListener(onCompletionListener);
    }

    @Override
    public void setOnBufferingUpdateListener(OnBufferingUpdateListener
                                                     onBufferingUpdateListener) {
        mPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
    }

    @Override
    public void setOnSeekCompleteListener(OnSeekCompleteListener onSeekCompleteListener) {
        mPlayer.setOnSeekCompleteListener(onSeekCompleteListener);
    }

    @Override
    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener
                                                      onVideoSizeChangedListener) {
        mPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
    }

    @Override
    public void setOnErrorListener(OnErrorListener onErrorListener) {
        mPlayer.setOnErrorListener(onErrorListener);
    }

    @Override
    public void setOnInfoListener(OnInfoListener onInfoListener) {
        mPlayer.setOnInfoListener(onInfoListener);
    }

//    @Override
//    public void setOnTimedTextListener(IMediaPlayer.OnTimedTextListener onTimedTextListener) {
//        mPlayer.setOnTimedTextListener(onTimedTextListener);
//    }

    @Override
    public void setAudioStreamType(int i) {
        mPlayer.setAudioStreamType(i);
    }

    @Override
    public void setKeepInBackground(boolean b) {
        mPlayer.setKeepInBackground(b);
    }

    @Override
    public int getVideoSarNum() {
        return mPlayer.getVideoSarNum();
    }

    @Override
    public int getVideoSarDen() {
        return mPlayer.getVideoSarDen();
    }

    @Override
    public void setWakeMode(Context context, int i) {
        mPlayer.setWakeMode(context, i);
    }

    @Override
    public void setLooping(boolean b) {
        mPlayer.setLooping(b);
    }

    @Override
    public boolean isLooping() {
        return mPlayer.isLooping();
    }

    @Override
    public ITrackInfo[] getTrackInfo() {
        return mPlayer.getTrackInfo();
    }

    @Override
    public void setSurface(Surface surface) {
        mPlayer.setSurface(surface);
    }

    @Override
    public void setDataSource(IMediaDataSource iMediaDataSource) {
        mPlayer.setDataSource(iMediaDataSource);
    }
}
