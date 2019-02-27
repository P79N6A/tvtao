package com.yunos.tvtaobao.biz.model;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taobao.atlas.update.model.UpdateInfo;


public class AppInfo {

    private static final String RET = "ret";

    private static final String DATA = "data";

    private static final String ID = "id";

    private static final String RESOURCE_ID = "resourceId";

    private static final String VERSION = "version";

    private static final String VERSION_NAME = "versionName";

    private static final String RELEASE_NOTE = "releaseNote";

    private static final String UPDATE_BUNDLES = "updateBundles";

    private static final String DOWNLOAD_URL = "downloadUrl";

    private static final String OSS_DOWNLOAD_URL = "ossDownloadUrl";

    private static final String DOWNLOAD_MD5 = "downloadMd5";

    private static final String BASE_VERSION = "baseVersion";

    private static final String UPDATE_VERSION = "updateVersion";

    private static final String SIZE = "size";

    private static final String TYPE = "type";

    private static final String TAPTCH_DOWNLOAD_URL = "tpatchDownloadUrl";

    private static final String STATUS = "status";

    private static final String EXTEND = "extend";

    private static final String TIME_STAMP = "timeStamp";

    private static final String RELEASE_AFTER_NOTE = "releaseAfterNote";
    //image1.背景图；image2.圆点；image3.稍后再说聚焦；image4.稍后再说未聚焦；image5.马上升级聚焦；image6.马上升级未聚焦；
    private static final String IMAGE1 = "image1";

    private static final String IMAGE2 = "image2";

    private static final String IMAGE3 = "image3";

    private static final String IMAGE4 = "image4";

    private static final String IMAGE5 = "image5";

    private static final String IMAGE6 = "image6";

    private static final String NEW_RELEASE_NOTE = "newReleaseNote";

    private static final String UPGRADE_MODE = "upgradeMode";

    private static final String COLOR = "color";

    private static final String LATER_ON = "laterOn";

    private static final String UPGRADE_NOW = "upgradeNow";



    /**
     * 调用是否成功
     */
    public boolean isSuccess;
    public boolean isForced;
    /**
     * 是否已经是最新版本
     */
    public boolean isLatest;

    public int type = 0;  //type:0, APK; type:1, tpatch文件

    public String returnText;

    public String versionName;

    public String getReleaseNote() {
        return releaseNote;
    }

    public void setReleaseNote(String releaseNote) {
        this.releaseNote = releaseNote;
    }

    public String releaseNote;

    public String downloadUrl;

    public String tpatchDownloadUrl;

    public String ossDownloadUrl;// 与downloadUrl有什么区别

    public String downloadMd5;

    public String extend;

    public String id;

    public String resourceId;

    public String version;

    public String size;

    public String status;

    public String timeStamp;

    public String apkName;

    public UpdateInfo mUpdateInfo;

    public String releaseAfterNote;

    private JSONObject upgradeObject;

    private JSONArray retArray;

    public String getImage1() {
        return image1;
    }

    public String getImage2() {
        return image2;
    }

    public String getImage3() {
        return image3;
    }

    public String getImage4() {
        return image4;
    }

    public String getImage5() {
        return image5;
    }

    public String getImage6() {
        return image6;
    }

    public String getNewReleaseNote() {
        return newReleaseNote;
    }

    public String getUpgradeMode() {
        return upgradeMode;
    }

    public String getColor() {
        return color;
    }

    public String getLaterOn() {
        return laterOn;
    }

    public String getUpgradeNow() {
        return upgradeNow;
    }
    //image1.背景图；image2.圆点；image3.稍后再说聚焦；image4.稍后再说未聚焦；image5.马上升级聚焦；image6.马上升级未聚焦；
    private  String image1;

    private  String image2;

    private  String image3;

    private  String image4;

    private  String image5;

    private  String image6;

    private  String newReleaseNote;

    private  String upgradeMode;

    private  String color;

    private  String laterOn;

    private  String upgradeNow;

