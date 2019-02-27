package com.tvtaobao.voicesdk.register;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.iflytek.xiri.Feedback;
import com.iflytek.xiri.scene.ISceneListener;
import com.iflytek.xiri.scene.Scene;
import com.tvtaobao.voicesdk.ASRInput;
import com.tvtaobao.voicesdk.utils.LogPrint;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Set;

/**
 * <pre>
 *     author : pan
 *     QQ : 1065475118
 *     time   : 2018/03/26
 *     desc   : 科大讯飞语点注册
 *     version: 1.0
 * </pre>
 */
public class KDXFRegister {
    private final String TAG = "KDXFRegister";
    private Scene scene;
    private Feedback feedback;
    private String _scene = null;
    private boolean isRegister = false;
    public KDXFRegister() {
        scene = new Scene(CoreApplication.getApplication());
        feedback = new Feedback(CoreApplication.getApplication());
    }

    public void onRegister(String className, JSONArray jsonArray) {
        JSONObject object = new JSONObject();
        try {
            JSONObject tvtaobaoASR = new JSONObject();
            tvtaobaoASR.put("tvtaobaoASR", jsonArray);
            object.put("_commands", tvtaobaoASR);
            object.put("_scene", className);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        _scene = object.toString();
        LogPrint.e(TAG, "onExecute _scene : " + _scene);
        scene.init(new KDXFBackListener());
        isRegister = true;
    }

    public void release() {
        try {
            if (isRegister) {
                scene.release();
                isRegister = false;
            }
        } catch (Exception e) {
            LogPrint.e(TAG, "release e : " + e.getMessage());
        }
    }

    /**
     * 讯飞返回
     * key : _scene ,value : com.yunos.tvtaobao.detailbundle.activity.NewDetailActivity
     * key : _token ,value : 37
     * key : _command ,value : tvtaobaoASR
     * key : _rawtext ,value : 我想要查看商品评价
     * key : package ,value : rca.rc.tvtaobao
     * key : pkgname ,value : com.iflytek.xiri
     * key : _objhash ,value : 68901045
     */
    private class KDXFBackListener implements ISceneListener {

        @Override
        public String onQuery() {
            return _scene;
        }

        @Override
        public void onExecute(Intent intent) {
            feedback.begin(intent);
            String asr = intent.getStringExtra("_rawtext");
            AppDebug.i(TAG, TAG + ".onExecute asr : " + asr);
            if (TextUtils.isEmpty(asr)) {
                return;
            }
            ASRInput.getInstance().nlpRequest(asr);
        }
    }
}
