package com.yunos.tvtaobao.zhuanti.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunos.tv.blitz.account.LoginHelper;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.config.SystemConfig;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tv.core.util.ActivityPathRecorder;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tv.core.util.IntentDataUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tv.tvsdk.media.IMediaPlayer;
import com.yunos.tv.tvsdk.media.view.VideoView;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.bo.MockData;
import com.yunos.tvtaobao.biz.request.bo.ProductTagBo;
import com.yunos.tvtaobao.biz.request.bo.SearchResult;
import com.yunos.tvtaobao.biz.request.bo.TBDetailResultV6;
import com.yunos.tvtaobao.biz.request.bo.Unit;
import com.yunos.tvtaobao.biz.request.core.ServiceCode;
import com.yunos.tvtaobao.biz.request.utils.DetailV6Utils;
import com.yunos.tvtaobao.biz.util.StringUtil;
import com.yunos.tvtaobao.zhuanti.R;
import com.yunos.tvtaobao.zhuanti.bo.TvBuyItems;
import com.yunos.tvtaobao.zhuanti.bo.TvGetIntegration;
import com.yunos.tvtaobao.zhuanti.bo.TvIntegration;
import com.yunos.tvtaobao.zhuanti.bo.enumration.GoodDetail;
import com.yunos.tvtaobao.zhuanti.constant.IntentKey;
import com.yunos.tvtaobao.zhuanti.net.ZhuanTiBusinessRequest;
import com.yunos.tvtaobao.zhuanti.utils.AnimationUtil;
import com.yunos.tvtaobao.zhuanti.utils.SharePreUtil;
import com.yunos.tvtaobao.zhuanti.utils.TimePickerUtil;
import com.yunos.tvtaobao.zhuanti.utils.TimeUtil;
import com.yunos.tvtaobao.zhuanti.view.ArcProgress;
import com.yunos.tvtaobao.zhuanti.view.MyProgressbarRelativeLayout;
import com.yunos.tvtaobao.zhuanti.view.PopAddTipDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


/**
 * Created by pan on 2017/4/20.
 */

public class TvBuyActivity extends ZhuanTiBaseActivity {
    private boolean flag = false;
    private static final int SHOW_BOTTOM_AND_TOP_POP = 101;
    private static final int TVBUY_CALCULATE_SYSTEM_TIME = 7;
    private static final int TVBUY_SHOW_COUNT_DOWN = 8;
    private static final int TVBUY_SHOW_GET_VIEW = 9;
    private static final int TVBUY_SHOW_GET_SUCCESS = 10;
    private static final int TVBUY_SHOW_GET_FAIL = 16;
    private static final int HIDE_PIANTOU_PIANWEI = 1;
    private static final int TVBUY_SEEKTO_PLAY = 106;
    private MyProgressbarRelativeLayout myProgressbarRelativeLayout;
    private ImageButton btn_left;
    private ImageButton btn_center;
    private ImageButton btn_right;
    private VideoView videoView;
    private int currentVideoNum;
    private int currentPosition = 0;//ms
    private int totallength = 0;  //s
    private int seekToPosition = 0;
    private ProgressBar progressBar;
    private LinearLayout loading_relativeLayout;//加载中状态的视图
    private TextView tv_error;
    private PopupWindow pop_bottom;
    private PopupWindow pop_top;
    private View bottom_view;
    private View top_view;
    private ImageView ib_next;
    private ImageView ib_last;
    private Timer timer;
    private ImageView iv_home_play;
    private TextView tv_top_pop_video_name;
    private TextView tv_bottom_pop_video_name;
    private TextView tv_bottom_pop_price;
    private ImageView iv_bottom_pop_good_picture;
    private List<TvBuyItems.TvBuyItem> data1;
    private LinearLayout ll_lastVideo;
    private LinearLayout ll_nextVideo;
    private TextView tv_good_name_last;
    private TextView tv_good_name_next;
    private ImageView iv_good_last;
    private ImageView iv_good_next;
    private View pianwei_view;
    private PopupWindow pianwei_pop;
    private ImageButton ib_right;
    private ImageButton ib_left;
    private ImageView iv_next_video;
    private ImageView iv_last_video;
    private Animation anim_visible;
    private Animation anim_invisible;
    private PopAddTipDialog popAddTipDialog;
    private ImageView iv_error;

    //
    private TBDetailResultV6 tbDetailResultV6;
    private boolean isFeizhu;
    private boolean isZTC;
    private String source;
    private boolean isPre;
    private String nowPrice;
    private static String tagId;
    private static String outPreferentialId;

    public void setMyProgress(long position) {
        if (totallength == 0) {
            totallength = videoView.getDuration();
            return;
        } else {
            if (totallength > 0) {
                myProgressbarRelativeLayout.setMaxProgress(totallength);
                progressBar.setMax(totallength);
            } else if (totallength < 0) {
                return;
            }
        }
        progressBar.setProgress((int) position);
        myProgressbarRelativeLayout.setCurrentProgress(position);
        myProgressbarRelativeLayout.setTime((position));
        myProgressbarRelativeLayout.setCurrentProgressOnProgressBar(position);
        if (position >= totallength - 5000 && !pianwei_pop.isShowing() && !pop_get.isShowing() && videoView.isPlaying()) {
            pianwei_pop.showAtLocation(videoView, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 0);
        }
    }

