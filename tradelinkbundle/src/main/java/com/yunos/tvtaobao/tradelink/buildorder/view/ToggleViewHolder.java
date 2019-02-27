package com.yunos.tvtaobao.tradelink.buildorder.view;


import android.content.Context;
import android.view.KeyEvent;
import android.view.View;

import com.taobao.wireless.trade.mbuy.sdk.co.basic.ToggleComponent;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.tradelink.activity.BuildOrderActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class ToggleViewHolder extends GallryListViewHolder {

    // 以下两个变量需要对应  mOptionName 和  mOptionCheck
    private final String mOptionName[] = {"使用 ", "不使用"};
    private final Boolean mOptionCheck[] = {true, false};
    private ArrayList<String> mList;

    public ToggleViewHolder(Context context) {
        super(context);
        mList = new ArrayList<String>();
    }

    @Override
    protected void bindData() {
        ToggleComponent toggleComponent = (ToggleComponent) component;
        tvTitle.setText(toggleComponent.getName());
        mList.clear();
        for (int i = 0; i < mOptionName.length; i++) {
            mList.add(mOptionName[i]);
        }
        mGallryListAdapter.setOptionName(mList);
        mSelectPos = getDefaultPosition(toggleComponent.isChecked());
        mBuildOrderGallery.setSelection(mSelectPos);
        mGallryListAdapter.notifyDataSetChanged();
        onVisibilityArrow();
    }

    @Override
    protected void handlerChecked(int realselect) {
        ToggleComponent toggleComponent = (ToggleComponent) component;
        toggleComponent.setChecked(mOptionCheck[realselect]);
        AppDebug.i(TAG, "handlerChecked    realselect = " + realselect + ";  mOptionCheck[realselect] = "
                + mOptionCheck[realselect]);
        if (getContext() instanceof BuildOrderActivity) {
            HashMap<String, String> params = new HashMap<>();
            BuildOrderActivity activity = (BuildOrderActivity) getContext();
            activity.utControlHit(toggleComponent.getTag(), params);
        }
    }

    /**
     * 根据Gallay的position值获取 使用 或者不使用
     *
     * @param position
     * @return
     */
    private boolean getOptionCheck(int position) {
        int realposition = mGallryListAdapter.getRealPosition(position);
        if (realposition >= 0 && realposition < mOptionCheck.length) {
            return mOptionCheck[realposition];
        }
        return false;
    }

    /**
     * 根据check 定位Gallay的值
     *
     * @param check
     * @return
     */
    private int getDefaultPosition(boolean check) {
        int position = Integer.MAX_VALUE / 2;
        boolean checkV = getOptionCheck(position);
        if (checkV != check) {
            position--;
        }
        AppDebug.i(TAG, "getDefaultPosition  getDefaultPosition  checkV = " + checkV + ";  check = " + check
                + "; position = " + position);
        return position;
    }

    @Override
    protected boolean onKeyListener(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
            // 如果在这种组件中，按OK键，那么就当作向左处理
            keyCode = KeyEvent.KEYCODE_DPAD_LEFT;
        }
        return super.onKeyListener(v, keyCode, event);
    }

}
