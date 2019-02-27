package com.yunos.tvtaobao.flashsale.view;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.tvlife.imageloader.core.listener.ImageLoadingListener;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tv.lib.DisplayUtil;
import com.yunos.tvtaobao.flashsale.R;
import com.yunos.tvtaobao.flashsale.utils.DateUtils;
import com.yunos.tvtaobao.flashsale.utils.FullVideoCountdown;
import com.yunos.tvtaobao.flashsale.utils.TbsUtil;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by guozhuoxing on 15-四月-29.
 */
public class AnimationDialog extends Dialog implements ImageLoadingListener, FullVideoCountdown.ICountdownListener {

    private static final String TAG = "AnimationDialog";
    private final static String AUTO_DESTROY = "auto";
    private final static String MANUAL_DESTROY = "manual";
    private final static String MENU_KEY_DESTROY = "menu";
    private String mDestroyType = AUTO_DESTROY;

    private final static int DURATION_TIME = 10 * 1000;
    private long mRemindTime;

    private final static int STEP_1 = 1;
    private final static int STEP_2 = 2;
    private final static int STEP_3 = 3;
    private DeeperLayer deeperLayer;
    private UpperLayer upperLayer;
    private Timer timer;
    private int step1 = 0;
    private int step2 = 1;
    private int step3 = 1;
    private float lineXend;
    private float pxLineXend = 320;
    private Myhandler myhandler;
    private Bitmap screenBitmap = null;
    private TimerTask task;
    private String screenInfo;
    private String anchorName;
    private String watchNumber;
    private String headUrl;
    private String screenUrl;
    private int isScreenConplete = 0;
    private Context mContext;
    private String pageId;
    private FrameLayout layout;
    private int tasktimes = 1;
    private float SCREENWIDTH;
    private float SCREENHEIGHT;

    private ImageLoaderManager mImageLoaderManager;

