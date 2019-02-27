package com.yunos.tvtaobao.biz.request.item;


import com.yunos.tvtaobao.biz.request.base.BaseHttpRequest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by huangdaju on 16/9/20.
 */
public class MachineLoginRequest extends BaseHttpRequest {

    private String appKey = "";
    private String appSecret = "alitv";
    private String machineId = "";
    private String sign = "";

    public MachineLoginRequest(String appKey, String machineId) {
        this.appKey = appKey;
        this.machineId = machineId;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String resolveResult(String result) throws Exception {
        return result;
    }

    @Override
    protected Map<String, String> getAppData() {
        Map<String, String> params = new TreeMap<String, String>();
        params.put("app_key", appKey);
        params.put("user_id", machineId);
        String sign = createSign(params);
        params.put("sign", sign);

        return params;
    }

    @Override
    protected String getHttpDomain() {
        return "http://218.108.132.66:7430/api/v2/alitv/get_session";
    }


    protected String createSign(Map<String, String> params) {
        //Date date = DateUtils.changeTimeZone(new Date(), DateUtils.getTimeZone(), TimeZone.getTimeZone("GMT+8"));
        //timeStamp = DateUtils.formatDate(date, "yyyyMMddHHmmss");
        //String appKey = Utils.metaData(mContext);
        StringBuffer sbBuffer = new StringBuffer();
        sbBuffer.append(appSecret);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sbBuffer.append(entry.getKey()).append(entry.getValue());
        }
        sbBuffer.append(appSecret);
        String md5Url = sbBuffer.toString();
        sign = getMD5(md5Url);
        return sign;
    }


    private String getMD5(String str) {
        String reStr = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(str.getBytes());
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : bytes) {
                int bt = b & 0xff;
                if (bt < 16) {
                    stringBuffer.append(0);
                }
                stringBuffer.append(Integer.toHexString(bt));
            }
            reStr = stringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return reStr.toUpperCase();
    }
}
