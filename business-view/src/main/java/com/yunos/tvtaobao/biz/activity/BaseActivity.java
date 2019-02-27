/**
 *
 */
package com.yunos.tvtaobao.biz.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.tvtaobao.voicesdk.ASRNotify;
import com.tvtaobao.voicesdk.bo.DomainResultVo;
import com.tvtaobao.voicesdk.bo.PageReturn;
import com.tvtaobao.voicesdk.interfaces.ASRHandler;
import com.tvtaobao.voicesdk.register.LPR;
import com.tvtaobao.voicesdk.utils.ActivityUtil;
import com.yunos.RunMode;
import com.yunos.ott.sdk.core.Environment;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.blitz.activity.BzBaseActivity;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.common.DeviceJudge;
import com.yunos.tv.core.common.DeviceJudge.MemoryType;
import com.yunos.tv.core.common.ImageHandleManager;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tv.core.util.SharedPreferencesUtils;
import com.yunos.tv.core.common.ActivityQueueManager;
import com.yunos.tvtaobao.biz.common.NoPutToStack;
import com.yunos.tvtaobao.biz.dialog.util.DialogUtil;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.listener.NetworkOkDoListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.request.core.AsyncDataLoader;
import com.yunos.tvtaobao.biz.request.info.GlobalConfigInfo;
import com.yunos.tvtaobao.blitz.account.AccountActivityHelper;
import com.yunos.tvtaobao.businessview.R;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;


/**
 * @author Administrator
 */
public abstract class BaseActivity extends CoreActivity {

    private final static String BTAG = "BaseActivity";
    private final String BASETAG = "BaseActivity";

    private final String MENU_URI = "tvtaobao://home?module=menu";
    private final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    private SdkBroadcastReceiver mNetworkChangeBroadcastReceiver;
    protected DialogUtil mDialogUtil;
    private NetworkOkDoListener mNetworkOkDoListener;

