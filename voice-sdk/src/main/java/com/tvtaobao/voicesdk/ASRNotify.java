package com.tvtaobao.voicesdk;

import com.tvtaobao.voicesdk.bo.CommandReturn;
import com.tvtaobao.voicesdk.bo.DomainResultVo;
import com.tvtaobao.voicesdk.bo.PageReturn;
import com.tvtaobao.voicesdk.interfaces.ASRHandler;
import com.tvtaobao.voicesdk.interfaces.ASRSearchHandler;
import com.tvtaobao.voicesdk.interfaces.VoiceListener;
import com.tvtaobao.voicesdk.utils.LogPrint;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : pan
 *     QQ : 1065475118
 *     time   : 2018/03/27
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class ASRNotify {
    private static String TAG = "ASRNotify";

    private static ASRNotify notify;

    private List<ASRHandler> asrHandlerList;
    private List<ASRSearchHandler> asrHandlerSearchList = new ArrayList<>();
    private WeakReference<VoiceListener> voiceListener;

    public static ASRNotify getInstance() {
        if (notify == null) {
            synchronized (ASRNotify.class) {
                if (notify == null) {
                    notify = new ASRNotify();
                }
            }
        }

        return notify;
    }

    private ASRNotify() {
        asrHandlerList = new ArrayList<>();
    }

    /**
     * 判断当前展示的页面有没有调用。
     * 供语音助手来源调用
     *
     * @param domainResultVo 语义理解对象
     * @return true 代表页面打开，并进行了操作；false 代表当前页面没有操作。
     */
    public PageReturn isAction(DomainResultVo domainResultVo) {
        LogPrint.i(TAG, TAG + ".isAction size 1: " + asrHandlerList.size() + " ,asrHandlerList : " + asrHandlerList);
        if (asrHandlerList.size() != 0) {
            return asrHandlerList.get(asrHandlerList.size() - 1).onASRNotify(domainResultVo);
        }

        return null;
    }

    public boolean isActionSearch(String asr_text, String txt, String spoken, List<String> tips, DomainResultVo.OtherCase otherCase) {
        LogPrint.i(TAG, TAG + ".isAction size 1: " + asrHandlerSearchList.size() + " ,asrHandlerList : " + asrHandlerSearchList);
        if (asrHandlerSearchList.size() != 0) {
            return asrHandlerSearchList.get(asrHandlerSearchList.size() - 1).onSearch(asr_text,txt,spoken,tips,otherCase);
        }

        return false;
    }

    /**
     * 设置监听回调。
     * 供语音页面调用
     *
     * @param handler
     */
    public void setHandler(ASRHandler handler) {
        LogPrint.i(TAG, TAG + ".setHandler size : " + asrHandlerList.size() + " ,handler : " + handler);
        if (handler == null) {
            if (asrHandlerList.size() == 0)
                return;

            LogPrint.i(TAG, TAG + ".setHandler : " + asrHandlerList);
            asrHandlerList.remove(asrHandlerList.size() - 1);
            LogPrint.i(TAG, TAG + ".setHandler : " + asrHandlerList);
        } else {
            asrHandlerList.add(handler);
        }
    }

    public void setHandlerSearch(ASRSearchHandler handler) {
        LogPrint.i(TAG, TAG + ".setHandler size : " + asrHandlerSearchList.size() + " ,handler : " + handler);
        if (handler == null) {
            if (asrHandlerSearchList.size() == 0)
                return;

            LogPrint.i(TAG, TAG + ".setHandler : " + asrHandlerSearchList);
            asrHandlerSearchList.remove(asrHandlerSearchList.size() - 1);
            LogPrint.i(TAG, TAG + ".setHandler : " + asrHandlerSearchList);
        } else {
            asrHandlerSearchList.add(handler);
        }
    }
    public void setFeedBack(VoiceListener listener) {
        voiceListener = new WeakReference<>(listener);
    }

    public void playTTS(String msg) {
        playTTS(msg, false);
    }

    public void playTTS(String msg, boolean show) {
        if (voiceListener != null && voiceListener.get() != null) {
            CommandReturn commandReturn = new CommandReturn();
            commandReturn.mIsHandled = true;
            commandReturn.mAction = CommandReturn.TYPE_TTS_PLAY;
            commandReturn.showUI = show;
            commandReturn.mMessage = msg;
            voiceListener.get().callback(commandReturn);
        }
    }
}
