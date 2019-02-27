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
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
 *
 * @author mi.cao
 * @data 2015年2月27日 下午2:28:24
 */
public class ContractItemLayout extends RelativeLayout {


    public enum VALUE_VIEW_STATUS {
        //未知，未选择，已选择， 不可选
        UNKNOWN, UNSELECT, SELECTED, DISABLE, ENABLE
    }

    private String TAG = "ContractItemLayout";

    private Context mContext;
    // sku属性名称
    private TextView mSkuPropName;
    // sku属性界面
    private SkuView mSkuPropView;
    // 属性左边距
    private int mMarginLeft;
    // skuEngine
    private View mSelectedView;
    // 当选择某个View时 是第一次Focus
    private boolean mSelectedOnFirstFocus;

    private boolean manualCancel = false;

    private TBDetailResultV6.ContractData.VersionData selectedVersionData;

    public ContractItemLayout(Context context) {
        super(context);
        mContext = context;
        mMarginLeft = (int) context.getResources().getDimension(R.dimen.dp_60);
        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.trade_sku_contract_item_layout, null);
        addView(view);

        mSkuPropName = (TextView) view.findViewById(R.id.sku_prop_item_name);
        mSkuPropView = (SkuView) view.findViewById(R.id.sku_prop_item_content);
        RelativeLayout mSkuPropItemLayout = (RelativeLayout) view.findViewById(R.id.sku_prop_item_layout);
        mSkuPropItemLayout.setVisibility(View.VISIBLE);
        initSkuView();
    }

    /**
     * 初始化sku属性界面
     */
    private void initSkuView() {
        mSkuPropView.setFocusable(true);
        mSkuPropView.setFocusDrawable(new ColorDrawable(getResources().getColor(R.color.ytm_sku_fucus_bg)));
        mSkuPropView.setReferenceDistance(mMarginLeft);
        mSkuPropView.setItemSpace((int) getResources().getDimension(R.dimen.dp_28));
        mSkuPropView.setDefaultSelectedItem(0);
        mSkuPropView.setScrollDuration(500);
        mSkuPropView.setNextFocusLeftId(R.id.sku_prop_item_content);
        mSkuPropView.setNextFocusRightId(R.id.sku_prop_item_content);
        mSkuPropView.setOnScrollStateChangedListener(new OnScrollStateChangedListener() {

            @Override
            public void onScrollStateChanged(ScrollState state) {
                AppDebug.v(TAG, TAG + ".initSkuView.state = " + state);
                if (state == ScrollState.IDLE) {
                    for (int i = 0; i < mSkuPropView.getChildCount(); i++) {
                        View child = mSkuPropView.getChildAt(i);
                        if (child != mSelectedView) {
                            child.setBackgroundDrawable(null);
                        } else {
                            child.setBackgroundResource(R.drawable.ytm_sku_select_tag);
                        }
                    }
                }
            }
        });

        mSkuPropView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                onLayouFocusChange(hasFocus);
                if (hasFocus) {
                    mSelectedOnFirstFocus = true;
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
                if (selected) {// 当属性值选中时，更新到选中列表中。
                    mSelectedView = view;
                    selectedVersionData = (TBDetailResultV6.ContractData.VersionData) view.getTag();
                    if (mSelectedOnFirstFocus) {// 第一次focus并且选中的时候控件没有滚动，需要此处设置背景。
                        mSelectedView.setBackgroundResource(R.drawable.ytm_sku_select_tag);
                        mSelectedOnFirstFocus = false;
                    }
                } else {
                    if (!manualCancel) {
                        updateValueViewStatus(view, VALUE_VIEW_STATUS.SELECTED);
                    } else {
                        mSelectedView.setBackgroundDrawable(null);
                        mSelectedView = null;// 取消选择时选择的view置null，保证在动画结束前取消选择时不会设置背景标志
                    }
                }
                manualCancel = false;
            }
        });
    }

    /**
     * 更新属性值界面的状态
     *
     * @param status
     */
    public void updateValueViewStatus(View view, VALUE_VIEW_STATUS status) {
        if (view == null || !(view instanceof TextView)) {
            return;
        }
        TextView textView = (TextView) view;

        switch (status) {
            case UNKNOWN:
                break;
            case UNSELECT:
                textView.setEnabled(true);
                textView.setTextColor(getResources().getColor(android.R.color.white));
                textView.setBackgroundDrawable(null);
                break;
            case SELECTED://从该类属性离开时，选中该属性,包括快速离开的极限情况
                textView.setEnabled(true);
                textView.setTextColor(getResources().getColor(android.R.color.white));
                textView.setBackgroundResource(R.drawable.ytm_sku_prop_selected);
                break;
            case DISABLE:
                textView.setEnabled(false);
                textView.setTextColor(getResources().getColor(R.color.ytm_sku_text_disable));
                textView.setBackgroundDrawable(null);
                break;
            case ENABLE:
                textView.setEnabled(true);
                textView.setTextColor(getResources().getColor(android.R.color.white));
                textView.setBackgroundDrawable(null);
                break;
        }


    }

    /**
     * 当属性控件获取焦点时
     *
     * @param focus
     */
    private void onLayouFocusChange(boolean focus) {
        if (mOnPropViewFocusListener != null) {
            mOnPropViewFocusListener.OnPorpViewFocus(this, focus);
        }
    }

    /**
     * 设置sku的名称
     *
     * @param text
     */
    public void setSkuPropName(String text) {
        mSkuPropName.setText(text);
    }

    /**
     * 设置Sku属性
     *
     * @param
     */
    public void setSkuPropView(final List<TBDetailResultV6.ContractData.VersionData> values) {
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
            textView.setText(values.get(i).versionName);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) getResources().getDimension(R.dimen.sp_20));
            textView.setMinWidth((int) getResources().getDimension(R.dimen.dp_84));
            textView.setMaxWidth((int) getResources().getDimension(R.dimen.dp_160));
            textView.setEllipsize(TruncateAt.MARQUEE);
            textView.setSingleLine();
            textView.setTag(values.get(i));
            textView.setMarqueeRepeatLimit(1);
            textView.setGravity(Gravity.CENTER);
            textView.setIncludeFontPadding(false);
            textView.setPadding(5, 0, 5, 0);
            textView.setFocusable(false);
            mSkuPropView.addView(textView, params);
            textView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    AppDebug.v(TAG, TAG + ".setOnClickListener v = " + v);
                    mSelectedView = v;
                    TBDetailResultV6.ContractData.VersionData data = (TBDetailResultV6.ContractData.VersionData) mSelectedView.getTag();
                    if (data == selectedVersionData) {
                        manualCancel = true;
                        mSelectedView.setBackgroundDrawable(null);
                        selectedVersionData = null;
                    } else {
                        mSelectedView.setBackgroundResource(R.drawable.ytm_sku_select_tag);
                        selectedVersionData = data;
                        manualCancel = false;
                    }
                }
            });
        }
    }

    public TBDetailResultV6.ContractData.VersionData getSelectedVersionData() {

        return selectedVersionData;
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

    private SkuPropItemLayout.OnPropViewFocusListener mOnPropViewFocusListener;

    public void setOnPropViewFocusListener(SkuPropItemLayout.OnPropViewFocusListener l) {
        mOnPropViewFocusListener = l;
    }

}
