/**
 * $
 * PROJECT NAME: TVTaobao
 * PACKAGE NAME: com.yunos.tvtaobao.activity
 * FILE NAME: CouponActivity.java
 * CREATED TIME: 2016年3月11日
 * COPYRIGHT: Copyright(c) 2013 ~ 2016 All Rights Reserved.
 */
package com.yunos.tvtaobao.mytaobao.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.AbsoluteSizeSpan;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.FocusLinearLayout;
import com.yunos.tv.app.widget.focus.FocusListView;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.FocusRelativeLayout;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.params.Params;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.listener.NetworkOkDoListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.CouponRecommend;
import com.yunos.tvtaobao.biz.request.bo.CouponRecommendList;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.request.bo.MyAlipayHongbao;
import com.yunos.tvtaobao.biz.request.bo.MyAlipayHongbaoList;
import com.yunos.tvtaobao.biz.request.bo.MyCoupon;
import com.yunos.tvtaobao.biz.request.bo.MyCouponsList;
import com.yunos.tvtaobao.biz.request.info.GlobalConfigInfo;
import com.yunos.tvtaobao.biz.widget.FocusNoDeepLinearLayout;
import com.yunos.tvtaobao.mytaobao.R;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 卡券包
 * Class Descripton.
 *
 * @author mi.cao
 * @data 2016年3月11日 上午9:58:12
 */
public class CouponActivity extends BaseActivity {

    final public static String COUPON_TYPE_DIANPU_YOUHUI = "0";// 店铺优惠
    final public static String COUPON_TYPE_DIANPU_HONGBAO = "15"; // 店铺红包
    final public static String COUPON_TYPE_SHANGPIN_YOUHUI = "1"; // 商品优惠
    final public static String COUPON_TYPE_DOUBLE_11_YOUHUI = "78"; //双11店铺优惠
    final public static String COUPON_TYPE_BAOYOUQUAN = "8"; // 包邮券
    final public static String COUPON_TYPE_TIANMAO_GOUWUQUAN = "44"; // 天猫购物券
    final public static String COUPON_TYPE_TIANMAO_CHAOSHI = "54";

    private String TAG = "CouponActivity";
    private FocusPositionManager mFocusPositionManager;

    private FocusListView mFocusListView;
    private ListViewAdapter mAdapter;
    private SparseIntArray mItemHongbaoMap = new SparseIntArray();
    private SparseIntArray mItemCouponMap = new SparseIntArray();

    private ImageView mMaskTop;
    private ImageView mMaskBottom;
    private ImageView mNoDataView;

    private int mHongbaoRowCount = 5;
    private int mCouponRowCount = 3;

    private BusinessRequest mBusinessRequest;

    // 优惠券列表
    private MyCouponsList mCouponsList;
    // 支付宝红包列表
    private MyAlipayHongbaoList mHongbaoList;
    // 重新调整后的优惠券数据列表
    private ArrayList<MyCoupon> mCouponDataList;

    // 是否是第一次加载数据
    private boolean isFirstLoadData = true;

    private Drawable mCouponRedBg;
    private Drawable mCouponYellowBg;

    // 价格的单位
    private String mPriceUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onKeepActivityOnlyOne(CouponActivity.class.getName());
        setContentView(R.layout.ytm_card_coupon_list_activity);

        registerLoginListener();
        mBusinessRequest = BusinessRequest.getBusinessRequest();
        isFirstLoadData = true;
//        mTypeface = Typeface.createFromAsset(getAssets(), "fonts/DINOffcPro-Bold.ttf");
        mCouponRedBg = getResources().getDrawable(R.drawable.ytm_coupon_red_bg);
        mCouponYellowBg = getResources().getDrawable(R.drawable.ytm_coupon_yellow_bg);
        mPriceUnit = getString(R.string.ytbv_price_unit_text);

