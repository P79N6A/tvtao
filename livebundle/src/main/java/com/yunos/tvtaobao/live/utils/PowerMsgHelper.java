package com.yunos.tvtaobao.live.utils;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.widget.Toast;
//
//import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
//import com.taobao.tao.messagekit.core.model.BaseMessage;
//import com.taobao.tao.powermsg.common.Constant;
//import com.taobao.tao.powermsg.common.CountPowerMessage;
//import com.taobao.tao.powermsg.common.IPowerMsgCallback;
//import com.taobao.tao.powermsg.common.IPowerMsgDispatcher;
//import com.taobao.tao.powermsg.common.PowerMessage;
//import com.taobao.tao.powermsg.common.PowerMsgService;
//import com.taobao.tao.powermsg.common.TextPowerMessage;
////import com.taobao.tao.powermsg.test.biz.protocol.base.nano.BaseMessage;
//import com.yunos.tv.core.common.AppDebug;
//import com.yunos.tv.core.common.User;
//import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
//import com.yunos.tvtaobao.biz.request.info.GlobalConfigInfo;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.lang.ref.WeakReference;
//import java.util.HashMap;
//import java.util.Map;

/**
 * Created by pan on 16/11/29.
 */

public class PowerMsgHelper {
//    private static String TAG = PowerMsgHelper.class.getName();
//    private static PowerMsgHelper powerMsgHelper;
//    private static int bizCode = 1;
//
//    private WeakReference<Handler> mHandler;
//
//    public void setHandler(Handler handler) {
//        if (handler == null)
//            mHandler = null;
//        this.mHandler = new WeakReference(handler);
//    }
//
//    private IPowerMsgDispatcher dispatcher = new IPowerMsgDispatcher() {
//        @Override
//        public void onDispatch(PowerMessage powerMessage) {
//            if (mHandler != null && mHandler.get() != null)
//                toType(mHandler.get(), powerMessage);
//        }
//
//        @Override
//        public void onError(int i, Object o) {
//            AppDebug.e(TAG, "powerMessage -> onError i :" + i + " , o : " + o);
//        }
//    };
//
//    public void registerBizCode(int bizCode){
//        PowerMsgService.registerDispatcher(bizCode, dispatcher);
//    }
//
//
//    public static PowerMsgHelper getInstance() {
//        if (powerMsgHelper == null)
//            powerMsgHelper = new PowerMsgHelper();
//
//        return powerMsgHelper;
//    }
//
//
//    public static void toType(Handler handler, PowerMessage powerMessage) {
//        Message msg = new Message();
//        int type = powerMessage.type;
//        String data = null;
//        try {
//                /*
//                * 跟服务端约定的数据结构来解析powerMessage.data (byte[])
//                * 下面使用的protoBuff 也可以使用json
//                * */
//            if (type == Constant.SubType.textMsg) {
//                TextPowerMessage msgText = (TextPowerMessage) powerMessage;
//                if (msgText.text.equals("⁂∰⏇follow")) {
//                    data = powerMessage.from;
//
//                    msg.what = PowerMsgType.LIVE_ATTENTION;
//                    msg.obj = data;
//                } else {
//                    Bundle bundle = new Bundle();
//                    bundle.putString("nick", powerMessage.from);
//                    bundle.putString("comment", msgText.text);
//                    msg.what = Constant.SubType.textMsg;
//                    msg.setData(bundle);
//                }
//            } else if (type == PowerMsgType.systemMsg
//                    || type == PowerMsgType.studioMsg
//                    || type == PowerMsgType.bizMsg) {
//                try {
//                    data = new String(powerMessage.data);
//                    JSONObject object = new JSONObject(data);
//                    String studioType = object.getString("type");
//                    if (studioType.equals("liveGift")) {
//                        String nick = object.getString("senderNick");
//                        String gift = object.getString("taskId");
//                        String num = object.getString("comboNum");
//
//                        msg.obj = nick + "送出" + num + "个" + gift;
//                        msg.what = PowerMsgType.LIVE_GIFT;
//                    } else if (studioType.equals("liveVideoStreamBreak")) {
//
//                        msg.what = PowerMsgType.LIVE_STREAM_BREAK;
//                    } else if (studioType.equals("liveVideoStreamRestore")) {
//
//                        msg.what = PowerMsgType.LIVE_STREAM_RESTORE;
//                    } else if (studioType.equals("endLiveVideo")) {
//                        msg.what = PowerMsgType.LIVE_STREAM_END;
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            } else if (type == PowerMsgType.joinMsg) {
//                //进出群
////                String userId = "";
////                BaseMessage.JoinNotify notify = BaseMessage.JoinNotify.parseFrom(powerMessage.data);
////                if (notify.addUsers != null) {
////                    for (Map.Entry<String, String> item : notify.addUsers.entrySet()) {
////                        userId = item.getKey();
////                    }
////                }
////
////                Bundle bundle = new Bundle();
////                bundle.putString("onlineCount", notify.onlineCount + "");
////                bundle.putString("joinUserId", userId);
////                bundle.putString("joinFrom", powerMessage.from);
////                msg.what = PowerMsgType.joinMsg;
////                msg.setData(bundle);
//            } else if (type == Constant.SubType.dig) {
//                //点赞消息下发
//                CountPowerMessage item = (CountPowerMessage) powerMessage;
//                Long dig = null;
//                if (null != item.value) {
//                    dig = item.value.get(PowerMsgType.KEY_FAVOR);
//                } else {
//                    return;
//                }
//                data = dig + "";
//
//                msg.what = Constant.SubType.dig;
//                msg.obj = data;
//            } else if (type == PowerMsgType.shareMsg) {
//                //商品消息下发
////                BaseMessage.ShareMessage item = BaseMessage.ShareMessage.parseFrom(powerMessage.data);
////                data = "title=" + item.title + "+" + item.content;
//            } else if (type == PowerMsgType.tradeShowMsg) {
//                data = powerMessage.from;
//
//                msg.what = PowerMsgType.tradeShowMsg;
//                msg.obj = data;
//            }
//
//            handler.sendMessage(msg);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void addFav(String topic, final Double favs) {
//        PowerMsgService.countValue(bizCode, topic, new HashMap<String, Double>() {{
//            put(PowerMsgType.KEY_FAVOR, favs);
//        }}, false, new IPowerMsgCallback() {
//            @Override
//            public void onResult(int i, Map<String, Object> map, Object... objects) {
//            }
//        });
//    }
//
//    public static void sendCommit(Context context, String topic, String text) {
//        if (User.getUserId() == null) {
//            Toast.makeText(context, "您还没有登陆,请先登陆", Toast.LENGTH_LONG).show();
//        } else {
//            GlobalConfig gc = GlobalConfigInfo.getInstance().getGlobalConfig();
//            if (gc != null && gc.getTMallLive() != null && gc.getTMallLive().postfix() != null) {
//                text += gc.getTMallLive().postfix();
//            }
//
//            TextPowerMessage msg = new TextPowerMessage();
//            msg.bizCode = bizCode;
//            msg.topic = topic;
//            msg.text = text;
////                msg.needAck = true;
//            msg.userId = User.getUserId();
//            msg.from = User.getNick();
//
//            PowerMsgService.sendText(1, msg, new IPowerMsgCallback() {
//                @Override
//                public void onResult(int i, Map<String, Object> map, Object... objects) {
//                    AppDebug.e(TAG, "powerMessage -> sendText i :" + i + " , map : " + map);
//                }
//            });
//        }
//    }
}
