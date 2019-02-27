package com.tvtaobao.voicesdk.control;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.tvtaobao.voicesdk.base.SDKInitConfig;
import com.tvtaobao.voicesdk.bo.DomainResultVo;
import com.tvtaobao.voicesdk.control.base.BizBaseControl;
import com.tvtaobao.voicesdk.tts.TTSUtils;
import com.tvtaobao.voicesdk.type.DomainType;
import com.tvtaobao.voicesdk.utils.ActivityUtil;
import com.yunos.tv.core.util.Utils;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/4/20
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class PageIntentControl extends BizBaseControl {

    @Override
    public void execute(final DomainResultVo domainResultVO) {
        if (!TextUtils.isEmpty(domainResultVO.getToUri())) {
            if (DomainType.TAKEOUT_GOTO_INDEX.equals(domainResultVO.getIntent())) {
                if ("TtCommon_tvtaobao-waimai".equals(SDKInitConfig.getCurrentPage())) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Context context = ActivityUtil.getTopActivity();
                            TTSUtils.getInstance().showDialog(context, 1);
                        }
                    });

                    return;
                }
            }

            gotoActivity(domainResultVO.getToUri());

            Utils.utCustomHit("Voice_jump", getProperties());
        }
    }
}
