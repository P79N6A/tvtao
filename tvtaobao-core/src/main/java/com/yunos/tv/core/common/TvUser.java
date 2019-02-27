package com.yunos.tv.core.common;


import android.content.SharedPreferences;
import android.text.TextUtils;

import com.ali.auth.third.core.model.InternalSession;
import com.ali.auth.third.core.model.Session;
import com.ali.auth.third.core.service.impl.CredentialManager;
import com.ali.auth.third.offline.login.context.LoginContext;
import com.ut.mini.UTAnalytics;
import com.yunos.tv.core.CoreApplication;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 电视用户的相关信息
 */
public class TvUser implements Serializable {

    private static final long serialVersionUID = 3870687249006922603L;
    private static final String TAG = "JuUser";
    //是否提交过设备
    private boolean isRecorded = false;
    //是否注册过DeviceId
    private boolean isRegDeviceId = false;
    private boolean isUpdateLogo = true;
    private String userId;

    private String nick;
    private String password;

    //是否成功完成支付插件支付
    private boolean mIsNickSecurePayDone = false;
    // mtop用户的会话id唯一标识用户身份的标评
    private String sessionId;
    // top用户的会话id
    private String topSession;

    // 登陆时的日期
    private String loginDate;

    private String loginTime;

    // 加签参数
    private String ecode;

    // 自动登录加密丄1�7
    private String mtopToken;

    // 用户头像
    private String userLogo;

    // 支付宝
    private String alipay;

    // 是否绑定支付宝
    private String alipayEnable;

    // 淘宝交易信息
    // 已经支付的交易数
    private String hasPaid;

    // 付款中的交易数
    private String refuandBiz;

    // 成功交易数
    private String successBiz;

    // 等待确认交易数
    private String toConfirmBiz;

    // 待支付的交易数
    private String toPayBiz;

    // 订单数
    private String orderSum;

    // 共消费
    private String moneySum;

    // 共节省
    private String saveSum;

    private Object mLock = new Object();

    private SharedPreferences mSecurePayDonePre;

    // 登录成功的时候更新会话信息
    public boolean updateUserFromJson(JSONObject json) {
        if (null != json) {
            String sid = json.optString("sid", "");
            String nick = json.optString("nick", "");
            String userId = json.optString("userId", "");
            String mtopToken = json.optString("token", "");
            String topSession = json.optString("topSession", "");
            String ecode = json.optString("ecode", "");
            if (!TextUtils.isEmpty(sid) && !TextUtils.isEmpty(userId) && !TextUtils.isEmpty(ecode)) {
                // 有可能用手机号登陆的
                setNick(nick);
                setSessionId(sid);
                setUserId(userId);
                setMtopToken(mtopToken);
                setTopSession(topSession);
                setEcode(ecode);
                readSecurePayDone(nick);
                AppDebug.d(TAG, "更新会话信息成功");

                // 成功后初始化用户信息
                //AppHolder.initUserManager();

                // 统计账号登录埋点
                if (!TextUtils.isEmpty(nick)) {
                    UTAnalytics.getInstance().updateUserAccount(nick, userId);
                }

                return true;
            }
        }
        AppDebug.d(TAG, "更新会话信息失败");
        return false;
    }

