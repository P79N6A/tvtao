package com.yunos.tvtaobao.tvlive.tvtaomsg;

import android.content.Context;

import com.taobao.accs.ACCSManager;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.User;
import com.yunos.tvtaobao.tvlive.tvtaomsg.enums.BizTypeEnum;
import com.yunos.tvtaobao.tvlive.tvtaomsg.enums.ProtocolTypeEnum;
import com.yunos.tvtaobao.tvlive.tvtaomsg.po.Protocol;
import com.yunos.tvtaobao.tvlive.tvtaomsg.po.json.SubscribeTaobaoLiveTopic;
import com.yunos.tvtaobao.tvlive.tvtaomsg.po.json.UnSubscribeTaobaoLiveTopic;
import com.yunos.tvtaobao.tvlive.tvtaomsg.service.ITVTaoDiapatcher;
import com.yunos.tvtaobao.tvlive.tvtaomsg.utility.ByteCodec;
import com.yunos.tvtaobao.tvlive.tvtaomsg.utility.DataResolver;

import java.util.Map;

/**
 * Created by pan on 16/12/16.
 */

public class TvTaobaoMsgService {
    public static int TYPE_TAOBAO_LIVE = 1;
    public static int TYPE_YANGGUANG_LIVE = 2;
    private static ITVTaoDiapatcher mDiapatcher;

    public static final String TVTAOBAO_SERVER_ID = "tvtaobao";
    public static final String POWER_SERVER_ID = "powermsg";

    public static void registerTVLive(Context context, int type, String topic, ITVTaoDiapatcher diapatcher) {
        mDiapatcher = diapatcher;

        SubscribeTaobaoLiveTopic subscribe = new SubscribeTaobaoLiveTopic();
        subscribe.setType(type);
        subscribe.setTopic(topic);
        subscribe.setUserid(User.getUserId());

        byte[] body = subscribe.encode();

        try {
            byte[] data = ByteCodec.encode(BizTypeEnum.SUBSCRIBE_LIVE_TOPIC.getType(), ProtocolTypeEnum.JSON.getType(), body);
            ACCSManager.sendData(context, User.getUserId(), TVTAOBAO_SERVER_ID, data, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unRegisterTVLive(Context context, int type, String topic) {
        UnSubscribeTaobaoLiveTopic unSubscribe = new UnSubscribeTaobaoLiveTopic();
        unSubscribe.setType(type);
        unSubscribe.setTopic(topic);
        unSubscribe.setUserid(User.getUserId());
        byte[] body = unSubscribe.encode();
        mDiapatcher = null;

        try {
            byte[] data = ByteCodec.encode(BizTypeEnum.UNSUBSCRIBE_LIVE_TOPIC.getType(), ProtocolTypeEnum.JSON.getType(), body);
            ACCSManager.sendData(context, User.getUserId(), TVTAOBAO_SERVER_ID, data, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void eventMsg(byte[] data) {
        Protocol p = ByteCodec.decode(data);
        if (p == null)
            return;
        AppDebug.e("TvTaobaoReceviceService", "TvTaobaoReceviceService.eventMsg type = " + p.getBizType());
        if (p.getBizType() == BizTypeEnum.NOTIFY_LIVE_BLACKLIST.getType()) {
            DataResolver.notifyBlackList(p, mDiapatcher);
        }

        if (p.getBizType() == BizTypeEnum.NOTIFY_CNR_CHANGE_ITEM_ID.getType()) {
            DataResolver.notifyChangeItemId(p, mDiapatcher);
        }

        if (p.getBizType() == BizTypeEnum.NOTIFY_TVTAOBAO_UPDATE.getType()) {
            DataResolver.notifyUpdate(p);
        }

        if (p.getBizType() == BizTypeEnum.NOTIFY_TVTAOBAO_PROMOTION.getType()) {
            DataResolver.notifyPromotion(p);
        }

        if (p.getBizType() == BizTypeEnum.NOTIFY_CHANGE_ASR_CONFIG.getType()) {
            DataResolver.notifyASRConfig(p);
        }

        if (p.getBizType() == BizTypeEnum.NOTIFY_CHANFE_GLOBAL_CONFIG.getType()) {
            DataResolver.notifyGlobalConfig(p);
        }
    }

    public static void eventError(Map<String, String> map, int error) {
        if (mDiapatcher != null) {
            mDiapatcher.Error(map, error);
        }
    }
}