        initView();
        requestData();
    }

    @Override
    protected void refreshData() {
        requestData();
    }

    @Override
    protected void onDestroy() {
        unRegisterLoginListener();
        onRemoveKeepedActivity(CouponActivity.class.getName());
        super.onDestroy();
    }

    private void initView() {
        mFocusPositionManager = (FocusPositionManager) findViewById(R.id.focus_position_manager);
        mFocusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(
                R.drawable.ytbv_common_focus)));

        mFocusListView = (FocusListView) findViewById(R.id.focus_listview_layout);
        mMaskTop = (ImageView) findViewById(R.id.mask_top);
        mMaskBottom = (ImageView) findViewById(R.id.mask_bottom);
        mNoDataView = (ImageView) findViewById(R.id.ytm_card_coupon_no_data);
    }

    private void requestData() {
        OnWaitProgressDialog(true);
        mBusinessRequest.requestMyCouponsList("1", null, new GetCouponListBusinessRequestListener(
                new WeakReference<BaseActivity>(this)));
    }

    /**
     * 获取支付宝数量
     */
    public void getAlipayCount() {
        mBusinessRequest.requestMyAlipayHongbaoList("1", new GetMyAlipayListBusinessRequestListener(
                new WeakReference<BaseActivity>(this), isFirstLoadData));
    }

    private void onHandleRequestDataList() {
        if (mHongbaoList != null && mHongbaoList.getCouponList() != null && mHongbaoList.getCouponList().size() > 0) {
            AppDebug.v(TAG, TAG + ", hongbaoList.size = " + mHongbaoList.getCouponList().size());
            mItemHongbaoMap.clear();
            int hongbaoCount = mHongbaoList.getCouponList().size();
            int hongbaoLine = hongbaoCount / mHongbaoRowCount;
            for (int i = 0; i < hongbaoLine; i++) {
                mItemHongbaoMap.put(i, mHongbaoRowCount);
            }
            if (hongbaoCount % mHongbaoRowCount != 0) {
                mItemHongbaoMap.put(mItemHongbaoMap.size(), hongbaoCount % mHongbaoRowCount);
                hongbaoLine++;
            }
        }

        if (mCouponsList != null && mCouponsList.getCouponList() != null && mCouponsList.getCouponList().size() > 0) {
            mCouponDataList = reBuildCouponList(mCouponsList.getCouponList());
            if (mCouponDataList != null && mCouponDataList.size() > 0) {
                AppDebug.v(TAG, TAG + ", couponList.size = " + mCouponsList.getCouponList().size()
                        + ", new couponList.size = " + mCouponDataList.size());
                mItemCouponMap.clear();
                int couponCount = mCouponDataList.size();
                int couponLine = couponCount / mCouponRowCount;
                for (int i = 0; i < couponLine; i++) {
                    mItemCouponMap.put(i, mCouponRowCount);
                }

                if (couponCount % mCouponRowCount != 0) {
                    mItemCouponMap.put(mItemCouponMap.size(), couponCount % mCouponRowCount);
                    couponLine++;
                }
            }
        }

        // 有数据时
        if (mItemCouponMap.size() + mItemHongbaoMap.size() > 0) {
            changeShowView(true);
        } else {
            changeShowView(false);
        }
    }

    /**
     * 切换界面显示
     *
     * @param showList
     */
    void changeShowView(boolean showList) {
        if (showList) {
            mNoDataView.setVisibility(View.GONE);
            mFocusListView.setVisibility(View.VISIBLE);
            mAdapter = new ListViewAdapter(this, mItemHongbaoMap, mItemCouponMap);
            mFocusListView.setDeepMode(true);
            // mFocusListView.setPreLoadCount(10);
            mFocusListView.setDefatultScrollStep(30f);
            mFocusListView.setSelection(0);
            mFocusListView.setParams(new Params(1.05f, 1.05f, 10, null, true, 20,
                    new AccelerateDecelerateFrameInterpolator()));
            mFocusListView.setAdapter(mAdapter);
            mFocusListView.requestFocus();
            mFocusListView.setOnItemSelectedListener(new ItemSelectedListener() {

                @Override
                public void onItemSelected(View v, int position, boolean isSelected, View view) {
                    if (isSelected) {// 当某行被选中，且需要显示蒙版时才做判断
                        if (position == 0 && mMaskTop.isShown()) {
                            mMaskTop.setVisibility(View.INVISIBLE);
                        }
                        if (position > 0 && !mMaskTop.isShown()) {
                            mMaskTop.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        } else {
            mNoDataView.setVisibility(View.VISIBLE);
            mFocusListView.setVisibility(View.GONE);
            mMaskTop.setVisibility(View.GONE);
            mMaskBottom.setVisibility(View.GONE);
        }
    }

    class ViewHolder {

        TextView textView;
        FocusLinearLayout hongbaoLayout;
        FocusLinearLayout couponLayout;
    }

    class ListViewAdapter extends BaseAdapter {

        Context mContext;
        SparseIntArray mHongbaoItemMap;
        SparseIntArray mCouponItemMap;

        public ListViewAdapter(Context context, SparseIntArray hongbaoItemMap, SparseIntArray couponItemMap) {
            mContext = context;
            mHongbaoItemMap = hongbaoItemMap;
            mCouponItemMap = couponItemMap;
        }

        @Override
        public int getCount() {
            return mHongbaoItemMap.size() + mCouponItemMap.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position < 0) {
                return null;
            }
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.ytm_card_coupon_item, null);
                holder = new ViewHolder();
                holder.textView = (TextView) convertView.findViewById(R.id.card_coupon_item_text);
                holder.hongbaoLayout = (FocusLinearLayout) convertView.findViewById(R.id.hongbao_linearlayout);
                holder.couponLayout = (FocusLinearLayout) convertView.findViewById(R.id.coupon_linearlayout);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            FocusRelativeLayout parentView = (FocusRelativeLayout) convertView;

            boolean showTextView = false;
            if (mHongbaoItemMap.size() > 0) {// 有红包的情况下
                if (position == 0) {
                    holder.textView.setText(getString(R.string.ytm_hongbao_title));
                    holder.textView.setVisibility(View.VISIBLE);
                    showTextView = true;
                }

                if (position == mHongbaoItemMap.size() && mCouponItemMap.size() > 0) {// 红包结束，如果有优惠券
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.textView
                            .getLayoutParams();
                    if (params != null) {
                        params.topMargin = (int) getResources().getDimension(R.dimen.dp_60);
                    }
                    holder.textView.setText(getString(R.string.ytm_coupon_title));
                    holder.textView.setVisibility(View.VISIBLE);
                    showTextView = true;
                }
            } else if (mCouponItemMap.size() > 0) {// 如果没有红包，但有优惠券
                if (position == 0) {
                    holder.textView.setText(getString(R.string.ytm_coupon_title));
                    holder.textView.setVisibility(View.VISIBLE);
                    showTextView = true;
                }
            }
            if (!showTextView) {// 隐藏textview
                holder.textView.setVisibility(View.GONE);
            }

            if (position < mHongbaoItemMap.size()) {// 布局红包
                if (holder.hongbaoLayout.getVisibility() != View.VISIBLE
                        && holder.couponLayout.getVisibility() == View.VISIBLE) {
                    // parentView.forceInitNode();//当两种view调换显示时，重新initNode;
                }
                holder.hongbaoLayout.setVisibility(View.VISIBLE);
                holder.couponLayout.setVisibility(View.GONE);

                int index = parentView.getViewNodeIndex(holder.hongbaoLayout);
                if (index != -1) {
                    parentView.setFocusedIndex(index);
                }
                for (int i = mHongbaoItemMap.get(position); i < mHongbaoRowCount; i++) {
                    FocusNoDeepLinearLayout itemLayout = (FocusNoDeepLinearLayout) holder.hongbaoLayout.getChildAt(i);
                    itemLayout.setVisibility(View.GONE);
                    itemLayout.setFocusable(false);
                    itemLayout.setOnClickListener(null);
                }
                for (int i = 0; i < mHongbaoItemMap.get(position); i++) {
                    FocusNoDeepLinearLayout itemLayout = (FocusNoDeepLinearLayout) holder.hongbaoLayout.getChildAt(i);
                    if (itemLayout != null) {
                        itemLayout.setCustomerFocusPaddingRect(new Rect(
                                (int) getResources().getDimension(R.dimen.dp_8), (int) getResources().getDimension(
                                R.dimen.dp_12), (int) getResources().getDimension(R.dimen.dp_8),
                                (int) getResources().getDimension(R.dimen.dp_12)));
                        itemLayout.setVisibility(View.VISIBLE);
                        itemLayout.setFocusable(true);
                        itemLayout.setOnClickListener(mHongbaoClickListener);
                        setHongbaoContent(itemLayout, position * mHongbaoRowCount + i);
                    }
                }
                // 如果当前控件内的节点不等于需要显示的节点数，重新初始化节点
                if (holder.hongbaoLayout.getNodeMap() != null
                        && holder.hongbaoLayout.getNodeMap().size() != mHongbaoItemMap.get(position)) {
                    holder.hongbaoLayout.forceInitNode();
                }

                // 如果一行没有排满,且当前的index大于子控件的个数，强行设置index为最后一个子控件
                //                AppDebug.v(TAG,
                //                        TAG + ".mHongbaoItemMap = " + mHongbaoItemMap.get(position) + ", mHongbaoRowCount = "
                //                                + mHongbaoRowCount + ", position = " + position + ", getFocusedIndex = "
                //                                + hongbaoLayout.getFocusedIndex());
                if ((mHongbaoItemMap.get(position) < mHongbaoRowCount)// || hongbaoLayout.getFocusedIndex() == -1
                        && (mHongbaoItemMap.get(position) <= holder.hongbaoLayout.getFocusedIndex())) {
                    holder.hongbaoLayout.setFocusedIndex(mHongbaoItemMap.get(position) - 1);
                }
                holder.hongbaoLayout.setOnItemSelectedListener(mItemSelectedListener);
            } else {// 布局优惠券
                if (holder.hongbaoLayout.getVisibility() == View.VISIBLE
                        && holder.couponLayout.getVisibility() != View.VISIBLE) {
                    //                    parentView.forceInitNode();//当两种view调换显示时，重新initNode;
                }
                holder.hongbaoLayout.setVisibility(View.GONE);
                holder.couponLayout.setVisibility(View.VISIBLE);

                int index = parentView.getViewNodeIndex(holder.couponLayout);
                if (index != -1) {
                    parentView.setFocusedIndex(index);
                }
                for (int i = mCouponItemMap.get(position - mHongbaoItemMap.size()); i < mCouponRowCount; i++) {
                    FocusNoDeepLinearLayout itemLayout = (FocusNoDeepLinearLayout) holder.couponLayout.getChildAt(i);
                    itemLayout.setVisibility(View.GONE);
                    itemLayout.setFocusable(false);
                    itemLayout.setOnClickListener(null);
                }
                for (int i = 0; i < mCouponItemMap.get(position - mHongbaoItemMap.size()); i++) {
                    FocusNoDeepLinearLayout itemLayout = (FocusNoDeepLinearLayout) holder.couponLayout.getChildAt(i);
                    if (itemLayout != null) {
                        itemLayout.setCustomerFocusPaddingRect(new Rect(
                                (int) getResources().getDimension(R.dimen.dp_8), (int) getResources().getDimension(
                                R.dimen.dp_12), (int) getResources().getDimension(R.dimen.dp_8),
                                (int) getResources().getDimension(R.dimen.dp_12)));
                        itemLayout.setVisibility(View.VISIBLE);
                        itemLayout.setFocusable(true);
                        itemLayout.setOnClickListener(mCouponClickListener);
                        setCouponContent(itemLayout, (position - mHongbaoItemMap.size()) * mCouponRowCount + i);
                    }
                }
                // 如果当前控件内的节点不等于需要显示的节点数，重新初始化节点
                if (holder.couponLayout.getNodeMap() != null
                        && holder.couponLayout.getNodeMap().size() != mCouponItemMap.get(position
                        - mHongbaoItemMap.size())) {
                    holder.couponLayout.forceInitNode();
                }
                // 如果一行没有排满,且当前的index大于子控件的个数，强行设置index为最后一个子控件
                if ((mCouponItemMap.get(position - mHongbaoItemMap.size()) < mCouponRowCount)
                        && mCouponItemMap.get(position - mHongbaoItemMap.size()) <= holder.couponLayout
                        .getFocusedIndex()) {
                    holder.couponLayout.setFocusedIndex(mCouponItemMap.get(position - mHongbaoItemMap.size()) - 1);
                }
                holder.hongbaoLayout.setOnItemSelectedListener(null);
            }

            return convertView;
        }
    }

    /**
     * 设置红包数据
     *
     * @param itemLayout
     * @param index
     */
    private void setHongbaoContent(View itemLayout, int index) {
        if (itemLayout == null || mHongbaoList == null || mHongbaoList.getCouponList() == null) {
            return;
        }
        if (index < 0 || index >= mHongbaoList.getCouponList().size()) {
            return;
        }

        MyAlipayHongbao hongbao = mHongbaoList.getCouponList().get(index);
        if (hongbao == null) {
            return;
        }

        itemLayout.setTag(index);

        TextView hongbao_name = (TextView) itemLayout.findViewById(R.id.hongbao_name);
        TextView hongboo_price_coustom = (TextView) itemLayout.findViewById(R.id.hongboo_price_coustom);
        TextView hongbao_expire_date = (TextView) itemLayout.findViewById(R.id.hongbao_expire_date);
        TextView hongbao_dsc = (TextView) itemLayout.findViewById(R.id.hongbao_dsc);

        if (hongbao_name != null && !TextUtils.isEmpty(hongbao.getCouponName())) {
            hongbao_name.setText(hongbao.getCouponName());
        }

        if (hongbao_expire_date != null && !TextUtils.isEmpty(hongbao.getGmtExpired())
                && !TextUtils.isEmpty(hongbao.getGmtActive())) {
            String activityDate = hongbao.getGmtActive().replace(".", "/").substring(0, hongbao.getGmtActive().indexOf(" "));
            String expiredDate = hongbao.getGmtExpired().replace(".", "/").substring(0, hongbao.getGmtActive().indexOf(" "));
            hongbao_expire_date.setText(activityDate + "-" + expiredDate);
        }

        if (hongbao_dsc != null && !TextUtils.isEmpty(hongbao.getPublisherName())) {
            hongbao_dsc.setText(hongbao.getPublisherName());
        }

        setHongbaoPrice(hongboo_price_coustom, hongbao.getCurrentAmount());
    }

    /**
     * 设置优惠券数据
     *
     * @param itemLayout
     * @param index
     */
    private void setCouponContent(View itemLayout, int index) {
        if (itemLayout == null || mCouponDataList == null) {
            return;
        }
        if (index < 0 || index >= mCouponDataList.size()) {
            return;
        }

        MyCoupon coupon = mCouponDataList.get(index);
        if (coupon == null) {
            return;
        }

        itemLayout.setTag(index);

        RelativeLayout coupon_layout = (RelativeLayout) itemLayout.findViewById(R.id.coupon_layout);
        TextView coupon_name = (TextView) itemLayout.findViewById(R.id.coupon_name);
        TextView coupon_price = (TextView) itemLayout.findViewById(R.id.coupon_price);
        TextView coupon_use_condition = (TextView) itemLayout.findViewById(R.id.coupon_use_condition);
        TextView limitedPrompt = (TextView) itemLayout.findViewById(R.id.coupon_limited_prompt);
        TextView coupon_expire_date = (TextView) itemLayout.findViewById(R.id.coupon_expire_date);

        if (coupon_layout != null) {// 如果是店铺红包或天猫购物券，背景是红色
            if (COUPON_TYPE_DIANPU_HONGBAO.equals(coupon.getCouponType())
                    || COUPON_TYPE_TIANMAO_GOUWUQUAN.equals(coupon.getCouponType())) {
                coupon_layout.setBackgroundDrawable(mCouponRedBg);
            } else {
                coupon_layout.setBackgroundDrawable(mCouponYellowBg);
            }
        }

        if (coupon_name != null && !TextUtils.isEmpty(coupon.getTitle())) {
            coupon_name.setText(coupon.getTitle());
        }

        setCouponPrice(coupon_price, coupon.getAmount());

        if (coupon_use_condition != null && !TextUtils.isEmpty(coupon.getUseCondition())) {
            coupon_use_condition.setText(coupon.getUseCondition());
        }

        if (limitedPrompt != null) {
            if (!TextUtils.isEmpty(coupon.getLimitedPrompt())) {
                limitedPrompt.setText(coupon.getLimitedPrompt());
            } else {
                limitedPrompt.setText(getString(R.string.ytm_coupon_use_area));
            }
        }

        if (coupon_expire_date != null & !TextUtils.isEmpty(coupon.getStartTime())
                && !TextUtils.isEmpty(coupon.getEndTime())) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
            try {
                String start = format.format(new Date(Long.parseLong(coupon.getStartTime()) * 1000));
                String end = format.format(new Date(Long.parseLong(coupon.getEndTime()) * 1000));
                coupon_expire_date.setText(start + "\n" + end);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    ItemSelectedListener mItemSelectedListener = new ItemSelectedListener() {

        @Override
        public void onItemSelected(View v, int position, boolean isSelected, View view) {
            if (v.getVisibility() == View.VISIBLE) {
                TextView hongbaoName = (TextView) v.findViewById(R.id.hongbao_name);
                TextView hongbaoDsc = (TextView) v.findViewById(R.id.hongbao_dsc);
                if (isSelected) {
                    hongbaoName.setEllipsize(TruncateAt.MARQUEE);
                    hongbaoName.setMarqueeRepeatLimit(-1);
                    hongbaoDsc.setEllipsize(TruncateAt.MARQUEE);
                    hongbaoDsc.setMarqueeRepeatLimit(-1);
                } else {
                    hongbaoName.setEllipsize(TruncateAt.END);
                    hongbaoDsc.setEllipsize(TruncateAt.END);
                }
            }
        }
    };

    private OnClickListener mHongbaoClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mFocusListView.isScrolling()) {
                mFocusListView.forceResetFocusParams(mFocusPositionManager);
            }
            int index = (Integer) (v.getTag());
            Map<String, String> p = Utils.getProperties();
            String controlName = Utils.getControlName("click", index);
            p.put("position", String.valueOf(index));
            p.put("type", "hongbao");

            if (mHongbaoList != null && mHongbaoList.getCouponList() != null) {
                MyAlipayHongbao hongbao = mHongbaoList.getCouponList().get(index);
                String price = hongbao.getCurrentAmount();
                p.put("amt", price);
            }

            Utils.utControlHit(getFullPageName(), controlName, p);

            //跳转到首页
            Intent intent = new Intent();
            // 进入首页后就不能返回红包页面
            intent.setClassName(CouponActivity.this, BaseConfig.SWITCH_TO_HOME_ACTIVITY);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    };

    private OnClickListener mCouponClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mFocusListView.isScrolling()) {
                mFocusListView.forceResetFocusParams(mFocusPositionManager);
            }
            int index = (Integer) (v.getTag());
            Map<String, String> p = Utils.getProperties();
            String controlName = Utils.getControlName("click", index);
            p.put("position", String.valueOf(index));
            p.put("type", "coupon");

            if (mCouponDataList != null) {
                MyCoupon coupon = mCouponDataList.get(index);
                String price = coupon.getAmount();
                if (!TextUtils.isEmpty(price)) {
                    p.put("amt", price);
                }

                if (!TextUtils.isEmpty(coupon.getCouponType())) {
                    p.put("couponType", coupon.getCouponType());
                }
            }

            Utils.utControlHit(getFullPageName(), controlName, p);

            MyCoupon coupon = null;
            if (mCouponDataList != null) {
                coupon = mCouponDataList.get(index);
            }

            if (coupon == null) {
                return;
            }

            // 如果是商品优惠券，则获取跳转到对应的商品详情中
            if (COUPON_TYPE_SHANGPIN_YOUHUI.equals(coupon.getCouponType())) {
                OnWaitProgressDialog(true);
                mBusinessRequest.requestCouponRecommendList(coupon.getSupplierId(), coupon.getCouponId(),
                        new GetCouponRecommendListBusinessRequestListener(new WeakReference<BaseActivity>(
                                CouponActivity.this), coupon));
            } else if (COUPON_TYPE_DOUBLE_11_YOUHUI.equals(coupon.getCouponType())) {
                //跳转到首页
                Intent intent = new Intent();
                // 进入首页后就不能返回红包页面
                intent.setClassName(CouponActivity.this, BaseConfig.SWITCH_TO_HOME_ACTIVITY);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            } else {
                gotoShop(coupon);
            }
        }
    };

    /**
     * 去店铺页
     *
     * @param coupon
     */
    private void gotoShop(MyCoupon coupon) {
        AppDebug.v(TAG, TAG + ".gotoShop.coupon = " + coupon);
        if (coupon == null) {
            return;
        }
        // 跳转到店铺
        String sellerId = null;
        Intent intent = new Intent();
        // 天猫超市优惠券跳转到天猫超市首页
        if (!TextUtils.isEmpty(coupon.getTitle()) && coupon.getTitle().contains(getString(R.string.ytm_tmall_chaoshi))) {
            intent.setClassName(CouponActivity.this, BaseConfig.SWITCH_TO_CHAOSHI_ACTIVITY);
        } else {
            sellerId = coupon.getSupplierId();
            if (!TextUtils.isEmpty(sellerId)) {
                intent.putExtra(BaseConfig.SELLER_NUMID, sellerId);
            }
            GlobalConfig globalConfig = GlobalConfigInfo.getInstance().getGlobalConfig();
            if (globalConfig == null|| !globalConfig.isBlitzShop()) {
                intent.setClassName(CouponActivity.this, BaseConfig.SWITCH_TO_SHOP_ACTIVITY);
            } else {
                intent.setClassName(CouponActivity.this, BaseConfig.SWITCH_TO_SHOP_BLIZ_ACTIVITY);
            }
        }

        startActivity(intent);
    }

    /**
     * 去商品详情页面
     */
    private void gotoItemDetail(String itemId) {
        AppDebug.v(TAG, TAG + ".gotoItemDetail.itemId = " + itemId);
        if (TextUtils.isEmpty(itemId)) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(BaseConfig.INTENT_KEY_ITEMID, itemId);
        intent.setClassName(CouponActivity.this, BaseConfig.SWITCH_TO_DETAIL_ACTIVITY);

        startActivity(intent);
    }

    /**
     * @return
     */
    private ArrayList<MyCoupon> reBuildCouponList(ArrayList<MyCoupon> couponList) {
        if (couponList == null) {
            return null;
        }
        ArrayList<MyCoupon> newCouponList = new ArrayList<MyCoupon>();
        for (int i = 0; i < couponList.size(); i++) {
            MyCoupon myCoupon = couponList.get(i);
            if (Config.isDebug()) {
                AppDebug.v(TAG,
                        TAG + ".reBuildCouponList.myCoupon = " + myCoupon + ", couponList.size = " + couponList.size());
            }
            // 过滤有效的的优惠券，以及去除包邮券,天猫购物券
            if (myCoupon != null && "1".equals(myCoupon.getStatus())
                    && !COUPON_TYPE_BAOYOUQUAN.equals(myCoupon.getCouponType())
                    && !COUPON_TYPE_TIANMAO_GOUWUQUAN.equals(myCoupon.getCouponType())) {
                newCouponList.add(myCoupon);
            }
        }
        return newCouponList;
    }

    /**
     * 设置红包的价格
     */
    private void setHongbaoPrice(TextView view, String price) {
//        view.setTypeface(mTypeface);
        view.getPaint().setAntiAlias(true);
        String title = price;
        //        if (!title.contains(mPriceUnit)) {
        //            title = mPriceUnit + title;
        //        }
        Pattern p = Pattern.compile("\\d+");//在这里，编译 成一个正则。;
        Matcher m = p.matcher(title);//获得匹配;
        String strDigit = "";
        int start = 0;
        int end = 0;
        if (m.find()) { //如果字符中有多段含有数字，可以用while找出所以数字，如abc55dc77,找出55和77
            strDigit = m.group();
        }

        int textSize = (int) (getResources().getDimension(R.dimen.sp_70));
        if (!TextUtils.isEmpty(strDigit)) {
            // 先去掉数据自带的￥
            int index = title.indexOf(strDigit);
            if (index >= 0) {
                title = title.substring(index);
            }
            title = mPriceUnit + title;

            start = title.indexOf(strDigit);
            end = title.length();
            if (title.indexOf(".") > 0) {// 含有小数点
                if (strDigit.length() > 3) {// 大于千元的 去掉小数点
                    title = title.substring(0, title.indexOf("."));
                    start = title.indexOf(strDigit);
                    end = title.length();
                    if (strDigit.length() > 4) {
                        textSize = (int) (getResources().getDimension(R.dimen.sp_48));
                    } else {
                        textSize = (int) (getResources().getDimension(R.dimen.sp_60));
                    }
                }
            } else {
                if (strDigit.length() > 4) {
                    textSize = (int) (getResources().getDimension(R.dimen.sp_48));
                }
            }

            SpannableString ss = new SpannableString(title);//
            ss.setSpan(new AbsoluteSizeSpan(textSize), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            if (title.indexOf(".") > 0) {// 含有小数点
                start = title.indexOf(".");
                end = title.length();
                textSize = (int) (getResources().getDimension(R.dimen.sp_32));
                ss.setSpan(new AbsoluteSizeSpan(textSize), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            view.setText(ss);
        }
    }

    /**
     * 设置优惠券价格
     *
     * @param view
     * @param price
     */
    private void setCouponPrice(TextView view, String price) {
//        view.setTypeface(mTypeface);
        view.getPaint().setAntiAlias(true);
        String title = price;
        //        if (!title.contains(mPriceUnit)) {
        //            title = mPriceUnit + title;
        //        }
        Pattern p = Pattern.compile("\\d+");//在这里，编译 成一个正则。;
        Matcher m = p.matcher(title);//获得匹配;
        String strDigit = "";
        int start = 0;
        int end = 0;
        if (m.find()) { //如果字符中有多段含有数字，可以用while找出所以数字，如abc55dc77,找出55和77
            strDigit = m.group();
        }

        int textSize = (int) (getResources().getDimension(R.dimen.sp_70));
        if (!TextUtils.isEmpty(strDigit)) {
            // 先去掉数据自带的￥
            int index = title.indexOf(strDigit);
            if (index >= 0) {
                title = title.substring(index);
            }
            title = mPriceUnit + title;

            start = title.indexOf(strDigit);
            end = title.length();
            if (title.indexOf(".") > 0) {// 含有小数点
                if (strDigit.length() > 2) {// 大于百元的 去掉小数点
                    title = title.substring(0, title.indexOf("."));
                    start = title.indexOf(strDigit);
                    end = title.length();
                    if (strDigit.length() > 5) {
                        textSize = (int) (getResources().getDimension(R.dimen.sp_32));
                    } else if (strDigit.length() > 4) {
                        textSize = (int) (getResources().getDimension(R.dimen.sp_40));
                    } else if (strDigit.length() > 3) {
                        textSize = (int) (getResources().getDimension(R.dimen.sp_48));
                    } else {
                        textSize = (int) (getResources().getDimension(R.dimen.sp_60));
                    }
                } else {
                    if (strDigit.length() > 1) {
                        textSize = (int) (getResources().getDimension(R.dimen.sp_55));
                    }
                }
            } else {
                if (strDigit.length() > 5) {
                    textSize = (int) (getResources().getDimension(R.dimen.sp_32));
                } else if (strDigit.length() > 4) {
                    textSize = (int) (getResources().getDimension(R.dimen.sp_40));
                } else if (strDigit.length() > 3) {
                    textSize = (int) (getResources().getDimension(R.dimen.sp_48));
                } else if (strDigit.length() > 2) {
                    textSize = (int) (getResources().getDimension(R.dimen.sp_60));
                }
            }

            SpannableString ss = new SpannableString(title);//
            ss.setSpan(new AbsoluteSizeSpan(textSize), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            if (title.indexOf(".") > 0) {// 含有小数点
                start = title.indexOf(".");
                end = title.length();
                textSize = (int) (getResources().getDimension(R.dimen.sp_32));
                ss.setSpan(new AbsoluteSizeSpan(textSize), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            view.setText(ss);
        }
    }

    @Override
    public String getPageName() {
        return "Coupon";
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> p = super.getPageProperties();
        int hongbaoCount = 0;
        if (mHongbaoList != null && mHongbaoList.getCouponList() != null) {
            hongbaoCount = mHongbaoList.getCouponList().size();
        }

        int couponCount = 0;
        if (mCouponDataList != null) {
            couponCount = mCouponDataList.size();
        }

        p.put("cnt", "coupon:" + couponCount + "&hongbao:" + hongbaoCount);
        return p;
    }

    /**
     * 获取优惠券数据
     * Class Descripton.
     *
     * @author mi.cao
     * @data 2016年4月25日 下午9:10:32
     */
    private static class GetCouponListBusinessRequestListener extends BizRequestListener<MyCouponsList> {

        public GetCouponListBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef) {
            super(mBaseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            CouponActivity activity = (CouponActivity) mBaseActivityRef.get();
            if (activity != null) {
                if (Config.isDebug()) {
                    AppDebug.i(activity.TAG, activity.TAG
                            + ".GetCouponListBusinessRequestListener.onError.resultCode = " + resultCode + ".msg = "
                            + msg);
                }
                activity.mCouponsList = null;
                activity.getAlipayCount();

            }
            return true;
        }

        @Override
        public void onSuccess(MyCouponsList data) {
            CouponActivity activity = (CouponActivity) mBaseActivityRef.get();
            if (activity != null) {
                if (Config.isDebug()) {
                    AppDebug.i(activity.TAG, activity.TAG + ".GetCouponListBusinessRequestListener.onSuccess.data = "
                            + data);
                }
                activity.mCouponsList = data;
                activity.getAlipayCount();
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    /**
     * 获取支付宝红包数据
     * Class Descripton.
     *
     * @author mi.cao
     * @data 2016年4月25日 下午9:10:12
     */
    private static class GetMyAlipayListBusinessRequestListener extends BizRequestListener<MyAlipayHongbaoList> {

        boolean isFirstLoadData;

        public GetMyAlipayListBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef,
                                                      boolean isFirstLoadData) {
            super(mBaseActivityRef);
            this.isFirstLoadData = isFirstLoadData;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            final CouponActivity activity = (CouponActivity) mBaseActivityRef.get();
            if (activity != null) {
                if (Config.isDebug()) {
                    AppDebug.i(activity.TAG, activity.TAG
                            + ".MyAlipayListBusinessRequestListener.onError.resultCode = " + resultCode + ".msg = "
                            + msg + ", isFirstLoadData = " + isFirstLoadData);
                }
                activity.OnWaitProgressDialog(false);
                activity.mHongbaoList = null;

                if (resultCode == 1 && isFirstLoadData) {//网络未连接，且是第一次请求数据
                    activity.setNetworkOkDoListener(new NetworkOkDoListener() {

                        @Override
                        public void todo() {
                            // 数据更新后取消网络注册
                            if (activity != null) {
                                AppDebug.v(activity.TAG, activity.TAG
                                        + ".GetMyAlipayListBusinessRequestListener.onError.refreshData");
                                activity.refreshData();
                                activity.setNetworkOkDoListener(null);
                            }
                        }
                    });
                } else {
                    activity.onHandleRequestDataList();
                }
            }
            return false;
        }

        @Override
        public void onSuccess(MyAlipayHongbaoList data) {
            CouponActivity activity = (CouponActivity) mBaseActivityRef.get();
            if (activity != null) {
                if (Config.isDebug()) {
                    AppDebug.i(activity.TAG, activity.TAG + ".GetMyAlipayListBusinessRequestListener.onSuccess.data = "
                            + data);
                }

                activity.isFirstLoadData = false;
                activity.OnWaitProgressDialog(false);
                activity.mHongbaoList = data;
                activity.onHandleRequestDataList();
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    /**
     * 获取商品优惠券推荐商品列表
     * Class Descripton.
     *
     * @author mi.cao
     * @data 2016年4月26日 上午9:59:00
     */
    private static class GetCouponRecommendListBusinessRequestListener extends BizRequestListener<CouponRecommendList> {

        private MyCoupon coupon;

        public GetCouponRecommendListBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef,
                                                             MyCoupon coupon) {
            super(mBaseActivityRef);
            this.coupon = coupon;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            final CouponActivity activity = (CouponActivity) mBaseActivityRef.get();
            if (activity != null) {
                if (Config.isDebug()) {
                    AppDebug.i(activity.TAG, activity.TAG
                            + ".GetCouponRecommendListBusinessRequestListener.onError.resultCode = " + resultCode
                            + ".msg = " + msg + ", coupon = " + coupon);
                }
                activity.OnWaitProgressDialog(false);

                // 如果是网络错误，或者账号未登录，先自行弹出对话框进行相应的处理。
                if (resultCode == 1 || resultCode == 102 || resultCode == 104) {
                    return false;
                } else {
                    // 否则，如果请求失败 则去店铺页
                    activity.gotoShop(coupon);
                    return true;
                }
            }
            return false;
        }

        @Override
        public void onSuccess(CouponRecommendList data) {
            CouponActivity activity = (CouponActivity) mBaseActivityRef.get();
            if (activity != null) {
                if (Config.isDebug()) {
                    AppDebug.i(activity.TAG, activity.TAG
                            + ".GetCouponRecommendListBusinessRequestListener.onSuccess.data = " + data + ", coupon = "
                            + coupon);
                }

                activity.OnWaitProgressDialog(false);
                // 如果没有推荐数据，则去详情，否则 不管有多少条数据，取第一条数据去第一个商品的详情页面
                if (data == null || data.getCouponRecommendList() == null || data.getCouponRecommendList().size() == 0) {
                    activity.gotoShop(coupon);
                } else {
                    CouponRecommend recommend = data.getCouponRecommendList().get(0);
                    if (recommend != null && !TextUtils.isEmpty(recommend.getItemId())) {
                        activity.gotoItemDetail(recommend.getItemId());
                    } else {
                        activity.gotoShop(coupon);
                    }
                }
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }
}
