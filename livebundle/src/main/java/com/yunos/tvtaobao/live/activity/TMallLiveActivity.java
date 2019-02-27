package com.yunos.tvtaobao.live.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.media.ijkmediaplayer.IjkVideoView;
import com.taobao.wireless.security.sdk.securitybody.ISecurityBodyComponent;
import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvtaobao.voicesdk.utils.QRCodeUtil;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.pay.LoginHelperImpl;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.request.bo.TMallLiveBean;
import com.yunos.tvtaobao.biz.request.bo.TMallLiveCommentBean;
import com.yunos.tvtaobao.biz.request.bo.TMallLiveDetailBean;
import com.yunos.tvtaobao.biz.request.bo.TMallLiveShopList;
import com.yunos.tvtaobao.biz.request.info.GlobalConfigInfo;
import com.yunos.tvtaobao.live.R;
import com.yunos.tvtaobao.live.adapter.LiveCommectAdapter;
import com.yunos.tvtaobao.live.adapter.TMallShopAdapter;
import com.yunos.tvtaobao.live.data.CommentBase;
import com.yunos.tvtaobao.live.request.GetTMallCommentRequest;
import com.yunos.tvtaobao.live.request.GetTMallDetailRequest;
import com.yunos.tvtaobao.live.request.GetTMallShopRequest;
import com.yunos.tvtaobao.live.request.SendCommentRequest;
import com.yunos.tvtaobao.live.request.TMallLiveListRequest;
import com.yunos.tvtaobao.live.utils.AnimUtils;
import com.yunos.tvtaobao.live.utils.Tools;
import com.yunos.tvtaobao.live.view.Displayer;
import com.yunos.tvtaobao.live.view.LinearLayoutManagerTV;
import com.yunos.tvtaobao.live.view.LiveListDialog;
import com.yunos.tvtaobao.live.view.TimerToast;
import com.yunos.tvtaobao.live.view.ZPListView;
import com.yunos.tvtaobao.live.view.heart.TMallPeriscopeLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by pan on 16/10/8.
 */

public class TMallLiveActivity extends BaseActivity implements View.OnClickListener, Handler.Callback {
    private RelativeLayout parent_layout;
    private LinearLayout ll_first_module;
    private IjkVideoView mVideoView;
    private RelativeLayout tmall_live_loading_img;
    private LinearLayoutManagerTV linearLayoutManager;
    private TextView tmall_live_loading_txt;
    //天猫商品推荐,视频列表
    private RelativeLayout tmall_live_shop_back_prompt;
    private ZPListView rv_live_listview;
    private TMallShopAdapter mTMallShopAdapter;
    private TextView tv_live_title;
    private ImageView tmall_live_unshop_prompt, tmall_live_shop_log;
    //评论
    private Handler myHandler = new Handler();
    private RelativeLayout voice_layout;
    private ListView tmall_live_tall;
    private LiveCommectAdapter mTMallCommentAdapter;
    private ImageView voice_chat_prompt, iv_tvmall_micro;
    private LinearLayout ll_tvmall_comment;
    private TextView tv_tvmall_comment, tv_tvmall_comment_success;
    //直播详情
    private TextView tmall_live_title, tmall_live_home_num;
    private ImageView iv_praise, iv_shop, iv_login, iv_light, iv_live_list, tmall_live_state_img;
    //code
    private ImageView tv_tmall_live_code, tv_tmall_live_userhead;
    private RelativeLayout tv_tmall_live_code_layout;
    private TextView tv_tmall_live_username, tv_tmall_live_address;

    private TMallPeriscopeLayout hl_praise;

    public static final int TMALL_COMMENT_QUERRY_REQUEST = 0;
    public static final int TMALL_FULLSCREEN_PRAISE = 1;
    public static final int TMALL_REFRESH_COMMENT = 2;
    public static final int TMALL_ERROR_NOTIFY_CODE = 4;
    private int point_of_praise = 0; //进入全屏时点赞次数
    private static boolean isLight = true;
    private boolean isFirstLoad = true;

    private ImageLoaderManager imageLoaderManager;
    private BusinessRequest mBusinessRequest;

    private String liveUrl, cid, liveTitle;
    private int currentTMallLivePos = 0;
    private TMallLiveDetailBean mTMallLiveDetailBean;
    private List<String> commentIdList;
    private List<CommentBase> commentList;
    private Handler mHandler = new Handler(this);
    private ISecurityBodyComponent securityBodyComponent;
    private List<TMallLiveBean> mTMallLiveListBean = null;
    private boolean UpAndDownKey = true;
    private TimerToast toast;
    private BitmapDrawable loadingDR;
    private boolean isFirstLoading = true;
    private LiveListDialog liveListDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onKeepActivityOnlyOne(TMallLiveActivity.class.getName());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        UpAndDownKey = SharePreferences.getBoolean("UpAndDownKey", true);


//        securityBodyComponent = (ISecurityBodyComponent) SecurityGuardManager.getInstance(new ContextWrapper(this)).getSecurityBodyComp();
//        securityBodyComponent.initSecurityBody(Config.getAppKey());
        setContentView(R.layout.ytm_v_mall_live_window);
        mTMallShopAdapter = new TMallShopAdapter(this);
        mTMallCommentAdapter = new LiveCommectAdapter(this);
        initView();