    public AnimationDialog(Context context) {
        super(context, R.style.Transparent);
        Window window = getWindow();
        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        window.addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);// 增加硬件加速
        window.setWindowAnimations(R.style.mystyle);
        mContext = context;
        setContentView(R.layout.dialog_layout);
        Display mDisplay = getWindow().getWindowManager().getDefaultDisplay();
        SCREENWIDTH = mDisplay.getWidth();
        SCREENHEIGHT = mDisplay.getHeight();
        AppDebug.w("AnimationDialog", "Device resolution Width = " + SCREENWIDTH + " Height " + SCREENHEIGHT);
        mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(mContext);
    }

    public void dialogShow(String screen, String head, String info, String name, String Number, String id, long realTime) {
        initView();
        screenInfo = info;
        anchorName = name;
        watchNumber = Number;
        headUrl = head;
        screenUrl = screen;
        pageId = id;
        mRemindTime = realTime;

        mDestroyType = AUTO_DESTROY;

        isScreenConplete = 0;

        if (null != headUrl) {
            mImageLoaderManager.loadImage(headUrl, this);
        }

        if (null != screenUrl) {
            mImageLoaderManager.loadImage(screenUrl, this);
        }

        startCountdownTimer();
    }

    public void show() {
        super.show();
        startAnimation();
    }

    public void cancel() {
        super.cancel();
            AppDebug.w(TAG,"animationDialog cancel ");
        if (timer != null) {
            timer.cancel();
        }
        layout.removeView(deeperLayer);
        layout.removeView(upperLayer);
        if (null != screenBitmap) {
            screenBitmap.recycle();
            screenBitmap = null;
        }
    }

    private void initView() {
        layout = (FrameLayout) findViewById(R.id.root);
        myhandler = new Myhandler();
        deeperLayer = new DeeperLayer(mContext);
        upperLayer = new UpperLayer(mContext);
        deeperLayer.setPXALL(SCREENWIDTH, SCREENHEIGHT);
        upperLayer.setPXALL(SCREENWIDTH, SCREENHEIGHT);
        pxLineXend = mContext.getResources().getDimension(R.dimen.lines_length);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(DisplayUtil.dip2px(mContext, 320), DisplayUtil.dip2px(
                mContext, 180));
        params.gravity = (Gravity.BOTTOM | Gravity.LEFT);
        deeperLayer.setLayoutParams(params);
        //通知view组件重绘
        //        deeperLayer.setSweepAngl(0);
        params.gravity = (Gravity.BOTTOM | Gravity.LEFT);
        upperLayer.setLayoutParams(params);
        //通知view组件重绘
        layout.addView(deeperLayer);
        layout.addView(upperLayer);

    }

    public void startAnimation() {
        step1 = 0;
        step2 = 1;
        step3 = 1;
        timer = new Timer(true);
        deeperLayer.setData(screenBitmap, null, screenInfo, anchorName, watchNumber);
        if (screenBitmap == null)
            screenBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.remind_taoqiangou);
        deeperLayer.setImage(screenBitmap, null);
        deeperLayer.initLocation();
        upperLayer.init();
        deeperLayer.setImageProperty(0, 1, 0, false);
        upperLayer.init();
        task = new TimerTask() {

            public void run() {
                //                myhandler.sendMessage(message);
                animationController();
                tasktimes++;

            }
        };
        timer.schedule(task, 100, 10);
    }

    @Override
    public void onLoadingStarted(String s, View view) {
        AppDebug.i("Tag", " onLoadingStarted " + s);

    }

    @Override
    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
        if (s.equals(headUrl)) {
            AppDebug.i("Tag", " head complete " + s);
            isScreenConplete = isScreenConplete + 1;

        }
        if (s.equals(screenUrl)) {
            isScreenConplete = isScreenConplete + 1;
            screenBitmap = bitmap;
            AppDebug.i("Tag", " screen complete " + s);

        }
        if (isScreenConplete == 2) {
            Message msg = new Message();
            msg.what = 1;
            myhandler.sendMessage(msg);
        }
    }

    @Override
    public void onLoadingCancelled(String s, View view) {

    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();

        AppDebug.d("", "keyCode = " + keyCode);

        //如果是语音搜索键
        if (keyCode == KeyEvent.KEYCODE_UNKNOWN || keyCode == 142) {
            return super.dispatchKeyEvent(event);
        }

        if ((null != upperLayer && !upperLayer.isAnimationEnd())
                || (null != deeperLayer && !deeperLayer.isAllAnimationEnd())) {
            return true;
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_MENU:
                    AppDebug.w(TAG, "pageId " + pageId);
                    clearCountdownTimer();
                    mDestroyType = MENU_KEY_DESTROY;
                    tbsPopupEventExit();
                    cancel();
                    startMyconcernActivity();
                    return true;
                default:
                    mDestroyType = MANUAL_DESTROY;
                    cancel();
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    FullVideoCountdown mCountdown = null;

    private void startCountdownTimer() {
        mCountdown = new FullVideoCountdown(DURATION_TIME, 1000);
        mCountdown.setListener(this);
        mCountdown.start();
    }

    private void clearCountdownTimer() {
        if (null != mCountdown) {
            mCountdown.cancel();
            mCountdown = null;
        }
    }

    class Myhandler extends Handler {

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                show();
                return;
            }

            //第1步 画菜单按钮
            if (msg.what == 2) {
                deeperLayer.invalidate();
            }
            if (msg.what == 3) {
                upperLayer.invalidate();
            }
        }
    }

    private final static int ANI_FRAME = 500;

    public void animationController() {
        Log.v("increments", "timertasks " + tasktimes);
        step1++;
        float angle = 360 * 10 * step1 / ANI_FRAME;
        if (angle <= (float) 360) {
            //        	if(AppConfig.DEBUG){
            //        		LogUtil.w(" 第一类动画执行");
            //        	}
            deeperLayer.setStep(STEP_1);
            lineXend = pxLineXend * 10 * step1 / ANI_FRAME;
            deeperLayer.setSweepAngl(angle);
            deeperLayer.setLineXend(lineXend);

            Message message = new Message();
            message.what = 2;
            myhandler.sendMessage(message);
            //                    deeperLayer.invalidate();

        } else {
            //第2步动态画初始斜线
            deeperLayer.setStep(STEP_2);
            if (!deeperLayer.isStep2LineAnimationEnd()) {
                //            	if(AppConfig.DEBUG){
                //            		LogUtil.w(" 第二类动画执行");
                //            	}
                Message message = new Message();
                message.what = 2;
                myhandler.sendMessage(message);
                //                        deeperLayer.invalidate();
            } else {
                //            	if(AppConfig.DEBUG){
                //            		LogUtil.w(" 第三类动画执行");
                //            	}

                //第3步开始画平移动画
                deeperLayer.setStep(STEP_3);

                if (!upperLayer.isAnimationEnd()) {
                    upperLayer.updateScripeLocation();
                    Message message = new Message();
                    message.what = 3;
                    myhandler.sendMessage(message);
                    //                            upperLayer.invalidate();
                }

                if (!deeperLayer.isAllAnimationEnd()) {

                    //                	if(AppConfig.DEBUG){
                    //                		LogUtil.w(" 平移执行第"+step3+"次");
                    //                	}
                    Message message = new Message();
                    message.what = 2;
                    myhandler.sendMessage(message);
                    step3++;
                    //                            deeperLayer.invalidate();
                } else {
                    //                	if(AppConfig.DEBUG){
                    //                		LogUtil.e("结束动画");
                    //                	}
                    if (null != timer) {
                        timer.cancel();
                        timer = null;
                    }
                }
            }

        }

    }

    @Override
    public void onTick(long millsUntilFinished) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFinish() {
        // TODO Auto-generated method stub
        cancel();
        tbsPopupEventExit();
    }

    @Override
    public void onAnimtionPause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLoadingFailed(String imageUri, View view, com.tvlife.imageloader.core.assist.FailReason failReason) {
        // TODO Auto-generated method stub

    }

    private void tbsPopupEventEntry() {
        Map<String, String> prop = Utils.getProperties();
        prop.put("time", DateUtils.timestamp2String(mRemindTime));
        Utils.utCustomHit(Utils.utGetCurrentPage(), TbsUtil.CUSTOM_PopUp_Click, prop);
    }

    private void tbsPopupEventExit() {
        Map<String, String> prop = Utils.getProperties();
        prop.put("time", DateUtils.timestamp2String(mRemindTime));
        prop.put("type", mDestroyType);
        Utils.utCustomHit(Utils.utGetCurrentPage(), TbsUtil.CUSTOM_PopUp_Disappear, prop);
    }

    private void startMyconcernActivity() {
        tbsPopupEventEntry();
        String url = "tvtaobao://home?app=flashsale&from=homepage&pageType=0";
        //		String url = "tvtaobao://flashsale?app=flashsale&from=homepage&pageType=0";
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(uri);
        mContext.startActivity(intent);
    }
}
