package com.yunos.tvtaobao.tvshoppingbundle.bean;


import android.os.Bundle;
import android.text.TextUtils;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 边看边购接收到的广播数据
 * @author tingmeng.ytm
 */
@SuppressWarnings("unused")
public class TbTvShoppingReceiverData implements Serializable {

    private static final long serialVersionUID = -9197445884434379921L;

    private static String TAG = "TbTvShoppingReceiverData";
    //播放错误
    public static final int STATE_ERROR = -1;
    //播放前
    public static final int STATE_IDLE = 0;
    //播放准备中
    public static final int STATE_PREPARING = 1;
    //播放准备完成
    public static final int STATE_PREPARED = 2;
    //播放中
    public static final int STATE_PLAYING = 3;
    //暂停
    public static final int STATE_PAUSED = 4;
    //没用到过
    public static final int STATE_PLAYBACK_COMPLETED = 5;
    //在播放过程中加载中
    public static final int STATE_LOADING = 6;

    // activity显示
    public static final String STATE_ACTIVITY_RESUME = "onresume";
    // activity暂停
    public static final String STATE_ACTIVITY_PAUSE = "onpause";

    //视频类型，电影，电视剧，综艺，回看等
    public enum VideoPlayType {
        //未知
        NONE(0, "none"),
        //电影
        DIANYING(1, "dianying"),
        //综艺
        ZONGYI(2, "zongyi"),
        //资讯
        ZIXUN(3, "zixun"),
        //电视剧
        DIANSHIJU(4, "dianshiju"),
        //直播
        LIVE(5, "live"),
        //回看
        PLAYBACK(6, "playback");

        private int index;
        private String name;

        private static Map<Integer, VideoPlayType> protocols = new HashMap<Integer, VideoPlayType>();

        static {
            for (VideoPlayType type : VideoPlayType.values()) {
                protocols.put(type.getIndex(), type);
            }
        }

