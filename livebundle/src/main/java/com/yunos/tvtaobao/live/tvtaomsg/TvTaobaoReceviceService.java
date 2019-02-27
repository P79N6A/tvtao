package com.yunos.tvtaobao.live.tvtaomsg;

//import com.taobao.accs.base.TaoBaseService;
//import com.yunos.tv.core.common.AppDebug;
//
//import java.util.HashMap;
//import java.util.Map;

/**
 * Created by pan on 16/12/2.
 */

public class TvTaobaoReceviceService{
//        extends TaoBaseService {
//    private String TAG = "TvTaobaoReceviceService";
//    private Map<String, String> map = new HashMap<>();
//    @Override
//    public void onData(String s, String s1, String s2, byte[] bytes, ExtraInfo extraInfo) {
//        AppDebug.d(TAG, "onData s : " + s + " ,s1 : " + s1 + " ,s2 : " + s2 + " ,bytes : " + (new String(bytes)));
//        TvTaobaoMsgService.eventMsg(bytes);
//    }
//
//    @Override
//    public void onBind(String s, int i, ExtraInfo extraInfo) {
//        AppDebug.d(TAG, "onBind s : " + s + " ,i : " + i);
//        if (i != 200) {
//            map.clear();
//            map.put("serviceId", s);
//            map.put("Type", "onBind");
//            TvTaobaoMsgService.eventError(map, i);
//        }
//    }
//
//    @Override
//    public void onUnbind(String s, int i, ExtraInfo extraInfo) {
//        AppDebug.d(TAG, " onUnbind s : " + s + " ,i : " + i);
//        if (i != 200) {
//            map.clear();
//            map.put("serviceId", s);
//            map.put("Type", "onUnbind");
//            TvTaobaoMsgService.eventError(map, i);
//        }
//    }
//
//    @Override
//    public void onSendData(String s, String s1, int i, ExtraInfo extraInfo) {
//        AppDebug.d(TAG, "onSendData s : " + s + " ,s1 : " + s1 + " ,i : " + i);
//        if (i != 200) {
//            map.clear();
//            map.put("serviceId", s);
//            map.put("Type", "onSendData");
//            TvTaobaoMsgService.eventError(map, i);
//        } else {
//            if (extraInfo != null && extraInfo.extHeader != null) {
//                Map<ExtHeaderType, String> extHeader = extraInfo.extHeader;
//                AppDebug.e(TAG, "fromPackage : " + extraInfo.fromPackage + " ,fromHost : " + extraInfo.fromHost);
//                for (ExtHeaderType type : extHeader.keySet()) {
//                    AppDebug.e(TAG, "type : " + type.ordinal());
//                }
//            }
//        }
//    }
//
//    @Override
//    public void onResponse(String s, String s1, int i, byte[] bytes, ExtraInfo extraInfo) {
//        AppDebug.d(TAG, "onResponse s : " + s + " ,s1 : " + s1 + " ,i : " + i + " ,bytes : " + (new String(bytes)));
//        if (i != 200) {
//            map.clear();
//            map.put("serviceId", s);
//            map.put("Type", "onResponse");
//            TvTaobaoMsgService.eventError(map, i);
//        }
//    }
}
