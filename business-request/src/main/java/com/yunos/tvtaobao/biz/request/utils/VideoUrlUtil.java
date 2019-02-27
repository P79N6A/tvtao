package com.yunos.tvtaobao.biz.request.utils;


import com.taobao.orange.util.StringUtil;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CountList;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.ItemMO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mtopsdk.common.util.StringUtils;

/**
 * 视频地址格式化
 * @author hanqi
 */
public class VideoUrlUtil {

    public static String videoCdnDomain = "http://cloud.video.taobao.com/";
    /**
     * 播放类型。类型：自动播放或空=1，非自动播放=2
     */
    public static String playType = "1";
    /**
     * 设备类型。
     * 类型：pc=1； iPad = 2；iPhone,=3；androidPad(>=3.0),=4；
     * androidPhone(>=3.0),=5；androidPhoneV23=6；androidPadV23,=7
     */
    public static String equipmentType = "2";
    /**
     * templateId,模板id,代表播放器整体外观风格，默认模板id为1，主图播放器模板id为6
     * 默认=1，新浪=2，旺铺=3，标注=4，主图播放器=6，
     * 淘宝=10100{广告强效果：10101，广告中效果：10102，广告弱效果：10103}，
     * 聚划算=10200
     */
    public static String templateId = "10200";
    /**
     * 清晰度，统一考量码率和分辨率两个维度的参数。
     * 同一个清晰度，PC和iPad，iPhone等平台，码率和分辨率可能不一样。
     * 对于m3u8格式，不指定清晰度，则返回包含多码率的索引m3u8。
     * mp4必须指定清晰度。从高到低排列：ud超清 hd高清 sd标清 ld流畅
     */
    public static String definition = "";
    /**
     * flash版本，高版本的flash支持更好的播放效果。
     * 101：<=10.1版本的flash，2或空：>10.1版本的falsh
     */
    public static String flashVersion = "";

    /**
     * 批量替换数组中的视频地址
     * @param items
     * @return
     */
    public static CountList<ItemMO> toM3u8(CountList<ItemMO> items) {
        if (null != items && !items.isEmpty()) {
            for (ItemMO item : items) {
                if (!StringUtils.isEmpty(item.getVideoUrl())) {
                    item.setVideoUrl(toM3u8(item.getVideoUrl()));
                }
            }
        }
        return items;
    }

    public static List<ItemMO> toM3u8(List<ItemMO> items) {
        if (null != items && !items.isEmpty()) {
            for (ItemMO item : items) {
                if (!StringUtils.isEmpty(item.getVideoUrl())) {
                    item.setVideoUrl(toM3u8(item.getVideoUrl()));
                }
            }
        }
        return items;
    }

    public static String toM3u8(String url) {
        if (url.endsWith(".swf")) {
            return swfToM3u8(url);
        } else if (url.indexOf("?") != -1) {
            return infoToM3u8(url);
        }
        return url;
    }

    public static String swfToM3u8(String url) {
        url = url.replaceAll("\\/p\\/[^\\/]*\\/", "/p/" + playType + "/");
        url = url.replaceAll("\\/e\\/[^\\/]*\\/", "/e/" + equipmentType + "/");
        url = url.replaceAll("\\/t\\/[^\\/]*\\/", "/t/" + templateId + "/");
        url = url.replaceAll("\\/d\\/[^\\/]*\\/", "/d/" + definition + "/");
        url = url.replaceAll("\\.(swf|mp4)$", ".m3u8");
        return url;
    }

    public static String infoToM3u8(String url) {
        Map<String, String> mapRequest = new HashMap<String, String>();
        String[] arrSplit = url.split("[?]");
        if (arrSplit.length > 1) {
            arrSplit = arrSplit[1].split("[&]");
            for (String strSplit : arrSplit) {
                String[] arrSplitEqual = strSplit.split("[=]");
                if (arrSplitEqual.length > 1) {
                    mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
                } else {
                    if (arrSplitEqual[0] != "") {
                        mapRequest.put(arrSplitEqual[0], "");
                    }
                }
            }
        }
        StringBuffer urlStringBuffer = new StringBuffer();
        urlStringBuffer.append(videoCdnDomain);
        urlStringBuffer.append("u/").append(mapRequest.get("uid")).append("/");
        urlStringBuffer.append("p/").append(playType).append("/");
        urlStringBuffer.append("e/").append(equipmentType).append("/");
        urlStringBuffer.append("t/").append(templateId).append("/");
        urlStringBuffer.append("d/").append(definition).append("/");
        urlStringBuffer.append(mapRequest.get("vid")).append(".m3u8");
        return urlStringBuffer.toString();
    }

}
