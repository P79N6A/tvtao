package com.yunos.tvtaobao.flashsale.activity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.taobao.statistic.CT;
import com.taobao.statistic.TBS;
import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvlife.imageloader.core.assist.ImageScaleType;
import com.yunos.tv.app.widget.AdapterView;
import com.yunos.tv.app.widget.focus.FocusHListView;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.app.widget.focus.params.Params;
import com.yunos.tv.app.widget.focus.params.ScaleParams;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.DeviceJudge;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.request.core.ServiceCode;
import com.yunos.tvtaobao.flashsale.AppManager;
import com.yunos.tvtaobao.flashsale.R;
import com.yunos.tvtaobao.flashsale.adapter.RecommendAdapter;
import com.yunos.tvtaobao.flashsale.bo.GoodsInfo;
import com.yunos.tvtaobao.flashsale.bo.RecommendInfo;
import com.yunos.tvtaobao.flashsale.request.RequestManager;
import com.yunos.tvtaobao.flashsale.utils.AppConfig;
import com.yunos.tvtaobao.flashsale.utils.CommUtil;
import com.yunos.tvtaobao.flashsale.utils.TbsUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecommendActivity extends FlashSaleBaseActivity {

    private RequestManager mRequestManager;
    protected ImageLoaderManager mImageLoaderManager;
    protected DisplayImageOptions mOptions;

    private RecommendAdapter mAdapter;

    private FocusPositionManager mFocusPositionManager;
    private FocusHListView mFocusHListView;

    private final static int RECOMMEND_COUNT = 4;

    static final private AbsoluteSizeSpan mHightlightSpan = new AbsoluteSizeSpan(AppConfig.PRICE_HIGHTLIGHT_SIZE);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mPageName = TbsUtil.PAGE_Sell_Out;
        Intent intent = this.getIntent();
        Uri uri = getIntent().getData();
        if (uri != null) {
            // 解析Uri
            Bundle bundle = decodeUri(uri);
            if (bundle == null) {

                return;
            }
        }


        String itemId = intent.getStringExtra("itemId");
        if (itemId == null) {
            return;
        }

        mRequestManager = AppManager.getInstance(this).getRequestManager();
        mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(this);
        onInitOptions();

        setContentView(R.layout.fs_recommend);
        AppDebug.e("TVTao_" + TAG,"inrecommendactivity");

        initView();

        if (NetWorkUtil.isNetWorkAvailable()) {
            loadRecommadData(itemId);
        } else {
            showNetworkErrorDialog(false);
        }
    }

    @Override
    public void enterDetail(GoodsInfo info) {
        AppDebug.e(TAG,"enterdetail-----");


    }

    private RequestListener<List<RecommendInfo>> mGetRecommadByIdListener = new RequestListener<List<RecommendInfo>>() {

        @Override
        public void onRequestDone(List<RecommendInfo> data, int resultCode, String msg) {
            // TODO Auto-generated method stub
            OnWaitProgressDialog(false);
            if (resultCode == RequestManager.CODE_SUCCESS) {
                if (data != null) {
                    //LogUtil.d("size = " + data.size() + ";resultCode = " + resultCode);
                    loadDataSuccess(data);
                }
            } else {
                for (ServiceCode serviceCode : ServiceCode.values()) {
                    if (resultCode == serviceCode.getCode()) {
                        String errorMsg = serviceCode.getMsg();
                        if (null != errorMsg) {
                            showErrorDialog(errorMsg, false);
                        }
                    }
                }
            }
        }
    };

    /**
     * 获取推荐列表
     * @param itemId
     */
    private void loadRecommadData(String itemId) {
        OnWaitProgressDialog(true);

        mRequestManager.getRecommadById(itemId, null, mGetRecommadByIdListener);

    }

    private void initView() {
        mFocusPositionManager = (FocusPositionManager) findViewById(R.id.fs_home_rootview);
        mFocusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(R.drawable.comm_focus)));

        mFocusHListView = (FocusHListView) findViewById(R.id.recommend_list);
        // mFocusPositionManager.setFirstFocusChild(mFocusHListView);
        mFocusPositionManager.requestFocus(mFocusHListView, View.FOCUS_RIGHT);

        initFocusHListView();

        initSaleOutGoodsView();
    }

    private void initFocusHListView() {

        mFocusHListView.setPadding(AppConfig.HLISTVIEW_PADDING_LEFT, AppConfig.HLISTVIEW_PADDING_TOP,
                AppConfig.HLISTVIEW_PADDING_LEFT, AppConfig.HLISTVIEW_PADDING_TOP);
        mFocusHListView.setAnimateWhenGainFocus(false, true, false, false);
        Params param = mFocusHListView.getParams();
        ScaleParams scale = param.getScaleParams();
        scale.setScale(ScaleParams.SCALED_FIXED_COEF, 1.05f, 1.05f);

        if (DeviceJudge.isLowDevice()) {
            // 修改动画的参数
            mFocusHListView.setFlipScrollFrameCount(5);
            mFocusHListView.setFlingSlowDownRatio(20.0f);
        } else {
            // 修改动画的参数
            mFocusHListView.setFlipScrollFrameCount(10);
            mFocusHListView.setFlingSlowDownRatio(8.0f);
        }
        mAdapter = new RecommendAdapter(this, mFocusHListView);
        mFocusHListView.setAdapter(mAdapter);
        mFocusHListView.setDivider(getResources().getDrawable(R.drawable.listview_space));

        mFocusHListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (NetWorkUtil.isNetWorkAvailable()) {
                    RecommendInfo info = (RecommendInfo) mAdapter.getItem(position);

                    if (null == info) {
                        return;
                    }
                    GoodsInfo goodsInfo = new GoodsInfo();
                    goodsInfo.setItemId(info.getItemId());
                    goodsInfo.setType(GoodsInfo.ITEM_TYPE_STOCK_INFO);
                    CommUtil.enterDetail(goodsInfo, RecommendActivity.this);
                    tbsClickEvent(info, position);
                } else {
                    showNetworkErrorDialog(false);
                }
            }
        });
    }

    private GoodsInfo mSeckInfo;
    private String mStatus;
    private String mQianggouTime;

    /**
     * 初始化卖完了的商品界面数据
     */
    private void initSaleOutGoodsView() {
        Intent intent = this.getIntent();

        String name = intent.getStringExtra("name");
        double salePrice = intent.getDoubleExtra("salePrice", 0);
        double price = intent.getDoubleExtra("price", 0);
        String picUrl = intent.getStringExtra("picUrl");
        String saleMessage = intent.getStringExtra("saleMessage");
        mQianggouTime = intent.getStringExtra("time");

        mSeckInfo = new GoodsInfo();
        mSeckInfo.setName(name);
        mSeckInfo.setSalePrice(salePrice);
        mSeckInfo.setPrice(price);
        mSeckInfo.setStartTime(intent.getStringExtra("now_time"));
        mSeckInfo.setEndTime(intent.getStringExtra("end_time"));
        mSeckInfo.setItemId(intent.getStringExtra("itemId"));
        mStatus = intent.getStringExtra("status");
        View v = super.findViewById(R.id.sale_out_id);
        v.setFocusable(false);
        v.setPadding(0, AppConfig.HLISTVIEW_PADDING_TOP, 0, AppConfig.HLISTVIEW_PADDING_TOP);

        TextView nameTx = (TextView) findViewById(R.id.fs_tv_desc);
        nameTx.setText(name);

        TextView salePriceTx = (TextView) findViewById(R.id.fs_tv_saleprice);
        String strTemp = "￥" + String.format("%.2f", salePrice);
        int end = strTemp.indexOf('.');
        if (end > 0) {
            SpannableStringBuilder style = new SpannableStringBuilder(strTemp);
            style.setSpan(mHightlightSpan, 1, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            salePriceTx.setText(style);
        } else {
            salePriceTx.setText(strTemp);
        }

        TextView priceTx = (TextView) findViewById(R.id.fs_tv_price);
        priceTx.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        priceTx.setText("￥" + price);

        TextView saleMessageTx = (TextView) findViewById(R.id.fs_tv_sale_message);
        saleMessageTx.setText(saleMessage);

        ImageView stockImage = (ImageView) findViewById(R.id.fs_iv_stock_image);
        if (null != picUrl) {
            mImageLoaderManager.displayImage(picUrl, stockImage, mOptions);
        }

    }

    private void loadDataSuccess(List<RecommendInfo> list) {
        // List<RecommendInfo> list = (List<RecommendInfo>) msg.obj;
        if (null == list) {
            return;
        }

        int end = list.size() > RECOMMEND_COUNT ? RECOMMEND_COUNT : list.size();

        List<RecommendInfo> listRecommend = list.subList(0, end);
        if (listRecommend != null) {
            mAdapter.setAdapter(listRecommend);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void onInitOptions() {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder().cacheInMemory(true)
                .showImageForEmptyUri(R.drawable.common_default).showImageOnFail(R.drawable.common_default)
                .bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.EXACTLY_STRETCHED);
        // .resetViewBeforeLoading();

        // if (AppConfig.IMAGELOAD_SWITCH_TIMEOUT > 0) {
        // builder.displayer(new FadeInBitmapDisplayer(
        // AppConfig.IMAGELOAD_SWITCH_TIMEOUT));
        // }
        mOptions = builder.build();
    }

    private void tbsClickEvent() {
        if (null == mSeckInfo) {
            if (AppConfig.DEBUG) {
                //LogUtil.i("RecommendActivity", "tbsClickEvent: info:" + (null != mSeckInfo) + " qianggou: ");
            }
            return;
        }

        Map<String, String> prop = TbsUtil.getTbsProperty(mSeckInfo, CommUtil.getTime(mQianggouTime),
                CommUtil.getTime(mSeckInfo.getStartTime()), CommUtil.getTime(mSeckInfo.getEndTime()), mStatus);
        Utils.utUpdatePageProperties(mPageName, prop);
    }



    private void tbsClickEvent(RecommendInfo info, int pos) {
        if (null == info) {
            return;
        }
        Map<String, String> prop = TbsUtil.getTbsProperty(info);
        String event = TbsUtil.getControlName(null, TbsUtil.CLICK_Recommend_P, pos + 1);
        TBS.Adv.ctrlClicked(CT.Button, event, TbsUtil.getKeyValue(prop));
    }

    @Override
    protected void onResume() {
        super.onResume();
        /** 更新埋点数据 */
        tbsClickEvent();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            int code = event.getKeyCode();
            if (code == KeyEvent.KEYCODE_DPAD_DOWN || code == KeyEvent.KEYCODE_DPAD_UP) {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 解析url地址
     * @param uri
     * @return
     */
    private Bundle decodeUri(Uri uri) {
        AppDebug.d(TAG,".decodeUri uri=" + uri.toString());
        String query = uri.getEncodedQuery();
        if (query == null) {
            return null;
        }
        try {
            Bundle bundle = new Bundle();
            Set<String> params = uri.getQueryParameterNames();
            for (String key : params) {
                String value = uri.getQueryParameter(key);
                bundle.putString(key, value);
            }
            return bundle;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
