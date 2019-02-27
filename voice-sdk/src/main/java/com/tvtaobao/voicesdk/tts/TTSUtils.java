package com.tvtaobao.voicesdk.tts;

import android.content.Context;
import android.text.TextUtils;

import com.tvtaobao.voicesdk.ASRNotify;
import com.tvtaobao.voicesdk.base.SDKInitConfig;
import com.tvtaobao.voicesdk.bo.DomainResultVo;
import com.tvtaobao.voicesdk.dialogs.TipsDialog;
import com.yunos.tv.core.config.Config;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/5/2
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class TTSUtils {
    private static String TAG = "TTSUtils";

    private static TTSUtils ttsUtils;
    private TipsDialog tipsDialog;
    private DomainResultVo domainResultVO;

    public static TTSUtils getInstance() {
        if (ttsUtils == null) {
            synchronized (TTSUtils.class) {
                if (ttsUtils == null) {
                    ttsUtils = new TTSUtils();
                }
            }
        }

        return ttsUtils;
    }

    public void setDomainResult(DomainResultVo domainResultVo) {
        this.domainResultVO = domainResultVo;
    }

    public void showDialog(Context context, int result) {
        if (domainResultVO != null) {
            if (tipsDialog != null) {
                tipsDialog.dismiss();
                tipsDialog = null;
            }

            if (result == 1) {
                //找到结果
                if (!TextUtils.isEmpty(domainResultVO.getSpokenTxt()) || domainResultVO.getTips() != null) {
                    if (!SDKInitConfig.needTakeOutTips()) {
                        ASRNotify.getInstance().playTTS(domainResultVO.getSpoken(), true);
                    } else {
                        tipsDialog = new TipsDialog(context);
                        tipsDialog.setTts(domainResultVO.getSpokenTxt(), domainResultVO.getSpoken());
                        tipsDialog.setTips(domainResultVO.getTips());
                        tipsDialog.show();
                    }
                }
            } else if (result == 0) {
                //没找到结果
                if (!TextUtils.isEmpty(domainResultVO.getOtherCaseSpokens().getNoSearchResult().getSpokenTxt())
                        || domainResultVO.getTips() != null) {
                    if (!SDKInitConfig.needTakeOutTips()) {
                        ASRNotify.getInstance().playTTS(domainResultVO.getOtherCaseSpokens().getNoSearchResult().getSpoken(), true);
                    } else {
                        tipsDialog = new TipsDialog(context);
                        tipsDialog.setTts(domainResultVO.getOtherCaseSpokens().getNoSearchResult().getSpokenTxt(),
                                domainResultVO.getOtherCaseSpokens().getNoSearchResult().getSpoken());
                        tipsDialog.setTips(domainResultVO.getTips());
                        tipsDialog.show();
                    }
                }
            }

            //TODO 简单修复一下从系统跳转到页面，domainResultVO不为空，会反复播报的事情
            domainResultVO = null;
        }
    }
}
