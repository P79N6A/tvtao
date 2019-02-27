/**
 * $
 * PROJECT NAME: TVTaobao
 * PACKAGE NAME: com.yunos.tvtaobao.h5
 * FILE NAME: BaseMtopRequest.java
 * CREATED TIME: 2016年1月13日
 * COPYRIGHT: Copyright(c) 2013 ~ 2016 All Rights Reserved.
 */
package com.yunos.tvtaobao.biz.request.base;


import android.text.TextUtils;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.MtopRequestConfig;
import com.yunos.tv.core.util.DataEncoder;
import com.yunos.tvtaobao.biz.request.core.DataRequest;
import com.yunos.tvtaobao.biz.request.core.DataRequestType;
import com.yunos.tvtaobao.biz.request.core.ServiceCode;
import com.yunos.tvtaobao.biz.request.core.ServiceResponse;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mtopsdk.mtop.util.ReflectUtil;

public abstract class BaseMtopRequest extends DataRequest {

    private static final long serialVersionUID = -8470193626187609708L;

    public BaseMtopRequest() {
        dataParams = new HashMap<String, String>();
//        dataParams.put("tb_eagleeyex_scm_project","20180822-aone2-join-tvtaotag");

    }

    /**
     * 增加参数
     *
     * @param key
     * @param value
     */
    public void addParams(String key, String value) {
        if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {

            dataParams.put(key, value);
        }
    }

    /**
     * 设置参数信息，在请求前必须调用
     */
    public void setParamsData() {
        initialize();
        setData(ReflectUtil.converMapToDataStr(dataParams));
    }

    /**
     * 获取所有的参数
     */
    @Override
    protected void initialize() {
        if (!TextUtils.isEmpty(getApi())) {
            setApiName(getApi());
        }

        if (!TextUtils.isEmpty(getApiVersion())) {
            setVersion(getApiVersion());
        }

        setNeedEcode(getNeedEcode());
        setNeedSession(getNeedSession());

        //获取自定义参数
        Map<String, String> param = getAppData();
        if (param != null) {
            dataParams.putAll(param);
        }
    }

    @Override
    public DataRequestType getRequestType() {
        return DataRequestType.MTOP;
    }

    @Override
    protected List<? extends NameValuePair> getPostParameters() {
        initialize();
        List<BasicNameValuePair> paramList = new ArrayList<BasicNameValuePair>();
        for (Entry<String, String> entry : dataParams.entrySet()) {
            BasicNameValuePair param = new BasicNameValuePair(entry.getKey(), entry.getValue());
            paramList.add(param);
        }

        return paramList;
    }

    @Override
    protected String getHttpParams() {
        initialize();
        StringBuilder sb = new StringBuilder();
        int i = 0;
        int len = dataParams.size();
        for (Entry<String, String> entry : dataParams.entrySet()) {
            sb.append(entry.getKey()).append("=")
                    .append(DataEncoder.encodeUrl(DataEncoder.decodeUrl(entry.getValue())));
            if (i < (len - 1)) {
                sb.append("&");
            }
        }
        AppDebug.v(TAG, TAG + ".getHttpParams sb = " + sb.toString());
        return sb.toString();
    }

    @Override
    protected String getHttpDomain() {
        return MtopRequestConfig.getHttpDomain();
    }

    /**
     * 获取ttid，子类可以根据自己特定需求重写这个函数
     *
     * @return
     */
    public String getTTid() {
        return Config.getTTid();
    }

    /**
     * 获取json中的"data"字段
     *
     * @param json
     * @return
     */
    public JSONObject getDataElement(String json) {
        if (json == null) {
            return null;
        }

        JSONObject data = null;
        try {
            data = new JSONObject(json).optJSONObject("data");
        } catch (JSONException e) {
        }

        return data;
    }

