package com.yunos.tvtaobao.live.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.media.ijkmediaplayer.IjkVideoView;
import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvlife.imageloader.core.assist.FailReason;
import com.tvlife.imageloader.core.listener.ImageLoadingListener;
import com.tvtaobao.voicesdk.utils.QRCodeUtil;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.pay.LoginHelperImpl;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.dialog.util.SnapshotUtil;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.LiveBlackList;
import com.yunos.tvtaobao.biz.request.bo.LiveDetailBean;
import com.yunos.tvtaobao.biz.request.bo.TBaoLiveListBean;
import com.yunos.tvtaobao.biz.request.bo.TBaoShopBean;
import com.yunos.tvtaobao.biz.request.bo.TMallLiveBean;
import com.yunos.tvtaobao.biz.request.bo.TMallLiveDetailBean;
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
import com.yunos.tvtaobao.live.utils.PowerMsgHelper;
import com.yunos.tvtaobao.live.utils.PowerMsgType;
import com.yunos.tvtaobao.live.utils.Tools;
import com.yunos.tvtaobao.live.view.Displayer;
import com.yunos.tvtaobao.live.view.FocusImageView;
import com.yunos.tvtaobao.live.view.LinearLayoutManagerTV;
import com.yunos.tvtaobao.live.view.LiveListDialog;
import com.yunos.tvtaobao.live.view.TimerToast;
import com.yunos.tvtaobao.live.view.ZPListView;
import com.yunos.tvtaobao.live.view.heart.TBaoPeriscopeLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by pan on 16/9/23.
 */
public class TaoBaoLiveActivity extends BaseActivity implements View.OnClickListener, Handler.Callback, Animation.AnimationListener {
    private static final String TAG = "TVLive_Taobao";
    private ZPListView rv_live_product;
    private LinearLayoutManagerTV linearLayoutManager;
    private RelativeLayout not_product_prompt, tbao_live_info_prompt, voice_chat_prompt_layout;
    private IjkVideoView vv_live;

    private LinearLayout rl_qr;
    private ImageView iv_back_ground, tbao_live_loading_img;
    private TextView joincount, tv_praise_count;
    private FocusImageView iv_praise, iv_tao_live_list, iv_care, iv_light;
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

    //二维码
    private ImageView iv_live_rq_image, iv_live_qr_icon;
    private TextView tv_live_qr_nickname, tv_live_qr_popularity, tv_live_qr_fans;

    private View oldFocusView;
    private TBaoPeriscopeLayout hl_praise;

    private String liveUrl, liveId, accountId, liveTitle, topic, headImg, accountInfoUrl;
    private int currentTBaoLivePos = 0;
    private static boolean isLight = false;
    private long changeJoinCount, totalJoinCount;
    private Double praise_count = 0.0, praise_count_click = 0.0;

    private BusinessRequest mBusinessRequest;
    private ImageLoaderManager mImageLoaderManager;
    private LiveDataController mLiveDataController;
    private List<TBaoLiveListBean> mTBaoLiveListBean = null;
    private DisplayImageOptions roundImageOptions;
    private Handler mHandler = new Handler(this);
    private boolean UpAndDownKey = true;
    private TimerToast toast;
    private boolean isFirstLoading = true;
    private LiveListDialog liveListDialog;

