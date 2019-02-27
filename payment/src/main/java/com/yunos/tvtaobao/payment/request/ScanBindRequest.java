package com.yunos.tvtaobao.payment.request;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.yunos.tvtaobao.payment.utils.CloudUUIDWrapper;

import org.json.JSONException;
import org.json.JSONObject;

import mtopsdk.mtop.domain.MtopRequest;

/**
 * <pre>
 *     author : xutingting
 *     e-mail : xutingting@zhiping.tech
 *     time   : 2018/01/10
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ScanBindRequest extends MtopRequest {
    private String API = "mtop.taobao.tvtao.user.bind";
    private String API_VERSION = "1.0";
    private static String appKey;

    public ScanBindRequest(Context context) {
        setApiName(API);
        setVersion(API_VERSION);
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        JSONObject data = new JSONObject();
        try {
            data.put("uuid", CloudUUIDWrapper.getCloudUUID());
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            data.put("deviceId", tm.getDeviceId());
            data.put("appKey", appKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setData(data.toString());
        setNeedEcode(false);
        setNeedSession(false);
    }


    public static void setAppKey(String appKeyName) {
        appKey = appKeyName;

    }

//    public String getAppkey(Context mContext) {
//        ContextWrapper contextWrapper = new ContextWrapper(mContext);
//        StaticDataStore dataStore = new StaticDataStore(contextWrapper);
//        String appkey = dataStore.getAppKey(new DataContext(0, null));
//        Log.d("APPKEY", "test adk getappkey=" + appkey);
//        return appkey;
//    }


}
