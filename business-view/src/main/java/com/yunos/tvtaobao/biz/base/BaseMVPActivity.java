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

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.yunos.tvtaobao.biz.activity.BaseActivity;


/**
 * ================================================
 * ================================================
 */
public abstract class BaseMVPActivity<T extends IPresenter> extends BaseActivity {
    protected final String TAG = this.getClass().getSimpleName();
    @Nullable
    protected T mPresenter;//如果当前页面逻辑简单, Presenter 可以为 null


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(provideContentViewId());
        mPresenter = createPresenter();
        initView();
        initData();
        initListener();
    }

    public void initListener() {

    }

    public void initData() {

    }

    public void initView() {

    }

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
}
