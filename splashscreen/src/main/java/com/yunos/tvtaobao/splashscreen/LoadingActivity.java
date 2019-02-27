package com.yunos.tvtaobao.splashscreen;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yunos.CloudUUIDWrapper;
import com.yunos.RunMode;
import com.yunos.ott.sdk.core.Environment;
import com.ut.mini.UTAnalytics;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.AppHandler;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.config.SystemConfig;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tv.net.network.NetworkManager;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.request.bo.LoadingBo;
import com.yunos.tvtaobao.biz.request.info.GlobalConfigInfo;
import com.yunos.tvtaobao.biz.updatesdk.UpdateClient;
import com.yunos.tvtaobao.biz.util.BitMapUtil;
import com.yunos.tvtaobao.biz.util.FileUtil;
import com.yunos.tvtaobao.biz.util.MD5Util;
import com.yunos.tvtaobao.biz.util.TimeUtil;
import com.yunos.tvtaobao.splashscreen.config.BaseConfig;
import com.yunos.tvtaobao.splashscreen.service.LoadingService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadingActivity extends BaseActivity {

    private View loadingLayout;
    private static final String SWITCH_TO_HOME_ACTIVITY = "com.yunos.tvtaobao.homebundle.activity.HomeActivity";

    //loading停留时间,默认为1S,再从缓存里读取
    private int duration = 0;
    private boolean mIsShowLoading = true; // 是否显示欢迎页面

    private ProgressBar mProgressBar;
    private TextView loadingWaitText;
    private TextView loadingFailText;
    private Button loadingFailButton;
    private TextView mAppVersion;

    private Thread newThread; //声明一个子线程

    private ProcessActivity mProcessActivity;

    private Handler mHandler;

    private BitmapDrawable mBitmapDrawable;
    private Bitmap mBitmap;

    private String spm_url = null;
    Intent intentValue;

    private NetworkManager.INetworkListener lnetWork = new NetworkManager.INetworkListener() {
        @Override
        public void onNetworkChanged(boolean isConnected, boolean lastIsConnected) {
            if (isConnected && NetWorkUtil.isNetWorkAvailable()) {
                //网络连接后重新进入
                if (mIsShowLoading) {
                    gotoActivity();
                } else {
                    // 直接进入下个页面
                    gotoActivityExt();
                }
            }
        }

    };

    private boolean mFirstStartReceiver = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (RunMode.isYunos() == Environment.getInstance().isYunos()) {

            intentValue = getIntent();
            mProcessActivity = ProcessActivity.getInstance();
            mProcessActivity.init(this);

            AppDebug.d(TAG, "init activity startLoading " + getIntent() + ",data:" + intentValue.getData() + " , hasExtra:" + intentValue.hasExtra("data"));
            if (intentValue.getData() != null) {
                boolean result = mProcessActivity.processActivity();
                AppDebug.i("StartActivity", "open result=" + result);

                Utils.utControlSkip(this);

            } else {
                //NOTE: 开屏广告页面可以使用下面的值，重新覆盖设定是否显示加载页！
                mIsShowLoading = showLoading();
                AppDebug.i(TAG, "LoadingActivity onCreate isShowLoading=" + mIsShowLoading);
                if (mIsShowLoading) {
                    setContentView(R.layout.ytm_activity_loading);
                    //初始化view
                    loadingLayout = findViewById(R.id.loading_layout);
                    mProgressBar = (ProgressBar) findViewById(R.id.loading_progressbar);
                    loadingWaitText = (TextView) findViewById(R.id.loading_wait_text);
                    loadingFailText = (TextView) findViewById(R.id.loading_fail_text);
                    loadingFailButton = (Button) findViewById(R.id.loading_fail_button);
                    mAppVersion = (TextView) findViewById(R.id.loading_app_version);
                    mHandler = new LoadingHandler(this);
                    newThread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            displayImage();
                        }
                    });
                    //启动线程
                    newThread.start();
                    startLoadingService();
                } else {
                    // 不显示欢迎页直接进入下个页面
                    gotoActivityExt();
                }
            }
