package com.yunos.tvtaobao.live.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.media.ijkmediaplayer.IjkVideoView;
//import com.taobao.tao.powermsg.common.Constant;
//import com.taobao.tao.powermsg.common.IPowerMsgCallback;
//import com.taobao.tao.powermsg.common.PowerMsgService;
import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvlife.imageloader.core.assist.FailReason;
import com.tvlife.imageloader.core.listener.ImageLoadingListener;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.app.widget.focus.listener.FocusListener;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.pay.LoginHelperImpl;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.dialog.util.SnapshotUtil;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.LiveBlackList;
import com.yunos.tvtaobao.biz.request.bo.LiveBonusResult;
import com.yunos.tvtaobao.biz.request.bo.LiveBonusTimeItem;
import com.yunos.tvtaobao.biz.request.bo.LiveBonusTimeResult;
import com.yunos.tvtaobao.biz.request.bo.LiveDetailBean;
import com.yunos.tvtaobao.biz.request.bo.LiveFollowResult;
import com.yunos.tvtaobao.biz.request.bo.LiveIsFollowStatus;
import com.yunos.tvtaobao.biz.request.bo.TBaoLiveListBean;
import com.yunos.tvtaobao.biz.request.bo.TBaoShopBean;
import com.yunos.tvtaobao.biz.request.item.GetLiveListRequest;
import com.yunos.tvtaobao.live.R;
import com.yunos.tvtaobao.live.adapter.LiveCommectAdapter;
import com.yunos.tvtaobao.live.adapter.TBaoShopAdapter;
import com.yunos.tvtaobao.live.controller.LiveDataController;
import com.yunos.tvtaobao.live.data.CommentBase;
import com.yunos.tvtaobao.live.request.GetLiveDetailRequest;
import com.yunos.tvtaobao.live.request.GetLiveHotItemListRequest;
import com.yunos.tvtaobao.live.request.LiveBlackListRequest;
import com.yunos.tvtaobao.live.tvtaomsg.TvTaobaoMsgService;
import com.yunos.tvtaobao.live.tvtaomsg.po.TVTaoMessage;
import com.yunos.tvtaobao.live.tvtaomsg.service.ITVTaoDiapatcher;
import com.yunos.tvtaobao.live.utils.AnimUtils;
//import com.yunos.tvtaobao.live.utils.PowerMsgHelper;
import com.yunos.tvtaobao.live.utils.PowerMsgType;
import com.yunos.tvtaobao.live.utils.Tools;
import com.yunos.tvtaobao.live.view.CareFocusTipView;
import com.yunos.tvtaobao.live.view.Displayer;
import com.yunos.tvtaobao.live.view.FocusImageView;
import com.yunos.tvtaobao.live.view.LiveFocusPositionManager;
import com.yunos.tvtaobao.live.view.LiveListDialog;
import com.yunos.tvtaobao.live.view.TVmallAnimationActivityNew;
import com.yunos.tvtaobao.live.view.ZPListView;
import com.yunos.tvtaobao.live.view.heart.TBaoPeriscopeLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by pan on 16/9/23.
 *
 * @
 */
public class TBaoLiveActivity extends BaseActivity implements View.OnClickListener, Handler.Callback {
    private static final String TAG = "TVLive_Taobao";
    private ZPListView rv_live_product;
    private LinearLayoutManager linearLayoutManager;
    private RelativeLayout not_product_prompt,tbao_custom_live_info_prompt ,tbao_live_info_prompt, voice_chat_prompt_layout;
    private IjkVideoView vv_live;

    private RelativeLayout liveFloatLayout;
    private LinearLayout rl_qr;
    private ImageView iv_back_ground, tbao_live_loading_img;
    private TextView joincount, tv_praise_count;
    private FocusImageView iv_praise, iv_tao_live_list, iv_care, iv_light;
    private CareFocusTipView careFocusTipView;
    private TBaoShopAdapter shopAdapter;

    private ImageView iv_head_icon, online_head_pic_1, online_head_pic_2, online_head_pic_3, online_head_pic_4, online_head_pic_5;
    private ImageView[] online_head_pic_byte;
    private TextView tv_name, tv_location, tbao_live_title;
    //聊天
    private LiveCommectAdapter commentAdapter;
    private ListView ll_live_comment;
    private LinearLayout ll_live_toplayout;
    private TextView ll_live_topcomment;
    private ImageView ll_live_topicon;
    private BitmapDrawable shopcart;
    private BitmapDrawable attention;
    //登录提示
    private TextView tvMessageToast;

    //二维码
    private ImageView iv_live_rq_image, iv_live_qr_icon;
    private TextView tv_live_qr_nickname, tv_live_qr_popularity, tv_live_qr_fans;
    private LiveDetailBean liveDetailBean;

    private View oldFocusView;
    private TBaoPeriscopeLayout hl_praise;

    //节目列表聚焦显示文案
    private TextView tvLiveList;

    private String liveUrl, liveId, accountId, liveTitle, topic, headImg, accountNick, landScape;
    private int fansNum = 0;
    private int currentTBaoLivePos = 0;
    private static boolean isLight = true;
    private long changeJoinCount, totalJoinCount;
    private Double praise_count = 0.0, praise_count_click = 0.0;

    private BusinessRequest mBusinessRequest;
    private ImageLoaderManager mImageLoaderManager;
    private LiveDataController mLiveDataController;
    private List<TBaoLiveListBean> mTBaoLiveListBean = null;
    private DisplayImageOptions roundImageOptions;
    private Handler mHandler = new Handler(this);
    private LiveListDialog liveListDialog;

    private String codeLevel = "2";//high quality

    private LiveFocusPositionManager focusPositionManager;

