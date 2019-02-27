package com.yunos.tvtaobao.tradelink.buildorder.view;


import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunos.tv.app.widget.MarqueeTextView;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.tradelink.R;
import com.yunos.tvtaobao.tradelink.buildorder.adapter.GallryListSelectAdapter;

public abstract class GallryListViewHolder extends PurchaseViewHolder {

    protected View view;
    protected SelectMarqueeTextView tvTitle;
    protected int mSelectPos;

    protected BuildOrderGallery mBuildOrderGallery;
    protected GallryListSelectAdapter mGallryListAdapter;

    public GallryListViewHolder(Context context) {
        super(context);
        mSelectPos = 0;
    }

    @Override
    protected View makeView() {

        view = View.inflate(context, R.layout.ytm_buildorder_buy_item_layout, null);
        if (view instanceof BuildOrderItemView)
            ((BuildOrderItemView) view).setBlockLeftRightKey(true);
        tvTitle = (SelectMarqueeTextView) view.findViewById(R.id.goods_buy_documents);
        mBuildOrderGallery = (BuildOrderGallery) view.findViewById(R.id.goods_buy_gallery);
        mGallryListAdapter = new GallryListSelectAdapter(context);
        mBuildOrderGallery.setAdapter(mGallryListAdapter);

        onVisibilityArrow();

        return view;
    }

    protected void onVisibilityArrow() {

        // 处理显示左右箭头
        int visibility = View.INVISIBLE;
        int count = mGallryListAdapter.getCount();
        if (count > GallryListSelectAdapter.LOOPCOUNT_START) {
            visibility = View.VISIBLE;
        }

        ImageView leftView = (ImageView) view.findViewById(R.id.goods_buy_left_arrow);
        leftView.setVisibility(visibility);
        ImageView rightView = (ImageView) view.findViewById(R.id.goods_buy_right_arrow);
        rightView.setVisibility(visibility);
    }

    @Override
    protected boolean onKeyListener(View v, int keyCode, KeyEvent event) {
        int count = mGallryListAdapter.getCount();
        AppDebug.i(TAG, "onKeyListener count = " + count);
        if (count > GallryListSelectAdapter.LOOPCOUNT_START) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (mSelectPos > 0) {
                        mSelectPos--;

                        mBuildOrderGallery.setSelection(mSelectPos);
                        int realPosL = mGallryListAdapter.getRealPosition(mSelectPos);
                        handlerChecked(realPosL);
                        AppDebug.i(TAG, "onKeyListener LEFT = mSelectPos = " + mSelectPos + "; realPosL = " + realPosL);
                    }

                    return true;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (mSelectPos < mGallryListAdapter.getCount() - 1) {
                        mSelectPos++;

                        mBuildOrderGallery.setSelection(mSelectPos);
                        int realPosR = mGallryListAdapter.getRealPosition(mSelectPos);
                        handlerChecked(realPosR);
                        AppDebug.i(TAG, "onKeyListener RIGHT= mSelectPos = " + mSelectPos + "; realPosR = " + realPosR);
                    }
                    return true;
            }
        }
        return false;
    }

    /**
     * 处理按键
     *
     * @param realselect 实际的pos值
     */
    protected abstract void handlerChecked(int realselect);

}
