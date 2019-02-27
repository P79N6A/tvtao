package com.tvtaobao.voice.services;

import android.os.RemoteException;

import com.bftv.fui.constantplugin.TellCode;
import com.bftv.fui.tell.Tell;
import com.bftv.fui.tell.TellManager;
import com.bftv.fui.thirdparty.IRemoteVoice;
import com.bftv.fui.thirdparty.RomoteVoiceService;
import com.bftv.fui.thirdparty.VoiceFeedback;
import com.tvtaobao.voice.VoiceUtils;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.voice.utils.LogPrint;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by pan on 2017/8/2.
 * 与暴风通信Service
 * AIDL
 */

public class BftvAsrService extends RomoteVoiceService {
    private final String TAG = "BftvAsrService";
    private VoiceUtils mVoice;
    private Listener mListener;

    @Override
    public void onCreate() {
        super.onCreate();
        mVoice = VoiceUtils.getInstance();
        mVoice.setContext(this);
        mListener = new Listener();
        registeredTell();
    }

    /**
     * 暴风通过AIDL与我们进行数据上的通信。
     * 可以去了解一下AIDL通行方式。
     * @param s ASR的内容
     * @param s1 JSON数据，用户说ASR之后，暴风做好处理，给我们需要的JSON
     * @param iRemoteVoice
     */
    @Override
    public void send(String s, String s1, final IRemoteVoice iRemoteVoice) {
        LogPrint.e(TAG, TAG + ".send s : " + s + " ,s1 : " + s1);
        mListener.setIRemoteVoice(iRemoteVoice);
        mVoice.handleInput(s, s1, mListener);
    }

    /**
     * 数据处理好之后，通过VoiceListener返回。
     * 然后通过IRemoteVoice，将我们转换好的数据格式，返回给暴风。
     */
    class Listener implements VoiceListener {

        private IRemoteVoice iRemoteVoice;
        private void setIRemoteVoice(IRemoteVoice iRemoteVoice) {
            this.iRemoteVoice = iRemoteVoice;
        }

        @Override
        public void onVoiceResult(VoiceFeedback back) {
            LogPrint.w(TAG, TAG + "onVoiceResult back : " + back);
            try {
                iRemoteVoice.sendMessage(back);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void registeredTell() {
        Tell tell = new Tell();
        ConcurrentHashMap<String, String> hashMap = new ConcurrentHashMap<String, String>();
        hashMap.put("取消订单", "取消订单");
        hashMap.put("继续", "继续");
        hashMap.put("继续购买", "继续购买");
        hashMap.put("立即购买", "立即购买");
        hashMap.put("加入购物车", "加入购物车");
        hashMap.put("确认下单", "确认下单");
        hashMap.put("图文详情", "图文详情");
        hashMap.put("宝贝评价", "宝贝评价");
        hashMap.put("评价", "评价");
        hashMap.put("下一步", "下一步");
        hashMap.put("去结算", "去结算");
        hashMap.put("离开", "离开");
        hashMap.put("再逛逛", "再逛逛");
        hashMap.put("查看店铺", "查看店铺");
        hashMap.put("去店铺", "去店铺");
        hashMap.put("进店铺", "进店铺");
        hashMap.put("优惠券", "优惠券");
        hashMap.put("分享", "分享");
        hashMap.put("是", "是");
        hashMap.put("是的", "是的");
        hashMap.put("确定", "确定");
        hashMap.put("确认", "确认");
        hashMap.put("好的", "确认");
        hashMap.put("买", "买");
        tell.setAppCacheMap(hashMap);
        tell.pck = AppInfo.getPackageName();
        tell.tellType = TellCode.TELL_APP_CACHE;
        TellManager.getInstance().tell(BftvAsrService.this.getApplication(), tell);
    }
}
