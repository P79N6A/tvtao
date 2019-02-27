/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yunos.tvtaobao.biz.base;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.lib.DisplayUtil;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.request.info.GlobalConfigInfo;
import com.yunos.tvtaobao.businessview.R;

import java.util.Map;


/**
 * ================================================
 * ================================================
 */
public abstract class BaseTabMVPActivity<T extends IPresenter> extends BaseActivity {
    protected final String TAG = this.getClass().getSimpleName();
    @Nullable
    protected T mPresenter;//如果当前页面逻辑简单, Presenter 可以为 null
    //把父类activity和子类activity的view都add到这里
    private ConstraintLayout constraintLayout;
    protected RightSideView rightSideView;
    //判断购物车是否有活动
    protected boolean hasActive = false;
    Map<String, String> lProperties = Utils.getProperties();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(provideContentViewId());
        initContentView(R.layout.activity_tab_base_mvp);
        rightSideView = (RightSideView)findViewById(R.id.right_side_view);
        mPresenter = createPresenter();
        initView();
        initData();
        initListener();
        isHasActivityFromNet();

    }



    private void initContentView(int layoutResID){
         LayoutInflater.from(this).inflate(layoutResID, constraintLayout, true);
    }


    @Override
    public void setContentView(int layoutResID) {
        FrameLayout viewGroup = (FrameLayout) findViewById(android.R.id.content);
        viewGroup.removeAllViews();
        constraintLayout = new ConstraintLayout(this) {

            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                AppDebug.d(TAG, "dispatchKeyEvent focus="+DisplayUtil.getViewStr(getCurrentFocus())+
                        " focusedChild="+DisplayUtil.getViewStr(getFocusedChild()));
                return super.dispatchKeyEvent(event);
            }

            @Override
            public View focusSearch(View focused, int direction) {
                View rtn = super.focusSearch(focused, direction);
                AppDebug.d(TAG, "focusSearch focus="+DisplayUtil.getViewStr(focused)
                        +" rtn="+DisplayUtil.getViewStr(rtn));
                return rtn;
            }
        };
        viewGroup.addView(constraintLayout);
        LayoutInflater.from(this).inflate(layoutResID, constraintLayout, true);
    }

    public void initListener() {

    }

    public void initData() {

    }

    public void initView() {

    }

    //等到当前activity的类名
    protected abstract  String getNewFullPageName();

    //用于创建Presenter和判断是否使用MVP模式(由子类实现)
    protected abstract T createPresenter();

    //得到当前界面的布局文件id(由子类实现)
    protected abstract int provideContentViewId();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null)
            mPresenter.onDestroy();//释放资源
        this.mPresenter = null;
    }

    /**
     * 得到是否有活动
     */
    private void isHasActivityFromNet() {
        GlobalConfig globalConfig = GlobalConfigInfo.getInstance().getGlobalConfig();
        if (globalConfig != null) {
            GlobalConfig.ShopCartFlag shopCartFlag = globalConfig.getShopCartFlag();
            if (shopCartFlag != null && shopCartFlag.isActing()) {
                rightSideView.getFiv_pierce_background().setBackgroundResource(R.drawable.goodlist_fiv_pierce_bg_d11);
//                fiv_pierce_cart_focusd.setImageResource(R.drawable.goodlist_fiv_pierce_cart_focus);
                hasActive = shopCartFlag.isActing();
                String sideBarImgUrl = shopCartFlag.getSideBarIcon();
                ImageLoaderManager.getImageLoaderManager(this).displayImage(sideBarImgUrl, rightSideView.getIv_pierce_cart_active());
                rightSideView.getIv_pierce_cart_active().setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rightSideView.getIv_pierce_cart_active().setVisibility(View.INVISIBLE);
                    }
                }, 5000);
                //判断是否有活动
                lProperties.put("name", "购物车气泡");
                Utils.utControlHit(getNewFullPageName(), "Expose_tool_cartbubble", lProperties);

            }
        }
    }




}
