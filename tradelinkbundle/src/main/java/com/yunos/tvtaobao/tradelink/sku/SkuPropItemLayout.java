/**
 * $
 * PROJECT NAME: TVTaobao
 * PACKAGE NAME: com.yunos.tvtaobao.activity.sku
 * FILE NAME: SkuPropItemLayout.java
 * CREATED TIME: 2015年2月27日
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 *
 */
package com.yunos.tvtaobao.tradelink.sku;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taobao.detail.domain.DetailVO.StaticItem.SaleInfo.SkuProp.SkuPropValue;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.biz.request.bo.TBDetailResultV6;
import com.yunos.tvtaobao.biz.widget.SkuView;
import com.yunos.tvtaobao.biz.widget.SkuView.OnScrollStateChangedListener;
import com.yunos.tvtaobao.biz.widget.SkuView.OnSelectedItemListener;
import com.yunos.tvtaobao.biz.widget.SkuView.ScrollState;
import com.yunos.tvtaobao.tradelink.R;

import java.util.List;

/**
 * Class Descripton.
 * @version
 * @author mi.cao
 * @data 2015年2月27日 下午2:28:24
 */
public class SkuPropItemLayout extends RelativeLayout {


    public enum VALUE_VIEW_STATUS {
        //未知，未选择，已选择， 不可选
        UNKNOWN, UNSELECT, SELECTED, DISABLE, ENABLE
    }

    private String TAG = "SkuPropItemLayout";

    private Context mContext;
    // sku属性名称
    private TextView mSkuPropName;
    // sku属性界面
    private SkuView mSkuPropView;
    // sku购买数量界面
    private LinearLayout mSkuBuyNumLayout;
    // 左箭头
    private ImageView mSkuBuyNumLeftArray;
    // 右箭头
    private ImageView mSkuBuyNumRightArray;
    // 购买数量
    private TextView mSkuBuyNumTextView;
    // 库存数量
    private TextView mSkuKuCunTextView;
    // 加倍购买倍数
    private TextView mSkuUnitBuy;
    // 库存数量
    private long mSkuKuCunNum;
    // 限购商品数量
    private long mLimitCount;
    // 单位购买，倍数
    private int mUnit = 1;
    // 当前购买数
    private int mCurBuyCount;
    // 属性左边距便宜
    private int mMarginLeft;
    // skuEngine
    private SkuEngine mSkuEngine;
    // sku属性ID
    private long mPropId;
    // 属性值ID
    private long mValueId;
    // 区分是sku界面还是库存加减界面
    private boolean mIsSkuView;
    // 手动取消属性选择
    private boolean mManualCancel;
    // 选择的View
    private View mSelectedView;
    // 当选择某个View时 是第一次Focus
    private boolean mSelectedOnFirstFocus;

    public SkuPropItemLayout(Context context, long propId, boolean isSkuView, SkuEngine skuEngine) {
        super(context);
        mContext = context;
        mPropId = propId;
        mSkuEngine = skuEngine;
        mIsSkuView = isSkuView;
        mMarginLeft = (int) context.getResources().getDimensionPixelSize(R.dimen.dp_60);
        mManualCancel = false;
        mCurBuyCount = 1;
        mLimitCount = -1;
        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.ytm_sku_prop_item_layout, null);
        addView(view);