    @SuppressWarnings("unchecked")
    public <T> ServiceResponse<T> resolveResponse(String response) throws Exception {
        ServiceResponse<T> serviceResponse = new ServiceResponse<T>();
        if (getApi().equals("mtop.taobao.tvtao.itemservice.getdetail")) {
            AppDebug.e("GetProductTagRequest", "response = " + response);
//            response ="{ \"api\": \"mtop.taobao.tvtao.itemservice.getdetail\",\"data\":{ \"couponType\":\"1\",\"tag\":{\"position\":\"1\",\"icon\":\"https://img.alicdn.com/tfsTB18LPNclfH8KJjy1XbXXbLdXXa-450-48.png\", \"outPreferentialId\": \"12\",\"isVip\": \"false\",\"cart\":\"0\",\"coupon\":\"1\",\"couponMessage\":\"定金全返返返返返返返返\",\"picUrl\":\"https://www.baidu.com/img/bd_logo1.png?qua=high\"},\"item\":{\"itemId\":\"558338337266\"}},\"ret\":[\"SUCCESS::调用成功\"],\"v\":\"1.0\"}";
        }

        // 解析返回结果的状态描述
        JSONObject json = new JSONObject(response);

        JSONArray retArray = json.optJSONArray("ret");
        if (retArray != null && retArray.length() > 0 && !retArray.toString().contains("SUCCESS")) {

            // 长度为1的情况
            if (retArray.length() == 1) {
                String[] flagStatus = retArray.getString(0).split("::");
                // 类型1 "ret":["FAIL::userNotLogin"]
                if (flagStatus.length == 1) {
                    if (flagStatus[0].equals("FAIL_BIZ_PERSON_LIMIT_EXCEED")) {// 如果是购买超过限制，没有对应的提示，得自己来处理
                        serviceResponse.update(ServiceCode.FAIL_BIZ_PERSON_LIMIT_EXCEED.getCode(), flagStatus[0],
                                ServiceCode.FAIL_BIZ_PERSON_LIMIT_EXCEED.getMsg());
                    } else {
                        // 这是为防止不规范的错误描述
                        serviceResponse.update(ServiceCode.API_ERROR.getCode(), flagStatus[0],
                                ServiceCode.API_ERROR.getMsg());
                    }
                } else {
                    if (flagStatus[1].equals("userNotLogin")) {
                        // 用户没有登录
                        serviceResponse.update(ServiceCode.API_NOT_LOGIN.getCode(), "userNotLogin",
                                ServiceCode.API_NOT_LOGIN.getMsg());
                    } else {
                        // 类型2 "ret":["ERR_SID_INVALID::SESSION过期"],"
                        if (flagStatus[0].equals("ERR_SID_INVALID") || flagStatus[0].equals("FAIL_SYS_SESSION_EXPIRED")
                                || flagStatus[0].equals("FAIL_SYS_ILEGEL_SIGN")) {
                            // 无效session
                            serviceResponse.update(ServiceCode.API_SID_INVALID.getCode(), "ERR_SID_INVALID",
                                    flagStatus[1]);
                        } else if (flagStatus[0].equals("FAIL_SYS_HSF_THROWN_EXCEPTION")) {
                            //HSF目标方法抛出异常
                            serviceResponse.update(ServiceCode.USER_LOGIN_OTHER_FAIL.getCode(),
                                    "FAIL_SYS_HSF_THROWN_EXCEPTION", flagStatus[1]);
                        } else if (flagStatus[0].equals("ERRCODE_AUTH_REJECT")) {
                            // 非法请求
                            serviceResponse.update(ServiceCode.API_ERRCODE_AUTH_REJECT.getCode(),
                                    "ERRCODE_AUTH_REJECT", flagStatus[1]);
                        } else if (flagStatus[0].equals("ADDRESS_EMPTY") || flagStatus[0].equals("NO_ADDRESS")) {
                            // 没有收获地址
                            serviceResponse.update(ServiceCode.NO_ADDRESS.getCode(), "ADDRESS_EMPTY", flagStatus[1]);
                        } else if (flagStatus[0].equals("NO_RATES")) {
                            // 没有评论
                            serviceResponse.update(ServiceCode.SERVICE_OK.getCode(), "NO_RATES", flagStatus[1]);
                        } else if (flagStatus[0].equals("ERROR_PARAM_DEVICE_ID")
                                || flagStatus[0].equals("ERROR_BIZ_NO_DEVICE_ID")
                                || flagStatus[0].equals("ERROR_DEVICE_NOT_FOUND")
                                || flagStatus[0].equals("ERROR_INVALID_DEVICE_ID")
                                || flagStatus[0].equals("ERROR_INVALID_PUSH_KEY")
                                || flagStatus[0].equals("ERROR_PARAM_PUSH_TOKEN")) {
                            // 需要重新注册DeviceId
                            serviceResponse.update(ServiceCode.PUSH_MESSAGE_ERROR_DEVICE_ID.getCode(),
                                    "PUSH_MESSAGE_ERROR_DEVICE_ID", flagStatus[1]);
                        } else if (flagStatus[0].equals("1142")) {
                            // 重复购买
                            serviceResponse.update(ServiceCode.REPEAT_CREATE.getCode(), "REPEAT_CREATE",
                                    ServiceCode.REPEAT_CREATE.getMsg());
                        } else if (flagStatus[0].equals("CREATE_ALIPAY_ORDER_ERROR")) {
                            //创建支付宝订单失败,可能是支付宝没有绑定或账号状态不对
                            serviceResponse.update(ServiceCode.CREATE_ALIPAY_ORDER_ERROR.getCode(),
                                    "CREATE_ALIPAY_ORDER_ERROR", ServiceCode.CREATE_ALIPAY_ORDER_ERROR.getMsg());
                        } else if (flagStatus[0].equals("NEED_CHECK_CODE")) {
                            // 需要校验码
                            serviceResponse.update(ServiceCode.USER_NEED_CHECK_CODE.getCode(), "NEED_CHECK_CODE",
                                    flagStatus[1]);
                        } else if (flagStatus[0].equals("BUYER_TOO_MANY_UNPAID_ORDERS")) {
                            // 您的未付款订单过多
                            serviceResponse.update(ServiceCode.ORDER_MORE.getCode(), "BUYER_TOO_MANY_UNPAID_ORDERS",
                                    flagStatus[1]);
                        } else if (flagStatus[0].equals("DUPLICATED_ORDER_ERROR")) {
                            // 请勿重复提交订单
                            serviceResponse.update(ServiceCode.DUPLICATED_ORDER_ERROR.getCode(), "DUPLICATED_ORDER_ERROR",
                                    flagStatus[1]);
                        } else if (flagStatus[0].equals("ADD_CART_FAILURE")) {
                            // 亲，您的购物车商品总数（含超市商品）已满99件，建议您先去结算或清理
                            serviceResponse.update(ServiceCode.ADD_CART_FAILURE.getCode(), "ADD_CART_FAILURE",
                                    flagStatus[1]);
                        } else if (flagStatus[0].equals("BONUS_BALANCES_INSUFFICIENT")) {
                            // 红包余额不够本次支付！海尔VIP购物来源时才会有
                            serviceResponse.update(ServiceCode.BONUS_BALANCES_INSUFFICIENT.getCode(),
                                    "BONUS_BALANCES_INSUFFICIENT", flagStatus[1]);
                        } else {
                            // 否则统一算作API其他错误
                            String msg = (TextUtils.isEmpty(flagStatus[1]) || flagStatus[1].equals("null")) ? ServiceCode.API_ERROR
                                    .getMsg() : flagStatus[1];
                            serviceResponse.update(ServiceCode.API_ERROR.getCode(), flagStatus[0], msg);
                        }
                    }
                }
            } else {
                // 第二种情况，MTOP的接口太傻逼了,搞个JSON对象不就可以了嘛
                // ret":["FAIL::您还没有设置收货地址,请先创建一个收货地址！","ERR_CODE::NO_ADDRESS"]"
                String[] errCodes = retArray.getString(1).split("::");
                String[] errMsgs = retArray.getString(0).split("::");
                if (errCodes.length >= 2 && "ERR_CODE".equals(errCodes[0])) {
                    //匹配已经定义的错误， 如果匹配不到则需要在 ServiceCode 对象中添加，MTop接口居然对这种错误没有一个完整的说明，实在太坑爹了
                    ServiceCode serviceCode = ServiceCode.valueOf(errCodes[1]);
                    int errorCodeValue = ServiceCode.API_ERROR.getCode();
                    String errorMsg = ServiceCode.API_ERROR.getMsg();
                    if (null != serviceCode) {
                        errorCodeValue = serviceCode.getCode();
                        errorMsg = serviceCode.getMsg();
                    } else {
                        if (errMsgs.length >= 2) {
                            errorMsg = errMsgs[1];
                        }
                    }
                    serviceResponse.update(errorCodeValue, errCodes[1], errorMsg);
                } else {
                    // 这是为防止不规范的错误描述
                    serviceResponse
                            .update(ServiceCode.API_ERROR.getCode(), "api_error", ServiceCode.API_ERROR.getMsg());
                }
            }
        }

        JSONObject dataElement = getDataElement(response);

        if (!serviceResponse.isSucess()) {
            // 返回错误码,但是可能仍然有正常数据,MTOP接口设计的真傻逼
            if (dataElement != null && dataElement.length() != 0) {
                serviceResponse.setData((T) resolveResponse(dataElement));
            }
            return serviceResponse;
        }

        // 解析应用数据
        T t = null;
        if (dataElement != null && dataElement.length() != 0) {
            t = (T) resolveResponse(dataElement);
        }
        serviceResponse.setData(t);
        serviceResponse.setStatusCode(ServiceCode.SERVICE_OK.getCode());
        return serviceResponse;
    }

    /**
     * 解析返回结果
     *
     * @param obj
     * @return
     * @throws Exception
     * @date 2012-10-20下午4:08:22
     */
    protected abstract <T> T resolveResponse(JSONObject obj) throws Exception;

    public abstract boolean getNeedEcode();

    public abstract boolean getNeedSession();
}