//        startUpdateApp();
        } else {
            Intent intent = new Intent();
            intent.setClassName(LoadingActivity.this, com.yunos.tvtaobao.biz.common.BaseConfig.SWITCH_TO_YUNOSORDMODE_ACTIVITY);
            startActivity(intent);
            finish();

        }
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> p = super.getPageProperties();
        p.put(SPMConfig.SPM_CNT, "a2o0j.11297213.0.0");

        return p;
    }

    @Override
    protected boolean isTbs() {
        return false;
    }

    public void nextPageSpm() {
        if (intentValue.getData() != null) {
            Uri uri = intentValue.getData();
            Bundle bundle = ProcessActivity.decodeUri(uri);
            if (bundle != null) {
                spm_url = bundle.getString(ProcessActivity.INTENT_KEY_SPM);
                AppDebug.i(TAG, TAG + ".StartActivity.spm_url=" + spm_url);
                Utils.updateNextPageProperties(spm_url);
                AppDebug.d(TAG, "init activity startLoading " + getIntent() + ",data:" + intentValue.getData() + " , hasExtra:" + intentValue.hasExtra("data"));
            }

        } else if (intentValue.getExtras() != null) {
            Bundle bundle = intentValue.getExtras();
            if (bundle != null) {
                String prInputUri = bundle.getString("pr_input_uri");
                if (prInputUri != null) {
                    Uri uri = Uri.parse(prInputUri);
                    AppDebug.i(TAG, TAG + ".pr_input_uri =" + prInputUri);

                    Bundle bundleSpm = ProcessActivity.decodeUri(uri);
                    if (bundle != null) {
                        spm_url = bundleSpm.getString(ProcessActivity.INTENT_KEY_SPM);
                        AppDebug.i(TAG, TAG + ".StartActivity.spm_url=" + spm_url);
                        Utils.updateNextPageProperties(spm_url);
                        AppDebug.d(TAG, "init activity startLoading " + getIntent() + ",data:" + intentValue.getData() + " , hasExtra:" + intentValue.hasExtra("data"));
                    }

                }

            }
        }
    }


    /**
     * 显示loading图片
     */
    private void displayImage() {
        String loadingJson = FileUtil.read(this, getFilesDir() + "/" + BaseConfig.LOADING_CACHE_JSON);
        AppDebug.v(TAG, "displayImage loadingJsonCache result:" + loadingJson);
        //如果缓存json不存在,使用默认图
        if (TextUtils.isEmpty(loadingJson)) {
            setBackgroudImage();
            return;
        }

        //转换为json格式
        List<LoadingBo> cacheListLoading = new ArrayList<LoadingBo>();
        cacheListLoading = JSON.parseArray(loadingJson, LoadingBo.class);
        //检查是否有当前需要展示的缓存图片
        for (int i = 0; i < cacheListLoading.size(); i++) {
            LoadingBo mLoadingBo = cacheListLoading.get(i);
            if (mLoadingBo == null || TextUtils.isEmpty(mLoadingBo.getStartTime())
                    || TextUtils.isEmpty(mLoadingBo.getEndTime())) {
                continue;
            }
            if (!TimeUtil.isBteenStartAndEnd(mLoadingBo.getStartTime(), mLoadingBo.getEndTime())) {
                continue;
            }
            String fileName = BaseConfig.LOADING_CACHE_DIR + "/" + mLoadingBo.getMd5();
            File file = new File(getFilesDir(), fileName);
            String md5 = MD5Util.getFileMD5(file);
            if (!TextUtils.isEmpty(md5)) {
                md5 = md5.toUpperCase();
            }
            AppDebug.i("md5", "md5:" + md5);
            if (mLoadingBo.getMd5().equals(md5)) {
                AppDebug.i("fileName", "fileName:" + getFilesDir() + "/" + fileName);
                mBitmap = FileUtil.getBitmap(this, file);
                if (mLoadingBo.getDuration() > 0) {
                    duration = mLoadingBo.getDuration() * 1000;
                }
                break;
            }
        }
        setBackgroudImage();
    }

    /**
     * 在主线程中设置背景图
     * 1.设置背景图
     * 2.写应用版本号
     */
    private void setBackgroudImage() {
        if (mBitmapDrawable == null) {
            if (mBitmap == null) {
                mBitmap = BitMapUtil.readBitMap(this, R.drawable.ytm_ui2_loading);
                AppDebug.i(TAG, "setBackgroudImage: R.drawable.ytm_ui2_loading" );
            }
            mBitmapDrawable = new BitmapDrawable(mBitmap);
        }
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                AppDebug.i(TAG, "setBackgroudImage for loadingLayout" );
                loadingLayout.setBackgroundDrawable(mBitmapDrawable);
//                fadeInAndShowImage(loadingLayout);
                if (!TextUtils.isEmpty(SystemConfig.APP_VERSION)) {
                    mAppVersion.setText(SystemConfig.APP_VERSION);
                }
                //检测网络
                if (!NetWorkUtil.isNetWorkAvailable()) {
                    registerNewWork();
                    mFirstStartReceiver = true;
                    showNetworkErrorDialog(true);
                } else {
                    gotoActivity();
                }
            }

        });
    }

    /**
     * 启动服务
     */
    private void startLoadingService() {
        Intent intent = new Intent(this, LoadingService.class);
        startService(intent);
    }

    public boolean needFinish = false;

    @Override
    protected void onPause() {
        super.onPause();

        if (RunMode.isYunos() == Environment.getInstance().isYunos()) {

            if (needFinish)
                finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    /**
     * 进入下一个页面
     */
    private void gotoActivityExt() {
        if (GlobalConfig.instance == null) {
            GlobalConfigInfo.getInstance().requestGlobalConfigData();
            BusinessRequest.getBusinessRequest().requestGlobalConfig(new RequestListener<GlobalConfig>() {
                @Override
                public void onRequestDone(GlobalConfig data, int resultCode, String msg) {
                    AppDebug.d(TAG, "GlobalConfig resultCode=" + resultCode + "data: " + data);
                    if (mHandler == null) {
                        mHandler = new LoadingHandler(LoadingActivity.this);
                    }
                    mHandler.sendEmptyMessage(0);
                }
            });
//            return;//FIXME maybe comment back
        }
        //初始化机顶盒号
        if (TextUtils.isEmpty(DeviceUtil.getStbID())) {
            if (mHandler == null) {
                mHandler = new LoadingHandler(this);
            }
            mHandler.sendEmptyMessage(2);
        }
        AppDebug.v(TAG, TAG + ".gotoActivityExt");
        //网络未通弹出提示框并返回
        if (!NetWorkUtil.isNetWorkAvailable()) {
            registerNewWork();
            mFirstStartReceiver = true;
            showNetworkErrorDialog(true);
            return;
        }

        // 外部调用自动拉起的欢迎页面
        if (mProcessActivity != null && mProcessActivity.isFromProcess()) {
            boolean result = mProcessActivity.processActivity();
            AppDebug.i(TAG, "mProcessActivity result=" + result);
        } else {
            AppDebug.v(TAG, TAG + ".gotoActivityExt.isYunos40 = " + SystemConfig.SYSTEM_YUNOS_4_0);
            Intent intent = new Intent();
            // 加new task的标识是为了首页进入应用默认的task里面，因为loading页面本身是自定义的专门给外部调用的task
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClassName(LoadingActivity.this, SWITCH_TO_HOME_ACTIVITY);
            startActivity(intent);
            needFinish = true;
//            finish();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_DOWN) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {

        if (RunMode.isYunos() == Environment.getInstance().isYunos()) {

            NetworkManager.instance().unregisterStateChangedListener(lnetWork);
            mProcessActivity.destroy();
            mProcessActivity = null;
            if (mBitmapDrawable != null) {
                mBitmapDrawable.setCallback(null);
            }
            if ((mBitmap != null) && (!mBitmap.isRecycled())) {
                mBitmap.recycle();
                mBitmap = null;
            }
        }
        super.onDestroy();

    }

    /**
     * 注册网络广播变化接收器
     */
    private void registerNewWork() {
        if (!mFirstStartReceiver)
            return;
        NetworkManager.instance().registerStateChangedListener(lnetWork);
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
     * 是否显示欢迎页面（默认是显示除非有指定关键字）
     *
     * @return
     */
    private boolean showLoading() {
        if (mProcessActivity != null) {
            boolean noShow = mProcessActivity.isNotShowLoading();
            if (noShow) {
                return false;
            }
        }
        return true;
    }

    /**
     * 显示UUID初始化失败信息
     */
    private void showUuidError() {
        mProgressBar.setVisibility(View.GONE);
        loadingWaitText.setVisibility(View.GONE);
        loadingFailText.setVisibility(View.VISIBLE);
        loadingFailButton.setVisibility(View.VISIBLE);
        loadingFailButton.requestFocus();
        loadingFailButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
    }


    /**
     * 跳转到指定页
     * 注：如果从外部分发过来的就跳转到指定的activity里面
     * 否则就跳转到首页
     */
    private void gotoActivity() {
        if (mHandler != null) {
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    gotoActivityExt();
                }
            }, duration);
        }
    }

    /**
     * 开始应用自升级
     */
    private void startUpdateApp() {
        AppDebug.i(TAG, TAG + ".startUpdateApp");
        try {
            PackageInfo packInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = packInfo.versionName;
            int versionCode = packInfo.versionCode;
            String channelId = Config.getChannel();
            String deviceId = CloudUUIDWrapper.getCloudUUID();
            UpdateClient client = UpdateClient.getInstance(getApplicationContext());
            client.startDownload("tvtaobao", versionName, versionCode, deviceId, channelId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static final class LoadingHandler extends AppHandler<LoadingActivity> {
        public LoadingHandler(LoadingActivity loadingActivity) {
            super(loadingActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            LoadingActivity mLoadingActivity = getT();
            if (mLoadingActivity == null || mLoadingActivity.isFinishing()) {
                return;
            }

            switch (msg.what) {
                case 0:
                    mLoadingActivity.gotoActivity();
                    break;
                case 1:
                    mLoadingActivity.showUuidError();
                    break;
                case 2:
                    DeviceUtil.initMacAddress(mLoadingActivity);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean isUpdate() {
        return false;
    }

    @Override
    public boolean isUpdateBlackList() {
        return true;
    }
}
