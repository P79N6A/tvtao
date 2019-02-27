package com.tvtaobao.voicesdk.services;

import android.os.RemoteException;
import android.text.TextUtils;

import com.bftv.fui.tell.TTS;
import com.bftv.fui.tell.TellManager;
import com.bftv.fui.thirdparty.IRemoteVoice;
import com.bftv.fui.thirdparty.RomoteVoiceService;
import com.bftv.fui.thirdparty.VoiceFeedback;
import com.google.gson.reflect.TypeToken;
import com.tvtaobao.voicesdk.ASRInput;
import com.tvtaobao.voicesdk.ASRNotify;
import com.tvtaobao.voicesdk.base.SDKInitConfig;
import com.tvtaobao.voicesdk.bo.CommandReturn;
import com.tvtaobao.voicesdk.bo.JinnangDo;
import com.tvtaobao.voicesdk.bo.ProductDo;
import com.tvtaobao.voicesdk.interfaces.VoiceListener;
import com.tvtaobao.voicesdk.utils.BFDataController;
import com.tvtaobao.voicesdk.utils.JSONUtil;
import com.tvtaobao.voicesdk.utils.LogPrint;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.util.GsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2017/08/02
 *     desc : 接入暴风SDK，继承RomoteVoiceService，获取暴风语音助手ASR
 *     version : 1.0
 * </pre>
 */

public class BftvASRService extends RomoteVoiceService {
    private final String TAG = "BftvASRService";
    private ASRInput asrInput;
    private ASRNotify asrNotify;
    private Listener mListener;

    @Override
    public void onCreate() {
        super.onCreate();
        asrNotify = ASRNotify.getInstance();
        asrInput = ASRInput.getInstance();
        asrInput.setContext(this);
        mListener = new Listener();

        try {
            JSONObject object = new JSONObject();
            object.put("needTakeOutTips", false);
            SDKInitConfig.init(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 暴风通过AIDL与我们进行数据上的通信。
     * 可以去了解一下AIDL通行方式。
     *
     * @param s            ASR的内容
     * @param s1           JSON数据，用户说ASR之后，暴风做好处理，给我们需要的JSON
     * @param iRemoteVoice
     */
    @Override
    public void send(String s, String s1, final IRemoteVoice iRemoteVoice) {
        LogPrint.e(TAG, TAG + ".send s : " + s + " ,s1 : " + s1);
        asrNotify.setFeedBack(mListener);
        mListener.destroy();
        mListener.setIRemoteVoice(iRemoteVoice, s);
//        if (isApplicationInForeground()) {
        asrInput.handleInput(s, s1, mListener);
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        asrInput.destroy();
        LogPrint.e(TAG, "onDestroy() executed");
    }

    /**
     * 数据处理好之后，通过VoiceListener返回。
     * 然后通过IRemoteVoice，将我们转换好的数据格式，返回给暴风。
     */
    class Listener implements VoiceListener {

        private IRemoteVoice iRemoteVoice;
        private String asr_text;

        private void setIRemoteVoice(IRemoteVoice iRemoteVoice, String asr) {
            this.iRemoteVoice = iRemoteVoice;
            this.asr_text = asr;
        }

        public void destroy() {
            this.iRemoteVoice = null;
        }

        @Override
        public void callback(CommandReturn command) {
            LogPrint.e(TAG, TAG + "Listener callback mIsHandled : " + command.mIsHandled + " ,Action : " + command.mAction);
            try {
                VoiceFeedback voiceFeedback = new VoiceFeedback();
                voiceFeedback.isHasResult = command.mIsHandled;
                switch (command.mAction) {
                    case CommandReturn.TYPE_TTS_PLAY:
//                        if (!TextUtils.isEmpty(command.mMessage)) {
//                            voiceFeedback.feedback = command.mMessage;
//                            voiceFeedback.type = VoiceFeedback.TYPE_CMD;
//                        }
                        if (!TextUtils.isEmpty(command.mMessage)) {
                            TTS tts = new TTS();
                            tts.pck = AppInfo.getPackageName();
                            tts.tts = command.mMessage;
                            tts.userTxt = asr_text;
                            tts.isDisplayLayout = command.showUI;
                            TellManager.getInstance().tts(CoreApplication.getApplication(), tts);
                        }
                        break;
                    default:
                        break;
                }
//                if (command.mTips != null && command.mTips.size() != 0) {
//                    voiceFeedback.listPrompts = command.mTips;
//                }

                if (iRemoteVoice != null) {
                    iRemoteVoice.sendMessage(voiceFeedback);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void searchResult(String data) {
            LogPrint.e(TAG, TAG + ".Listener.searchResult");
            try {
                JSONObject object = new JSONObject(data);
                String keyword = object.getString("keyword");
                List<ProductDo> mProducts = new ArrayList<>();
                if (object.has("model")) {
                    JSONArray model = object.getJSONArray("model");
                    LogPrint.e(TAG, TAG + ".SearchResponse size : " + model.length());
                    for (int i = 0; i < model.length(); i++) {
                        mProducts.add(GsonUtil.parseJson(model.getJSONObject(i).toString(),
                                new TypeToken<ProductDo>() {
                                }));
                    }
                }

                List<JinnangDo> mJinnangs = new ArrayList<>();
                if (object.has("jinNangItems")) {
                    JSONArray jinnang = object.getJSONArray("jinNangItems");
                    for (int i = 0; i < jinnang.length(); i++) {
                        mJinnangs.add(JinnangDo.resolverData(jinnang.getJSONObject(i)));
                    }
                }

                String mMessage = JSONUtil.getString(object, "spoken");
                JSONArray tipsArray = JSONUtil.getArray(object,"tips");
                List<String> tips = new ArrayList<>();
                if (tipsArray != null) {
                    for (int i = 0 ; i < tipsArray.length() ; i++) {
                        tips.add(tipsArray.getString(i));
                    }
                }

                VoiceFeedback voiceFeedback = BFDataController.onSearchSuccess(keyword, mProducts, mJinnangs, mMessage, tips);

                if (iRemoteVoice != null) {
                    iRemoteVoice.sendMessage(voiceFeedback);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
