package com.yunos.tvtaobao.blitz.account;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.yunos.tv.blitz.account.LoginHelper;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;

/**
 * 针对账号界面操作的类
 * LOGIN:登入
 * LOGOUT：登出
 * CANCEL：取消登录
 * 上面两个消息是广播过来的，CANCEL因为没有广播
 * 所以只有通过界面的显示跟隐藏来再加入登录状态的判断来模拟（不一样准确）
 *
 * @author tingmeng.ytm
 */
public class AccountActivityHelper {
    private final String TAG = "AccountActivityHelper";

    /* 账号界面的显示状态 */
    private enum AccountActivityState {
        START_SHOWING, SHOWED, HIDED;
    }

    ;

    /* 账号界面的登录状态 */
    public enum AccountLoginState {
        UNKNOWN,
        LOGIN,
        LOGOUT,
        CANCEL
    }

    ;
    /* 账号的登录状态 */
    private AccountLoginState mAccountLoginState;
    /* 账号界面的显示状态 */
    private AccountActivityState mAccountActivityState;
    /* 当前登录的状态为无效的状态，主要是解决sessionId过期后TYID返回的结果还是登录状态 */
    private boolean mCurrLoginInvalid;
    /* 登录接口类 */
    private LoginHelper mLoginHelper;
    /* 监听登录广播的类 */
    private LoginHelper.SyncLoginListener mLoginLister;
    private OnAccountStateChangedListener mOnAccountStateChangedListener;

    public AccountActivityHelper() {
        mAccountLoginState = AccountLoginState.UNKNOWN;
        mAccountActivityState = AccountActivityState.HIDED;
    }

    /**
     * 设置界面的状态为隐藏(建议放到页面的onResume里面)
     */
    public void setAccountActivityHide() {
        AppDebug.i(TAG, "setAccountActivityHide mAccountActivityState=" + mAccountActivityState);
        // 只有显示完成后才能再判断是否要隐藏，有的页面START_SHOWING时还没有启动好
        if (mAccountActivityState.compareTo(AccountActivityState.SHOWED) == 0) {
            mAccountActivityState = AccountActivityState.HIDED;
            // 如果在隐藏的时候还不是登录状态那就说明是登录取消了
            boolean login = checkLoginState();
            if (!login) {
                onStateChanged(AccountLoginState.CANCEL);
            }
        }
    }

    /**
     * 设置界面的状态为已显示(建议放到页面的onPause里面)
     */
    public void setAccountActivityShowed() {
        AppDebug.i(TAG, "setAccountActivityShowed mAccountActivityState=" + mAccountActivityState);
        if (mAccountActivityState.compareTo(AccountActivityState.START_SHOWING) == 0) {
            mAccountActivityState = AccountActivityState.SHOWED;
        }
    }

    /**
     * 设置界面的状态为正在显示(建议放到登录页面启动之前)
     */
    public void setAccountActivityStartShowing() {
        AppDebug.i(TAG, "setAccountActivityStartShowing mAccountActivityState" + mAccountActivityState);
        // 只要是显示的登录界面就直接设置成登录状态
        mAccountLoginState = AccountLoginState.LOGOUT;
        mAccountActivityState = AccountActivityState.START_SHOWING;
        boolean digital = (mAccountLoginState == AccountLoginState.LOGOUT);
        boolean equal = mAccountLoginState.equals(AccountLoginState.LOGOUT);
        boolean compareTo = mAccountLoginState.compareTo(AccountLoginState.LOGOUT) == 0;
        AppDebug.i(TAG, "test mAccountLoginState=" + mAccountLoginState + " digital=" + digital + " equal=" + equal + " compareTo=" + compareTo);
    }

    /**
     * 设置当前的登录状态是否为非法
     */
    public void setAccountInvalid() {
        AppDebug.i(TAG, "setAccountInvalid");
        Log.i("lihaile------------", "设置合法");
        // 只要是非法的账号就直接设置成登录状态
        mAccountLoginState = AccountLoginState.LOGOUT;
        mCurrLoginInvalid = true;
    }

    /**
     * 启动账户登录页面
     *
     * @param activity
     * @param from
     * @param forceLogin
     */
    public void startAccountActivity(Activity activity, String from, boolean forceLogin) {
        AppDebug.i(TAG, "startLoginActivity");
        setAccountActivityStartShowing();
        boolean ifChangeAccount = true;
        if (!TextUtils.isEmpty(from) || !forceLogin) {
            ifChangeAccount = false;
        }
        CoreApplication.getLoginHelper(CoreApplication.getApplication()).startYunosAccountActivity(activity, ifChangeAccount);
    }

