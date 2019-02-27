package com.yunos.tvtaobao.tvlive.tvtaomsg.utility;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.tvlive.tvtaomsg.enums.BizTypeEnum;
import com.yunos.tvtaobao.tvlive.tvtaomsg.listener.NotifyListener;
import com.yunos.tvtaobao.tvlive.tvtaomsg.po.Protocol;
import com.yunos.tvtaobao.tvlive.tvtaomsg.po.TVTaoMessage;
import com.yunos.tvtaobao.tvlive.tvtaomsg.po.json.NotifyCnrChangeItemId;
import com.yunos.tvtaobao.tvlive.tvtaomsg.po.json.NotifyLiveBlackList;
import com.yunos.tvtaobao.tvlive.tvtaomsg.service.ITVTaoDiapatcher;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;

/**
 * Created by pan on 2017/6/20.
 */

public class DataResolver {

    private static WeakReference<NotifyListener> notifyListener;
    public static void setNotifyListener(NotifyListener listener) {
        notifyListener = listener == null ? null : new WeakReference<NotifyListener>(listener);
        AppDebug.d("TvTaobaoReceviceService", "TvTaobaoReceviceService.setNotifyListener listener : " + notifyListener);
    }

    public static void notifyBlackList(Protocol p, ITVTaoDiapatcher diapatcher) {
        NotifyLiveBlackList biz = new NotifyLiveBlackList();
        biz.decode(p.getData());

        if (diapatcher != null) {
            TVTaoMessage msg = new TVTaoMessage();
            msg.type = BizTypeEnum.NOTIFY_LIVE_BLACKLIST.getType();
            msg.topic = biz.getTopic();
            diapatcher.Diapatcher(msg);
        }
    }

    public static void notifyChangeItemId(Protocol p, ITVTaoDiapatcher diapatcher) {
        NotifyCnrChangeItemId biz = new NotifyCnrChangeItemId();
        biz.decode(p.getData());

        if (diapatcher != null) {
            TVTaoMessage msg = new TVTaoMessage();
            msg.type = BizTypeEnum.NOTIFY_CNR_CHANGE_ITEM_ID.getType();
            msg.topic = biz.getTopic();
            msg.tid = biz.getTid();
            diapatcher.Diapatcher(msg);
        }
    }

    public static void notifyUpdate(Protocol p) {
        try {
            JSONObject json = JSON.parseObject(new String(p.getData(),"UTF-8"));
            AppDebug.d("TvTaobaoReceviceService", "TvTaobaoReceviceService.notifyUpdate notifyListener = " + notifyListener);
            if (notifyListener != null && notifyListener.get() != null)
                notifyListener.get().notifyUpdate();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void notifyPromotion(Protocol p) {
        try {
            JSONObject json = JSON.parseObject(new String(p.getData(),"UTF-8"));
            if (notifyListener != null && notifyListener.get() != null)
                notifyListener.get().notifyPromotion(json.getString("note"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void notifyASRConfig(Protocol p) {
        try {
            JSONObject json = JSON.parseObject(new String(p.getData(),"UTF-8"));
            AppDebug.d("TvTaobaoReceviceService", "TvTaobaoReceviceService.notifyASRConfig notifyListener = " + notifyListener);
            if (notifyListener != null && notifyListener.get() != null)
                notifyListener.get().notifyASRConfig();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void notifyGlobalConfig(Protocol p) {
        try {
            JSONObject json = JSON.parseObject(new String(p.getData(),"UTF-8"));
            AppDebug.d("TvTaobaoReceviceService", "TvTaobaoReceviceService.notifyGlobalConfig notifyListener = " + notifyListener);
            if (notifyListener != null && notifyListener.get() != null)
                notifyListener.get().notifyGlobalConfig();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
