package com.yunos.tvtaobao.juhuasuan.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.juhuasuan.activity.HomeActivity;

@SuppressLint("ValidFragment")
public class BaseFragment extends Fragment {

    public static final String TAG = "BaseFragment";

    private int mLayoutResId;
    private int mContainerViewId;
    private String mTag;
    private HomeActivity mActivity;
    /**
     * 是否要聚焦到商品上
     */
    private boolean doRequestFocus = false;

    public HomeActivity getHomeActivity() {
        return mActivity;
    }

    public BaseFragment() {

    }

    public BaseFragment(int layoutResId, int containerViewId, String tag, HomeActivity activity) {

        this.mLayoutResId = layoutResId;
        this.mContainerViewId = containerViewId;
        this.mTag = tag;
        this.mActivity = activity;
        Bundle args = new Bundle();
        args.putInt("layout_id", layoutResId);
        args.putInt("container_id", containerViewId);
        args.putString("tag_name", tag);
        this.setArguments(args);
    }

    public int getContainerViewId() {
        return this.mContainerViewId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof HomeActivity) {
            this.mActivity = (HomeActivity) activity;
        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppDebug.d(TAG, "onCreateView --  tag:" + mTag + ", mLayoutResId:" + mLayoutResId);
        if (savedInstanceState != null) {
            mLayoutResId = savedInstanceState.getInt("layout_id");
            mTag = savedInstanceState.getString("tag_name");
            this.mContainerViewId = savedInstanceState.getInt("container_id");
        }
        AppDebug.d(TAG, "onCreateView 2--  tag:" + mTag + ", mLayoutResId:" + mLayoutResId);
        //        inflater = AuiResourceFetcher.getLayoutInflater(AppHolder.getContext());
        inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(mLayoutResId, container, false);
    }

    public void onHide(FragmentTransaction ft) {
        AppDebug.d(TAG, "onHide --  tag:" + mTag);
    }

    public void onShow(FragmentTransaction ft) {
        AppDebug.d(TAG, "onShow --  tag:" + mTag);
    }

    public void requestFocus() {
        setDoRequestFocus(true);
        View view = getView();
        if (null != view) {
            view.requestFocus();
            setDoRequestFocus(false);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putAll(getArguments());
    }

    public boolean isDoRequestFocus() {
        return doRequestFocus;
    }

    public void setDoRequestFocus(boolean doRequestFocus) {
        this.doRequestFocus = doRequestFocus;
    }

    public void recyleMemory() {

    }
}