        liveUrl = getIntent().getStringExtra("liveUrl");
        cid = getIntent().getStringExtra("liveId");
        mBusinessRequest = BusinessRequest.getBusinessRequest();
        imageLoaderManager = ImageLoaderManager.getImageLoaderManager(TMallLiveActivity.this);

        requestLiveDetail(cid);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null && !mVideoView.isPlaying() && liveUrl != null) {
            initVideoView(liveUrl);
        }
        requestCommentQuery(cid);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //aliTVASRManager.release();
        if (mVideoView != null)
//            mVideoView.stopPlayback();//prev
            mVideoView.pause();
    }

    private void clearData() {
        commentIdList.clear();
        commentList.clear();

        if (mTMallCommentAdapter != null)
            mTMallCommentAdapter.clearData();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (mVideoView != null){
            mVideoView.switchOff();
            mVideoView.release();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onRemoveKeepedActivity(TMallLiveActivity.class.getName());
        clearData();
        clearViews();
    }

    private void clearViews() {
        if (liveListDialog != null)
            liveListDialog.destory();

        iv_login.setOnFocusChangeListener(null);

        iv_live_list.setOnFocusChangeListener(null);

        iv_praise.setOnFocusChangeListener(null);

        iv_shop.setOnFocusChangeListener(null);

        iv_light.setOnFocusChangeListener(null);

//        mVideoView.setOnErrorListener(null);
//
//        mVideoView.setOnPreparedListener(null);
//
//        mVideoView.setOnInfoListener(null);
        iv_praise.setOnClickListener(null);
        iv_shop.setOnClickListener(null);
        iv_login.setOnClickListener(null);
        iv_light.setOnClickListener(null);
        iv_live_list.setOnClickListener(null);
        if (toast != null)
            toast.hide();
        toast = null;
        mVideoView = null;
        iv_praise = null;
        iv_shop = null;
        iv_login = null;
        iv_light = null;
        hl_praise = null;
        iv_live_list = null;
        tmall_live_state_img = null;
        tmall_live_title = null;
        tmall_live_home_num = null;
        ll_first_module = null;
        rv_live_listview = null;
        tmall_live_shop_back_prompt = null;
        tmall_live_shop_log = null;
        tmall_live_tall = null;
        voice_layout = null;
        voice_chat_prompt = null;
        ll_tvmall_comment = null;
        tv_tvmall_comment = null;
        tv_tvmall_comment_success = null;

        tmall_live_unshop_prompt = null;
        tv_tmall_live_code_layout = null;
        tv_tmall_live_code = null;
        iv_tvmall_micro = null;
        tv_live_title = null;
        tv_tmall_live_userhead = null;
        tv_tmall_live_username = null;
        tv_tmall_live_address = null;
        loadingDR = null;
        tmall_live_loading_img = null;
        tmall_live_loading_txt = null;

        parent_layout.removeAllViews();
        parent_layout = null;


        mVideoView = null;
        if (mTMallLiveDetailBean != null)
            mTMallLiveDetailBean.clear();
        mTMallLiveDetailBean = null;
    }

    private void initView() {
        parent_layout = (RelativeLayout) findViewById(R.id.parent_layout);
        mVideoView = (IjkVideoView) findViewById(R.id.vv_live);
        iv_praise = (ImageView) findViewById(R.id.iv_praise);
        iv_shop = (ImageView) findViewById(R.id.iv_shop);
        iv_login = (ImageView) findViewById(R.id.iv_login);
        iv_light = (ImageView) findViewById(R.id.iv_light);
        hl_praise = (TMallPeriscopeLayout) findViewById(R.id.hl_praise);
        iv_live_list = (ImageView) findViewById(R.id.iv_live_list);
        tmall_live_state_img = (ImageView) findViewById(R.id.tmall_live_state_img);
        tmall_live_title = (TextView) findViewById(R.id.tmall_live_title);
        tmall_live_home_num = (TextView) findViewById(R.id.tmall_live_home_num);
        ll_first_module = (LinearLayout) findViewById(R.id.ll_first_module);
        rv_live_listview = (ZPListView) findViewById(R.id.rv_live_listview);
        linearLayoutManager = new LinearLayoutManagerTV(this);
        linearLayoutManager.setBottomPadding(Tools.compatiblePx(this, 260));
        linearLayoutManager.setTopPadding(Tools.compatiblePx(this, 260));
        rv_live_listview.setLayoutManager(linearLayoutManager);
        tmall_live_shop_back_prompt = (RelativeLayout) findViewById(R.id.tmall_live_shop_back_prompt);
        tmall_live_shop_log = (ImageView) findViewById(R.id.tmall_live_shop_log);
        tmall_live_tall = (ListView) findViewById(R.id.tmall_live_tall);
        voice_layout = (RelativeLayout) findViewById(R.id.voice_layout);
        voice_chat_prompt = (ImageView) findViewById(R.id.voice_chat_prompt);
        ll_tvmall_comment = (LinearLayout) findViewById(R.id.ll_tvmall_comment);
        tv_tvmall_comment = (TextView) findViewById(R.id.tv_tvmall_comment);
        tv_tvmall_comment_success = (TextView) findViewById(R.id.tv_tvmall_comment_success);
        if ((Config.getChannel().equals("701229") || Config.getChannel().equals("10003226") || Config.getChannel().equals("10004416")) && DeviceUtil.getYuyinPackageCode(this) >= 2100300000)
            voice_layout.setVisibility(View.VISIBLE);

        tmall_live_unshop_prompt = (ImageView) findViewById(R.id.tmall_live_unshop_prompt);
        tv_tmall_live_code_layout = (RelativeLayout) findViewById(R.id.tv_tmall_live_code_layout);
        tv_tmall_live_code = (ImageView) findViewById(R.id.tv_tmall_live_code);
        iv_tvmall_micro = (ImageView) findViewById(R.id.iv_tvmall_micro);
        tv_live_title = (TextView) findViewById(R.id.tv_live_title);
        tv_tmall_live_userhead = (ImageView) findViewById(R.id.tv_tmall_live_userhead);
        tv_tmall_live_username = (TextView) findViewById(R.id.tv_tmall_live_username);
        tv_tmall_live_address = (TextView) findViewById(R.id.tv_tmall_live_address);
        tmall_live_loading_img = (RelativeLayout) findViewById(R.id.tmall_live_loading_img);
        tmall_live_loading_txt = (TextView) findViewById(R.id.tmall_live_loading_txt);
        loadingDR = new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.tmall_live_loading_img));
        tmall_live_loading_img.setBackgroundDrawable(loadingDR);

        iv_praise.setOnClickListener(this);
        iv_shop.setOnClickListener(this);
        iv_login.setOnClickListener(this);
        iv_light.setOnClickListener(this);
        iv_live_list.setOnClickListener(this);

        onFocusListener();

        tmall_live_state_img.setImageResource(R.drawable.live_state_playing);
