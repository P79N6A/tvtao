/**
 * $
 * PROJECT NAME: TvTaoBaoBase
 * PACKAGE NAME: com.yunos.tv.tvtaobaobase.common
 * FILE NAME: User.java
 * CREATED TIME: 2014-10-24
 * COPYRIGHT: Copyright(c) 2013 ~ 2014 All Rights Reserved.
 */
/**
 *
 */
package com.yunos.tv.core.common;


import android.text.TextUtils;

import com.ali.auth.third.core.model.Session;
import com.ali.auth.third.core.service.impl.CredentialManager;
import com.yunos.tv.blitz.global.BzApplication;
import com.yunos.tv.core.config.Config;

import org.json.JSONObject;

import static com.ali.auth.third.core.context.KernelContext.getApplicationContext;


/**
 * 登陆用户辅助函数
 *
 * @author hanqi
 * @data 2014-10-24 上午10:58:18
 */
public class User {

    private static TvUser user;

    public static TvUser getUser() {
        return user;
    }

    public static void setUser(TvUser user) {
        User.user = user;
    }

    /**
     * 用户信息填充
     *
     * @param json
     * @return
     */
    public static boolean updateUser(JSONObject json) {
        if (null == user) {
            user = new TvUser();
        }
        return user.updateUserFromJson(json);
    }

    public static boolean updateUser(Session session) {
        if (null == user) {
            user = new TvUser();
        }
        return user.updateFromSession(session);
    }

    /**
     * 退出时把清除用户信息
     */
    public static void clearUser() {
        user = null;
    }

    /**
     * 是否登陆
     *
     * @return
     * @author hanqi
     * @date 2014-5-19
     */
    public static boolean isLogined() {
//        return user != null && !TextUtils.isEmpty(user.getSessionId()) && !TextUtils.isEmpty(user.getEcode())
//                && !TextUtils.isEmpty(user.getNick());

        return BzApplication.getLoginHelper(getApplicationContext()).isLogin();


    }

    /**
     * 用户ID
     *
     * @return
     */
    public static String getUserId() {
        String uid = null == user ? null : user.getUserId();
        try{
            if (TextUtils.isEmpty(uid) && Config.isAgreementPay()) {
                if (CredentialManager.INSTANCE.getSession() != null)
                    uid = CredentialManager.INSTANCE.getSession().userid;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }catch (NoClassDefFoundError e){
                e.printStackTrace();
        }


        return uid;
    }

    /**
     * 获取用户名称
     *
     * @return
     */
    public static String getNick() {
        String nick = user == null ? null : user.getNick();
        if (TextUtils.isEmpty(nick) && Config.isAgreementPay()) {
            if (CredentialManager.INSTANCE.getSession() != null)
                nick = CredentialManager.INSTANCE.getSession().nick;
        }
        return nick;
    }

    public static String getPassword() {
        return user == null ? null : user.getPassword();
    }

    /**
     * 获取sid
     *
     * @return
     */
    public static String getSessionId() {
        return user == null ? null : user.getSessionId();
    }

    public static String getTopSessionId() {
        return user == null ? null : user.getTopSession();
    }

    /**
     * 加签参数
     *
     * @return
     */
    public static String getEcode() {
        return null == user ? null : user.getEcode();
    }

    public static boolean getIsNickSecurePayDone() {
        return (null == user) ? null : user.getisNickSecurePayDone();
    }
}