    FocusPositionManager focusPositionManager;
    private boolean isFollow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, TAG + ".onCreate");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        onKeepActivityOnlyOne(TaoBaoLiveActivity.class.getName());
        UpAndDownKey = SharePreferences.getBoolean("UpAndDownKey", true);

        liveUrl = getIntent().getStringExtra("liveUrl");
        liveId = getIntent().getStringExtra("liveId");
        accountId = getIntent().getStringExtra("accountId");
        topic = getIntent().getStringExtra("topic");

        Log.i(TAG, TAG + ".onCreate liveUrl = " + liveUrl + " ,liveId : " + liveId + " ,accountId : " + accountId + " ,topic : " + topic);
        setContentView(R.layout.ytm_v_live_window);
        initData();
        findView();
        londingState();

        requestData(liveId, accountId);

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
        commentAdapter = new LiveCommectAdapter(TaoBaoLiveActivity.this);
        shopAdapter = new TBaoShopAdapter(TaoBaoLiveActivity.this);
    }

    private void requestData(String liveid, String accountid) {
        //黑名单列表
        mBusinessRequest.baseRequest(new LiveBlackListRequest(), new GetLiveBlackListener(new WeakReference<BaseActivity>(this)), false);
        //直播详情
        mBusinessRequest.baseRequest(new GetLiveDetailRequest(liveid), new GetLiveDetailListener(new WeakReference<BaseActivity>(this)), false);
        //商品详情
        mBusinessRequest.baseRequest(new GetLiveHotItemListRequest("0", liveid, accountid), new GetLiveHotItemListlListener(new WeakReference<BaseActivity>(this)), false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        subscribeTopic(topic);
        if (vv_live != null && !vv_live.isPlaying() && liveUrl != null) {
            initVideoView(liveUrl);
        }
        startLoopNotify();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unSubscribeTopic(topic);
        vv_live.pause();
        removeNotify();
    }

    @Override
    public void finish() {
//        ASRUtils.getInstance().setHandler(null);
        vv_live.release();
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onRemoveKeepedActivity(TaoBaoLiveActivity.class.getName());

        clearData();
        clearViews();
        mHandler = null;
        shopcart = null;
        attention = null;
//        powerMsgCallback = null;
//        ASRUtils.getInstance().setHandler(null);
    }

    private void clearViews() {
        if (toast != null)
            toast.hide();

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
        if (alphaAnimation != null)
            alphaAnimation.setAnimationListener(null);
        alphaAnimation = null;
        toast = null;
        mBusinessRequest = null;
        mImageLoaderManager = null;
        mHandler = null;
    }

    private void clearData() {
        commentList.clear();
        if (commentAdapter != null)
            commentAdapter.clearData();

        if (shopAdapter != null)
            shopAdapter.clearData();

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
        mImageLoaderManager.clearMemoryCache();
    }

    private void findView() {
        focusPositionManager = (FocusPositionManager) findViewById(R.id.activity_main);
        focusPositionManager.setBackgroundDrawable(Tools.getBitmapDrawable(this, R.drawable.ytm_live_background));
        focusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(
                R.drawable.focusbox)));
        vv_live = (IjkVideoView) findViewById(R.id.vv_live);
        tbao_live_info_prompt = (RelativeLayout) findViewById(R.id.tbao_live_info_prompt);
        rv_live_product = (ZPListView) findViewById(R.id.rv_live_product);
        linearLayoutManager = new LinearLayoutManagerTV(TaoBaoLiveActivity.this);
        linearLayoutManager.setBottomPadding(Tools.compatiblePx(this, 260));
        linearLayoutManager.setTopPadding(Tools.compatiblePx(this, 260));
        rv_live_product.setLayoutManager(linearLayoutManager);
        not_product_prompt = (RelativeLayout) findViewById(R.id.not_product_prompt);
        rl_qr = (LinearLayout) findViewById(R.id.rl_qr);
        iv_back_ground = (ImageView) findViewById(R.id.iv_back_ground);
        tbao_live_loading_img = (ImageView) findViewById(R.id.tbao_live_loading_img);
        tbao_live_loading_img.setImageDrawable(Tools.getBitmapDrawable(this, R.drawable.tbao_live_loading_img));
        voice_chat_prompt_layout = (RelativeLayout) findViewById(R.id.voice_chat_prompt_layout);

        iv_light = (FocusImageView) findViewById(R.id.iv_light);
        iv_live_qr_icon = (ImageView) findViewById(R.id.iv_live_qr_icon);
        iv_praise = (FocusImageView) findViewById(R.id.iv_praise);
        focusPositionManager.requestFocus(iv_praise, 0);
        iv_care = (FocusImageView) findViewById(R.id.iv_care);
        iv_tao_live_list = (FocusImageView) findViewById(R.id.iv_tao_live_list);
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

        //todo add config