    /**
     * 启动账户登录页面
     *
     * @param from
     * @param forceLogin
     */
    public void startAccountActivity(String from, boolean forceLogin) {
        AppDebug.i(TAG, "startLoginActivity");
        setAccountActivityStartShowing();
        boolean ifChangeAccount = true;
        if (!TextUtils.isEmpty(from) || !forceLogin) {
            ifChangeAccount = false;
        }
        CoreApplication.getLoginHelper(CoreApplication.getApplication()).startYunosAccountActivity(CoreApplication.getApplication(), ifChangeAccount);
    }

    /**
     * 登录界面是否是被取消
     *
     * @return
     */
    public boolean loginIsCanceled() {
        AppDebug.i(TAG, "loginIsCanceled mAccountLoginState=" + mAccountLoginState);
        if (mAccountLoginState.compareTo(AccountLoginState.CANCEL) == 0) {
            return true;
        }
        return false;
    }

    /**
     * 注册登录监听接口（不要忘记在退出时反注册）
     *
     * @param listener
     */
    public void registerAccountActivity(OnAccountStateChangedListener listener) {
        mOnAccountStateChangedListener = listener;
        registerLoginListener();
    }

    /**
     * 反注册登录监听接口
     *
     * @param listener
     */
    public void unRegisterAccountActivity(OnAccountStateChangedListener listener) {
        mOnAccountStateChangedListener = null;
        unRegisterLoginListener();
    }

    /**
     * 向LoginHelper注册登录的状态监听
     */
    private void registerLoginListener() {
        mLoginHelper = CoreApplication.getLoginHelper(CoreApplication.getApplication());
        if (mLoginLister == null) {
            mLoginLister = new LoginHelper.SyncLoginListener() {
                @Override
                public void onLogin(boolean isSuccess) {
                    AppDebug.i(TAG, "onLogin isSuccess=" + isSuccess);
                    // 不管是登录还是登出，只要是操作了就可以通过TYID的接口来判断是否登录
                    mCurrLoginInvalid = false;
                    if (isSuccess) {
                        onStateChanged(AccountLoginState.LOGIN);
                    } else {
                        onStateChanged(AccountLoginState.LOGOUT);
                    }
                }
            };
            if(mLoginHelper!=null){
                mLoginHelper.addReceiveLoginListener(mLoginLister);
            }
        }
    }

    /**
     * 向LoginHelper删除注册登录的状态监听
     */
    private void unRegisterLoginListener() {
        if (mLoginLister != null && mLoginHelper != null) {
            mLoginHelper.removeReceiveLoginListener(mLoginLister);
            mLoginLister = null;
            mLoginHelper = null;
        }
    }

    /**
     * 检查登录状态
     *
     * @return
     */
    private boolean checkLoginState() {
        AppDebug.i(TAG, "checkLoginState mCurrLoginInvalid=" + mCurrLoginInvalid);
        if (mCurrLoginInvalid) {
            return false;
        }

//        LoginService service = MemberSDK.getService(LoginService.class);
//        return service == null ? false : service.checkSessionValid();

        return CoreApplication.getLoginHelper(CoreApplication.getApplication()).isLogin();

//        try {
//            int loginState = -1;
//            TYIDManager mTYIDManager = TYIDManager.get(CoreApplication.getApplication());
//            loginState = mTYIDManager.yunosGetLoginState();
//            AppDebug.i(TAG, "checkLoginState loginState=" + loginState);
//            if (loginState == 200) {
//                return true;
//            } else {
//                return false;
//            }
//        } catch (Exception e) {
//            AppDebug.e(TAG, "asyncDataLoader get login state exception:" + e);
//        }
//        return false;
    }

    /**
     * 登录发生状态变化
     *
     * @param state
     */
    private void onStateChanged(AccountLoginState state) {
        AppDebug.i(TAG, "onStateChanged state=" + state);
        mAccountLoginState = state;
        if (mOnAccountStateChangedListener != null) {
            mOnAccountStateChangedListener.onAccountStateChanged(state);
        }
    }


    /**
     * @author tingmeng.ytm
     */
    public interface OnAccountStateChangedListener {
        public void onAccountStateChanged(AccountLoginState state);
    }
}
