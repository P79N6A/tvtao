package com.media.ijkmediaplayer;

/**
 * Created by wuhaoteng on 2018/9/15.
 * 视频播放参数配置
 */

class Settings {
    public boolean getEnableDetachedSurfaceTextureView() {
        return false;
    }

    public String getPixelFormat() {
        return "";
    }

    public boolean getUsingOpenSLES() {
        return true;
    }

    public boolean getMediaCodecHandleResolutionChange() {
        return true;
    }

    public boolean getUsingMediaCodecAutoRotate() {
        return true;
    }

    public boolean getUsingMediaCodec() {
        return true;
    }

    public boolean getUsingMediaDataSource() {
        return false;
    }
}