    public boolean updateFromSession(Session session) {
        if (null != session) {
            String sid = session.openSid;
            if (TextUtils.isEmpty(sid)) {
                InternalSession internalSession = CredentialManager.INSTANCE.getInternalSession();
                if (internalSession != null)
                    sid = internalSession.sid;
            }
            String nick = session.nick;
            String userId = session.userid;
            String mtopToken = session.topAccessToken;
//            String topSession = json.optString("topSession", "");
//            String ecode = json.optString("ecode", "");
            if (!TextUtils.isEmpty(userId)) {
                // 有可能用手机号登陆的
                setNick(nick);
                setSessionId(sid);
                setUserId(userId);
                setMtopToken(mtopToken);
//                setTopSession(topSession);
//                setEcode(ecode);
                readSecurePayDone(nick);
                AppDebug.d(TAG, "更新会话信息成功");

                // 成功后初始化用户信息
                //AppHolder.initUserManager();

                // 统计账号登录埋点
                if (!TextUtils.isEmpty(nick)) {
                    UTAnalytics.getInstance().updateUserAccount(nick, userId);
                }

                return true;
            }
        }
        AppDebug.d(TAG, "更新会话信息失败");
        return false;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isRecorded() {
        return isRecorded;
    }

    public void setRecorded(boolean recorded) {
        isRecorded = recorded;
    }

    public boolean isRegDeviceId() {
        return isRegDeviceId;
    }

    public void setRegDeviceId(boolean regDeviceId) {
        isRegDeviceId = regDeviceId;
    }

    public boolean isUpdateLogo() {
        return isUpdateLogo;
    }

    public void setUpdateLogo(boolean updateLogo) {
        isUpdateLogo = updateLogo;
    }

    public boolean getisNickSecurePayDone() {
        return mIsNickSecurePayDone;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSessionId() {
        synchronized (mLock) {
            return sessionId;
        }
    }

    public void setSessionId(String sessionId) {
        synchronized (mLock) {
            this.sessionId = sessionId;
        }
    }

    public String getTopSession() {
        return topSession;
    }

    public void setTopSession(String topSession) {
        this.topSession = topSession;
    }

    public String getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(String loginDate) {
        this.loginDate = loginDate;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String loginTime) {
        this.loginTime = loginTime;
    }

    public String getEcode() {
        synchronized (mLock) {
            return ecode;
        }
    }

    public void setEcode(String ecode) {
        synchronized (mLock) {
            this.ecode = ecode;
        }
    }

    public String getMtopToken() {
        return mtopToken;
    }

    public void setMtopToken(String token) {
        this.mtopToken = token;
    }

    public String getUserLogo() {
        return userLogo;
    }

    public void setUserLogo(String userLogo) {
        this.userLogo = userLogo;
    }

    public String getAlipay() {
        return alipay;
    }

    public void setAlipay(String alipay) {
        this.alipay = alipay;
    }

    public String getAlipayEnable() {
        return alipayEnable;
    }

    public void setAlipayEnable(String alipayEnable) {
        this.alipayEnable = alipayEnable;
    }

    public String getHasPaid() {
        return hasPaid;
    }

    public void setHasPaid(String hasPaid) {
        this.hasPaid = hasPaid;
    }

    public String getRefuandBiz() {
        return refuandBiz;
    }

    public void setRefuandBiz(String refuandBiz) {
        this.refuandBiz = refuandBiz;
    }

    public String getSuccessBiz() {
        return successBiz;
    }

    public void setSuccessBiz(String successBiz) {
        this.successBiz = successBiz;
    }

    public String getToConfirmBiz() {
        return toConfirmBiz;
    }

    public void setToConfirmBiz(String toConfirmBiz) {
        this.toConfirmBiz = toConfirmBiz;
    }

    public String getToPayBiz() {
        return toPayBiz;
    }

    public void setToPayBiz(String toPayBiz) {
        this.toPayBiz = toPayBiz;
    }

    public String getOrderSum() {
        return orderSum;
    }

    public void setOrderSum(String orderSum) {
        this.orderSum = orderSum;
    }

    public String getMoneySum() {
        return moneySum;
    }

    public void setMoneySum(String moneySum) {
        this.moneySum = moneySum;
    }

    public String getSaveSum() {
        return saveSum;
    }

    public void setSaveSum(String saveSum) {
        this.saveSum = saveSum;
    }

    public void saveSecurePayDone(String userName) {
        if (userName == null) {
            mIsNickSecurePayDone = false;
            return;
        }
        if (mSecurePayDonePre == null) {
            mSecurePayDonePre = CoreApplication.getApplication().getSharedPreferences("securePayDone", 0);
        }
        SharedPreferences.Editor editor = mSecurePayDonePre.edit();
        editor.putBoolean(userName, true);
        mIsNickSecurePayDone = true;
        editor.commit();
    }

    public void readSecurePayDone(String userName) {
        if (userName == null) {
            mIsNickSecurePayDone = false;
            return;
        }
        if (mSecurePayDonePre == null) {
            mSecurePayDonePre = CoreApplication.getApplication().getSharedPreferences("securePayDone", 0);
        }
        mIsNickSecurePayDone = mSecurePayDonePre.getBoolean(userName, false);

    }

    @Override
    public String toString() {
        return "JuUser [isRecorded=" + isRecorded + ", isRegDeviceId=" + isRegDeviceId + ", isUpdateLogo="
                + isUpdateLogo + ", userId=" + userId + ", nick=" + nick + ", password=" + password
                + ", mIsNickSecurePayDone=" + mIsNickSecurePayDone + ", sessionId=" + sessionId + ", topSession="
                + topSession + ", loginDate=" + loginDate + ", loginTime=" + loginTime + ", ecode=" + ecode
                + ", mtopToken=" + mtopToken + ", userLogo=" + userLogo + ", alipay=" + alipay + ", alipayEnable="
                + alipayEnable + ", hasPaid=" + hasPaid + ", refuandBiz=" + refuandBiz + ", successBiz=" + successBiz
                + ", toConfirmBiz=" + toConfirmBiz + ", toPayBiz=" + toPayBiz + ", orderSum=" + orderSum
                + ", moneySum=" + moneySum + ", saveSum=" + saveSum + ", mLock=" + mLock + ", mSecurePayDonePre="
                + mSecurePayDonePre + "]";
    }

}