    private AccountActivityHelper mAccountActivityHelper;
    private AccountActivityHelper.OnAccountStateChangedListener mOnAccountStateChangedListener;
    public static final String LOW_MEM_STACK_TAG = "LowMemoryDevice";
    public static final String TAG_KEEP_STACK = "needKeepPage";
    private final int LOW_MEM_KEEP_NUM = 2;
    private final int MED_MEM_KEEP_NUM = 4;
    private final int HIGH_MEM_KEEP_NUM = 6;//for beta testing
    private BusinessRequest mBusinessRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (RunMode.isYunos() == Environment.getInstance().isYunos()) {
            //在中低内存下控制activity个数
            addActivityStack();

            AppDebug.d(BASETAG, "TAG  ---> " + TAG + ";   ---- >  onCreate;  this =  " + this);

            // 注册网络广播接收器
            if (mNetworkChangeBroadcastReceiver == null) {
                mNetworkChangeBroadcastReceiver = new SdkBroadcastReceiver();
                IntentFilter filter = new IntentFilter();
                filter.addAction(CONNECTIVITY_CHANGE_ACTION);
                filter.setPriority(1000);
                registerReceiver(mNetworkChangeBroadcastReceiver, filter);
            }

            mAccountActivityHelper = new AccountActivityHelper();

            // 创建对话框
            mDialogUtil = new DialogUtil(this);
            GlobalConfigInfo.getInstance().requestGlobalConfigData();
        }
    }

    /**
     * 跳转到需要登录的界面
     *
     * @param intent
     */
    protected void startNeedLoginActivity(Intent intent) {

        AppDebug.d(BASETAG, "TAG  ---> " + TAG + ";   ---- >  startNeedLoginActivity;  this =  " + this);

        // 未登录则弹出提示框并返回
        try {
//            TYIDManager mTYIDManager = TYIDManager.get(getApplicationContext());
//            int loginState = mTYIDManager.yunosGetLoginState();
            if (!CoreApplication.getLoginHelper(CoreApplication.getApplication()).isLogin()) {
                // 跳转到未登录页面
                setLoginActivityStartShowing();
                CoreApplication.getLoginHelper(CoreApplication.getApplication()).startYunosAccountActivity(this, false);
                return;
            }
        } catch (Exception e) {
            AppDebug.e(TAG, "get login state exception:" + e);
            setLoginActivityStartShowing();
            CoreApplication.getLoginHelper(CoreApplication.getApplication()).startYunosAccountActivity(this, false);
        }

        // 跳转到指定界面
        startActivity(intent);
    }


    /**
     * 跳转到需要登录的界面
     *
     * @param intent
     */
    protected void startNeedLoginActivityForResult(Intent intent, int requestCode) {

        AppDebug.d(BASETAG, "TAG  ---> " + TAG + ";   ---- >  startNeedLoginActivityForResult;  this =  " + this);

        // 未登录则弹出提示框并返回
        try {
//            TYIDManager mTYIDManager = TYIDManager.get(getApplicationContext());
//            int loginState = mTYIDManager.yunosGetLoginState();
            if (!CoreApplication.getLoginHelper(CoreApplication.getApplication()).isLogin()) {
                // 跳转到未登录页面
                CoreApplication.getLoginHelper(CoreApplication.getApplication()).startYunosAccountActivity(this, false);
                return;
            }
        } catch (Exception e) {
            AppDebug.e(TAG, "get login state exception:" + e);
            CoreApplication.getLoginHelper(CoreApplication.getApplication()).startYunosAccountActivity(this, false);
        }

        // 跳转到指定界面
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onStartActivityNetWorkError() {
        showNetworkErrorDialog(false);
    }

    /**
     * 显示网络异常对话框
     */
    public void showNetworkErrorDialog(final boolean isfinishActivity) {
        mDialogUtil.showNetworkErrorDialog(isfinishActivity);
    }

    /**
     * 一般的错误提示
     *
     * @param msg
     */
    public void showErrorDialog(String msg, final boolean isFinishActivity) {

        AppDebug.d(BASETAG, "TAG  ---> " + TAG + ";   ---- >   showErrorDialog;  this =  " + this);

        mDialogUtil.showErrorDialog(msg, getString(R.string.ytbv_confirm), new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (isFinishActivity) {
                    finish();
                    return;
                }

            }
        }, new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    if (isFinishActivity) {
                        finish();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public boolean isHasDestroyActivity() {
        return isFinishing();
    }

    /**
     * 一般的错误提示
     *
     * @param msg
     */
    public void showErrorDialog(String msg, OnClickListener listener, final OnKeyListener onKeyListener) {

        mDialogUtil.showErrorDialog(msg, getString(R.string.ytbv_confirm), listener, onKeyListener);
    }

    public void showErrorDialog(String msg, String button, OnClickListener listener,
                                final OnKeyListener onKeyListener) {
        mDialogUtil.showErrorDialog(msg, button, listener, onKeyListener);
    }

    /**
     * 打开yunos4.0界面
     */
    public void showYunosHostPage(Bundle bundle, String pageUrl) throws Exception {
        AppDebug.d(TAG, TAG + ".showYunosHostPage. bundle = " + bundle);
        Method methodMeta = getClass().getMethod("startHostPage", String.class, String.class, Bundle.class,
                boolean.class);
        AppDebug.d(TAG, TAG + ".showYunosHostPage.methodMeta = " + methodMeta);
        if (methodMeta != null) {
            methodMeta.invoke(this, pageUrl, null, bundle, true);
            AppDebug.d(TAG, TAG + ".showYunosHostPage ok");
        }
    }

    /**
     * 控制loading显示隐藏
     *
     * @param show
     */
    public void OnWaitProgressDialog(boolean show) {
        if (mDialogUtil != null) {
            mDialogUtil.OnWaitProgressDialog(show);
        }
    }

    /**
     * 控制text loading显示隐藏
     *
     * @param show
     */
    public void onTextProgressDialog(CharSequence text, boolean show) {
        if (mDialogUtil != null) {
            mDialogUtil.onTextProgressDialog(text, show);
        }
    }

    /**
     * 设置进度条 按返回键 取消是否有效
     *
     * @param flag
     */
    public void setProgressCancelable(boolean flag) {
        if (mDialogUtil != null) {
            mDialogUtil.setProgressCancelable(flag);
        }
    }

    /**
     * 网络状态变化
     *
     * @param available true 连接 or 断开
     */
    protected void changedNetworkStatus(boolean available) {
        AppDebug.i(TAG, "changedNetworkStatus available=" + available);
    }

    // 网络接收器
    public class SdkBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (TextUtils.equals(action, CONNECTIVITY_CHANGE_ACTION)) {
                // 网络可用时关闭提示框
                if (NetWorkUtil.isNetWorkAvailable() && mDialogUtil != null) {
                    mDialogUtil.networkDialogDismiss();
                    if (mNetworkOkDoListener != null) {
                        mNetworkOkDoListener.todo();
                    }
                }
                changedNetworkStatus(NetWorkUtil.isNetWorkAvailable());
            }
        }
    }


    @Override
    protected void onResume() {
        if (RunMode.isYunos() == Environment.getInstance().isYunos()) {
//        if (!getClass().getSimpleName().contains("loading"))
//            ASRUtils.getInstance().setActivity(this);
            //设置账号界面隐藏
            mAccountActivityHelper.setAccountActivityHide();
        }
        super.onResume();
        ActivityUtil.addTopActivity(this);
        if (!NoPutToStack.getVoiceMap().containsKey(this.getClass().getName())) {
            LPR.getInstance().registed(this);
            ASRNotify.getInstance().setHandler(new VoiceAction());
        }
    }

    @Override
    protected void onPause() {

        if (RunMode.isYunos() == Environment.getInstance().isYunos()) {

            mAccountActivityHelper.setAccountActivityShowed();
            super.onPause();

            // 清理网络下载的线程池
            AsyncDataLoader.purge();

            // 清理图片处理的线程池
            ImageHandleManager.getImageHandleManager(getApplicationContext()).purge();

            if (!NoPutToStack.getVoiceMap().containsKey(this.getClass().getName())) {
                ASRNotify.getInstance().setHandler(null);
                LPR.getInstance().unregistered();
            }
        } else {
            super.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (RunMode.isYunos() == Environment.getInstance().isYunos()) {
            AppDebug.d(BASETAG, "TAG   ---> " + TAG + ";   ---- >   onDestroy;  this =  " + this);

            unRegisterLoginListener();

            // 将对话框消失并释放引用
            if (mDialogUtil != null) {
                mDialogUtil.onDestroy();
            }

            // 取消注册的广播接收器，并释放引用
            if (mNetworkChangeBroadcastReceiver != null) {
                unregisterReceiver(mNetworkChangeBroadcastReceiver);
                mNetworkChangeBroadcastReceiver = null;
            }

            //从堆栈中移除当前activity
            removeActivityFromStack();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (event.getKeyCode() == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_DOWN) {
//            if (GlobalConfig.instance != null && GlobalConfig.instance.isBeta())
//                return true;
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setData(Uri.parse(MENU_URI));
//            try {
//                startActivity(intent);
//            } catch (ActivityNotFoundException e) {
//                e.printStackTrace();
//            }
//            //只有在中低内存下才清理堆栈,只保留前面activity
//            if (!MemoryType.HighMemoryDevice.equals(DeviceJudge.getMemoryType())) {
//                ActivityQueueManager.getInstance().clearhActivity(LOW_MEM_STACK_TAG, 1);
//            }
//        }

        if (getFocusManager()!=null && event.getAction()==KeyEvent.ACTION_DOWN){
            getFocusManager().onKeyEvent(event.getKeyCode(),event);
        }
        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT
                || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
            FocusPositionManager focusPositonManager = getFocusPositionManager();
            //AppDebug.v(TAG, TAG + ".dispatchKeyEvent.focusPositonManager = " + focusPositonManager);
            if (focusPositonManager != null && !focusPositonManager.IsFocusStarted()) {// 开启focus模式
                focusPositonManager.focusStart();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        AppDebug.i(TAG, TAG + ".dispatchTouchEvent.ev = " + ev);
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            FocusPositionManager focusPositonManager = getFocusPositionManager();
            AppDebug.v(TAG, TAG + ".dispatchTouchEvent.focusPositonManager = " + focusPositonManager);
            if (focusPositonManager != null && focusPositonManager.IsFocusStarted()) {// 关闭focus模式
                focusPositonManager.focusStop();
                focusPositonManager.invalidate();
                View focused = focusPositonManager.findFocus();
                AppDebug.v(TAG, TAG + ".dispatchTouchEvent.focused = " + focused);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    protected PageReturn onVoiceAction(DomainResultVo action) {
        return null;
    }

    private class VoiceAction implements ASRHandler {
        @Override
        public PageReturn onASRNotify(DomainResultVo object) {
            return onVoiceAction(object);
        }
    }

    /**
     * 登录界面是否是被取消
     *
     * @return
     */
    protected boolean loginIsCanceled() {
        return mAccountActivityHelper.loginIsCanceled();
    }

    /**
     * 设置登录界面正在显示
     */
    public void setLoginActivityStartShowing() {
        mAccountActivityHelper.setAccountActivityStartShowing();
    }

    /**
     * 设置当前的登录状态是否为非法
     */
    public void setCurrLoginInvalid() {
        mAccountActivityHelper.setAccountInvalid();
    }

    /**
     * 启动登录页面
     *
     * @param from
     * @param forceLogin
     */
    public void startLoginActivity(String from, boolean forceLogin) {
        mAccountActivityHelper.startAccountActivity(this, from, forceLogin);
    }

    /**
     * 注册登录的状态监听
     */
    protected void registerLoginListener() {
        if (mOnAccountStateChangedListener == null) {
            mOnAccountStateChangedListener = new AccountActivityHelper.OnAccountStateChangedListener() {
                @Override
                public void onAccountStateChanged(AccountActivityHelper.AccountLoginState state) {
                    switch (state) {
                        case LOGIN:
                            onLogin();
                            break;
                        case LOGOUT:
                            onLogout();
                            break;
                        case CANCEL:
                            onLoginCancel();
                            break;
                        default:
                            break;
                    }
                }
            };
            mAccountActivityHelper.registerAccountActivity(mOnAccountStateChangedListener);
        }
    }

    /**
     * 删除注册登录的状态监听
     */
    protected void unRegisterLoginListener() {
        if (mOnAccountStateChangedListener != null) {
            mAccountActivityHelper.unRegisterAccountActivity(mOnAccountStateChangedListener);
        }
    }

    /**
     * 登录
     */
    protected void onLogin() {
        refreshData();
        //TODO 双11之后修改淘客登录打点
        if (CoreApplication.getLoginHelper(CoreApplication.getApplication()).isLogin()) {
            mBusinessRequest = BusinessRequest.getBusinessRequest();
            mBusinessRequest.requestTaokeLoginAnalysis(User.getNick(), new TaokeBussinessRequestListener(new WeakReference<BaseActivity>(this)));
        }
    }

    /**
     * 登出
     */
    protected void onLogout() {
        finish();
    }

    /**
     * 登录取消
     */
    protected void onLoginCancel() {
        AppDebug.i(TAG, "onLoginCancel");
    }

    /**
     * 刷新界面,用于子页面重写,登录成功时可能会调用
     */
    protected void refreshData() {
    }

    /**
     * 删除网络重连监听
     */
    public void removeNetworkOkDoListener() {
        mNetworkOkDoListener = null;
    }

    /**
     * 设置网络重连监听
     *
     * @param mNetworkOkDoListener
     */
    public void setNetworkOkDoListener(NetworkOkDoListener mNetworkOkDoListener) {
        this.mNetworkOkDoListener = mNetworkOkDoListener;
    }

    /**
     * Webview加载完成的回调方法，供h5页面重写
     *
     * @param url
     */
    public void onWebviewPageDone(String url) {

    }

    @Override
    protected String getAppTag() {
        return "Tt";
    }

    @Override
    protected String getAppName() {
        return "tvtaobao";
    }

    /**
     * 保持activity只有一个
     */
    protected void onKeepActivityOnlyOne(String tag) {
        ActivityQueueManager manager = ActivityQueueManager.getInstance();
        manager.onDestroyActivityOfList(tag);
        manager.onAddDestroyActivityToList(tag, this);
    }

    /**
     * 移除保持的activity
     */
    protected void onRemoveKeepedActivity(String tag) {
        ActivityQueueManager manager = ActivityQueueManager.getInstance();
        manager.onRemoveDestroyActivityFromList(tag, this);
    }

    /**
     * 增加系统状态栏
     */
    protected void addSystemBar() {
        getWindow().addFlags(0x80000000);
    }

    /**
     * H5页面的背景颜色
     */
    protected void setH5BackGroud() {
        setWebViewBackgroundColor(44, 47, 53, 255);
    }

    /**
     * 添加activity到堆栈中
     */
    private void addActivityStack() {
        AppDebug.i(LOW_MEM_STACK_TAG, DeviceJudge.getDevicePerformanceString());
        //首页不加入堆栈,并且请空堆栈
        if ("com.yunos.tvtaobao.homebundle.activity.HomeActivity".equals(this.getClass().getName())) {
//            if (!MemoryType.HighMemoryDevice.equals(DeviceJudge.getMemoryType())) {
//                ActivityQueueManager.getInstance().onDestroyActivityOfList(LOW_MEM_STACK_TAG);
//            }
            //TODO why
            ActivityQueueManager.getInstance().pushActivity(TAG_KEEP_STACK, this, HIGH_MEM_KEEP_NUM);
            return;
        }

        //首页,过渡页不加入堆栈
        if (NoPutToStack.getMap().containsKey(this.getClass().getName())) {
            return;
        }
        //外部调用的第一个页面也不加入堆栈
        boolean isFirst = getIntent().getBooleanExtra(CoreIntentKey.URI_IS_FIRST_ACTIVITY, false);
        if (isFirst) {
            AppDebug.v(TAG, TAG + ".addActivityStack.isFirstActivity = " + isFirst);
            //TODO why
            ActivityQueueManager.getInstance().pushActivity(TAG_KEEP_STACK, this, HIGH_MEM_KEEP_NUM);
            return;
        }

        boolean beta = GlobalConfig.instance != null && GlobalConfig.instance.isBeta();
        if (MemoryType.HighMemoryDevice.equals(DeviceJudge.getMemoryType())) {
            ActivityQueueManager.getInstance().pushActivity(LOW_MEM_STACK_TAG, this, HIGH_MEM_KEEP_NUM);
        } if (MemoryType.MediumMemoryDevice.equals(DeviceJudge.getMemoryType())) {
            ActivityQueueManager.getInstance().pushActivity(LOW_MEM_STACK_TAG, this, beta ? MED_MEM_KEEP_NUM - 1 : MED_MEM_KEEP_NUM);
        } else if (MemoryType.LowMemoryDevice.equals(DeviceJudge.getMemoryType())) {
            ActivityQueueManager.getInstance().pushActivity(LOW_MEM_STACK_TAG, this, beta ? LOW_MEM_KEEP_NUM - 1 : LOW_MEM_KEEP_NUM);
        }
    }

    @Override
    protected void initBlitzContext(String initStr, int type) {
        super.initBlitzContext(initStr, type);
        ArrayList<BzBaseActivity> activityArrayList = null;
        Class clz = BzBaseActivity.class;
        try {
            Field field = clz.getDeclaredField("ActivityList");
            field.setAccessible(true);
            activityArrayList = (ArrayList<BzBaseActivity>) field.get(null);
            if (GlobalConfig.instance != null && GlobalConfig.instance.isBeta()) {
                if (activityArrayList.size() > 2) {
                    BzBaseActivity bottom1 = activityArrayList.get(0);
                    activityArrayList.remove(0);
                    bottom1.finish();
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从堆栈中移除,在finish中调用
     */
    private void removeActivityFromStack() {
        if (!NoPutToStack.getMap().containsKey(this.getClass().getName())) {
            ActivityQueueManager.getInstance().onRemoveDestroyActivityFromList(LOW_MEM_STACK_TAG, this);
        }
    }

    protected FocusPositionManager getFocusPositionManager() {
        return null;
    }


    /**
     * 淘客打点监听
     */
    private class TaokeBussinessRequestListener extends BizRequestListener<JSONObject> {

        public TaokeBussinessRequestListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return true;
        }

        @Override
        public void onSuccess(JSONObject data) {
            AppDebug.d(BTAG, data.toString());
            long historyTime = System.currentTimeMillis() + 604800000;//7天
            SharedPreferencesUtils.saveTvBuyTaoKe(BaseActivity.this, historyTime);
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }


}
