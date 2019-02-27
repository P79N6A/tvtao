package com.tvtaobao.voicesdk.control;

import com.tvtaobao.voicesdk.ASRNotify;
import com.tvtaobao.voicesdk.bo.CommandReturn;
import com.tvtaobao.voicesdk.bo.DomainResultVo;
import com.tvtaobao.voicesdk.bo.PageReturn;
import com.tvtaobao.voicesdk.control.base.BizBaseControl;
import com.tvtaobao.voicesdk.utils.LogPrint;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/5/9
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class OpenIndexControl extends BizBaseControl {
    @Override
    public void execute(DomainResultVo domainResultVO) {
        PageReturn pageReturn = ASRNotify.getInstance().isAction(domainResultVO);
        if (pageReturn != null && pageReturn.isHandler) {
            alreadyDeal(pageReturn.feedback);
            return;
        }
        setActionIndex(domainResultVO.getResultVO().getNorm());
    }

    public void setActionIndex(String pos) {
        LogPrint.w(TAG, TAG + ".WeakListener : " + mWeakListener + " ,VoiceListener : " + mWeakListener.get());
        if (mWeakListener != null && mWeakListener.get() != null) {
            CommandReturn commandReturn = new CommandReturn();
            commandReturn.mIsHandled = true;
            commandReturn.mAction = CommandReturn.TYPE_SEE_INDEX;
            commandReturn.mASRMessage = configVO.asr_text;
            commandReturn.mData = "{\"index\":" + pos + "}";
            mWeakListener.get().callback(commandReturn);
        }
    }
}