    public AppInfo(JSONObject upgradeObject) {
//        retArray = json.getJSONArray(RET);
        if (upgradeObject == null) {
            isSuccess = false;
            return;
        }
        isSuccess = true;
//        upgradeObject = json.getJSONObject(DATA);
        if (upgradeObject == null || upgradeObject.size() <= 0) {
            isLatest = true;
            return;
        }
        id = upgradeObject.getString(ID);
        resourceId = upgradeObject.getString(RESOURCE_ID);
        version = upgradeObject.getString(VERSION);
        versionName = upgradeObject.getString(VERSION_NAME);
        releaseNote = upgradeObject.getString(RELEASE_NOTE);
        downloadUrl = upgradeObject.getString(DOWNLOAD_URL);
        if (downloadUrl != null) {
            String[] tmp = downloadUrl.split("/");
            String[] tmp1 = tmp[tmp.length - 1].split("[?]");
            apkName = tmp1[0];
        }
        ossDownloadUrl = upgradeObject.getString(OSS_DOWNLOAD_URL);
        downloadMd5 = upgradeObject.getString(DOWNLOAD_MD5);
//        tpatchDownloadUrl = upgradeObject.getString(TAPTCH_DOWNLOAD_URL);
        type = upgradeObject.getInteger(TYPE);
        if (type == 1) {
            String updateBundles = upgradeObject.getString(UPDATE_BUNDLES);
            mUpdateInfo = JSON.parseObject(updateBundles,UpdateInfo.class);
        }
        size = upgradeObject.getString(SIZE);
        status = upgradeObject.getString(STATUS);
        extend = upgradeObject.getString(EXTEND);
        if ("forceInstall".equalsIgnoreCase(extend))
            isForced = true;
        else
            isForced = false;
        timeStamp = upgradeObject.getString(TIME_STAMP);
        releaseAfterNote = upgradeObject.getString(RELEASE_AFTER_NOTE);

        image1 = upgradeObject.getString(IMAGE1);
        image2 = upgradeObject.getString(IMAGE2);
        image3 = upgradeObject.getString(IMAGE3);
        image4 = upgradeObject.getString(IMAGE4);
        image5 = upgradeObject.getString(IMAGE5);
        image6 = upgradeObject.getString(IMAGE6);
        newReleaseNote = upgradeObject.getString(NEW_RELEASE_NOTE);
        upgradeMode = upgradeObject.getString(UPGRADE_MODE);
        color = upgradeObject.getString(COLOR);
        laterOn = upgradeObject.getString(LATER_ON);
        upgradeNow = upgradeObject.getString(UPGRADE_NOW);

    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "isSuccess=" + isSuccess +
                ", isForced=" + isForced +
                ", isLatest=" + isLatest +
                ", type=" + type +
                ", returnText='" + returnText + '\'' +
                ", versionName='" + versionName + '\'' +
                ", releaseNote='" + releaseNote + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", tpatchDownloadUrl='" + tpatchDownloadUrl + '\'' +
                ", ossDownloadUrl='" + ossDownloadUrl + '\'' +
                ", downloadMd5='" + downloadMd5 + '\'' +
                ", extend='" + extend + '\'' +
                ", id='" + id + '\'' +
                ", resourceId='" + resourceId + '\'' +
                ", version='" + version + '\'' +
                ", size='" + size + '\'' +
                ", status='" + status + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", apkName='" + apkName + '\'' +
                ", upgradeObject=" + upgradeObject +
                ", retArray=" + retArray +
                ", releaseAfterNote=" + releaseAfterNote +
                ", image1=" + image1 +
                ", image2=" + image2 +
                ", image3=" + image3 +
                ", image4=" + image4 +
                ", image5=" + image5 +
                ", image6=" + image6 +
                ", newReleaseNote=" + newReleaseNote +
                ", upgradeMode=" + upgradeMode +
                ", color=" + color +
                ", laterOn=" + laterOn +
                ", upgradeNow=" + upgradeNow +
                '}';
    }
}