    private RelativeLayout rl_point_get;
    private long mCountDownStart = -1; //开抢前倒计时
    private long mCountRashStart = 0;  //开抢增量
    private long mCountRashMax = 1;
    private long mShowEndStart = 3;  //抢完后倒计时，正式设定值3s，结束后贴片出现
    private List<TvIntegration.TvIntegrationItem> mTvInterItemList = new ArrayList<TvIntegration.TvIntegrationItem>();
    //商品详情
    private ImageView iv_good, iv_add_bug, iv_good_deliveryFee;
    private TextView tv_goods_title, tv_tip_1, tv_tip_2, tv_tip_3, tv_good_price, tv_originalPrice, tv_good_sold, tv_count_time, tv_point_next, tv_originalPrice_tip;
    private TextView tv_fail_next_time, tv_point_value, tv_count_bottom;
    private LinearLayout ll_tip_1, ll_tip_2, ll_tip_3, ll_original_price;
    // 图片下载管理
    private ImageLoaderManager mImageLoaderManager;
    private TvBuyLoginListener tvBugLogin;
    private boolean isGotoBug = false;
    private boolean canAddBug = true;
    private PopupWindow pop_detail;
    private View pop_detail_view;
    private PopupWindow pop_count;
    private View pop_count_view;
    private View pop_get_view;
    private PopupWindow pop_get;
    private PopupWindow pop_get_success;
    private ArcProgress arcProgress;
    private View pop_get_success_view;
    private PopupWindow pop_get_fail;
    private View pop_get_fail_view;
    private int loginType = -1;//0 是加购物车登录，1是抢积分登录 2是去购物车

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.cytz_tvbuy_activity);
        String ztcSource = getIntent().getStringExtra(BaseConfig.INTENT_KEY_ISZTC);
        if (TextUtils.isEmpty(ztcSource)) {
            isZTC = false;
        } else {
            isZTC = ztcSource.equals("true") ? true : false;
        }

        String channleCode = getIntent().getStringExtra(CoreIntentKey.URI_CHANNEL_CODE);
        String channelName = getIntent().getStringExtra(CoreIntentKey.URI_CHANNEL_NAME);
        if(channleCode != null){
            TvOptionsConfig.setTvOptionsChannel(channleCode);
        }
        TvOptionsConfig.setTvOptionVoiceSystem(getIntent().getStringExtra(CoreIntentKey.URI_FROM));

        source = getIntent().getStringExtra(BaseConfig.INTENT_KEY_SOURCE);
        String mTmsUrl = IntentDataUtil.getString(getIntent(), IntentKey.URI_TMS, null);
        AppDebug.e(TAG, "运营配置的视频信息url=" + mTmsUrl);
        initView();
        initPop();
        requestTvBuyItems(mTmsUrl);
        addAccountListen();
        requestIntegrationTime();
    }

    private void requestGoodDetail(String itemId) {
        AppDebug.e("TAG", "itemId = " + itemId);
        mGoodDetail = new GoodDetail();
        getNewDetail(itemId);
    }

    /**
     * 显示商品详情
     *
     * @param goodDetail
     */
    private GoodDetail mGoodDetail;

    private void showGoodsDetail(final GoodDetail goodDetail) {
        AppDebug.e(TAG, "showGoodsDetail-----");
        if (mGoodDetail == null)
            return;

        mGoodDetail.isStart = true;
        //设置图片
        if (!TextUtils.isEmpty(goodDetail.picsPath)) {
            setItemImage(goodDetail.picsPath, iv_good);
        }

        //设置
        if (!TextUtils.isEmpty(goodDetail.deliveryFees)) {
            if (goodDetail.deliveryFees.equals("免运费")) {
                iv_good_deliveryFee.setVisibility(View.VISIBLE);
            } else {
                iv_good_deliveryFee.setVisibility(View.INVISIBLE);
            }
        } else {
            iv_good_deliveryFee.setVisibility(View.INVISIBLE);
        }
        //设置标题
        if (!TextUtils.isEmpty(goodDetail.title)) {
            if (goodDetail.title.length() > 18) {
                tv_goods_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.sp_24));
            } else {
                tv_goods_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.sp_30));
            }
            tv_goods_title.setText(goodDetail.title);
        }
        //设置商品标签
        ll_tip_1.setVisibility(View.INVISIBLE);
        ll_tip_2.setVisibility(View.INVISIBLE);
        ll_tip_3.setVisibility(View.INVISIBLE);
        if (goodDetail.afterGuaranteeList != null && goodDetail.afterGuaranteeList.size() > 0) {
            int size = goodDetail.afterGuaranteeList.size();
            AppDebug.e(TAG, "afterGuaranteeList = " + size);
            if (size == 1) {
                ll_tip_1.setVisibility(VISIBLE);
                tv_tip_1.setText(goodDetail.afterGuaranteeList.get(0));
            } else if (size == 2) {
                ll_tip_1.setVisibility(VISIBLE);
                tv_tip_1.setText(goodDetail.afterGuaranteeList.get(0));
                ll_tip_2.setVisibility(VISIBLE);
                tv_tip_2.setText(goodDetail.afterGuaranteeList.get(1));
            } else if (size >= 3) {
                ll_tip_1.setVisibility(VISIBLE);
                tv_tip_1.setText(goodDetail.afterGuaranteeList.get(0));
                ll_tip_2.setVisibility(VISIBLE);
                tv_tip_2.setText(goodDetail.afterGuaranteeList.get(1));
                ll_tip_3.setVisibility(VISIBLE);
                tv_tip_3.setText(goodDetail.afterGuaranteeList.get(2));
            }
        }
        if (!TextUtils.isEmpty(goodDetail.price)) {
            if (!TextUtils.isEmpty(StringUtil.formatPriceToSpan(TvBuyActivity.this, goodDetail.price)))
                tv_good_price.setText(StringUtil.formatPriceToSpan(TvBuyActivity.this, goodDetail.price));
        }
        ll_original_price.setVisibility(View.VISIBLE);
        //设置商品原价
        if (!TextUtils.isEmpty(goodDetail.originalPrice) && !TextUtils.isEmpty(goodDetail.price)) {
            String originalPrice = goodDetail.originalPrice;
            int p = originalPrice.indexOf(".");
            tv_originalPrice_tip.setVisibility(VISIBLE);
            if (p > 6) {
                ll_original_price.setVisibility(INVISIBLE);
            } else {
                tv_originalPrice.setText(goodDetail.originalPrice);
                tv_originalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                ll_original_price.setVisibility(VISIBLE);
            }
        } else {
            tv_originalPrice_tip.setVisibility(INVISIBLE);
            tv_originalPrice.setText("");

        }
        //显示销售量
        tv_good_sold.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(goodDetail.sold)) {
            tv_good_sold.setText("月销:" + goodDetail.sold + "件");
        } else {
            tv_good_sold.setText("月销:0件");
        }
        AppDebug.e(TAG, "canAddBug = " + canAddBug + "isStart = " + mGoodDetail.isStart);
        if (canAddBug) {
            //能加入购买
            if (isGotoBug) {
                //已经加入购物车，显示去购物车
                iv_add_bug.setImageResource(R.drawable.cytz_go_bug);
            } else {
                //未加入购物车，显示加购物车
                iv_add_bug.setImageResource(R.drawable.cytz_add_bug);
            }
        } else {
            if (!mGoodDetail.isStart) {
                //不支持购买，并且是因为没有开售的情况
                iv_add_bug.setImageResource(R.drawable.cytz_good_notstart);
            } else {
                //其他情况,显示被抢光
                iv_add_bug.setImageResource(R.drawable.cytz_good_null);
            }

        }

        pop_detail.showAtLocation(pop_detail_view, Gravity.BOTTOM, 0, 0);

    }

    public void addAccountListen() {
        tvBugLogin = new TvBuyLoginListener(new WeakReference<TvBuyActivity>(TvBuyActivity.this));
        CoreApplication.getLoginHelper(TvBuyActivity.this).addReceiveLoginListener(tvBugLogin);

    }

    private void removeAccountListen() {
        if (tvBugLogin != null) {
            CoreApplication.getLoginHelper(TvBuyActivity.this).removeReceiveLoginListener(
                    tvBugLogin);
        }
    }

    /**
     * 登录成功的回调，如果是是抢积分时
     * 则登录成功后，调抢积分的接口，自动完成抢积分。
     */
    private class TvBuyLoginListener implements LoginHelper.SyncLoginListener {

        private WeakReference<TvBuyActivity> tvBuyActivity;

        public TvBuyLoginListener(WeakReference<TvBuyActivity> ref) {
            tvBuyActivity = ref;
        }

        @Override
        public void onLogin(boolean success) {
            AppDebug.e(TAG, "登录成功的回调");
            if (success && loginType == 1) {
                long time = System.currentTimeMillis();
                if (mTvIntegerItem != null) {
                    if (time <= mTvIntegerItem.endAt) {
                        AppDebug.e(TAG, "自动帮助用户领取积分  loginType= " + loginType);
                        if (!TextUtils.isEmpty(mTvIntegerItem.id)) {
                            ZhuanTiBusinessRequest.getBusinessRequest().requestGetVideoPointRequest(mTvIntegerItem.pointSchemeId,
                                    new GetVideoPointListener(tvBuyActivity));
                        }
                        loginType = -1;
                    }
                }
            } else if (success && loginType == 2) {
                AppDebug.e(TAG, "去购物车 loginType = " + loginType);
                Intent mIntent = new Intent();
                mIntent.setData(Uri.parse("tvtaobao://home?module=cart&notshowloading=true"));
                if (tvBuyActivity.get()!=null){
                    tvBuyActivity.get().startActivity(mIntent);
                }
                loginType = -1;
            } else if (success && loginType == 0) {
                AppDebug.e(TAG, "加入购物购  = " + loginType);
                if (mGoodDetail != null) {
                    ZhuanTiBusinessRequest.getBusinessRequest().requestAddCartRequest(data1.get(currentVideoNum).getItemId(), 1, mGoodDetail.skuId, buildAddBagExParams(),
                            new GetAddCartRequestListener(tvBuyActivity));
                }

                loginType = -1;
            }
        }
    }


    /**
     * 设置图片
     *
     * @param picsPath
     */
    public void setItemImage(String picsPath, ImageView imageView) {
        AppDebug.e(TAG, " picsPath = " + picsPath);
        if (SystemConfig.DENSITY > 1.0) {
            picsPath = picsPath + "_960x960.jpg";
        } else {
            picsPath = picsPath + "_640x640.jpg";
        }
        if (Config.isDebug()) {
            AppDebug.v(TAG, TAG + ".setItemImage.theImageUrl = " + picsPath);
        }
        mImageLoaderManager.loadImage(picsPath, imageView, null);
    }


    /**
     * 请求积分打点的时间
     */
    private void requestIntegrationTime() {

        ZhuanTiBusinessRequest.getBusinessRequest().requestVideoPointScheme(
                new GetVideoPointSchemeListener(new WeakReference<BaseActivity>(this)));
    }

    private MyHandler mHandler = new MyHandler(TvBuyActivity.this);
    private static final int UPDATE_VIDEO_POSITION_ONE_SECOND = 100;
    private static final int DISMISS_TOP_POP = 102;

    private class MyHandler extends Handler {
        private WeakReference<Context> weakReference;

        public MyHandler(Context context) {
            weakReference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            TvBuyActivity tvBuyActivity = (TvBuyActivity) weakReference.get();
            if (tvBuyActivity == null)
                return;
            switch (msg.what) {
                case UPDATE_VIDEO_POSITION_ONE_SECOND:
                    currentPosition = videoView.getCurrentPosition();
                    setMyProgress(currentPosition);
                    break;

                case SHOW_BOTTOM_AND_TOP_POP:
                    if (mGoodDetail != null) {
                        if (!pop_get.isShowing())
                            showGoodsDetail(mGoodDetail);
                        progressBar.setVisibility(VISIBLE);
                        showTitlePop();
                        mHandler.sendEmptyMessageDelayed(DISMISS_TOP_POP, 5000);
                    }

                    break;
                case DISMISS_TOP_POP:
                    if (pop_top.isShowing())
                        pop_top.dismiss();
                    break;

                case SET_LEFT_RIGHT_IMAGE_BUTTON_ORIGIN:
                    if (ib_left.isPressed())
                        ib_left.setPressed(false);
                    if (ib_right.isPressed())
                        ib_right.setPressed(false);
                    break;
                case KUAIJIN_KUAITUI_MESSAGE:
                    if (mHandler.hasMessages(KUAIJIN_KUAITUI_MESSAGE))
                        mHandler.removeMessages(KUAIJIN_KUAITUI_MESSAGE);
                    if (currentPosition > videoView.getCurrentPosition()) {
                        loading_relativeLayout.setVisibility(VISIBLE);
                        AppDebug.e(TAG, "显示缓冲条" + currentPosition + " 当前进度=" + videoView.getCurrentPosition());
                    } else
                        loading_relativeLayout.setVisibility(View.INVISIBLE);
                    break;

                case HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL:
                    if (myProgressbarRelativeLayout.getVisibility() == VISIBLE) {
                        myProgressbarRelativeLayout.showOrHide(false);
                        ib_next.clearFocus();
                        ib_last.clearFocus();
                        iv_next_video.setVisibility(View.INVISIBLE);
                        iv_last_video.setVisibility(View.INVISIBLE);
                    }
                    showGoodsDetail(mGoodDetail);
                    progressBar.setVisibility(VISIBLE);
                    if (pop_top.isShowing())
                        pop_top.dismiss();

                    break;

                case TVBUY_CALCULATE_SYSTEM_TIME:
                    tvBuyActivity.isStartCountDown();
                    break;
                case TVBUY_SHOW_COUNT_DOWN:
                    //角标开始倒计时，展示角标，并刷新角标显示的数字
                    AppDebug.e(TAG, "TVBUY_SHOW_COUNT_DOWN");
                    if (mHandler.hasMessages(TVBUY_CALCULATE_SYSTEM_TIME)) {
                        mHandler.removeMessages(TVBUY_CALCULATE_SYSTEM_TIME);
                    }
                    if (mTvIntegerItem != null) {
                        tv_count_bottom.setVisibility(View.VISIBLE);
                        long system = System.currentTimeMillis();
                        long time = mTvIntegerItem.beginAt - system;
                        if (time > 0) {
                            tvBuyActivity.mCountDownStart = time / 1000;
                            if (isError) {
                                if (tvBuyActivity.pop_count.isShowing()) {
                                    tvBuyActivity.pop_count.dismiss();
                                }
                            } else {
                                if (!tvBuyActivity.pop_count.isShowing()) {
                                    tvBuyActivity.pop_count.showAtLocation(pop_count_view, Gravity.RIGHT, 0, 0);
                                    utCount();
                                }
                            }
                            AppDebug.e(TAG, "开抢前 isError = " + isError);
                            tvBuyActivity.mCountDownTimer.start(true);
                        } else {
                            if (mHandler.hasMessages(TVBUY_SHOW_COUNT_DOWN))
                                mHandler.removeMessages(TVBUY_SHOW_COUNT_DOWN);
                            mHandler.sendEmptyMessage(TVBUY_SHOW_GET_VIEW);
                        }

                    }

                    break;
                case TVBUY_SHOW_GET_VIEW:
                    AppDebug.e(TAG, "TVBUY_SHOW_GET_VIEW");
                    if (mHandler.hasMessages(TVBUY_CALCULATE_SYSTEM_TIME)) {
                        mHandler.removeMessages(TVBUY_CALCULATE_SYSTEM_TIME);
                    }
                    hideAllOtherPop();
                    if (mTvIntegerItem != null) {
                        tvBuyActivity.mCountRashStart = (System.currentTimeMillis() - mTvIntegerItem.beginAt) / 1000;
                        tvBuyActivity.mCountRashMax = (mTvIntegerItem.endAt - mTvIntegerItem.beginAt) / 1000;
                        if (tvBuyActivity.mCountRashMax > 0) {
                            if (isError) {
                                if (pop_get.isShowing()) {
                                    pop_get.dismiss();
                                }
                            } else {
                                if (!pop_get.isShowing()) {
                                    pop_get.showAtLocation(pop_get_view, Gravity.RIGHT, 0, 0);
                                    utPointGet();
                                }
                            }

                            AppDebug.e(TAG, "开抢——————> isError = " + isError);
                            tvBuyActivity.mCountRashTimer.start(true);
                        }

                    }

                    break;
                case TVBUY_SHOW_GET_SUCCESS:
                    AppDebug.e(TAG, "TVBUY_SHOW_GET_SUCCESS");
                    tvBuyActivity.showPopGetSuccess();
                    break;
                case TVBUY_SHOW_GET_FAIL:
                    AppDebug.e(TAG, "TVBUY_SHOW_GET_FAIL");
                    tvBuyActivity.showPopGetFail();
                    break;

                case TVBUY_SEEKTO_PLAY:
                    videoView.seekTo(seekToPosition);
                    getProgressTimer();
                    break;

            }
        }
    }

    /**
     * 详情按钮点击事件点
     */


    private void utDetail(Boolean isGoToCart) {
        Map<String, String> properties = Utils.getProperties();
        if (data1.get(currentVideoNum) != null) {
            properties.put("item_id", data1.get(currentVideoNum).getItemId());
            properties.put("video_id", data1.get(currentVideoNum).getId());
        }

        if (isGoToCart) {
            properties.put("spm", SPMConfig.VIDEO_DETAIL_GOTO_CART);
            Utils.updateNextPageProperties(SPMConfig.VIDEO_DETAIL_GOTO_CART);

        }else {
            properties.put("spm", SPMConfig.VIDEO_DETAIL_ADD);
        }
        Utils.utControlHit(getFullPageName(), "Page_Videodetail_detail_ok", properties);
    }

    /**
     * 开奖倒计时曝光事件
     */

    private void utCount() {
        Map<String, String> properties = Utils.getProperties();
        if (data1 != null) {
            if (data1.get(currentVideoNum) != null) {
                properties.put("item_id", data1.get(currentVideoNum).getItemId());
                properties.put("video_id", data1.get(currentVideoNum).getId());
            }
        }

        properties.put("spm", SPMConfig.VIDEO_DETAIL_FINALTIME_RUNLOTTERY);
        Utils.utCustomHit(getFullPageName(), "Expore_Videodetail_lottocount", properties);
    }

    /**
     * 领取倒计时事件
     */

    private void utPointGet() {
        Map<String, String> properties = Utils.getProperties();
        if (data1 != null) {
            if (data1.get(currentVideoNum) != null) {
                properties.put("item_id", data1.get(currentVideoNum).getItemId());
                properties.put("video_id", data1.get(currentVideoNum).getId());
            }
        }
        properties.put("spm", SPMConfig.VIDEO_DETAIL_FINALTIME_RECEIVELOTTERY);

        Utils.utCustomHit(getFullPageName(), "Expore_Videodetail_getcount", properties);
    }

    /**
     * 积分领取点击事件
     */

    private void utPointClick() {
        Map<String, String> properties = Utils.getProperties();
        if (data1 != null) {
            if (data1.get(currentVideoNum) != null) {
                AppDebug.e(TAG, "utPointClick");
                properties.put("item_id", data1.get(currentVideoNum).getItemId());
                properties.put("video_id", data1.get(currentVideoNum).getId());
            }
        }
        AppDebug.e(TAG, "utPointClick2");

        properties.put("spm", SPMConfig.VIDEO_DETAIL_RECEIVEPIONT);
        Utils.utControlHit(getFullPageName(), "Page_Videodetail_integral", properties);
    }


    /**
     * 获取成功
     */

    private void showPopGetSuccess() {
        if (pop_get.isShowing())
            pop_get.dismiss();
        if (!pop_get_success.isShowing())
            pop_get_success.showAtLocation(pop_get_success_view, Gravity.RIGHT, 0, 0);
        tv_point_value.setText("+" + mTvGetIntegration.points);
        if (nextTvIntegrationItem != null) {
            //如果存在下一波积分,设置时间点
            String tip = showNextIntegrationView(nextTvIntegrationItem);
            tv_point_next.setText(tip);
        } else {
            tv_point_next.setText("");
        }
        mShowEndTimer.start(true);
    }

    /**
     * 显示失败
     */
    private void showPopGetFail() {
        if (pop_get.isShowing())
            pop_get.dismiss();
        if (isError) {
            if (pop_get_fail.isShowing())
                pop_get_fail.dismiss();
        } else {
            if (!pop_get_fail.isShowing())
                pop_get_fail.showAtLocation(pop_get_fail_view, Gravity.RIGHT, 0, 0);
            mShowEndTimer.start(true);

            if (nextTvIntegrationItem != null) {
                //如果存在下一波积分,设置时间点
                String tip = showNextIntegrationView(nextTvIntegrationItem);
                tv_fail_next_time.setText(tip);
            } else {
                tv_fail_next_time.setText("");
            }
        }


    }


    private void initView() {
        popAddTipDialog = new PopAddTipDialog(this);
        pop_detail_view = LayoutInflater.from(this).inflate(R.layout.cytz_good_detail, null);
        pop_detail = new PopupWindow(pop_detail_view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pop_detail.setAnimationStyle(R.style.pop_bottom_animation);
        iv_error = (ImageView) findViewById(R.id.iv_error);
        pop_count_view = LayoutInflater.from(this).inflate(R.layout.cytz_count_view, null);
        pop_count = new PopupWindow(pop_count_view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pop_count.setAnimationStyle(R.style.pop_count_animation);

        pop_get_view = LayoutInflater.from(this).inflate(R.layout.cytz_point_get, null);
        pop_get = new PopupWindow(pop_get_view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pop_get.setAnimationStyle(R.style.pop_get_animation);

        pop_get_success_view = LayoutInflater.from(this).inflate(R.layout.cytz_point_get_success, null);
        pop_get_success = new PopupWindow(pop_get_success_view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pop_get_success.setAnimationStyle(R.style.pop_get_ok_animation);

        pop_get_fail_view = LayoutInflater.from(this).inflate(R.layout.cytz_point_get_fail, null);
        pop_get_fail = new PopupWindow(pop_get_fail_view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pop_get_fail.setAnimationStyle(R.style.pop_get_ok_animation);

        iv_next_video = (ImageView) findViewById(R.id.next_video_iv);
        iv_last_video = (ImageView) findViewById(R.id.last_video_iv);
        pop_get_fail.setAnimationStyle(R.style.pop_add_tip_animation);


        anim_visible = AnimationUtils.loadAnimation(this, R.anim.next_last_show);
        anim_invisible = AnimationUtils.loadAnimation(this, R.anim.next_last_dismiss);

        myProgressbarRelativeLayout = (MyProgressbarRelativeLayout) findViewById(R.id.myProgressbarRelativelayout);
        videoView = (VideoView) findViewById(R.id.videoview);
        videoView.setMediaPlayerType(com.yunos.tv.tvsdk.media.MediaPlayer.SYSTEM_MEDIA_PLAYER);
        btn_left = (ImageButton) findViewById(R.id.ib_left);
        btn_center = (ImageButton) findViewById(R.id.ib_center);
        btn_right = (ImageButton) findViewById(R.id.ib_right);
        ib_next = (ImageView) findViewById(R.id.ib_next);
        ib_last = (ImageView) findViewById(R.id.ib_last);
        progressBar = (ProgressBar) findViewById(R.id.tvbuy_bottom_progress);
        iv_home_play = (ImageView) findViewById(R.id.iv_stopEnterToPlay);
        loading_relativeLayout = (LinearLayout) findViewById(R.id.loading_relativelayout);
        tv_error = (TextView) findViewById(R.id.tv_error);
        //详情pop
        iv_good = (ImageView) pop_detail_view.findViewById(R.id.iv_good_pic);
        ll_original_price = (LinearLayout) pop_detail_view.findViewById(R.id.ll_original_price);
        iv_good_deliveryFee = (ImageView) pop_detail_view.findViewById(R.id.iv_good_deliveryFee);
        tv_originalPrice = (TextView) pop_detail_view.findViewById(R.id.tv_originalPrice);
        tv_originalPrice_tip = (TextView) pop_detail_view.findViewById(R.id.tv_originalPrice_tip);
        tv_goods_title = (TextView) pop_detail_view.findViewById(R.id.tv_goods_title);
        tv_tip_1 = (TextView) pop_detail_view.findViewById(R.id.tv_tip_1);
        tv_tip_2 = (TextView) pop_detail_view.findViewById(R.id.tv_tip_2);
        tv_tip_3 = (TextView) pop_detail_view.findViewById(R.id.tv_tip_3);
        tv_good_price = (TextView) pop_detail_view.findViewById(R.id.tv_good_price);
        tv_good_sold = (TextView) pop_detail_view.findViewById(R.id.tv_good_sold);
        ll_tip_1 = (LinearLayout) pop_detail_view.findViewById(R.id.ll_tip_1);
        ll_tip_2 = (LinearLayout) pop_detail_view.findViewById(R.id.ll_tip_2);
        ll_tip_3 = (LinearLayout) pop_detail_view.findViewById(R.id.ll_tip_3);
        iv_add_bug = (ImageView) pop_detail_view.findViewById(R.id.iv_add_bug);

        ib_right = (ImageButton) findViewById(R.id.ib_right);
        ib_left = (ImageButton) findViewById(R.id.ib_left);

        ll_lastVideo = (LinearLayout) findViewById(R.id.ll_lastVideo);
        ll_nextVideo = (LinearLayout) findViewById(R.id.ll_nextVideo);
        tv_good_name_last = (TextView) findViewById(R.id.tv_good_name_last);
        tv_good_name_next = (TextView) findViewById(R.id.tv_good_name_next);
        iv_good_last = (ImageView) findViewById(R.id.iv_good_last);
        iv_good_next = (ImageView) findViewById(R.id.iv_good_next);
        //pop_detail结束
        tv_count_time = (TextView) pop_count_view.findViewById(R.id.tv_count_time);
        tv_count_bottom = (TextView) pop_count_view.findViewById(R.id.tv_count_bottom);
        //开抢view
        rl_point_get = (RelativeLayout) pop_get_view.findViewById(R.id.rl_point_get);
        arcProgress = (ArcProgress) pop_get_view.findViewById(R.id.arcProgress);
        arcProgress.setMax(103);//此处比100大3为必须

        tv_point_value = (TextView) pop_get_success_view.findViewById(R.id.tv_point_value);
        tv_point_next = (TextView) pop_get_success_view.findViewById(R.id.tv_point_next);
        tv_fail_next_time = (TextView) pop_get_fail_view.findViewById(R.id.tv_fail_next_time);


        mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(this);
        videoView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(Object mp) {
                AppDebug.e(TAG, "视频播放完成");
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                if (pop_bottom.isShowing())
                    pop_bottom.dismiss();
                if (pop_detail.isShowing())
                    pop_detail.dismiss();
                if (pop_top.isShowing())
                    pop_top.dismiss();
                if (currentVideoNum < data1.size() - 1)
                    currentVideoNum++;
                else
                    currentVideoNum = 0;
                playNextVideo();
                requestUpdate();
            }
        });
        videoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(Object mp, int what, int extra) {
                isError = true;
                AppDebug.e(TAG, "视频加载出错 what= " + what + "iserror = " + isError);
                if (mHandler != null) {
                    removeMsg();
                }
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                if (pop_bottom.isShowing())
                    pop_bottom.dismiss();
                if (pop_detail.isShowing())
                    pop_detail.dismiss();
                if (pianwei_pop.isShowing())
                    pianwei_pop.dismiss();
                iv_error.setVisibility(VISIBLE);
                return true;
            }
        });
        videoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(Object mp) {
                isError = false;
                if (iv_error.getVisibility() == VISIBLE) {
                    iv_error.setVisibility(INVISIBLE);
                }
                if (iv_home_play.getVisibility() == VISIBLE)
                    iv_home_play.setVisibility(INVISIBLE);
                AppDebug.e(TAG, "视频准备完成 isrror = " + isError);
                totallength = videoView.getDuration();
                AppDebug.e(TAG, "视频总长度=" + totallength);
                if (totallength > 0) {
                    myProgressbarRelativeLayout.setMaxProgress(totallength);
                    progressBar.setMax(totallength);
                    progressBar.setVisibility(VISIBLE);
                }
                loading_relativeLayout.setVisibility(View.INVISIBLE);
                getProgressTimer();
                videoView.start();
                if (currentPosition != 0) {
                    videoView.seekTo(currentPosition);
                }
                if (!pop_detail.isShowing() && totallength > 0) {
                    if (mHandler.hasMessages(SHOW_BOTTOM_AND_TOP_POP)) {
                        mHandler.removeMessages(SHOW_BOTTOM_AND_TOP_POP);
                    }
                    if (loading_relativeLayout.getVisibility() == View.INVISIBLE)
                        mHandler.sendEmptyMessageDelayed(SHOW_BOTTOM_AND_TOP_POP, 1000);
                }
                utClickLastNext();
            }
        });
        videoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(Object mp, int what, int extra) {

                switch (what) {
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        loading_relativeLayout.setVisibility(VISIBLE);
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        loading_relativeLayout.setVisibility(INVISIBLE);
                        break;
                }

                return true;
            }
        });
    }

    private boolean isError;

    /**
     * 积分领取成功后，如果有下一个积分，穿透下个积分的信息
     *
     * @param nextTvIntegrationItem
     */
    private String showNextIntegrationView(TvIntegration.TvIntegrationItem nextTvIntegrationItem) {
        String nextTime = "";
        long nextBeginAt = nextTvIntegrationItem.beginAt;
        long time = nextBeginAt - System.currentTimeMillis();
        int days = TimeUtil.generateDays(System.currentTimeMillis(), nextBeginAt);
        if (days == 0) {
            //当天
            nextTime = TimeUtil.formateNextIntagTime(time / 1000, String.valueOf(nextBeginAt / 1000));
        } else if (days == 1) {
            nextTime = "下一波明天" + TimeUtil.timeStamp2Date(String.valueOf(nextBeginAt / 1000), "HH:mm") + "继续派送";
            //隔一天
        } else if (days >= 2) {
            //一天后
            nextTime = TimeUtil.timeStamp2Date(String.valueOf(nextBeginAt / 1000), "M月d日HH:mm") + "继续派送";
        }
        AppDebug.e(TAG, "showNextIntegrationView nextTime = " + nextTime);
        return nextTime;

    }


    private TvIntegration.TvIntegrationItem mTvIntegerItem;

    /**
     * 判断是否有积分开抢
     *
     * @return
     */

    private TvIntegration.TvIntegrationItem nextTvIntegrationItem = new TvIntegration.TvIntegrationItem();
    private int mCurentTvInterPosi = -1;

    private boolean loop = true;

    private void isStartCountDown() {
        if (mTvInterItemList != null && mTvInterItemList.size() > 0) {
            for (int i = 0; i < mTvInterItemList.size(); i++) {
                long time = System.currentTimeMillis();
                if (mTvInterItemList.get(i).showAt <= time && time < mTvInterItemList.get(i).beginAt) {
                    AppDebug.e(TAG, " 积分倒计---->时间戳  showAt = " + mTvInterItemList.get(i).showAt + " time = " + time);
                    mTvIntegerItem = mTvInterItemList.get(i);
                    mCurentTvInterPosi = i;
                    loop = false;
                    mHandler.sendEmptyMessage(TVBUY_SHOW_COUNT_DOWN);
                    hasNextPoint(mTvInterItemList, i);
                    break;
                } else if (mTvInterItemList.get(i).beginAt <= time && time < mTvInterItemList.get(i).endAt) {
                    AppDebug.e(TAG, " 积分开抢--->时间戳 " + mTvInterItemList.get(i).toString());
                    mTvIntegerItem = mTvInterItemList.get(i);
                    mCurentTvInterPosi = i;
                    loop = false;
                    mHandler.sendEmptyMessage(TVBUY_SHOW_GET_VIEW);
                    hasNextPoint(mTvInterItemList, i);
                    break;
                }
            }
            if (loop) {
                mHandler.sendEmptyMessage(TVBUY_CALCULATE_SYSTEM_TIME);
            }
        }
    }

    private void hasNextPoint(List<TvIntegration.TvIntegrationItem> tvInterItemList, int i) {
        if ((tvInterItemList.size() - 1) >= (i + 1)) {
            nextTvIntegrationItem = tvInterItemList.get(i + 1);
            AppDebug.e(TAG, "下一个积分倒计时的时间点 time = " +
                    TimeUtil.timeStamp2Date(String.valueOf(nextTvIntegrationItem.beginAt / 1000), null));
        } else {
            nextTvIntegrationItem = null;
            AppDebug.e(TAG, "没有下一个积分");
        }
    }


    /**
     * 开抢前倒计时，倒计时结束，隐藏所有贴片
     */

    private TimePickerUtil mCountDownTimer = new TimePickerUtil(1000) {

        @Override
        public void doOnUIThread() {
            mCountDownStart--;
            //入参数是秒 折算成时、分、秒
            if (isError) {
                if (pop_count.isShowing()) {
                    pop_count.dismiss();
                }
            } else {
                if (!pop_count.isShowing()) {
                    utCount();
                    pop_count.showAtLocation(pop_count_view, Gravity.RIGHT, 0, 0);
                }
            }
            if (mCountDownStart >= 0 && mCountDownStart <= 60)
                tv_count_bottom.setVisibility(View.INVISIBLE);
            String showTime = TimeUtil.formateCountTime(mCountDownStart);
            AppDebug.e(TAG, " isError = " + isError + " TimeUtil time = " + TimeUtil.generateTime(mCountDownStart));
            tv_count_time.setText(showTime);
            if (mCountDownStart <= 0) {
                //此时开抢，隐藏所有的pop
                if (pop_detail.isShowing()) {
                    pop_detail.dismiss();
                }
                if (pop_bottom.isShowing()) {
                    pop_bottom.dismiss();
                }
                if (pop_top.isShowing()) {
                    pop_top.dismiss();
                }
                stop();
                mHandler.sendEmptyMessage(TVBUY_SHOW_GET_VIEW);
            }
        }

    };
    /**
     * 开抢后倒计时
     */

    private TimePickerUtil mCountRashTimer = new TimePickerUtil(1000) {
        @Override
        public void doOnUIThread() {
            mCountRashStart += 1;
            if (isError) {
                if (pop_get.isShowing()) {
                    pop_get.dismiss();
                }
            } else {
                if (!pop_get.isShowing()) {
                    pop_get.showAtLocation(pop_get_view, Gravity.RIGHT, 0, 0);
                    utPointGet();
                }
            }
            try {
                arcProgress.setProgress((int) (mCountRashStart * 100 / mCountRashMax));
            } catch (Exception ex) {

                AppDebug.e(TAG, "arcProgress Exception =  " + ex);
            }


            hideAllOtherPop();
            String showTime = TimeUtil.generateTime(mCountRashStart);
            AppDebug.e(TAG, "isError = " + isError + "  开抢后倒计时 -- showTime = " + showTime);
            if (mCountRashStart >= mCountRashMax) {
                AppDebug.e(TAG, "抢购时间结束");
                stop();
                mHandler.sendEmptyMessage(TVBUY_SHOW_GET_FAIL);
            }
        }
    };


    /**
     * 结束时出现详情贴片
     *
     * @param url
     */

    private TimePickerUtil mShowEndTimer = new TimePickerUtil(1000) {

        @Override
        public void doOnUIThread() {
            mShowEndStart--;
            AppDebug.e(TAG, "角标最后显示倒计时：" + mShowEndStart);
            if (mShowEndStart <= 0) {
                AppDebug.e(TAG, "角标消失——————>");
                if (pop_get_success.isShowing())
                    pop_get_success.dismiss();
                if (pop_get_fail.isShowing())
                    pop_get_fail.dismiss();
                loop = true;
                mShowEndStart = 3;
                stop();
                mHandler.sendEmptyMessage(TVBUY_CALCULATE_SYSTEM_TIME);
                if (mHandler.hasMessages(SHOW_BOTTOM_AND_TOP_POP))
                    mHandler.removeMessages(SHOW_BOTTOM_AND_TOP_POP);
                mHandler.sendEmptyMessageDelayed(SHOW_BOTTOM_AND_TOP_POP, 1000);

            }

        }
    };

    public void hideAllOtherPop() {
        if (pop_top.isShowing())
            pop_top.dismiss();
        if (pop_detail.isShowing())
            pop_detail.dismiss();
        if (pop_bottom.isShowing())
            pop_bottom.dismiss();
        if (pop_count.isShowing())
            pop_count.dismiss();
        if (myProgressbarRelativeLayout.getVisibility() == View.VISIBLE)
            myProgressbarRelativeLayout.setVisibility(View.INVISIBLE);
        if (pianwei_pop.isShowing())
            pianwei_pop.dismiss();

    }


    public void requestTvBuyItems(String url) {
        ZhuanTiBusinessRequest.getBusinessRequest().requestTvBuyItems(this, url,
                new GetTvBuyItemsRequestListener(new WeakReference<BaseActivity>(this)));
    }

    private static final int HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL = 105;

    private class GetTvBuyItemsRequestListener extends BizRequestListener<TvBuyItems> {

        public GetTvBuyItemsRequestListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            tv_error.setVisibility(VISIBLE);
            return true;
        }

        @Override
        public void onSuccess(TvBuyItems data) {
            if (data != null) {
                if (tv_error.getVisibility() == VISIBLE)
                    tv_error.setVisibility(INVISIBLE);
                TvBuyActivity tvBuyActivity = (TvBuyActivity) mBaseActivityRef.get();
                AppDebug.e(tvBuyActivity.TAG, "data " + data.toString());
                data1 = data.getData();
                if (data1.size() == 1) {
                    flag = true;
                } else {
                    flag = false;
                }
                requestGoodDetail(data1.get(currentVideoNum).getItemId());
                playVideo();
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return true;
        }
    }

    private void playVideo() {
        videoView.setVideoPath(data1.get(currentVideoNum).getVideo());
        //videoView.start();
    }

    private void stopVideoPlay() {
        if (videoView != null)
            videoView.stopPlayback();
        if (mHandler != null && mHandler.hasMessages(UPDATE_VIDEO_POSITION_ONE_SECOND))
            mHandler.removeMessages(UPDATE_VIDEO_POSITION_ONE_SECOND);
        if (pop_bottom != null && pop_bottom.isShowing())
            pop_bottom.dismiss();
        if (pop_top != null && pop_top.isShowing())
            pop_top.dismiss();
        if (pianwei_pop != null && pianwei_pop.isShowing())
            pianwei_pop.dismiss();
        finish();
    }

    private int[] msg = new int[]{UPDATE_VIDEO_POSITION_ONE_SECOND, SHOW_BOTTOM_AND_TOP_POP,
            DISMISS_TOP_POP, SET_LEFT_RIGHT_IMAGE_BUTTON_ORIGIN, KUAIJIN_KUAITUI_MESSAGE, HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL, TVBUY_SEEKTO_PLAY};

    private void removeMsg() {
        for (int i = 0; i < msg.length; i++) {
            if (mHandler.hasMessages(msg[i]))
                mHandler.removeMessages(msg[i]);
        }
    }

    private void playNextVideo() {
        videoView.pause();
//        videoView.release();
        if (timer != null) {
            timer.cancel();
            timer = null;

        }
        if (mHandler != null) {
            removeMsg();
        }
        AppDebug.e(TAG, "播放下一个视频");
        seekToPosition = 0;
        currentPosition = 0;
        canAddBug = true;//初始化是否显示加购图片参数
        isGotoBug = false;//初始化是否显示去购物车图片参数
        requestGoodDetail(data1.get(currentVideoNum).getItemId());
        AppDebug.e(TAG, "播放下一个视频  url=" + data1.get(currentVideoNum).getVideo());
        loading_relativeLayout.setVisibility(VISIBLE);
        if (iv_home_play.getVisibility() == VISIBLE)
            iv_home_play.setVisibility(INVISIBLE);
        videoView.setVideoPath(data1.get(currentVideoNum).getVideo());
        myProgressbarRelativeLayout.setVisibility(View.INVISIBLE);

        if (iv_next_video.getVisibility() == VISIBLE)
            iv_next_video.setVisibility(INVISIBLE);
        if (iv_last_video.getVisibility() == VISIBLE)
            iv_last_video.setVisibility(INVISIBLE);
        if (pop_detail.isShowing())
            pop_detail.dismiss();
        if (pianwei_pop.isShowing())
            pianwei_pop.dismiss();
        if (pop_top.isShowing())
            pop_top.dismiss();
    }

    private void pauseVideoPlay() {
        videoView.pause();
        iv_home_play.setVisibility(VISIBLE);
        btn_center.setPressed(false);
        if (mHandler.hasMessages(UPDATE_VIDEO_POSITION_ONE_SECOND))
            mHandler.removeMessages(UPDATE_VIDEO_POSITION_ONE_SECOND);
        if (timer != null)
            timer.cancel();
        timer = null;
    }

    private void getProgressTimer() {
        if (timer == null)
            timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mHandler != null)
                    mHandler.sendEmptyMessage(UPDATE_VIDEO_POSITION_ONE_SECOND);

            }
        }, 0, 1000);

    }

    private void initPop() {
        bottom_view = LayoutInflater.from(this).inflate(R.layout.bottom_good_pop, null);
        pop_bottom = new PopupWindow(bottom_view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pop_bottom.setAnimationStyle(R.style.pop_bottom_animation);
        top_view = LayoutInflater.from(this).inflate(R.layout.top_title_pop, null);
        pop_top = new PopupWindow(top_view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pop_top.setAnimationStyle(R.style.pop_top_animation);


        pianwei_view = LayoutInflater.from(this).inflate(R.layout.pianwei_pop, null);
        pianwei_pop = new PopupWindow(pianwei_view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pianwei_pop.setAnimationStyle(R.style.pianwie_pop_animation);


        setGoodBottomView();

    }

    private void setGoodBottomView() {
        tv_top_pop_video_name = (TextView) top_view.findViewById(R.id.tv_name_pop_top);
        tv_bottom_pop_video_name = (TextView) bottom_view.findViewById(R.id.tv_name_pop_bottom);
        tv_bottom_pop_price = (TextView) bottom_view.findViewById(R.id.tvbuy_good_bottom_price);
        iv_bottom_pop_good_picture = (ImageView) bottom_view.findViewById(R.id.iv_good_pop_bottom);
    }

    private void showBottomPop() {
        if (mGoodDetail == null)
            return;
        if (mGoodDetail.getTitle() != null && !mGoodDetail.getTitle().equals(""))
            tv_bottom_pop_video_name.setText(mGoodDetail.getTitle());
        if (mGoodDetail.getPrice()!=null&&!mGoodDetail.getPrice().equals("")) {

            tv_bottom_pop_price.setText(StringUtil.formatPriceToSpan(TvBuyActivity.this, mGoodDetail.price));

        }
        if (!TextUtils.isEmpty(mGoodDetail.picsPath))
            mImageLoaderManager.loadImage(mGoodDetail.picsPath, iv_bottom_pop_good_picture, null);
        pop_bottom.showAtLocation(bottom_view, Gravity.BOTTOM, 0, 0);
    }

    private void showTitlePop() {

        if (mGoodDetail == null) {
            return;
        } else {
            tv_top_pop_video_name.setText(data1.get(currentVideoNum).getName());
            pop_top.showAtLocation(top_view, Gravity.TOP, 0, 0);
        }
    }

    private static final int KUAIJIN_KUAITUI_MESSAGE = 104;
    private static final int SET_LEFT_RIGHT_IMAGE_BUTTON_ORIGIN = 103;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (iv_error.getVisibility() == VISIBLE) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == 111) {
                    stopVideoPlay();
                }
                return true;
            }
            if (loading_relativeLayout.getVisibility() == VISIBLE && !videoView.isPlaying()) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == 111) {
                    stopVideoPlay();
                }
                return true;
            }
            if (mGoodDetail == null && myProgressbarRelativeLayout.getVisibility() == INVISIBLE && (videoView.isPlaying() || videoView.isPause())) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    myProgressbarRelativeLayout.showOrHide(true);
                    ll_nextVideo.setVisibility(INVISIBLE);
                    ll_lastVideo.setVisibility(INVISIBLE);
                    if (videoView.isPlaying()) {
                        pauseVideoPlay();
                    } else {
                        videoView.start();
                        iv_home_play.setVisibility(View.INVISIBLE);
                        btn_center.setPressed(true);
                        getProgressTimer();
                        mHandler.sendEmptyMessageDelayed(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL, 5000);
                    }
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    getShowProgressControllerLayout();
                    myProgressbarRelativeLayout.showOrHide(true);
