package com.yunos.tvtaobao.biz.widget.newsku.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yunos.tvtaobao.biz.request.bo.TBDetailResultV6;
import com.yunos.tvtaobao.biz.widget.newsku.SkuActivity;
import com.yunos.tvtaobao.biz.widget.newsku.interfaces.SkuInfoUpdate;

import java.util.List;

import com.yunos.tvtaobao.businessview.R;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/6/11
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class SkuItemLayout extends LinearLayout {
    private static final String TAG = "SkuItemLayout";

    public enum VALUE_VIEW_STATUS {
        //未知，未选择，已选择， 不可选
        UNKNOWN, UNSELECT, SELECTED, DISABLE, ENABLE
    }

    private Context context;
    private TextView nameTxt;
    private LinearLayout valueLayout;
    private long propId;
    private String name;
    private int skuItemSize = 0;
    private SkuInfoUpdate skuInfoUpdate;
    private int[] itemReso = {R.id.sku_item_1, R.id.sku_item_2, R.id.sku_item_3};

    private List<TBDetailResultV6.SkuBaseBean.PropsBeanX.ValuesBean> valuesBeans;

    public SkuItemLayout(Context context) {
        super(context);
        initView(context);
    }

    public SkuItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SkuItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.context = context;
        setOrientation(LinearLayout.VERTICAL);

        nameTxt = new TextView(context);
        nameTxt.setTextSize(getResources().getDimension(R.dimen.sp_16));
        nameTxt.setTextColor(Color.parseColor("#202020"));
        addView(nameTxt);
        valueLayout = new LinearLayout(context);
        valueLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, (int) getResources().getDimension(R.dimen.dp_6), 0, 0);
        valueLayout.setLayoutParams(lp);
        addView(valueLayout);
    }

    public void setProps(TBDetailResultV6.SkuBaseBean.PropsBeanX props) {
        initData(props);
    }

    private void initData(TBDetailResultV6.SkuBaseBean.PropsBeanX props) {
        name = props.getName();
        nameTxt.setText(name);
        propId = Long.parseLong(props.getPid());
        valuesBeans = props.getValues();
        int size = skuItemSize = valuesBeans.size();
        int line = size % 3 == 0 ? size / 3 : size / 3 + 1;

        for (int i = 0; i < line; i++) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_sku_layout, null);
            if (i > 0) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT);
                lp.setMargins(0, getResources().getDimensionPixelSize(R.dimen.dp_f2), 0, 0);
                view.setLayoutParams(lp);
            }
            int column = 3;
            if (size > 3) {
                size = size - 3;
            } else {
                column = size;
            }

            for (int j = 0; j < column; j++) {
                int pos = (i + 1) * 3 - (3 - j);
                SkuItem item = (SkuItem) view.findViewById(itemReso[j]);
                long valueId = Long.parseLong(valuesBeans.get(pos).getVid());
                item.setTag(getPropKey(propId, valueId));
                item.setVisibility(VISIBLE);
                item.setHeight((int) getResources().getDimension(R.dimen.dp_60));
                item.setText(valuesBeans.get(pos).getName());
                item.setTextSize(getResources().getDimension(R.dimen.sp_16));
                item.setPadding((int) getResources().getDimensionPixelSize(R.dimen.dp_8), 0,
                        (int) getResources().getDimensionPixelSize(R.dimen.dp_8), (int) getResources().getDimensionPixelSize(R.dimen.dp_3));
                item.setFocusable(true);
                item.setOnClickListener(skuItemClickListener);
                item.setValueId(valueId);
                if (skuItemSize == 1) { //TODO 当sku只有一个的时候，默认选中
                    item.setState(SkuItem.State.SELECT);
                    skuInfoUpdate.addSelectedPropData(propId, valueId);
                }
            }
            valueLayout.addView(view);
        }
    }

    /**
     * 设置sku更新监听
     *
     * @param update
     */
    public void setSkuUpdateListener(SkuInfoUpdate update) {
        this.skuInfoUpdate = update;
    }

    private View.OnClickListener skuItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view instanceof SkuItem
                    && ((SkuItem) view).getCurrentState() == SkuItem.State.DISABLE) {
                String errorInfo = context.getResources().getString(R.string.new_shop_product_stockout);
                if (context instanceof SkuActivity) {
                    SkuActivity activity = (SkuActivity) context;
                    activity.onShowError(errorInfo);
                } else {
                    Toast.makeText(context, errorInfo, Toast.LENGTH_SHORT).show();
                }
                return;
            }

            for (int i = 0; i < skuItemSize; i++) {
                long valueId = Long.parseLong(valuesBeans.get(i).getVid());
                SkuItem skuItem = getSkuItemById(propId, valueId);
                if (view == skuItem) {
                    if (skuItem.getCurrentState() == SkuItem.State.SELECT) {
                        skuItem.setState(SkuItem.State.UNSELECT);
                        skuInfoUpdate.deleteSelectedPropData(propId,valueId);
                    } else {
                        skuItem.setState(SkuItem.State.SELECT);
                        skuInfoUpdate.addSelectedPropData(propId, valueId);
                    }
                } else {
                    if (skuItem.getCurrentState() == SkuItem.State.SELECT) {
                        skuItem.setState(SkuItem.State.UNSELECT);
                    }
                }
            }
        }
    };

    public String getName() {
        return name;
    }

    public long getPropId() {
        return propId;
    }

    /**
     * 获取选中的item的valueID
     *
     * @return
     */
    private long getSelectedValueId() {
        long valueId = 0;
        for (int i = 0; i < skuItemSize; i++) {
            SkuItem skuItem = getSkuItemById(propId, Long.valueOf(valuesBeans.get(i).getVid()));
            if (skuItem.getCurrentState() == SkuItem.State.SELECT) {
                valueId = skuItem.getValueId();
                Log.i(TAG, TAG + ".getPropsId valueId ——> " + valueId);
            }
        }
        return valueId;
    }

    /**
     * 更新属性值界面的状态
     *
     * @param status
     * @param propId
     * @param valueId
     */
    public void updateValueViewStatus(Long propId, Long valueId, VALUE_VIEW_STATUS status) {
        SkuItem skuItem = getSkuItemById(propId, valueId);
        if (skuItem == null) {
            return;
        }

        switch (status) {
            case UNKNOWN:
            case UNSELECT:
                skuItem.setState(SkuItem.State.UNSELECT);
                break;
            case SELECTED://从该类属性离开时，选中该属性,包括快速离开的极限情况
                skuItem.setState(SkuItem.State.SELECT);
                skuInfoUpdate.addSelectedPropData(propId, valueId);
                break;
            case DISABLE:
                skuItem.setState(SkuItem.State.DISABLE);
                break;
            case ENABLE:
                if (skuItem.getCurrentState() != SkuItem.State.SELECT) {
                    skuItem.setState(SkuItem.State.UNSELECT);
                }
                break;
        }
    }

    /**
     * SkuItem
     *
     * @param propId
     * @param valueId
     * @return
     */
    public SkuItem getSkuItemById(Long propId, Long valueId) {
        String textViewTag = getPropKey(propId, valueId);
        if (TextUtils.isEmpty(textViewTag)) {
            return null;
        }

        int count = valueLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            if (valueLayout.getChildAt(i) instanceof LinearLayout) {
                LinearLayout linearLayout = (LinearLayout) valueLayout.getChildAt(i);
                int counts = linearLayout.getChildCount();
                for (int j = 0; j < counts; j++) {
                    String tag = (String) linearLayout.getChildAt(j).getTag();
                    if (textViewTag.equals(tag)) {
                        return (SkuItem) linearLayout.getChildAt(j);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取SkuItem
     *
     * @param pos
     * @return
     */
    public SkuItem getSkuItem(int pos) {
        int p = 0;
        int count = valueLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            if (valueLayout.getChildAt(i) instanceof LinearLayout) {
                LinearLayout linearLayout = (LinearLayout) valueLayout.getChildAt(i);
                int counts = linearLayout.getChildCount();
                for (int j = 0; j < counts; j++) {
                    if (linearLayout.getChildAt(j) instanceof SkuItem) {
                        if (pos == p) {
                            return (SkuItem) linearLayout.getChildAt(j);
                        }
                    }
                    p++;
                }
            }
        }
        return null;
    }

    private String getPropKey(long propId, long valueId) {
        return propId + ":" + valueId;
    }
}
