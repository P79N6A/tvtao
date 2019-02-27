package com.yunos.tvtaobao.biz.request.item.alimama;


import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.util.RSASign;
import com.yunos.tvtaobao.biz.request.base.BaseHttpRequest;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vincent on 1/16/17.
 * 领取阿里妈妈商品优惠券
 */

public class ApplyCoupon extends BaseHttpRequest {

    public static final String TAG = ApplyCoupon.class.getSimpleName();

    public String itemId = "";
    public String couponKey = "";
    public String userId = "";
    public String pid = "";

    //加密使用的公钥
    private final static byte[] pubKeyBytes = {48, -126, 1, 34, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3, -126, 1, 15, 0, 48, -126, 1, 10, 2, -126, 1, 1, 0, -67, 54, -128, -94, -106, 16, 25, 66, -23, 98, -81, -28, -38, 90, 61, 119, -61, -85, 122, -104, 53, -30, -92, 10, 38, 80, 75, 81, 62, -108, 11, -36, -69, 58, -28, 28, 25, 4, 34, -36, 91, -69, -8, 61, -93, 127, 115, 101, 60, -77, 89, -90, -105, 28, -127, -8, -3, 49, -22, -55, 71, 76, 24, -5, 74, -14, -3, 83, 126, 10, -44, -83, -44, 103, 51, -92, -35, -71, 53, 46, 116, -44, 33, -61, -74, -82, 66, -12, -36, 9, 105, -21, -61, -66, -117, -12, -4, -87, 56, 12, 47, 108, -88, -87, 67, 89, -116, 71, 4, -104, 35, 105, -17, -60, -97, 22, 50, 3, 30, 98, -2, -47, 64, -68, 88, 126, -90, 79, 96, -9, -111, 53, 29, 70, 122, -72, 85, -10, -28, -27, 10, 10, 12, -87, -117, 122, -36, -13, -96, -8, -78, -11, -44, 127, -93, -10, 118, -11, -43, 101, 58, -23, 122, 127, -93, 85, -96, 99, -55, 31, 55, -68, 89, 34, -92, -25, 17, 71, 108, -83, -45, -119, 39, -8, -57, 21, -113, 114, -17, -76, 35, 2, 56, -103, -128, -112, 29, 51, -122, -124, 20, 86, 76, -116, -48, 11, 58, -47, -29, 11, -27, -92, -41, 56, -19, 67, 45, -73, -31, 46, 89, -117, 82, -15, 63, -56, -117, 53, 49, 33, -110, -106, 97, -108, -84, 64, 43, 85, -44, 15, 123, -20, -37, 85, 34, -61, 47, 111, -25, -103, -37, -88, 111, 114, -89, 33, 2, 3, 1, 0, 1};
    //optional
    private final static String apiVersion = "1.0";
    //optional
    private String reqTimeStamp = "";

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getCouponKey() {
        return couponKey;
    }

    public void setCouponKey(String conponKey) {
        this.couponKey = conponKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }


    public ApplyCoupon(String itemId, String couponKey, String userId) {
        this.itemId = itemId;
        this.couponKey = couponKey;
        this.userId = userId;
        this.pid = "mm_113859958_12524229_70604430";
    }