//                    ll_nextVideo.setVisibility(INVISIBLE);
//                    ll_lastVideo.setVisibility(INVISIBLE);


                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    getShowProgressControllerLayout();
                    myProgressbarRelativeLayout.showOrHide(true);
//                    ll_nextVideo.setVisibility(INVISIBLE);
//                    ll_lastVideo.setVisibility(INVISIBLE);
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == 111) {
                    stopVideoPlay();
                }
                return true;
            }
            if (pop_get.isShowing()) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    AppDebug.e(TAG, "抢积分");
                    loginType = 1;
                    utPointClick();
                    AppDebug.e(TAG, "pop_get  id = " + mTvIntegerItem.id);
                    ZhuanTiBusinessRequest.getBusinessRequest().requestGetVideoPointRequest(mTvIntegerItem.pointSchemeId,
                            new GetVideoPointListener(new WeakReference<BaseActivity>(TvBuyActivity.this)));
                    return true;
                }
            }

            if (pop_detail.isShowing()) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (canAddBug) {
                        if (isGotoBug) {

                            utDetail(true);
                            AppDebug.e(TAG, "去购物车");
                            //去购物车
                            loginType = 2;
                            Intent mIntent = new Intent();
                            mIntent.setData(Uri.parse("tvtaobao://home?module=cart&notshowloading=true"));
                            startActivity(mIntent);
                            return true;
                        } else {
                            //加购

                            utDetail(false);
                            loginType = 0;
                            AppDebug.e(TAG, "加购");
                            if (mGoodDetail != null) {
                                ZhuanTiBusinessRequest.getBusinessRequest().requestAddCartRequest(data1.get(currentVideoNum).getItemId(), 1, mGoodDetail.skuId, buildAddBagExParams(),
                                        new GetAddCartRequestListener(new WeakReference<BaseActivity>(TvBuyActivity.this)) {
                                        });
                            }
                            return true;
                        }
                    } else {
                        AppDebug.e(TAG, "图片抖动");
                        AnimationUtil.startShakeByPropertyAnim(iv_add_bug, 10f, 1000);
                        return true;
                    }

                } else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == 111) {
                    if (pop_detail.isShowing())
                        pop_detail.dismiss();
                    if (!pop_bottom.isShowing())
                        showBottomPop();
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                    //上一个商品,下个迭代做
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                    //下一个商品,下个迭代做
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    if (mHandler.hasMessages(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL))
                        mHandler.removeMessages(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL);

                    if (mHandler.hasMessages(DISMISS_TOP_POP))
                        mHandler.removeMessages(DISMISS_TOP_POP);

                    getShowProgressControllerLayout();
                    if (pop_detail.isShowing()) {
                        pop_detail.dismiss();
                        mHandler.sendEmptyMessageDelayed(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL, 5000);
                    }

                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    if (mHandler.hasMessages(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL))
                        mHandler.removeMessages(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL);
                    getShowProgressControllerLayout();
                    if (pop_detail.isShowing()) {
                        pop_detail.dismiss();
                        mHandler.sendEmptyMessageDelayed(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL, 5000);
                    }
                }
                if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP||event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN){
                    myProgressbarRelativeLayout.setVisibility(INVISIBLE);
                    return false;
                }
                myProgressbarRelativeLayout.hideControl(false);
                myProgressbarRelativeLayout.setControlImageview(true, this);
                if (pop_detail.isShowing())
                    pop_detail.dismiss();
                if (myProgressbarRelativeLayout.getVisibility() == View.INVISIBLE) {
                    myProgressbarRelativeLayout.setVisibility(VISIBLE);
                }
                if (ll_lastVideo.getVisibility() == VISIBLE)
                    ll_lastVideo.setVisibility(INVISIBLE);
                if (ll_nextVideo.getVisibility() == VISIBLE)
                    ll_nextVideo.setVisibility(INVISIBLE);

                if (iv_next_video.getVisibility() == VISIBLE)
                    iv_next_video.setVisibility(INVISIBLE);
                if (iv_last_video.getVisibility() == VISIBLE)
                    iv_last_video.setVisibility(INVISIBLE);
                mHandler.sendEmptyMessageDelayed(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL, 5000);
                return true;
            }
            if (pop_bottom.isShowing()) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    utClickBottomPop();
                    pop_bottom.dismiss();
                    showGoodsDetail(mGoodDetail);
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == 111) {
                    stopVideoPlay();
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    getShowProgressControllerLayout();
                    pop_bottom.dismiss();

                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    getShowProgressControllerLayout();
                    pop_bottom.dismiss();
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP || event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    return false;
                }

                return true;
            }
            if (myProgressbarRelativeLayout.getVisibility() == VISIBLE && ib_next.hasFocus() && videoView.isPlaying()) {

                if (mHandler.hasMessages(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL))
                    mHandler.removeMessages(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL);
                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    utClickProgressBar("next");
                    if (currentVideoNum < data1.size() - 1) {
                        currentVideoNum += 1;
                    } else {
                        currentVideoNum = 0;
                    }
                    playNextVideo();
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    utFocusNextLast("beforevideo");
                    ib_last.requestFocus();
                    startAnimation(iv_last_video);
                    if (iv_next_video.getVisibility() == VISIBLE)
                        endAnimation(iv_next_video);
                    setNextAndLastVideoMsg(1, data1);
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                    myProgressbarRelativeLayout.hideControl(false);
                    myProgressbarRelativeLayout.setControlImageview(true, this);
                    if (videoView.isPlaying())
                        btn_center.setPressed(true);
                    else
                        btn_center.setPressed(false);
                    ib_next.clearFocus();
                    endAnimation(iv_next_video);
                    ll_nextVideo.setVisibility(View.INVISIBLE);
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == 111) {
                    mHandler.sendEmptyMessage(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL);
                    return true;
                }

                mHandler.sendEmptyMessageDelayed(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL, 5000);
            }
            if (myProgressbarRelativeLayout.getVisibility() == VISIBLE && ib_last.hasFocus() && !videoView.isPlaying()) {
                if (mHandler.hasMessages(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL))
                    mHandler.removeMessages(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL);
                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    utClickProgressBar("before");
                    if (currentVideoNum - 1 >= 0) {
                        currentVideoNum--;
                    } else {
                        currentVideoNum = data1.size() - 1;
                    }
                    playNextVideo();
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    utFocusNextLast("nxetvideo");
                    ib_next.requestFocus();
                    startAnimation(iv_next_video);
                    if (iv_last_video.getVisibility() == VISIBLE)
                        endAnimation(iv_last_video);
                    setNextAndLastVideoMsg(2, data1);
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                    myProgressbarRelativeLayout.hideControl(false);
                    myProgressbarRelativeLayout.setControlImageview(true, this);
                    if (videoView.isPlaying())
                        btn_center.setPressed(true);
                    else
                        btn_center.setPressed(false);
                    ib_last.clearFocus();
                    endAnimation(iv_last_video);
                    ll_lastVideo.setVisibility(View.INVISIBLE);
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == 111) {
                    mHandler.sendEmptyMessage(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL);
                    return true;
                }
                return true;
            }
            if (myProgressbarRelativeLayout.getVisibility() == VISIBLE && ib_next.hasFocus() && !videoView.isPlaying()) {
                if (mHandler.hasMessages(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL))
                    mHandler.removeMessages(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL);
                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    utClickProgressBar("next");
                    if (currentVideoNum < data1.size() - 1) {
                        currentVideoNum += 1;
                    } else {
                        currentVideoNum = 0;
                    }
                    playNextVideo();
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    utFocusNextLast("beforevideo");
                    ib_last.requestFocus();
                    startAnimation(iv_last_video);
                    if (iv_next_video.getVisibility() == VISIBLE)
                        endAnimation(iv_next_video);
                    setNextAndLastVideoMsg(1, data1);
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                    myProgressbarRelativeLayout.hideControl(false);
                    myProgressbarRelativeLayout.setControlImageview(true, this);
                    if (videoView.isPlaying())
                        btn_center.setPressed(true);
                    else
                        btn_center.setPressed(false);
                    ib_next.clearFocus();
                    endAnimation(iv_next_video);
                    ll_nextVideo.setVisibility(View.INVISIBLE);

                } else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == 111) {
                    mHandler.sendEmptyMessage(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL);
                    return true;
                }
                return true;
            }
            if (myProgressbarRelativeLayout.getVisibility() == VISIBLE && ib_last.hasFocus() && videoView.isPlaying()) {
                if (mHandler.hasMessages(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL))
                    mHandler.removeMessages(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL);
                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    utClickProgressBar("before");
                    if (currentVideoNum - 1 >= 0) {
                        currentVideoNum--;
                    } else {
                        currentVideoNum = data1.size() - 1;
                    }
                    playNextVideo();
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    utFocusNextLast("nxetvideo");
                    ib_next.requestFocus();
                    startAnimation(iv_next_video);
                    if (iv_last_video.getVisibility() == VISIBLE)
                        endAnimation(iv_last_video);
                    setNextAndLastVideoMsg(2, data1);
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                    myProgressbarRelativeLayout.hideControl(false);
                    myProgressbarRelativeLayout.setControlImageview(true, this);
                    if (videoView.isPlaying())
                        btn_center.setPressed(true);
                    else
                        btn_center.setPressed(false);
                    ib_last.clearFocus();
                    endAnimation(iv_last_video);
                    ll_lastVideo.setVisibility(View.INVISIBLE);
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == 111) {
                    mHandler.sendEmptyMessage(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL);
                    return true;
                }

                mHandler.sendEmptyMessageDelayed(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL, 5000);
            }


            if (myProgressbarRelativeLayout.getVisibility() == VISIBLE && !videoView.isPlaying()) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == 111) {
                    if (pop_top.isShowing())
                        pop_top.dismiss();
                    if (pop_bottom.isShowing())
                        pop_bottom.dismiss();
                    if (myProgressbarRelativeLayout.getVisibility() == VISIBLE)
                        myProgressbarRelativeLayout.showOrHide(false);
                    if (pop_detail.isShowing())
                        pop_detail.dismiss();
                    return true;
                }
            }
            if (iv_home_play.getVisibility() == VISIBLE && myProgressbarRelativeLayout.getVisibility() == View.INVISIBLE) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == 111) {
                    stopVideoPlay();
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    videoView.start();
                    iv_home_play.setVisibility(View.INVISIBLE);
                    getProgressTimer();
                    mHandler.sendEmptyMessageDelayed(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL, 1000);
                    return true;
                }
            }
            if (myProgressbarRelativeLayout.getVisibility() == VISIBLE && !ib_last.hasFocus() && !ib_next.hasFocus() && !flag) {
                if (mHandler.hasMessages(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL))
                    mHandler.removeMessages(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL);
                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (mHandler.hasMessages(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL))
                        mHandler.removeMessages(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL);
                    if (videoView.isPlaying()) {
                        pauseVideoPlay();
                        utClickProgressBar("stop");
                    } else {
                        utClickProgressBar("start");
                        videoView.start();
                        iv_home_play.setVisibility(View.INVISIBLE);
                        btn_center.setPressed(true);
                        getProgressTimer();
                        mHandler.sendEmptyMessageDelayed(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL, 5000);
                    }
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    utClickProgressBar("left");
                    //快退
                    mHandler.removeMessages(UPDATE_VIDEO_POSITION_ONE_SECOND);
                    if (timer != null) {
                        timer.cancel();
                        timer.purge();
                        timer = null;
                    }
                    if (pianwei_pop.isShowing())
                        pianwei_pop.dismiss();
                    ib_left.setPressed(true);
                    if (mHandler.hasMessages(SET_LEFT_RIGHT_IMAGE_BUTTON_ORIGIN))
                        mHandler.removeMessages(SET_LEFT_RIGHT_IMAGE_BUTTON_ORIGIN);
                    ib_left.setPressed(true);
                    mHandler.sendEmptyMessageDelayed(SET_LEFT_RIGHT_IMAGE_BUTTON_ORIGIN, 100);
                    if (mHandler.hasMessages(HIDE_PIANTOU_PIANWEI))
                        mHandler.removeMessages(HIDE_PIANTOU_PIANWEI);
//                    if (seekToPosition > currentPosition) {
//                        seekToPosition = currentPosition;
//                    }else if (seekToPosition <=currentPosition){
//                        seekToPosition = currentPosition;
//                    }
                    seekToPosition = currentPosition;
                    if (seekToPosition > 0) {
                        seekToPosition = seekToPosition - 5000;
                        currentPosition = seekToPosition;
                    }
                    if (seekToPosition <= 0) {
                        seekToPosition = 0;
                        currentPosition = 0;
                        mHandler.sendEmptyMessageDelayed(HIDE_PIANTOU_PIANWEI, 500);
                    }
                    setMyProgress(seekToPosition);
                    if (mHandler.hasMessages(TVBUY_SEEKTO_PLAY))
                        mHandler.removeMessages(TVBUY_SEEKTO_PLAY);
                    mHandler.sendEmptyMessageDelayed(TVBUY_SEEKTO_PLAY, 500);
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    utClickProgressBar("right");
                    //快进
                    if (mHandler.hasMessages(UPDATE_VIDEO_POSITION_ONE_SECOND))
                        mHandler.removeMessages(UPDATE_VIDEO_POSITION_ONE_SECOND);
                    if (timer != null) {
                        timer.cancel();
                        timer.purge();
                        timer = null;
                    }
                    if (mHandler.hasMessages(SET_LEFT_RIGHT_IMAGE_BUTTON_ORIGIN))
                        mHandler.removeMessages(SET_LEFT_RIGHT_IMAGE_BUTTON_ORIGIN);
                    ib_right.setPressed(true);
                    mHandler.sendEmptyMessageDelayed(SET_LEFT_RIGHT_IMAGE_BUTTON_ORIGIN, 100);
                    if (mHandler.hasMessages(HIDE_PIANTOU_PIANWEI))
                        mHandler.removeMessages(HIDE_PIANTOU_PIANWEI);
                    if (currentPosition >= totallength - 10000 && currentPosition <= totallength - 5000) {
                        currentPosition = totallength - 5000;
                        //显示片尾
                        if (!pop_get.isShowing()) {
                            pianwei_pop.showAtLocation(videoView, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 0);
                            mHandler.sendEmptyMessageDelayed(HIDE_PIANTOU_PIANWEI, 500);
                        }
                    }
                    if (seekToPosition < currentPosition)
                        seekToPosition = currentPosition;
                    if (seekToPosition < totallength - 10000) {
                        seekToPosition = seekToPosition + 5000;
                    }
                    setMyProgress(seekToPosition);
                    if (mHandler.hasMessages(TVBUY_SEEKTO_PLAY))
                        mHandler.removeMessages(TVBUY_SEEKTO_PLAY);
                    mHandler.sendEmptyMessageDelayed(TVBUY_SEEKTO_PLAY, 500);

                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                    utFocusNextLast("nxetvideo");
                    ib_next.requestFocus();
                    startAnimation(iv_next_video);
                    myProgressbarRelativeLayout.hideControl(true);
                    myProgressbarRelativeLayout.setControlImageview(false, this);
                    //绑定商品数据
                    setNextAndLastVideoMsg(2, data1);
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == 111) {
                    mHandler.sendEmptyMessage(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL);
                    return true;
                }
                mHandler.sendEmptyMessageDelayed(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL, 5000);
                return true;
            }

            if (myProgressbarRelativeLayout.getVisibility() == VISIBLE && !ib_last.hasFocus() && !ib_next.hasFocus() && flag) {
                if (mHandler.hasMessages(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL))
                    mHandler.removeMessages(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL);
                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (mHandler.hasMessages(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL))
                        mHandler.removeMessages(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL);
                    if (videoView.isPlaying()) {
                        pauseVideoPlay();
                        utClickProgressBar("stop");
                    } else {
                        utClickProgressBar("start");
                        videoView.start();
                        iv_home_play.setVisibility(View.INVISIBLE);
                        btn_center.setPressed(true);
                        getProgressTimer();
                        mHandler.sendEmptyMessageDelayed(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL, 5000);
                    }
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP || event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    return false;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    utClickProgressBar("left");
                    //快退
                    mHandler.removeMessages(UPDATE_VIDEO_POSITION_ONE_SECOND);
                    if (timer != null) {
                        timer.cancel();
                        timer.purge();
                        timer = null;
                    }
                    if (pianwei_pop.isShowing())
                        pianwei_pop.dismiss();
                    ib_left.setPressed(true);
                    if (mHandler.hasMessages(SET_LEFT_RIGHT_IMAGE_BUTTON_ORIGIN))
                        mHandler.removeMessages(SET_LEFT_RIGHT_IMAGE_BUTTON_ORIGIN);
                    ib_left.setPressed(true);
                    mHandler.sendEmptyMessageDelayed(SET_LEFT_RIGHT_IMAGE_BUTTON_ORIGIN, 100);
                    if (mHandler.hasMessages(HIDE_PIANTOU_PIANWEI))
                        mHandler.removeMessages(HIDE_PIANTOU_PIANWEI);
                    seekToPosition = currentPosition;
                    if (seekToPosition > 0) {
                        seekToPosition = seekToPosition - 5000;
                        currentPosition = seekToPosition;
                    }
                    if (seekToPosition <= 0) {
                        seekToPosition = 0;
                        currentPosition = 0;
                        mHandler.sendEmptyMessageDelayed(HIDE_PIANTOU_PIANWEI, 500);
                    }
                    setMyProgress(seekToPosition);
                    if (mHandler.hasMessages(TVBUY_SEEKTO_PLAY))
                        mHandler.removeMessages(TVBUY_SEEKTO_PLAY);
                    mHandler.sendEmptyMessageDelayed(TVBUY_SEEKTO_PLAY, 500);
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    utClickProgressBar("right");
                    //快进
                    if (mHandler.hasMessages(UPDATE_VIDEO_POSITION_ONE_SECOND))
                        mHandler.removeMessages(UPDATE_VIDEO_POSITION_ONE_SECOND);
                    if (timer != null) {
                        timer.cancel();
                        timer.purge();
                        timer = null;
                    }
                    if (mHandler.hasMessages(SET_LEFT_RIGHT_IMAGE_BUTTON_ORIGIN))
                        mHandler.removeMessages(SET_LEFT_RIGHT_IMAGE_BUTTON_ORIGIN);
                    ib_right.setPressed(true);
                    mHandler.sendEmptyMessageDelayed(SET_LEFT_RIGHT_IMAGE_BUTTON_ORIGIN, 100);
                    if (mHandler.hasMessages(HIDE_PIANTOU_PIANWEI))
                        mHandler.removeMessages(HIDE_PIANTOU_PIANWEI);
                    if (currentPosition >= totallength - 10000 && currentPosition <= totallength - 5000) {
                        currentPosition = totallength - 5000;
                        //显示片尾
                        if (!pop_get.isShowing()) {
                            pianwei_pop.showAtLocation(videoView, Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 0);
                            mHandler.sendEmptyMessageDelayed(HIDE_PIANTOU_PIANWEI, 500);
                        }
                    }
                    if (seekToPosition < currentPosition)
                        seekToPosition = currentPosition;
                    if (seekToPosition < totallength - 10000) {
                        seekToPosition = seekToPosition + 5000;
                    }
                    setMyProgress(seekToPosition);
                    if (mHandler.hasMessages(TVBUY_SEEKTO_PLAY))
                        mHandler.removeMessages(TVBUY_SEEKTO_PLAY);
                    mHandler.sendEmptyMessageDelayed(TVBUY_SEEKTO_PLAY, 500);

                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == 111) {
                    mHandler.sendEmptyMessage(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL);
                    return true;
                }
                mHandler.sendEmptyMessageDelayed(HIDE_PROGRESSBAR_SHOW_GOOD_DETAIL, 5000);
                return true;
            }


        }
        return super.dispatchKeyEvent(event);
    }

    private void getShowProgressControllerLayout() {
        if (myProgressbarRelativeLayout.getVisibility() == View.INVISIBLE) {

            if (ll_lastVideo.getVisibility() == VISIBLE)
                ll_lastVideo.setVisibility(View.INVISIBLE);
            if (ll_nextVideo.getVisibility() == VISIBLE)
                ll_nextVideo.setVisibility(View.INVISIBLE);
            btn_center.setPressed(true);
            iv_next_video.setVisibility(INVISIBLE);
            iv_last_video.setVisibility(INVISIBLE);
            myProgressbarRelativeLayout.hideLastNextBtn(flag);
            myProgressbarRelativeLayout.setVisibility(VISIBLE);
        }
        if (!pop_top.isShowing())
            showTitlePop();
        if (progressBar.getVisibility() == VISIBLE)
            progressBar.setVisibility(View.INVISIBLE);
        utShowProgressBar();
    }


    /**
     * 自定义淘客详情页打点
     */
    private void anaylisysTaoke() {
        if (CoreApplication.getLoginHelper(this).isLogin()) {

            AppDebug.e(TAG, "isLogin-->");
            long historyTime = SharePreUtil.getTaoKeLogin(TvBuyActivity.this);
            long currentTime = System.currentTimeMillis();
            AppDebug.e(TAG, "historyTime = " + historyTime + " currentTime = " + currentTime);
            if (currentTime > historyTime) {
                AppDebug.e(TAG, "isNotLogin--->");
                ZhuanTiBusinessRequest.getBusinessRequest().requestTaokeLoginAnalysis(User.getNick(), new TaokeBussinessRequestListener(new WeakReference<BaseActivity>(this)));
            }
            String sellerId = mGoodDetail.seller + "";
            String ShopType = mGoodDetail.type;
            AppDebug.d(TAG, "anaylisysTaoke User.sellerId " + sellerId);
            if (!TextUtils.isEmpty(sellerId)) {
                String stbId = DeviceUtil.initMacAddress(this);
                ZhuanTiBusinessRequest.getBusinessRequest().requestTaokeDetailAnalysis(stbId, User.getNick(), mGoodDetail.itemId, ShopType, sellerId, new TaokeBussinessRequestListener2(new WeakReference<BaseActivity>(this)));
            }
        }
    }

    /**
     * 淘客登录打点监听
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
            AppDebug.e(TAG, "onSuccess__>");
            long historyTime = System.currentTimeMillis() + 604800000;//7天
            SharePreUtil.saveTvBuyTaoKe(TvBuyActivity.this, historyTime);

        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    /**
     * 第二个打点监听
     */
    private class TaokeBussinessRequestListener2 extends BizRequestListener<JSONObject> {

        public TaokeBussinessRequestListener2(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return true;
        }

        @Override
        public void onSuccess(JSONObject data) {


        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    private void onHandlerReuqstV6(TBDetailResultV6 tBetailResultVO) {
        mGoodDetail = new GoodDetail();
        if (tBetailResultVO == null) {
            return;
        }

        //商品id
        String itemId = tBetailResultVO.getItem().getItemId();
        mGoodDetail.itemId = itemId;

        mGoodDetail.seller = Long.parseLong(tBetailResultVO.getSeller().getUserId());

        mGoodDetail.type = tBetailResultVO.getSeller().getSellerType();

        //月销量
        mGoodDetail.sold = "0";
        Unit unit = null;
        if (DetailV6Utils.getUnit(tbDetailResultV6) != null) {
            unit = DetailV6Utils.getUnit(tbDetailResultV6);
        }
        if (tBetailResultVO != null && unit != null && unit.getItem() != null && !TextUtils.isEmpty(unit.getItem().getSellCount())) {
            mGoodDetail.sold = StringUtil.formatValue(unit.getItem().getSellCount() + "");
        }
        // 判断是否可以加入购物车
        //TODO 一键加购功能需要考虑是否能直接加购，特殊商品不能，至于哪些不能，产品也不清楚。

        if (tbDetailResultV6.getApiStack() != null) {
            if (unit != null && unit.getTrade() != null) {
                if (unit.getTrade().getCartEnable().equals("true")) {
                    mGoodDetail.canAddBug = true;
                } else {
                    mGoodDetail.canAddBug = false;
                }
            } else {
                AppDebug.e("商品不能购买", "不能购买");
                mGoodDetail.canAddBug = false;
            }
        } else {
            MockData mockdata = DetailV6Utils.getMockdata(tbDetailResultV6);
            if (mockdata != null && mockdata.getTrade() != null) {
                if (mockdata.getTrade().isCartEnable()) {
                    mGoodDetail.canAddBug = true;
                } else {
                    mGoodDetail.canAddBug = false;
                }
            } else {
                AppDebug.e("飞猪商品不能购买", "不能购买");
                mGoodDetail.canAddBug = false;
            }
        }
        AppDebug.e(TAG, "canAddBug = " + canAddBug);
        canAddBug = mGoodDetail.canAddBug;
        //判断不能加入购物车的情况是不是因为未开售
        // TODO 6.0接口不能判断商品未开售情况，改逻辑基于新的架构下，需要产品和UED重新规划UI
        //聚划算商品， "status":0, //聚划算状态，0:即将开始，1：可购买，2：有占座，3：卖光了，4：团购已结束
        if (unit.getVertical() != null && unit.getVertical().getJhs() != null && unit.getVertical().getJhs().getStatus() != null) {
            String status = unit.getVertical().getJhs().getStatus();
            if (status.equals("0")) {
                mGoodDetail.isStart = false;
            }
        }

        //预售商品，fasle表示未开团，true表示抢光了。
        if (unit.getVertical() != null && unit.getVertical().getPresale() != null && unit.getVertical().getPresale().getStatus() != null) {
            String status = unit.getVertical().getPresale().getStatus();
            if (status.equals("1")) {
                mGoodDetail.isStart = false;
            } else if (status.equals("2")) {
                mGoodDetail.isStart = true;
            }
        }
        // 商品标题
        String title = "";
        if (unit != null && unit.getItem() != null && unit.getItem().getTitle() != null) {
            title = unit.getItem().getTitle();
        }
        if (TextUtils.isEmpty(title)) {
            title = tBetailResultVO.getItem().getTitle();
        }

        mGoodDetail.title = title;
        if (unit.getPrice() != null) {
            //现价
            if (unit.getPrice().getPrice() != null) {
                if (unit.getPrice().getPrice().getPriceText() != null) {
                    String price = unit.getPrice().getPrice().getPriceText();
                    AppDebug.e(TAG, "price = " + price);
                    mGoodDetail.price = StringUtil.formatPrice(price);
                }
            }
            //原价
            //TODO subprice 主价格右侧的附属价格,
            String originalPrice = "";
            if (unit.getPrice().getSubPrice() != null) {
                if (unit.getPrice().getSubPrice().getPriceText() != null) {
                    originalPrice = unit.getPrice().getSubPrice().getPriceText();
                }
            }
            if (TextUtils.isEmpty(originalPrice)) {
                //TODO 额外的价格
                if (unit.getPrice().getExtraPrices() != null) {
                    if (unit.getPrice().getExtraPrices().size() > 0 && unit.getPrice().getExtraPrices().get(0) != null &&
                            unit.getPrice().getExtraPrices().get(0).getPriceTitle() != null) {
                        originalPrice = unit.getPrice().getExtraPrices().get(0).getPriceText();
                    }
                }
            }
            mGoodDetail.originalPrice = StringUtil.formatPrice(originalPrice);

            if (unit != null && unit.getVertical().getPresale() != null) {
                //预售商品
                isPre = true;

            }
            if(unit != null){
                if(unit.getPrice()!=null){
                    if(unit.getPrice().getPrice()!=null){
                        nowPrice = unit.getPrice().getPrice().getPriceText();
                    }
                }
            }
            ZhuanTiBusinessRequest.getBusinessRequest().requestProductTag(itemId, ActivityPathRecorder.getInstance().getCurrentPath(this),
                    isZTC, source, isPre, nowPrice, this,new GetProductTagListener(new WeakReference<BaseActivity>(this)));


        }


        // 快递费用
        //TODO 此处快递文案取值不靠谱，需产品规划，UED考虑是否取消直接用图的不靠谱方案
        String deliveryFee = "";
        if (unit != null) {
            if (unit.getDelivery() != null && unit.getDelivery().getPostage() != null) {
                deliveryFee = unit.getDelivery().getPostage();
                if (deliveryFee.equals("快递: 0.00")) {
                    deliveryFee = "免运费";
                } else if (deliveryFee.contains("免运费") || deliveryFee.contains("免邮费")) {
                    deliveryFee = "免运费";
                }
            } else {
                deliveryFee = "免运费";
            }
        } else if (tbDetailResultV6.getDelivery() != null) {
            if (tbDetailResultV6.getDelivery().getPostage() != null) {
                deliveryFee = tbDetailResultV6.getDelivery().getPostage();
            }
        }

        mGoodDetail.deliveryFees = deliveryFee;
        //主图
        if (tbDetailResultV6.getItem() != null && tbDetailResultV6.getItem().getImages() != null &&
                tbDetailResultV6.getItem().getImages().size() > 0 && tbDetailResultV6.getItem().getImages().get(0) != null) {
            mGoodDetail.picsPath = tbDetailResultV6.getItem().getImages().get(0);

        }
        //TODO 此处筛选方式仓促不靠谱，产品规划不够详细，对商品了解不够清楚，最好重新规划对于服务字段的筛选
        // 服务承诺
        List<String> GuaranteeList = new ArrayList<String>();

        List<String> GuaranteeList2 = new ArrayList<String>();
        List<String> afterGuaranteeList = new ArrayList<String>();
        // 服务承诺
        if (unit.getConsumerProtection() != null) {
            List<Unit.ConsumerProtectionBean.ItemsBeanX> list = new ArrayList<>();
            if (unit.getConsumerProtection().getItems() != null) {
                //淘宝服务取GUARANTEES，其他取afterGuarantees
                list.addAll(unit.getConsumerProtection().getItems());
            }

            AppDebug.e(TAG, " list -----> " + list.size() + "   = " + list.toString());
            if (list != null) {
                // 判断是否有N天无理由退换，有就显示在第一个，放入列表
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getTitle().contains("天无理由退换")) {
                        GuaranteeList.add(list.get(i).getTitle());
                        list.remove(i);
                        break;
                    }
                }
                //剩余的数据存入列表
                for (int j = 0; j < list.size(); j++) {
                    GuaranteeList.add(list.get(j).getTitle());
                }
                // 如果前三个包含蚂蚁花呗、集分宝、信用卡支付，移除这三个
                int j = 0;
                int q = GuaranteeList.size();
                while (j < q) {
                    if (0 <= j && j <= 2) {
                        if (GuaranteeList.get(j).contains("蚂蚁花呗")) {
                            GuaranteeList.remove(j);
                            AppDebug.e(TAG, "蚂蚁花呗");
                            j = 0;
                            q = GuaranteeList.size();
                        } else if (GuaranteeList.get(j).contains("集分宝")) {
                            GuaranteeList.remove(j);
                            AppDebug.e(TAG, "集分宝");
                            j = 0;
                            q = GuaranteeList.size();
                        } else if (GuaranteeList.get(j).contains("信用卡支付")) {
                            GuaranteeList.remove(j);
                            AppDebug.e(TAG, "信用卡支付");
                            j = 0;
                            q = GuaranteeList.size();

                        } else {
                            j++;
                        }
                    } else {
                        j++;
                    }
                }
            }
            //格式化显示字段
            AppDebug.e(TAG, "GuaranteeList = " + GuaranteeList.size());
            if (!GuaranteeList.isEmpty() && GuaranteeList != null) {
                for (int i = 0; i < GuaranteeList.size(); i++) {
                    String formate = StringUtil.formatGautee(GuaranteeList.get(i));
                    GuaranteeList2.add(formate);
                }

                //去重复
                Set set = new HashSet();
                for (String cd : GuaranteeList2) {
                    if (set.add(cd)) {
                        afterGuaranteeList.add(cd);
                    }
                }
                AppDebug.e(TAG, "afterGuaranteeList = " + afterGuaranteeList.size());
            }

        }
        mGoodDetail.afterGuaranteeList = afterGuaranteeList;

        String skuId = null;
        if (unit != null && unit.getSkuBase() != null && unit.getSkuBase().getSkus() != null) {
            if (unit.getSkuBase().getSkus().size() > 0) {
                if (unit.getSkuBase().getSkus().get(0) != null) {
                    skuId = unit.getSkuBase().getSkus().get(0).getSkuId();
                }
            }
        }

        if (TextUtils.isEmpty(skuId) && tbDetailResultV6 != null && tbDetailResultV6.getSkuBase() != null
                && tbDetailResultV6.getSkuBase().getSkus() != null
                && tbDetailResultV6.getSkuBase().getSkus().size() > 0
                && tbDetailResultV6.getSkuBase().getSkus().get(0) != null && tbDetailResultV6.getSkuBase().getSkus().get(0).getSkuId() != null) {
            skuId = tbDetailResultV6.getSkuBase().getSkus().get(0).getSkuId();
        }


        mGoodDetail.skuId = skuId;
        AppDebug.e(TAG, "默认sku为:" + mGoodDetail.skuId);
        AppDebug.e(TAG, "mGoodDetail = " + mGoodDetail.toString());
        anaylisysTaoke();


    }

    /**
     * 加购失败展示浮层
     *
     * @param text
     */
    public void showAddTipPop(String text) {
        popAddTipDialog.showDialog(text);
        mShowAddTipTimer.start(true);
    }

    private int mShowAddTipCount = 3;
    private TimePickerUtil mShowAddTipTimer = new TimePickerUtil(1000) {

        @Override
        public void doOnUIThread() {
            mShowAddTipCount--;
            if (mShowAddTipCount <= 0) {
                if (popAddTipDialog.isShowing())
                    popAddTipDialog.dismiss();
                stop();
                mShowAddTipCount = 3;
            }


        }

    };

    private class GetAddCartRequestListener extends BizRequestListener<ArrayList<SearchResult>> {

        public GetAddCartRequestListener(WeakReference<? extends BaseActivity> baseActivityRef) {
            super((WeakReference<BaseActivity>) baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            int result = resultCode;
            AppDebug.e(TAG, "加购失败，错误码是resultCode =  " + result + " msg = " + msg);
            TvBuyActivity tvBuyActivity = (TvBuyActivity) mBaseActivityRef.get();
            if (resultCode == ServiceCode.ADD_CART_FAILURE.getCode()) {
                tvBuyActivity.mGoodDetail.canAddBug = false;
                tvBuyActivity.canAddBug = false;
                AppDebug.e(TAG, "显示抢光图片");
                showAddTipPop("亲！该宝贝已被抢光了！");
                tvBuyActivity.iv_add_bug.setImageResource(R.drawable.cytz_good_null);
                return true;
            } else if (resultCode == 106 && msg.equals("您的购物车宝贝总数（含超市宝贝）已满120件，建议您先去结算或清理")) {
                AppDebug.e(TAG, "购物车已满提示");
                showAddTipPop("亲的购物车被塞满了，快去清理吧~");
                return true;

            } else {
                return false;
            }


        }

        @Override
        public void onSuccess(ArrayList<SearchResult> data) {
            AppDebug.e(TAG, "加入购物车成功");
            isGotoBug = true;
            iv_add_bug.setImageResource(R.drawable.cytz_go_bug);

        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }


    /**
     * 获取积分时间戳的回调
     */
    private class GetVideoPointSchemeListener extends BizRequestListener<TvIntegration> {

        public GetVideoPointSchemeListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(TvIntegration data) {
            if (data == null)
                return;
            mTvInterItemList = data.result;
            //被领过的积分
            List<String> historyId = SharePreUtil.getHistoryIntegrationRecord(TvBuyActivity.this);
            if (historyId != null && historyId.size() > 0) {
                for (int i = 0; i < historyId.size(); i++) {
                    for (int j = 0; j < mTvInterItemList.size(); j++) {
                        if (historyId.get(i).equals(mTvInterItemList.get(j).id)) {
                            mTvInterItemList.remove(j);
                            AppDebug.e(TAG, " 移除该历史记录  id = " + historyId.get(i));
                        }
                    }
                }
            }
            AppDebug.e(TAG, "最终本次拿到的数据(展示) = " + mTvInterItemList.toString());
            mHandler.sendEmptyMessage(TVBUY_CALCULATE_SYSTEM_TIME);

        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    /**
     * 领取积分
     */

    private TvGetIntegration mTvGetIntegration;

    private class GetVideoPointListener extends BizRequestListener<TvGetIntegration> {

        public GetVideoPointListener(WeakReference<? extends BaseActivity> baseActivityRef) {
            super((WeakReference<BaseActivity>) baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            AppDebug.e(TAG, "领取积分失败，错误msg = " + msg);
            //未登录判断
            if (resultCode == ServiceCode.API_NOT_LOGIN.getCode()) {
                AppDebug.e(TAG, "用户未登录,不移除记录，登陆后重新领取");
                return false;
            } else {
                mCountRashTimer.stop();
                //领取失败，不能再领取积分，当前页面list移除id,缓存中加入id
                SharePreUtil.saveHistoryIntegration(TvBuyActivity.this, mTvIntegerItem.id);
                mTvInterItemList.remove(mCurentTvInterPosi);
                mHandler.sendEmptyMessage(TVBUY_SHOW_GET_FAIL);
                return true;
            }
        }

        @Override
        public void onSuccess(TvGetIntegration data) {
            if (data != null) {
                AppDebug.e(TAG, "积分领取成功！积分id是 = " + mTvIntegerItem.id + "积分规则pointSchemeId = " + mTvIntegerItem.pointSchemeId);
                mTvGetIntegration = data;
                if (!TextUtils.isEmpty(mTvIntegerItem.id)) {
                    SharePreUtil.saveHistoryIntegration(TvBuyActivity.this, mTvIntegerItem.id);
                    mTvInterItemList.remove(mCurentTvInterPosi);
                    mCountRashTimer.stop();
                    mHandler.sendEmptyMessage(TVBUY_SHOW_GET_SUCCESS);
                }
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }


    private void videoDestroy() {
        if (videoView != null) {
            videoView.stopPlayback();
            videoView.destroyDrawingCache();
            videoView.release();
            videoView = null;
        }
    }

    @Override
    protected void onDestroy() {
        videoDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        clearData();
        clearView();
        removeAccountListen();
        clearTimePicker();
        super.onDestroy();
    }

    private void clearTimePicker() {
        mCountDownTimer.stop();
        mCountRashTimer.stop();
        mShowEndTimer.stop();

    }

    public void clearData() {
        mTvInterItemList = null;
        mHandler.removeCallbacksAndMessages(null);
        if (pop_detail.isShowing())
            pop_detail.dismiss();
        if (pop_count.isShowing())
            pop_count.dismiss();
        if (pop_get_fail.isShowing())
            pop_get_fail.dismiss();
        if (pop_get_success.isShowing())
            pop_get_success.dismiss();
        if (pop_get.isShowing())
            pop_get.dismiss();
        if (popAddTipDialog.isShowing())
            popAddTipDialog.dismiss();
        popAddTipDialog = null;
    }

    public void clearView() {
        iv_good = null;
        iv_add_bug = null;
        tv_goods_title = null;
        tv_good_sold = null;
        tv_tip_1 = null;
        tv_tip_2 = null;
        tv_tip_3 = null;
        tv_good_price = null;
        ll_tip_1 = null;
        ll_tip_2 = null;
        ll_tip_3 = null;
        mImageLoaderManager = null;
        tv_originalPrice = null;
        tv_point_next = null;
        tv_point_value = null;
        tv_fail_next_time = null;
    }


    @Override
    protected void onPause() {

        currentPosition = videoView.getCurrentPosition();
        if (videoView != null) {
            videoView.pause();
            videoView.release();
            //videoView.stopPlayback();
        }

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (pop_top.isShowing())
            pop_top.dismiss();
        if (pop_detail.isShowing())
            pop_detail.dismiss();


        if (mHandler != null) {
            //mHandler.removeCallbacksAndMessages(null);
            removeMsg();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (currentPosition != 0) {
            loading_relativeLayout.setVisibility(View.VISIBLE);
            videoView.setVideoPath(data1.get(currentVideoNum).getVideo());


            AppDebug.e("bug", "onresume执行" + currentPosition);
        }
    }

    /**
     * 根据type给下一个上一个视频设置详情
     *
     * @param type
     * @param data1
     */
    public void setNextAndLastVideoMsg(int type, List<TvBuyItems.TvBuyItem> data1) {
        if (type == 1) {//上一个视频
            startAnimation(ll_lastVideo);
            endAnimation(ll_nextVideo);
            if (currentVideoNum >= 1) {
                if (!TextUtils.isEmpty(data1.get(currentVideoNum - 1).getName()))
                    tv_good_name_last.setText(data1.get(currentVideoNum - 1).getName());
                if (!TextUtils.isEmpty(data1.get(currentVideoNum - 1).getCover()))
                    setItemImage(data1.get(currentVideoNum - 1).getCover(), iv_good_last);
            } else if (currentVideoNum < 1) {
                if (!TextUtils.isEmpty(data1.get(data1.size() - 1).getName()))
                    tv_good_name_last.setText(data1.get(data1.size() - 1).getName());
                if (!TextUtils.isEmpty(data1.get(data1.size() - 1).getCover()))
                    setItemImage(data1.get(data1.size() - 1).getCover(), iv_good_last);
            }
            //ll_nextVideo.setVisibility(View.INVISIBLE);
            endAnimation(ll_nextVideo);
        } else if (type == 2) {//下一个视频
            //ll_nextVideo.setVisibility(VISIBLE);
            startAnimation(ll_nextVideo);
            endAnimation(ll_lastVideo);
            //ll_lastVideo.setVisibility(View.INVISIBLE);
            if (currentVideoNum < data1.size() - 1) {
                if (!TextUtils.isEmpty(data1.get(currentVideoNum + 1).getName()))
                    tv_good_name_next.setText(data1.get(currentVideoNum + 1).getName());
                if (!TextUtils.isEmpty(data1.get(currentVideoNum + 1).getCover()))
                    setItemImage(data1.get(currentVideoNum + 1).getCover(), iv_good_next);
            } else if (currentVideoNum == data1.size() - 1) {
                if (!TextUtils.isEmpty(data1.get(0).getName()))
                    tv_good_name_next.setText(data1.get(0).getName());
                if (!TextUtils.isEmpty(data1.get(0).getCover()))
                    setItemImage(data1.get(0).getCover(), iv_good_next);
            }

        }
    }

    @Override
    public String getPageName() {
        return "Page_Videodetail";
    }

    @Override
    public Map<String, String> getPageProperties() {
        if (data1 != null) {
            Map<String, String> properties = Utils.getProperties();
            properties.put(SPMConfig.SPM_CNT, SPMConfig.VIDEO_DETAIL);
            if (data1.get(currentVideoNum) != null) {
                properties.put("item_id", data1.get(currentVideoNum).getItemId());
                properties.put("video_id", data1.get(currentVideoNum).getId());
                return properties;
            }
        }
        return super.getPageProperties();
    }

    /**
     * 淡入动画
     *
     * @param iv
     */
    private void startAnimation(View iv) {
        if (iv.getVisibility() == INVISIBLE) {
            iv.startAnimation(anim_visible);
            iv.setVisibility(VISIBLE);
        }
    }

    /**
     * 淡出动画
     *
     * @param iv
     */
    private void endAnimation(View iv) {
        if (iv.getVisibility() == VISIBLE) {
            iv.startAnimation(anim_invisible);
            iv.setVisibility(INVISIBLE);
        }

    }

    /**
     * 视频购物详情打点，视频播放时就打，放在onprepare监听中
     */
    private void utClickLastNext() {
        Map<String, String> properties = Utils.getProperties();
        if (CoreApplication.getLoginHelper(TvBuyActivity.this).isLogin()) {
            properties.put("is_sign", 1 + "");
        } else {
            properties.put("is_sign", 2 + "");
        }
        if (data1 != null) {
            if (data1.get(currentVideoNum) != null) {
                properties.put("item_id", data1.get(currentVideoNum).getItemId());
                properties.put("video_id", data1.get(currentVideoNum).getId());
                properties.put("name", data1.get(currentVideoNum).getName());
            }
        }

        Utils.utControlHit(getFullPageName(), "Page_Videodetail_" + data1.get(currentVideoNum).getId(), properties);
    }

    /**
     * 底部贴片被点击
     */
    private void utClickBottomPop() {

        Map<String, String> properties = Utils.getProperties();
        if (data1 != null) {
            if (data1.get(currentVideoNum) != null) {
                properties.put("item_id", data1.get(currentVideoNum).getItemId());
                properties.put("video_id", data1.get(currentVideoNum).getId());
            }
        }

        properties.put("spm", SPMConfig.VIDEO_DETAIL_BAFFLE_OK);
        Utils.utControlHit(getFullPageName(), "Page_Videodetail_baffle_ok", properties);

    }

    /**
     * 进度条显示打点，就是mprogressbarlayout显示的时候打点
     */
    private void utShowProgressBar() {
        Map<String, String> properties = Utils.getProperties();
        if (data1 != null) {
            if (data1.get(currentVideoNum) != null) {
                properties.put("item_id", data1.get(currentVideoNum).getItemId());
                properties.put("video_id", data1.get(currentVideoNum).getId());
            }
        }

        properties.put("spm", SPMConfig.VIDEO_DETAIL_BAR);
        Utils.utControlHit(getFullPageName(), "Page_Videodetail_bar", properties);

    }

    /**
     * 进度条点击事件，left，right，stop，start，next，before
     *
     * @param s
     */
    private void utClickProgressBar(String s) {
        Map<String, String> properties = Utils.getProperties();
        if (data1 != null) {
            if (data1.get(currentVideoNum) != null) {
                properties.put("item_id", data1.get(currentVideoNum).getItemId());
                properties.put("video_id", data1.get(currentVideoNum).getId());
                properties.put("buttonname", s);
            }
        }

        properties.put("spm", SPMConfig.VIDEO_DETAIL_BAR_CLICK);
        Utils.utControlHit(getFullPageName(), "Page_Videodetail_bar_" + s, properties);
    }

    /**
     * 自定义上一个下一个视频按钮聚焦打点
     */
    private void utFocusNextLast(String s) {
        Map<String, String> properties = Utils.getProperties();
        if (data1 != null) {
            if (data1.get(currentVideoNum) != null) {
                properties.put("item_id", data1.get(currentVideoNum).getItemId());
                properties.put("video_id", data1.get(currentVideoNum).getId());
                properties.put("buttonname", s);

            }
        }

        properties.put("spm", SPMConfig.VIDEO_DETAIL_CHANCE);
        Utils.utCustomHit(getFullPageName(), "Expore_Videodetail_" + s, properties);
    }


    public void getNewDetail(final String mItemId) {
        String host = "https://acs.m.taobao.com/gw/mtop.taobao.detail.getdetail/6.0/?data=";
        String tag = "%7B%22itemNumId%22%3A%22";
        String tag2 = "%22%2C%22detail_v%22%3A%223.1.0%22%7D";
        OkHttpClient okHttpClient = new OkHttpClient();
        String url = host + tag + mItemId + tag2 + "&ttid=142857@taobao_iphone_7.10.3";
        final Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String result = response.body().string();
                AppDebug.e(TAG, "result = " + result);
                try {
                    final TBDetailResultV6 data = GetDetail.resolveResult(result);

                    mGoodDetail = null;
                    AppDebug.e("TBDetailResultVO_v6数据请求成功", "");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (data == null || data.getItem() == null) {
                                //商品不存在或者过期
                                showErrorDialog("商品不存在", true);
                                return;
                            }
                            tbDetailResultV6 = data;
                            if (data != null && data.getTrade() != null && data.getTrade().getRedirectUrl() != null && data.getTrade().getRedirectUrl().contains("trip")) {
                                //飞猪商品

                            } else if (data != null && DetailV6Utils.getUnit(data) != null) {//普通商品
                                onHandlerReuqstV6(data);
                            }


                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("okhttp", result);
            }
        });

    }

    private String buildAddBagExParams(){
        JSONObject outJsonObject = new JSONObject();
        JSONObject innerJsonObject = new JSONObject();
        try {
            innerJsonObject.put("outPreferentialId",outPreferentialId);
            innerJsonObject.put("tagId",tagId);
            TvOptionsConfig.setTvOptionsCart(true);
            innerJsonObject.put("tvOptions",TvOptionsConfig.getTvOptions());
            TvOptionsConfig.setTvOptionsCart(false);
            innerJsonObject.put("appKey",Config.getChannel());
            outJsonObject.put("tvtaoExtra",innerJsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return outJsonObject.toString();
    }

    /**
     * 打标活动标签请求监听
     */
    private static class GetProductTagListener extends BizRequestListener<ProductTagBo> {
        public GetProductTagListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            return false;
        }

        @Override
        public void onSuccess(ProductTagBo data) {
            AppDebug.e("打标数据请求成功", "ProductTagBo data" + data);
             tagId = data.getTagId();
             outPreferentialId = data.getOutPreferentialId();
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

}