    private TVmallAnimationActivityNew animation;
    private boolean isFollow;
    //直播流状态 直播中：0 ，直播结束：1 ， 直播流中断：2  ，直播未开始：3
    private String status;
    private String coverImg;
    private String source;
    private Timer mTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, TAG + ".onCreate");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        onKeepActivityOnlyOne(TBaoLiveActivity.class.getName());

        liveUrl = getIntent().getStringExtra("liveUrl");
        liveId = getIntent().getStringExtra("liveId");
        accountId = getIntent().getStringExtra("accountId");
        topic = getIntent().getStringExtra("topic");
        codeLevel = getIntent().getStringExtra("codeLevel");
        if (TextUtils.isEmpty(codeLevel)) {
            codeLevel = "2";
        }
        Log.i(TAG, TAG + ".onCreate liveUrl = " + liveUrl + " ,liveId : " + liveId + " ,accountId : " + accountId + " ,topic : " + topic);
        setContentView(R.layout.ytm_v_live_window);
        initData();
        findView();
        loadingState();
        if (TextUtils.isEmpty(liveId)) {
            mBusinessRequest.baseRequest(new GetLiveListRequest(), new GetTBaoLiveListListener(new WeakReference<BaseActivity>(this)), false);
        } else {
            requestData(liveId, accountId);
        }
    }


    /**
     * 初始化数据
     */
    private void initData() {
        mBusinessRequest = BusinessRequest.getBusinessRequest();
        mLiveDataController = LiveDataController.getInstance();
        mTBaoLiveListBean = mLiveDataController.getTBaoLiveListBean();
        AppDebug.e(TAG, "mTBaoLiveListBean = " + mTBaoLiveListBean);
        currentTBaoLivePos = mLiveDataController.getCurrentTBaoLivePos();
        mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(this);
        roundImageOptions = new DisplayImageOptions.Builder().displayer(new Displayer(0)).build();

        commentList = new ArrayList<>();
        commentAdapter = new LiveCommectAdapter(TBaoLiveActivity.this);
        shopAdapter = new TBaoShopAdapter(TBaoLiveActivity.this);
    }

    private void requestData(String liveid, String accountid) {
        AppDebug.i(TAG, TAG + "requestData liveId : " + liveid + " ,accountId : " + accountid);

        //黑名单列表
        mBusinessRequest.baseRequest(new LiveBlackListRequest(), new GetLiveBlackListener(new WeakReference<BaseActivity>(this)), false);
        //直播详情
        mBusinessRequest.baseRequest(new GetLiveDetailRequest(liveid), new GetLiveDetailListener(new WeakReference<BaseActivity>(this)), false);
        //商品详情
        mBusinessRequest.baseRequest(new GetLiveHotItemListRequest("0", liveid, accountid), new GetLiveHotItemListlListener(new WeakReference<BaseActivity>(this)), false);
        //获取权益发放的时间
        mBusinessRequest.getLiveBonusTime(new GetLiveBonusTimeListener(new WeakReference<BaseActivity>(this)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //subscribeTopic(topic);
        if (liveDetailBean != null) {
            //直播详情
            mBusinessRequest.baseRequest(new GetLiveDetailRequest(liveDetailBean.getLiveId()), new GetLiveDetailListener(new WeakReference<BaseActivity>(this)), false);
        }
        //startLoopNotify();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unSubscribeTopic(topic);
        vv_live.pause();
        removeNotify();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onRemoveKeepedActivity(TBaoLiveActivity.class.getName());
        vv_live.release();
        clearData();
        clearViews();
        mHandler = null;
        shopcart = null;
        attention = null;
//        powerMsgCallback = null;
        if (animation != null && animation.getParent() != null) {
            WindowManager wm = getWindowManager();
            if (wm != null)
                wm.removeView(animation);
        }
    }

    private void clearViews() {
        careFocusTipView = null;
        iv_praise.setOnClickListener(null);
        iv_care.setOnClickListener(null);
        iv_tao_live_list.setOnClickListener(null);
        iv_light.setOnClickListener(null);
        iv_care.setOnFocusChangeListener(null);
        vv_live.setOnErrorListener(null);
        vv_live.setOnPreparedListener(null);
        vv_live.setOnInfoListener(null);

        vv_live = null;
        linearLayoutManager = null;
        tbao_live_info_prompt = null;
        rv_live_product = null;
        not_product_prompt = null;
        rl_qr = null;
        iv_back_ground = null;
        tbao_live_loading_img = null;
        voice_chat_prompt_layout = null;

        iv_light = null;
        iv_live_qr_icon = null;
        iv_praise = null;
        iv_care = null;
        iv_tao_live_list = null;
        iv_live_rq_image = null;

        tv_name = null;
        tv_live_qr_nickname = null;
        tv_location = null;
        iv_head_icon = null;
        tbao_live_title = null;
        tv_live_qr_popularity = null;
        tv_live_qr_fans = null;
        joincount = null;
        tv_praise_count = null;
        online_head_pic_1 = null;
        online_head_pic_2 = null;
        online_head_pic_3 = null;
        online_head_pic_4 = null;
        online_head_pic_5 = null;
        online_head_pic_byte = null;
        oldFocusView = null;

        ll_live_comment = null;
        ll_live_toplayout = null;
        ll_live_topcomment = null;
        ll_live_topicon = null;
        shopcart = null;
        attention = null;
        hl_praise = null;

        commentList.clear();
        commentAdapter = null;
        shopAdapter = null;
        LiveListDialog.getInstance(this).destory();
        if (focusPositionManager != null)
            focusPositionManager.removeAllViews();
        focusPositionManager = null;
//        if (alphaAnimation != null)
//            alphaAnimation.setAnimationListener(null);
//        alphaAnimation = null;
        mBusinessRequest = null;
        mImageLoaderManager = null;
        mHandler = null;
        mTimer = null;
    }

    private void clearData() {
        commentList.clear();
        if (commentAdapter != null)
            commentAdapter.clearData();

        if (shopAdapter != null)
            shopAdapter.clearData();

        LiveDataController.getInstance().clear();
        online_head_pic_1.setImageDrawable(null);
        online_head_pic_2.setImageDrawable(null);
        online_head_pic_3.setImageDrawable(null);
        online_head_pic_4.setImageDrawable(null);
        online_head_pic_5.setImageDrawable(null);
    }

    private void removeNotify() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        //mImageLoaderManager.clearMemoryCache();
    }

    private void findView() {
        focusPositionManager = (LiveFocusPositionManager) findViewById(R.id.activity_main);
        focusPositionManager.setBackgroundDrawable(Tools.getBitmapDrawable(this, R.drawable.ytm_live_background));
        focusPositionManager.setOnkeyDownListener(new LiveFocusPositionManager.OnKeyDownListener() {
            @Override
            public boolean onKeyDown(int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
                    if(!isLight){
                        //关灯状态,屏蔽向左移动焦点
                        if(getFocusView() == iv_tao_live_list){
                            iv_tao_live_list.requestFocus();
                            return true;
                        }
                    }
                }
                return focusPositionManager.superOnKeyDown(keyCode,event);
            }
        });
//        focusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(
//                R.drawable.focusbox)));
        liveFloatLayout = (RelativeLayout) findViewById(R.id.live_float_layout);
        vv_live = (IjkVideoView) findViewById(R.id.vv_live);
        //全屏主播已离开
        tbao_custom_live_info_prompt = (RelativeLayout)findViewById(R.id.tbao_custom_live_info_prompt);
        tbao_live_info_prompt = (RelativeLayout) findViewById(R.id.tbao_live_info_prompt);
        rv_live_product = (ZPListView) findViewById(R.id.rv_live_product);
        linearLayoutManager = new LinearLayoutManager(TBaoLiveActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_live_product.setLayoutManager(linearLayoutManager);
        not_product_prompt = (RelativeLayout) findViewById(R.id.not_product_prompt);
        rl_qr = (LinearLayout) findViewById(R.id.rl_qr);
        iv_back_ground = (ImageView) findViewById(R.id.iv_back_ground);
        tbao_live_loading_img = (ImageView) findViewById(R.id.tbao_live_loading_img);
        tbao_live_loading_img.setImageDrawable(Tools.getBitmapDrawable(this, R.drawable.tbao_live_loading_img));
        voice_chat_prompt_layout = (RelativeLayout) findViewById(R.id.voice_chat_prompt_layout);

        careFocusTipView = new CareFocusTipView(this);

        iv_light = (FocusImageView) findViewById(R.id.iv_light);
        iv_live_qr_icon = (ImageView) findViewById(R.id.iv_live_qr_icon);
        iv_praise = (FocusImageView) findViewById(R.id.iv_praise);
        tvLiveList = (TextView) findViewById(R.id.tv_live_list);
        iv_care = (FocusImageView) findViewById(R.id.iv_care);
        iv_tao_live_list = (FocusImageView) findViewById(R.id.iv_tao_live_list);
        focusPositionManager.requestFocus(iv_tao_live_list, 0);
        iv_light.setAnimateWhenGainFocus(false, false, false, false);
        iv_tao_live_list.setAnimateWhenGainFocus(false, false, false, false);
        iv_care.setAnimateWhenGainFocus(false, false, false, false);
        iv_praise.setAnimateWhenGainFocus(false, false, false, false);
        iv_live_rq_image = (ImageView) findViewById(R.id.iv_live_rq_image);

        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_live_qr_nickname = (TextView) findViewById(R.id.tv_live_qr_nickname);
        tv_location = (TextView) findViewById(R.id.tv_location);
        iv_head_icon = (ImageView) findViewById(R.id.iv_head_icon);
        tbao_live_title = (TextView) findViewById(R.id.tbao_live_title);
        tv_live_qr_popularity = (TextView) findViewById(R.id.tv_live_qr_popularity);
        tv_live_qr_fans = (TextView) findViewById(R.id.tv_live_qr_fans);
        joincount = (TextView) findViewById(R.id.joincount);
        tv_praise_count = (TextView) findViewById(R.id.tv_praise_count);
        online_head_pic_1 = (ImageView) findViewById(R.id.online_head_pic_1);
        online_head_pic_2 = (ImageView) findViewById(R.id.online_head_pic_2);
        online_head_pic_3 = (ImageView) findViewById(R.id.online_head_pic_3);
        online_head_pic_4 = (ImageView) findViewById(R.id.online_head_pic_4);
        online_head_pic_5 = (ImageView) findViewById(R.id.online_head_pic_5);
        online_head_pic_byte = new ImageView[]{online_head_pic_1, online_head_pic_2, online_head_pic_3, online_head_pic_4, online_head_pic_5};

        ll_live_comment = (ListView) findViewById(R.id.ll_live_comment);
        ll_live_toplayout = (LinearLayout) findViewById(R.id.ll_live_toplayout);
        ll_live_topcomment = (TextView) findViewById(R.id.ll_live_topcomment);
        ll_live_topicon = (ImageView) findViewById(R.id.ll_live_topicon);
        shopcart = Tools.getBitmapDrawable(this, R.drawable.tbao_live_shopcart_icon);
        attention = Tools.getBitmapDrawable(this, R.drawable.tbao_live_attention_icon);
        hl_praise = (TBaoPeriscopeLayout) findViewById(R.id.hl_praise);

        //登录提示
        tvMessageToast = (TextView) findViewById(R.id.tv_message_toast);
        if (!CoreApplication.getLoginHelper(this).isLogin()) {
            AnimationSet animationSet = new AnimationSet(true);
            TranslateAnimation wholeCatAnimationDismiss = new TranslateAnimation(0, 0, -300, 0);
            animationSet.addAnimation(wholeCatAnimationDismiss);
            animationSet.setDuration(1500);
            tvMessageToast.startAnimation(animationSet);
            wholeCatAnimationDismiss.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    Map<String, String> properties = Utils.getProperties();
                    properties.put("spm", SPMConfig.LIVE_TIPS_SHOW_SPM);
                    Utils.utCustomHit(getFullPageName(),"Expose_tips", properties);
                    tvMessageToast.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mTimer = new Timer();
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AnimationSet animationSet = new AnimationSet(true);
                                    TranslateAnimation wholeCatAnimationDismiss = new TranslateAnimation(0, 0, 0, -300);
                                    wholeCatAnimationDismiss.setFillAfter(true);
                                    animationSet.addAnimation(wholeCatAnimationDismiss);
                                    animationSet.setDuration(1500);
                                    animationSet.setFillAfter(true);
                                    tvMessageToast.startAnimation(animationSet);
                                }
                            });

                        }
                    },5000);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        } else {
            tvMessageToast.setVisibility(View.GONE);
        }

        //todo add config