    public ApplyCoupon(String itemId, String pid, String couponKey, String userId) {
        this.itemId = itemId;
        this.couponKey = couponKey;
        this.userId = userId;
        this.pid = pid;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String resolveResult(String result) throws Exception {
        AppDebug.v(TAG, "response: " + result);
        return result;
    }

    @Override
    protected Map<String, String> getAppData() {
        Map<String, String> params = new HashMap<String, String>();
        Long tsLong = System.currentTimeMillis() / 1000;
        this.reqTimeStamp = tsLong.toString();
        String encryptedData = encryptBizParams();
        AppDebug.v(TAG, "encryptedData:=====\n" + encryptedData);
        params.put("apiVersion", apiVersion);
        params.put("timestamp", reqTimeStamp);
        params.put("encryptStr", encryptedData);
        //remove decrypt codes
        //AppDebug.v(TAG, "decryptedData:=====\n" + decryptBizParams(encryptedData));
        return params;
    }

    @Override
    protected String getHttpDomain() {
        //TODO: 正式/预发接口地址
//        return "http://30.11.252.15:10000/cp/coupon_apply";
          return "http://uland.taobao.com/cp/outer_coupon_apply";
//        return "http://140.205.215.130/cp/outer_coupon_apply";
    }

    @Override
    public String toString() {
        //TODO:
        return null;
    }

    @Override
    public List<Header> getHttpHeader() {
        //NOTE: @亦纯说post请求需要校验session，添加ajax的请求头可以忽略校验
        //X-Requested-With:XMLHttpRequest
        List<Header> headers = new ArrayList<Header>();
        Header xhrHeader = new BasicHeader("X-Requested-With", "XMLHttpRequest");
        headers.add(xhrHeader);
        return headers;
    }

    private String encryptBizParams(){
        StringBuilder builder = new StringBuilder();
        builder.append("itemId=");
        builder.append(this.itemId);

        builder.append("&couponKey=");
        builder.append(this.couponKey);

        builder.append("&userId=");
        builder.append(this.userId);

        builder.append("&pid=");
        builder.append(this.pid);

        builder.append("&ts=");
        builder.append(this.reqTimeStamp);

        String bizParams = builder.toString();
        String encryptedBizParams = "";
        try{
            encryptedBizParams = RSASign.encryptWithPubkey(pubKeyBytes, bizParams);
            encryptedBizParams = URLEncoder.encode(encryptedBizParams, "UTF-8");
        }catch(Exception e){
            AppDebug.v(TAG, "encrypted error:" + e.getMessage());
            e.printStackTrace();
        }
        return encryptedBizParams;
    }

    private String decryptBizParams(String encryptedData){
        String decryptedBizParams = "";
        /*
        byte[] privateKey = {48, -126, 4, -66, 2, 1, 0, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 4, -126, 4, -88, 48, -126, 4, -92, 2, 1, 0, 2, -126, 1, 1, 0, -67, 54, -128, -94, -106, 16, 25, 66, -23, 98, -81, -28, -38, 90, 61, 119, -61, -85, 122, -104, 53, -30, -92, 10, 38, 80, 75, 81, 62, -108, 11, -36, -69, 58, -28, 28, 25, 4, 34, -36, 91, -69, -8, 61, -93, 127, 115, 101, 60, -77, 89, -90, -105, 28, -127, -8, -3, 49, -22, -55, 71, 76, 24, -5, 74, -14, -3, 83, 126, 10, -44, -83, -44, 103, 51, -92, -35, -71, 53, 46, 116, -44, 33, -61, -74, -82, 66, -12, -36, 9, 105, -21, -61, -66, -117, -12, -4, -87, 56, 12, 47, 108, -88, -87, 67, 89, -116, 71, 4, -104, 35, 105, -17, -60, -97, 22, 50, 3, 30, 98, -2, -47, 64, -68, 88, 126, -90, 79, 96, -9, -111, 53, 29, 70, 122, -72, 85, -10, -28, -27, 10, 10, 12, -87, -117, 122, -36, -13, -96, -8, -78, -11, -44, 127, -93, -10, 118, -11, -43, 101, 58, -23, 122, 127, -93, 85, -96, 99, -55, 31, 55, -68, 89, 34, -92, -25, 17, 71, 108, -83, -45, -119, 39, -8, -57, 21, -113, 114, -17, -76, 35, 2, 56, -103, -128, -112, 29, 51, -122, -124, 20, 86, 76, -116, -48, 11, 58, -47, -29, 11, -27, -92, -41, 56, -19, 67, 45, -73, -31, 46, 89, -117, 82, -15, 63, -56, -117, 53, 49, 33, -110, -106, 97, -108, -84, 64, 43, 85, -44, 15, 123, -20, -37, 85, 34, -61, 47, 111, -25, -103, -37, -88, 111, 114, -89, 33, 2, 3, 1, 0, 1, 2, -126, 1, 0, 78, 46, 33, -14, -117, -4, -76, -29, 95, -39, -122, 2, 18, 114, -84, -23, 58, 113, 53, 35, -123, 72, 83, 45, 90, 109, 92, -31, -127, -16, -36, 1, -27, 94, -52, -8, 11, 34, 25, 97, 97, -118, 6, 101, 57, -108, 36, -45, 20, -60, -86, 107, 90, 14, -50, 105, 89, -4, -15, 29, 31, -105, -126, -50, -6, 69, -14, -124, 56, 21, 51, -111, 107, -83, 4, -70, -65, 94, -110, 105, -46, -98, 0, 33, -124, -18, -11, -3, -124, -103, 99, 41, -90, 100, -86, 33, -100, -16, 50, -105, 94, 85, 16, 79, 119, -29, -99, 65, -114, -43, 105, -12, -112, -68, 118, 91, 78, 104, -39, 122, 95, -3, -120, 57, 10, 123, -78, 45, 109, -77, 103, 57, -41, -84, -88, 1, 49, -89, 35, -115, -10, 6, 87, 99, -122, -126, 29, -127, -65, -66, -74, 22, 24, -60, -4, -63, -44, -105, 58, -74, -42, -17, 103, -50, 103, -45, 84, 26, -19, 24, -92, 82, -3, 25, 46, -67, 63, -33, 123, 114, 17, 40, -78, 9, 76, -24, -42, -117, 77, 21, -29, -61, 49, -57, -53, -108, -64, -14, -111, -6, 96, -17, -6, -110, -48, 40, 30, 118, 82, 113, -80, 10, -30, -46, 101, 82, -47, 94, -117, 9, -52, 61, -29, 8, -110, 64, 116, -36, -102, -62, 12, 126, -100, -93, -99, 79, -45, -20, -18, -59, -108, 111, -120, -57, 113, -86, -68, -122, -71, -113, -85, -118, 0, 84, 58, -115, 2, -127, -127, 0, -16, -109, 27, 23, -108, 16, -67, 37, -83, 17, 22, 1, 116, -125, -128, 106, -61, 49, -10, 91, -26, -42, 51, 86, 47, -50, -52, -100, -97, 121, -15, -81, -112, -123, -31, 63, -5, 127, 3, -118, -63, 12, 115, 56, -11, -50, 0, -6, -82, 56, 90, 24, 2, -81, 110, 19, -56, -14, 42, 89, -64, 67, -68, -99, -44, -5, -52, 45, 96, -115, -74, -83, 2, -122, -59, 120, -91, 22, 46, 53, 57, -25, 68, 15, -120, 108, -119, 23, 114, -27, -126, 84, 17, 44, -108, -60, -52, 121, 78, -71, 6, 110, -84, 48, -35, -5, -108, 68, -69, -122, -3, 88, -11, 122, -87, -60, 27, -107, 84, -4, -62, -95, -109, 7, 108, 14, 69, 35, 2, -127, -127, 0, -55, 88, 82, -56, -68, 116, 81, -127, -9, -79, 47, -83, 31, -27, -10, 25, -42, 122, 117, 33, 20, 74, -97, 68, 113, -107, 39, -53, -109, -51, -80, -59, -108, 101, -85, -28, -76, 50, 109, -56, -60, -31, 48, 21, 17, 64, 61, -110, 126, -122, 86, -57, -90, -112, -40, 69, 8, -37, -118, -91, -53, -92, -118, 10, -83, 59, -89, -40, 64, -103, -33, 85, -35, 113, 75, -19, -98, 31, -46, 107, 101, -46, -123, 40, -35, 67, -54, -86, -56, 86, 89, -8, -91, 77, -37, 17, -45, 101, -37, -78, 60, -99, -7, -18, 108, 74, -50, -101, 93, 98, 125, -55, 23, 44, 108, 34, 21, 79, -107, 45, 98, 67, -61, 22, 95, -51, 16, -21, 2, -127, -127, 0, -118, 110, 43, -123, -70, -58, -109, 0, 110, 0, -55, -25, -42, 114, -96, -33, 78, 40, -98, -71, 45, 48, 14, 100, 70, -88, 57, -90, -38, 65, 98, 40, 3, 2, 118, -91, -55, -126, -63, -72, 2, -13, -74, 13, -115, -85, -105, 118, -89, -17, 119, 48, 31, -61, 112, -108, -29, -23, 45, 35, 109, -11, -52, 64, 9, -63, 70, 48, -54, 65, 97, 95, 86, 119, -44, -125, 98, -125, 75, -35, -122, -49, 35, -93, -56, 125, -93, -71, 33, 125, 29, 44, 96, -95, -123, -115, 100, -105, 93, 12, 44, -66, 92, 72, -42, 67, 32, -125, 24, 97, 73, -64, -7, -88, 54, -83, -56, -83, 122, 52, -29, -115, -13, -69, -82, -102, -37, 127, 2, -127, -128, 33, 94, 22, -46, 23, -111, 59, 84, -53, -92, -68, -98, -9, 83, 87, -10, -82, 57, -69, 33, -111, 72, 80, -98, -70, 40, 26, -31, 61, 12, 112, -120, 61, -16, -97, -115, 22, 99, -23, 23, -59, -79, 0, 19, -19, -72, -12, -69, 75, 102, -98, 9, 22, 76, -104, -38, -49, -3, -6, 59, 65, -59, 113, -17, 21, 8, 118, -25, 122, -59, 69, 98, -24, -128, -9, 49, 88, -20, 51, 38, 3, 72, -31, 50, -123, -128, -49, -100, 127, -8, -9, -114, 34, 111, 5, -88, 45, -99, -118, -52, 62, 9, 124, 59, 12, 13, 85, -78, 31, -58, -53, -53, -64, -96, 103, 7, 58, -6, -26, 97, 16, 16, -28, -54, -41, 115, -105, -93, 2, -127, -127, 0, -78, 29, 112, -105, -56, -3, 122, 83, -5, 55, 92, 85, 87, 99, 109, -26, -121, -110, -75, 122, -107, -57, 106, 111, 122, 97, -56, 35, 24, 87, -89, 26, 115, 114, -96, -37, 32, 6, -17, 30, -14, -127, 32, 13, -117, -115, -81, -112, -120, 114, -128, -23, -100, -121, -122, -114, 108, 58, -72, 73, -41, -64, 12, 58, 67, -22, 37, -88, 75, 108, 67, 112, -113, -23, -92, -71, 49, 8, -83, 23, -44, -34, 26, 105, 60, -117, -43, -106, -124, 32, -21, -34, -101, 36, -37, 125, 73, 90, -2, -100, 55, 22, 118, -38, -53, -125, 126, 108, -23, 38, 115, 123, -101, -3, 93, -83, -105, -122, -105, -85, -30, 110, 116, 24, 7, -43, 27, -127};
        try{
            encryptedData = URLDecoder.decode(encryptedData, "UTF-8");
            decryptedBizParams = RSASign.decryptWithPrivatekey(privateKey, encryptedData);
        }
        catch(Exception e){
            e.printStackTrace();
            AppDebug.v(TAG, "decrypted error:" + e.getMessage());
        }*/
        return decryptedBizParams;
    }
}