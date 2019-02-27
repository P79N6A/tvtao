package com.yunos.tvtaobao.tvlive.utils;

import com.alibaba.fastjson.JSON;
import com.yunos.tvtaobao.tvlive.bo.model.bean.YGVideoInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangdaju on 17/7/19.
 */

public class JsonResolver {

    /**
     * 解析央广购物视频列表
     * @return
     */
    public static List<YGVideoInfo> resolveYGVideoList(JSONObject data) throws JSONException {
        if (data == null)
            return null;

        List<YGVideoInfo> list = new ArrayList<YGVideoInfo>();
        if (data.has("lives")) {
            JSONArray liveList = data.getJSONArray("lives");
            int liveLenght = liveList.length();
            for (int i = 0; i < liveLenght; i++) {
                YGVideoInfo y = YGVideoInfo.fromMTOP(liveList.getJSONObject(i));
                if (y == null)
                    continue;
                y.setType(VideoType.YG_LIVE);
                list.add(y);
            }
        }

        if (data.has("videos")) {
            JSONArray videoList = data.getJSONArray("videos");
            int videoLenght = videoList.length();
            for (int i = 0; i < videoLenght; i++) {
                YGVideoInfo y = YGVideoInfo.fromMTOP(videoList.getJSONObject(i));
                if (y == null)
                    continue;
                y.setType(VideoType.YG_VIDEO);
                list.add(y);
            }
        }
        return list;
    }
}