//        if ((Config.getChannel().equals("701229") || Config.getChannel().equals("10003226") || Config.getChannel().equals("10004416")) && DeviceUtil.getYuyinPackageCode(this) >= 2100300000)
//            voice_chat_prompt_layout.setVisibility(View.VISIBLE);

        iv_praise.setOnClickListener(this);
        iv_care.setOnClickListener(this);
        iv_tao_live_list.setOnClickListener(this);
        iv_light.setOnClickListener(this);
        iv_tao_live_list.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    tvLiveList.setVisibility(View.VISIBLE);
                }else {
                    tvLiveList.setVisibility(View.GONE);

                }

            }
        });

        iv_care.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                if (rl_qr.getVisibility() == View.VISIBLE) {
//                    AnimUtils.fadeOut(rl_qr, 300);
//                    iv_care.setImageResource(R.drawable.ytm_tbao_live_qrcode_btn_bg);
//                }
                if (hasFocus) {
                    careFocusTipView.show(headImg, accountNick, fansNum
                            , CoreApplication.getLoginHelper(TBaoLiveActivity.this).isLogin()
                            , isFollow, iv_care);
                    if (isFollow) {
                        iv_care.setImageResource(R.drawable.ytm_tbao_live_followed_focused);
                    } else {
                        iv_care.setImageResource(R.drawable.ytm_tbao_live_unfollow_focused);
                    }
                } else {
                    careFocusTipView.hide();
                    if (isFollow) {
                        iv_care.setImageResource(R.drawable.ytm_tbao_live_followed_unfocuse);
                    } else {
                        iv_care.setImageResource(R.drawable.ytm_tbao_live_unfollow_unfocuse);

                    }
                }
            }
        });
        initComment();
        videoListener();
    }

    private void videoListener() {
        vv_live.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                AppDebug.d(TAG, TAG + ".videoListener onError");
                videoError();
                return false;
            }
        });

        vv_live.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                AppDebug.d(TAG, TAG + ".videoListener onCompletion");
                videoError();
            }
        });

        vv_live.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                AppDebug.d(TAG, TAG + ".videoListener onPrepared");
                initScreenOrientation(landScape, coverImg, source);
                vv_live.setVisibility(View.VISIBLE);
                tbao_custom_live_info_prompt.setVisibility(View.GONE);
                tbao_live_info_prompt.setVisibility(View.GONE);
                tbao_live_loading_img.setVisibility(View.GONE);
                vv_live.start();
            }
        });

        vv_live.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int i1) {
                boolean handled = false;
                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        AppDebug.d(TAG, "onInfo MediaPlayer.MEDIA_INFO_BUFFERING_START");
                        handled = true;
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        AppDebug.d(TAG, "onInfo MediaPlayer.MEDIA_INFO_BUFFERING_END");
                        handled = true;
                        break;
                }
                return handled;
            }
        });
    }

    private void initComment() {
        ll_live_comment.setStackFromBottom(true);
        ll_live_comment.setFocusable(false);
        ll_live_comment.setAdapter(commentAdapter);
    }

    private void startLoopNotify() {
        mHandler.sendEmptyMessageDelayed(PowerMsgType.LIVE_COMMENT_NOTIFY, 500);
        mHandler.sendEmptyMessageDelayed(PowerMsgType.LIVE_COMMENT_UP_NOTIFY, 2000);
        mHandler.sendEmptyMessageDelayed(PowerMsgType.LIVE_CHANGE_ONLINE_HEAD_MSG, 3000);
        mHandler.sendEmptyMessageDelayed(PowerMsgType.LIVE_PRAISE_SEND, 2000);
    }

    /**
     * 开灯
     */
    private void toLight() {
        findViewById(R.id.ll_first_module).setVisibility(View.VISIBLE);
        findViewById(R.id.ll_right_top_moudle).setVisibility(View.VISIBLE);
        iv_back_ground.setVisibility(View.VISIBLE);
        findViewById(R.id.iv_total_joincount_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.tbao_live_comment_layout).setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        Map<String, String> properties = Utils.getProperties();
        int i = v.getId();
        if (i == R.id.iv_light) {
//            showBonusAnimation(0, "测试信息");
            if (!isLight) {
                //开灯
                toLight();
                properties.put("spm", SPMConfig.LIVE_LIGHT_CLICK_SPM);
                Utils.utControlHit(getFullPageName(),"button_light", properties);
                isLight = true;
            } else {
                //关灯
                findViewById(R.id.ll_first_module).setVisibility(View.INVISIBLE);
                findViewById(R.id.ll_right_top_moudle).setVisibility(View.INVISIBLE);
                iv_back_ground.setVisibility(View.INVISIBLE);
                findViewById(R.id.iv_total_joincount_layout).setVisibility(View.INVISIBLE);
                findViewById(R.id.tbao_live_comment_layout).setVisibility(View.INVISIBLE);
                properties.put("spm", SPMConfig.LIVE_LIGHT_CLICK_SPM);
                Utils.utControlHit(getFullPageName(),"button_light", properties);
                isLight = false;
            }


        } else if (i == R.id.iv_praise) {
            hl_praise.addHeart();
            praise_count++;
            tv_praise_count.setText(Tools.getNum(praise_count));
            praise_count_click++;
            properties.put("spm", SPMConfig.LIVE_ZAN_CLICK_SPM);
            Utils.utControlHit(getFullPageName(),"button_like", properties);

        } else if (i == R.id.iv_tao_live_list) {
            if (liveListDialog == null)
                liveListDialog = LiveListDialog.getInstance(this);
            liveListDialog.show(iv_tao_live_list);
            setFocusableF();
            properties.put("spm", SPMConfig.LIVE_LIST_MENU_CLICK_SPM);
            Utils.utControlHit(getFullPageName(),"button_VideoList", properties);

        } else if (i == R.id.iv_care) {
            properties.put("spm", SPMConfig.LIVE_FOLLOW_CLICK_SPM);
            Utils.utControlHit(getFullPageName(),"button_follow", properties);
            if (!TextUtils.isEmpty(accountId)) {
                if (isFollow) {
                    mBusinessRequest.getLiveCancelFollowResult(accountId, new GetLiveCancelFollowResultListener(new WeakReference<BaseActivity>(this)));
                } else {
                    mBusinessRequest.getLiveFollowResult(accountId, new GetLiveFollowResultListener(new WeakReference<BaseActivity>(this)));
                }
            }

//            if (rl_qr.getVisibility() == View.VISIBLE) {
//                AnimUtils.fadeOut(rl_qr, 300);
//                iv_care.setImageResource(R.drawable.ytm_tbao_live_qrcode_btn_bg);
//            } else {
//                AlphaAnimation animation = new AlphaAnimation(0f, 1f);
//                animation.setDuration(300);
//                animation.setAnimationListener(new Animation.AnimationListener() {
//                    @Override
//                    public void onAnimationStart(Animation animation) {
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//                        rl_qr.setVisibility(View.VISIBLE);
//                        createQR();
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animation animation) {
//                    }
//                });
//                rl_qr.startAnimation(animation);
//                iv_care.setImageResource(R.drawable.live_qrcode_open);
//            }

        }
    }



    public void getDetailDate(LiveDetailBean data) {
        Log.e(TAG, TAG + ".getDetailDate data : " + data);
        if (data == null)
            return;

        liveDetailBean = data;
        String location = data.getLocation();
        coverImg = data.getCoverImg();
        source = data.getSource();
        accountNick = data.getAccountNick();
        liveTitle = data.getTitle();
        headImg = data.getHeadImg();
        liveUrl = data.getInputStreamUrl();
        fansNum = data.getFansNum();
        accountId = data.getAccountId();
        status = data.getStatus();
        isFollow = false;
        if (CoreApplication.getLoginHelper(this).isLogin() && !TextUtils.isEmpty(accountId)) {
            //获取是否有关注主播的状态
            //iv_care.setVisibility(View.INVISIBLE);
            mBusinessRequest.getLiveIsFollowStatus(accountId, new GetLiveIsFollowStatusListener(new WeakReference<BaseActivity>(this)));
        } else {
            iv_care.setVisibility(View.VISIBLE);
            View focusView = getFocusView();
            if(focusView == iv_care){
                iv_care.setImageResource(R.drawable.ytm_tbao_live_unfollow_focused);
            }else {
                iv_care.setImageResource(R.drawable.ytm_tbao_live_unfollow_unfocuse);
            }
        }

        tv_name.setText(accountNick);
        tv_live_qr_nickname.setText(accountNick);
        tbao_live_title.setText(liveTitle);
        tv_location.setText(location);
        tv_praise_count.setText(Tools.getNum(praise_count));
        mImageLoaderManager.displayImage(headImg, iv_head_icon, roundImageOptions);

        initVideoView(liveUrl);
    }


    /**
     * @param landScape 横竖屏标识
     * @param coverImg  竖屏毛玻璃海报
     * @param source    直播来源
     */
    private void initScreenOrientation(String landScape, String coverImg, String source) {
        AppDebug.i(TAG, "landScape = " + landScape);
        if ("true".equals(landScape) || "CUSTOM".equals(source)) {
            //横屏
            if ("CUSTOM".equals(source)) {
                //全屏双11晚会
                liveFloatLayout.setVisibility(View.GONE);
            } else {
                liveFloatLayout.setVisibility(View.VISIBLE);
            }
            //设置右侧背景透明
            iv_back_ground.setImageDrawable(getResources().getDrawable(R.drawable.bg_transparent));
            //设置视频区域
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(vv_live.getLayoutParams());
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.setMargins(0, 0, 0, 0);
            vv_live.setLayoutParams(lp);
        } else if ("false".equals(landScape)) {
            //竖屏
            liveFloatLayout.setVisibility(View.VISIBLE);
            //设置右侧背景毛玻璃效果
            setGuess(coverImg);
            //设置视频区域
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(vv_live.getLayoutParams());
            lp.width  = (int) getResources().getDimension(R.dimen.dp_414);
            int marginLeft = (int) getResources().getDimension(R.dimen.dp_392);
            lp.setMargins(marginLeft, 0, 0, 0);
            vv_live.setLayoutParams(lp);
        }
    }

    private void showBonusAnimation(int type, String bouns) {
        WindowManager mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mLayoutParams.format = PixelFormat.TRANSLUCENT;
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        if (animation == null)
            animation = new TVmallAnimationActivityNew(this);
        mWindowManager.addView(animation, mLayoutParams);
        animation.setText(type, bouns);
    }

    private void initShopList(TBaoShopBean tBaoShopBean) {
        if (tBaoShopBean == null || tBaoShopBean.getItemList() == null || tBaoShopBean.getItemList().size() == 0) {
            not_product_prompt.setVisibility(View.VISIBLE);
            rv_live_product.setAdapter(null);
            rv_live_product.setVisibility(View.GONE);
            return;
        } else {
            not_product_prompt.setVisibility(View.GONE);
            rv_live_product.setVisibility(View.VISIBLE);
        }

        shopAdapter.setData(tBaoShopBean);
        shopAdapter.setRightView(iv_tao_live_list);
        rv_live_product.setAdapter(shopAdapter);
        rv_live_product.setCount(shopAdapter.getItemCount());
        rv_live_product.post(new Runnable() {
            @Override
            public void run() {
                oldFocusView = rv_live_product.getChildAt(1);
                if (oldFocusView != null)
                    iv_tao_live_list.setNextFocusLeftId(oldFocusView.getId());
            }
        });

        //自定义淘客打点
        onHandleAnaylisysTaoke(tBaoShopBean);
    }

    private List<CommentBase> commentList;
    private List<String> userIdList = new ArrayList<>();

    @Override
    public boolean handleMessage(Message msg) {
        Log.i(TAG, TAG + ".handleMessage what : " + msg.what);
        switch (msg.what) {
//            case Constant.SubType.textMsg:
//                if (commentList != null) {
//                    Bundle bundle = msg.getData();
//                    CommentBase commentBase = new CommentBase();
//                    commentBase.setColor(Tools.color[Tools.getRandomBySize(Tools.color.length)]);
//                    commentBase.setNick(bundle.getString("nick"));
//                    commentBase.setComment(bundle.getString("comment"));
//                    commentList.add(commentBase);
//                }
//                break;
            case PowerMsgType.LIVE_COMMENT_NOTIFY:
                if (commentList.size() > 0) {
                    synchronized (commentList) {
                        if (commentList.size() > 2) {
                            List<CommentBase> cl = commentList.subList(0, 2);
                            commentAdapter.addItem(cl);
                            commentList = commentList.subList(2, commentList.size());
                            ll_live_comment.setSelection(commentAdapter.getCount() - 1);
                        } else {
                            commentAdapter.addItem(commentList);
                            ll_live_comment.setSelection(commentAdapter.getCount() - 1);
                            commentList.clear();
                        }
                    }
                }
                mHandler.sendEmptyMessageDelayed(PowerMsgType.LIVE_COMMENT_NOTIFY, 1000);
                break;
            case PowerMsgType.LIVE_COMMENT_UP_NOTIFY:
                if (ll_live_toplayout.getVisibility() == View.VISIBLE)
                    AnimUtils.fadeOut(ll_live_toplayout);

                if (hasAttentionMsg) {
                    hasAttentionMsg = false;
                    ATTENTION_COUNT = 0;

                    showAttentionMsg(current_attention_nick);
                } else if (hasAddCartMsg) {
                    hasAddCartMsg = false;
                    ADD_ADDCART_COUNT = 0;

                    showAddCartMsg(Tools.fuzzyNick(current_addshop_nick));
                }
                mHandler.sendEmptyMessageDelayed(PowerMsgType.LIVE_COMMENT_UP_NOTIFY, 3000);
                break;
            case PowerMsgType.LIVE_CHANGE_ONLINE_HEAD_MSG:
                int userIdListSize = userIdList.size();
                for (int i = 0; i < userIdListSize; i++) {
                    if (i < 5) {
                        mImageLoaderManager.displayImage(Tools.getUserHead(userIdList.get(i), 60), online_head_pic_byte[i], roundImageOptions);
                    }
                }

                userIdList.clear();
                mHandler.sendEmptyMessageDelayed(PowerMsgType.LIVE_CHANGE_ONLINE_HEAD_MSG, 10 * 1000);
                break;
            case PowerMsgType.LIVE_STREAM_BREAK:
                //TODO 断流
                break;
            case PowerMsgType.LIVE_STREAM_RESTORE:
                initVideoView(liveUrl);
                break;
            case PowerMsgType.LIVE_STREAM_END:
                //TODO 直播结束
                videoError();
                break;
//            case PowerMsgType.LIVE_PRAISE_SEND:
//                if (praise_count_click > 0) {
//                    PowerMsgHelper.addFav(topic, praise_count_click);
//                    praise_count_click = 0.0;
//                }
//                mHandler.sendEmptyMessageDelayed(PowerMsgType.LIVE_PRAISE_SEND, 5000);
//                break;
            case PowerMsgType.LIVE_GIFT:
                //TODO 送礼物,本迭代不做
                break;
            case PowerMsgType.LIVE_ATTENTION:
                addTopItem(PowerMsgType.LIVE_ATTENTION, msg.obj.toString());
                break;
            case PowerMsgType.joinMsg:
                String onlineCount = msg.getData().getString("onlineCount");
                String userId = msg.getData().getString("joinUserId");
                userIdList.add(userId);
                long newJoinCount = Long.parseLong(onlineCount) - changeJoinCount;
                if (newJoinCount > 0 && joincount != null) {
                    totalJoinCount = totalJoinCount + newJoinCount;
                    //joincount.setText(totalJoinCount + " 观看");
                }
                changeJoinCount = Integer.parseInt(onlineCount);

                String join = msg.getData().getString("joinFrom") + "进入了直播间";
                break;
//            case Constant.SubType.dig:
//                if (Long.parseLong(msg.obj.toString()) > praise_count) {
//                    praise_count = Double.parseDouble(msg.obj.toString());
//                    if (tv_praise_count != null)
//                        tv_praise_count.setText(Tools.getNum(praise_count));
//                }
//                if (hl_praise != null)
//                    hl_praise.addHeart();
//                break;
            case PowerMsgType.tradeShowMsg:
                addTopItem(PowerMsgType.tradeShowMsg, msg.obj.toString());
                break;
            case PowerMsgType.LIVE_ERROR:
                break;
            case PowerMsgType.LIVE_LOADING_NOTIFY:
                break;
            case PowerMsgType.LIVE_BONUS:
                Bundle bundle = msg.getData();
                String asac = bundle.getString("asac");
                String ruleType = bundle.getString("ruleType");
                String timeType = bundle.getString("timeType");
                AppDebug.e(TAG, "asac = " + asac + "，ruleType= " + ruleType + "，timeType = " + timeType);
                if (CoreApplication.getLoginHelper(this).isLogin()) {
                    mBusinessRequest.getLiveBonusResult(liveId + timeType, ruleType, asac, new GetLiveBonusResultListener(new WeakReference<BaseActivity>(this)));
                }
                break;

        }
        return false;
    }

    private int ATTENTION_COUNT = 0;
    private int ADD_ADDCART_COUNT = 0;
    private boolean hasAttentionMsg = false;
    private boolean hasAddCartMsg = false;
    private String current_attention_nick = "";
    private String current_addshop_nick = "";

    public void addTopItem(int type, String str) {
        if (type == PowerMsgType.LIVE_ATTENTION) {
            ATTENTION_COUNT++;
            hasAttentionMsg = true;
            current_attention_nick = str;
        } else if (type == PowerMsgType.tradeShowMsg) {
            ADD_ADDCART_COUNT++;
            hasAddCartMsg = true;
            current_addshop_nick = str;
        }
    }

    private void showAddCartMsg(String nick) {
        if (ADD_ADDCART_COUNT > 1) {
            ll_live_topcomment.setText(nick + "等" + ADD_ADDCART_COUNT + "人正在去买");
        } else {
            ll_live_topcomment.setText(nick + "正在去买");
        }
        GradientDrawable background = (GradientDrawable) ll_live_toplayout.getBackground();
        ll_live_topicon.setImageDrawable(shopcart);
        background.setColor(0x99FF5500);

        AnimUtils.translateLeftIn(ll_live_toplayout, 1000);
    }

    private void showAttentionMsg(String nick) {
        if (ATTENTION_COUNT > 1) {
            ll_live_topcomment.setText(nick + "等" + ATTENTION_COUNT + "人关注了主播");
        } else {
            ll_live_topcomment.setText(nick + "关注了主播");
        }
        GradientDrawable background = (GradientDrawable) ll_live_toplayout.getBackground();

        ll_live_topicon.setImageDrawable(attention);
        background.setColor(0x99FF0055);

        AnimUtils.translateLeftIn(ll_live_toplayout, 1000);
    }

    private class GetTBaoLiveListListener extends BizRequestListener<List<TBaoLiveListBean>> {

        private GetTBaoLiveListListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(List<TBaoLiveListBean> data) {
            if (data != null && data.size() > 0) {
                requestData(data.get(0).getLiveId(), data.get(0).getAccountId());
                topic = data.get(0).getLiveId();
                liveTitle = data.get(0).getTitle();
                liveId = data.get(0).getLiveId();
                accountId = data.get(0).getAccountId();
                landScape = data.get(0).getLandScape();
                mLiveDataController.setTBaoItemsBean(data.get(0));
                mLiveDataController.setCurrentTBaoLivePos(0);
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    /**
     * 直播推荐列表data
     */
    private class GetLiveHotItemListlListener extends BizRequestListener<TBaoShopBean> {

        public GetLiveHotItemListlListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            initShopList(null);
            return false;
        }

        @Override
        public void onSuccess(TBaoShopBean data) {
            initShopList(data);
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    /**
     * 直播详情下载监听
     */
    private static class GetLiveDetailListener extends BizRequestListener<LiveDetailBean> {

        private WeakReference<BaseActivity> baseActivityWR;
        public GetLiveDetailListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
            this.baseActivityWR = baseActivityRef;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(LiveDetailBean data) {
            if (baseActivityWR != null && baseActivityWR.get() != null) {
                TBaoLiveActivity activity = (TBaoLiveActivity) baseActivityWR.get();
                activity.getDetailDate(data);
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    private class GetLiveBlackListener extends BizRequestListener<LiveBlackList> {

        public GetLiveBlackListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(LiveBlackList data) {
            int size = data.getTaobaoLiveAccountIdList().size();
            for (int i = 0; i < size; i++) {
                if (data.getTaobaoLiveAccountIdList().get(i).equals(accountId)) {
                    videoError();
                    return;
                }


            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    public class GetLiveBonusTimeListener extends BizRequestListener<LiveBonusTimeResult> {

        public GetLiveBonusTimeListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(LiveBonusTimeResult data) {
            long currentTime = Long.parseLong(data.getCurrentTime());
            List<LiveBonusTimeItem> liveBonusTimeItemList = data.getList();
            if (data != null && liveBonusTimeItemList.size() > 0) {
                for (int i = 0; i < liveBonusTimeItemList.size(); i++) {
                    LiveBonusTimeItem liveBonusTimeResult = liveBonusTimeItemList.get(i);
                    if (!TextUtils.isEmpty(liveBonusTimeResult.getDrawBeginAt())) {
                        long drawBeginAt = Long.parseLong(liveBonusTimeResult.getDrawBeginAt());
                        long diffTime = drawBeginAt - currentTime;
                        if (drawBeginAt > currentTime) {
                            if (videoError){
                                // 如果视频已经出错，不再直播权益消息
                                videoError = false;
                                return;
                            }
                            Message message = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("asac", liveBonusTimeResult.getSafeCode());
                            bundle.putString("ruleType", liveBonusTimeResult.getRuleType());
                            bundle.putString("timeType", liveBonusTimeResult.getTimeType());
                            message.what = PowerMsgType.LIVE_BONUS;
                            message.setData(bundle);
                            mHandler.sendMessageDelayed(message, diffTime);

                        }
                    }
                }
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }


    public class GetLiveBonusResultListener extends BizRequestListener<LiveBonusResult> {

        public GetLiveBonusResultListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            if (resultCode == 106) {
                return true;
            }
            return false;
        }

        @Override
        public void onSuccess(LiveBonusResult data) {
            if (data != null && data.getType() != null) {
                String type = data.getType();
                //红包
                if (type.equals("coupon")) {
                    String amount = Utils.getRebateCoupon(data.getAmount()+"");
                    if(amount!=null) {
                        showBonusAnimation(0, amount + "元");
                    }
                } else if (type.equals("promotion")) {//店铺优惠券
                    String amount = Utils.getRebateCoupon(data.getAmount()+"");
                    if (amount!=null) {
                        showBonusAnimation(4, amount + "元");
                    }
                } else {
                    showBonusAnimation(10, "");


                }

            }


        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }


    public class GetLiveFollowResultListener extends BizRequestListener<LiveFollowResult> {


        public GetLiveFollowResultListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(LiveFollowResult data) {
            if (data != null && !TextUtils.isEmpty(data.getFollowAccount())) {
                if (data.getFollowAccount().equals("true")) {
                    isFollow = true;
                    if(tvMessageToast!=null && tvMessageToast.getVisibility() == View.VISIBLE){
                        tvMessageToast.setVisibility(View.GONE);
                    }
                    if (iv_care.hasFocus()) {
                        iv_care.setImageResource(R.drawable.ytm_tbao_live_followed_focused);
                        careFocusTipView.show(headImg, accountNick, ((fansNum + 1 >= 0) ? (fansNum + 1) : (0))
                                , CoreApplication.getLoginHelper(TBaoLiveActivity.this).isLogin()
                                , isFollow, iv_care);
                        careFocusTipView.playCareSuccessAnim(iv_care);
                    } else {
                        iv_care.setImageResource(R.drawable.ytm_tbao_live_followed_unfocuse);
                    }
                }
            }

        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    public class GetLiveCancelFollowResultListener extends BizRequestListener<String> {

        public GetLiveCancelFollowResultListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(String data) {
            isFollow = false;
            iv_care.setImageResource(R.drawable.ytm_tbao_live_unfollow_focused);
            if (iv_care.hasFocus()) {
                iv_care.setImageResource(R.drawable.ytm_tbao_live_unfollow_focused);
                careFocusTipView.show(headImg, accountNick, ((fansNum - 1 >= 0) ? (fansNum - 1) : (0))
                        , CoreApplication.getLoginHelper(TBaoLiveActivity.this).isLogin()
                        , isFollow, iv_care);
            } else {
                iv_care.setImageResource(R.drawable.ytm_tbao_live_unfollow_unfocuse);
            }


        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }


    public class GetLiveIsFollowStatusListener extends BizRequestListener<LiveIsFollowStatus> {


        public GetLiveIsFollowStatusListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            iv_care.setVisibility(View.VISIBLE);
            iv_care.setImageResource(R.drawable.ytm_tbao_live_unfollow_unfocuse);
            return false;
        }

        @Override
        public void onSuccess(LiveIsFollowStatus data) {
            if (data != null && !TextUtils.isEmpty(data.getFollow()) && data.getFollow().equals("true")) {
                isFollow = true;
                if(iv_care.isFocused()){
                    iv_care.setImageResource(R.drawable.ytm_tbao_live_followed_focused);
                }else {
                    iv_care.setImageResource(R.drawable.ytm_tbao_live_followed_unfocuse);
                }
            } else {
                isFollow = false;
                if(iv_care.isFocused()){
                    iv_care.setImageResource(R.drawable.ytm_tbao_live_unfollow_focused);
                }else {
                    iv_care.setImageResource(R.drawable.ytm_tbao_live_unfollow_unfocuse);
                }
            }
            iv_care.setVisibility(View.VISIBLE);

        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }


    /**
     * 下载右侧聊天背景图(毛玻璃效果)
     *
     * @param url
     */
    public void setGuess(String url) {
        url = Tools.getTrueImageUrl(url);
        mImageLoaderManager.loadImage(url, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                //TODO 图片加载失败
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                SnapshotUtil.fastBlur(loadedImage, 5, new SnapshotUtil.OnFronstedGlassSreenDoneListener() {
                    @Override
                    public void onFronstedGlassSreenDone(final Bitmap bitmap) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (iv_back_ground != null) {
                                    if (bitmap != null && (!bitmap.isRecycled())) {
                                        Drawable[] array = new Drawable[2];
                                        array[0] = new BitmapDrawable(bitmap);
                                        array[1] = new ColorDrawable(getResources().getColor(com.yunos.tvtaobao.businessview.R.color.ytbv_shadow_color_50));
                                        LayerDrawable la = new LayerDrawable(array);
                                        iv_back_ground.setImageDrawable(la);
                                    }
                                }
                            }
                        });
                    }
                });
                /*SnapshotUtil.getFronstedBitmap(loadedImage, 5, new SnapshotUtil.OnFronstedGlassSreenDoneListener() {
                    @Override
                    public void onFronstedGlassSreenDone(final Bitmap bitmap) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (iv_back_ground != null) {
                                    if (bitmap != null && (!bitmap.isRecycled())) {
                                        Drawable[] array = new Drawable[2];
                                        array[0] = new BitmapDrawable(bitmap);
                                        array[1] = new ColorDrawable(getResources().getColor(com.yunos.tvtaobao.businessview.R.color.ytbv_shadow_color_50));
                                        LayerDrawable la = new LayerDrawable(array);
                                        iv_back_ground.setImageDrawable(la);
                                    }
                                }
                            }
                        });
                    }
                });*/

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
            }
        });
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                if (rl_qr.getVisibility() == View.VISIBLE) {
                    AnimUtils.fadeOut(rl_qr, 300);
                    iv_care.setImageResource(R.drawable.ytm_tbao_live_qrcode_btn_bg);
                    return true;
                }
            }

            if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
                if (liveListDialog == null)
                    liveListDialog = LiveListDialog.getInstance(this);
                liveListDialog.show(getFocusView());
                setFocusableF();

                Utils.utControlHit("telecast_detail_menu", Utils.getProperties());
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    //视频出错处理
    boolean videoError = false;
    private void videoError() {
        vv_live.stop();
        if ("true".equals(landScape) || "CUSTOM".equals(source)) {
            //横屏
            if ("CUSTOM".equals(source)) {
                //全屏双11晚会
                liveFloatLayout.setVisibility(View.GONE);
                tbao_custom_live_info_prompt.setVisibility(View.VISIBLE);
            } else {
                liveFloatLayout.setVisibility(View.VISIBLE);
                tbao_live_info_prompt.setVisibility(View.VISIBLE);
                //设置右侧背景透明
                iv_back_ground.setImageDrawable(getResources().getDrawable(R.drawable.ytm_live_background));
            }
        } else if ("false".equals(landScape)) {
            liveFloatLayout.setVisibility(View.VISIBLE);
            tbao_live_info_prompt.setVisibility(View.VISIBLE);
        }
        tbao_live_loading_img.setVisibility(View.GONE);
        videoError = true;
        removeNotify();
    }

    private void loadingState() {
        tbao_live_info_prompt.setVisibility(View.GONE);
        tbao_custom_live_info_prompt.setVisibility(View.GONE);
        tbao_live_loading_img.setVisibility(View.VISIBLE);
        vv_live.setVisibility(View.INVISIBLE);
    }

    /**
     * 切换视频
     *
     * @param data
     */
    public void changeLive(int currentPos, TBaoLiveListBean data) {
        removeNotify();
        loadingState();
        //unSubscribeTopic(topic);
        clearData();
        requestData(data.getLiveId(), data.getAccountId());
        //initVideoView(liveUrl);
        initComment();
        toLight();
        //startLoopNotify();
        topic = data.getLiveId();
        liveTitle = data.getTitle();
        liveId = data.getLiveId();
        accountId = data.getAccountId();
        landScape = data.getLandScape();
        //subscribeTopic(topic);
//        focusPositionManager.requestFocus(iv_praise, 0);
        LiveListDialog.getInstance(this).setFocusView(iv_praise);
        mLiveDataController.setTBaoItemsBean(data);
        mLiveDataController.setCurrentTBaoLivePos(currentPos);
    }

    public void requestFocus(View view) {
        if(view instanceof FocusListener){
            focusPositionManager.requestFocus(view, 0);
        }
    }

    private void setFocusableF() {
        if (iv_praise != null)
            iv_praise.setFocusable(false);
        if (iv_tao_live_list != null)
            iv_tao_live_list.setFocusable(false);
        if (iv_care != null)
            iv_care.setFocusable(false);
        if (iv_light != null)
            iv_light.setFocusable(false);
    }

    public void setFocusableT() {
        if (iv_praise != null)
            iv_praise.setFocusable(true);
        if (iv_tao_live_list != null)
            iv_tao_live_list.setFocusable(true);
        if (iv_care != null)
            iv_care.setFocusable(true);
        if (iv_light != null)
            iv_light.setFocusable(true);
    }

    /**
     * 注册powerMsg
     *
     * @param topic
     */
    private int bizCode = 1;


//    private IPowerMsgCallback powerMsgCallback = new IPowerMsgCallback() {
//        @Override
//        public void onResult(int i, Map<String, Object> map, Object... objects) {
//
//        }
//    };

    private void subscribeTopic(final String topic) {
        //TODO 原本接入推送
//        PowerMsgHelper.getInstance().registerBizCode(bizCode);
//        PowerMsgHelper.getInstance().setHandler(mHandler);
//        PowerMsgService.setMsgFetchMode(bizCode, topic, Constant.MsgFetchMode.PUSH_AND_PULL);
//        PowerMsgService.subscribe(bizCode, topic, User.getNick(), powerMsgCallback);

        TvTaobaoMsgService.registerTVLive(TBaoLiveActivity.this, TvTaobaoMsgService.TYPE_TAOBAO_LIVE, topic, new ITVTaoDiapatcher() {
            @Override
            public void Diapatcher(TVTaoMessage taoMessage) {
                AppDebug.e(TAG, "type : " + taoMessage + " , topic : " + taoMessage.topic);
                if (taoMessage.topic.equals(topic)) {
                    videoError();
                }
            }

            @Override
            public void Error(Map<String, String> map, int Error) {
                AppDebug.e(TAG, "map : " + map + " ,Error : " + Error);
            }
        });
    }

    private View getFocusView() {
        View rootview = TBaoLiveActivity.this.getWindow().getDecorView();
        return rootview.findFocus();
    }

    /**
     * 取消订阅
     *
     * @param topic
     */
    private void unSubscribeTopic(String topic) {
        //TODO 原本接入推送
//        PowerMsgService.unSubscribe(bizCode, topic, User.getNick(), powerMsgCallback);
//
        TvTaobaoMsgService.unRegisterTVLive(TBaoLiveActivity.this, TvTaobaoMsgService.TYPE_TAOBAO_LIVE, topic);
    }

    /**
     * 加载视频
     *
     * @param hlsUrl
     */
    private void initVideoView(String hlsUrl) {
        Log.e(TAG, TAG + ".initVideoView hlsUrl:" + hlsUrl);
        if ("0".equals(status)) {
            tbao_live_info_prompt.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(hlsUrl)) {
                vv_live.setVideoPath(hlsUrl);
            }

            if (vv_live != null) {
                vv_live.start();
            }
        } else {
            videoError();
        }

    }

    /**
     * 自定义淘客详情页打点
     */
    private void onHandleAnaylisysTaoke(TBaoShopBean tBaoShopBean) {

        if (LoginHelperImpl.getJuLoginHelper().isLogin()) {
            List<TBaoShopBean.ItemListBean> itemList = tBaoShopBean.getItemList();
            StringBuilder itemIds = new StringBuilder();

            for (int i = 0; i < itemList.size(); i++) {
                int goodsListSize = itemList.get(i).getGoodsList().size();
                for (int j = 0; j < goodsListSize; j++) {
                    TBaoShopBean.ItemListBean.GoodsListBean productBean = itemList.get(i).getGoodsList().get(j);
                    itemIds.append(productBean.getItemId()).append(",");
                }
            }
            String sellerIds = "null";
            String shopTypes = "null";
            String stbId = DeviceUtil.initMacAddress(CoreApplication.getApplication());
            AppDebug.d(TAG, "isLogin " + "itemIds " + itemIds + " shopTypes " + shopTypes + " sellerIds " + sellerIds);
            BusinessRequest.getBusinessRequest().requestTaokeDetailAnalysis(stbId, User.getNick(), itemIds.toString(), shopTypes, sellerIds, null);
        }
    }

    @Override
    public String getPageName() {
        return "Page_OnAir";
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> properties = Utils.getProperties();
        properties.put("live_name", liveTitle);
        properties.put("live_id", liveId);
        properties.put("is_login", User.isLogined() ? "1" : "0");
        return properties;
    }
}
