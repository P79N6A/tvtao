package com.yunos.tvtaobao.takeoutbundle.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.powyin.scroll.adapter.AdapterDelegate;
import com.powyin.scroll.adapter.PowViewHolder;
import com.yunos.tvtaobao.takeoutbundle.R;
import com.yunos.tvtaobao.takeoutbundle.activity.TakeOutShopSearchActivity;
import com.yunos.tvtaobao.takeoutbundle.widget.InnerFocusDispatchFrameLayout;

import java.text.DecimalFormat;

/**
 * Created by haoxiang on 2017/12/12. 商品 item
 */

public class ViewHolderSearchMore extends PowViewHolder<Object> {
    private final static DecimalFormat format = new DecimalFormat("¥#.##");
    private final ImageView imgShopHome;


    private InnerFocusDispatchFrameLayout innerFocusDispatchFrameLayout;
    private View good_focus_status;

    private boolean isFocus = false;
    private ImageView currentFocus;
    private AdapterDelegate<? super Object> mMultipleAdapter;
    private int mPosition = 0;

    private InnerFocusDispatchFrameLayout.OnFocusDropListener mOnFocusDropListener = new InnerFocusDispatchFrameLayout.OnFocusDropListener() {


        @Override
        public void onFocusLeave(View... unFocusView) {
            if (isFocus) {
                isFocus = false;
//                good_focus_status.setVisibility(View.INVISIBLE);
                mItemView.clearAnimation();
                currentFocus = null;
                if (mMultipleAdapter != null) {
                    loadData(mMultipleAdapter, mData, -1);
                }
            }
        }

        @Override
        public void onFocusEnter(View focusView, View... unFocusView) {
            if (!isFocus) {
                isFocus = true;
//                good_focus_status.setVisibility(View.VISIBLE);
                mItemView.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.toke_out_good_focus_in));
                if (mMultipleAdapter != null) {
                    loadData(mMultipleAdapter, mData, -1);

                    // todo 埋点
                }
            }

            if (currentFocus != focusView) {
                currentFocus = (ImageView) focusView;
                if (mMultipleAdapter != null) {
                    loadData(mMultipleAdapter, mData, -1);
                }
            }
        }

        @Override
        public void onFocusClick(View focus, View... unFocusView) {
            isFocus = true;
            currentFocus = (ImageView) focus;
            performClick(focus);
            if (mMultipleAdapter != null) {
                loadData(mMultipleAdapter, mData, -1);
            }
        }
    };


    public ViewHolderSearchMore(Activity activity, ViewGroup viewGroup) {
        super(activity, viewGroup);

        innerFocusDispatchFrameLayout = (InnerFocusDispatchFrameLayout) mItemView;
        imgShopHome = findViewById(R.id.img_shop_home);
        good_focus_status = findViewById(R.id.good_focus_status);
        innerFocusDispatchFrameLayout.setOnFocusDropListener(mOnFocusDropListener);


    }

    /**
     * @param view 点击view
     */
    private void performClick(View view) {
//        if (view == null) return;
        TakeOutShopSearchActivity activity = (TakeOutShopSearchActivity) mActivity;
        activity.goToShopHome();

    }


    @Override
    protected int getItemViewRes() {
//        if (Build.VERSION.SDK_INT >= 21) {
        return R.layout.item_takeout_shop_home;
//        } else {
//            return R.layout.item_takeout_home_api_low;
//        }
    }


    @Override
    public void loadData(AdapterDelegate<? super Object> multipleAdapter, Object data, int position) {
        mMultipleAdapter = multipleAdapter;
        mPosition = position >= 0 ? position : mPosition;

    }


}












