//        tmall_live_state_img.setImageResource(R.drawable.live_state_review);

        initCommentView();
        startLoopNotify();
        videoStateListener();
    }

    private void startLoopNotify() {
        mHandler.sendEmptyMessageDelayed(TMALL_REFRESH_COMMENT, 1000);
    }

    /**
     * 视频状态监听
     */
    private void videoStateListener() {
        mVideoView.setOnErrorListener(new tv.danmaku.ijk.media.player.IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(tv.danmaku.ijk.media.player.IMediaPlayer iMediaPlayer, int i, int i1) {
                AppDebug.e("TMallLiveBean", " onErrorListener  " + iMediaPlayer);
                long eTime = 0;
                if (mTMallLiveDetailBean != null && mTMallLiveDetailBean.getChannel() != null && mTMallLiveDetailBean.getChannel().getEtime() != null)
                    eTime = Long.parseLong(Tools.getTime(mTMallLiveDetailBean.getChannel().getEtime())) * 1000;
                //结束时间大于现在的时间,还没有播放结束
                if (eTime != 0 && eTime > System.currentTimeMillis() && isFirstLoad) {
                    mHandler.sendEmptyMessageDelayed(TMALL_ERROR_NOTIFY_CODE, 20 * 1000);
                    isFirstLoad = false;

                    tmall_live_loading_img.setVisibility(View.VISIBLE);
                    tmall_live_loading_img.setBackgroundColor(0x99000000);
                    tmall_live_loading_txt.setVisibility(View.VISIBLE);
                    tmall_live_loading_txt.setText("主播在赶来的路上了～");

                } else {
                    mBusinessRequest.baseRequest(new TMallLiveListRequest(), new GetTMallLiveListListener(new WeakReference<BaseActivity>(TMallLiveActivity.this)), false);
                }
                return false;
            }
        });

        mVideoView.setOnPreparedListener(new tv.danmaku.ijk.media.player.IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(tv.danmaku.ijk.media.player.IMediaPlayer iMediaPlayer) {
                isFirstLoad = true;

                AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
                alphaAnimation.setDuration(400);
                tmall_live_loading_img.startAnimation(alphaAnimation);
                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        tmall_live_loading_txt.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        tmall_live_loading_img.setVisibility(View.GONE);
                        loadingType = 0;
                        mHandler.sendEmptyMessage(TMALL_FULLSCREEN_PRAISE);
                        point_of_praise = 0;
                        if (UpAndDownKey && isFirstLoading) {
                            if (toast == null)
                                toast = TimerToast.makeText(TMallLiveActivity.this, "[上下键]可切换视频", 5 * 1000);
                            toast.show();

                            isFirstLoading = false;
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });

        mVideoView.setOnInfoListener(new tv.danmaku.ijk.media.player.IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(tv.danmaku.ijk.media.player.IMediaPlayer iMediaPlayer, int what, int i1) {
                boolean handled = false;
                switch (what) {
                    case IjkMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        AppDebug.d(TAG, "onInfo MediaPlayer.MEDIA_INFO_BUFFERING_START");
                        handled = true;
                        break;
                    case IjkMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        AppDebug.d(TAG, "onInfo MediaPlayer.MEDIA_INFO_BUFFERING_END");
                        handled = true;
                        break;
                }
                return handled;
            }
        });

    }

    /**
     * 加载视频
     *
     * @param hlsUrl
     */
    private void initVideoView(String hlsUrl) {
        liveUrl = hlsUrl;
        if (hlsUrl != null)
            mVideoView.setVideoURI(Uri.parse(hlsUrl));

        mVideoView.start();
    }

    /**
     * 插入评论视图
     */
    private void initCommentView() {
//        tmall_live_tall.doTopGradualEffect();
        tmall_live_tall.setStackFromBottom(true);
        tmall_live_tall.setFocusable(false);
        commentIdList = new ArrayList<>();
        commentList = new ArrayList<>();
        tmall_live_tall.setAdapter(mTMallCommentAdapter);
    }

    private void onFocusListener() {
        final Map<String, String> properties = Utils.getProperties();
        properties.put("live_name", liveTitle);
        properties.put("live_id", cid);
        iv_login.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AppDebug.w(TAG, "iv_login onFocusChange hasFocus " + hasFocus);
                iv_login.setImageResource(R.drawable.ytm_qrcode_focused);
                if (!hasFocus) {
                    if (tv_tmall_live_code_layout.getVisibility() == View.VISIBLE)
                        closeQRCode();
                } else {
                    properties.put("controlname", "code");
                    Utils.utCustomHit("Page_Tmlive", properties);
                }

            }
        });

        iv_live_list.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    properties.put("controlname", "change");
                    Utils.utCustomHit("Page_Tmlive", properties);
                }

            }
        });

        iv_praise.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    properties.put("controlname", "like");
                    Utils.utCustomHit("Page_Tmlive", properties);
                }
            }
        });

        iv_shop.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    properties.put("controlname", "commodity");
                    Utils.utCustomHit("Page_Tmlive", properties);
                }
            }
        });

        iv_light.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    properties.put("controlname", "switch");
                    Utils.utCustomHit("Page_Tmlive", properties);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        Map<String, String> properties = Utils.getProperties();
        properties.put("live_name", liveTitle);
        properties.put("live_id", cid);
        int i = v.getId();
        if (i == R.id.iv_live_list) {
            if (liveListDialog == null)
                liveListDialog = LiveListDialog.getInstance(this);
            liveListDialog.show(iv_live_list);
            clearFocus();
            properties.put("controlname", "change");
            Utils.utControlHit("telecast_detail_button_change", properties);

        } else if (i == R.id.iv_praise) {
            hl_praise.addHeart();
            properties.put("controlname", "like");
            Utils.utControlHit("telecast_detail_button_like", properties);

        } else if (i == R.id.iv_shop) {
            if (ll_first_module.getVisibility() == View.GONE) {
                clearFocus();
                translateIn();
            }
            properties.put("controlname", "commodity");
            Utils.utControlHit("telecast_detail_button_commodity", properties);

        } else if (i == R.id.iv_login) {
            if (mTMallLiveDetailBean == null)
                return;

            iv_login.setImageResource(R.drawable.live_qrcode_open);
            if (tv_tmall_live_code_layout.getVisibility() == View.INVISIBLE) {
                AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
                alphaAnimation.setDuration(400);
                tv_tmall_live_code_layout.startAnimation(alphaAnimation);
                alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        tv_tmall_live_code_layout.setVisibility(View.VISIBLE);

                        tv_tmall_live_username.setText(mTMallLiveDetailBean.getChannel().getOwnerNick());
                        tv_tmall_live_address.setText(mTMallLiveDetailBean.getChannel().getAddress());
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        createQR(mTMallLiveDetailBean.getChannel().getShareUrl());
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            } else {
                closeQRCode();
                iv_login.setImageResource(R.drawable.ytm_qrcode_focused);
            }
            properties.put("controlname", "qrcode");
            Utils.utControlHit("telecast_detail_button_qrcode", properties);

        } else if (i == R.id.iv_light) {
            if (isLight) {
                iv_light.setImageResource(R.drawable.ytm_light_close_comment);

                AnimUtils.fadeOut(tmall_live_tall, 300);
                AnimUtils.fadeOut(tmall_live_state_img, 300);
                AnimUtils.fadeOut(tmall_live_title, 300);
                AnimUtils.fadeOut(tmall_live_home_num, 300);
                if (voice_layout.getVisibility() == View.VISIBLE)
                    AnimUtils.fadeOut(voice_layout, 300);

                isLight = false;
            } else {
                iv_light.setImageResource(R.drawable.ytm_light_fouced);

                AnimUtils.fadeIn(tmall_live_state_img, 300);
                AnimUtils.fadeIn(tmall_live_title, 300);
                AnimUtils.fadeIn(tmall_live_home_num, 300);
                AnimUtils.fadeIn(tmall_live_tall, 300);
                if ((Config.getChannel().equals("701229") || Config.getChannel().equals("10003226") || Config.getChannel().equals("10004416")) && DeviceUtil.getYuyinPackageCode(this) >= 2100300000)
                    AnimUtils.fadeIn(voice_layout, 300);

                isLight = true;
            }
            properties.put("controlname", "switch");
            Utils.utControlHit("telecast_detail_button_switch", properties);

        }
    }

    /**
     * 请求直播详情
     */
    private void requestLiveDetail(String liveId) {
        mBusinessRequest.baseRequest(new GetTMallDetailRequest(liveId), new GetTMallDetailListener(new WeakReference<BaseActivity>(this)), false);
    }

    /**
     * 淡出消失动画
     */
    private void closeQRCode() {
        AnimUtils.fadeOut(tv_tmall_live_code_layout, 400);
    }

    /**
     * 请求评论
     */
    private void requestCommentQuery(String sourceId) {
        mBusinessRequest.baseRequest(new GetTMallCommentRequest("tlive", sourceId, 2, -1, "0", "0", 30, false), new GetTMallCommentListener(new WeakReference<BaseActivity>(this)), false);
    }

    /**
     * 发送评论
     *
     * @param text
     */
    private void sendComment(String text) {
        GlobalConfig gc = GlobalConfigInfo.getInstance().getGlobalConfig();
        if (gc != null && gc.getTMallLive() != null && gc.getTMallLive().postfix() != null) {
            text += gc.getTMallLive().postfix();
        }

        if (!text.equals("") || text != null) {
            sendCommentStyle(text);

            mBusinessRequest.baseRequest(new SendCommentRequest(cid, "0", text, "hot", "0", "0"), new SendCommentListener(new WeakReference<BaseActivity>(this)), true);
        }
    }

    /**
     * 插入商品
     *
     * @param itemList
     */
    private void initShopRecylerView(List<TMallLiveShopList.ModelBean.DataBean> itemList) {
        if (itemList.size() == 0) {
            tmall_live_unshop_prompt.setVisibility(View.VISIBLE);
            tmall_live_unshop_prompt.setImageResource(R.drawable.tmall_live_unproduct_prompt);
            rv_live_listview.requestFocus();
            return;
        } else {
            tmall_live_unshop_prompt.setVisibility(View.GONE);
        }

        rv_live_listview.setCount(itemList.size());
        mTMallShopAdapter.setData(itemList);
        rv_live_listview.setAdapter(mTMallShopAdapter);

        rv_live_listview.setFocusable(false);

        rv_live_listview.post(new Runnable() {
            @Override
            public void run() {
                if (rv_live_listview.getChildCount() > 0) {
                    rv_live_listview.getChildAt(0).requestFocus();
                }
            }
        });
        //淘客打点
        onHandleAnaylisysTaoke(itemList);
    }

    /**
     * 生成二维码
     *
     * @param accountUrl
     */
    private void createQR(String accountUrl) {
        Bitmap qrBitmap = null;
        try {
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_tmall_app);
            ViewGroup.LayoutParams para = tv_tmall_live_code.getLayoutParams();
            qrBitmap = QRCodeUtil.create2DCode(accountUrl, para.width, para.height, icon);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        Drawable newBitmapDrawable = new BitmapDrawable(qrBitmap);

        tv_tmall_live_code.setImageDrawable(newBitmapDrawable);

        DisplayImageOptions options = new DisplayImageOptions.Builder().displayer(new Displayer(0)).build();
        imageLoaderManager.displayImage(Tools.getTrueImageUrl(mTMallLiveDetailBean.getAnchormen().get(0).getAvatar()), tv_tmall_live_userhead, options);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case TMALL_COMMENT_QUERRY_REQUEST:
                requestCommentQuery(cid);
                break;
            case TMALL_FULLSCREEN_PRAISE:
                if (point_of_praise < 10 && hl_praise != null && mHandler != null) {
                    hl_praise.addHeart();
                    mHandler.sendEmptyMessageDelayed(TMALL_FULLSCREEN_PRAISE, 200);
                    point_of_praise++;
                }
                break;
            case TMALL_ERROR_NOTIFY_CODE:
                initVideoView(liveUrl);
                break;
            case TMALL_REFRESH_COMMENT:
                if (commentList.size() != 0) {
                    synchronized (commentList) {
                        if (commentList.size() > 2) {
                            List<CommentBase> cl = commentList.subList(0, 2);
                            mTMallCommentAdapter.addItem(cl);
                            commentList = commentList.subList(2, commentList.size());
                            tmall_live_tall.setSelection(mTMallCommentAdapter.getCount() - 1);
                        } else {
                            mTMallCommentAdapter.addItem(commentList);
                            tmall_live_tall.setSelection(mTMallCommentAdapter.getCount() - 1);
                            commentList.clear();
                        }
                    }
                }
                mHandler.sendEmptyMessageDelayed(TMALL_REFRESH_COMMENT, 1000);
                break;
            default:
                break;
        }
        return false;
    }

    private class GetTMallDetailListener extends BizRequestListener<TMallLiveDetailBean> {

        public GetTMallDetailListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            AppDebug.e(TAG, "直播详情请求失败 ========  " + msg);
            if (cid != null)
                requestLiveDetail(cid);
            else
                return false;

            return true;
        }

        @Override
        public void onSuccess(TMallLiveDetailBean data) {

            mTMallLiveDetailBean = data;

            tmall_live_home_num.setText("直播间ID：" + data.getChannel().getRoomId());
            liveTitle = data.getChannel().getCname();
            tmall_live_title.setText(liveTitle);
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    private class GetTMallCommentListener extends BizRequestListener<TMallLiveCommentBean> {

        public GetTMallCommentListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            mHandler.sendEmptyMessageDelayed(TMALL_COMMENT_QUERRY_REQUEST, 5000);
            return true;
        }

        @Override
        public void onSuccess(TMallLiveCommentBean data) {
            if (commentIdList.size() < 30) {
                for (int i = 0; i < 30; i++) {
                    commentIdList.add(data.getModel().getData().get(i).getCommentId());
                }
            } else {
                if (commentIdList.size() > 40)
                    commentIdList = commentIdList.subList(commentIdList.size() - 40, commentIdList.size());
                for (TMallLiveCommentBean.ModelBean.DataBean datasbean : data.getModel().getData()) {
                    String[] key = datasbean.getText().split("\n");
                    if (!commentIdList.contains(datasbean.getCommentId())) {
                        if (datasbean.getText().length() <= 40 && key.length <= 3) {
                            commentIdList.add(datasbean.getCommentId());
                            CommentBase commentBase = new CommentBase();
                            commentBase.setComment(datasbean.getText());
                            commentBase.setNick(datasbean.getAuthor().getDisplayName());
                            commentBase.setColor(Tools.color[Tools.getRandomBySize(Tools.color.length)]);
                            commentList.add(commentBase);
                        }
                    }
                }
            }

            mHandler.sendEmptyMessageDelayed(TMALL_COMMENT_QUERRY_REQUEST, 5000);
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    private class SendCommentListener extends BizRequestListener<TMallLiveCommentBean.ModelBean.DataBean> {

        public SendCommentListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            Toast.makeText(TMallLiveActivity.this, "语音发送失败,请重新发送", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void onSuccess(TMallLiveCommentBean.ModelBean.DataBean data) {

        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    private class GetTMallLiveListListener extends BizRequestListener<List<TMallLiveBean>> {

        public GetTMallLiveListListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            AppDebug.d(TAG, "直播列表失败  ========  " + msg);
            return false;
        }

        @Override
        public void onSuccess(List<TMallLiveBean> data) {
            if (data != null && data.size() > 0) {
                Map<String, String> properties = Utils.getProperties();
                properties.put("name", data.get(0).getName());
                properties.put("live_id", data.get(0).getLive_id());
                Utils.utCustomHit("telecast_detail", properties);

                changeLive(0, data.get(0));
            } else {
                tmall_live_loading_img.setVisibility(View.VISIBLE);
                tmall_live_loading_img.setBackgroundColor(0x99000000);
                tmall_live_loading_txt.setText("天猫直播都播完了，快来电视淘宝逛逛吧～");
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    private class GetTMallShopListener extends BizRequestListener<TMallLiveShopList> {
        public GetTMallShopListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(TMallLiveShopList data) {
            initShopRecylerView(data.getModel().getData());
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                if (ll_first_module.getVisibility() == View.VISIBLE) {
                    translateOut();
                    return true;
                }
                if (tv_tmall_live_code_layout.getVisibility() == View.VISIBLE) {
                    closeQRCode();

                    iv_login.setImageResource(R.drawable.ytm_qrcode_focused);
                    return true;
                }
            }

            if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
                if (liveListDialog == null)
                    liveListDialog = LiveListDialog.getInstance(this);
                liveListDialog.show(getFocusView());
                clearFocus();
                return true;
            }

            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                UpDownKeyChangeLive(false);
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                UpDownKeyChangeLive(true);
            }
        }

        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (ll_first_module.getVisibility() == View.VISIBLE) {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void londingState() {
        loadingType = 1;
        tmall_live_loading_img.setVisibility(View.VISIBLE);
        tmall_live_loading_img.setBackgroundDrawable(loadingDR);
    }

    private View getFocusView() {
        View rootview = TMallLiveActivity.this.getWindow().getDecorView();
        return rootview.findFocus();
    }

    public void clearFocus() {
        if (iv_login != null)
            iv_login.setFocusable(false);
        if (iv_live_list != null)
            iv_live_list.setFocusable(false);
        if (iv_shop != null)
            iv_shop.setFocusable(false);
        if (iv_praise != null)
            iv_praise.setFocusable(false);
        if (iv_light != null)
            iv_light.setFocusable(false);
    }

    public void setFocus() {
        if (iv_login != null)
            iv_login.setFocusable(true);
        if (iv_live_list != null)
            iv_live_list.setFocusable(true);
        if (iv_shop != null)
            iv_shop.setFocusable(true);
        if (iv_praise != null)
            iv_praise.setFocusable(true);
        if (iv_light != null)
            iv_light.setFocusable(true);
    }

    private int loadingType = 0;

    public int getLoadingType() {
        return loadingType;
    }

    private void UpDownKeyChangeLive(boolean isDownKey) {
        if (loadingType == 1)
            return;

        if (iv_praise.hasFocus() || iv_live_list.hasFocus() || iv_login.hasFocus() || iv_shop.hasFocus() || iv_light.hasFocus()) {
            if (mTMallLiveListBean != null) {
                boolean isINData = currentTMallLivePos < mTMallLiveListBean.size();
                if (isDownKey) {
                    if (currentTMallLivePos < mTMallLiveListBean.size() - 1 && isINData) {
                        currentTMallLivePos = currentTMallLivePos + 1;
                        changeLive(currentTMallLivePos, mTMallLiveListBean.get(currentTMallLivePos));
                    } else {
                        Toast.makeText(TMallLiveActivity.this, "已经到最底部", Toast.LENGTH_SHORT).show();
                    }

                    Utils.utControlHit("telecast_detail_down", Utils.getProperties());
                } else {
                    if (currentTMallLivePos > 0 && isINData) {
                        currentTMallLivePos = currentTMallLivePos - 1;
                        changeLive(currentTMallLivePos, mTMallLiveListBean.get(currentTMallLivePos));
                    } else {
                        Toast.makeText(TMallLiveActivity.this, "已经到最顶部", Toast.LENGTH_SHORT).show();
                    }

                    Utils.utControlHit("telecast_detail_up", Utils.getProperties());
                }
            }

            if (UpAndDownKey) {
                SharePreferences.put("UpAndDownKey", false);
            }
        }
    }

    /**
     * 打开商品列表
     */
    private void translateIn() {
        AnimationSet animationSet = new AnimationSet(true);
        Animation animation = new TranslateAnimation(-getResources()
                .getDisplayMetrics().widthPixels, 0, 0, 0);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        animationSet.addAnimation(animation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(400);
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());

        animationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                rv_live_listview.setAdapter(null);
                ll_first_module.setVisibility(View.VISIBLE);
                AnimUtils.fadeIn(tmall_live_shop_back_prompt, 400);

                if (mTMallLiveDetailBean != null && mTMallLiveDetailBean.getChannel() != null)
                    tv_live_title.setText(mTMallLiveDetailBean.getChannel().getOwnerNick());
                tmall_live_shop_log.setImageResource(R.drawable.icon_tmall_app);
                iv_shop.clearFocus();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBusinessRequest.baseRequest(new GetTMallShopRequest("tlive", cid, -1, 50), new GetTMallShopListener(new WeakReference<BaseActivity>(TMallLiveActivity.this)), false);

                ll_first_module.clearAnimation();
            }
        });
        ll_first_module.startAnimation(animationSet);
    }

    public void changeLive(int currentPos, TMallLiveBean itemsBean) {
        if (loadingType == 1)
            return;

        clearData();
        londingState();
        requestLiveDetail(itemsBean.getLive_id());
        initVideoView(itemsBean.getStream_url());
        requestCommentQuery(itemsBean.getLive_id());
        initCommentView();
        startLoopNotify();
        cid = itemsBean.getLive_id();
        liveTitle = itemsBean.getName();
    }

    /**
     * 关闭商品列表
     */
    private void translateOut() {
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation animation = new TranslateAnimation(0, -getResources()
                .getDisplayMetrics().widthPixels, 0, 0);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        animationSet.addAnimation(animation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(400);
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                AnimUtils.fadeOut(tmall_live_shop_back_prompt, 400);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ll_first_module.setVisibility(View.GONE);
                ll_first_module.clearAnimation();
                setFocus();
                iv_shop.requestFocus();
            }
        });
        ll_first_module.startAnimation(animationSet);
    }

    /**
     * 语音评论的样式变化
     */
    private void sendCommentStyle(String key) {
        final AnimationSet animationSetChu1 = new AnimationSet(true);
        final AnimationSet animationSetChu2 = new AnimationSet(true);
        final AnimationSet animationSetChu3 = new AnimationSet(true);
        final AnimationSet animationSetRu1 = new AnimationSet(true);
        final AnimationSet animationSetRu2 = new AnimationSet(true);
        final AnimationSet animationSetRu3 = new AnimationSet(true);

        final Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                tv_tvmall_comment.startAnimation(animationSetChu2);
                tv_tvmall_comment_success.startAnimation(animationSetRu2);
            }
        };

        final Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                ll_tvmall_comment.startAnimation(animationSetChu3);
                voice_chat_prompt.startAnimation(animationSetRu3);
            }
        };

        tv_tvmall_comment.setText(key);

        voice_chat_prompt.clearAnimation();
        ll_tvmall_comment.clearAnimation();
        tv_tvmall_comment.clearAnimation();
        tv_tvmall_comment_success.clearAnimation();
        myHandler.removeCallbacksAndMessages(null);