//        if ((Config.getChannel().equals("701229") || Config.getChannel().equals("10003226") || Config.getChannel().equals("10004416")) && DeviceUtil.getYuyinPackageCode(this) >= 2100300000)
//            voice_chat_prompt_layout.setVisibility(View.VISIBLE);

        iv_praise.setOnClickListener(this);
        iv_care.setOnClickListener(this);
        iv_tao_live_list.setOnClickListener(this);
        iv_light.setOnClickListener(this);

        iv_care.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (rl_qr.getVisibility() == View.VISIBLE) {
                    AnimUtils.fadeOut(rl_qr, 300);
                    iv_care.setImageResource(R.drawable.ytm_tbao_live_qrcode_btn_bg);
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

        vv_live.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                AppDebug.d(TAG, TAG + ".videoListener onPrepared");
                mHandler.removeMessages(PowerMsgType.LIVE_ERROR);
            }
        });

        vv_live.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int i1) {
                boolean handled = false;
                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        AppDebug.d(TAG, "onInfo MediaPlayer.MEDIA_INFO_BUFFERING_START");
                        mHandler.removeMessages(PowerMsgType.LIVE_ERROR);
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
        properties.put("live_name", liveTitle);
        properties.put("live_id", topic);
        int i = v.getId();
        if (i == R.id.iv_light) {
            if (isLight) {
                toLight();
                if (iv_tao_live_list != null && oldFocusView != null)
                    iv_tao_live_list.setNextFocusLeftId(oldFocusView.getId());

                properties.put("controlname", "switch_on");
                Utils.utControlHit("telecast_detail_switch_on", properties);
                isLight = false;
            } else {
                findViewById(R.id.ll_first_module).setVisibility(View.INVISIBLE);
                findViewById(R.id.ll_right_top_moudle).setVisibility(View.INVISIBLE);
                iv_back_ground.setVisibility(View.INVISIBLE);
                findViewById(R.id.iv_total_joincount_layout).setVisibility(View.INVISIBLE);
                findViewById(R.id.tbao_live_comment_layout).setVisibility(View.INVISIBLE);
                if (iv_tao_live_list != null)
                    iv_tao_live_list.setNextFocusLeftId(iv_tao_live_list.getId());

                properties.put("controlname", "swtich_off");
                Utils.utControlHit("telecast_detail_switch_on", properties);
                isLight = true;
            }


        } else if (i == R.id.iv_praise) {
            hl_praise.addHeart();
            praise_count++;
            tv_praise_count.setText(Tools.getNum(praise_count));
            praise_count_click++;
            properties.put("controlname", "like");
            Utils.utControlHit("telecast_detail_button_like", properties);

        } else if (i == R.id.iv_tao_live_list) {
            if (liveListDialog == null)
                liveListDialog = LiveListDialog.getInstance(this);
            liveListDialog.show(iv_tao_live_list);
            setFocusableF();
            properties.put("controlname", "videolist");
            Utils.utControlHit("telecast_detail_button_change", properties);

        } else if (i == R.id.iv_care) {
            properties.put("controlname", "code");
            Utils.utControlHit("telecast_detail_button_qrcode", properties);
            if (rl_qr.getVisibility() == View.VISIBLE) {
                AnimUtils.fadeOut(rl_qr, 300);
                iv_care.setImageResource(R.drawable.ytm_tbao_live_qrcode_btn_bg);
            } else {
                AlphaAnimation animation = new AlphaAnimation(0f, 1f);
                animation.setDuration(300);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        rl_qr.setVisibility(View.VISIBLE);
                        createQR();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                rl_qr.startAnimation(animation);
                iv_care.setImageResource(R.drawable.live_qrcode_open);
            }

        }
    }

    public void getDetailDate(LiveDetailBean data) {
        Log.e(TAG, TAG + ".getDetailDate data : " + data);
        if (data == null)
            return;

        if (!vv_live.isPlaying()) {
            initVideoView(data.getInputStreamUrl());
        }

        String accountNick = data.getAccountNick();
        liveTitle = data.getTitle();
        String location = data.getLocation();
        headImg = data.getHeadImg();
        //praise_count = Double.parseDouble(data.getPraiseCount());
        //accountInfoUrl = data.getBroadCaster().getJumpUrl();

        tv_name.setText(accountNick);
        tv_live_qr_nickname.setText(accountNick);
        tbao_live_title.setText(liveTitle);
        //tv_live_qr_popularity.setText(data.getTotalJoinCount() + "人气");//人气
        tv_location.setText(location);
        //tv_live_qr_fans.setText(Tools.exchangeUnit(data.getBroadCaster().getFansNum()) + "粉丝");//粉丝
        tv_praise_count.setText(Tools.getNum(praise_count));
        //totalJoinCount = Integer.parseInt(data.getTotalJoinCount());
        joincount.setText(totalJoinCount + "观看");

        mImageLoaderManager.displayImage(headImg, iv_head_icon, roundImageOptions);

        setGuess(data.getCoverImg());
    }

    private void initShopList(TBaoShopBean tBaoShopBean) {
        if (tBaoShopBean == null || tBaoShopBean.getItemList() == null || tBaoShopBean.getItemList().size() == 0) {
            not_product_prompt.setVisibility(View.VISIBLE);
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

    /**
     * 生成二维码
     */
    private void createQR() {
        mImageLoaderManager.displayImage(headImg, iv_live_qr_icon, roundImageOptions);

        String accountUrl = Tools.getTrueImageUrl(accountInfoUrl);
        Bitmap qrBitmap = null;
        try {
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_tbao_app);
            ViewGroup.LayoutParams para = iv_live_rq_image.getLayoutParams();
            qrBitmap = QRCodeUtil.create2DCode(accountUrl, para.width, para.height, icon);
            Drawable newBitmapDrawable = new BitmapDrawable(qrBitmap);
            iv_live_rq_image.setImageDrawable(newBitmapDrawable);
        } catch (WriterException e) {
            e.printStackTrace();
        }

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
                    joincount.setText(totalJoinCount + " 观看");
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
                autoChangeLive();
                break;
            case PowerMsgType.LIVE_LOADING_NOTIFY:
                if (alphaAnimation == null) {
                    alphaAnimation = new AlphaAnimation(1f, 0f);
                    alphaAnimation.setDuration(600);
                    alphaAnimation.setAnimationListener(this);
                }
                tbao_live_loading_img.startAnimation(alphaAnimation);
                break;
        }
        return false;
    }


    @Override
    public void onAnimationStart(Animation animation) {
        AppDebug.e(TAG, "onAnimationStart");
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        AppDebug.e(TAG, "onAnimationEnd");
        if (animation == alphaAnimation) {
            tbao_live_loading_img.clearAnimation();
            tbao_live_loading_img.setVisibility(View.GONE);
            if (UpAndDownKey && isFirstLoading) {
                if (toast == null)
                    toast = TimerToast.makeText(TaoBaoLiveActivity.this, "[上下键]可切换视频", 5 * 1000);
                toast.show();
                isFirstLoading = false;
            }
        }

    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }


    private AlphaAnimation alphaAnimation = null;


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
            return true;
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
    private class GetLiveDetailListener extends BizRequestListener<LiveDetailBean> {

        public GetLiveDetailListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(LiveDetailBean data) {
            getDetailDate(data);
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
                SnapshotUtil.getFronstedBitmap(loadedImage, 5, new SnapshotUtil.OnFronstedGlassSreenDoneListener() {
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
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                if (isFunBtnHasFocus())
                    UpDownKeyChangeLive(false);
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (isFunBtnHasFocus())
                    UpDownKeyChangeLive(true);
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private boolean isFunBtnHasFocus() {
        return iv_praise.hasFocus() || iv_tao_live_list.hasFocus() || iv_care.hasFocus() || iv_light.hasFocus();
    }

    private void UpDownKeyChangeLive(boolean isDownKey) {
        if (loadingType == 1)
            return;

        if (mTBaoLiveListBean != null) {
            boolean isINData = currentTBaoLivePos < mTBaoLiveListBean.size();
            if (isDownKey) {
                AppDebug.e(TAG, "mTBaoLiveListBean size = " + mTBaoLiveListBean.size() + "   ,currentTBaoLivePos = " + currentTBaoLivePos);
                if (currentTBaoLivePos < mTBaoLiveListBean.size() - 1 && isINData) {
                    currentTBaoLivePos = currentTBaoLivePos + 1;

                    changeLive(currentTBaoLivePos, mTBaoLiveListBean.get(currentTBaoLivePos));
                } else {
                    Toast.makeText(TaoBaoLiveActivity.this, "已经到最底部", Toast.LENGTH_SHORT).show();
                }

                Utils.utControlHit("telecast_detail_down", Utils.getProperties());
            } else {
                if (currentTBaoLivePos > 0 && isINData) {
                    currentTBaoLivePos = currentTBaoLivePos - 1;

                    changeLive(currentTBaoLivePos, mTBaoLiveListBean.get(currentTBaoLivePos));
                } else {
                    Toast.makeText(TaoBaoLiveActivity.this, "已经到最顶部", Toast.LENGTH_SHORT).show();
                }

                Utils.utControlHit("telecast_detail_up", Utils.getProperties());
            }
        }

        if (UpAndDownKey) {
            SharePreferences.put("UpAndDownKey", false);
        }
    }

    //视频出错处理
    private void videoError() {
        vv_live.release();
        tbao_live_info_prompt.setVisibility(View.VISIBLE);

        mHandler.sendEmptyMessageDelayed(PowerMsgType.LIVE_ERROR, 8000);
    }

    //自动切换视频
    private void autoChangeLive() {
        if (mTBaoLiveListBean == null) {
            return;
        }

        if (currentTBaoLivePos >= mTBaoLiveListBean.size() - 1) {
            int tbaoLiveSize = mTBaoLiveListBean.size();
            int random = Tools.getRandomBySize(tbaoLiveSize);
            changeLive(random, mTBaoLiveListBean.get(random));
        } else {
            UpDownKeyChangeLive(true);
        }
    }

    //切换视频loading状态
    private int loadingType = 0; //0 可以切换,1 不可切换视频


    private void londingState() {
        tbao_live_loading_img.setVisibility(View.VISIBLE);
        mHandler.sendEmptyMessageDelayed(PowerMsgType.LIVE_LOADING_NOTIFY, 2000);
    }

    /**
     * 切换视频
     *
     * @param data
     */
    public void changeLive(int currentPos, TBaoLiveListBean data) {
        if (loadingType == 1){
            return;
        }

        if(data == null){
            return;
        }

        removeNotify();
        londingState();
        unSubscribeTopic(topic);
        clearData();
        requestData(data.getLiveId(), data.getAccountId());
        //initVideoView(data.getInputStreamUrl());
        initComment();
        toLight();
        startLoopNotify();
        topic = data.getLiveId();
        liveTitle = data.getTitle();
        liveId = data.getLiveId();
        accountId = data.getAccountId();
        //liveUrl = data.getInputStreamUrl();
        subscribeTopic(topic);
        focusPositionManager.requestFocus(iv_praise, 0);
        mLiveDataController.setTBaoItemsBean(data);
        mLiveDataController.setCurrentTBaoLivePos(currentPos);
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


//    private
//    IPowerMsgCallback powerMsgCallback = new IPowerMsgCallback() {
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

        TvTaobaoMsgService.registerTVLive(TaoBaoLiveActivity.this, TvTaobaoMsgService.TYPE_TAOBAO_LIVE, topic, new ITVTaoDiapatcher() {
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
        View rootview = TaoBaoLiveActivity.this.getWindow().getDecorView();
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
        TvTaobaoMsgService.unRegisterTVLive(TaoBaoLiveActivity.this, TvTaobaoMsgService.TYPE_TAOBAO_LIVE, topic);
    }

    /**
     * 加载视频
     *
     * @param hlsUrl
     */
    private void initVideoView(String hlsUrl) {
        Log.e(TAG, TAG + ".initVideoView hlsUrl");
        tbao_live_info_prompt.setVisibility(View.GONE);
        vv_live.setFocusable(false);
        if (hlsUrl != null)
            vv_live.setVideoURI(Uri.parse(hlsUrl));

        if (vv_live != null)
            vv_live.start();
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
        return "Page_Tblive_telecast_detail";
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> properties = Utils.getProperties();
        properties.put("live_name", liveTitle);
        properties.put("live_id", liveId);
        properties.put("is_login", User.isLogined() ? "1" : "0");
        return properties;
    }

    private static class TaobaoLivePresenter {
        private interface TaobaoLiveModel {
            void getChannels(ResultCallback<List<TMallLiveDetailBean.ChannelBean>> callback);

            void getLiveList(String channelId, long timeOffset, ResultCallback<List<TMallLiveBean>> callback);//根据频道获取视频列表

            void getLiveDetail(String liveId, ResultCallback<TMallLiveDetailBean> callback);//获取视频详细信息

            void subscribe(String actorId, ResultCallback<Boolean> callback);//关注主播

            void unsubscribe(String actorId, ResultCallback<Boolean> callback);//取消关注

            void getCommoditiyList(String liveId, ResultCallback<TBaoShopBean> callback);//商品列表
        }

        private interface TaobaoLiveView {
            void updateLiveList(List<TMallLiveBean> list, String selectedId);

            void switchLive(String liveId, boolean landscape);

            void showErrorMsg(String errorMsg);

            void updateLiveDetail(TMallLiveDetailBean bean);

            void updateCommodities(TBaoShopBean bean);

            void showItemDetail(TBaoShopBean.ItemListBean.GoodsListBean bean);

            void switchChannel(String channlId, String selectedVideoId);


        }
    }

    private interface ResultCallback<T> {
        void onSuccess(T result);

        void onError(int code, String errorMsg);
    }
}
