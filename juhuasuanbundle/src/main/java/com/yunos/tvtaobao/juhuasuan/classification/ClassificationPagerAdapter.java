package com.yunos.tvtaobao.juhuasuan.classification;


import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.yunos.tv.core.common.AppDebug;

import java.util.ArrayList;
import java.util.List;

public class ClassificationPagerAdapter extends PagerAdapter {

    private final String TAG = "GoodListPagerAdapter";

    private Context mContext = null;
    private String mOrderby = null;
    private int mTotalCount = 1;

    private onItemHandleListener mOnItemHandleListener = null;

    private Item_Info mItem_Info = null;

    private List<ClassficationFocusedRelativeLayout> mRecycleClassficationFocusedRelativeLayout = null;
    private SparseArray<ClassficationFocusedRelativeLayout> mActivateClassficationFocusedRelativeLayout = null;

    private int mOldSelectPage = -1;
    private int mOldSelectIndex = -1;

    private boolean mFristInstantiate = true;

    public ClassificationPagerAdapter(Context context) {
        // TODO Auto-generated constructor stub 
        mContext = context;

        mRecycleClassficationFocusedRelativeLayout = new ArrayList<ClassficationFocusedRelativeLayout>();
        mRecycleClassficationFocusedRelativeLayout.clear();

        mActivateClassficationFocusedRelativeLayout = new SparseArray<ClassficationFocusedRelativeLayout>();
        mActivateClassficationFocusedRelativeLayout.clear();
    }

    public void onSetOnItemHandleListener(onItemHandleListener l) {
        mOnItemHandleListener = l;
    }

    public void onSetViewPagerInfo(Item_Info info) {
        mItem_Info = info;
        if (mItem_Info != null) {
            mTotalCount = mItem_Info.mTotalPage;
        }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub  
        if (mItem_Info != null) {
            mTotalCount = mItem_Info.mTotalPage;
        }

        return mTotalCount;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO Auto-generated method stub

        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub  

        ClassficationFocusedRelativeLayout classficationFocusedRelativeLayout = (ClassficationFocusedRelativeLayout) object;
        if (classficationFocusedRelativeLayout != null) {
            container.removeView(classficationFocusedRelativeLayout);
            classficationFocusedRelativeLayout.onRecycleRes();

            if (mOnItemHandleListener != null) {
                mOnItemHandleListener.onRemoveItemOfHandle(container, null, position);
            }

            if (mRecycleClassficationFocusedRelativeLayout != null) {
                mRecycleClassficationFocusedRelativeLayout.add(classficationFocusedRelativeLayout);
            }

            if (mActivateClassficationFocusedRelativeLayout != null) {
                mActivateClassficationFocusedRelativeLayout.delete(position);
            }
        }

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // TODO Auto-generated method stub

        AppDebug.i(TAG, "container = " + container);
        AppDebug.i(TAG, "position = " + position);

        ClassficationFocusedRelativeLayout classficationFocusedRelativeLayout = null;

        if (mRecycleClassficationFocusedRelativeLayout != null) {
            int count = mRecycleClassficationFocusedRelativeLayout.size();
            if (count > 0) {
                classficationFocusedRelativeLayout = mRecycleClassficationFocusedRelativeLayout.get(count - 1);
            }
        }

        if (classficationFocusedRelativeLayout == null) {
            classficationFocusedRelativeLayout = new ClassficationFocusedRelativeLayout(mContext);

            classficationFocusedRelativeLayout.onSetPagerAdapter(this);
            classficationFocusedRelativeLayout.onSetPageIndex(position);

            classficationFocusedRelativeLayout.onSetOnItemHandleListener(mOnItemHandleListener);
        } else {

            classficationFocusedRelativeLayout.onSetPageIndex(position);

            if (mRecycleClassficationFocusedRelativeLayout != null) {
                mRecycleClassficationFocusedRelativeLayout.remove(classficationFocusedRelativeLayout);
            }
        }

        container.addView(classficationFocusedRelativeLayout);

        classficationFocusedRelativeLayout.onRefreshItem();

        if (mActivateClassficationFocusedRelativeLayout != null) {
            mActivateClassficationFocusedRelativeLayout.put(position, classficationFocusedRelativeLayout);
        }

        if (mOnItemHandleListener != null) {
            mOnItemHandleListener.onInstantiateItemOfHandle(container, null, position);
        }

        if (mFristInstantiate) {
            mFristInstantiate = false;
            classficationFocusedRelativeLayout.onSetSelectView();
        }

        return classficationFocusedRelativeLayout;

    }

    public void upDateFoucs(boolean gainFocus, int currentPage) {

    }

    public void onClearAndDestroy() {
        if (mActivateClassficationFocusedRelativeLayout != null) {

            int count = mActivateClassficationFocusedRelativeLayout.size();
            for (int i = 0; i < count; i++) {
                int key = mActivateClassficationFocusedRelativeLayout.keyAt(i);
                ClassficationFocusedRelativeLayout goodlistLayout = mActivateClassficationFocusedRelativeLayout
                        .get(key);
                goodlistLayout.ClearAndDestroy();
                goodlistLayout = null;
            }
            mActivateClassficationFocusedRelativeLayout.clear();
            mActivateClassficationFocusedRelativeLayout = null;
        }

        if (mRecycleClassficationFocusedRelativeLayout != null) {

            int count = mRecycleClassficationFocusedRelativeLayout.size();
            for (int i = 0; i < count; i++) {
                ClassficationFocusedRelativeLayout goodlistLayout = mRecycleClassficationFocusedRelativeLayout.get(i);
                goodlistLayout.ClearAndDestroy();
                goodlistLayout = null;
            }

            mRecycleClassficationFocusedRelativeLayout.clear();
            mRecycleClassficationFocusedRelativeLayout = null;
        }
    }
}