//        myHandler.removeCallbacks(runnable1);
//        myHandler.removeCallbacks(runnable2);

        AlphaAnimation alphaAnimationChu = new AlphaAnimation(1, 0);
        alphaAnimationChu.setDuration(100);
        AlphaAnimation alphaAnimationRu = new AlphaAnimation(0, 1);
        alphaAnimationRu.setDuration(100);

        animationSetChu1.addAnimation(alphaAnimationChu);//助手提示框淡出
        animationSetChu2.addAnimation(alphaAnimationChu); //评论淡出
        animationSetChu3.addAnimation(alphaAnimationChu); // 评论成功淡出
        animationSetRu1.addAnimation(alphaAnimationRu);//评论淡入
        animationSetRu2.addAnimation(alphaAnimationRu); //评论成功淡入
        animationSetRu3.addAnimation(alphaAnimationRu); //助手提示框淡入

        voice_chat_prompt.startAnimation(animationSetChu1);
        ll_tvmall_comment.startAnimation(animationSetRu1);
        animationSetChu1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                voice_chat_prompt.setVisibility(View.INVISIBLE);
                myHandler.postDelayed(runnable1, 3000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animationSetRu2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                tv_tvmall_comment_success.setVisibility(View.VISIBLE);
                iv_tvmall_micro.setBackgroundResource(R.drawable.ytm_tvmall_success);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                myHandler.postDelayed(runnable2, 1000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animationSetChu2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv_tvmall_comment.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animationSetChu3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ll_tvmall_comment.setVisibility(View.INVISIBLE);
                tv_tvmall_comment_success.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animationSetRu1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ll_tvmall_comment.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animationSetRu3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                voice_chat_prompt.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv_tvmall_comment.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    /**
     * 自定义淘客详情页打点
     */
    private void onHandleAnaylisysTaoke(List<TMallLiveShopList.ModelBean.DataBean> goodsData){
        if (LoginHelperImpl.getJuLoginHelper().isLogin()) {
            StringBuilder sellerIds = new StringBuilder();
            StringBuilder shopTypes = new StringBuilder();
            StringBuilder itemIds = new StringBuilder();

            for (int i=0; i < goodsData.size(); i++){
                TMallLiveShopList.ModelBean.DataBean goods = goodsData.get(i);
                itemIds.append(goods.getItemId()).append(",");
            }
            String stbId = DeviceUtil.initMacAddress(this);
            BusinessRequest.getBusinessRequest().requestTaokeDetailAnalysis(stbId, User.getNick(), itemIds.toString(), shopTypes.toString(), sellerIds.toString(), null);
        }
    }

    @Override
    public String getPageName() {
        return "Page_Tm_live_telecast_detail";
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> properties = Utils.getProperties();
        properties.put("live_name", liveTitle);
        properties.put("live_id", cid);
        properties.put("is_login", User.isLogined() + "");
        return properties;
    }

}
