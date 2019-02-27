package com.yunos.tvtaobao.biz.preference;


import android.util.SparseArray;

public class UpdatePreference {

    /************************************** update handle **************************************/

    public static final int MTOP_DONE = 1000;

    public static final int MTOP_FAIL = 1001;

    public static final int EXCEPTION = 1002;

    public static final int NEW_APK_EXIST = 1003;

    public static final int DOWNLOAD_DONE = 1004;

    public static final int NEW_APK_INVALID = 1005;

    public static final int DOWNLOAD_TIMEOUT = 1006;

    public static final int DOWNLOAD_INTERRUPT = 1007;

    public static final int UPDATE_TERMINATED = 1008;

    public static final int DOWNLOAD_PROCESSING = 1009;

    public static final int DOWNLOAD_PROGRESS_UPDATE = 1010;

    public static final int NEW_APK_VALID = 1011;

    public static final int NEW_TPATCH_VALID = 2011;

    public static final int DOWNLOAD_TPATCH_INTERRUPT = 2002;

    public static final int DOWNLOAD_TPATCH_TIMEOUT = 2003;

    public static final int NEW_TPATCH_INVALID = 2001;

    public static final int DOWNLOAD_TPATCH_DONE = 2000;

    public static final int SHOW_UPDATE_DIALOG = 2004;



    /************************************ key *********************************************/

    public static final String INTENT_KEY_FORCE_INSTALL = "isForced";

    public static final String INTENT_KEY_UPDATE_INFO = "updateInfo";

    public static final String INTENT_KEY_TARGET_FILE = "targetFile";

    public static final String INTENT_KEY_TARGET_MD5 = "targetMD5";

    public static final String INTENT_KEY_TARGET_SIZE = "targetSize";

    public static final String INTENT_KEY_APP_CODE = "appCode";

    /***************************************** MTOP *****************************************/

    //    public static final String API = "tvtao.itemService.update";
    public static final String API = "mtop.taobao.tvtao.tvtaoappservice.upgrade";

    public static final String API_VERSION = "1.0";

    /************************************ service *********************************************/

    public static final String KEY_CALLBACK_STATUS = "status";

    public static final String KEY_CALLBACK_INFO = "info";

    public static final String KEY_CALLBACK_CODE = "code";

    public static final String KEY_CALLBACK_BUNDLE = "bundle";

    public static final int STATUS_ERROR = 10000;

    public static final int STATUS_TERMINATED = 10001;

    public static final int STATUS_PROCESSING = 10002;

    public static final int STATUS_START_UPDATE = 10003;

    public static final int STATUS_START_UPDATE_ACTIVITY = 10004;

    public static final int STATUS_NO_UPDATE = 10005;

    public static final int STATUS_UPDATE_PROCESSING = 10006;

    /************************************* service handler *****************************************/

    public static final int TERMINATED = 100;

    public static final int START_UPDATE_ACTIVITY = 101;

    public static final int NO_UPDATE = 102;

    public static final int PROCESSING = 103;

    public static final int LOG_RECEIVE = 104;

    public static final int LOG_STOP = 105;

    public static final int LOG_READ= 106;

    /************************************* share preference *****************************************/

    public static final String SP_FILE_NAME = "updateInfo";

    public static final String SP_TPATCH_FILE_NAME = "tpatchInfo";

    public static final String SP_KEY_VERSION = "version";

    public static final String SP_KEY_VERSION_NAME = "versionName";

    public static final String SP_KEY_PATH = "filepath";

    public static final String SP_KEY_MD5 = "MD5";

    public static final String SP_KEY_RELEASE_NOTE = "releaseNote";


    /************************************* Error Type *****************************************/

    public static final int ERROR_TYPE_NETWORK_DISCONNECT = 0;

    public static final int ERROR_TYPE_NETWORK_INAVAILABLE = 1;

    public static final int ERROR_TYPE_INVALID_UPDATE_FILE = 2;

    public static final int ERROR_TYPE_INVALID_TPATCH_UPDATE_FILE = 6;

    public static final int ERROR_TYPE_FILE_EXCEPTION = 3;

    public static final int ERROR_TYPE_MTOP_FAIL = 4;

    public static final int ERROR_TYPE_MTOP_TPATCH_FAIL = 7;

    public static final int ERROR_TYPE_DOWNLOAD_TIMEOUT = 5;

    /************************************* Error Type vs Error Info *****************************************/

    public static final SparseArray<String> ERROR_TYPE_INFO_MAP = new SparseArray<String>() {

        {
            put(ERROR_TYPE_NETWORK_DISCONNECT, "网络连接断开");
            put(ERROR_TYPE_NETWORK_INAVAILABLE, "网络不可用");
            put(ERROR_TYPE_INVALID_UPDATE_FILE, "更新文件无效");
            put(ERROR_TYPE_DOWNLOAD_TIMEOUT, "下载更新文件超时，可能由于网络原因");
            put(ERROR_TYPE_FILE_EXCEPTION, "文件异常（文件路径不存在或无法被删除等）");
            put(ERROR_TYPE_MTOP_FAIL, "访问MTOP失败，可能由于网络原因或参数不正确");
        }
    };

    /************************************* Download Type *****************************************/

    // 最新版本
    public static final int DOWNLOAD_TYPE_LATEST = 0;
    // 非强制更新
    public static final int DOWNLOAD_TYPE_UNFORCED = 1;

    /************************************* UserTrack *****************************************/

    public static final String UT_SHOW_UPDATE_ACTIVITY = "Update_view";

    public static final String UT_CLICK_UPDATE = "update";

    public static final String UT_CLICK_LATER = "later_update";

    public static final String UT_CLICK_RETRY = "retry_update";

    public static final String UT_CLICK_EXIT = "exit_when_update_error";

    public static final String UT_CANCEL = "cancel";

    public static final String UT_DOWNLOAD_SUCCESS = "download_succ";

    public static final String UT_INSTALL_SUCCESS = "install_succ";

    public static final String UT_ERROR = "install_error_info";

    /************************************** call type **********************************************/

    public static final String TVTAOBAO = "tvtaobao";

    public static final String TVTAOBAO_EXTERNAL = "tvtaobao_external";


    public static final String UPDATE_CURRENT_VERSION_CODE = "update_current_version_code";

    public static final String UPDATE_TIPS = "update_tips";

    public static final String UPDATE_OBJECT = "update_object";

//    public static final String UPDATE_LOG_UPLOAD_DONE = "update_log_update_done";

    public static final String IS_MOHE_LOG_ON = "is_mohe_log_on";

    public static final String IS_LIANMNEG_LOG_ON = "is_lianmeng_log_on";

    public static final String IS_YITIJI_LOG_ON = "is_yitiji_log_on";


    //image1.背景图；image2.圆点；image3.稍后再说聚焦；image4.稍后再说未聚焦；image5.马上升级聚焦；image6.马上升级未聚焦；
    public static final String IMAGE1 = "image1";

    public static final String IMAGE2 = "image2";

    public static final String IMAGE3 = "image3";

    public static final String IMAGE4 = "image4";

    public static final String IMAGE5 = "image5";

    public static final String IMAGE6 = "image6";

    public static final String NEW_RELEASE_NOTE = "newReleaseNote";

    public static final String UPGRADE_MODE = "upgradeMode";

    public static final String COLOR = "color";

    public static final String LATER_ON = "laterOn";

    public static final String UPGRADE_NOW = "upgradeNow";

}