        VideoPlayType(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public static VideoPlayType valueOf(int index) {
            return protocols.get(index);
        }

        public int getIndex() {
            return index;
        }

        public String getName() {
            return name;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // 必选
    public static final String TAG_VIDEO_POSTION = "position";
    public static final String TAG_VIDEO_ID = "videoid"; //program id， channel key ,playback key
    public static final String TAG_VIDEO_NAME = "videoname";//program name, channel name, playback name
    public static final String TAG_TYPE = "type";//点播（电影，电视剧，综艺，资讯），直播,回看
    public static final String TAG_VIODE_SUB_ID = "viodesubid";//value // 字段拼写
    public static final String TAG_VIDEO_SUB_ID = "videosubid";//value //对字段拼写错误兼容
    public static final String TAG_VIDEO_SUB_NAME = "viodesubname"; //billname
    public static final String TAG_IS_FULLSCREEN = "isfullscreen";
    public static final String TAG_IS_SHOW_VIEW = "isshowview";
    public static final String TAG_VIDEO_STATE = "videostate";
    public static final String TAG_IS_MANUAL_UNFULLSCRREN = "ismanualunfullscreen";
    public static final String TAG_ACTIVITY_STATE = "activitystate";
    public static final String TAG_APP_FROM = "appfrom";

    // 可选
    public static final String TAG_SHOW_TYPE = "showtype";
    public static final String TAG_START_TIME = "starttime";//回看
    public static final String TAG_END_TIME = "endtime";//回看

    // 必选
    private long position; // 视频播放位置
    private int state; // 视频的播放状态
    private boolean isFullScreen; // 当前是否是全屏播放
    private boolean isShowView; // 当前是否显示了控制面板
    private String videoId; // 视频的ID
    private String videoSubId; // 当前播放的集数
    private boolean isManualUnFullscreen; // 当前是否是手动退出
    private VideoPlayType type; // 视频的显示类型 0 非影视类 1电影 3电视剧  4综艺详情页
    private String videoName; // 视频的名称
    private String videoSubName;// 视频子集名称
    private String activityState;// activity的状态
    private int from;// 视频来源，0淘TV,3优酷，4搜狐,5爱奇艺
    private String from_app;

    // 可选
    private int fileIndex; // 用户选择的当前位置（相对于影视列表的选中的序号从0开始）
    private int chargeType; // 收费类型 0-免费 1-限免 2-单点 3-包月 4-红包 5-单包
    private boolean isPay; // 是否是收费影片
    private boolean isPurchased; // 用户是否购买过
    private long price; // 现价
    private long primeCost; // 原价
    private boolean isPrevue; // 预告片
    private int fileCount; // 电视总集数
    private boolean isDynCount; // 是否全集了
    private String fileId; // 两种情况 1. 非影视类的节目，并且是搜索结果中才有值,否则为空 2. 一级分类的高清栏目(记录,综艺)，视频列表的每项program中

    public long getPositon() {
        return position;
    }

    public boolean isFullScreen() {
        return isFullScreen;
    }

    public boolean isShowView() {
        return isShowView;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getVideoName() {
        return videoName;
    }

    public String getVideoSubid() {
        return videoSubId;
    }

    public boolean isManualUnFullscreen() {
        return isManualUnFullscreen;
    }

    public int getState() {
        return state;
    }

    public VideoPlayType getType() {
        return type;
    }

    public boolean getIsShowView() {
        return isShowView;
    }

    /**
     * 获取影视状态
     * @return
     */
    public String getActivityState() {
        return activityState;
    }

    /**
     * 获取视频来源 -1为unknow,0为淘TV,3优酷，4搜狐,5爱奇艺
     * @return
     */
    public int getVideoFrom() {
        return from;
    }

    /**
     * 影视来源(包名)
     * @return
     */
    public String getFromApp() {
        return from_app;
    }

    /**
     * 是否强制需要隐藏商品
     * @return
     */
    public boolean needHideShop() {
        return (!isFullScreen || isManualUnFullscreen);
    }

    /**
     * 是否非强制隐藏商品，当商品界面已经出来，则不关闭
     * @return
     */
    public boolean needWeakHideShop() {
        return isShowView || state != STATE_PLAYING;
    }

    public static TbTvShoppingReceiverData buildReceiverData(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        TbTvShoppingReceiverData data = new TbTvShoppingReceiverData();
        data.position = bundle.getLong(TAG_VIDEO_POSTION, -1);
        if (data.position == -1) {// 如果position参数解析失败，则解析String类型
            try {
                data.position = Long.valueOf(bundle.getString(TAG_VIDEO_POSTION, "-1"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        data.state = bundle.getInt(TAG_VIDEO_STATE, STATE_ERROR);
        data.isFullScreen = bundle.getBoolean(TAG_IS_FULLSCREEN, false);
        data.isShowView = bundle.getBoolean(TAG_IS_SHOW_VIEW, false);
        data.videoId = bundle.getString(TAG_VIDEO_ID, "");
        data.videoName = bundle.getString(TAG_VIDEO_NAME, "");
        data.videoSubName = bundle.getString(TAG_VIDEO_SUB_NAME, "");
        Integer type = bundle.getInt(TAG_TYPE, 0);
        if (null != type) {
            data.type = VideoPlayType.valueOf(type);
        }

        // 先取string，取不到再取long，兼容videosubid viodesubid两种关键字
        data.videoSubId = bundle.getString(TAG_VIDEO_SUB_ID, null);
        if (TextUtils.isEmpty(data.videoSubId)) {
            data.videoSubId = String.valueOf(bundle.getLong(TAG_VIDEO_SUB_ID, -1));
        }
        // 如果没取到，或取到值为-1，取另一个关键字
        if (TextUtils.isEmpty(data.videoSubId) || data.videoSubId.equals("-1")) {
            data.videoSubId = bundle.getString(TAG_VIODE_SUB_ID, null);
            if (TextUtils.isEmpty(data.videoSubId)) {
                data.videoSubId = String.valueOf(bundle.getLong(TAG_VIODE_SUB_ID, 0));
            }
        }

        data.isManualUnFullscreen = bundle.getBoolean(TAG_IS_MANUAL_UNFULLSCRREN, false);
        data.activityState = bundle.getString(TAG_ACTIVITY_STATE, "");
        data.from = bundle.getInt("from", -1);
        data.from_app = bundle.getString(TAG_APP_FROM, "");

        data.fileIndex = bundle.getInt("fileIndex", -1);
        data.chargeType = bundle.getInt("chargeType", -1);
        data.isPay = bundle.getBoolean("isPay", false);
        data.isPurchased = bundle.getBoolean("isPurchased", false);
        data.price = bundle.getLong("price", -1);
        data.primeCost = bundle.getLong("primeCost", -1);
        data.isPrevue = bundle.getBoolean("isPrevue", false);
        data.fileCount = bundle.getInt("fileCount", -1);
        data.isDynCount = bundle.getBoolean("isDynCount", false);
        data.fileId = bundle.getString("fileId", "");

        if (Config.isDebug()) {
            AppDebug.i(TAG, TAG + ".buildReceiverData:position=" + data.position + " state=" + data.state
                    + " isFullScreen=" + data.isFullScreen + " isShowView=" + data.isShowView + " id=" + data.videoId
                    + " name=" + data.videoName + " value=" + data.videoSubId + " isManualUnFullscreen="
                    + data.isManualUnFullscreen + " type = " + type + ",type = " + data.type + ".activityState = "
                    + data.activityState + ".from = " + data.from + ".from_app = " + data.from_app + ", bundle = "
                    + bundle);
        }

        return data;
    }
}