        mSkuPropName = (TextView) view.findViewById(R.id.sku_prop_item_name);
        mSkuPropView = (SkuView) view.findViewById(R.id.sku_prop_item_content);
        RelativeLayout mSkuPropItemLayout = (RelativeLayout) view.findViewById(R.id.sku_prop_item_layout);
        mSkuBuyNumLayout = (LinearLayout) view.findViewById(R.id.sku_buy_num_layout);
        mSkuBuyNumLeftArray = (ImageView) view.findViewById(R.id.sku_buy_num_left_array);
        mSkuBuyNumRightArray = (ImageView) view.findViewById(R.id.sku_buy_num_right_array);
        mSkuBuyNumTextView = (TextView) view.findViewById(R.id.sku_buy_num);
        mSkuKuCunTextView = (TextView) view.findViewById(R.id.sku_kucun_text);
        mSkuUnitBuy = (TextView) view.findViewById(R.id.sku_unit_buy_text);
        if (mIsSkuView) {
            mSkuPropItemLayout.setVisibility(View.VISIBLE);
            mSkuBuyNumLayout.setVisibility(View.GONE);
            initSkuView();
        } else {
            mSkuPropItemLayout.setVisibility(View.GONE);
            mSkuBuyNumLayout.setVisibility(View.VISIBLE);
            initBuyNumView();
        }
    }

    /**
     * 初始化sku属性界面
     */
    private void initSkuView() {
        mSkuPropView.setTag(mPropId);
        mSkuPropView.setFocusable(true);
        mSkuPropView.setFocusDrawable(new ColorDrawable(getResources().getColor(R.color.ytm_sku_fucus_bg)));
        mSkuPropView.setReferenceDistance(mMarginLeft);
        mSkuPropView.setItemSpace((int) getResources().getDimensionPixelSize(R.dimen.dp_28));
        mSkuPropView.setDefaultSelectedItem(0);
        mSkuPropView.setScrollDuration(500);
        mSkuPropView.setNextFocusLeftId(R.id.sku_prop_item_content);
        mSkuPropView.setNextFocusRightId(R.id.sku_prop_item_content);
        mSkuPropView.setOnScrollStateChangedListener(new OnScrollStateChangedListener() {

            @Override
            public void onScrollStateChanged(ScrollState state) {
                AppDebug.v(TAG, TAG + ".initSkuView.state = " + state);
                if (state == ScrollState.IDLE) {
                    if (mSelectedView != null) {
                        mSelectedView.setBackgroundResource(R.drawable.ytm_sku_select_tag);
                        mSkuEngine.addSelectedPropData(mPropId, mValueId);
                    }
                }
            }
        });

        mSkuPropView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (Config.isDebug()) {
                    AppDebug.i(TAG, TAG + ".initSkuView.onFocusChange skuView =" + v + " hasFocus=" + hasFocus
                            + ", mManualCancel = " + mManualCancel);
                }
                onLayouFocusChange(hasFocus);
                if (hasFocus) {
                    mSelectedOnFirstFocus = true;
                    resetValueViewStatus(v);
                }
            }
        });
        mSkuPropView.setOnSelectedItemListener(new OnSelectedItemListener() {

            @Override
            public void onSelectedItemListener(int position, View view, boolean selected) {
                if (Config.isDebug()) {
                    AppDebug.i(TAG, TAG + ".initSkuView.onSelectedItemListener position =" + position + " view=" + view
                            + " selected=" + selected + ", mSelectedOnFirstFocus = " + mSelectedOnFirstFocus);
                }
                mValueId = getValueIdFromKey((String) view.getTag());
                if (selected) {// 当属性值选中时，更新到选中列表中。
                    mSelectedView = view;
                    if (mSelectedOnFirstFocus) {// 第一次focus并且选中的时候控件没有滚动，需要此处设置背景。
                        mSelectedView.setBackgroundResource(R.drawable.ytm_sku_select_tag);
                        mSkuEngine.addSelectedPropData(mPropId, mValueId);
                        mSelectedOnFirstFocus = false;
                    }
                } else {
                    if (!mManualCancel) { // 如果没有手动取消，则更新选中状态
                        updateValueViewStatus(mPropId, mValueId, VALUE_VIEW_STATUS.SELECTED);
                    } else {
                        mSelectedView.setBackgroundDrawable(null);
                        mSelectedView = null;// 取消选择时选择的view置null，保证在动画结束前取消选择时不会设置背景标志
                    }
                }
                mManualCancel = false;
            }
        });
    }

    /**
     * 初始化购买数量layout
     */
    private void initBuyNumView() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mSkuBuyNumLayout.getLayoutParams();
        if (params == null) {
            params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.leftMargin = (int) mContext.getResources().getDimensionPixelSize(R.dimen.dp_70);
        }
        params.leftMargin += mMarginLeft;
        mSkuBuyNumLayout.setLayoutParams(params);
        mSkuBuyNumLayout.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                boolean buyCountChanged = false;
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    long canBuyCount = getCanBuyCount();

                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if (mCurBuyCount < canBuyCount) {
                            mCurBuyCount++;
                            buyCountChanged = true;
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        if (mCurBuyCount > 1) {
                            mCurBuyCount--;
                            buyCountChanged = true;
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                        if (mOnBuyCountClickedListener != null) {
                            mOnBuyCountClickedListener.OnBuyCountClicked();
                        }
                    }
                }

                if (buyCountChanged) {
                    if (mOnBuyCountChangedListener != null) {
                        mOnBuyCountChangedListener.OnBuyCountChanged(mCurBuyCount);
                    }
                    setBuyNumLayout();
                }
                return false;
            }
        });
        mSkuBuyNumLayout.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                onLayouFocusChange(hasFocus);
                if (hasFocus) {
                    mSkuBuyNumTextView.setBackgroundColor(getResources().getColor(R.color.ytm_sku_fucus_bg));
                    mSkuBuyNumTextView.setTextColor(getResources().getColor(android.R.color.white));
                } else {
                    mSkuBuyNumTextView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    mSkuBuyNumTextView.setTextColor(getResources().getColor(R.color.ytm_sku_fucus_bg));
                }
            }
        });
    }

    /**
     * 当属性控件获取焦点时
     * @param focus
     */
    private void onLayouFocusChange(boolean focus) {
        if (mOnPropViewFocusListener != null) {
            mOnPropViewFocusListener.OnPorpViewFocus(this, focus);
        }
    }

    /**
     * 初始化购买数量界面
     */
    private void setBuyNumLayout() {
        long canBuyCount = getCanBuyCount();

        if (mCurBuyCount <= 1) {
            mSkuBuyNumLeftArray.setImageResource(R.drawable.ytm_sku_buy_num_left_disable);
        } else {
            mSkuBuyNumLeftArray.setImageResource(R.drawable.ytm_sku_buy_num_left_normal);
        }

        if (mCurBuyCount >= canBuyCount) {
            mSkuBuyNumRightArray.setImageResource(R.drawable.ytm_sku_buy_num_right_disable);
        } else {
            mSkuBuyNumRightArray.setImageResource(R.drawable.ytm_sku_buy_num_right_normal);
        }

        mSkuBuyNumTextView.setText(String.valueOf(mCurBuyCount));
    }

    /**
     * 设置库存数量
     */
    public void setKuCunNum(long num, long limitCount) {
        mSkuKuCunNum = num;
        mLimitCount = limitCount;
        String text = String.format(getResources().getString(R.string.ytm_sku_kucun_text), num);
        if (mLimitCount > 0&&mLimitCount<=mSkuKuCunNum) {
            AppDebug.e(TAG,"商品存在限购，且<=库存，去显示限购信息");
            text += " ";
            text += String.format(getResources().getString(R.string.ytm_sku_limit_count_text), mLimitCount);
        }
        mSkuKuCunTextView.setText(text);
        if (Config.isDebug()) {
            AppDebug.v(TAG, TAG + ".setKuCunNum.mSkuKuCunNum = " + mSkuKuCunNum + ".mLimitCount = " + mLimitCount);
        }
    }

    /**
     * 设置显示加倍购买
     */
    public void setUnitBuy(int unit) {
        AppDebug.v(TAG, TAG + ".setUnitBuy.unit = " + unit);
        this.mUnit = unit;
        String text = String.format(getResources().getString(R.string.ytm_sku_unit_buy_text), mUnit);
        mSkuUnitBuy.setVisibility(VISIBLE);
        mSkuUnitBuy.setText(text);
    }

    /**
     * 设置购买数量
     * @param count
     */
    public void setCurBuyCount(int count) {
        if (Config.isDebug()) {
            AppDebug.v(TAG, TAG + ".setCurBuyCount.count = " + count + ", mSkuKuCunNum = " + mSkuKuCunNum);
        }
        long canBuyCount = getCanBuyCount();

        if (count <= 0) {
            mCurBuyCount = 1;
        } else if (count >= canBuyCount) {
            mCurBuyCount = (int) canBuyCount;
        } else {
            mCurBuyCount = count;
        }
        setBuyNumLayout();
        if (mOnBuyCountChangedListener != null) {
            mOnBuyCountChangedListener.OnBuyCountChanged(mCurBuyCount);
        }
    }

    /**
     * 可购买数量
     * @return
     */
    private long getCanBuyCount() {
        long canBuyCount;
        if (mLimitCount > 0) {
            canBuyCount = mLimitCount < mSkuKuCunNum ? mLimitCount : mSkuKuCunNum;
        } else {
            canBuyCount = mSkuKuCunNum;
        }

        if (mUnit > 1) {
            canBuyCount = canBuyCount / mUnit;
        }

        AppDebug.i(TAG, TAG + ".getCanBuyCount canBuyCount = " + canBuyCount);

        return canBuyCount;
    }

    /**
     * 获取当前购买数
     * @return
     */
    public int getCurBuyNum() {
        return mCurBuyCount;
    }

    /**
     * 设置sku的名称
     * @param text
     */
    public void setSkuPropName(String text) {
        mSkuPropName.setText(text);
    }

    /**
     * 设置Sku属性
     * @param
     */
    public void setSkuPropView(List<TBDetailResultV6.SkuBaseBean.PropsBeanX.ValuesBean> values) {
        if (values == null || values.size() == 0) {
            return;
        }

        int size = values.size();
        for (int i = 0; i < size; i++) {
            TextView textView = new TextView(mContext);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.MATCH_PARENT);
            textView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            textView.setTextColor(getResources().getColorStateList(android.R.color.white));
            textView.setText(values.get(i).getName());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) getResources().getDimensionPixelSize(R.dimen.sp_20));
            textView.setMinWidth((int) getResources().getDimensionPixelSize(R.dimen.dp_84));
            textView.setMaxWidth((int) getResources().getDimensionPixelSize(R.dimen.dp_160));
            textView.setEllipsize(TruncateAt.MARQUEE);
            textView.setSingleLine();
            textView.setMarqueeRepeatLimit(1);
            textView.setGravity(Gravity.CENTER);
            textView.setIncludeFontPadding(false);
            textView.setPadding(5, 0, 5, 0);
            Long valueId = Long.parseLong(values.get(i).getVid());
            textView.setTag(mSkuEngine.getPropKey(mPropId, valueId));//pid,vid
            textView.setFocusable(false);
            mSkuPropView.addView(textView, params);
            SkuEngine.PropData propData = new SkuEngine.PropData();
            propData.propKey = mSkuEngine.getPropKey(mPropId, valueId);
            propData.propId = mPropId;
            propData.valueId = valueId;
            if (values.get(i).getImage()!=null)
                propData.imageUrl = values.get(i).getImage();
            mSkuEngine.addPropData(propData);

            textView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    AppDebug.v(TAG, TAG + ".setOnClickListener v = " + v);
                    mSelectedView = v;
                    mValueId = getValueIdFromKey((String) v.getTag());
                    if (mValueId > -1) {
                        SkuEngine.PropData data = mSkuEngine.getPropDataFromList(mPropId, mValueId);
                        if (data != null) {
                            if (data.selected) {
                                mSelectedView.setBackgroundDrawable(null);
                                mSkuEngine.deleteSelectedPropData(mPropId);
                                mManualCancel = true;// 手动取消选中状态
                            } else {
                                mSelectedView.setBackgroundResource(R.drawable.ytm_sku_select_tag);
                                mSkuEngine.addSelectedPropData(mPropId, mValueId);
                                mManualCancel = false;
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * sku 默认选择
     * @param propId
     * @param
     */
    public void setDefaultSelectSku(long propId, long defaultValueId) {
        if (mSkuPropView == null || mSkuPropView.getChildCount() <= 0) {
            return;
        }

        String textViewTag = mSkuEngine.getPropKey(propId, defaultValueId);
        if (TextUtils.isEmpty(textViewTag)) {
            return;
        }
        int index = -1;
        for (int i = 0; i < mSkuPropView.getChildCount(); i++) {
            String tag = (String) mSkuPropView.getChildAt(i).getTag();
            if (textViewTag.equals(tag)) {
                index = i;
                break;
            }
        }
        if (index >= 0) {// 默认选择的索引
            mSkuPropView.setDefaultSelectedItem(index);
            updateValueViewStatus(mPropId, defaultValueId, VALUE_VIEW_STATUS.SELECTED);
        }
    }

    /**
     * 更新属性值界面的状态
     * @param status
     * @param propId
     * @param valueId
     */
    public void updateValueViewStatus(Long propId, Long valueId, VALUE_VIEW_STATUS status) {
        SkuEngine.PropData propData = mSkuEngine.getPropDataFromList(propId, valueId);
        if (propData == null) {
            return;
        }
        TextView textView = getViewFromId(propId, valueId);
        if (textView == null) {
            return;
        }
        if (Config.isDebug()) {
            AppDebug.v(TAG, TAG + ".updateValueViewStatus.propData = " + propData + ", status = " + status);
        }
        switch (status) {
            case UNKNOWN:
                break;
            case UNSELECT:

                textView.setEnabled(true);
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(getResources().getColor(android.R.color.white));
                textView.setBackgroundDrawable(null);
                break;
            case SELECTED://从该类属性离开时，选中该属性,包括快速离开的极限情况
                textView.setEnabled(true);
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(getResources().getColor(android.R.color.white));
                textView.setBackgroundResource(R.drawable.ytm_sku_prop_selected);
                mSkuEngine.addSelectedPropData(propId, valueId);
                break;
            case DISABLE:

                textView.setEnabled(false);
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(getResources().getColor(R.color.ytm_sku_text_disable));
                textView.setBackgroundDrawable(null);
                break;
            case ENABLE:
                textView.setEnabled(true);
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(getResources().getColor(android.R.color.white));
                textView.setBackgroundDrawable(null);
                break;
        }
    }

    /**
     * 重置所有的属性字体状态
     */
    public void resetValueViewStatus(View v) {
        if (mSkuPropView == null || v == null) {
            return;
        }

        for (int i = 0; i < mSkuPropView.getChildCount(); i++) {
            TextView textView = (TextView) mSkuPropView.getChildAt(i);
            String tag = (String) textView.getTag();
            if (!TextUtils.isEmpty(tag)) {
                SkuEngine.PropData data = null;
                String[] str = tag.split(":");// 从tag中取出propid和valueid
                if (str != null && str.length == 2) {
                    try {// 得到该属性所在的数据
                        data = mSkuEngine.getPropDataFromList(Long.valueOf(str[0]), Long.valueOf(str[1]));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                if (data != null) {
                    if (data.enable) {
                        textView.setTextColor(getResources().getColor(android.R.color.white));
                    }
                    if (data.selected) {
                        textView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    }
                }
            }
        }
    }

    /**
     * 获取属性ID
     * @return
     */
    public long getPropId() {
        return mPropId;
    }

    /**
     * 通过ID获取TextView
     * @param propId
     * @param valueId
     * @return
     */
    public TextView getViewFromId(Long propId, Long valueId) {
        if (Config.isDebug()) {
            AppDebug.v(TAG, TAG + ".getViewFromId.propId = " + propId + ". valueId = " + valueId);
        }

        if (mSkuPropView == null || mSkuPropView.getChildCount() <= 0) {
            return null;
        }

        String textViewTag = mSkuEngine.getPropKey(propId, valueId);
        if (TextUtils.isEmpty(textViewTag)) {
            return null;
        }
        for (int i = 0; i < mSkuPropView.getChildCount(); i++) {
            String tag = (String) mSkuPropView.getChildAt(i).getTag();
            if (textViewTag.equals(tag)) {
                return (TextView) mSkuPropView.getChildAt(i);
            }
        }

        return null;
    }

    /**
     * 通过textView的tag获取ValueId
     * @param key
     * @return
     */
    private long getValueIdFromKey(String key) {
        AppDebug.v(TAG, TAG + ". getValueIdFromKey key = " + key);
        if (TextUtils.isEmpty(key)) {
            return -1;
        }

        String[] valueArray = key.split(":");
        if (valueArray != null && valueArray.length == 2) {
            try {
                return Long.parseLong(valueArray[1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return -1;
            }
        }

        return -1;
    }

    /**
     * 销毁数据
     */
    public void onDestroy() {
        if (mSkuPropView != null) {
            mSkuPropView.removeAllViews();
            mSkuPropView = null;
        }
    }

    private OnPropViewFocusListener mOnPropViewFocusListener;

    public void setOnPropViewFocusListener(OnPropViewFocusListener l) {
        mOnPropViewFocusListener = l;
    }

    /**
     * 属性控件聚焦监听
     * Class Descripton.
     * @version
     * @author mi.cao
     * @data 2015年3月12日 下午3:18:36
     */
    public interface OnPropViewFocusListener {

        public void OnPorpViewFocus(View view, boolean focus);
    }

    private OnBuyCountChangedListener mOnBuyCountChangedListener;

    public void setOnBuyCountChangedListener(OnBuyCountChangedListener l) {
        mOnBuyCountChangedListener = l;
    }

    /**
     * 当购买数量发生变化时
     * Class Descripton.
     * @version
     * @author mi.cao
     * @data 2015年3月17日 下午6:19:03
     */
    public interface OnBuyCountChangedListener {

        public void OnBuyCountChanged(int buyCount);
    }

    private OnBuyCountClickedListener mOnBuyCountClickedListener;

    public void setOnBuyCountClickedListener(OnBuyCountClickedListener l) {
        mOnBuyCountClickedListener = l;
    }

    /**
     * 当购买数量控件被点击时
     * Class Descripton.
     * @version
     * @author mi.cao
     * @data 2015年3月30日 上午10:45:32
     */
    public interface OnBuyCountClickedListener {

        public void OnBuyCountClicked();
    }
}
