package com.tvtaobao.voicesdk.bo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by pan on 2017/9/14.
 */

public class CommandReturn {
    public final static int TYPE_UNKNOW = 1000;
    public final static int TYPE_TTS_PLAY = 1001;
    public final static int TYPE_GOODS_SHOW = 1002;
    public final static int TYPE_SEE_INDEX = 1003;
    public final static int TYPE_BUY_INDEX = 1004;
    public final static int TYPE_FEEDBACK_INFO = 1005;
    public final static int TYPE_TAKE_OUT_SEARCH = 1006;       //外卖搜索
    public final static int TYPE_NOT_FOUND_APP = 1007;  //没有发现电视淘宝应用
    public final static int TYPE_ERROR_INFO = 1008; //错误信息

    public boolean mIsHandled = false;
    public double score;
    public int mAction = 1000;
    public boolean showUI = false; //false 不展现系统浮层， true 展现系统浮层
    public int mCode = 200;
    public String mMessage;
    public String mASRMessage;
    public String mData;
    public List<String> mTips;

    @Override
    public String toString() {
        try {
            JSONObject object = new JSONObject();
            object.put("mIsHandled", mIsHandled);
            object.put("score", score);
            object.put("mASRMessage", mASRMessage);
            object.put("mData", mData);
            object.put("mAction", mAction);
            object.put("mMessage", mMessage);
            object.put("mCode", mCode);
            if (mTips != null) {
                JSONArray array = new JSONArray();
                for (int i = 0 ; i < mTips.size() ; i++) {
                    array.put(mTips.get(i));
                }
                object.put("mTips", array);
            }
            return object.toString();
        } catch (JSONException e) {
            return "";
        }
    }
}
